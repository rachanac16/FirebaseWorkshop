package com.rachana.firebaseworkshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean isFound = false;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfilesDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    private FirebaseUser mCurrentUser;
    private LinearLayout profile_found_ll;
    private LinearLayout profile_not_found_ll;
    private ChildEventListener mChildEventListener;
    private String mUsername;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;
    private Uri downloadURI;
    private ImageView photoUrl_iv;
    private Button check_results;
    private EditText name_et;
    private EditText college_et;
    private EditText collegeId_et;
    private TextView latitude_tv2;
    private TextView longitude_tv2;
    private Button view_profile_btn;
    private Button edit_profile_btn;
    private Button delete_profile_btn;
    private Button photo_picker;
    private TextView result_tv;
    private LinearLayout result_ll;
    private UserResponse userResponse;
    private TextView name_tv;
    private TextView college_tv;
    private TextView collegeid_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotoStorageReference = mFirebaseStorage.getReference().child("profile_photos");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProfilesDatabaseReference = mFirebaseDatabase.getReference("profiles");

//        if(mCurrentUser!=null){
//            mUserDatabaseReference = mProfilesDatabaseReference.child(mCurrentUser.getUid());
//        }else{
//            Toast.makeText(this, "User id not found", Toast.LENGTH_SHORT).show();
//        }



        profile_found_ll = (LinearLayout) findViewById(R.id.profile_found_ll);
        profile_not_found_ll = (LinearLayout) findViewById(R.id.profile_not_found_ll);

        check_results = (Button) findViewById(R.id.submit_btn);
        name_et = (EditText) findViewById(R.id.name_et);
        college_et = (EditText) findViewById(R.id.college_et);
        collegeId_et = (EditText) findViewById(R.id.collegeid_et);
        latitude_tv2 = (TextView) findViewById(R.id.latitude_tv2);
        longitude_tv2 = (TextView) findViewById(R.id.longitude_tv2);
        photoUrl_iv = (ImageView) findViewById(R.id.photourl_iv);
        result_tv = (TextView) findViewById(R.id.result_tv);
        photo_picker = (Button) findViewById(R.id.photo_picker_btn);
        view_profile_btn = (Button) findViewById(R.id.view_profile_btn);
        edit_profile_btn = (Button) findViewById(R.id.edit_profile_btn);
        delete_profile_btn = (Button) findViewById(R.id.delete_profile_btn);
        result_ll = (LinearLayout) findViewById(R.id.result_ll);
        name_tv = (TextView) findViewById(R.id.name_tv);
        college_tv = (TextView)findViewById(R.id.college_tv);
        collegeid_tv = (TextView)findViewById(R.id.collegeid_tv);

        view_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_et.setVisibility(View.GONE);
                college_et.setVisibility(View.GONE);
                collegeId_et.setVisibility(View.GONE);
                name_tv.setVisibility(View.VISIBLE);
                college_tv.setVisibility(View.VISIBLE);
                collegeid_tv.setVisibility(View.VISIBLE);
                name_tv.setText(userResponse.getName());
                college_tv.setText(userResponse.getCollege());
                collegeid_tv.setText(userResponse.getCollegeId());
                latitude_tv2.setText(userResponse.getLatitude());
                longitude_tv2.setText(userResponse.getLongitude());
                result_ll.setVisibility(View.VISIBLE);
                result_tv.setText(userResponse.getResult());
                Glide.with(photoUrl_iv.getContext())
                        .load(userResponse.getPhotoUrl())
                        .apply(new RequestOptions().override(600, 200))
                        .into(photoUrl_iv);
                profile_found_ll.setVisibility(View.GONE);
                profile_not_found_ll.setVisibility(View.VISIBLE);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_et.setText(userResponse.getName());
                college_et.setText(userResponse.getCollege());
                collegeId_et.setText(userResponse.getCollegeId());
                latitude_tv2.setText(userResponse.getLatitude());
                longitude_tv2.setText(userResponse.getLongitude());
                result_ll.setVisibility(View.GONE);
                Glide.with(photoUrl_iv.getContext())
                        .load(userResponse.getPhotoUrl())
                        .apply(new RequestOptions().override(600, 200))
                        .into(photoUrl_iv);
                profile_found_ll.setVisibility(View.GONE);
                profile_not_found_ll.setVisibility(View.VISIBLE);
            }
        });

        delete_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserDatabaseReference = mProfilesDatabaseReference.child(mCurrentUser.getUid());
                mUserDatabaseReference.removeValue();
                Toast.makeText(MainActivity.this, "Profile Deleted!", Toast.LENGTH_SHORT).show();
                profile_found_ll.setVisibility(View.GONE);
                profile_not_found_ll.setVisibility(View.VISIBLE);
                name_et.setVisibility(View.VISIBLE);

                college_et.setVisibility(View.VISIBLE);
                collegeId_et.setVisibility(View.VISIBLE);
                name_tv.setVisibility(View.GONE);
                college_tv.setVisibility(View.GONE);
                collegeid_tv.setVisibility(View.GONE);
            }
        });

        photo_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


        check_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user!=null){

                    DatabaseReference userReference = mProfilesDatabaseReference.child(user.getUid());
                    userReference.setValue(new UserResponse(name_et.getText().toString(),
                            college_et.getText().toString(), collegeId_et.getText().toString(),
                            latitude_tv2.getText().toString(), longitude_tv2.getText().toString(),
                            downloadURI.toString(), ""));
                    Toast.makeText(MainActivity.this, "Profile Added", Toast.LENGTH_SHORT).show();
                    //clear the form after submitting values
                    name_et.setText("");
                    college_et.setText("");
                    collegeId_et.setText("");
                    profile_not_found_ll.setVisibility(View.GONE);
                    profile_found_ll.setVisibility(View.VISIBLE);

                }else{
                    Toast.makeText(MainActivity.this, "null user object, profile not added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    onSignedInInitialize(user.getDisplayName());
                }else{
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        profile_found_ll.setVisibility(View.GONE);
        profile_not_found_ll.setVisibility(View.VISIBLE);
        result_ll.setVisibility(View.GONE);
        if(mChildEventListener==null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    UserResponse currentUserResponse = (UserResponse) snapshot.getValue(UserResponse.class);
                    if(snapshot.getKey()!=null && !snapshot.getKey().equals("")){
                        if(mCurrentUser!=null){
                            if(snapshot.getKey().equals(mCurrentUser.getUid())){
                                isFound = true;
                                userResponse = currentUserResponse;
                                profile_found_ll.setVisibility(View.VISIBLE);
                                profile_not_found_ll.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Profile found!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "mCurrent user null", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "snapshot.getkey is null or empty", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mProfilesDatabaseReference.addChildEventListener(mChildEventListener);
        }

    }



    private void onSignedOutCleanUp() {
        mUsername = ANONYMOUS;
        detachDatabaseListener();
    }

    private void detachDatabaseListener() {
        if(mChildEventListener!=null){
            mProfilesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.clear_chat_menu:
                //mMessagesDatabaseReference.updateChildren(null);
                //Log.d(log_tag, mMessagesDatabaseReference.child("messages").push().getKey());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener !=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed in Successfully", Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in Failed!", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == RC_PHOTO_PICKER && resultCode==RESULT_OK){
            Uri photoUrl = data.getData();
            final StorageReference photoRef = mPhotoStorageReference.child(photoUrl.getLastPathSegment());

            UploadTask uploadTask = photoRef.putFile(photoUrl);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        downloadURI = downloadUri;
                        Glide.with(photoUrl_iv.getContext())
                                .load(downloadUri)
                                .apply(new RequestOptions().override(600, 200))
                                .into(photoUrl_iv);

                    } else {
                        Toast.makeText(MainActivity.this, "Couldn't Upload try again!", Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
}