package com.example.sms_receiver;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.sms_receiver.MainActivity.apiToken;
import static com.example.sms_receiver.MainActivity.chatId;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;
import static com.example.sms_receiver.MainActivity.send;

public class SendWorker extends Worker {

    public SendWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        loadSharedPreferencesLogList(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        String message = getInputData().getString("message");
        Runnable task = () -> {
            try {
                send(apiToken, chatId, message);
            } catch (Exception ignored) {
            }
        };

        Thread thread = new Thread(task);
        thread.start();
        return Result.success();
    }
}