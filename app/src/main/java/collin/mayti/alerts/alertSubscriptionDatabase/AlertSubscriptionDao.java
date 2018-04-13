package collin.mayti.alerts.alertSubscriptionDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AlertSubscriptionDao {

    @Query("SELECT * FROM alerts WHERE symbol LIKE :symbol")
    List<Alert> findAlertsBySymbol(String symbol);

    @Query("SELECT * FROM alerts")
    List<Alert> getAllAlerts();

    @Query("SELECT COUNT(*) FROM alerts")
    int getTotalNumberOfAlerts();

    @Query("SELECT COUNT(*) FROM alerts WHERE symbol = :symbol AND alertType = :type AND alertTriggerValue = :trigger LIMIT 1")
    int getNumberAlertBySymbolTypeTrigger(String symbol, String type, String trigger);

    @Insert
    void insertAlert(Alert alert);

    @Delete
    void delete(Alert alert);
}
