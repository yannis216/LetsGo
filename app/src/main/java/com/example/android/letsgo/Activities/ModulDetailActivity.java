package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.Adapter.ModulElementDetailListAdapter;
import com.example.android.letsgo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_modul_detail);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startModulEditActivityIntent = new Intent(ModulDetailActivity.this, ModulEditActivity.class);
                startActivity(startModulEditActivityIntent);

                //TODO This Should trigger Doing-Mode at some point
            }
        });

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