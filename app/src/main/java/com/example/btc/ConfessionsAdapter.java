package com.example.btc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.FirestoreGrpc;

import java.util.ArrayList;
import java.util.List;


public class ConfessionsAdapter extends RecyclerView.Adapter<ConfessionsAdapter.ViewHolder> {

    private final Confession[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * This template comes with a TextView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView text;
        private final Button comment;
        private final Button heart;
        private final Button more;

        public ViewHolder(View view) {
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

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public ConfessionsAdapter(Confession[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_confession, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication();

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getUsername().setText(localDataSet[position].getUser().getDisplayName());
        viewHolder.getText().setText(localDataSet[position].getText());
        viewHolder.getComment().setText(String.valueOf(localDataSet[position].getComments().size()));
        viewHolder.getHeart().setText(String.valueOf(localDataSet[position].getHearts().size()));

        ArrayList<String> heartsList = localDataSet[position].getHearts();
        String userId = firebaseAuthentication.currentUser.getUid();
        String documentId = localDataSet[position].getDocumentId();
        FirebaseFirestore db = firebaseAuthentication.db;

        updateHeartIcon(heartsList, viewHolder, userId);

        viewHolder.getHeart().setOnClickListener(view -> {

            if (heartsList.contains(userId)) {
                db.collection("confessions")
                        .document(documentId)
                        .update("hearts", FieldValue.arrayRemove(userId))
                        .addOnCompleteListener(task -> updateHeartIcon(heartsList, viewHolder, userId));
            } else {
                db.collection("confessions")
                        .document(documentId)
                        .update("hearts", FieldValue.arrayUnion(userId))
                        .addOnCompleteListener(task -> updateHeartIcon(heartsList, viewHolder, userId));
            }
        });
    }


    private void updateHeartIcon(ArrayList<String> heartsList, ViewHolder viewHolder, String userId) {
        if (heartsList.contains(userId)) {
            viewHolder.getHeart().setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.heart_outline, 0, 0, 0);
        } else {
            viewHolder.getHeart()
                    .setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.heart_filled, 0, 0, 0);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public long getItemId(int position) {
        String documentID = localDataSet[position].getDocumentId();
        String digits = documentID.replaceAll("[^0-9]", "");

        return (long) Integer.parseInt(digits);
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}