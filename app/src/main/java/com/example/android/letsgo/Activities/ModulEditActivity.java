package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.letsgo.Adapter.ModulElementEditListAdapter;
import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnModulElementListChangedListener;
import com.example.android.letsgo.Utils.TouchHelper.SimpleItemTouchHelperCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulEditActivity extends AppCompatActivity implements ModulElementEditListAdapter.ModulElementOnClickHandler, OnModulElementListChangedListener {

    Modul currentModul;
    List<ModulElement> modulElements = new ArrayList<ModulElement>();
    List<Element> addElements;
    ImageButton mAddNewElement;
    EditText mTitleView;
    FloatingActionButton fab;

    RecyclerView mRvModulElements;
    RecyclerView.LayoutManager mLayoutManager;
    ModulElementEditListAdapter mAdapter;
    String uId;

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    //TODO Make it so that List of Modulelelemnts does not forget every element that has been there before goind to add new ones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_edit);

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        //Get the current User from Auth
        //TODO Do this in Utils? Is it best Practice to do this in every Activity?
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if(authUser != null){
            uId = authUser.getUid();
        }else{
            Log.e("ModulEdit Auth", "No User authenticated");
        }

        mTitleView = findViewById(R.id.et_modul_edit_title);
        mAddNewElement = (ImageButton) findViewById(R.id.ib_modul_edit_add_element);
        fab = findViewById(R.id.fab_modul_edit);

        mRvModulElements = findViewById(R.id.rv_modul_element_edit_list);
        mLayoutManager = new LinearLayoutManager(this);

        if(currentModul == null) {
            currentModul = new Modul("title", "", modulElements);
            modulElements = currentModul.getModulElements();
        }

        //TODO Set OrderinModul for new Modulelements somewhere

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Element>>(){}.getType();
            addElements = gson.fromJson(receivedIntent.getStringExtra("selectedElements"), listType);

            //Adds already existing data to the display when returning from Moudlelement Selection
            if(receivedIntent.hasExtra("modul")) {
                currentModul = (Modul) receivedIntent.getSerializableExtra("modul");
                mTitleView.setText(currentModul.getTitle());
            }
            Log.e("addElements: ", "" + addElements);

            //TODO May have to make this happen only after Database fetch is completed?
            if(addElements != null){
                modulElements = generateModulElementsFromElements();
            }
        }

        addOnClickListeners();
        updateUiWithModulElements();

    }



    private void updateUiWithModulElements(){
        mRvModulElements.setLayoutManager(mLayoutManager);
        mAdapter = new ModulElementEditListAdapter(this, modulElements, this, this);
        mRvModulElements.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvModulElements);
    }


    public List<ModulElement> generateModulElementsFromElements(){
        for(Element element: addElements){
            // TODO Not sure if I can set other varaible when only using this simple Constructor
            ModulElement newModulElement = new ModulElement(element);
            modulElements.add(newModulElement);
        }
        return modulElements;
    }

    public void addOnClickListeners(){
        mAddNewElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("onClick AddNew", "clicklistener Has been fired");
                Intent addIntent = new Intent(ModulEditActivity.this, ElementListActivity.class);
                addIntent.putExtra("modulEdit", true);
                addIntent.putExtra("modul", currentModul);
                if(mTitleView.getText() != null){
                    //TODO Muss man hier wirklich das ganze Modul verschicken?
                    String titleText = mTitleView.getText().toString();
                    currentModul.setTitle(titleText);
                }
                addIntent.putExtra("modul", currentModul);
                startActivity(addIntent);

            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mTitleView.getText().toString().equals("")&& modulElements.size() > 0) {
                    modulElements = mAdapter.getModulElements();
                    String titleText = mTitleView.getText().toString();
                    currentModul.setModulElements(modulElements);
                    currentModul.setCreatorUid(uId);
                    currentModul.setCreationTimestamp(System.currentTimeMillis());
                    currentModul.setTitle(titleText);
                    saveModulToDb(currentModul);
                }else{
                    Toast.makeText(ModulEditActivity.this, "Your Modul needs a title and min. 1 ModulElement", Toast.LENGTH_SHORT).show();
                }
                //TODO Intent to ModulDetaiLAcitivty
                Intent startModulDetailActivityIntent = new Intent(ModulEditActivity.this, ModulDetailActivity.class);
                startModulDetailActivityIntent.putExtra("modul", currentModul);
                startActivity(startModulDetailActivityIntent);

            }
        });
    }

    //public Modul getModulFromDatabase(){
        //TODO get Modul from Database for update functions
       //
       // return modul;
    //}

    @Override
    public void onClick(ModulElement clickedModulElement) {
        //TODO Change this completely
        Intent startElementDetailActivityIntent = new Intent(this, ElementDetailActivity.class);
        startElementDetailActivityIntent.putExtra("element", clickedModulElement);
        startActivity(startElementDetailActivityIntent);

    }

    @Override
    public void onNoteListChanged(List<ModulElement> modulElements) {
       for(int i = 0; i<modulElements.size(); i++){
           modulElements.get(i).setOrderInModul(i);
       }


    }

    public void saveModulToDb(Modul modul){
        DocumentReference newModulRef = db.collection("user").document(uId).collection("createdModuls").document();
        modul.setId(newModulRef.getId());
        newModulRef
                .set(modul)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("saveModul", "Document Snap added");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveModul", "Error adding document", e);
                    }
                });
    }


}
