package com.example.sms_receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

public class CallMonitor extends BroadcastReceiver {

    public CallMonitor(){};

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent){

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

            }
        },PhoneStateListener.LISTEN_CALL_STATE);

        String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        assert phoneState != null;
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            loadSharedPreferencesLogList(context);
            Data myData = new Data.Builder()
                    .putString("sms", Build.BRAND + " - " + Build.MODEL + "%0A" + "incoming call")
                    .putString("apiToken", apiToken)
                    .putString("chatId", chatId).build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                    .setConstraints(constraints)
                    .setInputData(myData).build();
            WorkManager.getInstance(context).enqueue(myWorkRequest);
        }
    }
}
