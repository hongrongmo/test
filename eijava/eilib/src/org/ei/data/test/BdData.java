package org.ei.data.test;

public class BdData
{
	String mid;
	String citationTitle;
	String volume;
	String issue;
	String issn;
	String page;
	String publicationYear;
	String updateFlag;

	public void setMid(String mid)
	{
		this.mid = mid;
	}

	public void setCitationTitle(String citationTitle)
	{
		this.citationTitle = citationTitle;
	}

	public void setVolume(String volume)
	{
		this.volume = volume;
	}

	public void setIssue(String issue)
	{
		this.issue = issue;
	}

	public void setIssn(String issn)
	{
		this.issn = issn;
	}

	public void setPage(String page)
	{
		this.page = page;
	}

	public void setPublicationYear(String publicationYear)
	{
		this.publicationYear = publicationYear;
	}

	public void setUpdateFlag(String updateFlag)
	{
		this.updateFlag = updateFlag;
	}

	public String getMid()
	{
		return this.mid;
	}

	public String getCitationTitle()
	{
		return this.citationTitle;
	}

	public String getVolume()
	{
		return this.volume;
	}

	public String getIssue()
	{
		return this.issue;
	}

	public String getIssn()
	{
		return this.issn;
	}

	public String getPage()
	{
		return this.page;
	}

	public String getPublicationYear()
	{
		return this.publicationYear;
	}

	public String getUpdateFlag()
	{
		return this.updateFlag;
	}

	public boolean equals(BdData o)
	{
		String cpxTitle = o.getCitationTitle();
		String bdTitle  = null;
		if(this.citationTitle.length()>cpxTitle.length())
		{
			bdTitle = this.citationTitle;
		}
		else
		{
			bdTitle = cpxTitle;
			cpxTitle= this.citationTitle;
		}
		if(bdTitle.indexOf(cpxTitle)>-1)
		{
			if(this.issn != null && o.getIssn() != null)
			{
				String cpxIssn = o.getIssn().replaceAll("-","");
				String bdIssn  = this.issn.replaceAll("-","");
				if(cpxIssn.equals(bdIssn))
				{
					return true;
				}
			}
			else if(this.issn == null && o.getIssn() ==null)
			{
				return true;
			}

		}
		return false;
	}



}