package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateRange implements IRange {

    protected Log log = LogFactory.getLog(DateRange.class);

    private Calendar end = null;
    private Calendar start = null;

    private Calendar getStart(){ return start; }
    private Calendar getEnd(){ return end; }

    protected void setStart(Calendar cal){ start = cal; }
    protected void setEnd(Calendar cal){ end = cal; }

    public DateRange(Map parameters) {
        // do nothing
    }
    public boolean isYearWithinRange(int year)
    {
		boolean result = false;

        if((getStart() != null) && (getEnd() != null)) {
            result = (getStart().get(Calendar.YEAR) == year) || (getEnd().get(Calendar.YEAR) == year);
        }
        return result;
    }

    public boolean isWithinRange(Object val)
    {
		boolean result = false;
		Calendar in = (Calendar) val;
        if((getStart() != null) && (getEnd() != null)) {
            result = (in.before(getStart()) || in.after(getEnd()));
        }
        return !result;
    }
}


