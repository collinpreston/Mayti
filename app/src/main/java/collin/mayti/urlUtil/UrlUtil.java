package collin.mayti.urlUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.datacapture.GetJSONData;


/**
 * Created by Collin on 1/13/2018.
 */

public class UrlUtil{
    protected String PRICE_URL = "https://www.alphavantage.co/query?function=BATCH_STOCK_QUOTES&symbols=REPLACE&apikey=5M3Y2HWYLNAXUA12";
    protected String FULL_DATA_URL = "https://api.iextrading.com/1.0/stock/REPLACE/quote";

    private String dataStream;
    private String fullDataStream;


    public String getStockData(List<String> symbols) throws MalformedURLException, ExecutionException, InterruptedException {
        URL requestURL = buildURLForStockPrice(symbols);
        GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                dataStream = output;
            }
        });
        getJSONData.execute(requestURL).get();
        return dataStream;
    }
    private URL buildURLForStockPrice(List<String> symbols) throws MalformedURLException {
        String replacedString = PRICE_URL.replace("REPLACE", symbols.get(0));
        return new URL(replacedString);
    }

    private URL buildURLForFullStockData(String symbol) throws MalformedURLException {
        String replacedString = FULL_DATA_URL.replace("REPLACE", symbol);
        return new URL(replacedString);
    }
    public String getFullStockData (String symbol) throws MalformedURLException, ExecutionException, InterruptedException {
        URL requestURL = buildURLForFullStockData(symbol);
        GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                fullDataStream = output;
            }
        });
        getJSONData.execute(requestURL).get();

        return fullDataStream;
    }

}