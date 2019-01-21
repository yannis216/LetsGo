package com.example.android.letsgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ElementEditActivity extends AppCompatActivity {
    EditText mTitleEdit;
    String createdTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_edit);

        mTitleEdit = findViewById(R.id.et_title);       

        createdTitle = mTitleEdit.getText().toString();


    }
}
