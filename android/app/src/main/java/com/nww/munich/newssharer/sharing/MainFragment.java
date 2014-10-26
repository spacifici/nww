package com.nww.munich.newssharer.sharing;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nww.munich.newssharer.Constants;
import com.nww.munich.newssharer.R;
import com.nww.munich.newssharer.views.PersonView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    // private LinearLayout tagsLinearLayout;
    private LinearLayout topicsLineraLayout;
    private Button addQuoteButton;
    private Button changeQuoteButton;
    private ArrayList<PersonView> personViews;
    private ArrayList<CheckBox> topicsCheckBoxes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_main, null);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        peopleLinearLayout = (LinearLayout) view.findViewById(R.id.peopleLinearLayout);
        quoteTextView = (TextView) view.findViewById(R.id.quoteTextView);
        // tagsLinearLayout = (LinearLayout) view.findViewById(R.id.tagsLinearLayout);
        topicsLineraLayout = (LinearLayout) view.findViewById(R.id.topicsLinearLayout);

        quoteTextView.setText(((ShareActivity) getActivity()).articleQuote);
        addQuoteButton = (Button) view.findViewById(R.id.addQuoteButton);
        addQuoteButton.setOnClickListener(this);
        changeQuoteButton = (Button) view.findViewById(R.id.changeQuoteButton);
        changeQuoteButton.setOnClickListener(this);

        // view.findViewById(R.id.addTagButton).setOnClickListener(this);

        ShareActivity activity = (ShareActivity) getActivity();
        titleTextView.setText(activity.articleTitle);

        addPersons();
        setupTopics();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_item:
                handleSubmit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleSubmit() {
        // check if we fulfill the form
        boolean topics = false;
        boolean people = false;
        ArrayList<Integer> topicIndexes = new ArrayList<Integer>();
        ArrayList<Integer> personIndexes = new ArrayList<Integer>();

        for (int ix = 0; ix < personViews.size(); ix++) {
            boolean checked = personViews.get(ix).isChecked();
            people |= checked;
            if (checked)
                personIndexes.add(ix);
        }

        for (int ix = 0; ix < topicsCheckBoxes.size(); ix++) {
            boolean checked = topicsCheckBoxes.get(ix).isChecked();
            topics |= checked;
            if (checked)
                topicIndexes.add(ix);
        }

        if (topics && people) {
            ShareActivity activity = (ShareActivity) getActivity();
            activity.topicIndexes = topicIndexes;
            activity.personIndexes = personIndexes;
            activity.submitData();
        } else {
            ((ShareActivity) getActivity())
                    .showErrorDialog(R.string.need_a_topic_and_a_person, false);
        }
    }

    private void setupTopics() {
        ShareActivity activity = (ShareActivity) getActivity();
        try {
            JSONArray topics = activity.jsonArticle.getJSONObject("meta").getJSONArray("topics");
            topicsCheckBoxes = new ArrayList<CheckBox>(topics.length());
            for (int ix = 0; ix < topics.length(); ix++) {
                JSONObject topic = topics.getJSONObject(ix);
                String name = topic.getString("name");
                CheckBox checkBox = new CheckBox(getActivity());

                checkBox.setText(name);
                checkBox.setTextColor(getResources().getColor(R.color.default_text_color));
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                int margin = getResources().getDimensionPixelSize(R.dimen.gridviews_padding);
                layoutParams.setMargins(margin, margin, margin, margin);
                layoutParams.gravity = Gravity.LEFT;
                topicsCheckBoxes.add(checkBox);

                topicsLineraLayout.addView(checkBox, layoutParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void old_addPersons() {
        personViews = new ArrayList<PersonView>(Constants.PERSONS.length);

        for (Object[] record: Constants.PERSONS)
            addPeopleView(record);
    }

    private void addPersons() {
        JSONObject article = ((ShareActivity) getActivity()).jsonArticle;

        try {
            JSONArray people = article.getJSONObject("meta").getJSONArray("people");
            personViews = new ArrayList<PersonView>(people.length());
            for (int ix = 0; ix < people.length(); ix++) {
                addPeopleViewUsingJSON(people.getJSONObject(ix));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addPeopleViewUsingJSON(JSONObject person) throws JSONException {
        String name = person.getString("name");
        String handle = person.getString("handle");
        String position = person.getString("position");

        Integer photoID = Constants.HANDLE_TO_PHOTO.get(handle);
        if (photoID == null) {
            photoID = new Integer(R.drawable.anonymous);
        }
        Resources res = getResources();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        PersonView view = new PersonView(getActivity());
        view.setName(name);
        view.setPosition(position);
        Bitmap bm = BitmapFactory.decodeResource(res, photoID);
        view.setProfileBitmap(bm);

        peopleLinearLayout.addView(view, layoutParams);
        personViews.add(view);
    }

    private void addPeopleView(Object[] record) {
        Resources res = getResources();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        PersonView view = new PersonView(getActivity());
        view.setName((String) record[0]);
        view.setPosition(res.getString((Integer) record[1]));
        Bitmap bm = BitmapFactory.decodeResource(res, ((Integer) record[2]));
        view.setProfileBitmap(bm);

        peopleLinearLayout.addView(view, layoutParams);
        personViews.add(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.addPeopleButton:
//                handleAddPeople();
//                break;
            case R.id.addQuoteButton:
            case R.id.changeQuoteButton:
                handelAddQuote();
                break;
//            case R.id.addTagButton:
//                handleAddTag();
//                break;
            default:
                break;
        }
    }

    // private void handleAddTag() {
    //     throw new RuntimeException("Not yet implemented");
    // }

    private void handelAddQuote() {
        Fragment textSelectionFragment = new TextSelectionFragment();
        textSelectionFragment.setTargetFragment(this, 0);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, textSelectionFragment)
                .addToBackStack("text-selection-fragment")
                .commit();
    }

    public void setQuote(String quote) {
        addQuoteButton.setVisibility(View.INVISIBLE);
        changeQuoteButton.setVisibility(View.VISIBLE);
        quoteTextView.setText(quote);
        ((ShareActivity) getActivity()).articleQuote = quote;
    }
}
