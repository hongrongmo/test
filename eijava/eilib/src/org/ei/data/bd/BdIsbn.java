package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdIsbn
{
	private String type;
	private String length;
	private String volume;
	private String value;
	private String isbnString;
	private String isbn10;
	private String isbn13;

	public BdIsbn()
	{
	}

	public BdIsbn(String isbnString)
	{
		setISBN(isbnString);
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setLength(String length)
	{
		this.length = length;
	}

	public void setVolume(String volume)
	{
		this.volume = volume;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	public String getLength()
	{
		return length;
	}

	public String getVolume()
	{
		return volume;
	}

	public String getValue()
	{
		return value;
	}

	public void setISBN(String isbnString)
	{
		this.isbnString = isbnString;
	}

	public List getISBN() throws Exception
	{
		return parseISBNString();
	}

	public String getISBN10() throws Exception
	{
		List bdisbnList = this.getISBN();
		String isbnString = null;

		for(int i=0;i<bdisbnList.size();i++)
		{
			BdIsbn isbnObject = (BdIsbn)bdisbnList.get(i);
			if(isbnObject.getValue()!=null && isbnObject.getLength().equals("10"))
			{
				isbnString = isbnObject.getValue();
			}
		}
		return isbnString;
	}

	public String getISBN13() throws Exception
	{
		List bdisbnList = this.getISBN();
		String isbnString = null;

		for(int i=0;i<bdisbnList.size();i++)
		{
			BdIsbn isbnObject = (BdIsbn)bdisbnList.get(i);
			if(isbnObject.getValue()!=null && isbnObject.getLength().equals("13"))
			{
				isbnString = isbnObject.getValue();
			}
		}
		return isbnString;
	}

	private List parseISBNString()
	{
		List isbnList = new ArrayList();

		if(isbnString != null)
		{
			String[] isbnGroupArray = isbnString.split(BdParser.AUDELIMITER);

			for(int j=0;j<isbnGroupArray.length;j++)
			{
				String isbnGroupString=isbnGroupArray[j];
				String[] singleIsbnObject = null;
				if(isbnGroupString != null && isbnGroupString.indexOf(BdParser.IDDELIMITER)>-1)
				{
					BdIsbn isbn = new BdIsbn();
					singleIsbnObject = isbnGroupString.split(BdParser.IDDELIMITER);

					if(singleIsbnObject!=null && singleIsbnObject.length>3)
					{
						isbn.setType(singleIsbnObject[0]);
						isbn.setLength(singleIsbnObject[1]);
						isbn.setVolume(singleIsbnObject[2]);
						isbn.setValue(singleIsbnObject[3]);

					}
					isbnList.add(isbn);
				}

			}
		}
		return isbnList;
	}
}