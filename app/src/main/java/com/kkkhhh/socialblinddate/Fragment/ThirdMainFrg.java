package com.kkkhhh.socialblinddate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkkhhh.socialblinddate.Adapter.ChatAdapter;
import com.kkkhhh.socialblinddate.Adapter.ChatListAdapter;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Model.ChatList;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdMainFrg extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;

    private List<ChatList> chatLists;
    private ChatListAdapter mAdapter;
    private TextView noPost;
    private LinearLayoutManager mManager;
    public ThirdMainFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_third_main, container, false);
        _init(rootView);
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        _initReference();

    }
    private void _init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_recycler_view);
        recyclerView.setHasFixedSize(true);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        chatLists = new ArrayList<ChatList>();
        noPost=(TextView)view.findViewById(R.id.no_post);

    }
    private void _initReference(){
        String uID=fireAuth.getCurrentUser().getUid();
        mDatabase.child("user-chatList").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){

                }else {
                    noPost.setVisibility(View.INVISIBLE);
                    //초기에 리스트를 초기화
                    chatLists.clear();
                    //for문을 돌려 리스트 값만큼 추가
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ChatList chatList = postSnapshot.getValue(ChatList.class);
                        chatLists.add(chatList);
                    }
                    //PostAdapter 참조
                    mAdapter = new ChatListAdapter(chatLists,getActivity());
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
}
