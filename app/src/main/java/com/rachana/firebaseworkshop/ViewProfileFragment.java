package com.rachana.firebaseworkshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class ViewProfileFragment extends Fragment {

    private UserResponse userResponse;
    public ViewProfileFragment() {
        // Required empty public constructor
    }
    public ViewProfileFragment(UserResponse userR) {
        userResponse = userR;
    }


    public static ViewProfileFragment newInstance(String param1, String param2) {
        return new ViewProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        TextView name_tv = view.findViewById(R.id.name_tv);
        TextView college = view.findViewById(R.id.college_tv);
        TextView college_id = view.findViewById(R.id.collegeid_tv);
        TextView latitude_tv_vp = view.findViewById(R.id.latitude_tv_vp);
        TextView longitude_tv_vp = view.findViewById(R.id.longitude_tv_vp);
        TextView result_tv = view.findViewById(R.id.result_tv);
        Button back_to_home = view.findViewById(R.id.back_to_home);

        name_tv.setText(userResponse.getName());
        college.setText(userResponse.getCollege());
        college_id.setText(userResponse.getCollegeId());
        latitude_tv_vp.setText(userResponse.getLatitude());
        longitude_tv_vp.setText(userResponse.getLongitude());
        result_tv.setText(userResponse.getResult());

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}