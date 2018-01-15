package collin.mayti.watchlist;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

/**
 * Created by Collin on 1/14/2018.
 */


@Entity
public class Stock {
    @PrimaryKey
    private int positionID;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "price")
    private String price;

    @ColumnInfo(name = "volume")
    private String volume;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}


