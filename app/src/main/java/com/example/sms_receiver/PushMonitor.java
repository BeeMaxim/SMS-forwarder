package com.example.sms_receiver;

import android.annotation.SuppressLint;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class PushMonitor extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String to_send = sbn.getPackageName();
        Data myData = new Data.Builder().putString("sms", to_send).build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SMSWorker.class)
                .setConstraints(constraints)
                .setInputData(myData).build();
        WorkManager.getInstance().enqueue(myWorkRequest);
    }
}
