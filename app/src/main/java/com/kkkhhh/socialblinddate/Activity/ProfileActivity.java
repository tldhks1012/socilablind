package com.kkkhhh.socialblinddate.Activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.signature.StringSignature;
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
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Model.LikeModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImg;
    private TextView nickname,local,gender,age,likeCount;
    private String postUid;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private RequestManager mGlideRequestManager;
    private ProgressView progressView;
    private ImageView postLike;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private TextView noPost;
    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private int index = 0;
    private int lastPosition = 10;
    private static int current_page = 1;
    private PostAdapter mAdapter;
    private List<Post> postList;



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
        nickname=(TextView)findViewById(R.id.nickname);
        local=(TextView)findViewById(R.id.local);
        gender=(TextView)findViewById(R.id.gender);
        age=(TextView)findViewById(R.id.age);
        likeCount=(TextView)findViewById(R.id.likeCount);
        noPost=(TextView)findViewById(R.id.no_post);
        progressView=(ProgressView)findViewById(R.id.progressview);
        mGlideRequestManager= Glide.with(getApplicationContext());
        postLike=(ImageView)findViewById(R.id.post_like);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(ProfileActivity.this);
        mManager.setReverseLayout(false);
        mManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(mManager);
        postList = new ArrayList<Post>();
        _reference();
        _initReference(databaseReference.child("user-posts").child(postUid));

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
                    if(!userModel._uImage1.equals("@null")) {

                        _initProfileImg(userModel._uImage1,userModel.updateStamp);

                    }
                    if(userModel.starCount>=0) {
                        likeCount.setText(String.valueOf(userModel.starCount));
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

    private void _initProfileImg(final String imgPath,final String stamp){
        profileImg.post(new Runnable() {
            @Override
            public void run() {

                mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(imgPath)).signature(new StringSignature(stamp))
                        .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(profileImg);
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
    private void _initReference(final DatabaseReference databaseReference){

        databaseReference.orderByChild("stump").limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    noPost.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else {

                    noPost.setVisibility(View.GONE);
                    //초기에 리스트를 초기화
                    postList.clear();

                    //for문을 돌려 리스트 값만큼 추가
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if(postSnapshot.getValue()!=null) {
                            Post postModel = postSnapshot.getValue(Post.class);
                            postList.add(postModel);
                        }
                    }
                    //PostAdapter 참조
                    mAdapter = new PostAdapter(postList, ProfileActivity.this, mGlideRequestManager,progressView,recyclerView);

                    //RecycleView 어댑터 세팅
                    recyclerView.setAdapter(mAdapter);



                  /*  recyclerView.setVisibility(View.VISIBLE);*/
                    //index 값
                    index = postList.size() - 1;

                    recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager) {
                        @Override
                        public void onLoadMore(int currentPage) {
                            progressView.setVisibility(View.VISIBLE);
                            loadPaging(databaseReference,current_page);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadPaging(DatabaseReference dbRef,int current_page) {


        dbRef.orderByChild("stump").startAt(postList.get(index).stump).limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for문을 돌려 리스트 값만큼 추가
                postList.remove(index);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post postModel = postSnapshot.getValue(Post.class);

                    postList.add(postModel);

                }


                mAdapter.notifyDataSetChanged();
                index = postList.size() - 1;


                //리스트뷰 애니메이션 효과
                progressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String getUid(){
        return firebaseAuth.getCurrentUser().getUid().toString();
    }
}
