/*
 * Created on Jan 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.village;

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
import org.apache.oro.text.perl.Perl5Util;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.Options;
import org.ei.struts.backoffice.customeroptions.village.cv.Cv;
import org.ei.struts.backoffice.customeroptions.village.enc2.Enc2;
import org.ei.struts.backoffice.customeroptions.village.ev2.Ev2;
import org.ei.struts.backoffice.customeroptions.village.pv2.Pv2;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class U_ACCESS_VillageDatabase {

	private static Log log = LogFactory.getLog(U_ACCESS_VillageDatabase.class);

	/* ----------------------- EV2 ---------------------- */
	public boolean save_U_ACCESS_Ev2(Options options) {

		boolean result = false;
		Village ev2 = (Village) options;
		try {
			if (find_U_ACCESS_Ev2(ev2.getContractID(), ev2.getCustomerID()) != null) {
				log.info("Update Options");
				result = update_U_ACCESS_Ev2(ev2);
			} else {
				log.info("Insert Options");
				result = insert_U_ACCESS_Ev2(ev2);
			}

		} catch (Exception e) {
			log.error("save_U_ACCESS_Ev2 ", e);
		}

		return result;
	}

	private Village find_U_ACCESS_Ev2(String contractid, String customerid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String strCartridges = Constants.EMPTY_STRING;
		Collection carts = new ArrayList();
		Ev2 options = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM EV2_CUSTOMER_OPTION WHERE CONTRACT_ID=? AND CUST_ID=?");
			pstmt.setString(idx++, contractid);
			pstmt.setString(idx++, customerid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				strCartridges = rs.getString("CARTRIDGE");
				options = new Ev2();

				if (strCartridges != null) {
					Perl5Util perl = new Perl5Util();
					perl.split(carts, "#;#", strCartridges);
				}

				options.setSelectedOptions((String[]) carts
						.toArray(new String[] {}));
				options.setDefaultDatabase(rs.getString("DEFAULTDB"));
				options.setStartCID(rs.getString("STARTPAGE"));
				options.setReferenceServicesLink(rs.getString("REFEMAIL"));

			}
		} catch (SQLException e) {
			log.error("find_U_ACCESS_Ev2 ", e);

		} catch (NamingException e) {
			log.error("find_U_ACCESS_Ev2 ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("find_U_ACCESS_Ev2 ", e);
			}
		}

		return options;

	}

	private boolean update_U_ACCESS_Ev2(Village ev2) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("UPDATE EV2_CUSTOMER_OPTION SET CARTRIDGE=?, DEFAULTDB=?, STARTPAGE=?, REFEMAIL=?  WHERE CONTRACT_ID=? AND CUST_ID=?");

			pstmt.setString(idx++, ev2.joinOptions());
			pstmt.setString(idx++, ((Ev2) ev2).getDefaultDatabase());
			pstmt.setString(idx++, ((Ev2) ev2).getStartCID());
			pstmt.setString(idx++, ((Ev2) ev2).getReferenceServicesLink());
			pstmt.setLong(idx++, Long.parseLong(ev2.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(ev2.getCustomerID()));
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("update_U_ACCESS_Ev2 ", e);

		} catch (NamingException e) {
			log.error("update_U_ACCESS_Ev2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("update_U_ACCESS_Ev2 ", e);
			}
		}

		return (result == 1);
	}

	private boolean insert_U_ACCESS_Ev2(Village ev2) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO EV2_CUSTOMER_OPTION (CONTRACT_ID, CUST_ID, CARTRIDGE, DEFAULTDB, STARTPAGE, REFEMAIL) VALUES(?,?,?,?,?,?)");
			pstmt.setLong(idx++, Long.parseLong(ev2.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(ev2.getCustomerID()));
			pstmt.setString(idx++, ev2.joinOptions());
			pstmt.setString(idx++, ((Ev2) ev2).getDefaultDatabase());
			pstmt.setString(idx++, ((Ev2) ev2).getStartCID());
			pstmt.setString(idx++, ((Ev2) ev2).getReferenceServicesLink());
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insert_U_ACCESS_Ev2 ", e);

		} catch (NamingException e) {
			log.error("insert_U_ACCESS_Ev2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insert_U_ACCESS_Ev2 ", e);
			}
		}

		return (result == 1);
	}

	/* ----------------------- CV ---------------------- */

	public boolean save_U_ACCESS_Cv(Options options) {

		boolean result = false;
		Village cv = (Village) options;
		try {
			if (find_U_ACCESS_Cv(cv.getContractID(), cv.getCustomerID()) != null) {
				result = update_U_ACCESS_Cv(cv);
			} else {
				result = insert_U_ACCESS_Cv(cv);
			}

		} catch (Exception e) {
			log.error("save_U_ACCESS_Cv ", e);
		}

		return result;
	}

	private Village find_U_ACCESS_Cv(String contractid, String customerid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String strCartridges = Constants.EMPTY_STRING;
		Collection carts = new ArrayList();
		Village options = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM CUSTOMER_OPTION_NEWCHV WHERE CONTRACT_ID=? AND CUST_ID=? AND PRODUCT_ID=?");
			pstmt.setString(idx++, contractid);
			pstmt.setString(idx++, customerid);
			pstmt.setString(idx++, Constants.CV);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				options = new Cv();
				strCartridges = rs.getString("CARTRIDGE");

				if (strCartridges != null) {
					Perl5Util perl = new Perl5Util();
					perl.split(carts, "#;#", strCartridges);
					log.info("Found CV" + carts);
				}

				options.setSelectedOptions((String[]) carts
						.toArray(new String[] {}));

			}

		} catch (SQLException e) {
			log.error("find_U_ACCESS_Cv ", e);

		} catch (NamingException e) {
			log.error("find_U_ACCESS_Cv ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("find_U_ACCESS_Cv ", e);
			}
		}

		return options;

	}

	private boolean update_U_ACCESS_Cv(Village cv) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("UPDATE CUSTOMER_OPTION_NEWCHV SET CARTRIDGE=? WHERE CONTRACT_ID=? AND CUST_ID=? AND PRODUCT_ID=?");

			pstmt.setString(idx++, cv.joinOptions());
			pstmt.setLong(idx++, Long.parseLong(cv.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(cv.getCustomerID()));
			pstmt.setString(idx++, Constants.CV);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("update_U_ACCESS_Cv ", e);

		} catch (NamingException e) {
			log.error("update_U_ACCESS_Cv ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("update_U_ACCESS_Cv ", e);
			}
		}

		return (result == 1);
	}

	private boolean insert_U_ACCESS_Cv(Village cv) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO CUSTOMER_OPTION_NEWCHV (CONTRACT_ID, CUST_ID, CARTRIDGE, PRODUCT_ID, PRODUCT_NAME, INDEX_ID) VALUES(?,?,?,?,?,?)");
			pstmt.setLong(idx++, Long.parseLong(cv.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(cv.getCustomerID()));
			pstmt.setString(idx++, cv.joinOptions());
			pstmt.setString(idx++, cv.getProduct());
			pstmt.setString(idx++, Constants.CV_NAME.toUpperCase());
			pstmt.setLong(idx++, 0);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insert_U_ACCESS_Cv ", e);

		} catch (NamingException e) {
			log.error("insert_U_ACCESS_Cv ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insert_U_ACCESS_Cv ", e);
			}
		}

		return (result == 1);
	}

	/* ----------------------- PV2 ---------------------- */

	public boolean save_U_ACCESS_Pv2(Options options) {

		boolean result = false;
		Village vill = (Village) options;
		try {
			if (find_U_ACCESS_Pv2(vill.getContractID(), vill.getCustomerID()) != null) {
				result = update_U_ACCESS_Pv2(vill);
			} else {
				result = insert_U_ACCESS_Pv2(vill);
			}

		} catch (Exception e) {
			log.error("save_U_ACCESS_Pv2 ", e);
		}

		return result;
	}

	private Village find_U_ACCESS_Pv2(String contractid, String customerid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String strCartridges = Constants.EMPTY_STRING;
		Collection carts = new ArrayList();
		Village options = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM PV2_CUSTOMER_OPTION WHERE CONTRACT_ID=? AND CUST_ID=?");
			pstmt.setString(idx++, contractid);
			pstmt.setString(idx++, customerid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				strCartridges = rs.getString("CARTRIDGE");
				options = new Pv2();

				if (strCartridges != null) {
					Perl5Util perl = new Perl5Util();
					perl.split(carts, "#;#", strCartridges);
				}
				options.setSelectedOptions((String[]) carts
						.toArray(new String[] {}));
			}
		} catch (SQLException e) {
			log.error("find_U_ACCESS_Pv2 ", e);

		} catch (NamingException e) {
			log.error("find_U_ACCESS_Pv2 ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("find_U_ACCESS_Pv2 ", e);
			}
		}

		return options;

	}

	private boolean update_U_ACCESS_Pv2(Village vill) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("UPDATE PV2_CUSTOMER_OPTION SET CARTRIDGE=? WHERE CONTRACT_ID=? AND CUST_ID=?");

			pstmt.setString(idx++, vill.joinOptions());
			pstmt.setLong(idx++, Long.parseLong(vill.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(vill.getCustomerID()));
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("update_U_ACCESS_Pv2 ", e);

		} catch (NamingException e) {
			log.error("update_U_ACCESS_Pv2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("update_U_ACCESS_Pv2 ", e);
			}
		}

		return (result == 1);
	}

	private boolean insert_U_ACCESS_Pv2(Village vill) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO PV2_CUSTOMER_OPTION (CONTRACT_ID, CUST_ID, CARTRIDGE) VALUES(?,?,?)");
			pstmt.setLong(idx++, Long.parseLong(vill.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(vill.getCustomerID()));
			pstmt.setString(idx++, vill.joinOptions());
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insert_U_ACCESS_Pv2 ", e);

		} catch (NamingException e) {
			log.error("insert_U_ACCESS_Pv2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insert_U_ACCESS_Pv2 ", e);
			}
		}

		return (result == 1);
	}

	/* ----------------------- ENC2 ---------------------- */

	public boolean save_U_ACCESS_Enc2(Options options) {

		boolean result = false;
		Village vill = (Village) options;
		try {
			if (find_U_ACCESS_Enc2(vill.getContractID(), vill.getCustomerID()) != null) {
				result = update_U_ACCESS_Enc2(vill);
			} else {
				result = insert_U_ACCESS_Enc2(vill);
			}

		} catch (Exception e) {
			log.error("save_U_ACCESS_Enc2 ", e);
		}

		return result;
	}

	private Village find_U_ACCESS_Enc2(String contractid, String customerid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection carts = new ArrayList();
		Collection bulletins = new ArrayList();
		Village options = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM ENC_CUSTOMER_OPTION WHERE CONTRACT_ID=? AND CUST_ID=?");
			pstmt.setString(idx++, contractid);
			pstmt.setString(idx++, customerid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				options = new Enc2();

				String strCartridges = rs.getString("CARTRIDGE");
				if (strCartridges != null) {
					Perl5Util perl = new Perl5Util();
					perl.split(carts, "#;#", strCartridges);
				}
				options.setSelectedOptions((String[]) carts
						.toArray(new String[] {}));
				((Enc2) options).setLitbulletins((String[]) carts
						.toArray(new String[] {}));
				((Enc2) options).setPatbulletins((String[]) carts
						.toArray(new String[] {}));

				((Enc2) options).setDefaultDatabase(rs.getString("DEFAULTDB"));
				((Enc2) options).setStartCID(rs.getString("STARTPAGE"));

			}
		} catch (SQLException e) {
			log.error("find_U_ACCESS_Enc2 ", e);

		} catch (NamingException e) {
			log.error("find_U_ACCESS_Enc2 ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("find_U_ACCESS_Enc2 ", e);
			}
		}

		return options;

	}

	private boolean update_U_ACCESS_Enc2(Village vill) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("UPDATE ENC_CUSTOMER_OPTION SET CARTRIDGE=?, DEFAULTDB=?, STARTPAGE=? WHERE CONTRACT_ID=? AND CUST_ID=?");

			pstmt.setString(idx++, vill.joinOptions());
			pstmt.setString(idx++, ((Enc2) vill).getDefaultDatabase());
			pstmt.setString(idx++, ((Enc2) vill).getStartCID());

			pstmt.setLong(idx++, Long.parseLong(vill.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(vill.getCustomerID()));
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("update_U_ACCESS_Enc2 ", e);

		} catch (NamingException e) {
			log.error("update_U_ACCESS_Enc2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("update_U_ACCESS_Enc2 ", e);
			}
		}

		return (result == 1);
	}

	private boolean insert_U_ACCESS_Enc2(Village vill) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx
					.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx
					.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("INSERT INTO ENC_CUSTOMER_OPTION (CONTRACT_ID, CUST_ID, CARTRIDGE, DEFAULTDB, STARTPAGE) VALUES(?,?,?,?,?)");
			pstmt.setLong(idx++, Long.parseLong(vill.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(vill.getCustomerID()));
			pstmt.setString(idx++, vill.joinOptions());
			pstmt.setString(idx++, ((Enc2) vill).getDefaultDatabase());
			pstmt.setString(idx++, ((Enc2) vill).getStartCID());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insert_U_ACCESS_Enc2 ", e);

		} catch (NamingException e) {
			log.error("insert_U_ACCESS_Enc2 ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null) {
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insert_U_ACCESS_Enc2 ", e);
			}
		}

		return (result == 1);
	}

}
