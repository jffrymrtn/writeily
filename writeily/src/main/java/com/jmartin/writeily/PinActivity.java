package com.jmartin.writeily;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by jeff on 2014-08-20.
 */
public class PinActivity extends Activity {

    private Context context;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfPinRequired();

        setContentView(R.layout.activity_note);
        context = getApplicationContext();

        pin = PreferenceManager.getDefaultSharedPreferences(this).getString("user_pin", "");

        if (pin.isEmpty()) {

        }
        super.onCreate(savedInstanceState);
    }

    private void checkIfPinRequired() {
        boolean pinRequired = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_pin", false);

        if (pinRequired) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }
}
