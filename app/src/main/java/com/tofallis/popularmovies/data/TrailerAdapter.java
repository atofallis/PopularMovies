package com.tofallis.popularmovies.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tofallis.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private Context mContext;
    // store list of Movies from the api query.
    private List<Trailer> mTrailers = new ArrayList<>();

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        mTrailers = trailers;
    }

    public TrailerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.trailer, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, final int position) {
        holder.mTrailerName.setText(mTrailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    /**
     * Creates a new ImageView for each item referenced by the adapter
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mTrailerIcon;
        public TextView mTrailerName;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrailerName = itemView.findViewById(R.id.trailerTitle);
            mTrailerIcon = itemView.findViewById(R.id.trailerIcon);
            mTrailerIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Trailer t = getTrailers().get(position);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v=" + t.getKey())));
            }
        }
    }
}
