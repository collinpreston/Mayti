package collin.mayti.alerts.viewAlerts;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.Util.FormatLargeDouble;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionViewModel;

public class ViewAlertsListViewAdapter extends BaseAdapter {
    private Context mContext;
    private Activity mActivity;
    private String mSymbol;
    private List<Alert> alertsForSymbol = new ArrayList<>();


    public ViewAlertsListViewAdapter(Context context, Activity activity, String symbol) {
        this.mContext = context;
        this.mActivity = activity;
        this.mSymbol = symbol;
        try {
            alertsForSymbol = getAlertsForSymbol();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return alertsForSymbol.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.current_alert_list_item, parent, false);

        TextView alertTypeTxt = row.findViewById(R.id.alertTypeTxt);
        TextView alertValueTxt = row.findViewById(R.id.alertValueTxt);
        String alertType = alertsForSymbol.get(position).getAlertType();
        switch (alertType) {
            case "PRICE_CHANGE_PRICE":
                alertTypeTxt.setText("Price Change:");
                alertValueTxt.setText("$" + alertsForSymbol.get(position).getAlertTriggerValue());
                break;
            case "PRICE_CHANGE_PERCENT":
                alertTypeTxt.setText("Price Change:");
                alertValueTxt.setText(alertsForSymbol.get(position).getAlertTriggerValue() + "%");
                break;
            case "PRICE_TARGET":
                alertTypeTxt.setText("Price Hits:");
                alertValueTxt.setText("$" + alertsForSymbol.get(position).getAlertTriggerValue());
                break;
            case "VOLUME_EXCEEDS_PERCENTAGE":
                alertTypeTxt.setText("Volume Surpasses:");
                String triggerDisplayValue = FormatLargeDouble.format(Double.parseDouble(alertsForSymbol.get(position).getAlertTriggerValue()));
                alertValueTxt.setText(triggerDisplayValue);
                break;
            default:
                alertTypeTxt.setText(alertType);
                alertValueTxt.setText(alertsForSymbol.get(position).getAlertTriggerValue());

        }
        return row;
    }

    private List<Alert> getAlertsForSymbol() throws ExecutionException, InterruptedException {
        AlertSubscriptionViewModel alertViewModel = ViewModelProviders.of((FragmentActivity) mActivity).get(AlertSubscriptionViewModel.class);
        List<Alert> listAlerts = alertViewModel.getAllAlertsForSymbol(mSymbol);
        return listAlerts;
    }
}
