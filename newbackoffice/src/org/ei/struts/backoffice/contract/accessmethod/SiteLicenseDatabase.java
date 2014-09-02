/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/accessmethod/SiteLicenseDatabase.java-arc   1.0   Jan 14 2008 17:10:30   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:30  $
 *
 * ====================================================================
 */
package org.ei.struts.backoffice.contract.accessmethod;


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

public class SiteLicenseDatabase {

  private static Log log = LogFactory.getLog("SiteLicenseDatabase");

	public LabelValueBean findSiteLicense(String sitelicenseid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		LabelValueBean aSiteLicense = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM SITE_LICENSE WHERE ID=?");
			pstmt.setString(idx++, sitelicenseid);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				aSiteLicense = new LabelValueBean(rs.getString("LICENSE"), rs.getString("LICENSE"));
			}

		} catch(SQLException e) {
			log.error("findSiteLicense ",e);

		} catch(NamingException e) {
			log.error("findSiteLicense ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("findSiteLicense ",e);
			}
		}

		return aSiteLicense;
	}


	public Collection getSiteLicenses() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		LabelValueBean aSiteLicense = null;
		Collection colSiteLicenses = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM SITE_LICENSE ORDER BY ID ASC");

			rs = pstmt.executeQuery();

			while(rs.next()) {
				aSiteLicense = new LabelValueBean(rs.getString("LICENSE"), rs.getString("LICENSE"));
				colSiteLicenses.add(aSiteLicense);
			}

		} catch(SQLException e) {
			log.error("getSiteLicenses ",e);

		} catch(NamingException e) {
			log.error("getSiteLicenses ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("getSiteLicenses ",e);
			}
		}

		return colSiteLicenses;
	}

	public boolean saveSiteLicense(LabelValueBean sitelicense) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO SITE_LICENSE VALUES(?,?)");

			pstmt.setString(idx++, sitelicense.getValue());
			pstmt.setString(idx++, sitelicense.getLabel());

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("saveSiteLicense ",e);

		} catch(NamingException e) {
			log.error("saveSiteLicense ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();

					conn.close();
				}
			} catch(SQLException e) {
				log.error("saveSiteLicense ",e);
			}
		}

		return (result == 1);
	}


}
