package com.hovvyoung.talkapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayList<String> tUsers;
    private ArrayAdapter adapter;

    private String followedUser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        listView = findViewById(R.id.chatLIstVIew);
        listView.setOnItemClickListener(this);

        tUsers = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tUsers);

        getFriends();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView checkedTextView = (TextView)view;
        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
        intent.putExtra("selectedUser", tUsers.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // use my_menu.xml
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutItem:
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;

            case R.id.postItem:
                Intent intent = new Intent(FriendsActivity.this, PostActivity.class);
                startActivity(intent);
                break;

            case R.id.addItem:
                intent = new Intent(FriendsActivity.this, UsersList.class);
                startActivity(intent);
                break;

            case R.id.reflashItem:
                getFriends();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFriends() {
        try {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            tUsers.clear();
//            query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereContainedIn("username", ParseUser.getCurrentUser().getList("fanOf"));
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseUser fan : objects) {
                            tUsers.add(fan.getUsername());
                        }
                        listView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
