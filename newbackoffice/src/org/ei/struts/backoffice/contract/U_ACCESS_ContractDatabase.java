/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/U_ACCESS_ContractDatabase.java-arc   1.0   Jan 14 2008 17:10:30   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:30  $
 *
 */
package org.ei.struts.backoffice.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;

public class U_ACCESS_ContractDatabase {

	private static Log log = LogFactory.getLog("U_ACCESS_ContractDatabase");

	public Contract find_U_ACCESS_Contract(String contractid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Contract aContract = null;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM CONTRACT_DATA WHERE CONTRACT_ID=?");
			pstmt.setString(idx++, contractid);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				aContract = new Contract();
				aContract.setContractID(rs.getString("CONTRACT_ID"));
				aContract.setCustomerID(rs.getString("CUST_ID"));
			}
		} catch(SQLException e) {
			log.error("find_U_ACCESS_Contract ",e);

		} catch(NamingException e) {
			log.error("find_U_ACCESS_Contract ",e);

		} finally {

			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				log.error("find_U_ACCESS_Contract ",e);
			}
		}

		return aContract;
	}

	public Contract saveContract(Contract contract) {

		Contract result = null;

		try {

			if(find_U_ACCESS_Contract(contract.getContractID()) != null) {
				result = update_U_ACCESS_Contract(contract);
			} else {
				result = insert_U_ACCESS_Contract(contract);
			}

		} catch(Exception e) {
			log.error("savecontract ",e);
		}

		return result;
	}


	private Contract update_U_ACCESS_Contract(Contract contract) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("UPDATE CONTRACT_DATA SET STATUS=? WHERE CONTRACT_ID=?");

			pstmt.setString(idx++, (contract.getEnabled() == Constants.ENABLED  ? Constants.Y : Constants.N ));
			pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("update_U_ACCESS_Contract ",e);

		} catch(NamingException e) {
			log.error("update_U_ACCESS_Contract ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch(SQLException e) {
				log.error("update_U_ACCESS_Contract ",e);
			}
		}

		return contract;
	}

	private Contract insert_U_ACCESS_Contract(Contract contract) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds = (DataSource) envCtx.lookup(Constants.U_ACCESS_DBCP_POOL);
			int idx = 1;

			conn = ds.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO CONTRACT_DATA (CONTRACT_ID, CUST_ID, FIRM_NAME, STATUS, INDEX_ID) VALUES(?,?,?,?,?)");


			pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));
			pstmt.setLong(idx++, Long.parseLong(contract.getCustomerID()));
			pstmt.setString(idx++, contract.getCustomer().getName());
			pstmt.setString(idx++, (contract.getEnabled() == Constants.ENABLED  ? Constants.Y : Constants.N ));
			pstmt.setLong(idx++, Long.parseLong(contract.getContractID()));

			result = pstmt.executeUpdate();

		} catch(SQLException e) {
			log.error("insert_U_ACCESS_Contract ",e);

		} catch(NamingException e) {
			log.error("insert_U_ACCESS_Contract ",e);

		} finally {

			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch(SQLException e) {
				log.error("insert_U_ACCESS_Contract ",e);
			}
		}

		return contract;
	}

}
