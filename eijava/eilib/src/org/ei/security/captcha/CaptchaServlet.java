package org.ei.security.captcha;

import java.awt.image.BufferedImage;
import java.io.Writer;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.net.URLEncoder;
import org.ei.util.Base64Coder;
import org.ei.security.utils.Encrypter;
import org.ei.security.utils.SecureID;


public class CaptchaServlet extends HttpServlet {

    private static Encrypter _stringEncrypter = new Encrypter();
    private static final String SKEW_PROCESSOR_CLASS = Config.getProperty(Config.SKEW_PROCESSOR_CLASS);

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException {
        try
        {
        	boolean displayHTML = false;
        	String redirectEnc;
        	String imageidEnc;
        	String imageidClear;

        	if(request.getParameter("requestType") == null)
        	{
        		displayHTML = true;
        		StringBuffer redirectParam  = new StringBuffer("?");
	        	for(Enumeration e = request.getParameterNames(); e.hasMoreElements();)
	    		{
	    			String name = (String) e.nextElement();
	    			String value = (String) request.getParameter(name);
	    			//redirectParam.append(name + "=" + value + "&");
	    			redirectParam.append(name + "=" + URLEncoder.encode(value, "UTF-8") + "&");
	    			
	    		}
	        	redirectEnc = Base64Coder.encode(redirectParam.toString());
        	}
        	else
        	{
        		displayHTML = request.getParameter("requestType").equals("html");
        		redirectEnc = request.getParameter("redirectEnc");
        	}

        	String serverName = "http://" + request.getServerName();
    		int serverPort = request.getServerPort();
    		if(serverPort != 80)
    		{
    			serverName = serverName+":"+Integer.toString(serverPort);
    		}

        	if(displayHTML)
        	{
        		String randomLetters = new String("");
    		    for (int i = 0; i < Config.getPropertyInt(Config.MAX_NUMBER); i++) {
    		        randomLetters += (char) (65 + (Math.random() * 24));
    		    }
    		    randomLetters = randomLetters.replaceAll("I","X");
    		    randomLetters = randomLetters.replaceAll("Q","Z");
    		    String imagetextClear = randomLetters + "." + request.getSession().getId();
    		    String imagetextEnc  = Base64Coder.encode(_stringEncrypter.encrypt(imagetextClear) );
        		String error = request.getParameter("errObject");
        		if(error == null)
        			error = "";
        		response.setContentType("text/html");
        		Writer out = response.getWriter();
        		/*
        		out.write("<html><head><meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\"><title>Image Verification</title>");
        		out.write("<style> .SmBlackText {font-size:11px; font-family:arial,verdana,geneva; color:#000000;} A.MedBlueLink:link {font-size:11px; ");
        		out.write("font-family:arial,verdana,geneva; color:#0000FF;}.MedBlackText {font-size:12px; font-family:arial,verdana,geneva; color:#000000;} ");
        		out.write("</style></head><body><a href=\"http://www.engineeringvillage.com/\"><img alt=\"Engineering Village\" src=\"/engresources/images/ev2logo5.gif\" ");
        		out.write("align=\"left\" border=\"0\"></a><p><br><br><br><table width=\"100%\" align=\"left\" bgcolor=\"#001902\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        		out.write("<tbody><tr><td bordercolor=\"#ffffff\" bgcolor=\"#c0c0c0\"><br></td></tr></tbody></table><br></p>");
        		out.write("<h3><font face=\"Arial\" color=\"red\">  </font><big style=\"FONT-FAMILY: Arial\"><span style=\"COLOR: rgb(102,102,102)\">");
        		out.write("<span style=\"COLOR: rgb(153,153,153)\"><br></span></span></big>");
        		if(error.equalsIgnoreCase("failed"))
        		{        			
        			out.write("<table width=\"100%\" border=\"0\"><tr><td><h3><font face=\"Arial\" color=\"red\">Validation failed. Please try again. </font></h3></td></tr></table>");
        		}
        		out.write("<table height=\"387\" cellspacing=\"2\" cellpadding=\"40\" width=\"648\" align=\"left\" border=\"0\"><tr><td>");
        		out.write("<big style=\"FONT-FAMILY: Arial\"><span style=\"COLOR: rgb(102,102,102)\"><span style=\"COLOR: rgb(195,201,209)\">");
        		out.write("This  Engineering Village search session is experiencing heavy activity.</span><br style=\"COLOR: rgb(195,201,209)\">");
        		out.write("<span style=\"COLOR: rgb(195,201,209)\">In order to continue searching, please enter the code</span>");
        		out.write("<br style=\"COLOR: rgb(195,201,209)\"><span style=\"COLOR: rgb(153,153,153)\"><span style=\"COLOR: rgb(195,201,209)\">");
        		out.write("shown in the image below.</span><br><br></span></span></big> ");
        		out.write("<table height=\"96\" cellspacing=\"0\" cellpadding=\"10\" width=\"376\" align=\"left\" border=\"1\"><tr>");
        		out.write("<td bgcolor=\"white\" colspan=\"2\"><table height=\"102\" cellspacing=\"0\" cellpadding=\"0\" width=\"89\" align=\"left\" border=\"0\">");
        		out.write("<tr><form name=\"captcha_form\" action=\"" + serverName + "/controller/servlet/Captcha?requestType=validate\" method=\"post\"></form><input type=\"hidden\" ");
        		out.write("value=\"" + imagetextEnc + "\" name=\"imageidEnc\"><input type=\"hidden\"  value=\"" + redirectEnc + "\" name=\"redirectEnc\"> ");
        		out.write("<td valign=\"center\" nowrap=\"\" align=\"left\"><img height=\"40\" alt=\"\" src=\""+ serverName +"/controller/servlet/Captcha?requestType=image&imageidEnc="+ imagetextEnc +"\" width=\"198\" border=\"0\">");
        		out.write("</td><td valign=\"top\" nowrap=\"\" align=\"left\"><br><a href=\"" + serverName + "/controller/servlet/Captcha?requestType=html&amp;redirectEnc=" + redirectEnc + "\">");
        		out.write("<img height=\"26\" alt=\"\" src=\"/engresources/images/refresh_captcha.gif\" width=\"26\" border=\"0\"></a><a onclick=\"window.open('/engresources/captchaFAQ.html','_blank','width=400, ");
        		out.write("height=300, left=' + (screen.width-450) + ', top=100');return false;\" href=\"" + serverName + "/engresources/captchaFAQ.html\">");
        		out.write("<img height=\"27\" alt=\"\" src=\"/engresources/images/help_captcha.gif\" width=\"27\" border=\"0\"></a><br>");
        		out.write("</td></tr><tr><td valign=\"top\" align=\"left\"><input size=\"29\" name=\"userEntry\"><br></td>");
        		out.write("<td valign=\"top\" align=\"left\"><input type=\"image\" height=\"19\" alt=\"submit button\" width=\"56\" src=\"/engresources/images/verify.gif\" value=\"submit\" name=\"submit\"></td>");
        		out.write("</tr></table></td></tr></table><br><br><br><br><br><br><br><span style=\"COLOR: rgb(102,102,102)\"><span style=\"COLOR: rgb(153,153,153)\"><small style=\"COLOR: black\">");
        		out.write("<br><font face=\"Arial\" size=\"3\">Please enter the text from the image and click Verify  ");
        		out.write("button</font> </small><br><small><br><font size=\"3\"><font face=\"Arial\"><span style=\"COLOR: black\">If you have any questions, please contact customer</span><br style=\"COLOR: black\">");
        		out.write("<span style=\"COLOR: black\">support ");
        		out.write("at </span></font></font><a style=\"COLOR: black\" href=\"mailto:eicustomersupport@elsevier.com\"><font face=\"Arial\" size=\"3\">eicustomersupport@elsevier.com</font></a></small> </span></span>");        		
        		out.write("<br><br><br><br></body></html>");   
        		*/
        		
        		out.write("<html><head><meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\"><title>Image Verification</title></head><body><p><br>");
        		out.write("<table style=\"Z-INDEX: 100; LEFT: 2px; POSITION: absolute; TOP: 6px\" width=\"100%\" border=\"0\">");
        		out.write("<tr><!-- Row 1 -->");
        		out.write("<td valign=\"top\" align=\"left\"><a href=\"http://www.engineeringvillage.com/\"><IMG height=46 alt=\"Engineering Village\" ");
        		out.write("src=\"/engresources/images/ev2logo5.gif\" width=249 align=left border=0></a></td><!-- Col 1 --></tr>");
        		out.write("<tr><!-- Row 2 -->");
        		out.write("<td bgcolor=\"#c0c0c0\"><BR></td><!-- Col 1 --></tr></table><br></p><h3><font color=\"red\" face=\"Arial\"></font><big style=\"FONT-FAMILY: ");
        		out.write("Arial\"><span style=\"COLOR: rgb(102,102,102)\"><span style=\"COLOR: rgb(153,153,153)\"><br></span></span></big><table width=\"690\" align=\"left\" ");
        		out.write("border=\"0\" cellpadding=\"40\" cellspacing=\"2\" height=\"523\"><tbody><tr><td><big style=\"FONT-FAMILY: Arial\"><span style=\"COLOR: rgb(102,102,102)\">");
        		out.write("<span style=\"COLOR: rgb(195,201,209)\"><FONT size=4>");
        		if(error.equalsIgnoreCase("failed"))
        		{        			        			
        			out.write("<h3><font face=\"Arial\" color=\"red\">Validation failed. Please try again. </font></h3><br><P>We would like to validate the actions you are taking in this ");
        		}
        		else
        		{
        			out.write("<P>We would like to validate the actions you are taking in this ");
        		}
        		out.write("Engineering Village session. In order to continue, please enter the text ");
        		out.write("shown in the image below.</P></FONT>         </span><span style=\"COLOR: rgb(153,153,153)\"><br></span></span></big> <table width=\"376\" ");
        		out.write("align=\"left\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" height=\"96\"><tbody><tr><td colspan=\"2\" bgcolor=\"white\"><table width=\"89\" ");
        		out.write("align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"102\"><tbody><tr><form name=\"captcha_form\" ");
        		out.write("action=\"" + serverName + "/controller/servlet/Captcha?requestType=validate\" method=\"post\">");
        		out.write("<input value=\"" + imagetextEnc + "\" name=\"imageidEnc\" type=\"hidden\">");
        		out.write("<input value=\"" + redirectEnc + "\" name=\"redirectEnc\" type=\"hidden\"> <td valign=\"center\" align=\"left\" nowrap>");
        		out.write("<IMG height=40 alt=\"\" src=\""+ serverName +"/controller/servlet/Captcha?requestType=image&imageidEnc="+ imagetextEnc +"\" width=\"198\" border=\"0\" ");
        		out.write("width=198 border=0></td><td valign=\"top\" align=\"left\" nowrap><br><a href=\"" + serverName + "/controller/servlet/Captcha?requestType=html&redirectEnc=" + redirectEnc + "\" ");
        		out.write("><IMG height=26 alt=\"\" src=\"/engresources/images/refresh_captcha.gif\" width=26 border=0></a>");
        		out.write("<a onclick=\"window.open('/engresources/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;\""); 
        		out.write("href=\"" + serverName + "/engresources/captchaFAQ.html\"><IMG height=27 alt=\"\" src=\"/engresources/images/help_captcha.gif\" width=27 border=0>");
        		out.write("</a><br></td></tr><tr><td valign=\"top\" align=\"left\"><input size=\"29\" name=\"userEntry\"><br></td><td valign=\"top\" align=\"left\">");
        		out.write("<input alt=\"submit button\" src=\"/engresources/images/verify.gif\" value=\"submit\" name=\"submit\" type=\"image\" width=\"56\" height=\"19\"></form>");
        		out.write("</td></tr></tbody></table></td></tr></tbody></table><br><br><br><br><br><br><br>");
        		out.write("<span style=\"COLOR: rgb(102,102,102)\"><SPAN style=\"COLOR: rgb(153,153,153)\"><SMALL style=\"COLOR: black\"><BR></SMALL></SPAN></span><br><br>");
        		out.write("<TABLE height=118 width=430 border=0>");
        		out.write("<TR><!-- Row 1 -->");
        		out.write("<TD><FONT face=Arial>Please enter the text from the image and click ");
        		out.write("Verify button</FONT><FONT size=2> <BR></FONT><SMALL><BR><FONT ");
        		out.write("size=3><FONT face=Arial><SPAN style=\"COLOR: black\">If you have any ");
        		out.write("questions, please contact customer</SPAN><BR ");
        		out.write("style=\"COLOR: black\"><SPAN style=\"COLOR: black\">support at ");
        		out.write("</SPAN></FONT></FONT><A style=\"COLOR: black\" ");
        		out.write("href=\"mailto:eicustomersupport@elsevier.com\"><FONT face=Arial ");
        		out.write("size=3>eicustomersupport@elsevier.com</FONT></A></SMALL><FONT color=#999999> ");
        		out.write("</FONT><BR><BR></TD><!-- Col 1 --></TR></TABLE></td></tr></tbody></table></h3></body></html>");
        	}
        	else if(request.getParameter("requestType").equals("image"))
        	{
    		    imageidEnc = request.getParameter("imageidEnc");
                if (imageidEnc == null) {
                    ServletException se = new ServletException("Invalid Image ID");
                    throw se;
                }
                if (imageidEnc.length() < Config.getPropertyInt(Config.MAX_NUMBER)) {
                    ServletException se = new ServletException("Invalid Image ID Length");
                    throw se;
                }
                imageidEnc = imageidEnc.trim();
                imageidClear = _stringEncrypter.decrypt(Base64Coder.decode(imageidEnc));
                response.setContentType("image/jpeg");
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                String [] sprocessorClasses = SKEW_PROCESSOR_CLASS.split(":");
                ISkewImage skewImage = (ISkewImage) cl.loadClass(sprocessorClasses[(int) (Math.random() * sprocessorClasses.length)]).newInstance();
                BufferedImage bufferedImage = skewImage.skewImage(imageidClear.substring(0, imageidClear.indexOf(".")));
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(response.getOutputStream());
                encoder.encode(bufferedImage);
        	}
    	    else
    	    {
    	    	boolean failed = false;
    	    	String userEntry = request.getParameter("userEntry");
    	    	imageidEnc = request.getParameter("imageidEnc");
    	    	userEntry = userEntry.trim();
    	    	imageidEnc = imageidEnc.trim();

    	    	if ((userEntry!=null) || (imageidEnc !=null)) {

                    String imageidDec = Base64Coder.decode(imageidEnc);
                    imageidClear = _stringEncrypter.decrypt(imageidDec);
                    String sessionID = imageidClear.substring(imageidClear.indexOf(".")+1, imageidClear.length());
                    imageidClear = imageidClear.substring(0, Config.getPropertyInt(Config.MAX_NUMBER));

                    if (!sessionID.equals(request.getSession().getId())) {
                    	failed = true;
                    }
                    imageidClear = imageidClear.trim();
                    if (!userEntry.toUpperCase().equals(imageidClear.toUpperCase())) {
                    	failed = true;
                    }
                }
    	    	else
    	    	{
    	    		failed = true;
    	    	}

    	    	if(failed)
    	    	{
    	    		response.sendRedirect(serverName + "/controller/servlet/Captcha?requestType=html&errObject=failed&redirectEnc=" + redirectEnc);
    	    	}
    	    	else
    	    	{
    	    		response.sendRedirect(serverName + "/controller/servlet/Controller" + Base64Coder.decode(redirectEnc) + "captchaID=" + SecureID.getSecureID(60000L));
    	    	}
    	    }
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
