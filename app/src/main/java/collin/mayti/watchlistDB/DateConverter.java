package collin.mayti.watchlistDB;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * Created by chpreston on 2/17/18.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
