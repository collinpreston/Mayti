package collin.mayti.stockNewsDB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class StockNewsViewModel extends AndroidViewModel {

    private StockNewsDatabase stockNewsDatabase;

    private static List<Article> articleList;

    public StockNewsViewModel(@NonNull Application application) {
        super(application);
        stockNewsDatabase = StockNewsDatabase.getDatabase(this.getApplication());
    }

    public List<Article> getAllArticles(String symbol) throws ExecutionException, InterruptedException {
        new StockNewsViewModel.getAllArticlesAsyncTask(stockNewsDatabase, symbol).execute().get();
        return articleList;
    }

    private static class getAllArticlesAsyncTask extends AsyncTask<String, Void, Void> {

        private StockNewsDatabase db;
        private String mSymbol;

        getAllArticlesAsyncTask(StockNewsDatabase stockNewsDatabase, String symbol) {
            db = stockNewsDatabase;
            mSymbol = symbol;
        }

        @Override
        protected Void doInBackground(String... params) {
            articleList = db.stockNewsDbDao().findAllArticlesBySymbol(mSymbol);
            return null;
        }

    }

}
