package org.ei.common.bd;

import java.util.*;
import org.ei.common.Constants;

public class BdPage
{
	private String pages;
	private String firstPage;
	private String lastPage;
	private String inputPageString;


	public BdPage()
	{

	}

	public BdPage(String inputPageString)
	{
		setPages(inputPageString);
	}

	public void setPageText(String pages)
	{
		this.pages = pages;
	}

	public void setFirstPage(String firstPage)
	{
		this.firstPage = firstPage;
	}

	public void setLastPage(String lastPage)
	{
		this.lastPage = lastPage;
	}

	public String getPageText()
	{
		return pages;
	}

	public String getFirstPage()
	{
		return firstPage;
	}

	public String getLastPage()
	{
		return lastPage;
	}

	public void setPages(String inputPageString)
	{
		this.inputPageString = inputPageString;
	}

	private BdPage parsePages()
	{
		BdPage pages = new BdPage();
		if(inputPageString != null)
		{
			String[] pagesArray = inputPageString.split(Constants.AUDELIMITER,-1);

			if(pagesArray!=null && pagesArray.length>2)
			{
				pages.setPageText(pagesArray[0]);
				pages.setFirstPage(pagesArray[1]);
				pages.setLastPage(pagesArray[2]);
			}
		}
		return pages;
	}

	public BdPage getPages()
	{
		return parsePages();
	}

	public String getStartPage()
	{
		BdPage page = parsePages();
		String firstPage = null;
		if(page.getPageText()!=null && page.getPageText().trim().length()>0)
		{
			firstPage = page.getPageText();
			firstPage = firstPage.replace('&',' ');
			firstPage = firstPage.replace(',',' ');
			firstPage = firstPage.replace(';',' ');
			firstPage = firstPage.replaceAll("[<>-]"," ");
			if(firstPage.indexOf(" ")>-1)
			{
				firstPage = firstPage.substring(0,firstPage.indexOf(" "));
			}
		}
		else if(page.getFirstPage()!=null && page.getFirstPage().trim().length()>0)
		{
			firstPage = page.getFirstPage();
		}
		else if(page.getLastPage()!=null && page.getLastPage().trim().length()>0)
		{
			firstPage = page.getLastPage();
		}
		return firstPage;
	}

}
