package collin.mayti.datacapture;


/**
 * Created by chpreston on 3/7/18.
 *
 */

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import collin.mayti.stockSymbolDB.Symbol;
import collin.mayti.stockSymbolDB.SymbolDatabase;

/**
 * This class is kicked off from the MainActivity when the app is opened for the first time each
 * day.  It will update the stock symbol master list from NASDAQ if connected to the internet.
 */
public class DailyUpdateDataRetriever extends AsyncTask {
    private Context mContext;

    public DailyUpdateDataRetriever (Context context) {
        mContext = context;
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        List<Symbol> symbolsList = new ArrayList<>();
        SymbolDatabase symbolDb = SymbolDatabase.getDatabase(mContext);

        // This will be used for holding the string returned by the server.
        StringBuilder buffer = new StringBuilder();
        try {

            String SYMBOL_URL_PATH = "https://api.iextrading.com/1.0/ref-data/symbols";
            URL symbolUrl = new URL(SYMBOL_URL_PATH);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(symbolUrl.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line).append('\n');
            }

            JSONArray jsonArray = new JSONArray(buffer.toString());
            // Check to make sure the array is not empty.
            if (jsonArray.length() != 0) {

                // Loop through each array item to get the symbol.
                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String symbol = jsonObject.getString("symbol");
                    Symbol symbolItem = new Symbol();
                    symbolItem.setSymbol(symbol);

                    // Add the symbol item to the symbolsList.
                    symbolsList.add(symbolItem);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // IF we reach this point, that means we were unable to download the stock symbol list
            // on the first attempt.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // As long as the symbols list is not empty, we will update the database.
        if (!symbolsList.isEmpty()) {
            symbolDb.symbolDbDao().insertAll(symbolsList);
        }

        // TODO: Check if today is a trading day.
        String OPEN_MARKET_DAY_URL = "https://api.iextrading.com/1.0/stats/recent";
        try {
            URL openMarketDayUrl = new URL(OPEN_MARKET_DAY_URL);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

    }
}
