package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.letsgo.Adapter.ModulListAdapter;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.SocialModulInfo;
import com.example.android.letsgo.Classes.User;
import com.example.android.letsgo.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulListActivity extends BaseNavDrawActivity implements ModulListAdapter.ModulOnClickHandler {

    RecyclerView mRvModuls;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    User user;
    List<Modul> moduls;
    List<SocialModulInfo> socialModulInfos;
    int counter;
    User filterUser;
    TextView mFilterCreatedBy;

    String filterMode;

    //Arbitrary sign in code for google auth sign in flow
    private static final int RC_SIGN_IN = 567;

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_modul_list, (ViewGroup) findViewById(R.id.content_frame));

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        mRvModuls = findViewById(R.id.rv_modul_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvModuls.setLayoutManager(mLayoutManager);
        mFilterCreatedBy = findViewById(R.id.tv_modul_list_filter_created_by);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_modul_list);
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab_activity_modul_list);

        drawerLayout = findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        User filterUserGivenByIntent = (User) intent.getSerializableExtra("filterUser");
        if(filterUserGivenByIntent != null){
            filterMode = "selectedCreator";
            filterUser=filterUserGivenByIntent;
        }else{
            filterMode = "byAuthUser";
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startModulEditActivityIntent = new Intent(ModulListActivity.this, ModulEditActivity.class);
                startActivity(startModulEditActivityIntent);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                authUser = firebaseAuth.getCurrentUser();
                if(authUser != null){
                    //user is signed in
                    //If necessary checks if the user exists also in the database, if not -> sends to UserCreationActivity
                    if(user == null) {
                        DocumentReference docRef = db.collection("user").document(authUser.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        user = document.toObject(User.class);
                                    } else {
                                        Intent startUserCreationActivityIntent = new Intent(ModulListActivity.this, UserCreationActivity.class);
                                        startActivity(startUserCreationActivityIntent);
                                    }
                                }
                            }
                        });
                    }
                    onSignedInInitialize();
                }else{
                    onSignedOutCleanUp();
                    //user is signed out
                    Log.e("oStartActivity", "For Result has been called");
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //TODO Might wanna enable following before launch
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
        if(authUser != null){
            populateBasicUi();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                authUser = mFirebaseAuth.getCurrentUser();
            }
            else if (resultCode ==RESULT_CANCELED){
                Toast.makeText(ModulListActivity.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getModulsFromDatabase(){
        String filteredById;
        if(filterUser != null){
            filteredById = filterUser.getAuthId();
        }else{
            filteredById = authUser.getUid();
        }

        moduls = new ArrayList<Modul>();
        db.collection("moduls").whereEqualTo("editorUid", filteredById)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            final int resultSize = task.getResult().size();
                            counter = 0;
                            final SocialModulInfo[] socialModulInfos = new SocialModulInfo[resultSize];
                            for(final QueryDocumentSnapshot document : task.getResult()){
                                Modul modul = document.toObject(Modul.class);
                                moduls.add(modul);
                                final int currentsize = moduls.size();

                                //TODO This is a complicated way to retrieve the Infos. Maybe think about restrcuturing database
                                // or find a batched read solution or use transactions when they have offline support
                                //But Maybe its not a problem at all for performance? Dont know
                                db.collection("moduls")
                                        .document(modul.getId())
                                        .collection("socialModulInfo")
                                        .document("avg")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    SocialModulInfo soc = task.getResult().toObject(SocialModulInfo.class);
                                                    socialModulInfos[currentsize-1] = soc;

                                                    //This was the only solution I found to make sure that Ui
                                                    // gets only updated after every single SocialModulinfo has been retrieved
                                                    // as I did not find a solution to batch read all the socialModulInfos
                                                    if(counter+1==resultSize){
                                                        updateUiWithFetchedModuls(moduls, socialModulInfos);
                                                    }
                                                    counter++;
                                            }
                                        };

                            });
                        }
                            Log.e("ModulListActivity", "Has read Moduls from db");
                        }
                        else{
                            Log.d("getModulsFromDB", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private void updateUiWithFetchedModuls(List<Modul> moduls, SocialModulInfo[] socialModulInfosArray){
        List<SocialModulInfo> socialModulInfos = new ArrayList<SocialModulInfo>(Arrays.asList(socialModulInfosArray));
        mAdapter = new ModulListAdapter(this, moduls, socialModulInfos, this);
        mRvModuls.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Modul clickedModul, SocialModulInfo clickedSocialModulInfo) {

        Intent startModulDetailActivityIntent = new Intent(this, ModulDetailActivity.class);
        startModulDetailActivityIntent.putExtra("modul", clickedModul);
        startModulDetailActivityIntent.putExtra("socialModulInfo", clickedSocialModulInfo);
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

    private void onSignedInInitialize(){
        //This happens here because only authenticated users are allowed to read moduls from
        // the database and we would get an error if we get them from DB before USer is authenticated
        if(moduls ==null){
            getModulsFromDatabase();
            populateBasicUi();
        }

    }

    private void onSignedOutCleanUp(){

    }

    private void populateBasicUi(){
        String filteredByName;
        if(filterUser != null){
            filteredByName = filterUser.getDisplayName();
        }else{
            filteredByName = authUser.getDisplayName();
        }
        mFilterCreatedBy.setText(getResources().getString(R.string.modul_list_filter_by_creator) + " " +filteredByName);
    }



}
