package com.example.kush.moviedb.utilities;

/**
 * Created by saini on 08-Feb-17.
 */
public class MovieReviewClass {
    public String author;
    public String content;

    public MovieReviewClass(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
