package org.ei.common.bd;

import java.util.*;
import org.ei.common.Constants;

public class BdPersonalName
{
	private String initials;
	private String indexedName;
	private String degrees;
	private String surname;
	private String givenName;
	private String suffix;
	private String nametext;
	private String text;
	private String id;
	private String personalName;
	private static final int idIndex = 0;
	private static final int initialsIndex = 1;
	private static final int indexedNameIndex = 2;
	private static final int degreesIndex = 3;
	private static final int surnameIndex = 4;
	private static final int givenNameIndex = 5;
	private static final int suffixIndex = 6;
	private static final int nametextIndex = 7;
	private static final int textIndex = 8;


	public void setID(String id)
	{
		this.id = id;
	}

	public void setInitials(String initials)
	{
		this.initials = initials;
	}

	public void setIndexedName(String indexedName)
	{
		this.indexedName = indexedName;
	}

	public void setDegrees(String degrees)
	{
		this.degrees = degrees;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public void setGivenName(String givenName)
	{
		this.givenName = givenName;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public void setNametext(String nametext)
	{
		this.nametext = nametext;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getInitials()
	{
		return initials;
	}

	public String getID()
	{
		return id;
	}

	public String getIndexedName()
	{
		return indexedName;
	}

	public String getDegrees()
	{
		return degrees;
	}

	public String getSurname()
	{
		return surname;
	}

	public String getGivenName()
	{
		return givenName;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public String getNametext()
	{
		return nametext;
	}

	public String getText()
	{
		return text;
	}

	public void setPersonalName(String personalName)
	{
		this.personalName = personalName;
	}

	public List getPersonalName()
	{
		return parsePersonalNameString();
	}

	private List parsePersonalNameString()
	{
		List personalNameList = new ArrayList();
		if(personalName != null && personalName.indexOf(Constants.AUDELIMITER)>0)
		{
			StringTokenizer personalNameGroupToken = new StringTokenizer(personalName,Constants.AUDELIMITER);

			while(personalNameGroupToken.hasMoreTokens())
			{
				String personalNameGroupString=personalNameGroupToken.nextToken();
				String[] singlePersonalNameObject = null;
				if(personalNameGroupString != null && personalNameGroupString.indexOf(Constants.IDDELIMITER)>0)
				{
					singlePersonalNameObject = personalNameGroupString.split(Constants.IDDELIMITER);
					BdPersonalName personalNameObject = new BdPersonalName();
					if(singlePersonalNameObject!=null && singlePersonalNameObject.length>8)
					{
						personalNameObject.setID(singlePersonalNameObject[idIndex]);
						personalNameObject.setInitials(singlePersonalNameObject[initialsIndex]);
						personalNameObject.setIndexedName(singlePersonalNameObject[indexedNameIndex]);
						personalNameObject.setDegrees(singlePersonalNameObject[degreesIndex]);
						personalNameObject.setSurname(singlePersonalNameObject[surnameIndex]);
						personalNameObject.setGivenName(singlePersonalNameObject[givenNameIndex]);
						personalNameObject.setSuffix(singlePersonalNameObject[suffixIndex]);
						personalNameObject.setNametext(singlePersonalNameObject[nametextIndex]);
						personalNameObject.setText(singlePersonalNameObject[textIndex]);

					}
					personalNameList.add(personalNameObject);
				}

			}
		}
		return personalNameList;
	}

}
