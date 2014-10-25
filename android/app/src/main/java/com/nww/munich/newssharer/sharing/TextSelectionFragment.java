package com.nww.munich.newssharer.sharing;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.nww.munich.newssharer.R;

import static android.view.View.OnClickListener;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class TextSelectionFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_selection, null);
        webView = (WebView) view.findViewById(R.id.webView);

        // Set listeners
        webView.setWebViewClient(new MyWebClient());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ClipboardManager cm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(clipboarPrimaryClipChangedListener);

        webView.loadUrl(((ShareActivity) getActivity()).articleURL.toString());
        ((ShareActivity) getActivity()).showLoadingDialog(getString(R.string.please_select_quote));
    }

    @Override
    public void onPause() {
        super.onPause();
        ClipboardManager cm =
                (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        cm.removePrimaryClipChangedListener(clipboarPrimaryClipChangedListener);
    }

    private void backSettingQuote(String quote) {
        MainFragment main = (MainFragment) getTargetFragment();
        main.setQuote(quote);
        getFragmentManager().popBackStack();
    }

    private final class MyWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            ((ShareActivity) getActivity()).hideLoadingDialog();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            ((ShareActivity) getActivity()).hideLoadingDialog();
            ((ShareActivity) getActivity()).showErrorDialog(R.string.loading_page_error, false);
        }
    }

    private final ClipboardManager.OnPrimaryClipChangedListener clipboarPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            ClipboardManager cm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
            backSettingQuote(item.getText().toString());
        }
    };
}
