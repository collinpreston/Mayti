package collin.mayti.stockIndicators;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.CMOIndicator;
import org.ta4j.core.indicators.candles.BearishEngulfingIndicator;
import org.ta4j.core.indicators.candles.BearishHaramiIndicator;
import org.ta4j.core.indicators.candles.BullishEngulfingIndicator;
import org.ta4j.core.indicators.candles.BullishHaramiIndicator;
import org.ta4j.core.indicators.candles.DojiIndicator;
import org.ta4j.core.indicators.candles.ThreeBlackCrowsIndicator;
import org.ta4j.core.indicators.helpers.CloseLocationValueIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.datacapture.GetJSONData;
import collin.mayti.notifications.NotificationUtil;
import collin.mayti.notifications.NotificationsDatabase.Notification;
import collin.mayti.urlUtil.UrlUtil;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

public class IndicatorEngine {

    private static JSONArray jsonArrayChartData = new JSONArray();

    private static JSONObject jsonArrayForKeyStats = new JSONObject();

    private IndicatorProfileSensitivity profileSensitivity;

    private Context mContext;

    private List<Stock> currentListOfStocks = new ArrayList<>();

    private HashMap<String, TimeSeries> timeSeriesDayHashMap = new HashMap<>();

    private HashMap<String, TimeSeries> timeSeriesWeekHashMap = new HashMap<>();

    private static String FIVE_DAY_CHART = "FIVE_DAY_CHART";

    private static String ONE_DAY_CHART = "ONE_DAY_CHART";

