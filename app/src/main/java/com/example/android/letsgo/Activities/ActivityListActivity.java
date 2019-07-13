package com.example.android.letsgo.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letsgo.Adapter.ActivityListAdapter;
import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityListActivity extends BaseNavDrawActivity implements ActivityListAdapter.ActivityOnClickHandler {

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String uId;

    RecyclerView mRvActivity;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DrawerLayout drawerLayout;
    List<Activity> activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list, (ViewGroup) findViewById(R.id.content_frame));

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if(authUser != null){
            uId = authUser.getUid();
        }else{
            Log.e("ActivityList Auth", "No User authenticated");
        }

        mRvActivity = findViewById(R.id.rv_activity_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRvActivity.setLayoutManager(mLayoutManager);


        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_list);
        drawerLayout = findViewById(R.id.drawer_layout);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getActivityListFromDatabase();


    }

    private void getActivityListFromDatabase(){
        activities = new ArrayList<Activity>();
        db.collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            final int resultSize = task.getResult().size();
                            Log.e("ActivityListActivity", "Has read Activities from db");
                            for(final QueryDocumentSnapshot document : task.getResult()) {
                                Activity activity = document.toObject(Activity.class);
                                activities.add(activity);
                                final int currentsize = activities.size();
                            }
                            populateUiWithLoadedActivities();
                        }
                        else{
                            Log.d("getActivitiesFromDB", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private void populateUiWithLoadedActivities(){
        mAdapter = new ActivityListAdapter(this, activities, this);
        mRvActivity.setAdapter(mAdapter);
    }

    @Override
    public void onClick(Activity clickedActivity) {
        //TODO Do Something
    }

}
