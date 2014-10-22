/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/access/U_ACCESS_Database.java-arc   1.0   Jan 14 2008 17:10:36   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:36  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.credentials.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.credentials.Credentials;
import org.ei.struts.backoffice.credentials.ip.Ip;
import org.ei.struts.backoffice.product.Product;
import org.ei.struts.backoffice.product.ProductDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $$
 */
public class U_ACCESS_Database {

	private static Log log = LogFactory.getLog("U_ACCESS_Database");
	// --------------------------------------------------------------------------

	public int getNextAccessID() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int intNextAccessID = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);

			conn = ds.getConnection();
			pstmt =
				conn.prepareStatement(
					"SELECT SEQ_CONTRACT_ID.NEXTVAL FROM DUAL");

			rs = pstmt.executeQuery();

			if (rs.next()) {
				intNextAccessID = rs.getInt(1);
			}

		} catch (SQLException e) {
			log.error("getNextAccessID", e);

		} catch (NamingException e) {
			log.error("getNextAccessID", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getNextAccessID", e);
			}
		}

		return intNextAccessID;
	}

	public boolean save_U_ACCESS(Access saved, Access updated) {

		boolean result = false;
		boolean update = false;

		if (saved != null) {
			update = true;
		}

		Collection entitlements = new ArrayList();

		log.info(" COUNT" + entitlements.size());
		Iterator itrEnt = entitlements.iterator();
		Item ent = null;

		while (itrEnt.hasNext()) {
			ent = (Item) itrEnt.next();

			Product prd =
				(new ProductDatabase()).findProduct(ent.getProductID());
			if (prd == null) {
				prd = new Product();
				prd.setProductID(Constants.USAGE_ID);
				prd.setName(Constants.USAGE_NAME);
			}

			if (update) {

				log.info(" UPDATING U_ACCESS RECORD ");
			} else {
				log.info(" INSERTING U_ACCESS RECORD ");
			}
			log.info(" RESULT = " + result);
		} // while

		return result;

	}

	// --------------------------------------------------------------------------

	private boolean insert_U_ACCESS(
		String strContractID,
		String strCustomerID,
		Access access,
		String strProductID,
		String strProductName) {

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
			String strQuery = null;

			conn = ds.getConnection();

			pstmt = conn.prepareStatement(strQuery);

			pstmt.setString(idx++, strContractID);
			pstmt.setString(idx++, strCustomerID);

			Credentials creds = new Ip(); //access.getCredentials();

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("insert_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("insert_U_ACCESS ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("insert_U_ACCESS ", e);
			}
		}

		return (result == 1);
	}
	// --------------------------------------------------------------------------
	private boolean update_U_ACCESS(
		String strContractID,
		String strCustomerID,
		Credentials updated,
		Credentials saved,
		String strProductID,
		String strProductName) {

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
			String strQuery = null;

			conn = ds.getConnection();

			Credentials updatedCreds = updated;
			Credentials savedCreds = saved;

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("update_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("update_U_ACCESS ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("update_U_ACCESS ", e);
			}
		}

		return (result == 1);
	}

	// --------------------------------------------------------------------------
	public boolean deleteAccess_U_ACCESS(Access access) {

		boolean result = false;

		Collection entitlements = new ArrayList();

		log.info(" COUNT" + entitlements.size());
		Iterator itrEnt = entitlements.iterator();
		Item ent = null;

		while (itrEnt.hasNext()) {
			ent = (Item) itrEnt.next();

			Product prd =
				(new ProductDatabase()).findProduct(ent.getProductID());
			if (prd == null) {
				prd = new Product();
				prd.setProductID(Constants.USAGE_ID);
				prd.setName(Constants.USAGE_NAME);
			}

		} // while

		return result;

	}

	// --------------------------------------------------------------------------
	public boolean assignToEntitlement_U_ACCESS(
		String contractid,
		String contactid) {

		boolean result = false;

		Contract contract = (new ContractDatabase()).findContract(contractid);

		return result;

	}

	public boolean deleteEntitlement_U_ACCESS(String contractid) {

		boolean result = false;

		Contract contract = (new ContractDatabase()).findContract(contractid);

		return result;

	}

	// --------------------------------------------------------------------------
	private boolean delete_U_ACCESS(
		String strContractID,
		String strCustomerID,
		Access access,
		String strProductID,
		String strProductName) {

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
			String strQuery = null;

			conn = ds.getConnection();

		} catch (SQLException e) {
			log.error("delete_U_ACCESS ", e);

		} catch (NamingException e) {
			log.error("delete_U_ACCESS ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("delete_U_ACCESS ", e);
			}
		}

		return (result == 1);
	}

}
