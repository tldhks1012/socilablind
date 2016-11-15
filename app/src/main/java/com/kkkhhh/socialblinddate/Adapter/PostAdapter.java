package com.kkkhhh.socialblinddate.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.DetailPostAct;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.TimeMaximum;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;


import com.rey.material.widget.ProgressView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-09.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   /* private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;*/
    private List<Post> postList;

    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private Activity activity;
    private DatabaseReference ref;
    private ProgressView progressView;
    private RecyclerView recyclerView;


    public PostAdapter(List<Post> postList, Activity activity, DatabaseReference ref,ProgressView progressView,RecyclerView recyclerView) {
        this.postList = postList;
        this.activity=activity;
        this.ref=ref;
        this.progressView=progressView;
        this.recyclerView=recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType ==TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_header, parent, false);
//            return new HeaderViewHolder(v);
//        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
            return new PostHolder(v);
//        }
//        /*View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);*/
//        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        if(holder instanceof HeaderViewHolder){
//
//        }
if (holder instanceof PostHolder) {

            final Post post=postList.get(position);
            if(post.userProfileImg!=null) {
                ((PostHolder) holder).cardUserGender.setText(post.gender);
                ((PostHolder) holder).cardUserAge.setText(post.age);
                ((PostHolder) holder).cardUserLocal.setText(post.local);
                ((PostHolder) holder).cardPostTitle.setText(post.title);

               Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                       crossFade(1000).into(((PostHolder) holder).cardUserImg);
                progressView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                String stringDate = post.stampTime;
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = format.parse(stringDate);
                    TimeMaximum maximum = new TimeMaximum();
                    String getDate = maximum.formatTimeString(date);
                    ((PostHolder) holder).cardTimeStamp.setText(getDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((PostHolder) holder).goToPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, DetailPostAct.class);
                        intent.putExtra("gender",post.gender);
                        intent.putExtra("postKey",post.postKey);
                        activity.startActivity(intent);

                    }
                });



            }else{
                /*((ViewHolder) holder).cardUserGender.setText(post.gender);
                ((ViewHolder) holder).cardUserAge.setText(post.age);
                ((ViewHolder) holder).cardUserLocal.setText(post.local);
                ((ViewHolder) holder).cardPostTitle.setText(post.title);*/
            }
        }
    }


//    @Override
//    public int getItemViewType(int position) {
//        if(isPositionHeader(position)) {
//            return TYPE_HEADER;
//        }else {
//            return TYPE_ITEM;
//        }
//    }


//
//    private Post getItem(int position)
//    {
//        return postList.get(position);
//    }


    @Override
    public int getItemCount() {

        return  postList.size();
    }

//    private boolean isPositionHeader(int position)
//    {
//        return position == TYPE_HEADER;
//    }

    ////컨텐츠 뷰
    public static class PostHolder extends RecyclerView.ViewHolder{
        private ImageView cardUserImg;
        private TextView cardUserGender;
        private TextView cardUserAge;
        private TextView cardUserLocal;
        private TextView cardPostTitle;
        private CardView cardView;
        private TextView cardTimeStamp;
        private FrameLayout goToPost;
        private FrameLayout goToProfile;

        public PostHolder(View itemView) {
            super(itemView);
            cardUserImg =(ImageView)itemView.findViewById(R.id.card_img);
            cardUserGender=(TextView)itemView.findViewById(R.id.card_gender);
            cardUserAge=(TextView)itemView.findViewById(R.id.card_age);
            cardUserLocal=(TextView)itemView.findViewById(R.id.card_local);
            cardPostTitle=(TextView)itemView.findViewById(R.id.card_title);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            cardTimeStamp=(TextView)itemView.findViewById(R.id.card_timestamp);
            goToPost=(FrameLayout)itemView.findViewById(R.id.go_to_post);
            goToProfile=(FrameLayout)itemView.findViewById(R.id.go_to_profile);
        }
    }



    ////헤드 뷰
//    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
//
//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//        }
//    }
}
