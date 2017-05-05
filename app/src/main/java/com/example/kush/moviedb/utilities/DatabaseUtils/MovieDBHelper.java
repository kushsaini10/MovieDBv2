package com.example.kush.moviedb.utilities.DatabaseUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by saini on 21-Feb-17.
 */

public class MovieDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movieDb.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_DETAILS_TABLE = "CREATE TABLE " +
                MovieDBContract.MovieDBEntry.MOVIE_DETAILS_TABLE_NAME + " (" +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL" +
                ");";
        final String SQL_CREATE_MOVIE_FAV_TABLE = "CREATE TABLE " +
                MovieDBContract.MovieDBEntry.MOVIE_FAV_TABLE_NAME + " (" +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL" +
                ");";
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME + " (" +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_TYPE + " TEXT NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_MOVIE_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_FAV_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO (1) Add some upgrade code
    }
}
