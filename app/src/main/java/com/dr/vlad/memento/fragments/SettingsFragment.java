package com.dr.vlad.memento.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.dr.vlad.memento.R;

/**
 * Created by vlad.drinceanu on 28.02.2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
