package com.nww.munich.newssharer.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nww.munich.newssharer.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class ShareActivity extends Activity {

    URL articleURL;
    String articleTitle;
    String articleQuote;
    JSONObject jsonArticle;

    private ProgressDialog progressDialog;

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
            } catch (JSONException e) {
                showErrorDialog(R.string.error_parsing_backend_response, true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
