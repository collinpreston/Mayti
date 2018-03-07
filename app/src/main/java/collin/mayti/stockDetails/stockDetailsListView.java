package collin.mayti.stockDetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import collin.mayti.R;

import static android.content.ContentValues.TAG;

/**
 * Created by chpreston on 3/1/18.
 */

public class stockDetailsListView extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_details, container, false);
        rootView.setTag(TAG);

        // TODO: I'll have to get the stock symbol from the search on the add page.  Then I will need to
        // get the stock data for that symbol from dataretriever.  Once I have the data for the stock, I can
        // begin populating the various list rows.

        ListView listView = rootView.findViewById(R.id.stockDetailsList);
        listView.setAdapter(new stockDetailsListViewAdapter());

        return rootView;
    }


    class stockDetailsListViewAdapter extends BaseAdapter {

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
                    break;
                case 1:
                    // Set the row view equal to the chart.
                    row = inflater.inflate(R.layout.fragment_expanded_stock_chart, viewGroup, false);
                    break;
                default :
                    // set the row view equal to the detail row.
                    row = inflater.inflate(R.layout.fragment_single_stock_details_row, viewGroup, false);
                    break;
            }
            return row;

        }
    }
}
