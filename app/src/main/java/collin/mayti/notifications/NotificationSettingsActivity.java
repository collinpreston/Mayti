package collin.mayti.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.TimeUnit;

import collin.mayti.R;

public class NotificationSettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static String SCAN_FREQUENCY_PREFERENCE_ID = "pref_scanFrequency";

    public static String INDICATOR_SENSITIVITY_LEVEL_PREFERENCE_ID = "pref_sensitivityLevel";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Set the description summary for scan frequency.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String scanFrequency = sharedPref.getString(SCAN_FREQUENCY_PREFERENCE_ID, "");
        long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(scanFrequency));
        Preference pref = findPreference(SCAN_FREQUENCY_PREFERENCE_ID);
        pref.setSummary("Scan every " + minutes + " minutes");

        // Set the description summary for scan sensitivity.
        String scanSensitivity = sharedPref.getString(INDICATOR_SENSITIVITY_LEVEL_PREFERENCE_ID, "");
        Preference sensitivityPref = findPreference(INDICATOR_SENSITIVITY_LEVEL_PREFERENCE_ID);
        sensitivityPref.setSummary(scanSensitivity);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(SCAN_FREQUENCY_PREFERENCE_ID)) {
            Preference frequencyPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String scanFrequency = sharedPref.getString(SCAN_FREQUENCY_PREFERENCE_ID, "");
            long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(scanFrequency));
            frequencyPref.setSummary("Scan every " + minutes + " minutes");
        }
        if (key.equals(INDICATOR_SENSITIVITY_LEVEL_PREFERENCE_ID)) {
            Preference sensitivityPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String indicatorSensitivity = sharedPref.getString(INDICATOR_SENSITIVITY_LEVEL_PREFERENCE_ID, "");
            sensitivityPref.setSummary(indicatorSensitivity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }
}
