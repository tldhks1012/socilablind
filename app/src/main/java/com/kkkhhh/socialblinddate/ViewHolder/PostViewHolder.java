package com.kkkhhh.socialblinddate.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.DetailPostAct;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.TimeMaximum;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.text.ParseException;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-14.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    private ImageView cardUserImg;
    private TextView cardUserGender;
    private TextView cardUserAge;
    private TextView cardUserLocal;
    private TextView cardPostTitle;
    private CardView cardView;
    private TextView cardTimeStamp;

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public PostViewHolder(View itemView) {
        super(itemView);
        cardUserImg = (ImageView) itemView.findViewById(R.id.card_img);
        cardUserGender = (TextView) itemView.findViewById(R.id.card_gender);
        cardUserAge = (TextView) itemView.findViewById(R.id.card_age);
        cardUserLocal = (TextView) itemView.findViewById(R.id.card_local);
        cardPostTitle = (TextView) itemView.findViewById(R.id.card_title);
        cardView = (CardView) itemView.findViewById(R.id.card_view);
        cardTimeStamp = (TextView) itemView.findViewById(R.id.card_timestamp);
    }

    public void bindToPost(final Post post, final Activity activity, final ProgressView progressView, final RecyclerView recyclerView, final RequestManager mGlideRequestManager) {
        if (post.userProfileImg != null) {

            if (post.userProfileImg != null) {
                cardUserGender.setText(post.gender);
                cardUserAge.setText(post.age);
                cardUserLocal.setText(post.local);
                cardPostTitle.setText(post.title);
                mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(post.userProfileImg)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).
                        crossFade(1000).into(cardUserImg);


                String stringDate = post.stampTime;
                String getDate = _nowTime(stringDate);
                cardTimeStamp.setText(getDate);
                cardView.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    private String _nowTime(String stringDate) {
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


