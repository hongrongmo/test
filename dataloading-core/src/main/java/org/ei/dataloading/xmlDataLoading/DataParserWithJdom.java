package org.ei.dataloading.xmlDataLoading;

//import org.ei.xml.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilterReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.ei.dataloading.DataValidator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class DataParserWithJdom extends FilterReader
{
	public static String preferedDatabase = "GEO";
	String 		name;
	String 		sName;

	String 		subTagContent;
	Hashtable 	attributes;
	private Document doc = null;
	BaseElement element;
	private Stack nodes;
	private List articles = null;
	OutputDataToFile out = null;
	static String fileName = "geoBase.data";
	//static final char delimited02 = ';';
	static final char delimited02 = 02;
	static final char delimited29 = 29;
	//static final char delimited29 = ':';
	static final char delimited30 = 30;
	//static final char delimited31 = 31;
	static final char delimited31 = '|';
	Iterator rec =null;

	public static void main(String[] args) throws Exception
	{

		String name = null;

		HashSet entity = null;
		if(args.length>0)
		{
			name = args[0];
		}
		else
		{
			System.out.println("please enter a filename");
		}

		if(args.length>1)
		{
			preferedDatabase = args[1];
		}

		DataValidator d = new DataValidator();
		String[] fileNameArray = null;

		if(name.indexOf(";")>-1)
		{
			fileNameArray = name.split(";");
		}
		else
		{
			fileNameArray = new String[1];
			fileNameArray[0] = name;
		}

		for(int i=0;i<fileNameArray.length;i++)
		{
			fileName = fileNameArray[i];
			d.validateFile(fileNameArray[i]);
			BufferedReader in = new BufferedReader(new FileReader(new File(fileNameArray[i])));
			DataParserWithJdom r = new DataParserWithJdom(in);
			r.getRecord();
		    //while((rec=r.getRecord())!=null)
		    //{
			//	System.out.println(rec.toString());
		 	//}
		}
		System.exit(1);
	}

	public DataParserWithJdom(Reader r)throws Exception
	{
		super(r);
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		this.doc = builder.build(this);
		Element geoRoot = doc.getRootElement();
		this.articles = geoRoot.getChildren("item");
		this.rec=articles.iterator();
	}

	public int getRecordCount()
	{
		return articles.size();
	}
	public void close()
	{
		System.out.println("Closed");
	}

	private void getProcess_Info(Element item,Hashtable record)
	{

		Element process_info 		= item.getChild("ait:process-info");
		Element date_delivered 		= process_info.getChild("ait:date-delivered");
		Element date_sort 			= process_info.getChild("ait:date-sort");
		Element status 				= process_info.getChild("ait:status");
		String  date_delivered_year = date_delivered.getAttributeValue("year");
		String  date_delivered_month= date_delivered.getAttributeValue("month");
		String  date_delivered_day  = date_delivered.getAttributeValue("day");
		String  date_sort_year 		= date_sort.getAttributeValue("year");
		String  date_sort_month		= date_sort.getAttributeValue("month");
		String  date_sort_day  		= date_sort.getAttributeValue("day");
		String  status_year 		= status.getAttributeValue("year");
		String  status_month		= status.getAttributeValue("month");
		String  status_day  		= status.getAttributeValue("day");

		System.out.println("data_delivered_year= "+date_delivered_year);
		System.out.println("data_delivered_month= "+date_delivered_month);
		System.out.println("data_delivered_day= "+date_delivered_day);

	}

	private void getBibrecord(Element item,Hashtable record)
	{

	}

	public Hashtable getRecord()
	{

		Hashtable record = new Hashtable();
		if(rec.hasNext())
		{
			Element item = (Element)rec.next();
			record = new Hashtable();

			/**** Item group ****/

			getProcess_Info(item,record);
			getBibrecord(item,record);
		}
		return record;
	}
}








