package org.ei.struts.emetrics.actions;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.businessobjects.reports.castor.JdoReport;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.FrameworkBaseForm;

/**
 * Form bean for the user report page.
 */
public class ReportForm extends FrameworkBaseForm {

    protected static Log log = LogFactory.getLog("ReportForm");

    private Collection reps = new ArrayList();
    private Collection members = new ArrayList();
    private Report aReport = new JdoReport();
    private int customerId = 0;
    private boolean consortium = false;

    private String action = Constants.EMPTY_STRING;
    private String searchform = Constants.EMPTY_STRING;

    private String repid = Constants.EMPTY_STRING;
    private String month = Constants.EMPTY_STRING;
    private String endmonth = Constants.EMPTY_STRING;
    private String singleday = Constants.EMPTY_STRING;
    private String startday = Constants.EMPTY_STRING;
    private String endday  = Constants.EMPTY_STRING;
    private String oneyear = Constants.EMPTY_STRING;
    private String quarter = Constants.EMPTY_STRING;
    private String member = Constants.ALL;

    private String searchtype  = Constants.RANGE;
    private String orderby = Constants.EMPTY_STRING;
    private String direction = Constants.DESCENDING;


    private class ReportCompare implements Comparator
    {
      public int compare(Object a, Object b)
      {
        return (new Integer(((Report) a).getDisplayId())).compareTo(new Integer(((Report) b).getDisplayId()));
      }
    }

    public void setAllReports(Collection allreps) {
        this.reps = allreps;
    }
    public Collection getAllReports() {
        return reps;
    }

    public void setMembers(Collection coll) {
        this.members = coll;
    }
    public Collection getAllMembers() {
        return members;
    }

    public Map getThisReportParams() {

        Map linkparams = new HashMap();
        linkparams.put("repid", this.repid);
        linkparams.put("month", this.month);

        return linkparams;
    }

    public Collection getAllReportLinks() {
        Collection replinks = new ArrayList();

        Collections.sort( (List) reps, new ReportCompare());

        Iterator itr = reps.iterator();
        while(itr.hasNext()) {
            Report rep = (Report) itr.next();
            boolean includerep = true;
            // This smells - we can definately move this somewhere else,
            // but for now - do not show DEPT report if the Dept file does not exist
            if(getCustomerId() != 0)
            {
                if((rep.getReportId() == 990) && !getIsConsortium()){
                    // Consortiums non parent-only  view
                    includerep = false;
                }
                if(rep.getReportId() == 720)
                {

                  String strPath = ((String)getServlet().getInitParameter("reportdata"))
                                      .concat(System.getProperty("file.separator"))
                                      .concat(String.valueOf(getCustomerId()))
                                      .concat(System.getProperty("file.separator"));

                  String filename = strPath.concat((String) rep.getFilename());
                  File inputFile = new File(filename);
                  includerep = inputFile.exists();
                  inputFile = null;
                }
            }

            Map props = new HashMap();
            props.put("repid", Integer.toString(rep.getReportId()));
            props.put("title", rep.getTitle());
            props.put("displayid", String.valueOf(rep.getDisplayId()));
            props.put("description", rep.getDescription());

            Map linkparams = new HashMap();
            linkparams.put("repid", Integer.toString(rep.getReportId()));
            linkparams.put("month", this.month);
            props.put("linkparams", linkparams);

            if(includerep)
            {
              replinks.add(props);
            }

        }
        return replinks;
    }

    public Collection getTwoYears() {
        Collection twoyears = new ArrayList();

        twoyears.add(new LabelValueBean(this.getThisYear(),this.getThisYear()));
        twoyears.add(new LabelValueBean(this.getThisYear(-1),this.getThisYear(-1)));
        twoyears.add(new LabelValueBean(this.getThisYear(-2),this.getThisYear(-2)));

        return twoyears;
    }

