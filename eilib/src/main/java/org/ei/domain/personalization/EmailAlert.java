package org.ei.domain.personalization;

/** project specific imports*/
import java.io.StringWriter;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
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
import org.ei.email.EIMessage;
import org.ei.email.EMail;
import org.ei.query.base.FastQueryWriter;
import org.ei.util.StringUtil;

/**
 *
 * Handles EmailAlert related tasks such as
 * <p>
 * a)This object gets distinct userIds and his email address where emailalerts is not null.
 * </p>
 * <p>
 * b)For each userId gets query object where emailAlert is not null.
 * </p>
 * <p>
 * c)For query object it call corresponding SearchControl depending on database of query. From that compenent gets first 25 documents DocIDs list.
 * </p>
 * <p>
 * d)This DocIds list is send to the EmailAlertResultsManager and then call Email.java to send mail.
 * </p>
 *
 */

public class EmailAlert {

    protected static Log log = LogFactory.getLog(EmailAlert.class);

    // private static final EmailAlert INSTANCE = new EmailAlert();
    private static String m_strYearWeek = StringUtil.EMPTY_STRING;

    private ApplicationProperties eiProps;
    private ConnectionBroker m_broker = null;
    private EMail m_emailInstance = null;
    private Perl5Util perlUtil = new Perl5Util();

    private int m_intHitCount = -1;
    private int emailAlertSize;
    private String serverLocation;
    private String m_strPoolname = StringUtil.EMPTY_STRING;
    private String m_strPoolsFilename = StringUtil.EMPTY_STRING;
    private String m_strXSLFilename = StringUtil.EMPTY_STRING;
    private String m_strConfigFilePath = StringUtil.EMPTY_STRING;
    private String m_strPropertiesFile = "./etc/emailalert.properties";
    private String m_strBASE_URL = StringUtil.EMPTY_STRING;

    // California Digital Library Properties
    private List<String> m_cdlList = new ArrayList<String>();
    private int m_cdlEmailAlertSize = 1000;
    private List<String> m_skipEmptyList = new ArrayList<String>();

    public void cleanup() {
        if (m_broker != null) {
            try {
                m_broker.closeConnections();
            } catch (ConnectionPoolException cpe) {
            }
        }
    }

