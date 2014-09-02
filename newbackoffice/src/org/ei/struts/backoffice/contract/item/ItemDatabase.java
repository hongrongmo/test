/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/item/ItemDatabase.java-arc   1.0   Jan 14 2008 17:10:32   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:32  $
 *
 */
package org.ei.struts.backoffice.contract.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.contract.Contract;

public class ItemDatabase {

    private static Log log = LogFactory.getLog("ItemDatabase");

	public Item createItem(String contractid) {

		Item aItem = null;

		aItem = new Item();

		aItem.setItemID(String.valueOf(this.getNextItemID(contractid)));
		aItem.getContract().setContractID(contractid);

		return aItem ;

	}

	// This does not use the generic sequence generator
	// ALSO: We now do an ABS on the ITEM id column so that we do not reuse ITEM id numbers.
	// Item deletion sets the ITEM to be negative.  We may need to undelete,
	// so we cannot reuse id numbers within the same contract.
	// ABS() will prevent 1 from being used when a -1 exists, and so on
	private long getNextItemID(String contractid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long lngItemID = 1;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT MAX(ABS(ITEM))+1 FROM CONTRACT WHERE CONTRACT_NUMBER=?");
			pstmt.setString(idx++, contractid);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				lngItemID = rs.getLong(1);
			}

