package org.ei.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.RuntimeProperties;

public class HealthCheck extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log4j = Logger.getLogger(HealthCheck.class);

	public void init() throws ServletException {
	}

	/**
	 * The HealthCheck should NOT complete for the ROOT project unless the
	 * data service (engvillage) is available.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Check 1: ensure RuntimeProperties instance available
		RuntimeProperties rtp = RuntimeProperties.getInstance();
		if (rtp == null) {
			log4j.error("RuntimeProperties object cannot be created!");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "EV2 Instance Unavailable - Runtime Properties not set!");
			return;
		}

		// Check 2: ensure engvillage (data service) is available
		String dataurl = RuntimeProperties.getInstance().getDataUrl();
		if (GenericValidator.isBlankOrNull(dataurl)) {
			log4j.error("Data url (engvillage app) is empty!");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "EV2 Instance Unavailable - Data URL not set!");
			return;
		}

		// Create a new connection to the data service
		HttpURLConnection connection = null;
        InputStream is = null;
		PrintWriter out = null;
		try {
			URL url = new URL(dataurl + "/engvillage/models/world/healthcheck.jsp");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "text/html");
			connection.connect();
			
	        int status = connection.getResponseCode();
	        if (status != HttpStatus.SC_OK) {
				log4j.error("Data url response code was NOT 200:  " + status + "!");
				response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "EV2 Instance Unavailable - Data URL response invalid (" + status + ")!");
				return;
	        }else {
	        	// Write contents of response from healthcheck.jsp
				out = response.getWriter();
	            is = connection.getInputStream();
	            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	            String line;
	            StringBuffer writeme = new StringBuffer();
	            while ((line = rd.readLine()) != null) {
	                out.append(line);
	            }
	            rd.close();
	            out.flush();
	        } 
			response.setContentType("text/plain");
			log4j.warn("Health check successful!");
		} catch (Exception e) {
			log4j.error("Unable to connect to '" + dataurl + "/models/world/healthcheck.jsp'!", e);
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,	"EV2 Instance Unavailable - Exception occurred (" + e.getMessage() + ")");
			return;
		} finally {
			if (is != null) {
				is.close();
			}
			if (out != null) {
				out.close();
			}
		}


	}
}
