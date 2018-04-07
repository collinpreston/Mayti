package collin.mayti.stockDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;

import collin.mayti.R;

public class StockExpandedDetailsListViewAdapter extends BaseAdapter{

    @SuppressLint("UseSparseArrays")
    private static HashMap<Integer, String> detailsListOrder = new HashMap<>();
    private HashMap<String, String> titleValueMap = new HashMap<>();

    private Context myContext;

    public StockExpandedDetailsListViewAdapter(Context context, HashMap<String, String> valueMap) {
        this.myContext = context;
        this.titleValueMap = valueMap;
        initializeDetailsListOrder();
    }

    @Override
    public int getCount() {
        return getDetailTitlesArray().length;
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

    private String[] getDetailTitlesArray() {
        String[] fullDetailTitles = {
                myContext.getString(R.string.primary_exchange), myContext.getString(R.string.open),
                myContext.getString(R.string.open_time), myContext.getString(R.string.close), myContext.getString(R.string.close_time),
                myContext.getString(R.string.high), myContext.getString(R.string.low),
                myContext.getString(R.string.latest_source), myContext.getString(R.string.latest_time), myContext.getString(R.string.latest_update),
                myContext.getString(R.string.latest_volume), myContext.getString(R.string.previous_close), myContext.getString(R.string.change),
                myContext.getString(R.string.market_cap), myContext.getString(R.string.pe_ratio), myContext.getString(R.string.week_52_high),
                myContext.getString(R.string.week_52_low), myContext.getString(R.string.ytd_change)};
        return fullDetailTitles;
    }
}
