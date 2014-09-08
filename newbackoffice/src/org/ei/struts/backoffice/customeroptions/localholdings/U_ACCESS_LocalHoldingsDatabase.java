/*
 * Created on Jan 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.localholdings;

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
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class U_ACCESS_LocalHoldingsDatabase {

	private static Log log = LogFactory.getLog("U_ACCESS_LocalHoldingsDatabase");

	public LocalHoldings find_U_ACCESS_LocalHoldings(String indexid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection localholdings = new ArrayList();
		LocalHoldings localholding =  null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =	(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM LOCAL_LINKING_OPTIONS WHERE INDEX_ID=?");
			pstmt.setString(idx++, indexid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				localholding = new LocalHoldings();
				localholding.setLocalHoldingsID(rs.getString("INDEX_ID"));
				localholding.setLinkLabel(rs.getString("LINK_LABEL"));
				localholding.setDynamicUrl(rs.getString("DYNAMIC_URL"));
				localholding.setDefaultUrl(rs.getString("DEFAULT_URL"));
				localholding.setFutureUrl(rs.getString("FUTURE_URL"));

			}

		} catch (SQLException e) {
			log.error("find_U_ACCESS_LocalHolding ", e);

		} catch (NamingException e) {
			log.error("find_U_ACCESS_LocalHolding ", e);

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("find_U_ACCESS_LocalHolding ", e);
			}
		}

		return localholding;

	}
	public boolean save_U_ACCESS_LocalHoldings(LocalHoldings localholdings) {

	  boolean result = false;

	  try {

		if(find_U_ACCESS_LocalHoldings(localholdings.getLocalHoldingsID()) != null) {
		  result = update_U_ACCESS_LocalHoldings(localholdings);
		} else {
		  result = insert_U_ACCESS_LocalHoldings(localholdings);
		}

	  } catch(Exception e) {
		log.error("save_U_ACCESS_LocalHoldings ",e);
	  }

	  return result;
	}

	private boolean update_U_ACCESS_LocalHoldings(LocalHoldings localholdings) {

	  Connection conn = null;
	  PreparedStatement pstmt = null;
	  int result = 0;

	  try {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
		DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
		int idx = 1;

		conn = ds.getConnection();
		pstmt = conn.prepareStatement("UPDATE LOCAL_LINKING_OPTIONS SET LINK_LABEL=?, DYNAMIC_URL=?, DEFAULT_URL=?, FUTURE_URL=?, DATE_UPDATED=SYSDATE WHERE INDEX_ID=?");

		pstmt.setString(idx++, localholdings.getLinkLabel());
		pstmt.setString(idx++, localholdings.getDynamicUrl());
		pstmt.setString(idx++, localholdings.getDefaultUrl());
		pstmt.setString(idx++, localholdings.getFutureUrl());
		pstmt.setLong(idx++, Long.parseLong(localholdings.getLocalHoldingsID()));

		result = pstmt.executeUpdate();

	  } catch(SQLException e) {
		log.error("update_U_ACCESS_LocalHoldings ",e);

	  } catch(NamingException e) {
		log.error("update_U_ACCESS_LocalHoldings ",e);

	  } finally {

		try {
		  if(pstmt != null)
			pstmt.close();
		  if(conn != null){
			  conn.commit();
			conn.close();
		}
		} catch(SQLException e) {
		  log.error("update_U_ACCESS_LocalHoldings ",e);
		}
	  }

	  return (result == 1);
	}

	private boolean insert_U_ACCESS_LocalHoldings(LocalHoldings localholdings) {

	  Connection conn = null;
	  PreparedStatement pstmt = null;
	  int result = 0;

	  try {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
		DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
		int idx = 1;

		conn = ds.getConnection();
		pstmt = conn.prepareStatement("INSERT INTO LOCAL_LINKING_OPTIONS" +
			" (INDEX_ID, CUSTOMER_ID, CONTRACT_ID, PRODUCT_NAME, LINK_LABEL, DYNAMIC_URL, DEFAULT_URL, FUTURE_URL, DATE_CREATED, DATE_UPDATED)" +
			" VALUES(?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)");

		String productname = localholdings.getItem().getProduct().getName();
		if(Constants.EV2_NAME.equalsIgnoreCase(productname)) {
			productname = Constants.EV2_LONGNAME;
		}

		pstmt.setLong(idx++, Long.parseLong(localholdings.getLocalHoldingsID()));
		pstmt.setString(idx++, localholdings.getItem().getContract().getCustomerID());
		pstmt.setString(idx++, localholdings.getItem().getContract().getContractID());
		pstmt.setString(idx++, productname);
		pstmt.setString(idx++, localholdings.getLinkLabel());
		pstmt.setString(idx++, localholdings.getDynamicUrl());
		pstmt.setString(idx++, localholdings.getDefaultUrl());
		pstmt.setString(idx++, localholdings.getFutureUrl());

		result = pstmt.executeUpdate();

	  } catch(SQLException e) {
		log.error("insert_U_ACCESS_LocalHoldings ",e);

	  } catch(NamingException e) {
		log.error("insert_U_ACCESS_LocalHoldings ",e);

	  } finally {

		try {
		  if(pstmt != null)
			pstmt.close();
		  if(conn != null){
		    conn.commit();
			conn.close();
		  }
		} catch(SQLException e) {
		  log.error("insert_U_ACCESS_LocalHoldings ",e);
		}
	  }

	  return (result == 1);
	}

	public boolean delete_U_ACCESS_LocalHoldings(String indexid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =	(DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;
			conn = ds.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM LOCAL_LINKING_OPTIONS WHERE INDEX_ID=?");
			pstmt.setString(idx++, indexid);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("delete_U_ACCESS_LocalHoldings ", e);

		} catch (NamingException e) {
			log.error("delete_U_ACCESS_LocalHoldings ", e);

		} finally {

			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {
				log.error("delete_U_ACCESS_LocalHoldings ", e);
			}
		}

		return (result == 1);

	}

}
