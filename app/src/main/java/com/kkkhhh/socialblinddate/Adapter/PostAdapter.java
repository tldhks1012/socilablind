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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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


    private List<Post> postList;
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    public static final int ITEM_TYPE_BOTTOM = 2;
    private int mHeaderCount=1;
    private int mBottomCount=1;
    private LayoutInflater mLayoutInflater;

    private int lastPosition;

    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private Activity activity;
    private DatabaseReference ref;
    private ProgressView progressView;
    private RecyclerView recyclerView;

///생성자 포스트리스트, 엑티비티, 데이터베이스 레퍼런스, 프로그래스뷰, 리사이클뷰
    public PostAdapter(List<Post> postList, Activity activity, DatabaseReference ref,ProgressView progressView,RecyclerView recyclerView,int lastPosition) {
        this.postList = postList;
        this.activity=activity;
        this.ref=ref;
        this.progressView=progressView;
        this.recyclerView=recyclerView;
        mLayoutInflater=LayoutInflater.from(activity);
        this.lastPosition=lastPosition;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType ==ITEM_TYPE_HEADER) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.card_post_header, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT) {
            return  new PostHolder(mLayoutInflater.inflate(R.layout.card_post, parent, false));
        } else if (viewType == ITEM_TYPE_BOTTOM) {
            return new BottomViewHolder(mLayoutInflater.inflate(R.layout.card_post_footer, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof PostHolder) {
            final Post post = postList.get(position-mHeaderCount);
            if (post.userProfileImg != null) {
                ((PostHolder) holder).cardUserGender.setText(post.gender);
                ((PostHolder) holder).cardUserAge.setText(post.age);
                ((PostHolder) holder).cardUserLocal.setText(post.local);
                ((PostHolder) holder).cardPostTitle.setText(post.title);
                Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                        crossFade(1000).into(((PostHolder) holder).cardUserImg);
                progressView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

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
        }else if (holder instanceof BottomViewHolder) {
            if (recyclerView.getVisibility() == View.VISIBLE) {
                ((BottomViewHolder) holder).plusData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ref.startAt(lastPosition).endAt(3).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Post post = postSnapshot.getValue(Post.class);
                                    postList.add(post);
                                    lastPosition++;
                                }
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }
    }



    public int getContentItemCount(){
        return postList.size();
    }
    @Override
    public int getItemCount() {

        return mHeaderCount + getContentItemCount() + mBottomCount;
    }

    @Override
    public int getItemViewType(int position) {
        int dataItemCount = getContentItemCount();
        if (mHeaderCount != 0 && position < mHeaderCount) {
            return ITEM_TYPE_HEADER;
        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {
            return ITEM_TYPE_BOTTOM;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    ////컨텐츠 뷰
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);

        }
    }
    public static class BottomViewHolder extends RecyclerView.ViewHolder {
        private TextView plusData;
        public BottomViewHolder(View itemView) {
            super(itemView);
            plusData=(TextView)itemView.findViewById(R.id.plus_data);
        }
    }

    public static class PostHolder extends RecyclerView.ViewHolder{
        private ImageView cardUserImg;
        private TextView cardUserGender;
        private TextView cardUserAge;
        private TextView cardUserLocal;
        private TextView cardPostTitle;
        private CardView cardView;
        private TextView cardTimeStamp;

        public PostHolder(View itemView) {
            super(itemView);
            cardUserImg =(ImageView)itemView.findViewById(R.id.card_img);
            cardUserGender=(TextView)itemView.findViewById(R.id.card_gender);
            cardUserAge=(TextView)itemView.findViewById(R.id.card_age);
            cardUserLocal=(TextView)itemView.findViewById(R.id.card_local);
            cardPostTitle=(TextView)itemView.findViewById(R.id.card_title);
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
