package org.ei.struts.backoffice.contract.status;

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

public class StatusDatabase {

    private static Log log = LogFactory.getLog("StatusDatabase");

	public Collection getStatus() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allTypes = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM CUST_STATUS ORDER BY STATUS ASC");

			rs = pstmt.executeQuery();

			// STATUS is stored in the CNVT_STATUS field in CONTRACT table and labeled 'Access Type' on the Contract Maintenance screen
			while(rs.next()) {
				// org.apache.struts.util.LabelValueBean(java.lang.String label, java.lang.String value)
				LabelValueBean lbvType = new LabelValueBean(rs.getString("STATUS").concat(" - ").concat(rs.getString("DESCRIPTION")),rs.getString("STATUS"));
				allTypes.add(lbvType);
			}

		} catch(SQLException e) {
			log.error("getStatus",e);

		} catch(NamingException e) {
			log.error("getStatus",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("getStatus",e);
			}
		}

		return allTypes;
	}

}