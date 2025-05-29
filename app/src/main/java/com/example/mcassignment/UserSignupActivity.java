package com.example.mcassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

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

public class UserSignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ENSURES THAT THE LAYOUT FILE IS DISPLAYED
        setContentView(R.layout.activity_patient_signup);

        //HAVEN'T DECIDED IF I WANNA COMMIT TO THIS OR NOT
//        ColorPickerView colorPicker = findViewById(R.id.colorPicker);
//        final int[] selectedColor = {Color.GRAY};
//
//        colorPicker.setColorListener(new ColorListener() {
//            @Override
//            public void onColorSelected(int color, boolean fromUser) {
//                selectedColor[0] = color;
//            }
//        });

        //A MAP OF FIELDS AND THEIR ERROR MESSAGES
        Map<TextInputEditText, String> fields = new HashMap<>();
        fields.put(findViewById(R.id.etUsername), "Username is required");
        fields.put(findViewById(R.id.etUserPassword), "Password is required");
        fields.put(findViewById(R.id.etUserConfPass), "Password confirmation is required");

        Button next = findViewById(R.id.btnUserNext);
        next.setOnClickListener(v -> {
            //LOOPS THROUGH EACH FIELD AND ENSURES THAT THEY ARE NOT EMPTY
            boolean isValid = true;
            for (Map.Entry<TextInputEditText, String> entry : fields.entrySet()) {
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

            TextInputEditText password = findViewById(R.id.etUserPassword);
            TextInputEditText passConf = findViewById(R.id.etUserConfPass);
            TextInputEditText username = findViewById(R.id.etUsername);
            String passStr = password.getText().toString();
            String passConfStr = passConf.getText().toString();
            String usernameStr = username.getText().toString();

//            int confirmedColor = colorPicker.getColor();
//            String colourStr = String.format("#%06X", (0xFFFFFF & confirmedColor));
//            Log.d("Color", "HEX: " + colourStr);


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

                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("username", usernameStr)
                        .add("password", passStr)
                //        .add("colour", colourStr)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/user_signup.php")
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
                            startActivity(new Intent(UserSignupActivity.this, UserProblems.class));;
                        });

                        final String responseData = response.body().string();

    //                    UserSignupActivity.this.runOnUiThread(new Runnable() {
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
