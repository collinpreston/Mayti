package collin.mayti.notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import collin.mayti.R;
import collin.mayti.alerts.AlertsUtil;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionDatabase;
import collin.mayti.applicationSettingsDB.SettingDatabase;
import collin.mayti.applicationSettingsDB.SettingObject;
import collin.mayti.notifications.NotificationsDatabase.Notification;
import collin.mayti.notifications.NotificationsDatabase.NotificationsDatabase;
import collin.mayti.stockIndicators.IndicatorEngine;
import collin.mayti.stockIndicators.IndicatorProfileSensitivity;

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


        // Alert checks will run every 1 minute.
        timer.schedule(new DelayedAlertTask(), 300, 60000);

        // Indicator checks will run every 10 minutes.
        Timer timerIndicator = new Timer();
        timerIndicator.schedule(new DelayedIndicatorTask(), 300, 600000);

        // TODO:
        // Setting the flag below will keep the service running after the app is closed.
        //flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private class DelayedAlertTask extends TimerTask {

        @Override
        public void run() {
            alertList = retrieveAlertsSetByUser();
            performAllAlertChecks(alertList);
        }
    }

    private class DelayedIndicatorTask extends TimerTask {

        @Override
        public void run() {
            // TODO: get all stocks in watchlist.
            performAllIndicatorChecks();
        }
    }



    private void performAllIndicatorChecks() {
        // Create an instance of the indicator engine and supply the indicator sensitivity level.
        IndicatorEngine indicatorEngine = new IndicatorEngine(getUserIndicatorProfileSensitivity());
        // Perform all of the indicator checks.
        indicatorEngine.performIndicatorChecks();
    }

    private IndicatorProfileSensitivity getUserIndicatorProfileSensitivity() {
        // TODO: Use this method to access the settings database and retrieve the indicator profile sensitivity value.
        SettingDatabase settingDatabase = SettingDatabase.getDatabase(this.getApplication());
        SettingObject settingObject = settingDatabase.settingDbDao().findBySettingID("INDICATOR_SENSITIVITY_LEVEL");
        switch (settingObject.getSettingValue()) {
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
                        triggerNotification(alertList.get(i));
                    }
                    break;
                case "PRICE_CHANGE_PERCENT":
                    if (alertsUtil.priceCheck(alertList.get(i))) {
                        triggerNotification(alertList.get(i));
                    }
                    break;
                case "PRICE_TARGET":
                    if (alertsUtil.priceCheck(alertList.get(i))) {
                        triggerNotification(alertList.get(i));
                    }
                    break;
                case "VOLUME_EXCEEDS_PERCENTAGE":
                    if (alertsUtil.volumeCheck(alertList.get(i))) {
                        triggerNotification(alertList.get(i));
                    }
                    break;
                case "STOCK_ARTICLES_PUBLISHED":
                    if (alertsUtil.stockNewsCheck(alertList.get(i))) {
                        triggerNotification(alertList.get(i));
                    }
                    break;
            }
        }
    }
    private void triggerNotification(Alert alert) {
        String notificationTitle = "Mayti Alert";
        String notificationText = "Mayti has detected an alert on a stock in your watchlist!";
        switch (alert.getAlertType()) {
            case "PRICE_CHANGE_PRICE":
                notificationTitle = alert.getSymbol() + " Price Change Alert";
                if (Integer.parseInt(alert.getAlertTriggerValue()) > 0) {
                    notificationText = alert.getSymbol() + " has gone up $" + alert.getAlertTriggerValue();
                } else {
                    notificationText = alert.getSymbol() + " has gone down $" + alert.getAlertTriggerValue();
                }
                break;
            case "PRICE_CHANGE_PERCENT":
                notificationTitle = alert.getSymbol() + " Percent Change Alert";
                if (Integer.parseInt(alert.getAlertTriggerValue()) > 0) {
                    notificationText = alert.getSymbol() + " has gone up " + alert.getAlertTriggerValue() + "%";
                } else {
                    notificationText = alert.getSymbol() + " has gone down " + alert.getAlertTriggerValue() + "%";
                }
                break;
            case "PRICE_TARGET":
                notificationTitle = alert.getSymbol() + " Price Alert at " + alert.getAlertTriggerValue();
                notificationText = alert.getSymbol() + " has reached $" + alert.getAlertTriggerValue()  + ".";
                break;
            case "VOLUME_EXCEEDS_PERCENTAGE":
                notificationTitle =  alert.getSymbol() + " has higher than normal volume";
                notificationText = "Over " + alert.getAlertTriggerValue() + " share have traded today for " + alert.getSymbol() + ".";
                break;
            case "STOCK_ARTICLES_PUBLISHED":
                notificationTitle = alert.getSymbol() + " News Alert";
                notificationText = alert.getSymbol() + " is experiencing higher than normal media coverage.";
                break;
        }

        removeAlertAndCreateNotification(notificationTitle, notificationText, alert);



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

        // TODO: Create switch statement for generating the notification for different triggers (indicator, volume alert, news alert).

    }
    private void removeAlertAndCreateNotification(String notificationTitle, String notificationText, Alert alert) {
        // Remove the alert from the alert subscription database.
        AlertSubscriptionDatabase alertSubscriptionDatabase = AlertSubscriptionDatabase.getDatabase(getApplication());
        alertSubscriptionDatabase.alertSubscriptionDao().delete(alert);

        // Create entry in the notificationsDatabase
        NotificationsDatabase notificationsDatabase = NotificationsDatabase.getDatabase(getApplication());
        Notification notification = new Notification();
        notification.setNotificationTitle(notificationTitle);
        notification.setNotificationActive(true);
        notification.setSymbol(alert.getSymbol());
        notification.setNotificationText(notificationText);
        Date currentDate = new Date(System.currentTimeMillis());
        notification.setDateTriggered(currentDate);

        // Insert the alert into the alerts database and save the notificationID.
        long notificationID = notificationsDatabase.notificationsDbDao().insert(notification);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CUSTOM_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.propeller_layer)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique number that is used as the primary key for the notification DB.
        // It is returned by the insert of a new notification.
        notificationManager.notify((int) notificationID, mBuilder.build());

    }
}

