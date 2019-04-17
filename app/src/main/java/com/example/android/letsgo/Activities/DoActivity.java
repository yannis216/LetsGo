package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.OnSwipeTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.constraintlayout.widget.ConstraintLayout;

public class DoActivity extends BaseNavDrawActivity {

    ImageView mImageView;
    TextView mMultiplierView;
    TextView mTitleView;
    TextView mNextTitleView;
    TextView mMinHumansView;
    ProgressBar mSavingActivityBar;
    ConstraintLayout mConstraintLayout;
    ImageButton mNextElementButton;
    ImageButton mPreviousElementButton;
    Activity currentDoingActivity;
    CountDownTimer countDown;
    Modul givenModul;

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String uId;

    MediaPlayer mp5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Screen should stay on in this activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getLayoutInflater().inflate(R.layout.activity_do, (ViewGroup) findViewById(R.id.content_frame));

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

        mConstraintLayout = findViewById(R.id.cl_do);
        mImageView = findViewById(R.id.iv_do_modulelement);
        mMultiplierView =  findViewById(R.id.tv_do_modulelement_multiplier);
        mTitleView = findViewById(R.id.tv_do_modulelement_title);
        mMinHumansView = findViewById(R.id.tv_do_modulelement_minHumans);
        mNextTitleView = findViewById(R.id.tv_do_next_modulelement);
        mNextElementButton = findViewById(R.id.ib_do_next);
        mPreviousElementButton = findViewById(R.id.ib_do_prev);
        mSavingActivityBar = findViewById(R.id.pb_saving_activity);

        mPreviousElementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousStep();
            }
        });


        Intent intent = getIntent();
        givenModul = (Modul) intent.getSerializableExtra("modul");
        currentDoingActivity = generateActivityFromModul(givenModul);

        mConstraintLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                if(currentDoingActivity.getModulElements().size()-1 >currentDoingActivity.getCurrentPosition()) {
                    nextStep();
                }
            }

            @Override
            public void onSwipeRight(){
                if(!(currentDoingActivity.getCurrentPosition() == 0)) {
                    previousStep();
                }
            }
        });

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

        String[] typeStrings = this.getResources().getStringArray(R.array.multiplier_type_array);
        if(currentModulElement.getMultiplier().getType().equals(typeStrings[1])
                || currentModulElement.getMultiplier().getType().equals(typeStrings[2])  ){
            int millis = 0;
            if(currentModulElement.getMultiplier().getType().equals(typeStrings[1])){
                millis = currentModulElement.getMultiplier().getTimesMultiplied() * 1000;
            }
            if(currentModulElement.getMultiplier().getType().equals(typeStrings[2])){
                millis = currentModulElement.getMultiplier().getTimesMultiplied() * 1000 * 60;
            }
            startCountdownWithMillis(currentModulElement, millis);
        }


        mMultiplierView.setText(currentModulElement.getMultiplier().getTimesMultiplied() + " " + currentModulElement.getMultiplier().getType());
        mTitleView.setText(currentModulElement.getTitle());

        mMinHumansView.setText(String.valueOf(currentModulElement.getMinNumberOfHumans()));


        //Set Content regarding the next Element
        //TODO Might wanna add Multiplier to be displayed here (or no?)
        if(currentDoingActivity.getModulElements().size()-1 >currentDoingActivity.getCurrentPosition()) {
            ModulElement nextModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition() + 1);
            mNextTitleView.setText(nextModulElement.getTitle());
            mNextElementButton.setImageResource(R.drawable.baseline_play_arrow_black_24dp);
            mNextElementButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    nextStep();
                }
            });

        }else{
            //If in the last Element let user upload his activity to database when finished
            mNextTitleView.setText(getResources().getText(R.string.do_activity_finish));
            mNextElementButton.setImageResource(R.drawable.baseline_check_circle_black_24dp);
            mNextElementButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    currentDoingActivity.setEndTime(System.currentTimeMillis());
                    Intent startAddModulRatingIntent = new Intent(DoActivity.this, AddModulRatingActivity.class);
                    startAddModulRatingIntent.putExtra("doneActivity", currentDoingActivity);
                    startAddModulRatingIntent.putExtra("modul", givenModul);
                    startActivity(startAddModulRatingIntent);
                }
            });
        }



    }


    private void nextStep(){
        if(countDown!= null){
            countDown.cancel();
        }
        currentDoingActivity.setCurrentPosition(currentDoingActivity.getCurrentPosition()+1);
        updateUi();
    }

    private void previousStep(){
        if(countDown!= null){
            countDown.cancel();
        }
        currentDoingActivity.setCurrentPosition(currentDoingActivity.getCurrentPosition()-1);
        updateUi();
    }





    private void startCountdownWithMillis(final ModulElement currentModulElement, int millis){
        mp5 = MediaPlayer.create(getApplicationContext(), R.raw.countdown_from_five);


        //Constructs the countdown with a short delay of 499ms (Feels better for the user)
        countDown = new CountDownTimer(millis+499, 1000) {

            public void onTick(long millisUntilFinished) {
                //TODO Make this show Minutes properly
                mMultiplierView.setText(String.valueOf(millisUntilFinished/1000) + "s" );
                int currentSecondLeft = (int) millisUntilFinished/1000;

                switch(currentSecondLeft){
                    case 5:
                        mp5.start();
                        break;
                }

            }

            public void onFinish() {
                mp5.release();
                if((currentDoingActivity.getModulElements().size()-1 >currentDoingActivity.getCurrentPosition())) {
                    nextStep();
                }
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp5 != null){
            mp5.release();
        }
    }
}
