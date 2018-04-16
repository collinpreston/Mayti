package collin.mayti.alerts.viewAlerts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.stockDetails.StockDetailsListViewAdapter;

public class ViewCurrentAlertsDialog extends DialogFragment {

    private View rootView;
    private String symbol;

    public static ViewCurrentAlertsDialog newInstance(String symbol) {
        ViewCurrentAlertsDialog f = new ViewCurrentAlertsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_view_current_alerts, container, false);

        ListView currentAlertsListView = rootView.findViewById(R.id.currentAlertsList);
        ViewAlertsListViewAdapter stockAlertsListViewAdapter = null;
        stockAlertsListViewAdapter = new ViewAlertsListViewAdapter(rootView.getContext(), getActivity(), symbol);
        currentAlertsListView.setAdapter(stockAlertsListViewAdapter);

        Button closeDialogBtn = rootView.findViewById(R.id.closeCurrentAlertsDialog);
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
