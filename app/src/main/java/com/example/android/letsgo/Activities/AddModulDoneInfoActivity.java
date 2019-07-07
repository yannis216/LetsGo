package com.example.android.letsgo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

public class AddModulDoneInfoActivity extends BaseNavDrawActivity {

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
    LinearLayout mLlRating;
    ConstraintLayout mClDoneInfo;
    CoordinatorLayout mCol;
    LinearLayout mLlToggleHelper;

    ImageView mIvUserPic;
    TextView mTvUsername;

    EditText mEtDiComment;
    ImageButton mIbAddPicture;
    ImageView mIvDIPicture;
    TextView mTvAddPictureHelpText;

    TextView mTvModulTitle;
    ImageView mIvModulPicture;
    RatingBar mRbRatedBar;
    TextView mTvDuration;

    SharedPreferences mPrefs;





    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modul_done_info);

        getLayoutInflater().inflate(R.layout.activity_modul_list, (ViewGroup) findViewById(R.id.content_frame));

        mPrefs = this.getSharedPreferences("com.example.android.letsgo.Activities", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth =FirebaseAuth.getInstance();
        authUser = mFirebaseAuth.getCurrentUser();
        uId = authUser.getUid();

        //get current user from sharedprefs
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        Log.e("user", "Read from SharedPrefs:"+json);
        user = gson.fromJson(json, User.class);

        drawerLayout = findViewById(R.id.drawer_layout);

        BottomAppBar bar= (BottomAppBar) findViewById(R.id.bar_add_modul_rating);
        fab =(FloatingActionButton) findViewById(R.id.fab_add_modul_rating);
        rateBar = (RatingBar) findViewById(R.id.rb_modul_rating);
        mLlRating = findViewById(R.id.ll_modul_DI_rating);
        mClDoneInfo = findViewById(R.id.cl_modul_DI_layout);
        mCol = findViewById(R.id.col_activity_modul_DI_coordinator);
        mLlToggleHelper = findViewById(R.id.ll_modul_DI_toggleHelper);

        mIvUserPic = findViewById(R.id.iv_modul_DI_userpic);
        mTvUsername = findViewById(R.id.tv_modul_DI_username);

        mEtDiComment = findViewById(R.id.et_modul_DI_comment);
        mIbAddPicture = findViewById(R.id.ib_modul_DI_addpicture);
        mIvDIPicture = findViewById(R.id.iv_modul_DI_picture);
        mTvAddPictureHelpText = findViewById(R.id.tv_modul_DI_addpicture);

        mTvModulTitle = findViewById(R.id.tv_modul_DI_modul_title);
        mIvModulPicture = findViewById(R.id.iv_modul_DI_thumb);
        mRbRatedBar = findViewById(R.id.rb_modul_DI);
        mTvDuration = findViewById(R.id.tv_modul_DI_duration);

        setOnClickListeners();

        Intent intent = getIntent();
        doneActivity = (Activity) intent.getSerializableExtra("doneActivity");
        givenModul = (Modul) intent.getSerializableExtra("modul");
    }

    private void fillUiWithEntries(){
        mTvUsername.setText(user.getDisplayName());
        //TODO fillIvUserPic
        mRbRatedBar.setRating(rateBar.getRating());
        mTvModulTitle.setText(givenModul.getTitle());
        long duration = doneActivity.getEndTime() - doneActivity.getStartTime();
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        mTvDuration.setText(hms);

    }


    private void setOnClickListeners(){
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                slideRatingOut(mLlRating);
                fillUiWithEntries();
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
                Toast.makeText(AddModulDoneInfoActivity.this , getResources().getText(R.string.add_modul_rating_rateFirst).toString(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void slideRatingOut(View view){
        Transition transition = new TransitionSet()
                .addTransition(new Slide(Gravity.TOP).setDuration(300).addTarget(view))
                .addTransition(new Slide(Gravity.BOTTOM).setDuration(300).addTarget(mLlToggleHelper));

        TransitionManager.beginDelayedTransition(mCol, transition);
        if(view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.INVISIBLE);
            mLlToggleHelper.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.VISIBLE);
            mLlToggleHelper.setVisibility(View.INVISIBLE);
        }
    }
}