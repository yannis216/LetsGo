package com.example.android.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
        Element displayedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_detail);

        mTitleView = findViewById(R.id.tv_element_title);

        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("CreatedElement");

        String elementTitle = displayedElement.getTitle();
        populateUi(elementTitle);

    }

    private void populateUi(String title){
        mTitleView.setText(title);
    }

}
