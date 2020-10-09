package com.rachana.firebaseworkshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
                String name = name_et.getText().toString();
                String college = college_et.getText().toString();
                String college_id = collegeId_et.getText().toString();
                if(name.equals("") || college.equals("") || college_id.equals("")){
                    Toast.makeText(getActivity(), "Any field cannot be blank!", Toast.LENGTH_SHORT).show();
                }else if(!college_id.matches("[0-9]*")){
                    Toast.makeText(getActivity(), "Invalid ID", Toast.LENGTH_SHORT).show();
                }else if(name.matches(".*\\d.*") || college.matches(".*\\d.*")){
                    Toast.makeText(getActivity(), "Name or College cannot have digits", Toast.LENGTH_SHORT).show();
                } else{
                    userReference.setValue(new UserResponse(name, college, college_id,
                            latitude_tv_pnf.getText().toString(), longitude_tv2_pnf.getText().toString(), ""));
                }
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_et.getText().toString();
                String college = college_et.getText().toString();
                String college_id = collegeId_et.getText().toString();
                if(name.equals("") || college.equals("") || college_id.equals("")){
                    Toast.makeText(getActivity(), "Any field cannot be blank!", Toast.LENGTH_SHORT).show();
                }else if(!college_id.matches("[0-9]*")){
                    Toast.makeText(getActivity(), "Invalid ID", Toast.LENGTH_SHORT).show();
                }else if(name.matches(".*\\d.*") || college.matches(".*\\d.*")){
                    Toast.makeText(getActivity(), "Name or College cannot have digits", Toast.LENGTH_SHORT).show();
                } else{
                    userReference.setValue(new UserResponse(name, college, college_id,
                            latitude_tv_pnf.getText().toString(), longitude_tv2_pnf.getText().toString(), ""));
                }
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