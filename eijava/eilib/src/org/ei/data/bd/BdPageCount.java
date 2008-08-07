package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdPageCount
{
	private String type;
	private String value;
	private String pageCountString;

	public BdPageCount()
	{
	}

	public BdPageCount(String pageCount)
	{
		setPageCount(pageCount);
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	public void setPageCount(String pageCountString)
	{
		this.pageCountString = pageCountString;
	}

	public List getPageCount()
	{
		return parsePageCount();
	}

	private List parsePageCount()
	{
		List pageCountList = new ArrayList();
		if(pageCountString != null && pageCountString.indexOf(BdParser.AUDELIMITER)>0)
		{
			StringTokenizer pageCountToken = new StringTokenizer(pageCountString,BdParser.AUDELIMITER);

			while(pageCountToken.hasMoreTokens())
			{
				String pageCountGroupString=pageCountToken.nextToken();
				String[] singlePageCountObject = null;
				BdPageCount pageCount = new BdPageCount();
				if(pageCountGroupString != null)
				{
					if(pageCountGroupString.indexOf(BdParser.IDDELIMITER)>0)
					{
						singlePageCountObject = pageCountGroupString.split(BdParser.IDDELIMITER);
						if(singlePageCountObject!=null && singlePageCountObject.length>1)
						{
							pageCount.setType(singlePageCountObject[0]);
							pageCount.setValue(singlePageCountObject[1]);
						}
						pageCountList.add(pageCount);
					}
					else
					{
						pageCount.setValue(singlePageCountObject[1]);
					}
				}

			}
		}
		return pageCountList;
	}
}