package collin.mayti.applicationSettingsDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import collin.mayti.stockSymbolDB.SymbolDbDao;

/**
 * Created by chpreston on 3/10/18.
 */

@Database(entities = {SettingObject.class}, version = 6)
public abstract class SettingDatabase extends RoomDatabase {

    private static collin.mayti.applicationSettingsDB.SettingDatabase INSTANCE;

    public static collin.mayti.applicationSettingsDB.SettingDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), collin.mayti.applicationSettingsDB.SettingDatabase.class, "settings").fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public abstract SettingDbDao settingDbDao();

}
