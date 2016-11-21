package com.kkkhhh.socialblinddate.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.R;

import java.util.List;

/**
 * Created by Dev1 on 2016-11-18.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatModel> chatList;
    public static final int SENDER = 0;
    public static final int RECEIVER = 1;


    public ChatAdapter(List<ChatModel> chatList){
        this.chatList=chatList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_recieve, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_send, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    final ChatModel chatModel=chatList.get(position);
        ((ViewHolder)holder).chatBody.setText(chatModel.body);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nickName;
        private TextView chatBody;

        public ViewHolder(View itemView) {
            super(itemView);
            nickName=(TextView)itemView.findViewById(R.id.chat_nickname);
            chatBody=(TextView)itemView.findViewById(R.id.chat_body);
        }
    }
    @Override

    public int getItemViewType(int position) {

        ChatModel chatModel = chatList.get(position);
        if (chatModel.uID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return SENDER;
        } else {
            return RECEIVER;
        }
    }

}
