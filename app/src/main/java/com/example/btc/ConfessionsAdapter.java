package com.example.btc;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ConfessionsAdapter extends FirestoreRecyclerAdapter<Confession, ConfessionHolder> {

    public final static String modelKey = "postObject";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ConfessionsAdapter(@NonNull FirestoreRecyclerOptions<Confession> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConfessionHolder viewHolder, int position, @NonNull Confession model) {
        FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication();
        Button heartButton = viewHolder.getHeart();
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getUsername().setText(model.getDisplayName());
        viewHolder.getText().setText(model.getText());
        viewHolder.getComment().setText(String.valueOf(model.getComments()));
        heartButton.setText(String.valueOf(model.getHearts().size()));

        ArrayList<String> heartsList = model.getHearts();
        String userId = firebaseAuthentication.currentUser.getUid();
        String documentId = model.getDocumentId();
        FirebaseFirestore db = firebaseAuthentication.db;

        updateHeartIcon(heartsList, heartButton, userId);

        viewHolder.getHeart().setOnClickListener(view -> {
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


        viewHolder.getComment().setOnClickListener(view -> {
            Intent intent = new Intent (viewHolder.itemView.getContext(), CommentsActivity.class);
            intent.putExtra(modelKey, model);
            viewHolder.itemView.getContext().startActivity(intent);
        });

        viewHolder.itemView.findViewById(R.id.cardview_item_confession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.getText().getMaxLines() == 20){
                    viewHolder.getText().setMaxLines(4);
                }else {
                    viewHolder.getText().setMaxLines(20);
                }
            }
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