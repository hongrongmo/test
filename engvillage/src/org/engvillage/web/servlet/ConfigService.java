package org.engvillage.web.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;

/**
 * This servlet is used in 2 ways:
 * 1) From the engvillage app, the ApplicationProperties are used from the static accessor.
 * 2) From the controller app, both the ApplicationProperties and ContentConfig files are
 * accessed by HTTP connection
 *
 * @author harovetm
 *
 */
@SuppressWarnings("serial")
public class ConfigService extends HttpServlet {
	private static ApplicationProperties props;

	private static ServletContext context;

	private Logger log4j = Logger.getLogger(ConfigService.class);

	public void init() throws ServletException {

		context = getServletContext();

		try {
			log4j.debug("Config service starting up. Configuring system...");
			props = ApplicationProperties.getInstance();

		} catch (Exception e) {
			log("Init Error:", e);
			throw new ServletException(e.getMessage(), e);
		}
	}

	public static ApplicationProperties getApplicationProperties() {
		return props;
	}

	public static String getConfigPath(String configName) {
		String configURL = null;

		try {
			configURL = context.getRealPath("/WEB-INF/" + configName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return configURL;
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ServletOutputStream out = response.getOutputStream();
		BufferedReader reader = new BufferedReader(new FileReader(
				getConfigPath(request.getParameter("configName"))));
		try {
			String s = null;
			while ((s = reader.readLine()) != null) {
				out.println(s);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
