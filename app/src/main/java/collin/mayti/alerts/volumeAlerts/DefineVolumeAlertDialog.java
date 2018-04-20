package collin.mayti.alerts.volumeAlerts;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionViewModel;
import collin.mayti.watchlist.WatchlistViewModel;

public class DefineVolumeAlertDialog extends DialogFragment {

    private View rootView;
    private String symbol;

    private AlertSubscriptionViewModel alertViewModel;
    private WatchlistViewModel watchlistViewModel;

    String avgVolume = "";
    double avgVolumeNum;

    private boolean[] volumeAlertsSet = {false, false, false, false, false};

    public static DefineVolumeAlertDialog newInstance(String symbol) {
        DefineVolumeAlertDialog f = new DefineVolumeAlertDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_define_volume_alert, container, false);

        // TODO: Display the current volume average to the user.

        MultiStateToggleButton volumeAlertMtb = rootView.findViewById(R.id.volumeNewAlertMtb);
        volumeAlertMtb.enableMultipleChoice(true);

        volumeAlertMtb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (volumeAlertsSet[position]) {
                    volumeAlertsSet[position] = false;
                } else {
                    volumeAlertsSet[position] = true;
                }
                //Log.d(TAG, "Position: " + position);
            }
        });

        Activity activity = getActivity();
        watchlistViewModel = ViewModelProviders.of((FragmentActivity) activity).get(WatchlistViewModel.class);
        alertViewModel = ViewModelProviders.of((FragmentActivity) activity).get(AlertSubscriptionViewModel.class);

        try {
            avgVolume = watchlistViewModel.findStockItemBySymbold(symbol).getAverageVolume();
            avgVolumeNum = Double.parseDouble(avgVolume);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Button saveBtn = rootView.findViewById(R.id.saveVolumeAlertBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i < volumeAlertsSet.length; i++) {
                    if (volumeAlertsSet[i]) {
                        Alert volumeAlert;
                        double calculatedVolume;
                        switch (i) {
                            case 0:
                                // Create an alert for 25% above average volume.
                                volumeAlert = new Alert();
                                volumeAlert.setSymbol(symbol);
                                calculatedVolume = avgVolumeNum * 1.25;
                                volumeAlert.setAlertTriggerValue(String.valueOf(calculatedVolume));
                                volumeAlert.setAlertType("VOLUME_EXCEEDS_PERCENTAGE");
                                alertViewModel.addAlert(volumeAlert);
                                break;
                            case 1:
                                // Create an alert for 50% above average volume.
                                volumeAlert = new Alert();
                                volumeAlert.setSymbol(symbol);
                                calculatedVolume = avgVolumeNum * 1.5;
                                volumeAlert.setAlertTriggerValue(String.valueOf(calculatedVolume));
                                volumeAlert.setAlertType("VOLUME_EXCEEDS_PERCENTAGE");
                                alertViewModel.addAlert(volumeAlert);
                                break;
                            case 2:
                                // Create an alert for 100% above average volume.
                                volumeAlert = new Alert();
                                volumeAlert.setSymbol(symbol);
                                calculatedVolume = avgVolumeNum * 2;
                                volumeAlert.setAlertTriggerValue(String.valueOf(calculatedVolume));
                                volumeAlert.setAlertType("VOLUME_EXCEEDS_PERCENTAGE");
                                alertViewModel.addAlert(volumeAlert);
                                break;
                            case 3:
                                // Create an alert for 150% above average volume.
                                volumeAlert = new Alert();
                                volumeAlert.setSymbol(symbol);
                                calculatedVolume = avgVolumeNum * 2.5;
                                volumeAlert.setAlertTriggerValue(String.valueOf(calculatedVolume));
                                volumeAlert.setAlertType("VOLUME_EXCEEDS_PERCENTAGE");
                                alertViewModel.addAlert(volumeAlert);
                                break;
                            case 4:
                                // Create an alert for 200% above average volume.
                                volumeAlert = new Alert();
                                volumeAlert.setSymbol(symbol);
                                calculatedVolume = avgVolumeNum * 3;
                                volumeAlert.setAlertTriggerValue(String.valueOf(calculatedVolume));
                                volumeAlert.setAlertType("VOLUME_EXCEEDS_PERCENTAGE");
                                alertViewModel.addAlert(volumeAlert);
                                break;
                        }
                    }
                }
                // Close the dialog after the user selects Save and the alerts are created.
                dismiss();
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        MultiDex.install(getContext());
        symbol = getArguments().getString("symbol");

    }
}
