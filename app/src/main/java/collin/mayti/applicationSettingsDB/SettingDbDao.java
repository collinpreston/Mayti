package collin.mayti.applicationSettingsDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.RequiresPermission;

import java.util.List;

/**
 * Created by chpreston on 3/10/18.
 */

@Dao
public interface SettingDbDao {

    @Query("SELECT * FROM settings")
    List<SettingObject> getAll();

    @Query("SELECT * FROM settings WHERE settingID LIKE :settingID LIMIT 1")
    SettingObject findBySettingID(String settingID);

    @Insert
    void insertAll(List<SettingObject> settingObjects);

    @Insert
    void insert(SettingObject settingObject);

    @Update
    void updateSetting(SettingObject... settingObjects);

    @Query("SELECT COUNT(*) FROM settings")
    int getTotalNumberOfRows();

}
