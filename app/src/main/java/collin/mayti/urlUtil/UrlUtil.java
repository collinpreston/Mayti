package collin.mayti.urlUtil;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.datacapture.GetJSONData;


/**
 * Created by Collin on 1/13/2018.
 */

public class UrlUtil{
    protected String BATCH_QUOTE_URL = "https://api.iextrading.com/1.0/stock/market/batch?symbols=REPLACE&types=quote&range=1m";
    protected String FULL_DATA_URL = "https://api.iextrading.com/1.0/stock/REPLACE/quote";

    protected String NEWS_URL = "https://api.iextrading.com/1.0/stock/REPLACE/news/last/50";

    protected String KEY_STATS_URL = "https://api.iextrading.com/1.0/stock/REPLACE/stats";

    protected String ONE_DAY_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1d";
    protected String FIVE_DAY_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/5d";
    protected String ONE_MONTH_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1m";
    protected String THREE_MONTH_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/3m";
    protected String ONE_YEAR_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/1y";
    protected String FIVE_YEAR_CHART_URL = "https://api.iextrading.com/1.0/stock/REPLACE/chart/5y";

    private final String ONE_DAY_CHART = "ONE_DAY_CHART";
    private final String FIVE_DAY_CHART = "FIVE_DAY_CHART";
    private final String ONE_MONTH_CHART = "ONE_MONTH_CHART";
    private final String THREE_MONTH_CHART = "THREE_MONTH_CHART";
    private final String ONE_YEAR_CHART = "ONE_YEAR_CHART";
    private final String FIVE_YEAR_CHART = "FIVE_YEAR_CHART";

    private String dataStream = "";

    public String getStockData(List<String> symbols) throws MalformedURLException, ExecutionException, InterruptedException {
        URL requestURL = buildURLForStockPrice(symbols);
        if (!requestURL.equals("")) {
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
        }
        return dataStream;
    }

    public HashMap<String, String> getChartData(List<String> symbols, String lengthOfTime) throws MalformedURLException, ExecutionException, InterruptedException {
        HashMap<String, String> chartData = new HashMap<>();
        for (String symbol : symbols) {
            URL requestURL = buildURLForChartData(symbol, lengthOfTime);
            if (!requestURL.equals("")) {
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
            }
            chartData.put(symbol, dataStream);
        }
        return chartData;
    }

    private URL buildURLForStockPrice(List<String> symbols) throws MalformedURLException {
        String insertString = "";
        try {
            insertString = symbols.get(0);
        } catch (IndexOutOfBoundsException e) {
            // If all of the stocks were removed from the application by the user, we will just return an
            // empty URL.
            return new URL(insertString);
        }
        // Set i to 1 since we set the first symbol above.
        for (int i=1; i < symbols.size(); i++) {
            insertString = insertString.concat("," + symbols.get(i));
        }
        String replacedString = BATCH_QUOTE_URL.replace("REPLACE", insertString);
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
            case THREE_MONTH_CHART:
                replacedString = THREE_MONTH_CHART_URL.replace("REPLACE", symbol);
                break;
            case ONE_YEAR_CHART:
                replacedString = ONE_YEAR_CHART_URL.replace("REPLACE", symbol);
                break;
            case FIVE_YEAR_CHART:
                replacedString = FIVE_YEAR_CHART_URL.replace("REPLACE", symbol);
                break;
        }
        return new URL(replacedString);
    }

    public URL buildURLForNewsData(String symbol) throws MalformedURLException {
        String replacedString = NEWS_URL.replace("REPLACE", symbol);
        return new URL(replacedString);
    }

    public URL buildURLForKeyStats(String symbol) throws MalformedURLException {
        String replacedString = KEY_STATS_URL.replace("REPLACE", symbol);
        return new URL(replacedString);
    }

}