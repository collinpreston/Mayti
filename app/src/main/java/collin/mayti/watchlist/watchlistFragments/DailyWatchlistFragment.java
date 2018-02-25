package collin.mayti.watchlist.watchlistFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import collin.mayti.MainActivity;
import collin.mayti.R;
import collin.mayti.datacapture.DataRetriever;
import collin.mayti.watchlist.MyWatchlistRecyclerViewAdapter;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

/**
 * Created by chpreston on 2/17/18.
 */

public class DailyWatchlistFragment extends Fragment {
//    private static final String TAG = "RecyclerViewFragment";
//    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
//    private static final int SPAN_COUNT = 1;
//
//    private enum LayoutManagerType {
//        GRID_LAYOUT_MANAGER,
//        LINEAR_LAYOUT_MANAGER
//    }
//
//    protected DailyWatchlistFragment.LayoutManagerType mCurrentLayoutManagerType;
//
//    protected RecyclerView mRecyclerView;
//    protected MyWatchlistRecyclerViewAdapter mAdapter;
//    protected RecyclerView.LayoutManager mLayoutManager;
//    protected List<Stock> watchlistItems = new ArrayList<>();
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        AppDatabase db = MainActivity.db;
//
//        // TODO: Populate this list of all the watchlist items for the permanent watchlist
//        // Need to get a list from the View Model
//        // TODO: Get the total number of stocks for this watchlist, create a String array of that length,
//        // Add each items symbol to that string array.  This gets passed to the data retriever.
//        String[] myStocks = new String[1];
//        myStocks[0] = "DVAX";
//        //watchlistItems.add(item1);
//
//        // Initialize the viewModel for LiveData
//        WatchlistViewModel viewModel = ViewModelProviders.of(this).get(WatchlistViewModel.class);
//
//        // Start the stock updating service which grabs data from the external web
//        Intent dataRetrieverIntent = new Intent(getContext(), DataRetriever.class);
//        dataRetrieverIntent.putExtra("symbols", myStocks);
//        getActivity().startService(dataRetrieverIntent);
//
//
//        viewModel.getStockList().observe(DailyWatchlistFragment.this, new Observer<List<Stock>>() {
//            @Override
//            public void onChanged(@Nullable List<Stock> stocks) {
//                mAdapter.updateItems(stocks);
//            }
//        });
//
//
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_watchlist_permanent, container, false);
//        rootView.setTag(TAG);
//
//        mRecyclerView = rootView.findViewById(R.id.recyclerView);
//
//        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
//        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
//        // elements are laid out.
//        mLayoutManager = new LinearLayoutManager(getActivity());
//
//        mCurrentLayoutManagerType = DailyWatchlistFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//
//        if (savedInstanceState != null) {
//            // Restore saved layout manager type.
//            mCurrentLayoutManagerType = (DailyWatchlistFragment.LayoutManagerType) savedInstanceState
//                    .getSerializable(KEY_LAYOUT_MANAGER);
//        }
//        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
//
//        mAdapter = new MyWatchlistRecyclerViewAdapter(watchlistItems);
//        // Set CustomAdapter as the adapter for RecyclerView.
//        mRecyclerView.setAdapter(mAdapter);
//
//        return rootView;
//    }
//
//    /**
//     * Set RecyclerView's LayoutManager to the one given.
//     *
//     * @param layoutManagerType Type of layout manager to switch to.
//     */
//    public void setRecyclerViewLayoutManager(DailyWatchlistFragment.LayoutManagerType layoutManagerType) {
//        int scrollPosition = 0;
//
//        // If a layout manager has already been set, get current scroll position.
//        if (mRecyclerView.getLayoutManager() != null) {
//            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
//                    .findFirstCompletelyVisibleItemPosition();
//        }
//
//        switch (layoutManagerType) {
//            case GRID_LAYOUT_MANAGER:
//                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
//                mCurrentLayoutManagerType = DailyWatchlistFragment.LayoutManagerType.GRID_LAYOUT_MANAGER;
//                break;
//            case LINEAR_LAYOUT_MANAGER:
//                mLayoutManager = new LinearLayoutManager(getActivity());
//                mCurrentLayoutManagerType = DailyWatchlistFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//                break;
//            default:
//                mLayoutManager = new LinearLayoutManager(getActivity());
//                mCurrentLayoutManagerType = DailyWatchlistFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//        }
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.scrollToPosition(scrollPosition);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save currently selected layout manager.
//        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
//        super.onSaveInstanceState(savedInstanceState);
//    }
}
