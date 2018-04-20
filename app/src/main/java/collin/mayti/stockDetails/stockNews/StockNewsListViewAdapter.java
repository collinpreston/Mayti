package collin.mayti.stockDetails.stockNews;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.stockNewsDB.Article;
import collin.mayti.stockNewsDB.StockNewsViewModel;

public class StockNewsListViewAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mActivity;
    private String mSymbol;

    private List<Article> articlesList = new ArrayList<>();


    public StockNewsListViewAdapter(Context context, Activity activity, String symbol) throws ExecutionException, InterruptedException {
        this.mContext = context;
        this.mActivity = activity;
        this.mSymbol = symbol;

        articlesList = getArticlesForSymbol();
    }

    @Override
    public int getCount() {
        return articlesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.news_article_list_item, parent, false);

        TextView articleHeadline = row.findViewById(R.id.articleHeadlineTxt);
        articleHeadline.setText(articlesList.get(position).getHeadline());

        TextView articleSource = row.findViewById(R.id.articleSourceTxt);
        articleSource.setText(articlesList.get(position).getSource());

        TextView articleDate = row.findViewById(R.id.articleDateTxt);
        articleDate.setText(articlesList.get(position).getDateTime());

        return row;
    }

    private List<Article> getArticlesForSymbol() throws ExecutionException, InterruptedException {
        StockNewsViewModel stockNewsViewModel = ViewModelProviders.of((FragmentActivity) mActivity).get(StockNewsViewModel.class);
        List<Article> listAlerts = stockNewsViewModel.getAllArticles(mSymbol);

        return listAlerts;
    }
}
