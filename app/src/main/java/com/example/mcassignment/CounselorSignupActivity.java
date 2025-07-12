package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class CounselorSignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ENSURES THAT THE LAYOUT FILE IS DISPLAYED
        setContentView(R.layout.activity_counsellor_signup);

        TextInputEditText password = findViewById(R.id.etPassword);
        TextInputEditText passConf = findViewById(R.id.etConfirmPassword);
        TextInputEditText fname = findViewById(R.id.etFirstName);
        TextInputEditText lname = findViewById(R.id.etLastName);
        TextInputEditText creds = findViewById(R.id.etCredentials);
        TextInputEditText email = findViewById(R.id.etEmail);

        //A MAP OF FIELDS AND THEIR ERROR MESSAGES
        Map<EditText, String> fields = new HashMap<>();
        fields.put(fname, "First name is required");
        fields.put(lname, "Last name is required");
        fields.put(email, "Email is required");
        fields.put(password, "Password is required");
        fields.put(passConf, "Password confirmation is required");
        fields.put(creds, "Credentials are required");

        Button next = findViewById(R.id.btnNext);
        next.setOnClickListener(v -> {
            // LOOPS THROUGH EACH FIELD AND ENSURES THAT THEY ARE NOT EMPTY
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

            String passStr = password.getText().toString();
            String passConfStr = passConf.getText().toString();
            String fnameStr = fname.getText().toString();
            String lnameStr = lname.getText().toString();
            String emailStr = email.getText().toString().trim();
            String credsStr = creds.getText().toString();

            // EMAIL VALIDATION
            if (!emailStr.isEmpty() && !isValidEmail(emailStr)) {
                email.setError("Please enter a valid email address");
                if (isValid) email.requestFocus();
                isValid = false;
            }

            //ENSURE PASSWORD IS STRONG IF IT IS NOT EMPTY
            if(!passStr.isEmpty()){
                if (passStr.length() < 8) {
                    password.setError("Password must be at least 8 characters");
                    if (isValid) password.requestFocus();
                    isValid = false;
                }
                else if (!passStr.matches(".*[A-Z].*")) {
                    password.setError("Password must contain at least one uppercase letter");
                    if (isValid) password.requestFocus();
                    isValid = false;
                }
                else if (!passStr.matches(".*[a-z].*")) {
                    password.setError("Password must contain at least one lowercase letter");
                    if (isValid) password.requestFocus();
                    isValid = false;
                }
                else if (!passStr.matches(".*\\d.*")) {
                    password.setError("Password must contain at least one number");
                    if (isValid) password.requestFocus();
                    isValid = false;
                }
                else if (!passStr.matches(".*[!@#$%^&*()_+-=:'`~<>.].*")) {
                    password.setError("Password must contain at least one special character");
                    if (isValid) password.requestFocus();
                    isValid = false;
                }
            }

            //CONFIRM PASSWORD
            if(!passStr.isEmpty() && !passConfStr.isEmpty() && !passStr.equals(passConfStr)) {
                passConf.setError("Passwords do not match");
                if (isValid) passConf.requestFocus();
                isValid = false;
            }

            //PROCEEDS TO NEXT SCREEN IF ALL FIELDS ARE VALID
            if (isValid) {

                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("first_name", fnameStr)
                        .add("last_name", lnameStr)
                        .add("credentials", credsStr)
                        .add("password", passStr)
                        .add("email", emailStr)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_signup.php")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(CounselorSignupActivity.this,
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
                                            CounselorSignupActivity.this,
                                            json.optString("message", "Registration successful"),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Proceed to next activity
                                    Intent intent = new Intent(CounselorSignupActivity.this, UserProblems.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Login failed
                                    String error = json.optString("error", "Unknown error occurred");
                                    Toast.makeText(
                                            CounselorSignupActivity.this,
                                            "Login failed: " + error,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(
                                        CounselorSignupActivity.this,
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

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;

        // Basic pattern check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        return email.length() <= 254 &&            // MAX LENGTH
                !email.startsWith(".") &&           // NO LEADING DOT
                !email.endsWith(".") &&             // NO TRAILING DOT
                !email.contains("..") &&            // NO CONSECUTIVE DOTS
                email.indexOf('@') > 0 &&           // @ IS NOT AT THE START
                email.lastIndexOf('.') > email.indexOf('@');  // DOT IS AFTER THE @
    }
}
