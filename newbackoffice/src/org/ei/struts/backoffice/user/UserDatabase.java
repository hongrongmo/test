/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/user/UserDatabase.java-arc   1.2   Apr 17 2009 10:15:00   johna  $
 * $Revision:   1.2  $
 * $Date:   Apr 17 2009 10:15:00  $
 *
 */
package org.ei.struts.backoffice.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.util.uid.UID;

public class UserDatabase {

	private static Log log = LogFactory.getLog("UserDatabase");

	private String ACCESS_FIELD_NAME = "MANAGER";

	public User createUser() {

		User aUser = new User();
		return aUser;

	}

	private long getNextSalesRepID() throws SQLException {
		return UID.getNextId();
	}

	// updated
	public Collection getUsers() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection aUsers = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM SALES_ORG ORDER BY SALES_REP");

			rs = pstmt.executeQuery();

			User aUser = null;
			while (rs.next()) {
				aUser = new User();
				aUser.setSalesRepId(rs.getString("SALES_REP_ID"));
				aUser.setName(rs.getString("SALES_REP"));

				aUser.setEmail(rs.getString("EMAIL_ADDRESS"));
				aUser.setPhone(rs.getString("PHONE_NUMBER"));
				aUser.setUsername(rs.getString("USER_ID"));
				aUser.setPassword(rs.getString("PASSWORD"));
				aUser.setIsEnabled(rs.getInt(ACCESS_FIELD_NAME));

				aUser.setRoles(new String[] { rs.getString("ACCESS_LEVEL") });
				aUser.setRegions(new String[] { rs.getString("REGION_CODE") });

				aUsers.add(aUser);

			}

		} catch (SQLException e) {
			log.error("getUsers ", e);

		} catch (NamingException e) {
			log.error("getUsers ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getUsers ", e);
			}
		}

		return aUsers;
	}

	public User findUserByUsername(String username) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User aUser = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM SALES_ORG WHERE USER_ID=?");
			pstmt.setString(idx++, username);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				aUser = new User();
				aUser.setSalesRepId(rs.getString("SALES_REP_ID"));
				aUser.setName(rs.getString("SALES_REP"));
				aUser.setEmail(rs.getString("EMAIL_ADDRESS"));
				aUser.setPhone(rs.getString("PHONE_NUMBER"));
				aUser.setUsername(rs.getString("USER_ID"));
				aUser.setPassword(rs.getString("PASSWORD"));
				aUser.setIsEnabled(rs.getInt(ACCESS_FIELD_NAME));

				aUser.setRoles(new String[] { rs.getString("ACCESS_LEVEL") });
				aUser.setRegions(new String[] { rs.getString("REGION_CODE") });
			}

		} catch (SQLException e) {
			log.error("findUserByUsername ", e);

		} catch (NamingException e) {
			log.error("findUserByUsername ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("findUserByUsername ", e);
			}
		}

		return aUser;
	}

	// updated
	public User findUser(String salesrepid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User aUser = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM SALES_ORG WHERE SALES_REP_ID=?");
			pstmt.setLong(idx++, Long.parseLong(salesrepid));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				aUser = new User();
				aUser.setSalesRepId(rs.getString("SALES_REP_ID"));
				aUser.setName(rs.getString("SALES_REP"));
				aUser.setEmail(rs.getString("EMAIL_ADDRESS"));
				aUser.setPhone(rs.getString("PHONE_NUMBER"));
				aUser.setUsername(rs.getString("USER_ID"));
				aUser.setPassword(rs.getString("PASSWORD"));
				aUser.setIsEnabled(rs.getInt(ACCESS_FIELD_NAME));
				log.info("isEnabled = " + rs.getInt(ACCESS_FIELD_NAME));

				aUser.setRoles(new String[] { rs.getString("ACCESS_LEVEL") });
				aUser.setRegions(new String[] { rs.getString("REGION_CODE") });
			}

		} catch (NumberFormatException nfe) {
			aUser = null;
			log.info("User salesrepid:" + salesrepid + " not found.");
		} catch (SQLException e) {
			log.error("findUser ", e);

		} catch (NamingException e) {
			log.error("findUser ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("findUser ", e);
			}
		}

		return aUser;
	}

	public boolean saveUser(User user) {

		boolean result = false;

		try {

			if (findUser(user.getSalesRepId()) != null) {
				result = updateUser(user);
			} else {
				result = insertUser(user);
			}

		} catch (Exception e) {
			log.error("saveUser ", e);
		}

		return result;
	}

	private boolean updateUser(User user) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("UPDATE SALES_ORG SET SALES_REP=?, "
							+ ACCESS_FIELD_NAME
							+ "=?, EMAIL_ADDRESS=?, PHONE_NUMBER=?, USER_ID=?, PASSWORD=?, REGION_CODE=?, ACCESS_LEVEL=?, LAST_UPDATED=SYSDATE WHERE SALES_REP_ID=?");

			pstmt.setString(idx++, user.getName());
			pstmt.setInt(idx++, user.getIsEnabled());
			pstmt.setString(idx++, user.getEmail());
			pstmt.setString(idx++, user.getPhone());
			pstmt.setString(idx++, user.getUsername());
			pstmt.setString(idx++, user.getPassword());
			pstmt.setString(idx++, (user.getRegions())[0]);
			pstmt.setString(idx++, (user.getRoles())[0]);

			pstmt.setString(idx++, user.getSalesRepId());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("updateUser ", e);

		} catch (NamingException e) {
			log.error("updateUser ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("updateUser ", e);
			}
		}

		return (result == 1);
	}

	private boolean insertUser(User user) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO SALES_ORG (SALES_REP_ID, SALES_REP, "
							+ ACCESS_FIELD_NAME
							+ ", REGION_CODE, ACCESS_LEVEL, EMAIL_ADDRESS, PHONE_NUMBER, USER_ID, PASSWORD) VALUES(?,?,?,?,?,?,?,?,?)");

			long lngSalesRepID = getNextSalesRepID();
			pstmt.setLong(idx++, lngSalesRepID);
			pstmt.setString(idx++, user.getName());
			pstmt.setInt(idx++, user.getIsEnabled());
			pstmt.setString(idx++, (user.getRegions())[0]);
			pstmt.setString(idx++, (user.getRoles())[0]);
			pstmt.setString(idx++, user.getEmail());
			pstmt.setString(idx++, user.getPhone());
			pstmt.setString(idx++, user.getUsername());
			pstmt.setString(idx++, user.getPassword());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insertUser ", e);

		} catch (NamingException e) {
			log.error("insertUser ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("insertUser ", e);
			}
		}

		return (result == 1);
	}

}
