package org.ei.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.exception.ErrorXml;
import org.ei.exception.ExceptionWriter;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.User;
import org.ei.session.UserSession;

public class DataResponseForActionBean {
	private final static Logger log4j = Logger.getLogger(DataResponseForActionBean.class);

	private String cacheDir;

	public DataResponseForActionBean() {
	}

	public DataResponseForActionBean(String cacheDir) {
		this.cacheDir = cacheDir;

	}

	public DataResponse getDataResponse(OutputPrinter printer, DataRequest dataRequest, String xmlUrl) throws ServiceException, InfrastructureException {

		DataResponse dataResponse = null;
		HttpURLConnection con = null;
		BufferedWriter outWriter = null;
		InputStream inStream = null;

		try {
			URL dataURL = null;
			log4j.info("Connecting to '" + xmlUrl + "' to retrieve XML...");
			dataURL = new URL(xmlUrl);
			con = (HttpURLConnection) dataURL.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setConnectTimeout(60000);
            con.setRequestProperty("content-type","application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("accept-charset", "UTF-8");

			UserSession usersession = dataRequest.getUserSession();
			if (usersession != null) {
				Properties sesProps = usersession.unloadToProperties();
				Enumeration<?> sesKeys = sesProps.keys();

				while (sesKeys.hasMoreElements()) {
					String key = (String) sesKeys.nextElement();
					con.setRequestProperty(key, sesProps.getProperty(key));
				}
			}

			try {
				outWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			} catch (Exception e) {
				if (e.getMessage().trim().equals("Connection refused")) {
					throw new ServiceException(SystemErrorCodes.DATA_SERVICE_CONNECTION_ERROR, "SHUTDOWN");
				}
			}

			Properties requestParams = dataRequest.getRequestParameters();
			Enumeration<?> paramKeys = requestParams.keys();
			StringBuffer debugBuffer = new StringBuffer();
			while (paramKeys.hasMoreElements()) {
				String paramKey = (String) paramKeys.nextElement();
				String[] values = (String[]) requestParams.get(paramKey);
				for (int i = 0; i < values.length; i++) {
					debugBuffer.append(paramKey + "=" + values[i] + "&");
					outWriter.write(paramKey + "=" + URLEncoder.encode(values[i], "UTF-8"));
					if (i < (values.length - 1)) {
						outWriter.write("&");
					}
				}
				if (paramKeys.hasMoreElements()) {
					outWriter.write("&");
				}
			}
			// TMH - special code to add the CID from the ActionBean. These have
			// been
			// removed from most URLs so the ActionBean may need to set them
			// directly
			String CID = dataRequest.getBizBean().getCID();
			if (!GenericValidator.isBlankOrNull(CID)) {
				outWriter.write("&CID=" + CID);
			}
			outWriter.flush();
			outWriter.close();
			outWriter = null;

			inStream = con.getInputStream();

			Properties logProps = new Properties();
			Properties sessionProps = new Properties();
			String redirectURL = null;
			String viewURL = null;
			User u = null;
			boolean cache = false;
			long timeToLive = 1L;
			ArrayList<String> comments = new ArrayList<String>();
			String strCdFilename = null;

			int j = 1;

			ErrorXml errorXml = null;
			while (true) {
				String hkey = con.getHeaderFieldKey(j);

				if (hkey == null) {
					break;
				}

				if (hkey.indexOf("SESSION") > -1) {
					sessionProps.setProperty(hkey, con.getHeaderField(j));
				}
				if (hkey.indexOf("FILENAME") > -1) {
					strCdFilename = con.getHeaderField(j);
				} else if (hkey.indexOf("COMMENT") > -1) {
					comments.add(con.getHeaderField(j));
				} else if (hkey.indexOf("LOG") > -1) {
					// Get logging information.
					String lkey = hkey.substring(hkey.indexOf(".") + 1, hkey.length());
					logProps.put(lkey, con.getHeaderField(j));
				} else if (hkey.indexOf("REDIRECT") > -1) {
					redirectURL = con.getHeaderField(j);
				} else if (hkey.indexOf("VIEW") > -1) {
					viewURL = con.getHeaderField(j);
				} else if (hkey.indexOf("EXCEPTION") > -1) {
					errorXml = ExceptionWriter.toObject(inStream);
				}

				++j;
			}

			// System.out.println( new
			// Scanner(inStream).useDelimiter("\\A").next());
			dataResponse = new DataResponse(dataRequest, usersession, logProps);
			if (errorXml != null) {
				// Error/Exception happened from data service call.
				dataResponse.setErrorXml(errorXml);
				dataRequest.getBizBean().setComments(comments);
			} else if (redirectURL != null) {
				// Redirect
				dataResponse.setRedirectURL(redirectURL);
			} else {
				dataRequest.getBizBean().processModelXml(inStream);
				dataRequest.getBizBean().setComments(comments);
			}
		} catch (MalformedURLException e) {
		    log4j.error("Unable to build DataResponse!", e);
			ServiceException se = new ServiceException(SystemErrorCodes.DATA_SERVICE_BAD_URL,  e);
			se.setStackTrace(e.getStackTrace());
			throw se;
		} catch (IOException e) {
            log4j.error("Unable to build DataResponse!", e);
			ServiceException se = new ServiceException(SystemErrorCodes.DATA_SERVICE_CONNECTION_ERROR, e);
			se.setStackTrace(e.getStackTrace());
			throw se;
		} finally {

			if (outWriter != null) {
				try {
					outWriter.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			if (con != null) {
				try {
					con.disconnect();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		}

		return dataResponse;
	}

}
