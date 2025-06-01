package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FindCounsellorActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView statusText;
    private Button retryButton;
    private String currentUserId = "user123"; // Replace with actual user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_counselor);

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(v -> findCounselorMatch());

        // Start matching immediately
        findCounselorMatch();
    }

    private void findCounselorMatch() {
        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Finding a counselor...");
        retryButton.setVisibility(View.GONE);

        ApiClient.findMatch(currentUserId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.has("error")) {
                            showError(response.getString("error"));
                            return;
                        }
                        if (response.getBoolean("matched")) {
                            handleSuccessfulMatch(response);
                        } else {
                            showError(response.optString("reason", "No counselors available"));
                        }
                    } catch (JSONException e) {
                        Log.e("FindCounselor", "Response parsing error", e);
                        showError("Invalid server response");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> showError("Network error: " + e.getMessage()));
            }
        });
    }

    private void handleSuccessfulMatch(JSONObject response) throws JSONException {
        String convId = response.getString("conv_id");
        JSONObject counsellor = response.getJSONObject("counsellor");
        String counsellorName = counsellor.getString("username");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CONV_ID", convId);
        intent.putExtra("COUNSELOR_NAME", counsellorName);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        statusText.setText(message);
        retryButton.setVisibility(View.VISIBLE);
    }
}