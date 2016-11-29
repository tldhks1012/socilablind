package com.kkkhhh.socialblinddate.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.RequestManager;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;

import android.widget.LinearLayout;
import android.widget.Toast;

import android.widget.ImageButton;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailPostAct extends AppCompatActivity {

    private TextView bodyTv, genderTv, ageTv, localTv, noImgTv;
    private ImageView profileIv, detailImgIv;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot();
    private DatabaseReference postsReference;
    private ImageButton postLike;

    private String gender, postKey, local, detailImgStr;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ProgressView progressView;
    private FrameLayout goToProfile, goToMessage, deleteBtn, updateBtn;
    private LinearLayout noUidMenu, uIDMenu;
    private String postUid;
    public RequestManager mGlideRequestManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        Intent intent = getIntent();
        gender = intent.getStringExtra("gender");
        postKey = intent.getStringExtra("postKey");
        local = intent.getStringExtra("local");
        DataBaseFiltering dataBaseFiltering=new DataBaseFiltering();
        local = dataBaseFiltering.changeLocal(local);
        postsReference=databaseReference.child("posts");
        init();
    }


    public void onStart() {
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
        progressView = (ProgressView) findViewById(R.id.detail_image_progress);
        goToProfile = (FrameLayout) findViewById(R.id.go_to_profile);
        goToMessage = (FrameLayout) findViewById(R.id.go_to_message);
        noUidMenu = (LinearLayout) findViewById(R.id.detail_no_uid_menu);
        uIDMenu = (LinearLayout) findViewById(R.id.detail_uid_menu);
        deleteBtn = (FrameLayout) findViewById(R.id.detail_post_delete_btn);
        updateBtn = (FrameLayout) findViewById(R.id.detail_post_change_btn);
        goToMessage = (FrameLayout) findViewById(R.id.go_to_message);
        postLike=(ImageButton)findViewById(R.id.post_like);
        getData();
        _deletePost(deleteBtn);
        _updatePost(updateBtn);
        sendMessage();
        mGlideRequestManager=Glide.with(getApplicationContext());
    }

    private void getData() {
    setDataReference(postsReference);
    }

    private void setDataReference(final DatabaseReference dataReference) {
        dataReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        if (post.stars.containsKey(getUid())) {
                            postLike.setImageResource(R.drawable.ic_action_like_pull_white);
                        } else {
                            postLike.setImageResource(R.drawable.ic_action_like_white);
                        }


                        postLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference globalPosts = databaseReference.child("posts").child(postKey);
                                if (gender.equals("여자")) {
                                    String gender = "woman";
                                    DatabaseReference genderPosts = databaseReference.child("posts-local").child(gender).child(local).child(postKey);
                                    onLike(genderPosts);
                                } else if (gender.equals("남자")) {
                                    String gender = "man";
                                    DatabaseReference genderPosts = databaseReference.child("posts-local").child(gender).child(local).child(postKey);
                                    onLike(genderPosts);
                                }
                                DatabaseReference userPosts = databaseReference.child("user-posts").child(getUid()).child(postKey);
                                onLike(globalPosts);
                                onLike(userPosts);
                            }
                        });

                        postUid = post.uid;
                        if (postUid.equals(getUid())) {
                            noUidMenu.setVisibility(View.GONE);
                            uIDMenu.setVisibility(View.VISIBLE);
                        } else {
                            noUidMenu.setVisibility(View.VISIBLE);
                            uIDMenu.setVisibility(View.GONE);
                        }

                        profileIv.post(new Runnable() {
                            @Override
                            public void run() {
                                mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                                        crossFade(1000).into(profileIv);
                            }
                        });


                        bodyTv.setText(post.body);
                        bodyTv.setMovementMethod(new ScrollingMovementMethod());
                        ageTv.setText(post.age);
                        genderTv.setText(post.gender);
                        localTv.setText(post.local);
                        detailImgStr = post.img1;


                        if (post.img1.equals("@null")) {
                            noImgTv.setVisibility(View.VISIBLE);
                            progressView.setVisibility(View.GONE);
                        } else {
                            detailImgIv.post(new Runnable() {
                                @Override
                                public void run() {
                                    mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(post.img1)).fitCenter().listener(new RequestListener<StorageReference, GlideDrawable>() {
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
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DataError", databaseError.getMessage().toString());
            }
        });
    }
    //좋아요 구현
    private void onLike(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
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

    //수정 구현
    private void _updatePost(FrameLayout frameLayout) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPostAct.this, PostWriterAct.class);
                intent.putExtra("gender", gender);
                intent.putExtra("local", local);
                intent.putExtra("postKey", postKey);
                startActivity(intent);
            }
        });
    }

    //삭제 구현
    private void _deletePost(FrameLayout frameLayout) {
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
                            deletePost("woman", local);
                        } else if (gender.equals("남자")) {
                            deletePost("man", local);
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
        DataBaseFiltering dataBaseFiltering = new DataBaseFiltering();
        local = dataBaseFiltering.changeLocal(local);
        databaseReference.child("posts").child(postKey).removeValue();
        databaseReference.child("posts-local").child(gender).child(local).child(postKey).removeValue();
        databaseReference.child("user-posts").child(firebaseAuth.getCurrentUser().getUid()).child(postKey).removeValue();
        if (!detailImgStr.equals("@null")) {
            storageReference.child(detailImgStr).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
        finish();
    }

    private void sendMessage() {
        goToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendMessage();
            }
        });
    }


    //메세지 보낼때  send 메세지창
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

                        if (uCoin > 300) {
                            databaseReference.child("user-chatList").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        for (DataSnapshot chatListSnapShot : dataSnapshot.getChildren()) {
                                            ChatList chatListValue = chatListSnapShot.getValue(ChatList.class);
                                            if (chatListValue.partnerID.equals(postUid)) {
                                                Toast.makeText(getApplicationContext(), "개설된 채팅방이 있습니다", Toast.LENGTH_SHORT).show();
                                            } else {
                                                messageUpload(userID, editor, uCoin);
                                            }
                                        }
                                    } else {
                                        messageUpload(userID, editor, uCoin);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("databaseError", databaseError.getMessage());
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "코인이 부족합니다", Toast.LENGTH_SHORT).show();
                        }
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
                    Intent intent = new Intent(DetailPostAct.this, ChatAct.class);
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


