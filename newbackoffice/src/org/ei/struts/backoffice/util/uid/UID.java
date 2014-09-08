/*
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.util.uid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.ei.struts.backoffice.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UID {

	private UID() {
	}
	private static Log log = LogFactory.getLog("UID");

	public static long getNextId() throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("select GENERIC_UNIQUE_ID.nextval FROM DUAL");

			rs = pstmt.executeQuery();

			if(rs.next()) {
				result = rs.getLong(1);
			}


		} catch (SQLException e) {
			log.error("getNextId", e);
			throw e;

		} catch (NamingException e) {
			log.error("getNextId", e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getNextId", e);
				throw e;
			}
		}

		return result;

	}


	public static long getHighestId() throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0;
            
		try {

            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT MAX(i) FROM ");
            sqlBuffer.append("(");
            sqlBuffer.append("select 'CONTRACT_DATA.CONTRACT_ID' id,MAX(X.CONTRACT_ID) i FROM CONTRACT_DATA X WHERE NOT (CONTRACT_ID = 123456789 OR CONTRACT_ID=99999999)");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'CONTRACT.CONTRACT_NUMBER' id,MAX(X.CONTRACT_NUMBER) i FROM CONTRACT X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'CUSTOMER_MASTER.CUSTOMER_ID' id,MAX(X.CUSTOMER_ID) i FROM CUSTOMER_MASTER X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'CONTACT.CONTACT_ID' id,MAX(X.CONTACT_ID) i FROM CONTACT X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'IP_DATA.INDEX_ID' id,MAX(X.INDEX_ID) i FROM IP_DATA X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'USER_PASS_DATA.INDEX_ID' id,MAX(X.INDEX_ID) i FROM USER_PASS_DATA X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'GATEWAY_DATA.INDEX_ID' id,MAX(X.INDEX_ID) i FROM GATEWAY_DATA X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'SALES_REGION.REGION_CODE' id,MAX(X.REGION_CODE) i FROM SALES_REGION X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'SALES_ORG.SALES_REP_ID' id,MAX(X.SALES_REP_ID) i FROM SALES_ORG X");
            sqlBuffer.append(" UNION ");
            sqlBuffer.append("select 'LOCAL_LINKING_OPTIONS.INDEX_ID' id,MAX(X.INDEX_ID) i FROM LOCAL_LINKING_OPTIONS X");
            sqlBuffer.append(" ORDER BY i DESC ");
            sqlBuffer.append(")");

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);

			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sqlBuffer.toString());

			rs = pstmt.executeQuery();

			if(rs.next()) {
				result = rs.getLong(1);
			}


		} catch (SQLException e) {
			log.error("getHighestId", e);
			throw e;

		} catch (NamingException e) {
			log.error("getHighestId", e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getHighestId", e);
				throw e;
			}
		}
        
		return result;

	}

}
