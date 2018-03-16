package collin.mayti.datacapture;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.urlUtil.UrlUtil;

/**
 * Created by chpreston on 3/15/18.
 */

public class FullDataRetriever {
    private Context appContext;

    FullDataRetriever(Context context) {
        appContext = context;
    }

    private boolean isJSONDataValid(String dataFromConnection) {
        return dataFromConnection != null && !dataFromConnection.isEmpty();
    }

    private JSONArray getFullDataAsJSON(String symbol) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        String dataRetrievedString;
        do {
            // TODO: Test for internet connection instead of spamming the server with requests.
            dataRetrievedString = new UrlUtil().getFullStockData(symbol);
            System.out.println("CP: " + dataRetrievedString);
        } while (!isJSONDataValid(dataRetrievedString));

        JSONObject dataObj = new JSONObject(dataRetrievedString);
        JSONArray stockData;
        // TODO: need to change this to conform with IEX JSON format.
        stockData = dataObj.getJSONArray("");
        return stockData;
    }

    /**
     * This method is called to retrieve the full data associated with a stock symbol.  It takes the
     * stock symbol as a parameter and returns a hashmap using the field as the key and the field value
     * as the value.
     * @param symbol
     * @return
     */
    public HashMap<String, String> getFullDataAsHashMap(String symbol) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Call to private method to get the JSON string of data.
        JSONArray quotesJSON = getFullDataAsJSON(symbol);
        HashMap <String, String> fullDataMap = new HashMap<>();
        for (int i=0; i < quotesJSON.length(); i++) {

            JSONObject quoteJSON = quotesJSON.getJSONObject(i);

            // TODO: Finish exporting/importing these strings from strings.xml
            fullDataMap.put(appContext.getString(R.string.symbol), quoteJSON.getString("symbol"));
            fullDataMap.put(appContext.getString(R.string.company_name), quoteJSON.getString(appContext.getString(R.string.company_name_key)));
            fullDataMap.put(appContext.getString(R.string.primary_exchange), quoteJSON.getString("primaryExchange"));
            fullDataMap.put(appContext.getString(R.string.calculation_price), quoteJSON.getString("calculationPrice"));
            fullDataMap.put("open", quoteJSON.getString("open"));
            fullDataMap.put("openTime", quoteJSON.getString("openTime"));
            fullDataMap.put("close", quoteJSON.getString("close"));
            fullDataMap.put("closeTime", quoteJSON.getString("closeTime"));
            fullDataMap.put("high", quoteJSON.getString("high"));
            fullDataMap.put("low", quoteJSON.getString("low"));
            fullDataMap.put("latestPrice", quoteJSON.getString("latestPrice"));
            fullDataMap.put("latestSource", quoteJSON.getString("latestSource"));
            fullDataMap.put("latestTime", quoteJSON.getString("latestTime"));
            fullDataMap.put("latestUpdate", quoteJSON.getString("latestUpdate"));
            fullDataMap.put("latestVolume", quoteJSON.getString("latestVolume"));
            fullDataMap.put("previousClose", quoteJSON.getString("previousClose"));
            fullDataMap.put("change", quoteJSON.getString("changePercent"));
            fullDataMap.put("marketCap", quoteJSON.getString("marketCap"));
            fullDataMap.put("peRatio", quoteJSON.getString("peRatio"));
            fullDataMap.put("week52High", quoteJSON.getString("week52High"));
            fullDataMap.put("week52Low", quoteJSON.getString("week52Low"));
            fullDataMap.put("ytdChange", quoteJSON.getString("ytdChange"));

        }
        return fullDataMap;
    }
}
