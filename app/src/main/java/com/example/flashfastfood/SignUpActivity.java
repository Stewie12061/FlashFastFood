package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private Button signupBtn, loginBtn2;
    private ImageView logo;
    private TextView slogan, text;
    private EditText edtmail, edtpassword, edtfullname, edtphonenumber, edtpasswordconfirm;
    private RadioButton radioButtonselected;
    private RadioGroup radioGroup;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loginBtn2 = findViewById(R.id.loginbtn2);
        signupBtn =  findViewById(R.id.signupbtn);
        logo = findViewById(R.id.logoSignUp);
        slogan = findViewById(R.id.sloganSignUp);
        text = findViewById(R.id.textSignUp);

        edtmail = findViewById(R.id.edtEmail);
        edtpassword = findViewById(R.id.edtPassword);
        edtfullname = findViewById(R.id.edtFullname);
        edtphonenumber = findViewById(R.id.edtPhoneNumber);
        edtpasswordconfirm = findViewById(R.id.edtPasswordConfirm);

        radioGroup = findViewById(R.id.rdgr);
        radioGroup.clearCheck();

        loginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View,String>(logo, "logo");
                pairs[1] = new Pair<View,String>(slogan, "slogan");
                pairs[2] = new Pair<View,String>(text, "text");
                pairs[3] = new Pair<View,String>(edtmail, "email");
                pairs[4] = new Pair<View,String>(edtpassword, "password");
                pairs[5] = new Pair<View,String >(loginBtn2, "button2");
                pairs[6] = new Pair<View,String >(signupBtn, "button");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId = radioGroup.getCheckedRadioButtonId();
                radioButtonselected = findViewById(selectedGenderId);

                String email = edtmail.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();
                String fullname = edtfullname.getText().toString().trim();
                String phonenumber = edtphonenumber.getText().toString().trim();
                String passwordconfirm = edtpasswordconfirm.getText().toString().trim();
                String gender;
                String role = "User".toString().trim();

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
                    progressDialog = new ProgressDialog(SignUpActivity.this);
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        finishAffinity();
                                    }else {
                                        Toast.makeText(SignUpActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}