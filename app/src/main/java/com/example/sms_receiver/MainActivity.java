package com.example.sms_receiver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "MyMessages";
    public static String apiToken, chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPreferencesLogList(this);
    }

    @Override
    protected void onPause(){
        saveSharedPreferencesLogList(this);
        super.onPause();
    }

    public static void loadSharedPreferencesLogList(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        apiToken = mPrefs.getString("apiToken", "");
        chatId = mPrefs.getString("chatId", "");
    }

    public static void saveSharedPreferencesLogList(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("apiToken", apiToken);
        prefsEditor.putString("chatId", chatId);
        prefsEditor.apply();
    }

    public void onSetterClick(View view){
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

    public static void send(String apiToken, String chatId, String text) throws IOException {
        HttpURLConnection con = null;
        String urlToken = "https://api.telegram.org/bot"+apiToken+"/sendMessage";
        String urlParameters = "chat_id="+chatId+"&text="+text+"&parse_mode=MarkDown";

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
            }catch (Exception ignored){};
        } finally {
            assert con != null;
            con.disconnect();
        }
    }
}