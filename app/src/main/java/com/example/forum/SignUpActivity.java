package com.example.forum;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextVerificationCode;
    private Button buttonSignUp, buttonSendVerificationCode, buttonVerifyCode;
    private boolean isVerified = false;
    private boolean canResendCode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize EditTexts
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSendVerificationCode = findViewById(R.id.buttonSendVerificationCode);

        // Initialize TextViews for hints
        TextView textViewUsernameHint = findViewById(R.id.textViewUsernameHint);
        TextView textViewEmailHint = findViewById(R.id.textViewEmailHint);
        TextView textViewPasswordHint = findViewById(R.id.textViewPasswordHint);
        TextView textViewVerificationHint = findViewById(R.id.textViewVerificationHint);

        // Set focus change listeners for each EditText to show/hide hints
        setupFocusChangeListener(editTextUsername, textViewUsernameHint);
        setupFocusChangeListener(editTextEmail, textViewEmailHint);
        setupFocusChangeListener(editTextPassword, textViewPasswordHint);
        setupFocusChangeListener(editTextVerificationCode, textViewVerificationHint);



        // Set up click listener for the sign-up button
        buttonSignUp.setOnClickListener(v -> registerUser());

        // Set up click listener for the send verification code button
        buttonSendVerificationCode.setOnClickListener(v -> sendVerificationCode());
        buttonVerifyCode = findViewById(R.id.buttonVerifyCode);

        buttonSendVerificationCode.setOnClickListener(v -> {
            if (canResendCode) {
                sendVerificationCode();
                startResendTimer();
            } else {
                Toast.makeText(SignUpActivity.this, "Please wait before resending code.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonVerifyCode.setOnClickListener(v -> verifyCode());

    }
    private void setupFocusChangeListener(EditText editText, TextView textViewHint) {
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                textViewHint.setVisibility(View.VISIBLE);
            } else {
                textViewHint.setVisibility(View.GONE);
            }
        });
    }

    private void startResendTimer() {
        canResendCode = false;
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSendVerificationCode.setText("Resend in " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                buttonSendVerificationCode.setText("Resend Code");
                canResendCode = true;
            }
        }.start();
    }

    private void sendVerificationCode() {
        String email = editTextEmail.getText().toString().trim();

        if(email.isEmpty()) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Localhost test USE
        //String url = "http://10.0.2.2:3000/SendVerification";

        //Global connection
        String url = "https://forum-node.vercel.app/SendVerification";

        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                response -> Toast.makeText(getApplicationContext(), "Verification code sent to your email.", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getApplicationContext(), "Failed to send verification code.", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjReq);
    }
    private void verifyCode() {
        String email = editTextEmail.getText().toString().trim();
        String verificationCode = editTextVerificationCode.getText().toString().trim();

        if(email.isEmpty() || verificationCode.isEmpty()) {
            Toast.makeText(this, "Email and verification code cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Localhost test USE
        //String url = "http://10.0.2.2:3000/verifyCode";

        //Global connection
        String url = "https://forum-node.vercel.app/verifyCode";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("verificationCode", verificationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            isVerified = true;
                            Toast.makeText(getApplicationContext(), "Verification successful.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.optString("message", "Verification failed. Please try again.");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to parse verification response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Failed to send verification code. " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjReq);
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Username and password validation
        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || editTextVerificationCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Username restriction: No special characters and length between 4 to 20 characters
        if (!username.matches("^[a-zA-Z0-9._-]{4,20}$")) {
            Toast.makeText(this, "Username must be 4-20 characters long and can include letters, numbers, '.', '_', and '-'.", Toast.LENGTH_LONG).show();
            return;
        }

        // Password restriction: At least 8 characters with a mix of letters, numbers, and at least one special character
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            Toast.makeText(this, "Password must be at least 8 characters long and include a mix of uppercase and lowercase letters, numbers, and special characters (@#$%^&+=).", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isVerified) {
            Toast.makeText(this, "You need to verify your email address before signing up.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If all validations pass, proceed to register the user
        RequestQueue queue = Volley.newRequestQueue(this);
        // Localhost test USE
        //String url = "http://10.0.2.2:3000/signup";

        //Global connection
        String url = "https://forum-node.vercel.app/signup";

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to WelcomeActivity
                    Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(getApplicationContext(), "Failed to register: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjReq);

    }

}
