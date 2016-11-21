package com.kkkhhh.socialblinddate.Activity;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Fragment.FirstMainFrg;
import com.kkkhhh.socialblinddate.Fragment.FourMainFrg;
import com.kkkhhh.socialblinddate.Fragment.SecondMainFrg;
import com.kkkhhh.socialblinddate.Fragment.ThirdMainFrg;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;

public class MainAct extends AppCompatActivity implements View.OnClickListener {
    private ImageView actionPublicList, actionMyList, actionMsg, actionProfile;
    private FirebaseAuth mFireAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String uID = mFireAuth.getCurrentUser().getUid();
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUser();

    }

    private void init() {

        actionPublicList = (ImageView) findViewById(R.id.list_public_img);
        actionMyList = (ImageView) findViewById(R.id.list_my_img);
        actionMsg = (ImageView) findViewById(R.id.msg_img);
        actionProfile = (ImageView) findViewById(R.id.profile_img);

        actionPublicList.setOnClickListener(this);
        actionMyList.setOnClickListener(this);
        actionMsg.setOnClickListener(this);
        actionProfile.setOnClickListener(this);

        mFragment = new FirstMainFrg();

        actionPublicList.setImageResource(R.drawable.ic_action_list_public_yellow);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, mFragment);
        fragmentTransaction.commit();

    }
    private void checkUser() {

        if (mFireAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainAct.this, WelcomeAct.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences preferences = getSharedPreferences(UserValue.SHARED_NAME, MODE_PRIVATE);
            String userID = preferences.getString(UserValue.USER_ID, null);
            if (userID == null) {
                databaseReference.child("users").child(uID).child("check").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int value = dataSnapshot.getValue(Integer.class);
                        if (value == 1) {
                            Intent intent = new Intent(MainAct.this, SignProfileAct.class);
                            startActivity(intent);
                            finish();
                        } else if (value == 2) {
                            Intent intent = new Intent(MainAct.this, SignImageAct.class);
                            startActivity(intent);
                            finish();
                        } else if (value == 3) {
                            _userValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }else{
                init();
            }

        }


    }

    private void _userValue() {
        databaseReference.child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    databaseReference.child("users").child(uID).child("tokenValue").setValue(refreshedToken);
                    SharedPreferences preferences = getSharedPreferences(UserValue.SHARED_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(UserValue.USER_ID, userModel._uID);
                    editor.putString(UserValue.USER_NAME, userModel._uNickname);
                    editor.putString(UserValue.USER_AGE, userModel._uAge);
                    editor.putString(UserValue.USER_GENDER, userModel._uGender);
                    editor.putString(UserValue.USER_LOCAL, userModel._uLocal);
                    editor.putString(UserValue.USER_IMG1, userModel._uImage1);
                    editor.putString(UserValue.USER_IMG2, userModel._uImage2);
                    editor.putString(UserValue.USER_IMG3, userModel._uImage3);
                    editor.putString(UserValue.USER_IMG4, userModel._uImage4);
                    editor.putString(UserValue.USER_IMG5, userModel._uImage5);
                    editor.putString(UserValue.USER_IMG6, userModel._uImage6);
                    editor.putInt(UserValue.USER_COIN,userModel._uCoin);
                    editor.putString(UserValue.USER_TOKEN,refreshedToken);
                    editor.commit();
                    init();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.list_public_img: {
                mFragment = new FirstMainFrg();
                actionPublicList.setImageResource(R.drawable.ic_action_list_public_yellow);
                actionMyList.setImageResource(R.drawable.ic_action_list_my_white);
                actionMsg.setImageResource(R.drawable.ic_action_msg_white);
                actionProfile.setImageResource(R.drawable.ic_action_profile_white);
                break;
            }
            case R.id.list_my_img: {
                mFragment = new SecondMainFrg();
                actionPublicList.setImageResource(R.drawable.ic_action_list_public_white);
                actionMyList.setImageResource(R.drawable.ic_action_list_my_yellow);
                actionMsg.setImageResource(R.drawable.ic_action_msg_white);
                actionProfile.setImageResource(R.drawable.ic_action_profile_white);
                break;
            }
            case R.id.msg_img: {
                mFragment = new ThirdMainFrg();
                actionPublicList.setImageResource(R.drawable.ic_action_list_public_white);
                actionMyList.setImageResource(R.drawable.ic_action_list_my_white);
                actionMsg.setImageResource(R.drawable.ic_action_msg_yellow);
                actionProfile.setImageResource(R.drawable.ic_action_profile_white);
                break;
            }
            case R.id.profile_img: {
                mFragment = new FourMainFrg();
                actionPublicList.setImageResource(R.drawable.ic_action_list_public_white);
                actionMyList.setImageResource(R.drawable.ic_action_list_my_white);
                actionMsg.setImageResource(R.drawable.ic_action_msg_white);
                actionProfile.setImageResource(R.drawable.ic_action_profile_yellow);
                break;
            }
            default:
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, mFragment);
        fragmentTransaction.commit();
    }

}

