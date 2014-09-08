/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/ContractDatabase.java-arc   1.0   Jan 14 2008 17:10:28   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:28  $
 *
 */
package org.ei.struts.backoffice.contract;

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
import org.ei.struts.backoffice.contract.item.ItemDatabase;
import org.ei.struts.backoffice.util.uid.UID;

public class ContractDatabase {

  private static Log log = LogFactory.getLog("ContractDatabase");

    public Contract createContract() throws Exception {

        Contract aContract = null;
        aContract = new Contract();
        return aContract ;

    }

    private long getNextContractID()  throws SQLException {
        return UID.getNextId();
    }

// jam 4/2007 BUG was here - WHERE clause in query was testing for 'NOT(ITEM= -1)', it sould have been 'ITEM > 0'
// deleted CONRACT recrods have their ITEM number set to number * -1, so it is possible to have ITEM=-2, ITEM=-3 ,etc.
// Si in some cases this mtheod was returning a deleted contract item and using it to populate the start and end dates of the edit Contract form
// This method picks the first contract record and returns its values
    public Contract findContract(String contractid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Contract aContract = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM CONTRACT WHERE CONTRACT_NUMBER=? AND ITEM > 0 ");
            pstmt.setString(idx++, contractid);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                aContract = new Contract();
                aContract.setContractID(contractid);
                aContract.setCustomerID(rs.getString("CUSTOMER_ID"));

                // Lookup Contract_Data value to set enabled Y/N
                Contract contractData = findContractData(rs.getString("CONTRACT_NUMBER"));
                if(contractData != null) {
                    aContract.setEnabled(contractData.getEnabled());
                }
                else {
                    aContract.setEnabled(Constants.DISABLED);
                }

                aContract.setRenewalRefID(rs.getString("RENEWAL_REF"));
                aContract.setStatus(rs.getString("CNVT_STATUS"));
                aContract.setSiteLicense(rs.getString("ACCESS_METHOD"));
                aContract.setContractType(rs.getString("CONTRACT_TYPE"));
                aContract.setAccessType(rs.getString("STATUS"));

                aContract.setStartdate(rs.getString("START_DATE"));
                aContract.setEnddate(rs.getString("END_DATE"));
                aContract.setContractStartdate(rs.getString("CONTRACT_DATE"));
                aContract.setContractEnddate(rs.getString("CONTRACT_END_DATE"));

                aContract.setItemIDs(new ItemDatabase().getContractItemIDs(aContract.getContractID()));
//              if(aContract.getItemIDs() != null && aContract.getItemIDs().length != 0) {
//                  String str_itemID = aContract.getItemIDs()[0];
//                  Item item = (new ItemDatabase()).findItem(aContract.getContractID(), str_itemID);
//
//                  aContract.setStartDate(item.getContract().getStartDate());
//                  aContract.setEndDate(item.getContract().getEndDate());
//                  aContract.setContractStartDate(item.getContract().getContractStartDate());
//                  aContract.setContractEndDate(item.getContract().getContractEndDate());
//
//                  aContract.setRenewalRefID(item.getContract().getRenewalRefID());
//
//                  aContract.setSiteLicense(item.getContract().getSiteLicense());
//                  aContract.setStatus(item.getContract().getStatus());
//                  aContract.setAccessType(item.getContract().getAccessType());
//                  aContract.setContractType(item.getContract().getContractType());
//              }

            }
        } catch(SQLException e) {
            log.error("findContract ",e);

        } catch(NamingException e) {
            log.error("findContract ",e);

        } finally {

            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
                if(conn != null)
                    conn.close();
            } catch(SQLException e) {
                log.error("findContract ",e);
            }
        }

        return aContract;
    }


    public Collection getContracts(String customerid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection allContracts = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();

            //pstmt = conn.prepareStatement("SELECT * FROM CONTRACT_DATA WHERE CUST_ID=? ORDER BY CONTRACT_ID DESC");
            pstmt = conn.prepareStatement("SELECT UNIQUE(CONTRACT_NUMBER) FROM CONTRACT WHERE CUSTOMER_ID=? ORDER BY CONTRACT_NUMBER DESC");

            pstmt.setString(idx++, customerid);

            rs = pstmt.executeQuery();

            Contract aContract = null;
            while(rs.next()) {

                aContract = findContract(rs.getString("CONTRACT_NUMBER"));
                if(aContract != null) {
                    allContracts.add(aContract);
                }

            }

        } catch(SQLException e) {
            log.error("getContracts ",e);

        } catch(NamingException e) {
            log.error("getContracts ",e);

        } finally {

            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
                if(conn != null)
                    conn.close();
            } catch(SQLException e) {
                log.error("getContracts ",e);
            }
        }

        return allContracts;
    }

    public Contract saveContract(Contract contract) {

        Contract result = null;

        try {

            if(findContractData(contract.getContractID()) != null) {
                result = updateContractData(contract);
            } else {
                result = insertContractData(contract);
            }

        } catch(Exception e) {
            log.error("savecontract ",e);
        }
        (new U_ACCESS_ContractDatabase()).saveContract(contract);
        return result;
    }

    private Contract findContractData(String contractid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Contract aContract = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM CONTRACT_DATA WHERE CONTRACT_ID=?");
            pstmt.setString(idx++, contractid);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                aContract = new Contract();
                aContract.setContractID(contractid);
                aContract.setCustomerID(rs.getString("CUST_ID"));
                aContract.setEnabled(Constants.Y.equalsIgnoreCase(rs.getString("STATUS")) ? Constants.ENABLED : Constants.DISABLED);
            }
        } catch(SQLException e) {
            log.error("findContractData ",e);

        } catch(NamingException e) {
            log.error("findContractData ",e);

        } finally {

            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
                if(conn != null)
                    conn.close();
            } catch(SQLException e) {
                log.error("findContractData ",e);
            }
        }

        return aContract;
    }


    private Contract updateContractData(Contract contract) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE CONTRACT_DATA SET STATUS=? WHERE CONTRACT_ID=?");

            pstmt.setString(idx++, (contract.getEnabled() == Constants.ENABLED  ? Constants.Y : Constants.N ));
            pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));

            result = pstmt.executeUpdate();
			conn.commit();
        } catch(SQLException e) {
            log.error("updateContractData ",e);

        } catch(NamingException e) {
            log.error("updateContractData ",e);

        } finally {

            try {
                if(pstmt != null)
                    pstmt.close();
                if(conn != null){
                    conn.commit();
                    conn.close();
				}
            } catch(SQLException e) {
                log.error("updateContractData ",e);
            }
        }

        return contract;
    }

    private Contract insertContractData(Contract contract) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO CONTRACT_DATA (CONTRACT_ID, CUST_ID, FIRM_NAME, STATUS, INDEX_ID) VALUES(?,?,?,?,?)");

            long lngID = getNextContractID();
            pstmt.setLong(idx++, lngID);
            pstmt.setLong(idx++, Long.parseLong(contract.getCustomerID()));
            pstmt.setString(idx++, contract.getCustomer().getName());
            pstmt.setString(idx++, (contract.getEnabled() == Constants.ENABLED  ? Constants.Y : Constants.N ));
            pstmt.setLong(idx++, lngID);

            result = pstmt.executeUpdate();

            contract.setContractID(String.valueOf(lngID));
			conn.commit();
        } catch(SQLException e) {
            log.error("insertContractData ",e);

        } catch(NamingException e) {
            log.error("insertContractData ",e);

        } finally {

            try {
                if(pstmt != null)
                    pstmt.close();
                if(conn != null){
                   conn.commit();
                    conn.close();
				}
            } catch(SQLException e) {
                log.error("insertContractData ",e);
            }
        }

        return contract;
    }

}
