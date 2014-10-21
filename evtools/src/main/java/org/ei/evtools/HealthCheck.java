package org.ei.evtools;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@WebServlet("/HealthCheck")
public class HealthCheck extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(HealthCheck.class);
	
	private static final long serialVersionUID = 1L;
       
  	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Health check ping....");
		String up = "UP";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(up);
	}
}
