package com.example.mcassignment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CounselorProblems extends AppCompatActivity {
    private LinearLayout dynamicViewsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselor_problems);

        dynamicViewsContainer = findViewById(R.id.dynamic_views_container);

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

                CounselorProblems.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processJSON(responseData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    private void processJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);

        //Get references to existing views
        CheckBox category = findViewById((R.id.check_cat1));
        TextView desc = findViewById(R.id.desc_cat1);

        for(int i = 0;i < ja.length();i++){
            JSONObject jo = ja.getJSONObject(i);
            String counselorCategoryName = jo.getString("category_name");
            String counselorDesc = jo.getString("category_description");
            //do we get the id too? for inserting in CounsellorExpertise?

            // Create CheckBox
            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            checkBox.setText(counselorCategoryName);
            dynamicViewsContainer.addView(checkBox);

            // Create Description TextView
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(counselorDesc);
            dynamicViewsContainer.addView(textView);
        }
    }
}
