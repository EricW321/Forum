package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewThread extends AppCompatActivity {

    Button cancel;
    Button submit;
    EditText newTitle;
    EditText newContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);

        newTitle=findViewById(R.id.editTextNewTitle);
        newContent=findViewById(R.id.editTextNewContent);
        submit =findViewById(R.id.buttonSubmitThread);


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("userId", "defaultStudentId");
        int user_id = Integer.parseInt(studentId);
        //int category_id=sharedPreferences.getInt("category_id",1);
        int id=getIntent().getIntExtra("category_id",0);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int category_id=sharedPreferences.getInt("category_id",1);
                String new_title=newTitle.getText().toString();
                String new_content=newContent.getText().toString();
                postThread(user_id,id,new_title,new_content);
                finish();
            }
        });



        cancel = findViewById(R.id.buttonCancelThread);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void postThread(int user_id, int category_id, String title, String thread_content){
        String url="https://forum-node.vercel.app/sendThread";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", user_id);
            jsonBody.put("category_id", category_id);
            jsonBody.put("title", title);
            jsonBody.put("thread_content", thread_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody,response -> {
            Toast.makeText(NewThread.this, "new thread submit successfully", Toast.LENGTH_SHORT).show();
            finish();
        },error -> {
            Toast.makeText(NewThread.this, "Submit failed", Toast.LENGTH_SHORT).show();
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
}