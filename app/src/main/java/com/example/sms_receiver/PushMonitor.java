package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.ChosenApps;
import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class PushMonitor extends NotificationListenerService {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String name = sbn.getPackageName();
        PackageManager packageManager = getPackageManager();
        String label;
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(name, 0);
            label = packageManager.getApplicationLabel(info).toString();
        }catch (Exception e){
            label = "Not defined";
        }
        sbn.getPackageName();
        loadSharedPreferencesLogList(getApplicationContext());
        if (!ChosenApps.containsKey(name) || !ChosenApps.get(name)){
            return;
        }
        Notification nt = sbn.getNotification();
        Bundle ex = nt.extras;
        String text;
        try{
            text = ex.getCharSequence(Notification.EXTRA_TEXT).toString();
        }
        catch(Exception e){
            text = "Not defined";
        }
        Data myData = new Data.Builder()
                .putString("sms", MainActivity.info() + "App: " + label + "\n" + text)
                .build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                .setConstraints(constraints)
                .setInputData(myData).build();
        WorkManager.getInstance().enqueue(myWorkRequest);
    }
}
