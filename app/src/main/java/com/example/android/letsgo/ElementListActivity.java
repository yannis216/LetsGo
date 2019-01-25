package com.example.android.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.android.letsgo.Classes.Element;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ElementListActivity extends AppCompatActivity implements ElementListAdapter.ElementOnClickHandler{

    RecyclerView mRvElements;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_list);

        db = FirebaseFirestore.getInstance();

        //TODO Make this a fragment for TwoPane Layouts
        mRvElements = findViewById(R.id.rv_elements_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvElements.setLayoutManager(mLayoutManager);

        //TODO This Should take search Input paramters and category at some point
        getElementsFromDatabase();


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
        mAdapter = new ElementListAdapter(this, elements, this);
        mRvElements.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Element clickedElement) {
        Intent startElementDetailActivityIntent = new Intent(this, ElementDetailActivity.class);
        startElementDetailActivityIntent.putExtra("element", clickedElement);
        startActivity(startElementDetailActivityIntent);
    }
}
