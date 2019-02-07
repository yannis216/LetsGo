package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.ModulElementDetailListAdapter;
import com.example.android.letsgo.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulDetailActivity extends AppCompatActivity implements ModulElementDetailListAdapter.ModulElementOnClickHandler {
    private RecyclerView mRvModulElements;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Modul displayedModul;
    private TextView mTvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_detail);


        mRvModulElements =(RecyclerView) findViewById(R.id.rv_modul_detail_modulelement__list);
        mTvTitle = findViewById(R.id.tv_modul_detail_title);

        mLayoutManager = new LinearLayoutManager(this);
        mRvModulElements.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();
        displayedModul = (Modul) intent.getSerializableExtra("modul");
        generateModulElementsList(displayedModul.getModulElements());

        populateUi();




    }

    @Override
    public void onClick(ModulElement clickedModulElement){
        Context context = this;

        Intent startElementDetailActivityIntent = new Intent(context, ElementDetailActivity.class);
        startElementDetailActivityIntent.putExtra("clickedModulElement", clickedModulElement);
        startActivity(startElementDetailActivityIntent);
    }



    private void generateModulElementsList(List<ModulElement> modulElements){
        mAdapter = new ModulElementDetailListAdapter(this, modulElements, this );
        mRvModulElements.setAdapter(mAdapter);

    }

    private void populateUi(){
        String title = displayedModul.getTitle();
        mTvTitle.setText(title);

    }


}