package com.example.android.letsgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
    Chip mUsedForChip;
    ImageView mThumbnailUrlView;
    TextView mVideoUrlView;
    TextView mMinHumansView;
    Element displayedElement;
    Palette.Swatch swatchVibrant;
    Palette.Swatch swatchMutedLight;
    Palette.Swatch swatchMutedDark;
    Palette.Swatch swatchDominant;
    int titleColor;
    int titleBackgroundColor;
    ConstraintLayout mElementLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_detail);

        mElementLayout = findViewById(R.id.cl_element_layout);
        mTitleView = findViewById(R.id.tv_element_title);
        mUsedForChip =findViewById(R.id.tv_element_usedFor);
        mThumbnailUrlView=findViewById(R.id.iv_element_thumbnailUrl);
        mVideoUrlView=findViewById(R.id.tv_element_videoUrl);
        mMinHumansView=findViewById(R.id.tv_element_min_humans);
        titleColor = getResources().getColor(R.color.colorAccent);
        titleBackgroundColor= getResources().getColor(R.color.colorPrimaryDark);

        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("CreatedElement");

        populateUi(displayedElement);

    }

    private void populateUi(Element element){
        mTitleView.setText(element.getTitle());
        mUsedForChip.setText(element.getUsedFor());
        mVideoUrlView.setText(element.getVideoUrl());
        mMinHumansView.setText("min. " + String.valueOf(element.getMinNumberOfHumans()));
        initializePictureWithColours();

    }

    private void initializePictureWithColours(){
        int imageHeight = (int) getResources().getDimension(R.dimen.element_picture_height);
        int imageWidth = (int) getResources().getDimension(R.dimen.element_picture_width);

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("onBitmapLoaded","was called");
                assert mThumbnailUrlView != null;
                mThumbnailUrlView.setImageBitmap(bitmap);
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                swatchMutedDark =palette.getDarkMutedSwatch();
                                swatchDominant = palette.getDominantSwatch();
                                swatchVibrant =palette.getVibrantSwatch();
                                if (swatchDominant == null && swatchVibrant == null) {
                                    Toast.makeText(ElementDetailActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(swatchVibrant!=null){
                                    titleColor =swatchVibrant.getRgb();
                                    titleBackgroundColor=swatchVibrant.getBodyTextColor();
                                    Toast.makeText(ElementDetailActivity.this, "Vibrant Colour used", Toast.LENGTH_LONG).show();
                                }else{
                                    titleColor = swatchDominant.getTitleTextColor();
                                    titleBackgroundColor =swatchDominant.getRgb();
                                    //TODO May choose to do this only when vibrant colour is available
                                }
                                mTitleView.setTextColor(titleColor);
                                mTitleView.setBackgroundColor(titleBackgroundColor);
                                mTitleView.getBackground().setAlpha(200);
                                //TODO Adapt Default and SetColours in Design phase
                            }
                        });

            };
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("onBitmapFailed","was called");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepareLoad","was called");
            }
        };
        mThumbnailUrlView.setTag(target);

        Picasso.with(ElementDetailActivity.this)
                .load("https://source.unsplash.com/random")//TODO Replace with dynamic picture url
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .into(target);
    }

}
