package com.example.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationCodeActivity extends AppCompatActivity {

    private EditText editTextVerificationCode;
    private Button buttonSend, buttonVerify;
    private String username,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonSend = findViewById(R.id.buttonSend);
        buttonVerify = findViewById(R.id.buttonVerify);

        // Get the username passed from LoginActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email"); // Assume you pass email as well

        // Log the username and email for debugging
        Log.d("VerificationActivity", "Username: " + username + ", Email: " + email);

        buttonSend.setOnClickListener(v -> sendVerificationCode());
        buttonVerify.setOnClickListener(v -> verifyCode());
    }

    private void sendVerificationCode() {
        // Localhost test USE
        //String url = "http://10.0.2.2:3000/sendVerification";

        //Global connection
        String url = "https://forum-node.vercel.app/sendVerification";
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> Toast.makeText(getApplicationContext(), "Verification code sent to your email.", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getApplicationContext(), "Failed to send verification code.", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void verifyCode() {
        String verificationCode = editTextVerificationCode.getText().toString().trim();
        // Localhost test USE
        //String url = "http://10.0.2.2:3000/reVerify";

        //Global connection
        String url = "https://forum-node.vercel.app/reVerify";

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("verificationCode", verificationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            String token = response.getString("token");
                            // Assuming the server includes userId in the response
                            String userId = response.getString("userId");

                            // Save the new token and userId
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putString("userId", userId);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Verification successful.", Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity or any other activity as necessary
                            Intent intent = new Intent(VerificationCodeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to verify code.", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(getApplicationContext(), "Failed to verify code.", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
