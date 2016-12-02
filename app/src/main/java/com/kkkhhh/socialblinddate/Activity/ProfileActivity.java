package com.kkkhhh.socialblinddate.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kkkhhh.socialblinddate.Adapter.ProfilePostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.LikeModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import android.widget.ImageButton;
import com.rey.material.widget.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {
  /*  private ImageView profileImg;
    private TextView nickname,local,likeCount;*/
    private String postUid;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private RequestManager mGlideRequestManager;
    private ProgressView progressView;
    private ImageButton profileLike,profileAlbum,profileMsg,profileReport;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private TextView noPost;
    private int index = 0;
    private int lastPosition = 10;
    private static int current_page = 1;
    private ProfilePostAdapter mAdapter;
    private List<Post> postList;
    private LinearLayout profileMenu;
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
 /*       profileImg=(ImageView)findViewById(R.id.profile_img);
        nickname=(TextView)findViewById(R.id.nickname);
        local=(TextView)findViewById(R.id.local);
        likeCount=(TextView)findViewById(R.id.likeCount);*/
        noPost=(TextView)findViewById(R.id.no_post);
        progressView=(ProgressView)findViewById(R.id.progressview);
        mGlideRequestManager= Glide.with(getApplicationContext());
        profileLike=(ImageButton)findViewById(R.id.profile_like);
        profileAlbum=(ImageButton)findViewById(R.id.profile_album);
        profileMsg=(ImageButton)findViewById(R.id.profile_message);

        profileMenu=(LinearLayout)findViewById(R.id.profile_no_uid_menu);

        if(postUid.equals(getUid())){
            profileMenu.setVisibility(View.INVISIBLE);
        }
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(ProfileActivity.this);
        mManager.setReverseLayout(false);
        mManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(mManager);
        postList = new ArrayList<Post>();
        _initReference(databaseReference.child("user-posts").child(postUid));
        sendMessage(profileMsg);

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
                    mAdapter = new ProfilePostAdapter(postList, ProfileActivity.this, mGlideRequestManager,progressView,recyclerView,profileLike,postUid);

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



    private void sendMessage(ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendMessage();
            }
        });
    }
    private void dialogSendMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set the title of the Alert Dialog
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("상대방과 채팅 할 시 300코인이 소요가 됩니다.\n채팅을 하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        final String userID = firebaseAuth.getCurrentUser().getUid();
                        SharedPreferences preferences = getSharedPreferences(UserValue.SHARED_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        final int uCoin = preferences.getInt(UserValue.USER_COIN, 0);


                        databaseReference.child("user-chatList").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot chatListSnapShot : dataSnapshot.getChildren()) {
                                        ChatList chatListValue = chatListSnapShot.getValue(ChatList.class);
                                        if (chatListValue.partnerID.equals(postUid)) {
                                            Toast.makeText(getApplicationContext(), "개설된 채팅방이 있습니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            noCoinMessage(uCoin,userID,editor);
                                        }
                                    }
                                } else {
                                    noCoinMessage(uCoin,userID,editor);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("databaseError", databaseError.getMessage());
                            }
                        });
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void noCoinMessage(int uCoin, String userID, SharedPreferences.Editor editor){
        if (uCoin > 300) {
            messageUpload(userID, editor, uCoin);
        }else{
            Toast.makeText(getApplicationContext(), "코인이 부족합니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void messageUpload(final String userID, final SharedPreferences.Editor editor, final int uCoin) {
        final String chatKey = databaseReference.child("message").push().getKey();
        ChatList chatListUser = new ChatList(chatKey, postUid, userID);
        ChatList chatListPartner = new ChatList(chatKey, userID, postUid);
        Map<String, Object> chatListUserValues = chatListUser.toMap();
        Map<String, Object> chatListPartnerValues = chatListPartner.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/user-chatList/" + postUid + "/" + chatKey, chatListPartnerValues);
        childUpdates.put("/user-chatList/" + userID + "/" + chatKey, chatListUserValues);
        childUpdates.put("/message/" + chatKey, chatListUserValues);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("dataError", databaseError.toString());
                } else {
                    int userCoin;
                    userCoin = uCoin - 300;
                    editor.putInt(UserValue.USER_COIN, userCoin);
                    editor.commit();
                    databaseReference.child("users").child(userID).child("_uCoin").setValue(userCoin);
                    Intent intent = new Intent(ProfileActivity.this, ChatAct.class);
                    intent.putExtra("chatKey", chatKey);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private String getUid(){
        return firebaseAuth.getCurrentUser().getUid().toString();
    }
}
