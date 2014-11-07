package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class YearRange extends DateRange {

    public YearRange(Map parameters) throws ParseException {

		super(parameters);

		String oneyear = (String) parameters.get("oneyear");

        // month range - provided by two values
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		Date startdate = formatter.parse(oneyear);
		Calendar calstart = Calendar.getInstance();

		calstart.setTime(startdate);
		calstart.set(Calendar.DAY_OF_MONTH, 1);
		calstart.set(Calendar.MONTH, Calendar.JANUARY);

	    Calendar calend = Calendar.getInstance();

        if(calend.get(Calendar.YEAR) == (Integer.parseInt(oneyear)))
        {
            calend.setTime(new Date());
        }
        else
        {
    		calend.setTime(startdate);
    		calend.set(Calendar.MONTH, Calendar.DECEMBER);
    		calend.set(Calendar.DAY_OF_MONTH, calend.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        setStart(calstart);
        setEnd(calend);
    }

}
