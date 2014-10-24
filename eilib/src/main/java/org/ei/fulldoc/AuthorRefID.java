package org.ei.fulldoc;


public class AuthorRefID implements OHUBID
{

	private String firstAuthorSurname;
	private String year;
	private String firstPage;
	private String lastPage;
	private String firstAuthorInitial;
	
	public String getSaltString()
	{
		return getFirstAuthorSurname()+getYear()+getFirstPage()+notNull(getLastPage())+notNull(getFirstAuthorInitial());
	}
	
	public String getXMLString()
	{
		StringBuffer xbuf = new StringBuffer("<ref firstAuthorSurname=\""+ this.firstAuthorSurname + "\" ");
		xbuf.append("year=\""+ this.year + "\" ");
		xbuf.append("firstPage=\""+ this.firstPage + "\" ");
		
		if(lastPage != null)
		{
			xbuf.append("lastPage=\""+ this.lastPage + "\" ");	
		}
		
		if(firstAuthorInitial != null)
		{
			xbuf.append("firstAuthorInitial=\""+ this.firstAuthorInitial + "\" ");	
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
	
	public void setFirstAuthorSurname(String firstAuthorSurname)
	{
		this.firstAuthorSurname = firstAuthorSurname;
	}
	
	public String getFirstAuthorSurname()
	{
		return this.firstAuthorSurname;	
	}
	
	public void setYear(String year)
	{
		this.year = year;
	}
	
	public String getYear()
	{
		return this.year;	
	}
	
	public void setFirstPage(String firstPage)
	{
		this.firstPage = firstPage;
	}
	
	public String getFirstPage()
	{
		return this.firstPage;	
	}
	
	public void setLastPage(String lastPage)
	{
		this.lastPage = lastPage;
	}
	
	public String getLastPage()
	{
		return this.lastPage;
	}
	
	public void setFirstAuthorInitial(String firstAuthorInitial)
	{
		this.firstAuthorInitial = firstAuthorInitial;	
	}
	
	public String getFirstAuthorInitial()
	{
		return this.firstAuthorInitial;	
	}
}
