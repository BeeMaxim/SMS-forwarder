package com.example.sms_receiver;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.BATTERY_SERVICE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

public class SMSMonitor extends BroadcastReceiver {

    public SMSMonitor() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] message = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            StringBuilder bodyText = new StringBuilder();
            for (SmsMessage smsMessage : message) {
                bodyText.append(smsMessage.getMessageBody());
            }
            String from = message[0].getDisplayOriginatingAddress();
            Object smth = bundle.get("subscription");

            loadSharedPreferencesLogList(context);
            String to_send = bodyText.toString();
            SubscriptionManager manager = SubscriptionManager.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.getActiveSubscriptionInfoList();
            SubscriptionInfo nowSubscription = manager.getActiveSubscriptionInfo((int) smth);
            String SIM = String.valueOf(nowSubscription.getSimSlotIndex() + 1);
            String SIMName = nowSubscription.getDisplayName().toString();
            String SIMNumber = nowSubscription.getNumber();

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

            Data myData = new Data.Builder()
                    .putString("sms", request(from, SIM, SIMName, SIMNumber, to_send, powerLevel))
                    .putString("apiToken", apiToken)
                    .putString("chatId", chatId).build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance(context).enqueue(myWorkRequest);
        } catch (Exception ignored) {
        }
    }

    public String request(String from, String index, String name, String number, String body, String powerLevel) {
        String ModelInfo = MainActivity.info() + powerLevel + "\n";
        String to = "SIM " + index + ": " + name + ", " + number + "\n";
        return ModelInfo + to + "from: " + from + "\n" + body;
    }
}
