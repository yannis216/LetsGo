package com.example.android.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
    TextView mUsedForView;
    TextView mThumbnailUrlView;
    TextView mVideoUrlView;
    TextView mMinHumansView;
    Element displayedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_detail);

        mTitleView = findViewById(R.id.tv_element_title);
        mUsedForView=findViewById(R.id.tv_element_usedFor);
        mThumbnailUrlView=findViewById(R.id.tv_element_thumbnailUrl);
        mVideoUrlView=findViewById(R.id.tv_element_videoUrl);
        mMinHumansView=findViewById(R.id.tv_element_min_humans);

        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("CreatedElement");

        populateUi(displayedElement);

    }

    private void populateUi(Element element){
        mTitleView.setText(element.getTitle());
        mUsedForView.setText(element.getUsedFor());
        mThumbnailUrlView.setText(element.getThumbnailUrl());
        mVideoUrlView.setText(element.getVideoUrl());
        mMinHumansView.setText(String.valueOf(element.getMinNumberOfHumans()));

    }

}
