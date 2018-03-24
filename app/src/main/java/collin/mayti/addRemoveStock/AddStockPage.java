package collin.mayti.addRemoveStock;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

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
import collin.mayti.stockDetails.StockDetailsListViewAdapter;
import collin.mayti.stockSymbolDB.SymbolViewModel;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.Stock;

import static android.content.ContentValues.TAG;

/**
 * Created by collinhpreston on 15/02/2018.
 */

public class AddStockPage extends Fragment {

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
                    getFullStockData(searchSymbolEditTxt.getText().toString());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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
        return watchlistDBViewModel.findBySymbolAndWatchlist(stock.getWatchlist(), stock.getSymbol()) != null;
    }

    private void tryToAddStockToWatchlist(Stock stock) throws ExecutionException, InterruptedException {
        // Need to set the symbol to uppercase since this is how we link the database updates with the
        // data being retrieved.  The data comes in with uppercase symbols, so when we update the symbol
        // the sql statement looks for is an uppercase symbol.  This is also preferred for display.
        stock.setSymbol(stock.getSymbol().toUpperCase());

        // If the stock is not already in the database for that watchlist, add to the database.
        if (!isStockAlreadyInDatabaseOnWatchlist(stock)) {
            watchlistDBViewModel.addItem(stock);
            // Return true on successful insert.
        }
        else {
            Toast.makeText(getActivity(), stock.getSymbol() + " is already on that watchlist.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isJSONDataValid(String dataFromConnection) {
        return dataFromConnection != null && !dataFromConnection.isEmpty();
    }

    /**
     * This method is called to retrieve the full data associated with a stock symbol.  It takes the
     * stock symbol as a parameter and returns a hashmap using the field as the key and the field value
     * as the value.
     * @param dataRetrievedString
     * @return
     */
    private HashMap<String, String> getFullDataAsHashMap(String dataRetrievedString) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Call to private method to get the JSON string of data.

        JSONObject dataObj;
        if (isJSONDataValid(dataRetrievedString)) {
            dataObj = new JSONObject(dataRetrievedString);

            HashMap<String, String> fullDataMap = new HashMap<>();
            // TODO: Finish exporting to/importing these strings from strings.xml
            fullDataMap.put(getContext().getString(R.string.symbol), dataObj.getString("symbol"));
            fullDataMap.put(getContext().getString(R.string.company_name), dataObj.getString("companyName"));
            fullDataMap.put(getContext().getString(R.string.primary_exchange), dataObj.getString("primaryExchange"));
            fullDataMap.put(getContext().getString(R.string.calculation_price), dataObj.getString("calculationPrice"));
            fullDataMap.put(getContext().getString(R.string.open), dataObj.getString("open"));
            fullDataMap.put(getContext().getString(R.string.open_time), dataObj.getString("openTime"));
            fullDataMap.put(getContext().getString(R.string.close), dataObj.getString("close"));
            fullDataMap.put(getContext().getString(R.string.close_time), dataObj.getString("closeTime"));
            fullDataMap.put(getContext().getString(R.string.high), dataObj.getString("high"));
            fullDataMap.put(getContext().getString(R.string.low), dataObj.getString("low"));
            fullDataMap.put(getContext().getString(R.string.latest_price), dataObj.getString("latestPrice"));
            fullDataMap.put(getContext().getString(R.string.latest_source), dataObj.getString("latestSource"));
            fullDataMap.put(getContext().getString(R.string.latest_time), dataObj.getString("latestTime"));
            fullDataMap.put(getContext().getString(R.string.latest_update), dataObj.getString("latestUpdate"));
            fullDataMap.put(getContext().getString(R.string.latest_volume), dataObj.getString("latestVolume"));
            fullDataMap.put(getContext().getString(R.string.previous_close), dataObj.getString("previousClose"));
            fullDataMap.put(getContext().getString(R.string.change), dataObj.getString("changePercent"));
            fullDataMap.put(getContext().getString(R.string.market_cap), dataObj.getString("marketCap"));
            fullDataMap.put(getContext().getString(R.string.pe_ratio), dataObj.getString("peRatio"));
            fullDataMap.put(getContext().getString(R.string.week_52_high), dataObj.getString("week52High"));
            fullDataMap.put(getContext().getString(R.string.week_52_low), dataObj.getString("week52Low"));
            fullDataMap.put(getContext().getString(R.string.ytd_change), dataObj.getString("ytdChange"));
            return fullDataMap;
            }
        return null;
    }

    private void getFullStockData (String symbol) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();
        URL requestURL = urlUtil.buildURLForFullStockData(symbol);
        final GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                // At this point I also want to display the listview.
                HashMap<String, String> fullDataMap = getFullDataAsHashMap(output);
                ListView fullDetailsListView = rootView.findViewById(R.id.stockDetailsList);
                StockDetailsListViewAdapter stockDetailsListViewAdapter = new StockDetailsListViewAdapter(getContext(), fullDataMap);
                fullDetailsListView.setAdapter(stockDetailsListViewAdapter);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {
                rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }
        });
        getJSONData.execute(requestURL);
    }

}
