package collin.mayti.datacapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import collin.mayti.stock.StockContent;
import collin.mayti.urlUtil.UrlUtil;

/**
 * Created by Collin on 1/13/2018.
 */

public class DataRetriever extends Service {

    public DataRetriever() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void isJSONDataValid() {
        // TODO: test if the file comes back blank or with an error.
    }
    private JSONArray getQuotesAsJSON(List<String> symbols) throws MalformedURLException, JSONException {
            String dataRetrievedString = new UrlUtil().getStockData(symbols);
            JSONObject dataObj = new JSONObject(dataRetrievedString);
            JSONArray stockData;
            // TODO: Make this a private static variable.
            stockData = dataObj.getJSONArray("Stock Quotes");
            return stockData;
    }
    public HashMap<String, StockContent.StockItem> getQuotesAsMap(List<String> symbols)
            throws MalformedURLException, JSONException {
        JSONArray quotesJSON = getQuotesAsJSON(symbols);
        HashMap<String, StockContent.StockItem> watchlistData = new HashMap<>();
        for (int i=0; i < quotesJSON.length(); i++) {

            JSONObject quoteJSON = quotesJSON.getJSONObject(i);
            StockContent.StockItem stockItem = new StockContent.StockItem(
            quoteJSON.getString("symbol"),
            quoteJSON.getString("price"),
            quoteJSON.getString("volume"));

            watchlistData.put(stockItem.symbol, stockItem);
        }
        return watchlistData;
    }


}
