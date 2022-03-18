package com.example.btc;

import android.content.Intent;
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


public class ConfessionsAdapter extends FirestoreRecyclerAdapter<Confession, ConfessionHolder> {

    public final static String modelKey = "postObject";
    FirebaseAuthentication firebaseAuthentication;
    FirebaseFirestore db;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options Firestore options
     */
    public ConfessionsAdapter(@NonNull FirestoreRecyclerOptions<Confession> options) {
        super(options);
        firebaseAuthentication = new FirebaseAuthentication();
        db = firebaseAuthentication.db;
    }

    @Override
    protected void onBindViewHolder(@NonNull ConfessionHolder viewHolder, int position, @NonNull Confession model) {
        Button heartButton = viewHolder.getHeart();
        viewHolder.getUsername().setText(model.getDisplayName());
        viewHolder.getText().setText(model.getText());
        viewHolder.getComment().setText(String.valueOf(model.getComments()));
        heartButton.setText(String.valueOf(model.getHearts().size()));
        ArrayList<String> heartsList = model.getHearts();
        String userId = firebaseAuthentication.currentUser.getUid();
        String documentId = model.getDocumentId();

        updateHeartIcon(heartsList, heartButton, userId);
        setHeartClickListener(viewHolder.getHeart(), documentId, heartsList, model, userId);
        setCommentClickListener(model, viewHolder);
        setTextClickListenerExpand(viewHolder.getText());
    }

    public void setTextClickListenerExpand(TextView comment) {
        comment.setOnClickListener(view -> {
            if (comment.getMaxLines() == 50){
                comment.setMaxLines(4);
                comment.setEllipsize(TextUtils.TruncateAt.END);
            }else {
                comment.setMaxLines(50);
                comment.setEllipsize(null);
            }
        });

    }

    private void setCommentClickListener(Confession model, ConfessionHolder viewHolder) {
        viewHolder.getComment().setOnClickListener(view -> {
            Intent intent = new Intent (viewHolder.itemView.getContext(), CommentsActivity.class);
            intent.putExtra(modelKey, model);
            viewHolder.itemView.getContext().startActivity(intent);
        });
    }

    private void setHeartClickListener(Button heart, String documentId, ArrayList<String> heartsList,
                                       Confession model, String userId) {
        heart.setOnClickListener(view -> {
            if (heartsList.contains(userId)) {
                model.removeHeart(userId);
                db.collection("confessions")
                        .document(documentId)
                        .update("hearts", FieldValue.arrayRemove(userId));
            } else {
                model.addHeart(userId);
                db.collection("confessions")
                        .document(documentId)
                        .update("hearts", FieldValue.arrayUnion(userId));
            }
            db.collection("confessions")
                    .document(documentId)
                    .update("popularityIndex", model.getPopularityIndex());
        });
    }

    private void updateHeartIcon(ArrayList<String> heartsList, Button heartButton, String userId) {
        if (heartsList.contains(userId)) {
            ((MaterialButton) heartButton).setIconResource(R.drawable.ic_heart_filled);
        } else {
            ((MaterialButton) heartButton).setIconResource(R.drawable.ic_heart_outline);
        }
    }

    @NonNull
    @Override
    public ConfessionHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_confession, group, false);
        return new ConfessionHolder(view);
    }

}