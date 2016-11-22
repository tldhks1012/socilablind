package com.kkkhhh.socialblinddate.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Activity.PostWriterAct;


import com.kkkhhh.socialblinddate.Adapter.PostAdapter;

import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;


import com.kkkhhh.socialblinddate.ViewHolder.PostViewHolder;
import com.melnykov.fab.FloatingActionButton;

import com.rey.material.widget.ProgressView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;


public class FirstMainFrg extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    private DatabaseReference manReference;
    private DatabaseReference womanReference;
    /*    private ParallaxRecyclerAdapter<Post> mAdapter;*/
    private PostAdapter mAdapter;
    private List<Post> postList;
    private ProgressView progressView;
    private RequestManager mGlideRequestManager;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> fireAdapter;
    private AlertDialog filterDialog;
    private ImageButton filterBtn;
    private static DatabaseReference loadingDBReference;

    private int limitPosition=1;
    private int lastPosition=3;

    private String[] itemsLocal = {"전국", "서울", "부산", "대구", "대전", "울산", "광주", "인천", "세종", "경기", "경남", "경북", "전남", "전북", "강원", "제주", "충북", "충남"};

    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();

    private String uID, genderCheck;
    private FrameLayout manBtn, womanBtn;
    private TextView manText, womanText, noPost, coin;


    public FirstMainFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first_main, container, false);

        //UI 초기 설정 값
        _init(rootView);

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
        mManager.setStackFromEnd(true);
        mManager.setReverseLayout(false);
        recyclerView.setLayoutManager(mManager);
        fab.attachToRecyclerView(recyclerView);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostWriterAct.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void _init(View rootView) {
        //플로팅액션버튼
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        //필터 이미지 버튼
        filterBtn = (ImageButton) rootView.findViewById(R.id.frg_first_filter_btn);
        //progressView
        progressView = (ProgressView) rootView.findViewById(R.id.progressview);
        mGlideRequestManager = Glide.with(this);
        postList = new ArrayList<Post>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        recyclerView.setHasFixedSize(true);


        manBtn = (FrameLayout) rootView.findViewById(R.id.frg_first_man_btn);
        womanBtn = (FrameLayout) rootView.findViewById(R.id.frg_first_woman_btn);

        manText = (TextView) rootView.findViewById(R.id.frg_first_man_txt);
        womanText = (TextView) rootView.findViewById(R.id.frg_first_woman_txt);
        noPost = (TextView) rootView.findViewById(R.id.no_post);
        coin = (TextView) rootView.findViewById(R.id.frg_first_coin);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserValue.SHARED_NAME, Context.MODE_PRIVATE);
        int coinValue = sharedPreferences.getInt(UserValue.USER_COIN, 0);

        coin.setText("Coin: " + coinValue);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        womanReference = mDatabase.child("/posts/woman-posts/");
        manReference = mDatabase.child("/posts/man-posts/");
        uID = fireAuth.getCurrentUser().getUid();

     /*   mPostRef.keepSynced(true);*/
        _initReference();
        genderBtnClick(manBtn);
        genderBtnClick(womanBtn);
    }

    private void _initReference() {
        mDatabase.child("users").child(uID).child("_uGender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender = dataSnapshot.getValue(String.class);

                switch (gender) {
                    case "남자":
                        _initDataBaseReference(womanReference);
                        womanTextChange();
                        break;
                    case "여자":
                        _initDataBaseReference(manReference);
                        manTextChange();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void genderBtnClick(final FrameLayout btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btn.getId()) {
                    case R.id.frg_first_man_btn:
                        _initDataBaseReference(manReference);
                        manTextChange();
                        progressView.setVisibility(View.VISIBLE);
                        break;

                    case R.id.frg_first_woman_btn:
                        progressView.setVisibility(View.VISIBLE);
                        _initDataBaseReference(womanReference);
                        womanTextChange();
                        break;
                }
            }
        });

    }

    private void manTextChange() {
        genderCheck = "남자";
        womanText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        womanText.setTextColor(Color.GRAY);
        manText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        manText.setTextColor(Color.BLACK);
    }

    private void womanTextChange() {
        genderCheck = "여자";
        womanText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        womanText.setTextColor(Color.BLACK);
        manText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        manText.setTextColor(Color.GRAY);
    }

    //초기 레퍼런스 설정
    private void _initDataBaseReference(final DatabaseReference dbRef) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    noPost.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    noPost.setVisibility(View.GONE);
                    //초기에 리스트를 초기화
                    postList.clear();
                    //for문을 돌려 리스트 값만큼 추가
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post postModel = postSnapshot.getValue(Post.class);
                        postList.add(postModel);
                        limitPosition++;
                    }
                    //PostAdapter 참조
                    mAdapter = new PostAdapter(postList, getActivity(), dbRef, progressView, recyclerView,lastPosition);
                    //리스트뷰 애니메이션 효과
                    AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
                    alphaAdapter.setDuration(1000);
                    //RecycleView 어댑터 세팅
                    recyclerView.setAdapter(alphaAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void alertDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //dialogView 레이아웃 참조
        final View dialogView = inflater.inflate(R.layout.dialog_filter_list, null);
        //필터링 지역 버튼 참조
        final Button filter_local = (Button) dialogView.findViewById(R.id.filter_dialog_local);
        //필터링 성별 버튼 참조

        //필터링 업로드 됐을때 버튼 참조
        final Button filter_upload_btn = (Button) dialogView.findViewById(R.id.filter_upload_btn);

        filter_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog ListView 메소드 (지역 배열,제목,버튼)
                showDialog(itemsLocal, "지역선택", filter_local);
            }
        });
        filter_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //필터링 지역 버튼 값 String 값으로 받음
                String localStr = filter_local.getText().toString();

                //genderStr,localStr(성별, 지역) 값이 없을 경우 토스트 쇼
                if (TextUtils.isEmpty(localStr)) {
                    Toast.makeText(getActivity(), "지역을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (genderCheck.equals("남자")) {
                        manTextChange();
                        filterGender(localStr, manReference);
                        filterDialog.cancel();
                    } else if (genderCheck.equals("여자")) {
                        womanTextChange();
                        filterGender(localStr, womanReference);
                        filterDialog.cancel();
                    }
                }
            }

        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        filterDialog = builder.create();
        filterDialog.show();
    }


    //리스트 다이아로그
    private void showDialog(final String[] item, String title, final Button btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//타이틀 값 설정
        builder.setTitle(title);
        builder.setPositiveButton("닫기", null);
//값이 입력되면 버튼으로 값 전달
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

    //setData 값을 받아서 리싸이클뷰에 뿌려주는 메소드
    private void setDatabaseReference(final DatabaseReference databaseReference, final String local) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post postModel = postSnapshot.getValue(Post.class);
                    if (postModel.local.equals(local)) {
                        postList.add(postModel);
                    }
                }
             /*   mAdapter = new PostAdapter(postList, getActivity(), databaseReference, progressView, recyclerView);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
                alphaAdapter.setDuration(1000);
                recyclerView.setAdapter(alphaAdapter);*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void filterGender(String local, DatabaseReference databaseReference) {
        setDatabaseReference(databaseReference, local);
    }

}


