package com.kkkhhh.socialblinddate.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.ChatAct;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.TimeMaximum;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-21.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatList> chatList;
    private Activity activity;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ProgressView progressView;
    private RecyclerView recyclerView;


    public ChatListAdapter(List<ChatList> chatList, Activity activity, ProgressView progressView, RecyclerView recyclerView) {
        this.chatList = chatList;
        this.activity = activity;
        this.progressView = progressView;
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ChatList chatModel = chatList.get(position);

        final String partnerID = chatModel.partnerID;
        initReference(partnerID, holder, chatModel.chatKey);
        lastChatBody(holder,chatModel.chatKey);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImg;
        private TextView local;
        private TextView gender;
        private TextView age;
        private CardView chatView;
        private TextView lastChat;
        private TextView timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImg = (ImageView) itemView.findViewById(R.id.chat_list_profile_img);
            local = (TextView) itemView.findViewById(R.id.chat_list_local);
            gender = (TextView) itemView.findViewById(R.id.chat_list_gender);
            age = (TextView) itemView.findViewById(R.id.chat_list_age);
            chatView = (CardView) itemView.findViewById(R.id.chat_list_view);
            lastChat=(TextView)itemView.findViewById(R.id.chat_list_last_body);
            timeStamp=(TextView)itemView.findViewById(R.id.chat_list_timestamp);

        }
    }

    private void lastChatBody(final RecyclerView.ViewHolder holder, String chatKey) {
        FirebaseDatabase.getInstance().getReference().child("message").child(chatKey).child("chat").limitToLast(1).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                String chatBody = chatModel.body;
                String chatTimeStamp = _nowTime(chatModel.timeStamp);
                ((ViewHolder) holder).lastChat.setText(chatBody);
                ((ViewHolder) holder).timeStamp.setText(chatTimeStamp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initReference(String uID, final RecyclerView.ViewHolder holder, final String chatKey) {
        FirebaseDatabase.getInstance().getReference().child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                ((ViewHolder) holder).age.setText(userModel._uAge);
                ((ViewHolder) holder).local.setText(userModel._uLocal);
                ((ViewHolder) holder).gender.setText(userModel._uGender);
                Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference.child(userModel._uImage1)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                        crossFade(1000).listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (progressView.getVisibility() == View.VISIBLE) {
                            progressView.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (progressView.getVisibility() == View.VISIBLE) {
                            progressView.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                }).into(((ViewHolder) holder).profileImg);
                ((ViewHolder) holder).chatView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, ChatAct.class);
                        intent.putExtra("chatKey", chatKey);
                        activity.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String _nowTime(String stringDate){
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        try {
            Date date = format.parse(stringDate);
            TimeMaximum maximum = new TimeMaximum();
            stringDate = maximum.formatTimeString(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDate;
    }
}

