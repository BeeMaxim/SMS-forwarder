package com.example.sms_receiver;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;
import static com.example.sms_receiver.MainActivity.saveSharedPreferencesLogList;
import static com.example.sms_receiver.MainActivity.send;

public class SMSWorker extends Worker {

    Context context;

    public SMSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        loadSharedPreferencesLogList(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        String SMS = getInputData().getString("message");
        Runnable task = () -> {
            try {
                send(apiToken, chatId, SMS);
            } catch (Exception ignored) {
            }
        };

        saveSharedPreferencesLogList(context);
        Thread thread = new Thread(task);
        thread.start();
        return Result.success();
    }
}