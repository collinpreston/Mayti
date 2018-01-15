package collin.mayti.watchlist;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Collin on 1/15/2018.
 */

@Database(entities = {Stock.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WatchlistDao watchlistDao();
}
