package collin.mayti.applicationSettingsDB;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by chpreston on 3/10/18.
 */

@Entity(tableName = "settings", indices = {@Index(value = "settingID")})
public class SettingObject {

    @PrimaryKey
    @NonNull
    private String settingID;


    private String settingValue;

    @NonNull
    public String getSettingID() {
        return settingID;
    }

    public void setSettingID(@NonNull String settingID) {
        this.settingID = settingID;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

}
