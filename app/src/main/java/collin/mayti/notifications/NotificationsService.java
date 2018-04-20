package collin.mayti.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import collin.mayti.R;
import collin.mayti.alerts.AlertsUtil;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionDatabase;

public class NotificationsService extends Service{

    private String CUSTOM_NOTIFICATION_CHANNEL_ID = "CUSTOM_NOTIFICATION_CHANNEL_ID";

    /**
     * Used to store the list of alerts set by the user.
     */
    private List<Alert> alertList = new ArrayList<>();

    private AlertsUtil alertsUtil;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public NotificationsService() {
        this.alertsUtil = new AlertsUtil(this.getApplication());
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();

        timer.schedule(new DelayedTask(), 300, 5000);
        // Setting the flag below will keep the service running after the app is closed.
        // TODO: Need to configure this in order to provide notifications.
        //flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private class DelayedTask extends TimerTask {

        @Override
        public void run() {
            alertList = retrieveAlertsSetByUser();
            performAllAlertChecks(alertList);
        }
    }

    /**
     * Get all the alerts set by the user from the AlertSubscriptionDatabase.
     * @return
     */
    private List<Alert> retrieveAlertsSetByUser() {
        AlertSubscriptionDatabase alertSubscriptionDatabase = AlertSubscriptionDatabase.getDatabase(this.getApplication());
        return alertSubscriptionDatabase.alertSubscriptionDao().getAllAlerts();
    }

    private void performAllAlertChecks(List<Alert> alerts) {
        for (int i=0; i < alerts.size(); i++) {
            switch (alerts.get(i).getAlertType()) {
                case "PRICE_CHANGE_PRICE":
                    if (alertsUtil.priceCheck(alertList.get(i))) {
                        removeAlertAndCreateNotification(alertList.get(i));
                    }
                    break;
                case "PRICE_CHANGE_PERCENT":
                    if (alertsUtil.priceCheck(alertList.get(i))) {
                        removeAlertAndCreateNotification(alertList.get(i));
                    }
                    break;
                case "PRICE_TARGET":
                    if (alertsUtil.priceCheck(alertList.get(i))) {
                        removeAlertAndCreateNotification(alertList.get(i));
                    }
                    break;
                case "VOLUME_EXCEEDS_PERCENTAGE":
                    if (alertsUtil.volumeCheck(alertList.get(i))) {
                        removeAlertAndCreateNotification(alertList.get(i));
                    }
                    break;
                case "STOCK_ARTICLES_PUBLISHED":
                    if (alertsUtil.stockNewsCheck(alertList.get(i))) {
                        removeAlertAndCreateNotification(alertList.get(i));
                    }
                    break;
            }
        }
    }
    private void triggerNotification(Alert alert) {


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create the NotificationChannel, but only on API 26+ because
//            // the NotificationChannel class is new and not in the support library
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
//            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CUSTOM_NOTIFICATION_CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            notificationManager.createNotificationChannel(channel);
//        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CUSTOM_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.propeller_layer)
                .setContentTitle(alert.getAlertType())
                .setContentText(alert.getAlertTriggerValue())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    }
    private void removeAlertAndCreateNotification(Alert alert) {
        // Remove the alert from the alert subscription database.
        AlertSubscriptionDatabase alertSubscriptionDatabase = AlertSubscriptionDatabase.getDatabase(getApplication());
        alertSubscriptionDatabase.alertSubscriptionDao().delete(alert);

    }




}

