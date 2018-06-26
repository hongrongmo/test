package org.ei.alert.email;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.alert.email.helper.SESEmail;
import org.ei.alert.email.helper.SESMessage;
import org.ei.config.ApplicationProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.domain.Citation;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DriverConfig;
import org.ei.domain.FastSearchControl;
import org.ei.domain.Page;
import org.ei.domain.Query;
import org.ei.domain.SearchResult;
import org.ei.domain.personalization.SavedSearches;
import org.ei.query.base.FastQueryWriter;
import org.ei.util.StringUtil;

/**
 * @author kamaramx
 *
 */
public class AlertEmail {
	
	protected static Log log = LogFactory.getLog(AlertEmail.class);
	private String environment = StringUtil.EMPTY_STRING;
	private boolean isThisTestEmailer = true;
    private String m_strYearWeek = StringUtil.EMPTY_STRING;
    private Properties eiProps;
    private ConnectionBroker m_broker = null;
    private Perl5Util perlUtil = new Perl5Util();
    private int m_intHitCount = -1;
    private int emailAlertSize;
    private String serverLocation;
    private String m_strPoolname = StringUtil.EMPTY_STRING;
    private String m_strBASE_URL = StringUtil.EMPTY_STRING;

    // California Digital Library Properties
    private List<String> m_cdlList = new ArrayList<String>();
    private int m_cdlEmailAlertSize = 1000;
    private List<String> m_skipEmptyList = new ArrayList<String>();
    
    
    /**
     * @throws IOException
     */
    public AlertEmail() throws IOException {
    	Properties properties = new Properties();
    	URL url = this.getClass().getResource("/emailalert.properties");
    	properties.load(url.openStream());
    	ApplicationProperties.load(properties);
    	eiProps = ApplicationProperties.getInstance();
    }

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		 try{
			 log.info("*********************************************************************************************************");
			 log.info("********************************Welcome to EV Alert Emails application tool******************************");
			 log.info("*********************************************************************************************************");
			 BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			 log.info("");
			 String cleanup = "";
			 log.info("Would you like to clean up the existing email transactions for a particular year week?(ex:'yes' or 'no') :");
			 cleanup = bufferRead.readLine();
			 while(cleanup == null || (!cleanup.equalsIgnoreCase("yes") && !cleanup.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 cleanup = bufferRead.readLine();
			 }
			 if(cleanup.equalsIgnoreCase("yes")){
				 cleanUpTheEmailTransction();
			 }else{
				 String sendemail = "";
				 log.info("Would you like to start the email transaction for a particular year week?(ex:'yes' or 'no') :");
				 sendemail = bufferRead.readLine();
				 while(sendemail == null || (!sendemail.equalsIgnoreCase("yes") && !sendemail.equalsIgnoreCase("no"))){
					 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
					 sendemail = bufferRead.readLine();
				 }
				 if(sendemail.equalsIgnoreCase("yes")){
					 initiateEmailTransaction();
				 }else{
					 log.info("Thank You!");
				 }
			 }
			 
		 }catch(Exception e){
			 e.printStackTrace();
			 log.error("Error Occurred!", e);
		 }
	 }
	
