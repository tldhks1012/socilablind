package com.kkkhhh.socialblinddate.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-21.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatList> chatList;
    private Activity activity;
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();


    public ChatListAdapter(List<ChatList> chatList, Activity activity) {
        this.chatList = chatList;
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

        final String partnerID=chatModel.partnerID;
        initReference(partnerID,holder);
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

        public ViewHolder(View itemView) {
            super(itemView);
            profileImg = (ImageView) itemView.findViewById(R.id.chat_list_profile_img);
            local = (TextView) itemView.findViewById(R.id.chat_list_local);
            gender= (TextView) itemView.findViewById(R.id.chat_list_gender);
            age=(TextView) itemView.findViewById(R.id.chat_list_age);
        }
    }


private void initReference(String uID,final RecyclerView.ViewHolder holder ){
    FirebaseDatabase.getInstance().getReference().child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserModel userModel = dataSnapshot.getValue(UserModel.class);
            ((ViewHolder)holder).age.setText(userModel._uAge);
            ((ViewHolder)holder).local.setText(userModel._uLocal);
            ((ViewHolder)holder).gender.setText(userModel._uGender);
     /*       Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference.child(userModel._uImage1)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                    crossFade(1000).into(((ViewHolder) holder).profileImg);*/
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
}

