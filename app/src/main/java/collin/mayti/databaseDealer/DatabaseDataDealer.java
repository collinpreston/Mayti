package collin.mayti.databaseDealer;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import collin.mayti.MainActivity;
import collin.mayti.stock.StockContent;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

import static collin.mayti.stock.StockContent.*;

/**
 * Created by collinhpreston on 27/01/2018.
 */

public class DatabaseDataDealer {
    public DatabaseDataDealer () {

    }

    public List<StockItem> getAllWatchlistData (List<StockItem> uiUpdatedWatchlist) {
        final List<StockItem> watchlistData = new ArrayList<>();
        GetWatchlistDataFromDB getWatchlistDataFromDBIntent = new GetWatchlistDataFromDB(new AsyncResponse() {
            @Override
            public void processFinish(List<StockItem> output) {
                    watchlistData.addAll(output);
            }
        });
        getWatchlistDataFromDBIntent.execute();

        return watchlistData;
    }

    private class GetWatchlistDataFromDB extends AsyncTask<String, Integer, List<StockContent.StockItem>> {
        private AsyncResponse taskResponse;

        public GetWatchlistDataFromDB(AsyncResponse taskComplete) {
            this.taskResponse = taskComplete;
        }
        // Use the string parameter to specify the watchlist to be queuried
        @Override
        protected List<StockContent.StockItem> doInBackground(String... strings) {
            AppDatabase db = MainActivity.db;
            List<Stock> stockDataForWatchlist = (List<Stock>) db.watchlistDao().getAll();
            return convertStockToStockItem(stockDataForWatchlist);
        }
        @Override
        protected void onPostExecute(List<StockContent.StockItem> s) {
            // In onPostExecute we check if the listener is valid
            if (this.taskResponse != null) {
                // And if it is we call the callback function on it.
                this.taskResponse.processFinish(s);
            }
        }
    }


    public interface AsyncResponse {
        void processFinish(List<StockContent.StockItem> output);
    }

    private static List<StockContent.StockItem> convertStockToStockItem(List<Stock> stockList){
        List<StockContent.StockItem> stockItemList = new ArrayList<>();
        for (Stock stock : stockList) {
            stockItemList.add(new StockItem(stock.getSymbol(), stock.getPrice(), stock.getVolume()));
        }
        return stockItemList;
    }

}
