package org.ei.struts.backoffice.customer;

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

public class CustomerDatabase {

	private static Log log = LogFactory.getLog("CustomerDatabase");

	public Customer createCustomer() throws Exception {

		Customer aCustomer = new Customer();
		aCustomer.setCustomerID(String.valueOf(this.getNextCustomerID()));
		return aCustomer;
	}

	protected long getNextCustomerID() throws SQLException {
		return UID.getNextId();
	}

	// removed deleteCustomer()!

	public Collection getCustomers() {
		return getCustomers(0, -1);
	}

	public Collection getCustomers(int min, int max) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allCustomers = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			if (max < 0) {
				pstmt = conn
						.prepareStatement("SELECT CUSTOMER_ID, CUSTOMER_NAME FROM CUSTOMER_MASTER ORDER BY CUSTOMER_NAME ASC");
			} else {
				pstmt = conn
						.prepareStatement("SELECT CUSTOMER_ID, CUSTOMER_NAME FROM ( SELECT CUSTOMER_ID, CUSTOMER_NAME, ROW_NUMBER() OVER (ORDER BY CUSTOMER_NAME) AS X FROM CUSTOMER_MASTER ORDER BY CUSTOMER_NAME ASC) WHERE X >= ? AND X <= ? ");
				pstmt.setInt(idx++, min);
				pstmt.setInt(idx++, max);
			}
			rs = pstmt.executeQuery();

