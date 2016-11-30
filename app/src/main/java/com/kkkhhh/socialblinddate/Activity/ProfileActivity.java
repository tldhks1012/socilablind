package com.kkkhhh.socialblinddate.Activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Model.LikeModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImg,img1,img2,img3,img4,img5,img6;
    private TextView nickname,local,gender,age,likeCount;
    private String postUid;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private RequestManager mGlideRequestManager;
    private ProgressView progressView;
    private ImageView postLike;

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        postUid=getIntent().getStringExtra("postUid");
        if(postUid!=null) {
            init();
        }
    }

    private void init(){
        profileImg=(ImageView)findViewById(R.id.profile_img);
        img1=(ImageView)findViewById(R.id.sign_img1);
        img2=(ImageView)findViewById(R.id.sign_img2);
        img3=(ImageView)findViewById(R.id.sign_img3);
        img4=(ImageView)findViewById(R.id.sign_img4);
        img5=(ImageView)findViewById(R.id.sign_img5);
        img6=(ImageView)findViewById(R.id.sign_img6);

        nickname=(TextView)findViewById(R.id.nickname);
        local=(TextView)findViewById(R.id.local);
        gender=(TextView)findViewById(R.id.gender);
        age=(TextView)findViewById(R.id.age);
        likeCount=(TextView)findViewById(R.id.likeCount);

        progressView=(ProgressView)findViewById(R.id.progressview);
        mGlideRequestManager= Glide.with(getApplicationContext());

        postLike=(ImageView)findViewById(R.id.post_like);
        _reference();

    }
    private void _reference(){
        databaseReference.child("users").child(postUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    nickname.setText(userModel._uNickname);
                    local.setText(userModel._uLocal);
                    gender.setText(userModel._uGender);
                    age.setText(userModel._uAge);

                    if(userModel.starCount>=0) {
                        likeCount.setText(String.valueOf(userModel.starCount));
                    }
                    if(!userModel._uImage1.equals("@null")){
                        profileImg.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                                                crossFade(1000).listener(new RequestListener<Uri, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                progressView.setVisibility(View.INVISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                progressView.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        }).into(profileImg);
                                    }
                                });


                            }
                        });
                        img1.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img1);
                                    }
                                });

                            }
                        });
                    }
                    if(!userModel._uImage2.equals("@null")){
                        img2.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage2).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img2);
                                    }
                                });
                            }
                        });

                    }
                    if(!userModel._uImage3.equals("@null")){
                        img3.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage3).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img3);
                                    }
                                });
                            }
                        });

                    }
                    if(!userModel._uImage4.equals("@null")){
                        img4.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage4).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img4);
                                    }
                                });
                            }
                        });

                    }
                    if(!userModel._uImage5.equals("@null")){
                        img5.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage5).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img5);
                                    }
                                });
                            }
                        });

                    }
                    if(!userModel._uImage6.equals("@null")){
                        img6.post(new Runnable() {
                            @Override
                            public void run() {
                                storageReference.child(userModel._uImage6).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mGlideRequestManager.load(uri).crossFade(1000).centerCrop().into(img6);
                                    }
                                });
                            }
                        });
                    }
                    if (userModel.stars.containsKey(getUid())) {
                        postLike.setImageResource(R.drawable.ic_action_like_pull_white);
                    } else {
                        postLike.setImageResource(R.drawable.ic_action_like_white);
                    }


                    postLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference userLike = databaseReference.child("users").child(userModel._uID);
                            onLike(userLike);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //좋아요 구현
    private void onLike(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserModel userModel = mutableData.getValue(UserModel.class);
                if (userModel == null) {
                    return Transaction.success(mutableData);
                }

                if (userModel.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    userModel.starCount = userModel.starCount - 1;
                    userModel.stars.remove(getUid());
                    databaseReference.child("like").child(postUid).child(getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"하트를 취소했습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Star the post and add self to stars
                    userModel.starCount = userModel.starCount + 1;
                    userModel.stars.put(getUid(), true);
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stampTime = CurDateFormat.format(date);
                    long yetNow = -1 * new Date().getTime();
                    LikeModel likeModel =new LikeModel(getUid(),stampTime,yetNow);
                    databaseReference.child("like").child(postUid).child(getUid()).setValue(likeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"하트를 보냈습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Set value and report transaction success
                mutableData.setValue(userModel);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    private String getUid(){
        return firebaseAuth.getCurrentUser().getUid().toString();
    }
}
