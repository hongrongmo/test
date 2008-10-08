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
    		    String imagetextClear = randomLetters + "." + Config.SEED;
    		    String imagetextEnc  = Base64Coder.encode(_stringEncrypter.encrypt(imagetextClear) );
        		String error = request.getParameter("errObject");
        		if(error == null)
        			error = "";
        		response.setContentType("text/html");
        		Writer out = response.getWriter();


        		out.write("<html><head><meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">");
        		out.write("<title>Engineering Village Image Verification</title>");
        		out.write(" <SCRIPT LANGUAGE=\"Javascript\" SRC=\"/engresources/js/StylesheetLinks.js\"></SCRIPT>");
        		out.write("</head>");
        		out.write("<body  bgcolor=\"#FFFFFF\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\">");

            // logo and gray bar
            out.write("<center>");
            out.write("  <table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"0\">");
            out.write("   <tr>");
            out.write("     <td valign=\"top\">");
            out.write("       <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
            out.write("         <tr>");
            out.write("           <td valign=\"top\">");
            out.write("             <a target=\"_top\" href=\"" + serverName + "/controller/servlet/Controller?CID=home\">");
            out.write("                 <img alt=\"Engineering Village\" src=\"/engresources/images/ev2logo5.gif\" border=\"0\"/>");
            out.write("             </a>");
            out.write("           </td>");
            out.write("         </tr>");
            out.write("       </table>");
            out.write("     </td>");
            out.write("   </tr>");
            out.write("  </table>");
            out.write("  <table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"0\">");
            out.write("    <tr><td valign=\"top\" colspan=\"10\" height=\"20\" bgcolor=\"#C3C8D1\"><img src=\"/engresources/images/s.gif\" border=\"0\"/></td></tr>");
            out.write("    <tr><td valign=\"top\" colspan=\"10\" height=\"20\"><img src=\"/engresources/images/s.gif\" border=\"0\"/></td></tr>");
            out.write("  </table>");
            out.write("</center>");

        		// validation error
        		if(error.equalsIgnoreCase("failed"))
        		{
              out.write("<center>");
        			out.write("<table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"10\"><tr><td><h3><font face=\"Arial\" color=\"red\">Validation failed. Please try again.</font></h3></td></tr></table>");
              out.write("</center>");
        		}
            out.write("<center>");
            out.write("<table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"10\">");
        		out.write("<td>");
        		out.write("<span CLASS=\"EvHeaderText\">");
        		out.write("We would like to validate the actions you are taking in this Engineering Village session.<br>");
        		out.write("In order to continue, please enter the text shown in the image below.</span>");
        		out.write("</td>");
        		out.write("</tr>");
        		out.write("<tr>");
        		out.write("<td>");
        		out.write(" <table border=\"1\" width=\"150\" cellspacing=\"0\" cellpadding=\"5\" align=\"left\">");
        		out.write(" <tr>");
        		out.write(" <td bgcolor=\"#FFFFFF\">");
        		out.write("   <table border=\"0\" width=\"89\" height=\"102\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">");
        		out.write("   <tr>");
        		out.write("     <form name=\"captcha_form\" action=\"" + serverName + "/controller/servlet/Captcha?requestType=validate\" method=\"post\">");
        		out.write("       <input type=\"hidden\" value=\"" + imagetextEnc + "\" name=\"imageidEnc\"><input type=\"hidden\"  value=\"" + redirectEnc + "\" name=\"redirectEnc\"> ");
        		out.write("     <td valign=\"center\" nowrap=\"\" align=\"left\">");
        		out.write("       <img height=\"40\" alt=\"\" src=\""+ serverName +"/controller/servlet/Captcha?requestType=image&imageidEnc="+ imagetextEnc +"\" width=\"198\" border=\"0\">");
        		out.write("     </td>");
        		out.write("     <td>");
        		out.write("      &nbsp;");
        		out.write("     </td>");
        		out.write("     <td valign=\"center\" nowrap=\"\" align=\"left\">");
        		out.write("       <a href=\"" + serverName + "/controller/servlet/Captcha?requestType=html&amp;redirectEnc=" + redirectEnc + "\">");
        		out.write("       <img alt=\"Refresh\" src=\"/engresources/images/refresh_captcha.gif\" height=\"26\" width=\"26\" border=\"0\"></a>");
        		out.write("       <a onclick=\"window.open('/engresources/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;\" href=\"" + serverName + "/engresources/captchaFAQ.html\">");
        		out.write("       <img alt=\"Help\" src=\"/engresources/images/help_captcha.gif\" height=\"27\" width=\"27\" border=\"0\"></a>");
        		out.write("     </td>");
        		out.write("   </tr>");
        		out.write("   <tr>");
        		out.write("     <td valign=\"top\" align=\"left\"><input size=\"29\" name=\"userEntry\"></td>");
        		out.write("     <td>");
        		out.write("      &nbsp;");
        		out.write("     </td>");
        		out.write("     <td valign=\"top\" align=\"left\"><input type=\"image\" height=\"19\" alt=\"submit button\" width=\"56\" src=\"/engresources/images/verify.gif\" value=\"submit\" name=\"submit\">");
        		out.write("     </td>");
        		out.write("     </form>");
        		out.write("   </tr>");
        		out.write("   </table>");
        		out.write(" </td>");
        		out.write(" </tr>");
        		out.write(" </table>");
        		out.write("</td>");
        		out.write("</tr>");
        		out.write("<tr>");
        		out.write("<td>");
        		out.write("<span CLASS=\"MedBlackText\">Please enter the text from the image and click Verify button");
        		out.write("<p>");
        		out.write("If you have any questions, please contact customer");
        		out.write("<br>");
        		out.write("support at <a style=\"color: black\" href=\"mailto:eicustomersupport@elsevier.com\">eicustomersupport@elsevier.com</a></span>");
        		out.write("</p>");
        		out.write("</td>");
        		out.write("</tr>");
        		out.write("</table>");
            out.write("</center>");
        		out.write("<br>");
        		out.write("</body></html>");
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

                    if (!sessionID.equals(Config.SEED)) {
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
