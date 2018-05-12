package collin.mayti.util;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.stockDetails.LineChartData;

public class FormatChartData {
    private Context mContext;

    public FormatChartData(Context context) {
        mContext = context;
    }

    public LineData convertLineChartDataListToLineData(List<LineChartData> lineChartDataList) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        // Convert the JSON string to a list of LineChartData.

        // Add each item in the list of LineChartData to a list of Entry's which is used for displaying the chart.
        List<Entry> entryList = new ArrayList<>();
        for (LineChartData chartData : lineChartDataList) {
            entryList.add(new Entry(chartData.getxCoordinate().floatValue(), chartData.getyCoordinate().floatValue()));
        }
        // Sort through all of the chart values and remove any value of 0.  If left in,
        // zero values will distort the chart.
        List<Entry> noZeroEntryList = new ArrayList<>();
        for (int i = 0; i < entryList.size(); i++) {
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
        // Must sort the entries or else they can be out of order.
        Collections.sort(entryList, new EntryXComparator());
        LineDataSet dataSet = new LineDataSet(entryList, "");
        dataSet.setColor(mContext.getResources().getColor(R.color.blue));
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);

        return lineData;
    }

    public List<LineChartData> getLineChartDataFromJsonString(String dataRetrievedString) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
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

    private boolean isJSONDataValid(String dataFromConnection, JSONArray jsonArray) {
        return dataFromConnection != null && !dataFromConnection.isEmpty() && jsonArray.length()!=0;
    }
}
