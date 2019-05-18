package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letsgo.Adapter.ModulDetailElementListAdapter;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulDetailActivity extends BaseNavDrawActivity implements ModulDetailElementListAdapter.ModulElementOnClickHandler {
    private RecyclerView mRvModulElements;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Modul displayedModul;
    private TextView mTvTitle;
    private ImageView mIvPicture;
    private ProgressBar progressBar;

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String uId;

    FirebaseStorage storage;

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_modul_detail, (ViewGroup) findViewById(R.id.content_frame));

        //Get the current User from Auth
        //TODO Do this in Utils? Is it best Practice to do this in every Activity?
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if(authUser != null){
            uId = authUser.getUid();
        }else{
            Log.e("ModulDetail Auth", "No User authenticated");
        }

        storage = FirebaseStorage.getInstance();

        mRvModulElements =(RecyclerView) findViewById(R.id.rv_modul_detail_modulelement__list);
        mRvModulElements.setFocusable(false);
        mTvTitle = findViewById(R.id.tv_modul_detail_title);
        mIvPicture = findViewById(R.id.iv_modul_detail_picture);
        progressBar = findViewById(R.id.pb_modul_detail_modul_picture_load);



        drawerLayout = findViewById(R.id.drawer_layout);

        BottomAppBar bar = findViewById(R.id.bar_modul_detail);
        setSupportActionBar(bar);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_modul_detail);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startDoActivityIntent = new Intent(ModulDetailActivity.this, DoActivity.class);
                startDoActivityIntent.putExtra("modul", displayedModul);
                startActivity(startDoActivityIntent);

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
        mAdapter = new ModulDetailElementListAdapter(this, modulElements, this );
        mRvModulElements.setAdapter(mAdapter);

    }

    private void populateUi(){
        String title = displayedModul.getTitle();
        mTvTitle.setText(title);
        loadPictureIntoHeader();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(uId.equals(displayedModul.getEditorUid())){
            inflater.inflate(R.menu.modul_detail_bottom_menu, menu);
            //TODO Test if this works only when I am the creator!
            //TODO Add for users that have not created this modul the option to copy and edit it
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit_modul:
                Log.e("onClick EditModul", "User clicked that he wants to edit Modul");
                Intent addIntent = new Intent(ModulDetailActivity.this, ModulEditActivity.class);
                addIntent.putExtra("modulToEdit", displayedModul);
                startActivity(addIntent);
                break;
            case R.id.action_copy_modul:
                Log.e("onClick CopyModul", "User clicked that he wants to copy Modul");
                Intent copyIntent = new Intent(ModulDetailActivity.this, ModulEditActivity.class);
                copyIntent.putExtra("modulToCopy", displayedModul);
                startActivity(copyIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadPictureIntoHeader(){
        if(displayedModul.getPictureUrl() == null){
        }else{
            mIvPicture.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    mIvPicture.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure() {
                    Log.e("ModulPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(this, mIvPicture, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveModulImageFromDatabaseToLocalStorage(storage, displayedModul);
        }
    }
}