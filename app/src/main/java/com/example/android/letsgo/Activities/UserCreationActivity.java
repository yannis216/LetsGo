package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.letsgo.Classes.User;
import com.example.android.letsgo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class UserCreationActivity extends AppCompatActivity {
    EditText mUsernameEdit;
    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser=mFirebaseAuth.getCurrentUser();


        FloatingActionButton fab = findViewById(R.id.fab_activity_user_creation);
        mUsernameEdit = findViewById(R.id.et_user_creation_name);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUsernameEdit.getText().toString();
                User newUser = new User(userName);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName).build();

                authUser.updateProfile(profileUpdates);

                db.collection("user").document(authUser.getUid()).set(newUser);

                Intent startModulListActivityIntent = new Intent(UserCreationActivity.this, ModulListActivity.class);
                startActivity(startModulListActivityIntent);

            }
        });


    }
}
