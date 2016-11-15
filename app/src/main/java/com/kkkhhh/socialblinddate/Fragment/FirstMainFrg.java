package com.kkkhhh.socialblinddate.Fragment;


import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.PostWriterAct;

import com.kkkhhh.socialblinddate.Activity.SignProfileAct;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;


import com.kkkhhh.socialblinddate.ViewHolder.PostViewHolder;
import com.melnykov.fab.FloatingActionButton;

import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;


public class FirstMainFrg extends Fragment {
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostRef;
/*    private ParallaxRecyclerAdapter<Post> mAdapter;*/
    private PostAdapter mAdapter;
    private List<Post> postList;
    private ProgressView progressView;
    private RequestManager mGlideRequestManager;
    private FirebaseRecyclerAdapter<Post,PostViewHolder> fireAdapter;

    private AlertDialog filterDialog;

    private ImageButton filterBtn;

    private String[] itemsLocal = {"전국","서울", "부산", "대구", "대전","울산","광주","인천","세종","경기","경남","경북","전남","전북","강원","제주","충북","충남"};
    private String[] itemsGender = {"남자","여자"};


    public FirstMainFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first_main, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostWriterAct.class);
                startActivity(intent);
            }
        });
        filterBtn=(ImageButton)rootView.findViewById(R.id.frg_first_filter_btn);
        progressView = (ProgressView) rootView.findViewById(R.id.frg_first_progress);
        mGlideRequestManager=Glide.with(this);
        postList = new ArrayList<Post>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        recyclerView.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostRef = mDatabase.child("/posts/man-posts/");
        mPostRef.keepSynced(true);



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);
        init() ;
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fireAdapter != null) {
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onStop() {
        super.onStop();

    }


    private void init() {

        mPostRef.orderByChild("local").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post postModel = postSnapshot.getValue(Post.class);
                        postList.add(postModel);
                }
                mAdapter=new PostAdapter(postList,getActivity(),mPostRef,progressView,recyclerView);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
                alphaAdapter.setDuration(1000);
                recyclerView.setAdapter(alphaAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void alertDialog(){
        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_filter_list,null);
        final Button filter_local=(Button)dialogView.findViewById(R.id.filter_dialog_local);
        final Button filter_gender=(Button)dialogView.findViewById(R.id.filter_dialog_gender);
        final Button filter_upload_btn=(Button)dialogView.findViewById(R.id.filter_upload_btn);

        filter_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(itemsLocal,"지역선택",filter_local);
            }
        });

        filter_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(itemsGender, "성별 선택", filter_gender);
            }
        });
        filter_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String genderStr= filter_gender.getText().toString();
                String localStr= filter_local.getText().toString();
                DatabaseReference setDatabaseRef;

                if(TextUtils.isEmpty(genderStr)){
                    Toast.makeText(getActivity(),"성별을 선택해주세요",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(localStr)){
                    Toast.makeText(getActivity(),"지역을 선택해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    if(genderStr.equals("여자")) {
                        switch (localStr){
                            case "전국" :
                                setDatabaseRef = mDatabase.child("/posts/women-posts");
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "서울" :
                                setDatabaseRef = mDatabase.child("/posts/women/seoul/");
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "부산" :
                                setDatabaseRef = mDatabase.child("/posts/women/busan/");
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "대구" :
                                setDatabaseRef = mDatabase.child("/posts/women/deagu/"  );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "대전" :
                                setDatabaseRef = mDatabase.child("/posts/women/deajoen/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "울산" :
                                setDatabaseRef = mDatabase.child("/posts/women/ulsan/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "광주" :
                                setDatabaseRef = mDatabase.child("/posts/women/gwangju" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "인천" :
                                setDatabaseRef = mDatabase.child("/posts/women/incheon/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "세종" :
                                setDatabaseRef = mDatabase.child("/posts/women/sejong/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경기" :
                                setDatabaseRef = mDatabase.child("/posts/women/kyunggi/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경남" :
                                setDatabaseRef = mDatabase.child("/posts/women/kyungnam/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경북" :
                                setDatabaseRef = mDatabase.child("/posts/women/kyungbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "전남" :
                                setDatabaseRef = mDatabase.child("/posts/women/jeonnam/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "전북" :
                                setDatabaseRef = mDatabase.child("/posts/women/jeonbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "강원" :
                                setDatabaseRef = mDatabase.child("/posts/women/kangwon/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "제주" :
                                setDatabaseRef = mDatabase.child("/posts/women/jeju/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "충북" :
                                setDatabaseRef = mDatabase.child("/posts/women/chungbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "충남" :
                                setDatabaseRef = mDatabase.child("/posts/women/chungnam/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            default:
                                break;
                        }
                    }else if(genderStr.equals("남자")){

                        switch (localStr){
                            case "전국" :
                                setDatabaseRef = mDatabase.child("/posts/man-posts/" );
                                setDatabaseReference(setDatabaseRef);
                                dialogView.setVisibility(View.INVISIBLE);
                                break;
                            case "서울" :
                                setDatabaseRef = mDatabase.child("/posts/man/seoul/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "부산" :
                                setDatabaseRef = mDatabase.child("/posts/man/busan/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "대구" :
                                setDatabaseRef = mDatabase.child("/posts/man/deagu/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "대전" :
                                setDatabaseRef = mDatabase.child("/posts/man/deajoen/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "울산" :
                                setDatabaseRef = mDatabase.child("/posts/man/ulsan/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "광주" :
                                setDatabaseRef = mDatabase.child("/posts/man/gwangju" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "인천" :
                                setDatabaseRef = mDatabase.child("/posts/man/incheon/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "세종" :
                                setDatabaseRef = mDatabase.child("/posts/man/sejong/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경기" :
                                setDatabaseRef = mDatabase.child("/posts/man/kyunggi/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경남" :
                                setDatabaseRef = mDatabase.child("/posts/man/kyungnam/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "경북" :
                                setDatabaseRef = mDatabase.child("/posts/man/kyungbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "전남" :
                                setDatabaseRef = mDatabase.child("/posts/man/jeonnam/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "전북" :
                                setDatabaseRef = mDatabase.child("/posts/man/jeonbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "강원" :
                                setDatabaseRef = mDatabase.child("/posts/man/kangwon/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "제주" :
                                setDatabaseRef = mDatabase.child("/posts/man/jeju/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "충북" :
                                setDatabaseRef = mDatabase.child("/posts/man/chungbuk/" );
                                setDatabaseReference(setDatabaseRef);
                                break;
                            case "충남" :
                                setDatabaseRef = mDatabase.child("/posts/man/chungnam/" );
                                setDatabaseReference(setDatabaseRef);

                                break;
                            default:
                                break;
                        }
                }
            }
        }

});
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        filterDialog=builder.create();
        filterDialog.show();
    }
    private void showDialog(final String[] item, String title, final Button btn){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(title);
        builder.setPositiveButton("닫기", null);

        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = Arrays.asList(item).get(which);

                btn.setText(selectedText);
                dialog.cancel();

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }
    private void setDatabaseReference(DatabaseReference databaseReference){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post postModel = postSnapshot.getValue(Post.class);
                    postList.add(postModel);
                }
                mAdapter=new PostAdapter(postList,getActivity(),mPostRef,progressView,recyclerView);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
                alphaAdapter.setDuration(1000);
                recyclerView.setAdapter(alphaAdapter);
                filterDialog.cancel();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


