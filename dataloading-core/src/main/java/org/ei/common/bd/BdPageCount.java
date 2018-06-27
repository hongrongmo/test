package org.ei.common.bd;

import java.util.*;
import org.ei.common.Constants;

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
		if(pageCountString != null)
		{
			StringTokenizer pageCountToken = new StringTokenizer(pageCountString,Constants.AUDELIMITER);

			while(pageCountToken.hasMoreTokens())
			{
				String pageCountGroupString=pageCountToken.nextToken();
				String[] singlePageCountObject = null;
				BdPageCount pageCount = new BdPageCount();
				if(pageCountGroupString != null)
				{
					if(pageCountGroupString.indexOf(Constants.IDDELIMITER)>0)
					{
						singlePageCountObject = pageCountGroupString.split(Constants.IDDELIMITER);
						if(singlePageCountObject!=null && singlePageCountObject.length>1)
						{
							pageCount.setType(singlePageCountObject[0]);
							pageCount.setValue(singlePageCountObject[1]);
						}

					}
					else
					{
						pageCount.setValue(pageCountGroupString);
					}
					pageCountList.add(pageCount);
				}

			}
		}
		return pageCountList;
	}

	public String getDisplayValue()
	{
		List pageCount = parsePageCount();
		StringBuffer sbuffer = new StringBuffer();
		for(int i=0; i<pageCount.size(); i++)
		{
			if(sbuffer.length()>0)
			{
				sbuffer.append("; ");
			}
			BdPageCount page = (BdPageCount)pageCount.get(i);

			if(page.getValue() !=null )
			{
				sbuffer.append(page.getValue());
			}
		}
		if(sbuffer.length()>0)
			return sbuffer.toString();
		else
			return null;
	}
}
