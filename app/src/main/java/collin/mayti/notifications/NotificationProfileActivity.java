package collin.mayti.notifications;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.applicationSettingsDB.SettingObject;
import collin.mayti.applicationSettingsDB.SettingViewModel;
import collin.mayti.stockIndicators.IndicatorProfileSensitivity;

public class NotificationProfileActivity extends AppCompatActivity {

    private IndicatorProfileSensitivity currentLevel = IndicatorProfileSensitivity.MEDIUM;

    private int MEDIUM_LEVEL_BAR_MIN = 25;
    private int HIGH_LEVEL_BAR_MIN = 50;
    private int VERY_HIGH_LEVEL_BAR_MIN = 75;
    private int VERY_HIGH_LEVEL_BAR_MAX = 100;

    private String LOW_LEVEL_TITLE = "Low Sensitivity";
    private String MEDIUM_LEVEL_TITLE = "Medium Sensitivity";
    private String HIGH_LEVEL_TITLE = "High Sensitivity";
    private String VERY_HIGH_LEVEL_TITLE = "Very High Sensitivity";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_profile_setting);

        // Here I set my custom app theme for the action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        TextView alertSensitivityLevelTxt = findViewById(R.id.alertSensitvityLevelTxt);
        TextView alertSensitivityLevelDesc = findViewById(R.id.alertSensivityLevelDescTxt);

        Button saveBtn = findViewById(R.id.saveAlertProfileSensitivityBtn);

        // Get the current level.
        try {
            currentLevel = getCurrentSensitivityLevel();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SeekBar sensitivitySeekBar = findViewById(R.id.sensitivitySeekBar);
        // Start the seek bar in the middle of the current level min and max.
        switch (currentLevel) {
            case LOW:
                sensitivitySeekBar.setProgress(MEDIUM_LEVEL_BAR_MIN/2);
                alertSensitivityLevelTxt.setText(LOW_LEVEL_TITLE);
                break;
            case MEDIUM:
                sensitivitySeekBar.setProgress((MEDIUM_LEVEL_BAR_MIN + HIGH_LEVEL_BAR_MIN)/2);
                alertSensitivityLevelTxt.setText(MEDIUM_LEVEL_TITLE);
                break;
            case HIGH:
                sensitivitySeekBar.setProgress((HIGH_LEVEL_BAR_MIN + VERY_HIGH_LEVEL_BAR_MIN)/2);
                alertSensitivityLevelTxt.setText(HIGH_LEVEL_TITLE);
                break;
            case VERY_HIGH:
                sensitivitySeekBar.setProgress((VERY_HIGH_LEVEL_BAR_MIN + VERY_HIGH_LEVEL_BAR_MAX)/2);
                alertSensitivityLevelTxt.setText(VERY_HIGH_LEVEL_TITLE);
                break;
        }

        // Listen for changes.
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress < MEDIUM_LEVEL_BAR_MIN) {
                    // The current setting is at LOW.
                    alertSensitivityLevelTxt.setText(LOW_LEVEL_TITLE);
                } else {
                    if (progress < HIGH_LEVEL_BAR_MIN) {
                        // The current setting is MEDIUM.
                        alertSensitivityLevelTxt.setText(MEDIUM_LEVEL_TITLE);
                    } else {
                        if (progress < VERY_HIGH_LEVEL_BAR_MIN) {
                            // The current setting is HIGH.
                            alertSensitivityLevelTxt.setText(HIGH_LEVEL_TITLE);
                        } else {
                            // The current setting is VERY_HIGH
                            alertSensitivityLevelTxt.setText(VERY_HIGH_LEVEL_TITLE);
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the current alert sensitivity level setting to the settings database.
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private IndicatorProfileSensitivity getCurrentSensitivityLevel() throws ExecutionException, InterruptedException {
        SettingViewModel settingViewModel = ViewModelProviders.of((FragmentActivity) this).get(SettingViewModel.class);
        SettingObject currentSetting = settingViewModel.readSetting("INDICATOR_SENSITIVITY_LEVEL");
        switch (currentSetting.getSettingValue()) {
            case "LOW":
                return IndicatorProfileSensitivity.LOW;
            case "MEDIUM":
                return IndicatorProfileSensitivity.MEDIUM;
            case "HIGH":
                return IndicatorProfileSensitivity.HIGH;
            case "VERY_HIGH":
                return IndicatorProfileSensitivity.VERY_HIGH;
        }
        return null;
    }
}
