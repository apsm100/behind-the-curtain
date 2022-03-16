package com.example.btc;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentHolder> {

    private String originalPosterId;
    private String currentUserId;
    FirebaseAuthentication firebaseAuthentication;
    FirebaseFirestore db;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options Firestore options
     */
    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, String originalPosterId, String currentUserId) {
        super(options);
        this.originalPosterId = originalPosterId;
        this.currentUserId = currentUserId;
        firebaseAuthentication = new FirebaseAuthentication();
        db = firebaseAuthentication.db;
    }

    private void updateVoteButton(ArrayList<String> list1, ArrayList<String> list2, Button button1,
                                  Button button2, String userId, int activeColor1, int activeColor2,
                                  int inactiveColor) {
        if (list1.contains(userId)) {
            ((MaterialButton) button1).setIconTintResource(activeColor1);
            ((MaterialButton) button2).setIconTintResource(inactiveColor);
        } else  if (!list1.contains(userId)) {
            ((MaterialButton) button1).setIconTintResource(inactiveColor);
            if (list2.contains(userId)) {
                ((MaterialButton) button2).setIconTintResource(activeColor2);
            }
        }
        if (list2.contains(userId)) {
            ((MaterialButton) button2).setIconTintResource(activeColor2);
            ((MaterialButton) button1).setIconTintResource(inactiveColor);
        } else  if (!list2.contains(userId)) {
            ((MaterialButton) button2).setIconTintResource(inactiveColor);
            if (list1.contains(userId)) {
                ((MaterialButton) button1).setIconTintResource(activeColor1);
            }
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder viewHolder, int position, @NonNull Comment model) {
        String userUid = firebaseAuthentication.currentUser.getUid();

        setVoting(model, viewHolder, userUid, db);

        setSpecialUsernames(model.getUserId(), viewHolder);
        viewHolder.getComment().setText(model.getData());

        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(model.getDate(), now, TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minute" + ((timeElapsed == 1) ? "" : "s") + " Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hour" + ((timeElapsed / 60 == 1) ? "" : "s") + " Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Day" + ((timeElapsed / 1440 == 1) ? "" : "s") + " Ago";

        if (timeElapsed < 60){
            if(timeElapsed == 0) {
                viewHolder.getDate().setText(R.string.date_recent_post);
            } else {
                viewHolder.getDate().setText(timeLessThan60minutes);
            }
        }else if (timeElapsed < 1440){
            viewHolder.getDate().setText(lessThan24Hours);
        }else {
            viewHolder.getDate().setText(MoreThan24Hours);
        }

        viewHolder.itemView.findViewById(R.id.textView_itemcomment_text).setOnClickListener(view -> {
            if (viewHolder.getComment().getMaxLines() == 50){
                viewHolder.getComment().setMaxLines(4);
                viewHolder.getComment().setEllipsize(TextUtils.TruncateAt.END);
            }else {
                viewHolder.getComment().setMaxLines(50);
                viewHolder.getComment().setEllipsize(null);
            }
        });

    }

    public void setVoting(Comment model, CommentHolder viewHolder, String userUid, FirebaseFirestore db) {
        String documentId = model.getDocumentId();
        String commentDocumentId = model.getCommentDocumentId();

        Button upVoteButton =  viewHolder.getUpVote();
        Button downVoteButton =  viewHolder.getDownVote();
        TextView voteCount = viewHolder.getVoteCount();

        ArrayList<String> upVoteIds = model.getUpVoteIds();
        ArrayList<String> downVoteIds = model.getDownVoteIds();

        voteCount.setText(String.valueOf(model.getVoteCount()));

        updateVoteButton(upVoteIds, downVoteIds, upVoteButton, downVoteButton, userUid,
                R.color.upVoteActive, R.color.downVoteActive, R.color.voteInactive);
        upVoteButton.setOnClickListener(view -> {
            if (model.addUpVote(userUid)) {
                db.collection("confessions")
                        .document(commentDocumentId).collection("comments").document(documentId)
                        .update("upVoteIds", FieldValue.arrayUnion(userUid));
                if (model.removeDownVote(userUid)) {
                    db.collection("confessions")
                            .document(commentDocumentId).collection("comments").document(documentId)
                            .update("downVoteIds", FieldValue.arrayRemove(userUid));
                }
            } else {
                model.removeUpVote(userUid);
                db.collection("confessions")
                        .document(commentDocumentId).collection("comments").document(documentId)
                        .update("upVoteIds", FieldValue.arrayRemove(userUid));
            }
            db.collection("confessions")
                    .document(commentDocumentId).collection("comments").document(documentId)
                    .update("voteCount", model.getVoteCount());
        });

        downVoteButton.setOnClickListener(view -> {
            if (model.addDownVote(userUid)) {
                db.collection("confessions")
                        .document(commentDocumentId).collection("comments").document(documentId)
                        .update("downVoteIds", FieldValue.arrayUnion(userUid));
                if (model.removeUpVote(userUid)) {
                    db.collection("confessions")
                            .document(commentDocumentId).collection("comments").document(documentId)
                            .update("upVoteIds", FieldValue.arrayRemove(userUid));
                }
            } else {
                model.removeDownVote(userUid);
                db.collection("confessions")
                        .document(commentDocumentId).collection("comments").document(documentId)
                        .update("downVoteIds", FieldValue.arrayRemove(userUid));
            }
            db.collection("confessions")
                    .document(commentDocumentId).collection("comments").document(documentId)
                    .update("voteCount", model.getVoteCount());
        });

    }

    public void setSpecialUsernames(String userId, CommentHolder viewHolder) {
        boolean originalPost = originalPosterId.equals(userId);
        boolean currentUser = currentUserId.equals(userId);
        if ( originalPost && currentUser) {
            viewHolder.getUsername().setText(R.string.comments_you_op);
        } else if (currentUser) {
            viewHolder.getUsername().setText(R.string.comments_you);
        } else if (originalPost){
            viewHolder.getUsername().setText(R.string.comments_op);
        } else {
            viewHolder.getUsername().setText(userId);
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