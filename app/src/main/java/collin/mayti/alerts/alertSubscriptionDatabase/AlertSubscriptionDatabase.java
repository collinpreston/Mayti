package collin.mayti.alerts.alertSubscriptionDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Alert.class}, version = 2)
public abstract class AlertSubscriptionDatabase extends RoomDatabase {

    private static AlertSubscriptionDatabase INSTANCE;

    public static AlertSubscriptionDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AlertSubscriptionDatabase.class, "alerts").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract AlertSubscriptionDao alertSubscriptionDao();
}
