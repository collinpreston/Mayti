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
    protected String ONE_DAY_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1d";
    protected String FIVE_DAY_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/5d";
    protected String ONE_MONTH_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1m";
    protected String SIX_MONTH_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/6m";
    protected String ONE_YEAR_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1y";

    private final String ONE_DAY_CHART = "ONE_DAY_CHART";
    private final String FIVE_DAY_CHART = "FIVE_DAY_CHART";
    private final String ONE_MONTH_CHART = "ONE_MONTH_CHART";
    private final String SIX_MONTH_CHART = "SIX_MONTH_CHART";
    private final String ONE_YEAR_CHART = "ONE_YEAR_CHART";

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

    public URL buildURLForChartData(String symbol, String lengthOfTime) throws MalformedURLException {
        String replacedString = null;
        switch (lengthOfTime) {
            case ONE_DAY_CHART:
                replacedString = ONE_DAY_CHART_URL.replace("REPLACE", symbol);
                break;
            case FIVE_DAY_CHART:
                replacedString = FIVE_DAY_CHART_URL.replace("REPLACE", symbol);
                break;
            case ONE_MONTH_CHART:
                replacedString = ONE_MONTH_CHART_URL.replace("REPLACE", symbol);
                break;
            case SIX_MONTH_CHART:
                replacedString = SIX_MONTH_CHART_URL.replace("REPLACE", symbol);
                break;
            case ONE_YEAR_CHART:
                replacedString = ONE_YEAR_CHART_URL.replace("REPLACE", symbol);
                break;
        }
        return new URL(replacedString);
    }

}