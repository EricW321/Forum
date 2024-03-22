package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreadDetail extends AppCompatActivity {
    public String title;
    public String thread_content;
    public String thread_time;
    public String user_name;

    private List<CommentDetail> commentsList;
    private CommentsAdapter adapter;

    TextView title_tv;
    TextView thread_content_tv;
    TextView thread_user_tv;

    TextView thread_time_tv;

    RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);

        int thread_id=getIntent().getIntExtra("thread_id",1);
        recyclerView=findViewById(R.id.comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentsList=new ArrayList<>();



        fetchThreadDetail(thread_id);
        fetchComments(thread_id);

        adapter = new CommentsAdapter(commentsList,ThreadDetail.this);
        recyclerView.setAdapter(adapter);
    }

    private void fetchThreadDetail(int thread_id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://forum-node.vercel.app/threadDetail?thread_id=" + thread_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            if (jsonArray.length()>0){
                                JSONObject obj = jsonArray.getJSONObject(0);
                                title=obj.getString("title");
                                user_name=obj.getString("user_name");
                                thread_time=obj.getString("thread_time");
                                thread_content=obj.getString("thread_content");

                                title_tv=findViewById(R.id.thread_title);
                                thread_content_tv=findViewById(R.id.thread_content);
                                thread_user_tv=findViewById(R.id.thread_user);
                                thread_time_tv=findViewById(R.id.thread_time);

                                title_tv.setText(title);
                                thread_content_tv.setText(thread_content);
                                thread_user_tv.setText(user_name);
                                thread_time_tv.setText(thread_time);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void fetchComments(int thread_id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://forum-node.vercel.app/commentsList?thread_id=" + thread_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String user_name = obj.getString("user_name");
                                int user_id = obj.getInt("user_id");
                                int comment_id = obj.getInt("comment_id");
                                String comment_content=obj.getString("comment_content");
                                int parent_comment_id;
                                if(obj.isNull("parent_comment_id")){
                                    parent_comment_id=-1;
                                }
                                else{
                                    parent_comment_id=obj.getInt("parent_comment_id");
                                }
                                String parent_user_name;
                                if(obj.isNull("parent_user_name")){
                                    parent_user_name="";
                                }
                                else{
                                    parent_user_name=obj.getString("parent_user_name");
                                }
                                String comment_time = obj.getString("comment_time");

                                CommentDetail comment = new CommentDetail(user_name,comment_time,comment_content,parent_user_name,user_id,comment_id,parent_comment_id);
                                commentsList.add(comment);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

}