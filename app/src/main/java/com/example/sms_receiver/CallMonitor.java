package com.example.sms_receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

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
            Data myData = new Data.Builder()
                    .putString("sms", MainActivity.info() + "incoming call" + "%0A" + incomingNumber)
                    .build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance().enqueue(myWorkRequest);
        }
    }
}
