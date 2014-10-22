package org.ei.stripes.action;

import org.apache.log4j.Logger;

public class GoogleWebAnalyticsEvent implements IWebAnalyticsEvent{
	private String category = "";
	private String action  = "";
	private String label = "";

	
	
	private final static Logger log4j = Logger.getLogger(GoogleWebAnalyticsEvent.class);
	
	public GoogleWebAnalyticsEvent(){
			
	}
	public GoogleWebAnalyticsEvent(String category, String action, String label){
		this.category = category;
		this.action = action;
		this.label = label;
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

	
	/**
	 * Write out the js code to push the event to google.
	 */
	@Override
	public String getWebEvent() {
		log4j.info("Google Web Analytics Event Created : Category " + this.getCategory() + " Action: " + this.getAction() + " Label: " +  this.getLabel());
		return WebAnalyticsEventProperties.GOOGLE_TRACK_EVENT + ",'" + this.getCategory() + "','" + this.getAction() + "','" + this.getLabel() + "']);";
		
	}
			
}
