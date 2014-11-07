package org.ei.download.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class SaveToGoogleUsage {
	
    private final static Logger log4j = Logger.getLogger(SaveToGoogleUsage.class);
    
    public static void trackUsage(String downloadFormat,String ip,String acctNo){
		Connection con = null;
        ConnectionBroker broker = null;
        PreparedStatement pstmt = null;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("INSERT INTO GOOGLE_DRIVE_USAGE (TS,IP,FORMAT,ACCTNO) VALUES(SYSDATE,?,?,?)");
            pstmt.setString(1, ip);
            pstmt.setString(2, downloadFormat);
            pstmt.setString(3, acctNo);
            pstmt.executeUpdate();
            con.commit();
            log4j.info("Google drive usage recorded for the download format '"+downloadFormat+"' from the account number'"+acctNo+"'.");
        } catch (Exception e) {
        	try {
                con.rollback();
            } catch (Exception re) {
            	log4j.error("Exception thrown while doing rollback : "+re.getMessage());
            }
        	log4j.error("Exception thrown while adding usage count for save to google drive :"+e.getMessage());
        	
        } finally {
        	close(pstmt);
            replace(con);
        }
		
	}
	
	
	 /**
 	 * Close.
 	 *
 	 * @param pstmt the pstmt
 	 */
 	private static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	 
	 /**
 	 * Replace.
 	 *
 	 * @param con the con
 	 */
 	private static void replace(Connection con) {
        if (con != null) {
            try {
                ConnectionBroker broker = ConnectionBroker.getInstance();
                con.setAutoCommit(true);
                broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
            } catch (Exception cpe) {
                cpe.printStackTrace();
            }
        }
    }
 	
 	
	
}
