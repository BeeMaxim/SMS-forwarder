package com.example.sms_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SMSMonitor extends BroadcastReceiver {

    public SMSMonitor() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] message = new SmsMessage[pduArray.length];
            String format = bundle.getString("format");

            for (int i = 0; i < pduArray.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) pduArray[i], format);
            }

            StringBuilder bodyText = new StringBuilder();
            for (SmsMessage smsMessage : message) {
                bodyText.append(smsMessage.getMessageBody());
            }
            String from = message[0].getDisplayOriginatingAddress();
            int subscription = bundle.getInt("subscription", -1);

            String to_send = bodyText.toString();

            int slot = Functions.getSlotBySubscription(context, subscription);

            Data myData = new Data.Builder()
                    .putString("message", Functions.request(context, from, slot, "sms", to_send))
                    .build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SendWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance(context).enqueue(myWorkRequest);
        } catch (Exception ignored) {
        }
    }
}
