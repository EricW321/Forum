package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ThreadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ThreadAdapter adapter;
    private List<ThreadItem> threadList;

    FloatingActionButton newThread;
    Button refresh, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        TextView categoryHeader = findViewById(R.id.categoryHeader);

        recyclerView = findViewById(R.id.threadsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        threadList = new ArrayList<>();
        adapter = new ThreadAdapter(threadList, ThreadActivity.this);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        final String userIdString = sharedPreferences.getString("userId", "defaultUserId");
        int userId = -1;
        try {
            userId = Integer.parseInt(userIdString);
        } catch (NumberFormatException e) {
            Log.e("ThreadActivity", "Failed to parse userId as int", e);
        }

        int categoryId = getIntent().getIntExtra("category_id", 0);
        String displayMode = getIntent().getStringExtra("display_mode");
        if ("my_threads".equals(displayMode)) {
            categoryHeader.setText("My Threads");
            fetchMyThreads(userId);
        } else {
            String categoryName = getIntent().getStringExtra("category_name");
            categoryHeader.setText(categoryName + " Category");
            fetchThreads(categoryId);
        }

        newThread = findViewById(R.id.newThreadButton);
        newThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewThread.class);
                if (!"my_threads".equals(displayMode)) {
                    intent.putExtra("category_id", categoryId);
                }
                startActivity(intent);
            }
        });

        refresh = findViewById(R.id.refreshButton);
        int finalUserId = userId;
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadList.clear();
                if ("my_threads".equals(displayMode)) {
                    fetchMyThreads(finalUserId);
                } else {
                    fetchThreads(categoryId);
                }
            }
        });

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void fetchThreads(int categoryId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://forum-node.vercel.app/threadDetailsByCategory?category_id=" + categoryId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String title = obj.getString("title");
                                String userName = obj.getString("user_name");
                                String threadTime = convertISOTimeToSimpleFormat(obj.getString("thread_time"));
                                int threadID=obj.getInt("thread_id");

                                ThreadItem thread = new ThreadItem(title, userName, threadTime,threadID);
                                threadList.add(thread);
                            }
                            Log.d("ThreadActivity", "Number of items in threadList: " + threadList.size());
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

    private void fetchMyThreads(int userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://forum-node.vercel.app/myThreads?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String title = obj.getString("title");
                                String userName = obj.getString("user_name");
                                String threadTime = obj.getString("thread_time");
                                int thread_id = obj.getInt("thread_id");

                                ThreadItem thread = new ThreadItem(title, userName, threadTime, thread_id);
                                threadList.add(thread);
                            }
                            Log.d("ThreadActivity", "Number of items in threadList: " + threadList.size());
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
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

