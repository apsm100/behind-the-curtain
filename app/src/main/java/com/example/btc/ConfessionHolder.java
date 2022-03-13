package com.example.btc;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Provide a reference to the type of views that you are using
 * This template comes with a TextView
 */
public class ConfessionHolder extends RecyclerView.ViewHolder {

    private final TextView username;
    private final TextView text;
    private final Button comment;
    private final Button heart;
    private final Button more;


    public ConfessionHolder(@NonNull View view) {
        super(view);

        this.username = view.findViewById(R.id.textView_itemconfession_username);
        this.text = view.findViewById(R.id.textView_itemconfession_text);
        this.comment = view.findViewById(R.id.Button_itemconfession_comment);
        this.heart = view.findViewById(R.id.Button_itemconfession_heart);
        this.more = view.findViewById(R.id.Button_itemconfession_more);
    }

    public TextView getUsername() {
        return username;
    }

    public TextView getText() {
        return text;
    }

    public Button getComment() {
        return comment;
    }

    public Button getHeart() {
        return heart;
    }

    public Button getMore() {
        return more;
    }
}