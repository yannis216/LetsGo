package com.example.android.letsgo.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

public class PictureUtil {
    private Context context;
    Palette.Swatch swatchVibrant;
    Palette.Swatch swatchMutedLight;
    Palette.Swatch swatchMutedDark;
    Palette.Swatch swatchDominant;
    int titleColor;
    int titleBackgroundColor;
    ImageView imageView;
    CallbackHelper callbackHelper;

    public PictureUtil(Context current, ImageView imageView){
        this.context = current;
        this.imageView = imageView;
    }

    public PictureUtil(Context current, ImageView imageView, CallbackHelper callbackHelper){
        this.context = current;
        this.imageView = imageView;
        this.callbackHelper = callbackHelper;
    }

     public void loadTitlePictureIntoImageView(String pictureUrl){

        Picasso.get()
                .load(pictureUrl)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    //TODO The following picture loading routines do not support update of pictures
    // (When new modulpicture is uploaded on update of modul, changed picture does not get displayed)

    public void saveElementImageFromDatabaseToLocalStorage(FirebaseStorage storage, Element element){
        final File localFile;
        String elementId = element.getElementId();
        //TODO Change Directory to an external storage (?)
        String elementsPath = context.getFilesDir().getAbsolutePath() + File.separator + "elements";
        File myDir = new File(elementsPath);
        myDir.mkdirs();
        String fname = elementId +"_originalPicture";
        localFile = new File(myDir, fname);
        Log.e("LocalFilePath" , localFile.getAbsolutePath());
        if(localFile.exists()){
            Picasso.get().load(localFile).into(imageView);
            if(callbackHelper != null){
                callbackHelper.onSuccess();
            }
            Log.e("LocalFileExists", "Loaded local Image into View");
        }else{
            StorageReference storageRef = storage.getReferenceFromUrl(element.getPictureUrl());
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    if(callbackHelper != null){
                        callbackHelper.onSuccess();
                    }
                    Picasso.get().load(localFile).into(imageView);
                    Log.e("onSuccess", "Image has been saved to localFile");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    if(callbackHelper != null){
                        callbackHelper.onFailure();
                    }
                }
            });
        }



    }

    public void saveElementThumbnailFromDatabaseToLocalStorage(FirebaseStorage storage, Element element){
        final File localFile;
        String elementId = element.getElementId();
        //TODO Change Directory to an external storage (?)
        String elementsPath = context.getFilesDir().getAbsolutePath() + File.separator + "elements";
        File myDir = new File(elementsPath);
        myDir.mkdirs();
        String fname = elementId +"_thumbnail";
        localFile = new File(myDir, fname);
        Log.e("LocalFilePath" , localFile.getAbsolutePath());

        final int dimens = (int) context.getResources().getDimension(R.dimen.element_thumbnail_dimens);
        if(localFile.exists()){
            Picasso.get()
                    .load(localFile)
                    .resize(dimens, dimens)
                    .centerCrop()
                    .into(imageView);
            if(callbackHelper != null){
                callbackHelper.onSuccess();
            }
            Log.e("LocalFileExists", "Loaded local Thumbnail into View");
        }else{
            StorageReference storageRef = storage.getReferenceFromUrl(element.getPictureUrl());
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    if(callbackHelper != null){
                        callbackHelper.onSuccess();
                    }
                    Picasso.get()
                            .load(localFile)
                            .resize(dimens, dimens)
                            .centerCrop()
                            .into(imageView);

                    Log.e("onSuccess", "Thumbnail has been saved to localFile");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    if(callbackHelper != null){
                        callbackHelper.onFailure();
                    }
                }
            });
        }
    }

    public void saveModulImageFromDatabaseToLocalStorage(FirebaseStorage storage, Modul modul){
        final File localFile;
        String modulId = modul.getId();
        //TODO Change Directory to an external storage (?)
        String modulsPath = context.getFilesDir().getAbsolutePath() + File.separator + "moduls";
        File myDir = new File(modulsPath);
        myDir.mkdirs();
        String fname = modulId +"_originalPicture";
        localFile = new File(myDir, fname);
        Log.e("LocalFilePath" , localFile.getAbsolutePath());
        if(localFile.exists()){
            Picasso.get().load(localFile).into(imageView);
            if(callbackHelper != null){
                callbackHelper.onSuccess();
            }
            Log.e("LocalFileExists", "Loaded local Image into View");
        }else{
            StorageReference storageRef = storage.getReferenceFromUrl(modul.getPictureUrl());
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    if(callbackHelper != null){
                        callbackHelper.onSuccess();
                    }
                    Picasso.get().load(localFile).into(imageView);
                    Log.e("onSuccess", "Image has been saved to localFile");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    if(callbackHelper != null){
                        callbackHelper.onFailure();
                    }
                }
            });
        }



    }

    public void saveModulThumbnailFromDatabaseToLocalStorage(FirebaseStorage storage, Modul modul){
        final File localFile;
        String modulId = modul.getId();
        //TODO Change Directory to an external storage (?)
        String modulsPath = context.getFilesDir().getAbsolutePath() + File.separator + "moduls";
        File myDir = new File(modulsPath);
        myDir.mkdirs();
        String fname = modulId +"_thumbnail";
        localFile = new File(myDir, fname);
        Log.e("LocalFilePath" , localFile.getAbsolutePath());

        final int dimens = (int) context.getResources().getDimension(R.dimen.modul_thumbnail_dimens);
        if(localFile.exists()){
            Picasso.get()
                    .load(localFile)
                    .resize(dimens, dimens)
                    .centerCrop()
                    .into(imageView);
            if(callbackHelper != null){
                callbackHelper.onSuccess();
            }
            Log.e("LocalFileExists", "Loaded local Thumbnail into View");
        }else{
            StorageReference storageRef = storage.getReferenceFromUrl(modul.getPictureUrl());
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    if(callbackHelper != null){
                        callbackHelper.onSuccess();
                    }
                    Picasso.get()
                            .load(localFile)
                            .resize(dimens, dimens)
                            .centerCrop()
                            .into(imageView);

                    Log.e("onSuccess", "Modul Thumbnail has been saved to localFile");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    if(callbackHelper != null){
                        callbackHelper.onFailure();
                    }
                }
            });
        }



    }
}
