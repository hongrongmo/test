package org.ei.system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ei.config.RuntimeProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastClient;
import org.ei.email.EIMessage;
import org.ei.email.EMail;
import org.ei.session.SessionCache;
import org.ei.session.SessionID;
import org.ei.session.UserSession;

public class CheckEV {
	/**
	 * EMail instance used to mail a feedback form
	 **/
	EMail mail;
	RuntimeProperties eiProps;

	private String url = "/controller/servlet/Controller?CID=openXML&dbchkbx=1&DATABASE=1&XQUERYX=%3Cquery%3E%3CandQuery%3E%3Cword+path%3D%22db%22%3Ecpx%3C%2Fword%3E%3Cword%3Eworld%3C%2Fword%3E%3C%2FandQuery%3E%3C%2Fquery%3E&AUTOSTEM=on&STARTYEAR=1990&ENDYEAR=2009&SORT=re&xmlsearch=Submit+Query";
	private String configFile;
	private static RuntimeProperties props;
	private String server;
	private String email;
	private String fastURL;

	public static void main(String[] args) {
		if (args.length > 0) {

			CheckEV cEV = new CheckEV();
			String server = args[0];
			cEV.checkFast();
			cEV.doSearch(server);
		}

	}

	public void init() {
		try {
			mail = EMail.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean checkSessionService(String authURL, String appName,
			String ipAddress, String referrerURL, String username,
			String password) {
		SessionCache sCache = null;
		String entryToken = null;
		UserSession us = null;
		SessionID sesID = null;
		try {
			sCache = SessionCache.init(authURL, appName);
			us = sCache.getUserSession(sesID, ipAddress, referrerURL, username,
					password, entryToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (us == null) {
			sendEmail("Session service is down");
			return false;
		} else {
			return true;
		}
	}

	public boolean checkSearchDatabase() {

		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;
		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
			pstmt = con.prepareStatement("select m_id from bd_master");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String mID = rs.getString("m_id");
				if (mID != null)
					flag = true;
				else
					flag = false;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
				} catch (Exception cpe) {
					cpe.printStackTrace();
				}
			}
		}
		if (!flag) {
			sendEmail("Search database is down");
		}
		return flag;
	}

	protected Connection getConnection(String schema) throws Exception {
		Connection con = null;
		ConnectionBroker broker = ConnectionBroker.getInstance();
		if (schema.equals("search")) {
			con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
		} else if (schema.equals("session")) {
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
		}
		return con;
	}

	public boolean checkFast() {
		BufferedReader in = null;
		boolean testResult = false;

		try {
			String query = "db:cpx";
			FastClient client = new FastClient();
			client.setBaseURL(getFastURL());
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(25);
			client.setQueryString(query);
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();
			int countresult = client.getHitCount();
			if (countresult > -1) {
				return true;
			}
		} catch (Exception e) {
			testResult = false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		sendEmail("Fast is down");
		return testResult;
	}

	public boolean doSearch(String server) {

		InputStream stream = null;
		String resultCount = null;
		boolean searchFlag = false;

		try {
			url = "http://" + server + url;
			HttpClient httpClient = new HttpClient();
			HttpMethod postMethod = new PostMethod(url);
			httpClient.executeMethod(postMethod);
			stream = postMethod.getResponseBodyAsStream();
			EVSaxParser parser = new EVSaxParser();
			parser.parseDocument(stream);
			resultCount = parser.getResultCount();
			if (resultCount != null && Integer.parseInt(resultCount) > -1) {
				searchFlag = true;
			} else {
				searchFlag = false;
				sendEmail("EV is down");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchFlag;

	}

	public void setFastURL(String url) {
		this.fastURL = url;
	}

	private String getFastURL() {
		return fastURL;
	}

	private void sendEmail(String msgString) {
		try {
			EIMessage msg = new EIMessage();

			msg.setMessageBody(msgString);
			msg.setFrom("emergencyalert@elsevier.com");
			msg.setSubject("Emergency: EV is Down");
			msg.setSender("h.mo@elsevier.com");
			msg.addTORecepient("h.mo@elsevier.com");
			String[] emailArray = null;
			if (email != null) {
				if (email.indexOf("|") > -1) {
					emailArray = email.split("|");
				} else {
					emailArray = new String[1];
					emailArray[0] = email;
				}
			}
			for (int i = 0; i < emailArray.length; i++) {
				msg.addCCRecepient(emailArray[i]);
			}

			mail.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
