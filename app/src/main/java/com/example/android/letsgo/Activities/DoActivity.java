package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class DoActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);

        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        TextView mMultiplierView =  findViewById(R.id.tv_do_modulelement_multiplier);
        TextView mTitleView = findViewById(R.id.tv_do_modulelement_title);

        Intent intent = getIntent();
        Modul givenModul = (Modul) intent.getSerializableExtra("modul");
        generateActivityFromModul(givenModul);
    }


    private Activity generateActivityFromModul(Modul givenModul){

        //TODO On Authentication write user displayname to device
        long currentTime = System.currentTimeMillis();
        //TODO Add Firebase Id of Modul to modul itself
        new Activity(givenModul.getId, givenModul.getTitle(), givenModul.getModulElements(), authUser.getUid(),  )
    }
}
