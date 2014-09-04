/*
 * $Author:   johna  $
 * $Revision:   1.37  $
 * $Date:   Jun 09 2009 15:30:56  $
 *
*/
package org.ei.struts.emetrics.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.struts.util.LabelValueBean;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Reports;
import org.ei.struts.emetrics.customer.view.CustomerDatabase;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.exceptions.InvalidReportException;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.exceptions.InvalidLoginException;
import org.ei.struts.framework.service.IFrameworkService;

public class EmetricsServiceImpl implements IFrameworkService {

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(EmetricsServiceImpl.class);

    private static String[] charlist = new String[] {"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"};

    public static Collection getCharlist() {
        Collection allChars = new ArrayList();

        for(int i = 0; i < charlist.length; i++) {
            allChars.add(new LabelValueBean(charlist[i], charlist[i]));
        }
        return allChars ;
    }

    public static Collection getSalesRegions() {

        Collection allRegions = new ArrayList();
        allRegions.add(new LabelValueBean(Constants.EMPTY_STRING,Constants.EMPTY_STRING));
        allRegions.add(new LabelValueBean("South","1"));
        allRegions.add(new LabelValueBean("Atlantic","2"));
        allRegions.add(new LabelValueBean("Northeast","3"));
        allRegions.add(new LabelValueBean("Midwest","4"));
        allRegions.add(new LabelValueBean("West","5"));
        allRegions.add(new LabelValueBean("Southwest","6"));
        allRegions.add(new LabelValueBean("Emerging Markets","7"));
        allRegions.add(new LabelValueBean("Asia","8"));
        allRegions.add(new LabelValueBean("Europe 1","9"));
        allRegions.add(new LabelValueBean("Europe 2","10"));
        allRegions.add(new LabelValueBean("Europe 3","11"));
        allRegions.add(new LabelValueBean("China","12"));
        allRegions.add(new LabelValueBean("Japan","13"));
        allRegions.add(new LabelValueBean("Small Corporate","14"));
        allRegions.add(new LabelValueBean("Europe - Corp 1","15"));
        allRegions.add(new LabelValueBean("Europe - Corp 2","16"));
        allRegions.add(new LabelValueBean("Europe - Corp 3","17"));
        allRegions.add(new LabelValueBean("Europe - A&G 1","18"));
        allRegions.add(new LabelValueBean("Europe - A&G 2","19"));
        allRegions.add(new LabelValueBean("Europe - A&G 3","20"));
        allRegions.add(new LabelValueBean("NA - Corp 1","21"));
        allRegions.add(new LabelValueBean("NA - Corp 2","22"));
        allRegions.add(new LabelValueBean("NA - Corp 3","23"));
        allRegions.add(new LabelValueBean("NA - A&G 1","24"));
        allRegions.add(new LabelValueBean("NA - A&G 2","25"));
        allRegions.add(new LabelValueBean("NA - Life Science Corp 1","26"));
        allRegions.add(new LabelValueBean("NA - Life Science Corp 2","27"));

        return allRegions;
    }

    public static Collection getStatuses() {

        Collection allStatus = new ArrayList();

        allStatus.add(new LabelValueBean(Constants.EMPTY_STRING,Constants.EMPTY_STRING));
        allStatus.add(new LabelValueBean("CST - Paid customer", "CST"));
        allStatus.add(new LabelValueBean("CNL - Cancelled customer","CNL"));
        allStatus.add(new LabelValueBean("EXP - Expired customer","EXP"));
        allStatus.add(new LabelValueBean("CMP - Complimentary customer","CMP"));
        allStatus.add(new LabelValueBean("TRY - Trial Customer","TRY"));
        allStatus.add(new LabelValueBean("PMT - Permanent","PMT"));

        return allStatus;
    }

    public static Collection getProducts() {
        Collection allProds = new ArrayList();

        allProds.add(new LabelValueBean("EngVillage2", "4000"));
        allProds.add(new LabelValueBean("ChemVillage", "4400"));
        allProds.add(new LabelValueBean("PaperVillage2", "4100"));
        allProds.add(new LabelValueBean("EnCompassWEB", "4600"));
        allProds.add(new LabelValueBean("DDS", "9004"));

        return allProds;
    }

    public static Collection getYesNoOption() {

        Collection yesno = new ArrayList();

        yesno.add(new LabelValueBean(Constants.EMPTY_STRING,Constants.EMPTY_STRING));
        yesno.add(new LabelValueBean(Constants.YES, Constants.Y));
        yesno.add(new LabelValueBean(Constants.NO, Constants.N));

        return yesno;
     }

    // Implementation specific references
	private static JCS emetricsCache;
	private static JCS reportCache;

    /*
        A handle to the unique Singleton instance.
    */
    static private EmetricsServiceImpl _instance = null;

    /**
     * Create the service, which includes initializing the persistence
     * framework.
     */
    private EmetricsServiceImpl() throws DatastoreException {
    	init();
    }

