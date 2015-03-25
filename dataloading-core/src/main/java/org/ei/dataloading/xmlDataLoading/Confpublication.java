package org.ei.dataloading.xmlDataLoading;

public class Confpublication extends BaseElement
{

  	Confeditors confeditors;
  	String procpartno;
  	String procpagerange;
  	String procpagecount;

	public void setConfeditors(Confeditors confeditors)
	{
		this.confeditors = confeditors;
	}

	public Confeditors getConfeditors()
	{
		return this.confeditors;
	}

	public void setProcpartno(String procpartno)
	{
		this.procpartno = procpartno;
	}

	public String getProcpartno()
	{
		return this.procpartno;
	}

	public void setProcpagerange(String procpagerange)
	{
		this.procpagerange = procpagerange;
	}

	public String getProcpagerange()
	{
		return this.procpagerange;
	}

	public void setProcpagecount(String procpagecount)
	{
		this.procpagecount = procpagecount;
	}

	public String getProcpagecount()
	{
		return this.procpagecount;
	}

}
