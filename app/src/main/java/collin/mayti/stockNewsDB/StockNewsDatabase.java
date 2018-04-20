package collin.mayti.stockNewsDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Article.class}, version = 2)
public abstract class StockNewsDatabase extends RoomDatabase{

    private static collin.mayti.stockNewsDB.StockNewsDatabase INSTANCE;

    public static collin.mayti.stockNewsDB.StockNewsDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), collin.mayti.stockNewsDB.StockNewsDatabase.class, "news").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract StockNewsDbDao stockNewsDbDao();
}
