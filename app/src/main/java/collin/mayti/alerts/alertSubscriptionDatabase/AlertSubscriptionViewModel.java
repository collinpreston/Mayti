package collin.mayti.alerts.alertSubscriptionDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


public class AlertSubscriptionViewModel extends AndroidViewModel {

    private AlertSubscriptionDatabase alertSubscriptionDatabase;


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
            if (db.alertSubscriptionDao().getNumberAlertBySymbolTypeTrigger(alert.getSymbol(), alert.getAlertType(), alert.getAlertTriggerValue()) != 0) {
                db.alertSubscriptionDao().insertAlert(params[0]);
            }
            return null;
        }

    }
}
