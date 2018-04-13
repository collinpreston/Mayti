package collin.mayti.alerts.alertSubscriptionDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;


@Entity(tableName = "alerts", indices = {@Index(value = "symbol")}, primaryKeys = {"symbol", "alertType", "alertTriggerValue"})
public class Alert {

    @NonNull
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.symbol = symbol;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertTriggerValue() {
        return alertTriggerValue;
    }

    public void setAlertTriggerValue(String alertTriggerValue) {
        this.alertTriggerValue = alertTriggerValue;
    }

    @NonNull
    private String symbol;

    @NonNull
    @ColumnInfo(name = "alertType")
    private String alertType;

    @NonNull
    @ColumnInfo(name = "alertTriggerValue")
    private String alertTriggerValue;


}
