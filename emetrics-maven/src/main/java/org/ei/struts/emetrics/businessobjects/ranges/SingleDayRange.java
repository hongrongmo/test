package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SingleDayRange extends DateRange {
    
    public SingleDayRange(Map parameters) throws ParseException {

		super(parameters);

		String singleday = (String) parameters.get("startdate");

    	// single day 
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Date startdate = formatter.parse(singleday);
    	Calendar calstart = Calendar.getInstance();
    	Calendar calend = Calendar.getInstance();
    	
    	calstart.setTime(startdate);
    	calend.setTime(startdate);
    	
        setStart(calstart);
        setEnd(calend);
    }
    
}

