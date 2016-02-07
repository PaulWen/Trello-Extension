package de.wenzel.paul.trelloextansion;

import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerService extends Service {

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

            // Notification löschen
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra("notificationId", -1));

            // Date Picker öffnen
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog fromDatePickerDialog = new DatePickerDialog(DatePickerService.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Datum holen
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);

                    // Lockscreen wieder aktivieren
                    kl.reenableKeyguard();

                    // Karte auf das neue Datum verschieben
                    resheduleCard(intent.getStringExtra("cardId"), newDate);
                }


            },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            fromDatePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            kl=km.newKeyguardLock("My_App");
            kl.disableKeyguard();

            fromDatePickerDialog.show();


                // die Notification Section schließen, damit der Date Picker gesehen werden kann
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);
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
     * Die Methode ändert das Fälligkeitsdatum der gewünschte Karte auf das gewünschte Datum.
     *
     * @param cardeId
     * @param newDate
     */
    private void resheduleCard(String cardeId, Calendar newDate) {
        // Datum in String umwandeln
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newDateString = dateFormat.format(newDate.getTime());;

        // die gewünschte Karte archivieren
        try {
            URL url = new URL("https://trello.com/1/cards/" + cardeId + "/due?value=" + newDateString + "T08:00:00.000Z" + "&key=" + MainService.APPLICATION_KEY + "&token=" + MainService.USER_TOKEN);
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
