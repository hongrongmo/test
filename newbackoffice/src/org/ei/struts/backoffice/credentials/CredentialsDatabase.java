/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/CredentialsDatabase.java-arc   1.1   Mar 18 2009 13:25:38   johna  $
 * $Revision:   1.1  $
 * $Date:   Mar 18 2009 13:25:38  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.credentials;

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
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.credentials.access.Access;
import org.ei.struts.backoffice.credentials.gateway.Gateway;
import org.ei.struts.backoffice.credentials.ip.Ip;
import org.ei.struts.backoffice.credentials.ip.IpRange;
import org.ei.struts.backoffice.credentials.username.Username;
import org.ei.struts.backoffice.product.Product;
import org.ei.struts.backoffice.util.uid.UID;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Mar 18 2009 13:25:38  $
 */
public class CredentialsDatabase {

    private static Log log = LogFactory.getLog("CredentialsDatabase");

    public Credentials createCredentials(String strAccess) {

        Credentials creds = null;

        if(Constants.IP.equalsIgnoreCase(strAccess)) {
            creds = new Ip();
        }
        else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
            creds = new Gateway();
        }
        else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
            creds = new Username();
        }
        return creds;

    }

    public Credentials getCredentialsData(String strAccess, String strIndexID) {

        Credentials creds = null;

        if(Constants.IP.equalsIgnoreCase(strAccess)) {
            creds = getIpData(strIndexID);
        }
        else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
            creds = getGatewayData(strIndexID);
        }
        else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
            creds = getUsernameData(strIndexID);
        }
        return creds;

    }

    public Collection getAllCredentialsData(
            String contractid,
            String customerid,
            String productid) {
        Collection all = new ArrayList();
        all.addAll(getGatewayData(contractid, customerid, productid));
        all.addAll(getIpData(contractid, customerid, productid));
        all.addAll(getUsernameData(contractid, customerid, productid));
        return all;
    }

    public Credentials saveCredentials(Credentials creds) {

        Credentials saved = null;
        String strAccess = creds.getType();

        if(Constants.IP.equalsIgnoreCase(strAccess)) {
            saved = saveIpData(creds);
        }
        else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
            saved = saveGatewayData(creds);
        }
        else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
            saved = saveUsernameData(creds);
        }

        log.info(" saveCredentials " + saved);
        (new U_ACCESS_Database()).save_U_ACCESS_Credentials(saved);

        return saved;

    }

    /* ---------------------------------------------- DELETE DATA -------------------------------- */
    public boolean deleteCredentials(String strAccess, String strIndexID) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            if(Constants.IP.equalsIgnoreCase(strAccess)) {
                pstmt = conn.prepareStatement("DELETE FROM IP_DATA WHERE INDEX_ID=?");
            } else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
                pstmt = conn.prepareStatement("DELETE FROM GATEWAY_DATA WHERE INDEX_ID=?");
            } else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
                pstmt = conn.prepareStatement("DELETE FROM USER_PASS_DATA WHERE INDEX_ID=?");
            }
            pstmt.setString(idx++, strIndexID);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("deleteCredentials", e);

        } catch (NamingException e) {
            log.error("deleteCredentials", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("deleteCredentials", e);
            }
        }

        (new U_ACCESS_Database()).delete_U_ACCESS_Credentials(strAccess, strIndexID);

        return (result == 1);

    }

	// jam - 05/03/2004
	// changed code to use same sequence for all three credentials index_id value
	// simpler to maintain - single starting value of 10001001
	// creates somewhat of an 'order' for data insertion
    private long getNextId() throws SQLException {
        return UID.getNextId();
    }

    public Collection getCredentialsOverlap(Credentials credentials) {

        Collection collection = new ArrayList();
        Collection conflicts = new ArrayList();

        if(Constants.IP.equalsIgnoreCase(credentials.getType())) {
            collection = getOverlapIpData(credentials);
        }
        else if(Constants.GATEWAY.equalsIgnoreCase(credentials.getType())) {
            collection = getOverlapGatewayData(credentials);
        }
        else if(Constants.USERNAME.equalsIgnoreCase(credentials.getType())) {
            collection = getOverlapUsernameData(credentials);
        }

        // check credentials
        Iterator itr = collection.iterator();
        String customerid = credentials.getAccess().getContract().getCustomerID();
        String contractid = credentials.getAccess().getContractID();

        log.info("looking @ conflicts");
        log.info("customerid " + customerid);
        log.info("contractid " + contractid);

        while(itr.hasNext()) {
            Credentials creds = (Credentials) itr.next();

            // look for overlaps with other customers
            if(!customerid.equals(creds.getAccess().getContract().getCustomerID())) {
                conflicts.add(creds);
            }
            // look for overlaps within the same customer
            else if(!contractid.equals(creds.getAccess().getContractID())) {

                // jam 2/24/2004 - version '1.2'
                // only prevent conflicts between same product!
                // Conflicts within same customer are
                // OK as long as the products are different
                Item thisItem = credentials.getAccess().getItem();
                String productid = thisItem.getProductID();

                Item credItem = creds.getAccess().getItem();
                String conflictproductid = credItem.getProductID();

                if(productid.equals(conflictproductid)) {
                    conflicts.add(creds);
                }
            }
        }

        return conflicts;

    }

    /* ---------------------------------------------- GATEWAY DATA -------------------------------- */

    public Collection getOverlapGatewayData(Credentials credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection accessids = new ArrayList();
        Gateway gateway = (Gateway) credentials;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            if(gateway.getGatewayUrl().indexOf("%")<0)
            {
                pstmt =conn.prepareStatement("SELECT INDEX_ID FROM GATEWAY_DATA WHERE URL=?");
            }
            else
            {
                pstmt =conn.prepareStatement("SELECT INDEX_ID FROM GATEWAY_DATA WHERE URL like ?");
            }
            pstmt.setString(idx++, gateway.getGatewayUrl());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                accessids.add(this.getGatewayData(rs.getString("INDEX_ID")));
            }

        } catch (SQLException e) {
            log.error("getOverlapGatewayData", e);

        } catch (NamingException e) {
            log.error("getOverlapGatewayData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getOverlapGatewayData", e);
            }
        }

        return accessids;
    }

    public Collection getGatewayData(
        String contractid,
        String customerid,
        String productid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Gateway aCredentials = null;
        Collection allCredentials = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM GATEWAY_DATA WHERE CONTRACT_ID=? AND CUST_ID=? AND PROD_ID=?");
            pstmt.setString(idx++, contractid);
            pstmt.setString(idx++, customerid);
            pstmt.setString(idx++, productid);

            rs = pstmt.executeQuery();

            Access access = new Access();
            access.setContractID(contractid);
            Collection items = access.getContract().getAllItems();

            while(rs.next()) {
                aCredentials = new Gateway();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setGatewayUrl(rs.getString("URL"));

                access = getAccess(contractid, productid, items);
                aCredentials.setAccess(access);

                allCredentials.add(aCredentials);
            }

        } catch (SQLException e) {
            log.error("getGatewayData", e);

        } catch (NamingException e) {
            log.error("getGatewayData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getGatewayData", e);
            }
        }

        return allCredentials;
    }

    public Gateway getGatewayData(String indexid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Gateway aCredentials = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM GATEWAY_DATA WHERE INDEX_ID=?");
            pstmt.setString(idx++, indexid);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                aCredentials = new Gateway();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setGatewayUrl(rs.getString("URL"));

                String contractid = rs.getString("CONTRACT_ID");
                String productid = rs.getString("PROD_ID");

                Access access = getAccess(contractid, productid);
                aCredentials.setAccess(access);
            }

        } catch (SQLException e) {
            log.error("getGatewayData", e);

        } catch (NamingException e) {
            log.error("getGatewayData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getGatewayData", e);
            }
        }

        return aCredentials;
    }

    public Credentials saveGatewayData(Credentials credentials) {

        Credentials saved = null;
        Gateway gateway = (Gateway) credentials;

        try {

            if (getGatewayData(credentials.getIndexID()) != null) {
                saved = updateGatewayData(gateway);
            } else {
                saved = insertGatewayData(gateway);
            }
        } catch (Exception e) {
            log.error("saveGatewayData ", e);
        }

        return saved;
    }

    private Credentials updateGatewayData(Gateway credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE GATEWAY_DATA SET URL=? WHERE INDEX_ID=?");
            pstmt.setString(idx++, credentials.getGatewayUrl());
            pstmt.setString(idx++, credentials.getIndexID());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("updateGatewayData", e);

        } catch (NamingException e) {
            log.error("updateGatewayData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("updateGatewayData", e);
            }
        }

        return credentials;
    }

    private Credentials insertGatewayData(Gateway credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 1;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            Contract contract = credentials.getAccess().getContract();
            Product product = credentials.getAccess().getItem().getProduct();

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO GATEWAY_DATA (CONTRACT_ID, CUST_ID, URL, PROD_ID, PROD_NAME, INDEX_ID) VALUES(?,?,?,?,?,?)");
            pstmt.setString(idx++, contract.getContractID());
            pstmt.setString(idx++, contract.getCustomerID());
            pstmt.setString(idx++, credentials.getGatewayUrl());
            pstmt.setString(idx++, product.getProductID());
            pstmt.setString(idx++, product.getName());
            long lngId = getNextId();
            pstmt.setLong(idx++, lngId);

            result = pstmt.executeUpdate();
            credentials.setIndexID(String.valueOf(lngId));

        } catch (SQLException e) {
            log.error("insertGatewayData", e);

        } catch (NamingException e) {
            log.error("insertGatewayData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("insertGatewayData", e);
            }
        }

        return credentials;

    }

    /* ---------------------------------------------- USERNAME/PASSWORD DATA -------------------------------- */

    public Collection getOverlapUsernameData(Credentials credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection accessids = new ArrayList();
        Username username = (Username) credentials;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            if (Constants.EMPTY_STRING.equals(username.getPassword())) {
                pstmt = conn.prepareStatement("SELECT INDEX_ID FROM USER_PASS_DATA WHERE UPPER(USERNAME)=UPPER(?)");
                pstmt.setString(idx++, username.getUsername());
            } else {
                pstmt = conn.prepareStatement("SELECT INDEX_ID FROM USER_PASS_DATA WHERE USERNAME=? AND PASSWORD=?");
                pstmt.setString(idx++, username.getUsername());
                pstmt.setString(idx++, username.getPassword());
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                accessids.add(this.getUsernameData(rs.getString("INDEX_ID")));
            }

        } catch (SQLException e) {
            log.error("getOverlapUsernameData", e);

        } catch (NamingException e) {
            log.error("getOverlapUsernameData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getOverlapUsernameData", e);
            }
        }

        return accessids;
    }

    public Collection getUsernameData(
        String contractid,
        String customerid,
        String productid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Username aCredentials = null;
        Collection allCredentials = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USER_PASS_DATA WHERE CONTRACT_ID=? AND CUST_ID=? AND PROD_ID=?");
            pstmt.setString(idx++, contractid);
            pstmt.setString(idx++, customerid);
            pstmt.setString(idx++, productid);

            rs = pstmt.executeQuery();

            Access access = new Access();
            access.setContractID(contractid);
            Collection items = access.getContract().getAllItems();

            while (rs.next()) {
                aCredentials = new Username();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setUsername(rs.getString("USERNAME"));
                aCredentials.setPassword(rs.getString("PASSWORD"));

                access = getAccess(contractid, productid, items);
                aCredentials.setAccess(access);

                allCredentials.add(aCredentials);
            }

        } catch (SQLException e) {
            log.error("getUsernamePasswordData", e);

        } catch (NamingException e) {
            log.error("getUsernamePasswordData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getUsernamePasswordData", e);
            }
        }

        return allCredentials;
    }

    public Username getUsernameData(String accessid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Username aCredentials = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USER_PASS_DATA WHERE INDEX_ID=?");
            pstmt.setString(idx++, accessid);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                aCredentials = new Username();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setUsername(rs.getString("USERNAME"));
                aCredentials.setPassword(rs.getString("PASSWORD"));

                String contractid = rs.getString("CONTRACT_ID");
                String productid = rs.getString("PROD_ID");

                Access access = getAccess(contractid, productid);
                aCredentials.setAccess(access);
            }

        } catch (SQLException e) {
            log.error("getUsernamePasswordData", e);

        } catch (NamingException e) {
            log.error("getUsernamePasswordData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getUsernamePasswordData", e);
            }
        }

        return aCredentials;
    }

    public Credentials saveUsernameData(Credentials credentials) {

        Credentials saved = null;
        Username username = (Username) credentials;
        try {

            if (getUsernameData(credentials.getIndexID()) != null) {
                saved = updateUsernameData(username);
            } else {
                saved = insertUsernameData(username);
            }
        } catch (Exception e) {
            log.error("saveUsernameData ", e);
        }

        return saved;
    }

    private Credentials updateUsernameData(Username credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        Credentials aCredentials = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt =conn.prepareStatement("UPDATE USER_PASS_DATA SET USERNAME=?, PASSWORD=? WHERE INDEX_ID=?");
            pstmt.setString(idx++, credentials.getUsername());
            pstmt.setString(idx++, credentials.getPassword());
            pstmt.setString(idx++, credentials.getIndexID());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("updateUsernameData", e);

        } catch (NamingException e) {
            log.error("updateUsernameData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("updateUsernamePasswordData", e);
            }
        }

        return credentials;
    }

    private Credentials insertUsernameData(Username credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 1;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            Contract contract = credentials.getAccess().getContract();
            Product product = credentials.getAccess().getItem().getProduct();

            conn = ds.getConnection();
            pstmt = conn.prepareStatement( "INSERT INTO USER_PASS_DATA (CONTRACT_ID, CUST_ID, USERNAME, PASSWORD, PROD_ID, PROD_NAME, INDEX_ID)  VALUES(?,?,?,?,?,?,?)");

            pstmt.setString(idx++, contract.getContractID());
            pstmt.setString(idx++, contract.getCustomerID());
            pstmt.setString(idx++, credentials.getUsername());
            pstmt.setString(idx++, credentials.getPassword());
            pstmt.setString(idx++, product.getProductID());
            pstmt.setString(idx++, product.getName());
            long lngId = getNextId();
            pstmt.setLong(idx++, lngId);

            result = pstmt.executeUpdate();

            credentials.setIndexID(String.valueOf(lngId));

        } catch (SQLException e) {
            log.error("insertUsernamePasswordData", e);

        } catch (NamingException e) {
            log.error("insertUsernamePasswordData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("insertUsernamePasswordData", e);
            }
        }

        return credentials;

    }

    /* ---------------------------------------------- IP DATA -------------------------------- */
    public Collection getOverlapIpData(Credentials credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection accessids = new ArrayList();
        Ip ip = (Ip) credentials;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            conn = ds.getConnection();
            int idx = 1;

            Collection ipranges = ip.getIpRanges();
            Iterator ipitr = ipranges.iterator();
            while(ipitr.hasNext())
            {
              IpRange iprange = (IpRange) ipitr.next();

              String highip = Constants.cleanIpAddress(iprange.getHighIp());
              String lowip = Constants.cleanIpAddress(iprange.getLowIp());

              idx = 1;
              pstmt = conn.prepareStatement("SELECT INDEX_ID FROM IP_DATA WHERE (? BETWEEN LOW_IP AND HIGH_IP) OR (? BETWEEN LOW_IP AND HIGH_IP) OR (? < LOW_IP AND ? > HIGH_IP)");
              pstmt.setString(idx++, lowip);
              pstmt.setString(idx++, highip);
              pstmt.setString(idx++, lowip);
              pstmt.setString(idx++, highip);

              rs = pstmt.executeQuery();

              while (rs.next()) {
                  accessids.add(this.getIpData(rs.getString("INDEX_ID")));
              }
              rs.close();
              pstmt.close();
            }

        } catch (SQLException e) {
            log.error("getOverlapIPData", e);

        } catch (NamingException e) {
            log.error("getOverlapIPData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getOverlapIPData", e);
            }
        }

        return accessids;
    }

    public Collection getIpData(
        String contractid,
        String customerid,
        String productid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Ip aCredentials = null;
        Collection allCredentials = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM IP_DATA WHERE CONTRACT_ID=? AND CUST_ID=? AND PROD_ID=? ORDER BY LOW_IP ASC");
            pstmt.setString(idx++, contractid);
            pstmt.setString(idx++, customerid);
            pstmt.setString(idx++, productid);

            rs = pstmt.executeQuery();

            // get the other information for this contract item
            Access access = new Access();
            access.setContractID(contractid);
            Collection items = access.getContract().getAllItems();
            access = getAccess(contractid, productid, items);

            while (rs.next()) {
                aCredentials = new Ip();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setHighIp(Constants.formatIpAddress(rs.getString("HIGH_IP")));
                aCredentials.setLowIp(Constants.formatIpAddress(rs.getString("LOW_IP")));

                aCredentials.setAccess(access);

                allCredentials.add(aCredentials);
            }

        } catch (SQLException e) {
            log.error("getIpData", e);

        } catch (NamingException e) {
            log.error("getIpData", e);

        } catch (Exception e) {
            log.error("getIpData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getIpData", e);
            }
        }

        return allCredentials;
    }

    public Ip getIpData(String indexid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Ip aCredentials = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM IP_DATA WHERE INDEX_ID=?");
            pstmt.setString(idx++, indexid);

            log.info(" indexid " + indexid);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                aCredentials = new Ip();
                aCredentials.setIndexID(rs.getString("INDEX_ID"));
                aCredentials.setHighIp(Constants.formatIpAddress(rs.getString("HIGH_IP")));
                aCredentials.setLowIp(Constants.formatIpAddress(rs.getString("LOW_IP")));

                String contractid = rs.getString("CONTRACT_ID");
                String productid = rs.getString("PROD_ID");

                log.info(" contractid " + contractid);
                log.info(" productid " + productid);

                Access access = getAccess(contractid, productid);

                aCredentials.setAccess(access);
            }

        } catch (SQLException e) {
            log.error("getIpData", e);

        } catch (NamingException e) {
            log.error("getIpData", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getIpData", e);
            }
        }

        return aCredentials;
    }

    public Credentials saveIpData(Credentials credentials) {

        Credentials saved = null;
        Ip ip = (Ip) credentials;

        try {

            if (getIpData(credentials.getIndexID()) != null) {
                saved = updateIpData(ip);
            } else {
                saved = insertIpData(ip);
            }
        } catch (Exception e) {
            log.error("saveIpData ", e);
        }

        return saved;
    }

    // This updates a single IP address
    private Credentials updateIpData(Ip credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        Credentials aCredentials = null;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            int idx = 1;

            conn = ds.getConnection();
            pstmt = conn.prepareStatement("UPDATE IP_DATA SET HIGH_IP=?, LOW_IP=? WHERE INDEX_ID=?");
            pstmt.setString(idx++, Constants.cleanIpAddress(credentials.getHighIp()));
            pstmt.setString(idx++, Constants.cleanIpAddress(credentials.getLowIp()));
            pstmt.setString(idx++, credentials.getIndexID());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("updateIpData", e);

        } catch (NamingException e) {
            log.error("updateIpData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("updateIpData", e);
            }
        }

        return credentials;
    }

    // This inserts one or more ipaddresses at a time
    // Therefore it calls getIpRanges() to get a collection - which has one or more IPs in it
    private Credentials insertIpData(Ip credentials) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 1;

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            conn = ds.getConnection();
            int idx = 1;

            Contract contract = credentials.getAccess().getContract();
            Product product = credentials.getAccess().getItem().getProduct();

            Collection ipranges = credentials.getIpRanges();
            Iterator ipitr = ipranges.iterator();
            while(ipitr.hasNext())
            {
              IpRange iprange = (IpRange) ipitr.next();

              String highip = Constants.cleanIpAddress(iprange.getHighIp());
              String lowip = Constants.cleanIpAddress(iprange.getLowIp());
              if((highip != null) && (lowip != null))
              {
                pstmt = conn.prepareStatement( "INSERT INTO IP_DATA (CONTRACT_ID, CUST_ID, HIGH_IP, LOW_IP, PROD_ID, PROD_NAME, INDEX_ID)  VALUES(?,?,?,?,?,?,?)");

                idx = 1;
                pstmt.setString(idx++, contract.getContractID());
                pstmt.setString(idx++, contract.getCustomerID());
                pstmt.setString(idx++, highip);
                pstmt.setString(idx++, lowip);
                pstmt.setString(idx++, product.getProductID());
                pstmt.setString(idx++, product.getName());
                long lngId = getNextId();
                pstmt.setLong(idx++, lngId);

                result = pstmt.executeUpdate();

                // It is very important to set this ID so that the U_ACCESS database IP_DATA table
                // entries have the same indexid as the BO_OFFICE database IP_DATA table
                // in order to delete and update IPs
                // Here it has to be set on each individual IP range since we are interating a collection
                iprange.setIndexID(String.valueOf(lngId));

                pstmt.close();
              }
            }

        } catch (SQLException e) {
            log.error("insertIpData", e);

        } catch (NamingException e) {
            log.error("insertIpData", e);

        } finally {

            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("insertIpData", e);
            }
        }

        return credentials;

    }

    private Access getAccess(String contractid, String productid, Collection items) {

//        log.info("Cached Items process");
        Access access = new Access();
        access.setContractID(contractid);

        Iterator itr = items.iterator();
        while(itr.hasNext()) {
            Item item = (Item) itr.next();
            if(item.getProductID().equals(productid)) {
                access.setItemID(item.getItemID());
                break;
            }
        }
        return access;
    }

    private Access getAccess(String contractid, String productid) {

//        log.info("SLOW process");
        Access access = new Access();
        access.setContractID(contractid);

        Collection items = access.getContract().getAllItems();

        return getAccess(contractid, productid, items);
    }

}