package com.example.android.letsgo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class ElementEditActivity extends AppCompatActivity {
    EditText mTitleEdit;
    EditText mUsedForEdit;
    Button mPicturePicker;
    EditText mPictureUrlEdit;
    String pictureUrl;
    EditText mVideoUrlEdit;
    NumberPicker mMinHumansPicker;
    Button mSaveButton;
    int PICK_PHOTO_FOR_ELEMENT = 2;


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
        mPictureUrlEdit =findViewById(R.id.et_element_picture_url);

        mMinHumansPicker.setMinValue(1);
        mMinHumansPicker.setMaxValue(32);
        //TODO Could add a 32+ Value -> Maybe use .setDisplayedValues


        mPicturePicker.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View view) {
                  Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                  getIntent.setType("image/*");

                  Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  pickIntent.setType("image/*");

                  Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                  chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                  startActivityForResult(chooserIntent, PICK_PHOTO_FOR_ELEMENT);
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
        return new Element(createdTitle,createdUsedForEdit, pictureUrl, createdVideoUrl,createdMinHumans);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_ELEMENT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //TODO Display an error
                return;
            }
            try {
                InputStream inputStream = ElementEditActivity.this.getContentResolver().openInputStream(data.getData());
                Uri inputUri = data.getData();
                pictureUrl = inputUri.toString();
                mPictureUrlEdit.setText(pictureUrl);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
