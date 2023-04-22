package com.example.flashfastfood.Guest;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.LoginActivity;
import com.example.flashfastfood.MainActivity;
import com.example.flashfastfood.R;
import com.example.flashfastfood.ReadWriteUserDetails;
import com.example.flashfastfood.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuestProfileFragment extends Fragment {
    private Button signupBtn, loginBtn2;
    private ImageView logo;
    private TextView slogan, text;
    private EditText edtmail, edtpassword, edtfullname, edtphonenumber, edtpasswordconfirm;
    private RadioButton radioButtonselected;
    private RadioGroup radioGroup;

    private ProgressDialog progressDialog;

    public GuestProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginBtn2 = view.findViewById(R.id.loginbtn2);
        signupBtn =  view.findViewById(R.id.signupbtn);
        logo = view.findViewById(R.id.logoSignUp);
        slogan = view.findViewById(R.id.sloganSignUp);
        text = view.findViewById(R.id.textSignUp);

        edtmail = view.findViewById(R.id.edtEmail);
        edtpassword = view.findViewById(R.id.edtPassword);
        edtfullname = view.findViewById(R.id.edtFullname);
        edtphonenumber = view.findViewById(R.id.edtPhoneNumber);
        edtpasswordconfirm = view.findViewById(R.id.edtPasswordConfirm);

        radioGroup = view.findViewById(R.id.rdgr);
        radioGroup.clearCheck();

        loginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId = radioGroup.getCheckedRadioButtonId();
                radioButtonselected = view.findViewById(selectedGenderId);

                String email = edtmail.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();
                String fullname = edtfullname.getText().toString().trim();
                String phonenumber = edtphonenumber.getText().toString().trim();
                String passwordconfirm = edtpasswordconfirm.getText().toString().trim();
                String gender;
                String role = "User";

                //Validate Mobile Number using Matcher and Patter
                String mobileRegex = "(0[3|5|7|8|9])+([0-9]{8})";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(phonenumber);

                if (TextUtils.isEmpty(fullname)){
                    edtfullname.setError("You have to fill this information!");
                    edtfullname.requestFocus();
                }else if (TextUtils.isEmpty(email)){
                    edtmail.setError("You have to fill this information!");
                    edtmail.requestFocus();
                }else if (radioGroup.getCheckedRadioButtonId() == -1){
                    radioButtonselected.setError("You have to select your gender!");
                    radioButtonselected.requestFocus();
                }else if (TextUtils.isEmpty(phonenumber)){
                    edtphonenumber.setError("You have to fill this information!");
                    edtphonenumber.requestFocus();
                }else if (phonenumber.length() != 10){
                    edtphonenumber.setError("Mobile number should be 10 digits!");
                    edtphonenumber.requestFocus();
                }else if (!mobileMatcher.find()){
                    edtphonenumber.setError("Mobile is not valid!");
                    edtphonenumber.requestFocus();
                }else if (TextUtils.isEmpty(password)){
                    edtpassword.setError("You have to fill this information!");
                    edtpassword.requestFocus();
                }else if (password.length() < 6){
                    edtpassword.setError("Password should be at least 6 digits!");
                    edtpassword.requestFocus();
                }else if (TextUtils.isEmpty(passwordconfirm)){
                    edtpasswordconfirm.setError("You have to fill this information!");
                    edtpasswordconfirm.requestFocus();
                }else if (!password.equals(passwordconfirm)){
                    edtpasswordconfirm.setError("Password confirm incorrect!");
                    edtpasswordconfirm.requestFocus();
                }else {
                    gender = radioButtonselected.getText().toString();
                    //load progressbar
                    progressDialog = new ProgressDialog(getActivity().getApplicationContext());
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    registerUser(role, fullname, email, gender, phonenumber, password, passwordconfirm);
                }
            }
        });
    }

    private void registerUser(String role, String fullname, String email, String gender, String phonenumber, String password, String passwordconfirm) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            //enter users data into firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(role, fullname, email, gender, phonenumber);

                            //extracting user reference from database for "registered users"
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
                            DatabaseReference referenceProfile = firebaseDatabase.getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                        startActivity(intent);

                                    }else {
                                        Toast.makeText(getContext(), "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}