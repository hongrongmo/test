package org.ei.controller;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.ei.controller.content.ContentDescriptor;
import org.ei.domain.DocumentBasket;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.SessionCache;
import org.ei.session.UserSession;

public class DataResponseCache {
	private final static Logger log4j = Logger.getLogger(DataResponseCache.class);

	public DataResponseCache(String cacheDir) {

	}

	public DataResponse getDataResponse(OutputPrinter printer, DataRequest dataRequest) throws ServiceException {

		DataResponse dataResponse = null;
		HttpURLConnection con = null;
		BufferedWriter outWriter = null;
		InputStream inStream = null;
		SectionStream sectionStream = null;

		try {

			/*
			 * 1) Get the dataSourceURL and open the connection.
			 */
			ContentDescriptor cd = dataRequest.getContentDescriptor();
			String dataSourceURL = cd.getDataSourceURL();
			log4j.info("Connecting to '" + dataSourceURL + "' to service request...");
			URL dataURL = null;
			dataURL = new URL(dataSourceURL);
			con = (HttpURLConnection) dataURL.openConnection();
			con.setRequestMethod("GET");
			// TMH - set the User-Agent so we can see calls in the log
			con.setRequestProperty("User-Agent", "EngineeringVillage DataResponseCache");
			con.setDoOutput(true);
			con.setConnectTimeout(60000);

			/*
			 * 2) Put the session information in the out-going headers.
			 */

			UserSession us = dataRequest.getUserSession();

			Properties sesProps = us.unloadToProperties();
			Enumeration<?> sesKeys = sesProps.keys();

			while (sesKeys.hasMoreElements()) {
				String key = (String) sesKeys.nextElement();
				con.setRequestProperty(key, sesProps.getProperty(key));
			}

			/*
			 * 3) Get the request parameters and send them to the dataSource.
			 */

			Properties requestParams = dataRequest.getRequestParameters();

			try {
				outWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			} catch (Exception e) {
				if (e.getMessage().trim().equals("Connection refused")) {
					// System.out.println("Fatal Error.....");
					throw new ServiceException(SystemErrorCodes.DATA_SERVICE_CONNECTION_ERROR, "Unable to connect to service!  URL: " + dataSourceURL, e);
				}
			}

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
			outWriter.flush();
			outWriter.close();
			outWriter = null;

			inStream = con.getInputStream();

			/*
			 * 4) Get the logging and session info from the headers.
			 */

			Properties logProps = new Properties();
			boolean sessionUpdated = false;
			UserSession nsession = new UserSession();
			Properties sessionProps = new Properties();
			String redirectURL = null;
			String viewURL = null;
			ArrayList<String> comments = new ArrayList<String>();
			String strCdFilename = null;

			int j = 1;

			while (true) {
				String hkey = con.getHeaderFieldKey(j);

				if (hkey == null) {
					break;
				}

				if (hkey.indexOf("SESSION") > -1) {
					sessionProps.setProperty(hkey, con.getHeaderField(j));
					sessionUpdated = true;
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
					throw new ServletException("Exception from engvillage layer: " + con.getHeaderField(j));
				}

				++j;
			}

			if (sessionUpdated) {

				SessionCache sCache = SessionCache.getInstance();
				nsession.loadFromProperties(sessionProps);

				System.currentTimeMillis();
				nsession = sCache.updateUserSession(nsession);
				System.currentTimeMillis();
			} else {
				nsession.setTouched(false);
				nsession = dataRequest.getUserSession();
			}

			/*
			 * 5) Check to see if redirect was indicated above.
			 */
			if (redirectURL != null) {
				// TMH - update reirectURL instead of updating response
				redirectURL = printer.getModelRedirect(redirectURL, nsession.getSessionID());
				/*
				 * printer.printModelRedirect(redirectURL,
				 * nsession.getSessionID());
				 */
			}

			/*
			 * 6) No redirect - Deal with the incoming XML. TMH - only do this
			 * when NOT using one of the new URLs
			 */

			else {
				printer.setContentType(cd.getMimeType());
				// if the FILENAME header was set, then send
				// it on to the printer
				if (strCdFilename != null)
					printer.setContentDisposition(strCdFilename);

				// Transform to an IBizBean object NOT to the printer
				if (cd.isRefresh()) {
					dataRequest.getBizBean().processModelXml(inStream);
					dataRequest.getBizBean().setComments(comments);
				}

				// TODO document bulk mode. What is it???
				else if (!cd.isBulkmode()) {
					printer.setComments(comments);
					printer.print(getDisplayURL(cd.getDisplayURL(), viewURL), inStream, nsession);
				}

				// This request has XML in the data URL that needs to be
				// transformed via a stylesheet specified in the display
				// URL
				else {
					int bufferIndex = -1;
					sectionStream = new SectionStream(inStream);

					// Get the header and transform it
					byte[] header = sectionStream.readHeader();
					bufferIndex = sectionStream.getBufferIndex();
					ByteArrayInputStream bi = new ByteArrayInputStream(header, 0, bufferIndex);
					printer.print(cd.getDisplayURL(), bi, nsession);

					// Get the records and transform them one at a time
					byte[] record = null;
					int i = 0;
					while ((record = sectionStream.readRecord()) != null) {
						bufferIndex = sectionStream.getBufferIndex();
						bi = new ByteArrayInputStream(record, 0, bufferIndex);

						printer.print(cd.getDisplayURL(), bi, nsession);
						i++;
						if (i > DocumentBasket.maxSize) {
							throw new Exception("Section Stream Exception:" + debugBuffer.toString());

						}
					}
					log4j.info("Number of bulk mode records:" + i);

					// Get the footer and transform it
					byte[] footer = sectionStream.readFooter();
					bufferIndex = sectionStream.getBufferIndex();
					bi = new ByteArrayInputStream(footer, 0, bufferIndex);
					printer.print(cd.getDisplayURL(), bi, nsession);
					sectionStream.close();
					// long end = System.currentTimeMillis();
					// long dif = end - begin;
					// System.out.println("Record Transform Time:"+ dif);
				}
			}

			dataResponse = new DataResponse(dataRequest, nsession, logProps);
			dataResponse.setRedirectURL(redirectURL);

		} catch (Exception e) {
			ServiceException e2 = new ServiceException(SystemErrorCodes.UNKNOWN_SERVICE_ERROR, "Unable to call data service!", e);
			e2.setStackTrace(e.getStackTrace());
			throw (e2);
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

			if (sectionStream != null) {
				try {
					sectionStream.close();
				} catch (Exception e) {
					e.printStackTrace();
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

	private String getDisplayURL(String defaultURL, String customURL) {
		if (defaultURL == null) {
			log4j.warn("Display URL is null!");
			return null;
		}
		if (customURL != null) {
			String[] urlparts = defaultURL.split("/");
			StringBuffer urlbase = new StringBuffer("http://");
			urlbase.append(urlparts[2]);
			urlbase.append(customURL);

			return urlbase.toString();
		}

		return defaultURL;
	}
}
