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
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;

import android.widget.LinearLayout;
import com.rey.material.widget.ProgressView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailPostAct extends AppCompatActivity {

    private TextView detailBody, detailProfileGender, detailProfileAge, detailProfileLocal, detailNoImg;
    private ImageView detailProfileImg, detailImg;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = firebaseDatabase.getReference().getRoot();
    private DatabaseReference man_Ref;
    private DatabaseReference women_Ref;
    private String gender, postKey, local ,detailImgStr;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ProgressView progressView;
    private FrameLayout goToProfile,goToMessage,deletePostBtn,updatePostBtn;
    private LinearLayout noUidMenu,uIDMenu;

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
        man_Ref = dbRef.child("/posts/man-posts/");
        women_Ref = dbRef.child("/posts/women-posts/");
        init();
    }

    private void init() {
        detailBody = (TextView) findViewById(R.id.detail_post_body);
        detailProfileGender = (TextView) findViewById(R.id.detail_profile_gender);
        detailProfileAge = (TextView) findViewById(R.id.detail_profile_age);
        detailProfileLocal = (TextView) findViewById(R.id.detail_profile_local);
        detailProfileImg = (ImageView) findViewById(R.id.detail_profile_img);
        detailImg = (ImageView) findViewById(R.id.detail_post_img);
        detailNoImg = (TextView) findViewById(R.id.detail_no_img);
        progressView=(ProgressView)findViewById(R.id.detail_image_progress);
        goToProfile=(FrameLayout)findViewById(R.id.go_to_profile);
        goToMessage=(FrameLayout)findViewById(R.id.go_to_message);
        noUidMenu = (LinearLayout)findViewById(R.id.detail_no_uid_menu);
        uIDMenu=(LinearLayout)findViewById(R.id.detail_uid_menu);
        deletePostBtn=(FrameLayout)findViewById(R.id.detail_post_delete_btn);
        updatePostBtn=(FrameLayout)findViewById(R.id.detail_post_change_btn);
        getData();
        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogYesOrNo();
            }
        });

        updatePostBtn.setOnClickListener(new View.OnClickListener() {
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

    private void getData() {
        if (gender.equals("여자")) {
            setDataReference(women_Ref);
        } else if (gender.equals("남자")) {
            setDataReference(man_Ref);
        }
    }

    private void deletePost(String gender, String local) {
        dbRef.child("posts").child(gender + "-posts").child(postKey).removeValue();
        dbRef.child("posts").child(gender).child(local).child(postKey).removeValue();
        dbRef.child("user-posts").child(firebaseAuth.getCurrentUser().getUid()).child(postKey).removeValue();
        if (!detailImgStr.equals("@null")) {
            storageReference.child(detailImgStr).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
        finish();
    }



    private void setDataReference(DatabaseReference dataReference){
        dataReference.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Post post = dataSnapshot.getValue(Post.class);
                    String userID=firebaseAuth.getCurrentUser().getUid().toString();
                    if(post.uid.equals(userID)){
                        noUidMenu.setVisibility(View.GONE);
                        uIDMenu.setVisibility(View.VISIBLE);
                    }else{
                        noUidMenu.setVisibility(View.VISIBLE);
                        uIDMenu.setVisibility(View.GONE);
                    }

                    Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                            crossFade(1000).into(detailProfileImg);
                    detailBody.setText(post.body);
                    detailBody.setMovementMethod(new ScrollingMovementMethod());
                    detailProfileAge.setText(post.age);
                    detailProfileGender.setText(post.gender);
                    detailProfileLocal.setText(post.local);
                    detailImgStr=post.img1;
                    if (post.img1.equals("@null")) {
                        detailNoImg.setVisibility(View.VISIBLE);
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
                        }).into(detailImg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DataError", databaseError.getMessage().toString());
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
                            deletePost("women",local);
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
}


