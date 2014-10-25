package com.nww.munich.newssharer.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.nww.munich.newssharer.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class TextSelectionFragment extends Fragment {

    private WebView webView;
    private Button button;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_selection, null);
        webView = (WebView) view.findViewById(R.id.webView);
        button = (Button) view.findViewById(R.id.button);
        button.setEnabled(false);

        // Set listeners
        webView.setWebViewClient(new MyWebClient());
        progressDialog = null;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ClipboardManager cm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(clipboarPrimaryClipChangedListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        ClipboardManager cm =
                (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        cm.removePrimaryClipChangedListener(clipboarPrimaryClipChangedListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getActivity().getIntent();

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
        showErrorDialog(R.string.cant_handle_intent);
    }

    private void handleSendImage(Intent intent) {
        showErrorDialog(R.string.cant_handle_intent);
    }

    private void showErrorDialog(int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setCancelable(false)
            .setMessage(messageId)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getActivity().finish();
                }
            })
            .show();
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
                webView.loadUrl(url.toString());
                String title = intent.getStringExtra(Intent.EXTRA_TITLE);

                ShareActivity activity = (ShareActivity)getActivity();
                activity.articleURL = url;
                activity.articleTitle = title;

                showLoadingDialog();
            } catch (MalformedURLException e) {
                showErrorDialog(R.string.not_a_valid_url);
            }
        }
    }

    private void showLoadingDialog() {
        progressDialog = ProgressDialog.show(getActivity(),
                getString(R.string.loading),
                getString(R.string.please_select_quote),
                true);
    }

    private void hideLoadingDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }

    private final class MyWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            hideLoadingDialog();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            hideLoadingDialog();
        }
    }

    private final ClipboardManager.OnPrimaryClipChangedListener clipboarPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            button.setEnabled(true);

            ClipboardManager cm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
            String text = item.getText().toString();

            ShareActivity activity = (ShareActivity)getActivity();
            activity.articleQuote = text;
        }
    };
}
