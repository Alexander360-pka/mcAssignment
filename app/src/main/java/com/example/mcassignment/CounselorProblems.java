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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselor_problems);

        dynamicViewsContainer = findViewById(R.id.dynamic_views_container);
        inflater = LayoutInflater.from(this);
        button = findViewById(R.id.counselor_problems_proceed);
        errorTextView = findViewById(R.id.counsErrorTextView);

        button.setOnClickListener(v -> {
            boolean isValid = validateChoices();

            if (isValid) {
                //GET ALL SELECTED CATEGORIES
                List<String> selectedCategories = new ArrayList<>();
                for (CheckBox checkBox : allCheckBoxes) {
                    if (checkBox.isChecked()) {
                        selectedCategories.add(checkBox.getText().toString());
                    }
                }
                //PROCEED TO LOGIN PAGE
                Intent intent = new Intent(CounselorProblems.this, CounselorLogIn.class);
                startActivity(intent);
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
            //do we get the id too? for inserting in CounsellorExpertise?
            //Is the id not automatically created when we input?

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
