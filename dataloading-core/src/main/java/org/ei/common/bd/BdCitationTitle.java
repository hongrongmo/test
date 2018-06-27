package org.ei.common.bd;

import java.util.*;
import org.ei.common.Constants;

public class BdCitationTitle
{
	private String id;
	private String original;
	private String language;
	private String title;
	private String citationTitle;
	private List citationTitleList = new ArrayList();;
	private List translatedCitationTitleList = new ArrayList();
	private TreeMap displayValueMap = new TreeMap();

	public void setDisplayValue(String citTitle)
	{
	    setCitationTitle(citTitle);
	    for (int i = 0; i < citationTitleList.size(); i++)
	    {
	        BdCitationTitle bdtitle = (BdCitationTitle) citationTitleList.get(i);
	        displayValueMap.put(new Integer(bdtitle.getId()),bdtitle );
	    }
	    for (int i = 0; i < translatedCitationTitleList.size(); i++)
	    {
	        BdCitationTitle bdtitle = (BdCitationTitle) translatedCitationTitleList.get(i);
	        displayValueMap.put(new Integer(bdtitle.getId()),bdtitle );
	    }
	}

	public TreeMap getDisplayValue()
	{
	    return displayValueMap;
	}

	public BdCitationTitle()
	{

	}

	public BdCitationTitle(String citationTitle)
	{
		setCitationTitle(citationTitle);
	}
	public void setId(String id)
	{
		this.id = id;
	}

	public void setOriginal(String original)
	{
		this.original = original;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getId()
	{
		return id;
	}

	public String getOriginal()
	{
		return original;
	}

	public String getLanguage()
	{
		return language;
	}

	public String getTitle()
	{
		return title;
	}

	public void setCitationTitle(String citationTitle)
	{
		this.citationTitle = citationTitle;
		parseCitationTitle();
	}

	private void parseCitationTitle()
	{
		if(citationTitle != null)
		{
			String[] citationTitleGroup = citationTitle.split(Constants.AUDELIMITER,-1);

			for(int i=0;i<citationTitleGroup.length;i++)
			{
				String ctGroupString=citationTitleGroup[i];
				String[] singleCtObject = null;
				if(ctGroupString != null && ctGroupString.indexOf(Constants.IDDELIMITER)>-1)
				{
					BdCitationTitle ct = new BdCitationTitle();
					singleCtObject = ctGroupString.split(Constants.IDDELIMITER,-1);
					if(singleCtObject!=null && singleCtObject.length>3)
					{
						ct.setId(singleCtObject[0]);
						ct.setTitle(singleCtObject[1]);
						ct.setOriginal(singleCtObject[2]);
						ct.setLanguage(singleCtObject[3]);

						if(singleCtObject[3].equalsIgnoreCase("eng")||singleCtObject[3].equalsIgnoreCase("en") )
						{
							citationTitleList.add(ct);
						}
						else
						{
							translatedCitationTitleList.add(ct);
						}

					}
				}

			}
		}
	}

	public List getCitationTitle()
	{
		return citationTitleList;
	}

	public List getTranslatedCitationTitle()
	{
		return translatedCitationTitleList;
	}

}
