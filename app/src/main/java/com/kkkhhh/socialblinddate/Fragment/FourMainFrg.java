package com.kkkhhh.socialblinddate.Fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Etc.CustomBitmapPool;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
    private TextView nickName;
    private String uID;
    private ProgressView progressView;
    private ScrollView scrollView;
    private RequestManager mGlideRequestManager;


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
        progressView = (ProgressView) view.findViewById(R.id.frg_four_progress);
        scrollView = (ScrollView) view.findViewById(R.id.frg_four_scroll);
        scrollView.setVisibility(View.GONE);
        uID = mFireBaseAuth.getCurrentUser().getUid();


        ValueEventListener profileImgListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    mGlideRequestManager.using(new FirebaseImageLoader()).load(mStoreRef.child(userModel._uImage1)).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(profileImg);
                    nickName.setText(userModel._uNickname);
                    scrollView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference userImgRef = mDatabaseRef.child("users").child(uID);
        userImgRef.keepSynced(true);
        userImgRef.addListenerForSingleValueEvent(profileImgListener);
    }


}
