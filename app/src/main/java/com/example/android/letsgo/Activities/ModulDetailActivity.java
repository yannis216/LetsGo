package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.letsgo.Adapter.ModulDetailElementListAdapter;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.Classes.SocialModulInfo;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.example.android.letsgo.Utils.UsedForSorter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulDetailActivity extends BaseNavDrawActivity implements ModulDetailElementListAdapter.ModulElementOnClickHandler {
    private RecyclerView mRvModulElements;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Modul displayedModul;
    private SocialModulInfo displayedSocialModulInfo;
    private List<ModulElement> modulElements;
    List<String> neededMaterialsIds;
    int minPeopleNeeded;

    private TextView mTvTitle;
    private ImageView mIvPicture;
    private ProgressBar progressBar;
    private TextView doneCountView;
    private TextView avgDurationView;
    private  RatingBar avgRatingView;
    private  ImageView durationClock;
    private TextView numMaterialsNeededView;
    private TextView numMinPeopleNeededView;

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

        doneCountView = findViewById(R.id.tv_modul_detail_times_done);
        avgDurationView = findViewById(R.id.tv_modul_detail_avg_duration);
        avgRatingView = findViewById(R.id.rb_modul_detail);
        durationClock = findViewById(R.id.iv_modul_detail_duration_clock);

        numMaterialsNeededView = findViewById(R.id.tv_modul_detail_num_materials);
        numMinPeopleNeededView = findViewById(R.id.tv_modul_detail_num_humans);

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
        displayedSocialModulInfo = (SocialModulInfo) intent.getSerializableExtra("socialModulInfo");

        modulElements = displayedModul.getModulElements();
        generateMaterialsInfo();
        generateMinPeopleNeeded();
        generateModulElementsList(modulElements);





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
        populateSocialInfoBar();
        populateRequirementsBar();

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

    public void populateSocialInfoBar(){
        if(displayedSocialModulInfo!=null) {
            doneCountView.setText(String.valueOf(displayedSocialModulInfo.getDoneCount()));
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(displayedSocialModulInfo.getDurationAvg()),
                    TimeUnit.MILLISECONDS.toMinutes(displayedSocialModulInfo.getDurationAvg()) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(displayedSocialModulInfo.getDurationAvg()) % TimeUnit.MINUTES.toSeconds(1));

            avgDurationView.setText("~ " +hms);

            if(displayedSocialModulInfo.getRatingNum()>0) {
                avgRatingView.setVisibility(View.VISIBLE);
                avgRatingView.setRating(displayedSocialModulInfo.getRating());
            }else{
                avgRatingView.setVisibility(View.GONE);
            }
            if(!(displayedSocialModulInfo.getDoneCount()==0)){
                avgDurationView.setVisibility(View.VISIBLE);
                durationClock.setVisibility(View.VISIBLE);
            }else{
                avgDurationView.setVisibility(View.GONE);
                durationClock.setVisibility(View.GONE);
            }
        }else{
            doneCountView.setText("0");
            avgDurationView.setText(" -");
            avgRatingView.setVisibility(View.GONE);
            avgDurationView.setVisibility(View.GONE);
            durationClock.setVisibility(View.GONE);
        }
    }

    public void generateMaterialsInfo(){
        neededMaterialsIds = new ArrayList<String>();
        for(int i = 0; i < modulElements.size(); i++){
            ModulElement currentModulElement = modulElements.get(i);
            List<String> newMaterialIds= currentModulElement.getNeededMaterialsIds();
            Log.e("ModulElement loop", ""+i);
            if(newMaterialIds.isEmpty()){
                continue;
            }
            for(int j = 0; j <newMaterialIds.size(); j++){
                String currentNewMaterialId = newMaterialIds.get(j);
                if(!neededMaterialsIds.contains(currentNewMaterialId)){
                    neededMaterialsIds.add(currentNewMaterialId);
                }
            }
        }
    }

    public void generateMinPeopleNeeded(){
        minPeopleNeeded = 0;
        for(int i = 0; i < modulElements.size(); i++){
            ModulElement currentModulElement = modulElements.get(i);
            if(minPeopleNeeded<currentModulElement.getMinNumberOfHumans()){
                minPeopleNeeded = currentModulElement.getMinNumberOfHumans();
            }
        }
    }

    public void populateRequirementsBar(){
        if(neededMaterialsIds.isEmpty()){
            numMaterialsNeededView.setText("0");
        }else{
            numMaterialsNeededView.setText(""+neededMaterialsIds.size());
        }
        numMinPeopleNeededView.setText("min. "+minPeopleNeeded);

        UsedForSorter sorter = new UsedForSorter(displayedModul, this);
        Map<String, Integer> sortedUsedFors = sorter.getMostImportantUsedFor();
        final List<String> usedForStrings = new ArrayList<String>();
        for(int i = 0; i <3 && i <sortedUsedFors.size() ; i++ ){
            usedForStrings.add(sortedUsedFors.keySet().toArray()[i].toString());
        }

        for(String s : usedForStrings){
            //Builds the Textview that holds the usedfor Strings
            LinearLayout usedForLinearLayout = findViewById(R.id.ll_modul_detail_usedFor);
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,20,0);
            textView.setLayoutParams(params);
            textView.setBackgroundColor(this.getResources().getColor(R.color.backgroundUsedForChips));
            textView.setPadding(5,0,5,0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            textView.setGravity(Gravity.CENTER);
            textView.setText("#"+s);
            usedForLinearLayout.addView(textView);
        }
    }
}