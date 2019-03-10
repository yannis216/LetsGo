package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DoActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mMultiplierView;
    TextView mTitleView;
    TextView mNextTitleView;
    TextView mMinHumansView;
    ImageButton mNextElementButton;
    ImageButton mPreviousElementButton;
    Activity currentDoingActivity;

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);

        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        //Get the current User from Auth
        //TODO Do this in Utils? Is it best Practice to do this in every Activity?
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if(authUser != null){
            uId = authUser.getUid();
        }else{
            Log.e("DoActivity Auth", "No User authenticated");
        }

        mImageView = findViewById(R.id.iv_do_modulelement);
        mMultiplierView =  findViewById(R.id.tv_do_modulelement_multiplier);
        mTitleView = findViewById(R.id.tv_do_modulelement_title);
        mMinHumansView = findViewById(R.id.tv_do_modulelement_minHumans);
        mNextTitleView = findViewById(R.id.tv_do_next_modulelement);
        mNextElementButton = findViewById(R.id.ib_do_next);
        mPreviousElementButton = findViewById(R.id.ib_do_prev);

        mPreviousElementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousStep();
            }
        });


        Intent intent = getIntent();
        Modul givenModul = (Modul) intent.getSerializableExtra("modul");
        currentDoingActivity = generateActivityFromModul(givenModul);

        updateUi();
    }

    private Activity generateActivityFromModul(Modul givenModul){

        //TODO On Authentication write user displayname to device
        long currentTime = System.currentTimeMillis();
        return new Activity(givenModul.getId(), givenModul.getTitle(), givenModul.getModulElements(), authUser.getUid(), currentTime, 0);
    }

    private void updateUi(){
        //Handle Previous Button
        if(currentDoingActivity.getCurrentPosition() == 0){
            mPreviousElementButton.setVisibility(View.INVISIBLE);
        }else{
            mPreviousElementButton.setVisibility(View.VISIBLE);
        }

        //Set all content regarding the current ModulElement
        ModulElement currentModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition());
        if(currentModulElement.getPictureUrl()!= null){
            Picasso.get()
                    .load(currentModulElement.getPictureUrl())
                    .into(mImageView);
        }
        mMultiplierView.setText(currentModulElement.getMultiplier().getTimesMultiplied() + " " + currentModulElement.getMultiplier().getType());
        mTitleView.setText(currentModulElement.getTitle());

        mMinHumansView.setText(String.valueOf(currentModulElement.getMinNumberOfHumans()));


        //Set Content regarding the next Element
        //TODO Might wanna add Multiplier to be displayed here (or no?)
        if(currentDoingActivity.getModulElements().size()-1 >currentDoingActivity.getCurrentPosition()) {
            ModulElement nextModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition() + 1);
            mNextTitleView.setText(nextModulElement.getTitle());
            mNextElementButton.setVisibility(View.VISIBLE);
            mNextElementButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    nextStep();
                }
            });

        }else{
            //If in the last Element let user upload his activity to database when finished
            mNextTitleView.setText(getResources().getText(R.string.do_activity_finish));
            mNextElementButton.setImageResource(R.drawable.baseline_save_black_24dp);
            mNextElementButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    currentDoingActivity.setEndTime(System.currentTimeMillis());
                    saveDoneActivityToDatabase();
                }
            });
        }

    }

    private void nextStep(){
        currentDoingActivity.setCurrentPosition(currentDoingActivity.getCurrentPosition()+1);
        updateUi();
    }

    private void previousStep(){
        currentDoingActivity.setCurrentPosition(currentDoingActivity.getCurrentPosition()-1);
        updateUi();
    }

    private void saveDoneActivityToDatabase(){
        DocumentReference newActivityRef = db.collection("user").document(uId).collection("doneActivities").document();
        currentDoingActivity.setId(newActivityRef.getId());
        newActivityRef
                .set(currentDoingActivity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("saveActivity", "Document Snap added");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveActivity", "Error adding document", e);
                    }
                });
    }
}
