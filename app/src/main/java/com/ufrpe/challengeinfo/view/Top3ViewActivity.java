package com.ufrpe.challengeinfo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ufrpe.challengeinfo.R;
import com.ufrpe.challengeinfo.controller.ResultFormat;
import com.ufrpe.challengeinfo.model.PostResult;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Top3ViewActivity extends AppCompatActivity {

    private TextView txtLastChangeAt;
    private ListView listTopPosts;
    private List<PostResult> posts;
    private ResultFormat resultFormat;
    private static String result;

    @Override
    public void onBackPressed() {
        finish();
        Intent intentBackRecord = new Intent(Top3ViewActivity.this, RecordActivity.class);
        startActivity(intentBackRecord);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_posts_screen);
        posts = new LinkedList<>();
        resultFormat = new ResultFormat();
        txtLastChangeAt = (TextView) findViewById(R.id.txt_last_change);
        listTopPosts = (ListView) findViewById(R.id.list_top_posts);
        update();
    }

    public void populate() {
        ArrayAdapter<PostResult> adapter = new Top3ViewActivity.PostListAdapter();
        listTopPosts.setAdapter(adapter);
    }

    public void update() {
        try {
            resultFormat.format(result);
            txtLastChangeAt.setText("Last change at: " + resultFormat.getTimestamp());
            posts = resultFormat.getTop3();
            populate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class PostListAdapter extends ArrayAdapter {

        public PostListAdapter() {
            super(Top3ViewActivity.this, R.layout.list_top_post_item, posts);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_top_post_item, parent, false);
            }
            TextView txtPosition = (TextView) convertView.findViewById(R.id.txt_post_score);
            TextView txtPostId = (TextView) convertView.findViewById(R.id.txt_post_id);
            TextView txtPostAuthor = (TextView) convertView.findViewById(R.id.txt_post_author);
            TextView txtNumberOfCommenters = (TextView) convertView.findViewById(R.id.txt_number_of_commenters);

            PostResult actualPost = posts.get(position);

            txtPosition.setText("Post Score: " + actualPost.getPostScore());
            txtPostId.setText("Post ID: " + actualPost.getId().toString());
            txtPostAuthor.setText("Post Author: " + actualPost.getAuthor());
            txtNumberOfCommenters.setText("Number of Commenters: " + actualPost.getNumberOfCommenters().toString());

            return convertView;
        }

    }

    public static void setResult(String result) {
        Top3ViewActivity.result = result;
    }
}
