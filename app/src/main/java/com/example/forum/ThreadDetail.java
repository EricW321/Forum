package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ThreadDetail extends AppCompatActivity implements CommentsAdapter.OnCommentClickListener {
    public String title;
    public String thread_content;
    public String thread_time;
    public String user_name;
    public String parent_comment_name;
    public int parent_comment_id;

    private List<CommentDetail> commentsList;
    private CommentsAdapter adapter;

    TextView title_tv;
    TextView thread_content_tv;
    TextView thread_user_tv;

    TextView thread_time_tv;

    RecyclerView recyclerView;

    Button comment_btn;

    Button refresh_btn;

    EditText comment_edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);
        parent_comment_id= -1;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("userId", "defaultStudentId");
        int user_id = Integer.parseInt(studentId);

        int thread_id=getIntent().getIntExtra("thread_id",1);
        recyclerView=findViewById(R.id.comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentsList=new ArrayList<>();



        fetchThreadDetail(thread_id);
        fetchComments(thread_id);

        adapter = new CommentsAdapter(commentsList,ThreadDetail.this,this);
        recyclerView.setAdapter(adapter);

        comment_btn=findViewById(R.id.button_comment);
        comment_edittext=findViewById(R.id.editText_comment);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_content=comment_edittext.getText().toString();
                postComment(user_id,thread_id,parent_comment_id,comment_content);
                parent_comment_id=-1;
            }
        });

        thread_content_tv=findViewById(R.id.thread_content);

        thread_content_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_edittext.setHint("Add a comment...");
                comment_edittext.setText("");
                parent_comment_id=-1;
            }
        });

        refresh_btn=findViewById(R.id.button_refresh);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsList.clear();
                fetchComments(thread_id);
            }
        });
    }



    public void reorderComments() {
        Map<Integer, List<CommentDetail>> childrenMap = new HashMap<>();

        // Build the map for parent-child relationships
        for (CommentDetail comment : commentsList) {
            int parentId = comment.getParent_comment_id();
            List<CommentDetail> children = childrenMap.get(parentId);
            if (children == null) {
                children = new ArrayList<>();
                childrenMap.put(parentId, children);
            }
            children.add(comment);
        }

        List<CommentDetail> orderedComments = new ArrayList<>();
        for (CommentDetail comment : commentsList) {
            if (comment.getParent_comment_id() == -1) {
                addCommentAndChildren(comment, orderedComments, childrenMap);
            }
        }

        commentsList.clear();
        commentsList.addAll(orderedComments);
    }

    private void addCommentAndChildren(CommentDetail comment, List<CommentDetail> orderedComments, Map<Integer, List<CommentDetail>> childrenMap) {
        orderedComments.add(comment);
        List<CommentDetail> children = childrenMap.get(comment.getComment_id());
        if (children != null) {
            for (CommentDetail child : children) {
                addCommentAndChildren(child, orderedComments, childrenMap);
            }
        }
    }

    @Override
    public void onCommentClick(int parentCommentId, String parentCommentName){
        this.parent_comment_id = parentCommentId;
        this.parent_comment_name=parentCommentName;
        comment_edittext.setHint("reply @"+parent_comment_name);
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

                                title_tv.setText(" " + title);
                                thread_content_tv.setText(thread_content);
                                thread_user_tv.setText("By " + user_name + " ");
                                String formattedTime = convertISOTimeToSimpleFormat(thread_time);
                                if (formattedTime != null) {
                                    thread_time_tv.setText(" " + formattedTime);
                                } else {
                                    thread_time_tv.setText(thread_time);
                                }
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
                            reorderComments();
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

    private void postComment(int user_id, int thread_id, int parent_comment_id, String comment_content){
        String url="https://forum-node.vercel.app/sendComment";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", user_id);
            jsonBody.put("thread_id", thread_id);
            if(parent_comment_id<0){
                jsonBody.put("parent_comment_id", null);
            }
            else {
                jsonBody.put("parent_comment_id",parent_comment_id);
            }

            jsonBody.put("comment_content", comment_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody, response -> {
            Toast.makeText(ThreadDetail.this, "new comment submit successfully", Toast.LENGTH_SHORT).show();
            comment_edittext.setText("");
            commentsList.clear();
            fetchComments(thread_id);
        },error -> {
            Toast.makeText(ThreadDetail.this, "Submit failed", Toast.LENGTH_SHORT).show();
            Log.e("Error.Response", "Error: ", error);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private String convertISOTimeToSimpleFormat(String isoTime) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = isoFormat.parse(isoTime);
            return simpleFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}