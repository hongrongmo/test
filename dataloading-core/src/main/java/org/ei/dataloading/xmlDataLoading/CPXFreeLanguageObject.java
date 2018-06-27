package org.ei.dataloading.xmlDataLoading;

import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;

public class CPXFreeLanguageObject
{
	Hashtable outputTable = new Hashtable();
	Perl5Util perl = new Perl5Util();
	String accessionNumber="";
	String year="";
	String freeLanguage="";
	String classficationCode="";
	String controllTerm = "";
	int rowID = 0;
	int count = 0;

	public int getRowID()
	{
		return this.rowID;
	}

	public int getCount()
	{
		return this.count;
	}

	public String getAccessionNumber()
	{
		return this.accessionNumber;
	}

	public String getYear()
	{
		return this.year;
	}

	public String getFreeLanguage()
	{
		return this.freeLanguage;
	}
	public String getClassificationCode()
	{
		return this.classficationCode;
	}
	public String getControllTerm()
	{
		return this.controllTerm;
	}

	public void setAccessionNumber(String accessionNumber)
	{
		this.accessionNumber = accessionNumber;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public void setRowID(int rowID)
	{
		this.rowID = rowID;
	}

	public void setCount(int count)
	{
		this.count = count;
	}


	public void setFreeLanguage(String freeLanguage)
	{
		this.freeLanguage = freeLanguage;
	}

	public void setClassificationCode(String classficationCode)
	{
		this.classficationCode = classficationCode;
	}

	public void setControllTerm(String controllTerm)
	{
		this.controllTerm = controllTerm;
	}


}
