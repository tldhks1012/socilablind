package com.kkkhhh.socialblinddate.Activity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkkhhh.socialblinddate.R;

public class StartAct extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid().toString();
            databaseReference.child("users").child(uid).child("check").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int value = dataSnapshot.getValue(Integer.class);
                    if (value == 1) {
                        Intent intent = new Intent(StartAct.this, SignProfileAct.class);
                        startActivity(intent);
                        finish();
                    } else if (value == 2) {
                        Intent intent = new Intent(StartAct.this, SignImageAct.class);
                        startActivity(intent);
                        finish();
                    } else if (value == 3) {
                        Intent intent = new Intent(StartAct.this, MainAct.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            Intent intent = new Intent(StartAct.this, WelcomeAct.class);
            startActivity(intent);
            finish();
        }


    }
}


