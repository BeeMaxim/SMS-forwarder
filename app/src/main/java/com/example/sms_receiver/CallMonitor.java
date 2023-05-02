package com.example.sms_receiver;

import static android.content.Context.BATTERY_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

import java.util.List;

public class CallMonitor extends BroadcastReceiver {

    public CallMonitor(){}

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent){
        String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        assert phoneState != null;
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING) && incomingNumber != null) {
            int slot = intent.getIntExtra("slot", -1);

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            int power = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            String powerLevel = "[" + Integer.toString(power) + "%]";

            if (power <= 5) {
                powerLevel += ' ' + "\uD83D\uDD34";
            } else if (power <= 20) {
                powerLevel += ' ' + "\uD83D\uDFE1";
            } else {
                powerLevel += ' ' + "\uD83D\uDFE2";
            }

            SubscriptionManager manager = SubscriptionManager.from(context);
            SubscriptionInfo nowSubscription = manager.getActiveSubscriptionInfoForSimSlotIndex(slot);
            String SIMName = nowSubscription.getDisplayName().toString();
            String SIMNumber = nowSubscription.getNumber();

            Data myData = new Data.Builder()
                    .putString("sms", request(incomingNumber, Integer.toString(slot + 1), SIMName, SIMNumber, "incoming call", powerLevel))
                    .build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance().enqueue(myWorkRequest);
        }
    }

    public String request(String from, String index, String name, String number, String body, String powerLevel) {
        String ModelInfo = MainActivity.info() + powerLevel + "\n";
        String to = "SIM " + index + ": " + name + ", " + number + "\n";
        return ModelInfo + to + "from: " + from + "\n" + body;
    }
}
