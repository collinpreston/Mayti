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


        public StockItem(String symbol, String price, String volume) {
            this.position = -1;
            this.symbol = symbol;
            this.price = price;
            this.volume = volume;

        }

    }
}
