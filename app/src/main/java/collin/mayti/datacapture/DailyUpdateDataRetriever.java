package collin.mayti.datacapture;


/**
 * Created by chpreston on 3/7/18.
 *
 */

import android.content.Context;
import android.os.AsyncTask;

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

        try {

            String SYMBOL_URL_PATH = "ftp://ftp.nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt";
            URL symbolUrl = new URL(SYMBOL_URL_PATH);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(symbolUrl.openStream()));
            String str;
            int i = 0;
            // Read in each line into the str variable.  Skip the first line, and read only the
            // first characters up to the delimiter '|'.  Save that to the list.
            while ((str = in.readLine()) != null) {
                // Test to see if this is the first line.  We don't want to read the headings.
                if (i != 0) {
                    // Find out the index of the first occurring delimiter and read the characters
                    // up to that index.  This is the symbol.
                    String symbol = str.substring(0, str.indexOf('|'));
                    Symbol symbolItem = new Symbol();
                    symbolItem.setSymbol(symbol);
                    symbolsList.add(symbolItem);
                    System.out.println(symbol);
                }
                i++;
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // IF we reach this point, that means we were unable to download the stock symbol list
            // on the first attempt.
        } catch (IOException e) {
            e.printStackTrace();
        }

        // As long as the symbols list is not empty, we will update the database.
        if (!symbolsList.isEmpty()) {
            symbolDb.symbolDbDao().insertAll(symbolsList);
        }

        // Check if today is a
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
