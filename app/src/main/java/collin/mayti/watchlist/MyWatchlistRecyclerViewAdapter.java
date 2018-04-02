package collin.mayti.watchlist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.datacapture.GetJSONData;
import collin.mayti.stockDetails.LineChartData;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlistDB.Stock;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class MyWatchlistRecyclerViewAdapter extends RecyclerView.Adapter<MyWatchlistRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Stock> watchlistItems;

    private List<Entry> entryList = new ArrayList<>();

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView symbolTextView;
        private final TextView priceTextView;
        private final TextView volumeTextView;
        private final LineChart lineChart;

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
            lineChart = v.findViewById(R.id.watchlistChart);
            lineChart.setHardwareAccelerationEnabled(true);
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
        LineChart getLineChartView() {
            return lineChart;
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
            int decimalLoc = watchlistItems.get(position).getPrice().indexOf(".");
            // Display the price with two decimal places showing
            viewHolder.getPriceTextView().setText(watchlistItems.get(position).getPrice().substring(0, decimalLoc+3));
            viewHolder.getVolumeTextView().setText("Volume " + watchlistItems.get(position).getVolume());
            getChartData(watchlistItems.get(position).getSymbol(), viewHolder.getLineChartView());

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

    private boolean isJSONDataValid(String dataFromConnection, JSONArray jsonArray) {
        return dataFromConnection != null && !dataFromConnection.isEmpty() && jsonArray.length()!=0;
    }

    private List<LineChartData> getChartAsLineChartData(String dataRetrievedString) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Call to private method to get the JSON string of data.

        JSONArray jsonArray = new JSONArray(dataRetrievedString);
        List<LineChartData> lineChartDataList = new ArrayList<>();

        if (isJSONDataValid(dataRetrievedString, jsonArray)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObj = jsonArray.getJSONObject(i);
                // TODO: Need to adjust this for day, month, year.
                // TODO: For day, we need to get the data and fill in blank data for the rest of the day.
                // This will ensure that the graph doesn't look like it's a full size line, but only
                // displaying a line for part of the graph length.  Thus showing, there's more to the
                // day.
                String closeString = dataObj.getString("average");
                Double closeDouble = Double.parseDouble(closeString);
                LineChartData lineChartData = new LineChartData((double) i, closeDouble);
                lineChartDataList.add(lineChartData);
            }
        } else {
            // If we get here, then the market must be closed today.  We will go back to get the
            // chart data from most recent day the stock was updated.
        }

        return lineChartDataList;
    }

    /**
     * This method retrieves the chart data and updates a LineChart given a symbol,
     * length of time, and the LineChart handle.  This method will get the chart data from IEX for
     * a length of 1d.
     * @param symbol
     * @param lineChart
     * @throws MalformedURLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void getChartData (String symbol, final LineChart lineChart) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();

        // Check the current date.  We'll either get the ONE_DAY_CHART if the market is open today,
        // or we'll get the most recent date the market was opened.

        URL requestURL = urlUtil.buildURLForChartData(symbol, "ONE_DAY_CHART");
        final GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
                // At this point I also want to display the chart
                List<LineChartData> lineChartDataList = getChartAsLineChartData(output);
                if (!lineChartDataList.isEmpty()) {
                    // Don't process data if nothing retrieved.  But we still want to show the graph text.
                    entryList.clear();
                    for (LineChartData chartData : lineChartDataList) {
                        entryList.add(new Entry(chartData.getxCoordinate().floatValue(), chartData.getyCoordinate().floatValue()));
                    }
                    Collections.sort(entryList, new EntryXComparator());
                    LineDataSet dataSet = new LineDataSet(entryList, "");
                    dataSet.setDrawCircles(false);
                    LineData lineData = new LineData(dataSet);
                    lineData.setDrawValues(false);
                    lineChart.setData(lineData);
                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setEnabled(false);
                    YAxis yAxisLeft = lineChart.getAxisLeft();
                    YAxis yAxisRight = lineChart.getAxisRight();
                    yAxisLeft.setEnabled(false);
                    yAxisRight.setEnabled(false);
                    Legend legend = lineChart.getLegend();
                    legend.setEnabled(false);
                }
                lineChart.setDrawGridBackground(false);
                lineChart.setAutoScaleMinMaxEnabled(true);
                lineChart.setDescription(null);
                lineChart.invalidate();
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {
                // TODO: Display progress circle on top of chart while download is happening
            }
        });
        getJSONData.execute(requestURL);
    }
}
