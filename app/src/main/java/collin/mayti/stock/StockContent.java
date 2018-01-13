package collin.mayti.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Collin on 1/6/2018.
 */

public class StockContent {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<StockContent.StockItem> ITEMS = new ArrayList<StockContent.StockItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, StockContent.StockItem> ITEM_MAP = new HashMap<String, StockContent.StockItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createStockItem(i));
        }
    }

    private static void addItem(StockContent.StockItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static StockContent.StockItem createStockItem(int position) {
        return new StockContent.StockItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class StockItem {
        public final String id;
        public final String content;
        public final String details;

        public StockItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
