package com.suhendro.movieapps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.suhendro.movieapps.model.Movie;

import java.util.List;

/**
 * Created by Suhendro on 6/27/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ImageViewHolder> {
    private List<Movie> movies;
    private ListItemClickListener mListItemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }

    public MovieAdapter(List<Movie> movieList, ListItemClickListener itemClickListener) {
        movies = movieList;
        mListItemClickListener = itemClickListener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item, parent, shouldAttachToParentImmediately);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPoster;

        public ImageViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
        }

        public void bind(Movie movie) {
            Context context = mPoster.getContext();
            Picasso.with(context)
                    .load(movie.getPosterUrl())
                    .into(mPoster);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListItemClickListener.onListItemClick(position);
        }
    }
}
