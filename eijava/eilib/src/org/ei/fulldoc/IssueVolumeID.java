package org.ei.fulldoc;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

public class IssueVolumeID implements OHUBID
{
	private String ISSN;
	private String firstVolume;
	private String firstPage;
	private String firstIssue;

	private String[] issuePatterns = {"/[1-9][0-9]*/"};

	public String getSaltString()
	{
		return getISSN()+notNull(getFirstVolume())+notNull(getFirstPage())+notNull(getFirstIssue());
	}

	public String getXMLString()
	{
		StringBuffer xbuf = new StringBuffer("<ivp issn=\""+ ISSN + "\" ");
		if(firstVolume != null)
		{
			xbuf.append("firstVolume=\""+ firstVolume + "\" ");
		}

		if(firstPage != null)
		{
			xbuf.append("firstPage=\""+ firstPage + "\" ");
		}

		if(firstIssue != null)
		{
			xbuf.append("firstIssue=\""+ firstIssue + "\" ");
		}

		xbuf.append("/>");

		return xbuf.toString();
	}

	private String notNull(String testString)
	{
		String r = null;

		if(testString == null)
		{
			r = "";
		}
		else
		{
			r = testString;
		}

		return r;
	}

	public IssueVolumeID()
	{
		super();
	}

	public void setISSN(String ISSN)
	{
		this.ISSN = ISSN;
	}

	public String getISSN()
	{
		return this.ISSN;
	}

	public void setFirstVolume(String firstVolume)
	{
		this.firstVolume = firstVolume;
	}

	public String getFirstVolume()
	{
		return this.firstVolume;
	}

	public void setFirstPage(String firstPage)
	{
		this.firstPage = firstPage;
	}

	public String getFirstPage()
	{
		return this.firstPage;
	}

	public void setFirstIssue(String firstIssue)
	{
		this.firstIssue = fixIssue(firstIssue);
	}

	public String getFirstIssue()
	{
		return this.firstIssue;
	}

	private String fixIssue(String issue)
	{
		Perl5Util perl = new Perl5Util();
		String i = null;
		for(int x=0; x<issuePatterns.length; ++x)
		{
			String pattern = issuePatterns[x];
			if(perl.match(pattern, issue))
			{
				MatchResult mResult = perl.getMatch();
				i = mResult.toString();
//				System.out.println("The Issue:"+i);
				break;
			}
		}

		return i;
	}

}
