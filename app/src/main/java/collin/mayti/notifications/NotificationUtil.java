package collin.mayti.notifications;

import collin.mayti.notifications.NotificationsDatabase.Notification;

public class NotificationUtil {

    public static Notification createNotification(String symbol, String title, String text) {
        Notification notification = new Notification();
        notification.setNotificationTitle(title);
        notification.setNotificationText(text);
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        notification.setDateTriggered(currentDate);
        notification.setSymbol(symbol);
        notification.setNotificationActive(true);

        return notification;
    }
}
