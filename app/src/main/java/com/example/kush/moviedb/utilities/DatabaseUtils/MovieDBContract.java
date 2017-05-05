package com.example.kush.moviedb.utilities.DatabaseUtils;

import android.provider.BaseColumns;

/**
 * Created by saini on 21-Feb-17.
 */

public class MovieDBContract {
    public static final class MovieDBEntry implements BaseColumns{
        public static final String MOVIE_DETAILS_TABLE_NAME = "movieDetails";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";
        public static final String COLUMN_MOVIE_BACKDROP = "movieBackdrop";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_RATING = "movieRating";
        public static final String COLUMN_MOVIE_TYPE = "movieType";
        public static final String COLUMN_MOVIE_DATE = "movieDate";
        public static final String MOVIES_TABLE_NAME = "movies";
        public static final String MOVIE_FAV_TABLE_NAME = "movieFav";
    }
}
