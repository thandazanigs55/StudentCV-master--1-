package com.example.studentcv;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    // Reference for the Realtime Database chat node (using jobId as room id)
    private DatabaseReference chatRef;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private EditText edtMessage;
    private Button btnSend;
    private String chatRoomId; // using jobId as room id for simplicity
    private String studentId;

    // Firestore instance to optionally sync chat messages and fetch Gemini responses
    private FirebaseFirestore firestore;

    // Map to store Gemini responses loaded from Firestore
    private Map<String, String> geminiResponses = new HashMap<>();

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firestore for optional syncing and Gemini responses loading
        firestore = FirebaseFirestore.getInstance();
        fetchGeminiResponses();  // load responses from Firestore

        // Get room details from intent extras
        chatRoomId = getIntent().getStringExtra("jobId");
        studentId = getIntent().getStringExtra("studentId");

        // Initialize Realtime Database reference for this chat room
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        // Listen for new chat messages from the Realtime Database
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                messageList.add(message);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // Send button sends a new chat message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edtMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    sendMessage(text);
                    edtMessage.setText("");
                }
            }
        });
    }

    // Load Gemini responses from Firestore into geminiResponses map.
    // Assumes a collection "gemini_responses" exists where each document has:
    // - trigger: key phrase (e.g., "apply for job", "application status", etc.)
    // - response: the response text.
    private void fetchGeminiResponses() {
        firestore.collection("gemini_responses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String trigger = document.getString("trigger");
                            String response = document.getString("response");
                            if (trigger != null && response != null) {
                                geminiResponses.put(trigger.toLowerCase(), response);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching Gemini responses", task.getException());
                    }
                });
    }

    private void sendMessage(String text) {
        // Create and push the user's message to the realtime database
        ChatMessage message = new ChatMessage(studentId, text, System.currentTimeMillis());
        chatRef.push().setValue(message);

        // Optionally sync to Firestore for persistence.
        firestore.collection("chats").document(chatRoomId)
                .collection("messages").add(message);

        // Check for specific phrases to trigger Gemini response.
        String geminiResponse = getGeminiResponse(text);
        if (geminiResponse != null) {
            ChatMessage responseMessage = new ChatMessage("Gemini", geminiResponse, System.currentTimeMillis());
            chatRef.push().setValue(responseMessage);

            // Optionally sync Gemini response to Firestore.
            firestore.collection("chats").document(chatRoomId)
                    .collection("messages").add(responseMessage);
        }
    }

    // Example method to return a Gemini response fetched from Firestore based on the user's text.
    // If there is no matching trigger, null is returned (so no Gemini message will be sent).
    private String getGeminiResponse(String userText) {
        String message = userText.toLowerCase();
        // Check for known triggers in the map
        for (Map.Entry<String, String> entry : geminiResponses.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}