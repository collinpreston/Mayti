package collin.mayti.datacapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import collin.mayti.MainActivity;
import collin.mayti.stock.StockContent;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlistDB.AppDatabase;

/**
 * Created by Collin on 1/13/2018.
 */

public class DataRetriever extends Service {
    public List<String> stockSymbols = new ArrayList<>();

    public DataRetriever() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isJSONDataValid(String dataFromConnection) {
        return dataFromConnection != null && !dataFromConnection.isEmpty();
    }
    private JSONArray getQuotesAsJSON(List<String> symbols) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {

        String dataRetrievedString;
        do {
                // TODO: Test for internet connection instead of spamming the server with requests.
                dataRetrievedString = new UrlUtil().getStockData(symbols);
                System.out.println("CP: " + dataRetrievedString);
            } while (!isJSONDataValid(dataRetrievedString));

            JSONObject dataObj = new JSONObject(dataRetrievedString);
            JSONArray stockData;
            stockData = dataObj.getJSONArray("Stock Quotes");
            return stockData;
    }
    private List<StockContent.StockItem> getQuotesAsList(List<String> symbols)
            throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
        JSONArray quotesJSON = getQuotesAsJSON(symbols);
        List <StockContent.StockItem> watchlistData = new ArrayList<>();
        for (int i=0; i < quotesJSON.length(); i++) {

            JSONObject quoteJSON = quotesJSON.getJSONObject(i);
            StockContent.StockItem stockItem = new StockContent.StockItem(
            quoteJSON.getString("1. symbol"),
            quoteJSON.getString("2. price"),
            quoteJSON.getString("3. volume"));

            watchlistData.add(stockItem);
        }
        return watchlistData;
    }

    private void updateDatabaseWithData(List <StockContent.StockItem> stockDataList) {
        AppDatabase db = MainActivity.db;
        for (StockContent.StockItem item : stockDataList) {
            db.watchlistDao().updateBySymbol(item.price, item.volume, item.symbol);
        }

    }

    private void updateListOfStocksInDB() {
        AppDatabase db = MainActivity.db;
        stockSymbols = db.watchlistDao().getAllSymbols();
    }
    private class DelayedTask extends TimerTask {
        @Override
        public void run() {
            try {
                updateDatabaseWithData(getQuotesAsList(stockSymbols));
                updateListOfStocksInDB();
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
        Timer timer = new Timer();
        stockSymbols = Arrays.asList(intent.getStringArrayExtra("symbols"));
        timer.schedule(new DelayedTask(),300, 5000);
        // Setting the flag below will keep the service running after the app is closed.
        // TODO: Need to configure this in order to provide notifications.
        //flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}
