package collin.mayti.watchlist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import collin.mayti.R;
import collin.mayti.watchlistDB.Stock;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class MyWatchlistRecyclerViewAdapter extends RecyclerView.Adapter<MyWatchlistRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Stock> watchlistItems;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView symbolTextView;
        private final TextView priceTextView;
        private final TextView volumeTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Here I will open up the details activity for the stock selected.
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            symbolTextView = v.findViewById(R.id.symbol);
            priceTextView = v.findViewById(R.id.price);
            volumeTextView = v.findViewById(R.id.volume);
        }

        TextView getVolumeTextView() {
            return volumeTextView;
        }
        TextView getPriceTextView() {
            return priceTextView;
        }
        TextView getSymbolTextView() {
            return symbolTextView;
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List</StockContent.StockItem> containing all of the database items
     */
    public MyWatchlistRecyclerViewAdapter(List<Stock> dataSet) {
        watchlistItems = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_watchlist_stock, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element.
        viewHolder.getSymbolTextView().setText(watchlistItems.get(position).getSymbol());
        try {
            // Try to get the price and volume if they've been populated.
            viewHolder.getPriceTextView().setText(watchlistItems.get(position).getPrice());
            viewHolder.getVolumeTextView().setText(watchlistItems.get(position).getVolume());
        } catch (Exception e) {
            // Otherwise the data hasn't been collected and we need to display hashmarks indicating
            // the data isn't here.
            viewHolder.getPriceTextView().setText("--");
            viewHolder.getVolumeTextView().setText("--");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return watchlistItems.size();
    }

    public void updateItems(List<Stock> stockList) {
        this.watchlistItems = stockList;
        for (int i=0; i < stockList.size(); i++) {
            notifyItemChanged(i);
        }
    }
}
