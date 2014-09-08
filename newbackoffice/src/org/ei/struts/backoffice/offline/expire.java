package org.ei.struts.backoffice.offline;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class expire {

  private static Log log = LogFactory.getLog("expire");

  public static void main(String[] args) throws Exception {

    //
    // First, we set up the PoolingDataSource.
    // Normally this would be handled auto-magically by
    // an external configuration, but in this example we'll
    // do it manually.
    //
    log.info("Setting up data source.");
    DataSource dataSource = setupDataSource("jdbc:oracle:thin:NEW_OFFICE/devel@e3000.ei.org:1521:apli");
    log.info("Done.");


    log.info(" START DELAYED CONTRACT(s) " + startDelayedContracts(dataSource));

    log.info(" TEST EMAL ");
    testmail();

  }

  public static Collection getExpirations(DataSource dataSource, String strType, int intGracePeriod) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
    Collection expiredEntitlements = new ArrayList();

		try {

			int idx = 1;
      Calendar cal = Calendar.getInstance();

			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM N_ENTITLEMENT, N_CONTRACT WHERE " +
			                                "N_ENTITLEMENT.CONTRACT_ID = N_CONTRACT.CONTRACT_ID " +
                                      "AND " +
                                      "N_CONTRACT.STATUS_ID=? " +
                                      "AND " +
			                                "N_ENTITLEMENT.END_DATE+?=?");

	    pstmt.setString(idx++, strType);
	    pstmt.setInt(idx++, intGracePeriod);
	    pstmt.setDate(idx++, new Date(cal.getTime().getTime()));
			rs = pstmt.executeQuery();

			while(rs.next()) {
        log.debug(" RECORD " + rs.getString("ENTITLEMENT_ID") + " END_DATE " + rs.getString("END_DATE"));
        expiredEntitlements.add(rs.getString("ENTITLEMENT_ID"));
			}

	  }
	  catch(SQLException e) {
			log.error("doTask ",e);

	  }
	  finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e) {
				log.error("doTask ",e);
			}
		}
    return expiredEntitlements;
  }


  public static Collection getNotifications(DataSource dataSource, String strType, int intGracePeriod) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
    Collection expiredEntitlements = new ArrayList();

		try {

			int idx = 1;
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_YEAR, intGracePeriod);

			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM N_ENTITLEMENT, N_CONTRACT WHERE " +
			                                "N_ENTITLEMENT.CONTRACT_ID = N_CONTRACT.CONTRACT_ID " +
                                      "AND " +
                                      "N_CONTRACT.STATUS_ID=? " +
                                      "AND " +
			                                "N_ENTITLEMENT.END_DATE=?");

	    pstmt.setString(idx++, strType);
	    pstmt.setDate(idx++, new Date(cal.getTime().getTime()));
			rs = pstmt.executeQuery();

			while(rs.next()) {
        log.debug(" RECORD " + rs.getString("ENTITLEMENT_ID") + " END_DATE " + rs.getString("END_DATE"));
        expiredEntitlements.add(rs.getString("ENTITLEMENT_ID"));
			}

	  }
	  catch(SQLException e) {
			log.error("doTask ",e);

	  }
	  finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e) {
				log.error("doTask ",e);
			}
		}
    return expiredEntitlements;
  }


  public static Collection startDelayedContracts(DataSource dataSource) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
    Collection delayedStartEntitlements = new ArrayList();

		try {

			int idx = 1;
      Calendar cal = Calendar.getInstance();

			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM N_ENTITLEMENT WHERE START_DATE = ?");

	    pstmt.setDate(idx++, new Date(cal.getTime().getTime()));
			rs = pstmt.executeQuery();

			while(rs.next()) {
        log.debug(" RECORD " + rs.getString("ENTITLEMENT_ID") + " END_DATE " + rs.getString("END_DATE"));
        delayedStartEntitlements.add(rs.getString("ENTITLEMENT_ID"));
			}

	  }
	  catch(SQLException e) {
			log.error("doTask ",e);

	  }
	  finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e) {
				log.error("doTask ",e);
			}
		}
    return delayedStartEntitlements;
  }

  // -----------------------------------------------------------------------------
  public static DataSource setupDataSource(String connectURI) throws Exception {

    //
    // First, we'll need a ObjectPool that serves as the
    // actual pool of connections.
    //
    // We'll use a GenericObjectPool instance, although
    // any ObjectPool implementation will suffice.
    //
    ObjectPool connectionPool = new GenericObjectPool(null);

    //
    // Next, we'll create a ConnectionFactory that the
    // pool will use to create Connections.
    // We'll use the DriverManagerConnectionFactory,
    // using the connect string passed in the command line
    // arguments.
    //
    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,null);

    //
    // Now we'll create the PoolableConnectionFactory, which wraps
    // the "real" Connections created by the ConnectionFactory with
    // the classes that implement the pooling functionality.
    //
    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

    //
    // Finally, we create the PoolingDriver itself,
    // passing in the object pool we created.
    //
    PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

    return dataSource;
  }

  public static void testmail() {

    try {
      Properties props = new Properties();

      // Set the host smtp address
      // create some properties and get the default Session
      props.put("mail.transport.protocol","smtp");
      props.put("mail.smtp.host", "cv-test.ei.org");
      props.put("mail.from", "eicustomersupport@elsevier.com");

      Session session = Session.getDefaultInstance(props);
      session.setDebug(false);
      //session.setDebugOut(log)

      // Create meassage
      Message msg = new MimeMessage(session);

      // Set from address
      //msg.setFrom(new InternetAddress("eicustomersupport@elsevier.com"));

      msg.addRecipient(Message.RecipientType.TO, new InternetAddress("j.moschetto@elsevier.com"));

      // Setting the Subject and Content Type
      msg.setSubject("HELLO");
      msg.setContent("TEST", "text/plain");

      Transport.send(msg);
    }
    catch(javax.mail.internet.AddressException ae) {
      log.error("offline email",ae);
    }
    catch(javax.mail.MessagingException me) {
      log.error("offline email",me);
    }

  }
}
