package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button btnPatient = findViewById(R.id.btnPatient);
        Button btnCounsellor = findViewById(R.id.btnCounsellor);

        btnPatient.setOnClickListener(V -> startActivity(new Intent(this, PatientLoginActivity.class)));

        btnCounsellor.setOnClickListener(V -> startActivity(new Intent(this, CounsellorLoginActivity.class)));
    }
}