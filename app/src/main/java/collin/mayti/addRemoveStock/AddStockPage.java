package collin.mayti.addRemoveStock;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.applicationSettingsDB.SettingObject;
import collin.mayti.applicationSettingsDB.SettingViewModel;
import collin.mayti.datacapture.DailyUpdateDataRetriever;
import collin.mayti.datacapture.DataRetriever;
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

/**
 * Class that displays the add Stock page and allows the user to add a new stock to a watchlist.
 */
public class AddStockPage extends Fragment {

    private static final String DAILY_WATCHLIST_NAME = "daily_watchlist";
    private static final String WEEKLY_WATCHLIST_NAME = "weekly_watchlist";
    private static final String PERMANENT_WATCHLIST_NAME = "permanent_watchlist";

    private static final String SYMBOL_DATABASE_LAST_UPDATE = "SYMBOL_DATABASE_LAST_UPDATE";

    private static List<String> symbolList;
    private WatchlistViewModel watchlistDBViewModel;

    private MaterialSheetFab materialSheetFab;
    private EditText searchSymbolEditTxt;

    private View rootView;

    private boolean symbolsDBIsEmpty = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SettingViewModel settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        try {
            if (settingViewModel.readSetting("SYMBOL_DATABASE_LAST_UPDATE").getSettingValue().equals("")) {
                symbolsDBIsEmpty = true;
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

        // If true, display a overlay screen showing text saying unable to download symbols.
        // TODO: Need to customize this view more.  Update font and add graphic.
        if (symbolsDBIsEmpty) {
            ConstraintLayout retryDownloadOverlay = rootView.findViewById(R.id.retryDownloadOverlay);
            retryDownloadOverlay.setVisibility(View.VISIBLE);
            retryDownloadOverlay.setAlpha(1);
        }
        else {
            ConstraintLayout retryDownloadOverlay = rootView.findViewById(R.id.retryDownloadOverlay);
            retryDownloadOverlay.setVisibility(View.GONE);
            retryDownloadOverlay.setAlpha(1);
        }

        Button retrySymbolDownloadBtn = rootView.findViewById(R.id.retrySymbolBtn);
        retrySymbolDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSymbolDatabaseAndSetting();
            }
        });

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

    /**
     * This method watches the text changes made to the search bar text entry.  When the text entered
     * by the user matches a stock symbol in the symbol database, getFullStockData is passed the
     * symbol currently entered.
     */
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

    /**
     * Method to check if the stock is already in the watchlist database.  This takes into consideration
     * the composite key which combines the symbol and watchlist.  If a stock already exists in the database
     * with the same symbol and watchlist, this method will return true.
     * @param stock
     * @return boolean
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private boolean isStockAlreadyInDatabaseOnWatchlist(Stock stock) throws ExecutionException, InterruptedException {
        return watchlistDBViewModel.findBySymbolAndWatchlist(stock.getWatchlist(), stock.getSymbol()) != null;
    }

    /**
     * This method takes a Stock object and tries to add it to the watchlist, but first checks if
     * the stock already exists in the watchlist or else it displays a toast message.  It also checks
     * whether this is the first stock being added to the app.  If so, then the dataretriever service
     * is kicked off.  Otherwise, the dataratriever service should already be running since stocks exist.
     * @param stock
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void tryToAddStockToWatchlist(Stock stock) throws ExecutionException, InterruptedException {
        // Need to set the symbol to uppercase since this is how we link the database updates with the
        // data being retrieved.  The data comes in with uppercase symbols, so when we update the symbol
        // the sql statement looks for is an uppercase symbol.  This is also preferred for display.
        stock.setSymbol(stock.getSymbol().toUpperCase());

        // Check if this is the first stock being added to the database.  If so, then we need to kick
        // off the data retriever service.
        boolean needToStartDataService = false;
        // We have to check this prior to the addItem method below since the add item method has an
        // asyncTask which might not complete by the time we run getTotalNumberOfRows, thus making it
        // impossible to predict the actual expected number of rows.
        if (watchlistDBViewModel.getTotalNumberOfRows() == 0) {
            needToStartDataService = true;
        }

        // If the stock is not already in the database for that watchlist, add to the database.
        if (!isStockAlreadyInDatabaseOnWatchlist(stock)) {
            // Adding the stock to the watchlist database
            watchlistDBViewModel.addItem(stock);

            // Here we check the above boolean to see if this is the first stock being added to the DB.
            if (needToStartDataService) {
                Intent dataRetrieverIntent = new Intent(getContext(), DataRetriever.class);
                final String[] myStocks = new String [1];
                myStocks[0] = stock.getSymbol();
                dataRetrieverIntent.putExtra("symbols", myStocks);
                getActivity().startService(dataRetrieverIntent);
            }
        }
        else {
            Toast.makeText(getActivity(), stock.getSymbol() + " is already on that watchlist.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method assures that the JSON data retrieved is not null or empty.  If the string parameter
     * is valid JSONData, this method returns true.  Otherwise this method returns false if the data
     * is not valid.  More validations can be
     * applied in this method.
     * @param dataFromConnection
     * @return boolean
     */
    private boolean isJSONDataValid(String dataFromConnection) {
        return dataFromConnection != null && !dataFromConnection.isEmpty();
    }

    /**
     * This method is called to retrieve the full data associated with a stock symbol.  It takes the
     * stock symbol as a parameter and returns a hashmap using the field as the key and the field value
     * as the value.
     *
     * @param dataRetrievedString
     * @return HashMap
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws MalformedURLException
     * @throws JSONException
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

    /**
     * This method takes in the stock symbol having been entered in the search bar, and retrieves
     * the full data quote for the stock.  On process finish, this method populates the StockDetailsListViewAdapter
     * which creates the listview for the full data quote on the addStockPage.
     * @param symbol
     * @throws MalformedURLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
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

    /**
     * This method updates the symbol database and settings database from the AddStockPage.  Once
     * the symbol list is downloaded and stored in the symbol database, the settings database
     * is updated, and the retry download overlay view is made invisible and the user is allowed to
     * add a new stock to a watchlist.  If unable to connect or download the data from NASDAQ,
     * this method will continue to display the retry
     * download overlay view.  This method is similar to the methods in SplashScreenActivity, but must
     * be contained in the respective class they are used since they interact with the UI of each
     * fragment/page.
     */
    public void updateSymbolDatabaseAndSetting() {
        // TODO: HOLY SMOKES!  I need to comment all of this mess....

        final UpdateSymbolAndSettingAsyncTask updateSymbolAndSettingAsyncTask = new UpdateSymbolAndSettingAsyncTask(new UpdateSymbolAndSettingAsyncTask.AsyncResponse() {
            @Override
            public void processFinish() throws ExecutionException, InterruptedException {
                SettingViewModel viewModel = ViewModelProviders.of(getActivity()).get(SettingViewModel.class);
                SymbolViewModel symbolViewModel = ViewModelProviders.of(getActivity()).get(SymbolViewModel.class);
                // Check if the symbol database length is larger than 0 in order to see if it downloaded.
                if (symbolViewModel.readAllSymbols().size() > 0) {
                    // Update the setting value to today's date.
                    SettingObject settingObject = new SettingObject();
                    settingObject.setSettingID(SYMBOL_DATABASE_LAST_UPDATE);
                    // Today's date string.
                    String currentDateString = DateFormat.getDateInstance().format(new java.util.Date());
                    settingObject.setSettingValue(currentDateString);
                    viewModel.updateSetting(settingObject);

                    // Here I need to clear the retryDownloadOverlay view.  The user is now ready
                    // to add a new stock.
                    // TODO: Create a method to show and hide the overlay.
                    ConstraintLayout retryDownloadOverlay = rootView.findViewById(R.id.retryDownloadOverlay);
                    retryDownloadOverlay.setVisibility(View.GONE);
                    retryDownloadOverlay.setAlpha(0);
                } else {
                    // If it has not been downloaded, display retry/continue page.  If user chooses continue, insert
                    // empty string for setting value.
                    SettingObject settingObject = new SettingObject();
                    settingObject.setSettingID(SYMBOL_DATABASE_LAST_UPDATE);
                    settingObject.setSettingValue("");
                    viewModel.updateSetting(settingObject);
                    // Here I need to continue to display the retryDownloadOverlay view.
                    rootView.findViewById(R.id.couldNotConnectTxt).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.retrySymbolBtn).setVisibility(View.VISIBLE);
                }
                // Hide the progress circle.  Either way, the retry page will be displayed again,
                // or the symbol database will be populated.
                rootView.findViewById(R.id.retryDownloadProgressBar).setVisibility(View.GONE);
            }
        }, new UpdateSymbolAndSettingAsyncTask.AsyncPreExecute() {
            @Override
            public void preExecute() {
                // Display the progress circle while trying to download the symbol database.
                rootView.findViewById(R.id.retryDownloadProgressBar).setVisibility(View.VISIBLE);

                // Hide the could not connect text and retry button while trying to download the symbols list again.
                rootView.findViewById(R.id.couldNotConnectTxt).setVisibility(View.GONE);
                rootView.findViewById(R.id.retrySymbolBtn).setVisibility(View.GONE);
            }
        }, this.getContext());
        updateSymbolAndSettingAsyncTask.execute();
    }

    /**
     * This class is an AsyncTask which works to download symbol data from NASDAQ.
     */
    private static class UpdateSymbolAndSettingAsyncTask extends AsyncTask {
        private Context mContext;
        private final UpdateSymbolAndSettingAsyncTask.AsyncResponse taskResponse;
        private final UpdateSymbolAndSettingAsyncTask.AsyncPreExecute taskPreExecute;

        public interface AsyncResponse {
            void processFinish() throws ExecutionException, InterruptedException;
        }

        public interface AsyncPreExecute {
            void preExecute();
        }

        // Constructor method.
        UpdateSymbolAndSettingAsyncTask(AsyncResponse taskComplete, AsyncPreExecute preExec, Context context) {
            this.taskResponse = taskComplete;
            this.taskPreExecute = preExec;
            mContext = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            DailyUpdateDataRetriever dailyUpdateDataRetriever = new DailyUpdateDataRetriever(mContext);
            try {
                dailyUpdateDataRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            // In onPostExecute we check if the listener is valid
            if(this.taskResponse != null) {
                // And if it is we call the callback function on it.
                try {
                    this.taskResponse.processFinish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            if (this.taskPreExecute != null) {
                this.taskPreExecute.preExecute();
            }
        }

    }
}