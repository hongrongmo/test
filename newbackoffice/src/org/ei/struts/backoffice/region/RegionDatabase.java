package org.ei.struts.backoffice.region;

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
import org.ei.struts.backoffice.util.uid.UID;

public class RegionDatabase {

  private static Log log = LogFactory.getLog("RegionDatabase");

	public Region createRegion() {
		return new Region();
	}

	private long getNextRegionID() throws SQLException {
		return UID.getNextId();
	}

	public Region findRegion(String region) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Region aRegion = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM SALES_REGION WHERE REGION_CODE=?");
			pstmt.setString(idx++, region);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				aRegion = new Region();
				aRegion.setRegionID(rs.getString("REGION_CODE"));
				aRegion.setRegionName(rs.getString("REGION"));
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

		return aRegion;
	}


	public Collection getRegions() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Region aRegion = null;
		Collection colRegions = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM SALES_REGION ORDER BY REGION_CODE ASC");

			rs = pstmt.executeQuery();

			while(rs.next()) {
				aRegion = new Region();
				aRegion.setRegionID(rs.getString("REGION_CODE"));
				aRegion.setRegionName(rs.getString("REGION"));
				colRegions.add(aRegion);
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

		return colRegions;
	}

//	public boolean saveRegion(Region region) {
//
//		boolean result = false;
//
//		try {
//
//			if(findRegion(region.getRegionID()) != null) {
//				result = updateRegion(region);
//			} else {
//				result = insertRegion(region);
//			}
//
//		} catch(Exception e) {
//			log.error("saveRegion ",e);
//		}
//
//		return result;
//	}
//
//	public boolean insertRegion(Region region) {
//
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		int result = 0;
//
//		try {
//
//			Context initCtx = new InitialContext();
//			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
//			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
//			int idx = 1;
//
//			conn = ds.getConnection();
//			pstmt = conn.prepareStatement("INSERT INTO SALES_REGION VALUES(?,?)");
//
//			long lngID = getNextRegionID();
//			pstmt.setString(idx++, region.getRegionID());
//			pstmt.setString(idx++, String.valueOf(lngID));
//
//			result = pstmt.executeUpdate();
//
//		} catch(SQLException e) {
//			log.error("insertRegion ",e);
//
//		} catch(NamingException e) {
//			log.error("insertRegion ",e);
//
//		} finally {
//
//			try {
//				if(pstmt != null)
//					pstmt.close();
//				if(conn != null)
//					conn.close();
//			} catch(SQLException e) {
//				log.error("insertRegion ",e);
//			}
//		}
//
//		return (result == 1);
//	}
//
//	public boolean updateRegion(Region region) {
//
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		int result = 0;
//
//		try {
//
//			Context initCtx = new InitialContext();
//			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
//			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
//			int idx = 1;
//
//			conn = ds.getConnection();
//			pstmt = conn.prepareStatement("UPDATE SALES_REGION SET REGION=? WHERE REGION_CODE=?");
//
//			pstmt.setString(idx++, region.getRegionName());
//			pstmt.setString(idx++, region.getRegionID());
//
//			result = pstmt.executeUpdate();
//
//		} catch(SQLException e) {
//			log.error("updateRegion ",e);
//
//		} catch(NamingException e) {
//			log.error("updateRegion ",e);
//
//		} finally {
//
//			try {
//				if(pstmt != null)
//					pstmt.close();
//				if(conn != null)
//					conn.close();
//			} catch(SQLException e) {
//				log.error("updateRegion ",e);
//			}
//		}
//
//		return (result == 1);
//	}

}