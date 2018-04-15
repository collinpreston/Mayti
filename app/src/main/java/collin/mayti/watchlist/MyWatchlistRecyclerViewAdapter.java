package collin.mayti.watchlist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionViewModel;
import collin.mayti.alerts.viewAlerts.ViewCurrentAlertsDialog;
import collin.mayti.datacapture.GetJSONData;
import collin.mayti.alerts.AlertTypeDialog;
import collin.mayti.stockDetails.LineChartData;
import collin.mayti.stockDetails.StockFullDetailsDialog;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlistDB.Stock;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class MyWatchlistRecyclerViewAdapter extends RecyclerView.Adapter<MyWatchlistRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Stock> watchlistItems;

    private List<Entry> entryList = new ArrayList<>();

    // Need to get the context in order to use application resources such as colors and strings.
    private Context mContext;

    private FragmentActivity mActivity;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView symbolTextView;
        private final TextView priceTextView;
        private final TextView volumeTextView;
        private final LineChart lineChart;
        private final TextView priceChangeTextView;
        private final TextView priceChangePercentageTextView;
        private final Button removeBtn;
        private final FrameLayout deleteOverlay;
        private final Button newAlertBtn;
        private final LinearLayout layoutStockItem;
        private final Button viewCurrentAlertsBtn;

        public ViewHolder(View v, final FragmentActivity activity) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // If the user long clicks a cardview, that cardview shows a dumpster in the top
                    // left and an overlay to dim the card.
                    removeBtn.setVisibility(View.VISIBLE);
                    deleteOverlay.setVisibility(View.VISIBLE);
                    deleteOverlay.setClickable(true);
                    removeBtn.setClickable(true);

                    return false;
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Here I will open up the details activity for the stock selected.
                    StockFullDetailsDialog dialogFrag = StockFullDetailsDialog.newInstance(symbolTextView.getText().toString());
                    dialogFrag.show(activity.getFragmentManager(), "");
                }
            });

            layoutStockItem = v.findViewById(R.id.linearLayout_Stock);
            symbolTextView = v.findViewById(R.id.symbol);
            priceTextView = v.findViewById(R.id.price);
            volumeTextView = v.findViewById(R.id.volume);
            lineChart = v.findViewById(R.id.watchlistChart);
            lineChart.setHardwareAccelerationEnabled(true);
            priceChangeTextView = v.findViewById(R.id.priceChange);
            priceChangePercentageTextView = v.findViewById(R.id.percentChange);
            deleteOverlay = v.findViewById(R.id.deleteOverlay);
            removeBtn = v.findViewById(R.id.deleteBtn);
            newAlertBtn = v.findViewById(R.id.newAlertBtn);
            viewCurrentAlertsBtn = v.findViewById(R.id.viewAlertsBtn);

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
        TextView getPriceChangeTextView() {
            return priceChangeTextView;
        }
        TextView getPriceChangePercentageTextView() {
            return priceChangePercentageTextView;
        }
        Button getRemoveBtn() {
            return removeBtn;
        }
        FrameLayout getDeleteOverlay() {
            return deleteOverlay;
        }
        Button getNewAlertBtn() {
            return newAlertBtn;
        }
        Button getViewCurrentAlertsBtn() {
            return viewCurrentAlertsBtn;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List</StockContent.StockItem> containing all of the database items
     */
    public MyWatchlistRecyclerViewAdapter(List<Stock> dataSet, Context context, FragmentActivity activity) {
        watchlistItems = dataSet;
        this.mContext = context;
        this.mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_watchlist_stock, viewGroup, false);

        return new ViewHolder(v, mActivity);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element.
        viewHolder.getSymbolTextView().setText(watchlistItems.get(position).getSymbol());
        try {
            // Display the price with two decimal places showing
            // Fix for IEX data since it will only show one decimal place if the hundreths position
            // is a zero.  Ex: 14.4 which we need to display as 14.40.
            String price = watchlistItems.get(position).getPrice();
            int priceDecimalLocation = price.indexOf('.');
            final int PRICE_DECIMAL_PLACES = 2;
            // Add 1 so that the decimal character does not get included in the comparison.
            if (price.length() - (priceDecimalLocation + 1) != PRICE_DECIMAL_PLACES) {
                // Add a zero in order to make for two decimals.
                price = price.concat("0");
            }

            viewHolder.getPriceTextView().setText(price);
            viewHolder.getVolumeTextView().setText("Volume " + watchlistItems.get(position).getVolume());
            viewHolder.getPriceChangeTextView().setText(watchlistItems.get(position).getChange());
            double percentageChange = Double.parseDouble(watchlistItems.get(position).getChangePercent()) * 100;
            viewHolder.getPriceChangePercentageTextView().setText("(" + new DecimalFormat("##.##").format(percentageChange) + "%)");

            // Set the text colors for the percentChange and priceChange textviews.
            if (percentageChange < 0) {
                viewHolder.getPriceChangePercentageTextView().setTextColor(mContext.getResources().getColor(R.color.darkRed));
                viewHolder.getPriceChangeTextView().setTextColor(mContext.getResources().getColor(R.color.darkRed));
            }
            else {
                viewHolder.getPriceChangePercentageTextView().setTextColor(mContext.getResources().getColor(R.color.green));
                viewHolder.getPriceChangeTextView().setTextColor(mContext.getResources().getColor(R.color.green));
            }

            viewHolder.getDeleteOverlay().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.getDeleteOverlay().getVisibility() == View.VISIBLE) {
                        viewHolder.getRemoveBtn().setVisibility(View.GONE);
                        viewHolder.getDeleteOverlay().setVisibility(View.GONE);
                        viewHolder.getDeleteOverlay().setClickable(false);
                        viewHolder.getRemoveBtn().setClickable(false);
                    }
                }
            });

            viewHolder.getRemoveBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WatchlistViewModel viewModel = ViewModelProviders.of(mActivity).get(WatchlistViewModel.class);
                    viewModel.deleteItem(watchlistItems.get(position));
                    watchlistItems.remove(position);
                    notifyDataSetChanged();
                }
            });

            viewHolder.newAlertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertTypeDialog dialogFrag = AlertTypeDialog.newInstance(watchlistItems.get(position).getSymbol());
                    dialogFrag.show(mActivity.getFragmentManager(), "");
                }
            });

            getChartData(watchlistItems.get(position).getSymbol(), viewHolder.getLineChartView());

            viewHolder.getViewCurrentAlertsBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewCurrentAlertsDialog dialogFrag = ViewCurrentAlertsDialog.newInstance(watchlistItems.get(position).getSymbol());
                    dialogFrag.show(mActivity.getFragmentManager(), "");
                }
            });

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
        List<Stock> oldWatchlistItems = new ArrayList<>(this.watchlistItems);
        this.watchlistItems = stockList;
        for (int i=0; i < stockList.size(); i++) {
            // Check whether the volume or price changed from the previous values.
            if (oldWatchlistItems.size() != 0) {
                if (oldWatchlistItems.size() == watchlistItems.size()) {
                    if (!oldWatchlistItems.get(i).getPrice().equals("") && !oldWatchlistItems.get(i).getVolume().equals("")) {
                        if (!(oldWatchlistItems.get(i).getPrice().equals(this.watchlistItems.get(i).getPrice())) &&
                                !(oldWatchlistItems.get(i).getVolume().equals(this.watchlistItems.get(i).getVolume()))) {
                            notifyItemChanged(i);
                        }
                    } else {
                        notifyItemChanged(i);
                    }
                }
            } else {
                notifyItemChanged(i);
            }
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

                String closeString = dataObj.getString("marketAverage");
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
                    // Sort through all of the chart values and remove any value of 0.  If left in,
                    // zero values will distort the chart.  TODO: This can become a method.
                    List<Entry> noZeroEntryList = new ArrayList<>();
                    for (int i=0; i < entryList.size(); i++) {
                        if (entryList.get(i).getY() != 0.0 && entryList.get(i).getY() != -1.0) {
                            noZeroEntryList.add(entryList.get(i));
                        }
                    }
                    entryList = noZeroEntryList;
                    // If the entryList is empty after removing all the zeros, we will display a straight
                    // line instead of showing an empty graph.  This assures the user that their connections
                    // are working, but the market data isn't available (usually pre market).
                    if (entryList.isEmpty()) {
                        entryList.add(new Entry(0.0f, 0.0f));
                    }
                    Collections.sort(entryList, new EntryXComparator());
                    LineDataSet dataSet = new LineDataSet(entryList, "");
                    dataSet.setColor(mContext.getResources().getColor(R.color.blue));
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
                lineChart.setClickable(false);
                lineChart.setDragEnabled(false);
                lineChart.setDragEnabled(false);
                lineChart.setPinchZoom(false);
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setHighlightPerTapEnabled(false);
                lineChart.setHighlightPerDragEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    lineChart.setDefaultFocusHighlightEnabled(false);
                }
                lineChart.setTouchEnabled(false);
                //lineChart.setKeepPositionOnRotation(true);
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

    private List<Alert> getAlertsForSymbol(String symbol) throws ExecutionException, InterruptedException {
        AlertSubscriptionViewModel alertViewModel = ViewModelProviders.of( mActivity).get(AlertSubscriptionViewModel.class);
        return alertViewModel.getAllAlertsForSymbol(symbol);
    }
}
