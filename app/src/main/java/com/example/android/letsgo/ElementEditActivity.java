package com.example.android.letsgo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


// TODO There is no Input Validation here. Think about security concerns etc
public class ElementEditActivity extends AppCompatActivity {
    ImageView mPicture;
    EditText mTitleEdit;
    EditText mShortDescEdit;
    //TODO Set Maximum number of character for edittexts that result in chips.
    // Otherwise error on display because long chips dont fit in a row and cant get textwrapped
    EditText mUsedForEdit;
    Button mUsedForAdder;
    ChipGroup mUsedForChipGroup;
    ImageButton mPicturePicker;
    String pictureUrl;
    EditText mVideoUrlEdit;
    //TODO Set Maximum number of character for edittexts that result in chips.
    // Otherwise error on display because long chips dont fit in a row and cant get textwrapped
    EditText mMaterialEdit;
    //TODO Should handle this in AlertDialog at some point
    CheckBox mMaterialGetsConsumed;
    Button mMaterialCommiter;
    NumberPicker mMinHumansPicker;
    Button mSaveButton;
    ChipGroup mMaterialChipGroup;
    List<Material> mCreatedNeededMaterials = new ArrayList<Material>();
    int PICK_PHOTO_FOR_ELEMENT = 2;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_edit);

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        mPicture =findViewById(R.id.iv_element_edit_picture);
        mTitleEdit = findViewById(R.id.et_element_title);
        mShortDescEdit=findViewById(R.id.et_element_edit_shortDesc);
        mUsedForEdit = findViewById(R.id.et_element_usedFor);
        mUsedForAdder =findViewById(R.id.bn_element_usedFor_add);
        mUsedForChipGroup =findViewById(R.id.cg_element_edit_usedFor_chips);
        mPicturePicker =findViewById(R.id.bn_element_picture_picker);
        mVideoUrlEdit=findViewById(R.id.et_element_videoUrl);
        mMinHumansPicker=findViewById(R.id.np_element_min_humans);
        mSaveButton = findViewById(R.id.bn_element_save);
        mMaterialEdit=findViewById(R.id.et_element_material);
        mMaterialGetsConsumed=findViewById(R.id.cb_element_material_gets_consumed);
        mMaterialCommiter=findViewById(R.id.bn_element_material_commit);
        mMaterialChipGroup=findViewById(R.id.cg_element_edit_material_chips);

        mMinHumansPicker.setMinValue(1);
        mMinHumansPicker.setMaxValue(32);
        //TODO Could add a 32+ Value -> Maybe use .setDisplayedValues
        mUsedForEdit.setOnEditorActionListener(new DoneOnEditorActionListener());

        setOnClickListeners();

    }

    private Element getElementFromInputs(){
        String createdTitle = mTitleEdit.getText().toString();
        String createdShortDesc =mShortDescEdit.getText().toString();
        String createdVideoUrl = mVideoUrlEdit.getText().toString();
        int createdMinHumans = mMinHumansPicker.getValue();
        List<String> createdUsedFor = generateListFromChipGroup(mUsedForChipGroup);
        String timeOnSavePressed= String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));


        return new Element(createdTitle, createdShortDesc, createdUsedFor, pictureUrl, createdVideoUrl, createdMinHumans, mCreatedNeededMaterials, timeOnSavePressed);
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
                InputStream inputStream = ElementEditActivity.this.getContentResolver().openInputStream(data.getData()); //TODO delete is safe?
                Uri inputUri = data.getData();
                pictureUrl = inputUri.toString();
                if(pictureUrl!= null){
                    PictureUtil pictureUtil = new PictureUtil(ElementEditActivity.this);
                    pictureUtil.initializePictureWithColours(pictureUrl, mPicture, mTitleEdit);
                }
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
                handleNewUsedForChipAdded();
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
                saveElementToDatabase(createdElement);

                Intent intent = new Intent(ElementEditActivity.this, ElementDetailActivity.class);
                intent.putExtra("CreatedElement", createdElement);
                startActivity(intent);

            }
        });

        mMaterialCommiter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                handleNewMaterialChipAdded();
            }
        });
    }

    private void handleNewUsedForChipAdded(){
        String newUsedForChipText = mUsedForEdit.getText().toString();
        final Chip entryChip = getChip(mUsedForChipGroup, newUsedForChipText);
        mUsedForChipGroup.addView(entryChip);
        mUsedForEdit.getText().clear();
    }

    private void handleNewMaterialChipAdded(){
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

    // TODO Könnte man glaub auch irgendwie in Utils packen
    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(v == mUsedForEdit) {
                    //InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    //TODO Add upper lines to close keyboard after click on Done? Test with users whats better
                    handleNewUsedForChipAdded();
                    return true;
                }
            }
            return false;
        }
    }

    public void saveElementToDatabase(Element newElement){
        db.collection("elements")
                .add(newElement)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("saveElement", "Document Snap added with id:" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveElement", "Error adding document", e);
                    }
                });

    }

}
