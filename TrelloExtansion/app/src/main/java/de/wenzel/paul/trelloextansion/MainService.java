package de.wenzel.paul.trelloextansion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Die Klasse {@link MainService} interagiert mit Trello über deren Web-API.
 *
 * Trello Application Key: 333e701bd67012a2f36ff556070af5de
 * Trello Application Geheim: f9fe8fe0f04229b4ec5ead7c1ae0ff120595587fbe903df0e1f4698968c34ab6
 *
 * Notes Trello API:
 * All API requests go to https://api.trello.com
 * The /1 part of the URI is the API version.
 * The /boards part means that we're addressing Trello's collection of boards
 * The /4d5ea62fd76aa1136000000c part is the id of the board that we want to interact with. You'll notice that the board id is also part of the board's URL in Trello
 *
 * Ziel der App:
 * - alle 15min soll sie sich aktualisieren bzw. in der App durch drücken eines Buttons
 * - alle fälligen Aufgaben sollen als permanent Benachrichtigungen angezeigt werden
 * - die Benachrichtigung kann man aufklappen und erhält zwei Buttons: 1. Aufgabe abhaken (archivieren) 2. Aufgabe ansehen (öffnet Trello App)
 *
 *
 * @author Paul Wenzel
 *
 */
public class MainService extends Service {

/////////////////////////////////////////////////Datenfelder/////////////////////////////////////////////////

    public static final String APPLICATION_KEY = "333e701bd67012a2f36ff556070af5de";
    public static final String USER_TOKEN = "e079cec790ac05585585339d166b3201594d7c54d2e4a076c815137e64474da8";
    public static final String STANDARD_LIST_ID = "56ac7232a03d5d6ebdf964bb";

/////////////////////////////////////////////////Konstruktor/////////////////////////////////////////////////


    @Override
    public void onCreate() {
        super.onCreate();

        // Refresh Service aufrufen
        Intent i = new Intent("TrelloExtansion");
        i.setClass(getApplicationContext(), RefreshService.class);
        getApplicationContext().startService(i);

        // Service shedulen, damit er sich immer selber aufruft
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(this, MainService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), intent, 0);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 15); // alle 15 Minuten aktualisieren!
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

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

//////////////////////////////////////////////Getter und Setter//////////////////////////////////////////////






///////////////////////////////////////////////geerbte Methoden//////////////////////////////////////////////






//////////////////////////////////////////////////Methoden///////////////////////////////////////////////////

    private static void authorizeAppConnection() {
        // der User muss die nachfolgende URL aufrufen und sich dort anmelden, damit meine Applikation für immer lesen und schreiben darf:
        // https://trello.com/1/authorize?key=333e701bd67012a2f36ff556070af5de&name=Trello Due Date Notifications&expiration=never&response_type=token&scope=read,write
        // wenn der Nutzer dies gemacht hat, wird ein Token zurück geschickt, welches die App ab sofort verwenden kann, um auf die Daten vom Trello-Account zuzugreifen
    }

    /**
     * Die Methode REarchiviert die gewünschte Karte.
     *
     * @param cardeID
     */
    private void openCard(String cardeID) {
        // die gewünschte Karte archivieren
        try {
            URL url = new URL("https://trello.com/1/cards/" + cardeID + "/closed?value=false&key=" + APPLICATION_KEY + "&token=" + USER_TOKEN);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("PUT");
            httpCon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