	/**
	 * 
	 */
	private void init(){
		try {
			configureDatabaseConnection();
			String sAlertSize = eiProps.getProperty("EMAILALERTSIZE");
            serverLocation = eiProps.getProperty("SERVERLOCATION");
            m_strBASE_URL = eiProps.getProperty("FastBaseUrl");
            m_strPoolname = DatabaseConfig.SESSION_POOL;
            m_broker = ConnectionBroker.getInstance();
            emailAlertSize = Integer.parseInt(sAlertSize);
            // California Digital Library Properties
            perlUtil.split(m_cdlList, "/,/", eiProps.getProperty("CDL_IDS"));
            m_cdlEmailAlertSize = Integer.parseInt(eiProps.getProperty("CDL_SIZE"));
            // CUST_IDs to skip sending zero results emails Property
            perlUtil.split(m_skipEmptyList, "/,/", eiProps.getProperty("SKIPZERO_IDS"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
	}
	
	/**
	 * 
	 */
	public static void cleanUpTheEmailTransction(){
		try{
			 BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			 
			 String yearweekvalue = "";
			 log.info("You must specify a Year and a Week number in the following format, YYYYWW. i.e. 200542 :");
			 yearweekvalue = bufferRead.readLine();
			 while(yearweekvalue == null || !validateYearWeek(yearweekvalue)){
				 log.info("Entered value is invalid, Year and a Week number in the following format, YYYYWW. i.e. 200542 :");
				 yearweekvalue = bufferRead.readLine();
			 }
			 String database = "";
			 log.info("Would you like to use the cert database?(ex:'yes' or 'no') :");
			 database = bufferRead.readLine();
			 while(database == null || (!database.equalsIgnoreCase("yes") && !database.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 database = bufferRead.readLine();
			 }
			 boolean certdatabaseused = true;	
			 if(database.equalsIgnoreCase("no"))certdatabaseused = false;
			 
			 log.info("-----------------------------------------------------------------------------------------------------------------");
			 log.info("Database mode  selected		: "+(certdatabaseused ?"CERT" : "PROD"));
			 log.info("Year Week value entered 		: "+yearweekvalue);
			 log.info("-----------------------------------------------------------------------------------------------------------------");
			 log.info("Are you sure to start the process? say 'yes' for start and 'no' for quit:");
			 String startorquit = "";
			 startorquit = bufferRead.readLine();
			 while(startorquit == null || (!startorquit.equalsIgnoreCase("yes") && !startorquit.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 startorquit = bufferRead.readLine();
			 }
			 if(startorquit.equalsIgnoreCase("yes")){
				 log.info("****************Starting to cleaning up the existing email transactions...****************");
				 AlertEmail alertEmail = new AlertEmail();	
				 alertEmail.setEnvironment("prod");
				 if(database.equalsIgnoreCase("yes"))alertEmail.setEnvironment("test");
				 alertEmail.init();
				 alertEmail.cleanEmailTransactiontable(yearweekvalue);
				 log.info("****************Completed the clean up process for the existing email transactions.****************");
			 }else{
				 log.info("Thank You!");
				 System.exit(0);
			 }
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error occured!", e);
		}
	}
	
	/**
	 * 
	 */
	public static void initiateEmailTransaction(){
		try{
			
			 String overirde = "";
			 log.info("Would you like to use override.properties?(ex:'yes' or 'no') :");
			 BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			 overirde = bufferRead.readLine();
			 while(overirde == null || (!overirde.equalsIgnoreCase("yes") && !overirde.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 overirde = bufferRead.readLine();
			 }
			 AlertEmail alertEmail = new AlertEmail();
			 Properties overrideProperties = new Properties();
			 if(overirde.equalsIgnoreCase("yes")){
				 	FileInputStream file;

				    //the base folder is ./, the root of the main.properties file  
				    String path = "./override.properties";

				    //load the file handle for main.properties
				    file = new FileInputStream(path);

				    //load all the properties from this file
				    overrideProperties.load(file);

				    //we have loaded the properties, so close the file handle
				    file.close();
			    	log.info("-----------------------------------------------------------------------------------------------------");
				    if(overrideProperties != null && overrideProperties.size()>0){
				    	log.info("*****************************Following overirdes will be applied for this process********************");
				    	for(Entry<Object, Object> e : overrideProperties.entrySet()) {
				    		log.info(e.getKey()+"="+e.getValue());
				    		alertEmail.eiProps.setProperty((String)e.getKey(), (String)e.getValue());
				        }
				    	
				    }else{
				    	log.info("*****************************No overirdes found in from the override.properties file********************");
				    }
				    log.info("-----------------------------------------------------------------------------------------------------");
			 }
			 String runmode = "";
			 log.info("Would you like to run this application in test mode?(ex:'yes' or 'no') :");
			 runmode = bufferRead.readLine();
			 while(runmode == null || (!runmode.equalsIgnoreCase("yes") && !runmode.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 runmode = bufferRead.readLine();
			 }
			 
			 
			 String yearweekvalue = "";
			 log.info("You must specify a Year and a Week number in the following format, YYYYWW. i.e. 200542 :");
			 yearweekvalue = bufferRead.readLine();
			 while(yearweekvalue == null || !validateYearWeek(yearweekvalue)){
				 log.info("Entered value is invalid, Year and a Week number in the following format, YYYYWW. i.e. 200542 :");
				 yearweekvalue = bufferRead.readLine();
			 }
			 alertEmail.setM_strYearWeek(yearweekvalue);
			 
			 
			 String database = "";
			 log.info("Would you like to use the cert database?(ex:'yes' or 'no') :");
			 database = bufferRead.readLine();
			 while(database == null || (!database.equalsIgnoreCase("yes") && !database.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 database = bufferRead.readLine();
			 }
			 boolean certdatabaseused = true;	
			 if(database.equalsIgnoreCase("no"))certdatabaseused = false;
			 
			 boolean testmodeenabled = true;
			 if(runmode.equalsIgnoreCase("no"))testmodeenabled = false;
			 
			 String filtertext = "";
			 String recepientText = "";
			 String ccedrecepientText = "";
			 if(testmodeenabled){
				 String filtermode = "";
				 log.info("Would you like to change the filter value '"+alertEmail.eiProps.getProperty("TESTEMAILFILTER")+"' in test mode?(ex:'yes' or 'no') :");
				 filtermode = bufferRead.readLine();
				 while(filtermode == null || (!filtermode.equalsIgnoreCase("yes") && !filtermode.equalsIgnoreCase("no"))){
					 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
					 filtermode = bufferRead.readLine();
				 }
				 boolean filterchangerequired = false;
				 if(filtermode.equalsIgnoreCase("yes"))filterchangerequired = true;
				 if(filterchangerequired){
					 log.info("Please enter the filter text, should be minimum 5 letters :");
					 filtertext = bufferRead.readLine();
					 while(filtertext == null || filtertext.trim().length()<5 ){
						 log.info("Entered value is invalid, filter text should be minimum 5 letters :");
						 filtertext = bufferRead.readLine();
					 }
				 }else{
					 filtertext = alertEmail.eiProps.getProperty("TESTEMAILFILTER");
				 }
				 
				 String changerecepient = "";
				 log.info("Would you like to change the recepient value '"+alertEmail.eiProps.getProperty("TESTRECEPIENTS")+"' in test mode?(ex:'yes' or 'no') :");
				 changerecepient = bufferRead.readLine();
				 while(changerecepient == null || (!changerecepient.equalsIgnoreCase("yes") && !changerecepient.equalsIgnoreCase("no"))){
					 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
					 changerecepient = bufferRead.readLine();
				 }
				 boolean recepientchangereq = false;
				 if(changerecepient.equalsIgnoreCase("yes"))recepientchangereq = true;
				 
				 if(recepientchangereq){
					 log.info("Please enter the valid recepient email id with minimum 5 letters :");
					 recepientText = bufferRead.readLine();
					 while(recepientText == null || recepientText.trim().length()<5 ){
						 log.info("Entered email is invalid, recepient email id should be minimum 5 letters :");
						 recepientText = bufferRead.readLine();
					 }
				 }else{
					 recepientText = alertEmail.eiProps.getProperty("TESTRECEPIENTS");
				 }
				 
				 String changeccrecepient = "";
				 log.info("Would you like to change the cced recepient value '"+alertEmail.eiProps.getProperty("TESTCCRECEPIENTS")+"' in test mode?(ex:'yes' or 'no') :");
				 changeccrecepient = bufferRead.readLine();
				 while(changeccrecepient == null || (!changeccrecepient.equalsIgnoreCase("yes") && !changeccrecepient.equalsIgnoreCase("no"))){
					 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
					 changeccrecepient = bufferRead.readLine();
				 }
				 boolean ccedrecepientchangereq = false;
				 if(changeccrecepient.equalsIgnoreCase("yes"))ccedrecepientchangereq = true;
				 
				 if(ccedrecepientchangereq){
					 log.info("Please enter the valid cced recepient email id with comma separated string(minimum 5 letters) :");
					 ccedrecepientText = bufferRead.readLine();
					 while(ccedrecepientText == null || ccedrecepientText.trim().length()<5 ){
						 log.info("Entered cced email is invalid, cced recepient email id should be minimum 5 letters :");
						 ccedrecepientText = bufferRead.readLine();
					 }
				 }else{
					 ccedrecepientText = alertEmail.eiProps.getProperty("TESTCCRECEPIENTS");
				 }
			 }
			 
			 log.info("-----------------------------------------------------------------------------------------------------------------");
			 log.info("Running  mode  selected		: "+(testmodeenabled ?"TEST" : "PROD"));
			 log.info("Database mode  selected		: "+(certdatabaseused ?"CERT" : "PROD"));
			 log.info("Year Week value entered 		: "+alertEmail.getM_strYearWeek());
			 if(certdatabaseused){
				 alertEmail.setEnvironment("test");
			 }else{
				 alertEmail.setEnvironment("prod");
			 }
			 
			 if(testmodeenabled){
				 log.info("Filter text used 			: "+filtertext);
				 log.info("Recepient email id used 		: "+recepientText);
				 log.info("CC Recepient email id used		: "+ccedrecepientText);
				 alertEmail.setThisTestEmailer(true);
				 alertEmail.eiProps.setProperty("TESTEMAILFILTER", filtertext);
				 alertEmail.eiProps.setProperty("TESTRECEPIENTS", recepientText);
				 alertEmail.eiProps.setProperty("TESTCCRECEPIENTS", ccedrecepientText);
			 }else{
				 alertEmail.setThisTestEmailer(false);
			 }
			 log.info("-----------------------------------------------------------------------------------------------------------------");
			 log.info("Are you sure to start the process? say 'yes' for start and 'no' for quit:");
			 String startorquit = "";
			 startorquit = bufferRead.readLine();
			 while(startorquit == null || (!startorquit.equalsIgnoreCase("yes") && !startorquit.equalsIgnoreCase("no"))){
				 log.info("Entered value is invalid, you must enter 'yes' or 'no' :");
				 startorquit = bufferRead.readLine();
			 }
			 if(startorquit.equalsIgnoreCase("yes")){
				 alertEmail.init();
		    	 alertEmail.sendMails();
			 }else{
				 log.info("Thank You!");
				 System.exit(0);
			 }
		 }catch(Exception e){
			e.printStackTrace();
			log.error("Error occured!", e);
		 }
	}
	
	/**
     * @ return java.util.Hashtable hashtable Steps it does a. gets distinct userids where he his vaing email alerts. b. At the same time getting the email
     * addess of the user. c. Adding userids as keys and email ids as values to yhe hashtable.
     */

    private Hashtable<String, String> getUserIds() throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        Hashtable<String, String> emailUserId = new Hashtable<String, String>();

        try {
            con = m_broker.getConnection(m_strPoolname);
            stmt = con.createStatement();
            log.info("******************Retriveing the user ids list...********************");
            rset = stmt.executeQuery("SELECT DISTINCT USER_PROFILE_CONTRACT.USER_PROFILE_ID, USER_PROFILE_CONTRACT.EMAIL, USER_PROFILE_CONTRACT.CUSTOMER_ID "
                + " FROM USER_PROFILE_CONTRACT, SEARCHES_SAVED WHERE " + " USER_PROFILE_CONTRACT.USER_PROFILE_ID=SEARCHES_SAVED.USER_ID "
                + " AND SEARCHES_SAVED.EMAIL_ALERT ='On'");

            String strUserProfileId, strUserProfileEmail, strUserProfileCustomer_id = StringUtil.EMPTY_STRING;
            String strFilter = eiProps.getProperty("TESTEMAILFILTER");

            while (rset.next()) {
                strUserProfileId = rset.getString(1); // USER_PROFILE_CONTRACT.USER_PROFILE_ID
                strUserProfileEmail = rset.getString(2); // USER_PROFILE_CONTRACT.EMAIL
                strUserProfileCustomer_id = rset.getString(3); // USER_PROFILE_CONTRACT.CUSTOMER_ID
                String strUserIdString = strUserProfileEmail.concat(",").concat(strUserProfileCustomer_id);
                if(this.isThisTestEmailer()) {
                	if (strUserProfileEmail.equals(strFilter) || (strUserProfileEmail.indexOf(strFilter) > -1)) {
                        emailUserId.put(strUserProfileId, strUserIdString);
                    } else if (strUserProfileCustomer_id.equals(strFilter) || (strUserProfileCustomer_id.indexOf(strFilter) > -1)) {
                        emailUserId.put(strUserProfileId, strUserIdString);
                    }

                } else {
                    emailUserId.put(strUserProfileId, strUserIdString);
                }
            }

        } catch (Exception e) {
            log.error("getUserIds Exception. ", e);
            throw e;
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e1) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqle) {
                }
            }
            if (con != null) {
                try {
                    m_broker.replaceConnection(con, m_strPoolname);
                } catch (ConnectionPoolException cpe) {
                }
            }
        }
        return emailUserId;
    }

    /**
     * @param java
     *            .lang.Object Query
     * @param int numberOfDocuments. @ return java.util.List list Steps it does a. gets Database objects to that query from databaseId in query object. b.
     *        Depending on database object creates instance of cearch control c. gets DocIdsList from searchControl.
     */

    private Page getDocIds(Query query, int alertSize) throws Exception {
        Page oPage = null;

        try {
            FastSearchControl mysearchcontrol = new FastSearchControl();
            FastSearchControl.BASE_URL = m_strBASE_URL;

            SearchResult sr = mysearchcontrol.openSearch(query, "NAN", alertSize, false);

            m_intHitCount = sr.getHitCount();
            log.info("Total Hit Count : " + m_intHitCount);

            if (sr.getHitCount() > 0) {

                String totalDocCount = Integer.toString(sr.getHitCount());
                oPage = sr.pageAt(1, Citation.CITATION_FORMAT);

                // setting the results count into hisquery object
                if (totalDocCount != null) {
                    query.setRecordCount(totalDocCount.trim());
                }
            }

        } catch (Exception e) {
            log.error("getDocIds - EXCEPTION ", e);
            throw e;
        } finally {

        }

        return oPage;
    }

    /**
     * @param yearweek
     * @throws Exception
     */
    private void cleanEmailTransactiontable(String yearweek) throws Exception {
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = m_broker.getConnection(m_strPoolname);

            pstmt = con.prepareStatement("DELETE FROM EMAIL_ALERT_TRANS WHERE YEARWEEK=?");
            pstmt.setString(1, yearweek);
            if (pstmt.executeUpdate() > 0) {
                log.info("Email transactions table cleaned up for the year week: "+yearweek);
            } else {
            	log.info("Email transactions table does not have any data to clean up for the year week: "+yearweek);
            }

        } catch (NoConnectionAvailableException ncae) {
            log.error("cleanEmailTransactiontable - NoConnectionAvailableException ", ncae);
            throw ncae;
        } catch (SQLException sqle) {
            log.error("cleanEmailTransactiontable - SQLException ", sqle);
            throw sqle;
        } catch (Exception e) {
            log.error("cleanEmailTransactiontable - Exception ", e);
            throw e;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
                if (con != null) {
                    m_broker.replaceConnection(con, m_strPoolname);
                }
            } catch (ConnectionPoolException cpe) {
                log.error("testEmailTransaction - ConnectionPoolException ", cpe);
            } catch (SQLException sqle) {
                log.error("testEmailTransaction - SQLException ", sqle);
            }

        }
    }
    
    /**
     * @return
     * @throws Exception
     */
    private int countTotalEmails() throws Exception {

        Connection con = null;
        ResultSet rset = null;
        PreparedStatement pstmt = null;
        int intCount = 0;
        int idx = 1;

        try {
            
            con = m_broker.getConnection(m_strPoolname);
            log.info("******************Calculating the total number of emails...********************");
            if (this.isThisTestEmailer()) {
            	String strFilter = eiProps.getProperty("TESTEMAILFILTER");
            	log.info("strFilter = [" + strFilter + "]");
                pstmt = con
                    .prepareStatement("select count(*) from SEARCHES_SAVED, USER_PROFILE_CONTRACT WHERE SEARCHES_SAVED.EMAIL_ALERT=? AND SEARCHES_SAVED.USER_ID = USER_PROFILE_CONTRACT.USER_PROFILE_ID AND INSTR(USER_PROFILE_CONTRACT.EMAIL,?) > 0");
                pstmt.setString(idx++, Query.ON);
                pstmt.setString(idx++, strFilter);
            } else {
                pstmt = con
                    .prepareStatement("select count(*) from SEARCHES_SAVED, USER_PROFILE_CONTRACT WHERE SEARCHES_SAVED.EMAIL_ALERT=? AND SEARCHES_SAVED.USER_ID = USER_PROFILE_CONTRACT.USER_PROFILE_ID");
                pstmt.setString(idx++, Query.ON);
            }
            rset = pstmt.executeQuery();

            if (rset.next()) {
                intCount = rset.getInt(1);
            }
        } catch (Exception e) {
            log.error("countTotalEmails - EXCEPTION ", e);
            throw e;
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                    rset = null;
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
                if (con != null) {
                    m_broker.replaceConnection(con, m_strPoolname);
                }
            } catch (ConnectionPoolException cpe) {
                log.error("countTotalEmails - ConnectionPoolException ", cpe);
            } catch (SQLException sqle) {
                log.error("countTotalEmails - SQLException ", sqle);
            }
        }

        return intCount;
    }
    
    /**
     * @param savedsearchid
     * @return
     */
    public String getCcList(String savedsearchid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String cclist = null;

        try {
            con = m_broker.getConnection(m_strPoolname);
            pstmt = con.prepareStatement("SELECT CC_LIST FROM SAVED_SEARCHES_CC where SEARCH_ID=?");
            pstmt.setString(1, savedsearchid);
            rset = pstmt.executeQuery();

            if (rset.next()) {
                cclist = rset.getString("CC_LIST");
            }
        } catch (Exception sqle) {
            log.error("Exception ", sqle);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
                }
            }

            if (con != null) {
                try {
                    m_broker.replaceConnection(con, m_strPoolname);
                } catch (ConnectionPoolException cpe) {
                }
            }
        }
        return cclist;
    }
    
    /**
     * @param strUserId
     * @param strUserEmail
     * @param strQueryId
     * @param sesMessage
     * @return
     * @throws Exception
     */
    private boolean emailTransaction(String strUserId, String strUserEmail, String strQueryId, SESMessage sesMessage) throws Exception {
        Connection con = null;
        ResultSet rset = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        boolean blnSuccess = false;

        try {
            con = m_broker.getConnection(m_strPoolname);

            pstmt = con.prepareStatement("SELECT DATETIME_SENT FROM EMAIL_ALERT_TRANS WHERE USER_ID=? AND USER_EMAIL=? AND YEARWEEK=? AND QUERY_ID=?");
            pstmt.setString(1, strUserId);
            pstmt.setString(2, strUserEmail);
            pstmt.setString(3, m_strYearWeek);
            pstmt.setString(4, strQueryId);

            rset = pstmt.executeQuery();
            if (rset.next()) {
                log.info("Email has already been sent for this Saved Search");
                log.info("To: " + strUserEmail);
                log.info("Query Id: " + strQueryId);
                log.info("On: " + rset.getString("DATETIME_SENT"));
                blnSuccess = false;

            } else {
                blnSuccess = true;
                if (sesMessage != null) {
                    
                	SESEmail.getInstance().send(sesMessage);
                	log.info("Email Sent for the user email: "+strUserEmail+", with subject as '"+sesMessage.getMessage().getSubject());

                    pstmt2 = con.prepareStatement("INSERT INTO EMAIL_ALERT_TRANS VALUES(?,?,?,?,SYSDATE)");
                    pstmt2.setString(1, strUserId);
                    pstmt2.setString(2, strUserEmail);
                    pstmt2.setString(3, m_strYearWeek);
                    pstmt2.setString(4, strQueryId);

                    if (pstmt2.executeUpdate() == 1) {
                        blnSuccess = true;
                    } else {

                    }
                }
            } // else
        } catch (Exception e) {
            log.error("emailTransaction EXCEPTION ", e);
            throw e;
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException sqle) {
                    log.error("EXCEPTION", sqle);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    log.error("EXCEPTION", sqle);
                }
            }
            if (pstmt2 != null) {
                try {
                    pstmt2.close();
                } catch (SQLException sqle) {
                    log.error("EXCEPTION", sqle);
                }
            }
            if (con != null) {
                try {
                    m_broker.replaceConnection(con, m_strPoolname);
                } catch (ConnectionPoolException cpe) {
                    log.error("EXCEPTION", cpe);
                }
            }
        }
        return blnSuccess;
    }
    
    /**
     * @param strUserId
     * @param strUserEmail
     * @param strQueryId
     * @param sesMessage
     * @return
     * @throws Exception
     */
    private boolean testEmailTransaction(String strUserId, String strUserEmail, String strQueryId, SESMessage sesMessage) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;
        boolean blnSuccess = false;

        try {
            blnSuccess = emailTransaction(strUserId, strUserEmail, strQueryId, sesMessage);

            con = m_broker.getConnection(m_strPoolname);

            pstmt = con.prepareStatement("DELETE FROM EMAIL_ALERT_TRANS WHERE USER_ID=? AND USER_EMAIL=? AND YEARWEEK=? AND QUERY_ID=?");
            pstmt.setString(1, strUserId);
            pstmt.setString(2, strUserEmail);
            pstmt.setString(3, m_strYearWeek);
            pstmt.setString(4, strQueryId);

            if (pstmt.executeUpdate() == 1) {
                log.info("Deleted test email from DB Table");
            } else {
                log.info("FAILED to delete test email from DB Table");
            }

        } catch (NoConnectionAvailableException ncae) {
            log.error("testEmailTransaction - NoConnectionAvailableException ", ncae);
            throw ncae;
        } catch (SQLException sqle) {
            log.error("testEmailTransaction - SQLException ", sqle);
            throw sqle;
        } catch (Exception e) {
            log.error("testEmailTransaction - Exception ", e);
            throw e;
        } finally {

            try {
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
                if (con != null) {
                    m_broker.replaceConnection(con, m_strPoolname);
                }
            } catch (ConnectionPoolException cpe) {
                log.error("testEmailTransaction - ConnectionPoolException ", cpe);
            } catch (SQLException sqle) {
                log.error("testEmailTransaction - SQLException ", sqle);
            }

        }
        return blnSuccess;

    }

    /**
     * 
     */
    private void sendMails() {
    	List<?> queryObjectList = null;
        List<?> userData = null;
        StringBuffer queryString = null;
        String userid = null;
        String emailAddress = null;
        String displayString = null;
        String customerid = null;
        EmailAlertResultsManager rm = null;
        Query queryObject = null;
        Page oPage = null;

        int intTotal = 0;
        int intSuccess = 0;
        List<String> skippedemails = new ArrayList<String>();
        List<String> erroremails = new ArrayList<String>();
        List<String> alreadysentemails = new ArrayList<String>();
        
        try {
        	 log.info("******************Starting the process...********************");
        	 DatabaseConfig databaseconfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
             String[] credentials = new String[] { "CPX", "INS", "NTI", "UPA", "EUP" };

             log.info("------------------------------------------------------------");

             intTotal = countTotalEmails();
             log.info("Count of Email Alerts to be sent = " + intTotal);
             log.info("");
             if (intTotal <= 0) {
            	 log.info("Process completed, sent zero emails.");
                 return;
             }
             Hashtable<String, String> userIdList = getUserIds();
             Iterator<String> itrUsers = userIdList.keySet().iterator();
             
             while (itrUsers.hasNext()) {
            	userid = (String) itrUsers.next();
                userData = new ArrayList<Object>();
                perlUtil.split(userData, "/,/", (String) userIdList.get(userid));
                emailAddress = (String) userData.get(0);
                customerid = (String) userData.get(1);
                log.info("******************Retriveing the alerts list for a user(userid="+userid+")...********************");
                queryObjectList = SavedSearches.getListUserAlerts(userid);
                log.info("User EMAIL,CUSTOMER_ID " + emailAddress + "," + customerid + " has " + queryObjectList.size() + " saved email alerts");
                if (m_cdlList.contains(customerid)) {
                    log.info("! CDL Alert Size will be " + m_cdlEmailAlertSize);
                }
                for (int querysize = 0; querysize < queryObjectList.size(); querysize++) {
                	
                	 try {
                		 queryObject = (Query) queryObjectList.get(querysize);
                         queryObject.setRecordCount("0");
                         if ((queryObject.getAutoStemming()).equals("on")) {
                         }

                         queryObject.setDatabaseConfig(databaseconfig);
                         queryObject.setCredentials(credentials);

                         // pre-test with null message
                         boolean blnAlreadySent = !emailTransaction(userid, emailAddress, queryObject.getID(), null);
                         if (blnAlreadySent == true) {
                        	 alreadysentemails.add("USERID="+userid+"/CUSTOMERID="+customerid+"/QUERYID="+queryObject.getID());
                             continue;
                         } else {
                             log.info("Processing Query: Id = " + queryObject.getID());
                         }
                         log.info("******************Building  email body for a user(userid="+userid+") and alert query object("+queryObject.getID()+")...********************");
                         // do these again - may be a saved search which doesn't save these!
                         queryObject.setEmailAlertWeek(m_strYearWeek);
                         queryObject.setSearchQueryWriter(new FastQueryWriter());
                         queryObject.compile();
                         oPage = getDocIds(queryObject, (m_cdlList.contains(customerid) ? m_cdlEmailAlertSize : emailAlertSize));

                         if ((oPage == null) && (m_skipEmptyList.contains(customerid))) {
                             log.info("! Skipping alert with zero results.");
                             skippedemails.add("USERID="+userid+"/CUSTOMERID="+customerid+"/QUERYID="+queryObject.getID());
                             continue;
                         }

                         queryString = new StringBuffer(queryObject.toXMLString());

                         // jam - added 1/13/2004 - was causing bug in search years - missing XML
                         // jam this XML was added directly to JSP output also! - not in query object
                         StringWriter sw = new StringWriter();
                         databaseconfig.toXML(credentials, sw);
                         queryString.append(sw.toString());

                         // jam - added 12/19/2003 - Needed in CitationResults.xml for Author links
                         // this added to XML output in JSPs - not in query object
                         queryString.append("<DBMASK>").append(queryObject.getDataBase()).append("</DBMASK>");

                         queryString.append("<ALERTS-PER-PAGE>").append(emailAlertSize).append("</ALERTS-PER-PAGE>");
                         queryString.append("<SERVER-LOCATION>").append(serverLocation).append("</SERVER-LOCATION>");
                         queryString.append("<WEEK-OF-YEAR>").append(m_strYearWeek).append("</WEEK-OF-YEAR>");

                         // callibg results manager with docids list.
                         rm = new EmailAlertResultsManager(oPage, "emailalert", "citation", false);
                         rm.setQueryString(queryString.toString());
                         displayString = rm.toDisplayFormat();
                         log.info("Message Body Size : " + displayString.length());
                         
                         String sender = eiProps.getProperty("EMAIL_SENDER");
                         if(sender == null || sender.trim().equalsIgnoreCase("")){
                         	sender = "eiemailalert@elsevier.com";
                         }
                         String recepient = emailAddress.trim(); 
                         String cclist = getCcList(queryObject.getID());
                         if(this.isThisTestEmailer()){
                         	recepient = eiProps.getProperty("TESTRECEPIENTS");
                         	cclist = eiProps.getProperty("TESTCCRECEPIENTS");
                         }else{
                         	if(eiProps.getProperty("PROD_MODE_SAME_RECEPIENT") != null){
                         		recepient = eiProps.getProperty("PROD_MODE_SAME_RECEPIENT");
                         	}
                         	if(eiProps.getProperty("PROD_MODE_SAME_CCEDRECEPIENT") != null){
                         		cclist = eiProps.getProperty("PROD_MODE_SAME_CCEDRECEPIENT");
                         	}
                         }
                         List <String> torecepients = new  ArrayList<String>();
                         if (recepient != null) {
                         	torecepients = new ArrayList<String>(Arrays.asList(recepient.split(",")));
                         }
                         List <String> toccrecepients = new  ArrayList<String>();
                         if (cclist != null) {
                         	toccrecepients = new ArrayList<String>(Arrays.asList(cclist.split(",")));
                         }
                         
                         String strDisplayQuery = (queryObject.getSearchType().equals(Query.TYPE_EASY) ? queryObject.getIntermediateQuery() : queryObject
                                 .getDisplayQuery());
                         strDisplayQuery = perlUtil.substitute("s/[\n\r]/ /g", strDisplayQuery);
                         int MAX_SUBJECT_LINE = 40;
                         String subject = "Engineering Village Email Alert: "
                                 + ((strDisplayQuery.length() >= MAX_SUBJECT_LINE) ? strDisplayQuery.substring(0, MAX_SUBJECT_LINE) + "..." : strDisplayQuery);
                         log.info("Sending email to " + recepient);
                         if (cclist != null) {
                             log.info("Sending Cc: email to " + Arrays.asList(cclist.split(",")));
                         }
                         log.info("Ei email " + queryObject.getSearchType() + " Search Alert for: " + strDisplayQuery);
                         
                         
                         SESMessage sesmessage = new SESMessage();
                         sesmessage.setDestination(torecepients,toccrecepients);
                         sesmessage.setMessage(subject,displayString,true);
                         sesmessage.setFrom(sender);
                         log.info("******************Sending  email for a user(userid="+userid+") and alert query object("+queryObject.getID()+")...********************");
                         boolean blnSent = false;
                         if (this.isThisTestEmailer()) {
                             blnSent = testEmailTransaction(userid, recepient, queryObject.getID(), sesmessage);
                         } else {
                             blnSent = emailTransaction(userid, emailAddress, queryObject.getID(), sesmessage);
                         }
                         log.info("Email sent status "+((blnSent) ? "Success." : "Failed"));
                         intSuccess = intSuccess + ((blnSent) ? 1 : 0);
                	 } catch (Exception e) {
                		 String queryId=null;
                		 if(queryObject != null){
                			 queryId = queryObject.getID();
                		 }
                		 erroremails.add("USERID="+userid+"/CUSTOMERID="+customerid+"/QUERYID="+queryId);
                         log.error("sendMails Exception. ", e);
                     }
                	
                }
             }
             log.info("*******************************************All email transaction process has been completed successfully!*****************************************");
        }catch (Exception e) {
        	 log.error("Exception. ", e);
        	 log.info("*******************************************Email transaction process has been failed/interrupted!*************************************************");
        } finally {
        	showFinalStatus(intTotal, intSuccess, alreadysentemails, skippedemails, erroremails);
        }
    }
    
    /**
     * @param total
     * @param success
     * @param alreadysentemails
     * @param skippedemails
     * @param erroremails
     */
    private static void showFinalStatus(int total, int success, List<String> alreadysentemails, List<String> skippedemails, List<String> erroremails){
    	log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
    	log.info("Count of Email Alerts sent = " + success + " out of " + total);
    	log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
        log.info("Count of Email Alerts already sent by previous transactions = " + alreadysentemails.size());
        for(String emailstatus : alreadysentemails){
        	log.info(emailstatus);
        }
        log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
        log.info("Count of Email Alerts skipped due to the zero results skippable configuration = " + skippedemails.size());
        for(String emailstatus : skippedemails){
        	log.info(emailstatus);
        }
        log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
        log.info("Count of Email Alerts not sent due to error = " + erroremails.size());
        for(String emailstatus : erroremails){
        	log.info(emailstatus);
        }
        log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
        log.info("Note: What if total count mismatches, which means we have omitted the duplicate saved searches during the email processing.");
        log.info("Try searching the text 'duplicate Saved Search!' from the log files for the further analysis.");
        log.info("-------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * @throws Exception
     */
    private void configureDatabaseConnection() throws Exception {
        // setup the jndi context and the datasource
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, 
                "org.apache.naming");            
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
           
            // Construct DataSource
            OracleConnectionPoolDataSource dsforsessionDB = new OracleConnectionPoolDataSource();
            dsforsessionDB.setURL(eiProps.getProperty("jdbc.url."+getEnvironment()));
            dsforsessionDB.setUser(eiProps.getProperty("jdbc.session.username"));
            dsforsessionDB.setPassword(eiProps.getProperty("jdbc.session.password"));
            ic.bind("java:comp/env/jdbc/"+eiProps.getProperty("jdbc.session.name"), dsforsessionDB);
            
            OracleConnectionPoolDataSource dsforsearchDB = new OracleConnectionPoolDataSource();
            dsforsearchDB.setURL(eiProps.getProperty("jdbc.url."+getEnvironment()));
            dsforsearchDB.setUser(eiProps.getProperty("jdbc.search.username"));
            dsforsearchDB.setPassword(eiProps.getProperty("jdbc.search.password"));
            ic.bind("java:comp/env/jdbc/"+eiProps.getProperty("jdbc.search.name"), dsforsearchDB);
        } catch (Exception ex) {
           ex.printStackTrace();
           log.error("Datasource creation failed!", ex);
        }
        
    }
    
    
    /**
     * @param yearWeek
     * @return
     */
    public static boolean validateYearWeek(String yearWeek){
    	boolean returnVal = true;
    	if (yearWeek == null || StringUtil.EMPTY_STRING.equals(yearWeek) || yearWeek.length() != 6) {
    		returnVal = false;
    	}
    	return returnVal;
    }
    
    public boolean isThisTestEmailer() {
		return isThisTestEmailer;
	}

	public void setThisTestEmailer(boolean isThisTestEmailer) {
		this.isThisTestEmailer = isThisTestEmailer;
	}
	
	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getM_strYearWeek() {
		return m_strYearWeek;
	}

	public void setM_strYearWeek(String m_strYearWeek) {
		this.m_strYearWeek = m_strYearWeek;
	}
}

