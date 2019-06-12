package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letsgo.Adapter.ElementListAdapter;
import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ElementListActivity extends BaseNavDrawActivity implements ElementListAdapter.ElementOnClickHandler {

    RecyclerView mRvElements;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    boolean isModulElementsEditMode = false;
    List<Element> selectedElementsForModul;
    Modul currentModul;
    String updateOrEditMode;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_element_list, (ViewGroup) findViewById(R.id.content_frame));

        db = FirebaseFirestore.getInstance();
        selectedElementsForModul = new ArrayList<Element>();

        Intent intent = getIntent();
        isModulElementsEditMode =  intent.getBooleanExtra("modulElementsEdit", false);
        if(isModulElementsEditMode){
            currentModul = (Modul) intent.getSerializableExtra("modul");
            updateOrEditMode = intent.getStringExtra("mode");
        }


        //TODO Make this a fragment for TwoPane Layouts
        mRvElements = findViewById(R.id.rv_elements_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvElements.setLayoutManager(mLayoutManager);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_element_list);
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_activity_element_list);

        drawerLayout = findViewById(R.id.drawer_layout);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //TODO This Should take search Input paramters and category at some point
        getElementsFromDatabase();
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle actions based on the menu item
                if (item.getItemId()==R.id.fab_activity_element_detail){
                    //TODO DO Something on other id clicks
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isModulElementsEditMode){
                    Intent fabIntent = new Intent(ElementListActivity.this, ModulEditActivity.class);
                    String selectedElementsListSerialized = new Gson().toJson(selectedElementsForModul);
                    fabIntent.putExtra("selectedElements", selectedElementsListSerialized);
                    fabIntent.putExtra("modul", currentModul);
                    fabIntent.putExtra("mode", updateOrEditMode);
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
                .orderBy("title")
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
        mAdapter = new ElementListAdapter(this, elements, this, isModulElementsEditMode);
        mRvElements.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Element clickedElement) {
        Log.e("onClickElement", "was called with" +clickedElement);
        if (isModulElementsEditMode) {
           if(clickedElement.isSelected()){
               selectedElementsForModul.add(clickedElement);
           }else {
               for(int i = 0; i < selectedElementsForModul.size(); i++){
                   if (clickedElement == selectedElementsForModul.get(i)){
                       selectedElementsForModul.remove(i);
                       break;
                   }
               }
           }
        }else {
            Intent startElementDetailActivityIntent = new Intent(this, ElementDetailActivity.class);
            startElementDetailActivityIntent.putExtra("element", clickedElement);
            startActivity(startElementDetailActivityIntent);
        }
    }
}
