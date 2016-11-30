package com.ufrpe.challengeinfo.controller;

import android.widget.Toast;

import com.ufrpe.challengeinfo.model.PostResult;
import com.ufrpe.challengeinfo.view.TopPostsScreenActivity;

import java.util.LinkedList;
import java.util.List;

public class ResultFormat {

    private LinkedList<PostResult> top3;
    private String timestamp;

    public ResultFormat(){
        this.top3 = new LinkedList<>();
    }


    //1 - timestamp
    //2...5 - post1
        //2 - postID
        //3 - userPostID
        //4 - postScore
        //5 - numCommPost
    //6...9 - post2
    //10...13 - post 3
    public void format(String result){
        LinkedList<PostResult> results = new LinkedList<>();
        String time = "";
        String[] parts = result.split(",");
        parts[13] = parts[13].substring(0, parts[13].length()-2);
        time = parts[1].substring(0, parts[1].length()-10);
        for (int i = 2; i < 13;){
            PostResult postResult = new PostResult();
            postResult.setId(parts[i]);
            postResult.setAuthor(parts[i+1]);
            postResult.setPostScore(parts[i+2]);
            postResult.setNumberOfCommenters(parts[i+3]);
            results.add(postResult);
            i = i+4;

        }
        setTimestamp(time);
        setTop3(results);
    }
    public LinkedList<PostResult> getTop3() {
        return top3;
    }

    public void setTop3(LinkedList<PostResult> top3) {
        this.top3 = top3;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String addAZero(String text){
        text = text.replaceAll(" ", "");
        if (text.length() == 1){
            text = "0"+text;
        }
        return text;
    }
}
