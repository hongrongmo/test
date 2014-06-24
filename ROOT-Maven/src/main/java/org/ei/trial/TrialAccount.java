package org.ei.trial;


public class TrialAccount
{
	private String username;
	private String password;
	private String beginDate;
	private String endDate;
	private String prodName;
	
	
	public TrialAccount(String username,
			    String password,
			    String beginDate,
			    String endDate,
			    String prodName)
	{
		this.username = username;
		this.password = password;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.prodName = prodName;
	}

	public String getProdName()
	{
		return this.prodName;
	}

	public void setProdName(String prodName)
	{
		this.prodName = prodName;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return this.password;
	}

	public String getBeginDate()
	{
		return this.beginDate;
	}

	public void setBeginDate(String beginDate)
	{
		this.beginDate = beginDate;
	}

	public String getEndDate()
	{
		return this.endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
}
