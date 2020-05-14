package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.MessageServiceFait;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageSFAdapter  extends ArrayAdapter<MessageServiceFait> {

    private HashMap<Integer, MessageServiceFait> messages;

    public MessageSFAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull HashMap<Integer, MessageServiceFait> messages) {
        super(context, resource,  new ArrayList<MessageServiceFait>(messages.values()));
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final String message = messages.get(position).getMessage();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_sf, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.message_text);
        textView.setText(message);

        return convertView;
    }

    public HashMap<Integer, MessageServiceFait> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<Integer, MessageServiceFait> messages) {
        this.messages = messages;
        for(int i = 0 ; i < messages.size() ; i++) {
            this.insert(messages.get(i), i);
        }
    }
}
