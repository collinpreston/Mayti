package collin.mayti.datacapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import collin.mayti.MainActivity;
import collin.mayti.stock.StockContent;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlist.AppDatabase;

/**
 * Created by Collin on 1/13/2018.
 */

public class DataRetriever extends Service {
    Timer timer = new Timer();
    private String[] stockSymbols;
    private String dataRetrievedString;
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
    private JSONArray getQuotesAsJSON(List<String> symbols) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
            dataRetrievedString = new UrlUtil().getStockData(symbols);
            JSONObject dataObj = new JSONObject(dataRetrievedString);
            JSONArray stockData;
            // TODO: Make this a private static variable.
            stockData = dataObj.getJSONArray("Stock Quotes");
            return stockData;
    }
    public List<StockContent.StockItem> getQuotesAsList(List<String> symbols)
            throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
        JSONArray quotesJSON = getQuotesAsJSON(symbols);
        List <StockContent.StockItem> watchlistData = null;
        for (int i=0; i < quotesJSON.length(); i++) {

            JSONObject quoteJSON = quotesJSON.getJSONObject(i);
            StockContent.StockItem stockItem = new StockContent.StockItem(
            quoteJSON.getString("symbol"),
            quoteJSON.getString("price"),
            quoteJSON.getString("volume"));

            watchlistData.add(stockItem);
        }
        return watchlistData;
    }
    private void updateDatabaseWithData(List <StockContent.StockItem> stockDataList) {
        AppDatabase db = MainActivity.db;
        for (StockContent.StockItem item : stockDataList) {
            db.watchlistDao().updateBySymbol(item.price, item.price, item.symbol);
        }

    }
    private class DelayedTask extends TimerTask {
        @Override
        public void run() {
            List<String> symbolList = Arrays.asList(stockSymbols);
            try {
                updateDatabaseWithData(getQuotesAsList(symbolList));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stockSymbols = intent.getStringArrayExtra("symbols");
        timer.schedule(new DelayedTask(),300, 3000);
        return super.onStartCommand(intent, flags, startId);
    }
}
