package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class DoActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    ImageView mImageView;
    TextView mMultiplierView;
    TextView mTitleView;
    TextView mNextTitleView;
    TextView mMinHumansView;
    Activity currentDoingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);

        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        mImageView = findViewById(R.id.iv_do_modulelement);
        mMultiplierView =  findViewById(R.id.tv_do_modulelement_multiplier);
        mTitleView = findViewById(R.id.tv_do_modulelement_title);
        mMinHumansView = findViewById(R.id.tv_do_modulelement_minHumans);
        mNextTitleView = findViewById(R.id.tv_do_next_modulelement);

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

        //Set all content regarding the current ModulElement
        ModulElement currentModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition());
        if(currentModulElement.getPictureUrl()!= null){
            PictureUtil pictureUtil = new PictureUtil(DoActivity.this);
            pictureUtil.initializePictureWithColours(currentModulElement.getPictureUrl(), mImageView, mTitleView);
        }
        mMultiplierView.setText(currentModulElement.getMultiplier().getTimesMultiplied() + " " + currentModulElement.getMultiplier().getType());
        mTitleView.setText(currentModulElement.getTitle());

        mMinHumansView.setText(String.valueOf(currentModulElement.getMinNumberOfHumans()));


        //Set Content regarding the next Element
        //TODO Might wanna add Multiplier to be displayed here (or no?)
        ModulElement nextModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition() +1 );
        mNextTitleView.setText(nextModulElement.getTitle());


    }
}
