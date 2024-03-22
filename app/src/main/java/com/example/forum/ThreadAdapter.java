package com.example.forum;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private List<ThreadItem> threadList;
    Context context;

    public ThreadAdapter(List<ThreadItem> threadList, Context context) {

        this.threadList = threadList;
        this.context=context;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
        return new ThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        ThreadItem thread = threadList.get(position);
        holder.threadTitle.setText(thread.getTitle());
        holder.threadUser.setText(thread.getUserName());
        holder.threadDate.setText(thread.getThreadTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int thread_id=thread.getThread_id();
                Intent intent=new Intent(context.getApplicationContext(), ThreadDetail.class);
                intent.putExtra("thread_id",thread_id);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return threadList.size();
    }

    public static class ThreadViewHolder extends RecyclerView.ViewHolder {
        TextView threadTitle, threadUser, threadDate;

        public ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            threadTitle = itemView.findViewById(R.id.threadTitle);
            threadUser = itemView.findViewById(R.id.threadUser);
            threadDate = itemView.findViewById(R.id.threadDate);
        }
    }
}

