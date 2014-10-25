package com.nww.munich.newssharer.sharing;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nww.munich.newssharer.R;
import com.nww.munich.newssharer.views.PersonView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;

import static android.view.View.OnClickListener;
import static android.widget.LinearLayout.LayoutParams;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class MainFragment extends Fragment implements OnClickListener {

    private TextView titleTextView;
    private LinearLayout peopleLinearLayout;
    private TextView quoteTextView;
    private LinearLayout tagsLinearLayout;
    private LinearLayout topicsLineraLayout;
    private Button addQuoteButton;
    private Button changeQuoteButton;
    private ArrayList<PersonView> personViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_main, null);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        peopleLinearLayout = (LinearLayout) view.findViewById(R.id.peopleLinearLayout);
        quoteTextView = (TextView) view.findViewById(R.id.quoteTextView);
        tagsLinearLayout = (LinearLayout) view.findViewById(R.id.tagsLinearLayout);
        topicsLineraLayout = (LinearLayout) view.findViewById(R.id.topicsLinearLayout);

        quoteTextView.setText(((ShareActivity) getActivity()).articleQuote);
        addQuoteButton = (Button) view.findViewById(R.id.addQuoteButton);
        addQuoteButton.setOnClickListener(this);
        changeQuoteButton = (Button) view.findViewById(R.id.changeQuoteButton);
        changeQuoteButton.setOnClickListener(this);

        view.findViewById(R.id.addTagButton).setOnClickListener(this);
        view.findViewById(R.id.addTopicsButton).setOnClickListener(this);

        ShareActivity activity = (ShareActivity) getActivity();
        titleTextView.setText(activity.articleTitle);

        addPersons();
        return view;
    }

    private void addPersons() {
        Resources res = getResources();
        Object[][] persons = {
                {"Angela Merkel", getString(R.string.chancellor_of_germany), BitmapFactory.decodeResource(res, R.drawable.merkel)},
                {"Barack Obama", getString(R.string.president_of_united_states), BitmapFactory.decodeResource(res, R.drawable.obama)},
                {"Vladimir Putin", getString(R.string.president_of_russia), BitmapFactory.decodeResource(res, R.drawable.vladimir)},
                {"François Hollande", getString(R.string.president_of_france), BitmapFactory.decodeResource(res, R.drawable.francois)}
        };
        personViews = new ArrayList<PersonView>(persons.length);

        for (Object[] record: persons)
            addPeopleView(record);
    }

    private void addPeopleView(Object[] record) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        PersonView view = new PersonView(getActivity());
        view.setName((String) record[0]);
        view.setPosition((String) record[1]);
        view.setProfileBitmap((Bitmap) record[2]);

        peopleLinearLayout.addView(view, layoutParams);
        personViews.add(view);
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
