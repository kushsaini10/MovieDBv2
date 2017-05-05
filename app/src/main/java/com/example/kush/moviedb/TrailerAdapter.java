package com.example.kush.moviedb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kush.moviedb.utilities.MoviePosterClass;
import com.example.kush.moviedb.utilities.MovieTrailerClass;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by saini on 08-Feb-17.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.OurHolder> {

    public class OurHolder extends RecyclerView.ViewHolder {
        ImageView trailer;
        public OurHolder(View itemView) {
            super(itemView);
            trailer = (ImageView) itemView.findViewById(R.id.trailerImage);
        }
    }
    Context mContext;
    ArrayList<MovieTrailerClass> data;

    public TrailerAdapter(ArrayList<MovieTrailerClass> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public TrailerAdapter.OurHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_card,parent,false);
        return new TrailerAdapter.OurHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.OurHolder holder, int position) {
        final MovieTrailerClass h = data.get(position);
        Picasso.with(mContext).load(h.getTrailerImgUrl()).into(holder.trailer);
        final Uri trailerUri = Uri.parse(h.trailerUrl);
        holder.trailer.setOnClickListener(new RecyclerView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,trailerUri);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
