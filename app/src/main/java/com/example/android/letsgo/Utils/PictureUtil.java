package com.example.android.letsgo.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letsgo.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.palette.graphics.Palette;

public class PictureUtil {
    private Context context;
    Palette.Swatch swatchVibrant;
    Palette.Swatch swatchMutedLight;
    Palette.Swatch swatchMutedDark;
    Palette.Swatch swatchDominant;
    int titleColor;
    int titleBackgroundColor;

    public PictureUtil(Context current){
        this.context = current;
    }

     public void initializePictureWithColours(String pictureUrl, final ImageView imageview, final TextView titleview){
        int imageHeight = (int) context.getResources().getDimension(R.dimen.element_picture_height);
        int imageWidth = (int) context.getResources().getDimension(R.dimen.element_picture_width);
        titleColor = context.getResources().getColor(R.color.colorAccent);
        titleBackgroundColor= context.getResources().getColor(R.color.colorPrimaryDark);

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("onBitmapLoaded","was called");
                assert imageview != null;
                imageview.setImageBitmap(bitmap);
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                swatchMutedDark =palette.getDarkMutedSwatch();
                                swatchDominant = palette.getDominantSwatch();
                                swatchVibrant =palette.getVibrantSwatch();
                                if (swatchDominant == null && swatchVibrant == null) {
                                    return;
                                }
                                if(swatchVibrant!=null){
                                    titleColor =swatchVibrant.getRgb();
                                    titleBackgroundColor=swatchVibrant.getBodyTextColor();
                                }else{
                                    titleColor = swatchDominant.getTitleTextColor();
                                    titleBackgroundColor =swatchDominant.getRgb();
                                    //TODO May choose to do this only when vibrant colour is available
                                }
                                titleview.setTextColor(titleColor);
                                titleview.setBackgroundColor(titleBackgroundColor);
                                titleview.getBackground().setAlpha(200);

                                //TODO Adapt Default and SetColours in Design phase
                                //TODO Change Colour of the Play video icon programmatically
                            }
                        });

            };
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e("onBitmapFailed","was called");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepareLoad","was called");
            }
        };
        imageview.setTag(target);


        Picasso.get()
                .load(pictureUrl)//TODO Replace with dynamic picture url
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .into(target);
    }
}
