package com.kkkhhh.socialblinddate.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.DetailPostAct;
import com.kkkhhh.socialblinddate.Activity.ProfileActivity;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.TimeMaximum;
import com.kkkhhh.socialblinddate.Model.LikeModel;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-30.
 */

public class LikeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LikeModel> likeList;
    private Activity activity;
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private RequestManager mGlideRequestManager;

    public LikeAdapter( List<LikeModel> likeList,Activity activity,RequestManager mGlideRequestManager){
        this.likeList = likeList;
        this.activity=activity;
        this.mGlideRequestManager=mGlideRequestManager;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new LikeAdapter.LikeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_like, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final LikeModel like = likeList.get(position);
        if (like.otherAuth != null) {
            databaseReference.child("users").child(like.otherAuth).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        ((LikeHolder) holder).cardText.setText(userModel._uNickname +"님이 하트를 보냈습니다.");
                        mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(userModel._uImage1)).placeholder(R.drawable.ic_action_like_white)
                                .signature(new StringSignature(userModel.updateStamp)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(((LikeAdapter.LikeHolder) holder).cardUserImg);

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            String stringDate = like.stampTime;
            String getDate = _nowTime(stringDate);
            ((LikeAdapter.LikeHolder) holder).cardTimeStamp.setText(getDate);
            ((LikeAdapter.LikeHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("postUid",like.otherAuth);
                    activity.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public static class LikeHolder extends RecyclerView.ViewHolder{
        private ImageView cardUserImg;

        private TextView cardText;
        private CardView cardView;
        private TextView cardTimeStamp;

        public LikeHolder(View itemView) {
            super(itemView);
            cardUserImg =(ImageView)itemView.findViewById(R.id.card_img);
            cardText=(TextView)itemView.findViewById(R.id.card_txt);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            cardTimeStamp=(TextView)itemView.findViewById(R.id.card_timestamp);
        }
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
