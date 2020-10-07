package com.rachana.firebaseworkshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfilesDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    private FirebaseUser mCurrentUser;
    private String mUsername;
    private ChildEventListener mChildEventListener;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;
    private Uri downloadURI;
    private UserResponse userResponse;
    private Fragment fragment;
    private ProgressDialog progress;

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
        if(fragment==null){
            changeFragment("profileNotFound");
        }
        final ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        Dialog.setMessage("Fetching Profile Details...");
        Dialog.show();
        if(mChildEventListener==null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    UserResponse currentUserResponse = snapshot.getValue(UserResponse.class);
                    if(snapshot.getKey()!=null && !snapshot.getKey().equals("")){
                        if(mCurrentUser!=null){
                            if(snapshot.getKey().equals(mCurrentUser.getUid())){
                                userResponse = currentUserResponse;
//                                if(fragment.getClass().toString().contains("profileNo"))
                                changeFragment("profileFound");
                                Toast.makeText(MainActivity.this, "Profile found!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "mCurrent user null", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "snapshot.getkey is null or empty", Toast.LENGTH_SHORT).show();
                    }
                    Dialog.hide();
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    UserResponse currentUserResponse = snapshot.getValue(UserResponse.class);
                    if(snapshot.getKey()!=null && !snapshot.getKey().equals("")){
                        if(mCurrentUser!=null){
                            if(snapshot.getKey().equals(mCurrentUser.getUid())){
                                userResponse = currentUserResponse;
                                getSupportFragmentManager().popBackStack();
                                changeFragment("profileFound");

                                Toast.makeText(MainActivity.this, "Profile Edited!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "mCurrent user null", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "snapshot.getkey is null or empty", Toast.LENGTH_SHORT).show();
                    }
                    Dialog.hide();
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Dialog.hide();
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Dialog.hide();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Dialog.hide();
                }
            };
            mProfilesDatabaseReference.addChildEventListener(mChildEventListener);

        }else{
            Dialog.hide();
        }
    }

    public void changeFragment(String fragmentType){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentType) {
            case "profileFound":
                fragment = new ProfileFoundFragment(userResponse);
                fragmentTransaction.replace(R.id.fragment_ll, fragment);
                break;
            case "profileNotFound":
                fragment = new ProfileNotFoundFragment();
                if(fragment.isAdded()){
                    fragmentTransaction.show(fragment);
                    Log.d("what", "showing");
                }else{
                    fragmentTransaction.replace(R.id.fragment_ll, fragment);
                    Log.d("what", "not showing");
                }
                    break;
            case "viewProfile":
                fragment = new ViewProfileFragment();
                fragmentTransaction.replace(R.id.fragment_ll, fragment);
                break;
        }
        fragmentTransaction.commit();
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
        if(mAuthStateListener !=null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
//        detachDatabaseListener();
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
        }
    }
}