package org.ei.session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ei.biz.personalization.IEVWebUser;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;

/** Custom properties Loader */

public class CustomProperties {

	private static final String AUTH_POOL = "AuthPool";

	private UserSession userSes;
	private IEVWebUser user;

	public CustomProperties(UserSession userSession) {
		this.userSes = userSession;
		this.user = userSes.getUser();
	}

	public UserSession loadCustom() throws SessionException {
		readLocalHolding();
		return userSes;
	}

	/**
	 * Read the local holding information for the curent customer from the
	 * configuration tables and store it in the localHolding property of the
	 * session. The property is formated as follows: link elements are seperated
	 * by commas and link groups are seperated by vertical bars null fields are
	 * left as a single space Example:
	 * linkLabel,dynamicURL,defaultURL,futureURL|
	 * linkLabel,dynamicURL,defaultURL,futureURL ...
	 */

	private void readLocalHolding() throws SessionException {
		ConnectionBroker broker = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;

		StringBuffer propString = new StringBuffer();

		try {
			if ((user.getCustomerID() != "-") && (user.getContractID() != "0")) {
				sqlQuery = " SELECT LINK_LABEL , DYNAMIC_URL , DEFAULT_URL , FUTURE_URL " + " FROM LOCAL_LINKING_OPTIONS WHERE CUSTOMER_ID = "
						+ user.getCustomerID() + " and CONTRACT_ID = " + user.getContractID();
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(AUTH_POOL);
				stmt = con.createStatement();
				rs = stmt.executeQuery(sqlQuery);

				int rsIdx = 0;

				while (rs.next()) {
					if (rsIdx > 0) {
						propString.append("|");
					}
					propString.append(repNull(rs.getString("LINK_LABEL"), " ") + ",");
					propString.append(repNull(rs.getString("DYNAMIC_URL"), " ") + ",");
					propString.append(repNull(rs.getString("DEFAULT_URL"), " ") + ",");
					propString.append(repNull(rs.getString("FUTURE_URL"), " "));
					rsIdx++;
				}
				userSes.setLocalHoldingKey(propString.toString());
			}
		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.LOCAL_HOLDING_ERROR, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, AUTH_POOL);
				} catch (ConnectionPoolException cpe) {
					cpe.printStackTrace();
				}
			}
		}
	}

	private String repNull(String inStr, String repStr) {
		if (inStr == null) {
			return repStr;
		}
		return inStr;
	}
}