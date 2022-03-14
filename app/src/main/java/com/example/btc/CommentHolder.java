package com.example.btc;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentHolder extends RecyclerView.ViewHolder {
    private final TextView username;
    private final TextView comment;
    private final TextView date;

    public CommentHolder(@NonNull View view) {
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
