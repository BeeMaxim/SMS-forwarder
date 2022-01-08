package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;
import static com.example.sms_receiver.MainActivity.saveSharedPreferencesLogList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_list);
        loadSharedPreferencesLogList(this);
        final PackageManager pm = this.getPackageManager();
        List<AppInfo> packages = new ArrayList<AppInfo>();
        for (ApplicationInfo el : pm.getInstalledApplications(PackageManager.GET_META_DATA)){
            if ((el.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                packages.add(new AppInfo(pm, el));
            }
        }
        AppsAdapter mainAdapter = new AppsAdapter(this, packages);
        RecyclerView recycler = findViewById(R.id.apps_recycler);
        recycler.setAdapter(mainAdapter);
    }

    @Override
    protected void onPause(){
        saveSharedPreferencesLogList(this);
        super.onPause();
    }
}