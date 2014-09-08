/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/U_ACCESS_Database.java-arc   1.2   Mar 31 2009 12:02:48   johna  $
 * $Revision:   1.2  $
 * $Date:   Mar 31 2009 12:02:48  $
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
import org.ei.struts.backoffice.credentials.gateway.Gateway;
import org.ei.struts.backoffice.credentials.ip.Ip;
import org.ei.struts.backoffice.credentials.ip.IpRange;
import org.ei.struts.backoffice.credentials.username.Username;
import org.ei.struts.backoffice.product.Product;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.2  $ $$
 */
public class U_ACCESS_Database {

  private static Log log = LogFactory.getLog("U_ACCESS_Database");
  // --------------------------------------------------------------------------

  // --------------------------------------------------------------------------
  public boolean save_U_ACCESS_Credentials(Credentials creds) {

    boolean result = false;
    String strAccess = creds.getType();

    log.info("save_U_ACCESS_Credentials: " + creds);

    if(Constants.IP.equalsIgnoreCase(strAccess)) {
      log.info("save_U_ACCESS_Credentials: save_U_ACCESS_IpData");
      result = save_U_ACCESS_IpData(creds);
    }
    else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
      log.info("save_U_ACCESS_Credentials: save_U_ACCESS_GatewayData");
      result = save_U_ACCESS_GatewayData(creds);
    }
    else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
      log.info("save_U_ACCESS_Credentials: save_U_ACCESS_UsernameData");
      result = save_U_ACCESS_UsernameData(creds);
    }
    return result;

  }

  // --------------------------------------------------------------------------
  public boolean delete_U_ACCESS_Credentials(String strAccess, String strIndexID) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
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
      log.error("delete_U_ACCESS_Credentials", e);

    } catch (NamingException e) {
      log.error("delete_U_ACCESS_Credentials", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	     }
      } catch (SQLException e) {
        log.error("delete_U_ACCESS_Credentials", e);
      }
    }

    return (result == 1);

  }

  // IP
  // --------------------------------------------------------------------------
  public boolean save_U_ACCESS_IpData(Credentials credentials) {

    boolean result = false;
    Ip ip = (Ip) credentials;

    try {

      if (get_U_ACCESS_IpData(credentials.getIndexID()) != null) {
        log.info("save_U_ACCESS_IpData: update_U_ACCESS_IpData");
        result = update_U_ACCESS_IpData(ip);
      } else {
        log.info("save_U_ACCESS_IpData: insert_U_ACCESS_IpData");
        result = insert_U_ACCESS_IpData(ip);
      }
    } catch (Exception e) {
      log.error("save_U_ACCESS_IpData ", e);
    }

    return result;
  }

  private Ip get_U_ACCESS_IpData(String indexid) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Ip aCredentials = null;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM IP_DATA WHERE INDEX_ID=?");
      pstmt.setString(idx++, indexid);

      rs = pstmt.executeQuery();

      if (rs.next()) {
        aCredentials = new Ip();
        aCredentials.setIndexID(rs.getString("INDEX_ID"));
        aCredentials.setHighIp(Constants.formatIpAddress(rs.getString("HIGH_IP")));
        aCredentials.setLowIp(Constants.formatIpAddress(rs.getString("LOW_IP")));

      }

    } catch (SQLException e) {
      log.error("get_U_ACCESS_IpData", e);

    } catch (NamingException e) {
      log.error("get_U_ACCESS_IpData", e);

    } finally {

      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
        log.error("get_U_ACCESS_IpData", e);
      }
    }

    return aCredentials;
  }

  private boolean update_U_ACCESS_IpData(Ip credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;
    Credentials aCredentials = null;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("UPDATE IP_DATA SET HIGH_IP=?, LOW_IP=? WHERE INDEX_ID=?");
      pstmt.setString(idx++, Constants.cleanIpAddress(credentials.getHighIp()));
      pstmt.setString(idx++, Constants.cleanIpAddress(credentials.getLowIp()));
      pstmt.setString(idx++, credentials.getIndexID());

      result = pstmt.executeUpdate();

    } catch (SQLException e) {
      log.error("update_U_ACCESS_IpData", e);

    } catch (NamingException e) {
      log.error("update_U_ACCESS_IpData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	    }
      } catch (SQLException e) {
        log.error("update_U_ACCESS_IpData", e);
      }
    }

    return (result == 1);
  }

  // This inserts one or more ipaddresses at a time
  // Therefore it calls getIpRanges() to get a collection - which has one or more IPs in it
  private boolean insert_U_ACCESS_IpData(Ip credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 1;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
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
        pstmt = conn.prepareStatement( "INSERT INTO IP_DATA (CONTRACT_ID, CUST_ID, HIGH_IP, LOW_IP, PROD_ID, PROD_NAME, INDEX_ID)  VALUES(?,?,?,?,?,?,?)");

        idx = 1;
        pstmt.setString(idx++, contract.getContractID());
        pstmt.setString(idx++, contract.getCustomerID());
        pstmt.setString(idx++, highip);
        pstmt.setString(idx++, lowip);
        pstmt.setString(idx++, product.getProductID());
        pstmt.setString(idx++, product.getName());
        pstmt.setLong(idx++, Long.parseLong(iprange.getIndexID()));

        result = pstmt.executeUpdate();

        pstmt.close();
      }

    } catch (SQLException e) {
      log.error("insert_U_ACCESS_IpData", e);

    } catch (NamingException e) {
      log.error("insert_U_ACCESS_IpData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	  	}
      } catch (SQLException e) {
        log.error("insert_U_ACCESS_IpData", e);
      }
    }

    return (result == 1);

  }

  // USERNAME
  // --------------------------------------------------------------------------

  public boolean save_U_ACCESS_UsernameData(Credentials credentials) {

    boolean result = false;
    Username username = (Username) credentials;
    try {
      if (get_U_ACCESS_UsernameData(credentials.getIndexID()) != null) {
        log.info("save_U_ACCESS_UsernameData: update_U_ACCESS_UsernameData");
        result = update_U_ACCESS_UsernameData(username);
      } else {
        log.info("save_U_ACCESS_UsernameData: insert_U_ACCESS_UsernameData");
        result = insert_U_ACCESS_UsernameData(username);
      }
    } catch (Exception e) {
      log.error("save_U_ACCESS_UsernameData ", e);
    }

    return result;
  }

  private Username get_U_ACCESS_UsernameData(String accessid) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Username aCredentials = null;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM USER_PASS_DATA WHERE INDEX_ID=?");
      pstmt.setString(idx++, accessid);

      rs = pstmt.executeQuery();

      if (rs.next()) {
        log.info(" get_U_ACCESS_UsernameData INDEX_ID " + rs.getString("INDEX_ID"));
        log.info(" get_U_ACCESS_UsernameData USERNAME " + rs.getString("USERNAME"));
        log.info(" get_U_ACCESS_UsernameData PASSWORD " + rs.getString("PASSWORD"));
        log.info(" get_U_ACCESS_UsernameData CUST_ID " + rs.getString("CUST_ID"));
        log.info(" get_U_ACCESS_UsernameData CONTRACT_ID " + rs.getString("CONTRACT_ID"));
        log.info(" get_U_ACCESS_UsernameData PROD_ID " + rs.getString("PROD_ID"));
        log.info(" get_U_ACCESS_UsernameData PROD_NAME " + rs.getString("PROD_NAME"));

        aCredentials = new Username();
        aCredentials.setIndexID(rs.getString("INDEX_ID"));
        aCredentials.setUsername(rs.getString("USERNAME"));
        aCredentials.setPassword(rs.getString("PASSWORD"));
      }

    } catch (SQLException e) {
      log.error("get_U_ACCESS_UsernamePasswordData", e);

    } catch (NamingException e) {
      log.error("get_U_ACCESS_UsernamePasswordData", e);

    } finally {

      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
        log.error("get_U_ACCESS_UsernamePasswordData", e);
      }
    }

    return aCredentials;
  }

  private boolean update_U_ACCESS_UsernameData(Username credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;
    Credentials aCredentials = null;

    log.info("update_U_ACCESS_UsernameData: " + credentials);
    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt =conn.prepareStatement("UPDATE USER_PASS_DATA SET USERNAME=?, PASSWORD=? WHERE INDEX_ID=?");
      pstmt.setString(idx++, credentials.getUsername());
      pstmt.setString(idx++, credentials.getPassword());
      pstmt.setString(idx++, credentials.getIndexID());

      result = pstmt.executeUpdate();

    } catch (SQLException e) {
      log.error("update_U_ACCESS_UsernameData", e);

    } catch (NamingException e) {
      log.error("update_U_ACCESS_UsernameData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	    }
      } catch (SQLException e) {
        log.error("update_U_ACCESS_UsernamePasswordData", e);
      }
    }

    return (result == 1);
  }

  private boolean insert_U_ACCESS_UsernameData(Username credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 1;

    log.info("insert_U_ACCESS_UsernameData: " + credentials);
    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
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
      pstmt.setLong(idx++, Long.parseLong(credentials.getIndexID()));

      result = pstmt.executeUpdate();

    } catch (SQLException e) {
      log.error("insert_U_ACCESS_UsernamePasswordData", e);

    } catch (NamingException e) {
      log.error("insert_U_ACCESS_UsernamePasswordData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	    }
      } catch (SQLException e) {
        log.error("insert_U_ACCESS_UsernamePasswordData", e);
      }
    }

    return (result == 1);

  }


  // GATEWAY
  // --------------------------------------------------------------------------

  public boolean save_U_ACCESS_GatewayData(Credentials credentials) {

    boolean result = false;
    Gateway gateway = (Gateway) credentials;

    try {

      if (get_U_ACCESS_GatewayData(credentials.getIndexID()) != null) {
        log.info("save_U_ACCESS_GatewayData: update_U_ACCESS_GatewayData");
        result = update_U_ACCESS_GatewayData(gateway);
      } else {
        log.info("save_U_ACCESS_GatewayData: insert_U_ACCESS_GatewayData");
        result = insert_U_ACCESS_GatewayData(gateway);
      }
    } catch (Exception e) {
      log.error("save_U_ACCESS_GatewayData ", e);
    }

    return result;
  }

  private Gateway get_U_ACCESS_GatewayData(String indexid) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Gateway aCredentials = null;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM GATEWAY_DATA WHERE INDEX_ID=?");
      pstmt.setString(idx++, indexid);

      rs = pstmt.executeQuery();

      if (rs.next()) {
        aCredentials = new Gateway();
        aCredentials.setIndexID(rs.getString("INDEX_ID"));
        aCredentials.setGatewayUrl(rs.getString("URL"));
      }

    } catch (SQLException e) {
      log.error("get_U_ACCESS_GatewayData", e);

    } catch (NamingException e) {
      log.error("get_U_ACCESS_GatewayData", e);

    } finally {

      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (conn != null)
          conn.close();
      } catch (SQLException e) {
        log.error("get_U_ACCESS_GatewayData", e);
      }
    }

    return aCredentials;
  }

  private boolean update_U_ACCESS_GatewayData(Gateway credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("UPDATE GATEWAY_DATA SET URL=? WHERE INDEX_ID=?");
      pstmt.setString(idx++, credentials.getGatewayUrl());
      pstmt.setString(idx++, credentials.getIndexID());

      result = pstmt.executeUpdate();

    } catch (SQLException e) {
      log.error("update_U_ACCESS_GatewayData", e);

    } catch (NamingException e) {
      log.error("update_U_ACCESS_GatewayData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	    }
      } catch (SQLException e) {
        log.error("update_U_ACCESS_GatewayData", e);
      }
    }

    return (result == 1);
  }

  private boolean insert_U_ACCESS_GatewayData(Gateway credentials) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 1;

    try {

      Context initCtx = new InitialContext();
      Context envCtx =
        (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds =
        (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
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
      pstmt.setLong(idx++, Long.parseLong(credentials.getIndexID()));

      result = pstmt.executeUpdate();

    } catch (SQLException e) {
      log.error("insert_U_ACCESS_GatewayData", e);

    } catch (NamingException e) {
      log.error("insert_U_ACCESS_GatewayData", e);

    } finally {

      try {
        if (pstmt != null)
          pstmt.close();
        if (conn != null){
          conn.commit();
          conn.close();
	     }
      } catch (SQLException e) {
        log.error("insert_U_ACCESS_GatewayData", e);
      }
    }

    return (result == 1);

  }

}
