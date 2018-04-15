package collin.mayti.alerts.alertSubscriptionDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;


public class AlertSubscriptionViewModel extends AndroidViewModel {

    private AlertSubscriptionDatabase alertSubscriptionDatabase;

    private static List<Alert> alertsList = new ArrayList<>();


    public AlertSubscriptionViewModel(@NonNull Application application) {
        super(application);
        alertSubscriptionDatabase = AlertSubscriptionDatabase.getDatabase(this.getApplication());
    }

    public void addAlert(Alert alert) {
        new AlertSubscriptionViewModel.addAlertAsyncTask(alertSubscriptionDatabase).execute(alert);
    }

    private static class addAlertAsyncTask extends AsyncTask<Alert, Void, Void> {

        private AlertSubscriptionDatabase db;

        addAlertAsyncTask(AlertSubscriptionDatabase alertSubscriptionDatabase) {
            db = alertSubscriptionDatabase;
        }

        @Override
        protected Void doInBackground(final Alert... params) {
            Alert alert = params[0];
            // Check to make sure that the alert doesn't already exist.
            if (db.alertSubscriptionDao().getNumberAlertBySymbolTypeTrigger(alert.getSymbol(), alert.getAlertType(), alert.getAlertTriggerValue()) == 0) {
                db.alertSubscriptionDao().insertAlert(params[0]);
            }
            return null;
        }

    }

    public List<Alert> getAllAlertsForSymbol(String symbol) throws ExecutionException, InterruptedException {
        new AlertSubscriptionViewModel.getAllAlertsForSymbolAsyncTask(alertSubscriptionDatabase, symbol).execute().get();
        return alertsList;
    }

    private static class getAllAlertsForSymbolAsyncTask extends AsyncTask<String, Void, Void> {

        private AlertSubscriptionDatabase db;
        private String mSymbol;

        getAllAlertsForSymbolAsyncTask(AlertSubscriptionDatabase alertSubscriptionDatabase, String symbol) {
            db = alertSubscriptionDatabase;
            mSymbol = symbol;
        }

        @Override
        protected Void doInBackground(final String... params) {
            try {
                alertsList = db.alertSubscriptionDao().findAlertsBySymbol(mSymbol);
            } catch (Exception e) {
            }
            return null;
        }
    }


}
