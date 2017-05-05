package com.example.kush.moviedb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kush.moviedb.utilities.DatabaseUtils.MovieDBContract;
import com.example.kush.moviedb.utilities.DatabaseUtils.MovieDBHelper;
import com.example.kush.moviedb.utilities.MovieDBJSONUtils;
import com.example.kush.moviedb.utilities.MoviePosterClass;
import com.example.kush.moviedb.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView homeRecyclerView;
    ArrayList<MoviePosterClass> data;
    TextView mErrorMessageDisplay;
    ProgressBar mLoadingIndicator;
    HomeAdapter adapter;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper sqLiteOpenHelper;
    String type = "a";
    Boolean initial = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Stetho here
        Stetho.initializeWithDefaults(this);

        homeRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movies);
        data = new ArrayList<>();
        adapter = new HomeAdapter(data,this);

        homeRecyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(gridLayoutManager);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadHomeData("pop");
    }

    private void loadHomeData(String type) {
        new FetchHomeTask().execute(type);
    }

    private void showHomeDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        homeRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        homeRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchHomeTask extends AsyncTask<String, Void, MoviePosterClass[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MoviePosterClass[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String type = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(type);

            String jsonResponse;
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                MoviePosterClass[] homeData = MovieDBJSONUtils.getMovieDataFromJson(
                        HomeActivity.this, jsonResponse);
                return homeData;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviePosterClass[] moviePosterClasses) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();
            if (moviePosterClasses != null) {
                data.clear();

                sqLiteOpenHelper = new MovieDBHelper(HomeActivity.this);
                sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
                if (initial) {
                    sqLiteDatabase.delete(MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME, "movieType=?", new String[]{type});
                }
                for(MoviePosterClass i : moviePosterClasses) {
                    data.add(i);
                    ContentValues cv =new ContentValues();
                    cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER, i.getImagePicPath());
                    cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID, i.getId());
                    cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_TYPE, type);
                    contentValuesArrayList.add(cv);
                    initial = false;
                }
                try
                {
                    sqLiteDatabase.beginTransaction();
                    for(ContentValues cv:contentValuesArrayList){
                        sqLiteDatabase.replace(MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME, null, cv);
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                }
                catch (SQLException e) {
                    showErrorMessage();
                }
                finally {
                    sqLiteDatabase.endTransaction();
                }

                adapter.notifyDataSetChanged();
                showHomeDataView();
            } else {
                if (checkForData(type)){
                    data.clear();
                    try {
                        sqLiteOpenHelper = new MovieDBHelper(HomeActivity.this);
                        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
                        String[] columns = {MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER};
                        Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME,
                                columns, MovieDBContract.MovieDBEntry.COLUMN_MOVIE_TYPE + "=?",
                                new String[]{type},null,null,null);
                        while (cursor.moveToNext()){
                            String poster = cursor.getString(
                                    cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER));
                            int movieId = cursor.getInt(
                                    cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID));
                            MoviePosterClass moviePosterClass = new MoviePosterClass(poster,movieId);
                            data.add(moviePosterClass);
                        }
                        cursor.close();
                        adapter.notifyDataSetChanged();
                        showHomeDataView();
                    }catch (SQLException e){
                        showErrorMessage();
                    }
                }
                else
                    showErrorMessage();
            }
        }
    }

    public boolean checkForData(String type){
        boolean hasData = false;
        sqLiteOpenHelper = new MovieDBHelper(HomeActivity.this);

        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME,
                    null, MovieDBContract.MovieDBEntry.COLUMN_MOVIE_TYPE + "=?",
                    new String[]{type},null,null,null);

            if(cursor != null && cursor.getCount() > 0){
                hasData = true;
                cursor.close();
            }
        }
        catch (SQLException e){
            return false;
        }


        return hasData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.popular){
            loadHomeData("pop");
            type = "a";
        }
        else if (menuItemId == R.id.highrateing){
            loadHomeData("top");
            type = "b";
        }
        else if (menuItemId == R.id.favourites){
            loadFavouriteMovies();
            type = "f";
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadFavouriteMovies() {
        if (checkForFavouriteMoviesData()){
            data.clear();
            try {
                sqLiteOpenHelper = new MovieDBHelper(HomeActivity.this);
                sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIE_FAV_TABLE_NAME,
                        null, null, null, null, null, null);

                while (cursor.moveToNext()){
                    String poster = cursor.getString(
                            cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER));
                    int movieId = cursor.getInt(
                            cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID));
                    MoviePosterClass moviePosterClass = new MoviePosterClass(poster,movieId);
                    data.add(moviePosterClass);
                }
                cursor.close();
                adapter.notifyDataSetChanged();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                showHomeDataView();
            }catch (SQLException e){
                showErrorMessage();
            }
        }
        else
            showErrorMessage();
    }

    public boolean checkForFavouriteMoviesData(){
        boolean hasData = false;
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIES_TABLE_NAME,
                    null, null, null, null, null, null);

            if(cursor != null && cursor.getCount() > 0){
                hasData = true;
                cursor.close();
            }
        }
        catch (SQLException e){
            return false;
        }
        return hasData;
    }
}
