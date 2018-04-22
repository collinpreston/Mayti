package collin.mayti.stockIndicators;

import java.util.ArrayList;
import java.util.List;

import collin.mayti.notifications.NotificationsDatabase.Notification;

public class IndicatorEngine {
    private IndicatorProfileSensitivity profileSensitivity;

    public IndicatorEngine(IndicatorProfileSensitivity sensitivity) {
        this.profileSensitivity = sensitivity;
    }

    public List<Notification> performIndicatorChecks() {
        List<Notification> indicatorNotificationList = new ArrayList<>();

        // Get the users indicator sensitivity and perform the checks associated with that level.
        switch(profileSensitivity) {
            case LOW:
                indicatorNotificationList = performLowLevelChecks();
                break;
            case MEDIUM:
                indicatorNotificationList = performMediumLevelChecks();
                break;
            case HIGH:
                indicatorNotificationList = performHighLevelChecks();
                break;
            case VERY_HIGH:
                indicatorNotificationList = performVeryHighLevelChecks();
                break;
        }

        return indicatorNotificationList;
    }


    private List<Notification> performLowLevelChecks() {
        return null;
    }

    private List<Notification> performMediumLevelChecks() {
        return null;
    }

    private List<Notification> performHighLevelChecks() {
        return null;
    }

    private List<Notification> performVeryHighLevelChecks() {
        return null;
    }

}
