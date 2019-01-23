package com.example.android.letsgo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.android.letsgo.Utils.ImagePicker;
import com.example.android.letsgo.Utils.PermissionRequestWriteExternal;

import java.io.ByteArrayOutputStream;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.android.letsgo.Utils.PermissionRequestWriteExternal.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class ElementEditActivity extends AppCompatActivity {
    EditText mTitleEdit;
    EditText mUsedForEdit;
    Button mPicturePicker;
    EditText mPictureUrlEdit;
    String pictureUrl;
    EditText mVideoUrlEdit;
    NumberPicker mMinHumansPicker;
    Button mSaveButton;
    private static final int PICK_IMAGE_ID = 23;
    Bitmap bitmap;


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

        return new Element(createdTitle,createdUsedForEdit, pictureUrl, createdVideoUrl,createdMinHumans);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                // TODO use bitmap
                if (PermissionRequestWriteExternal.checkPermissionWRITE_EXTERNAL_STORAGE(this)) {
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    pictureUrl = getRealPathFromURI(tempUri);
                    mPictureUrlEdit.setText(pictureUrl);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    pictureUrl = getRealPathFromURI(tempUri);
                    mPictureUrlEdit.setText(pictureUrl);
                } else {
                    Toast.makeText(ElementEditActivity.this, "Access to pictures denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
