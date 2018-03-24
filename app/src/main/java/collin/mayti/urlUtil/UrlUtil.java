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

    public String getStockData(List<String> symbols) throws MalformedURLException, ExecutionException, InterruptedException {
        URL requestURL = buildURLForStockPrice(symbols);
        GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                dataStream = output;
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {
                // Intentionally left empty.
            }
        });
        getJSONData.execute(requestURL).get();
        return dataStream;
    }
    private URL buildURLForStockPrice(List<String> symbols) throws MalformedURLException {
        String insertString = symbols.get(0);
        // Set i to 1 since we set the first symbol above.
        for (int i=1; i < symbols.size(); i++) {
            insertString = insertString.concat("," + symbols.get(i));
        }
        String replacedString = PRICE_URL.replace("REPLACE", insertString);
        return new URL(replacedString);
    }
    public URL buildURLForFullStockData(String symbol) throws MalformedURLException {
        String replacedString = FULL_DATA_URL.replace("REPLACE", symbol);
        return new URL(replacedString);
    }

}