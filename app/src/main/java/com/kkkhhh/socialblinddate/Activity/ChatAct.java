package com.kkkhhh.socialblinddate.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Adapter.ChatAdapter;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import android.widget.ImageButton;
import com.rey.material.widget.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatAct extends AppCompatActivity {
    private TextView nameTv;
    private ImageView profileIv;
    private EditText chatEd;
    private FrameLayout sendBtn;
    private String chatKey, chatStr, uID;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private List<ChatModel> chatModelList = new ArrayList<ChatModel>();
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private ChatAdapter chatAdapter;
    private ProgressView progressView;
    private String partnerID,userID;
    private ImageButton chatRemove;
    private RequestManager mGlideRequestManager;
    private StorageReference storageReference =FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        _init();
    }

    private void _init() {
        nameTv = (TextView) findViewById(R.id.chat_name);
        chatEd = (EditText) findViewById(R.id.chat_edit);
        sendBtn = (FrameLayout) findViewById(R.id.chat_send);
        progressView=(ProgressView)findViewById(R.id.progressview);
        chatRemove=(ImageButton)findViewById(R.id.chat_remove);
        profileIv=(ImageView)findViewById(R.id.chat_profile_img);
        uID = firebaseAuth.getCurrentUser().getUid();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);
        mGlideRequestManager= Glide.with(ChatAct.this);
        _initIntent();
        sendMessage();
        receiveMessage();
        removeChat();
    }

    private void _initIntent() {
        chatKey = getIntent().getStringExtra("chatKey");
        databaseReference.child("message").child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatList chatList = dataSnapshot.getValue(ChatList.class);
                partnerID = chatList.partnerID;
                userID = chatList.uID;
                if (userID.equals(uID)) {
                    userNickName(partnerID);
                } else {
                    userNickName(userID);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatStr = chatEd.getText().toString();
                if (TextUtils.isEmpty(chatStr)) {
                    Toast.makeText(getApplicationContext(), "채팅을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strCurDate = CurDateFormat.format(date);

                    chatEd.setText("");

                    ChatModel chatModel = new ChatModel(uID, chatStr, strCurDate);
                    databaseReference.child("message").child(chatKey).child("chat").push().setValue(chatModel);
                }
            }
        });
    }

    private void receiveMessage() {

        databaseReference.child("message").child(chatKey).child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatModelList.clear();
                for (DataSnapshot chatModelValue : dataSnapshot.getChildren()) {
                    ChatModel chatModel = chatModelValue.getValue(ChatModel.class);
                    chatModelList.add(chatModel);
                }
                if (chatModelList.size() > 0) {
                    chatAdapter = new ChatAdapter(chatModelList);
                    recyclerView.setAdapter(chatAdapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userNickName(String uID) {
        databaseReference.child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(userModel._uImage1)).signature(new StringSignature(userModel.updateStamp)).placeholder(R.drawable.ic_action_like_white)
                        .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                nameTv.setText(userModel._uNickname );
                                progressViewState();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                nameTv.setText(userModel._uNickname );
                                progressViewState();
                                return false;
                            }
                        }).into(profileIv);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void progressViewState(){
        if(progressView.getVisibility()==View.VISIBLE){
            recyclerView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
    }
    private void removeChat(){
        chatRemove.setOnClickListener(new View.OnClickListener() {
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
                .setMessage("삭제하시면 상대방의 대화창도 같이 삭제가 되며 전 대화내용 기록을 볼 수 없습니다 \n동일한 사람과 다시 채팅을 하시려면 코인을 지불해야합니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        deletePost();
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

    private void deletePost() {
        databaseReference.child("message").child(chatKey).removeValue();
        databaseReference.child("user-chatList").child(userID).child(chatKey).removeValue();
        databaseReference.child("user-chatList").child(partnerID).child(chatKey).removeValue().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ChatAct.this.finish();
            }
        });

    }
}
