package collin.mayti.stockDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.datacapture.GetJSONData;
import collin.mayti.urlUtil.UrlUtil;

/**
 * Created by chpreston on 3/23/18.
 */

public class StockDetailsListViewAdapter extends BaseAdapter {

    @SuppressLint("UseSparseArrays")
    private static HashMap<Integer, String> detailsListOrder = new HashMap<>();
    private HashMap<String, String> titleValueMap = new HashMap<>();

    private Context myContext;

    public StockDetailsListViewAdapter(Context context, HashMap<String, String> valueMap) {
        this.myContext = context;
        this.titleValueMap = valueMap;
        initializeDetailsListOrder();
    }

    @Override
    public int getCount() {
        // TODO: Adjust this to only show the number of fields we're actually using. Since we use
        // some more than 1 the array's items in the header, we need to adjust this.  That will remove
        // the extra blank list items at the bottom of the list.
        return titleValueMap.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        switch (i) {
            // TODO: Each of these cases should be a separate private method.
            case 0:
                // Set the row view equal to the header details.
                row = inflater.inflate(R.layout.fragment_expanded_stock_header, viewGroup, false);
                TextView symbolTxtView = row.findViewById(R.id.fullDataSymbolTxtView);
                TextView companyNameTxtView = row.findViewById(R.id.fullDataCompanyNameTxtView);
                TextView priceTxtView = row.findViewById(R.id.fullDataPriceTxtView);

                symbolTxtView.setText(titleValueMap.get(myContext.getString(R.string.symbol)));
                companyNameTxtView.setText(titleValueMap.get(myContext.getString(R.string.company_name)));
                priceTxtView.setText(titleValueMap.get(myContext.getString(R.string.latest_price)));
                break;
            case 1:
                // Set the row view equal to the chart.
                row = inflater.inflate(R.layout.fragment_expanded_stock_chart, viewGroup, false);
                // Initialize the line chart.
                LineChart chart = row.findViewById(R.id.dialogChart);
                try {
                    // Populate the line chart with data for one month of close prices from IEX.
                    getChartData(titleValueMap.get(myContext.getString(R.string.symbol)).toString(), "ONE_MONTH_CHART", chart);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            default:
                // set the row view equal to the detail row.
                row = inflater.inflate(R.layout.fragment_single_stock_details_row, viewGroup, false);
                TextView titleTxtView = row.findViewById(R.id.stockDetailRowTitleTxt);
                TextView valueTxtView = row.findViewById(R.id.stockDetailRowValueTxt);
                titleTxtView.setText(detailsListOrder.get(i));
                valueTxtView.setText(titleValueMap.get(detailsListOrder.get(i)));
                break;
        }
        return row;

    }

    private void initializeDetailsListOrder() {
        // TODO: There will be more of these once I finish importing all of the strings into the
        // strings.xml file.  There is a TODO tag somewhere else.
        String[] fullDetailTitles = {
                myContext.getString(R.string.primary_exchange), myContext.getString(R.string.open),
                myContext.getString(R.string.open_time), myContext.getString(R.string.close), myContext.getString(R.string.close_time),
                myContext.getString(R.string.high), myContext.getString(R.string.low),
                myContext.getString(R.string.latest_source), myContext.getString(R.string.latest_time), myContext.getString(R.string.latest_update),
                myContext.getString(R.string.latest_volume), myContext.getString(R.string.previous_close), myContext.getString(R.string.change),
                myContext.getString(R.string.market_cap), myContext.getString(R.string.pe_ratio), myContext.getString(R.string.week_52_high),
                myContext.getString(R.string.week_52_low), myContext.getString(R.string.ytd_change)};
        for (int i = 0; i < fullDetailTitles.length; i++) {
            detailsListOrder.put(i, fullDetailTitles[i]);
        }
    }

    /**
     * This method retrieves the chart data and updates a LineChart given a symbol,
     * length of time, and the LineChart handle.  This method will get the chart data from IEX for
     * a length of time of 1d, 5d, 1m, 3m, 6m, 1y.
     * @param symbol
     * @param lengthOfTime
     * @param lineChart
     * @throws MalformedURLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void getChartData (String symbol, final String lengthOfTime, final LineChart lineChart) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();
        URL requestURL = urlUtil.buildURLForChartData(symbol, lengthOfTime);
        final GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {
            @Override
            public void processFinish(String output) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
                // At this point I also want to display the chart
                List<LineChartData> lineChartDataList = getChartAsLineChartData(output, lengthOfTime);
                List<Entry> entryList = new ArrayList<>();
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
                lineChart.setDrawGridBackground(false);
                lineChart.setAutoScaleMinMaxEnabled(true);
                Description chartDescription = new Description();
                chartDescription.setText("One Month Chart");
                chartDescription.setPosition(lineChart.getWidth() - 100, 50);
                lineChart.setDescription(chartDescription);
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

    private List<LineChartData> getChartAsLineChartData(String dataRetrievedString, String lengthOfTime) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Call to private method to get the JSON string of data.

        JSONArray jsonArray = new JSONArray(dataRetrievedString);
        List<LineChartData> lineChartDataList = new ArrayList<>();

        if (isJSONDataValid(dataRetrievedString)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObj = jsonArray.getJSONObject(i);

                // Check if the lengthOfTime specified is for a day, or more. If it's just a day, then
                // the JSON keys will be "average" for the chart data.  Otherwise the key is "close".
                if (lengthOfTime == "ONE_DAY_CHART") {
                    // TODO: For day, we need to get the data and fill in blank data for the rest of the day.
                    // This will ensure that the graph doesn't look like it's a full size line, but only
                    // displaying a line for part of the graph length.  Thus showing, there's more to the
                    // day.
                    String closeString = dataObj.getString("average");
                    Double closeDouble = Double.parseDouble(closeString);
                    LineChartData lineChartData = new LineChartData((double) i, closeDouble);
                    lineChartDataList.add(lineChartData);

                } else {
                    String closeString = dataObj.getString("close");
                    Double closeDouble = Double.parseDouble(closeString);
                    LineChartData lineChartData = new LineChartData((double) i, closeDouble);
                    lineChartDataList.add(lineChartData);
                }
            }
        }

        return lineChartDataList;
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
}

