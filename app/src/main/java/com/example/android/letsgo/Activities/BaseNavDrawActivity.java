package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.letsgo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class BaseNavDrawActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    Context context;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_nav_draw);

        //TODO DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        TextView userNameView = findViewById(R.id.tv_nav_drawer_header_userName);

        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.tv_nav_drawer_header_userName);
        navUsername.setText(authUser.getDisplayName());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()){
                            case R.id.nav_new_modul:
                                Intent startModulEditActivityIntent = new Intent(getApplicationContext() , ModulEditActivity.class);
                                startActivity(startModulEditActivityIntent);
                                break;
                            case R.id.nav_moduls_created_by_me:
                                Intent startModulListActivityIntent = new Intent(getApplicationContext(), ModulListActivity.class);
                                startActivity(startModulListActivityIntent);
                                break;
                            case R.id.nav_new_element:
                                Intent startElementEditActivityIntent = new Intent(getApplicationContext(), ElementEditActivity.class);
                                startActivity(startElementEditActivityIntent);
                                break;
                        }

                        return true;
                    }
                });

    }
}
