package org.ei.system;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.stripes.action.personalaccount.LogoutAction;
import org.ei.web.cookie.EISessionCookie;

/**
 * Servlet implementation class CheckSessionStatus
 */
@WebServlet("/CheckSessionStatus")
public class CheckSessionStatus extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private static final Logger log4j = Logger.getLogger(CheckSessionStatus.class);

    private static final String  CHECK_STATUS = "checkstatus";
    private static final String  REDIRECT_SESSION_EXIPIRED_PAGE = "redirect";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckSessionStatus() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String resourcetype = request.getParameter("resourcetype");
		 if(resourcetype.equalsIgnoreCase(CHECK_STATUS)){
			 checkStatus(request,  response);
		 }else if(resourcetype.equalsIgnoreCase(REDIRECT_SESSION_EXIPIRED_PAGE)){
		     LogoutAction.clearClientCookies(response);
			 request.setAttribute("maintenanceMsg", getMaintenanceMsg());
			 request.setAttribute("IE7Msg", getIE7Msg());
	         request.setAttribute("contactuslink", EVProperties.getApplicationProperties().getProperty(ApplicationProperties.CONTACT_US_LINK));
			 request.getRequestDispatcher("/WEB-INF/pages/world/sessionexpired.jsp").forward(request, response);
		 }
	}


	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void checkStatus(HttpServletRequest request, HttpServletResponse response) throws IOException{
		EISessionCookie eiSessionCookie = new EISessionCookie();
		boolean isValid = eiSessionCookie.isSessionCookieValid(request);
		JSONObject JObject = new JSONObject();
		try {
			JObject.put("sessionValid", isValid);
		} catch (JSONException e) {
			log4j.error("Eror occured while checking the session status :"+e.getMessage());
		}
		response.setContentType("application/json");
		response.getWriter().write(JObject.toString());
	}


	 private String getMaintenanceMsg(){
		 String msg = null;
	    	boolean isEnabled = Boolean.parseBoolean((EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_ENABLED)));
	    	if(isEnabled){
	    		String message = EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_TEXT);
	    		if(message != null){
	    			msg = message;
	    			String color = EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_COLOR);
	    			if(color != null){
	    				msg="<span style=\"color:"+color+"\">"+msg+"</span>";
	    			}
	    		}
	    	}
	    	return msg;
	    }
	 
	 private String getIE7Msg(){
	    	String msg = null;
	    	boolean isEnabled = Boolean.parseBoolean((EVProperties.getProperty(EVProperties.IE7_WARN_MSG_ENABLED)));
	    	if(isEnabled){
	    		msg = EVProperties.getProperty(EVProperties.IE7_WARN_MSG_TEXT);
	    	}
	    	return msg;
	    }
}
