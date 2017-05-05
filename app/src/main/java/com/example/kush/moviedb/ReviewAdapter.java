package com.example.kush.moviedb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.kush.moviedb.utilities.MovieReviewClass;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by saini on 08-Feb-17.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.OurHolder> {

    public class OurHolder extends RecyclerView.ViewHolder {
        TextView reviewContent;
        TextView reviewAuthor;
        public OurHolder(View itemView) {
            super(itemView);
            reviewContent = (TextView) itemView.findViewById(R.id.reviewContentTV);
            reviewAuthor = (TextView) itemView.findViewById(R.id.reviewAuthorTV);
        }
    }
    Context mContext;
    ArrayList<MovieReviewClass> data;

    public ReviewAdapter(ArrayList<MovieReviewClass> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public ReviewAdapter.OurHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.review_card,parent,false);
        return new ReviewAdapter.OurHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.OurHolder holder, int position) {
        final MovieReviewClass h = data.get(position);
        holder.reviewContent.setText(h.getContent());
        holder.reviewAuthor.setText(" ~ " + h.getAuthor());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

