package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.ChosenApps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.sms_receiver.MainActivity.ChosenApps;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ControlAdapter extends RecyclerView.Adapter<ControlAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<MessageInfo> messages;

    ControlAdapter(Context context, List<MessageInfo> messages) {
        this.inflater = LayoutInflater.from(context);
        this.messages = messages;
    }

    @NonNull
    @Override
    public ControlAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_card, parent, false);
        return new ControlAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ControlAdapter.ViewHolder holder, int position) {
        MessageInfo message = messages.get(position);
        holder.messageText.setText(message.text);
        holder.messageStatus.setText(message.status);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        final TextView messageText;
        final TextView messageStatus;
        ViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.message_text);
            messageStatus = view.findViewById(R.id.message_status);
        }
    }
}
