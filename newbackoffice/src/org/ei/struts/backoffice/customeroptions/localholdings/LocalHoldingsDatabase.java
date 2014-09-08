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
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.util.uid.UID;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LocalHoldingsDatabase {

    private static Log log = LogFactory.getLog("LocalHoldingsDatabase");

    public LocalHoldings createLocalHoldings() throws Exception {

        LocalHoldings aLocalHoldings = null;

        aLocalHoldings = new LocalHoldings();
        aLocalHoldings.setLocalHoldingsID(
            String.valueOf(this.getNextLocalHoldingsID()));

        return aLocalHoldings;

    }
    private long getNextLocalHoldingsID() throws SQLException {
		return UID.getNextId();
    }

    public Collection getAllLocalHoldings(
        String contractid,
        String customerid,
        Item item) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection localholdings = new ArrayList();
        LocalHoldings localholding;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;
            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM LOCAL_LINKING_OPTIONS WHERE CONTRACT_ID=? AND CUSTOMER_ID=? AND PRODUCT_NAME=?");
            pstmt.setString(idx++, contractid);
            pstmt.setString(idx++, customerid);

            String productname = item.getProduct().getName();
            if (Constants.EV2_NAME.equalsIgnoreCase(productname)) {
                productname = Constants.EV2_LONGNAME;
            }

            pstmt.setString(idx++, productname);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                localholding = new LocalHoldings();
                localholding.setLocalHoldingsID(rs.getString("INDEX_ID"));
                localholding.setLinkLabel(rs.getString("LINK_LABEL"));
                localholding.setDynamicUrl(rs.getString("DYNAMIC_URL"));
                localholding.setDefaultUrl(rs.getString("DEFAULT_URL"));
				localholding.setFutureUrl(rs.getString("FUTURE_URL"));

                localholding.setItem(item);

                localholdings.add(localholding);
            }

        } catch (SQLException e) {
            log.error("getAllLocalHoldings ", e);

        } catch (NamingException e) {
            log.error("getAllLocalHoldings ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getAllLocalHoldings ", e);
            }
        }

        return localholdings;

    }

    public LocalHoldings findLocalHoldings(String indexid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection localholdings = new ArrayList();
        LocalHoldings localholding = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;
            conn = ds.getConnection();
            pstmt =  conn.prepareStatement("SELECT * FROM LOCAL_LINKING_OPTIONS WHERE INDEX_ID=?");
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
            log.error("findLocalHolding ", e);

        } catch (NamingException e) {
            log.error("findLocalHolding ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("findLocalHolding ", e);
            }
        }

        return localholding;

    }
    public boolean saveLocalHoldings(LocalHoldings localholdings) {

        boolean result = false;

        try {

            if (findLocalHoldings(localholdings.getLocalHoldingsID())
                != null) {
                result = updateLocalHoldings(localholdings);
            } else {
                result = insertLocalHoldings(localholdings);
            }

        } catch (Exception e) {
            log.error("saveLocalHoldings ", e);
        }
        (new U_ACCESS_LocalHoldingsDatabase()).save_U_ACCESS_LocalHoldings(
            localholdings);
        return result;
    }

    private boolean updateLocalHoldings(LocalHoldings localholdings) {

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
            pstmt = conn.prepareStatement("UPDATE LOCAL_LINKING_OPTIONS SET LINK_LABEL=?, DYNAMIC_URL=?, DEFAULT_URL=?, FUTURE_URL=?, DATE_UPDATED=SYSDATE WHERE INDEX_ID=?");

            pstmt.setString(idx++, localholdings.getLinkLabel());
            pstmt.setString(idx++, localholdings.getDynamicUrl());
            pstmt.setString(idx++, localholdings.getDefaultUrl());
			pstmt.setString(idx++, localholdings.getFutureUrl());
            pstmt.setLong(idx++, Long.parseLong(localholdings.getLocalHoldingsID()));

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("updateLocalHoldings ", e);

        } catch (NamingException e) {
            log.error("updateLocalHoldings ", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null){
					conn.commit();
                    conn.close();
				}
            } catch (SQLException e) {
                log.error("updateLocalHoldings ", e);
            }
        }

        return (result == 1);
    }

    private boolean insertLocalHoldings(LocalHoldings localholdings) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt =
                conn.prepareStatement(
                    "INSERT INTO LOCAL_LINKING_OPTIONS"
                        + " (INDEX_ID, CUSTOMER_ID, CONTRACT_ID, PRODUCT_NAME, LINK_LABEL, DYNAMIC_URL, DEFAULT_URL, FUTURE_URL, DATE_CREATED, DATE_UPDATED)"
                        + " VALUES(?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)");

            String productname = localholdings.getItem().getProduct().getName();
            if (Constants.EV2_NAME.equalsIgnoreCase(productname)) {
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

        } catch (SQLException e) {
            log.error("insertLocalHoldings ", e);

        } catch (NamingException e) {
            log.error("insertLocalHoldings ", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null){
					conn.commit();
                    conn.close();
				}
            } catch (SQLException e) {
                log.error("insertLocalHoldings ", e);
            }
        }

        return (result == 1);
    }

    public boolean deleteLocalHoldings(String indexid) {

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
            pstmt = conn.prepareStatement("DELETE FROM LOCAL_LINKING_OPTIONS WHERE INDEX_ID=?");
            pstmt.setString(idx++, indexid);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("deleteLocalHoldings ", e);

        } catch (NamingException e) {
            log.error("deleteLocalHoldings ", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null){
					conn.commit();
                    conn.close();
				}
            } catch (SQLException e) {
                log.error("deleteLocalHoldings ", e);
            }
        }
        (new U_ACCESS_LocalHoldingsDatabase()).delete_U_ACCESS_LocalHoldings(
            indexid);
        return (result == 1);

    }

}
