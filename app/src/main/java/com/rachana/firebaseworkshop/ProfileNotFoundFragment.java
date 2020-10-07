package com.rachana.firebaseworkshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileNotFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileNotFoundFragment extends Fragment {

    private UserResponse userResponse;

    EditText name_et;
    EditText college_et;
    EditText collegeId_et;
    TextView latitude_tv_pnf;
    TextView longitude_tv2_pnf;
    LinearLayout edit_ll;
    Button cancel_btn;
    Button submit_btn;
    Button edit_btn;
    Button location_btn;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    GPSTracker gps;

    public ProfileNotFoundFragment() {
        // Required empty public constructor
        setArguments(new Bundle());
    }

    public ProfileNotFoundFragment(UserResponse userR){
        userResponse = userR;
    }

    public static ProfileNotFoundFragment newInstance() {
        return new ProfileNotFoundFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_not_found, container, false);
        name_et = view.findViewById(R.id.name_et);
        college_et = view.findViewById(R.id.college_et);
        collegeId_et = view.findViewById(R.id.collegeid_et);
        latitude_tv_pnf = view.findViewById(R.id.latitude_tv_pnf);
        longitude_tv2_pnf = view.findViewById(R.id.longitude_tv_pnf);
        edit_ll = view.findViewById(R.id.edit_ll);
        cancel_btn = view.findViewById(R.id.cancel_btn);
        submit_btn = view.findViewById(R.id.submit_btn);
        edit_btn = view.findViewById(R.id.edit_btn);
        location_btn = view.findViewById(R.id.location_btn);

        if (getArguments() != null) {
            Bundle mySavedInstance = getArguments();
            Log.d("what", getArguments().toString());
            Log.d("what", "miracle name: "+mySavedInstance.getString("name") );
        }else{
            Log.d("what", "getArguments is null");
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference profilesDatabaseReference = firebaseDatabase.getReference("profiles");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final DatabaseReference userReference = profilesDatabaseReference.child(user.getUid());

        if(userResponse!=null){
            submit_btn.setVisibility(View.GONE);
            edit_ll.setVisibility(View.VISIBLE);
            name_et.setText(userResponse.getName());
            college_et.setText(userResponse.getCollege());
            collegeId_et.setText(userResponse.getCollegeId());
            latitude_tv_pnf.setText(userResponse.getLatitude());
            longitude_tv2_pnf.setText(userResponse.getLongitude());
        }else{
            submit_btn.setVisibility(View.VISIBLE);
            edit_ll.setVisibility(View.GONE);
        }

//        photo_picker_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
//                if(!downloadURI.equals("")){
//                    Glide.with(photourl_iv.getContext())
//                            .load(downloadURI)
//                            .apply(new RequestOptions().override(600, 300))
//                            .into(photourl_iv);
//                }
//            }
//        });

        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(getActivity());

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    latitude_tv_pnf.setText(latitude+"");
                    longitude_tv2_pnf.setText(longitude+"");
                    Toast.makeText(getActivity(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userReference.setValue(new UserResponse(name_et.getText().toString(),
                        college_et.getText().toString(), collegeId_et.getText().toString(),
                        latitude_tv_pnf.getText().toString(), longitude_tv2_pnf.getText().toString(), ""));
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserResponse userResponse = new UserResponse(name_et.getText().toString(),
                        college_et.getText().toString(), collegeId_et.getText().toString(),
                        latitude_tv_pnf.getText().toString(), longitude_tv2_pnf.getText().toString(), "");
                userReference.setValue(userResponse);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //super method removed
//        Log.d("what", "start");
//        if (resultCode == -1) {
//            Log.d("what", "in");
//            if (requestCode == 2) {
//                Uri photoUrl = data.getData();
//                FirebaseStorage mFirebaseStorage;
//                StorageReference mPhotoStorageReference;
//                mFirebaseStorage = FirebaseStorage.getInstance();
//                mPhotoStorageReference = mFirebaseStorage.getReference().child("profile_photos");
//                final StorageReference photoRef = mPhotoStorageReference.child(photoUrl.getLastPathSegment());
//
//                UploadTask uploadTask = photoRef.putFile(photoUrl);
//                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//                        // Continue with the task to get the download URL
//                        return photoRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//
//                            Uri downloadUri = task.getResult();
//                            if (downloadUri != null) {
//                                Log.d("what", downloadUri.toString());
//                                downloadURI = downloadUri.toString();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), "Couldn't Upload try again!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("name", name_et.getText().toString());
        outState.putString("college", college_et.getText().toString());
        outState.putString("college_id", collegeId_et.getText().toString());
        outState.putString("latitude", latitude_tv_pnf.getText().toString());
        outState.putString("longitude", longitude_tv2_pnf.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            name_et.setText(savedInstanceState.getString("name"));
            college_et.setText(savedInstanceState.getString("college"));
            collegeId_et.setText(savedInstanceState.getString("college_id"));
            latitude_tv_pnf.setText(savedInstanceState.getString("latitude"));
            longitude_tv2_pnf.setText(savedInstanceState.getString("longitude"));
        }
    }

}