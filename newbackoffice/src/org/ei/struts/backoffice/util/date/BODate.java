/*
 * Created on Jan 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.util.date;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BODate {

    protected static Log log = LogFactory.getLog("BODate");

    private String m_strMonth = null;
    private String m_strDay = null;
    private String m_strYear = null;

    // DATE Helper Methods
    public String getDisplaydate() {

        if (getDate() != null)
            return Constants.formatDate(getDate().getTime());
        else
            return Constants.EMPTY_STRING;
    }

    public Date getDate() {

        Calendar calendar = Calendar.getInstance();
        boolean calset = false;

        try {
            calendar.set(
                Integer.parseInt(m_strYear),
                Integer.parseInt(m_strMonth),
                Integer.parseInt(m_strDay));
            calset = true;
        } catch (NumberFormatException nfe) {

        }

        return (calset == true) ? (calendar.getTime()) : null;

    }

    public void setDate(Calendar calendar) {

        m_strMonth = String.valueOf(calendar.get(Calendar.MONTH));
        m_strDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        m_strYear = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public void setDate(String date) {

        if (date == null)
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Constants.createDate(date));

        this.setDate(calendar);
    }

    public String getMonth() {
        return m_strMonth;
    }
    public void setMonth(String month) {
        m_strMonth = month;
    }

    public String getDay() {
        return m_strDay;
    }
    public void setDay(String day) {
        m_strDay = day;
    }

    public String getYear() {
        return m_strYear;
    }
    public void setYear(String year) {
        m_strYear = year;
    }

}
