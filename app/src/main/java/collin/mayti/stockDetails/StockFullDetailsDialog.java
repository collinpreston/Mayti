package collin.mayti.stockDetails;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
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

public class StockFullDetailsDialog extends DialogFragment{

    private View rootView;
    private String symbol;

    private final String ONE_DAY_CHART = "ONE_DAY_CHART";
    private final String FIVE_DAY_CHART = "FIVE_DAY_CHART";
    private final String ONE_MONTH_CHART = "ONE_MONTH_CHART";
    private final String THREE_MONTH_CHART = "THREE_MONTH_CHART";
    private final String ONE_YEAR_CHART = "ONE_YEAR_CHART";
    private final String FIVE_YEAR_CHART = "FIVE_YEAR_CHART";

    public static StockFullDetailsDialog newInstance(String symbol) {
        StockFullDetailsDialog f = new StockFullDetailsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_expanded_stock_details, container, false);
        getDialog().setTitle(symbol);

        Button closeButton = rootView.findViewById(R.id.closeDetailsDialogBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final TextView chartLengthTxt = rootView.findViewById(R.id.chartLengthTxt);

        // Chart seek bar
        final SeekBar chartLenghtBar = rootView.findViewById(R.id.chartLengthBar);
        chartLenghtBar.setProgress(10);

        final LineChart lineChart = rootView.findViewById(R.id.dialogChart);
        try {
            getChartData(symbol, ONE_DAY_CHART, lineChart);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //int progress = chartLenghtBar.getProgress();
        chartLenghtBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 20) {
                    System.out.println("1d");
                    chartLengthTxt.setText("One Day");
                    try {
                        getChartData(symbol, ONE_DAY_CHART, lineChart);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (progress < 40) {
                        System.out.println("5d");
                        chartLengthTxt.setText("Five Day");
                        try {
                            getChartData(symbol, FIVE_DAY_CHART, lineChart);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (progress < 60) {
                            System.out.println("1m");
                            chartLengthTxt.setText("One Month");
                            try {
                                getChartData(symbol, ONE_MONTH_CHART, lineChart);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (progress < 80) {
                                System.out.println("3m");
                                chartLengthTxt.setText("Three Month");
                                try {
                                    getChartData(symbol, THREE_MONTH_CHART, lineChart);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (progress < 100) {
                                    System.out.println("1y");
                                    chartLengthTxt.setText("One Year");
                                    try {
                                        getChartData(symbol, ONE_YEAR_CHART, lineChart);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("5y");
                                    chartLengthTxt.setText("Five Year");
                                    try {
                                        getChartData(symbol, FIVE_YEAR_CHART, lineChart);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        MultiDex.install(getContext());
        symbol = getArguments().getString("symbol");

        try {
            getFullStockData(symbol);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
                // At this point I also want to display the listview.
                HashMap<String, String> fullDataMap = getFullDataAsHashMap(output);
                ListView stockDetailsExpandedList = rootView.findViewById(R.id.stockDetailsExpandedList);
                StockExpandedDetailsListViewAdapter stockExpandedDetailsListViewAdapter = new StockExpandedDetailsListViewAdapter(rootView.getContext(), fullDataMap);
                stockDetailsExpandedList.setAdapter(stockExpandedDetailsListViewAdapter);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {

            }
        });
        getJSONData.execute(requestURL);
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
            fullDataMap.put(rootView.getContext().getString(R.string.symbol), dataObj.getString("symbol"));
            fullDataMap.put(rootView.getContext().getString(R.string.company_name), dataObj.getString("companyName"));
            fullDataMap.put(rootView.getContext().getString(R.string.primary_exchange), dataObj.getString("primaryExchange"));
            fullDataMap.put(rootView.getContext().getString(R.string.calculation_price), dataObj.getString("calculationPrice"));
            fullDataMap.put(rootView.getContext().getString(R.string.open), dataObj.getString("open"));
            fullDataMap.put(rootView.getContext().getString(R.string.open_time), dataObj.getString("openTime"));
            fullDataMap.put(rootView.getContext().getString(R.string.close), dataObj.getString("close"));
            fullDataMap.put(rootView.getContext().getString(R.string.close_time), dataObj.getString("closeTime"));
            fullDataMap.put(rootView.getContext().getString(R.string.high), dataObj.getString("high"));
            fullDataMap.put(rootView.getContext().getString(R.string.low), dataObj.getString("low"));
            fullDataMap.put(rootView.getContext().getString(R.string.latest_price), dataObj.getString("latestPrice"));
            fullDataMap.put(rootView.getContext().getString(R.string.latest_source), dataObj.getString("latestSource"));
            fullDataMap.put(rootView.getContext().getString(R.string.latest_time), dataObj.getString("latestTime"));
            fullDataMap.put(rootView.getContext().getString(R.string.latest_update), dataObj.getString("latestUpdate"));
            fullDataMap.put(rootView.getContext().getString(R.string.latest_volume), dataObj.getString("latestVolume"));
            fullDataMap.put(rootView.getContext().getString(R.string.previous_close), dataObj.getString("previousClose"));
            fullDataMap.put(rootView.getContext().getString(R.string.change), dataObj.getString("changePercent"));
            fullDataMap.put(rootView.getContext().getString(R.string.market_cap), dataObj.getString("marketCap"));
            fullDataMap.put(rootView.getContext().getString(R.string.pe_ratio), dataObj.getString("peRatio"));
            fullDataMap.put(rootView.getContext().getString(R.string.week_52_high), dataObj.getString("week52High"));
            fullDataMap.put(rootView.getContext().getString(R.string.week_52_low), dataObj.getString("week52Low"));
            fullDataMap.put(rootView.getContext().getString(R.string.ytd_change), dataObj.getString("ytdChange"));
            return fullDataMap;
        }
        return null;
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
                // Sort through all of the chart values and remove any value of 0.  If left in,
                // zero values will distort the chart.  TODO: This can become a method.
                List<Entry> noZeroEntryList = new ArrayList<>();
                for (int i=0; i < entryList.size(); i++) {
                    if (entryList.get(i).getY() != 0.0) {
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
                lineChart.invalidate();

                rootView.findViewById(R.id.dialogChartProgressBar).setVisibility(View.GONE);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {
                rootView.findViewById(R.id.dialogChartProgressBar).setVisibility(View.VISIBLE);
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
}
