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
public class TimestampDecorator extends ColumnDecorator {

  protected static Log log = LogFactory.getLog(TimestampDecorator.class);

  public String decorate(Object columnValue) {

    if(columnValue == null) {
      return Constants.EMPTY_STRING;
    }

    if(((String) columnValue.toString()).equals(Constants.YTD))
    {
      return Constants.YTD_LABEL;
    }

    java.util.Date dateTime = Calendar.getInstance().getTime();


    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    try {
      dateTime = formatter.parse(columnValue.toString());
    }
    catch (ParseException e) {

      formatter = new SimpleDateFormat("yyyyMMdd");

      try {
        dateTime = formatter.parse(columnValue.toString());
      } catch (ParseException e1) {
        // TODO Auto-generated catch block
        // log.warn(" Decorate ",e);
      }
    }

    formatter = new SimpleDateFormat("yyyy/MMM/dd");

    return (formatter.format(dateTime));
  }
}
