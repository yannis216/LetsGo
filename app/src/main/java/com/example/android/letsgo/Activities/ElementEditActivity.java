package com.example.android.letsgo.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;


// TODO There is no Input Validation here. Think about security concerns etc
public class ElementEditActivity extends BaseNavDrawActivity {
    EditText mTitleEdit;
    EditText mShortDescEdit;
    //TODO Set Maximum number of character for edittexts that result in chips.
    // Otherwise error on display because long chips dont fit in a row and cant get textwrapped
    EditText mUsedForEdit;
    Button mUsedForAdder;
    ChipGroup mUsedForChipGroup;

    EditText mVideoUrlEdit;
    //TODO Set Maximum number of character for edittexts that result in chips.
    // Otherwise error on display because long chips dont fit in a row and cant get textwrapped
    EditText mMaterialEdit;
    //TODO Should handle this in AlertDialog at some point
    CheckBox mMaterialGetsConsumed;
    Button mMaterialCommiter;
    Button mSaveButton;
    ImageButton mMinHumansStarter;
    NumberPicker mMinHumansPicker;
    TextView mMinHumansTextView;
    ChipGroup mMaterialChipGroup;
    FloatingActionButton fab;
    ArrayList<Material> materials = new ArrayList<Material>();

    ImageButton mPickPictureButton;
    String pictureUrl;
    ImageView mPicture;
    FirebaseStorage storage;
    InputStream inputStream;
    DocumentReference elementReference;
    DocumentReference updateElementRef;

