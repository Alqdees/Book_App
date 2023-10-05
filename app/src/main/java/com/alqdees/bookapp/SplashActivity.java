package com.alqdees.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },3000);
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser ==null){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String usertype = (String) snapshot.child("usertype").getValue();
                    if (usertype.equals("user")){
                        startActivity(new Intent(
                                SplashActivity.this,
                                DashboardActivity.class));
                        finish();
                    }else if (usertype.equals("admin")){
                        startActivity(new Intent(
                                SplashActivity.this,
                                DashboardAdminActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(
                                SplashActivity.this,
                                "لايوجد حساب مطابق ",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(
                            SplashActivity.this,
                            ""+error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}