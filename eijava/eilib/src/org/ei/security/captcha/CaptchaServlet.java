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
	    			redirectParam.append(name + "=" + value + "&");
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
        		out.write("<html><head><meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\"><title>Image Verification</title>");
        		out.write("<style> .SmBlackText {font-size:11px; font-family:arial,verdana,geneva; color:#000000;} A.MedBlueLink:link {font-size:11px; font-family:arial,verdana,geneva; color:#0000FF;}");
        		out.write(".MedBlackText {font-size:12px; font-family:arial,verdana,geneva; color:#000000;} </style>");
        		out.write("</head><body>");
        		out.write("<a href='http://www.engineeringvillage.com/'><img src='/engresources/images/ev2logo5.gif' border='0' style='float:left;' alt='Engineering Village'></a><br>");
        		out.write("<br><table style=\"text-align: left; background-color: rgb(195, 201, 209); width: 100%;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">");
        		out.write("<tbody><tr><td><br></td></tr></tbody></table>");
        		if(error.equalsIgnoreCase("failed"))
        		{
        			out.write("<h3><font face=\"Arial\" color=\"red\">Validation failed. Please try again. </font></h3>");
        		}
        		else
        		{
        			out.write("<br>");
        		}
        		/*
        		out.write("<table style=\"text-align: left; width: 750px; height: 413px;\" border=\"0\" cellpadding=\"40\" cellspacing=\"2\">");
        		out.write("<tbody><tr><td><big style=\"font-family: Arial;\"><span style=\"color: rgb(102, 102, 102);\"><span style=\"color: rgb(195, 201, 209);\">");
        		out.write("This Engineering Village search session is experiencing heavy activity.</span><br style=\"color: rgb(195, 201, 209);\">");
        		out.write("<span style=\"color: rgb(195, 201, 209);\">In order to continue searching, please enter the code</span><br style=\"color: rgb(195, 201, 209);\">");
        		out.write("<span style=\"color: rgb(153, 153, 153);\"><span style=\"color: rgb(195, 201, 209);\">shown in the image below.</span><br>");
        		out.write("<br></span></span></big> <table style=\"text-align: left; width: 306px; height: 161px;\" border=\"1\" cellpadding=\"20\" cellspacing=\"0\">");
        		out.write("<tbody><tr><td style=\"background-color: white;\"><table style=\"text-align: left; width: 371px; height: 167px;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        		out.write("<tbody><tr><td style=\"vertical-align: top;\">");
        		out.write("<form name='captcha_form' method='post' action='" + serverName + "/controller/servlet/Captcha?requestType=validate'>");
        		out.write("<input type='hidden' name='imageidEnc' value='" + imagetextEnc + "'>");
        		out.write("<input type='hidden' name='redirectEnc' value='" + redirectEnc + "'>");
        		out.write("<img style=\"width: 396px; height: 80px;\"  border='0' src='" + serverName + "/controller/servlet/Captcha?requestType=image&imageidEnc=" + imagetextEnc +"'>");
        		out.write("<br><table style=\"text-align: left; width: 363px; height: 59px;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        		out.write("<tbody><tr><td style=\"text-align: left; vertical-align: middle;\"><input size=\"62\" name=\"userEntry\"></td> </tr> </tbody> </table>");
        		out.write("</td> <td style=\"text-align: left; vertical-align: top;\"> <table style=\"text-align: left; width: 211px; height: 127px;\"");
        		out.write(" border=\"0\" cellpadding=\"3\" cellspacing=\"0\"> <tbody> <tr> <td style=\"vertical-align: middle; white-space: nowrap; text-align: left;\">");
        		out.write("<a href='" + serverName + "/controller/servlet/Captcha?requestType=html&redirectEnc="+ redirectEnc +"'>");
        		out.write("<img style=\"width: 27px; height: 27px;\" alt=\"\"  border='0' src=\"/engresources/images/refresh_captcha.gif\">");
        		out.write("</a></td> <td align=\"left\" valign=\"top\"></td>");
        		out.write("<td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td>");
        		out.write("</tr> <tr> <td style=\"vertical-align: top; text-align: left;\">");
        		out.write("<a href='/engresources/captchaFAQ.html' onClick=\"window.open('/engresources/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;\">");
        		out.write("<img style=\"height: 27px; width: 27px;\" alt=\"\"  border='0' src=\"/engresources/images/help_captcha.gif\"></a>");
        		out.write("<br> </td> <td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td>");
        		out.write("<td align=\"left\" valign=\"top\"></td> </tr> <tr> <td style=\"vertical-align: top; text-align: left; white-space: nowrap;\"><br>");
        		out.write("<input style=\"height: 19px; width: 56px;\" type=\"image\" src=\"/engresources/images/verify.gif\" value=\"submit\" alt=\"submit button\" name=\"submit\">");
        		out.write("</form>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </td>");
        		out.write("<td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td> <td align=\"left\" valign=\"top\"></td>");
        		out.write("<td align=\"left\" valign=\"top\"></td> </tr> </tbody> </table></td></tr></tbody></table></td></tr></tbody></table>");
        		*/
        		
        		out.write("<table style=\"text-align: left; width: 750px; height: 398px;\" border=\"0\" cellpadding=\"40\" cellspacing=\"2\">");
        		out.write("<tbody><tr> <td><big style=\"font-family: Arial;\"><span style=\"color: rgb(102, 102, 102);\"><span ");
        		out.write("style=\"color: rgb(195, 201, 209);\">This Engineering Village search session is experiencing heavy activity.</span>");
        		out.write("<br style=\"color: rgb(195, 201, 209);\"> <span style=\"color: rgb(195, 201, 209);\">In order to ");
        		out.write("continue searching, please enter the code</span><br style=\"color: rgb(195, 201, 209);\">");
        		out.write("<span style=\"color: rgb(153, 153, 153);\"><span style=\"color: rgb(195, 201, 209);\">shown in the image below.</span><br>");
        		out.write("<br></span></span></big><table style=\"text-align: left; width: 376px; height: 96px;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\">");
        		out.write("<tbody><tr><td colspan=\"2\" style=\"background-color: white;\"><table style=\"text-align: left; width: 89px; height: 102px;\" border=\"0\"");
        		out.write(" cellpadding=\"0\" cellspacing=\"0\"> <tbody> <tr>");
        		out.write("<form name='captcha_form' method='post' action='" + serverName + "/controller/servlet/Captcha?requestType=validate'>");
        		out.write("<input type='hidden' name='imageidEnc' value='" + imagetextEnc + "'>");
        		out.write("<input type='hidden' name='redirectEnc' value='" + redirectEnc + "'>");
        		out.write("<td style=\"vertical-align: middle; white-space: nowrap; text-align: left;\">");        		
        		out.write("<img style=\"width: 198px; height: 40px;\" alt=\"\" border='0' src='" + serverName + "/controller/servlet/Captcha?requestType=image&imageidEnc=" + imagetextEnc +"'></td>");        		
        		out.write("<td style=\"vertical-align: top; text-align: left; white-space: nowrap;\"><br>"); 
        		out.write("<a href='" + serverName + "/controller/servlet/Captcha?requestType=html&redirectEnc="+ redirectEnc +"'>");
        		out.write("<img style=\"width: 26px; height: 26px;\" border='0' alt=\"\" src=\"/engresources/images/refresh_captcha.gif\"></a>");
        		out.write("<a href='/engresources/captchaFAQ.html' onClick=\"window.open('/engresources/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;\">");
        		out.write("<img style=\"height: 27px; width: 27px;\" alt=\"\" border='0' src=\"/engresources/images/help_captcha.gif\"></a><br>");        		
        		out.write("&nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp; &nbsp; </td>");
        		out.write("</tr><tr><td style=\"text-align: left; vertical-align: middle;\"><input size=\"29\" name=\"userEntry\"><br>");
        		out.write("</td> <td style=\"text-align: left; vertical-align: middle;\">");
        		out.write("<input style=\"height: 19px; width: 56px;\" type=\"image\" src=\"/engresources/images/verify.gif\" value=\"submit\" alt=\"submit button\" name=\"submit\"></form></td>");
        		out.write("</tr></tbody></table></td></tr></tbody></table>");        		        		        		
        		out.write("<big style=\"font-family: Arial;\"><span style=\"color: rgb(102, 102, 102);\"><span style=\"color: rgb(153, 153, 153);\">");
        		out.write("<small style=\"color: black;\"><br> Please enter the text from the image and click Verify button</small><br>");
        		out.write("<small><br><span style=\"color: black;\">If you have any questions, please contact customer</span><br style=\"color: black;\"> ");
        		out.write("<span style=\"color: black;\">support at </span><a style=\"color: black;\" href=\"mailto:eicustomersupport@elsevier.com\">");
        		out.write("eicustomersupport@elsevier.com</a></small><br> </span></span></big></td>");
        		out.write("</tr> </tbody></table><br><br><br><br><br><br><br><br><br><br><br><br> ");
        		out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tbody><tr><td>");
        		out.write("<center><a class=\"MedBlueLink\" target=\"_self\" href=\"/controller/servlet/Controller?CID=aboutEI&amp;database=2289095\">About Ei</a>");
        		out.write("<a class=\"SmBlackText\">&nbsp; - &nbsp;</a><a class=\"MedBlueLink\" target=\"_self\" href=\"/controller/servlet/Controller?CID=aboutEV&amp;database=2289095\">");
        		out.write("About Engineering Village</a><a class=\"SmBlackText\">&nbsp; - &nbsp;</a>");
        		out.write("<a class=\"MedBlueLink\" target=\"_self\" href=\"/controller/servlet/Controller?CID=feedback&amp;database=2289095\">Feedback</a>");
        		out.write("<a class=\"SmBlackText\">&nbsp; - &nbsp;</a> <a class=\"MedBlueLink\" target=\"_self\" href=\"/controller/servlet/Controller?CID=privacyPolicy&amp;database=2289095\">");
        		out.write("Privacy Policy</a><a class=\"SmBlackText\">&nbsp; - &nbsp;</a>");
        		out.write("<a class=\"MedBlueLink\" target=\"_self\" href=\"/controller/servlet/Controller?CID=TermsandConditions&amp;database=2289095\">Terms and Conditions</a><br>");
        		out.write("<a class=\"SmBlackText\">© 2008 Elsevier Inc. All rights reserved.</a></center>");
        		out.write("</td></tr></tbody></table></body></html>");


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
    	    		response.sendRedirect(serverName + "/controller/servlet/Controller" + Base64Coder.decode(redirectEnc) + "captchaID=" + SecureID.getSecureID(5000L));
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
