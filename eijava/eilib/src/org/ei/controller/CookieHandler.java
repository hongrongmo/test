package org.ei.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ei.session.User;
import org.ei.session.UserSession;
import org.ei.util.*;
import org.ei.security.utils.SecureID;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import java.io.*;



public class CookieHandler
{

	private static Map departmentMap;

	public static boolean handleRequest(HttpServletRequest request,
                        	     		HttpServletResponse response,
                        	     		Map cookieMap,
                        	     		UserSession ses)
    	throws Exception
	{

		Map dmap = getDepartmentMap();
		String customerID = ses.getUser().getCustomerID();
		String sessionID = ses.getSessionID().toString();

		/*
			This block handles secureID;
		*/
		if(cookieMap.containsKey("SECUREID"))
		{
			Cookie secureID = (Cookie) cookieMap.get("SECUREID");			
			if(!SecureID.validSecureID(secureID.getValue()))
			{
				printSecurityViolation(response);
				return true;
			}
		}
		else
		{
			addSecureIDCookie(response);
		}


		/*
			This block handles clientID cookies
		*/
		if(!cookieMap.containsKey("CLIENTID"))
		{
			addClientIDCookie(response);
		}

		/*
			This block handles department cookies.
		*/

		String userAgent = request.getHeader("User-Agent");
		String CID = request.getParameter("CID");

		if((CID == null || CID.equals("home") || CID.equals("login")) &&
		   !cookieMap.containsKey("DEPARTMENT") &&
		   (dmap != null && dmap.containsKey(customerID)) &&
		   (request.getParameter("SYSTEM_DEPARTMENT") == null))
		{
			printDepartmentForm(request,
								response,
								sessionID,
								customerID,
								dmap);
			return true;
		}
		else if(request.getParameter("SYSTEM_DEPARTMENT") != null)
		{
			addDepartmentCookie(request,
								response);
			return false;
		}
		else if(request.getParameter("SYSTEM_CLEAR_DEP_COOKIE") != null)
		{
			clearDepartmentCookie(response);
			return false;
		}
		else if(request.getParameter("SYSTEM_DEPARTMENT_UPDATE") != null)
		{
			resetDepartment();
			return false;
		}

		return false;
	}

	public static Map getCookieMap(HttpServletRequest request)
	{
		Map map = new HashMap();
		Cookie[] cookies = request.getCookies();
		if(cookies != null)
		{
			for(int i=0; i<cookies.length;++i)
			{
				Cookie cookie = cookies[i];
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	private static synchronized Map getDepartmentMap()
		throws Exception
	{
		if(departmentMap == null)
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			DepartmentConfigHandler handler = new DepartmentConfigHandler();
			parser.setContentHandler(handler);
			parser.parse("departments/department-config.xml");
			departmentMap = handler.getDepartmentMap();
		}
		return departmentMap;
	}

	private static synchronized void resetDepartment()
	{
		departmentMap = null;
	}

	private static void printSecurityViolation(HttpServletResponse response)
			throws Exception
	{
		response.setContentType("text/html");
		Writer out = response.getWriter();
		out.write("<html><head><title>Security violation</title>");
		out.write("<SCRIPT LANGUAGE='Javascript' SRC='/engresources/js/StylesheetLinks.js'></SCRIPT>");
		out.write("</head><body>Security Violation</body></html>");
	}

	private static void printDepartmentForm(HttpServletRequest request,
											HttpServletResponse response,
											String sessionID,
									        String customerID,
									        Map dmap)
		throws Exception
	{
		Customer cust = (Customer)dmap.get(customerID);
		Department[] department = cust.getDepartments();
		response.setContentType("text/html");
		Writer out = response.getWriter();
		out.write("<html><head><title>Department level tracking</title>");
		out.write("<SCRIPT LANGUAGE='Javascript' SRC='/engresources/js/StylesheetLinks.js'></SCRIPT>");
		out.write("</head>");
		out.write("<body bgcolor='#FFFFFF' topmargin='0' marginheight='0' marginwidth='0'>");
		out.write("<table border='0' width='99%' cellspacing='0' cellpadding='0'>");
		out.write("<tr>");
		out.write("<td valign='top'>");
		out.write("<a href='/controller/servlet/Controller?CID=home'><img src='/engresources/images/ev2logo5.gif' border='0'/></a>");
		out.write("</tr>");
		out.write("<tr>");
		out.write("<td valign='top' height='5'>");
		out.write("<img src='/engresources/images/spacer.gif' border='0' height='5'/>");
		out.write("</td>");
		out.write("</tr>");
		out.write("<tr>");
		out.write("<td valign='top' height='2' bgcolor='#3173B5'><img src='/engresources/images/spacer.gif' border='0' height='2'/></td>");
		out.write("</tr>");
		out.write("<tr>");
		out.write("<td valign='top' height='20'><img src='/engresources/images/spacer.gif' border='0' height='20'/></td>");
		out.write("</tr>");
		out.write("</table>");
		out.write("<form action='/controller/servlet/Controller'>");
		out.write("<center><table><tr><td><div CLASS='MedBlackText'>");
		out.write("Your organization has requested department level usage tracking.<br/> To enable this feature please select from one of the departments listed below:<br/><blockquote>");
		for(int i=0; i<department.length; i++)
		{
			out.write("<input type='radio'  name='SYSTEM_DEPARTMENT' and value='");
			out.write(department[i].getID());
			out.write("'/>");
			out.write(department[i].getName());
			out.write("<br/>");
		}
		out.write("<br/><input type='submit' value='Submit'/></blockquote><p/>");
		out.write("(If you have questions about which department to select or do not see your department please contact ");
		out.write(cust.getContactName());
		out.write(" at ");
		out.write(cust.getContactEmail());
		out.write(")");
		out.write("<input type='hidden' name='EISESSION' value='");
		out.write(sessionID);
		out.write("'/>");
		out.write("<input type='hidden' name='SYSTEM_USE_SESSION_PARAM' value='true'/>");
		out.write("</div></td></tr></table></center></form>");

		out.write("</body></html>");

	}

	private static void addDepartmentCookie(HttpServletRequest request,
                        	     	        HttpServletResponse response)
	{
		System.out.println("Adding department cookie.");
		String departmentID = request.getParameter("SYSTEM_DEPARTMENT");
		if(departmentID != null)
		{
			Cookie depCookie = new Cookie("DEPARTMENT", departmentID);
			depCookie.setMaxAge(63072000);
        	response.addCookie(depCookie);
		}
	}

	private static void clearDepartmentCookie(HttpServletResponse response)
		throws Exception
	{
		Cookie depCookie = new Cookie("DEPARTMENT","");
		depCookie.setMaxAge(0);
		response.addCookie(depCookie);
	}

	private static void addClientIDCookie(HttpServletResponse response)
		throws Exception
	{
		Cookie clientIDCookie = new Cookie("CLIENTID", new GUID().toString());
		clientIDCookie.setMaxAge(63072000);
		response.addCookie(clientIDCookie);
	}

	private static void addSecureIDCookie(HttpServletResponse response)
		throws Exception
	{
		Cookie secureIDCookie = new Cookie("SECUREID", SecureID.getSecureID(-1));
		secureIDCookie.setMaxAge(63072000);
		response.addCookie(secureIDCookie);
	}

}