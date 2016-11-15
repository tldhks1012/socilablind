package com.kkkhhh.socialblinddate.ViewHolder;

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
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-14.
 */

public class PostViewHolder extends RecyclerView.ViewHolder{
    private ImageView cardUserImg;
    private TextView cardUserGender;
    private TextView cardUserAge;
    private TextView cardUserLocal;
    private TextView cardPostTitle;
    private CardView cardview;
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    public PostViewHolder(View itemView) {
        super(itemView);
        cardUserImg =(ImageView)itemView.findViewById(R.id.card_img);
        cardUserGender=(TextView)itemView.findViewById(R.id.card_gender);
        cardUserAge=(TextView)itemView.findViewById(R.id.card_age);
        cardUserLocal=(TextView)itemView.findViewById(R.id.card_local);
        cardPostTitle=(TextView)itemView.findViewById(R.id.card_title);
        cardview=(CardView)itemView.findViewById(R.id.card_view);
        cardview.setVisibility(View.INVISIBLE);
    }
    public void bindToPost(Post post, final ProgressView progressView, final RecyclerView recyclerView ,final RequestManager mGlideRequestManager) {
        if(post.userProfileImg!=null) {
            cardUserGender.setText(post.gender);
            cardUserAge.setText(post.age);
            cardUserLocal.setText(post.local);
            cardPostTitle.setText(post.title);
            storageReference.child(post.userProfileImg).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mGlideRequestManager.load(uri).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).crossFade().into(cardUserImg);
                    progressView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    cardview.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
