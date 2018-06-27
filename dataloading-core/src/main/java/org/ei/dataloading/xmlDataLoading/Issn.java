package org.ei.dataloading.xmlDataLoading;

public class Issn extends BaseElement
{
	String issn;
	String issn_type;

    public void setIssn(String issn)
	{
		this.issn = issn;
	}

	public String getIssn()
	{
		return this.issn;
	}

	public void setIssn_type(String issn_type)
	{
		this.issn_type = issn_type;
	}

	public String getIssn_type()
	{
		return this.issn_type;
	}
}
