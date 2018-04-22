package collin.mayti.notifications.NotificationsDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Notification.class}, version = 1)
public abstract class  NotificationsDatabase extends RoomDatabase{

    private static NotificationsDatabase INSTANCE;

    public static NotificationsDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), NotificationsDatabase.class, "notifications").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract NotificationsDbDao notificationsDbDao();
}
