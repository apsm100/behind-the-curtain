package com.example.btc;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentHolder extends RecyclerView.ViewHolder {
    private final TextView username;
    private final TextView comment;
    private final TextView date;
    private final TextView voteCount;
    private final Button upVote;
    private final Button downVote;
    private final Button reply;
    private final Button replyPath;

    public TextView getVoteCount() {
        return voteCount;
    }

    public Button getUpVote() {
        return upVote;
    }

    public Button getReplyPath() {
        return replyPath;
    }

    public Button getDownVote() {
        return downVote;
    }

    public CommentHolder(@NonNull View view) {
        super(view);

        this.username = view.findViewById(R.id.textView_itemcomment_displayname);
        this.comment = view.findViewById(R.id.textView_itemcomment_text);
        this.date = view.findViewById(R.id.textView_itemcomment_time);

        this.voteCount = view.findViewById(R.id.textView_comment_vote_count);
        this.upVote = view.findViewById(R.id.button_comment_upvote);
        this.downVote = view.findViewById(R.id.button_comment_downvote);
        this.reply = view.findViewById(R.id.button_comment_reply);
        this.replyPath = view.findViewById(R.id.button_comment_reply_path);

    }

    public Button getReply() {
        return reply;
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
