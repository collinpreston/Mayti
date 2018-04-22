package collin.mayti.notifications.NotificationsDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NotificationsDbDao {

    @Insert
    long insert(Notification notification);

    @Query("SELECT * FROM notifications")
    List<Notification> getAll();

    @Query("SELECT * FROM notifications WHERE notificationActive = 'true'")
    List<Notification> getActiveNotifications();

    @Query("SELECT * FROM notifications WHERE notificationActive = 'false'")
    List<Notification> getNonActiveNotifications();


}