    /**
     * Get an instance of a EmetricsServiceImpl.  At this point it could be a singleton, or pooled etc...
     * But you don't care, so always use this method to get an instance.
     *
     * @return a EmetricsServiceImpl
     */
    public static EmetricsServiceImpl getInstance() throws DatastoreException {
        if (null == _instance) {
            _instance = new EmetricsServiceImpl();
        }
        return _instance;
    }

    /**
     * Authenticate the user's credentials and either return a UserView for the
     * user or throw one of the security exceptions.
     */
    public UserView authenticate(String username, String password)
        throws InvalidLoginException {

        log.info("Auth??" + username + ":*******");

        UserView userView = null;

        Connection conn = null;
//        PreparedStatement pstmt = null;
		Statement st = null;
        ResultSet rs = null;
		String q = null;

        try
        {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
//            int idx = 1;

            conn = ds.getConnection();


//			pstmt = conn.prepareStatement("SELECT user_id USERNAME, PASSWORD, 'marketer' ROLE , 0 CONTACT_ID, 0 PARENT_ID, 'marketer' USER_TYPE, REGION_CODE FROM SALES_ORG WHERE user_id = ? AND PASSWORD = ? AND MANAGER= ?");

//			pstmt = conn.prepareStatement("SELECT user_id USERNAME, PASSWORD, 'marketer' ROLE , 0 CONTACT_ID, 0 PARENT_ID, 'marketer' USER_TYPE, REGION_CODE FROM U_OFFICE.SALES_ORG WHERE user_id = ? AND PASSWORD = ?");
//
//            pstmt.setString(idx++, username);
//            pstmt.setString(idx++, password);
            // Set to filter only those accounts in SALES_ORG that are Enabled (MANAGER == 1)
//            pstmt.setInt(idx++, 1);

//            rs = pstmt.executeQuery();

			st = conn.createStatement();
			q =  "SELECT user_id USERNAME, PASSWORD, 'marketer' ROLE , 0 CONTACT_ID, 0 PARENT_ID, 'marketer' USER_TYPE, REGION_CODE FROM SALES_ORG WHERE user_id = '"+username+"' AND PASSWORD = '"+password+"'";
			rs = st.executeQuery(q);

            if (rs.next()) {
                userView = new UserView();
                userView.setCustomerId(Integer.parseInt(rs.getString("CONTACT_ID")));

                userView.setName(username);
                userView.setRole(rs.getString("ROLE"));
                userView.setChannel(rs.getString("ROLE"));

            }
        } catch (SQLException e) {
            log.error("authenticate ", e);

        } catch (NamingException e) {
            log.error("authenticate ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
//                if (pstmt != null)
//                    pstmt.close();
				if (st != null)
					st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("authenticate ", e);
            }
        }

        if(userView == null){

            CustomerDatabase custdb = new CustomerDatabase();
            userView = custdb.authenticate(username, password);

            String customerid = null;

            if (userView != null) {
                customerid = String.valueOf(userView.getCustomerId());

                userView.setChannel(Constants.USER_EXTERNAL_CUSTOMER);
                userView.setRole(Constants.USER_EXTERNAL_CUSTOMER);

                Map customer = custdb.getCustomer(customerid);
                userView.setName((String) customer.get("name"));

                Collection consortiums = this.getConsortiums();
                if (consortiums.contains(customerid)) {
                    userView.setChannel(Constants.USER_EXTERNAL_CONSORTIUM);

                    userView.setConsortiumMembers(custdb.getConsortiumMembers(customerid));
                }
            } else {
                throw new InvalidLoginException();
            }
        }

        log.info(" AUTHENTICATE() UserView " + userView.toString());

        return userView;
    }

    /**
     * Change a users view into supplied custid view and either return a UserView for the
     * user or throw one of the security exceptions.
     */
    public UserView switchView(UserView fromuser, int tocustid) {

        String fromcustomerid = String.valueOf(fromuser.getCustomerId());
        String tocustomerid = String.valueOf(tocustid);

        if (log.isInfoEnabled()) {
            log.info("Switching FROM custid:" + fromcustomerid );
            log.info("Switching TO custid:" + tocustomerid );
        }

        // check to see if tocustomerid exists before attempting switch
        Map tocustomer = this.getCustomer(tocustomerid );
        if(tocustomer.isEmpty()) {
            return null;
        }

        Collection consortiums = this.getConsortiums();

        // customers cannot switch to become other customers
        // only a consortium can switch to become one of its members
        if(Constants.USER_EXTERNAL_CUSTOMER.equals(fromuser.getRole())) {
            if(!consortiums.contains(fromcustomerid)) {
                return null;
            }
        }

        // if we are switching from consortium (parent)
        // to memeber (child ) - double check the relationship
        if(consortiums.contains(fromcustomerid)) {
            // allow switching from parent to parent
            // for viewing 'parentonly' data
            if(!fromcustomerid.equals(tocustomerid))
            {
                if(!fromuser.hasMember(tocustomerid))
                {
                    return null;
                }
            }
        }

        UserView newView = new UserView();

        newView.setCustomerId(tocustid);
        newView.setChannel(Constants.USER_EXTERNAL_CUSTOMER);
        newView.setRole(Constants.USER_EXTERNAL_CUSTOMER);

        newView.setName((String) tocustomer.get("name"));

        if (consortiums.contains(tocustomerid)) {
            newView.setChannel(Constants.USER_EXTERNAL_CONSORTIUM);
            newView.setConsortiumMembers(this.getConsortiumMembers(tocustomerid));
        }

        return newView;
    }

