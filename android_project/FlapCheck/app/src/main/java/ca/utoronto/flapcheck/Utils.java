/* See http://variableinc.com/terms-use-license for the full license governing this code. */
package ca.utoronto.flapcheck;

import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by coreymann on 7/2/13.
 */
public class Utils {

    private static final int COLOR_FLOAT_TO_INT_FACTOR = 255;

    public static int RGBToColor(final double pRed, final double pGreen, final double pBlue) {
        Log.d("Utils", "Before Scan= " + pRed + ", " + pGreen + " , " + pBlue);
        return Color.rgb(normalizeDouble(pRed), normalizeDouble(pGreen), normalizeDouble(pBlue));
    }

    private static int normalizeDouble(double f){
        double f2 = Math.max(0.0, Math.min(1.0, f));
        return (int) Math.floor(f2 == 1.0 ? COLOR_FLOAT_TO_INT_FACTOR : f2 * (COLOR_FLOAT_TO_INT_FACTOR + 1));
    }

    public static String prettyDate(long dtValue) {
        String formatStyle = "MMM dd, yyyy";
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(dtValue));
        dateTimeFormat.setTimeZone(cal.getTimeZone());
        return dateTimeFormat.format(cal.getTime());
    }

    public static String prettyTime(long dtValue) {
        String formatStyle = "HH:mm";
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(dtValue));
        dateTimeFormat.setTimeZone(cal.getTimeZone());
        return dateTimeFormat.format(cal.getTime());
    }

}
