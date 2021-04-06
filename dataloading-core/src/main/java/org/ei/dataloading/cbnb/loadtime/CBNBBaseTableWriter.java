package org.ei.dataloading.cbnb.loadtime;

import java.util.*;
import java.io.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import org.ei.common.Constants;
import org.ei.common.Language;

public class CBNBBaseTableWriter
{

    private Perl5Util perl = new Perl5Util();
    private int recsPerFile = -1;
    private int curRecNum = 0;
    private String filename;
    private String propsfilename;
    private PrintWriter out;
    private String filepath;
    private int loadnumber;
    private int filenumber = 0;
    private boolean open = false;
    private Properties props;
    private Properties xmlprops;
    private String[] baseTableFields = CBNBBaseTableRecord.baseTableFields;
    private CBNBRecordFixer fixer = new CBNBRecordFixer();

    {
        props = new Properties();
        props.setProperty("M_ID", "GU");
        props.setProperty("ABN", "ABN");
        props.setProperty("CDT", "CDT");
        props.setProperty("DOC", "DOC");
        props.setProperty("SCO", "SCO");
        props.setProperty("FJL", "FJL");
        props.setProperty("ISN", "ISN");
        props.setProperty("CDN", "CDN");
        props.setProperty("LAN", "LAN");
        props.setProperty("VOL", "VOL");
        props.setProperty("ISS", "ISS");
        props.setProperty("IBN", "IBN");
        props.setProperty("PBR", "PBR");
        props.setProperty("PAD", "PAD");
        props.setProperty("PAG", "PAG");
        props.setProperty("PBD", "PBD");
        props.setProperty("PBN", "PBN");
        props.setProperty("SRC", "SRC");
        props.setProperty("SCT", "SCT");
        props.setProperty("SCC", "SCC");
        props.setProperty("EBT", "EBT");
        props.setProperty("CIN", "CIN");
        props.setProperty("REG", "REG");
        props.setProperty("CYM", "CYM");
        props.setProperty("SIC", "SIC");
        props.setProperty("GIC", "GIC");
        props.setProperty("GID", "GID");
        props.setProperty("ATL", "ATL");
        props.setProperty("OTL", "OTL");
        props.setProperty("EDN", "EDN");
        props.setProperty("AVL", "AVL");
        props.setProperty("CIT", "CIT");
        props.setProperty("ABS", "S");
        props.setProperty("PYR", "PYR");
        props.setProperty("LOAD_NUMBER", "LN");

    }
    
    {
        xmlprops = new Properties();
        xmlprops.setProperty("M_ID", "M_ID");
        xmlprops.setProperty("ABN", "ACCESSNUMBER");
        xmlprops.setProperty("CDT", "DELIVERYDATE");
        xmlprops.setProperty("DOC", "CITTYPE");
        xmlprops.setProperty("SCO", "CBNBSCOPECLASSIFICATIONDESC");
        xmlprops.setProperty("FJL", "SOURCETITLE");
        xmlprops.setProperty("ISN", "ISSN");
        xmlprops.setProperty("CDN", "CODEN");
        xmlprops.setProperty("LAN", "CITATIONLANGUAGE");
        xmlprops.setProperty("VOL", "VOLUME");
        xmlprops.setProperty("ISS", "ISSUE");
        xmlprops.setProperty("IBN", "ISBN");//added by hmo at 4/5/2021
        xmlprops.setProperty("PBR", "");
        xmlprops.setProperty("PAD", "");
        xmlprops.setProperty("PAG", "PAGE");
        xmlprops.setProperty("PBD", "PUBLICATIONDATEDATETEXT");
        xmlprops.setProperty("PBN", "PUBLICATIONDATE");
        xmlprops.setProperty("SRC", "CBETERM");
        xmlprops.setProperty("SCT", "CBNBGEOCLASSIFICATIONDESC");
        xmlprops.setProperty("SCC", "CBNBGEOCLASSIFICATIONCODE");
        xmlprops.setProperty("EBT", "CBBTERM");
        xmlprops.setProperty("CIN", "CBCTERM");
        xmlprops.setProperty("REG", "CNCTERM");
        xmlprops.setProperty("CYM", "CBATERM");
        xmlprops.setProperty("SIC", "CBNBSICCLASSIFICATIONDESC");
        xmlprops.setProperty("GIC", "CBNBSECTORCLASSIFICATIONCODE");
        xmlprops.setProperty("GID", "CBNBSECTORCLASSIFICATIONDESC");
        xmlprops.setProperty("ATL", "CITATIONTITLE");
        xmlprops.setProperty("OTL", "CBNBFOREIGNTITLE");
        xmlprops.setProperty("EDN", "");
        xmlprops.setProperty("AVL", "SOURCEWEBSITE");
        xmlprops.setProperty("CIT", "");
        xmlprops.setProperty("ABS", "ABSTRACTDATA");
        xmlprops.setProperty("PYR", "");
        xmlprops.setProperty("LOAD_NUMBER", "LOADNUMBER");
        xmlprops.setProperty("SOURCE_TYPE", "SOURCETYPE");
        xmlprops.setProperty("CIT_TYPE", "CITTYPE");

    }



