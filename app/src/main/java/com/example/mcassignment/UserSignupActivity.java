package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class UserSignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ENSURES THAT THE LAYOUT FILE IS DISPLAYED
        setContentView(R.layout.user_signup);

        //A MAP OF FIELDS AND THEIR ERROR MESSAGES
        Map<EditText, String> fields = new HashMap<>();
        fields.put(findViewById(R.id.username), "Username is required");
        fields.put(findViewById(R.id.userPassword), "Password is required");
        fields.put(findViewById(R.id.userConfPass), "Password confirmation is required");

        Button proceed = findViewById(R.id.user_proceed_button);
        proceed.setOnClickListener(v -> {
            //LOOPS THROUGH EACH FIELD AND ENSURES THAT THEY ARE NOT EMPTY
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

            EditText password = findViewById(R.id.userPassword);
            EditText passConf = findViewById(R.id.userConfPass);
            EditText username = findViewById(R.id.username);
            String passStr = password.getText().toString();
            String passConfStr = passConf.getText().toString();
            String usernameStr = username.getText().toString();

            //ENSURE USERNAME IS FORMATTED CORRECTLY
            if (!usernameStr.isEmpty()) {
                if (usernameStr.length() < 6 || usernameStr.length() > 30) {
                    username.setError("Username must be 6-30 characters long");
                    if (isValid) {
                        username.requestFocus();
                        isValid = false;
                    }
                }
                else if (usernameStr.contains(" ")) {
                    username.setError("Username cannot contain spaces");
                    if (isValid) {
                        username.requestFocus();
                        isValid = false;
                    }
                }
                else if (!usernameStr.matches("^[a-zA-Z0-9._-]+$")) {
                    username.setError("Only letters, numbers and .-_ allowed");
                    if (isValid) {
                        username.requestFocus();
                        isValid = false;
                    }
                }
            }

            //ENSURE PASSWORD IS STRONG IF IT NOT EMPTY
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
                startActivity(new Intent(UserSignupActivity.this, UserProblems.class));
            }
        });
    }
}
