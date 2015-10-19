package com.laplasianin.duckhunt;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NesPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
