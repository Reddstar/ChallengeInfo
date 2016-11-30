package com.ufrpe.challengeinfo.model;

public class PostResult {

    private String id;
    private String numberOfCommenters;
    private Integer position;
    private String author;
    private String postScore;

    public PostResult(){

    }
    public PostResult(int position, String id, String author,String numberOfCommenters){
        this.id = id;
        this.author = author;
        this.numberOfCommenters = numberOfCommenters;
        this.position = position;
    }

    public String getPostScore() {
        return postScore;
    }

    public void setPostScore(String postScore) {
        this.postScore = postScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumberOfCommenters() {
        return numberOfCommenters;
    }

    public void setNumberOfCommenters(String numberOfCommenters) {
        this.numberOfCommenters = numberOfCommenters;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