    /**
     * Supply report.
     */
    public Collection getReportsByRole(String role) throws DatastoreException {

        Collection reps = new ArrayList();
        Collection reports = getReports();

        log.info("Get reports by role" + role);

        Iterator itrReports = reports.iterator();
        while(itrReports.hasNext())
        {
        	Report report = (Report) itrReports.next();
            log.info("Report roles: " + report.getRoles());

        	if(report.getRoles().indexOf(role) > -1)
        	{
                log.info("Added Report");

        		reps.add(report);
        	}
        }
        return reps;
    }

    public Report getReport(String reportid)
        throws DatastoreException, InvalidReportException {

        Report report = null;
        Collection reports = getReports();

        Iterator itrReports = reports.iterator();
        while(itrReports.hasNext())
        {
        	report = (Report) itrReports.next();
        	if(report.getReportId() == Integer.parseInt(reportid))
        	{
                log.info("returning Report: " + report.toString());

                break;
        	}
        }

        return report;
    }

    public void setReports(Collection reports)
    {
		try {
			log.info(" putting in cache ");
			reportCache.put(Constants.REPORTS_KEY, reports);
//			emetricsCache.put(Constants.REPORTS_KEY, "x");
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			log.error("Cache Exception",e);
		}

    }

    public Collection getReports()
    {
		log.info(" getting from cache ");
		return (Collection) reportCache.get(Constants.REPORTS_KEY);
    }

    // returns a collection of Maps of customer properties
    public Collection getCustomers(Map filter) {
        return (new CustomerDatabase()).getCustomers(filter);
    }

    // returns a collection of Maps of customer properties
    public Collection getConsortiumMembers(String custid) {
        return (new CustomerDatabase()).getConsortiumMembers(custid);
    }

    // returns a collection of Strings
    private Collection getConsortiums() {
        return (new CustomerDatabase()).getAllConsortiums();
    }

    // returns a Map of customer properties
    private Map getCustomer(String custid) {
        return (new CustomerDatabase()).getCustomer(custid);
    }

    public Collection getMonths() {

        Collection months = null;

		months = (Collection) emetricsCache.get(Constants.MONTHS);
		log.debug(" getting Months");

		if (months == null)
		{
			log.debug(" populating Months list ");

			months = new ArrayList();
			SimpleDateFormat formatterLabel = new SimpleDateFormat("MMM yyyy");
			SimpleDateFormat formatterValue = new SimpleDateFormat("yyyy-MM");

			Calendar calendarStart = Calendar.getInstance();
			Calendar calendarEnd = Calendar.getInstance();
			// we set the calendar day to 1 so month arithmetic
			// does not get screwed up near the end of a long month.
			// i.e. on Jan 31st + 1 month = Feb 31st which doesn't exist
			// so we would get ????
			// set at 1 - every month has a first!
			calendarEnd.set(Calendar.DAY_OF_MONTH, 1);

			// set ending month to two years back
			calendarEnd.add(Calendar.YEAR, -2);

			// set end month to first month of the year
			//calendarEnd.set(Calendar.DAY_OF_YEAR, 1);

			// insert the current month
			months.add(
				new LabelValueBean(
					formatterLabel.format(calendarStart.getTime()),
					formatterValue.format(calendarStart.getTime())));

			// loop backwards until we reach the end month
			while (calendarEnd.before(calendarStart)) {
				calendarStart.set(Calendar.DAY_OF_MONTH, 1);
				calendarStart.add(Calendar.MONTH, -1);
				months.add(
					new LabelValueBean(
						formatterLabel.format(calendarStart.getTime()),
						formatterValue.format(calendarStart.getTime())));
			}

			try {
				log.debug(" putting in cache ");
				emetricsCache.put(Constants.MONTHS, months);
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				log.error("Cache Exception",e);
			}

		}
		else
		{
			log.debug(" FROM Cache");
		}
        return months;
    }


    /**
     * Supply date of last update.
     */
    public Date getLastUpdate() throws DatastoreException {

        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }


    public void logout(UserView view) {
        // Do nothing with right now, but might want to log it for auditing reasons
    }

    public void destroy() {
        //db.close();
    }

    /**
     * Opens the database and prepares it for transactions
     */
    private void init() throws DatastoreException {

        //get database database
        try {

			emetricsCache = JCS.getInstance("emetricsCache");

			reportCache = JCS.getInstance("reportCache");

        } catch (CacheException cex) {
			log.error("Cache Exception",cex);
			throw DatastoreException.datastoreError(cex);
        } catch (Exception ex) {
			log.error("init() Exception",ex);
            throw DatastoreException.datastoreError(ex);
        }
    }

}