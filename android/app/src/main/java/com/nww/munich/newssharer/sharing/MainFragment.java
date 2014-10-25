package com.nww.munich.newssharer.sharing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nww.munich.newssharer.Constants;
import com.nww.munich.newssharer.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.view.View.OnClickListener;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class MainFragment extends Fragment implements OnClickListener {

    private GridLayout peopleGridView;
    private TextView quoteTextView;
    private LinearLayout tagsLinearLayout;
    private LinearLayout topicsLineraLayout;
    private Button addQuoteButton;
    private Button changeQuoteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_main, null);

        peopleGridView = (GridLayout) view.findViewById(R.id.peopleGridView);
        quoteTextView = (TextView) view.findViewById(R.id.quoteTextView);
        tagsLinearLayout = (LinearLayout) view.findViewById(R.id.tagsLinearLayout);
        topicsLineraLayout = (LinearLayout) view.findViewById(R.id.topicsLinearLayout);

        quoteTextView.setText(((ShareActivity) getActivity()).articleQuote);
        addQuoteButton = (Button) view.findViewById(R.id.addQuoteButton);
        addQuoteButton.setOnClickListener(this);
        changeQuoteButton = (Button) view.findViewById(R.id.changeQuoteButton);
        changeQuoteButton.setOnClickListener(this);

        view.findViewById(R.id.addPeopleButton).setOnClickListener(this);
        view.findViewById(R.id.addTagButton).setOnClickListener(this);
        view.findViewById(R.id.addTopicsButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ShareActivity activity = (ShareActivity)getActivity();
        Request.Builder builder = new Request.Builder();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPeopleButton:
                handleAddPeople();
                break;
            case R.id.addQuoteButton:
            case R.id.changeQuoteButton:
                handelAddQuote();
                break;
            case R.id.addTagButton:
                handleAddTag();
                break;
            case R.id.addTopicsButton:
                handleAddTopics();
                break;
            default:
                break;
        }
    }

    private void handleAddTopics() {
        throw new RuntimeException("Not yet implemented");
    }

    private void handleAddTag() {
        throw new RuntimeException("Not yet implemented");
    }

    private void handelAddQuote() {
        Fragment textSelectionFragment = new TextSelectionFragment();
        textSelectionFragment.setTargetFragment(this, 0);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, textSelectionFragment)
                .addToBackStack("text-selection-fragment")
                .commit();
    }

    private void handleAddPeople() {
        throw new RuntimeException("Not yet implemented");
    }

    public void setQuote(String quote) {
        addQuoteButton.setVisibility(View.INVISIBLE);
        changeQuoteButton.setVisibility(View.VISIBLE);
        quoteTextView.setText(quote);
        ((ShareActivity) getActivity()).articleQuote = quote;
    }
}
