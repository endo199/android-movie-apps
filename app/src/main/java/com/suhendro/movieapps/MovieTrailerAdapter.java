package com.suhendro.movieapps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suhendro.movieapps.model.Trailer;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by Suhendro on 6/28/2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {
    private List<Trailer> mTrailer;
    private TextView mTrailerTitle;

    private TrailerOnClickListener mTrailerOnClickListener;

    public interface TrailerOnClickListener {
        void onTrailerClick(int index);
    }

    public MovieTrailerAdapter(List<Trailer> trailers, TrailerOnClickListener listener) {
        mTrailer = trailers;
        mTrailerOnClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_list_item, parent, shouldAttachToParentImmediately);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(mTrailer.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrailer.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);
        }

        public void bind(Trailer trailer) {
            mTrailerTitle.setText(trailer.getName());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mTrailerOnClickListener.onTrailerClick(position);
        }
    }

}
