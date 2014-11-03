/*
 * Created on Apr 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.customer.view;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.emetrics.Constants;

public class CustomerDatabase {

    private static Log log = LogFactory.getLog("CustomerDatabase");

    private class CustCompare implements Comparator
    {
      public int compare(Object a, Object b)
      {
        return ((String) ((Map) a).get("name")).compareTo(((String) ((Map) b).get("name")));
      }
    }

    public UserView authenticate(String username, String password){

        Connection conn = null;
//        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserView aUser = null;
		Statement st = null;
		String q = null;

        try {

           Context initCtx = new InitialContext();
            Context envCtx =
                (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds =
                (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
            conn = ds.getConnection();


			System.err.println("Got connection already");

/*
 			int idx = 1;

            pstmt = conn.prepareStatement("SELECT * FROM USER_PASS_DATA WHERE USERNAME=? AND PASSWORD=? AND PROD_ID=?");
            pstmt.setString(idx++, username);
            pstmt.setString(idx++, password);
            pstmt.setString(idx++, Constants.USAGE_PROD_ID); */
//			pstmt.setInt(idx++, Integer.parseInt(Constants.USAGE_PROD_ID));

//            rs = pstmt.executeQuery();

			q = "SELECT * FROM USER_PASS_DATA WHERE USERNAME='"+username+"' AND PASSWORD='"+password+"' AND PROD_ID="+Constants.USAGE_PROD_ID;
			st = conn.createStatement();
			rs = st.executeQuery(q);

