package com.kkkhhh.socialblinddate.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkkhhh.socialblinddate.Adapter.PostAdapter;
import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Etc.EndlessRecyclerOnScrollListener;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class FilterLocalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressView progressView;
    private String gender;
    private String local;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference manDataReference;
    private DatabaseReference womanDataReference;
    private PostAdapter mAdapter;
    private int lastPosition = 10;
    private int index = 0;
    private static int current_page = 1;
    private List<Post> postList;
    private LinearLayoutManager mManager;
    private TextView noPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_local);

        if (getIntent() != null) {
            local = getIntent().getStringExtra("local");
            gender = getIntent().getStringExtra("gender");
        }

        DataBaseFiltering dataBaseFiltering = new DataBaseFiltering();
        local = dataBaseFiltering.changeLocal(local);

        if (gender.equals("남자")) {
            manDataReference = databaseReference.child("posts-local").child("man").child(local);
            _initDataBaseReference(manDataReference, current_page);
        } else if (gender.equals("여자")) {
            womanDataReference = databaseReference.child("posts-local").child("woman").child(local);
            _initDataBaseReference(womanDataReference, current_page);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mManager.setStackFromEnd(false);
        mManager.setReverseLayout(false);
        recyclerView.setLayoutManager(mManager);
        progressView = (ProgressView) findViewById(R.id.progressview);
        noPost = (TextView) findViewById(R.id.no_post);
        postList = new ArrayList<Post>();
    }

    private void _initDataBaseReference(final DatabaseReference dbRef, final int current_page) {


        dbRef.orderByChild("stump").limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
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
                    }
                    //PostAdapter 참조
                    mAdapter = new PostAdapter(postList, FilterLocalActivity.this);
                    //리스트뷰 애니메이션 효과
                    //RecycleView 어댑터 세팅
                    recyclerView.setAdapter(mAdapter);
                    progressView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    index = postList.size() - 1;

                    recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager) {
                        @Override
                        public void onLoadMore(int currentPage) {
                            progressView.setVisibility(View.VISIBLE);
                            loadPaging(dbRef, current_page);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadPaging(DatabaseReference dbRef, int current_page) {


        dbRef.orderByChild("stump").startAt(postList.get(index).stump).limitToFirst(lastPosition).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
