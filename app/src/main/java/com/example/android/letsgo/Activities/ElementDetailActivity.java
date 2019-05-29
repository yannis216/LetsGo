package com.example.android.letsgo.Activities;

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
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class ElementDetailActivity extends BaseNavDrawActivity {
    TextView mTitleView;
    ImageView mPictureView;
    TextView mMinHumansView;
    Element displayedElement;
    TextView mShortDescView;
    LinearLayout mLlUsedFors;

    ConstraintLayout mElementLayout;
    ChipGroup mUsedForChipGroup;
    ChipGroup mMaterialChipGroup;
    FirebaseFirestore db;

    FirebaseStorage storage;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String uId;

    final ArrayList<Material> materials = new ArrayList<Material>();

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_element_detail, (ViewGroup) findViewById(R.id.content_frame));

        // Access a Cloud Firestore, Auth and Storage Instance
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        uId = authUser.getUid();
        storage = FirebaseStorage.getInstance();

        mElementLayout = findViewById(R.id.cl_element_layout);
        mTitleView = findViewById(R.id.tv_element_detail_title);
        mPictureView =findViewById(R.id.iv_element_detail_picture);
        mShortDescView=findViewById(R.id.tv_element_detail_shortDesc);
        mMinHumansView=findViewById(R.id.tv_element_detail_num_humans);
        mLlUsedFors=findViewById(R.id.ll_element_detail_usedFor);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_element_detail);
        FloatingActionButton fab = findViewById(R.id.fab_activity_element_detail);

        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Navigation", "cliecked");
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("element");
        if (displayedElement == null){
            displayedElement = (Element) intent.getSerializableExtra("clickedModulElement");
            //TODO Do Something with the resulting info that this element was a modulElement before
            // e.g. take away opportunity to editcertain fields
        }

        populateUi(displayedElement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fabIntent = new Intent(ElementDetailActivity.this, ElementEditActivity.class);
                startActivity(fabIntent);
            }
        });


    }


    private void populateUi(final Element element){
        mTitleView.setText(element.getTitle());
        mShortDescView.setText(element.getShortDesc());
        mMinHumansView.setText("min. " + String.valueOf(element.getMinNumberOfHumans()));
        buildUsedForHashTexts(displayedElement.getUsedFor());
        if(displayedElement.getPictureUrl() != null){
            PictureUtil pictureUtil = new PictureUtil(this, mPictureView, mTitleView);
            pictureUtil.loadTitlePictureIntoImageView(displayedElement.getPictureUrl());
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Log.e("ElementCreaterId", displayedElement.getCreatorId());
        Log.e("uId", uId);
        if(uId.equals(displayedElement.getCreatorId())){
            inflater.inflate(R.menu.element_detail_bottom_menu, menu);
            //TODO Test if this works only when I am the creator!
            //TODO Add for users that have not created this modul the option to copy and edit it
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit_element:
                Log.e("onClick EditElement", "User clicked that he wants to edit Element");
                Intent updateIntent = new Intent(ElementDetailActivity.this, ElementEditActivity.class);
                updateIntent.putExtra("elementToEdit", displayedElement);
                updateIntent.putExtra("elementMaterials", materials);
                startActivity(updateIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildUsedForHashTexts(List<String> usedForStrings){
        mLlUsedFors.removeAllViews();
        if(usedForStrings.size()>0){
            for(String s : usedForStrings){
                if(!s.isEmpty()) {
                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 20, 0);
                    textView.setLayoutParams(params);
                    textView.setBackgroundColor(this.getResources().getColor(R.color.backgroundUsedForChips));
                    textView.setPadding(5, 0, 5, 0);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText("#" + s);
                    mLlUsedFors.addView(textView);
                }
            }
        }
    }
}
