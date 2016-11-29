package com.kkkhhh.socialblinddate.Fragment;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.WelcomeAct;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourMainFrg extends Fragment {
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDatabase.getReference();
    private FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStoreRef = firebaseStorage.getReference();
    private ImageView profileImg;
    private TextView nickName, coin;
    private String uID;
    private ProgressView progressView;
    private ScrollView scrollView;
    private RequestManager mGlideRequestManager;
    private FrameLayout logoutBtn;


    public FourMainFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_four_main, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View view) {

        mGlideRequestManager=Glide.with(this);

        profileImg = (ImageView) view.findViewById(R.id.frg_four_profile_img);
        nickName = (TextView) view.findViewById(R.id.frg_four_nickname);
        coin=(TextView)view.findViewById(R.id.frg_four_coin);
        progressView = (ProgressView) view.findViewById(R.id.frg_four_progress);
        logoutBtn=(FrameLayout)view.findViewById(R.id.logout_btn);
        scrollView = (ScrollView) view.findViewById(R.id.frg_four_scroll);
        scrollView.setVisibility(View.GONE);
        uID = mFireBaseAuth.getCurrentUser().getUid();


        ValueEventListener profileImgListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    mGlideRequestManager.using(new FirebaseImageLoader()).load(mStoreRef.child(userModel._uImage1)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            nickName.setText(userModel._uNickname);
                            coin.setText("Coin : " +userModel._uCoin);
                            scrollView.setVisibility(View.VISIBLE);
                            progressView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            nickName.setText(userModel._uNickname);
                            coin.setText("Coin : " +userModel._uCoin);
                            scrollView.setVisibility(View.VISIBLE);
                            progressView.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(profileImg);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference userImgRef = mDatabaseRef.child("users").child(uID);
        userImgRef.keepSynced(true);
        userImgRef.addListenerForSingleValueEvent(profileImgListener);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(), WelcomeAct.class);
                startActivity(intent);
                getActivity().finish();
                SharedPreferences.Editor preferences = getActivity().getSharedPreferences(UserValue.SHARED_NAME, MODE_PRIVATE).edit();
                preferences.clear();
                preferences.commit();
            }
        });
    }
}