    public IndicatorEngine(IndicatorProfileSensitivity sensitivity, Context context) {
        this.profileSensitivity = sensitivity;

        this.mContext = context;

        // Get all the current stocks and store them to a list.  This will be the basis of what we
        // need to check against our list of indicators.
        this.currentListOfStocks = getAllCurrentStocks();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Notification> performIndicatorChecks() throws InterruptedException, ExecutionException, MalformedURLException, JSONException, ParseException {
        List<Notification> indicatorNotificationList = new ArrayList<>();

        // Get the users indicator sensitivity and perform the checks associated with that level.
        switch(profileSensitivity) {
            case LOW:
                indicatorNotificationList = performLowLevelChecks(false);
                break;
            case MEDIUM:
                indicatorNotificationList = performMediumLevelChecks(false);
                break;
            case HIGH:
                indicatorNotificationList = performHighLevelChecks(false);
                break;
            case VERY_HIGH:
                indicatorNotificationList = performVeryHighLevelChecks();
                break;
        }

        return indicatorNotificationList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Notification> performLowLevelChecks(boolean isChildLevel) throws InterruptedException, ExecutionException, MalformedURLException, JSONException, ParseException {

        // The list that will be returned.
        List<Notification> notificationList = new ArrayList<>();


        for (Stock stock : currentListOfStocks) {
            String symbol = stock.getSymbol();

            // Build the week and day time series for the current symbol.
            timeSeriesDayHashMap.put(symbol, buildTimeSeriesForSymbol(symbol, ONE_DAY_CHART));
            timeSeriesWeekHashMap.put(symbol, buildTimeSeriesForSymbol(symbol, FIVE_DAY_CHART));

            // Always perform the checks below.  There is no need to change parameters for parent calls.

            // Perform all of the DMA checks and add the notifications to the returned list.
            notificationList.addAll(performDMAChecks(symbol));


            // TODO: Only perform at the end of the day or else this will trigger constantly
            if (isMarketClosed(timeSeriesWeekHashMap.get(symbol))) {
                // For this indicator in LOW, I want to check the last 4 days to see if it's either been
                // +1 (day high) for all three, or -1 for all three (day low)
                CloseLocationValueIndicator closeLocationValueIndicator = new CloseLocationValueIndicator(timeSeriesWeekHashMap.get(stock.getSymbol()));
                if (checkDaysCloseGreen(timeSeriesWeekHashMap.get(symbol), 5)) {

                }
                if (checkDaysCloseRed(timeSeriesWeekHashMap.get(symbol), 5)) {

                }

                for (int i = 0; i < 4; i++) {
                    System.out.println("CLV: " + closeLocationValueIndicator.getValue(timeSeriesWeekHashMap.get(symbol).getBeginIndex() + i).toString() + symbol);
                }
            }



            // Perform these checks if this method is not being called from a parent level.  Otherwise
            // parameters will need to be changed.
            if (!isChildLevel) {
                // A body factor of 0.03 will be used for LOW and MEDIUM, HIGH will use .1 and VERY_HIGH
                // will use .15.
                DojiIndicator dojiIndicator = new DojiIndicator(timeSeriesDayHashMap.get(symbol),
                        timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.03d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("Doji: " + dojiIndicator.getValue(i).toString() + symbol + i);
                }

                ThreeBlackCrowsIndicator threeBlackCrowsIndicator = new ThreeBlackCrowsIndicator(
                        timeSeriesDayHashMap.get(stock.getSymbol()), timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.03d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("TBC: " + threeBlackCrowsIndicator.getValue(i).toString() + symbol + i);
                }
            }

        }


        return notificationList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Notification> performMediumLevelChecks(boolean isChildLevel) throws InterruptedException, ExecutionException, MalformedURLException, ParseException, JSONException {
        // The list that will be returned.
        List<Notification> notificationList = new ArrayList<>();

        for (Stock stock : currentListOfStocks) {
            String symbol = stock.getSymbol();

            // Always perform the checks below.  There is no need to change parameters for parent calls.

            // Call performLowLevelChecks since it's a child of this method.
            performLowLevelChecks(true);

            BearishEngulfingIndicator bearishEngulfingIndicator = new BearishEngulfingIndicator(timeSeriesWeekHashMap.get(symbol));
            BearishHaramiIndicator bearishHaramiIndicator = new BearishHaramiIndicator(timeSeriesWeekHashMap.get(symbol));
            BullishEngulfingIndicator bullishEngulfingIndicator = new BullishEngulfingIndicator(timeSeriesWeekHashMap.get(symbol));
            BullishHaramiIndicator bullishHaramiIndicator = new BullishHaramiIndicator(timeSeriesWeekHashMap.get(symbol));

            ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(
                    timeSeriesWeekHashMap.get(stock.getSymbol()), timeSeriesWeekHashMap.get(stock.getSymbol()).getBarCount());

            for (int i = 0; i < timeSeriesWeekHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                System.out.println("bear: " + bearishEngulfingIndicator.getValue(i) + symbol + i);
                System.out.println("bear H: " + bearishHaramiIndicator.getValue(i) + symbol + i);

                System.out.println("bull: " + bullishEngulfingIndicator.getValue(i) + symbol + i);
                System.out.println("bull H: " + bullishHaramiIndicator.getValue(i) + symbol + i);

                // TODO: research this.  Might need to use this for only day chart.
                System.out.println("CMF: " + chaikinMoneyFlowIndicator.getValue(i) + symbol + i);
            }

            // TODO: Only trigger at the end of the day or else this will trigger constantly.
            if (isMarketClosed(timeSeriesWeekHashMap.get(symbol))) {
                // For this indicator in MEDIUM, I want to check the last 3 days to see if it's either been
                // +1 (day high) for all three, or -1 for all three (day low)
                CloseLocationValueIndicator closeLocationValueIndicator = new CloseLocationValueIndicator(timeSeriesWeekHashMap.get(stock.getSymbol()));
                if (checkDaysCloseGreen(timeSeriesWeekHashMap.get(symbol), 4)) {

                }
                if (checkDaysCloseRed(timeSeriesWeekHashMap.get(symbol), 4)) {

                }

                for (int i = 0; i < 3; i++) {
                    System.out.println("CLV: " + closeLocationValueIndicator.getValue(timeSeriesWeekHashMap.get(symbol).getBeginIndex() + i).toString() + symbol);
                }
            }

            // TODO: need to also check if it crosses upward.  This will be added as a check from MEDIUM - up.
            // This is only used on Monthly data.
//            CoppockCurveIndicator coppockCurveIndicator = new CoppockCurveIndicator(closePriceDay);
//            CrossedDownIndicatorRule crossedDownIndicatorRuleCoppock = new CrossedDownIndicatorRule(closePriceWeek, coppockCurveIndicator);
//            for (int i=0; i < closePriceDay.getTimeSeries().getBarCount(); i++) {
//                System.out.println("Coppock: " + crossedDownIndicatorRuleCoppock.isSatisfied(i) + symbol + i);
//            }

            // Perform these checks if this method is not being called from a parent level.  Otherwise
            // parameters will need to be changed.
            if (!isChildLevel) {
                // A body factor of 0.03 will be used for LOW and MEDIUM, HIGH will use .1 and VERY_HIGH
                // will use .15.
                DojiIndicator dojiIndicator = new DojiIndicator(timeSeriesDayHashMap.get(symbol),
                        timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.03d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("Doji: " + dojiIndicator.getValue(i).toString() + symbol + i);
                }

                ThreeBlackCrowsIndicator threeBlackCrowsIndicator = new ThreeBlackCrowsIndicator(
                        timeSeriesDayHashMap.get(stock.getSymbol()), timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.03d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("TBC: " + threeBlackCrowsIndicator.getValue(i).toString() + symbol + i);
                }
            }



        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Notification> performHighLevelChecks(boolean isChildLevel) throws InterruptedException, ExecutionException, MalformedURLException, ParseException, JSONException {
        // The list that will be returned.
        List<Notification> notificationList = new ArrayList<>();

        for (Stock stock : currentListOfStocks) {
            String symbol = stock.getSymbol();

            // Build the week and day close price indicators.
            ClosePriceIndicator closePriceWeek = new ClosePriceIndicator(timeSeriesWeekHashMap.get("DVAX"));
            ClosePriceIndicator closePriceDay = new ClosePriceIndicator(timeSeriesDayHashMap.get("DVAX"));

            // Call performLowLevelChecks and performMediumLevelChecks since they're a child of this method.
            performLowLevelChecks(true);
            performMediumLevelChecks(true);

            // TODO: Only trigger at the end of the day or else this will trigger constantly.
            if (isMarketClosed(timeSeriesWeekHashMap.get(symbol))) {
                // For this indicator in HIGH, I want to check the last 2 days to see if it's either been
                // +1 (day high) for all three, or -1 for all three (day low)
                CloseLocationValueIndicator closeLocationValueIndicator = new CloseLocationValueIndicator(timeSeriesWeekHashMap.get(stock.getSymbol()));
                if (checkDaysCloseGreen(timeSeriesWeekHashMap.get(symbol), 3)) {

                }
                if (checkDaysCloseRed(timeSeriesWeekHashMap.get(symbol), 3)) {

                }

                for (int i = 0; i < 2; i++) {
                    System.out.println("CLV: " + closeLocationValueIndicator.getValue(timeSeriesWeekHashMap.get(symbol).getBeginIndex() + i).toString() + symbol);
                }
            }

            // TODO: Check whether to use this for day or week. I think this might only be applicable
            // for week.

            // CMO will be used for HIGH and VERY_HIGH levels since it does not use smoothing which
            // allows it to hit extremes more often.  It is used to indicate oversold or overbought
            // more easily.
            CMOIndicator cmoIndicator = new CMOIndicator(closePriceDay,
                    timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount());
            for (int i=0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                System.out.println("CMO: " + cmoIndicator.getValue(i).toString() + symbol + i);
            }

            // Perform these checks if this method is not being called from a parent level.  Otherwise
            // parameters will need to be changed.
            if (!isChildLevel) {
                // A body factor of 0.03 will be used for LOW and MEDIUM, HIGH will use .1 and VERY_HIGH
                // will use .15.
                DojiIndicator dojiIndicator = new DojiIndicator(timeSeriesDayHashMap.get(symbol),
                        timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.1d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("Doji: " + dojiIndicator.getValue(i).toString() + symbol + i);
                }

                ThreeBlackCrowsIndicator threeBlackCrowsIndicator = new ThreeBlackCrowsIndicator(
                        timeSeriesDayHashMap.get(stock.getSymbol()), timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.1d);

                for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                    System.out.println("TBC: " + threeBlackCrowsIndicator.getValue(i).toString() + symbol + i);
                }
            }


        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Notification> performVeryHighLevelChecks() throws InterruptedException, ExecutionException, MalformedURLException, ParseException, JSONException {
        // The list that will be returned.
        List<Notification> notificationList = new ArrayList<>();

        for (Stock stock : currentListOfStocks) {
            String symbol = stock.getSymbol();


            // Call performLowLevelChecks, performMediumLevelChecks and performHighLevelChecks
            // since they're a child of this method.
            performLowLevelChecks(true);
            performMediumLevelChecks(true);
            performHighLevelChecks(true);

            // TODO: Only trigger at the end of the day or else this will trigger constantly.
            // For this indicator in VERY_HIGH, I want to check the last 2 days to see if it's either been
            // +1 (day high) for all three, or -1 for all three (day low)
            CloseLocationValueIndicator closeLocationValueIndicator = new CloseLocationValueIndicator(timeSeriesWeekHashMap.get(stock.getSymbol()));

            for (int i = 0; i < 2; i++) {
                System.out.println("CLV: " + closeLocationValueIndicator.getValue(timeSeriesWeekHashMap.get(symbol).getBeginIndex() + i).toString() + symbol);
            }

            // A body factor of 0.03 will be used for LOW and MEDIUM, HIGH will use .1 and VERY_HIGH
            // will use .15.
            DojiIndicator dojiIndicator = new DojiIndicator(timeSeriesDayHashMap.get(symbol),
                    timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.15d);

            for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                System.out.println("Doji: " + dojiIndicator.getValue(i).toString() + symbol + i);
            }

            ThreeBlackCrowsIndicator threeBlackCrowsIndicator = new ThreeBlackCrowsIndicator(
                    timeSeriesDayHashMap.get(stock.getSymbol()), timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(), 0.15d);

            for (int i = 0; i < timeSeriesDayHashMap.get(stock.getSymbol()).getBarCount(); i++) {
                System.out.println("TBC: " + threeBlackCrowsIndicator.getValue(i).toString() + symbol + i);
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isMarketClosed(TimeSeries timeSeries) {
        // check the time of the last bar to see if it's from 4PM, which would tell us the market is closed for the day.
        if (timeSeries.getBar(timeSeries.getEndIndex()).getEndTime().getHour() == 15) {
            return true;
        }
        return false;
    }

    private JSONObject getKeyStatsForSymbol(String symbol) throws MalformedURLException, ExecutionException, InterruptedException {
        UrlUtil urlUtil = new UrlUtil();
        URL requestURL = urlUtil.buildURLForKeyStats(symbol);
        final GetJSONData getJSONData = new GetJSONData(new GetJSONData.AsyncResponse() {

            @Override
            public void processFinish(String output) throws JSONException {
                jsonArrayForKeyStats = new JSONObject(output);
            }
        }, new GetJSONData.AsyncPreExecute() {
            @Override
            public void preExecute() {

            }
        });
        getJSONData.execute(requestURL).get();
        return jsonArrayForKeyStats;
    }
    private List<Notification> performDMAChecks(String symbol) throws InterruptedException, ExecutionException, MalformedURLException, JSONException {
        JSONObject jsonObject = getKeyStatsForSymbol(symbol);

        // Notification list to return.
        List<Notification> notificationList = new ArrayList<>();


        double shortDMA = 0, longDMA = 0;

        // TODO: Make a validateJSONArray method since this test is performed multiple times in this
        // class.
        // First check if the array is not empty.
        if (jsonObject.length() != 0) {
                shortDMA = Double.parseDouble(jsonObject.getString("day50MovingAvg"));
                longDMA = Double.parseDouble(jsonObject.getString("day50MovingAvg"));
        }

        if (checkShortDMABelowLongDMA(shortDMA, longDMA)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "50 Day Moving Average fell below the 200 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }
        if (checkShortDMAAboveLongDMA(shortDMA, longDMA)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "50 Day Moving Average rose above the 200 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }
        if (checkDropBelowShortDMA(shortDMA, symbol)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "The price has dropped below the 50 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }
        if (checkRiseAboveShortDMA(shortDMA, symbol)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "The price has risen above the 50 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }
        if (checkDropBelowLongDMA(longDMA, symbol)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "The price has dropped below the 200 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }
        if (checkRiseAboveLongDMA(longDMA, symbol)) {
            Notification notification;
            String title = symbol + " Indicator Triggered";
            String text = "The price has risen above the 200 Day Moving Average.";
            notification = NotificationUtil.createNotification(symbol, title, text);

            // Add the new notification to the returned list.
            notificationList.add(notification);
        }

        return notificationList;
    }
    private boolean checkShortDMABelowLongDMA(double shortDMA, double longDMA) {
        if (shortDMA < longDMA) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkShortDMAAboveLongDMA(double shortDMA, double longDMA) {
        if (shortDMA > longDMA) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkDropBelowShortDMA(double shortDMA, String symbol) {
        for (int i=0; i < timeSeriesDayHashMap.get(symbol).getBarCount(); i++) {
            if (timeSeriesDayHashMap.get(symbol).getBar(i).getMinPrice().doubleValue() < shortDMA) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRiseAboveShortDMA(double shortDMA, String symbol) {
        for (int i=0; i < timeSeriesDayHashMap.get(symbol).getBarCount(); i++) {
            if (timeSeriesDayHashMap.get(symbol).getBar(i).getMaxPrice().doubleValue() > shortDMA) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDropBelowLongDMA(double longDMA, String symbol) {
        for (int i=0; i < timeSeriesDayHashMap.get(symbol).getBarCount(); i++) {
            if (timeSeriesDayHashMap.get(symbol).getBar(i).getMinPrice().doubleValue() < longDMA) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRiseAboveLongDMA(double longDMA, String symbol) {
        for (int i=0; i < timeSeriesDayHashMap.get(symbol).getBarCount(); i++) {
            if (timeSeriesDayHashMap.get(symbol).getBar(i).getMaxPrice().doubleValue() > longDMA) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDaysCloseGreen(TimeSeries weekTimeSeries, int days) {
        boolean returnValue = false;
        // Make sure there are enough data points.  Or else return false.
        if (weekTimeSeries.getBarCount() > 3) {
            int lastIndex = weekTimeSeries.getEndIndex();
            for (int i=0; i <= lastIndex; i++) {
                if (weekTimeSeries.getBar(lastIndex - i).getClosePrice().isGreaterThan(weekTimeSeries.getBar(lastIndex - i).getOpenPrice())) {
                    returnValue = true;
                } else {
                    returnValue = false;
                    break;
                }
            }
        }
        return returnValue;
    }

    private boolean checkDaysCloseRed(TimeSeries weekTimeSeries, int days) {
        boolean returnValue = false;
        // Make sure there are enough data points.  Or else return false.
        if (weekTimeSeries.getBarCount() > days) {
            int lastIndex = weekTimeSeries.getEndIndex();
            for (int i=0; i <= lastIndex; i++) {
                if (weekTimeSeries.getBar(lastIndex - i).getClosePrice().isGreaterThan(weekTimeSeries.getBar(lastIndex - i).getOpenPrice())) {
                    returnValue = true;
                } else {
                    returnValue = false;
                    break;
                }
            }
        }
        return returnValue;
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
            JSONObject jsonBar = jsonArray.getJSONObject(i);
            long high = (long) (jsonBar.getDouble("high"));
            long open, low, close, volume;
            volume = (long) (jsonBar.getDouble("volume"));

            // Check if the high is -1.  If so, then we need to use the IEX only open, high, low, and close.
            if (high == -1 || high == 0) {
                // Now check if the IEX prices are valid and there, otherwise we won't get the data.
                high = (long) (jsonBar.getDouble("marketHigh"));
                if (!(high == -1 || high == 0)) {
                    // If the IEX market values are valid, we'll use these.
                    high = (long) (jsonBar.getDouble("marketHigh"));
                    open = (long) (jsonBar.getDouble("marketOpen"));
                    low = (long) (jsonBar.getDouble("marketLow"));
                    close = (long) (jsonBar.getDouble("marketClose"));

                    ZonedDateTime zonedDateTime = getZonedDateTimeLabelFromChartData(jsonBar.getString("label"));

                    // Add the bar to the series.
                    series.addBar(zonedDateTime, open, high, low, close, volume);
                }
            } else {
                open = (long) (jsonBar.getDouble("open"));
                low = (long) (jsonBar.getDouble("low"));
                close = (long) (jsonBar.getDouble("close"));

                ZonedDateTime zonedDateTime = getZonedDateTimeLabelFromChartData(jsonBar.getString("label"));

                // Add the bar to the series.
                series.addBar(zonedDateTime, open, high, low, close, volume);
            }
        }

        return series;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ZonedDateTime getZonedDateTimeLabelFromChartData(String label) throws ParseException {
        // Convert the time.
        Date endTime;
        // The date can be formatted in two ways (10 AM OR 10:00 AM).  So this has to try
        // each way.
        try {
            // First try using hh:mm aa.
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            endTime = dateFormat.parse(label);
        } catch (Exception e) {
            try {
                // If that doesn't work, then try hh a.
                DateFormat dateFormat = new SimpleDateFormat("hh a");
                endTime = dateFormat.parse(label);
            } catch (Exception i) {
                // If neither of the previous attempts work, then the data must be from the one week
                // chart.
                DateFormat dateFormat = new SimpleDateFormat("MMM dd");
                endTime = dateFormat.parse(label);
            }
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(endTime.toInstant(), ZoneId.of("UTC"));
        return zonedDateTime;
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
