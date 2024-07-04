package com.example.iisconnect;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.type.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    Messenger myService;
    Messenger myReceiver;
    boolean connect = false;
    Intent serviceIntent;
    Button connectButton;
    TextView statusText;
    TextView dataText;
    ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        connectButton = (Button) findViewById(R.id.connectBtn);
        statusText = (TextView) findViewById(R.id.statusText);
        dataText = (TextView) findViewById(R.id.dataText);
        progressBar = findViewById(R.id.progressBar);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        ((WebSettings) webSettings).setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/leaflet/map.html");
    }

    public void setButtonText(String text) {
        connectButton.setText(text);
    }

    public void setStatusText(String text) {
        statusText.setText(text);
    }

    private void setDataText(String data) throws JSONException {

        String testo;


        JSONObject jsonObject = new JSONObject(data);
        double latitude = jsonObject.getDouble("latitude");
        double longitude = jsonObject.getDouble("longitude");

        testo = "Latitudine: " + latitude + "\n Longitudine: " + longitude;


        dataText.setText(testo);

        aggiornaMappa(latitude, longitude);
    }

    private void aggiornaMappa(double latitude, double longitude) {
        String javascriptCommand = "aggiungiMarker(" + latitude + ", " + longitude + ");";
        webView.evaluateJavascript(javascriptCommand, null);
    }


    public void connectBtnHandler(View v) throws InterruptedException {

        if (!connect) {
            progressBar.setVisibility(View.VISIBLE);
            setStatusText("Tentativo di connessione in corso...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        serviceIntent = new Intent();
                        serviceIntent.setComponent(new ComponentName("com.example.iisconnectservice", "com.example.iisconnectservice.MyService"));
                        if (!bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)) {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        setStatusText("Errore durante la connessione");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, 3000);


        } else {
            setStatusText("Disconnessione in corso...");
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                unbindService(connection);
                                stopService(serviceIntent);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        setButtonText("Connetti");
                                        setStatusText("Non Connesso");
                                    }
                                });

                            }
                        });
                        t.start();

                    } catch (Exception e) {
                        setStatusText("Errore durante la disconnessione");

                    }
                }
            }, 3000);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = new Messenger(service);
            myReceiver = new Messenger(new receiverHandler());
            connect = true;

            Bundle bundle = new Bundle();
            bundle.putInt("key", 1);

            Message msg = Message.obtain(null, 1);
            msg.obj = bundle;
            msg.replyTo = myReceiver;

            try {
                myService.send(msg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            progressBar.setVisibility(View.GONE);
            setButtonText("Disconnetti");
            setStatusText("Connesso al servizio");
        }


        @Override
        public void onBindingDied(ComponentName name) {
            connect = false;
            progressBar.setVisibility(View.GONE);
            setButtonText("Connetti");
            setStatusText("Non Connesso");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connect = false;
            progressBar.setVisibility(View.GONE);
            setButtonText("Connetti");
            setStatusText("Non Connesso");
        }

    };

    class receiverHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = (Bundle) msg.obj;
                    String data = bundle.getString("data");
                    try {
                        setDataText(data);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                default:
                    super.handleMessage(msg);
            }
        }


    }
}