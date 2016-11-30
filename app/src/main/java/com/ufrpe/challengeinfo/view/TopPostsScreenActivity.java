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
import android.widget.Toast;


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

public class TopPostsScreenActivity extends AppCompatActivity {

    private TextView txtLastChangeAt;
    private ListView listTopPosts;
    private List<PostResult> posts;
    private ResultFormat resultFormat;

    @Override
    public void onBackPressed() {
        finish();
        Intent intentBackMainScreen = new Intent(TopPostsScreenActivity.this, MainScreenActivity.class);
        startActivity(intentBackMainScreen);
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
        ArrayAdapter<PostResult> adapter = new PostListAdapter();
        listTopPosts.setAdapter(adapter);
    }

    public void update() {
        String result = communicate();
        try {
            resultFormat.format(result);
            txtLastChangeAt.setText("Last change at: " + resultFormat.getTimestamp());
            posts = resultFormat.getTop3();
            populate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public String communicate(){
        final InfoGetter infoGetter = new InfoGetter();
        infoGetter.execute();
        try {
            infoGetter.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return infoGetter.result;
    }

    public class InfoGetter extends AsyncTask<String, Void, String> {

        public String result;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://" + MainScreenActivity.serverIP + ":5000/query1");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            this.result = result.toString();
            return result.toString();
        }

    }

    private class PostListAdapter extends ArrayAdapter {

        public PostListAdapter() {
            super(TopPostsScreenActivity.this, R.layout.list_top_post_item, posts);
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
}
