package com.example.iisconnectservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyService extends Service {

    public boolean stop = false;
    final Messenger myMessenger = new Messenger(new ReceiveMessengerHandler());
    Messenger replyMessenger = null;

    public MyService() {
    }

    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stop = false;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        stop = false;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!stop) {
                    String data = getDataFromURL();
                    Log.d("Info", data);

                    try {
                        if (replyMessenger != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            Message msg = Message.obtain(null, 1);
                            msg.obj = bundle;
                            replyMessenger.send(msg);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();

        return myMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Info", "Unbind");
        return super.onUnbind(intent);
    }

    class ReceiveMessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: { // inizio attivià
                    replyMessenger = msg.replyTo;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public String getDataFromURL() {
        String apiUrl = "https://api.wheretheiss.at/v1/satellites/25544";
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Impostazione dell'header X-RapidAPI-Key
            //urlConnection.setRequestProperty("X-RapidAPI-Key", "7cf6ed338bmsh6997cdaf7a1cc06p1a0e7fjsn6a5ea03909b1");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Chiudi la connessione
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

}