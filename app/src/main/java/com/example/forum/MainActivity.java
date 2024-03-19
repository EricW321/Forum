package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Message> messageList;
    private MessageAdapter adapter;
    RequestQueue requestQueue;

    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("userId", "defaultStudentId");
        String username = sharedPreferences.getString("username", "defaultUserName");
        TextView welcomText = findViewById(R.id.welcometext);
        String wt="Welcome "+username;
        welcomText.setText(wt);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);
        logOut=findViewById(R.id.button);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_login=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_login);
            }
        });
        fetchData();


//        Log.d("testt",studentId);
//        Log.d("testt",username);
    }

    private void fetchData() {
        String url = "https://forum-node.vercel.app/category";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Parse the JSON response and add each message to the list
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String content = jsonObject.getString("category_name");
                            messageList.add(new Message(content));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // Notify the adapter that the data set has changed to refresh the RecyclerView
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    // Log or display the error
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.e("FetchData", "Error: ", error);
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }
}