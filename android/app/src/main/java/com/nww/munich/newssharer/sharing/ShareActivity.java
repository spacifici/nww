package com.nww.munich.newssharer.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nww.munich.newssharer.Constants;
import com.nww.munich.newssharer.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ShareActivity extends Activity {

    URL articleURL;
    String articleTitle;
    String articleQuote;
    JSONObject jsonArticle;
    ArrayList<Integer> topicIndexes;
    ArrayList<Integer> personIndexes;

    private ProgressDialog progressDialog;
    private String articleID;

    private final Callback getCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            hideLoadingDialog();
            showErrorDialog(R.string.cant_contact_backend, true);
        }

        @Override
        public void onResponse(Response response) throws IOException {
            hideLoadingDialog();

            try {
                String json = response.body().string();
                Log.i("JSON", json);
                jsonArticle = new JSONObject(json);
                articleID = jsonArticle.getString("id");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMainFragment();
                    }
                });
            } catch (JSONException e) {
                showErrorDialog(R.string.error_parsing_backend_response, true);
            }
        }
    };

    private final Callback postCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            hideLoadingDialog();
            showErrorDialog(R.string.error_sending, false);
        }

        @Override
        public void onResponse(Response response) throws IOException {
            hideLoadingDialog();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.post_completed),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            });
        }
    };

    private void showMainFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, new MainFragment())
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    private void handleSendMultipleImages(Intent intent) {
        showErrorDialog(R.string.cant_handle_intent, true);
    }

    private void handleSendImage(Intent intent) {
        showErrorDialog(R.string.cant_handle_intent, true);
    }

    void showErrorDialog(final int messageId, final boolean finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
            builder
                    .setCancelable(false)
                    .setMessage(messageId)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (finish)
                                finish();
                        }
                    })
                    .show();
            }
        });
    }

    private void handleSendText(Intent intent) {
        // android.intent.extra.TEXT
        // android.intent.extra.SUBJECT
        // android.intent.extra.TITLE
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Try to parse the url
            try {
                URL url = new URL(sharedText);
                // webView.loadUrl(url.toString());
                String title = intent.getStringExtra(Intent.EXTRA_TITLE);

                this.articleURL = url;
                this.articleTitle = title;

                ApiRequests.sharedInstace.articleRequest(sharedText, getCallback);
                showLoadingDialog(getString(R.string.please_wait));
            } catch (MalformedURLException e) {
                showErrorDialog(R.string.not_a_valid_url, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showLoadingDialog(String message) {
        if (progressDialog != null)
            hideLoadingDialog();
        progressDialog = ProgressDialog.show(this,
                getString(R.string.loading),
                message,
                true);
    }

    void hideLoadingDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = null;
    }

    void submitData() {

        try {
            JSONArray origTopics = jsonArticle.getJSONObject("meta").getJSONArray("topics");
            JSONArray topics = jsonArticle.getJSONArray("topics");
            JSONArray people = jsonArticle.getJSONArray("people");
            JSONArray quotes = jsonArticle.getJSONArray("quotes");

            jsonArticle.put("title", articleTitle);
            quotes.put(articleQuote);
            jsonArticle.put("source_url", articleURL);

            // Topics
            for (Integer ix: topicIndexes) {
                topics.put(origTopics.get(ix));
            }

            // People
            for (Integer ix: personIndexes) {
                Object[] record = Constants.PERSONS[ix];

                JSONObject p = new JSONObject();
                p.put("handle", record[3])
                        .put("img_url", record[4])
                        .put("name", record[1])
                        .put("position", getString((Integer) record[1]));
                people.put(p);
            }

            showLoadingDialog(getString(R.string.posting));
            ApiRequests.sharedInstace.articlePost(articleID, jsonArticle, postCallback);
        } catch (JSONException ex) {
            showErrorDialog(R.string.error_parsing_backend_response, true);
        }
    }
}
