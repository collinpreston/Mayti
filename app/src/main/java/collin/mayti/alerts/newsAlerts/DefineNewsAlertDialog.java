package collin.mayti.alerts.newsAlerts;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.alerts.alertSubscriptionDatabase.AlertSubscriptionViewModel;
import collin.mayti.stockNewsDB.Article;
import collin.mayti.stockNewsDB.StockNewsViewModel;

public class DefineNewsAlertDialog extends DialogFragment{

    private View rootView;
    private String symbol;

    private StockNewsViewModel stockNewsViewModel;
    private AlertSubscriptionViewModel alertViewModel;
    private CharSequence[] mtbButtonLbls;

    private boolean[] newsArticleAlertSet = {false, false, false, false};

    public static DefineNewsAlertDialog newInstance(String symbol) {
        DefineNewsAlertDialog f = new DefineNewsAlertDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_define_news_alert, container, false);

        MultiStateToggleButton newsAlertMtb = rootView.findViewById(R.id.newsAlertMtb);
        newsAlertMtb.enableMultipleChoice(true);

        int avgNumArticles = 0;
        try {
            avgNumArticles = calculateAvgArticlesPerWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String baseLabel = "1";
        String avgLabel = "3";
        String highLabel = "10";
        String veryHighLabel = "20";
        if (avgNumArticles != 0) {
            avgLabel = Integer.toString(avgNumArticles);
            highLabel = Integer.toString((int) Math.ceil((double) avgNumArticles * 1.5));
            veryHighLabel = Integer.toString((int) Math.ceil((double) avgNumArticles * 2));
        }
        mtbButtonLbls = new CharSequence[]{baseLabel, avgLabel, highLabel, veryHighLabel};

        newsAlertMtb.setElements(mtbButtonLbls);


        newsAlertMtb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (newsArticleAlertSet[position]) {
                    newsArticleAlertSet[position] = false;
                } else {
                    newsArticleAlertSet[position] = true;
                }
            }
        });

        TextView avgNewsAlertTxt = rootView.findViewById(R.id.avgPublicationPerWeekTxt);
        avgNewsAlertTxt.setText(Integer.toString(avgNumArticles));

        Activity activity = getActivity();
        alertViewModel = ViewModelProviders.of((FragmentActivity) activity).get(AlertSubscriptionViewModel.class);
        Button saveNewsAlertBtn = rootView.findViewById(R.id.submitNewsAlertBtn);
        saveNewsAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i < newsArticleAlertSet.length; i++) {
                    if (newsArticleAlertSet[i]) {
                        Alert newsAlert = new Alert();
                        newsAlert.setSymbol(symbol);
                        newsAlert.setAlertType("STOCK_ARTICLES_PUBLISHED");
                        newsAlert.setAlertTriggerValue(mtbButtonLbls[i].toString());
                        alertViewModel.addAlert(newsAlert);
                    }
                }
                // Close the dialog after the user selects Save and the alerts are created.
                dismiss();
            }
        });


        return rootView;
    }

    private int calculateAvgArticlesPerWeek() throws ParseException {
        // Set the multi toggle button lbls equal to values dependent on the average news per week.
        Activity activity = getActivity();
        stockNewsViewModel = ViewModelProviders.of((FragmentActivity) activity).get(StockNewsViewModel.class);
        List<String> articleDates = new ArrayList<>();
        try {
            List<Article> stockArticles = stockNewsViewModel.getAllArticles(symbol);
            for (int i=0; i < stockArticles.size(); i++) {
                articleDates.add(stockArticles.get(i).getDateTime());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Determine the cutoff date using today's date minus 30 days.
        Date today = new Date();
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTime(today);
        currentDate.add(Calendar.DAY_OF_MONTH, -30);
        Date cutOffDate = currentDate.getTime();

        // Used as a counter array for the number of articles published each week within the last 30
        // days.
        int articlesPerEachWeek[] = new int [4];
        Calendar previousWeek = Calendar.getInstance();
        for (String dateTimeString : articleDates) {
            String trimmedDate = getOnlyTheDateFromDateTime(dateTimeString);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date articleDate = dateFormat.parse(trimmedDate);

            // Check if the article was published after the cutoff date. (within 30 days).
            if (articleDate.after(cutOffDate)) {
                Calendar articleCal = Calendar.getInstance();
                articleCal.setTime(articleDate);

                // find out which week the article was published in and add a +1 to that index of the counter array.

                // If the first array index contains 0, then no articles have been added and we must add the first.
                if (articlesPerEachWeek[0] == 0) {

                    // This is the first article being counted.
                    articlesPerEachWeek[0] = 1;

                    // Record this date to use as comparison for the following articles.
                    previousWeek.setTime(articleDate);
                } else {
                    // We need to check if this is in the same week as the previous article.
                    // If so, then we add it to the same index of the array.  If not, we have to find
                    // the next array index who's value is 0.
                    if (articleCal.get(Calendar.WEEK_OF_YEAR) == previousWeek.get(Calendar.WEEK_OF_YEAR)) {
                        for (int i=0; i < articlesPerEachWeek.length; i++) {
                            // Loop through the weekly article counter until we find the index that hasn't been entered.
                            if (articlesPerEachWeek[i] == 0) {
                                // Add one the index that is before the empty index.  We do this since,
                                // this article was published the same week as the previous.  Thus, this
                                // index of the counter should already have a value.
                                articlesPerEachWeek[i-1] = articlesPerEachWeek[i-1] + 1;
                            } else {
                                // If this article falls into the fourth week of the array, then
                                // the array counter index might already have a value since the previous
                                // article could have also been in the fourth week.  If this happens, then
                                // we need to check that i == 3.
                                if (i == 3) {
                                    articlesPerEachWeek[i] = articlesPerEachWeek[i] + 1;
                                }
                            }
                        }
                    } else {
                        // Since this article we're testing falls into a different week than the previous
                        // article tested, we know that it must be the next week.  Thus, we need to add
                        // +1 to the next empty index of the counter.
                        for (int i=0; i < articlesPerEachWeek.length; i++) {
                            // Loop through the weekly article counter until we find the index that hasn't been entered.
                            if (articlesPerEachWeek[i] == 0) {
                                articlesPerEachWeek[i] = 1;
                            }
                        }
                    }
                }
            }
        }

        int numberOfWeeks = 0;
        int totalNumArticles = 0;
        for (int i : articlesPerEachWeek) {
            if (i != 0) {
                numberOfWeeks = numberOfWeeks + 1;
                totalNumArticles = totalNumArticles + i;
            }
        }
        int avgArticles = totalNumArticles/numberOfWeeks;
        return avgArticles;
    }

    private String getOnlyTheDateFromDateTime(String dateTime) {
        int indexOfDelimeter = dateTime.indexOf(' ');
        return dateTime.substring(0, indexOfDelimeter);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        MultiDex.install(getContext());
        symbol = getArguments().getString("symbol");

    }
}
