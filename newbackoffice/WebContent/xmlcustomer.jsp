<%@ page session="false"%><%@ page  import="javax.naming.*"%><%@ page  import=" javax.sql.*"%><%@ page  import="java.sql.*"%><%@ page  import="org.apache.commons.dbcp.*"%><%@ page  import="org.ei.struts.backoffice.Constants"%><% javax.servlet.ServletOutputStream outputStream = response.getOutputStream();

  Connection conn = null;
  PreparedStatement pstmt = null;
  ResultSet rs = null;
  try {
      response.setContentType("application/x-download");
      response.setHeader("Pragma", "public");
      response.setHeader("Cache-Control","max-age=0");
      response.setHeader("Content-Disposition","attachment; filename=xmlcustomer.xml");

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      BasicDataSource ds = (BasicDataSource) envCtx.lookup("jdbc/XmlFeedPool");
      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT COUNTRY, CUSTOMER_ID, GOLDMINE_ID, CUSTOMER_TYPE, CUSTOMER_NAME, PARENT_COMPANY, SALES_REGION.REGION FROM CUSTOMER_MASTER, SALES_REGION WHERE CUSTOMER_MASTER.REGION_CODE = SALES_REGION.REGION_CODE ORDER BY CUSTOMER_ID");
/*
      pstmt = conn.prepareStatement("SELECT CUSTOMER_ID, GOLDMINE_ID, CUSTOMER_TYPE, CUSTOMER_NAME, PARENT_COMPANY, SALES_REGION.REGION, ACTIVE_CUSTOMERS.ACTIVE_CUSTOMER_ID FROM CUSTOMER_MASTER, SALES_REGION, (SELECT DISTINCT(CUST_ID) AS ACTIVE_CUSTOMER_ID FROM CONTRACT_DATA WHERE CONTRACT_DATA.STATUS='Y') ACTIVE_CUSTOMERS WHERE  CUSTOMER_MASTER.CUSTOMER_ID=ACTIVE_CUSTOMERS.ACTIVE_CUSTOMER_ID(+) AND CUSTOMER_MASTER.REGION_CODE = SALES_REGION.REGION_CODE ORDER BY CUSTOMER_ID");
      */
      rs = pstmt.executeQuery();
      outputStream.println("<CUSTOMERS>");
      outputStream.println("\n");
      while(rs.next()) {
          outputStream.println("<CUSTOMER>");
          outputStream.println("<SIS_ID>" + ((rs.getString("goldmine_id") != null) ? rs.getString("goldmine_id") : "") + "</SIS_ID>");
//          outputStream.println("<ACTIVE>" + ((rs.getString("ACTIVE_CUSTOMER_ID") == null) ? "N" : "Y") + "</ACTIVE>");
          outputStream.println("<COUNTRY>" + rs.getString("country") + "</COUNTRY>");
          outputStream.println("<CUSTOMER_ID>" + rs.getString("customer_id") + "</CUSTOMER_ID>");
          outputStream.println("<CUSTOMER_TYPE>" + rs.getString("customer_type") + "</CUSTOMER_TYPE>");
          if(rs.getString("customer_name") != null)
          {
              outputStream.println("<CUSTOMER_NAME><![CDATA[" + (rs.getString("customer_name")).replaceAll("[^a-zA-Z_0-9\\s\\.\\#\\&\\-\\(\\)\\@\\$\\%\\!\\:\\;\\,]"," ") + "]]></CUSTOMER_NAME>");
          }
          else {
              log("null customer name "+rs.getString("customer_id"));
              outputStream.println("<CUSTOMER_NAME>null</CUSTOMER_NAME>");
          }
          outputStream.println("<REGION><![CDATA[" + rs.getString("region") + "]]></REGION>");
          outputStream.println("<PARENT_COMPANY>" + rs.getString("parent_company") + "</PARENT_COMPANY>");
          outputStream.println("</CUSTOMER>");
          outputStream.println("\n");
      }
      outputStream.println("</CUSTOMERS>");
      outputStream.println("\n");
  }
  catch(SQLException e) {
      log("SQL Exception " + e.getMessage());
  }
  finally
  {
      if(rs != null)
      {
          try {
              rs.close();
          }
          catch(SQLException e) {
          }
      }
      if(pstmt != null)
      {
          try {
              pstmt.close();
          }
          catch(SQLException e) {
          }
      }
      if(conn != null)
      {
          try {
              conn.close();
          }
          catch(SQLException e) {
          }
      }
  }
%>