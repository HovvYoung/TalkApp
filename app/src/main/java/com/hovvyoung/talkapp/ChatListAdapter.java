package com.hovvyoung.talkapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/*
*   we build our customized adapter for chat list;
*/
public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private String mSenderName;
    private String mReceiverName;
    private ArrayList<String> mChatsList;  // data from firebase
    public ChatListAdapter(Activity activity, String sender, String receiver, ArrayList<String> chatsList) {
        mActivity = activity;
        mSenderName = sender;
        mReceiverName = receiver;
        mChatsList = chatsList;
    }

    private static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mChatsList.size();
    }

    @Override
    public String getItem(int position) {
        String snapshot = mChatsList.get(position);
        return snapshot;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // if we dont have View before, create one;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.authorName = convertView.findViewById(R.id.author);
            holder.body = convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            convertView.setTag(holder);

        }

        final String message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        String[] msgItem = divideMsg(message);

        // set the pattern either me is sender or receiver;
        boolean isMe = msgItem[0].equals(mSenderName);
        setChatRowAppearance(isMe, holder);

        String author = msgItem[0];
        holder.authorName.setText(author);

        String msg = msgItem[1];
        holder.body.setText(msg);

        return convertView;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {

        if (isItMe) {
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);

            // If you want to use colours from colors.xml
            // int colourAsARGB = ContextCompat.getColor(mActivity.getApplicationContext(), R.color.yellow);
            // holder.authorName.setTextColor(colourAsARGB);

            holder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }


    void cleanup() {

//        mDatabaseReference.removeEventListener(mListener);
    }

    private String[] divideMsg(String s) {
        String[] result = new String[2];
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ':') {
                result[0] = s.substring(0, i);
                if (i + 1 < s.length()) {
                    result[1] = s.substring(i+1, s.length());
                }
            }
        }
        return result;
    }
}
