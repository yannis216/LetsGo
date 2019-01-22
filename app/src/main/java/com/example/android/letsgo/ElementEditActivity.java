package com.example.android.letsgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.android.letsgo.Utils.ImagePicker;

public class ElementEditActivity extends AppCompatActivity {
    EditText mTitleEdit;
    EditText mUsedForEdit;
    Button mPicturePicker;
    EditText mVideoUrlEdit;
    NumberPicker mMinHumansPicker;
    Button mSaveButton;
    private static final int PICK_IMAGE_ID = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_edit);

        mTitleEdit = findViewById(R.id.et_element_title);
        mUsedForEdit = findViewById(R.id.et_element_usedFor);
        mPicturePicker =findViewById(R.id.bn_element_picture_picker);
        mVideoUrlEdit=findViewById(R.id.et_element_videoUrl);
        mMinHumansPicker=findViewById(R.id.np_element_min_humans);
        mSaveButton = findViewById(R.id.bn_element_save);

        mMinHumansPicker.setMinValue(1);
        mMinHumansPicker.setMaxValue(32);
        //TODO Could add a 32+ Value -> Maybe use .setDisplayedValues


        mPicturePicker.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View view) {
                  Intent chooseImageIntent = ImagePicker.getPickImageIntent(ElementEditActivity.this);
                  startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
              }
          });


        mSaveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Element createdElement = getElementFromInputs();
                Intent intent = new Intent(ElementEditActivity.this, ElementDetailActivity.class);
                intent.putExtra("CreatedElement", createdElement);
                startActivity(intent);

            }
        });
    }

    private Element getElementFromInputs(){
        String createdTitle = mTitleEdit.getText().toString();
        String createdUsedForEdit = mUsedForEdit.getText().toString();
        String createdVideoUrl = mVideoUrlEdit.getText().toString();
        int createdMinHumans = mMinHumansPicker.getValue();

        return new Element(createdTitle,createdUsedForEdit,createdVideoUrl,createdMinHumans);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                // TODO use bitmap
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
