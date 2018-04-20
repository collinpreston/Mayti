package collin.mayti.alerts;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import collin.mayti.R;
import collin.mayti.alerts.newsAlerts.DefineNewsAlertDialog;
import collin.mayti.alerts.priceAlerts.DefinePriceAlertDialog;
import collin.mayti.alerts.volumeAlerts.DefineVolumeAlertDialog;

public class AlertTypeDialog extends DialogFragment{

    private View rootView;
    private String symbol;

    public static AlertTypeDialog newInstance(String symbol) {
        AlertTypeDialog f = new AlertTypeDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("New Alert Type");
        rootView = inflater.inflate(R.layout.dialog_notification_type, container, false);

        Button newPriceAlert = rootView.findViewById(R.id.newPriceAlertBtn);
        newPriceAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefinePriceAlertDialog dialogFrag = DefinePriceAlertDialog.newInstance(symbol);
                dialogFrag.show(getActivity().getFragmentManager(), "");
                dismiss();

                // TODO: Open the new price alert dialog which will allow the user to create an alert
                // for price changes.  This can be specified as a percent change -/+/+-, price change
                // -/+/+-, quick changes shifts (+/-) (need to record price every 15 minutes as basis to compare with.
                // Or allow the user to select smart alerts and define the verbose level.  Smart alerts will
                // work by recognising when a stock is beginning to fall from it's day high, running past it's
                // 52w high or low, rising from it's day low, or if it's been resting at a price for 30 minutes after a sudden
                // drop or rise in order to tell the user that the stock has likely hit a high or low and will soon change
                // (for this we will compare with the 15 minute historical marker price set for quick shifts.

                // I'll need to pass this new dialog the symbol.

                // Notifications will be recorded in the notifications database table which will hold
                // the symbol, the notification type, and whether the notification is active or not.
                // It will also have a column for notification value which will be used for simple notification
                // comparisons such as price changes.

                // This database will need to be cleaned during the splash screen activity just as
                // the watchlist database is.

                // There will be a separate database table for recording historical markers that will be used for comparison.
                // Here we will store data such as price every 15 minutes.
            }
        });

        Button newVolumeAlert = rootView.findViewById(R.id.newVolumeAlertBtn);
        newVolumeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefineVolumeAlertDialog dialogFrag = DefineVolumeAlertDialog.newInstance(symbol);
                dialogFrag.show(getActivity().getFragmentManager(), "");
                dismiss();
            }
        });

        Button newStockNewsAlert = rootView.findViewById(R.id.newNewsAlertBtn);
        newStockNewsAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefineNewsAlertDialog dialogFrag = DefineNewsAlertDialog.newInstance(symbol);
                dialogFrag.show(getActivity().getFragmentManager(), "");
                dismiss();
            }
        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        MultiDex.install(getContext());
        symbol = getArguments().getString("symbol");


    }
}
