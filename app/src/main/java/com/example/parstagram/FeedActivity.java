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

        protected PostsAdapter adapter;
        protected List<Post> allPosts;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_feed);


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

            // initialize the array that will hold posts and create a PostsAdapter
            allPosts = new ArrayList<>();
            adapter = new PostsAdapter(this, allPosts);

            // set the adapter on the recycler view
            rvPosts.setAdapter(adapter);
            // set the layout manager on the recycler view
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            // query posts from Parstagram
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
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("TAG", "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i("TAG", "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
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