    private static Calendar Q1START = Calendar.getInstance();
    private static Calendar Q2START = (Calendar) Q1START.clone();
    private static Calendar Q3START = (Calendar) Q1START.clone();
    private static Calendar Q4START = (Calendar) Q1START.clone();
    static
    {
        Q1START.set(Calendar.MONTH,Calendar.JANUARY);
        Q1START.set(Calendar.DAY_OF_MONTH ,1);
        Q2START.set(Calendar.MONTH,Calendar.APRIL);
        Q1START.set(Calendar.DAY_OF_MONTH ,1);
        Q3START.set(Calendar.MONTH,Calendar.JULY);
        Q1START.set(Calendar.DAY_OF_MONTH ,1);
        Q4START.set(Calendar.MONTH,Calendar.OCTOBER);
        Q1START.set(Calendar.DAY_OF_MONTH ,1);
    }

    public Collection getAllQuarters() {
        Collection allqtrs = new ArrayList();

        allqtrs.add(new LabelValueBean("YTD","YTD"));

        Calendar today = Calendar.getInstance();
        if(today.after(Q4START))
            allqtrs.add(new LabelValueBean("Q4 " +this.getThisYear(), "Q4"));
        if(today.after(Q3START))
            allqtrs.add(new LabelValueBean("Q3 " +this.getThisYear(), "Q3"));
        if(today.after(Q2START))
            allqtrs.add(new LabelValueBean("Q2 " +this.getThisYear(), "Q2"));
        if(today.after(Q1START))
            allqtrs.add(new LabelValueBean("Q1 " +this.getThisYear(), "Q1"));

        allqtrs.add(new LabelValueBean("Q4 " +this.getThisYear(-1), "PQ4"));
        allqtrs.add(new LabelValueBean("Q3 " +this.getThisYear(-1), "PQ3"));
        allqtrs.add(new LabelValueBean("Q2 " +this.getThisYear(-1), "PQ2"));
        allqtrs.add(new LabelValueBean("Q1 " +this.getThisYear(-1), "PQ1"));

        if(getReport() != null) {
          if(getReport().getReportId() == 10030 || getReport().getReportId() == 30030 )
          {
            allqtrs.add(new LabelValueBean("Total " +this.getThisYear(-1), this.getThisYear(-1)));
            allqtrs.add(new LabelValueBean("Total " +this.getThisYear(-2), this.getThisYear(-2)));
          }
        }
        return allqtrs;
    }

    public Collection getAllMonths() {
      Collection allmonths = new ArrayList();
      EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) getFrameworkService();

      if(serviceImpl != null)
      {
        allmonths = (Collection) serviceImpl.getMonths();
      }

