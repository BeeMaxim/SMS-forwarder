package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.ChosenApps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

class AppInfo {
    Drawable logo;
    String label, packageName;
    boolean isChosen;
    AppInfo(PackageManager pm, ApplicationInfo app){
        try {
            logo = pm.getApplicationIcon(app.packageName);
        }catch (PackageManager.NameNotFoundException ignored){}
        label = pm.getApplicationLabel(app).toString();
        packageName = app.packageName;
        if (!ChosenApps.containsKey(app.packageName)){
            isChosen = false;
        }
        else isChosen = ChosenApps.get(app.packageName);
    }
}
