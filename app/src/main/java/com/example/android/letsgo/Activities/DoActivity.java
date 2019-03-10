package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class DoActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    TextView mMultiplierView;
    TextView mTitleView;
    TextView mNextTitleView;
    Activity currentDoingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);

        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        mMultiplierView =  findViewById(R.id.tv_do_modulelement_multiplier);
        mTitleView = findViewById(R.id.tv_do_modulelement_title);
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
        ModulElement currentModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition());
        mMultiplierView.setText(currentModulElement.getMultiplier().getTimesMultiplied() + " " + currentModulElement.getMultiplier().getType());
        mTitleView.setText(currentModulElement.getTitle());

        ModulElement nextModulElement = currentDoingActivity.getModulElements().get(currentDoingActivity.getCurrentPosition() +1 );
        mNextTitleView.setText(nextModulElement.getTitle());


    }
}
