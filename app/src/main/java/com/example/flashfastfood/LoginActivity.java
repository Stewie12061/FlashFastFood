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
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.Admin.MainAdminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button signupBtn2, loginBtn, forgetPass;
    private ImageView logo;
    private TextView slogan, text;
    private TextInputLayout edtmail, edtpassword;
    private EditText inputEmail, inputPassword;

    private ProgressDialog progressDialog;
    DatabaseReference userRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered Users");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        signupBtn2 = findViewById(R.id.signupbtn2);
        logo = findViewById(R.id.logoLogIn);
        slogan = findViewById(R.id.sloganLogIn);
        text = findViewById(R.id.textLogIn);
        edtmail = findViewById(R.id.EmailLogIn);
        edtpassword = findViewById(R.id.passwordLogIn);
        loginBtn = findViewById(R.id.loginbtn);

        forgetPass = findViewById(R.id.forgetpass);

        inputEmail = findViewById(R.id.edtEmailLogIn);
        inputPassword = findViewById(R.id.edtPasswordLogIn);

        signupBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(logo, "logo");
                pairs[1] = new Pair<View, String>(slogan, "slogan");
                pairs[2] = new Pair<View, String>(text, "text");
                pairs[3] = new Pair<View, String>(edtmail, "email");
                pairs[4] = new Pair<View, String>(edtpassword, "password");
                pairs[5] = new Pair<View, String>(loginBtn, "button");
                pairs[6] = new Pair<View, String>(signupBtn2, "button2");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn();
            }

            private void onClickSignIn() {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Email can't be blank!");
                    inputEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Password required!");
                    inputPassword.requestFocus();
                }else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String adminusername = snapshot.child("admin").child("Username").getValue().toString();
                            String passwordadmin = snapshot.child("admin").child("Password").getValue().toString();

                            if (adminusername.equals(email) && passwordadmin.equals(password)){
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressDialog.dismiss();

                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                                else {
                                                    Toast.makeText(LoginActivity.this, "Wrong Email or Password!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else {
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressDialog.dismiss();

                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                                else {
                                                    Toast.makeText(LoginActivity.this, "Wrong Email or Password!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

    }
}