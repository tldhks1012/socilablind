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


import com.bumptech.glide.RequestManager;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;


import com.rey.material.widget.ProgressView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-09.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Post> postList;
//    public static final int ITEM_TYPE_HEADER = 0;
//    public static final int ITEM_TYPE_CONTENT = 1;
//    public static final int ITEM_TYPE_BOTTOM = 2;
//    private int mHeaderCount=1;
//    private int mBottomCount=1;




    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private Activity activity;
    private RequestManager mGlideRequestManager;
    private ProgressView progressView;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();



///생성자 포스트리스트, 엑티비티, 데이터베이스 레퍼런스, 프로그래스뷰, 리사이클뷰
    public PostAdapter(List<Post> postList, Activity activity, RequestManager mGlideRequestManager,ProgressView progressView,RecyclerView recyclerView) {
        this.postList = postList;
        this.activity=activity;
        this.mGlideRequestManager=mGlideRequestManager;
        this.progressView=progressView;
        this.recyclerView=recyclerView;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType ==ITEM_TYPE_HEADER) {
//            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_header, parent, false));
//        } else if (viewType == ITEM_TYPE_CONTENT) {
            return  new PostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false));
//        } else if (viewType == ITEM_TYPE_BOTTOM) {
//            return new BottomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_footer, parent, false));
//        }
//        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof HeaderViewHolder) {
//
//        } else if (holder instanceof PostHolder) {
        final Post post = postList.get(position);
        if (post.userProfileImg != null) {
            ((PostHolder) holder).cardUserGender.setText(post.gender);
            ((PostHolder) holder).cardUserAge.setText(post.age);
            ((PostHolder) holder).cardUserLocal.setText(post.local);
            ((PostHolder) holder).cardPostTitle.setText(post.title);


            databaseReference.child("users").child(post.uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        ((PostHolder) holder).cardNickname.setText(userModel._uNickname);

                        mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(userModel._uImage1))
                                .signature(new StringSignature(userModel.updateStamp))
                                .placeholder(R.drawable.ic_action_like_white)
                                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        progressView.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressView.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        return false;
                                    }
                                }).into(((PostHolder) holder).cardUserImg);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ((PostHolder) holder).cardUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("postUid", post.uid);
                    activity.startActivity(intent);
                }
            });


            String stringDate = post.stampTime;
            String getDate = _nowTime(stringDate);
            ((PostHolder) holder).cardTimeStamp.setText(getDate);
            ((PostHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DetailPostAct.class);
                    intent.putExtra("gender", post.gender);
                    intent.putExtra("postKey", post.postKey);
                    intent.putExtra("local", post.local);
                    activity.startActivity(intent);

                }
            });
        }
//        }else if (holder instanceof BottomViewHolder) {
//
//        }
    }



    public int getContentItemCount(){
        return postList.size();
    }
    @Override
    public int getItemCount() {

        return /*mHeaderCount +*/ getContentItemCount()/* + mBottomCount*/;
    }

   /* @Override
    public int getItemViewType(int position) {
        int dataItemCount = getContentItemCount();
        if (mHeaderCount != 0 && position < mHeaderCount) {
            return ITEM_TYPE_HEADER;
        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {
            return ITEM_TYPE_BOTTOM;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }*/

    ////컨텐츠 뷰
/*    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);

        }
    }
    public static class BottomViewHolder extends RecyclerView.ViewHolder {
        private TextView plusData;
        public BottomViewHolder(View itemView) {
            super(itemView);

        }
    }*/

    public static class PostHolder extends RecyclerView.ViewHolder{
        private ImageView cardUserImg;

        private TextView cardUserGender;
        private TextView cardUserAge;
        private TextView cardUserLocal;
        private TextView cardPostTitle;
        private CardView cardView;
        private TextView cardTimeStamp;
        private TextView cardNickname;


        public PostHolder(View itemView) {
            super(itemView);
            cardUserImg =(ImageView)itemView.findViewById(R.id.card_img);
            cardUserGender=(TextView)itemView.findViewById(R.id.card_gender);
            cardUserAge=(TextView)itemView.findViewById(R.id.card_age);
            cardUserLocal=(TextView)itemView.findViewById(R.id.card_local);
            cardPostTitle=(TextView)itemView.findViewById(R.id.card_title);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            cardTimeStamp=(TextView)itemView.findViewById(R.id.card_timestamp);
            cardNickname=(TextView)itemView.findViewById(R.id.card_nickname);



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
