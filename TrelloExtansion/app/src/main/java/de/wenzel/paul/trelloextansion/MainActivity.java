package de.wenzel.paul.trelloextansion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.refreshButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("TrelloExtansion");
                i.setClass(getApplicationContext(), MainService.class);
                getApplicationContext().startService(i);
                Toast.makeText(MainActivity.this, "Aktualisiere...", Toast.LENGTH_LONG).show();
            }
        });
    }
}
