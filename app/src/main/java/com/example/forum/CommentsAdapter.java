package com.example.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<CommentDetail> commentList;
    Context context;

    public CommentsAdapter(List<CommentDetail> commentList, Context context){
        this.commentList=commentList;
        this.context=context;
    }
    @NonNull
    @Override
    public CommentsAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentViewHolder holder, int position) {
        CommentDetail comment=commentList.get(position);
        holder.content_tv.setText(comment.getComment_content());
        holder.time_tv.setText(comment.getComment_time());
        if(comment.getParent_user_name().equals("")){
            holder.reply_tv.setText("");
        }
        else {
            holder.reply_tv.setText("reply @"+comment.getParent_user_name());
        }

        holder.username_tv.setText(comment.getUser_name());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView username_tv, time_tv, reply_tv, content_tv;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username_tv=itemView.findViewById(R.id.comment_username);
            time_tv=itemView.findViewById(R.id.comment_time);
            reply_tv=itemView.findViewById(R.id.comment_reply);
            content_tv=itemView.findViewById(R.id.comment_content);


        }
    }
}


