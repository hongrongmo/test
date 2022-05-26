package org.ei.dataloading.ntis.loadtime;

import java.io.*;
import java.util.*;
import org.jdom2.*;               //HH: 12/24/2014 replace jdom by new jdom2
import org.jdom2.input.*;		  // replace jdom by new jdom2
import org.jdom2.output.*;        // replace jdom by new jdom2
import org.ei.util.GUID;
import org.ei.util.MixedData;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
//import org.ei.data.encompasslit.loadtime.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ei.data.ntis.runtime.*;
import org.ei.common.Constants;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.bd.loadtime.*;

public class NTISParser
{
	private List articles = null;
	private Document doc = null;
	private Iterator rec =null;
	public Hashtable reccTable =null;
	private Perl5Util perl = new Perl5Util();
	private boolean inabstract = false;
	private HashSet entity = null;
	//public static final String AUDELIMITER = new String(new char[] {30});
	//public static final String IDDELIMITER = new String(new char[] {31});
	//public static final String GROUPDELIMITER = new String(new char[] {29});
	private static String weekNumber;
	private PrintWriter out;
	private SAXBuilder builder;
	private String accessNumber;
	//private String action;
	private String databaseName = "cpx";
	private NTISDataDictionary dictionary = new NTISDataDictionary();
	private Namespace xmlNamespace=Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace");
	private int affid = 0;


	MixedData obj = new MixedData();
	private DataLoadDictionary dataDictionary = new DataLoadDictionary();

	public NTISParser()
	{
		builder = new SAXBuilder();
		builder.setExpandEntities(false);
	}

	public NTISParser(Reader r)throws Exception
	{
		//super(r);

		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		builder.setFeature( "http://xml.org/sax/features/namespaces", true );
		this.doc = builder.build(r);
		Element ntisRoot = doc.getRootElement();
		List lt = ntisRoot.getChildren();
		this.articles = ntisRoot.getChildren();
		this.rec=articles.iterator();

		//HH added 04/22/2022 to check title against any special char to fix case Anna/Aaron reported N220004209 with right single quotation mark
		obj = new MixedData();
	}

	public void setOutputWriter(PrintWriter out)
	{
		this.out = out;
	}

	public PrintWriter getOutputWriter()
	{
		return this.out;
	}

	public void setWeekNumber(String weekNumber)
	{
		this.weekNumber = weekNumber;
	}

