package org.ei.struts.backoffice.customeroptions.usage;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.Options;

public class UsageDatabase {

	private static Log log = LogFactory.getLog("UsageDatabase");

	public Usage createUsage() {
		Usage aUsage = new Usage();
		return aUsage;
	}

	public Usage findUsage(String contractid) {

//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
		Usage aUsage = null;

//		try {
//
//			Context initCtx = new InitialContext();
//			Context envCtx =
//				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
//			DataSource ds =
//				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
//			int idx = 1;
//
//			conn = ds.getConnection();
//			pstmt =
//				conn.prepareStatement(
//					"SELECT * FROM Usage_DATA WHERE CONTRACT_ID=?");
//			pstmt.setString(idx++, contractid);
//
//			log.info("findUsage " + contractid);
//
//			rs = pstmt.executeQuery();
//
//			if (rs.next()) {
//				aUsage = new Usage();
//				aUsage.setFirstName(rs.getString("USER_FIRST_NAME"));
//				aUsage.setLastName(rs.getString("USER_LAST_NAME"));
//				aUsage.setEmail(rs.getString("USER_EMAIL"));
//				aUsage.setCompany(rs.getString("COMPANY"));
//				aUsage.setAddress1(rs.getString("ADDRESS"));
//				aUsage.setAddress2(rs.getString("ADDRESS2"));
//				aUsage.setCity(rs.getString("CITY"));
//				aUsage.setState(rs.getString("STATE"));
//				aUsage.setZip(rs.getString("ZIP"));
//				aUsage.setPhone(rs.getString("PHONE_NUMBER"));
//				aUsage.setFax(rs.getString("FAX_NUMBER"));
//				aUsage.setAccountNumber(rs.getString("ACCOUNT_NUMBER"));
//				aUsage.setShipping(rs.getString("SHIPPING"));
//				aUsage.setCountry(rs.getString("COUNTRY"));
//				aUsage.setCustomerID(rs.getString("CUST_ID"));
//				aUsage.setContractID(contractid);
//			}
//
//		} catch (SQLException e) {
//			log.error("findUsage ", e);
//
//		} catch (NamingException e) {
//			log.error("findUsage ", e);
//
//		} finally {
//
//			try {
//				if (rs != null)
//					rs.close();
//				if (pstmt != null)
//					pstmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				log.error("findUsage ", e);
//			}
//		}

		return aUsage;
	}

	public boolean saveUsage(Options options) {

		boolean result = false;
		Usage usage = (Usage) options;
		try {
			if (findUsage(usage.getContractID()) != null) {
				result = updateUsage(usage);
			} else {
				result = insertUsage(usage);
			}

		} catch (Exception e) {
			log.error("saveusage ", e);
		}

		return result;
	}

	private boolean updateUsage(Usage usage) throws SQLException {

//		Connection conn = null;
//		PreparedStatement pstmt = null;
		int result = 0;
//
//		try {
//
//			Context initCtx = new InitialContext();
//			Context envCtx =
//				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
//			DataSource ds =
//				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
//			int idx = 1;
//
//			conn = ds.getConnection();
//			pstmt =
//				conn.prepareStatement(
//					"UPDATE Usage_DATA SET USER_FIRST_NAME=?, USER_LAST_NAME=?, USER_EMAIL=?, COMPANY=?, ADDRESS=?, CITY=?, STATE=?, ZIP=?, PHONE_NUMBER=?, FAX_NUMBER=?, ACCOUNT_NUMBER=?, SHIPPING=?, COUNTRY=?, ADDRESS2=? WHERE contract_id=? and cust_id=?");
//
//			pstmt.setString(idx++, usage.getFirstName());
//			pstmt.setString(idx++, usage.getLastName());
//			pstmt.setString(idx++, usage.getEmail());
//			pstmt.setString(idx++, usage.getCompany());
//			pstmt.setString(idx++, usage.getAddress1());
//			pstmt.setString(idx++, usage.getCity());
//			pstmt.setString(idx++, usage.getState());
//			pstmt.setString(idx++, usage.getZip());
//			pstmt.setString(idx++, usage.getPhone());
//			pstmt.setString(idx++, usage.getFax());
//			pstmt.setString(idx++, usage.getAccountNumber());
//			pstmt.setString(idx++, usage.getShipping());
//			pstmt.setString(idx++, usage.getCountry());
//			pstmt.setString(idx++, usage.getAddress2());
//			pstmt.setLong(idx++, Long.parseLong(usage.getContractID()));
//			pstmt.setLong(idx++, Long.parseLong(usage.getCustomerID()));
//			result = pstmt.executeUpdate();
//
//		} catch (SQLException e) {
//			log.error("updateUsage ", e);
//
//		} catch (NamingException e) {
//			log.error("updateUsage ", e);
//
//		} finally {
//
//			try {
//				if (pstmt != null)
//					pstmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				log.error("updateUsage ", e);
//			}
//		}

		return (result == 1);
	}

	private boolean insertUsage(Usage usage) {

//		Connection conn = null;
//		PreparedStatement pstmt = null;
		int result = 0;
//
//		try {
//
//			Context initCtx = new InitialContext();
//			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
//			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
//			int idx = 1;
//
//			conn = ds.getConnection();
//			pstmt = conn.prepareStatement("INSERT INTO Usage_DATA " +//					" (CUST_ID, CONTRACT_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, COMPANY, ADDRESS, CITY, STATE, ZIP, PHONE_NUMBER, FAX_NUMBER, ACCOUNT_NUMBER, CREATED_DATE, SHIPPING, COUNTRY, ADDRESS2)" +//					" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)");
//			pstmt.setLong(idx++, Long.parseLong(usage.getCustomerID()));
//			pstmt.setLong(idx++, Long.parseLong(usage.getContractID()));
//			pstmt.setString(idx++, usage.getFirstName());
//			pstmt.setString(idx++, usage.getLastName());
//			pstmt.setString(idx++, usage.getEmail());
//			pstmt.setString(idx++, usage.getCompany());
//			pstmt.setString(idx++, usage.getAddress1());
//			pstmt.setString(idx++, usage.getCity());
//			pstmt.setString(idx++, usage.getState());
//			pstmt.setString(idx++, usage.getZip());
//			pstmt.setString(idx++, usage.getPhone());
//			pstmt.setString(idx++, usage.getFax());
//			pstmt.setString(idx++, usage.getAccountNumber());
//			pstmt.setString(idx++, usage.getShipping());
//			pstmt.setString(idx++, usage.getCountry());
//			pstmt.setString(idx++, usage.getAddress2());
//			result = pstmt.executeUpdate();
//
//		} catch (SQLException e) {
//			log.error("insertUsage ", e);
//
//		} catch (NamingException e) {
//			log.error("insertUsage ", e);
//
//		} finally {
//
//			try {
//				if (pstmt != null)
//					pstmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				log.error("insertUsage ", e);
//			}
//		}
//
		return (result == 1);
	}

}