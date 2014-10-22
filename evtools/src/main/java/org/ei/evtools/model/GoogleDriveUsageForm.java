package org.ei.evtools.model;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class GoogleDriveUsageForm {
	
	private String usageOption;
    private String startDate;
    private String endDate;
    
	public String getUsageOption() {
		return usageOption;
	}
	public void setUsageOption(String usageOption) {
		this.usageOption = usageOption;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
