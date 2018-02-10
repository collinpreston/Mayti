package collin.mayti.watchlistDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Collin on 1/14/2018.
 */


@Entity(tableName = "watchlist", indices = {@Index(value = "symbol", unique = true)})
    public class Stock {
        public int getPositionID() {
            return positionID;
        }

        public void setPositionID(int positionID) {
            this.positionID = positionID;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        @PrimaryKey
        private int positionID;

        @ColumnInfo(name = "symbol")
        private String symbol;

        @ColumnInfo(name = "price")
        private String price;

        @ColumnInfo(name = "volume")
        private String volume;

}


