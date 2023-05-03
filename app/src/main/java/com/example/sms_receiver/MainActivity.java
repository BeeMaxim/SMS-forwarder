package com.example.sms_receiver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "MyMessages";
    public static String apiToken, chatId, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView version_view = findViewById(R.id.version_view);

        loadSharedPreferencesLogList(this);

        try {
            String[] permissions = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
            ActivityCompat.requestPermissions(this, permissions, 1);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        try {
            String app_version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            version_view.setText(String.format(getString(R.string.version_string), app_version));
        } catch (PackageManager.NameNotFoundException e) {
            version_view.setText(getString(R.string.unknown_version));
        }
    }

    @Override
    protected void onPause() {
        saveSharedPreferencesLogList(this);
        super.onPause();
    }

    public static void loadSharedPreferencesLogList(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        apiToken = mPrefs.getString("apiToken", "");
        chatId = mPrefs.getString("chatId", "");
        name = mPrefs.getString("name", "");
    }

    public static void saveSharedPreferencesLogList(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString("apiToken", apiToken);
        prefsEditor.putString("chatId", chatId);
        prefsEditor.putString("name", name);
        prefsEditor.apply();
    }

    public void onSetterClick(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View window = inflater.inflate(R.layout.api_dialogue, null);
        EditText apiTokenView = window.findViewById(R.id.api_token);
        EditText chatIdView = window.findViewById(R.id.chat_id);
        apiTokenView.setText(apiToken);
        chatIdView.setText(chatId);
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);
        DialogBuilder.setView(window);

        DialogBuilder.setPositiveButton("OK",
                (dialog, id) -> {
                    apiToken = apiTokenView.getText().toString();
                    chatId = chatIdView.getText().toString();
                });
        DialogBuilder.setNegativeButton("cancel",
                (dialog, id) -> dialog.cancel());
        DialogBuilder.create().show();
    }

    public void onNameClick(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View window = inflater.inflate(R.layout.name_dialogue, null);
        EditText nameView = window.findViewById(R.id.name_field);
        nameView.setText(name);
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);
        DialogBuilder.setView(window);

        DialogBuilder.setPositiveButton("OK",
                (dialog, id) -> {
                    name = nameView.getText().toString();
                });
        DialogBuilder.setNegativeButton("cancel",
                (dialog, id) -> dialog.cancel());
        DialogBuilder.create().show();
    }

    public static String info() {
        String result = Build.BRAND + " - " + Build.MODEL;
        if (name != null && !name.equals("")) {
            result += " (" + name + ")";
        }
        result += " ";
        return result;
    }

    public static String format(String text) throws UnsupportedEncodingException {
        String specialSymbols = "_*[]()~`>#+-=|{}.!";
        text = text.replace("\\", "\\\\");

        for (int i = 0; i < specialSymbols.length(); ++i) {
            String current = specialSymbols.substring(i, i + 1);
            text = text.replace(current, "\\" + current);
        }
        return URLEncoder.encode(text, "UTF-8");
    }

    public static void send(String apiToken, String chatId, String text) throws IOException {
        HttpURLConnection con = null;
        String urlToken = "https://api.telegram.org/bot" + apiToken + "/sendMessage";
        String urlParameters = "chat_id=" + chatId + "&text=[" + format(text) + "]&parse_mode=MarkdownV2";
        try {
            URL url = new URL(urlToken);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            try {
                con.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                PrintWriter out = new PrintWriter(con.getOutputStream());
                out.print(urlParameters);
                out.close();
                con.connect();
                con.getInputStream();
            } catch (Exception ignored) {
            }
        } finally {
            assert con != null;
            con.disconnect();
        }
    }
}