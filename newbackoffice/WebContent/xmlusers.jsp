<%@page session="false"%>
<%@page import="javax.naming.*"%>
<%@page import=" javax.sql.*"%>
<%@page import="java.sql.*"%>
<%@page import="org.apache.commons.dbcp.*"%>
<%@page import="org.ei.struts.backoffice.Constants"%>
<%
  Connection conn = null;
  PreparedStatement pstmt = null;
  ResultSet rs = null;
  try {
      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      BasicDataSource ds = (BasicDataSource) envCtx.lookup("jdbc/OfficePool");
      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM SALES_ORG ORDER BY SALES_REP;");
      rs = pstmt.executeQuery();
      out.print("<USERS>");
      out.print("\n");
      while(rs.next()) {
          out.print("<USER>");
          out.print("<NAME>" + rs.getString("SALES_REP") + "</NAME>");
          out.print("</USER>");
          out.print("\n");
      }
      out.print("</USERS>");
      out.print("\n");
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