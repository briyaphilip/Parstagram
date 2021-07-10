package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
        private RecyclerView rvPosts;
        private SwipeRefreshLayout swipeContainer;
        private Button postBtn;
        private EndlessRecyclerViewScrollListener scrollListener;
        private Button logoutBtn;

        protected PostsAdapter adapter;
        protected List<Post> allPosts;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_feed);

            logoutBtn = findViewById(R.id.logoutBtn);
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLogoutButton(v);
                }
            });


            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    queryPosts();
                }
            });

            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            postBtn = findViewById(R.id.postBtn);
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG", "onClick post button");
                    startActivity(new Intent(FeedActivity.this, PostActivity.class));
                }
            });


            Log.i("APP", "Feed Activity");
            rvPosts = findViewById(R.id.rvPosts);

            allPosts = new ArrayList<>();
            adapter = new PostsAdapter(this, allPosts);

            rvPosts.setAdapter(adapter);
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            queryPosts();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvPosts.setLayoutManager(linearLayoutManager);

            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    queryPosts();
                }
            };

            rvPosts.addOnScrollListener(scrollListener);

        }


    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("TAG", "Issue with getting posts", e);
                    return;
                }

                for (Post post : posts) {
                    Log.i("TAG", "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void onLogoutButton(View view) {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        finish();
    }


}