			Customer aCustomer = null;
			while (rs.next()) {
				// set minimal object data - for listing purposes only
				aCustomer = new Customer();
				aCustomer.setCustomerID(rs.getString("CUSTOMER_ID"));
				aCustomer.setName(rs.getString("CUSTOMER_NAME"));

				allCustomers.add(aCustomer);
			}

		} catch (SQLException e) {
			log.error("getCustomers ", e);

		} catch (NamingException e) {
			log.error("getCustomers ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getCustomers ", e);
			}
		}

		return allCustomers;
	}

	public Customer findCustomer(String customerId) {

		log.info(customerId);

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Customer aCustomer = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM CUSTOMER_MASTER WHERE CUSTOMER_ID=?");
			pstmt.setString(idx++, customerId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				aCustomer = new Customer();

				String[] members = getConsortiumMemberIDs(customerId);
				if (members != null && members.length != 0) {
					aCustomer.setConsortium(Constants.YES);
					aCustomer.setMemberIDs(members);
				} else {
					aCustomer.setConsortium(Constants.NO);
					if (rs.getString("PARENT_COMPANY") != null) {
						String parentid = rs.getString("PARENT_COMPANY");
						// NOTE: test to see parent actually exists
						if (findCustomer(parentid) != null) {
							aCustomer.setParentID(rs
									.getString("PARENT_COMPANY"));
						}
					}
				}

				aCustomer.setCustomerID(rs.getString("CUSTOMER_ID"));
				aCustomer.setName(rs.getString("CUSTOMER_NAME"));
				aCustomer.setType(rs.getString("CUSTOMER_TYPE"));
				aCustomer.setRegion(rs.getInt("REGION_CODE"));
				// using TECH_CONTACT field for Industry code
				aCustomer.setIndustry(rs.getInt("TECH_CONTACT"));

				aCustomer.setAddress1(rs.getString("STREET_ADDRESS1"));
				aCustomer.setAddress2(rs.getString("STREET_ADDRESS2"));
				aCustomer.setCity(rs.getString("CITY"));
				aCustomer.setState(rs.getString("STATE"));
				aCustomer.setZip(rs.getString("ZIP_CODE"));
				aCustomer.setCountry(rs.getString("COUNTRY"));

				aCustomer.setPhone(rs.getString("PHONE_NUMBER"));
				aCustomer.setFax(rs.getString("FAX_NUMBER"));
				aCustomer.setOtherID(rs.getString("GOLDMINE_ID"));
				if (rs.getString("ACTIVATION_DATE") != null) {
					aCustomer.setStartDate(rs.getString("ACTIVATION_DATE"));
				}

			}

		} catch (SQLException e) {
			log.error("findCustomer ", e);

		} catch (NamingException e) {
			log.error("findCustomer ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("findCustomer ", e);
			}
		}

		return aCustomer;
	}

	public boolean saveCustomer(Customer customer) {

		boolean result = false;

		try {

			if (findCustomer(customer.getCustomerID()) != null) {
				System.out.println("updatecustomer");
				result = updateCustomer(customer);
			} else {
				System.out.println("insertcustomer");
				result = insertCustomer(customer);
			}

		} catch (Exception e) {
			log.error("savecustomer ", e);
		}

		return result;
	}

	private boolean updateCustomer(Customer customer) {

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
			// using TECH_CONTACT field for Industry code
			pstmt = conn
					.prepareStatement("UPDATE CUSTOMER_MASTER SET CUSTOMER_NAME=?, CUSTOMER_TYPE=?, REGION_CODE=?, TECH_CONTACT=?, STREET_ADDRESS1=?, STREET_ADDRESS2=?, CITY=?, STATE=?, ZIP_CODE=?, COUNTRY=?, PHONE_NUMBER=?, FAX_NUMBER=?, GOLDMINE_ID=?, PARENT_COMPANY=?, LAST_UPDATED=SYSDATE WHERE CUSTOMER_ID=?");

			pstmt.setString(idx++, customer.getName());
			pstmt.setString(idx++, customer.getType());
			pstmt.setInt(idx++, customer.getRegion());
			pstmt.setInt(idx++, customer.getIndustry()); // using TECH_CONTACT
															// field for
															// Industry code
			pstmt.setString(idx++, customer.getAddress1());
			pstmt.setString(idx++, customer.getAddress2());
			pstmt.setString(idx++, customer.getCity());
			pstmt.setString(idx++, customer.getState());
			pstmt.setString(idx++, customer.getZip());
			pstmt.setString(idx++, customer.getCountry());
			pstmt.setString(idx++, customer.getPhone());
			pstmt.setString(idx++, customer.getFax());
			pstmt.setString(idx++, customer.getOtherID());

			if (!customer.getParentID().equals("0")) {
				pstmt.setLong(idx++, Long.parseLong(customer.getParentID()));
			} else {
				pstmt.setString(idx++, null);
			}

			pstmt.setLong(idx++, Long.parseLong(customer.getCustomerID()));

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("updateCustomer ", e);

		} catch (NamingException e) {
			log.error("updateCustomer ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("updateCustomer ", e);
			}
		}

		return (result == 1);
	}

	private boolean insertCustomer(Customer customer) {

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
					.prepareStatement("INSERT INTO CUSTOMER_MASTER ("
							+ "CUSTOMER_ID, "
							+ "PARENT_COMPANY, "
							+ "CUSTOMER_NAME, "
							+ "CUSTOMER_TYPE, "
							+ "REGION_CODE, "
							+ "TECH_CONTACT, "
							+ // using TECH_CONTACT field for Industry code
							"STREET_ADDRESS1,"
							+ "STREET_ADDRESS2,"
							+ "CITY, "
							+ "STATE, "
							+ "ZIP_CODE, "
							+ "COUNTRY, "
							+ "PHONE_NUMBER, "
							+ "FAX_NUMBER, "
							+ "GOLDMINE_ID, "
							+ "ACTIVATION_DATE, LAST_UPDATED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, SYSDATE, SYSDATE)");

			pstmt.setLong(idx++, Long.parseLong(customer.getCustomerID()));
			if (!customer.getParentID().equals("0")) {
				pstmt.setLong(idx++, Long.parseLong(customer.getParentID()));
			} else {
				pstmt.setString(idx++, null);
			}
			pstmt.setString(idx++, customer.getName());
			pstmt.setString(idx++, customer.getType());
			pstmt.setInt(idx++, customer.getRegion());
			pstmt.setInt(idx++, customer.getIndustry()); // using TECH_CONTACT
															// field for
															// Industry code
			pstmt.setString(idx++, customer.getAddress1());
			pstmt.setString(idx++, customer.getAddress2());
			pstmt.setString(idx++, customer.getCity());
			pstmt.setString(idx++, customer.getState());
			pstmt.setString(idx++, customer.getZip());
			pstmt.setString(idx++, customer.getCountry());
			pstmt.setString(idx++, customer.getPhone());
			pstmt.setString(idx++, customer.getFax());
			pstmt.setString(idx++, customer.getOtherID());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insertCustomer ", e);

		} catch (NamingException e) {
			log.error("insertCustomer ", e);

		} catch (Exception e) {
			log.error("insertCustomer ", e);
		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insertCustomer ", e);
			}
		}
		System.out.println("****insertresult= " + result);
		return (result == 1);
	}

	// -- CONSORTIUM
	public Collection getConsortiums() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allCustomers = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NULL AND CUSTOMER_MASTER.CUSTOMER_ID IN (SELECT CUSTOMER_MASTER.PARENT_COMPANY FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NOT NULL) ORDER BY CUSTOMER_NAME ASC");
			rs = pstmt.executeQuery();

			Customer aCustomer = null;
			while (rs.next()) {

				allCustomers.add(findCustomer(rs.getString("CUSTOMER_ID")));

			}

		} catch (SQLException e) {
			log.error("getConsortiums ", e);

		} catch (NamingException e) {
			log.error("getConsortiums ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getConsortiums ", e);
			}
		}

		return allCustomers;
	}

	public String[] getCustomerConsortiumIDs(String customerid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection consortia = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT PARENT_COMPANY FROM CUSTOMER_MASTER WHERE CUSTOMER_ID=? AND PARENT_COMPANY IS NOT NULL ORDER BY CUSTOMER_NAME ASC");
			pstmt.setString(idx++, customerid);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				consortia.add(rs.getString("PARENT_COMPANY"));
			}

		} catch (SQLException e) {
			log.error("getCustomerConsortiumIDs ", e);

		} catch (NamingException e) {
			log.error("getCustomerConsortiumIDs ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getCustomerConsortiumIDs ", e);
			}
		}
		return (String[]) consortia.toArray(new String[] {});
	}

	// updated
	public boolean saveConsortiumMembers(String consortiumid, String[] members) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();

			for (int idxMember = 0; idxMember < members.length; idxMember++) {
				pstmt = conn
						.prepareStatement("UPDATE CUSTOMER_MASTER SET PARENT_COMPANY=? WHERE CUSTOMER_ID=?");
				idx = 1;
				pstmt.setString(idx++, consortiumid);
				pstmt.setLong(idx++, Long.parseLong(members[idxMember]));
				result = pstmt.executeUpdate();
				if (pstmt != null) {
					pstmt.close();
				}
				if (result != 1) {
					break;
				}
			}

		} catch (SQLException e) {
			log.error("saveConsortiumMembers", e);

		} catch (NamingException e) {
			log.error("saveConsortiumMembers", e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("saveConsortiumMembers", e);
			}
		}

		return (result == 1);
	}

	// updated
	public boolean deleteConsortiumMember(String consortiumid, String memberid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
					.prepareStatement("UPDATE CUSTOMER_MASTER SET PARENT_COMPANY=NULL WHERE CUSTOMER_ID=?");
			pstmt.setString(idx++, memberid);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("deleteConsortiumMember", e);

		} catch (NamingException e) {
			log.error("deleteConsortiumMember", e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("deleteConsortiumMember", e);
			}
		}

		return (result == 1);
	}

	public boolean saveConsortium(Customer consortium) {

		boolean result = false;

		try {
			log.info(" SAVING !!! " + consortium);
			(new CustomerDatabase()).saveCustomer(consortium);
			// save/update list of memebers
			result = saveConsortiumMembers(consortium.getCustomerID(),
					consortium.getMemberIDs());
		} catch (Exception e) {
			log.error("saveconsortium ", e);
		}

		return result;
	}

	public String[] getConsortiumMemberIDs(String consortiumid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection customers = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM CUSTOMER_MASTER WHERE PARENT_COMPANY=? ORDER BY CUSTOMER_NAME ASC");
			pstmt.setString(idx++, consortiumid);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				customers.add(rs.getString("CUSTOMER_ID"));
			}

		} catch (SQLException e) {
			log.error("getConsortiumMemberIDs ", e);

		} catch (NamingException e) {
			log.error("getConsortiumMemberIDs ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getConsortiumMemberIDs ", e);
			}
		}
		return (String[]) customers.toArray(new String[] {});
	}

}
