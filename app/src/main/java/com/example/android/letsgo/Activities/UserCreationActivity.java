package com.example.android.letsgo.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.letsgo.Classes.User;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserCreationActivity extends AppCompatActivity {
    EditText mUsernameEdit;
    ImageView mUserProfilePictureView;
    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser authUser;
    String pictureUrl;
    InputStream inputStream;

    FirebaseStorage storage;
    DocumentReference userReference;
    Context context;

    int PICK_PHOTO_FOR_USER = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        authUser=mFirebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        context = getApplicationContext();


        FloatingActionButton fab = findViewById(R.id.fab_activity_user_creation);
        mUsernameEdit = findViewById(R.id.et_user_creation_name);
        mUserProfilePictureView = findViewById(R.id.iv_user_creation_picture);

        mUserProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_PHOTO_FOR_USER);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUsernameEdit.getText().toString();
                if(!pictureUrl.isEmpty()){

                }
                User newUser = new User(userName);

                prepareSaveUserToDatabase(newUser);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_USER && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //TODO Display an error
                return;
            }else {
                try {
                    inputStream = UserCreationActivity.this.getContentResolver().openInputStream(data.getData()); //TODO delete is safe?
                    Uri inputUri = data.getData();
                    pictureUrl = inputUri.toString();
                    if (pictureUrl != null) {
                        PictureUtil pictureUtil = new PictureUtil(UserCreationActivity.this, mUserProfilePictureView);
                        pictureUtil.loadTitlePictureIntoImageView(pictureUrl);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void prepareSaveUserToDatabase(User newUser){
        userReference = db.collection("user").document(authUser.getUid());
        String userId = authUser.getUid();
        newUser.setAuthId(userId);
        if(!(inputStream ==null)){
            saveProfilePictureToStorage(newUser);
        }else{
            continueSaveUserToDatabase(newUser);
        }
    }

    private void saveProfilePictureToStorage(final User user){
        final StorageReference userImageRef = storage.getReference().child("images/"+authUser.getUid()+"/profilepicture_originalPicture");

        UploadTask uploadTask = userImageRef.putStream(inputStream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, R.string.picture_upload_failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                //TODO Give some Visual Feedback on Loading Process and so on
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return userImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    user.setProfilePictureUrl(downloadUrl);
                    continueSaveUserToDatabase(user);
                    Log.e("DownloadUrl", downloadUrl);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void continueSaveUserToDatabase(final User newUser){
        userReference
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, R.string.user_creation_saved_successfully, Toast.LENGTH_SHORT).show();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getDisplayName()).build();
                        authUser.updateProfile(profileUpdates);

                        Intent startModulListActivityIntent = new Intent(UserCreationActivity.this, ModulListActivity.class);
                        startActivity(startModulListActivityIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveElement", "Error adding document", e);
                    }
                });
    }

}
