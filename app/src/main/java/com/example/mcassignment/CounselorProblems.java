package com.example.mcassignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CounselorProblems extends AppCompatActivity {
    private LinearLayout dynamicViewsContainer;
    private LayoutInflater inflater;
    private Button button;
    private List<CheckBox> allCheckBoxes = new ArrayList<>();
    private TextView errorTextView;
    private int counselorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselor_problems);

        if (getIntent() != null && getIntent().hasExtra("COUNSELOR_ID")) {
            counselorId = getIntent().getIntExtra("COUNSELOR_ID", -1);}
        dynamicViewsContainer = findViewById(R.id.dynamic_views_container);
        inflater = LayoutInflater.from(this);
        button = findViewById(R.id.counselor_problems_proceed);
        errorTextView = findViewById(R.id.counsErrorTextView);

        button.setOnClickListener(v -> {
            boolean isValid = validateChoices();

            if (isValid) {
                //GET ALL SELECTED CATEGORIES
                JSONArray selectedCategories = new JSONArray();
                for (CheckBox checkBox : allCheckBoxes) {
                    if (checkBox.isChecked()) {
                        selectedCategories.put(checkBox.getText().toString());
                    }
                }

                OkHttpClient client = new OkHttpClient();

                String selectedCategoriesStr = selectedCategories.toString();
                RequestBody formBody = new FormBody.Builder()
                        .add("category", selectedCategoriesStr)
                        .build();

                Request receiveRequest = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_signup.php")
                        .post(formBody)
                        .build();

                client.newCall(receiveRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        //PROCEED TO LOGIN PAGE
                        runOnUiThread(() -> {
                            startActivity(new Intent(CounselorProblems.this, CounsellorLoginActivity.class));
                        });

                        final String responseData = response.body().string();
                    }
                });

                //SEND SELECTED CATEGORIES AND COUNSELOR ID
                try {
                    // CREATE JSON OBJECT CONTAINING BOTH DATA
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("counselor_id", counselorId);
                    jsonData.put("categories", selectedCategories);

                    String jsonString = jsonData.toString();

                    RequestBody body = RequestBody.create(
                            jsonString,
                            MediaType.parse("application/json; charset=utf-8")
                    );

                    Request sendRequest = new Request.Builder()
                            .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_expertise.php")
                            .post(body)
                            .build();

                    client.newCall(sendRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(CounselorProblems.this,
                                        "Failed to send data", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }

                            final String responseData = response.body().string();
                            runOnUiThread(() -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(responseData);
                                    if (jsonResponse.getString("status").equals("success")) {
                                        startActivity(new Intent(CounselorProblems.this,
                                                CounsellorLoginActivity.class));
                                    } else {
                                        Toast.makeText(CounselorProblems.this,
                                                jsonResponse.getString("message"),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error creating JSON data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getProblemCategories();
    }

    //FETCHING THE PROBLEM CATEGORIES
    private void getProblemCategories(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/couns_problems.php")
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

                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        processJSON(responseData);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    private void processJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        for(int i = 0;i < ja.length();i++){
            JSONObject jo = ja.getJSONObject(i);
            String counselorCategoryName = jo.getString("category_name");
            String counselorDesc = jo.getString("category_desc");


            //DYNAMICALLY INCREASE THE OPTIONS
            View itemView = inflater.inflate(R.layout.counselor_problem_items, dynamicViewsContainer, false);

            CheckBox checkBox = itemView.findViewById(R.id.check_cat_dynamic);
            TextView textView = itemView.findViewById(R.id.desc_cat_dynamic);

            checkBox.setText(counselorCategoryName);
            textView.setText(counselorDesc);

            allCheckBoxes.add(checkBox);

            dynamicViewsContainer.addView(itemView);
        }
    }

    //OUTPUT AN ERROR MESSAGE IF NO OPTIONS ARE PICKED
    private boolean validateChoices() {
        boolean isValid = true;

        if (!atleastOneOption()) {
            errorTextView.setText("*Please select at least one problem area");
            errorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorTextView.setVisibility(View.GONE);
            errorTextView.setText("");
        }

        return isValid;
    }

    //CHECK THAT AT LEAST ONE CHECKBOX IS PICKED
    private boolean atleastOneOption() {
        for (int i = 0; i < allCheckBoxes.size(); i++) {
            CheckBox checkBox = allCheckBoxes.get(i);
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }
}
