package com.example.sms_receiver;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import static com.example.sms_receiver.MainActivity.send;

public class SMSWorker extends Worker {

    public SMSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String SMS = getInputData().getString("sms");
        String apiToken = getInputData().getString("apiToken");
        String chatId = getInputData().getString("chatId");
        Runnable task = () -> {
            try {
                send(apiToken, chatId, SMS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        return Result.success();
    }
}