System.out.println("executed query");

            if (rs.next()) {
                aUser = new UserView();
//                aUser.setCustomerId(Integer.parseInt(rs.getString("CUST_ID")));
				aUser.setCustomerId(rs.getInt("CUST_ID"));
			}

        } catch (SQLException e) {
            log.error("authenticate ", e);

        } catch (NamingException e) {
            log.error("authenticate ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("authenticate ", e);
            }
        }

        return aUser;
    }

    public Collection getAllConsortiums() {

        Connection conn = null;
//        PreparedStatement pstmt = null;
        Statement st = null;
        String q = null;
        ResultSet rs = null;
        Collection allConsortiums = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
//            int idx = 1;

            conn = ds.getConnection();
//            pstmt = conn.prepareStatement("SELECT CUSTOMER_ID FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NULL AND CUSTOMER_ID IN (SELECT CUSTOMER_MASTER.PARENT_COMPANY FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NOT NULL)");
//            rs = pstmt.executeQuery();
            st = conn.createStatement();
            q = "SELECT CUSTOMER_ID FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NULL AND CUSTOMER_ID IN (SELECT CUSTOMER_MASTER.PARENT_COMPANY FROM CUSTOMER_MASTER WHERE PARENT_COMPANY IS NOT NULL)";
			rs = st.executeQuery(q);

            while (rs.next()) {
                allConsortiums.add(rs.getString("CUSTOMER_ID"));
            }

        } catch (SQLException e) {
            log.error("getConsortiums ", e);

        } catch (NamingException e) {
            log.error("getConsortiums ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
//                if (pstmt != null)
//                    pstmt.close();
	            if (st != null)
                    st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getConsortiums ", e);
            }
        }

        return allConsortiums;
    }

    public Collection getConsortiumMembers(String parentid) {
        Connection conn = null;
//        PreparedStatement pstmt = null;
		Statement st = null;
		String q = null;
        ResultSet rs = null;
        List customers = new ArrayList();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
//            int idx = 1;

            conn = ds.getConnection();
/* do not use this one
			pstmt = conn.prepareStatement("SELECT CUSTOMER_ID, to_char(max(day),'MM-DD-RRRR') LAST, CUSTOMER_NAME" + " FROM (SELECT * FROM CUSTOMER_MASTER WHERE PARENT_COMPANY=?), DailySummary" +  " WHERE CUSTOMER_ID=DailySummary.CUSTID" + " AND CUSTOMER_ID IS NOT NULL" + " GROUP BY CUSTOMER_ID, CUSTOMER_NAME");
*/
/*            pstmt = conn.prepareStatement("SELECT CUSTOMER_ID,  to_char(SYSDATE,'MM-DD-RRRR') LAST, CUSTOMER_NAME FROM CUSTOMER_MASTER WHERE PARENT_COMPANY=? AND CUSTOMER_ID IS NOT NULL GROUP BY CUSTOMER_ID, CUSTOMER_NAME");
            pstmt.setString(idx++, parentid);
            rs = pstmt.executeQuery(); */

            st = conn.createStatement();
            q = "SELECT CUSTOMER_ID,  to_char(SYSDATE,'MM-DD-RRRR') LAST, CUSTOMER_NAME FROM CUSTOMER_MASTER WHERE PARENT_COMPANY='"+parentid+"' AND CUSTOMER_ID IS NOT NULL GROUP BY CUSTOMER_ID, CUSTOMER_NAME";
			rs = st.executeQuery(q);

            while (rs.next()) {
                Map member = new HashMap(); //this.getCustomer(rs.getString("CUSTOMER_ID"));
                member.put("custid", rs.getString("CUSTOMER_ID"));
                member.put("name", rs.getString("CUSTOMER_NAME"));
System.out.println("customer name 1: " + rs.getString("CUSTOMER_NAME"));
                member.put("last", rs.getString("LAST"));
                customers.add(member);
            }

        } catch (SQLException e) {
            log.error("getConsortiumMembers ", e);

        } catch (NamingException e) {
            log.error("getConsortiumMembers ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
//                if (pstmt != null)
//                    pstmt.close();
				if (st != null)
					st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getConsortiumMemberIDs ", e);
            }
        }
        Collections.sort(customers, new CustCompare());
        return customers;
    }

    public Collection getCustomers(Map filter) {

        Connection conn = null;
//        PreparedStatement pstmt = null;
		Statement st = null;
        ResultSet rs = null;
        Collection customers = new ArrayList();

        log.info(" getCustomers Map " + filter);

        if(filter.isEmpty()) {
            return customers;
        }

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
//            int idx = 1;

            StringBuffer sqlstring = new StringBuffer();
            // jam - added consort true or false field
            sqlstring.append("SELECT DECODE(X.PARENT_COMPANY,CUSTOMER_MASTER.CUSTOMER_ID,'true','false') ISCONSORT, CUSTOMER_MASTER.CUSTOMER_ID, CUSTOMER_NAME, REGION_CODE, CUSTOMER_TYPE ");
            List whereclause = new ArrayList();
            List fromclause = new ArrayList();

            fromclause.add("CUSTOMER_MASTER");
            // jam - added consort true or false field
            fromclause.add("(SELECT UNIQUE(PARENT_COMPANY) FROM CUSTOMER_MASTER) X");

            if((filter.get("searchName") != null) && !(Constants.EMPTY_STRING).equals((String)filter.get("searchName"))) {
                //fromclause.add("CUSTOMER_MASTER");
                whereclause.add(" UPPER(CUSTOMER_NAME) LIKE '%" + ((String)filter.get("searchName")).toUpperCase() + "%'");
            }

            if((filter.get("salesregion") != null) && !(Constants.EMPTY_STRING).equals((String)filter.get("salesregion"))){
                whereclause.add(" REGION_CODE = " + filter.get("salesregion") + "");
            }

            // jam - added consort true or false field
            whereclause.add(" CUSTOMER_MASTER.CUSTOMER_ID = X.PARENT_COMPANY(+) ");


            if(filter.get("products") != null) {
                fromclause.add("CONTRACT");
                whereclause.add(" CUSTOMER_MASTER.CUSTOMER_ID = CONTRACT.CUSTOMER_ID ");

                StringBuffer instring = new StringBuffer();
                Iterator itrProd = Arrays.asList((String[])filter.get("products")).iterator();
                while(itrProd.hasNext()) {
                    instring.append((String) itrProd.next());
                    if(itrProd.hasNext()) {
                        instring.append( "','");
                    }
                }
                whereclause.add(" CONTRACT.PRODUCT_ID IN ('" + instring.toString() + "')");

            }

            if((filter.get("contractstatus") != null) && !(Constants.EMPTY_STRING).equals((String) filter.get("contractstatus"))) {
                if(!fromclause.contains("CONTRACT")) {
                    fromclause.add("CONTRACT");
                }
                whereclause.add(" CUSTOMER_MASTER.CUSTOMER_ID = CONTRACT.CUSTOMER_ID ");
                whereclause.add(" CONTRACT.STATUS = '" + filter.get("contractstatus") + "'");
            }

            if((filter.get("access") != null) && !(Constants.EMPTY_STRING).equals((String) filter.get("access"))) {
                if(!fromclause.contains("CONTRACT_DATA")) {
                    fromclause.add("CONTRACT_DATA");
                }
                whereclause.add(" CUSTOMER_MASTER.CUSTOMER_ID = CONTRACT_DATA.CUST_ID ");
                whereclause.add(" CONTRACT_DATA.STATUS = '" + filter.get("access") + "'");
            }

            if((filter.get("consortiums") != null) && (Constants.YES).equalsIgnoreCase((String) filter.get("consortiums"))) {
                whereclause.add(" CUSTOMER_MASTER.CUSTOMER_ID IN (SELECT UNIQUE(PARENT_COMPANY)  FROM CUSTOMER_MASTER) ");
            }

            if(!fromclause.isEmpty()) {

                sqlstring.append(" FROM ");

                Iterator itrFrom = fromclause.iterator();
                while(itrFrom.hasNext()) {
                    sqlstring.append((String) itrFrom.next());
                    if(itrFrom.hasNext()) {
                        sqlstring.append(", ");
                    }
                }
            }

            if(!whereclause.isEmpty()) {

                sqlstring.append(" WHERE ");

                Iterator itrWhere = whereclause.iterator();
                while(itrWhere.hasNext()) {
                    sqlstring.append((String) itrWhere.next());
                    if(itrWhere.hasNext()) {
                        sqlstring.append(" AND ");
                    }
                }
            }


            sqlstring.append(" ORDER BY LOWER(CUSTOMER_NAME) ASC");
            String q = sqlstring.toString();
            log.info(" SQL " + 	q);

            conn = ds.getConnection();
//            pstmt = conn.prepareStatement(sqlstring.toString());
//            rs = pstmt.executeQuery();
			st = conn.createStatement();
			rs = st.executeQuery(q);

            while (rs.next()) {
System.out.println("new try: sql string 2: " + q);
System.out.println("customer id 2: " + rs.getString("CUSTOMER_ID"));
System.out.println("customer name 2: " + rs.getString("CUSTOMER_NAME"));
System.out.println("customer region 2: " + rs.getString("REGION_CODE"));
System.out.println("customer type 2: " + rs.getString("CUSTOMER_TYPE"));
System.out.println("isconsort 2: " + rs.getString("ISCONSORT"));
                Map customer = new HashMap();
                customer.put("custid",rs.getString("CUSTOMER_ID"));
                customer.put("name",rs.getString("CUSTOMER_NAME"));
                customer.put("region",rs.getString("REGION_CODE"));
                customer.put("type",rs.getString("CUSTOMER_TYPE"));
                customer.put("consortium",rs.getString("ISCONSORT"));


                if (!customers.contains(customer)) {
                	customers.add(customer);
				}
            }

        } catch (SQLException e) {
            log.error("getCustomers ", e);

        } catch (NamingException e) {
            log.error("getCustomers ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
//                if (pstmt != null)
//                    pstmt.close();
				if (st != null)
					st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getCustomers ", e);
            }
        }

        return customers;
    }

    public Map getCustomer(String custid) {

        Connection conn = null;
//        PreparedStatement pstmt = null;
		Statement st = null;
        ResultSet rs = null;
        String q = null;
        Map customer = new HashMap();

        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
//            int idx = 1;

            conn = ds.getConnection();
//            pstmt = conn.prepareStatement("SELECT CUSTOMER_ID, CUSTOMER_NAME, REGION_CODE, CUSTOMER_TYPE FROM CUSTOMER_MASTER WHERE CUSTOMER_ID = ?");
//            pstmt.setString(idx++,custid);
//            rs = pstmt.executeQuery();

			st = conn.createStatement();
			q = "SELECT CUSTOMER_ID, CUSTOMER_NAME, REGION_CODE, CUSTOMER_TYPE FROM CUSTOMER_MASTER WHERE CUSTOMER_ID = "+custid;
			rs = st.executeQuery(q);

            while (rs.next()) {
                customer.put("custid",rs.getString("CUSTOMER_ID"));
                customer.put("name",rs.getString("CUSTOMER_NAME"));
System.out.println("customer name 3: " + rs.getString("CUSTOMER_NAME"));
                customer.put("region",rs.getString("REGION_CODE"));
                customer.put("type",rs.getString("CUSTOMER_TYPE"));
            }

        } catch (SQLException e) {
            log.error("getCustomer ", e);

        } catch (NamingException e) {
            log.error("getCustomer ", e);

        } finally {

            try {
                if (rs != null)
                    rs.close();
//                if (pstmt != null)
//                    pstmt.close();
				if (st !=null)
					st.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                log.error("getCustomer ", e);
            }
        }

        return customer;
    }

}