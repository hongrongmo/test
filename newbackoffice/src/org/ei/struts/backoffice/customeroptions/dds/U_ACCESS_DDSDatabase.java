package org.ei.struts.backoffice.customeroptions.dds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.Options;

public class U_ACCESS_DDSDatabase {

	private static Log log = LogFactory.getLog("U_ACCESS_DDSDatabase");

	public DDS findDDS_U_ACCESS(String contractid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DDS aDDS = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"SELECT * FROM DDS_DATA WHERE CONTRACT_ID=?");
			pstmt.setString(idx++, contractid);

			log.info("findDDS " + contractid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				aDDS = new DDS();
				aDDS.setFirstName(rs.getString("USER_FIRST_NAME"));
				aDDS.setLastName(rs.getString("USER_LAST_NAME"));
				aDDS.setEmail(rs.getString("USER_EMAIL"));
				aDDS.setCompany(rs.getString("COMPANY"));
				aDDS.setAddress1(rs.getString("ADDRESS"));
				aDDS.setAddress2(rs.getString("ADDRESS2"));
				aDDS.setCity(rs.getString("CITY"));
				aDDS.setState(rs.getString("STATE"));
				aDDS.setZip(rs.getString("ZIP"));
				aDDS.setPhone(rs.getString("PHONE_NUMBER"));
				aDDS.setFax(rs.getString("FAX_NUMBER"));
				aDDS.setAccountNumber(rs.getString("ACCOUNT_NUMBER"));
				aDDS.setShipping(rs.getString("SHIPPING"));
				aDDS.setCountry(rs.getString("COUNTRY"));
				aDDS.setCustomerID(rs.getString("CUST_ID"));
				aDDS.setContractID(contractid);
			}

		} catch (SQLException e) {
			log.error("findDDS_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("findDDS_U_ACCESS ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("findDDS_U_ACCESS ", e);
			}
		}

		return aDDS;
	}

	public boolean saveDDS_U_ACCESS(Options options) {

		boolean result = false;
		DDS dds = (DDS) options;
		try {
			if (findDDS_U_ACCESS(dds.getContractID()) != null) {
				result = updateDDS_U_ACCESS(dds);
			} else {
				result = insertDDS_U_ACCESS(dds);
			}

		} catch (Exception e) {
			log.error("savedds_U_ACCESS ", e);
		}

		return result;
	}

	private boolean updateDDS_U_ACCESS(DDS dds) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"UPDATE DDS_DATA SET USER_FIRST_NAME=?, USER_LAST_NAME=?, USER_EMAIL=?, COMPANY=?, ADDRESS=?, CITY=?, STATE=?, ZIP=?, PHONE_NUMBER=?, FAX_NUMBER=?, ACCOUNT_NUMBER=?, SHIPPING=?, COUNTRY=?, ADDRESS2=? WHERE contract_id=? and cust_id=?");

			pstmt.setString(idx++, dds.getFirstName());
			pstmt.setString(idx++, dds.getLastName());
			pstmt.setString(idx++, dds.getEmail());
			pstmt.setString(idx++, dds.getCompany());
			pstmt.setString(idx++, dds.getAddress1());
			pstmt.setString(idx++, dds.getCity());
			pstmt.setString(idx++, dds.getState());
			pstmt.setString(idx++, dds.getZip());
			pstmt.setString(idx++, dds.getPhone());
			pstmt.setString(idx++, dds.getFax());
			pstmt.setString(idx++, dds.getAccountNumber());
			pstmt.setString(idx++, dds.getShipping());
			pstmt.setString(idx++, dds.getCountry());
			pstmt.setString(idx++, dds.getAddress2());
			pstmt.setLong(idx++, Long.parseLong(dds.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(dds.getCustomerID()));
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("updateDDS_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("updateDDS_U_ACCESS ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("updateDDS_U_ACCESS ", e);
			}
		}

		return (result == 1);
	}

	private boolean insertDDS_U_ACCESS(DDS dds) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"INSERT INTO DDS_DATA " +
					" (CUST_ID, CONTRACT_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, COMPANY, ADDRESS, CITY, STATE, ZIP, PHONE_NUMBER, FAX_NUMBER, ACCOUNT_NUMBER, CREATED_DATE, SHIPPING, COUNTRY, ADDRESS2)" +
					" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)");
			pstmt.setLong(idx++, Long.parseLong(dds.getCustomerID()));
			pstmt.setLong(idx++, Long.parseLong(dds.getContractID()));
			pstmt.setString(idx++, dds.getFirstName());
			pstmt.setString(idx++, dds.getLastName());
			pstmt.setString(idx++, dds.getEmail());
			pstmt.setString(idx++, dds.getCompany());
			pstmt.setString(idx++, dds.getAddress1());
			pstmt.setString(idx++, dds.getCity());
			pstmt.setString(idx++, dds.getState());
			pstmt.setString(idx++, dds.getZip());
			pstmt.setString(idx++, dds.getPhone());
			pstmt.setString(idx++, dds.getFax());
			pstmt.setString(idx++, dds.getAccountNumber());
			pstmt.setString(idx++, dds.getShipping());
			pstmt.setString(idx++, dds.getCountry());
			pstmt.setString(idx++, dds.getAddress2());
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insertDDS_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("insertDDS_U_ACCESS ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insertDDS_U_ACCESS ", e);
			}
		}

		return (result == 1);
	}

}