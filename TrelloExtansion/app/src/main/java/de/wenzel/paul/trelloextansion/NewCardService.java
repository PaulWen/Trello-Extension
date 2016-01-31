package de.wenzel.paul.trelloextansion;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewCardService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        if (isNetworkAvailable()) {
            // JSON Parsing erlauben
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // neue Karte anlegen
            String shortIdNewCard = createEmptyNewCard(MainService.STANDARD_LIST_ID);
            // neue Karte öffnen
            callTrelloApp(shortIdNewCard);
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
     * Die Methode erstellt eine neue Karte in die gewünschte Liste und gibt anschließend die shortUrl dieser neu erstellten
     * Karte zurück.
     *
     * @param listId
     * @return shortUrl der neuen Karte
     */
    private String createEmptyNewCard(String listId) {
        // eine neue Karte in der gewünschten Liste erstellen
        try {
            URL url = new URL("https://trello.com/1/cards?name=%00&desc=%00&due=null&idList=" + listId + "&urlSource=null&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
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

            JSONObject newCard = new JSONObject(jsonString);
            return newCard.getString("shortUrl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Die Methode ruft die Karte in der Trello App auf, welche hinter der mitgegebenen ShorURL zu finden ist.
     *
     * @param cardShortUrl
     */
    private void callTrelloApp(String cardShortUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(cardShortUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
