package org.ei.system;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthCheck extends HttpServlet {

	public void init() throws ServletException {
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		File down = new File("down");
		String up = "UP";
		if (down.exists()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "EV2 Instance Unavailable");
		} else {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println(up);
		}
		
	}
}
