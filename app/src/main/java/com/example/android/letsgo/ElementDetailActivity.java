package com.example.android.letsgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ElementDetailActivity extends AppCompatActivity {
    TextView mTitleView;
    TextView mUsedForView;
    ImageView mThumbnailUrlView;
    TextView mVideoUrlView;
    TextView mMinHumansView;
    Element displayedElement;
    Palette.Swatch swatchVibrant;
    Palette.Swatch swatchMutedLight;
    Palette.Swatch swatchMutedDark;
    int titleColor;
    int titleBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_detail);

        mTitleView = findViewById(R.id.tv_element_title);
        mUsedForView=findViewById(R.id.tv_element_usedFor);
        mThumbnailUrlView=findViewById(R.id.tv_element_thumbnailUrl);
        mVideoUrlView=findViewById(R.id.tv_element_videoUrl);
        mMinHumansView=findViewById(R.id.tv_element_min_humans);

        Intent intent = getIntent();

        displayedElement = (Element) intent.getSerializableExtra("CreatedElement");

        populateUi(displayedElement);

    }

    private void populateUi(Element element){
        mTitleView.setText(element.getTitle());
        mUsedForView.setText(element.getUsedFor());
        mVideoUrlView.setText(element.getVideoUrl());
        mMinHumansView.setText(String.valueOf(element.getMinNumberOfHumans()));
        initializePictureWithColours();

    }

    private void initializePictureWithColours(){
        int imageHeight = (int) getResources().getDimension(R.dimen.element_picture_height);
        int imageWidth = (int) getResources().getDimension(R.dimen.element_picture_width);

        Picasso.with(ElementDetailActivity.this)
                .load("https://source.unsplash.com/random")
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        assert mThumbnailUrlView != null;
                        mThumbnailUrlView.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        swatchVibrant = palette.getVibrantSwatch();
                                        swatchMutedLight = palette.getLightMutedSwatch();
                                        swatchMutedDark =palette.getDarkMutedSwatch();
                                    }

                                });
                        titleColor = getResources().getColor(R.color.colorAccent);
                        titleBackgroundColor= getResources().getColor(R.color.colorPrimaryDark);
                        if(swatchVibrant!=null){
                            titleColor = swatchVibrant.getRgb();
                            Toast.makeText(ElementDetailActivity.this, "SwatchColours used", Toast.LENGTH_LONG).show();
                        }
                        mTitleView.setTextColor(titleColor);
                        mTitleView.setBackgroundColor(titleBackgroundColor);
                        mTitleView.getBackground().setAlpha(150);

                        //TODO Adapt Default and SetColours in Design phase
                    };
                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }

}