    public CBNBBaseTableWriter(int recsPerFile, String filename)
    {
        this.recsPerFile = recsPerFile;
        this.filename = filename;
    }




    public CBNBBaseTableWriter(String filename)
    {
        this.filename = filename;

    }

    public void begin()
                    throws Exception
    {
                ++filenumber;

                out = new PrintWriter(new FileWriter(filename+"."+filenumber));
                open = true;
                curRecNum = 0;
    }


    public void writeRec(Hashtable record)
            throws Exception
    {


            StringBuffer recordBuf = new StringBuffer();
            for(int i=0; i<baseTableFields.length; ++i)
            {
                String bf = baseTableFields[i];

                if(record == null)
                {
                    System.out.println("Record was null");
                }

                StringBuffer value = (StringBuffer)record.get(props.getProperty(bf).trim());
                String valueS = null;
                if(value != null)
                {
                    valueS = value.toString();
                    valueS = perl.substitute("s/\\t/     /g", valueS);

                }


                if(i > 0)
                {
                    //recordBuf.append("  ");

                    //12/31/2014 add tab as in eijava not spaces
                    recordBuf.append("\t");

                }


                if(valueS != null)
                {
                    recordBuf.append(valueS);
                }

            }

                out.println(fixer.fixRecord(recordBuf.toString()));

            ++curRecNum;
    }
    
    public void writeXmlRec(Hashtable record)
            throws Exception
    {


            StringBuffer recordBuf = new StringBuffer();
            for(int i=0; i<baseTableFields.length; ++i)
            {
                String bf = baseTableFields[i];

                if(record == null)
                {
                    System.out.println("Record was null");
                }

                String value = (String)record.get(xmlprops.getProperty(bf).trim());
                String valueS = null;
                if(value != null)
                {
                	if((xmlprops.getProperty(bf).equals("CITATIONTITLE") || xmlprops.getProperty(bf).trim().equals("SOURCEWEBSITE")))
                	{
                		String[] vArray = value.split(Constants.IDDELIMITER,-1);
                		if(vArray.length>1)
                		{
                			value = vArray[1];
                		}
                		//valueS = perl.substitute("s/\\t/     /g", value);
                	}
                	//else if(xmlprops.getProperty(bf).trim().equals("SOURCETYPE"))
                	else if(bf.equals("DOC"))
                	{
                		if(value.equals("ar"))
                		{
                			value = "Journal";
                		}
                		else if(value.equals("pr"))
                		{                			
                			value = "Press Release";              			
                		}
                		else if(value.equals("bk") ||value.equals("ch"))
                		{
                			value = "Book";
                		}
                		//System.out.println("SourceType="+value);
                		//valueS = perl.substitute("s/\\t/     /g", value);
                	}
                	else if(xmlprops.getProperty(bf).trim().equals("CITATIONLANGUAGE"))
                	{
                		value = Language.getIso639Language(value);               		
                		//System.out.println("CITATIONLANGUAGE="+value);                	
                	}
                	else if(xmlprops.getProperty(bf).trim().equals("PUBLICATIONDATE"))
                	{
                		String[] dateArray = value.split(" ");
                		if(dateArray.length==3)
                		{
	                		String month=dateArray[0];
	                		String day=dateArray[1].replaceAll(",","");
	                		String year=dateArray[2];
	                		value = year+reverseMonth(month)+fixDay(day);   
                		}
                		//System.out.println("PUBLICATIONDATE="+value);                	
                	}
                	
                	
	                valueS = perl.substitute("s/\\t/     /g", value);
                	

                }


                if(i > 0)
                {
                    //recordBuf.append("  ");

                    //12/31/2014 add tab as in eijava not spaces
                    recordBuf.append("\t");

                }


                if(valueS != null)
                {
                    recordBuf.append(valueS);                  
                }
                //System.out.println(xmlprops.getProperty(bf)+"\t"+valueS);

            }

                out.println(fixer.fixRecord(recordBuf.toString()));

            ++curRecNum;
    }
    
    private String fixDay(String day)
    {
    	String dayString = day;
    	if(day!=null && day.length()==1 )
    	{
    		dayString="0"+day;
    	}
    	return dayString;
    }
    
    private String reverseMonth(String month)
	{
		String monthString = null;
		try
		{
		 	//int intMonth = Integer.parseInt(month);

		 	switch (month)
		 	{
		 		case "January":   monthString = "01" ; 	break;
		 		case "February":   monthString =  "02"; 	break;
		 		case "March":   monthString =  "03"; 		break;
		 		case "April":   monthString =  "04"; 		break;
		 		case "May":   monthString =  "05"; 		break;
		 		case "June":   monthString =  "06"; 		break;
		 		case "July":   monthString =  "07"; 		break;
				case "August":   monthString =  "08"; 		break;
				case "September":   monthString =  "09"; 	break;
				case "October":  monthString =  "10"; 	break;
				case "November":  monthString = "11"; 	break;
		 		case "December":  monthString = "12"; 	break;
		 		default:  monthString=month;			break;
			}
			//System.out.println("month= "+month+" intMonth= "+intMonth+" monthString= "+monthString);

		}
		catch(Exception e)
		{
			monthString=month;
			e.printStackTrace();
		}

		return monthString;

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
