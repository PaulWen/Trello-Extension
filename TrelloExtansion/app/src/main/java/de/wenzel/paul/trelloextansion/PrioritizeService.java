package de.wenzel.paul.trelloextansion;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrioritizeService extends Service {

    private  KeyguardManager.KeyguardLock kl;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);

        if (isNetworkAvailable()) {
            // JSON Parsing erlauben
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // prüfen ob die Prio-1 entfernt oder gesetzt werden soll
            if (!intent.getBooleanExtra("highPriority", false)) {
                // Prio setzten
                addHighPriority(intent.getStringExtra("cardId"));
            } else {
                // Prio entfernen
                removeLabel(intent.getStringExtra("cardId"), intent.getStringExtra("highPriorityLabelId"));
            }

            // alles neu laden
            Intent refreshIntent = new Intent("TrelloExtansion");
            refreshIntent.setClass(getApplicationContext(), RefreshService.class);
            getApplicationContext().startService(refreshIntent);
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
     * Die Methode fügt der gewünschten Karte ein rotes label hinzu.
     *
     * @param cardeId
     */
    private void addHighPriority(String cardeId) {
        // der gewünschten Karte ein rotes Label hinzufügen
        try {
            URL url = new URL("https://trello.com/1/cards/" + cardeId + "/labels?color=red&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String jsonString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();

//            Log.d("Test", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode löschet das gewünschte Labele von der gewünschten Karte.
     *
     * @param cardId
     * @param labelId
     */
    private void removeLabel(String cardId, String labelId) {
        // das gewünschte Lable von der gewünschten Karte löschen
        try {
            URL url = new URL("https://trello.com/1/cards/" + cardId + "/idLabels/" + labelId + "?key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("DELETE");
            httpCon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Methode gibt die ID von einem roten Label aus.
     *
     * @param boardId
     * @return ID von einem roten Label
     */
    private String idOfRedLabel(String boardId) {
        try {
            // alle Labels vom Board auslesen
            URL url = new URL("https://trello.com/1/boards/" + boardId + "/labels?key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));
            String jsonString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();

            JSONArray labelsJsonArray = new JSONArray(jsonString);
            Log.d("Labels Array:", jsonString);
            // ein rotes Label raussuchen
            for (int i = 0; i < labelsJsonArray.length(); i++) {
                JSONObject lable = labelsJsonArray.getJSONObject(i);

                // wenn ein rotes Lable dabei ist, dann hat die Karte eine hohe Priorität!
                if (lable.getString("color").equals("red")) {
                   return lable.getString("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
