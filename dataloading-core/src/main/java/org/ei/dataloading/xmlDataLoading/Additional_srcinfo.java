package org.ei.dataloading.xmlDataLoading;

public class Additional_srcinfo extends BaseElement
{
	Secondaryjournal secondaryjournal;
	Conferenceinfo conferenceinfo;
	Reportinfo reportinfo;
	Toc toc;

	public void setSecondaryjournal(Secondaryjournal secondaryjournal)
	{
		this.secondaryjournal = secondaryjournal;
	}

	public Secondaryjournal getSecondaryjournal()
	{
		return this.secondaryjournal;
	}

	public void setConferenceinfo(Conferenceinfo conferenceinfo)
	{
		this.conferenceinfo = conferenceinfo;
	}

	public Conferenceinfo getConferenceinfo()
	{
		return this.conferenceinfo;
	}

	public void setReportinfo(Reportinfo reportinfo)
	{
		this.reportinfo = reportinfo;
	}

	public Reportinfo getReportinfo()
	{
		return this.reportinfo;
	}

	public void setToc(Toc toc)
	{
		this.toc = toc;
	}

	public Toc getToc()
	{
		return this.toc;
	}

}
