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
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.applicationSettingsDB.SettingViewModel;
import collin.mayti.stockSymbolDB.SymbolViewModel;
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
        View rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);
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
                // TODO: This is where I will also need to show the full data details list.

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

}
