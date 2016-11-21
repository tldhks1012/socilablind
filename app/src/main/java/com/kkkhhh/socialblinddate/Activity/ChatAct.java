package com.kkkhhh.socialblinddate.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkkhhh.socialblinddate.Adapter.ChatAdapter;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAct extends AppCompatActivity {
    private TextView nameTv;
    private EditText chatEd;
    private FrameLayout sendBtn;
    private String partnerID,chatKey,chatStr,uID,nickName;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private List<ChatModel> chatModelList =new ArrayList<ChatModel>();
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        _init();
    }

    private void _init(){
        nameTv=(TextView)findViewById(R.id.chat_name);
        chatEd=(EditText)findViewById(R.id.chat_edit);
        sendBtn=(FrameLayout)findViewById(R.id.chat_send);
        uID=firebaseAuth.getCurrentUser().getUid();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        _initIntent();
        sendMessage();
        receiveMessage();
    }

    private void _initIntent(){
        chatKey=getIntent().getStringExtra("chatKey");
    }
    private void sendMessage(){

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatStr=chatEd.getText().toString();
                if(TextUtils.isEmpty(chatStr)){
                    Toast.makeText(getApplicationContext(),"채팅을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strCurDate = CurDateFormat.format(date);

                    ChatModel chatModel = new ChatModel(uID,chatStr,nickName,strCurDate);
                    databaseReference.child("message").child(chatKey).child("chat").push().setValue(chatModel);
                }
            }
        });
    }
    private void receiveMessage(){

        databaseReference.child("message").child(chatKey).child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatModelList.clear();
                for(DataSnapshot chatModelValue : dataSnapshot.getChildren()){
                    ChatModel chatModel =chatModelValue.getValue(ChatModel.class);
                    chatModelList.add(chatModel);
                }
                if(chatModelList.size()>0){
                    chatAdapter=new ChatAdapter(chatModelList);
                    recyclerView.setAdapter(chatAdapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
