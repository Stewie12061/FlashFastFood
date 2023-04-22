package com.example.flashfastfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.flashfastfood.Admin.MainAdminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageView logo;
        logo = findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          nextActivity();
                                      }
                                  }, 2000
        );

    }
    private void nextActivity(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            //not login
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);

        }
        else{
            //login
            if (user.getUid().equals("z5YxFgx5nHYe9sQCJU9Tb3h9N7J2")){
                Intent intent = new Intent(WelcomeActivity.this, MainAdminActivity.class);

                startActivity(intent);
            }
            else {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          nextActivity();
                                      }
                                  }, 2000
        );
    }

}