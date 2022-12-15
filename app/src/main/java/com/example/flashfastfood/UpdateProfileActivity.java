package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText UPfullname, UPphone, UPcurrentEmail, UPcurrnetPass;
    private RadioGroup UPradiogr;
    private RadioButton Upradiobtnseletected;
    private Button btnUpdate;
    private TextView goback;
    private String fullname, email, phonenumber, gender, password;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        UPfullname = findViewById(R.id.UPedtFullname);
        UPphone = findViewById(R.id.UPedtPhoneNumber);
        UPcurrentEmail = findViewById(R.id.UPedtEmailLogIn);
        UPcurrnetPass = findViewById(R.id.UPedtPasswordLogIn);
        UPradiogr = findViewById(R.id.UPrdgr);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        showProfile(firebaseUser);

        btnUpdate = findViewById(R.id.updatebtn);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullname = readUserDetails.FullName;
                    email = firebaseUser.getEmail();
                    phonenumber = readUserDetails.PhoneNumber;
                    gender = readUserDetails.Gender;

                    UPfullname.setText(fullname);
                    UPcurrentEmail.setText(email);
                    UPphone.setText(phonenumber);
                    if (gender.equals("Male")){
                        Upradiobtnseletected = findViewById(R.id.UPradioMen);
                    }else {
                        Upradiobtnseletected = findViewById(R.id.UPradioWomen);
                    }
                    Upradiobtnseletected.setChecked(true);
                }else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        int selectedGenderID = UPradiogr.getCheckedRadioButtonId();
        Upradiobtnseletected = findViewById(selectedGenderID);

        String email = UPcurrentEmail.getText().toString().trim();
        String password = UPcurrnetPass.getText().toString().trim();

        String mobileRegex = "(0[3|5|7|8|9])+([0-9]{8})";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(phonenumber);

        if (TextUtils.isEmpty(fullname)){
            UPfullname.setError("You have to fill this information!");
            UPfullname.requestFocus();
        }else if (TextUtils.isEmpty(email)){
            UPcurrentEmail.setError("You have to fill this information!");
            UPcurrentEmail.requestFocus();
        }else if (UPradiogr.getCheckedRadioButtonId() == -1){
            Upradiobtnseletected.setError("You have to select your gender!");
            Upradiobtnseletected.requestFocus();
        }else if (TextUtils.isEmpty(phonenumber)){
            UPphone.setError("You have to fill this information!");
            UPphone.requestFocus();
        }else if (phonenumber.length() != 10){
            UPphone.setError("Mobile number should be 10 digits!");
            UPphone.requestFocus();
        }else if (!mobileMatcher.find()){
            UPphone.setError("Mobile is not valid!");
            UPphone.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            UPcurrnetPass.setError("You have to fill this information!");
            UPcurrnetPass.requestFocus();
        }else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            //load progressbar
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_progress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UpdateProfileActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                gender = Upradiobtnseletected.getText().toString();
                                fullname = UPfullname.getText().toString();
                                phonenumber = UPphone.getText().toString();

                                //enter data into firebase
                                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(gender, fullname, phonenumber);

                                //extract user reference from database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

                                String userID = firebaseUser.getUid();
                                reference.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //Stop user from returning to UpdateProfileActivily
                                            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }else {
                                            try{
                                                throw task.getException();
                                            }catch (Exception e){
                                                Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(UpdateProfileActivity.this, "Wrong Email or Password!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}