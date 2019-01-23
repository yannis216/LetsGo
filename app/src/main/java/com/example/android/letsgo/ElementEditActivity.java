package com.example.android.letsgo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ElementEditActivity extends AppCompatActivity {
    EditText mTitleEdit;
    EditText mUsedForEdit;
    Button mUsedForAdder;
    ChipGroup mUsedForChipGroup;
    Button mPicturePicker;
    EditText mPictureUrlEdit;
    String pictureUrl;
    EditText mVideoUrlEdit;
    EditText mMaterialEdit;
    CheckBox mMaterialGetsConsumed;
    Button mMaterialCommiter;
    NumberPicker mMinHumansPicker;
    Button mSaveButton;
    ChipGroup mMaterialChipGroup;
    List<Material> mCreatedNeededMaterials = new ArrayList<Material>();
    int PICK_PHOTO_FOR_ELEMENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_edit);

        mTitleEdit = findViewById(R.id.et_element_title);
        mUsedForEdit = findViewById(R.id.et_element_usedFor);
        mUsedForAdder =findViewById(R.id.bn_element_usedFor_add);
        mUsedForChipGroup =findViewById(R.id.cg_element_edit_usedFor_chips);
        mPicturePicker =findViewById(R.id.bn_element_picture_picker);
        mVideoUrlEdit=findViewById(R.id.et_element_videoUrl);
        mMinHumansPicker=findViewById(R.id.np_element_min_humans);
        mSaveButton = findViewById(R.id.bn_element_save);
        mPictureUrlEdit =findViewById(R.id.et_element_picture_url);
        mMaterialEdit=findViewById(R.id.et_element_material);
        mMaterialGetsConsumed=findViewById(R.id.cb_element_material_gets_consumed);
        mMaterialCommiter=findViewById(R.id.bn_element_material_commit);
        mMaterialChipGroup=findViewById(R.id.cg_element_edit_material_chips);

        mMinHumansPicker.setMinValue(1);
        mMinHumansPicker.setMaxValue(32);
        //TODO Could add a 32+ Value -> Maybe use .setDisplayedValues

        setOnClickListeners();

    }

    private Element getElementFromInputs(){
        String createdTitle = mTitleEdit.getText().toString();
        String createdVideoUrl = mVideoUrlEdit.getText().toString();
        int createdMinHumans = mMinHumansPicker.getValue();
        List<String> createdUsedFor = generateListFromChipGroup(mUsedForChipGroup);


        return new Element(createdTitle, createdUsedFor, pictureUrl, createdVideoUrl, createdMinHumans, mCreatedNeededMaterials);
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

    private Chip getChip(final ChipGroup entryChipGroup, String text) {
        final Chip chip = new Chip(this);
        if(entryChipGroup == mUsedForChipGroup) {
            chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.used_for_edit_chip));
        }else if(entryChipGroup == mMaterialChipGroup){
            chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.material_edit_chip));
        }
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //For Materials: Find the Material with same title as deleted chip and delete it from list so it does not get passed to other activity
                if(entryChipGroup == mMaterialChipGroup){
                    String currentChipText = chip.getText().toString();
                    for(int i = 0; i<mCreatedNeededMaterials.size(); i++){
                        if(currentChipText.equals(mCreatedNeededMaterials.get(i).getTitle())){
                            Log.e("Deleted", "from List will be:"+mCreatedNeededMaterials.get(i).getTitle() );
                            mCreatedNeededMaterials.remove(i);
                            break;
                        }
                    }
                }
                entryChipGroup.removeView(chip);
            }
        });
        return chip;
    }
    private List<String> generateListFromChipGroup(ChipGroup chipGroup){
        List<String> strings = new ArrayList<String>();

        //Do this for when the user forgets to press the add button on his last chip entry
        if(!mUsedForEdit.getText().toString().equals("")){
            strings.add(mUsedForEdit.getText().toString());
            //TODO May have to ask user if he wants that
        }
        for(int i=0; i<chipGroup.getChildCount(); i++){
            Chip chip =(Chip) chipGroup.getChildAt(i);
            strings.add(chip.getText().toString());
        }
        Log.e("StringsList", ""+strings);
        return strings;
    }

    public void setOnClickListeners(){
        mUsedForAdder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String newUsedForChipText = mUsedForEdit.getText().toString();
                final Chip entryChip = getChip(mUsedForChipGroup, newUsedForChipText);
                mUsedForChipGroup.addView(entryChip);
                mUsedForEdit.getText().clear();
            }
        });


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

        mMaterialCommiter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String newMaterialTitle = mMaterialEdit.getText().toString();
                boolean newMaterialGetsConsumed = mMaterialGetsConsumed.isChecked();
                List<String> newMaterialShoppingLinks = new ArrayList<>();
                Material newMaterial = new Material(newMaterialTitle, newMaterialGetsConsumed, newMaterialShoppingLinks);
                mCreatedNeededMaterials.add(newMaterial);

                final Chip entryChip = getChip(mMaterialChipGroup, newMaterialTitle);
                mMaterialChipGroup.addView(entryChip);
                mMaterialEdit.getText().clear();
                mMaterialGetsConsumed.setChecked(false);


            }
        });
    }

}
