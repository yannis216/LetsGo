package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.letsgo.Adapter.ModulListAdapter;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulListActivity extends AppCompatActivity implements ModulListAdapter.ModulOnClickHandler {

    RecyclerView mRvModuls;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Arbitrary sign in code for google auth sign in flow
    private static final int RC_SIGN_IN = 567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_list);

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth =FirebaseAuth.getInstance();

        mRvModuls = findViewById(R.id.rv_modul_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvModuls.setLayoutManager(mLayoutManager);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_modul_list);
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_activity_modul_list);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ModulListActivity.this, "Nothing happens here yet",  Toast.LENGTH_SHORT).show();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName());
                }else{
                    onSignedOutCleanUp();
                    //user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //TODO Might wanna enable this before launch
                                    .setIsSmartLockEnabled(false)

                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
            }
            else if (resultCode ==RESULT_CANCELED){
                Toast.makeText(ModulListActivity.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getModulsFromDatabase(){
        final List<Modul> moduls = new ArrayList<Modul>();
        db.collection("moduls")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Modul modul = document.toObject(Modul.class);
                                moduls.add(modul);
                            }
                            updateUiWithFetchedModuls(moduls);
                        }else{
                            Log.d("getModulsFromDB", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private void updateUiWithFetchedModuls(List<Modul> moduls){
        mAdapter = new ModulListAdapter(this, moduls, this);
        mRvModuls.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Modul clickedModul) {

            Intent startModulDetailActivityIntent = new Intent(this, ModulDetailActivity.class);
            startModulDetailActivityIntent.putExtra("modul", clickedModul);
            startActivity(startModulDetailActivityIntent);

    }

    @Override
    protected void onResume(){
        super.onResume();
        //Attach the AuthStateListener here is best practice :-)
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Makes sure the AuthStateListener gets disattached to avoid memory leaks
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void onSignedInInitialize(String username){
        //This happens here because only authenticated users are allowed to read moduls from the database
        getModulsFromDatabase();
    }

    private void onSignedOutCleanUp(){

    }
}
