package org.ei.dataloading.xmlDataLoading;

public class Confevent extends BaseElement
{
	String confname;
	String confnumber;
	Conflocation conflocation;
	String conflocation_country;
	Confdate confdate;
	String confcatnumber;
	String confcode;
	Confsponsors confsponsors;


	public void setConfname(String confname)
	{
		this.confname = confname;
	}

	public String getConfname()
	{
		return this.confname;
	}

	public void setConfnumber(String confnumber)
	{
		this.confnumber = confnumber;
	}

	public String getConfnumber()
	{
		return this.confnumber;
	}

	public void setConflocation(Conflocation conflocation)
	{
		this.conflocation = conflocation;
	}

	public Conflocation getConflocation()
	{
		return this.conflocation;
	}

	public void setConflocation_country(String conflocation_country)
	{
		this.conflocation = conflocation;
	}

	public String getConflocation_country()
	{
		return this.conflocation_country;
	}

	public void setConfdate(Confdate confdate)
	{
		this.confdate = confdate;
	}

	public Confdate getConfdate()
	{
		return this.confdate;
	}

	public void setConfcatnumber(String confcatnumber)
	{
		this.confcatnumber = confcatnumber;
	}

	public String getConfcatnumber()
	{
		return this.confcatnumber;
	}

	public void setConfcode(String confcode)
	{
		this.confcode = confcode;
	}

	public String getConfcode()
	{
		return this.confcode;
	}

	public void setConfsponsors(Confsponsors confsponsors)
	{
		this.confsponsors = confsponsors;
	}

	public Confsponsors getConfsponsors()
	{
		return this.confsponsors;
	}

}
