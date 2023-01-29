package com.child.parent.kidcare.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends BaseActivity {

    private static final String PASSCODE_KEY = "passcode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(R.id.setting_framelayout, mPrefsFragment);
        mFragmentTransaction.commit();

    }

    public  static class PrefsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if(!Util.isParentMobile(getContext().getApplicationContext())) {
                PreferenceScreen screen = getPreferenceScreen();
                Preference pref = getPreferenceManager().findPreference(PASSCODE_KEY);
                screen.removePreference(pref);
            }
        }


    }
}
