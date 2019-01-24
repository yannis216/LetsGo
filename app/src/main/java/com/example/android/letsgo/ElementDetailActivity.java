package com.example.android.letsgo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Material;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
    ImageView mPictureView;
    TextView mMinHumansView;
    Element displayedElement;


    ConstraintLayout mElementLayout;
    ImageButton mPlayVideoButton;
    ChipGroup mUsedForChipGroup;
    ChipGroup mMaterialChipGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_detail);

        mElementLayout = findViewById(R.id.cl_element_layout);
        mTitleView = findViewById(R.id.tv_element_title);
        mPictureView =findViewById(R.id.iv_element_thumbnailUrl);
        mMinHumansView=findViewById(R.id.tv_element_min_humans);
        mPlayVideoButton =findViewById(R.id.ib_element_play_video);
        mUsedForChipGroup =findViewById(R.id.cg_element_detail_usedFor_chips);
        mMaterialChipGroup = findViewById(R.id.cg_element_detail_material_chips);



        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("CreatedElement");

        populateUi(displayedElement);

    }

    private void populateUi(final Element element){
        mTitleView.setText(element.getTitle());
        setChips(element.getUsedFor());
        setMaterialChips(element.getNeededMaterials());
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

    private void setMaterialChips(List<Material> materials){
        for(int i=0; i<materials.size(); i++){
            final Chip thisChip = getChip(mMaterialChipGroup, materials.get(i).getTitle());
            mMaterialChipGroup.addView(thisChip);
        }
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


}
