package com.jmartin.writeily.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.jmartin.writeily.R;

/**
 * Created by jeff on 2014-04-11.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set style for fitsSystemWindows
        setTheme(R.style.PreferencesStyle);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