    int PICK_PHOTO_FOR_ELEMENT = 2;

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    Element editableElement;
    String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_element_edit, (ViewGroup) findViewById(R.id.content_frame));
        setContentView(R.layout.activity_element_edit);

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        mPicture =findViewById(R.id.iv_element_edit_picture);
        mTitleEdit = findViewById(R.id.et_element_title);
        mShortDescEdit=findViewById(R.id.et_element_edit_shortDesc);
        mUsedForEdit = findViewById(R.id.et_element_usedFor);
        mUsedForAdder =findViewById(R.id.bn_element_usedFor_add);
        mUsedForChipGroup =findViewById(R.id.cg_element_edit_usedFor_chips);
        mPickPictureButton =findViewById(R.id.bn_element_picture_picker);
        mMinHumansStarter = findViewById(R.id.ib_element_edit_min_humans_starter);
        mMinHumansTextView = findViewById(R.id.tv_element_edit_num_humans);
        fab = findViewById(R.id.fab_element_edit);


        //mMinHumansPicker.setMinValue(1);
        //mMinHumansPicker.setMaxValue(32);
        //TODO Could add a 32+ Value -> Maybe use .setDisplayedValues

        mMinHumansStarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMinHumansDialog();
            }
        });


        mUsedForEdit.setOnEditorActionListener(new DoneOnEditorActionListener());

        Intent intent = getIntent();
        editableElement = (Element) intent.getSerializableExtra("elementToEdit");
        if(editableElement!= null){
            materials = (ArrayList<Material>) intent.getSerializableExtra("elementMaterials");
            initiateUpdateMode();
        }else{
            mode = "create";
        }

        setOnClickListeners();

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
                    for(int i = 0; i< materials.size(); i++){
                        if(currentChipText.equals(materials.get(i).getTitle())){
                            Log.e("Deleted", "from List will be:"+ materials.get(i).getTitle() );
                            materials.remove(i);
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


        mPickPictureButton.setOnClickListener(new View.OnClickListener() {

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



        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                List<String> materialIds = saveMaterialsToDatabase(materials);
                Element createdElement = getElementFromInputs(materialIds);
                if(mode.equals("update")){
                    prepareUpdateElementInDatabase(createdElement);
                }else{
                    prepareSaveElementToDatabase(createdElement);
                }


                //TODO Hand over List of Materials here via Intent to save time and Databasereads in DetailActivity
                Intent intent = new Intent(ElementEditActivity.this, ElementDetailActivity.class);
                intent.putExtra("element", createdElement);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_ELEMENT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //TODO Display an error
                return;
            }else {
                try {
                    inputStream = ElementEditActivity.this.getContentResolver().openInputStream(data.getData()); //TODO delete is safe?
                    Uri inputUri = data.getData();
                    pictureUrl = inputUri.toString();
                    if (pictureUrl != null) {
                        PictureUtil pictureUtil = new PictureUtil(ElementEditActivity.this, mPicture, mTitleEdit);
                        pictureUtil.loadTitlePictureIntoImageView(pictureUrl);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleNewUsedForChipAdded(){
        String newUsedForChipText = mUsedForEdit.getText().toString();
        mUsedForEdit.getText().clear();
        addUsedForChip(newUsedForChipText);

    }

    private void addUsedForChip(String usedForTitle){
        final Chip entryChip = getChip(mUsedForChipGroup, usedForTitle);
        mUsedForChipGroup.addView(entryChip);
    }

    private void handleNewMaterialChipAdded(){
        String newMaterialTitle = mMaterialEdit.getText().toString();
        boolean newMaterialGetsConsumed = mMaterialGetsConsumed.isChecked();
        List<String> newMaterialShoppingLinks = new ArrayList<>();
        Material newMaterial = new Material(newMaterialTitle, newMaterialGetsConsumed, newMaterialShoppingLinks);
        materials.add(newMaterial);

        addMaterialChip(newMaterialTitle);

    }

    private void addMaterialChip(String newMaterialTitle){
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

    private Element getElementFromInputs(List<String> materialIds){
        String createdTitle = mTitleEdit.getText().toString();
        String createdShortDesc =mShortDescEdit.getText().toString();
        int createdMinHumans = Integer.parseInt(mMinHumansTextView.getText().toString());
        List<String> createdUsedFor = generateListFromChipGroup(mUsedForChipGroup);
        String timeOnSavePressed= String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        Element element = new Element(createdTitle, createdShortDesc, createdUsedFor,createdMinHumans, materialIds, timeOnSavePressed, authUser.getUid());
        if(mode.equals("update")){
            element.setElementId(editableElement.getElementId());
        }
        return element;
    }

    public void prepareSaveElementToDatabase(Element newElement){
        elementReference = db.collection("elements").document();
        String elementId = elementReference.getId();
        newElement.setElementId(elementId);
        if(!(inputStream ==null)){
            savePictureToStorage(newElement);
        }else{
            continueSaveElementToDatabase(newElement);
        }

    }
    private void continueSaveElementToDatabase(Element newElement){
        elementReference
                .set(newElement)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveElement", "Error adding document", e);
                    }
                });

    }

    public void prepareUpdateElementInDatabase(Element updatedElement){
        updateElementRef = db.collection("elements").document(updatedElement.getElementId());
        if(!(inputStream ==null)){
            savePictureToStorage(updatedElement);
        }else{
            continueUpdateElementInDatabase(updatedElement);
        }
    }

    public void continueUpdateElementInDatabase(final Element updatedElement){
        updateElementRef
                .set(updatedElement)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("editElement", "Document Snap added");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("editElement", "Error adding document", e);
                    }
                });


        CollectionReference modulRef = db.collection("moduls");
        //TODO May limit this to only editing Moduls of this user (And prompt/ask other users if they wanna update their moduls)
        modulRef.whereArrayContains("elementIds", updatedElement.getElementId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //TODO May ask the user here for which moduls he wants the update
                        final ArrayList<Modul> moduls = new ArrayList<>();
                        if(task.isSuccessful()){
                            int sizeOfResultSet = task.getResult().size();
                            Log.e("List of Moduls", ""+sizeOfResultSet);
                            if(sizeOfResultSet != 0){
                                WriteBatch batch = db.batch();
                                for(final QueryDocumentSnapshot document : task.getResult()){
                                    Modul modul = document.toObject(Modul.class);
                                    moduls.add(modul);
                                }
                                Log.e("FoundModuls", ""+moduls);
                                for(Modul modul : moduls){
                                    List<ModulElement> modulElements = modul.getModulElements();
                                    for(ModulElement oldModulElement : modulElements){
                                        if(oldModulElement.getElementId().equals(updatedElement.getElementId())){
                                            Log.e("Found sameElementId", ""+updatedElement.getElementId());
                                            ModulElement updatedModulElement = new ModulElement(
                                                    updatedElement, oldModulElement.getMultiplier(), oldModulElement.getHint(), oldModulElement.getSourceElementId(), oldModulElement.getOrderInModul());
                                            modulElements.set(oldModulElement.getOrderInModul(), updatedModulElement);
                                        }
                                    }
                                    modul.setModulElements(modulElements);
                                    DocumentReference updatedModulRef = db.collection("moduls").document(modul.getId());
                                    batch.set(updatedModulRef, modul);
                                }
                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.e("Batch Write", "Completed successfully");
                                        }

                                    }
                                });
                            }

                        }

                    }
                });

    }

    public List<String> saveMaterialsToDatabase(List<Material> newMaterials){
        List<String> materialIds= new ArrayList<>();
        for(int i = 0; i<newMaterials.size(); i++){
            Material newMaterial = newMaterials.get(i);
            String materialId = db.collection("materials").document().getId();
            materialIds.add(materialId);
            db.collection("materials").document(materialId)
                    .set(newMaterial);

        }
        return materialIds;
    }

    private void savePictureToStorage(final Element element){
        final StorageReference elementImageRef = storage.getReference().child("images/"+authUser.getUid()+"/elements/"+element.getElementId()+"_originalPicture");

        UploadTask uploadTask = elementImageRef.putStream(inputStream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, R.string.picture_upload_failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                //TODO Give some Visual Feedback on Loading Process and so on
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return elementImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    element.setPictureUrl(downloadUrl);
                    if(mode.equals("update")){
                        continueUpdateElementInDatabase(element);
                    }else{
                        continueSaveElementToDatabase(element);
                    }
                    Log.e("DownloadUrl", downloadUrl);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }

    private void initiateUpdateMode(){
        mTitleEdit.setText(editableElement.getTitle());
        mShortDescEdit.setText(editableElement.getShortDesc());
        mMinHumansTextView.setText(String.valueOf(editableElement.getMinNumberOfHumans()));
        for(String usedFor:editableElement.getUsedFor()){
            addUsedForChip(usedFor);
        }
        for(Material material : materials){
            addMaterialChip(material.getTitle());
        }
        mode = "update";
    }

    public void showMinHumansDialog(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(
                R.layout.dialog_element_min_people, viewGroup, false);

        mMinHumansPicker = dialogView.findViewById(R.id.np_element_min_humans);
        mMinHumansPicker.setMaxValue(32);
        mMinHumansPicker.setMinValue(0);
        mMinHumansPicker.setValue(Integer.parseInt(mMinHumansTextView.getText().toString()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.element_edit_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String minHumansString = String.valueOf(mMinHumansPicker.getValue());
                mMinHumansTextView.setText(minHumansString);
            }
        });
        builder.setNegativeButton(R.string.element_edit_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
