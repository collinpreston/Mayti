package collin.mayti.addRemoveStock;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.applicationSettingsDB.SettingViewModel;
import collin.mayti.datacapture.GetJSONData;
import collin.mayti.stockSymbolDB.SymbolViewModel;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.Stock;

import static android.content.ContentValues.TAG;

/**
 * Created by collinhpreston on 15/02/2018.
 */

public class AddStockPage extends Fragment {
    protected String FULL_DATA_URL = "https://api.iextrading.com/1.0/stock/REPLACE/quote";
    private String fullDataStream;

    private static final String DAILY_WATCHLIST_NAME = "daily_watchlist";
    private static final String WEEKLY_WATCHLIST_NAME = "weekly_watchlist";
    private static final String PERMANENT_WATCHLIST_NAME = "permanent_watchlist";

    private static List<String> symbolList;
    private WatchlistViewModel watchlistDBViewModel;

    private MaterialSheetFab materialSheetFab;
    private EditText searchSymbolEditTxt;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SettingViewModel settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        try {
            if (settingViewModel.readSetting("SYMBOL_DATABASE_LAST_UPDATE").equals("")) {
                // TODO: The symbol database has not been populated.  Thus, the user can't search for
                // stock symbols to add to watchlists.  So instead, we should try to download the symbol
                // list once more.  If it fails, then we should display a "connect to the internet" page.
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SymbolViewModel symbolDBViewModel = ViewModelProviders.of(this).get(SymbolViewModel.class);
        try {
            symbolList = symbolDBViewModel.readAllSymbols();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);
        rootView.setTag(TAG);

        // Initialize the viewModel
        watchlistDBViewModel = ViewModelProviders.of(this).get(WatchlistViewModel.class);

        searchSymbolEditTxt = rootView.findViewById(R.id.searchSymbolEditTxt);
        // With a successful search, the data for the stock will be displayed in the listview.
        searchSymbolEditTxt.addTextChangedListener(mTextEditorWatcher);


        // Floating action button
        Fab fab = rootView.findViewById(R.id.fab);
        View sheetView = rootView.findViewById(R.id.fab_sheet);
        View overlay = rootView.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.cardViewBackground);
        int fabColor = getResources().getColor(R.color.lightBlue);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);
        materialSheetFab.hideSheetThenFab();
        // Set up cardview options on FAB.
        View addToDailyWatchlistOption = rootView.findViewById(R.id.fab_sheet_item_daily_watchlist);
        View addToPermWatchlistOption = rootView.findViewById(R.id.fab_sheet_item_permanent_watchlist);
        View addToWeeklyWatchlistOption = rootView.findViewById(R.id.fab_sheet_item_weekly_watchlist);

        addToDailyWatchlistOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stock stockItem = new Stock();
                stockItem.setWatchlist(DAILY_WATCHLIST_NAME);
                stockItem.setSymbol(searchSymbolEditTxt.getText().toString());
                try {
                    tryToAddStockToWatchlist(stockItem);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                materialSheetFab.hideSheet();
            }
        });

        addToPermWatchlistOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stock stockItem = new Stock();
                stockItem.setWatchlist(PERMANENT_WATCHLIST_NAME);
                stockItem.setSymbol(searchSymbolEditTxt.getText().toString());
                try {
                    tryToAddStockToWatchlist(stockItem);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                materialSheetFab.hideSheet();
            }
        });

        addToWeeklyWatchlistOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stock stockItem = new Stock();
                stockItem.setWatchlist(WEEKLY_WATCHLIST_NAME);
                stockItem.setSymbol(searchSymbolEditTxt.getText().toString());
                try {
                    tryToAddStockToWatchlist(stockItem);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                materialSheetFab.hideSheet();
            }
        });

        return rootView;
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Search for the symbol from the symbol master list.
            if (symbolList.contains(searchSymbolEditTxt.getText().toString().toUpperCase())) {
                // Show the fab.
                materialSheetFab.showFab();

                // Get the data for the full details list.
                try {
                    getFullData(searchSymbolEditTxt.getText().toString());
                } catch (ExecutionException e) {
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            } else {
                materialSheetFab.hideSheetThenFab();
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private boolean isStockAlreadyInDatabaseOnWatchlist(Stock stock) throws ExecutionException, InterruptedException {
        if (watchlistDBViewModel.findBySymbolAndWatchlist(stock.getWatchlist(), stock.getSymbol()) == null) {
            // The symbol and watchlist combination must exist in the database
            return false;
        }
        return true;
    }

    private boolean tryToAddStockToWatchlist(Stock stock) throws ExecutionException, InterruptedException {

        // If the stock is not already in the database for that watchlist, add to the database.
        if (!isStockAlreadyInDatabaseOnWatchlist(stock)) {
            watchlistDBViewModel.addItem(stock);
            // Return true on successful insert.
            return true;
        }
        else {
            Toast.makeText(getActivity(), stock.getSymbol() + " is already on that watchlist.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isJSONDataValid(String dataFromConnection) {
        return dataFromConnection != null && !dataFromConnection.isEmpty();
    }

    private JSONArray getFullDataAsJSON(String symbol) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        String dataRetrievedString;
        // TODO: Test for internet connection instead of spamming the server with requests.
        dataRetrievedString = getFullStockData(symbol);
        System.out.println("CP: " + dataRetrievedString);
        JSONArray stockData = null;
        if (isJSONDataValid(dataRetrievedString)) {
            JSONObject dataObj = new JSONObject(dataRetrievedString);
            // TODO: need to change this to conform with IEX JSON format.
            stockData = dataObj.getJSONArray("");
        }
        return stockData;
    }

    /**
     * This method is called to retrieve the full data associated with a stock symbol.  It takes the
     * stock symbol as a parameter and returns a hashmap using the field as the key and the field value
     * as the value.
     * @param symbol
     * @return
     */
    private HashMap<String, String> getFullDataAsHashMap(String symbol) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Call to private method to get the JSON string of data.
        JSONArray quotesJSON = getFullDataAsJSON(symbol);
        if (quotesJSON != null) {
            HashMap<String, String> fullDataMap = new HashMap<>();
            for (int i = 0; i < quotesJSON.length(); i++) {

                JSONObject quoteJSON = quotesJSON.getJSONObject(i);

                // TODO: Finish exporting to/importing these strings from strings.xml
                fullDataMap.put("Symbol", quoteJSON.getString("symbol"));
                fullDataMap.put("Company Name", quoteJSON.getString("companyName"));
                fullDataMap.put("Exchange", quoteJSON.getString("primaryExchange"));
                fullDataMap.put("Calculation Price", quoteJSON.getString("calculationPrice"));
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
                return fullDataMap;
            }
        }
        return null;
    }

    private HashMap<String, String> getFullData(String symbol) throws ExecutionException, InterruptedException, MalformedURLException, JSONException {
        return getFullDataAsHashMap(symbol);
    }

    private String getFullStockData (String symbol) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();
        URL requestURL = urlUtil.buildURLForFullStockData(symbol);
        GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                fullDataStream = output;
                rootView.findViewById(R.id.progressBar).setVisibility(rootView.GONE);
                // At this point I also want to display the listview.

                System.out.println(fullDataStream);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {
                rootView.findViewById(R.id.progressBar).setVisibility(rootView.VISIBLE);
            }
        });
        getJSONData.execute(requestURL);
        return fullDataStream;
    }

}
