package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SingleMonthRange extends DateRange {
    
    public SingleMonthRange(Map parameters) throws ParseException {

		super(parameters);

		String start = (String) parameters.get("startdate");

    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
    
    	Date startdate = formatter.parse(start);
    	Calendar calstart = Calendar.getInstance();
    	calstart.setTime(startdate);
    	calstart.set(Calendar.DAY_OF_MONTH, 1);
    
    	Date enddate = formatter.parse(start);
        Calendar calend = Calendar.getInstance();
    	calend.setTime(enddate);
    	calend.set(Calendar.DAY_OF_MONTH, calend.getActualMaximum(Calendar.DAY_OF_MONTH));
    
        setStart(calstart);
        setEnd(calend);
    }
    
}