      return  allmonths;
    }


    public Collection getDays() {

        Collection days = new ArrayList();
//        days.add(new LabelValueBean(Constants.EMPTY_STRING, Constants.EMPTY_STRING));

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        for (int idx = 0; idx < 90; idx++) {
            days.add(
                new LabelValueBean(
                    formatter.format(calendar.getTime()),
                    formatter2.format(calendar.getTime())));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        return days;
    }


    private String getThisYear() {
        return getThisYear(0);
    }
    private String getThisYear(int diff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, diff);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");

        return formatter.format(calendar.getTime());

    }

    private String getThisMonth() {
        return getThisMonth(0);
    }
    private String getThisMonth(int diff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, diff);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");

        return formatter.format(calendar.getTime());
    }

    public Collection getAllProducts() {
        return EmetricsServiceImpl.getProducts();
    }

    /**
     * @return
     */
    public Report getReport() {
        return aReport;
    }

    /**
     * @param string
     */
    public void setReport(Report report) {
        aReport = report;
    }

    public String getHtmlformpage() {

        if(aReport.getReportId() == Constants.COUNTER2_DATABASE_REPORT1_ID) {
            return "/common/htmlforms/counterRange.jsp";
        }
        else if(aReport.getReportId() == 2700 && getIsConsortium()) {
            return "/common/htmlforms/memberRange.jsp";
        }
        else if(aReport.getReportId() == 2700) {
            return "/common/htmlforms/empty.jsp";
        }
        else if(aReport.getTitle().toLowerCase().indexOf("month") > -1) {
            return "/common/htmlforms/monthRange.jsp";
        }
        else if((aReport.getTitle().toLowerCase().indexOf("usage by") > -1)  || (aReport.getReportId() == 810)) {
            return "/common/htmlforms/quarters.jsp";
        }
        else if(aReport.getTitle().toLowerCase().indexOf("custom") > -1) {
            return "/common/htmlforms/customRange.jsp";
        }
        else if(aReport.getTitle().toLowerCase().indexOf("combination") > -1) {
            return "/common/htmlforms/empty.jsp";
        }
        else if(aReport.getTitle().toLowerCase().indexOf("day") > -1) {
            return "/common/htmlforms/singleMonth.jsp";
        }
        else {
            return "/common/htmlforms/singleMonth.jsp";
        }
    }

    public int getRangeOffset() {

        Calendar x = Calendar.getInstance();
        Calendar y = Calendar.getInstance();
        int offset = 0;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");

            Date startdate = formatter.parse(formatter.format(x.getTime()));
            x.setTime(startdate);
            x.set(Calendar.DAY_OF_MONTH, 1);

            Date enddate = formatter.parse(getMonth());
            y.setTime(enddate);
            y.set(Calendar.DAY_OF_MONTH, 1);

// for debugging
//            SimpleDateFormat printformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

            while (y.before(x)) {
                x.set(Calendar.DAY_OF_MONTH, 1);
                x.add(Calendar.MONTH, -1);
                offset++;
            }

        }
        catch(ParseException pe){

        }

        return offset;
    }

    public int getRange() {

        Calendar x = Calendar.getInstance();
        Calendar y = Calendar.getInstance();
        int dif = 1;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
            Date startdate = formatter.parse(getMonth());
            Date enddate = formatter.parse(getEndmonth());
            x.setTimeInMillis(startdate.getTime());
            y.setTimeInMillis(enddate.getTime());

            while (y.before(x)) {
                x.set(Calendar.DAY_OF_MONTH, 1);
                x.add(Calendar.MONTH, -1);
                dif++;
            }

        }
        catch(ParseException pe){

        }

        return dif;
    }

    public void setMonth(String string) {
        month = string;
    }

    public String getMonth() {
        if(this.month.equals(Constants.EMPTY_STRING)) {
            this.month = getThisMonth();
        }

        return this.month;
    }

    public void setEndmonth(String string) {
        endmonth = string;
    }

    public String getEndmonth() {
        if(this.endmonth.equals(Constants.EMPTY_STRING)) {
            this.endmonth = getThisMonth(-Constants.DEFAULT_RANGE_SIZE);
        }

        return this.endmonth;
    }


    /**
     * @return
     */
    public String getRepid() {
        return repid;
    }

    /**
     * @param string
     */
    public void setRepid(String arepid) {
        this.repid = arepid;
    }

    public Map getChartParams() {
        Map params = new HashMap();
        params.put("repid", new Integer(aReport.getReportId()));
        params.put("cid", new Integer(-1));
        params.put("month", this.month);

        return params;
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping,
                    HttpServletRequest request) {
        this.aReport = new JdoReport();
        this.reps = new ArrayList();
        this.members = new ArrayList();

        this.repid = Constants.EMPTY_STRING;
        this.customerId = 0;

        this.action = Constants.EMPTY_STRING;
        this.searchform = Constants.EMPTY_STRING;

        this.month = Constants.EMPTY_STRING;
        this.endmonth = Constants.EMPTY_STRING;
        this.singleday = Constants.EMPTY_STRING;
        this.startday = Constants.EMPTY_STRING;
        this.endday = Constants.EMPTY_STRING;
        this.oneyear = Constants.EMPTY_STRING;
        this.quarter = Constants.EMPTY_STRING;
        this.member = Constants.ALL;

        this.searchtype  = Constants.RANGE;
        this.orderby = Constants.EMPTY_STRING;
        this.direction = Constants.DESCENDING;

    }

    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
      public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {

          MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

          // Perform validator framework validations
          ActionErrors errors = super.validate(mapping, request);

          return errors;
      }

    /**
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * @param string
     */
    public void setAction(String string) {
        action = string;
    }

    /**
     * @return
     */
    public String getSearchtype() {
        return searchtype;
    }

    /**
     * @param string
     */
    public void setSearchtype(String string) {
        searchtype = string;
    }

    /**
     * @return
     */
    public String getSingleday() {
        return singleday;
    }

    /**
     * @param string
     */
    public void setSingleday(String string) {
        singleday = string;
    }


    /**
     * @return
     */
    public String getOneyear() {
        if(this.oneyear.equals(Constants.EMPTY_STRING)) {
            oneyear = this.getThisYear();
        }
        return oneyear;
    }

    /**
     * @param string
     */
    public void setOneyear(String string) {
        oneyear = string;
    }


    public String getQuarter() {
        if(this.quarter.equals(Constants.EMPTY_STRING)) {
            quarter  = "YTD";
        }
        return quarter ;
    }

    /**
     * @param string
     */
    public void setMember(String string) {
        member = string;
    }

    public String getMember() {
        return member;
    }

    /**
     * @param string
     */
    public void setQuarter(String string) {
        quarter = string;
    }

    /**
     * @return
     */
    public String getEndday() {
        if(this.endday.equals(Constants.EMPTY_STRING)) {
            endday = this.getThisMonth();
        }
        return endday;
    }

    /**
     * @return
     */
    public String getStartday() {
        if(this.startday.equals(Constants.EMPTY_STRING)) {
            startday = this.getThisMonth();
        }
        return startday;
    }

    /**
     * @param string
     */
    public void setEndday(String string) {
        endday = string;
    }

    /**
     * @param string
     */
    public void setStartday(String string) {
        startday = string;
    }

    /**
     * @return
     */
    public String getOrderby() {
        return orderby;
    }

    /**
     * @param string
     */
    public void setOrderby(String string) {
        orderby = string;
    }

    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }

    /**
     * @param string
     */
    public void setDirection(String string) {
        direction = string;
    }

    /**
     * @return
     */
    public String getSearchform() {
        return searchform;
    }

    /**
     * @param string
     */
    public void setSearchform(String string) {
        searchform = string;
    }

    public Collection getSortableColumns() {
        Collection sortablecolumns = new ArrayList();
        ResultsSet rs = this.aReport.getResultsSet();
        Iterator itrR =  rs.getResults().iterator();
        LabelValueBean lastcol = null;
        while(itrR.hasNext()) {
            Result result = (Result) itrR.next();

            if("true".equalsIgnoreCase(result.getSortable())) {
                lastcol = new LabelValueBean(result.getName(),result.getColumn());
                sortablecolumns.add(lastcol);
            }
        }
        if(!sortablecolumns.isEmpty())
        {
          if((lastcol != null) && (getOrderby() == null || getOrderby().equals(Constants.EMPTY_STRING)))
          {
            setOrderby(lastcol.getValue());
            // Default direction is Constants.DESCENDING
          }
        }
        return sortablecolumns;
    }

   /**
     */
    public int getCustomerId() {
      return customerId;
    }

    /**
     * @param string
     */
    public void setCustomerId(int string) {
        customerId = string;
    }

   /**
     */
    public boolean getIsConsortium() {
      return consortium;
    }

    /**
     * @param string
     */
    public void setIsConsortium(boolean bool) {
        consortium = bool;
    }

}