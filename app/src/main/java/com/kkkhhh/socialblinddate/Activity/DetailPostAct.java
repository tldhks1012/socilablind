package com.kkkhhh.socialblinddate.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;

import android.widget.LinearLayout;
import com.rey.material.widget.ProgressView;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailPostAct extends AppCompatActivity {

    private TextView bodyTv, genderTv, ageTv, localTv, noImgTv;
    private ImageView profileIv, detailImgIv;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = firebaseDatabase.getReference().getRoot();
    private DatabaseReference manReference;
    private DatabaseReference womanReference;
    private String gender, postKey, local ,detailImgStr;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ProgressView progressView;
    private FrameLayout goToProfile,goToMessage,deleteBtn,updateBtn;
    private LinearLayout noUidMenu,uIDMenu;
    private String postUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        Intent intent = getIntent();
        gender = intent.getStringExtra("gender");
        postKey = intent.getStringExtra("postKey");
        local=intent.getStringExtra("local");
        DataBaseFiltering dataBaseFiltering=new DataBaseFiltering();
        local=dataBaseFiltering.changeLocal(local);
        manReference = databaseRef.child("/posts/man-posts/");
        womanReference = databaseRef.child("/posts/woman-posts/");
        init();
    }


    public void onStart(){
        super.onStart();
        progressView.setVisibility(View.VISIBLE);
        init();
    }

    private void init() {
        bodyTv = (TextView) findViewById(R.id.detail_post_body);
        genderTv = (TextView) findViewById(R.id.detail_profile_gender);
        ageTv = (TextView) findViewById(R.id.detail_profile_age);
        localTv = (TextView) findViewById(R.id.detail_profile_local);
        profileIv = (ImageView) findViewById(R.id.detail_profile_img);
        detailImgIv = (ImageView) findViewById(R.id.detail_post_img);
        noImgTv = (TextView) findViewById(R.id.detail_no_img);
        progressView=(ProgressView)findViewById(R.id.detail_image_progress);
        goToProfile=(FrameLayout)findViewById(R.id.go_to_profile);
        goToMessage=(FrameLayout)findViewById(R.id.go_to_message);
        noUidMenu = (LinearLayout)findViewById(R.id.detail_no_uid_menu);
        uIDMenu=(LinearLayout)findViewById(R.id.detail_uid_menu);
        deleteBtn=(FrameLayout)findViewById(R.id.detail_post_delete_btn);
        updateBtn=(FrameLayout)findViewById(R.id.detail_post_change_btn);
        goToMessage=(FrameLayout)findViewById(R.id.go_to_message);
        getData();
        _deletePost(deleteBtn);
        _updatePost(updateBtn);
        sendMessage();
    }

    private void getData() {
        if (gender.equals("여자")) {
            setDataReference(womanReference);
        } else if (gender.equals("남자")) {
            setDataReference(manReference);
        }
    }

    private void setDataReference(DatabaseReference dataReference){
        dataReference.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Post post = dataSnapshot.getValue(Post.class);
                    String userID=firebaseAuth.getCurrentUser().getUid().toString();

                    postUid=post.uid;
                    if(postUid.equals(userID)){
                        noUidMenu.setVisibility(View.GONE);
                        uIDMenu.setVisibility(View.VISIBLE);
                    }else{
                        noUidMenu.setVisibility(View.VISIBLE);
                        uIDMenu.setVisibility(View.GONE);
                    }

                    Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                            crossFade(1000).into(profileIv);

                    bodyTv.setText(post.body);
                    bodyTv.setMovementMethod(new ScrollingMovementMethod());
                    ageTv.setText(post.age);
                    genderTv.setText(post.gender);
                    localTv.setText(post.local);
                    detailImgStr=post.img1;

                    if (post.img1.equals("@null")) {
                        noImgTv.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    } else {
                        Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.img1)).fitCenter().listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressView.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressView.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(detailImgIv);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DataError", databaseError.getMessage().toString());
            }
        });
    }


    private void _updatePost(FrameLayout frameLayout){
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPostAct.this,PostWriterAct.class);
                intent.putExtra("gender",gender);
                intent.putExtra("local",local);
                intent.putExtra("postKey",postKey);
                startActivity(intent);
            }
        });
    }


    private void _deletePost(FrameLayout frameLayout){
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogYesOrNo();
            }
        });

    }

    private void dialogYesOrNo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set the title of the Alert Dialog
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("정말 삭제하시겠습니까 ?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        if (gender.equals("여자")) {
                            deletePost("woman",local);
                        } else if (gender.equals("남자")) {
                            deletePost("man",local);
                        }
                        DetailPostAct.this.finish();
                        dialog.cancel();
                    }
                })

                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deletePost(String gender, String local) {
        databaseRef.child("posts").child(gender + "-posts").child(postKey).removeValue();
        databaseRef.child("user-posts").child(firebaseAuth.getCurrentUser().getUid()).child(postKey).removeValue();
        if (!detailImgStr.equals("@null")) {
            storageReference.child(detailImgStr).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
        finish();
    }

    private void sendMessage(){
        goToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailPostAct.this,ChatAct.class);
                String chatKey=databaseRef.child("message").push().getKey();
                putChatList(chatKey);
                intent.putExtra("chatKey",chatKey);
                startActivity(intent);
            }
        });
    }

    private void putChatList(String chatKey){
        String userID=firebaseAuth.getCurrentUser().getUid();
        ChatList chatList=new ChatList(chatKey,postUid,userID);
       databaseRef.child("message").child(chatKey).setValue(chatList);

        databaseRef.child("users").child(userID).child("chatList").setValue(chatKey);
        databaseRef.child("users").child(postUid).child("chatList").setValue(chatKey);
    }
}


