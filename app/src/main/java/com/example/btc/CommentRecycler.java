package com.example.btc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CommentRecycler extends RecyclerView.Adapter<CommentRecycler.ViewHolder> {

    private ArrayList<Comment> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * This template comes with a TextView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;
        private final TextView comment;
        private final TextView date;

        public ViewHolder(View view) {
            super(view);

            this.username = view.findViewById(R.id.textView_itemcomment_displayname);
            this.comment = view.findViewById(R.id.textView_itemcomment_text);
            this.date = view.findViewById(R.id.textView_itemcomment_time);
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getComment() {
            return comment;
        }

        public TextView getDate() {
            return date;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public CommentRecycler(ArrayList<Comment> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_comment, viewGroup, false); //error here should be expected, this is a template

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        System.out.println(localDataSet.get(position).getUserId());
        System.out.println(localDataSet.get(position).getData());
        viewHolder.getUsername().setText(localDataSet.get(position).getUserId());
        viewHolder.getComment().setText(localDataSet.get(position).getData());

        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(localDataSet.get(position).getDate(), now, TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minutes Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hours Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Days Ago";

        if (timeElapsed < 60){
            viewHolder.getDate().setText(timeLessThan60minutes);
        }else if (timeElapsed < 1440){
            viewHolder.getDate().setText(lessThan24Hours);
        }else {
            viewHolder.getDate().setText(MoreThan24Hours);
        }

    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}