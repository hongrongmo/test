package org.ei.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import org.jdom2.*; 
import org.jdom2.input.*;
import org.jdom2.output.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class ConvertFastToJson
{
	public static void main(String[] args) throws Exception
	{
		 String infile = args[0];
		 ConvertFastToJson c = new ConvertFastToJson();
		 c.readingZipFile(infile);
		
	}
	
	private void readingZipFile(String fileName) throws Exception
	{
		 BufferedReader in = null;
		 String outfileName = fileName.replaceAll("zip", "json");
		 FileWriter out = new FileWriter(outfileName);   
	     String line = null;
	     try
	     {
			 if(fileName.toLowerCase().endsWith(".zip"))
	         {
	             System.out.println("IS ZIP FILE");
	             ZipFile zipFile = new ZipFile(fileName);
	             Enumeration entries = zipFile.entries();
	             int i=0;
	             while (entries.hasMoreElements())
	             {
	                 ZipEntry entry = (ZipEntry)entries.nextElement();
	                 String gzipFileName = entry.getName();  
	                 GZIPInputStream gzipFile = new GZIPInputStream(zipFile.getInputStream(entry));
	                 in = new BufferedReader(new InputStreamReader(gzipFile));
	                 StringBuffer aFile = new StringBuffer();
	                 while((line=in.readLine())!=null)
	                 {
	                	 aFile.append(line);
	                 }
	                
	                 parseXml(new StringReader(aFile.toString()),out);
	                 if(i>100)
	                 {
	                	 System.out.println("only try 100 files");
	                	 break;
	                 }
	                 i++;
	             }
	         }
	     }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	     finally
	     {
	    	 if(out != null)
	    	 {
	    		 out.close();
	    	 }
	     }
	}
	
	private void parseXml(StringReader file,FileWriter out) throws Exception
	{
		SAXBuilder builder = new SAXBuilder();
		HashMap<String,String> fastMap = new HashMap();
		builder.setExpandEntities(false);
		Document doc = null; doc = builder.build(file);
		doc.setBaseURI(".");
		Element fastRoot = doc.getRootElement();
		Element row = fastRoot.getChild("ROW");
		String eidocid = row.getChildTextTrim("EIDOCID");
		String parentid = row.getChildTextTrim("PARENTID");
		String dedupkey = row.getChildTextTrim("DEDUPKEY");
		String database = row.getChildTextTrim("DATABASE");
		String loadnumber = row.getChildTextTrim("LOADNUMBER");		
		String datesort = row.getChildTextTrim("DATESORT");
		String pubyear = row.getChildTextTrim("PUBYEAR");
		String accessnumber = row.getChildTextTrim("ACCESSIONNUMBER");
		String author = row.getChildTextTrim("AUTHOR");
		String authoraffiliation = row.getChildTextTrim("AUTHORAFFILIATION");		
		String affiliationlocation = row.getChildTextTrim("AFFILIATIONLOCATION");
		String title = row.getChildTextTrim("TITLE");
		String translatedtitle = row.getChildTextTrim("TRANSLATEDTITLE");
		String volumetitle = row.getChildTextTrim("VOLUMETITLE");
		String abstractData = row.getChildTextTrim("ABSTRACT");		
		String otherabstract = row.getChildTextTrim("OTHERABSTRACT");
		String editor = row.getChildTextTrim("EDITOR");
		String editoraffiliation = row.getChildTextTrim("EDITORAFFILIATION");
		String translator = row.getChildTextTrim("TRANSLATOR");
		String controlledterms = row.getChildTextTrim("CONTROLLEDTERMS");		
		String uncontrolledterm = row.getChildTextTrim("UNCONTROLLEDTERMS");
		String issn = row.getChildTextTrim("ISSN");
		String issnoftranslation = row.getChildTextTrim("ISSNOFTRANSLATION");
		String coden = row.getChildTextTrim("CODEN");
		String codenoftranslation = row.getChildTextTrim("CODENOFTRANSLATION");		
		String ISBN = row.getChildTextTrim("ISBN");
		String serialtitle = row.getChildTextTrim("SERIALTITLE");
		String serialtitletranslation = row.getChildTextTrim("SERIALTITLETRANSLATION");
		String mainheading = row.getChildTextTrim("MAINHEADING");
		String subheading = row.getChildTextTrim("SUBHEADING");		
		String publishername = row.getChildTextTrim("PUBLISHERNAME");		
		String treatmentcode = row.getChildTextTrim("TREATMENTCODE");
		String language = row.getChildTextTrim("LANGUAGE");
		String rectype = row.getChildTextTrim("RECTYPE");
		String classificationcode = row.getChildTextTrim("CLASSIFICATIONCODE");
		String conferencecode = row.getChildTextTrim("CONFERENCECODE");		
		String conferencename = row.getChildTextTrim("CONFERENCENAME");
		String conferencelocation = row.getChildTextTrim("CONFERENCELOCATION");
		String meetingdate = row.getChildTextTrim("MEETINGDATE");
		String monographtitle = row.getChildTextTrim("MONOGRAPHTITLE");
		String discipline = row.getChildTextTrim("DISCIPLINE");		
		String materialnumber = row.getChildTextTrim("MATERIALNUMBER");		
		String numericalindexing = row.getChildTextTrim("NUMERICALINDEXING");
		String checmicalindexing = row.getChildTextTrim("CHEMICALINDEXING");
		String astronomicalindexing = row.getChildTextTrim("ASTRONOMICALINDEXING");
		String reportnumber = row.getChildTextTrim("REPORTNUMBER");
		String ordernumber = row.getChildTextTrim("ORDERNUMBER");		
		String country = row.getChildTextTrim("COUNTRY");
		String volume = row.getChildTextTrim("VOLUME");
		String issue = row.getChildTextTrim("ISSUE");
		String startpage = row.getChildTextTrim("STARTPAGE");
		String page = row.getChildTextTrim("PAGE");	
		String availability = row.getChildTextTrim("AVAILABILITY");		
		String notes = row.getChildTextTrim("NOTES");
		String patentappdate = row.getChildTextTrim("PATENTAPPDATE");
		String paentissuedate = row.getChildTextTrim("PATENTISSUEDATE");
		String companies = row.getChildTextTrim("COMPANIES");
		String casregistrynumber = row.getChildTextTrim("CASREGISTRYNUMBER");		
		String businessterms = row.getChildTextTrim("BUSINESSTERMS");
		String chemicalterms = row.getChildTextTrim("CHEMICALTERMS");
		String chemac = row.getChildTextTrim("CHEMAC");
		String sic = row.getChildTextTrim("SIC");
		String industrialcodes = row.getChildTextTrim("INDUSTRIALCODES");		
		String industrialsectors = row.getChildTextTrim("INDUSTRIALSECTORS");		
		String scope = row.getChildTextTrim("SCOPE");
		String agency = row.getChildTextTrim("AGENCY");
		String derwentaccessionnumber = row.getChildTextTrim("DERWENTACCESSIONNUMBER");
		String applicationnumber = row.getChildTextTrim("APPLICATIONNUMBER");
		String applicationcountry = row.getChildTextTrim("APPLICATIONCOUNTRY");		
		String intpatentclassification = row.getChildTextTrim("INTPATENTCLASSIFICATION");
		String linkedterms = row.getChildTextTrim("LINKEDTERMS");
		String entryyear = row.getChildTextTrim("ENTRYYEAR");
		String prioritynumber = row.getChildTextTrim("PRIORITYNUMBER");
		String prioritydate = row.getChildTextTrim("PRIORITYDATE");	
		String prioritycountry = row.getChildTextTrim("PRIORITYCOUNTRY");		
		String source = row.getChildTextTrim("SOURCE");
		String secondarysrctitle = row.getChildTextTrim("SECONDARYSRCTITLE");
		String mainterm = row.getChildTextTrim("MAINTERM");
		String abbrvsrctitle = row.getChildTextTrim("ABBRVSRCTITLE");
		String noroleterms = row.getChildTextTrim("NOROLETERMS");		
		String reagentterms = row.getChildTextTrim("REAGENTTERMS");
		String productterms = row.getChildTextTrim("PRODUCTTERMS");
		String majornoroleterms = row.getChildTextTrim("MAJORNOROLETERMS");
		String majorreagentterms = row.getChildTextTrim("MAJORREAGENTTERMS");
		String majorproductterms = row.getChildTextTrim("MAJORPRODUCTTERMS");
		
		String conferenceaffiliations = row.getChildTextTrim("CONFERENCEAFFILIATIONS");		
		String conferenceeditors = row.getChildTextTrim("CONFERENCEEDITORS");
		String conferencestartdate = row.getChildTextTrim("CONFERENCESTARTDATE");
		String conferenceenddate = row.getChildTextTrim("CONFERENCEENDDATE");
		String conferencevenuesite = row.getChildTextTrim("CONFERENCEVENUESITE");
		String conferencecity = row.getChildTextTrim("CONFERENCECITY");		
//		String reagentterms = row.getChildTextTrim("CONFERENCECOUNTRYCODE");
//		String productterms = row.getChildTextTrim("CONFERENCEPAGERANGE");
//		String majornoroleterms = row.getChildTextTrim("CONFERENCENUMBERPAGES");
//		String majorreagentterms = row.getChildTextTrim("CONFERENCEPARTNUMBER");
//		String majorproductterms = row.getChildTextTrim("DESIGNATEDSTATES");
		
		
		
		if(row.getChildTextTrim("EIDOCID")!=null)
		{
			fastMap.put("EIDOCID",row.getChildTextTrim("EIDOCID"));
		}
		if(row.getChildTextTrim("PARENTID")!=null)
		{
			fastMap.put("PARENTID",row.getChildTextTrim("PARENTID"));
		}
		if(row.getChildTextTrim("DEDUPKEY")!=null)
		{
			fastMap.put("DEDUPKEY",row.getChildTextTrim("DEDUPKEY"));
		}
		if(row.getChildTextTrim("DATABASE")!=null)
		{
			fastMap.put("DATABASE",row.getChildTextTrim("DATABASE"));
		}
		if(row.getChildTextTrim("LOADNUMBER")!=null)
		{
			fastMap.put("LOADNUMBER",row.getChildTextTrim("LOADNUMBER"));
		}
		if(row.getChildTextTrim("DATESORT")!=null)
		{
			fastMap.put("DATESORT",row.getChildTextTrim("DATESORT"));
		}
		if(row.getChildTextTrim("PUBYEAR")!=null)
		{
			fastMap.put("PUBYEAR",row.getChildTextTrim("PUBYEAR"));
		}
		if(row.getChildTextTrim("ACCESSNUMBER")!=null)
		{
			fastMap.put("ACCESSNUMBER",row.getChildTextTrim("ACCESSNUMBER"));
		}
		if(row.getChildTextTrim("AUTHOR")!=null)
		{
			fastMap.put("AUTHOR",row.getChildTextTrim("AUTHOR"));
		}
		if(row.getChildTextTrim("AUTHORAFFILIATION")!=null)
		{
			fastMap.put("AUTHORAFFILIATION",row.getChildTextTrim("AUTHORAFFILIATION"));
		}		
		if(row.getChildTextTrim("AFFILIATIONLOCATION")!=null)
		{
			fastMap.put("AFFILIATIONLOCATION",row.getChildTextTrim("AFFILIATIONLOCATION"));
		}
		if(row.getChildTextTrim("TITLE")!=null)
		{
			fastMap.put("TITLE",row.getChildTextTrim("TITLE"));
		}
		if(row.getChildTextTrim("TRANSLATEDTITLE")!=null)
		{
			fastMap.put("TRANSLATEDTITLE",row.getChildTextTrim("TRANSLATEDTITLE"));
		}
		if(row.getChildTextTrim("VOLUMETITLE")!=null)
		{
			fastMap.put("VOLUMETITLE",row.getChildTextTrim("VOLUMETITLE"));
		}
		if(row.getChildTextTrim("ABSTRACT")!=null)
		{
			fastMap.put("ABSTRACT",row.getChildTextTrim("ABSTRACT"));
		}
		if(row.getChildTextTrim("OTHERABSTRACT")!=null)
		{
			fastMap.put("OTHERABSTRACT",row.getChildTextTrim("OTHERABSTRACT"));
		}
		if(row.getChildTextTrim("EDITOR")!=null)
		{
			fastMap.put("EDITOR",row.getChildTextTrim("EDITOR"));
		}
		if(row.getChildTextTrim("EDITORAFFILIATION")!=null)
		{
			fastMap.put("EDITORAFFILIATION",row.getChildTextTrim("EDITORAFFILIATION"));
		}
		if(row.getChildTextTrim("TRANSLATOR")!=null)
		{
			fastMap.put("TRANSLATOR",row.getChildTextTrim("TRANSLATOR"));
		}
		if(row.getChildTextTrim("CONTROLLEDTERMS")!=null)
		{
			fastMap.put("CONTROLLEDTERMS",row.getChildTextTrim("CONTROLLEDTERMS"));
		}		
		if(row.getChildTextTrim("UNCONTROLLEDTERMS")!=null)
		{
			fastMap.put("UNCONTROLLEDTERMS",row.getChildTextTrim("UNCONTROLLEDTERMS"));
		}
		if(row.getChildTextTrim("ISSN")!=null)
		{
			fastMap.put("ISSN",row.getChildTextTrim("ISSN"));
		}
		if(row.getChildTextTrim("ISSNOFTRANSLATION")!=null)
		{
			fastMap.put("ISSNOFTRANSLATION",row.getChildTextTrim("ISSNOFTRANSLATION"));
		}
		if(row.getChildTextTrim("CODEN")!=null)
		{
			fastMap.put("CODEN",row.getChildTextTrim("CODEN"));
		}
		if(row.getChildTextTrim("CODENOFTRANSLATION")!=null)
		{
			fastMap.put("CODENOFTRANSLATION",row.getChildTextTrim("CODENOFTRANSLATION"));
		}
		if(row.getChildTextTrim("ISBN")!=null)
		{
			fastMap.put("ISBN",row.getChildTextTrim("ISBN"));
		}
		if(row.getChildTextTrim("SERIALTITLE")!=null)
		{
			fastMap.put("SERIALTITLE",row.getChildTextTrim("SERIALTITLE"));
		}
		if(row.getChildTextTrim("SERIALTITLETRANSLATION")!=null)
		{
			fastMap.put("SERIALTITLETRANSLATION",row.getChildTextTrim("SERIALTITLETRANSLATION"));
		}
		if(row.getChildTextTrim("MAINHEADING")!=null)
		{
			fastMap.put("MAINHEADING",row.getChildTextTrim("MAINHEADING"));
		}
		if(row.getChildTextTrim("SUBHEADING")!=null)
		{
			fastMap.put("SUBHEADING",row.getChildTextTrim("SUBHEADING"));
		}		
		if(row.getChildTextTrim("PUBLISHERNAME")!=null)
		{
			fastMap.put("PUBLISHERNAME",row.getChildTextTrim("PUBLISHERNAME"));
		}
		if(row.getChildTextTrim("TREATMENTCODE")!=null)
		{
			fastMap.put("TREATMENTCODE",row.getChildTextTrim("TREATMENTCODE"));
		}
		if(row.getChildTextTrim("LANGUAGE")!=null)
		{
			fastMap.put("LANGUAGE",row.getChildTextTrim("LANGUAGE"));
		}
		if(row.getChildTextTrim("RECTYPE")!=null)
		{
			fastMap.put("RECTYPE",row.getChildTextTrim("RECTYPE"));
		}
		if(row.getChildTextTrim("CLASSIFICATIONCODE")!=null)
		{
			fastMap.put("CLASSIFICATIONCODE",row.getChildTextTrim("CLASSIFICATIONCODE"));
		}
		if(row.getChildTextTrim("CONFERENCECODE")!=null)
		{
			fastMap.put("CONFERENCECODE",row.getChildTextTrim("CONFERENCECODE"));
		}
		if(row.getChildTextTrim("CONFERENCENAME")!=null)
		{
			fastMap.put("CONFERENCENAME",row.getChildTextTrim("CONFERENCENAME"));
		}
		if(row.getChildTextTrim("CONFERENCELOCATION")!=null)
		{
			fastMap.put("CONFERENCELOCATION",row.getChildTextTrim("CONFERENCELOCATION"));
		}
		if(row.getChildTextTrim("MEETINGDATE")!=null)
		{
			fastMap.put("MEETINGDATE",row.getChildTextTrim("MEETINGDATE"));
		}
		if(row.getChildTextTrim("MONOGRAPHTITLE")!=null)
		{
			fastMap.put("MONOGRAPHTITLE",row.getChildTextTrim("MONOGRAPHTITLE"));
		}	
		if(row.getChildTextTrim("DISCIPLINE")!=null)
		{
			fastMap.put("DISCIPLINE",row.getChildTextTrim("DISCIPLINE"));
		}		
		if(row.getChildTextTrim("MATERIALNUMBER")!=null)
		{
			fastMap.put("MATERIALNUMBER",row.getChildTextTrim("MATERIALNUMBER"));
		}
		if(row.getChildTextTrim("NUMERICALINDEXING")!=null)
		{
			fastMap.put("NUMERICALINDEXING",row.getChildTextTrim("NUMERICALINDEXING"));
		}
		if(row.getChildTextTrim("CHEMICALINDEXING")!=null)
		{
			fastMap.put("CHEMICALINDEXING",row.getChildTextTrim("CHEMICALINDEXING"));
		}
		if(row.getChildTextTrim("ASTRONOMICALINDEXING")!=null)
		{
			fastMap.put("ASTRONOMICALINDEXING",row.getChildTextTrim("ASTRONOMICALINDEXING"));
		}
		if(row.getChildTextTrim("REPORTNUMBER")!=null)
		{
			fastMap.put("REPORTNUMBER",row.getChildTextTrim("REPORTNUMBER"));
		}
		if(row.getChildTextTrim("ORDERNUMBER")!=null)
		{
			fastMap.put("ORDERNUMBER",row.getChildTextTrim("ORDERNUMBER"));
		}
		if(row.getChildTextTrim("COUNTRY")!=null)
		{
			fastMap.put("COUNTRY",row.getChildTextTrim("COUNTRY"));
		}
		if(row.getChildTextTrim("VOLUME")!=null)
		{
			fastMap.put("VOLUME",row.getChildTextTrim("VOLUME"));
		}
		if(row.getChildTextTrim("ISSUE")!=null)
		{
			fastMap.put("ISSUE",row.getChildTextTrim("ISSUE"));
		}
		if(row.getChildTextTrim("STARTPAGE")!=null)
		{
			fastMap.put("STARTPAGE",row.getChildTextTrim("STARTPAGE"));
		}	
		if(row.getChildTextTrim("PAGE")!=null)
		{
			fastMap.put("PAGE",row.getChildTextTrim("PAGE"));
		}			
		if(row.getChildTextTrim("AVAILABILITY")!=null)
		{
			fastMap.put("AVAILABILITY",row.getChildTextTrim("AVAILABILITY"));
		}		
		if(row.getChildTextTrim("NOTES")!=null)
		{
			fastMap.put("NOTES",row.getChildTextTrim("NOTES"));
		}
		if(row.getChildTextTrim("PATENTAPPDATE")!=null)
		{
			fastMap.put("PATENTAPPDATE",row.getChildTextTrim("PATENTAPPDATE"));
		}
		if(row.getChildTextTrim("PATENTISSUEDATE")!=null)
		{
			fastMap.put("PATENTISSUEDATE",row.getChildTextTrim("PATENTISSUEDATE"));
		}
		if(row.getChildTextTrim("ASTRONOMICALINDEXING")!=null)
		{
			fastMap.put("ASTRONOMICALINDEXING",row.getChildTextTrim("ASTRONOMICALINDEXING"));
		}
		if(row.getChildTextTrim("COMPANIES")!=null)
		{
			fastMap.put("COMPANIES",row.getChildTextTrim("COMPANIES"));
		}
		if(row.getChildTextTrim("CASREGISTRYNUMBER")!=null)
		{
			fastMap.put("CASREGISTRYNUMBER",row.getChildTextTrim("CASREGISTRYNUMBER"));
		}
		if(row.getChildTextTrim("BUSINESSTERMS")!=null)
		{
			fastMap.put("BUSINESSTERMS",row.getChildTextTrim("BUSINESSTERMS"));
		}
		if(row.getChildTextTrim("CHEMICALTERMS")!=null)
		{
			fastMap.put("CHEMICALTERMS",row.getChildTextTrim("CHEMICALTERMS"));
		}
		if(row.getChildTextTrim("CHEMAC")!=null)
		{
			fastMap.put("CHEMAC",row.getChildTextTrim("CHEMAC"));
		}
		if(row.getChildTextTrim("SIC")!=null)
		{
			fastMap.put("SIC",row.getChildTextTrim("SIC"));
		}	
		if(row.getChildTextTrim("INDUSTRIALCODES")!=null)
		{
			fastMap.put("INDUSTRIALCODES",row.getChildTextTrim("INDUSTRIALCODES"));
		}					
		if(row.getChildTextTrim("INDUSTRIALSECTORS")!=null)
		{
			fastMap.put("INDUSTRIALSECTORS",row.getChildTextTrim("INDUSTRIALSECTORS"));
		}
		if(row.getChildTextTrim("SCOPE")!=null)
		{
			fastMap.put("SCOPE",row.getChildTextTrim("SCOPE"));
		}
		if(row.getChildTextTrim("AGENCY")!=null)
		{
			fastMap.put("AGENCY",row.getChildTextTrim("AGENCY"));
		}	
		if(row.getChildTextTrim("DERWENTACCESSIONNUMBER")!=null)
		{
			fastMap.put("DERWENTACCESSIONNUMBER",row.getChildTextTrim("DERWENTACCESSIONNUMBER"));
		}		
		if(row.getChildTextTrim("APPLICATIONNUMBER")!=null)
		{
			fastMap.put("APPLICATIONNUMBER",row.getChildTextTrim("APPLICATIONNUMBER"));
		}
		if(row.getChildTextTrim("APPLICATIONCOUNTRY")!=null)
		{
			fastMap.put("APPLICATIONCOUNTRY",row.getChildTextTrim("APPLICATIONCOUNTRY"));
		}
		if(row.getChildTextTrim("INTPATENTCLASSIFICATION")!=null)
		{
			fastMap.put("INTPATENTCLASSIFICATION",row.getChildTextTrim("INTPATENTCLASSIFICATION"));
		}
		if(row.getChildTextTrim("LINKEDTERMS")!=null)
		{
			fastMap.put("LINKEDTERMS",row.getChildTextTrim("LINKEDTERMS"));
		}
		if(row.getChildTextTrim("ENTRYYEAR")!=null)
		{
			fastMap.put("ENTRYYEAR",row.getChildTextTrim("ENTRYYEAR"));
		}
		if(row.getChildTextTrim("PRIORITYNUMBER")!=null)
		{
			fastMap.put("PRIORITYNUMBER",row.getChildTextTrim("PRIORITYNUMBER"));
		}
		if(row.getChildTextTrim("PRIORITYDATE")!=null)
		{
			fastMap.put("PRIORITYDATE",row.getChildTextTrim("PRIORITYDATE"));
		}	
		if(row.getChildTextTrim("PRIORITYCOUNTRY")!=null)
		{
			fastMap.put("PRIORITYCOUNTRY",row.getChildTextTrim("PRIORITYCOUNTRY"));
		}					
		if(row.getChildTextTrim("SOURCE")!=null)
		{
			fastMap.put("SOURCE",row.getChildTextTrim("SOURCE"));
		}
		if(row.getChildTextTrim("SECONDARYSRCTITLE")!=null)
		{
			fastMap.put("SECONDARYSRCTITLE",row.getChildTextTrim("SECONDARYSRCTITLE"));
		}
		if(row.getChildTextTrim("MAINTERM")!=null)
		{
			fastMap.put("MAINTERM",row.getChildTextTrim("MAINTERM"));
		}	
		if(row.getChildTextTrim("ABBRVSRCTITLE")!=null)
		{
			fastMap.put("ABBRVSRCTITLE",row.getChildTextTrim("ABBRVSRCTITLE"));
		}		
		if(row.getChildTextTrim("NOROLETERMS")!=null)
		{
			fastMap.put("NOROLETERMS",row.getChildTextTrim("NOROLETERMS"));
		}	
		if(row.getChildTextTrim("REAGENTTERMS")!=null)
		{
			fastMap.put("REAGENTTERMS",row.getChildTextTrim("REAGENTTERMS"));
		}					
		if(row.getChildTextTrim("PRODUCTTERMS")!=null)
		{
			fastMap.put("PRODUCTTERMS",row.getChildTextTrim("PRODUCTTERMS"));
		}
		if(row.getChildTextTrim("MAJORNOROLETERMS")!=null)
		{
			fastMap.put("MAJORNOROLETERMS",row.getChildTextTrim("MAJORNOROLETERMS"));
		}
		if(row.getChildTextTrim("MAJORREAGENTTERMS")!=null)
		{
			fastMap.put("MAJORREAGENTTERMS",row.getChildTextTrim("MAJORREAGENTTERMS"));
		}	
		if(row.getChildTextTrim("MAJORPRODUCTTERMS")!=null)
		{
			fastMap.put("MAJORPRODUCTTERMS",row.getChildTextTrim("MAJORPRODUCTTERMS"));
		}	
		if(row.getChildTextTrim("CONFERENCEAFFILIATIONS")!=null)
		{
			fastMap.put("CONFERENCEAFFILIATIONS",row.getChildTextTrim("CONFERENCEAFFILIATIONS"));
		}
		if(row.getChildTextTrim("CONFERENCEEDITORS")!=null)
		{
			fastMap.put("CONFERENCEEDITORS",row.getChildTextTrim("CONFERENCEEDITORS"));
		}
		if(row.getChildTextTrim("CONFERENCESTARTDATE")!=null)
		{
			fastMap.put("CONFERENCESTARTDATE",row.getChildTextTrim("CONFERENCESTARTDATE"));
		}
		if(row.getChildTextTrim("CONFERENCEENDDATE")!=null)
		{
			fastMap.put("CONFERENCEENDDATE",row.getChildTextTrim("CONFERENCEENDDATE"));
		}
		if(row.getChildTextTrim("CONFERENCEVENUESITE")!=null)
		{
			fastMap.put("CONFERENCEVENUESITE",row.getChildTextTrim("CONFERENCEVENUESITE"));
		}
		if(row.getChildTextTrim("CONFERENCECITY")!=null)
		{
			fastMap.put("CONFERENCECITY",row.getChildTextTrim("CONFERENCECITY"));
		}
		if(row.getChildTextTrim("CONFERENCECOUNTRYCODE")!=null)
		{
			fastMap.put("CONFERENCECOUNTRYCODE",row.getChildTextTrim("CONFERENCECOUNTRYCODE"));
		}	
		if(row.getChildTextTrim("CONFERENCEPAGERANGE")!=null)
		{
			fastMap.put("CONFERENCEPAGERANGE",row.getChildTextTrim("CONFERENCEPAGERANGE"));
		}					
		if(row.getChildTextTrim("CONFERENCENUMBERPAGES")!=null)
		{
			fastMap.put("CONFERENCENUMBERPAGES",row.getChildTextTrim("CONFERENCENUMBERPAGES"));
		}
		if(row.getChildTextTrim("CONFERENCEPARTNUMBER")!=null)
		{
			fastMap.put("CONFERENCEPARTNUMBER",row.getChildTextTrim("CONFERENCEPARTNUMBER"));
		}
		if(row.getChildTextTrim("DESIGNATEDSTATES")!=null)
		{
			fastMap.put("DESIGNATEDSTATES",row.getChildTextTrim("DESIGNATEDSTATES"));
		}		
		if(row.getChildTextTrim("STNCONFERENCE")!=null)
		{
			fastMap.put("STNCONFERENCE",row.getChildTextTrim("STNCONFERENCE"));
		}		
		if(row.getChildTextTrim("STNSECONDARYCONFERENCE")!=null)
		{
			fastMap.put("STNSECONDARYCONFERENCE",row.getChildTextTrim("STNSECONDARYCONFERENCE"));
		}	
		if(row.getChildTextTrim("FILINGDATE")!=null)
		{
			fastMap.put("FILINGDATE",row.getChildTextTrim("FILINGDATE"));
		}					
		if(row.getChildTextTrim("PRIORITYKIND")!=null)
		{
			fastMap.put("PRIORITYKIND",row.getChildTextTrim("PRIORITYKIND"));
		}
		if(row.getChildTextTrim("ECLACODE")!=null)
		{
			fastMap.put("ECLACODE",row.getChildTextTrim("ECLACODE"));
		}
		if(row.getChildTextTrim("ATTORNEYNAME")!=null)
		{
			fastMap.put("ATTORNEYNAME",row.getChildTextTrim("ATTORNEYNAME"));
		}	
		if(row.getChildTextTrim("PRIMARYEXAMINER")!=null)
		{
			fastMap.put("PRIMARYEXAMINER",row.getChildTextTrim("PRIMARYEXAMINER"));
		}		
		if(row.getChildTextTrim("ASSISTANTEXAMINER")!=null)
		{
			fastMap.put("ASSISTANTEXAMINER",row.getChildTextTrim("ASSISTANTEXAMINER"));
		}					
		if(row.getChildTextTrim("IPCCLASS")!=null)
		{
			fastMap.put("IPCCLASS",row.getChildTextTrim("IPCCLASS"));
		}
		if(row.getChildTextTrim("IPCSUBCLASS")!=null)
		{
			fastMap.put("IPCSUBCLASS",row.getChildTextTrim("IPCSUBCLASS"));
		}
		if(row.getChildTextTrim("ECLACLASS")!=null)
		{
			fastMap.put("ECLACLASS",row.getChildTextTrim("ECLACLASS"));
		}	
		if(row.getChildTextTrim("ECLASUBCLASS")!=null)
		{
			fastMap.put("ECLASUBCLASS",row.getChildTextTrim("ECLASUBCLASS"));
		}		
		if(row.getChildTextTrim("USPTOCLASS")!=null)
		{
			fastMap.put("USPTOCLASS",row.getChildTextTrim("USPTOCLASS"));
		}					
		if(row.getChildTextTrim("USPTOSUBCLASS")!=null)
		{
			fastMap.put("USPTOSUBCLASS",row.getChildTextTrim("USPTOSUBCLASS"));
		}
		if(row.getChildTextTrim("USPTOCODE")!=null)
		{
			fastMap.put("USPTOCODE",row.getChildTextTrim("USPTOCODE"));
		}
		if(row.getChildTextTrim("PATENTKIND")!=null)
		{
			fastMap.put("PATENTKIND",row.getChildTextTrim("PATENTKIND"));
		}	
		if(row.getChildTextTrim("KINDDESCRIPTION")!=null)
		{
			fastMap.put("KINDDESCRIPTION",row.getChildTextTrim("KINDDESCRIPTION"));
		}	
		if(row.getChildTextTrim("AUTHORITYCODE")!=null)
		{
			fastMap.put("AUTHORITYCODE",row.getChildTextTrim("AUTHORITYCODE"));
		}					
		if(row.getChildTextTrim("PCITED")!=null)
		{
			fastMap.put("PCITED",row.getChildTextTrim("PCITED"));
		}
		if(row.getChildTextTrim("PCITEDINDEX")!=null)
		{
			fastMap.put("PCITEDINDEX",row.getChildTextTrim("PCITEDINDEX"));
		}
		if(row.getChildTextTrim("PREFINDEX")!=null)
		{
			fastMap.put("PREFINDEX",row.getChildTextTrim("PREFINDEX"));
		}	
		if(row.getChildTextTrim("DMASK")!=null)
		{
			fastMap.put("DMASK",row.getChildTextTrim("DMASK"));
		}		
		if(row.getChildTextTrim("DOI")!=null)
		{
			fastMap.put("DOI",row.getChildTextTrim("DOI"));
		}					
		if(row.getChildTextTrim("SCOPUSID")!=null)
		{
			fastMap.put("SCOPUSID",row.getChildTextTrim("SCOPUSID"));
		}
		if(row.getChildTextTrim("AFFILIATIONID")!=null)
		{
			fastMap.put("AFFILIATIONID",row.getChildTextTrim("AFFILIATIONID"));
		}
		if(row.getChildTextTrim("LAT_NW")!=null)
		{
			fastMap.put("LAT_NW",row.getChildTextTrim("LAT_NW"));
		}	
		if(row.getChildTextTrim("LNG_NW")!=null)
		{
			fastMap.put("LNG_NW",row.getChildTextTrim("LNG_NW"));
		}		
		if(row.getChildTextTrim("LAT_NE")!=null)
		{
			fastMap.put("LAT_NE",row.getChildTextTrim("LAT_NE"));
		}	
		if(row.getChildTextTrim("LNG_NE")!=null)
		{
			fastMap.put("LNG_NE",row.getChildTextTrim("LNG_NE"));
		}
		if(row.getChildTextTrim("LAT_SW")!=null)
		{
			fastMap.put("LAT_SW",row.getChildTextTrim("LAT_SW"));
		}	
		if(row.getChildTextTrim("LNG_SW")!=null)
		{
			fastMap.put("LNG_SW",row.getChildTextTrim("LNG_SW"));
		}
		if(row.getChildTextTrim("LAT_SE")!=null)
		{
			fastMap.put("LAT_SE",row.getChildTextTrim("LAT_SE"));
		}	
		if(row.getChildTextTrim("LNG_SE")!=null)
		{
			fastMap.put("LNG_SE",row.getChildTextTrim("LNG_SE"));
		}		
		if(row.getChildTextTrim("CPCCLASS")!=null)
		{
			fastMap.put("CPCCLASS",row.getChildTextTrim("CPCCLASS"));
		}
		
		if(row.getChildTextTrim("TABLEOFCONTENT")!=null)
		{
			fastMap.put("TABLEOFCONTENT",row.getChildTextTrim("TABLEOFCONTENT"));
		}		
		if(row.getChildTextTrim("AMOUNTOFSUBSTANCE_RANGES")!=null)
		{
			fastMap.put("AMOUNTOFSUBSTANCE_RANGES",row.getChildTextTrim("AMOUNTOFSUBSTANCE_RANGES"));
		}	
		if(row.getChildTextTrim("AMOUNTOFSUBSTANCE_TEXT")!=null)
		{
			fastMap.put("AMOUNTOFSUBSTANCE_TEXT",row.getChildTextTrim("AMOUNTOFSUBSTANCE_TEXT"));
		}
		if(row.getChildTextTrim("ELECTRICCURRENT_RANGES")!=null)
		{
			fastMap.put("ELECTRICCURRENT_RANGES",row.getChildTextTrim("ELECTRICCURRENT_RANGES"));
		}	
		if(row.getChildTextTrim("ELECTRICCURRENT_TEXT")!=null)
		{
			fastMap.put("ELECTRICCURRENT_TEXT",row.getChildTextTrim("ELECTRICCURRENT_TEXT"));
		}
		if(row.getChildTextTrim("MASS_RANGES")!=null)
		{
			fastMap.put("MASS_RANGES",row.getChildTextTrim("MASS_RANGES"));
		}	
		if(row.getChildTextTrim("MASS_TEXT")!=null)
		{
			fastMap.put("MASS_TEXT",row.getChildTextTrim("MASS_TEXT"));
		}	
		if(row.getChildTextTrim("TEMPERATURE_RANGES")!=null)
		{
			fastMap.put("TEMPERATURE_RANGES",row.getChildTextTrim("TEMPERATURE_RANGES"));
		}
		if(row.getChildTextTrim("TEMPERATURE_TEXT")!=null)
		{
			fastMap.put("TEMPERATURE_TEXT",row.getChildTextTrim("TEMPERATURE_TEXT"));
		}	
		if(row.getChildTextTrim("TIME_RANGES")!=null)
		{
			fastMap.put("TIME_RANGES",row.getChildTextTrim("TIME_RANGES"));
		}
		if(row.getChildTextTrim("TIME_TEXT")!=null)
		{
			fastMap.put("TIME_TEXT",row.getChildTextTrim("TIME_TEXT"));
		}	
		if(row.getChildTextTrim("SIZE_RANGES")!=null)
		{
			fastMap.put("SIZE_RANGES",row.getChildTextTrim("SIZE_RANGES"));
		}
		if(row.getChildTextTrim("SIZE_TEXT")!=null)
		{
			fastMap.put("SIZE_TEXT",row.getChildTextTrim("SIZE_TEXT"));
		}		
		if(row.getChildTextTrim("ELECTRICALCONDUCTANCE_RANGES")!=null)
		{
			fastMap.put("ELECTRICALCONDUCTANCE_RANGES",row.getChildTextTrim("ELECTRICALCONDUCTANCE_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRICALCONDUCTANCE_TEXT")!=null)
		{
			fastMap.put("ELECTRICALCONDUCTANCE_TEXT",row.getChildTextTrim("ELECTRICALCONDUCTANCE_TEXT"));
		}	
		if(row.getChildTextTrim("ELECTRICALCONDUCTIVITY_RANGES")!=null)
		{
			fastMap.put("ELECTRICALCONDUCTIVITY_RANGES",row.getChildTextTrim("ELECTRICALCONDUCTIVITY_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRICALCONDUCTIVITY_TEXT")!=null)
		{
			fastMap.put("ELECTRICALCONDUCTIVITY_TEXT",row.getChildTextTrim("ELECTRICALCONDUCTIVITY_TEXT"));
		}	
		if(row.getChildTextTrim("VOLTAGE_RANGES")!=null)
		{
			fastMap.put("VOLTAGE_RANGES",row.getChildTextTrim("VOLTAGE_RANGES"));
		}
		if(row.getChildTextTrim("VOLTAGE_TEXT")!=null)
		{
			fastMap.put("VOLTAGE_TEXT",row.getChildTextTrim("VOLTAGE_TEXT"));
		}		 
		if(row.getChildTextTrim("ELECTRICFIELDSTRENGTH_RANGES")!=null)
		{
			fastMap.put("ELECTRICFIELDSTRENGTH_RANGES",row.getChildTextTrim("ELECTRICFIELDSTRENGTH_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRICFIELDSTRENGTH_TEXT")!=null)
		{
			fastMap.put("ELECTRICFIELDSTRENGTH_TEXT",row.getChildTextTrim("ELECTRICFIELDSTRENGTH_TEXT"));
		}	
		if(row.getChildTextTrim("CURRENTDENSITY_RANGES")!=null)
		{
			fastMap.put("CURRENTDENSITY_RANGES",row.getChildTextTrim("CURRENTDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("CURRENTDENSITY_TEXT")!=null)
		{
			fastMap.put("CURRENTDENSITY_TEXT",row.getChildTextTrim("CURRENTDENSITY_TEXT"));
		}	
		if(row.getChildTextTrim("ENERGY_RANGES")!=null)
		{
			fastMap.put("ENERGY_RANGES",row.getChildTextTrim("ENERGY_RANGES"));
		}
		if(row.getChildTextTrim("ENERGY_TEXT")!=null)
		{
			fastMap.put("ENERGY_TEXT",row.getChildTextTrim("ENERGY_TEXT"));
		}
		if(row.getChildTextTrim("ELECTRICALRESISTANCE_RANGES")!=null)
		{
			fastMap.put("ELECTRICALRESISTANCE_RANGES",row.getChildTextTrim("ELECTRICALRESISTANCE_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRICALRESISTANCE_TEXT")!=null)
		{
			fastMap.put("ELECTRICALRESISTANCE_TEXT",row.getChildTextTrim("ELECTRICALRESISTANCE_TEXT"));
		}	
		if(row.getChildTextTrim("ELECTRICALRESISTIVITY_RANGES")!=null)
		{
			fastMap.put("ELECTRICALRESISTIVITY_RANGES",row.getChildTextTrim("ELECTRICALRESISTIVITY_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRICALRESISTIVITY_TEXT")!=null)
		{
			fastMap.put("ELECTRICALRESISTIVITY_TEXT",row.getChildTextTrim("ELECTRICALRESISTIVITY_TEXT"));
		}	
		if(row.getChildTextTrim("ELECTRONVOLTENERGY_RANGES")!=null)
		{
			fastMap.put("ELECTRONVOLTENERGY_RANGES",row.getChildTextTrim("ELECTRONVOLTENERGY_RANGES"));
		}
		if(row.getChildTextTrim("ELECTRONVOLTENERGY_TEXT")!=null)
		{
			fastMap.put("ELECTRONVOLTENERGY_TEXT",row.getChildTextTrim("ELECTRONVOLTENERGY_TEXT"));
		}	    
		if(row.getChildTextTrim("CAPACITANCE_RANGES")!=null)
		{
			fastMap.put("CAPACITANCE_RANGES",row.getChildTextTrim("CAPACITANCE_RANGES"));
		}
		if(row.getChildTextTrim("CAPACITANCE_TEXT")!=null)
		{
			fastMap.put("CAPACITANCE_TEXT",row.getChildTextTrim("CAPACITANCE_TEXT"));
		}	
		if(row.getChildTextTrim("FREQUENCY_RANGES")!=null)
		{
			fastMap.put("FREQUENCY_RANGES",row.getChildTextTrim("FREQUENCY_RANGES"));
		}
		if(row.getChildTextTrim("FREQUENCY_TEXT")!=null)
		{
			fastMap.put("FREQUENCY_TEXT",row.getChildTextTrim("FREQUENCY_TEXT"));
		}	
		if(row.getChildTextTrim("POWER_RANGES")!=null)
		{
			fastMap.put("POWER_RANGES",row.getChildTextTrim("POWER_RANGES"));
		}
		if(row.getChildTextTrim("POWER_TEXT")!=null)
		{
			fastMap.put("POWER_TEXT",row.getChildTextTrim("POWER_TEXT"));
		}		
		if(row.getChildTextTrim("APPARENTPOWER_RANGES")!=null)
		{
			fastMap.put("APPARENTPOWER_RANGES",row.getChildTextTrim("APPARENTPOWER_RANGES"));
		}
		if(row.getChildTextTrim("APPARENTPOWER_TEXT")!=null)
		{
			fastMap.put("APPARENTPOWER_TEXT",row.getChildTextTrim("APPARENTPOWER_TEXT"));
		}	
		if(row.getChildTextTrim("PERCENTAGE_RANGES")!=null)
		{
			fastMap.put("PERCENTAGE_RANGES",row.getChildTextTrim("PERCENTAGE_RANGES"));
		}
		if(row.getChildTextTrim("PERCENTAGE_TEXT")!=null)
		{
			fastMap.put("PERCENTAGE_TEXT",row.getChildTextTrim("PERCENTAGE_TEXT"));
		}	
		if(row.getChildTextTrim("MAGNETICFLUXDENSITY_RANGES")!=null)
		{
			fastMap.put("MAGNETICFLUXDENSITY_RANGES",row.getChildTextTrim("MAGNETICFLUXDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("MAGNETICFLUXDENSITY_TEXT")!=null)
		{
			fastMap.put("MAGNETICFLUXDENSITY_TEXT",row.getChildTextTrim("MAGNETICFLUXDENSITY_TEXT"));
		}		
		if(row.getChildTextTrim("INDUCTANCE_RANGES")!=null)
		{
			fastMap.put("INDUCTANCE_RANGES",row.getChildTextTrim("INDUCTANCE_RANGES"));
		}
		if(row.getChildTextTrim("INDUCTANCE_TEXT")!=null)
		{
			fastMap.put("INDUCTANCE_TEXT",row.getChildTextTrim("INDUCTANCE_TEXT"));
		}	
		if(row.getChildTextTrim("VOLUMECHARGEDENSITY_RANGES")!=null)
		{
			fastMap.put("VOLUMECHARGEDENSITY_RANGES",row.getChildTextTrim("VOLUMECHARGEDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("VOLUMECHARGEDENSITY_TEXT")!=null)
		{
			fastMap.put("VOLUMECHARGEDENSITY_TEXT",row.getChildTextTrim("VOLUMECHARGEDENSITY_TEXT"));
		}	
		if(row.getChildTextTrim("SURFACECHARGEDENSITY_RANGES")!=null)
		{
			fastMap.put("SURFACECHARGEDENSITY_RANGES",row.getChildTextTrim("SURFACECHARGEDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("SURFACECHARGEDENSITY_TEXT")!=null)
		{
			fastMap.put("SURFACECHARGEDENSITY_TEXT",row.getChildTextTrim("SURFACECHARGEDENSITY_TEXT"));
		}			
		if(row.getChildTextTrim("DECIBEL_RANGES")!=null)
		{
			fastMap.put("DECIBEL_RANGES",row.getChildTextTrim("DECIBEL_RANGES"));
		}
		if(row.getChildTextTrim("DECIBEL_TEXT")!=null)
		{
			fastMap.put("DECIBEL_TEXT",row.getChildTextTrim("DECIBEL_TEXT"));
		}
		if(row.getChildTextTrim("LUMINOUSFLUX_RANGES")!=null)
		{
			fastMap.put("LUMINOUSFLUX_RANGES",row.getChildTextTrim("LUMINOUSFLUX_RANGES"));
		}
		if(row.getChildTextTrim("LUMINOUSFLUX_TEXT")!=null)
		{
			fastMap.put("LUMINOUSFLUX_TEXT",row.getChildTextTrim("LUMINOUSFLUX_TEXT"));
		}
		if(row.getChildTextTrim("ILLUMINANCE_RANGES")!=null)
		{
			fastMap.put("ILLUMINANCE_RANGES",row.getChildTextTrim("ILLUMINANCE_RANGES"));
		}	
		if(row.getChildTextTrim("ILLUMINANCE_TEXT")!=null)
		{
			fastMap.put("ILLUMINANCE_TEXT",row.getChildTextTrim("ILLUMINANCE_TEXT"));
		}		
		if(row.getChildTextTrim("BITRATE_RANGES")!=null)
		{
			fastMap.put("BITRATE_RANGES",row.getChildTextTrim("BITRATE_RANGES"));
		}
		if(row.getChildTextTrim("BITRATE_TEXT")!=null)
		{
			fastMap.put("BITRATE_TEXT",row.getChildTextTrim("BITRATE_TEXT"));
		}
		if(row.getChildTextTrim("MASSDENSITY_RANGES")!=null)
		{
			fastMap.put("MASSDENSITY_RANGES",row.getChildTextTrim("MASSDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("MASSDENSITY_TEXT")!=null)
		{
			fastMap.put("MASSDENSITY_TEXT",row.getChildTextTrim("MASSDENSITY_TEXT"));
		}
		if(row.getChildTextTrim("MASSFLOWRATE_RANGES")!=null)
		{
			fastMap.put("MASSFLOWRATE_RANGES",row.getChildTextTrim("MASSFLOWRATE_RANGES"));
		}	
		if(row.getChildTextTrim("MASSFLOWRATE_TEXT")!=null)
		{
			fastMap.put("MASSFLOWRATE_TEXT",row.getChildTextTrim("MASSFLOWRATE_TEXT"));
		}		
		if(row.getChildTextTrim("FORCE_RANGES")!=null)
		{
			fastMap.put("FORCE_RANGES",row.getChildTextTrim("FORCE_RANGES"));
		}
		if(row.getChildTextTrim("FORCE_TEXT")!=null)
		{
			fastMap.put("FORCE_TEXT",row.getChildTextTrim("FORCE_TEXT"));
		}
		if(row.getChildTextTrim("TORQUE_RANGES")!=null)
		{
			fastMap.put("TORQUE_RANGES",row.getChildTextTrim("TORQUE_RANGES"));
		}
		if(row.getChildTextTrim("TORQUE_TEXT")!=null)
		{
			fastMap.put("TORQUE_TEXT",row.getChildTextTrim("TORQUE_TEXT"));
		}
		if(row.getChildTextTrim("PRESSURE_RANGES")!=null)
		{
			fastMap.put("PRESSURE_RANGES",row.getChildTextTrim("PRESSURE_RANGES"));
		}	
		if(row.getChildTextTrim("PRESSURE_TEXT")!=null)
		{
			fastMap.put("PRESSURE_TEXT",row.getChildTextTrim("PRESSURE_TEXT"));
		}		
		if(row.getChildTextTrim("AREA_RANGES")!=null)
		{
			fastMap.put("AREA_RANGES",row.getChildTextTrim("AREA_RANGES"));
		}
		if(row.getChildTextTrim("AREA_TEXT")!=null)
		{
			fastMap.put("AREA_TEXT",row.getChildTextTrim("AREA_TEXT"));
		}
		if(row.getChildTextTrim("VOLUME_RANGES")!=null)
		{
			fastMap.put("VOLUME_RANGES",row.getChildTextTrim("VOLUME_RANGES"));
		}
		if(row.getChildTextTrim("VOLUME_TEXT")!=null)
		{
			fastMap.put("VOLUME_TEXT",row.getChildTextTrim("VOLUME_TEXT"));
		}
		if(row.getChildTextTrim("VELOCITY_RANGES")!=null)
		{
			fastMap.put("VELOCITY_RANGES",row.getChildTextTrim("VELOCITY_RANGES"));
		}	
		if(row.getChildTextTrim("VELOCITY_TEXT")!=null)
		{
			fastMap.put("VELOCITY_TEXT",row.getChildTextTrim("VELOCITY_TEXT"));
		}	      
		if(row.getChildTextTrim("ACCELERATION_RANGES")!=null)
		{
			fastMap.put("ACCELERATION_RANGES",row.getChildTextTrim("ACCELERATION_RANGES"));
		}
		if(row.getChildTextTrim("ACCELERATION_TEXT")!=null)
		{
			fastMap.put("ACCELERATION_TEXT",row.getChildTextTrim("ACCELERATION_TEXT"));
		}
		if(row.getChildTextTrim("ANGULARVELOCITY_RANGES")!=null)
		{
			fastMap.put("ANGULARVELOCITY_RANGES",row.getChildTextTrim("ANGULARVELOCITY_RANGES"));
		}
		if(row.getChildTextTrim("ANGULARVELOCITY_TEXT")!=null)
		{
			fastMap.put("ANGULARVELOCITY_TEXT",row.getChildTextTrim("ANGULARVELOCITY_TEXT"));
		}
		if(row.getChildTextTrim("ROTATIONALSPEED_RANGES")!=null)
		{
			fastMap.put("ROTATIONALSPEED_RANGES",row.getChildTextTrim("ROTATIONALSPEED_RANGES"));
		}	
		if(row.getChildTextTrim("ROTATIONALSPEED_TEXT")!=null)
		{
			fastMap.put("ROTATIONALSPEED_TEXT",row.getChildTextTrim("ROTATIONALSPEED_TEXT"));
		}  		
		if(row.getChildTextTrim("AGE_RANGES")!=null)
		{
			fastMap.put("AGE_RANGES",row.getChildTextTrim("AGE_RANGES"));
		}
		if(row.getChildTextTrim("AGE_TEXT")!=null)
		{
			fastMap.put("AGE_TEXT",row.getChildTextTrim("AGE_TEXT"));
		}
		if(row.getChildTextTrim("MOLARMASS_RANGES")!=null)
		{
			fastMap.put("MOLARMASS_RANGES",row.getChildTextTrim("MOLARMASS_RANGES"));
		}
		if(row.getChildTextTrim("MOLARMASS_TEXT")!=null)
		{
			fastMap.put("MOLARMASS_TEXT",row.getChildTextTrim("MOLARMASS_TEXT"));
		}
		if(row.getChildTextTrim("MOLALITYOFSUBSTANCE_RANGES")!=null)
		{
			fastMap.put("MOLALITYOFSUBSTANCE_RANGES",row.getChildTextTrim("MOLALITYOFSUBSTANCE_RANGES"));
		}	
		if(row.getChildTextTrim("MOLALITYOFSUBSTANCE_TEXT")!=null)
		{
			fastMap.put("MOLALITYOFSUBSTANCE_TEXT",row.getChildTextTrim("MOLALITYOFSUBSTANCE_TEXT"));
		} 		
		if(row.getChildTextTrim("RADIOACTIVITY_RANGES")!=null)
		{
			fastMap.put("RADIOACTIVITY_RANGES",row.getChildTextTrim("RADIOACTIVITY_RANGES"));
		}	
		if(row.getChildTextTrim("RADIOACTIVITY_TEXT")!=null)
		{
			fastMap.put("RADIOACTIVITY_TEXT",row.getChildTextTrim("RADIOACTIVITY_TEXT"));
		}  	
		if(row.getChildTextTrim("ABSORBEDDOSE_RANGES")!=null)
		{
			fastMap.put("ABSORBEDDOSE_RANGES",row.getChildTextTrim("ABSORBEDDOSE_RANGES"));
		}
		if(row.getChildTextTrim("ABSORBEDDOSE_TEXT")!=null)
		{
			fastMap.put("ABSORBEDDOSE_TEXT",row.getChildTextTrim("ABSORBEDDOSE_TEXT"));
		}
		if(row.getChildTextTrim("RADIATIONEXPOSURE_RANGES")!=null)
		{
			fastMap.put("RADIATIONEXPOSURE_RANGES",row.getChildTextTrim("RADIATIONEXPOSURE_RANGES"));
		}
		if(row.getChildTextTrim("RADIATIONEXPOSURE_TEXT")!=null)
		{
			fastMap.put("RADIATIONEXPOSURE_TEXT",row.getChildTextTrim("RADIATIONEXPOSURE_TEXT"));
		}
		if(row.getChildTextTrim("LUMINANCE_RANGES")!=null)
		{
			fastMap.put("LUMINANCE_RANGES",row.getChildTextTrim("LUMINANCE_RANGES"));
		}	
		if(row.getChildTextTrim("LUMINANCE_TEXT")!=null)
		{
			fastMap.put("LUMINANCE_TEXT",row.getChildTextTrim("LUMINANCE_TEXT"));
		}   	       
		if(row.getChildTextTrim("MAGNETICFIELDSTRENGTH_RANGES")!=null)
		{
			fastMap.put("MAGNETICFIELDSTRENGTH_RANGES",row.getChildTextTrim("MAGNETICFIELDSTRENGTH_RANGES"));
		}
		if(row.getChildTextTrim("MAGNETICFIELDSTRENGTH_TEXT")!=null)
		{
			fastMap.put("MAGNETICFIELDSTRENGTH_TEXT",row.getChildTextTrim("MAGNETICFIELDSTRENGTH_TEXT"));
		}
		if(row.getChildTextTrim("SPECTRALEFFICIENCY_RANGES")!=null)
		{
			fastMap.put("SPECTRALEFFICIENCY_RANGES",row.getChildTextTrim("SPECTRALEFFICIENCY_RANGES"));
		}
		if(row.getChildTextTrim("SPECTRALEFFICIENCY_TEXT")!=null)
		{
			fastMap.put("SPECTRALEFFICIENCY_TEXT",row.getChildTextTrim("SPECTRALEFFICIENCY_TEXT"));
		}
		if(row.getChildTextTrim("SURFACEPOWERDENSITY_RANGES")!=null)
		{
			fastMap.put("SURFACEPOWERDENSITY_RANGES",row.getChildTextTrim("SURFACEPOWERDENSITY_RANGES"));
		}	
		if(row.getChildTextTrim("SURFACEPOWERDENSITY_TEXT")!=null)
		{
			fastMap.put("SURFACEPOWERDENSITY_TEXT",row.getChildTextTrim("SURFACEPOWERDENSITY_TEXT"));
		}      		
		if(row.getChildTextTrim("THERMALCONDUCTIVITY_RANGES")!=null)
		{
			fastMap.put("THERMALCONDUCTIVITY_RANGES",row.getChildTextTrim("THERMALCONDUCTIVITY_RANGES"));
		}
		if(row.getChildTextTrim("THERMALCONDUCTIVITY_TEXT")!=null)
		{
			fastMap.put("THERMALCONDUCTIVITY_TEXT",row.getChildTextTrim("THERMALCONDUCTIVITY_TEXT"));
		}
		if(row.getChildTextTrim("DECIBELISOTROPIC_RANGES")!=null)
		{
			fastMap.put("DECIBELISOTROPIC_RANGES",row.getChildTextTrim("DECIBELISOTROPIC_RANGES"));
		}
		if(row.getChildTextTrim("DECIBELISOTROPIC_TEXT")!=null)
		{
			fastMap.put("DECIBELISOTROPIC_TEXT",row.getChildTextTrim("DECIBELISOTROPIC_TEXT"));
		}
		if(row.getChildTextTrim("DECIBELMILLIWATTS_RANGES")!=null)
		{
			fastMap.put("DECIBELMILLIWATTS_RANGES",row.getChildTextTrim("DECIBELMILLIWATTS_RANGES"));
		}	
		if(row.getChildTextTrim("DECIBELMILLIWATTS_TEXT")!=null)
		{
			fastMap.put("DECIBELMILLIWATTS_TEXT",row.getChildTextTrim("DECIBELMILLIWATTS_TEXT"));
		}  		
		if(row.getChildTextTrim("EQUIVALENTDOSE_RANGES")!=null)
		{
			fastMap.put("EQUIVALENTDOSE_RANGES",row.getChildTextTrim("EQUIVALENTDOSE_RANGES"));
		}
		if(row.getChildTextTrim("EQUIVALENTDOSE_TEXT")!=null)
		{
			fastMap.put("EQUIVALENTDOSE_TEXT",row.getChildTextTrim("EQUIVALENTDOSE_TEXT"));
		}
		if(row.getChildTextTrim("MOLARCONCENTRATION_RANGES")!=null)
		{
			fastMap.put("MOLARCONCENTRATION_RANGES",row.getChildTextTrim("MOLARCONCENTRATION_RANGES"));
		}
		if(row.getChildTextTrim("MOLARCONCENTRATION_TEXT")!=null)
		{
			fastMap.put("MOLARCONCENTRATION_TEXT",row.getChildTextTrim("MOLARCONCENTRATION_TEXT"));
		}
		if(row.getChildTextTrim("LINEARDENSITY_RANGES")!=null)
		{
			fastMap.put("LINEARDENSITY_RANGES",row.getChildTextTrim("LINEARDENSITY_RANGES"));
		}	
		if(row.getChildTextTrim("LINEARDENSITY_TEXT")!=null)
		{
			fastMap.put("LINEARDENSITY_TEXT",row.getChildTextTrim("LINEARDENSITY_TEXT"));
		}      	
		if(row.getChildTextTrim("LUMINOUSEFFICIENCY_RANGES")!=null)
		{
			fastMap.put("LUMINOUSEFFICIENCY_RANGES",row.getChildTextTrim("LUMINOUSEFFICIENCY_RANGES"));
		}
		if(row.getChildTextTrim("LUMINOUSEFFICIENCY_TEXT")!=null)
		{
			fastMap.put("LUMINOUSEFFICIENCY_TEXT",row.getChildTextTrim("LUMINOUSEFFICIENCY_TEXT"));
		}
		if(row.getChildTextTrim("LUMINOUSEFFICACY_RANGES")!=null)
		{
			fastMap.put("LUMINOUSEFFICACY_RANGES",row.getChildTextTrim("LUMINOUSEFFICACY_RANGES"));
		}
		if(row.getChildTextTrim("LUMINOUSEFFICACY_TEXT")!=null)
		{
			fastMap.put("LUMINOUSEFFICACY_TEXT",row.getChildTextTrim("LUMINOUSEFFICACY_TEXT"));
		}
		if(row.getChildTextTrim("SPECIFICENERGY_RANGES")!=null)
		{
			fastMap.put("SPECIFICENERGY_RANGES",row.getChildTextTrim("SPECIFICENERGY_RANGES"));
		}	
		if(row.getChildTextTrim("SPECIFICENERGY_TEXT")!=null)
		{
			fastMap.put("SPECIFICENERGY_TEXT",row.getChildTextTrim("SPECIFICENERGY_TEXT"));
		}      	      
		if(row.getChildTextTrim("SPECIFICSURFACEAREA_RANGES")!=null)
		{
			fastMap.put("SPECIFICSURFACEAREA_RANGES",row.getChildTextTrim("SPECIFICSURFACEAREA_RANGES"));
		}
		if(row.getChildTextTrim("SPECIFICSURFACEAREA_TEXT")!=null)
		{
			fastMap.put("SPECIFICSURFACEAREA_TEXT",row.getChildTextTrim("SPECIFICSURFACEAREA_TEXT"));
		}
		if(row.getChildTextTrim("SPECIFICVOLUME_RANGES")!=null)
		{
			fastMap.put("SPECIFICVOLUME_RANGES",row.getChildTextTrim("SPECIFICVOLUME_RANGES"));
		}
		if(row.getChildTextTrim("SPECIFICVOLUME_TEXT")!=null)
		{
			fastMap.put("SPECIFICVOLUME_TEXT",row.getChildTextTrim("SPECIFICVOLUME_TEXT"));
		}
		if(row.getChildTextTrim("SURFACETENSION_RANGES")!=null)
		{
			fastMap.put("SURFACETENSION_RANGES",row.getChildTextTrim("SURFACETENSION_RANGES"));
		}	
		if(row.getChildTextTrim("SURFACETENSION_TEXT")!=null)
		{
			fastMap.put("SURFACETENSION_TEXT",row.getChildTextTrim("SURFACETENSION_TEXT"));
		}             
		if(row.getChildTextTrim("SURFACEDENSITY_RANGES")!=null)
		{
			fastMap.put("SURFACEDENSITY_RANGES",row.getChildTextTrim("SURFACEDENSITY_RANGES"));
		}
		if(row.getChildTextTrim("SURFACEDENSITY_TEXT")!=null)
		{
			fastMap.put("SURFACEDENSITY_TEXT",row.getChildTextTrim("SURFACEDENSITY_TEXT"));
		}
		if(row.getChildTextTrim("NUMERICAL_UNITS")!=null)
		{
			fastMap.put("NUMERICAL_UNITS",row.getChildTextTrim("NUMERICAL_UNITS"));
		}
		if(row.getChildTextTrim("EID")!=null)
		{
			fastMap.put("EID",row.getChildTextTrim("EID"));
		}
		if(row.getChildTextTrim("DEPARTMENTID")!=null)
		{
			fastMap.put("DEPARTMENTID",row.getChildTextTrim("DEPARTMENTID"));
		}	
		if(row.getChildTextTrim("TITLEOFCOLLECTION")!=null)
		{
			fastMap.put("TITLEOFCOLLECTION",row.getChildTextTrim("TITLEOFCOLLECTION"));
		}         	     
		if(row.getChildTextTrim("UNIVERSITY")!=null)
		{
			fastMap.put("UNIVERSITY",row.getChildTextTrim("UNIVERSITY"));
		}
		if(row.getChildTextTrim("TYPEOFDEGREE")!=null)
		{
			fastMap.put("TYPEOFDEGREE",row.getChildTextTrim("TYPEOFDEGREE"));
		}
		if(row.getChildTextTrim("ANNOTATION")!=null)
		{
			fastMap.put("ANNOTATION",row.getChildTextTrim("ANNOTATION"));
		}
		if(row.getChildTextTrim("MAPSCALE")!=null)
		{
			fastMap.put("MAPSCALE",row.getChildTextTrim("MAPSCALE"));
		}
		if(row.getChildTextTrim("MAPTYPE")!=null)
		{
			fastMap.put("MAPTYPE",row.getChildTextTrim("MAPTYPE"));
		}	
		if(row.getChildTextTrim("SOURCENOTE")!=null)
		{
			fastMap.put("SOURCENOTE",row.getChildTextTrim("SOURCENOTE"));
		}  		
		if(row.getChildTextTrim("GRANTID")!=null)
		{
			fastMap.put("GRANTID",row.getChildTextTrim("GRANTID"));
		} 
		if(row.getChildTextTrim("GRANTAGENCY")!=null)
		{
			fastMap.put("GRANTAGENCY",row.getChildTextTrim("GRANTAGENCY"));
		}
		
		
		if(row.getChildTextTrim("EV_SPARE1")!=null)
		{
			fastMap.put("SOURCEBIBTEXT",row.getChildTextTrim("EV_SPARE1"));
		}	
		if(row.getChildTextTrim("EV_SPARE7")!=null)
		{
			fastMap.put("STANDARDID",row.getChildTextTrim("EV_SPARE7"));
		}  		
		if(row.getChildTextTrim("EV_SPARE8")!=null)
		{
			fastMap.put("STANDARDDESIGNATION",row.getChildTextTrim("EV_SPARE8"));
		} 
		if(row.getChildTextTrim("EV_SPARE9")!=null)
		{
			fastMap.put("GRANTTEXT",row.getChildTextTrim("EV_SPARE9"));
		}
		if(row.getChildTextTrim("EV_SPARE10")!=null)
		{
			fastMap.put("TOTALGRANTINFO",row.getChildTextTrim("EV_SPARE10"));
		}
	       
		
		writeJson(fastMap,out);				
	}
	
	private void writeJson(HashMap<String,String> fastMap, FileWriter out) throws Exception
	{
		
		JsonObject personObject = Json.createObjectBuilder()
                .add("eidocid", fastMap.get("EIDOCID"))
                .add("parentid", fastMap.get("PARENTID")==null?"":fastMap.get("PARENTID"))
                .add("dedupkey", fastMap.get("DEDUPKEY")==null?"":fastMap.get("DEDUPKEY"))              
                .add("loadnumber", fastMap.get("LOADNUMBER")==null?"":fastMap.get("LOADNUMBER"))
                .add("datesort", fastMap.get("DATESORT")==null?"":fastMap.get("DATESORT"))
                .add("pubyear", fastMap.get("PUBYEAR")==null?"":fastMap.get("PUBYEAR"))                
                .add("accessnumber", fastMap.get("ACCESSNUMBER")==null?"":fastMap.get("ACCESSNUMBER"))
                .add("author", buildContentArray(fastMap.get("AUTHOR")==null?"":fastMap.get("AUTHOR")))
                .add("authoraffiliation", buildContentArray(fastMap.get("AUTHORAFFILIATION")==null?"":fastMap.get("AUTHORAFFILIATION")))
                
                .add("authoraffiliationlocation", buildContentArray(fastMap.get("AFFILIATIONLOCATION")==null?"":fastMap.get("AFFILIATIONLOCATION")))
                .add("title", buildContentArray(fastMap.get("TITLE")==null?"":fastMap.get("TITLE")))
                .add("translatedtitle", buildContentArray(fastMap.get("TRANSLATEDTITLE")==null?"":fastMap.get("TRANSLATEDTITLE")))
                .add("volumetitle", buildContentArray(fastMap.get("VOLUMETITLE")==null?"":fastMap.get("VOLUMETITLE")))
                .add("abstract", buildContentArray(fastMap.get("ABSTRACT")==null?"":fastMap.get("ABSTRACT")))
                .add("otherabstract", buildContentArray(fastMap.get("OTHERABSTRACT")==null?"":fastMap.get("OTHERABSTRACT")))
                .add("editor", buildContentArray(fastMap.get("EDITOR")==null?"":fastMap.get("EDITOR")))
                .add("editoraffiliation", buildContentArray(fastMap.get("EDITORAFFILIATION")==null?"":fastMap.get("EDITORAFFILIATION")))
                
                /*
                .add("address", 
                     Json.createObjectBuilder().add("street", "Main Street")
                                               .add("city", "New York")
                                               .add("zipCode", "11111")
                                               .build()
                    )
                .add("phoneNumber", 
                     Json.createArrayBuilder().add("00-000-0000")
                                              .add("11-111-1111")
                                              .add("11-111-1112")
                                              .build()
                    )
                    */
                .build();
         
        //System.out.println("Object: " + personObject);
		out.write(personObject.toString()+"\n");
	}
	
	private JsonArray buildContentArray(String authorString)
	{
		String mainPart = "";
		String stemPart = "";
		JsonArrayBuilder builder = Json.createArrayBuilder();
		//if(authorString.indexOf("QstemQ")<0)
		//System.out.println(authorString.indexOf("QstemQ"));
		int qstemqPosition = authorString.indexOf("QstemQ");
		if(authorString!=null)
		{
			if(qstemqPosition>0)
			{
				mainPart = authorString.substring(0,qstemqPosition);
				if(authorString.length()>qstemqPosition+7)
				stemPart = authorString.substring(qstemqPosition+7);
			}
			else
			{
				mainPart = authorString;
			}			
		}
		String[] authorArray = mainPart.split("QQDelQQ");
		for(int i=0;i<authorArray.length;i++)
		{
			if(authorArray[i].trim().length()>0)
				builder.add(authorArray[i].trim());
		}
		return builder.build();
		
	}
}