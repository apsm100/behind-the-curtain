package com.example.btc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentHolder> {

    public final static String modelKey = "commentObject";


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull CommentHolder viewHolder, int position, @NonNull Comment model) {
        FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication();
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getUsername().setText(model.getUserId());
        viewHolder.getComment().setText(model.getData());

        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(model.getDate(), now, TimeUnit.MINUTES);
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


    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_comment, group, false);
        return new CommentHolder(view);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }


}