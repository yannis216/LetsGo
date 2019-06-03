package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letsgo.Adapter.UserListAdapter;
import com.example.android.letsgo.Classes.User;
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

public class UserListActivity extends BaseNavDrawActivity implements UserListAdapter.UserOnClickHandler {

    RecyclerView mRvUser;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DrawerLayout drawerLayout;

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    List<User> userList;
    String uId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_list, (ViewGroup) findViewById(R.id.content_frame));

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        if(authUser != null){
            uId = authUser.getUid();
        }else{
            Log.e("UserList Auth", "No User authenticated");
        }

        mRvUser = findViewById(R.id.rv_user_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRvUser.setLayoutManager(mLayoutManager);
        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_activity_user_list);
        drawerLayout = findViewById(R.id.drawer_layout);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getUserListFromDatabase();


    }

    private void getUserListFromDatabase(){
        userList = new ArrayList<User>();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            final int resultSize = task.getResult().size();
                            Log.e("UserListActivity", "Has read Users from db");
                            for(final QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                                final int currentsize = userList.size();
                            }
                            populateUiWithLoadedUsers();
                        }
                        else{
                            Log.d("getModulsFromDB", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private void populateUiWithLoadedUsers(){
        mAdapter = new UserListAdapter(this, userList, this);
        mRvUser.setAdapter(mAdapter);
    }
    @Override
    public void onClick(User clickedUser) {
        Intent startModulListActivityIntent = new Intent(this, ModulListActivity.class);
        startModulListActivityIntent.putExtra("user", clickedUser);
        startActivity(startModulListActivityIntent);

    }

}
