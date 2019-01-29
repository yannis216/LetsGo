package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.ElementListAdapter;
import com.example.android.letsgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ElementListActivity extends AppCompatActivity implements ElementListAdapter.ElementOnClickHandler {

    RecyclerView mRvElements;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    boolean isModulEditMode = false;
    List<Element> selectedElementsForModul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_list);

        db = FirebaseFirestore.getInstance();
        selectedElementsForModul = new ArrayList<Element>();

        Intent intent = getIntent();
        isModulEditMode =  intent.getBooleanExtra("modulEdit", false);


        //TODO Make this a fragment for TwoPane Layouts
        mRvElements = findViewById(R.id.rv_elements_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvElements.setLayoutManager(mLayoutManager);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_element_list);
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_activity_element_list);

        //TODO This Should take search Input paramters and category at some point
        getElementsFromDatabase();
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle actions based on the menu item
                if (item.getItemId()==R.id.fab_activity_element_detail){
                    //TODO DO Something
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isModulEditMode){
                    Intent fabIntent = new Intent(ElementListActivity.this, ModulEditActivity.class);
                    String selectedElementsListSerialized = new Gson().toJson(selectedElementsForModul);
                    fabIntent.putExtra("selectedElements", selectedElementsListSerialized);
                    startActivity(fabIntent);

                }else {
                    Intent fabIntent = new Intent(ElementListActivity.this, ElementEditActivity.class);
                    startActivity(fabIntent);
                }
            }
        });


    }

    private void getElementsFromDatabase(){
        final List<Element> elements = new ArrayList<Element>();
        db.collection("elements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Element element = document.toObject(Element.class);
                                elements.add(element);
                            }
                            updateUiWithFetchedElements(elements);
                        }else{
                            Log.d("getElementsFromDB", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    public void updateUiWithFetchedElements(List<Element> elements){
        mAdapter = new ElementListAdapter(this, elements, this, isModulEditMode);
        mRvElements.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Element clickedElement) {
        if (isModulEditMode) {
            //TODO IMPORTANT BUG Remove Element from List when clicked Twice
           selectedElementsForModul.add(clickedElement);
           Log.e("onClickElement", "" +clickedElement);

        }else {
            Intent startElementDetailActivityIntent = new Intent(this, ElementDetailActivity.class);
            startElementDetailActivityIntent.putExtra("element", clickedElement);
            startActivity(startElementDetailActivityIntent);
        }
    }
}
