package de.wenzel.paul.trelloextansion;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class ArchivateService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (isNetworkAvailable()) {
            // JSON Parsing erlauben
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Notification löschen
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra("notificationId", -1));

            // Karte archivieren
            closeCard(intent.getStringExtra("cardId"));
        } else {
            Toast.makeText(this, "Aktion derzeit nicht möglich, da keine Internetverbindung besteht!", Toast.LENGTH_LONG).show();
        }

        // Service beenden
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Die Methode archiviert die gewünschte Karte.
     *
     * @param cardeId
     */
    private void closeCard(String cardeId) {
        // die gewünschte Karte archivieren
        try {
            URL url = new URL("https://trello.com/1/cards/" + cardeId + "/closed?value=true&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("PUT");
            httpCon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode prüft, ob eine Internetverbindung besteht.
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