	public String getWeekNumber()
	{
		return this.weekNumber;
	}

	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}

	public String getDatabaseName()
	{
		return this.databaseName;
	}

	public void setAccessNumber(String accessNumber)
	{
		this.accessNumber = accessNumber;
	}

	public String getAccessNumber()
	{
		return this.accessNumber;
	}

	public void parseRecord(Reader r) throws Exception
	{
		this.doc = builder.build(r);
		doc.setBaseURI(".");
		Element ntisRoot = doc.getRootElement();
		setRecordTable(getRecord(ntisRoot));
	}

	public void setRecordTable(Hashtable recTable)
	{
		this.reccTable = recTable;
	}

	public Hashtable getRecordTable()
	{
		return this.reccTable;
	}

	public int getRecordCount()
	{
		return articles.size();
	}
	public void close()
	{
		System.out.println("Closed");
	}

	public Hashtable getRecord(Element ntisRoot) throws Exception
	{
		Hashtable recordTable = new Hashtable();
		Element source = null;
		String midstr= null;

		try
		{
			if(ntisRoot != null)
			{

				Element record = ntisRoot.getChild("Record");

				if(record!= null)
				{

					String mid = getDatabaseName().toLowerCase()+"_"+(new GUID()).toString();
					recordTable.put("M_ID",mid);

				    //ACCESS NUMBER
				    if(record.getChild("AccessionNumber")!=null)
				    {
						String accessionNumber = record.getChild("AccessionNumber").getTextTrim();
						recordTable.put("AccessionNumber",accessionNumber);

					}

					//CategoryCode

					List categoryCodeList 	= record.getChildren("CategoryCode");
					StringBuffer categoryCodeBuffer = new StringBuffer();
					for(int i=0;i<categoryCodeList.size();i++)
					{
						Element categoryCodeElement = (Element)categoryCodeList.get(i);
						String categoryCode = categoryCodeElement.getTextTrim();
						//if(categoryCode!=null && categoryCode.indexOf("|")>0)
						//{
						//	categoryCode=categoryCode.substring(0,categoryCode.indexOf("|"));
						//}
						categoryCodeBuffer.append(categoryCode.trim());
						if(i<categoryCodeList.size()-1)
						{
							categoryCodeBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("CategoryCode",categoryCodeBuffer.toString());

					//SourceCode

					List sourceCodeList 	= record.getChildren("SourceCode");
					StringBuffer sourceCodeBuffer = new StringBuffer();
					for(int i=0;i<sourceCodeList.size();i++)
					{
						Element sourceCodeElement = (Element)sourceCodeList.get(i);
						String sourceCode = sourceCodeElement.getTextTrim();
						sourceCodeBuffer.append(sourceCode.trim());
						if(i<sourceCodeList.size()-1)
						{
							sourceCodeBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("SourceCode",sourceCodeBuffer.toString());

					//CountryCode

					List countryCodeList 	= record.getChildren("CountryCode");
					StringBuffer countryCodeBuffer = new StringBuffer();
					for(int i=0;i<countryCodeList.size();i++)
					{
						Element countryCodeElement = (Element)countryCodeList.get(i);
						String countryCode = countryCodeElement.getTextTrim();
						countryCodeBuffer.append(countryCode.trim());
						if(i<countryCodeList.size()-1)
						{
							countryCodeBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("CountryCode",countryCodeBuffer.toString());

					//LanguageCode

					List languageCodeList 	= record.getChildren("LanguageCode");
					StringBuffer languageCodeBuffer = new StringBuffer();
					for(int i=0;i<languageCodeList.size();i++)
					{
						Element languageCodeElement = (Element)languageCodeList.get(i);
						String languageCode = languageCodeElement.getTextTrim();
						languageCodeBuffer.append(languageCode.trim());
						if(i<languageCodeList.size()-1)
						{
							languageCodeBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("LanguageCode",languageCodeBuffer.toString());

					//Media

					List mediaList 	= record.getChildren("Media");
					StringBuffer mediaBuffer = new StringBuffer();
					for(int i=0;i<mediaList.size();i++)
					{
						Element mediaElement = (Element)mediaList.get(i);
						String media = mediaElement.getTextTrim();
						mediaBuffer.append(media.trim());
						if(i<mediaList.size()-1)
						{
							mediaBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("Media",mediaBuffer.toString());


					//PrimaryAuthor
					 if(record.getChild("PrimaryAuthor")!=null)
					{
						String primaryAuthor = record.getChild("PrimaryAuthor").getTextTrim();
						recordTable.put("PrimaryAuthor",primaryAuthor);
					}

					//SecondaryAuthor

					List secondaryAuthorList 	= record.getChildren("SecondaryAuthor");
					StringBuffer secondaryAuthorBuffer = new StringBuffer();
					for(int i=0;i<secondaryAuthorList.size();i++)
					{
						Element secondaryAuthorElement = (Element)secondaryAuthorList.get(i);
						String secondaryAuthor = secondaryAuthorElement.getTextTrim();
						secondaryAuthorBuffer.append(secondaryAuthor.trim());
						if(i<secondaryAuthorList.size()-1)
						{
							secondaryAuthorBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("SecondaryAuthor",secondaryAuthorBuffer.toString());

					//SponsorAuthor
					List sponsorAuthorList 	= record.getChildren("SponsorAuthor");
					StringBuffer sponsorAuthorBuffer = new StringBuffer();
					for(int i=0;i<sponsorAuthorList.size();i++)
					{
						Element sponsorAuthorElement = (Element)sponsorAuthorList.get(i);
						String sponsorAuthor = sponsorAuthorElement.getTextTrim();
						sponsorAuthorBuffer.append(sponsorAuthor.trim());
						if(i<sponsorAuthorList.size()-1)
						{
							sponsorAuthorBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("SponsorAuthor",sponsorAuthorBuffer.toString());


					 //TITLE
					if(record.getChild("Title")!=null)
					{
						String title = record.getChild("Title").getTextTrim();
						//recordTable.put("Title", title);			// orig, HH commented out on 04/29/2022
						//HH added 04/29/2022, parse Title for any special character i.e. right single quotation mark (html entity &#x2019;) reported by Anna on 04/28/2022 
						recordTable.put("Title",dataDictionary.mapEntity(obj.getMixData(record.getChild("Title").getContent())));
					}


					 //GVVII
					if(record.getChild("GVVII")!=null)
					{
						String gvvii = record.getChild("GVVII").getTextTrim();
						recordTable.put("GVVII",gvvii);
					}


					//TitleNote
					if(record.getChild("TitleNote")!=null)
					{
						String titleNote = record.getChild("TitleNote").getTextTrim();
						recordTable.put("TitleNote",titleNote);
					}


					//PersonalAuthor
					List personalAuthorList 	= record.getChildren("PersonalAuthor");
					StringBuffer personalAuthorBuffer = new StringBuffer();
					for(int i=0;i<personalAuthorList.size();i++)
					{
						Element personalAuthorElement = (Element)personalAuthorList.get(i);
						String personalAuthor = personalAuthorElement.getTextTrim();
						personalAuthorBuffer.append(personalAuthor.trim());
						if(i<personalAuthorList.size()-1)
						{
							personalAuthorBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("PersonalAuthor",personalAuthorBuffer.toString());

					//ReportMonth
					if(record.getChild("ReportMonth")!=null)
					{
						String reportMonth = record.getChild("ReportMonth").getTextTrim();
						recordTable.put("ReportMonth",reportMonth);
					}

					//ReportDay
					if(record.getChild("ReportDay")!=null)
					{
						String reportDay = record.getChild("ReportDay").getTextTrim();
						recordTable.put("ReportDay",reportDay);
					}

					//ReportYear
					if(record.getChild("ReportYear")!=null)
					{
						String reportYear = record.getChild("ReportYear").getTextTrim();
						recordTable.put("ReportYear",reportYear);
					}

					//PageCount
					if(record.getChild("PageCount")!=null)
					{
						String pageCount = record.getChild("PageCount").getTextTrim();
						recordTable.put("PageCount",pageCount);
					}

					//ReportNumber
					List reportNumberList 	= record.getChildren("ReportNumber");
					StringBuffer reportNumberBuffer = new StringBuffer();
					for(int i=0;i<reportNumberList.size();i++)
					{
						Element reportNumberElement = (Element)reportNumberList.get(i);
						String reportNumber = reportNumberElement.getTextTrim();
						reportNumberBuffer.append(reportNumber.trim());
						if(i<reportNumberList.size()-1)
						{
							reportNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("ReportNumber",reportNumberBuffer.toString());

					//OrderNumber
					List orderNumberList 	= record.getChildren("OrderNumber");
					StringBuffer orderNumberBuffer = new StringBuffer();
					for(int i=0;i<orderNumberList.size();i++)
					{
						Element orderNumberElement = (Element)orderNumberList.get(i);
						String orderNumber = orderNumberElement.getTextTrim();
						orderNumberBuffer.append(orderNumber.trim());
						if(i<orderNumberList.size()-1)
						{
							orderNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("OrderNumber",orderNumberBuffer.toString());

					//GrantNumber
					List grantNumberList 	= record.getChildren("GrantNumber");
					StringBuffer grantNumberBuffer = new StringBuffer();
					for(int i=0;i<grantNumberList.size();i++)
					{
						Element grantNumberElement = (Element)grantNumberList.get(i);
						String grantNumber = grantNumberElement.getTextTrim();
						grantNumberBuffer.append(grantNumber.trim());
						if(i<grantNumberList.size()-1)
						{
							grantNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("GrantNumber",grantNumberBuffer.toString());

					//ContractNumber
					List contractNumberList 	= record.getChildren("ContractNumber");
					StringBuffer contractNumberBuffer = new StringBuffer();
					for(int i=0;i<contractNumberList.size();i++)
					{
						Element contractNumberElement = (Element)contractNumberList.get(i);
						String contractNumber = contractNumberElement.getTextTrim();
						contractNumberBuffer.append(contractNumber.trim());
						if(i<contractNumberList.size()-1)
						{
							contractNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("ContractNumber",contractNumberBuffer.toString());

					//ProjectNumber
					List projectNumberList 	= record.getChildren("ProjectNumber");
					StringBuffer projectNumberBuffer = new StringBuffer();
					for(int i=0;i<projectNumberList.size();i++)
					{
						Element projectNumberElement = (Element)projectNumberList.get(i);
						String projectNumber = projectNumberElement.getTextTrim();
						projectNumberBuffer.append(projectNumber.trim());
						if(i<projectNumberList.size()-1)
						{
							projectNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("ProjectNumber",projectNumberBuffer.toString());

					//TaskNumber
					List taskNumberList 	= record.getChildren("TaskNumber");
					StringBuffer taskNumberBuffer = new StringBuffer();
					for(int i=0;i<taskNumberList.size();i++)
					{
						Element taskNumberElement = (Element)taskNumberList.get(i);
						String taskNumber = taskNumberElement.getTextTrim();
						taskNumberBuffer.append(taskNumber.trim());
						if(i<taskNumberList.size()-1)
						{
							taskNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("TaskNumber",taskNumberBuffer.toString());


					//MonitorAgencyNumber
					List monitorAgencyNumberList 	= record.getChildren("MonitorAgencyNumber");
					StringBuffer monitorAgencyNumberBuffer = new StringBuffer();
					for(int i=0;i<monitorAgencyNumberList.size();i++)
					{
						Element monitorAgencyNumberElement = (Element)monitorAgencyNumberList.get(i);
						String monitorAgencyNumber = monitorAgencyNumberElement.getTextTrim();
						monitorAgencyNumberBuffer.append(monitorAgencyNumber.trim());
						if(i<monitorAgencyNumberList.size()-1)
						{
							monitorAgencyNumberBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("MonitorAgencyNumber",monitorAgencyNumberBuffer.toString());


					//SupplementaryNotes
					if(record.getChild("SupplementaryNotes")!=null)
					{
						String supplementaryNotes = record.getChild("SupplementaryNotes").getTextTrim();
						recordTable.put("SupplementaryNotes",supplementaryNotes);
					}


					//AvailabilityNote
					if(record.getChild("AvailabilityNote")!=null)
					{
						String supplementaryNotes = record.getChild("AvailabilityNote").getTextTrim();
						recordTable.put("AvailabilityNote",supplementaryNotes);
					}


					//Descriptor
					List descriptorList 	= record.getChildren("Descriptor");
					StringBuffer descriptorBuffer = new StringBuffer();
					for(int i=0;i<descriptorList.size();i++)
					{
						Element descriptorElement = (Element)descriptorList.get(i);
						String descriptor = descriptorElement.getTextTrim();
						descriptorBuffer.append(descriptor.trim());
						if(i<descriptorList.size()-1)
						{
							descriptorBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("Descriptor",descriptorBuffer.toString());


					//Identifier

					List identifierList 	= record.getChildren("Identifier");
					StringBuffer identifierBuffer = new StringBuffer();
					for(int i=0;i<identifierList.size();i++)
					{
						Element identifierElement = (Element)identifierList.get(i);
						String identifier = identifierElement.getTextTrim();
						identifierBuffer.append(identifier.trim());
						if(i<identifierList.size()-1)
						{
							identifierBuffer.append(Constants.AUDELIMITER);
						}
					}
					recordTable.put("Identifier",identifierBuffer.toString());


					//Abstract
					if(record.getChild("Abstract")!=null)
					{
						String abstractString = record.getChild("Abstract").getTextTrim();
						recordTable.put("Abstract",abstractString);
					}


					//DTICLimitationCode
					if(record.getChild("DTICLimitationCode")!=null)
					{
						String dTICLimitationCode = record.getChild("DTICLimitationCode").getTextTrim();
						recordTable.put("DTICLimitationCode",dTICLimitationCode);
					}


					//PrimaryAuthorCode
					if(record.getChild("PrimaryAuthorCode")!=null)
					{
						String primaryAuthorCode = record.getChild("PrimaryAuthorCode").getTextTrim();
						recordTable.put("PrimaryAuthorCode",primaryAuthorCode);
					}


					//SecondaryAuthorCode
					if(record.getChild("SecondaryAuthorCode")!=null)
					{
						String secondaryAuthorCode = record.getChild("SecondaryAuthorCode").getTextTrim();
						recordTable.put("SecondaryAuthorCode",secondaryAuthorCode);
					}

					//Load_number
						recordTable.put("LOAD_NUMBER",getWeekNumber());

				}
				else
				{
					System.out.println("record is null");
				}

			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("SIZE="+recordTable.size());
		return recordTable;
	}


	private  String getMixData(List l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }

    private  StringBuffer getMixData(List l, StringBuffer b)
    {
        Iterator it = l.iterator();

        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				String text=((Text)o).getText();

				//System.out.println("text::"+text);

				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);

				b.append(text);

            }
            else if(o instanceof EntityRef)
            {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }

        return b;
    }

    private  StringBuffer getMixCData(List l, StringBuffer b)
	{
		inabstract=true;
		b=getMixData(l,b);
		inabstract=false;
		return b;
	}

}
