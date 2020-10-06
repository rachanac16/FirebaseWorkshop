package com.rachana.firebaseworkshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFoundFragment extends Fragment {
    private UserResponse userResponse;
    public ProfileFoundFragment() {
        // Required empty public constructor
    }
    public ProfileFoundFragment(UserResponse userR){
        userResponse = userR;
    }

    public static ProfileFoundFragment newInstance() {
        //        Bundle args = new Bundle();
//        args.putSerializable("userResponse", userR);
//        fragment.setArguments(args);
        return new ProfileFoundFragment();
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
        View view =  inflater.inflate(R.layout.fragment_profile_found, container, false);
        Button view_profile = view.findViewById(R.id.view_profile_btn);
        Button edit_profile = view.findViewById(R.id.edit_profile_btn);
        Button delete_profile = view.findViewById(R.id.delete_profile_btn);

        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_ll, new ViewProfileFragment(userResponse));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_ll, new ProfileNotFoundFragment(userResponse));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        delete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference profilesDatabaseReference = firebaseDatabase.getReference("profiles");
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                profilesDatabaseReference.child(user.getUid()).removeValue();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_ll, new ProfileNotFoundFragment());
                transaction.commit();
                Toast.makeText(getActivity(), "Profile Deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}