package com.kkkhhh.socialblinddate.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkhhh.socialblinddate.Model.ChatModel;
import com.kkkhhh.socialblinddate.R;

import java.util.List;

/**
 * Created by Dev1 on 2016-11-18.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatModel> chatList;

    public ChatAdapter(List<ChatModel> chatList){
        this.chatList=chatList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    final ChatModel chatModel=chatList.get(position);
        ((ViewHolder)holder).nickName.setText(chatModel.nickName);
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
}
