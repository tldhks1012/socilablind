package com.kkkhhh.socialblinddate.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import android.widget.ImageButton;
import com.rey.material.widget.ProgressView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Dev1 on 2016-11-09.
 */

public class ProfilePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Post> postList;
    public static final int ITEM_TYPE_HEADER = 0;
   public static final int ITEM_TYPE_CONTENT = 1;
   public static final int ITEM_TYPE_BOTTOM = 2;
   private int mHeaderCount=1;
    private int mBottomCount=1;
    private ImageButton profileLike;
    private String postUid;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();


    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private Activity activity;
    private RequestManager mGlideRequestManager;
    private ProgressView progressView;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();



///생성자 포스트리스트, 엑티비티, 데이터베이스 레퍼런스, 프로그래스뷰, 리사이클뷰
    public ProfilePostAdapter(List<Post> postList, Activity activity, RequestManager mGlideRequestManager, ProgressView progressView, RecyclerView recyclerView, ImageButton profileLike,String postUid) {
        this.postList = postList;
        this.activity=activity;
        this.mGlideRequestManager=mGlideRequestManager;
        this.progressView=progressView;
        this.recyclerView=recyclerView;
        this.profileLike=profileLike;
        this.postUid=postUid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if (viewType ==ITEM_TYPE_HEADER) {
         return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_header, parent, false));
      } else if (viewType == ITEM_TYPE_CONTENT) {
            return  new PostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false));
       } else if (viewType == ITEM_TYPE_BOTTOM) {
            return new BottomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_footer, parent, false));
       }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
       if (holder instanceof HeaderViewHolder) {
           _reference(((HeaderViewHolder) holder).nickname,((HeaderViewHolder) holder).local,((HeaderViewHolder) holder).likeCount,((HeaderViewHolder) holder).profileImg);
           ((HeaderViewHolder) holder).postCount.setText(String.valueOf(getContentItemCount()));
      }
       else if (holder instanceof PostHolder) {
        final Post post = postList.get(position-1);
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
        }else if (holder instanceof BottomViewHolder) {

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
        private ImageView profileImg;
        private TextView nickname,local,likeCount,postCount;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            profileImg=(ImageView)itemView.findViewById(R.id.profile_img);
            nickname=(TextView)itemView.findViewById(R.id.nickname);
            local=(TextView)itemView.findViewById(R.id.local);
            likeCount=(TextView)itemView.findViewById(R.id.likeCount);
            postCount=(TextView)itemView.findViewById(R.id.postCount);
        }
    }
    public static class BottomViewHolder extends RecyclerView.ViewHolder {

        public BottomViewHolder(View itemView) {
            super(itemView);

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
    private void _reference(final TextView nickname,final TextView local,final TextView likeCount,final ImageView imageView){
        databaseReference.child("users").child(postUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    nickname.setText("닉네임: "+userModel._uNickname);
                    local.setText(userModel._uLocal + " "+ userModel._uGender + " "+userModel._uAge);
                    if(!userModel._uImage1.equals("@null")) {

                        _initProfileImg(imageView,userModel._uImage1,userModel.updateStamp);

                    }

                        likeCount.setText(String.valueOf(userModel.starCount));

                    if (userModel.stars.containsKey(getUid())) {
                        profileLike.setImageResource(R.drawable.ic_action_like_pull_white);
                    } else {
                        profileLike.setImageResource(R.drawable.ic_action_like_white);
                    }

                    profileLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference userLike = databaseReference.child("users").child(userModel._uID);
                            onLike(userLike);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void _initProfileImg(final ImageView profileImg,final String imgPath,final String stamp){
        profileImg.post(new Runnable() {
            @Override
            public void run() {

                mGlideRequestManager.using(new FirebaseImageLoader()).load(storageReference.child(imgPath)).signature(new StringSignature(stamp))
                        .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(profileImg);
            }
        });
    }
    //좋아요 구현
    private void onLike(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserModel userModel = mutableData.getValue(UserModel.class);
                if (userModel == null) {
                    return Transaction.success(mutableData);
                }

                if (userModel.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    userModel.starCount = userModel.starCount - 1;
                    userModel.stars.remove(getUid());
                    databaseReference.child("like").child(postUid).child(getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity,"하트를 취소했습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Star the post and add self to stars
                    userModel.starCount = userModel.starCount + 1;
                    userModel.stars.put(getUid(), true);
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stampTime = CurDateFormat.format(date);
                    long yetNow = -1 * new Date().getTime();
                    LikeModel likeModel =new LikeModel(getUid(),stampTime,yetNow);
                    databaseReference.child("like").child(postUid).child(getUid()).setValue(likeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity,"하트를 보냈습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Set value and report transaction success
                mutableData.setValue(userModel);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private String getUid(){
        return firebaseAuth.getCurrentUser().getUid().toString();
    }
}
