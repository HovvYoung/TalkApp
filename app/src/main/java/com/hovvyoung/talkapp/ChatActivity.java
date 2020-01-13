package com.hovvyoung.talkapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView chatListView;
    private ArrayList<String> chatsList;
    private ChatListAdapter adapter;
    private String selectedUser;

    private boolean listenFlag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String currentUser = ParseUser.getCurrentUser().getUsername();
        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this, "Chat with " + selectedUser + " now!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();

        findViewById(R.id.btnSend).setOnClickListener(this);

        chatListView = findViewById(R.id.chat_list_view);
        chatsList = new ArrayList<String>();
        adapter = new ChatListAdapter(this, currentUser, selectedUser, chatsList);
        chatListView.setAdapter(adapter);

        queryChat(adapter, chatsList);

    }

    @Override
    public void onClick(View v) {
        final EditText edtMessage = findViewById(R.id.edtSend);

        ParseObject chat = new ParseObject("Chat");
        chat.put("sender", ParseUser.getCurrentUser().getUsername());
        chat.put("receiver", selectedUser);
        chat.put("msg", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(ChatActivity.this, "Message from " + ParseUser.getCurrentUser().getUsername() + " send to " + selectedUser,
                            Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    chatsList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtMessage.getText().toString());
                    adapter.notifyDataSetChanged();
                    edtMessage.setText("");
                } else {
                    FancyToast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                }
            }

        });
    }

    private void queryChat (final ChatListAdapter adapter, final ArrayList<String> chatsList) {
        try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("receiver", selectedUser);
            secondUserChatQuery.whereEqualTo("sender", selectedUser);
            secondUserChatQuery.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");
            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject chatObject : objects) {
                            String msg = chatObject.get("msg") + "";

                            if (chatObject.get("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                msg = ParseUser.getCurrentUser().getUsername() + ": " + msg;
                            } else if (chatObject.get("sender").equals(selectedUser)) {
                                msg = selectedUser + ": " + msg;
                            }

                            chatsList.add(msg);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
