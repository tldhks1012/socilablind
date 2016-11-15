package com.kkkhhh.socialblinddate.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailPostAct extends AppCompatActivity {

    private TextView detailBody, detailProfileGender, detailProfileAge, detailProfileLocal, detailNoImg;
    private ImageView detailProfileImg, detailImg;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = firebaseDatabase.getReference().getRoot();
    private DatabaseReference man_Ref;
    private DatabaseReference women_Ref;
    private String gender, postKey;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        Intent intent = getIntent();
        gender = intent.getStringExtra("gender");
        postKey = intent.getStringExtra("postKey");

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
        getData();
    }

    private void getData() {
        if (gender.equals("여자")) {
            women_Ref.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);

                    Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                            crossFade(1000).into(detailProfileImg);

                    detailBody.setText(post.body);
                    detailProfileAge.setText(post.age);
                    detailProfileGender.setText(post.gender);
                    detailProfileLocal.setText(post.local);

                    if (post.img1.equals("@null")) {
                        detailNoImg.setVisibility(View.VISIBLE);
                    } else {
                        Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.img1)).into(detailImg);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (gender.equals("남자")) {
            man_Ref.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);

                    Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                            crossFade(1000).into(detailProfileImg);

                    detailBody.setText(post.body);
                    detailProfileAge.setText(post.age);
                    detailProfileGender.setText(post.gender);
                    detailProfileLocal.setText(post.local);

                    if (post.img1.equals("@null")) {
                        detailNoImg.setVisibility(View.VISIBLE);
                    } else {
                        Glide.with(DetailPostAct.this).using(new FirebaseImageLoader()).load(storageReference.child(post.img1)).into(detailImg);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
