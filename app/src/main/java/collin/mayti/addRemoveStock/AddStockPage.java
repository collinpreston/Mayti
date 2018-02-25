package collin.mayti.addRemoveStock;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import collin.mayti.R;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.Stock;

import static android.content.ContentValues.TAG;

/**
 * Created by collinhpreston on 15/02/2018.
 */

public class AddStockPage extends Fragment{
    private static final String PERMANENT_WATCHLIST_NAME = "permanent_watchlist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Before adding,
        // Check to see if existing stocks are already in the watchlist
        // For this I can just use read stock by symbol method, if it crashes - catch and return.
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);
        rootView.setTag(TAG);

        // Initialize the viewModel for LiveData
        final WatchlistViewModel viewModel = ViewModelProviders.of(this).get(WatchlistViewModel.class);

        final Stock item1 = new Stock();
        item1.setWatchlist(PERMANENT_WATCHLIST_NAME);
        item1.setSymbol("DVAX");

        final Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewModel.addItem(item1);
            }
        });

        return rootView;
    }
}
