package collin.mayti.notifications.NotificationsDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Date;

import collin.mayti.watchlistDB.DateConverter;

@Entity(tableName = "notifications", indices = {@Index(value = "notificationID")})
public class Notification {

    @PrimaryKey(autoGenerate = true)
    private int notificationID;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "notificationTitle")
    private String notificationTitle;

    @ColumnInfo(name = "notificationText")
    private String notificationText;

    @ColumnInfo(name = "notificationActive")
    private boolean notificationActive;

    @ColumnInfo
    @TypeConverters(DateConverter.class)
    private Date dateTriggered;

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public boolean isNotificationActive() {
        return notificationActive;
    }

    public void setNotificationActive(boolean notificationActive) {
        this.notificationActive = notificationActive;
    }

    public Date getDateTriggered() {
        return dateTriggered;
    }

    public void setDateTriggered(Date dateTriggered) {
        this.dateTriggered = dateTriggered;
    }
}
