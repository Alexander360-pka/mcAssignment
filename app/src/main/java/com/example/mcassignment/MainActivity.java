package com.example.mcassignment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
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
    private EditText messageInput;
    private RecyclerView messagesView;
    private TextView counselorNameText;

    private MessagesAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    private String currentUserId = "user123";
    private String currentConvId;
    private String counselorName;
    private long lastMessageId = 0;

    private Handler handler = new Handler();
    private Runnable messagePoller;
    private static final int POLL_INTERVAL_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In MainActivity, add null checks:
        currentConvId = getIntent().getStringExtra("CONV_ID");
        counselorName = getIntent().getStringExtra("COUNSELOR_NAME");

        if (currentConvId == null || counselorName == null) {
            Toast.makeText(this, "Missing conversation data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get conversation details from intent
        currentConvId = getIntent().getStringExtra("CONV_ID");
        counselorName = getIntent().getStringExtra("COUNSELOR_NAME");

        setupViews();
        setupRecyclerView();
        startMessagePolling();
    }

    private void setupViews() {
        messageInput = findViewById(R.id.message_input);
        messagesView = findViewById(R.id.messages_view);
        counselorNameText = findViewById(R.id.counselor_name);

        counselorNameText.setText("Chat with " + counselorName);

        findViewById(R.id.send_button).setOnClickListener(v -> sendChatMessage());
    }

    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messages, currentUserId);
        messagesView.setLayoutManager(new LinearLayoutManager(this));
        messagesView.setAdapter(adapter);
    }

    private void startMessagePolling() {
        stopMessagePolling();
        messagePoller = new Runnable() {
            @Override
            public void run() {
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
                        Log.w("MainActivity", "Polling error, retrying...", e);
                        handler.postDelayed(messagePoller, POLL_INTERVAL_MS);
                    }
                });
            }
        };
        handler.post(messagePoller);
    }

    private void stopMessagePolling() {
        if (messagePoller != null) {
            handler.removeCallbacks(messagePoller);
        }
    }

    private void processIncomingMessages(JSONObject response) throws JSONException {
        JSONArray newMessages = response.getJSONArray("messages");
        for (int i = 0; i < newMessages.length(); i++) {
            JSONObject msg = newMessages.getJSONObject(i);
            addMessageToChat(
                    msg.getString("sender_id"),
                    msg.getString("content"),
                    msg.optString("timestamp", getCurrentTimestamp())
            );
            lastMessageId = msg.getLong("mess_id");
        }
    }

    private void sendChatMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) return;

        // Optimistic UI update
        addMessageToChat(currentUserId, content, getCurrentTimestamp());
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
//                        runOnUiThread(() ->
//                                Toast.makeText(MainActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show()
//                        );
                    }
                });
    }

    private void addMessageToChat(String senderId, String content, String timestamp) {
        runOnUiThread(() -> {
            adapter.addMessage(new Message(senderId, content, timestamp));
            messagesView.smoothScrollToPosition(adapter.getItemCount() - 1);
        });
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    @Override
    protected void onDestroy() {
        stopMessagePolling();
        super.onDestroy();
    }


}