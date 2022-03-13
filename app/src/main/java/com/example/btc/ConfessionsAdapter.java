package com.example.btc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ConfessionsAdapter extends FirestoreRecyclerAdapter {
    private final Confession[] localDataSet;
    private FirebaseAuthentication firebaseAuthentication;
    private ConfessionHolder confessionHolder;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param localDataSet
     */
    public ConfessionsAdapter(@NonNull FirestoreRecyclerOptions options, Confession[] localDataSet) {
        super(options);
        this.localDataSet = localDataSet;
        this.firebaseAuthentication = new FirebaseAuthentication();
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object model) {
        Confession confession = (Confession) model;
        ConfessionHolder holder = (ConfessionHolder) viewHolder;

        ArrayList<String> heartsList = confession.getHearts();
        String userId = firebaseAuthentication.currentUser.getUid();
        String documentId = confession.getDocumentId();
        FirebaseFirestore db = firebaseAuthentication.db;

        holder.getText().setText(localDataSet[position].getText());
        holder.getUsername().setText(localDataSet[position].getUser().getDisplayName());
        holder.getComment().setText(String.valueOf(localDataSet[position].getComments().size()));
        holder.getHeart().setText(String.valueOf(localDataSet[position].getHearts().size()));
        
//        holder.getHeart().setOnClickListener(view -> {
//            if (heartsList.contains(userId)) {
//                heartsList.remove(userId);
//                db.collection("confessions")
//                        .document(documentId)
//                        .update("hearts", FieldValue.arrayRemove(userId));
//            } else {
//                heartsList.add(userId);
//                db.collection("confessions")
//                        .document(documentId)
//                        .update("hearts", FieldValue.arrayUnion(userId));
//            }
//        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_confession, parent, false);
        return new ConfessionHolder(view);
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


    private void updateHeartIcon(ArrayList<String> heartsList, Button heartButton, String userId) {
        if (heartsList.contains(userId)) {
            heartButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.heart_filled, 0, 0, 0);
        } else {
            heartButton
                    .setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.heart_outline, 0, 0, 0);
        }
    }




}