package collin.mayti.watchlist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

/**
 * Created by collinhpreston on 30/01/2018.
 */

public class WatchlistViewModel extends AndroidViewModel {

    private static int totalNumberOfRows;

    private final LiveData<List<Stock>> stockList;

    private AppDatabase appDatabase;


    public WatchlistViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        stockList = appDatabase.watchlistDao().getAll();
    }
    public LiveData<List<Stock>> getStockList() {
        return stockList;
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
        new addAsyncTask(appDatabase).execute();
        return totalNumberOfRows;
    }

//    private static class getTotalNumberOfRowsAsyncTask extends AsyncTask<Integer, Void, Void> {
//
//        private AppDatabase db;
//
//        getTotalNumberOfRowsAsyncTask(AppDatabase appDatabase) {
//            db = appDatabase;
//        }
//
//        @Override
//        protected void doInBackground(Integer... totalNum) {
//            try {
//                totalNumberOfRows = db.watchlistDao().getTotalNumberOfRows();
//            } catch (Exception e) {
//                totalNumberOfRows = 0;
//            }
//        }
//    }
}
