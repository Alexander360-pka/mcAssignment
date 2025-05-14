package com.example.mcassignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CounselorSignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.counselor_signup);

        //JUST GETTING ALL THE STRINGS FROM THE EDIT TEXTS
        EditText fname = (EditText) findViewById(R.id.couns_fname);
        String couns_fname = fname.getText().toString();

        EditText lname = (EditText) findViewById(R.id.couns_lname);
        String couns_lname = lname.getText().toString();

        EditText creds = (EditText) findViewById(R.id.couns_creds);
        String couns_creds = creds.getText().toString();

        EditText password = (EditText) findViewById(R.id.couns_password);
        String couns_pass = password.getText().toString();

        Button proceed = (Button) findViewById(R.id.proceed_button);

        //SWITCHING PAGES WHEN THEY CLICK PROCEED
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.counselor_problems);
            }
        });
    }
}
