package collin.mayti.addRemoveStock;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import collin.mayti.R;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.Stock;

import static android.content.ContentValues.TAG;

/**
 * Created by collinhpreston on 15/02/2018.
 */

public class AddStockPage extends Fragment{
    private static final String PERMANENT_WATCHLIST_NAME = "permanent_watchlist";
    private WatchlistViewModel viewModel;

    private MaterialSheetFab materialSheetFab;
    private EditText searchSymbolEditTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);
        rootView.setTag(TAG);

        // Initialize the viewModel for LiveData
        viewModel = ViewModelProviders.of(this).get(WatchlistViewModel.class);


        // TODO: Setup search bar.
        searchSymbolEditTxt = rootView.findViewById(R.id.searchSymbolEditTxt);
        // With a successful search, the data for the stock will be displayed in the listview.
        // I also need to initialize and show the floating action button that will allow the user to add
        // the stock to a specific watchlist.
        searchSymbolEditTxt.addTextChangedListener(mTextEditorWatcher);

        final Stock item1 = new Stock();
        item1.setWatchlist(PERMANENT_WATCHLIST_NAME);
        item1.setSymbol("DVAX");

        final Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isStockAlreadyInDB(item1.getSymbol())) {
                    // TODO: Find out how to handle when a symbol belongs to two watchlists.
                }
                tryToAddStockToWatchlist(item1, PERMANENT_WATCHLIST_NAME);

            }
        });

        // Floating action button
        Fab fab = rootView.findViewById(R.id.fab);
        View sheetView = rootView.findViewById(R.id.fab_sheet);
        View overlay = rootView.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.cardViewBackground);
        int fabColor = getResources().getColor(R.color.lightBlue);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        return rootView;
    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // If the user had entered in 4 characters for a symbol.
            if (searchSymbolEditTxt.getText().toString().trim().length() == 4) {
                // Search for the symbol from the symbol master list.
                // TODO: update the master list daily.  Write this in Data Retriever.
                if (searchSymbolEditTxt.getText().toString().equals("DVAX")) {
                    Toast.makeText(getActivity(), "Yes", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };
    private boolean isStockAlreadyInDB(String symbol) {
        //TODO

        return false;
    }
    private boolean tryToAddStockToWatchlist(Stock stock, String watchlistID) {
        // TODO: Throw snackbar message to the user.  Find out how to handle SQL exception and check if it
        // is throwing a uniqueness integrity error.
        viewModel.addItem(stock);
        // Return true on successful insert.
        return false;
    }

}
