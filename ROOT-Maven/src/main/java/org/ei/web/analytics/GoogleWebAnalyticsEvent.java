package org.ei.web.analytics;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.session.AWSInfo;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.WebAnalyticsEventProperties;

public class GoogleWebAnalyticsEvent implements IWebAnalyticsEvent{
	private String category = "";
	private String action  = "";
	private String label = "";
	private String gaURL;
	private String gaAccnt; 
	private boolean endSession = false;
	
	
	private final static Logger log4j = Logger.getLogger(GoogleWebAnalyticsEvent.class);
	
	public GoogleWebAnalyticsEvent(){
		//used for direct reporting from the server
		gaAccnt = EVProperties.getProperty(EVProperties.GOOGLE_ANALYTICS_ACCOUNT);
		gaURL = EVProperties.getProperty(EVProperties.GA_DIRECT_URL);
	}
	public GoogleWebAnalyticsEvent(String category, String action, String label){
		this.category = category;
		this.action = action;
		this.label = label;
		this.gaAccnt = EVProperties.getProperty(EVProperties.GOOGLE_ANALYTICS_ACCOUNT);
		this.gaURL = EVProperties.getProperty(EVProperties.GA_DIRECT_URL);
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
	
		this.category = category;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action)  {
	
		this.action = action;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {

		this.label = label;
	}
	
	public void recordRemoteEvent( EVActionBeanContext context){
		if(context == null || gaAccnt == null  || gaURL == null || context.getUserSession() == null || context.getUserSession().getUser() == null){
			log4j.error("**GA Event was unable to be created either context, accnt,session, user or url was null**");
			return;
		}
		String accntName = context.getUserSession().getUser().getAccount().getAccountName();
		String accntId = context.getUserSession().getUser().getAccount().getAccountId();
		boolean isWebUser = context.getUserSession().getUser().isIndividuallyAuthenticated();
		
		PostMethod payload = new PostMethod(gaURL);
		payload.addParameter("v", "1");
		payload.addParameter("tid", gaAccnt);
		payload.addParameter("t","event");
		
		if(!GenericValidator.isBlankOrNull(accntName)){
			payload.addParameter("cd1",accntName);
		}
		payload.addParameter("cd2",Boolean.toString(isWebUser));
			
		if(!GenericValidator.isBlankOrNull(accntId)){
			payload.addParameter("cd3",accntId);
		}
		
		String clientId =  context.getUserSession().getSessionid();
		if(GenericValidator.isBlankOrNull(clientId)){
			clientId = "NO_SESSION_ID";
		}
		payload.addParameter("cid",clientId.replace(":",""));
		
		
		String ipAddress = context.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
		    ipAddress = context.getRequest().getRemoteAddr();
		}
		if(!GenericValidator.isBlankOrNull(ipAddress)){
			payload.addParameter("uip", ipAddress);
		}
		if(!GenericValidator.isBlankOrNull(action)){
			payload.addParameter("ea",action);
		}
		if(!GenericValidator.isBlankOrNull(category)){
			payload.addParameter("ec", category);
		}
		if(!GenericValidator.isBlankOrNull(label)){
			payload.addParameter("el",label);
		}
		if(endSession){
			//ends the GA session
			payload.addParameter("sc", "end");
		}
		//create a runnable thread and add it to the executor so it
		//can be ran asynchronously
		Runnable postGA = new GAPostThread(payload);
		AnalyticsExecutor.getInstance().addEvent(postGA);
		log4j.info("**GA Event Added to Queue: Category:" + category + " Action: " + action + "**");
	}
	/**
	 * End the Google Analytics Session so it will stop tracking this user. 
	 * @param context
	 */
	public void endGASession(EVActionBeanContext context){
		if(context == null || gaAccnt == null  || gaURL == null){
			log4j.error("**GA Event was unable to be created either context, accnt, or url was null**");
			return;
		}
		
		PostMethod payload = new PostMethod(gaURL);
		payload.addParameter("v", "1");
		payload.addParameter("tid", gaAccnt);
		payload.addParameter("t","event");
		
		String clientId =  context.getExistingSession().getId();
		if(GenericValidator.isBlankOrNull(clientId)){
			clientId = "NO_SESSION_ID";
		}
		payload.addParameter("cid",clientId.replace(":",""));
		
		
		String ipAddress = context.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
		    ipAddress = context.getRequest().getRemoteAddr();
		}
		if(!GenericValidator.isBlankOrNull(ipAddress)){
			payload.addParameter("uip", ipAddress);
		}
		if(!GenericValidator.isBlankOrNull(action)){
			payload.addParameter("ea",action);
		}
		if(!GenericValidator.isBlankOrNull(category)){
			payload.addParameter("ec", category);
		}
		if(!GenericValidator.isBlankOrNull(label)){
			payload.addParameter("el",label);
		}
		payload.addParameter("sc", "end");
	
		//create a runnable thread and add it to the executor so it
		//can be ran asynchronously
		Runnable postGA = new GAPostThread(payload);
		AnalyticsExecutor.getInstance().addEvent(postGA);
		log4j.info("**GA Session Ended**");
	}
	/**
	 * Write out the js code to push the event to google.
	 */
	@Override
	public String getWebEvent() {
		log4j.info("Google Web Analytics Event Created : Category " + this.getCategory() + " Action: " + this.getAction() + " Label: " +  this.getLabel());
		return WebAnalyticsEventProperties.GOOGLE_TRACK_EVENT + ",'" + this.getCategory() + "','" + this.getAction() + "','" + this.getLabel() + "']);";
		
	}
	public boolean isEndSession() {
		return endSession;
	}
	public void setEndSession(boolean endSession) {
		this.endSession = endSession;
	}
			
}
