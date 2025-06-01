package com.example.mcassignment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final String BASE_URL = "https://lamp.ms.wits.ac.za/home/s2819916/solace/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(Exception e);
    }


    public static void findMatch(String userId, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", userId);  // Ensure this is a number string ("1" not "user123")

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2819916/solace/match_users.php")
                    .post(body)
                    .addHeader("Content-Type", "application/json")  // Must include this
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Log.e("NETWORK", "Connection failed", e);
                        callback.onError(new Exception("Network error: " + e.getMessage()));
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String responseData = responseBody.string();
                        Log.d("API_RESPONSE", "Raw: " + responseData);

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            new Handler(Looper.getMainLooper()).post(() ->
                                    callback.onSuccess(jsonResponse)
                            );
                        } catch (JSONException e) {
                            throw new IOException("Malformed JSON: " + responseData, e);
                        }
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Log.e("API", "Response handling failed", e);
                            callback.onError(new Exception("Error processing response: " + e.getMessage()));
                        });
                    }
                }
            });

        } catch (Exception e) {
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onError(new Exception("Request creation failed: " + e.getMessage()))
            );
        }
    }



    public static void sendMessage(String convId, String senderId, String content, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject()
                    .put("conv_id", convId)
                    .put("sender_id", senderId)
                    .put("content", content);

            Request request = new Request.Builder()
                    .url(BASE_URL + "message_handler.php")
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();

            makeRequest(request, callback);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public static void getMessages(String convId, long lastMessageId, ApiCallback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL + "get_messages.php").newBuilder()
                .addQueryParameter("conv_id", convId)
                .addQueryParameter("last_message_id", String.valueOf(lastMessageId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        makeRequest(request, callback);
    }

    private static void makeRequest(Request request, ApiCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    if (response.isSuccessful()) {
                        callback.onSuccess(json);
                    } else {
                        callback.onError(new Exception(json.optString("error", "Server error")));
                    }
                } catch (Exception e) {
                    callback.onError(new Exception("Invalid server response"));
                }
            }
        });
    }
}