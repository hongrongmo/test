/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contact/ContactDatabase.java-arc   1.0   Jan 14 2008 17:10:26   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:26  $
 *
 */
package org.ei.struts.backoffice.contact;

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

public class ContactDatabase {

	private static Log log = LogFactory.getLog("ContactDatabase");

	public Contact createContact() throws Exception {

		Contact aContact = null;

		aContact = new Contact();

		aContact.setContactID(String.valueOf(this.getNextContactID()));

		return aContact;

	}

	private long getNextContactID() throws SQLException {
		return UID.getNextId();
	}

	public Contact findContact(String contactid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Contact aContact = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"SELECT CUST_CONTACT.CUSTOMER_ID, CONTACT.* FROM CONTACT, CUST_CONTACT WHERE CUST_CONTACT.CONTACT_ID = CONTACT.CONTACT_ID AND CONTACT.CONTACT_ID=?");
			pstmt.setString(idx++, contactid);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				aContact = new Contact();

				aContact.setContactID(rs.getString("CONTACT_ID"));
				aContact.setFirstName(rs.getString("FIRST_NAME"));
				aContact.setLastName(rs.getString("LAST_NAME"));
				aContact.setTitle(rs.getString("TITLE"));
				aContact.setEmail(rs.getString("EMAIL_ADDRESS"));
				aContact.setPHONE(rs.getString("PHONE_NUMBER"));
				aContact.setFAX(rs.getString("FAX_NUMBER"));
				aContact.setADDRESS(rs.getString("STREET_ADDRESS1"));
				aContact.setADDRESS2(rs.getString("STREET_ADDRESS2"));
				aContact.setCITY(rs.getString("CITY"));
				aContact.setSTATE(rs.getString("STATE"));
				aContact.setZIP(rs.getString("ZIP_CODE"));
				aContact.setCOUNTRY(rs.getString("COUNTRY"));
				aContact.setCustomerID(rs.getString("CUSTOMER_ID"));

			}

		} catch (SQLException e) {
			log.error("findContact ", e);

		} catch (NamingException e) {
			log.error("findContact ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("findContact ", e);
			}
		}

		return aContact;
	}

	public Collection getContacts(String customerid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allContacts = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"SELECT CUST_CONTACT.CUSTOMER_ID, CONTACT.* FROM CONTACT, CUST_CONTACT WHERE CUST_CONTACT.CONTACT_ID = CONTACT.CONTACT_ID AND CUST_CONTACT.CUSTOMER_ID=? ORDER BY CONTACT.LAST_NAME");
			pstmt.setString(idx++, customerid);

			rs = pstmt.executeQuery();

			Contact aContact = null;
			while (rs.next()) {
				aContact = new Contact();

				aContact.setContactID(rs.getString("CONTACT_ID"));
				aContact.setFirstName(rs.getString("FIRST_NAME"));
				aContact.setLastName(rs.getString("LAST_NAME"));
				aContact.setTitle(rs.getString("TITLE"));
				aContact.setEmail(rs.getString("EMAIL_ADDRESS"));
				aContact.setPHONE(rs.getString("PHONE_NUMBER"));
				aContact.setFAX(rs.getString("FAX_NUMBER"));
				aContact.setADDRESS(rs.getString("STREET_ADDRESS1"));
				aContact.setADDRESS2(rs.getString("STREET_ADDRESS2"));
				aContact.setCITY(rs.getString("CITY"));
				aContact.setSTATE(rs.getString("STATE"));
				aContact.setZIP(rs.getString("ZIP_CODE"));
				aContact.setCOUNTRY(rs.getString("COUNTRY"));
				aContact.setCustomerID(rs.getString("CUSTOMER_ID"));

				allContacts.add(aContact);
			}

		} catch (SQLException e) {
			log.error("getContacts ", e);

		} catch (NamingException e) {
			log.error("getContacts ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getContacts ", e);
			}
		}

		return allContacts;
	}

	public Collection getAllContacts() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allContacts = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"SELECT CUST_CONTACT.CUSTOMER_ID, CONTACT.* FROM CONTACT, CUST_CONTACT WHERE CUST_CONTACT.CONTACT_ID = CONTACT.CONTACT_ID");

			rs = pstmt.executeQuery();

			Contact aContact = null;
			while (rs.next()) {
				aContact = new Contact();

				aContact.setContactID(rs.getString("CONTACT_ID"));
				aContact.setFirstName(rs.getString("FIRST_NAME"));
				aContact.setLastName(rs.getString("LAST_NAME"));
				aContact.setTitle(rs.getString("TITLE"));
				aContact.setEmail(rs.getString("EMAIL_ADDRESS"));
				aContact.setPHONE(rs.getString("PHONE_NUMBER"));
				aContact.setFAX(rs.getString("FAX_NUMBER"));
				aContact.setADDRESS(rs.getString("STREET_ADDRESS1"));
				aContact.setADDRESS2(rs.getString("STREET_ADDRESS2"));
				aContact.setCITY(rs.getString("CITY"));
				aContact.setSTATE(rs.getString("STATE"));
				aContact.setZIP(rs.getString("ZIP_CODE"));
				aContact.setCOUNTRY(rs.getString("COUNTRY"));
				aContact.setCustomerID(rs.getString("CUSTOMER_ID"));

				allContacts.add(aContact);
			}

		} catch (SQLException e) {
			log.error("getContacts ", e);

		} catch (NamingException e) {
			log.error("getContacts ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getContacts ", e);
			}
		}

		return allContacts;
	}

	public boolean saveContact(Contact contact) {

		boolean result = false;

		try {

			if (findContact(contact.getContactID()) != null) {
				result = updateContact(contact);
			} else {
				result = insertContact(contact);
			}

		} catch (Exception e) {
			log.error("savecontact ", e);
		}

		return result;
	}

	private boolean updateContact(Contact contact) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"UPDATE CONTACT SET LAST_NAME=?, FIRST_NAME=?, TITLE=?, EMAIL_ADDRESS=?, PHONE_NUMBER=?, FAX_NUMBER=?, STREET_ADDRESS1=?, STREET_ADDRESS2=?, CITY=?, STATE=?, ZIP_CODE=?, COUNTRY=? WHERE CONTACT_ID=?");

			pstmt.setString(idx++, contact.getLastName());
			pstmt.setString(idx++, contact.getFirstName());
			pstmt.setString(idx++, contact.getTitle());
			pstmt.setString(idx++, contact.getEmail());
			pstmt.setString(idx++, contact.getPHONE());
			pstmt.setString(idx++, contact.getFAX());
			pstmt.setString(idx++, contact.getADDRESS());
			pstmt.setString(idx++, contact.getADDRESS2());
			pstmt.setString(idx++, contact.getCITY());
			pstmt.setString(idx++, contact.getSTATE());
			pstmt.setString(idx++, contact.getZIP());
			pstmt.setString(idx++, contact.getCOUNTRY());

			pstmt.setLong(idx++, Long.parseLong(contact.getContactID()));

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("updateContact ", e);

		} catch (NamingException e) {
			log.error("updateContact ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
				    conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("updateContact ", e);
			}
		}

		return (result == 1);
	}

	private boolean insertContact(Contact contact) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"INSERT INTO CONTACT (CONTACT_ID, FIRST_NAME, LAST_NAME, TITLE, EMAIL_ADDRESS, PHONE_NUMBER, FAX_NUMBER, STREET_ADDRESS1, STREET_ADDRESS2, CITY, STATE, ZIP_CODE, COUNTRY) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

			pstmt.setLong(idx++, Long.parseLong(contact.getContactID()));
			pstmt.setString(idx++, contact.getFirstName());
			pstmt.setString(idx++, contact.getLastName());
			pstmt.setString(idx++, contact.getTitle());
			pstmt.setString(idx++, contact.getEmail());
			pstmt.setString(idx++, contact.getPHONE());
			pstmt.setString(idx++, contact.getFAX());
			pstmt.setString(idx++, contact.getADDRESS());
			pstmt.setString(idx++, contact.getADDRESS2());
			pstmt.setString(idx++, contact.getCITY());
			pstmt.setString(idx++, contact.getSTATE());
			pstmt.setString(idx++, contact.getZIP());
			pstmt.setString(idx++, contact.getCOUNTRY());

			result = pstmt.executeUpdate();

			// CUST_CONTACT
			if (pstmt != null)
				pstmt.close();
			idx = 1;
			pstmt =	conn.prepareStatement("INSERT INTO CUST_CONTACT (CONTACT_ID, CUSTOMER_ID) VALUES(?,?)");
			pstmt.setLong(idx++, Long.parseLong(contact.getContactID()));
			pstmt.setLong(idx++, Long.parseLong(contact.getCustomerID()));

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insertContact ", e);

		} catch (NamingException e) {
			log.error("insertContact ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
				    conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insertContact ", e);
			}
		}

		return (result == 1);
	}


	public boolean deleteContact(Contact contact) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"UPDATE CONTACT SET CUSTOMER_ID=? WHERE CONTACT_ID=?");

            // DO NOT ACTUALLY DELETE CONTACT FROM CONTACT TABLE
            // store CUSTOMER_ID in CONTACT table as backup - CUSTOMER_ID field is not used
            // to relate to CUST_CONTACT table - CONTACT_ID is the key
            pstmt.setLong(idx++, Long.parseLong(contact.getCustomerID()));
			pstmt.setLong(idx++, Long.parseLong(contact.getContactID()));

			result = pstmt.executeUpdate();

			if (pstmt != null)
				pstmt.close();

			// DELET CUST_CONTACT RELATIONSHIP RECORD

			idx = 1;
			pstmt =	conn.prepareStatement("DELETE FROM CUST_CONTACT WHERE CONTACT_ID = ? AND CUSTOMER_ID = ?");
			pstmt.setLong(idx++, Long.parseLong(contact.getContactID()));
			pstmt.setLong(idx++, Long.parseLong(contact.getCustomerID()));

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("deleteContact ", e);

		} catch (NamingException e) {
			log.error("deleteContact ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
				    conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("deleteContact ", e);
			}
		}

		return (result == 1);
	}

}
