package com.nww.munich.newssharer.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nww.munich.newssharer.R;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class PersonView extends FrameLayout {

    private CheckBox checkBox;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView positionTextView;

    public PersonView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.person_view_layout, null);

        checkBox = (CheckBox) view.findViewById(R.id.selectedCheckbox);
        profileImageView = (ImageView) view.findViewById(R.id.profileImageView);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        positionTextView = (TextView) view.findViewById(R.id.positionTextView);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        addView(view, params);
    }

    public void setName(String name) {
        nameTextView.setText(name);
    }

    public void setPosition(String position) {
        positionTextView.setText(position);
    }

    public void setProfileBitmap(Bitmap bm) {
        profileImageView.setImageBitmap(bm);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }
}
