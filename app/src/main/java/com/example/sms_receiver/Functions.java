package com.example.sms_receiver;

import static android.content.Context.BATTERY_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.core.app.ActivityCompat;

class Functions {

    public static String request(Context context, String from, int slot, String messageType, String messageText) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int power = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        String powerLevel = "[" + power + "%]";

        if (power <= 5) {
            powerLevel += ' ' + "\uD83D\uDD34";
        } else if (power <= 20) {
            powerLevel += ' ' + "\uD83D\uDFE1";
        } else {
            powerLevel += ' ' + "\uD83D\uDFE2";
        }

        SubscriptionInfo currentSubscription;
        String simName = "";
        String simNumber = "undefined";

        if (slot >= 0 && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionManager manager = SubscriptionManager.from(context);
            currentSubscription = manager.getActiveSubscriptionInfoForSimSlotIndex(slot);
            simName = currentSubscription.getDisplayName().toString();
            simNumber = currentSubscription.getNumber();
        }

        String ModelInfo = MainActivity.info() + powerLevel + "\n";
        String to = "SIM " + (slot + 1) + ": " + simName + ", " + simNumber + "\n";
        return ModelInfo + to + messageType + " from: " + from + "\n" + messageText;
    }

    public static int getSlotBySubscription(Context context, int subscription) {
        int slot = -1;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionManager manager = SubscriptionManager.from(context);
            SubscriptionInfo currentSubscription = manager.getActiveSubscriptionInfo(subscription);
            slot = currentSubscription.getSimSlotIndex();
        }

        return slot;
    }
}
