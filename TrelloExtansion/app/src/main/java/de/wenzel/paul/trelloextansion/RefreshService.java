package de.wenzel.paul.trelloextansion;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.wenzel.paul.dataobjects.TrelloCardDataObject;

public class RefreshService extends Service {

    private int notificationIdCounter;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // prüfen, ob es eine Internetverbindung gibt
        if (isNetworkAvailable()) {
//            Toast.makeText(this, "Aktualisiere...", Toast.LENGTH_LONG).show();

            notificationIdCounter = 0;

            // JSON Parsing erlauben
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // die neuen Daten runterladen
            ArrayList<TrelloCardDataObject> trelloCardsDataObjects = allUserCards();

            // Clear all notification
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
            notificationIdCounter = 0;

            // füge Aufgabe hinzu Notification
            // New Card Intent vorbereiten
            Intent newCard = new Intent(Intent.ACTION_VIEW);
            newCard.setClass(this, NewCardService.class);
            PendingIntent pendingNewCardIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), newCard, 0);

            // Refresh Intent vorbereiten
            Intent refreshIntent = new Intent(Intent.ACTION_VIEW);
            refreshIntent.setClass(this, RefreshService.class);
            PendingIntent pendingRefreshIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), refreshIntent, 0);

            // Build notification
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Neue Aufgabe...")
                    .setSmallIcon(R.drawable.task)
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.new_task)).getBitmap())
                    .setContentIntent(pendingNewCardIntent)
                    .addAction(R.drawable.ic_refresh_black_24dp, "Aktualisieren", pendingRefreshIntent)
                    .setPriority(Notification.PRIORITY_LOW).build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // die Notification soll nicht gelöscht werden können
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;

            notificationManager.notify(notificationIdCounter++, notification);

            // alle Notifications erstellen, welche keine hohe Prio haben
            for (TrelloCardDataObject object : trelloCardsDataObjects) {

                Calendar date = new GregorianCalendar();
                // reset hour, minutes, seconds and millis
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                date.add(Calendar.DATE, 1);

                // wenn die Aufgabe vor Heute oder genau Heute fällig ist, diese anzeigen & keine hohe Prio hat
                if (!object.isHighPriority() && object.getDueDate() != null && object.getDueDate().compareTo(new Date(date.getTimeInMillis())) <= 0) {
                    createNotification(object);

                }
            }

            // alle Notifications erstellen, welche eine hohe Prio haben
            for (TrelloCardDataObject object : trelloCardsDataObjects) {

                Calendar date = new GregorianCalendar();
                // reset hour, minutes, seconds and millis
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                date.add(Calendar.DATE, 1);

                // wenn die Aufgabe vor Heute oder genau Heute fällig ist, diese anzeigen & eine hohe Prio hat
                if (object.isHighPriority() && object.getDueDate() != null && object.getDueDate().compareTo(new Date(date.getTimeInMillis())) <= 0) {
                    createNotification(object);

                }
            }
        } else {
            Toast.makeText(this, "KEINE INTERNETVERBINDUNG!", Toast.LENGTH_LONG).show();
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
     * Die Methode erstellt eine Notification.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNotification(TrelloCardDataObject trelloCardDataObject) {
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(trelloCardDataObject.getCardShortUrl()));
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Archievieren Intent vorbereiten
        Intent archivateIntent = new Intent(Intent.ACTION_VIEW);
        archivateIntent.setClass(this, ArchivateService.class);
        archivateIntent.putExtra("cardId", trelloCardDataObject.getCardId());
        archivateIntent.putExtra("notificationId", notificationIdCounter);
        PendingIntent pendingArchivateIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), archivateIntent, 0);

        // Reshedule Intent vorbereiten
        Intent resheduleIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        resheduleIntent.setClass(this, DatePickerService.class);
        resheduleIntent.putExtra("cardId", trelloCardDataObject.getCardId());
        resheduleIntent.putExtra("notificationId", notificationIdCounter);
        PendingIntent pendingResheduleIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), resheduleIntent, 0);

        // Prioritize Intent vorbereiten
        Intent prioritizeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        prioritizeIntent.setClass(this, PrioritizeService.class);
        prioritizeIntent.putExtra("cardId", trelloCardDataObject.getCardId());
        prioritizeIntent.putExtra("highPriority", trelloCardDataObject.isHighPriority());
        prioritizeIntent.putExtra("highPriorityLabelId", trelloCardDataObject.getHighPriorityLabelId());
        PendingIntent pendingPrioritizeIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), prioritizeIntent, 0);


        Notification notification = null;
        if (trelloCardDataObject.isHighPriority()) {
            // Build notification
            notification = new Notification.Builder(this)
                    .setContentTitle(trelloCardDataObject.getBoardName() + ": " + trelloCardDataObject.getListName())
                    .setContentText(trelloCardDataObject.getCardName())
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.task)).getBitmap())
                    .setSmallIcon(R.drawable.ic_watch_later_black_24dp, 0)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.ic_done_black_24dp, "Fertig", pendingArchivateIntent)
                    .addAction(R.drawable.ic_event_black_24dp, "Datum", pendingResheduleIntent)
                    .addAction(R.drawable.ic_favorite_black_24dp, "Wichtig", pendingPrioritizeIntent)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setColor(Color.RED)
                    .setShowWhen(false).build();
        } else {
            // Build notification
            notification = new Notification.Builder(this)
                    .setContentTitle(trelloCardDataObject.getBoardName() + ": " + trelloCardDataObject.getListName())
                    .setContentText(trelloCardDataObject.getCardName())
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.task)).getBitmap())
                    .setSmallIcon(R.drawable.ic_watch_later_black_24dp, 0)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.ic_done_black_24dp, "Fertig", pendingArchivateIntent)
                    .addAction(R.drawable.ic_event_black_24dp, "Datum", pendingResheduleIntent)
                    .addAction(R.drawable.ic_favorite_black_24dp, "Wichtig", pendingPrioritizeIntent)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setShowWhen(false).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // die Notification soll nicht gelöscht werden können
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(notificationIdCounter++, notification);
    }

    /**
     * Die Methode ruft die Karte in der Trello App auf, welche hinter der mitgegebenen ShorURL zu finden ist.
     *
     * @param cardShortUrl
     */
    private void callTrelloApp(String cardShortUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(cardShortUrl));
        startActivity(intent);
    }

    /**
     * Die Methode erstellt eine Liste mit allen Karten eines Trello Users
     *
     * @return
     */
    private ArrayList<TrelloCardDataObject> allUserCards() {
        ArrayList<TrelloCardDataObject> trelloCardsDataObjects = new ArrayList<>();

        //... JSON Array mit allen Boards
        JSONArray trelloBoardsJsonArray = null;

        try {
            // JSON Array mit allen Boards hohlen
            URL boardUrl = new URL("https://trello.com/1/members/me/boards?filter=open&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);

            // JSON Array String hohlen
            URLConnection yc = boardUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String jsonString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();
            // JSON Array Objekt erzeugen
            trelloBoardsJsonArray = new JSONArray(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // für jedes Board...
        for (int i = 0; i < trelloBoardsJsonArray.length(); i++) {
            try {
                JSONObject board = trelloBoardsJsonArray.getJSONObject(i);

                //... JSON Array mit allen Karten
                JSONArray trelloCardsJsonArray = null;

                try {
                    // JSON Array mit allen Karten hohlen, die noch nicht bearbeitet wurden (= "open" sind)
                    URL cardUrl = new URL("https://trello.com/1/boards/" + board.get("id") + "/cards?filter=open&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);

                    // JSON Array String hohlen
                    URLConnection yc = cardUrl.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            yc.getInputStream()));
                    String jsonString = "";
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        jsonString += inputLine;
                    }
                    in.close();
                    // JSON Array Objekt erzeugen
                    trelloCardsJsonArray = new JSONArray(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // JSON Objekte, welche die Karten beschreiben in TrelloCardDataObject-Objekte packen
                for (int j = 0; j < trelloCardsJsonArray.length(); j++) {
                    TrelloCardDataObject cardDataObject = null;

                    try {
                        // JSON-Card-Objekt holen
                        JSONObject card = trelloCardsJsonArray.getJSONObject(j);

                        // JSON-List-Objekt holen
                        JSONObject list = null;
                        try {
                            URL urlGetListID = new URL("https://trello.com/1/lists/" + card.get("idList").toString() + "?cards=none&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
                            HttpURLConnection httpConGetListID = (HttpURLConnection) urlGetListID.openConnection();
                            httpConGetListID.setRequestMethod("GET");
                            BufferedReader in = new BufferedReader(new InputStreamReader(
                                    httpConGetListID.getInputStream()));
                            String jsonString = "";
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                jsonString += inputLine;
                            }
                            in.close();

                            list = new JSONObject(jsonString);
//                            Log.d("LISTE:", list.getString("name") + " - " + list.getString("id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // gucken, ob eine Karte ein rotes Lable hat und damit eine hohe Priorität
                        boolean highPriority = false;
                        String highPriorityLabelId = null;
                        JSONArray cardLabelsJsonArray = card.getJSONArray("labels");
                        for (int k = 0; k < cardLabelsJsonArray.length(); k++) {
                            JSONObject lable = cardLabelsJsonArray.getJSONObject(k);

                            // wenn ein rotes Lable dabei ist, dann hat die Karte eine hohe Priorität!
                            if (lable.getString("color").equals("red")) {
                                highPriority = true;
                                highPriorityLabelId = lable.getString("id");
                                break;
                            }
                        }

                        // das TrelloCardDataObject erstellen mit allen Infos
                        cardDataObject = new TrelloCardDataObject(card.get("id").toString(), card.get("shortUrl").toString(), board.getString("id"), board.get("name").toString(), list.get("name").toString(), card.get("name").toString(), convertDate(card.get("due").toString()), highPriority, highPriorityLabelId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (cardDataObject != null) {
                        trelloCardsDataObjects.add(cardDataObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trelloCardsDataObjects;
    }

    /**
     * Die Methode erstellt von einem Trello-Date-String das dazugehörige Java-Date-Object.
     *
     * @param date
     * @return
     */
    private static Date convertDate(String date) {
        if (!date.equals("null")) {
            // Date-String in ein Date-Objekt umwandeln
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date dateObject = null;
            try {
                dateObject = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return dateObject;
        } else {
            return null;
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