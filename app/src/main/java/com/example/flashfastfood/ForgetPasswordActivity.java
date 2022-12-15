package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button forgetpassbtn;
    private EditText emailforgetpass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView goback;

    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        forgetpassbtn = findViewById(R.id.forgetpassbtn);
        emailforgetpass = findViewById(R.id.edtEmailForgetpass);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        forgetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailforgetpass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter your registerd email!",Toast.LENGTH_SHORT).show();
                    emailforgetpass.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter valid email!",Toast.LENGTH_SHORT).show();
                    emailforgetpass.requestFocus();
                }else {
                    progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Please check your email for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException exception){
                        emailforgetpass.setError("User does not exists or is no longer valid. Please register again.");
                    }catch (Exception exception){
                        Log.e(TAG, exception.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}