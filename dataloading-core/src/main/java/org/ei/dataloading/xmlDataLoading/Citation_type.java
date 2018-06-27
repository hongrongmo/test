package org.ei.dataloading.xmlDataLoading;

public class Citation_type extends BaseElement
{
	String citation_type;
	String citation_type_code;

	public void setCitation_type(String citation_type)
	{
		this.citation_type = citation_type;
	}

	public String getCitation_type()
	{
		return citation_type;
	}

	public void setCitation_type_code(String citation_type_code)
	{
		this.citation_type_code = citation_type_code;
	}

	public String getCitation_type_code()
	{
		return citation_type_code;
	}
}
