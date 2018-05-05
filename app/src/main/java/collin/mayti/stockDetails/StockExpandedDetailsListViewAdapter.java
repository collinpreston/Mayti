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
import collin.mayti.util.FormatValues;

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
        // Need to add 1 since we have a header that we have to account for.
        return detailsListOrder.size() + 1;
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
            case 0:
                // Set the row view equal to the header details.
                row = inflater.inflate(R.layout.fragment_expanded_stock_header, viewGroup, false);
                TextView symbolTxtView = row.findViewById(R.id.fullDataSymbolTxtView);
                TextView companyNameTxtView = row.findViewById(R.id.fullDataCompanyNameTxtView);
                TextView priceTxtView = row.findViewById(R.id.fullDataPriceTxtView);

                symbolTxtView.setText(titleValueMap.get(myContext.getString(R.string.symbol)));
                companyNameTxtView.setText(titleValueMap.get(myContext.getString(R.string.company_name)));

                String price = titleValueMap.get(myContext.getString(R.string.latest_price));
                priceTxtView.setText(FormatValues.formatPrice(price));

                break;

            default:
                // set the row view equal to the detail row.
                row = inflater.inflate(R.layout.fragment_single_stock_details_row, viewGroup, false);
                TextView titleTxtView = row.findViewById(R.id.stockDetailRowTitleTxt);
                TextView valueTxtView = row.findViewById(R.id.stockDetailRowValueTxt);
                // Subtract 1 since this will actually be position i, but in our detailsListOrder
                // it will be index i+1 since the first list element is the header (case 0).
                titleTxtView.setText(detailsListOrder.get(i-1));


                // Check if it's the high/low price, since that gets displayed specially.
                if (titleTxtView.getText().equals(myContext.getString(R.string.high_low))) {
                    // Get the high value.
                    String highValue = titleValueMap.get(myContext.getString(R.string.high));
                    // Get the low value.
                    String lowValue = titleValueMap.get(myContext.getString(R.string.low));
                    valueTxtView.setText(lowValue + " / " + highValue);
                } else {
                    // Check it it's the volume or market cap list item since we'll need to format these
                    // large numbers.
                    if (titleTxtView.getText().equals(myContext.getString(R.string.latest_volume)) ||
                            titleTxtView.getText().equals(myContext.getString(R.string.market_cap))) {
                        String formattedValue = FormatValues.format(Double.parseDouble(titleValueMap.get(detailsListOrder.get(i-1))));
                        valueTxtView.setText(formattedValue);
                    } else {
                        // Check if it's the change or YTD change, then the percentage needs to be
                        // formatted.
                        if (titleTxtView.getText().equals(myContext.getString(R.string.change)) ||
                                titleTxtView.getText().equals(myContext.getString(R.string.ytd_change))) {
                                double unformattedValue = Double.parseDouble(titleValueMap.get(detailsListOrder.get(i-1)));

                                // Multiply the current number by 100 to get the percentage.
                                String formattedValue = String.valueOf((unformattedValue * 100));
                                valueTxtView.setText(formattedValue + "%");
                        } else {
                            // Check if it's the latest update list item.
                            if (titleTxtView.getText().equals(myContext.getString(R.string.latest_update))) {
                                String unformatted = titleValueMap.get(detailsListOrder.get(i - 1));
                                String formattedTime = FormatValues.getLatestUpdateHoursAndMinutes(unformatted);
                                valueTxtView.setText(formattedTime);
                            } else {
                                // Handle any other type of list item. (default case).
                                valueTxtView.setText(titleValueMap.get(detailsListOrder.get(i - 1)));
                            }
                        }
                    }
                }
                break;
        }
        return row;

    }

    /**
     * This method initialized the global detailsListOrder list which defines the order in which
     * each details list item is shown.
     */
    private void initializeDetailsListOrder() {
        // TODO: There will be more of these once I finish importing all of the strings into the
        // strings.xml file.  There is a TODO tag somewhere else.
        String[] fullDetailTitles = {
                myContext.getString(R.string.high_low), myContext.getString(R.string.change),
                myContext.getString(R.string.latest_volume),
                myContext.getString(R.string.open),
                myContext.getString(R.string.close),
                myContext.getString(R.string.previous_close),
                myContext.getString(R.string.week_52_high),
                myContext.getString(R.string.week_52_low),
                myContext.getString(R.string.ytd_change),
                myContext.getString(R.string.market_cap),
                myContext.getString(R.string.pe_ratio),
                myContext.getString(R.string.primary_exchange),
                myContext.getString(R.string.latest_update)};


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
}
