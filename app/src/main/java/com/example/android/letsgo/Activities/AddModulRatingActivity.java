package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.SocialModulInfo;
import com.example.android.letsgo.Classes.User;
import com.example.android.letsgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

public class AddModulRatingActivity extends BaseNavDrawActivity {

    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    User user;
    String uId;
    Activity doneActivity;
    Modul givenModul;
    RatingBar rateBar;
    FloatingActionButton fab;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modul_rating);

        getLayoutInflater().inflate(R.layout.activity_modul_list, (ViewGroup) findViewById(R.id.content_frame));

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth =FirebaseAuth.getInstance();
        uId = mFirebaseAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer_layout);

        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_add_modul_rating);
        fab =(FloatingActionButton) findViewById(R.id.fab_add_modul_rating);
        rateBar = (RatingBar) findViewById(R.id.rb_modul_rating);
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveDoneActivityToDatabase();
                    }
                });
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddModulRatingActivity.this , getResources().getText(R.string.add_modul_rating_rateFirst).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        doneActivity = (Activity) intent.getSerializableExtra("doneActivity");
        givenModul = (Modul) intent.getSerializableExtra("modul");
    }



    private void saveDoneActivityToDatabase() {
        final DocumentReference newActivityRef = db.collection("activities")
                .document();
        doneActivity.setId(newActivityRef.getId());

        final DocumentReference socialModulInfoAvgRef = db.collection("moduls")
                .document(givenModul.getId())
                .collection("socialModulInfo")
                .document("avg");

        // In a transaction, add the new rating and update the aggregate totals
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                SocialModulInfo socialModulInfoAvg = transaction.get(socialModulInfoAvgRef).toObject(SocialModulInfo.class);
                if(socialModulInfoAvg == null){
                    socialModulInfoAvg = new SocialModulInfo();
                }

                // Compute new DoneCOunt
                int newDoneCount = socialModulInfoAvg.getDoneCount() +1;
                socialModulInfoAvg.setDoneCount(newDoneCount);

                //Compute avgDuration
                long duration = doneActivity.getEndTime() - doneActivity.getStartTime();
                long newAvgDuration = (socialModulInfoAvg.getDurationAvg() *( newDoneCount-1) +duration)/newDoneCount;
                socialModulInfoAvg.setDurationAvg(newAvgDuration);

                //Compute new avg Rating
                float newRating = rateBar.getRating();
                float oldAvgRating = socialModulInfoAvg.getRating();
                float oldRateNum = socialModulInfoAvg.getRatingNum();
                float newRateNum = oldRateNum +1;
                float newAvgRating = (oldAvgRating * oldRateNum + newRating) /newRateNum;

                socialModulInfoAvg.setRating(newAvgRating);
                socialModulInfoAvg.setRatingNum(newRateNum);

                //Add LastDoneTimeStamp
                socialModulInfoAvg.setLastDoneTimestamp(doneActivity.getEndTime());

                // Update SocialModulInfo
                //TODO Maybe should set SetOption:Merge (Firestore special option) here for more efficeny
                transaction.set(socialModulInfoAvgRef, socialModulInfoAvg);

                // Save Activity
                transaction.set(newActivityRef, doneActivity);

                return null;
                //TODO Should catch Exception somewhere
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("saveActivity", "Document Snap added" );
                Intent startModulListActivityIntent = new Intent(getApplicationContext(), ModulListActivity.class);
                startActivity(startModulListActivityIntent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Tag", "Transaction failure.", e);
                    }
                });;
    }
}
