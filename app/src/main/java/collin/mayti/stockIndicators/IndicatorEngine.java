package collin.mayti.stockIndicators;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.datacapture.GetJSONData;
import collin.mayti.notifications.NotificationsDatabase.Notification;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

public class IndicatorEngine {

    private static JSONArray jsonArrayChartData = new JSONArray();

    private IndicatorProfileSensitivity profileSensitivity;

    private Context mContext;

    private List<Stock> currentListOfStocks = new ArrayList<>();

    private HashMap<String, TimeSeries> timeSeriesDayHashMap = new HashMap<>();

    private HashMap<String, TimeSeries> timeSeriesWeekHashMap = new HashMap<>();

    private static String ONE_WEEK_CHART = "ONE_WEEK_CHART";

    private static String ONE_DAY_CHART = "ONE_DAY_CHART";

    public IndicatorEngine(IndicatorProfileSensitivity sensitivity, Context context) {
        this.profileSensitivity = sensitivity;

        this.mContext = context;

        // Get all the current stocks and store them to a list.  This will be the basis of what we
        // need to check against our list of indicators.
        this.currentListOfStocks = getAllCurrentStocks();
    }

    public List<Notification> performIndicatorChecks() throws InterruptedException, ExecutionException, MalformedURLException, JSONException, ParseException {
        List<Notification> indicatorNotificationList = new ArrayList<>();

        // Get the users indicator sensitivity and perform the checks associated with that level.
        switch(profileSensitivity) {
            case LOW:
                indicatorNotificationList = performLowLevelChecks();
                break;
            case MEDIUM:
                indicatorNotificationList = performMediumLevelChecks();
                break;
            case HIGH:
                indicatorNotificationList = performHighLevelChecks();
                break;
            case VERY_HIGH:
                indicatorNotificationList = performVeryHighLevelChecks();
                break;
        }

        return indicatorNotificationList;
    }


    private List<Notification> performLowLevelChecks() throws InterruptedException, ExecutionException, MalformedURLException, JSONException, ParseException {
        for (int i=0; i < currentListOfStocks.size(); i++) {
            String symbol = currentListOfStocks.get(i).getSymbol();
            timeSeriesDayHashMap.put(symbol, buildTimeSeriesForSymbol(symbol, ONE_DAY_CHART));
            //timeSeriesWeekHashMap.put(symbol, buildTimeSeriesForSymbol(symbol, ONE_WEEK_CHART));
        }

        // TODO: Continue using the lists above to perform the indicator checks for Low sensitivity.
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeriesDayHashMap.get("DVAX"));
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, timeSeriesDayHashMap.get("DVAX").getBarCount());
        smaIndicator.getValue(timeSeriesDayHashMap.get("DVAX").getBarCount());

        EMAIndicator emaIndicator = new EMAIndicator(closePrice, timeSeriesDayHashMap.get("DVAX").getBarCount());
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);

        Rule entryRule = new OverIndicatorRule(shortEma, longEma);

        Rule exitRule = new UnderIndicatorRule(shortEma, longEma);

        TimeSeriesManager seriesManager = new TimeSeriesManager(timeSeriesDayHashMap.get("DVAX"));
        TradingRecord tradingRecord = seriesManager.run(new BaseStrategy(entryRule, exitRule));

        BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(closePrice);

        BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(bollingerBandsMiddleIndicator, closePrice);
        bollingerBandsLowerIndicator.getK();






        return null;
    }

    private List<Notification> performMediumLevelChecks() {
        return null;
    }

    private List<Notification> performHighLevelChecks() {
        return null;
    }

    private List<Notification> performVeryHighLevelChecks() {
        return null;
    }

    private List<Stock> getAllCurrentStocks() {
        // This method is called from the constructor and returns a list of all the current stocks
        // the user has added to their watchlists.
        List<Stock> stockList = new ArrayList<>();

        AppDatabase watchlistDatabase = AppDatabase.getDatabase(mContext);
        stockList = watchlistDatabase.watchlistDao().getAllStockItems();

        return stockList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private TimeSeries buildTimeSeriesForSymbol(String symbol, String lengthTime) throws JSONException, InterruptedException, ExecutionException, MalformedURLException, ParseException {

        TimeSeries series = new BaseTimeSeries(symbol + "_symbol");

        JSONArray jsonArray = getChartDataForSymbol(symbol, lengthTime);

        for (int i=0; i < jsonArray.length(); i++) {
            // Convert the time.
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            JSONObject jsonBar = jsonArray.getJSONObject(i);
            Date endTime = dateFormat.parse(jsonBar.getString("label"));
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(endTime.toInstant(), ZoneId.of("UTC"));


            long open = (long) (jsonBar.getDouble("open"));
            long high = (long) (jsonBar.getDouble("high"));
            long low = (long) (jsonBar.getDouble("low"));
            long close = (long) (jsonBar.getDouble("close"));
            long volume = (long) (jsonBar.getDouble("volume"));

            series.addBar(zonedDateTime, open, high, low, close, volume);
        }

        return series;
    }

    private JSONArray getChartDataForSymbol(String symbol, String lengthTime) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();
        URL requestURL = urlUtil.buildURLForChartData(symbol, lengthTime);
        final GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {

            @Override
            public void processFinish(String output) throws JSONException {
                jsonArrayChartData = new JSONArray(output);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {

            }
        });
        getJSONData.execute(requestURL).get();
        return jsonArrayChartData;
    }

}
