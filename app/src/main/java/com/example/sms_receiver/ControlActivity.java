package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;
import static com.example.sms_receiver.MainActivity.messages;
import static com.example.sms_receiver.MainActivity.saveSharedPreferencesLogList;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_list);
        loadSharedPreferencesLogList(this);
        ControlAdapter mainAdapter = new ControlAdapter(this, messages);
        RecyclerView recycler = findViewById(R.id.messages_recycler);
        recycler.setAdapter(mainAdapter);
    }

    @Override
    protected void onPause(){
        saveSharedPreferencesLogList(this);
        super.onPause();
    }
}
