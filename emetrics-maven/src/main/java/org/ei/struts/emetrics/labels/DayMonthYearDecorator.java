/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.display.ColumnDecorator;
import org.ei.struts.emetrics.Constants;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DayMonthYearDecorator extends ColumnDecorator {

    protected static Log log = LogFactory.getLog(DayMonthYearDecorator.class);

    public String decorate(Object columnValue) {

    	if(columnValue == null) {
			return Constants.EMPTY_STRING;
		}

        java.util.Date dateTime = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            dateTime = formatter.parse(columnValue.toString());
        } catch (ParseException e) {
        	dateTime = null;
        }

        // Try another format
    	//20050907
    	if(dateTime == null){
	    	formatter = new SimpleDateFormat("yyyyMMdd");
	        try {
	        	dateTime = formatter.parse(columnValue.toString());
	    	} catch (ParseException pe) {
	    		dateTime = null;
	    	}
    	}

    	// Try another format
    	//200509
    	if(dateTime == null){
	    	formatter = new SimpleDateFormat("yyyyMM");
	        try {
	        	dateTime = formatter.parse(columnValue.toString());
	    	} catch (ParseException pe) {
	    		dateTime = null;
	    	}
    	}

    	if(dateTime == null){
    		dateTime = Calendar.getInstance().getTime();
    	}

    	// return formatted date
    	formatter = new SimpleDateFormat("MM/dd/yyyy");
        return (formatter.format(dateTime));

     }
}
