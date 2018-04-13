package collin.mayti.alerts.priceAlerts;

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
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionViewModel;
import collin.mayti.watchlist.WatchlistViewModel;

public class DefinePriceAlertDialog extends DialogFragment {

    private View rootView;
    private String symbol;
    private String stockPrice;

    private boolean[] quickAlertsSet = {false, false, false, false};
    private boolean[] recordHighLowAlertsSet = {false, false};
    boolean changeTypeIsPrice = true;

    private AlertSubscriptionViewModel alertViewModel;
    private WatchlistViewModel watchlistViewModel;

    private TextView dollarIndTxt;
    private TextView percentIndTxt;



    public static DefinePriceAlertDialog newInstance(String symbol) {
        DefinePriceAlertDialog f = new DefinePriceAlertDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_define_price_alert, container, false);

        TextView priceTargetTxtView = rootView.findViewById(R.id.priceTarget);
        TextView priceChangeTxtView = rootView.findViewById(R.id.changeAmountTxt);

        dollarIndTxt = rootView.findViewById(R.id.dollarIndTxt);
        percentIndTxt = rootView.findViewById(R.id.percentIndTxt);

        MultiStateToggleButton priceAlertTypeMtb = rootView.findViewById(R.id.priceNewAlertTypeMtb);
        boolean[] selectedDefault = {true, false};
        priceAlertTypeMtb.setStates(selectedDefault);

        priceAlertTypeMtb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (position == 0) {
                    changeTypeIsPrice = true;
                    dollarIndTxt.setVisibility(View.VISIBLE);
                    percentIndTxt.setVisibility(View.INVISIBLE);
                }
                else {
                    changeTypeIsPrice = false;
                    dollarIndTxt.setVisibility(View.INVISIBLE);
                    percentIndTxt.setVisibility(View.VISIBLE);
                }
                //Log.d(TAG, "Position: " + position);
            }
        });

        MultiStateToggleButton quickAlertMtb = rootView.findViewById(R.id.priceQuickAlertsMtb);
        quickAlertMtb.enableMultipleChoice(true);
        quickAlertMtb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (quickAlertsSet[position]) {
                    quickAlertsSet[position] = false;
                } else {
                    quickAlertsSet[position] = true;
                }
                //Log.d(TAG, "Position: " + position);
            }
        });

        MultiStateToggleButton recordHighLowAlertMtb = rootView.findViewById(R.id.recordHighLowPriceAlertsMtb);
        recordHighLowAlertMtb.enableMultipleChoice(true);

        recordHighLowAlertMtb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (recordHighLowAlertsSet[position]) {
                    recordHighLowAlertsSet[position] = false;
                } else {
                    recordHighLowAlertsSet[position] = true;
                }
                //Log.d(TAG, "Position: " + position);
            }
        });


        Activity activity = getActivity();


        // Populate the price target entry field with the current price of the stock from the watchlist database.
        watchlistViewModel = ViewModelProviders.of((FragmentActivity) activity).get(WatchlistViewModel.class);
        try {
            stockPrice = watchlistViewModel.findStockItemBySymbold(symbol).getPrice();
            priceTargetTxtView.setText(stockPrice);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        alertViewModel = ViewModelProviders.of((FragmentActivity) activity).get(AlertSubscriptionViewModel.class);

        Button submitBtn = rootView.findViewById(R.id.submitNewPriceAlertBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!priceChangeTxtView.getText().toString().isEmpty()) {

                    Alert priceChangeAlert = new Alert();
                    priceChangeAlert.setSymbol(symbol);
                    priceChangeAlert.setAlertTriggerValue(priceChangeTxtView.getText().toString());

                    if (changeTypeIsPrice) {
                        priceChangeAlert.setAlertType("PRICE_CHANGE_PRICE");
                    } else {
                        priceChangeAlert.setAlertType("PRICE_CHANGE_PERCENT");
                    }

                    alertViewModel.addAlert(priceChangeAlert);
                }

                if (!priceTargetTxtView.getText().toString().equals(stockPrice) && !priceTargetTxtView.getText().toString().isEmpty()) {
                    Alert priceTargetAlert = new Alert();
                    priceTargetAlert.setSymbol(symbol);
                    priceTargetAlert.setAlertTriggerValue(priceTargetTxtView.getText().toString());
                    priceTargetAlert.setAlertType("PRICE_TARGET");

                    alertViewModel.addAlert(priceTargetAlert);
                }

                for (int i=0; i < quickAlertsSet.length; i++) {
                    if (quickAlertsSet[i]) {
                        Alert priceChangeAlert;
                        switch (i) {
                            case 0:
                                // Create +/- 1 percent change alert.
                                priceChangeAlert = new Alert();
                                priceChangeAlert.setSymbol(symbol);
                                priceChangeAlert.setAlertTriggerValue("1");
                                priceChangeAlert.setAlertType("PRICE_CHANGE_PERCENT");
                                alertViewModel.addAlert(priceChangeAlert);
                                break;
                            case 1:
                                // Create +/- 2.5 percent change alert.
                                priceChangeAlert = new Alert();
                                priceChangeAlert.setSymbol(symbol);
                                priceChangeAlert.setAlertTriggerValue("2.5");
                                priceChangeAlert.setAlertType("PRICE_CHANGE_PERCENT");
                                alertViewModel.addAlert(priceChangeAlert);
                                break;
                            case 2:
                                // Create +/- 5 percent change alert.
                                priceChangeAlert = new Alert();
                                priceChangeAlert.setSymbol(symbol);
                                priceChangeAlert.setAlertTriggerValue("5");
                                priceChangeAlert.setAlertType("PRICE_CHANGE_PERCENT");
                                alertViewModel.addAlert(priceChangeAlert);
                                break;
                            case 3:
                                // Create +/- 10 percent change alert.
                                priceChangeAlert = new Alert();
                                priceChangeAlert.setSymbol(symbol);
                                priceChangeAlert.setAlertTriggerValue("10");
                                priceChangeAlert.setAlertType("PRICE_CHANGE_PERCENT");
                                alertViewModel.addAlert(priceChangeAlert);
                                break;
                        }
                    }
                }

                for (int i=0; i < recordHighLowAlertsSet.length; i++) {
                    if (recordHighLowAlertsSet[i]) {
                        Alert priceTargetAlert;
                        switch (i) {
                            case 0:
                                // Create 52-wk low alert.
                                String recordLowValue = "";
                                try {
                                    recordLowValue = watchlistViewModel.findStockItemBySymbold(symbol).getRecordLow();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                priceTargetAlert = new Alert();
                                priceTargetAlert.setSymbol(symbol);
                                priceTargetAlert.setAlertTriggerValue(recordLowValue);
                                priceTargetAlert.setAlertType("PRICE_TARGET");
                                alertViewModel.addAlert(priceTargetAlert);
                                break;
                            case 1:
                                String recordHighValue = "";
                                try {
                                    recordHighValue = watchlistViewModel.findStockItemBySymbold(symbol).getRecordHigh();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                priceTargetAlert = new Alert();
                                priceTargetAlert.setSymbol(symbol);
                                priceTargetAlert.setAlertTriggerValue(recordHighValue);
                                priceTargetAlert.setAlertType("PRICE_TARGET");
                                alertViewModel.addAlert(priceTargetAlert);
                                // Create 52-wk high alert.
                                break;
                        }
                    }
                }
                // Close the dialog after the user presses the save button and the alerts are created.
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
