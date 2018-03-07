package collin.mayti.watchlist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

/**
 * Created by collinhpreston on 30/01/2018.
 */

public class WatchlistViewModel extends AndroidViewModel {

    private static int totalNumberOfRowsForDB;
    private static int totalNumberOfRowsForWatchlist;
    private static List<Stock> stocksForWatchlist;

    private static final String DAILY_WATCHLIST_NAME = "daily_watchlist";
    private static final String WEEKLY_WATCHLIST_NAME = "weekly_watchlist";
    private static final String PERMANENT_WATCHLIST_NAME = "permanent_watchlist";

    private final LiveData<List<Stock>> stockList;
    private final LiveData<List<Stock>> dailyStockList;
    private final LiveData<List<Stock>> weeklyStockList;

    private AppDatabase appDatabase;


    public WatchlistViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        stockList = appDatabase.watchlistDao().getLiveDataForWatchlist(PERMANENT_WATCHLIST_NAME);
        dailyStockList = appDatabase.watchlistDao().getLiveDataForWatchlist(DAILY_WATCHLIST_NAME);
        weeklyStockList = appDatabase.watchlistDao().getLiveDataForWatchlist(WEEKLY_WATCHLIST_NAME);
    }
    public LiveData<List<Stock>> getStockList() {
        return stockList;
    }

    public LiveData<List<Stock>> getStockListForWatchlist(String watchlistID) {
        switch (watchlistID) {
            case PERMANENT_WATCHLIST_NAME:
                return stockList;
            case DAILY_WATCHLIST_NAME:
                return dailyStockList;
            case WEEKLY_WATCHLIST_NAME:
                return weeklyStockList;
        }
        return null;
    }

    public void deleteItem(Stock stock) {
        new deleteAsyncTask(appDatabase).execute(stock);
    }

    private static class deleteAsyncTask extends AsyncTask<Stock, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Stock... params) {
            db.watchlistDao().delete(params[0]);
            return null;
        }

    }
    public void addItem(Stock stock) {
        new addAsyncTask(appDatabase).execute(stock);
    }

    private static class addAsyncTask extends AsyncTask<Stock, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Stock... params) {
            db.watchlistDao().insertStock(params[0]);
            return null;
        }

    }
    public int getTotalNumberOfRows() {
        new getTotalNumberOfRowsAsyncTask(appDatabase).execute();
        return totalNumberOfRowsForDB;
    }

    private static class getTotalNumberOfRowsAsyncTask extends AsyncTask<Integer, Void, Void> {

        private AppDatabase db;

        getTotalNumberOfRowsAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(Integer... totalNum) {
            try {
                totalNumberOfRowsForDB = db.watchlistDao().getTotalNumberOfRows();
            } catch (Exception e) {
                totalNumberOfRowsForDB = 0;
            }
            return null;
        }
    }
    public int getTotalNumberOfStocksForWatchlist(String watchlistName) throws ExecutionException, InterruptedException {
        new getTotalNumberOfStocksForWatchlistAsyncTask(appDatabase, watchlistName).execute().get();
        return totalNumberOfRowsForWatchlist;
    }

    private static class getTotalNumberOfStocksForWatchlistAsyncTask extends AsyncTask<String, Void, Void> {

        private AppDatabase db;
        private String watchlistID;

        getTotalNumberOfStocksForWatchlistAsyncTask(AppDatabase appDatabase, String watchlistName) {
            db = appDatabase;
            watchlistID = watchlistName;
        }

        @Override
        protected Void doInBackground(String... watchlistName) {
            try {
                totalNumberOfRowsForWatchlist = db.watchlistDao().getTotalNumberOfStocksForWatchlist(watchlistID);
            } catch (Exception e) {
                totalNumberOfRowsForWatchlist = 0;
            }
            return null;
        }
    }
    public List<Stock> getAllStocksForWatchlist(String watchlistName) throws ExecutionException, InterruptedException {
        new getAllStocksForWatchlistAsyncTask(appDatabase, watchlistName).execute().get();
        return stocksForWatchlist;
    }

    private static class getAllStocksForWatchlistAsyncTask extends AsyncTask<String, Void, Void> {

        private AppDatabase db;
        private String watchlistID;

        getAllStocksForWatchlistAsyncTask(AppDatabase appDatabase, String watchlistName) {
            db = appDatabase;
            watchlistID = watchlistName;
        }

        @Override
        protected Void doInBackground(String... watchlistName) {
            try {
                stocksForWatchlist = db.watchlistDao().getAllStocksForWatchlist(watchlistID);
            } catch (Exception e) {
                // This list will have to be validated by the receiving method.
            }
            return null;
        }
    }
}