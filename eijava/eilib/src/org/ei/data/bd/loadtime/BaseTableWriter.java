package org.ei.data.bd.loadtime;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.data.bd.*;

public class BaseTableWriter
{
	private Perl5Util perl = new Perl5Util();

	private String filename;

	private PrintWriter out;

	private boolean open = false;

	private LinkedHashSet bdColumns = BaseTableRecord.getBdColumns();
	public static final char FIELDDELIM = '\t';
	private String accessNumber;

	public BaseTableWriter(String filename)
	{
		this.filename = filename;
	}

	public void begin()
			throws Exception
	{

		out = new PrintWriter(new FileWriter(filename));
		System.out.println("Output Filename "+filename);
		open = true;
	}

	public void writeRec(Hashtable record)
			throws Exception
	{

		StringBuffer recordBuf = new StringBuffer();
		Iterator bdData = bdColumns.iterator();
		while (bdData.hasNext())
		{
		    BaseTableRecord column = (BaseTableRecord)bdData.next();
			String thisColumnName = (String)column.getName();
			if(record == null)
			{
				System.out.println("Record was null");
			}
			Integer columnLength = null;
			String valueString = null;
			if(record.get(thisColumnName)!=null)
			{
			    columnLength =(Integer) column.getColumnLength();
				valueString = checkColumnWidth(columnLength.intValue(),
				        					   thisColumnName,
				        					   (String)record.get(thisColumnName));
				if(thisColumnName.equals("ACCESSNUMBER"))
				{
					setAccessionNumber(valueString);
				}
			}

			if(valueString != null)
			{
				recordBuf.append(valueString);
			}

			recordBuf.append(FIELDDELIM);
		}
		//System.out.println("record= "+recordBuf.toString().trim());
		out.println(recordBuf.toString().trim());

	}

	private String getAccessionNumber()
	{
		return this.accessNumber;
	}

	private void setAccessionNumber(String accessNumber)
	{
		this.accessNumber = accessNumber;
	}

	private String checkColumnWidth(int columnWidth,
	        						String columnName,
	        						String data) throws Exception
	{
		int cutOffPosition = 0;
		if(columnWidth > 0  && data!= null)
		{
			if(data.length()>columnWidth)
			{
				System.out.println("Problem:  record "+getAccessionNumber()+"'s data for column "+columnName+" is too big. data length is "+data.length());
				data = data.substring(0,columnWidth);
				cutOffPosition = data.lastIndexOf(BdParser.AUDELIMITER);
				if(cutOffPosition<data.lastIndexOf(BdParser.IDDELIMITER))
				{
					cutOffPosition = data.lastIndexOf(BdParser.IDDELIMITER);
				}
				if(cutOffPosition>0)
				{
					data = data.substring(0,cutOffPosition);
				}

			}
		}
		return data;
	}


	public void end()
			throws Exception
	{
		if(open)
		{
			out.close();
			open = false;
		}
	}
}