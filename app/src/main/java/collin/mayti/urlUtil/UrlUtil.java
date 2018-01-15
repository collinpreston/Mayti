package collin.mayti.urlUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import collin.mayti.datacapture.GetJSONData;


/**
 * Created by Collin on 1/13/2018.
 */

public class UrlUtil implements AsyncResponse{
    protected String PRICE_URL = "https://www.alphavantage.co/query?function=BATCH_STOCK_QUOTES&symbols=MSFT,FB,AAPL&apikey=demo";

    private String dataStream;


    public String getStockData(List<String> symbols) throws MalformedURLException {
        URL requestURL = buildURLForStockPrice(symbols);

        new GetJSONData().execute(requestURL);
        return dataStream;
    }
    private URL buildURLForStockPrice(List<String> symbols) throws MalformedURLException {
        // TODO: Create string builder to build URL string of symbol names to replace in the URL.
        //String replacedString = PRICE_URL.replace("REPLACE", symbols.get(0));
        URL requestURL = new URL(PRICE_URL);
        return requestURL;
    }

    @Override
    public void processFinish(String output) {
        dataStream = output;
    }
}
