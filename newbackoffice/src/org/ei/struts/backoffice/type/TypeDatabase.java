package org.ei.struts.backoffice.type;

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
import org.apache.struts.util.LabelValueBean;
import org.ei.struts.backoffice.Constants;

public class TypeDatabase {

    private static Log log = LogFactory.getLog("TypeDatabase");

	public Collection getTypes() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allTypes = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM CUST_TYPES ORDER BY CUSTOMER_TYPE ASC");

			rs = pstmt.executeQuery();

			while(rs.next()) {
				// org.apache.struts.util.LabelValueBean(java.lang.String label, java.lang.String value)
				LabelValueBean lbvType = new LabelValueBean(rs.getString("CUSTOMER_TYPE"),rs.getString("CUSTOMER_TYPE"));
				allTypes.add(lbvType);
			}

		} catch(SQLException e) {
			log.error(this.getClass(),e);

		} catch(NamingException e) {
			log.error(this.getClass(),e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error(this.getClass(),e);
			}
		}

		return allTypes;
	}

}