package collin.mayti.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Collin on 1/6/2018.
 */

public class StockContent {

    public static final HashMap<String, StockItem> WATCHLIST = new HashMap<String, StockItem>();

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
