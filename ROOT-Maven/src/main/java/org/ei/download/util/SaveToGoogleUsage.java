package org.ei.download.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class SaveToGoogleUsage {
	
    private final static Logger log4j = Logger.getLogger(SaveToGoogleUsage.class);
    
    private final static String queryByDownloadFormat = "SELECT FORMAT AS KEY_NAME, COUNT(FORMAT)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE  GROUP BY FORMAT ORDER BY FORMAT";
    private final static String queryByIP = "SELECT IP AS KEY_NAME, COUNT(IP)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE  GROUP BY IP ORDER BY IP";
    private final static String queryByAcctNo = "SELECT ACCTNO AS KEY_NAME, COUNT(ACCTNO)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE  GROUP BY ACCTNO ORDER BY ACCTNO";
    
    private final static String totalCountQuery = "SELECT COUNT(*) as TOTAL FROM GOOGLE_DRIVE_USAGE";
    
    private final static String queryByDownloadFormatAndDate = "SELECT FORMAT AS KEY_NAME, COUNT(FORMAT)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE WHERE TS >= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss') AND TS <= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss')  GROUP BY FORMAT ORDER BY FORMAT";
    private final static String queryByIPAndDate = "SELECT IP AS KEY_NAME, COUNT(IP)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE WHERE TS >= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss') AND TS <= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss')  GROUP BY IP ORDER BY IP";
    private final static String queryByAcctNoAndDate = "SELECT ACCTNO AS KEY_NAME, COUNT(ACCTNO)AS DOWNLOAD_COUNT FROM GOOGLE_DRIVE_USAGE WHERE TS >= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss') AND TS <= TO_TIMESTAMP(?, 'dd-mm-yyyy hh24:mi:ss') GROUP BY ACCTNO ORDER BY ACCTNO";
    
    
	
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
	
	
	public static Map<String,String> getUsageData(String usageOption, String startDate,String endDate){
		 
		 Map<String,String> usageData = new HashMap<String,String>();
		 String sqlQuery = "";
		 
		 if(startDate != null && endDate != null){
			 if(usageOption.equalsIgnoreCase("downloadformat")){
				 sqlQuery =queryByDownloadFormatAndDate ;
			 }else if(usageOption.equalsIgnoreCase("ip")){
				 sqlQuery =queryByIPAndDate ;
			 }else if(usageOption.equalsIgnoreCase("acctNo")){
				 sqlQuery =queryByAcctNoAndDate ;
			 }
			 String sDate = startDate+" 00:00:00";
			 String eDate = endDate+" 23:59:59";
			 usageData = getUsageDataByDateFromDB(sqlQuery,sDate,eDate);
		 }else{
			 if(usageOption.equalsIgnoreCase("downloadformat")){
				 sqlQuery =queryByDownloadFormat ;
			 }else if(usageOption.equalsIgnoreCase("ip")){
				 sqlQuery =queryByIP;
			 }else if(usageOption.equalsIgnoreCase("acctNo")){
				 sqlQuery =queryByAcctNo;
			 }
			 usageData = getUsageDataFromDB(sqlQuery);
		 }
		 return usageData;
	}
	
	private static Map<String,String> getUsageDataFromDB(String queryStr){
		 Map<String,String> usageData = new HashMap<String,String>();
		 Connection con = null;
	     ConnectionBroker broker = null;
	     PreparedStatement pstmt = null;
	     ResultSet rs = null;
	        
	     try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(queryStr);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	String key = rs.getString("KEY_NAME");
            	String value = rs.getString("DOWNLOAD_COUNT");
            	usageData.put(key, value);
            }
            pstmt = con.prepareStatement(totalCountQuery);
            close(rs);
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	String total = rs.getString("TOTAL");
            	usageData.put("totalCount", total);
            }
            log4j.info("Successfully retreived save to drive usage data.");
        } catch (Exception e) {
        	try {
                con.rollback();
            } catch (Exception re) {
            	log4j.error("Exception thrown while doing rollback : "+re.getMessage());
            }
        	log4j.error("Exception thrown while retreiving save to drive usage data from table :"+e.getMessage());
        } finally {
        	close(rs);
        	close(pstmt);
            replace(con);
        }
	    return usageData;
	}
	
	private static Map<String,String> getUsageDataByDateFromDB(String queryStr,String sDate, String eDate){
		 Map<String,String> usageData = new HashMap<String,String>();
		 Connection con = null;
	     ConnectionBroker broker = null;
	     PreparedStatement pstmt = null;
	     ResultSet rs = null;
	        
	     try {
           broker = ConnectionBroker.getInstance();
           con = broker.getConnection(DatabaseConfig.SESSION_POOL);
           pstmt = con.prepareStatement(queryStr);
           pstmt.setString(1, sDate);
           pstmt.setString(2, eDate);
           rs = pstmt.executeQuery();
           while (rs.next()) {
           	String key = rs.getString("KEY_NAME");
           	String value = rs.getString("DOWNLOAD_COUNT");
           	usageData.put(key, value);
           }
           pstmt = con.prepareStatement(totalCountQuery);
           close(rs);
           rs = pstmt.executeQuery();
           while (rs.next()) {
           	String total = rs.getString("TOTAL");
           	usageData.put("totalCount", total);
           }
           log4j.info("Successfully retreived save to drive usage data.");
       } catch (Exception e) {
       	try {
               con.rollback();
           } catch (Exception re) {
           	log4j.error("Exception thrown while doing rollback : "+re.getMessage());
           }
       	log4j.error("Exception thrown while retreiving save to drive usage data from table :"+e.getMessage());
       } finally {
       	close(rs);
       	close(pstmt);
        replace(con);
       }
	    return usageData;
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
 	
 	/**
	 * Close.
	 *
	 * @param rs the rs
	 */
	private static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
}
