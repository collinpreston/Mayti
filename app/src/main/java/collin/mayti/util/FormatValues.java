package collin.mayti.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;

import collin.mayti.R;

public class FormatValues {
    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000d, "k");
        suffixes.put(1_000_000d, "M");
        suffixes.put(1_000_000_000d, "B");
        suffixes.put(1_000_000_000_000d, "T");
        suffixes.put(1_000_000_000_000_000d, "P");
        suffixes.put(1_000_000_000_000_000_000d, "E");
    }

    public static String format(double value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Double.MIN_VALUE) return format(Double.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Double.toString(value); //deal with easy case

        Map.Entry<Double, String> e = suffixes.floorEntry(value);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        double truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        String returnValue = new DecimalFormat("##.##").format(hasDecimal ? (truncated / 10d) : (truncated / 10));
        return returnValue + suffix;
    }

    /**
     * // Display the price with two decimal places showing
     // Fix for IEX data since it will only show one decimal place if the hundreths position
     // is a zero.  Ex: 14.4 which we need to display as 14.40.

     * @param price The current unformatted price from IEX.
     * @return The formatted price.
     */
    public static String formatPrice(String price) {
        String formattedPrice = price;
        int priceDecimalLocation = price.indexOf('.');
        final int PRICE_DECIMAL_PLACES = 2;
        // Add 1 so that the decimal character does not get included in the comparison.
        if (formattedPrice.length() - (priceDecimalLocation + 1) != PRICE_DECIMAL_PLACES) {
            // Add a zero in order to make for two decimals.
            formattedPrice = price.concat("0");
        }
        return formattedPrice;
    }

    public static String getLatestUpdateHoursAndMinutes(String milliseconds) {
        // Refers to the update time of latestPrice in milliseconds since midnight Jan 1, 1970.
        Date latestUpdateDate = new Date(Long.parseLong(milliseconds));
        TimeZone timeZone = TimeZone.getTimeZone("EST");
        DateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        format.setTimeZone(timeZone);

        // Add an hour to the date object  since toString uses the default time zone and not the configured.
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        cal.setTime(latestUpdateDate);
        cal.add(Calendar.HOUR_OF_DAY, 1);

        return format.format(cal.getTime()) + " EST";

    }
}
