package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CounsellorLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellor_login);


        TextView signUp = findViewById(R.id.tvSignUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CounsellorLoginActivity.this, CounselorSignupActivity.class));
            }
        });

        TextInputEditText email = findViewById(R.id.etEmail);
        TextInputEditText password = findViewById(R.id.etPassword);

        Map<EditText, String> fields = new HashMap<>();
        fields.put(email, "Email is required");
        fields.put(password, "Password is required");

        Button login = findViewById(R.id.btnLogin);
        login.setOnClickListener(v -> {

            boolean isValid = true;
            for (Map.Entry<EditText, String> entry : fields.entrySet()) {
                EditText field = entry.getKey();
                String errorMsg = entry.getValue();

                if (field.getText().toString().trim().isEmpty()) {
                    field.setError(errorMsg);
                    field.requestFocus();
                    isValid = false;
                }
                else {
                    field.setError(null);
                }
            }

            String emailStr = email.getText().toString().trim();
            String passStr = password.getText().toString().trim();

            if (isValid) {

                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("email", emailStr)
                        .add("password", passStr)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_login.php")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(CounsellorLoginActivity.this,
                                    "Network error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        runOnUiThread(() -> {
                            try {
                                JSONObject json = new JSONObject(responseData);

                                if (json.has("success") && json.getBoolean("success")) {
                                    // Login successful
                                    Toast.makeText(
                                            CounsellorLoginActivity.this,
                                            json.optString("message", "Login successful"),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Proceed to next activity
                                    startActivity(new Intent(CounsellorLoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // Login failed
                                    String error = json.optString("error", "Unknown error occurred");
                                    Toast.makeText(
                                            CounsellorLoginActivity.this,
                                            "Login failed: " + error,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(
                                        CounsellorLoginActivity.this,
                                        "Invalid server response",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
