
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SmartPantrySettings";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    private SwitchCompat switchExpiryAlerts;
    private TextView txtExpiryStatus;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        txtExpiryStatus = findViewById(R.id.txtExpiryStatus);

        preferences = getSharedPreferences(
                PREFS_NAME, MODE_PRIVATE
        );

        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS, true
        );

        switchExpiryAlerts.setChecked(alertsEnabled);
        updateStatus(alertsEnabled);

        switchExpiryAlerts.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton buttonView,
                            boolean isChecked) {

                        preferences.edit()
                                .putBoolean(KEY_EXPIRY_ALERTS, isChecked)
                                .apply();

                        updateStatus(isChecked);
                    }
                }
        );
    }

    private void updateStatus(boolean enabled) {

        if (enabled) {
            txtExpiryStatus.setText(
                    "Expiry alerts are enabled."
            );
        } else {
            txtExpiryStatus.setText(
                    "Expiry alerts are disabled."
            );
        }
    }
}
