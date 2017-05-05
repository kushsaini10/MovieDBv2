package com.example.kush.moviedb.utilities;

import android.content.Context;
import android.content.res.Configuration;

import com.example.kush.moviedb.DetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by saini on 02-Jan-17.
 */

public class MovieDBJSONUtils {
    private static int getSizeName(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL: {
                return 1;
            }

            case Configuration.SCREENLAYOUT_SIZE_NORMAL: {
                return 2;
            }
            case Configuration.SCREENLAYOUT_SIZE_LARGE: {
                return 3;
            }
            case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
                return 4;
            }
            default: {
                return 2;
            }
        }
    }
    public static MoviePosterClass[] getMovieDataFromJson(Context context, String moviesJson) {
        JSONObject obj;
        try {
                obj = new JSONObject(moviesJson);
            JSONArray movies = obj.getJSONArray("results");
            MoviePosterClass[] output = new MoviePosterClass[movies.length()];
            for (int i = 0; i < movies.length(); i++) {
                JSONObject batchesJSONObject = movies.getJSONObject(i);
                String imagePosterPath = batchesJSONObject.getString("poster_path");
                int movieId = batchesJSONObject.getInt("id");
                String imageButtonPath = "http://image.tmdb.org/t/p/w342" + imagePosterPath;
                output[i] = new MoviePosterClass(imageButtonPath, movieId);
            }
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MovieDetailClass getMovieDetailsFromJson(Context context, String movieJson) {
        JSONObject obj;
        String posterSize = "342";
        String backDropSize = "500";
        int size = getSizeName(context);
        if (size == 1){
            posterSize = "90";
            backDropSize = "342";
        }
        else if (size == 2){
            posterSize = "342";
            backDropSize = "500";
        }
        else if (size == 3){
            posterSize = "500";
            backDropSize = "500";
        }
        else if (size == 4){
            posterSize = "500";
            backDropSize = "500";
        }
//        switch (size){
//            case 3:{
//                posterSize = "500";
//                backDropSize = "500";
//            }
//            case 4:{
//                posterSize = "500";
//                backDropSize = "500";
//            }
//            case 1:{
//                posterSize = "90";
//                backDropSize = "342";
//            }
//            case 2:{
//                posterSize = "342";
//                backDropSize = "500";
//            }
//        }
        try {
            obj = new JSONObject(movieJson);
            String backDrop = "http://image.tmdb.org/t/p/w"+ backDropSize +"/" + obj.getString("backdrop_path");
            String poster = "http://image.tmdb.org/t/p/w" + posterSize + "/" + obj.getString("poster_path");
            int id = obj.getInt("id");
            int rating = obj.getInt("vote_average");
            String date = obj.getString("release_date");
            String name = obj.getString("original_title");
            String synops = obj.getString("overview");
            return new MovieDetailClass(id,name,synops,date,rating,backDrop,poster);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MovieTrailerClass[] getMovieTrailersFromJson(DetailsActivity detailsActivity, String trailersJson) {
        JSONObject obj;

        try {
            obj = new JSONObject(trailersJson);
            JSONArray trailers = obj.getJSONArray("results");
            MovieTrailerClass[] output = new MovieTrailerClass[trailers.length()];
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject trailersJSONObject = trailers.getJSONObject(i);
                String key = trailersJSONObject.getString("key");
                String trailerImgUrl = "http://img.youtube.com/vi/"+ key +"/hqdefault.jpg";
                String trailerUrl = "https://www.youtube.com/watch?v=" + key;
                output[i] = new MovieTrailerClass(key,trailerImgUrl,trailerUrl);
            }
            return output;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static MovieReviewClass[] getMovieReviewsFromJson(DetailsActivity detailsActivity, String reviewJson) {
        JSONObject obj;

        try {
            obj = new JSONObject(reviewJson);
            JSONArray reviews = obj.getJSONArray("results");
            MovieReviewClass[] output = new MovieReviewClass[reviews.length()];
            for (int i = 0; i < reviews.length(); i++) {
                JSONObject reviewsJSONObject = reviews.getJSONObject(i);
                String author = reviewsJSONObject.getString("author");
                String content = reviewsJSONObject.getString("content");
                output[i] = new MovieReviewClass(author,content);
            }
            return output;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