			// make sure we do not return 0 or negative id number
			if(lngItemID <= 0){
				lngItemID = 1;
			}

		} catch(SQLException e) {
			log.error("getNextItemID ",e);

		} catch(NamingException e) {
			log.error("getNextItemID ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("getNextItemID ",e);
			}
		}

		return lngItemID;
	}

	public Item findItem(String contractid, String itemid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Item aItem = null;


		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM CONTRACT WHERE CONTRACT_NUMBER=? AND ITEM=?");
			pstmt.setString(idx++, contractid);
			pstmt.setString(idx++, itemid);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				aItem = new Item();
				aItem.setItemID(rs.getString("ITEM"));
				aItem.setProductID(rs.getString("PRODUCT_ID"));
				aItem.setNotes(rs.getString("NOTES"));

				aItem.getContract().setCustomerID(rs.getString("CUSTOMER_ID"));
				aItem.getContract().setRenewalRefID(rs.getString("RENEWAL_REF"));
				aItem.getContract().setContractID(rs.getString("CONTRACT_NUMBER"));
				aItem.getContract().setStatus(rs.getString("CNVT_STATUS"));
				aItem.getContract().setSiteLicense(rs.getString("ACCESS_METHOD"));
				aItem.getContract().setContractType(rs.getString("CONTRACT_TYPE"));
				aItem.getContract().setAccessType(rs.getString("STATUS"));

				aItem.getContract().setStartdate(rs.getString("START_DATE"));
				aItem.getContract().setEnddate(rs.getString("END_DATE"));
				aItem.getContract().setContractStartdate(rs.getString("CONTRACT_DATE"));
				aItem.getContract().setContractEnddate(rs.getString("CONTRACT_END_DATE"));
			}
		} catch(SQLException e) {
			log.error("findItem ",e);

		} catch(NamingException e) {
			log.error("findItem ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("findItem ",e);
			}
		}

		return aItem;
	}

    // added test to only return contract items whose item # is > 0
	public Collection getItems(String contractid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allItems = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM CONTRACT WHERE CONTRACT_NUMBER=? AND ITEM > 0 ORDER BY ITEM ASC");
			pstmt.setString(idx++, contractid);

			rs = pstmt.executeQuery();

			Item aItem = null;
			while(rs.next()) {
				aItem = new Item();
				aItem.setItemID(rs.getString("ITEM"));
				aItem.setProductID(rs.getString("PRODUCT_ID"));
				aItem.setNotes(rs.getString("NOTES"));

				aItem.getContract().setCustomerID(rs.getString("CUSTOMER_ID"));
				aItem.getContract().setContractID(rs.getString("CONTRACT_NUMBER"));
				aItem.getContract().setRenewalRefID(rs.getString("RENEWAL_REF"));
				aItem.getContract().setStatus(rs.getString("CNVT_STATUS"));
				aItem.getContract().setSiteLicense(rs.getString("ACCESS_METHOD"));
				aItem.getContract().setContractType(rs.getString("CONTRACT_TYPE"));
				aItem.getContract().setAccessType(rs.getString("STATUS"));

				aItem.getContract().setStartdate(rs.getString("START_DATE"));
				aItem.getContract().setEnddate(rs.getString("END_DATE"));
				aItem.getContract().setContractStartdate(rs.getString("CONTRACT_DATE"));
				aItem.getContract().setContractEnddate(rs.getString("CONTRACT_END_DATE"));

				allItems.add(aItem);
			}

		} catch(SQLException e) {
			log.error("getItems ",e);

		} catch(NamingException e) {
			log.error("getItems ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("getItems  ",e);
			}
		}

		return allItems;
	}

	// get ITEM IDs that are children of this contRactid
	// use above method to get Items from database to avoid code
	// duplication and copy out item ids from collection
	public String[] getContractItemIDs(String contractid) {

		Collection items = new ArrayList();
		Collection wholeItems = this.getItems(contractid);

		Iterator itrItems = wholeItems.iterator();
		while(itrItems.hasNext()) {
			items.add( ((Item)itrItems.next()).getItemID());
		}
		return (String[]) items.toArray(new String[]{});
	}

	public boolean saveItem(Item item) {

		boolean result = false;

		try {

			if(findItem(item.getContract().getContractID(), item.getItemID()) != null) {
				log.info("saveItem ");
				result = updateItem(item);
			} else {
				result = insertItem(item);
			}

		} catch(Exception e) {
			log.error("saveItem ",e);
		}

		return result;
	}


	private boolean updateItem(Item item) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("UPDATE CONTRACT SET PRODUCT_ID=?, NOTES=?, START_DATE=?, END_DATE=?, CONTRACT_DATE=?, CONTRACT_END_DATE=?, CNVT_STATUS=?, STATUS=?, ACCESS_METHOD=?, CONTRACT_TYPE=?, LAST_UPDATED=SYSDATE WHERE CONTRACT_NUMBER =? AND ITEM=?");

			Contract contract = item.getContract();
			Date aDate;

			pstmt.setString(idx++, item.getProductID());
			pstmt.setString(idx++, item.getNotes());
			aDate = contract.getStartdate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getEnddate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getContractStartdate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getContractEnddate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			pstmt.setString(idx++, contract.getStatus());
			pstmt.setString(idx++, contract.getAccessType());
			pstmt.setString(idx++, contract.getSiteLicense());
			pstmt.setString(idx++, contract.getContractType());

			pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(item.getItemID()));

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("updateItem ",e);

		} catch(NamingException e) {
			log.error("updateItem ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch(SQLException e) {
				log.error("updateItem ",e);
			}
		}

		return (result == 1);
	}

	private boolean insertItem(Item item) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO CONTRACT (ITEM, CUSTOMER_ID, CONTRACT_NUMBER, RENEWAL_REF, PRODUCT_ID, NOTES, START_DATE, END_DATE, CONTRACT_DATE, CONTRACT_END_DATE, CNVT_STATUS, STATUS, ACCESS_METHOD, CONTRACT_TYPE, LAST_UPDATED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE)");

			Contract contract = item.getContract();
			Date aDate;

			pstmt.setLong(idx++, Long.parseLong(item.getItemID()));
			pstmt.setString(idx++, contract.getCustomerID());
			pstmt.setString(idx++, contract.getContractID());
			if(contract.getRenewalRefID() != "0") {
				pstmt.setString(idx++, contract.getRenewalRefID());
			}
			else {
				pstmt.setString(idx++, null);
			}
			pstmt.setString(idx++, item.getProductID());
			pstmt.setString(idx++, item.getNotes());
			aDate = contract.getStartdate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getEnddate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getContractStartdate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			aDate = contract.getContractEnddate().getDate();
			pstmt.setDate(idx++, (aDate != null) ? new java.sql.Date(aDate.getTime()) : null);
			pstmt.setString(idx++, contract.getStatus());
			pstmt.setString(idx++, contract.getAccessType());
			pstmt.setString(idx++, contract.getSiteLicense());
			pstmt.setString(idx++, contract.getContractType());

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("insertItem ",e);

		} catch(NamingException e) {
			log.error("insertItem ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch(SQLException e) {
				log.error("insertItem ",e);
			}
		}

		return (result == 1);
	}

    // we do not actually delete the contract item
    // we just set the contract item number to be negative
    // so that it no longer appears in the screens, etc.
	public boolean deleteItem(Item item) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("UPDATE CONTRACT SET ITEM=?, NOTES=?, LAST_UPDATED=SYSDATE WHERE CONTRACT_NUMBER =? AND ITEM=?");

			Contract contract = item.getContract();

			// make sure this is set to negative value (just in case)
			pstmt.setLong(idx++, -1 * Math.abs(Long.parseLong(item.getItemID())));
			pstmt.setString(idx++, item.getNotes());

			pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(item.getItemID()));

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("deleteItem ",e);

		} catch(NamingException e) {
			log.error("deleteItem ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch(SQLException e) {
				log.error("deleteItem ",e);
			}
		}

		return (result == 1);
	}

}
