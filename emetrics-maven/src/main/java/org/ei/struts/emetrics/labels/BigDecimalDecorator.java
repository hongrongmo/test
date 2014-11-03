/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.math.BigDecimal;
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
public class BigDecimalDecorator extends ColumnDecorator {

	protected static Log log = LogFactory.getLog(BigDecimalDecorator.class);

	public String decorate(Object columnValue) {
		if(columnValue == null) {
			return Constants.EMPTY_STRING;
		}

		String result = Constants.EMPTY_STRING;
		try {
		    result = NumberFormat.getInstance().format(BigDecimal.valueOf(Long.parseLong(columnValue.toString())));
		}
		catch(NumberFormatException e) {
		    result = columnValue.toString();
		}
		return result;
	 }
}
