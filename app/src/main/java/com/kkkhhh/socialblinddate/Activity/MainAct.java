package com.kkkhhh.socialblinddate.Activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kkkhhh.socialblinddate.Fragment.FirstMainFrg;
import com.kkkhhh.socialblinddate.Fragment.FourMainFrg;
import com.kkkhhh.socialblinddate.Fragment.SecondMainFrg;
import com.kkkhhh.socialblinddate.Fragment.ThirdMainFrg;
import com.kkkhhh.socialblinddate.R;

public class MainAct extends AppCompatActivity implements View.OnClickListener{
private Button userLogOutBtn;

    private ImageView actionPublicList,actionMyList,actionMsg,actionProfile;
    private FirebaseAuth mFireAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    Fragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUser();

    }
    private void init(){

        actionPublicList=(ImageView)findViewById(R.id.list_public_img);
        actionMyList=(ImageView)findViewById(R.id.list_my_img);
        actionMsg=(ImageView)findViewById(R.id.msg_img);
        actionProfile=(ImageView)findViewById(R.id.profile_img);

        actionPublicList.setOnClickListener(this);
        actionMyList.setOnClickListener(this);
        actionMsg.setOnClickListener(this);
        actionProfile.setOnClickListener(this);

        mFragment = new FirstMainFrg();

        actionPublicList.setImageResource(R.drawable.ic_action_list_public_yellow);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place,mFragment);
        fragmentTransaction.commit();

    }

    private void selectFlag(View view){

    }

    private void checkUser(){
        if(mFireAuth.getCurrentUser()==null) {
            Intent intent = new Intent(MainAct.this, WelcomeAct.class);
            startActivity(intent);
            finish();
        }
        else {
            init();
        }


    }
    @Override
    public void onClick(View view) {

       switch (view.getId()){
            case R.id.list_public_img : {
                mFragment = new FirstMainFrg();
                actionPublicList.setImageResource(R.drawable.ic_action_list_public_yellow);
                actionMyList.setImageResource(R.drawable.ic_action_list_my_white);
                actionMsg.setImageResource(R.drawable.ic_action_msg_white);
                actionProfile.setImageResource(R.drawable.ic_action_profile_white);
                break;
            }
           case R.id.list_my_img : {
                mFragment = new SecondMainFrg();
               actionPublicList.setImageResource(R.drawable.ic_action_list_public_white);
               actionMyList.setImageResource(R.drawable.ic_action_list_my_yellow);
               actionMsg.setImageResource(R.drawable.ic_action_msg_white);
               actionProfile.setImageResource(R.drawable.ic_action_profile_white);
                break;
            }
           case R.id.msg_img : {
               mFragment = new ThirdMainFrg();
               actionPublicList.setImageResource(R.drawable.ic_action_list_public_white);
               actionMyList.setImageResource(R.drawable.ic_action_list_my_white);
               actionMsg.setImageResource(R.drawable.ic_action_msg_yellow);
               actionProfile.setImageResource(R.drawable.ic_action_profile_white);
               break;
           }
           case R.id.profile_img : {
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
        fragmentTransaction.replace(R.id.fragment_place,mFragment);
        fragmentTransaction.commit();
       }

}

