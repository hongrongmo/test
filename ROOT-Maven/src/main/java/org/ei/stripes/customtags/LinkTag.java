package org.ei.stripes.customtags;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.stripes.util.HttpRequestUtil;

/**
 * This extended link tag allows a protocol to be specified and picks up the
 * port from system configuration.
 */
public class LinkTag extends net.sourceforge.stripes.tag.LinkTag {
	private static Logger log4j = Logger.getLogger(LinkTag.class);

	private String protocol = null;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol.toLowerCase();
	}

	public String getPort(HttpServletRequest request) {
		// If the current request has a port, use it!
		if (request.getServerPort() > 0 && request.getServerPort() != 80 && request.getServerPort() != 443) {
			return ":" + request.getServerPort();
		}

		// Otherwise, see if there is a runtime property set...
		try {
			int http_port = Integer.parseInt(EVProperties.getRuntimeProperty(RuntimeProperties.HTTP_PORT));
			int https_port = Integer.parseInt(EVProperties.getRuntimeProperty(RuntimeProperties.HTTPS_PORT));
			String protocol = getProtocol();
			if (protocol != null && "https".equals(protocol) && https_port > 0 && https_port != 443) {
				return ":" + https_port;
			} else if (protocol != null && "http".equals(protocol) && http_port > 0 && http_port != 80) {
				return ":" + http_port;
			}
		} catch (Throwable t) {
			log4j.warn("Unable to convert use port settings from Runtime.properties!");
		}
		return "";
	}

	@Override
	public int doEndTag() throws JspException {
		String action = getUrl();

		HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();

		URL currentUrl = null;
		try {
			currentUrl = new URL(request.getRequestURL().toString());
		} catch (MalformedURLException e) {
			throw new JspTagException("Invalid request URL: " + request.getRequestURL(), e);
		}

		String currentProtocol = currentUrl.getProtocol();
		String currentHost = currentUrl.getHost();

		if (protocol == null)
			protocol = currentProtocol;

		String baseURL = protocol + "://" + currentHost + getPort(request) + request.getContextPath() + action;
		baseURL = HttpRequestUtil.normalizeUrl(baseURL).toString(); // normalize
																	// (remove
																	// default
																	// port
																	// numbers
																	// etc.)
		super.setPrependContext(false);
		setUrl(baseURL);

		return super.doEndTag();
	}

	@Override
	public void release() {
		super.release();
		protocol = null;
	}
}
