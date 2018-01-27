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
    protected String PRICE_URL = "https://www.alphavantage.co/query?function=BATCH_STOCK_QUOTES&symbols=STOCK&apikey=5M3Y2HWYLNAXUA12";

    private String dataStream;


    public String getStockData(List<String> symbols) throws MalformedURLException, ExecutionException, InterruptedException {
        URL requestURL = buildURLForStockPrice(symbols);
        GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                dataStream = output;
            }
        });
        getJSONData.execute();
        //new GetJSONData(response).execute(requestURL).get();
        return dataStream;
    }
    private URL buildURLForStockPrice(List<String> symbols) throws MalformedURLException {
        // TODO: Create string builder to build URL string of symbol names to replace in the URL.
        String replacedString = PRICE_URL.replace("STOCK", symbols.get(0));
        URL requestURL = new URL(replacedString);
        return requestURL;
    }

}