    /**
     * This constructor reads runtimeProperties and gets size of documents to send as mails. and sends mails.
     */
    public EmailAlert() {

        try {
            eiProps = ApplicationProperties.getInstance(m_strPropertiesFile);

            // create an instance of EMail Object for sending email
            m_emailInstance = EMail.getInstance();

            String sAlertSize = eiProps.getProperty("EMAILALERTSIZE");
            serverLocation = eiProps.getProperty("SERVERLOCATION");

            m_strConfigFilePath = eiProps.getProperty("CONFIGFILEPATH");
            m_strBASE_URL = eiProps.getProperty("FastBaseUrl");

            m_strPoolname = DatabaseConfig.SESSION_POOL;
            m_strPoolsFilename = eiProps.getProperty("POOLSFILENAME");
            m_strXSLFilename = eiProps.getProperty("WEBAPPRESOURCEPATH") + eiProps.getProperty("XSLFILE");

            m_broker = ConnectionBroker.getInstance(m_strPoolsFilename);

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
     * @param return void. Steps it does a. calls getUserIds() which returns hsahtable contains userids,useremailIds. b. calls
     *        SavedSearches.getListUserSavedSearches(userid) which returs list of queryobjects which contains mail alerts. c.call getDocIds for each query
     *        objects and gets DocIds list and send to results manager. with to address as emailuserid.
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
        EIMessage eimessage = null;
        Query queryObject = null;
        Page oPage = null;

        int intTotal = 0;
        int intSuccess = 0;

        try {
            DatabaseConfig databaseconfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
            String[] credentials = new String[] { "CPX", "INS", "NTI", "UPA", "EUP" };

            log.info("------------------------------------------------------------");

            intTotal = countTotalEmails();
            log.info("Count of Email Alerts to be sent = " + intTotal);
            log.info("");
            if (intTotal <= 0) {
                return;
            }

            // calling getUserIds()
            Hashtable<String, String> userIdList = getUserIds();
            Iterator<String> itrUsers = userIdList.keySet().iterator();

            while (itrUsers.hasNext()) {

                // getting userId.
                userid = (String) itrUsers.next();

                // getting emailAddress and customerid for this userId.
                userData = new ArrayList<Object>();
                perlUtil.split(userData, "/,/", (String) userIdList.get(userid));
                emailAddress = (String) userData.get(0);
                customerid = (String) userData.get(1);

                // getting queryObjects to a userID
                // queryObjectList=getQueryList(userId);
                // jam
                queryObjectList = SavedSearches.getListUserAlerts(userid);

                log.info("User EMAIL,CUSTOMER_ID " + emailAddress + "," + customerid + " has " + queryObjectList.size() + " saved email alerts");
                if (m_cdlList.contains(customerid)) {
                    log.info("! CDL Alert Size will be " + m_cdlEmailAlertSize);
                }

                // Iterating through the query object list
                for (int querysize = 0; querysize < queryObjectList.size(); querysize++) {
                    try {
                        // getting a queryObject
                        // String strQueryXML = (String) queryObjectList.get(querysize);
                        // strQueryXML = perlUtil.substitute("s/[\n\r]/ /g", strQueryXML);
                        // queryObject = new Query(strQueryXML);
                        queryObject = (Query) queryObjectList.get(querysize);
                        queryObject.setRecordCount("0");

                        String cclist = getCcList(queryObject.getID());

                        if ((queryObject.getAutoStemming()).equals("on")) {
                        }

                        // NOV-DEC Release Changes needed to run
                        queryObject.setDatabaseConfig(databaseconfig);
                        queryObject.setCredentials(credentials);

                        // pre-test with null message
                        boolean blnAlreadySent = !emailTransaction(userid, emailAddress, queryObject.getID(), null);
                        if (blnAlreadySent == true) {
                            continue;
                        } else {
                            log.info("Processing Query: Id = " + queryObject.getID());
                        }

                        // do these again - may be a saved search which doesn't save these!
                        queryObject.setEmailAlertWeek(m_strYearWeek);
                        queryObject.setSearchQueryWriter(new FastQueryWriter());
                        queryObject.compile();
                        oPage = getDocIds(queryObject, (m_cdlList.contains(customerid) ? m_cdlEmailAlertSize : emailAlertSize));

                        if ((oPage == null) && (m_skipEmptyList.contains(customerid))) {
                            log.info("! Skipping alert with zero results.");
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
                        rm = new EmailAlertResultsManager(oPage, "emailalert", "citation", false, m_strXSLFilename);
                        rm.setQueryString(queryString.toString());
                        displayString = rm.toDisplayFormat();
                        log.info("Message Body Size : " + displayString.length());

                        // create an instance of eimessage and call the respective set methods
                        eimessage = new EIMessage();
                        eimessage.setFrom("<eiemailalert@elsevier.com>");
                        eimessage.setSender("<eiemailalert@elsevier.com>");

                        String strEmailAddresses = StringUtil.EMPTY_STRING;
                        if (eiProps.getProperty("TESTRECEPIENTS") != null) {
                            strEmailAddresses = eiProps.getProperty("TESTRECEPIENTS");
                            // if cclist is defined and we are testing -
                            // do not send tests to cc list!!
                            // use TESTCcRECEPIENTS or TESTRECEPIENTS
                            if (cclist != null) {
                                if (eiProps.getProperty("TESTCcRECEPIENTS") != null) {
                                    cclist = eiProps.getProperty("TESTCcRECEPIENTS");
                                } else {
                                    cclist = eiProps.getProperty("TESTRECEPIENTS");
                                }
                            }
                        } else {
                            strEmailAddresses = emailAddress.trim();
                        }

                        if (strEmailAddresses != null) {
                            String[] torecepients = strEmailAddresses.split(",");
                            eimessage.addTORecepients(Arrays.asList(torecepients));
                        }
                        if (cclist != null) {
                            // ALWAYS use the 'originator' in the sender text when Cc:'ing
                            queryString.append("<SENDER>").append(emailAddress).append("</SENDER>");

                            String[] ccrecepients = cclist.split(",");
                            eimessage.addCCRecepients(Arrays.asList(ccrecepients));
                            // When testing the strEmailAddresses sometimes is a lsit
                            // which was causing and error so changed to single sender emailAddress
                            eimessage.setFrom("<eiemailalert@elsevier.com>");
                            eimessage.setSender("<eiemailalert@elsevier.com>");
                        }

                        // change taken from Query Object toXMLString
                        // Easy search should not use Display - but Intermediate query
                        String strDisplayQuery = (queryObject.getSearchType().equals(Query.TYPE_EASY) ? queryObject.getIntermediateQuery() : queryObject
                            .getDisplayQuery());
                        // String strDisplayQuery = queryObject.getDisplayQuery(); String strDisplayQuery = queryObject.getDisplayQuery();

                        strDisplayQuery = perlUtil.substitute("s/[\n\r]/ /g", strDisplayQuery);

                        int MAX_SUBJECT_LINE = 40;
                        // take 22 chars for the "lead in", plus the first forty (or less) from the Display Query String
                        eimessage.setSubject("Engineering Village Email Alert: "
                            + ((strDisplayQuery.length() >= MAX_SUBJECT_LINE) ? strDisplayQuery.substring(0, MAX_SUBJECT_LINE) + "..." : strDisplayQuery));
                        eimessage.setSentDate(new java.util.Date(System.currentTimeMillis()));
                        eimessage.setContentType("text/html");
                        eimessage.setMessageBody("\n \n" + displayString);

                        log.info("Sending email to " + strEmailAddresses);
                        if (cclist != null) {
                            log.info("Sending Cc: email to " + Arrays.asList(cclist.split(",")));
                        }
                        log.info("Ei email " + queryObject.getSearchType() + " Search Alert for: " + strDisplayQuery);

                        // if this is a test - do not insert into transactions
                        // just directly send message
                        boolean blnSent = false;
                        if (eiProps.getProperty("TESTRECEPIENTS") != null) {
                            blnSent = testEmailTransaction(userid, strEmailAddresses, queryObject.getID(), eimessage);
                        } else {
                            blnSent = emailTransaction(userid, emailAddress, queryObject.getID(), eimessage);
                        }

                        log.info((blnSent) ? "Success." : "Failed");
                        intSuccess = intSuccess + ((blnSent) ? 1 : 0);

                    } catch (Exception e) {
                        log.error("sendMails Exception. ", e);
                    }

                    log.info("");
                } // for Query loop
            } // for each User loop
        } catch (Exception e) {
            log.error("Exception. ", e);
        } finally {
            log.info("Count of Email Alerts sent = " + intSuccess + " out of " + intTotal);
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

                if (strFilter != null) {
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

    private int countTotalEmails() throws Exception {

        Connection con = null;
        ResultSet rset = null;
        PreparedStatement pstmt = null;
        int intCount = 0;
        int idx = 1;

        try {
            String strFilter = eiProps.getProperty("TESTEMAILFILTER");
            log.info("strFilter = [" + strFilter + "]");

            con = m_broker.getConnection(m_strPoolname);

            if (strFilter != null) {
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

    private boolean testEmailTransaction(String strUserId, String strUserEmail, String strQueryId, EIMessage eimessage) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;
        boolean blnSuccess = false;

        try {
            blnSuccess = emailTransaction(strUserId, strUserEmail, strQueryId, eimessage);

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

    private boolean emailTransaction(String strUserId, String strUserEmail, String strQueryId, EIMessage eimessage) throws Exception {
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
                if (eimessage != null) {
                    // m_emailInstance.sendMultiPartMessage(eimessage);
                    // ZY 06/11/09: do not include s.gif in the email
                    m_emailInstance.sendMessage(eimessage);

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

    public static void main(String[] args) {

        // ANT: C:\javaplatform2\eijava\emailalerts>build run -Dyearweek=200241

        EmailAlert emaInstance = null;
        try {

            if ((args.length == 1) && (args[0] != null)) {
                m_strYearWeek = args[0];
            }

            if (m_strYearWeek == null || StringUtil.EMPTY_STRING.equals(m_strYearWeek) || m_strYearWeek.length() != 6) {
                log.info("You must specify a Year and a Week number in the following fomat, YYYYWW. i.e. 200542");
            } else {
                log.info("Year and Week. =[" + m_strYearWeek + "]");

                emaInstance = new EmailAlert();
                emaInstance.sendMails();
            }

        } catch (NumberFormatException nfe) {
            log.error("Main ", nfe);
        } catch (Exception e) {
            log.error("Main ", e);
        } finally {
            log.info("Cleaning up Connections");
            if (emaInstance != null) {
                emaInstance.cleanup();
            }
        }
        System.exit(0);
    }

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

}
