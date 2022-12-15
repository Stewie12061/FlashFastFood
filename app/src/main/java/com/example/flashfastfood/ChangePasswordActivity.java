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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView goback;
    private Button changePass;
    private EditText newPass, confirmNewPass, edtEmail, edtPass;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newPass = findViewById(R.id.edtNewPass);
        confirmNewPass = findViewById(R.id.edtConfirmNewPass);
        edtEmail = findViewById(R.id.CPedtEmailLogIn);
        edtPass = findViewById(R.id.CPedtPasswordLogIn);
        changePass = findViewById(R.id.changePassbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changePassword();
            }
        });

    }
    private void changePassword() {
        String userNewPass = newPass.getText().toString();
        String userConfirmNewPass = confirmNewPass.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();

        if (TextUtils.isEmpty(userNewPass)){
            newPass.setError("New password required!");
            newPass.requestFocus();
        }else if (userNewPass.length() < 6){
            newPass.setError("New Password should be at least 6 digits!");
            newPass.requestFocus();
        }else if(password.matches(userNewPass)){
            newPass.setError("New password must be different from your current password!");
            newPass.requestFocus();
        }else if(TextUtils.isEmpty(userConfirmNewPass)){
            confirmNewPass.setError("New Password confirm required!");
            confirmNewPass.requestFocus();
        }else if (!userNewPass.matches(userConfirmNewPass)){
            confirmNewPass.setError("New Password confirm incorrect!");
            confirmNewPass.requestFocus();
        }else if(TextUtils.isEmpty(email)){
            edtEmail.setError("You have to fill your current email to change your password!");
            edtEmail.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            edtPass.setError("You have to fill your current password to change your password!");
            edtPass.requestFocus();
        }else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_progress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(ChangePasswordActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(userNewPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(ChangePasswordActivity.this, "Your password has been changed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                        else{
                                            try{
                                                throw task.getException();
                                            }catch (Exception e){
                                                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Wrong Email or Password!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}