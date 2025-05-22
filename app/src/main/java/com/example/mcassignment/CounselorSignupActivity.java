package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

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

        setContentView(R.layout.counselor_signup);

        //JUST GETTING ALL THE STRINGS FROM THE EDIT TEXTS
        EditText fname = (EditText) findViewById(R.id.couns_fname);
        EditText lname = (EditText) findViewById(R.id.couns_lname);
        EditText creds = (EditText) findViewById(R.id.couns_creds);
        EditText password = (EditText) findViewById(R.id.couns_password);


        Button proceed = (Button) findViewById(R.id.proceed_button);

        Intent problems = new Intent(CounselorSignupActivity.this, CounsProblemsActivity.class);

        //SWITCHING PAGES WHEN THEY CLICK PROCEED
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.counselor_problems);
                //TextView t = (TextView) findViewById(R.id.textView);

                String couns_fname = fname.getText().toString();
                String couns_lname = lname.getText().toString();
                String couns_creds = creds.getText().toString();
                String couns_pass = password.getText().toString();

                //NETWORK REQUEST TO INSERT STRINGS IN DATABASE
                RequestBody formBody = new FormBody.Builder()
                        .add("fname", couns_fname)
                        .add("lname", couns_lname)
                        .add("creds", couns_creds)
                        .add("pass", couns_pass)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_signup.php")
                        .post(formBody)
                        .build();

                OkHttpClient client = new OkHttpClient();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                t.setText("Request failed: " + e.getMessage());
//                                String responseStr = "Request failed: " + e.getMessage();
//                                problems.putExtra("response",responseStr);
//                            }
//                        });
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseStr = response.body().string();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    t.setText(responseStr);
//                                    problems.putExtra("response",responseStr);
//                                }
//                            });
                        } else {
                            throw new IOException("Unexpected code " + response);
                        }
                    }
                });

                startActivity(problems);

            }
        });
    }
}
