package collin.mayti.stock;

import android.arch.lifecycle.LiveData;


/**
 * Created by Collin on 1/6/2018.
 */

public class StockContent extends LiveData{

    public static class StockItem {
        public final int position;
        public final String symbol;
        public final String price;
        public final String volume;
        public final String change;
        public final String changePercent;
        public final String recordHigh;
        public final String recordLow;
        public final String averageVolume;


        public StockItem(String symbol, String price, String volume, String change, String changePercent,
                         String recordHigh, String recordLow, String averageVolume) {
            this.position = -1;
            this.symbol = symbol;
            this.price = price;
            this.volume = volume;
            this.change = change;
            this.changePercent = changePercent;
            this.recordHigh = recordHigh;
            this.recordLow = recordLow;
            this.averageVolume = averageVolume;

        }

    }
}
