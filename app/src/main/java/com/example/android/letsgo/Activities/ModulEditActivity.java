package com.example.android.letsgo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.letsgo.Adapter.ModulElementEditListAdapter;
import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.Classes.ModulElementMultiplier;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnModulElementListChangedListener;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnStartDragListener;
import com.example.android.letsgo.Utils.TouchHelper.SimpleItemTouchHelperCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulEditActivity extends BaseNavDrawActivity implements ModulElementEditListAdapter.ModulElementOnClickHandler, OnModulElementListChangedListener, OnStartDragListener, ModulElementEditListAdapter.ClickAdapterListener {

    Modul currentModul;
    List<ModulElement> modulElements = new ArrayList<ModulElement>();
    List<Element> addElements;
    EditText mTitleView;
    FloatingActionButton fab;
    ImageButton mAddImageButton;
    ImageView mImageView;
    ProgressBar mSaveProgressBar;

    RecyclerView mRvModulElements;
    RecyclerView.LayoutManager mLayoutManager;
    ModulElementEditListAdapter mAdapter;
    DrawerLayout drawerLayout;
    CoordinatorLayout coordinatorLayout;

    String mode = "create";

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String uId;

    int PICK_PHOTO_FOR_Modul = 3;
    String pictureUrl;
    InputStream inputStream;

    FirebaseStorage storage;
    DocumentReference newModulRef;
    DocumentReference updateModulRef;

    ItemTouchHelper touchHelper;

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_modul_edit, (ViewGroup) findViewById(R.id.content_frame));

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        //Get the current User from Auth
        //TODO Do this in Utils? Is it best Practice to do this in every Activity?
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if (authUser != null) {
            uId = authUser.getUid();
        } else {
            Log.e("ModulEdit Auth", "No User authenticated");
        }
        storage = FirebaseStorage.getInstance();

        mTitleView = findViewById(R.id.et_modul_edit_title);
        mAddImageButton = findViewById(R.id.bn_modul_edit_picture_picker);
        mImageView = findViewById(R.id.iv_modul_edit_picture);
        mSaveProgressBar = findViewById(R.id.bar_modul_progress);
        coordinatorLayout = findViewById(R.id.col_activity_modul_edit_coordinatorLayout);


        fab = findViewById(R.id.fab_modul_edit);

        BottomAppBar bar = findViewById(R.id.bar_modul_edit);
        setSupportActionBar(bar);

        drawerLayout = findViewById(R.id.drawer_layout);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mRvModulElements = findViewById(R.id.rv_modul_element_edit_list);
        mLayoutManager = new LinearLayoutManager(this);
        actionModeCallback = new ActionModeCallback();


        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Element>>() {
            }.getType();
            addElements = gson.fromJson(receivedIntent.getStringExtra("selectedElements"), listType);
            if (receivedIntent.getStringExtra("mode") != null) {
                mode = receivedIntent.getStringExtra("mode");
            }

            //Adds already existing data to the display when returning from Moudlelement Selection
            if (receivedIntent.hasExtra("modul")) {
                currentModul = (Modul) receivedIntent.getSerializableExtra("modul");
                mTitleView.setText(currentModul.getTitle());
            }
            if (receivedIntent.hasExtra("modulToCopy")) {
                currentModul = (Modul) receivedIntent.getSerializableExtra("modulToCopy");
                mTitleView.setText(currentModul.getTitle());
                modulElements = currentModul.getModulElements();
                mode = "copy";
            }
            if (receivedIntent.hasExtra("modulToEdit")) {
                currentModul = (Modul) receivedIntent.getSerializableExtra("modulToEdit");
                mTitleView.setText(currentModul.getTitle());
                modulElements = currentModul.getModulElements();
                mode = "edit";
            }
            if (currentModul != null) {
                if (currentModul.getPictureUrl() != null) {
                    PictureUtil pictureUtil = new PictureUtil(ModulEditActivity.this, mImageView);
                    pictureUtil.loadTitlePictureIntoImageView(currentModul.getPictureUrl());
                }
            }

            //TODO May have to make this happen only after Database fetch is completed?
            if (addElements != null) {
                modulElements = generateModulElementsFromElements();
            }
        }

        if (currentModul == null) {
            currentModul = new Modul("title", "", modulElements);
            modulElements = currentModul.getModulElements();
        }
        //TODO Set OrderinModul for new Modulelements somewhere
        addOnClickListeners();
        updateUiWithModulElements();

    }


    private void updateUiWithModulElements() {
        mRvModulElements.setLayoutManager(mLayoutManager);
        mAdapter = new ModulElementEditListAdapter(this, modulElements, this, this, this, this);
        mRvModulElements.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvModulElements);
    }


    public List<ModulElement> generateModulElementsFromElements() {
        modulElements = currentModul.getModulElements();
        for (Element element : addElements) {
            // TODO Not sure if I can set other varaible when only using this simple Constructor
            ModulElement newModulElement = new ModulElement(element);
            newModulElement.setMultiplier(new ModulElementMultiplier(1, "x"));
            modulElements.add(newModulElement);
        }
        return modulElements;
    }

    public void addOnClickListeners() {

        mAddImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_PHOTO_FOR_Modul);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTitleView.getText().toString().equals("") && modulElements.size() > 0) {
                    fab.hide();
                    mSaveProgressBar.setVisibility(View.VISIBLE);
                    modulElements = mAdapter.getModulElements();
                    String titleText = mTitleView.getText().toString();
                    currentModul.setModulElements(modulElements);
                    currentModul.setElementIds(generateElementIds());
                    currentModul.setEditorUid(uId);
                    currentModul.setTitle(titleText);
                    currentModul.setEditorName(authUser.getDisplayName());
                    if (mode.equals("edit")) {
                        currentModul.setEditTimeStamp(System.currentTimeMillis());
                        prepareUpdateModulInDb(currentModul);
                    } else if (mode.equals("copy")) {
                        currentModul.setCreationTimestamp(System.currentTimeMillis());
                        prepareSaveModulToDb(currentModul);
                    } else {
                        currentModul.setCreatorUid(uId);
                        currentModul.setCreationTimestamp(System.currentTimeMillis());
                        prepareSaveModulToDb(currentModul);
                    }

                } else {
                    Toast.makeText(ModulEditActivity.this, "Your Modul needs a title and min. 1 ModulElement", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_Modul && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //TODO Display an error
                return;
            } else {
                try {
                    inputStream = ModulEditActivity.this.getContentResolver().openInputStream(data.getData()); //TODO delete is safe?
                    Uri inputUri = data.getData();
                    pictureUrl = inputUri.toString();
                    currentModul.setPictureUrl(pictureUrl);
                    if (pictureUrl != null) {
                        PictureUtil pictureUtil = new PictureUtil(ModulEditActivity.this, mImageView);
                        pictureUtil.loadTitlePictureIntoImageView(pictureUrl);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //public Modul getModulFromDatabase(){
    //TODO get Modul from Database for update functions
    //
    // return modul;
    //}

    @Override
    public void onClick(ModulElement clickedModulElement) {
        if(actionMode == null){
            Intent startElementDetailActivityIntent = new Intent(this, ElementDetailActivity.class);
            startElementDetailActivityIntent.putExtra("element", clickedModulElement);
            startActivity(startElementDetailActivityIntent);
        }
        //TODO Change this completely


    }

    @Override
    public void onNoteListChanged(List<ModulElement> modulElements) {
        for (int i = 0; i < modulElements.size(); i++) {
            modulElements.get(i).setOrderInModul(i);
        }
    }

    public void prepareSaveModulToDb(Modul modul) {
        newModulRef = db.collection("moduls").document();
        modul.setId(newModulRef.getId());
        if (!(inputStream == null)) {
            savePictureToStorage(modul);
        } else {
            continueSaveModulToDatabase(modul);
        }
    }

    private void continueSaveModulToDatabase(Modul modul) {
        newModulRef
                .set(modul)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goToSavedModulDetail();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveModul", "Error adding document", e);
                    }
                });
    }

    public void prepareUpdateModulInDb(Modul modul) {
        updateModulRef = db.collection("moduls").document(currentModul.getId());
        if (!(inputStream == null)) {
            savePictureToStorage(modul);
        } else {
            continueUpdateModulInDatabase(modul);
        }
    }

    public void continueUpdateModulInDatabase(Modul modul) {
        updateModulRef
                .set(modul)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("editModul", "Document Snap added");
                        goToSavedModulDetail();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("editModul", "Error adding document", e);
                    }
                });
    }

    private void savePictureToStorage(final Modul modul) {
        //TODO Handle special case where mode = edit and a picture already exists -> Currently the picture just gets
        // replaced without warning to the user that the other one will be deleted permanently
        final StorageReference modulImageRef = storage.getReference().child("images/" + authUser.getUid() + "/moduls/" + modul.getId() + "_originalPicture");
        //TODO Sollten die wirklich in nem nach Nutzer differenzierten Ordner liegen? Selbe bei Elements

        UploadTask uploadTask = modulImageRef.putStream(inputStream);
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
                return modulImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    modul.setPictureUrl(downloadUrl);
                    if (mode == "edit") {
                        continueUpdateModulInDatabase(modul);
                    } else {
                        continueSaveModulToDatabase(modul);
                    }

                    Log.e("DownloadUrl", downloadUrl);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modul_edit_bottom_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_modulelement:
                Log.e("onClick AddNew", "clicklistener Has been fired");
                Intent addIntent = new Intent(ModulEditActivity.this, ElementListActivity.class);
                addIntent.putExtra("modulElementsEdit", true);
                addIntent.putExtra("mode", mode);
                if (mTitleView.getText() != null) {
                    String titleText = mTitleView.getText().toString();
                    currentModul.setTitle(titleText);
                }
                modulElements = mAdapter.getModulElements();
                currentModul.setModulElements(modulElements);
                addIntent.putExtra("modul", currentModul);
                startActivity(addIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> generateElementIds() {
        ArrayList<String> elementIds = new ArrayList<>();
        for (Element element : currentModul.getModulElements()) {
            String currentElementId = element.getElementId();
            if (!elementIds.contains(currentElementId)) {
                elementIds.add(currentElementId);
            }
        }
        return elementIds;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void onRowClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void onRowLongClicked(int position) {

        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            actionMode = null;
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }


    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.modul_element_edit_action_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d("API123", "here");
            switch (item.getItemId()) {

                case R.id.action_delete:
                    // delete all the selected rows
                    deleteRows();
                    mode.finish();
                    return true;

                case R.id.action_duplicate:
                    // add all the selected rows at the end of the list
                    duplicateSelected();
                    mode.finish();
                    return true;


                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
        }

        private void deleteRows() {
            List selectedItemPositions =
                    mAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                int j =(int) selectedItemPositions.get(i);
                mAdapter.removeData(j);
            }
            onNoteListChanged(modulElements);
            mAdapter.notifyDataSetChanged();
            //TODO Add Nice Animation that also illustrates the ability to swipedelete


        }

        private void duplicateSelected(){

            List selectedItemPositions = mAdapter.getSelectedItems();
            int size= selectedItemPositions.size();
            for (int i = 0; i < size; i++) {
                int j =(int) selectedItemPositions.get(i);
                ModulElement clone = new ModulElement(modulElements.get(j).clone());
                clone.setMultiplier(modulElements.get(j).getMultiplier().clone());
                Log.e("CloneMultiplier Type", clone.getMultiplier().getType());
                modulElements.add(clone);
            }
            actionMode = null;
            onNoteListChanged(modulElements);
            for(ModulElement x : modulElements){
                Log.e("Type", x.getTitle());

            }
            updateUiWithModulElements();

            Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.modul_edit_duplicated), Snackbar.LENGTH_SHORT).setAnchorView(fab);
            snackbar.show();
        }

    }

    private void goToSavedModulDetail(){
        Intent startModulDetailActivityIntent = new Intent(ModulEditActivity.this, ModulDetailActivity.class);
        startModulDetailActivityIntent.putExtra("modul", currentModul);
        startActivity(startModulDetailActivityIntent);
    }
}
