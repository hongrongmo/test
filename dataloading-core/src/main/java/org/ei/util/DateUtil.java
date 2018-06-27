package org.ei.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Simple date utility to return various date constructs. Mainly used for XSLT files (1.0 xslt needs this, 2.0 has functions!)
 * 
 * @author harovetm
 * 
 */
public class DateUtil {

    /**
     * Return current year
     * 
     * @return
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Timestamp convertToTimeStamp(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        return new Timestamp(formatter.parse(dateString).getTime());
    }
}
