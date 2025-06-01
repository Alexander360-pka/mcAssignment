package com.example.mcassignment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // UI Components
    private EditText messageInput;
    private Button sendButton, findMatchButton;
    private RecyclerView messagesView;

    // Adapter and Data
    private MessagesAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    // Session State
    private String currentUserId = "user123"; // Replace with actual user ID
    private String currentConvId = null;
    private long lastMessageId = 0;

    // Polling Handler
    private Handler handler = new Handler();
    private Runnable messagePoller;
    private static final int POLL_INTERVAL_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupRecyclerView();
        setupClickListeners();
    }
    private void stopMessagePolling() {
        if (messagePoller != null) {
            handler.removeCallbacks(messagePoller);
            messagePoller = null;  // Important to prevent memory leaks
        }
    }
    private void setupViews() {
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        findMatchButton = findViewById(R.id.find_match_button);
        messagesView = findViewById(R.id.messages_view);
    }

    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messages, currentUserId);
        messagesView.setLayoutManager(new LinearLayoutManager(this));
        messagesView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        findMatchButton.setOnClickListener(v -> findCounselorMatch());
        sendButton.setOnClickListener(v -> sendChatMessage());
    }

    private void findCounselorMatch() {
        ApiClient.findMatch(currentUserId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.has("error")) {
                            showToast(response.getString("error"));
                            return;
                        }
                        handleMatchResponse(response);
                    } catch (JSONException e) {
                        Log.e("MainActivity", "Response parsing error", e);
                        showToast("Invalid server response");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> showToast("Network error: " + e.getMessage()));
            }
        });
    }

    private void handleMatchResponse(JSONObject response) throws JSONException {
        if (response.getBoolean("matched")) {
            currentConvId = response.getString("conv_id");
            JSONObject counsellor = response.getJSONObject("counsellor");
            showToast("Matched with: " + counsellor.getString("username"));
            startMessagePolling();
        } else {
            showToast(response.optString("reason", "No counselors available"));
        }
    }

    private void startMessagePolling() {
        stopMessagePolling();
        messagePoller = new Runnable() {
            @Override
            public void run() {
                if (currentConvId == null) return;

                ApiClient.getMessages(currentConvId, lastMessageId, new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.has("messages")) {
                                processIncomingMessages(response);
                            }
                        } catch (JSONException e) {
                            Log.e("MainActivity", "Message parsing error", e);
                        }
                        handler.postDelayed(messagePoller, POLL_INTERVAL_MS);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w("MainActivity", "Polling retry in " + POLL_INTERVAL_MS + "ms");
                        handler.postDelayed(messagePoller, POLL_INTERVAL_MS);
                    }
                });
            }
        };
        handler.post(messagePoller);
    }

    private void processIncomingMessages(JSONObject response) throws JSONException {
        JSONArray newMessages = response.getJSONArray("messages");
        for (int i = 0; i < newMessages.length(); i++) {
            JSONObject msg = newMessages.getJSONObject(i);
            addMessageToChat(
                    msg.getString("sender_id"),
                    msg.getString("content"),
                    msg.optString("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()))
            );
            lastMessageId = msg.getLong("mess_id");
        }
    }

    private void sendChatMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty() || currentConvId == null) return;

        // Optimistic UI update
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        addMessageToChat(currentUserId, content, timestamp);
        messageInput.setText("");

        ApiClient.sendMessage(currentConvId, currentUserId, content,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            lastMessageId = response.getLong("mess_id");
                        } catch (JSONException e) {
                            Log.w("MainActivity", "No message ID in response");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> showToast("Failed to send. Retrying..."));
                        // Consider message queue for retry logic
                    }
                });
    }

    // ... (keep existing utility methods addMessageToChat, scrollToLatestMessage, showToast, etc.)
    // ==================== CHAT UTILITIES ====================
    private void addMessageToChat(String senderId, String content, String timestamp) {
        runOnUiThread(() -> {
            adapter.addMessage(new Message(senderId, content, timestamp));
            scrollToLatestMessage();
        });
    }

    private void scrollToLatestMessage() {
        messagesView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        stopMessagePolling();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}