package com.jmartin.writeily.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jmartin.writeily.R;

/**
 * Created by jeff on 2014-04-11.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
