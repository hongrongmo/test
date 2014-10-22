package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class AllDataRange implements IRange {

    public AllDataRange() {}

    public AllDataRange(Map parameters) {
        // do nothing
    }

    public boolean isYearWithinRange(int year)
    {
        return true;
    }

    public boolean isWithinRange(Object val)
    {
        return true;
    }


}
