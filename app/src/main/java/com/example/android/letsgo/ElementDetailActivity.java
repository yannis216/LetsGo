package com.example.android.letsgo;

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

        mTitleView = findViewById(R.id.tv_title);

        displayedElement = new Element("Pushups");
        String elementTitle = displayedElement.getTitle();


        populateUi(elementTitle);

    }

    private void populateUi(String title){
        mTitleView.setText(title);
    }

}
