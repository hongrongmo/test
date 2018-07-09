package org.ei.dataloading.cafe;

public class MappingObj
{
	private String bd_accessnumber;
	private String cafe_accessnumber;
	private String bd_pui;
	private String cafe_pui;
	private String matched_criterion;
	private int loadnumber;
	
	public void setBdAccessnumber(String bd_an)
	{
		this.bd_accessnumber = bd_an;
	}
	
	public String getBdAccessnumber()
	{
		return this.bd_accessnumber;		
	}
	
	public void setCafeAccessnumber(String cafe_an)
	{
		this.cafe_accessnumber =cafe_an;
	}
	
	public String getCafeAccessnumber()
	{
		return this.cafe_accessnumber;		
	}
	
	public void setBdPui(String bd_pui)
	{
		this.bd_pui = bd_pui;
	}
	
	public String getBdPui()
	{
		return this.bd_pui;		
	}
	
	public void setMatchedCriterion(String criterion)
	{
		this.matched_criterion = criterion;
	}
	
	public String getMatchedCriterion()
	{
		return this.matched_criterion;		
	}
	
	public void setCafePui(String cafe_pui)
	{
		this.cafe_pui =cafe_pui;
	}
	
	public String getCafePui()
	{
		return this.cafe_pui;		
	}
	
	public void setLoadnumber(int loadnumber)
	{
		this.loadnumber =loadnumber;
	}
	
	public int getLoadnumber()
	{
		return this.loadnumber;		
	}
	
	
	
	
	
}