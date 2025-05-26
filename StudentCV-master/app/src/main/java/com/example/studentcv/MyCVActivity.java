package com.example.studentcv;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyCVActivity extends AppCompatActivity {

    private static final String TAG = "MyCVActivity";
    private LinearLayout chatBodyContainer;
    private ProgressBar progressBar;
    private ImageButton sendButton;
    private EditText messageInput;
    private ScrollView scrollView;

    private GeminiPro geminiPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cv);

        chatBodyContainer = findViewById(R.id.chatBodyContainer);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);

        geminiPro = new GeminiPro(); // Initialize GeminiPro

        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();
            if (userMessage.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            messageInput.setText(""); // Clear input
            addChatMessage("You", userMessage);

            progressBar.setVisibility(View.VISIBLE);
            GeminiPro.getResponse(geminiPro.getModel().startChat(), userMessage, new ResponseCallBack() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    addChatMessage("VOVO", response);
                }

                @Override
                public void onError(Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error getting response", t);
                    addChatMessage("VOVO", "Sorry, I couldn't answer that. Please try again.");
                }
            });
        });

        addChatMessage("VOVO", "Hi! I'm VOVO. Ask me anything about CVs, and I'll help you.");
    }

    private void addChatMessage(String sender, String message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message_block, null);

        TextView userAgentName = view.findViewById(R.id.userAgentNameTextfield);
        TextView userAgentMessage = view.findViewById(R.id.userAgentMessageTextView);

        userAgentName.setText(sender);
        userAgentMessage.setText(message);

        if (sender.equals("You")) {
            view.setBackgroundResource(R.drawable.user_message_background);
        } else {
            view.setBackgroundResource(R.drawable.ai_message_background);
        }

        chatBodyContainer.addView(view);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}