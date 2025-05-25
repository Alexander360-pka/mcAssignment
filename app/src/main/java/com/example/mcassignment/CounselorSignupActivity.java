package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

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
        setContentView(R.layout.counselor_signup);

        EditText password = findViewById(R.id.couns_password);
        EditText passConf = findViewById(R.id.couns_pass_conf);
        EditText fname = findViewById(R.id.couns_fname);
        EditText lname = findViewById(R.id.couns_lname);
        EditText creds = findViewById(R.id.couns_creds);

        //A MAP OF FIELDS AND THEIR ERROR MESSAGES
        Map<EditText, String> fields = new HashMap<>();
        fields.put(fname, "First name is required");
        fields.put(lname, "Last name is required");
        fields.put(password, "Password is required");
        fields.put(passConf, "Password confirmation is required");
        fields.put(creds, "Credentials are required");

        Button proceed = findViewById(R.id.proceed_button);
        proceed.setOnClickListener(v -> {
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
            String credsStr = creds.getText().toString();

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
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_signup.php")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        runOnUiThread(() -> {
                            startActivity(new Intent(CounselorSignupActivity.this, CounselorProblems.class));
                        });

                        final String responseData = response.body().string();

    //                    CounselorSignupActivity.this.runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            try {
    //                                processJSON(responseData);
    //                            } catch (JSONException e) {
    //                                throw new RuntimeException(e);
    //                            }
    //                        }
    //                    });
                    }
                });
            }
        });
    }
}
