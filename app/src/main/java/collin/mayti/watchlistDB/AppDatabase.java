package collin.mayti.watchlistDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Collin on 1/15/2018.
 */

@Database(entities = {Stock.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "watchlist").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract WatchlistDao watchlistDao();

}
