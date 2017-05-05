package com.example.kush.moviedb.utilities;

/**
 * Created by saini on 08-Feb-17.
 */
public class MovieTrailerClass {
    public String key;
    public String trailerImgUrl;
    public String trailerUrl;

    public MovieTrailerClass(String key, String trailerImgUrl, String trailerUrl) {
        this.key = key;
        this.trailerImgUrl = trailerImgUrl;
        this.trailerUrl = trailerUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getTrailerImgUrl() {
        return trailerImgUrl;
    }

    public void setTrailerImgUrl(String trailerImgUrl) {
        this.trailerImgUrl = trailerImgUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
