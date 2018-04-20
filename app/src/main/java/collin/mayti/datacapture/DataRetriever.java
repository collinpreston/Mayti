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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import collin.mayti.MainActivity;
import collin.mayti.stock.StockContent;
import collin.mayti.stockNewsDB.Article;
import collin.mayti.stockNewsDB.StockNewsDatabase;
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
        return dataFromConnection != null && !dataFromConnection.isEmpty() && !dataFromConnection.equals("");
    }

    private JSONObject getQuotesAsJSON(List<String> symbols) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {

        String dataRetrievedString;
        do {
                // TODO: Test for internet connection instead of spamming the server with requests.
                dataRetrievedString = new UrlUtil().getStockData(symbols);
                System.out.println("CP: " + dataRetrievedString);
            } while (!isJSONDataValid(dataRetrievedString));

            JSONObject dataObj = new JSONObject(dataRetrievedString);
            return dataObj;
    }

    private List<StockContent.StockItem> getQuotesAsList(List<String> symbols)
            throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
        JSONObject quotesJSON = getQuotesAsJSON(symbols);
        List <StockContent.StockItem> watchlistData = new ArrayList<>();
        for (String symbol : symbols) {
            JSONObject jsonStock = quotesJSON.getJSONObject(symbol);
            JSONObject jsonQuote = jsonStock.getJSONObject("quote");
            StockContent.StockItem stockItem = new StockContent.StockItem(
                    jsonQuote.getString("symbol"),
                    jsonQuote.getString("latestPrice"),
                    jsonQuote.getString("latestVolume"),
                    jsonQuote.getString("change"),
                    jsonQuote.getString("changePercent"),
                    jsonQuote.getString("week52High"),
                    jsonQuote.getString("week52Low"),
                    jsonQuote.getString("avgTotalVolume"),
                    jsonQuote.getString("latestUpdate")
                    );
            watchlistData.add(stockItem);
        }
        return watchlistData;
    }

    private void updateDatabaseWithData(List <StockContent.StockItem> stockDataList) {
        AppDatabase db = MainActivity.db;
        for (StockContent.StockItem item : stockDataList) {
            db.watchlistDao().updateBySymbol(item.price, item.volume, item.symbol, item.change, item.changePercent,
                    item.recordHigh, item.recordLow, item.averageVolume, item.latestUpdate);
        }

    }

    private void updateListOfStocksInDB() {
        AppDatabase db = MainActivity.db;
        stockSymbols = db.watchlistDao().getAllSymbols();
    }

    private HashMap<String, JSONArray> getNewsDataAsJSON(List<String> symbols) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {

        // Used to store the data after the asynctask is completed.
        HashMap<String, JSONArray> newsJSONObjectMap = new HashMap<>();


        // Loop through each symbols.
        for (String symbol : symbols) {
            do {
                // TODO: Test for internet connection instead of spamming the server with requests.
                URL newsURL = new UrlUtil().buildURLForNewsData(symbol);

                // Check to make sure that the URL was built correctly.
                if (!newsURL.equals("")) {
                    GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) throws JSONException {
                            JSONArray dataObj = new JSONArray(output);
                            newsJSONObjectMap.put(symbol, dataObj);
                        }
                    }, new GetJSONData.AsyncPreExecute() {
                        @Override
                        public void preExecute() {
                            // Intentionally left empty.
                        }
                    });
                    // Execute the asynctask.
                    getJSONData.execute(newsURL).get();
                }

                // Check to make sure that JSON data was retrieved correctly.
            } while (newsJSONObjectMap.size() == 0);
        }

        return newsJSONObjectMap;
    }

    private HashMap<String, List<Article>> getArticlesAsMap(List<String> symbols) throws InterruptedException, MalformedURLException, ExecutionException, JSONException {
        HashMap<String, JSONArray> newsObjectMap  = getNewsDataAsJSON(symbols);
        List<Article> newsArticleListForSymbol  = new ArrayList<>();
        HashMap<String, List<Article>> articleMap = new HashMap<>();
        for (String symbol : symbols) {
            JSONArray newsArticleJSONArray = newsObjectMap.get(symbol);
            // TODO: Length method call keeps crashing when adding a stock to the watchlist.
            if (newsArticleJSONArray != null) {
                for (int i = 0; i < newsArticleJSONArray.length(); i++) {
                    JSONObject newsArticleJSONObject = new JSONObject();
                    newsArticleJSONObject = newsArticleJSONArray.getJSONObject(i);

                    Article article = new Article();
                    article.setSymbol(symbol);
                    String rawDateTime = newsArticleJSONObject.getString("datetime");
                    int delimeterIndex = rawDateTime.indexOf('T');
                    String formattedDate = rawDateTime.substring(0, delimeterIndex);
                    String formattedTime = rawDateTime.substring(delimeterIndex + 1, delimeterIndex + 6);
                    article.setDateTime(formattedDate + " " + formattedTime);
                    article.setHeadline(newsArticleJSONObject.getString("headline"));
                    article.setSource(newsArticleJSONObject.getString("source"));
                    article.setUrl(newsArticleJSONObject.getString("url"));
                    newsArticleListForSymbol.add(article);
                }
            }
            articleMap.put(symbol, newsArticleListForSymbol);
        }
        return articleMap;
    }

    private void updateNewsDB() throws InterruptedException, MalformedURLException, JSONException, ExecutionException {
        HashMap<String, List<Article>> symbolArticleMap;
        symbolArticleMap = getArticlesAsMap(stockSymbols);
        StockNewsDatabase stockNewsDatabase = MainActivity.stockNewsDatabase;
        for (String symbol : stockSymbols) {
            List<Article> articleList;
            articleList = symbolArticleMap.get(symbol);
            stockNewsDatabase.stockNewsDbDao().insertAll(articleList);
        }
    }

    private class DelayedTask extends TimerTask {

        // Start the news counter at 60.  This will get the news data on the first run.
        private int getNewsData = 60;
        @Override
        public void run() {
            try {
                updateDatabaseWithData(getQuotesAsList(stockSymbols));
                updateListOfStocksInDB();

                // Increment the news counter.
                // Get the news data on every 60th data retrieval.
                if (getNewsData == 60) {
                    // Retrieve news data and update the news database.
                    updateNewsDB();
                    // Set the news counter back to zero
                    getNewsData = 0;
                }
                getNewsData++;
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
        try {
            stockSymbols = Arrays.asList(intent.getStringArrayExtra("symbols"));
        } catch (Exception e) {
        }
        timer.schedule(new DelayedTask(), 300, 5000);
        // Setting the flag below will keep the service running after the app is closed.
        //flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}
