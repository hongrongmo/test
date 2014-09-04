/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.text.NumberFormat;

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
public class MinutesSecondsDecorator extends ColumnDecorator {

	protected static Log log = LogFactory.getLog(MinutesSecondsDecorator.class);

	public String decorate(Object columnValue) {
		String result = Constants.EMPTY_STRING;

		if (columnValue == null) {
			return result;
		}
		//int timeInSeconds = 1000;
		//int hours, minutes, seconds;
		//hours = timeInSeconds / 3600;
		//timeInSeconds = timeInSeconds - (hours * 3600);
		//minutes = timeInSeconds / 60;
		//timeInSeconds = timeInSeconds - (minutes * 60);
		//seconds = timeInSeconds;

		try {
			result =
				NumberFormat.getInstance().format(
					Float.parseFloat(columnValue.toString()));
		} catch (NumberFormatException nfe) {
			result = Constants.EMPTY_STRING;
		}

		return result;
	}
}
