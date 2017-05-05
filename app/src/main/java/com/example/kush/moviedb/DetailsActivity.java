package com.example.kush.moviedb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kush.moviedb.utilities.DatabaseUtils.MovieDBContract;
import com.example.kush.moviedb.utilities.DatabaseUtils.MovieDBHelper;
import com.example.kush.moviedb.utilities.MovieDBJSONUtils;
import com.example.kush.moviedb.utilities.MovieDetailClass;
import com.example.kush.moviedb.utilities.MoviePosterClass;
import com.example.kush.moviedb.utilities.MovieReviewClass;
import com.example.kush.moviedb.utilities.MovieTrailerClass;
import com.example.kush.moviedb.utilities.NetworkUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ImageView backDrop;
    ImageView poster;
    TextView releaseDate;
    TextView name;
    TextView synopsis;
    TextView rating;
    LinearLayout details;
    RecyclerView trailerRecyclerView;
    ArrayList<MovieTrailerClass> data;
    TrailerAdapter adapter;
    ReviewAdapter adapterR;
    ArrayList<MovieReviewClass> dataR;
    RecyclerView reviewRecyclerView;
    TextView mErrorMessageDisplay;
    TextView tErrorMessageDisplay;
    TextView rErrorMessageDisplay;
    ProgressBar mLoadingIndicator;
    ProgressBar tLoadingIndicator;
    ProgressBar rLoadingIndicator;
    LikeButton favouriteBtn;
    SQLiteDatabase sqLiteDatabase;
    SQLiteOpenHelper sqLiteOpenHelper;
    String movieId;
    Boolean favourite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movieId = String.valueOf(getIntent().getExtras().getInt("movieId"));

        trailerRecyclerView = (RecyclerView) findViewById(R.id.trailersRecView);
        data = new ArrayList<>();
        adapter = new TrailerAdapter(data,this);

        trailerRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);

        reviewRecyclerView = (RecyclerView) findViewById(R.id.reviewsRecView);
        dataR = new ArrayList<>();
        adapterR = new ReviewAdapter(dataR,this);

        reviewRecyclerView.setAdapter(adapterR);

        LinearLayoutManager linearLayoutManagerR = new LinearLayoutManager(this);
        linearLayoutManagerR.setOrientation(LinearLayoutManager.VERTICAL);
        reviewRecyclerView.setLayoutManager(linearLayoutManagerR);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        tErrorMessageDisplay = (TextView) findViewById(R.id.tv_t_error_message_display);
        rErrorMessageDisplay = (TextView) findViewById(R.id.tv_r_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        rLoadingIndicator = (ProgressBar) findViewById(R.id.reviews_loading_indicator);
        tLoadingIndicator = (ProgressBar) findViewById(R.id.trailers_loading_indicator);
        details = (LinearLayout) findViewById(R.id.activity_details);
        favouriteBtn = (LikeButton) findViewById(R.id.fav_button);

        loadDetails(movieId);
        loadTrailers(movieId);
        loadReviews(movieId);

    }

    private Boolean checkForFavourite(String movieId) {
        sqLiteOpenHelper = new MovieDBHelper(DetailsActivity.this);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIE_FAV_TABLE_NAME,
                    null, MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{movieId},null,null,null);

            if(cursor != null && cursor.getCount() > 0){
                cursor.close();
                return true;
            }
        }
        catch (SQLException e){
            return false;
        }
        return false;
    }

    private void loadReviews(String id) { new FetchReviewsTask().execute("review",id);
    }

    private void loadTrailers(String id) { new FetchTrailersTask().execute("trailer",id);
    }

    private class FetchTrailersTask extends AsyncTask<String,Void,MovieTrailerClass[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tLoadingIndicator.setVisibility(View.VISIBLE);
            trailerRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected MovieTrailerClass[] doInBackground(String... params) {
            if (params.length == 0)
                return null;
            String type = params[0];
            String id = params[1];
            URL movieRequestUrl = NetworkUtils.buildUrl(type,id);
            String jsonResponse;
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return MovieDBJSONUtils.getMovieTrailersFromJson(DetailsActivity.this, jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieTrailerClass[] movieTrailerClasses) {
            super.onPostExecute(movieTrailerClasses);
            if (movieTrailerClasses != null){
                data.clear();

                for(MovieTrailerClass i : movieTrailerClasses)
                    data.add(i);
                adapter.notifyDataSetChanged();
                tLoadingIndicator.setVisibility(View.GONE);
                trailerRecyclerView.setVisibility(View.VISIBLE);
            }
            else{
                tLoadingIndicator.setVisibility(View.GONE);
                tErrorMessageDisplay.setVisibility(View.VISIBLE);
            }


        }
    }
    private class FetchReviewsTask extends AsyncTask<String,Void,MovieReviewClass[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rLoadingIndicator.setVisibility(View.VISIBLE);
            reviewRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected MovieReviewClass[] doInBackground(String... params) {
            if (params.length == 0)
                return null;
            String type = params[0];
            String id = params[1];
            URL movieRequestUrl = NetworkUtils.buildUrl(type,id);
            String jsonResponse;
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return MovieDBJSONUtils.getMovieReviewsFromJson(DetailsActivity.this, jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieReviewClass[] movieReviewClasses) {
            super.onPostExecute(movieReviewClasses);

            if (movieReviewClasses != null){
                dataR.clear();

                for(MovieReviewClass i : movieReviewClasses)
                    dataR.add(i);
                adapterR.notifyDataSetChanged();
                rLoadingIndicator.setVisibility(View.GONE);
                reviewRecyclerView.setVisibility(View.VISIBLE);
            }
            else {
                rLoadingIndicator.setVisibility(View.GONE);
                rErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }
    }
    private void loadDetails(String id) {
        new FetchDetailTask().execute(id);
    }

    private void showDetailsView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        details.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        details.setVisibility(View.INVISIBLE);
    }
    public class FetchDetailTask extends AsyncTask<String, Void, MovieDetailClass>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        @Override
        protected MovieDetailClass doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String type = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(type);

            String jsonResponse;
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return MovieDBJSONUtils.getMovieDetailsFromJson(DetailsActivity.this, jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final MovieDetailClass movieDetailClass) {
            super.onPostExecute(movieDetailClass);

            favourite = checkForFavourite(movieId);

            if (favourite)
                favouriteBtn.setLiked(true);

            backDrop = (ImageView) findViewById(R.id.movieBackDrop);
            poster = (ImageView) findViewById(R.id.moviePoster);
            releaseDate = (TextView) findViewById(R.id.releaseDate);
            synopsis = (TextView) findViewById(R.id.movieDesc);
            name = (TextView) findViewById(R.id.movieName);
            rating = (TextView) findViewById(R.id.rating);

            if (movieDetailClass != null){
                String dateStr;
                String movieNameStr;
                String synopsisStr;
                String ratingStr;

                Picasso.with(DetailsActivity.this).load(movieDetailClass.getBackDrop()).into(backDrop);
                Picasso.with(DetailsActivity.this).load(movieDetailClass.getPoster()).into(poster);
                String mDate = movieDetailClass.getReleaseDate();
                int date = Integer.parseInt(mDate.substring(8));
                int month = Integer.parseInt(mDate.substring(5,7));
                int year = Integer.parseInt(mDate.substring(0,4));
                dateStr = "" + date + " / " + month + " / " + year;
                releaseDate.setText(dateStr);
                movieNameStr = movieDetailClass.getName() + "  (" + year +")";
                name.setText(movieNameStr);
                synopsisStr = String.valueOf(movieDetailClass.getSynops());
                synopsis.setText(synopsisStr);
                ratingStr = String.valueOf(movieDetailClass.getRating());
                rating.setText(ratingStr);

                getSupportActionBar().setTitle(movieNameStr);

                sqLiteOpenHelper = new MovieDBHelper(DetailsActivity.this);
                sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

                ContentValues cv =new ContentValues();
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID, movieDetailClass.getId());
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER, movieDetailClass.getPoster());
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_BACKDROP, movieDetailClass.getBackDrop());
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_RATING, ratingStr+"");
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_NAME, movieNameStr);
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_SYNOPSIS, synopsisStr);
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_DATE, dateStr);

                try {
                    sqLiteDatabase.beginTransaction();
                    sqLiteDatabase.replace(MovieDBContract.MovieDBEntry.MOVIE_DETAILS_TABLE_NAME, null, cv);
                    sqLiteDatabase.setTransactionSuccessful();
                }catch (SQLException e){
                    //error msg
                }finally {
                    sqLiteDatabase.endTransaction();
                }

                setFavouriteBtnState(favouriteBtn,movieId,movieDetailClass.getPoster());
                showDetailsView();
            }
            else{
                if (checkForData()){
                    try {
                        sqLiteOpenHelper = new MovieDBHelper(DetailsActivity.this);
                        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
                        String[] columns = {MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_BACKDROP,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_NAME,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_DATE,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_SYNOPSIS,
                                MovieDBContract.MovieDBEntry.COLUMN_MOVIE_RATING
                        };
                        Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIE_DETAILS_TABLE_NAME,
                                columns, MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + "=?",
                                new String[]{movieId},null,null,null);
                        cursor.moveToFirst();
                        String posterDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER));
                        String backdropDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_BACKDROP));
                        String movieNameDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_NAME));
                        String dateDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_DATE));
                        String synopsisDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_SYNOPSIS));
                        String ratingDb = cursor.getString(
                                cursor.getColumnIndex(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_RATING));
                        cursor.close();

                        Picasso.with(DetailsActivity.this).load(backdropDb).into(backDrop);
                        Picasso.with(DetailsActivity.this).load(posterDb).into(poster);
                        rating.setText(ratingDb);
                        name.setText(movieNameDb);
                        synopsis.setText(synopsisDb);
                        releaseDate.setText(dateDb);

                        setFavouriteBtnState(favouriteBtn,movieId,posterDb);
                        showDetailsView();
                    }catch (SQLException e){
                        showErrorMessage();
                    }
                }else showErrorMessage();
            }

        }
    }
    public void setFavouriteBtnState(LikeButton favouriteBtn, final String id, final String poster){
        favouriteBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                sqLiteOpenHelper = new MovieDBHelper(DetailsActivity.this);
                sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

                ContentValues cv =new ContentValues();
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID, id);
                cv.put(MovieDBContract.MovieDBEntry.COLUMN_MOVIE_POSTER, poster);

                try {
                    sqLiteDatabase.beginTransaction();
                    sqLiteDatabase.replace(MovieDBContract.MovieDBEntry.MOVIE_FAV_TABLE_NAME, null, cv);
                    sqLiteDatabase.setTransactionSuccessful();
                }catch (SQLException e){
                    //error msg
                }finally {
                    sqLiteDatabase.endTransaction();
                }
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                sqLiteDatabase.delete(MovieDBContract.MovieDBEntry.MOVIE_FAV_TABLE_NAME,
                        MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + "=" + id,null);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkForData(){
        boolean hasData = false;
        sqLiteOpenHelper = new MovieDBHelper(DetailsActivity.this);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(MovieDBContract.MovieDBEntry.MOVIE_DETAILS_TABLE_NAME,
                    null, MovieDBContract.MovieDBEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{movieId},null,null,null);

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
