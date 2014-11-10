package org.ei.security;

import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityService
	extends HttpServlet
{
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException
    {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException
    {
		try
		{
			response.setContentType("text/html");
        	Writer out = response.getWriter();
			out.write("<html><head><title>Security</title>");
			out.write("<SCRIPT LANGUAGE='Javascript' SRC='/engresources/js/StylesheetLinks_V1.js'></SCRIPT>");
			out.write("</head>");
			out.write("<body bgcolor='#FFFFFF' topmargin='0' marginheight='0' marginwidth='0'>");
			out.write("<table border='0' width='99%' cellspacing='0' cellpadding='0'>");
			out.write("<tr>");
			out.write("<td valign='top'>");
			out.write("<a href='/controller/servlet/Controller?CID=home'><img src='/static/images/ev2logo5.gif' border='0'/></a>");
			out.write("</tr>");
			out.write("<tr>");
			out.write("<td valign='top' height='5'>");
			out.write("<img src='/static/images/spacer.gif' border='0' height='5'/>");
			out.write("</td>");
			out.write("</tr>");
			out.write("<tr>");
			out.write("<td valign='top' height='2' bgcolor='#3173B5'><img src='/static/images/spacer.gif' border='0' height='2'/></td>");
			out.write("</tr>");
			out.write("<tr>");
			out.write("<td valign='top' height='20'><img src='/static/images/spacer.gif' border='0' height='20'/></td>");
			out.write("</tr>");
			out.write("</table>");
			out.write("<center><table><tr><td><div CLASS='MedBlackText'>");
			out.write("Security error");
			out.write("</div></td></tr></table></center></form>");
			out.write("</body></html>");
			out.close();
		}
		catch(Exception e)
		{
			log("Security exception:", e);
		}
    }
}