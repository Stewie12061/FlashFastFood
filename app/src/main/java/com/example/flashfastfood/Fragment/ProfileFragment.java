package com.example.flashfastfood.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.ChangePasswordActivity;
import com.example.flashfastfood.LoginActivity;
import com.example.flashfastfood.R;
import com.example.flashfastfood.ReadWriteUserDetails;
import com.example.flashfastfood.UpdateProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private TextView txtemail, txtgender, txtfullname, txtphone, changePassbtn, txtL1, txtL2, txtL3, txtL4, txtL5;
    private ImageView profileAvatar;
    private Button signOut;
    private LinearLayout editUser, notification;
    private Animation topAnim, bottomAnim, rightAnim, leftAnim;

    private TextView appName1, appName2;

    private FirebaseAuth firebaseAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileAvatar = view.findViewById(R.id.profile_avatar);
        txtemail = view.findViewById(R.id.settextEmail);
        txtfullname = view.findViewById(R.id.settextFullname);
        txtgender = view.findViewById(R.id.settextGender);
        txtphone = view.findViewById(R.id.settextPhone);
        changePassbtn = view.findViewById(R.id.goToChangePass);
        editUser = view.findViewById(R.id.edit_user);
        signOut = view.findViewById(R.id.signoutbnt);

        txtL1 = view.findViewById(R.id.textLeft1);
        txtL2 = view.findViewById(R.id.textLeft2);
        txtL3 = view.findViewById(R.id.textLeft3);
        txtL4 = view.findViewById(R.id.textLeft4);
        txtL5 = view.findViewById(R.id.textLeft5);

        appName1 = view.findViewById(R.id.appName1);
        appName2 = view.findViewById(R.id.appName2);

        //animation
        topAnim = AnimationUtils.loadAnimation(getContext(), R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_animation);
        leftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.left_animation);
        rightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.right_animation);

        txtemail.setAnimation(rightAnim);
        txtfullname.setAnimation(rightAnim);
        txtgender.setAnimation(rightAnim);
        txtphone.setAnimation(rightAnim);
        changePassbtn.setAnimation(rightAnim);
        txtL1.setAnimation(leftAnim);
        txtL2.setAnimation(leftAnim);
        txtL3.setAnimation(leftAnim);
        txtL4.setAnimation(leftAnim);
        txtL5.setAnimation(leftAnim);

        profileAvatar.setAnimation(bottomAnim);
        appName2.setAnimation(topAnim);
        appName1.setAnimation(topAnim);
        signOut.setAnimation(bottomAnim);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(getActivity(), "No user logon", Toast.LENGTH_SHORT).show();
        }else {
            showUserProfile(firebaseUser);
        }

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        changePassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        notification = view.findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Developing",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Registered Users");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    String fullname = readUserDetails.FullName;
                    String email = firebaseUser.getEmail();
                    String phoneNumber = readUserDetails.PhoneNumber;
                    String gender = readUserDetails.Gender;

                    txtgender.setText(gender);
                    txtfullname.setText(fullname);
                    txtphone.setText(phoneNumber);
                    txtemail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Something when wrong!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}