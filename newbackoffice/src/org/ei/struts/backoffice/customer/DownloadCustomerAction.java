/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/DownloadCustomerAction.java-arc   1.0   Jan 14 2008 17:10:40   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:40  $
 *
 */
package org.ei.struts.backoffice.customer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user contact information entered by the user. If a new contact is
 * created, the user is also implicitly logged on.
 * 
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:40  $
 */

public final class DownloadCustomerAction extends BackOfficeBaseAction {

    // ----------------------------------------------------- Instance Variables
    private static Log log = LogFactory.getLog("DownloadCustomerAction");

    /**
     * The <code>Log</code> instance for this application.
     */

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param actionForm
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * 
     * @exception Exception
     *                if the application business logic throws an exception
     */
    public ActionForward executeAction( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
    throws Exception {

        OutputStream out = null;
        response.setContentType("application/x-download");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control","max-age=0");
        response.setHeader("Content-Disposition","attachment; filename=xmlcustomer.xml");
        
        StringBuffer customerxml = new StringBuffer();
        byte[] buff = new byte[4096]; // 4KB
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            BasicDataSource ds = (BasicDataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
            conn = ds.getConnection();
            pstmt = conn.prepareStatement("SELECT CUSTOMER_ID, CUSTOMER_TYPE, CUSTOMER_NAME, PARENT_COMPANY, SALES_REGION.REGION FROM CUSTOMER_MASTER, SALES_REGION WHERE CUSTOMER_MASTER.REGION_CODE = SALES_REGION.REGION_CODE ORDER BY CUSTOMER_ID");
            rs = pstmt.executeQuery();
            customerxml.append("<CUSTOMERS>");
            customerxml.append("\n");
            while(rs.next()) {

                customerxml.append("<CUSTOMER>");
                customerxml.append(("<CUSTOMER_ID>" + rs.getString("customer_id") + "</CUSTOMER_ID>"));
                customerxml.append(("<CUSTOMER_TYPE>" + rs.getString("customer_type") + "</CUSTOMER_TYPE>"));
                if(rs.getString("customer_name") != null)
                {
                    customerxml.append(("<CUSTOMER_NAME><![CDATA[" + (rs.getString("customer_name")).replaceAll("[^a-zA-Z_0-9\\s\\.\\#\\&\\-\\(\\)\\@\\$\\%\\!\\:\\;\\,]"," ") + "]]></CUSTOMER_NAME>"));
                }
                else {
                 //   log("null customer name "+rs.getString("customer_id"));
                    customerxml.append("<CUSTOMER_NAME>null</CUSTOMER_NAME>");
                }
                customerxml.append(("<REGION><![CDATA[" + rs.getString("region") + "]]></REGION>"));
                customerxml.append(("<PARENT_COMPANY>" + rs.getString("parent_company") + "</PARENT_COMPANY>"));
                customerxml.append("</CUSTOMER>");
                customerxml.append("\n");
                
            }
            customerxml.append("</CUSTOMERS>");
            response.setContentLength(customerxml.length());
            out = response.getOutputStream();
            
            for (int pos = 0; pos < customerxml.length(); ) {
                 // read content in buff byte[]
                int end = pos + 4096;
                if(end > customerxml.length()) {
                    end = customerxml.length();
                }
                out.write(customerxml.substring(pos, end).getBytes());
                pos += 4096;
             }
           
        }
        catch(SQLException e) {
//            log("SQL Exception " + e.getMessage());
        }
        finally {
            
            try {
                if(rs != null)
                  rs.close();
                if(pstmt != null)
                  pstmt.close();
                if(conn != null)
                  conn.close();
              } catch(SQLException e) {
                log.error("getCustomers ",e);
              }
        }
        
        return null;
    }
}
