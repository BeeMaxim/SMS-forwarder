package com.example.sms_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Objects;

public class CallMonitor extends BroadcastReceiver {

    public CallMonitor() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), "android.intent.action.SUBSCRIPTION_PHONE_STATE")) {
            return;
        }

        String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        assert phoneState != null;
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING) && incomingNumber != null) {
            int slot = intent.getIntExtra("slot", -1);

            Data myData = new Data.Builder()
                    .putString("message", Functions.request(context, incomingNumber, slot, "call", ""))
                    .build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SendWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance(context).enqueue(myWorkRequest);
        }
    }
}
