package com.example.android.letsgo.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
    ImageView mPictureView;
    TextView mMinHumansView;
    Element displayedElement;
    TextView mShortDescView;



    ConstraintLayout mElementLayout;
    ImageButton mPlayVideoButton;
    ChipGroup mUsedForChipGroup;
    ChipGroup mMaterialChipGroup;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_element_detail, (ViewGroup) findViewById(R.id.content_frame));

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        mElementLayout = findViewById(R.id.cl_element_layout);
        mTitleView = findViewById(R.id.tv_element_title);
        mPictureView =findViewById(R.id.iv_element_thumbnailUrl);
        mShortDescView=findViewById(R.id.tv_element_detail_shortDesc);
        mMinHumansView=findViewById(R.id.tv_element_min_humans);
        mPlayVideoButton =findViewById(R.id.ib_element_play_video);
        mUsedForChipGroup =findViewById(R.id.cg_element_detail_usedFor_chips);
        mMaterialChipGroup = findViewById(R.id.cg_element_detail_material_chips);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_element_detail);
        FloatingActionButton fab = findViewById(R.id.fab_activity_element_detail);



        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("element");
        if (displayedElement == null){
            displayedElement = (Element) intent.getSerializableExtra("clickedModulElement");
            //TODO Do Something with the resulting info that this element was a modulElement before
            // e.g. take away opportunity to editcertain fields
        }

       getMaterialsFromDatabase(displayedElement.getNeededMaterialsIds());
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
        setChips(element.getUsedFor());
        mMinHumansView.setText("min. " + String.valueOf(element.getMinNumberOfHumans()));
        if(element.getVideoId().equals("")){
            mPlayVideoButton.setVisibility(View.GONE);
        }else{
            mPlayVideoButton.setVisibility(View.VISIBLE);
            mPlayVideoButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.e("onClickVideoButton", "was called");
                    watchYoutubeVideo(ElementDetailActivity.this, element.getVideoId());

                }
            });
        }
        if(element.getPictureUrl()!= null){
            PictureUtil pictureUtil = new PictureUtil(ElementDetailActivity.this);
            pictureUtil.initializePictureWithColours(element.getPictureUrl(), mPictureView, mTitleView);
        }

    }



    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    private void setChips(List<String> strings){
        for(int i=0; i<strings.size(); i++){
            final Chip thisChip = getChip(mUsedForChipGroup, strings.get(i));
            mUsedForChipGroup.addView(thisChip);
        }
    }

    private void addMaterialChip(Material material){
            final Chip thisChip = getChip(mMaterialChipGroup, material.getTitle());
            mMaterialChipGroup.addView(thisChip);

    }


    private Chip getChip(final ChipGroup entryChipGroup, String text) {
        final Chip chip = new Chip(this);
        if(entryChipGroup == mUsedForChipGroup){
            chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.used_for_detail_chip));
        }else if(entryChipGroup == mMaterialChipGroup) {
            chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.material_detail_chip));
        }

        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Show list of Elements with same usedFor?
                //TODO Show Shopping possibilities?
            }
        });
        return chip;
    }

    private List<Material> getMaterialsFromDatabase(List<String> materialIds){
        //TODO Revisit this: This might be better done with a query or a batch request or sth. like that. Maybe a query with or(Not possible yet in Jan. 2019 but feature is requested onm github)?
        final List<Material> materials = new ArrayList<Material>();
        for(int i = 0; i<materialIds.size(); i++){
            db.collection("materials")
                    .document(materialIds.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Material material=documentSnapshot.toObject(Material.class);
                            materials.add(material);
                            addMaterialChip(material);


                        }
                    });
        }
        //TODO Try addMaterialChips (With a list of Strings here to avoid single-loading chips

        return materials;

    }


}
