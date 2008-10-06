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
        		out.write("<html><head><title> Engineering Village Image Verification</title></head><style>");        		        		
        		out.write("body {margin:10px; padding-bottom: 40px; font-size:11px; font-family:Verdana,Arial,Helvetica,sans-serif; background-color:#FFFFFF;}");
        		out.write("h3 {font-size:12pt; font-weight:bold;color:red;}");
        		out.write("th {font-size:12pt; font-weight:bold;color:black;}");
        		out.write("td {font-size:11pt;color:black;}");
        		out.write("#bodycontent {height:100%; padding-top:30px; padding-bottom:30px; font-size:11px; font-family:Verdana,Arial,Helvetica,sans-serif; }");
        		out.write("#content {line-height: 14px; margin-left: 0.25in; padding-top: 0.25in; font-family: Verdana,Arial,Helvetica,sans-serif; text-decoration:none; color: black; font-size:11px; width:500px;}");
        		out.write("#content a { font-size:11px; }");
        		out.write("#contentheader {font-size:22px; font-family:Verdana,Arial,Helvetica,sans-serif; text-decoration:none; color:#00467F; font-style:normal;}");        		
        		out.write("form {margin:0px;font-family:verdana,arial,geneva,sans-serif;}");
        		out.write("input {font-family:verdana,arial,geneva,sans-serif;font-size:12px;}");
        		out.write("select {font-family:verdana,arial,geneva,sans-serif;font-size:10px;}");
        		out.write("textarea {font-family:verdana,arial,geneva,sans-serif;font-size:10px;}");
        		out.write("a { font-size:10px; text-decoration:none;}");
        		out.write("</style>");
        		out.write("<body><center>");
        		out.write("<table border='0' width='100%' cellspacing='0' cellpadding='0'>");
        		out.write("<tr><td width='800'><a href='http://www.engineeringvillage.com/'><img src='" + serverName + "/engresources/images/ev2logo5.gif' border='0' style='float:left;' alt='Engineering Village'></a></td></tr>");
        		out.write("<tr><td><br>");
        		if(error.equalsIgnoreCase("failed"))
        		{
        			out.write("<h3>Validation failed. Please try again.</h3>");
        		}
        		else
        		{
        			out.write("<br>");
        		}        			        		        
        		out.write("<table cellpadding=5 cellspacing=0 bgcolor='#E4F8E4' width='60%'>");
        		out.write("<tr bgcolor='#AAD6AA'><td colspan='2'><font color='#FFFFFF' face='Verdana' size='2'><b>Image Verification</b></font></td></tr><tr>");
        		out.write("<td style='padding: 2px;' width='10'>");
        		out.write("<form name='captcha_form' method='post' action='" + serverName + "/controller/servlet/Captcha?requestType=validate'>");        		
        		out.write("<input type='hidden' name='imageidEnc' value='" + imagetextEnc + "'>");
        		out.write("<input type='hidden' name='redirectEnc' value='" + redirectEnc + "'>");
        		out.write("<img src='" + serverName + "/controller/servlet/Captcha?requestType=image&imageidEnc=" + imagetextEnc +"'></td>");
        		out.write("<td valign='top'><font color='#000000'>Please enter the text from the image</font><br><input type='text' name='userEntry' value='' maxlength='100' size='10'>");
        		out.write("[ <a href='" + serverName + "/controller/servlet/Captcha?requestType=html&redirectEnc="+ redirectEnc +"'>Refresh Image</a> ] [ <a href='" + serverName + "/engresources/captchaFAQ.html' onClick=\"window.open('http://localhost:8080/engresources/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;\">What's This?</a> ]</td><br></tr>");
        		out.write("<tr><td></td><td align='left'><input type='submit' name='verify' value='Verify' style='height: 20px; width: 80px'> </form></td></tr></table></td></tr>");
        		out.write("</center></body></html>");

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
    	    		response.sendRedirect(serverName + "/controller/servlet/Controller" + Base64Coder.decode(redirectEnc) + "secureID=" + SecureID.getSecureID(5000L));
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
