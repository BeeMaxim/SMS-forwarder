package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class BatteryMonitor extends BroadcastReceiver {

    public BatteryMonitor() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "rrr", Toast.LENGTH_LONG).show();
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        Data myData = new Data.Builder()
                .putString("sms", "current power: " + Integer.toString(level) + "%")
                .putString("apiToken", apiToken)
                .putString("chatId", chatId).build();

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                .setConstraints(constraints)
                .setInputData(myData).build();

        WorkManager.getInstance(context).enqueue(myWorkRequest);
    }
}
