package collin.mayti.stockDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.HashMap;

import collin.mayti.R;

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
                LineChart chart = (LineChart) row.findViewById(R.id.chart);

                // TODO: populate an array of dataObjects from the ChartDataRetriever class.
//                    LineChartData[] dataObjects = ...;
//
//                    List<Entry> entries = new ArrayList<Entry>();
//
//                    for (LineChartData data : dataObjects) {
//
//                        // turn your data into Entry objects
//                        entries.add(new Entry(data.getxCoordinate(), data.getyCoordinate()));
//                    }
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
                myContext.getString(R.string.primary_exchange), myContext.getString(R.string.calculation_price), myContext.getString(R.string.open),
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
}

