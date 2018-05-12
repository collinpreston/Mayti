package collin.mayti.stockDetails.stockNews;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import collin.mayti.R;

public class ViewStockNewsDialog extends DialogFragment {

    private View rootView;
    private String symbol;

    public static ViewStockNewsDialog newInstance(String symbol) {
        ViewStockNewsDialog f = new ViewStockNewsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("View Stock News");
        rootView = inflater.inflate(R.layout.dialog_view_stock_news, container, false);

        Button closeButton = rootView.findViewById(R.id.closeViewStockNewsDialogBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView stockNewListView = rootView.findViewById(R.id.stockNewsList);
        StockNewsListViewAdapter stockNewsListViewAdapter = null;
        try {
            stockNewsListViewAdapter = new StockNewsListViewAdapter(rootView.getContext(), getActivity(), symbol);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stockNewListView.setAdapter(stockNewsListViewAdapter);

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        MultiDex.install(getContext());
        symbol = getArguments().getString("symbol");


    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
