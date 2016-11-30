package com.kkkhhh.socialblinddate.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.kkkhhh.socialblinddate.Activity.PostWriterAct;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;
import com.melnykov.fab.FloatingActionButton;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;


public class SecondMainFrg extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private ProgressView progressView;
    private List<Post> postList;
    private PostAdapter mAdapter;
    private TextView noPost;
    private LinearLayoutManager mManager;
    private int index = 0;
    private int lastPosition = 10;
    private static int current_page = 1;
    private FloatingActionButton fab;
    public SecondMainFrg() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_second_main, container, false);
        _init(rootView);
        return rootView;
    }
    private void _init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_recycler_view);
        recyclerView.setHasFixedSize(true);
        progressView = (ProgressView) view.findViewById(R.id.progressview);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        postList = new ArrayList<Post>();
        noPost=(TextView)view.findViewById(R.id.no_post);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(false);
        mManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(mManager);
        String uID=fireAuth.getCurrentUser().getUid();
        _initReference(mDatabase.child("user-posts").child(uID));
        fab.attachToRecyclerView(recyclerView);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostWriterAct.class);
                startActivity(intent);
            }
        });
    }



    private void _initReference(final DatabaseReference databaseReference){

        databaseReference.orderByChild("stump").limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    noPost.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else {

                    noPost.setVisibility(View.GONE);
                    //초기에 리스트를 초기화
                    postList.clear();

                    //for문을 돌려 리스트 값만큼 추가
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post postModel = postSnapshot.getValue(Post.class);

                        postList.add(postModel);

                    }
                    //PostAdapter 참조
                    mAdapter = new PostAdapter(postList, getActivity(),progressView);

                    //RecycleView 어댑터 세팅
                    recyclerView.setAdapter(mAdapter);



                    recyclerView.setVisibility(View.VISIBLE);
                    //index 값
                    index = postList.size() - 1;

                    recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager) {
                        @Override
                        public void onLoadMore(int currentPage) {
                            progressView.setVisibility(View.VISIBLE);
                            loadPaging(databaseReference,current_page);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadPaging(DatabaseReference dbRef,int current_page) {


        dbRef.orderByChild("stump").startAt(postList.get(index).stump).limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for문을 돌려 리스트 값만큼 추가
                postList.remove(index);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post postModel = postSnapshot.getValue(Post.class);

                    postList.add(postModel);

                }


                mAdapter.notifyDataSetChanged();
                index = postList.size() - 1;


                //리스트뷰 애니메이션 효과
                progressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
