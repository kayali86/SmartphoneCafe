package com.kayali_developer.smartphonecafe.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Article{

    @SerializedName("articleId")
    private String articleId;
    @SerializedName("imageURL")
    private String imageURL;
    @SerializedName("title")
    private String title;
    @SerializedName("shortDescription")
    private String shortDescription;
    @SerializedName("text")
    private String text;
    @SerializedName("author")
    private String author;
    @SerializedName("authorId")
    private String authorId;
    @SerializedName("publishDate")
    private long publishDate;
    @SerializedName("feedback")
    private double feedback;
    @SerializedName("feedbackCount")
    private int feedbackCount;
    @SerializedName("ratersUid")
    private List<String> ratersUid;

    public Article() {
    }

    public Article(String articleId, String imageURL, String title, String shortDescription, String text, String author, String authorId, long publishDate,
                   double feedback, int feedbackCount, List<String> ratersUid) {
        this.articleId = articleId;
        this.imageURL = imageURL;
        this.title = title;
        this.shortDescription = shortDescription;
        this.text = text;
        this.author = author;
        this.authorId = authorId;
        this.publishDate = publishDate;
        this.feedback = feedback;
        this.feedbackCount = feedbackCount;
        this.ratersUid = ratersUid;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String autor) {
        this.author = autor;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public double getFeedback() {
        return feedback;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public List<String> getRatersUid() {
        return ratersUid;
    }

    public void setRatersUid(List<String> ratersUid) {
        this.ratersUid = ratersUid;
    }

}
