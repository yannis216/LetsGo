package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.ModulElementEditListAdapter;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnModulElementListChangedListener;
import com.example.android.letsgo.Utils.TouchHelper.SimpleItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_edit);

        mTitleView = findViewById(R.id.et_modul_edit_title);
        mAddNewElement = (ImageButton) findViewById(R.id.ib_modul_edit_add_element);
        fab = findViewById(R.id.fab_modul_edit);

        mRvModulElements = findViewById(R.id.rv_modul_element_edit_list);
        mLayoutManager = new LinearLayoutManager(this);






        //currentModul = getModulFromDatabase();
        //TODO Delete following line
        currentModul = new Modul("title", modulElements);
        modulElements = currentModul.getModulElements();

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Element>>(){}.getType();
            addElements = gson.fromJson(receivedIntent.getStringExtra("selectedElements"), listType);
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

                if(currentModul != null){
                    startActivity(addIntent);
                }else{
                    if(mTitleView.getText() != null){
                        //TODO Muss man hier wirklich das ganze Modul verschicken?

                        startActivity(addIntent);

                    }else{
                        Toast.makeText(ModulEditActivity.this, "You have to define a Title first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Save Modul to Database
                Toast.makeText(ModulEditActivity.this, "Type at index 1 in Activity= "+ mAdapter.getModulElements().get(1).getMultiplier().getType(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //public Modul getModulFromDatabase(){
        //TODO get Modul from Database
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
           Log.e("OnNoteListChanged", "ModulElement:" +modulElements.get(i).getTitle() + "Order in Modul:" + modulElements.get(i).getOrderInModul());
       }


    }


}
