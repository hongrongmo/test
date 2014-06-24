package org.ei.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.personalization.EVWebUser;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.TimedObject;

public class UserBroker {
	private static final String AUTH_POOL = "AuthPool";
	private TimedObject gatewayListTO;
	private final static Logger log4j = Logger.getLogger(UserBroker.class);

	public String validateCustomerIP(String prodname, String ipAddress) throws SessionException {
		Connection con = null;
		try {
			con = getConnection(AUTH_POOL);
			String customerID = getCustomerIDByIp(ipAddress, prodname, con);
			if (customerID != null) {
				return customerID;
			} else {
				return "invalid_ip";
			}
		} catch (Exception e1) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e1);
		} finally {
			try {
				replaceConnection(con, AUTH_POOL);
			} catch (Exception e) {
				throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
			}
		}
	}

	public boolean isLegacyUser(String prodname, String username, String password) {
		boolean legacyuser = false;
		IEVWebUser u = null;
		Connection con = null;

		try {
			long begin = System.currentTimeMillis();
			con = getConnection(AUTH_POOL);

			log4j.info("Looking for '" + username + "' with password '" + password + "'");
			u = getUserByUsername(username, password, prodname, con);

			legacyuser = (u != null);
		} catch (Exception e1) {
			log4j.error("Unable to lookup user: " + e1.getMessage());
		} finally {
			try {
				replaceConnection(con, AUTH_POOL);
			} catch (Exception e) {
			}
		}

		return legacyuser;
	}

	public IEVWebUser getUser(String prodname, String username, String password, String ipAddress, String refURL, String entryToken) throws SessionException {

		IEVWebUser u = null;
		Connection con = null;

		try {
			long begin = System.currentTimeMillis();
			con = getConnection(AUTH_POOL);

			/*
			 * First check the entry token. Short circuit all other auth
			 * procedures if entry token is present
			 */

			if (entryToken != null) {
				log4j.info("Looking for entry token:" + entryToken);
				u = getUserByEntryToken(entryToken, con);
				u.setIpAddress(ipAddress);
				u.setUsername(User.USERNAME_ENTRY_TOKEN);
				return u;
			}

			/*
			 * Next check for username and password. Short circuit all other
			 * auth procedures if username and password is present.
			 */

			if (username != null && password != null) {
				log4j.info("Looking for username and password");
				u = getUserByUsername(username, password, prodname, con);
				if (u == null) {
					log4j.info("No customer found returning blank user");
					u = new EVWebUser();
					u.setIpAddress(ipAddress);
					u.setUsername(User.USERNAME_INDIV_AUTH_FAIL);
					return u;
				} else {
					log4j.info("Found customer found returning user with customer info");
					u.setIpAddress(ipAddress);
					return u;
				}
			}

			/*
			 * If entry entry token and username are not present the check
			 * refURL Don't short circuit if not found.
			 */

			if (refURL != null) {
				log4j.info("Looking for rerURL:" + refURL);
				u = getUserByRefURL(refURL, prodname, con);
				if (u != null) {
					u.setIpAddress(ipAddress);
					u.setUsername(User.USERNAME_REFERRER_URL);
					return u;
				}
			}

			/*
			 * If we've got this far then check for IP address.
			 */

			u = getUserByIp(ipAddress, prodname, con);
			if (u != null) {
				u.setIpAddress(ipAddress);
				u.setUsername(User.USERNAME_IP_AUTH);
				return u;
			}

			/*
			 * All forms of authentication failed so create a new User with no
			 * customer info
			 */

			u = new EVWebUser();
			u.setIpAddress(ipAddress);
			u.setUsername(User.USERNAME_AUTH_FAIL);
		} catch (Exception e1) {
            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e1);
		} finally {
			try {
				replaceConnection(con, AUTH_POOL);
			} catch (Exception e) {
	            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
			}
		}

		return u;

	}

	private IEVWebUser getUserByEntryToken(String entryToken, Connection con) throws Exception {
		IEVWebUser user = new EVWebUser();
		EntryTokenBroker eb = new EntryTokenBroker(con);
		EntryToken et = eb.startToken(entryToken);
		if (et != null) {
			user.setCustomerID("10");
			user.setContractID(Integer.toString(et.getCampaign()));
			user.setCartridge(parseCartridge(et.getCredentials(), et));
			user.setStartPage(Defaults.STARTPAGE);
			user.setDefaultDB(Defaults.DEFAULTDB);
			user.setEntryToken(et);
		}

		return user;
	}

	private IEVWebUser getUserByUsername(String username, String password, String prodname, Connection con) throws SessionException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		IEVWebUser user = null;

		try {
			StringBuffer qBuf = new StringBuffer();
			qBuf.append("select * from user_pass_data u ");
			qBuf.append("where u.username = ? and u.password = ?  and u.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				user = new EVWebUser();
				user.setCustomerID(rs.getString("cust_id"));
				user.setContractID(rs.getString("contract_id"));

				if (statusOn(con, user.getContractID())) {
					user = populate(con, user, prodname);
					user.setUsername(username);
				} else {
					user = null;
				}

				break;
			}

		} catch (Exception e) {
            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

		return user;

	}

	private synchronized List<GatewayData> getGatewayURLs(Connection con) throws Exception {

		ArrayList<GatewayData> list = null;

		Statement stmt = null;
		ResultSet rs = null;

		if (this.gatewayListTO != null && !this.gatewayListTO.expired()) {
			log4j.info("Got Ref URL from cache");
			return (List<GatewayData>) gatewayListTO.getObject();
		}

		list = new ArrayList<GatewayData>();

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from GATEWAY_DATA");
			while (rs.next()) {
				GatewayData g = new GatewayData();
				g.setURL(rs.getString("URL"));
				g.setContractID(rs.getString("CONTRACT_ID"));
				g.setCustID(rs.getString("CUST_ID"));
				g.setProdName(rs.getString("PROD_NAME"));
				list.add(g);
			}

			TimedObject gto = new TimedObject();
			gto.setExpireIn(3600000);
			gto.setObject(list);
			gatewayListTO = gto;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	private IEVWebUser getUserByRefURL(String refURL, String prodname, Connection con) throws SessionException {

		IEVWebUser user = null;

		try {

			List<GatewayData> gatewayURLs = getGatewayURLs(con);
			for (int i = 0; i < gatewayURLs.size(); i++) {
				GatewayData g = (GatewayData) gatewayURLs.get(i);

				if (g.matches(refURL, prodname)) {
					user = new EVWebUser();
					user.setCustomerID(g.getCustID());
					user.setContractID(g.getContractID());
					if (statusOn(con, user.getContractID())) {
						user = populate(con, user, prodname);
						user.setUsername(User.USERNAME_REFERRER_URL);
					} else {
						user = null;
					}

					break;
				}
			}
		} catch (Exception e) {
            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		}

		return user;
	}

	/**
	 * Lightweight method used to re-validate a customerID by IP address
	 **/

	private String getCustomerIDByIp(String ipAddress, String prodname, Connection con) throws SessionException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String customerID = null;
		try {
			StringBuffer ipBuffer = new StringBuffer();
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s.length() == 1) {
					s = "00" + s;
				} else if (s.length() == 2) {
					s = "0" + s;
				}

				ipBuffer.append(s);
			}

			ipAddress = ipBuffer.toString();
			StringBuffer qBuf = new StringBuffer();

			qBuf.append("select * from ip_data i where ");
			qBuf.append("i.high_ip >= ? and i.low_ip <= ? and i.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, ipAddress);
			pstmt.setString(2, ipAddress);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("cust_id");
			}
		} catch (Exception e) {
            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		return customerID;
	}

	private IEVWebUser getUserByIp(String ipAddress, String prodname, Connection con) throws SessionException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		IEVWebUser user = null;
		try {

			StringBuffer ipBuffer = new StringBuffer();
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s.length() == 1) {
					s = "00" + s;
				} else if (s.length() == 2) {
					s = "0" + s;
				}

				ipBuffer.append(s);
			}

			ipAddress = ipBuffer.toString();
			StringBuffer qBuf = new StringBuffer();

			qBuf.append("select * from ip_data i where ");
			qBuf.append("i.high_ip >= ? and i.low_ip <= ? and i.prod_name = ?");
			pstmt = con.prepareStatement(qBuf.toString());
			pstmt.setString(1, ipAddress);
			pstmt.setString(2, ipAddress);
			pstmt.setString(3, prodname);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				user = new EVWebUser();
				user.setCustomerID(rs.getString("cust_id"));
				user.setContractID(rs.getString("contract_id"));

				if (statusOn(con, user.getContractID())) {
					user = populate(con, user, prodname);
					user.setUsername(User.USERNAME_IP_AUTH);
					break;
				} else {
					user = null;
				}
			}
		} catch (Exception e) {
            throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

		return user;
	}

	private boolean statusOn(Connection con, String contractID) throws Exception {
		boolean on = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement("select STATUS from CONTRACT_DATA where CONTRACT_ID = ?");
			pstmt.setString(1, contractID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String status = rs.getString("STATUS");
				if (status.equalsIgnoreCase("Y")) {
					on = true;
				}
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return on;

	}

	private IEVWebUser populate(Connection con, IEVWebUser user, String prodname) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String carray[] = null;
		String startPage = null;
		String defaultDB = null;
		String refEmail = null;
		try {
			pstmt = con.prepareStatement("Select * from EV2_CUSTOMER_OPTION where CONTRACT_ID = ?");
			pstmt.setString(1, user.getContractID());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				carray = parseCartridge(rs.getString("CARTRIDGE"));
				startPage = rs.getString("STARTPAGE");
				defaultDB = rs.getString("DEFAULTDB");
				refEmail = rs.getString("REFEMAIL");
				break;
			}

			if (carray == null) {
				user.setCartridge(Defaults.CARRAY);
				user.setStartPage(Defaults.STARTPAGE);
				user.setDefaultDB(Defaults.DEFAULTDB);
			} else {
				user.setCartridge(carray);
				if (startPage != null && startPage.length() > 0) {
					user.setStartPage(startPage);
				}
				if (defaultDB != null && defaultDB.length() > 0) {
					user.setDefaultDB(defaultDB);
				}
				if (refEmail != null && refEmail.length() > 0)

				{
					user.setEmail(refEmail);
				}
			}
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return user;
	}

	private String[] parseCartridge(String bcartridge) {
		String cartridge = addTnfPerpetualCartridge(bcartridge);
		if (cartridge.charAt(cartridge.length() - 1) == ';') {
			cartridge = cartridge.substring(0, cartridge.length() - 1);
		}
		StringTokenizer tokens = new StringTokenizer(cartridge, ";");
		String[] carray = new String[tokens.countTokens()];
		int x = 0;
		while (tokens.hasMoreTokens()) {
			carray[x] = tokens.nextToken();
			++x;
		}

		return carray;
	}

	private String addTnfPerpetualCartridge(String cartridge) {
		StringBuffer cartridgeBuffer = new StringBuffer(cartridge);
		if (cartridge.toUpperCase().indexOf("BPE") < 0) {
			if ((cartridge.toUpperCase().indexOf("ELE") > -1) || (cartridge.toUpperCase().indexOf("CHE") > -1) || (cartridge.toUpperCase().indexOf("CIV") > -1)
					|| (cartridge.toUpperCase().indexOf("COM") > -1) || (cartridge.toUpperCase().indexOf("MAT") > -1)
					|| (cartridge.toUpperCase().indexOf("SEC") > -1)) {
				if (cartridge.charAt(cartridge.length() - 1) != ';') {
					cartridgeBuffer.append(";");
				}
				cartridgeBuffer.append("BPE;");

				String[] cartridgeArray = cartridge.split(";");
				for (int i = 0; i < cartridgeArray.length; i++) {
					String singleCartridge = cartridgeArray[i];
					if (singleCartridge.equals("CHE")) {
						cartridgeBuffer.append("CHE1;CHE2;CHE3;CHE4;CHE5;CHE6;CHE7;CHE8;");
					} else if (singleCartridge.equals("TNFCHE")) {
						cartridgeBuffer.append("TNFCHE1;TNFCHE7;");
					} else if (singleCartridge.equals("CIV")) {
						cartridgeBuffer.append("CIV1;CIV2;CIV3;CIV4;CIV5;CIV6;CIV7;CIV8;");
					} else if (singleCartridge.equals("TNFCIV")) {
						cartridgeBuffer.append("TNFCIV3;TNFCIV4;TNFCIV6;TNFCIV7;");
					} else if (singleCartridge.equals("ELE")) {
						cartridgeBuffer.append("ELE1;ELE2;ELE3;ELE4;ELE5;ELE6;ELE7;ELE8;");
					} else if (singleCartridge.equals("MAT")) {
						cartridgeBuffer.append("MAT1;MAT2;MAT3;MAT4;MAT5;MAT6;MAT7;MAT8;");
					} else if (singleCartridge.equals("TNFMAT")) {
						cartridgeBuffer.append("TNFMAT1;TNFMAT3;TNFMAT4;TNFMAT6;TNFMAT7;");
					} else if (singleCartridge.equals("SEC")) {
						cartridgeBuffer.append("SEC1;SEC2;SEC3;SEC4;SEC5;SEC6;SEC7;SEC8;");
					} else if (singleCartridge.equals("COM")) {
						cartridgeBuffer.append("COM1;COM2;COM3;COM4;COM5;COM6;COM7;COM8;");
					}

				}

			}
		}
		return cartridgeBuffer.toString();

	}

	private String[] parseCartridge(String bcartridge, EntryToken entryToken) {
		String cartridge = addTnfPerpetualCartridge(bcartridge);
		StringTokenizer tokens = new StringTokenizer(cartridge, ";");
		String[] carray = new String[tokens.countTokens() + 1];
		int x = 0;
		while (tokens.hasMoreTokens()) {
			carray[x] = tokens.nextToken();
			++x;
		}

		StringBuffer buf = new StringBuffer();
		buf.append("PPD~");
		buf.append(Long.toString(entryToken.getStartTime()));
		buf.append("~");
		buf.append(Long.toString(entryToken.getSessionLength()));
		carray[x] = buf.toString();

		return carray;
	}

	private Connection getConnection(String authCon) throws Exception {
		ConnectionBroker broker = ConnectionBroker.getInstance();
		return broker.getConnection(authCon);
	}

	private void replaceConnection(Connection con, String authCon) throws Exception {
		ConnectionBroker broker = ConnectionBroker.getInstance();
		broker.replaceConnection(con, authCon);
	}

	class GatewayData {
		private String URL;
		private String prodName;
		private String contractID;
		private String custID;

		public void setURL(String URL) {
			this.URL = URL;
		}

		public String getURL() {
			return this.URL;
		}

		public void setProdName(String prodName) {
			this.prodName = prodName;
		}

		public String getProdName() {
			return this.prodName;
		}

		public String getContractID() {
			return this.contractID;
		}

		public void setContractID(String contractID) {
			this.contractID = contractID;
		}

		public String getCustID() {
			return this.custID;
		}

		public void setCustID(String custID) {
			this.custID = custID;
		}

		public boolean matches(String refURL, String pName) {
			boolean b = false;
			int i = URL.length();
			char c1 = URL.charAt(0);
			char c2 = URL.charAt(i - 1);

			if (!prodName.equals(pName)) {
				return false;
			}

			if (c1 == '%' && c2 == '%') {

				String s = URL.substring(1, i - 1);
				if (refURL.toLowerCase().indexOf(s.toLowerCase()) > -1) {
					b = true;
				} else {
					b = false;
				}

			} else if (c1 != '%' && c2 == '%') {

				String s = URL.substring(0, i - 1);
				if (refURL.toLowerCase().indexOf(s.toLowerCase()) == 0) {
					b = true;
				} else {
					b = false;
				}
			} else if (c1 != '%' && c2 != '%') {
				if (URL.equalsIgnoreCase(refURL)) {
					b = true;
				} else {
					b = false;
				}
			}

			return b;
		}
	}
}