package collin.mayti.stockDetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.HashMap;

import collin.mayti.R;

import static android.content.ContentValues.TAG;

/**
 * Created by chpreston on 3/1/18.
 */

public class stockDetailsListView extends Fragment {

    private static HashMap<Integer, String> detailsListOrder = new HashMap<>();
    private HashMap<String, String> titleValueMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDetailsListOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_details_list, container, false);
        rootView.setTag(TAG);

        // TODO: I'll have to get the stock symbol from the search on the add page.  Then I will need to
        // get the stock data for that symbol from Fulldataretriever.  Once I have the data for the stock, I can
        // begin populating the various list rows.  Need to populate titleValueMap hashmap with the data.

        ListView listView = rootView.findViewById(R.id.stockDetailsList);
        listView.setAdapter(new stockDetailsListViewAdapter());

        return rootView;
    }


    class stockDetailsListViewAdapter extends BaseAdapter {
        stockDetailsListViewAdapter() {

        }

        @Override
        public int getCount() {
            return 3;
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
            LayoutInflater inflater = getLayoutInflater();
            View row;
            switch (i) {
                // TODO: Each of these cases should be a separate private method.
                case 0:
                    // Set the row view equal to the header details.
                    row = inflater.inflate(R.layout.fragment_expanded_stock_header, viewGroup, false);
                    TextView symbolTxtView = row.findViewById(R.id.fullDataSymbolTxtView);
                    TextView companyNameTxtView = row.findViewById(R.id.fullDataCompanyNameTxtView);
                    TextView priceTxtView = row.findViewById(R.id.fullDataPriceTxtView);

                    symbolTxtView.setText(titleValueMap.get(getContext().getString(R.string.symbol)));
                    companyNameTxtView.setText(titleValueMap.get(getContext().getString(R.string.company_name)));
                    priceTxtView.setText(titleValueMap.get(getContext().getString(R.string.latest_price)));
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
    }

    private void initializeDetailsListOrder() {
        // TODO: There will be more of these once I finish importing all of the strings into the
        // strings.xml file.  There is a TODO tag somewhere else.
        String[] fullDetailTitles = {
                getContext().getString(R.string.primary_exchange), getContext().getString(R.string.calculation_price), getContext().getString(R.string.open),
                getContext().getString(R.string.open_time), getContext().getString(R.string.close), getContext().getString(R.string.close_time),
                getContext().getString(R.string.high), getContext().getString(R.string.low),
                getContext().getString(R.string.latest_source), getContext().getString(R.string.latest_time), getContext().getString(R.string.latest_update),
                getContext().getString(R.string.latest_volume), getContext().getString(R.string.previous_close), getContext().getString(R.string.change),
                getContext().getString(R.string.market_cap), getContext().getString(R.string.pe_ratio), getContext().getString(R.string.week_52_high),
                getContext().getString(R.string.week_52_low), getContext().getString(R.string.ytd_change), getContext().getString(R.string.company_name_key)};
        for (int i = 0; i < fullDetailTitles.length; i++) {
            detailsListOrder.put(i, fullDetailTitles[i]);
        }
    }
}
