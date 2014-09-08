/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/product/ProductDatabase.java-arc   1.1   Feb 02 2009 16:34:04   johna  $
 * $Revision:   1.1  $
 * $Date:   Feb 02 2009 16:34:04  $
 *
 */
package org.ei.struts.backoffice.product;

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


public class ProductDatabase {

  private static Log log = LogFactory.getLog("ProductDatabase");

	public Product createProduct() {

		Product aProduct = null;

		aProduct = new Product();

		return aProduct ;

	}


	public Product findProduct(String productid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Product aProduct = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM EI_PRODUCT WHERE PROD_ID=?");
			pstmt.setString(idx++, productid);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				aProduct = new Product();
				aProduct.setProductID(rs.getString("PROD_ID"));
				aProduct.setName(rs.getString("PROD_NAME"));
				aProduct.setDescription(rs.getString("PROD_DESCRIPTION"));
			}
			else {
				aProduct = new Product();
				aProduct.setProductID(productid);
				aProduct.setName("Unknown");
				aProduct.setDescription("N/A");
			}
		} catch(SQLException e) {
			log.error("findProduct ",e);

		} catch(NamingException e) {
			log.error("findProduct ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("findProduct ",e);
			}
		}

		return aProduct;
	}


	public Collection getProducts() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Collection allProducts = new ArrayList();

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			// sorr by product ID to get EV2 to the Top of list
			pstmt = conn.prepareStatement("SELECT * FROM EI_PRODUCT ORDER BY PROD_ID ASC");
			rs = pstmt.executeQuery();

			Collection activeProducts = Constants.getActiveProducts();
			Product aProduct = null;
			while(rs.next()) {

				if(activeProducts.contains(rs.getString("PROD_ID"))) {
	  				aProduct = new Product();
	    			aProduct.setProductID(rs.getString("PROD_ID"));
	  				aProduct.setName(rs.getString("PROD_NAME"));
	  				aProduct.setDescription(rs.getString("PROD_DESCRIPTION"));

  					allProducts.add(aProduct);
		        }
			}

		} catch(SQLException e) {
			log.error("getProducts ",e);

		} catch(NamingException e) {
			log.error("getProducts ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("getProducts ",e);
			}
		}

		return allProducts;
	}

}