package com.example.btc;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static java.util.stream.Collectors.toList;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentHolder> {

    private String originalPosterId;
    private String currentUserId;
    private TextInputLayout textView;
    FirebaseAuthentication firebaseAuthentication;
    FirebaseFirestore db;
    RecyclerView mRecyclerView;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options Firestore options
     */
    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, String originalPosterId, String currentUserId, TextInputLayout view) {
        super(options);
        this.originalPosterId = originalPosterId;
        this.currentUserId = currentUserId;
        this.textView = view;
        firebaseAuthentication = new FirebaseAuthentication();
        db = firebaseAuthentication.db;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getPositionWithId(String id) {
        List<Comment> list = (List<Comment>) getSnapshots();
        List<String> ids = list.stream().map(Comment::getDocumentId).collect(toList());
        return ids.indexOf(id);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
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
        setReply(viewHolder, model);
        setReplyPath(viewHolder, model);
        setSpecialUsernames(model.getUserId(), viewHolder);
        Spannable s = setReplyTag(new SpannableString(model.getData()));
        viewHolder.getComment().setText(s);
        setTime(viewHolder.getDate(), model);
        setTextClickListenerExpand(viewHolder.getComment());
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

    public void setTime(TextView date, Comment model) {
        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(model.getDate(), now, TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minute" + ((timeElapsed == 1) ? "" : "s") + " Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hour" + ((timeElapsed / 60 == 1) ? "" : "s") + " Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Day" + ((timeElapsed / 1440 == 1) ? "" : "s") + " Ago";

        if (timeElapsed < 60){
            if(timeElapsed == 0) {
                date.setText(R.string.date_recent_post);
            } else {
                date.setText(timeLessThan60minutes);
            }
        }else if (timeElapsed < 1440){
            date.setText(lessThan24Hours);
        }else {
            date.setText(MoreThan24Hours);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setReplyPath(CommentHolder viewHolder, Comment model) {
       Button replyPath = viewHolder.getReplyPath();
       replyPath.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.N)
           @Override
           public void onClick(View view) {
               int position = getPositionWithId(model.getReplyDocumentId());
               mRecyclerView.smoothScrollToPosition(position);
               CommentHolder holder = (CommentHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
               if (holder != null) {
                   animateColor(holder.getLayout(), Color.parseColor("#1C1B1F"),  Color.parseColor("#00D0BCFF"));
                   return;
               }
               RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                   @Override
                   public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                       if (newState == SCROLL_STATE_IDLE) {
                           CommentHolder holder = (CommentHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                           animateColor(holder.getLayout(), Color.parseColor("#1C1B1F"), Color.parseColor("#00D0BCFF"));
                           recyclerView.removeOnScrollListener(this);
                       }
                   }
               };
               mRecyclerView.addOnScrollListener(onScrollListener);
           }
       });
        replyPath.setVisibility(View.GONE);
       if (model.getReplyDocumentId() != null) {
           System.out.println(model.getReplyDocumentId());
           replyPath.setVisibility(View.VISIBLE);
       }
    }

    public void animateColor(ConstraintLayout layout, int from, int to) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(from, to);
        anim.setEvaluator(new ArgbEvaluator());

        anim.addUpdateListener(valueAnimator -> layout.setBackgroundColor((Integer)valueAnimator.getAnimatedValue()));
        anim.setDuration(1000);
        anim.start();
    }

    public Spannable setReplyTag(Spannable s) {
        String[] str = String.valueOf(s).split("\\s+");
        int size;
        if (str[0].length() > 0) {
            size = getReplyTagSize(str[0]);
        } else {
            return s;
        }
        if (str[0].length() == size) {
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#D0BCFF")), 0, size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    public int getReplyTagSize(String str) {
        if (str.charAt(0) == '@') {
            if (str.contains("BCIT#")) {
                return 12;
            } else if (str.contains("UBC#") || str.contains("SFU#")) {
                return 11;
            }
        }
        return 0;
    }

    public void setReply(CommentHolder viewHolder, Comment model) {
        Button replyButton = viewHolder.getReply();
        String username = model.getUserId();
        EditText editText = textView.getEditText();
        replyButton.setOnClickListener(view -> {
            editText.setText( "@" + username + " ");
            textView.setHint("Reply");
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
            textView.setTag(model.getDocumentId());
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