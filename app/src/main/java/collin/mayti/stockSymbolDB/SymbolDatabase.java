package collin.mayti.stockSymbolDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by chpreston on 3/7/18.
 */

@Database(entities = {Symbol.class}, version = 1)
public abstract class SymbolDatabase extends RoomDatabase {

    private static collin.mayti.stockSymbolDB.SymbolDatabase INSTANCE;

    public static collin.mayti.stockSymbolDB.SymbolDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), collin.mayti.stockSymbolDB.SymbolDatabase.class, "symbols").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract SymbolDbDao symbolDbDao();

}