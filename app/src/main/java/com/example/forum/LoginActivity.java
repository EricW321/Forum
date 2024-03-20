package com.example.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        user_name=username;
        // Current timestamp in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Localhost test USE
        //String url = "http://10.0.2.2:3000/login";

        //Global connection
        String url = "https://forum-node.vercel.app/login";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("currentTimestamp", currentTimeMillis);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            Log.d("test",response.getString("userId"));
                            storeUserDetailsAndNavigate(response);
                        }if (!success) {

                            String message = response.getString("message");
                            if ("Verification required. A code has been sent to your email.".equals(message)) {
                                // Extract the email from the response if you need it for verification
                                String email = response.optString("email", username); // fallback to username if email isn't provided

                                // Navigate to VerificationCodeActivity
                                Intent intent = new Intent(LoginActivity.this, VerificationCodeActivity.class);
                                intent.putExtra("email", email); // Pass email to the VerificationCodeActivity
                                intent.putExtra("username", username); // Pass the username to the VerificationCodeActivity
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            String message = response.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> Toast.makeText(LoginActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show());

        queue.add(jsonObjectRequest);
    }



    private void storeUserDetailsAndNavigate(JSONObject response) throws JSONException {
        // Store JWT Token and User ID
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", response.getString("token"));
        editor.putString("userId", response.getString("userId"));
        editor.putString("username", user_name);
        //Log.d("test",userId);
        editor.apply();

        // Navigate to MainActivity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
