package collin.mayti.addRemoveStock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import collin.mayti.R;

import static android.content.ContentValues.TAG;

/**
 * Created by collinhpreston on 15/02/2018.
 */

public class AddStockPage extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Before adding,
        // Check to see if existing stocks are already in the watchlist
        // For this I can just use read stock by symbol method, if it crashes - catch and return.

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);
        rootView.setTag(TAG);

        return rootView;
    }
}
