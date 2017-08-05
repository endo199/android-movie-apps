package com.suhendro.movieapps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suhendro.movieapps.model.Review;

import java.util.List;

/**
 * Created by Suhendro on 8/5/2017.
 */

class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {
    private List<Review> mReviews;
    private TextView mDescription;
    private TextView mAuthor;

    public MovieReviewAdapter(List<Review> reviews) {
        this.mReviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean shouldAttachToParentImmediately = false;

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, shouldAttachToParentImmediately);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ReviewViewHolder extends ViewHolder {

        public ReviewViewHolder(View view) {
            super(view);

            mDescription = (TextView) view.findViewById(R.id.tv_review_description);
            mAuthor = (TextView) view.findViewById(R.id.tv_review_author);
        }

        public void bind(Review review) {
            mDescription.setText(review.getContent());
            mAuthor.setText(review.getAuthor());
        }
    }
}
