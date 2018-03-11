package collin.mayti.stockSymbolDB;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by chpreston on 3/7/18.
 */

@Entity(tableName = "symbols", indices = {@Index(value = "symbol", unique = true)})
public class Symbol {

    @NonNull
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.symbol = symbol;
    }

    @PrimaryKey
    @NonNull
    private String symbol;

}
