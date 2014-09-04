/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/role/RoleDatabase.java-arc   1.0   Jan 14 2008 17:11:06   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:06  $
 *
 */
package org.ei.struts.backoffice.role;

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
import org.ei.struts.backoffice.Constants;

public class RoleDatabase {

  private static Log log = LogFactory.getLog("RoleDatabase");

	public Role createRole() {

		return new Role();
	}

	public Role findRole(String roleid) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Role aRole = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ACCESS_LEVEL WHERE ACCESS_LEVEL=?");
			pstmt.setString(idx++, roleid);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				aRole = new Role();
				aRole.setRoleID(rs.getString("ACCESS_LEVEL"));
				aRole.setRoleName(rs.getString("DESCRIPTION"));
			}

	  }
	  catch(SQLException e) {
			log.error("findRole",e);
	  }
	  catch(NamingException e) {
			log.error("findRole",e);
  	}
  	finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e) {
  			log.error("findRole",e);
			}
		}

		return aRole;
	}



	public Collection getRoles() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Role aRole = null;
		Collection colRoles = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ACCESS_LEVEL WHERE ACCESS_LEVEL > 0 ORDER BY ACCESS_LEVEL ASC");

			rs = pstmt.executeQuery();

			while(rs.next()) {
				aRole = new Role();
				aRole.setRoleID(rs.getString("ACCESS_LEVEL"));
				aRole.setRoleName(rs.getString("DESCRIPTION"));
				colRoles.add(aRole);
			}

	  }
	  catch(SQLException e) {
			log.error("getRoles",e);

	  }
	  catch(NamingException e) {
			log.error("getRoles",e);

	  }
	  finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e) {
  			log.error("getRoles",e);
			}
		}

		return colRoles;
	}


}