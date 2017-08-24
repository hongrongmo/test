package org.ei.dataloading.cafe;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

import org.ei.common.Constants;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.util.GUID;
import org.jdom2.*;                  //// replace svn jdom with recent jdom2
import org.jdom2.input.*;
import org.jdom2.output.*;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class AuthorProfile
{
	private static int loadNumber;
	private String databaseName;
	private String action;
	private Namespace xoeNamespace=Namespace.getNamespace("xoe","http://www.elsevier.com/xml/xoe/dtd");
	private Namespace xocsNamespace=Namespace.getNamespace("xocs","http://www.elsevier.com/xml/xocs/dtd");
	private Namespace noNamespace=Namespace.getNamespace("","");
	private static Connection con;
	private static String infile;
	private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	private static String driver = "oracle.jdbc.driver.OracleDriver";
	private static String username = "ba_loading";
	private static String password = "ny5av";
	private SAXBuilder builder;
	public static final char FIELDDELIM = '\t';
	private DataLoadDictionary dictionary = new DataLoadDictionary();
	private static HashSet affIDSet = new HashSet();
	
			
	public static void main(String args[]) throws Exception
	{
		long startTime = System.currentTimeMillis();
		if(args.length<=1)
		{
		    System.out.println("please enter loadnumber and the name of file to be converted");
		    System.exit(1);
		}
			
		infile = args[0];	
		String loadnumber = args[1];
		
		FileWriter outFile_au = null;
		FileWriter outFile_af = null;
		try
        {         
			
            Pattern pattern = Pattern.compile("^\\d*$");
            Matcher matcher = pattern.matcher(loadnumber);
            if (matcher.find())
            {
            	loadNumber = Integer.parseInt(loadnumber);
            }
            else
            {
            	System.out.println("did not find loadnumber or loadnumber has wrong format");
            	System.exit(1);
            }
            AuthorProfile c = new AuthorProfile(loadNumber);  
            outFile_au = new FileWriter(infile+"_au_"+loadnumber+".out");
            outFile_af = new FileWriter(infile+"_af_"+loadnumber+".out");
            c.readFile(infile,outFile_au,outFile_af);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        { 
        	if(outFile_au != null)
 		    {
        		outFile_au.close();
 		    }
            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        }
	}
	
	public AuthorProfile(int loadnumber)
	{
		builder = new SAXBuilder();
		builder.setExpandEntities(false);
		this.loadNumber=loadnumber;
	}
	
	void readFile(String filename, FileWriter out, FileWriter out_aff) throws Exception
	{
		
	BufferedReader in = null;
		
		try
		{
		   
		    if(filename.toLowerCase().endsWith(".zip"))
		    {
		        System.out.println("IS ZIP FILE");
		        ZipFile zipFile = new ZipFile(filename);
		        Enumeration entries = zipFile.entries();
		        int i=0;
		        while (entries.hasMoreElements())
		        {
		            ZipEntry entry = (ZipEntry)entries.nextElement();
		            in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
		            if(in !=null)
		            {
		            	writeRecs(in,out,out_aff);
		            }
		            
		            i++;
		        }
		    }
		    else if(filename.toLowerCase().endsWith(".xml"))
		    {
		        System.out.println("IS XML FILE");
		        in = new BufferedReader(new FileReader(filename));
		        writeRecs(in,out,out_aff);
		    }        
		    else
		    {
		        System.out.println("this application only handle xml and zip file");
		    }
		
		}
		catch (IOException e)
		{
		    System.err.println(e);
		    System.exit(1);
		}
		finally
		{			
		    if(in != null)
		    {
		        in.close();
		    }
		}
			    
	}
	
	private void writeRecs(BufferedReader xmlReader, FileWriter out, FileWriter out_aff) throws Exception
    {
   
        try
        {
	
            if (xmlReader!=null)
            {           	
                Hashtable h = parser(xmlReader,out_aff);
            
                if (h != null)
                {                  
                	writeRec(h,out);                   
                }
               
            }
            out.flush();
            out_aff.flush();
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }   
    }
	
	private Hashtable parser(Reader r, FileWriter out_aff)throws Exception
	{
		
		Hashtable record = new Hashtable();
		if(r != null)
		{
			Document doc = builder.build(r);
			Element cpxRoot = doc.getRootElement();
			Element docElement = cpxRoot.getChild("doc",xocsNamespace);
			if(docElement!=null)
			{
				String mid = "aut_"+(new GUID()).toString();
				record.put("M_ID",mid);
				Element meta = docElement.getChild("meta",xocsNamespace);
				if(meta != null)
				{
					if(meta.getChild("eid",xocsNamespace)!=null)
					{
						String eid = meta.getChildText("eid",xocsNamespace);
						record.put("EID",eid);
						//System.out.println("EID= "+eid);
					}
					
					if(meta.getChild("orcid",xocsNamespace)!=null)
					{
						String orcid = meta.getChildText("orcid",xocsNamespace);
						record.put("ORCID",orcid);						
					}
					
					if(meta.getChild("timestamp",xocsNamespace)!=null)
					{
						String timestamp = meta.getChildText("timestamp",xocsNamespace);
						record.put("TIMESTAMP",timestamp);
						//System.out.println("timestamp= "+timestamp);
					}
					
					if(meta.getChild("indexeddate",xocsNamespace)!=null)
					{
						Element indexed = meta.getChild("indexeddate",xocsNamespace);
						if(indexed !=null)
						{
							String epoch = indexed.getAttributeValue("epoch",noNamespace);
							record.put("EPOCH",epoch);
							//System.out.println("epoch= "+epoch);
						}
						String indexeddate = meta.getChildText("indexeddate",xocsNamespace);				
						record.put("INDEXEDDATE", indexeddate);
						//System.out.println("indexeddate= "+indexeddate);					
					}	
					
					if(meta.getChild("srctitles",xocsNamespace)!=null)
					{
						StringBuffer srcBuffer = new StringBuffer();
						Element srctitles = meta.getChild("srctitles",xocsNamespace);
						List srctitleList = srctitles.getChildren("srctitle",xocsNamespace);						
						for (int g = 0; g< srctitleList.size();g++)
						{
							Element srctitle =(Element) srctitleList.get(g);
							srcBuffer.append(srctitle.getTextTrim());	
							if(g<srctitleList.size())
							{
								srcBuffer.append(Constants.IDDELIMITER);
							}
						}
						record.put("SRCTITLES",srcBuffer.toString());
						//System.out.println("SRCTITLES= "+srcBuffer.toString());
					}
					
				}
				
				Element xocsAuthorProfile = docElement.getChild("author-profile",xocsNamespace);
				if(xocsAuthorProfile != null)
				{
					Element authorProfile = xocsAuthorProfile.getChild("author-profile",noNamespace);
					if(authorProfile != null)
					{
						String authorID = authorProfile.getAttributeValue("id",noNamespace);
						record.put("AUTHORID",authorID);
						//System.out.println("authorID= "+authorID);
						if(authorProfile.getChild("status",noNamespace)!=null)
						{
							String status = authorProfile.getChildText("status",noNamespace);
							record.put("STATUS", status);
							//System.out.println("status= "+status);
						}
						
						if(authorProfile.getChild("date-created",noNamespace)!=null)
						{
							Element dateCreated = authorProfile.getChild("date-created",noNamespace);
							String day = dateCreated.getAttributeValue("day",noNamespace);
							String month = dateCreated.getAttributeValue("month",noNamespace);
							String year = dateCreated.getAttributeValue("year",noNamespace);
							String timestamp = dateCreated.getAttributeValue("timestamp",noNamespace);
							if(timestamp==null)
							{
								timestamp = year+"-"+month+"-"+day;
							}
							record.put("DATECREATED", timestamp);
							//System.out.println("date-created= "+timestamp);
						}
						
						if(authorProfile.getChild("date-revised",noNamespace)!=null)
						{
							List dateReviseds = authorProfile.getChildren("date-revised",noNamespace);
							Element dateRevised = (Element)dateReviseds.get(dateReviseds.size()-1);
							String day = dateRevised.getAttributeValue("day",noNamespace);
							String month = dateRevised.getAttributeValue("month",noNamespace);
							String year = dateRevised.getAttributeValue("year",noNamespace);
							String timestamp = dateRevised.getAttributeValue("timestamp",noNamespace);
							if(timestamp==null)
							{
								timestamp = year+"-"+month+"-"+day;
							}
							record.put("DATEREVISED",timestamp);
							//System.out.println("date-revised= "+timestamp);
						}
						
						if(authorProfile.getChild("preferred-name",noNamespace)!=null)
						{
							StringBuffer nameBuffer = new StringBuffer();
							Element preferredName = authorProfile.getChild("preferred-name",noNamespace);
							String initials = preferredName.getChildText("initials",noNamespace);
							String indexedName = preferredName.getChildText("indexed-name",noNamespace);
							String surname = preferredName.getChildText("surname",noNamespace);
							String givenName = preferredName.getChildText("given-name",noNamespace);
							if(initials!=null)
							{
								record.put("INITIALS",dictionary.mapEntity(initials));
							}					
							
							if(indexedName!=null)
							{
								record.put("INDEXEDNAME",dictionary.mapEntity(indexedName));
							}														
							
							if(surname!=null)
							{
								record.put("SURENAME",dictionary.mapEntity(surname));
							}														
							
							if(givenName!=null)
							{
								record.put("GIVENNAME",dictionary.mapEntity(givenName));
							}
							
							
						}																		
						
						if(authorProfile.getChild("name-variant",noNamespace)!=null)
						{
							StringBuffer nameBuffer = new StringBuffer();
							List nameVariants = authorProfile.getChildren("name-variant",noNamespace);
							for (int g = 0; g< nameVariants.size();g++)
							{
								Element nameVariant =(Element) nameVariants.get(g);
								
								Element preferredName = nameVariant.getChild("preferred-name",noNamespace);
								String initials = nameVariant.getChildText("initials",noNamespace);
								String indexedName = nameVariant.getChildText("indexed-name",noNamespace);
								String surname = nameVariant.getChildText("surname",noNamespace);
								String givenName = nameVariant.getChildText("given-name",noNamespace);
								if(initials!=null)
								{
									nameBuffer.append(dictionary.mapEntity(initials));
								}
								
								nameBuffer.append(Constants.IDDELIMITER);
								
								if(indexedName!=null)
								{
									nameBuffer.append(dictionary.mapEntity(indexedName));
								}
								
								nameBuffer.append(Constants.IDDELIMITER);
								
								if(surname!=null)
								{
									nameBuffer.append(dictionary.mapEntity(surname));
								}
								
								nameBuffer.append(Constants.IDDELIMITER);
								
								if(givenName!=null)
								{
									nameBuffer.append(dictionary.mapEntity(givenName));
								}
								
								if(g<nameVariants.size()-1)
								{
									nameBuffer.append(Constants.AUDELIMITER);
								}
								
							}
							record.put("NAMEVARIANT", nameBuffer.toString());
							//System.out.println("name-variant= "+nameBuffer.toString());
						}
						
						if(authorProfile.getChild("e-address",noNamespace)!=null)
						{
							Element e_address = authorProfile.getChild("e-address",noNamespace);
							String eaddress = e_address.getTextTrim();
							String type=e_address.getAttributeValue("type");
							StringBuffer eaddressBuffer = new StringBuffer();
							if(type!=null)
							{
								eaddressBuffer.append(type);
							}
							
							eaddressBuffer.append(Constants.IDDELIMITER);
							if(eaddress!=null)
							{
								eaddressBuffer.append(eaddress);
							}
							record.put("E_ADDRESS", eaddressBuffer.toString());		
						}
						
						if(authorProfile.getChild("classificationgroup",noNamespace)!=null)
						{
							StringBuffer classificationASJCBuffer = new StringBuffer();
							StringBuffer classificationSUBJABBRBuffer = new StringBuffer();
							Element classificationgroup = authorProfile.getChild("classificationgroup",noNamespace);
							if(classificationgroup.getChild("classifications",noNamespace)!=null)
							{
								List classificationsList = classificationgroup.getChildren("classifications",noNamespace);
								for (int g = 0; g< classificationsList.size();g++)
								{
									Element classifications =(Element) classificationsList.get(g);
									String classificationsType = classifications.getAttributeValue("type",noNamespace);
									if(classificationsType.equals("ASJC"))
									{
										if(classifications.getChild("classification")!=null)
										{
											List classificationList = classifications.getChildren("classification",noNamespace);
											for (int i = 0; i< classificationList.size();i++)
											{
												Element classification =(Element) classificationList.get(i);
												
												String frequency = classification.getAttributeValue("frequency");
												String code = classification.getTextTrim();
												if(frequency!=null)
												{
													classificationASJCBuffer.append(frequency);
												}
												classificationASJCBuffer.append(Constants.IDDELIMITER);
												if(code!=null)
												{
													classificationASJCBuffer.append(code);
												}
												
												if(i<classificationList.size()-1)
												{
													classificationASJCBuffer.append(Constants.AUDELIMITER);
												}
													
											}
												
										}
									}
									
									if (classificationsType.equals("SUBJABBR"))
									{
										if(classifications.getChild("search-classification")!=null)
										{
											List classificationList = classifications.getChildren("search-classification",noNamespace);
											for (int i = 0; i< classificationList.size();i++)
											{
												Element classification =(Element) classificationList.get(i);
												
												String frequency = classification.getChildText("frequency");
												String code = classification.getChildText("code");
												if(frequency!=null)
												{
													classificationSUBJABBRBuffer.append(frequency);
												}
												classificationSUBJABBRBuffer.append(Constants.IDDELIMITER);
												if(code!=null)
												{
													classificationSUBJABBRBuffer.append(code);
												}
												
												if(i<classificationList.size()-1)
												{
													classificationSUBJABBRBuffer.append(Constants.AUDELIMITER);
												}
													
											}
												
										}
									}
								}
							}
							record.put("CLASS_SUBJABBR",classificationSUBJABBRBuffer.toString());
							record.put("CLASS_ASJC",classificationASJCBuffer.toString());
						
						}
						
						if(authorProfile.getChild("publication-range",noNamespace)!=null)
						{
							StringBuffer publicationRangeBuffer = new StringBuffer();
							Element publicationRange = authorProfile.getChild("publication-range",noNamespace);
							String end = publicationRange.getAttributeValue("end",noNamespace);
							String start = publicationRange.getAttributeValue("start",noNamespace);
							if(start!=null)
							{
								publicationRangeBuffer.append(start);
								if(end != null)
								{
									publicationRangeBuffer.append("-");
								}
							}
							
							if(end != null)
							{
								publicationRangeBuffer.append(end);
							}
							
							if(publicationRangeBuffer.length()>0)
							{
								record.put("PUBLICATIONRANGE",publicationRangeBuffer.toString());
							}
						}
						
						
						if(authorProfile.getChild("journal-history",noNamespace)!=null)
						{
							Element journalHistory = authorProfile.getChild("journal-history",noNamespace);
							String journalHistoryType = journalHistory.getAttributeValue("type",noNamespace);
							List journals = journalHistory.getChildren("journal",noNamespace);
							
							if(journalHistoryType!=null)
							{
								record.put("JORNALHISTORYTYPE", journalHistoryType);
							}
							
							if(journalHistory.getChild("journal",noNamespace)!=null)
							{
								StringBuffer journalBuffer = new StringBuffer();
								for (int g = 0; g< journals.size();g++)
								{
									
									Element journal =(Element) journals.get(g);
									if(journal.getAttributeValue("type") != null )
									{
									    String journalType = journal.getAttributeValue("type");
									    journalBuffer.append(journalType);									    
									}
									journalBuffer.append(Constants.IDDELIMITER);
									
									if(journal.getChild("sourcetitle",noNamespace)!=null)
									{
										String sourcetitle = journal.getChildText("sourcetitle",noNamespace);
										journalBuffer.append(dictionary.mapEntity(sourcetitle));
									}
									
									journalBuffer.append(Constants.IDDELIMITER);
									
									if(journal.getChild("sourcetitle-abbrev",noNamespace)!=null)
									{
										String sourcetitleAbbrev = journal.getChildText("sourcetitle-abbrev",noNamespace);
										journalBuffer.append(dictionary.mapEntity(sourcetitleAbbrev));
									}
									
									journalBuffer.append(Constants.IDDELIMITER);
									
									if(journal.getChild("issn",noNamespace)!=null)
									{
										String issn = journal.getChildText("issn",noNamespace);
										journalBuffer.append(issn);
									}
									
									if(g< journals.size()-1)
									{
										journalBuffer.append(Constants.AUDELIMITER);
									}
								}
								
								if(journalBuffer.length()>0)
								{
									record.put("JOURNALS",journalBuffer.toString());
								}
							}
							
								
							
						}
						
						if(authorProfile.getChild("affiliation-current",noNamespace)!=null)
						{
							
							Element currentAffiliation  = authorProfile.getChild("affiliation-current",noNamespace);	
							Element affiliation  = currentAffiliation.getChild("affiliation",noNamespace);
							if(affiliation!=null)
							{
								String currentAffiliationID = affiliation.getAttributeValue("affiliation-id");
								String parentAffiliationID  = affiliation.getAttributeValue("parent");
								if(affiliation.getChild("ip-doc",noNamespace)!=null)
								{
									List ip_docs = affiliation.getChildren("ip-doc",noNamespace);
									for(int i=0;i<ip_docs.size();i++)
									{
										Element ip_doc =(Element) ip_docs.get(i);
										String id = ip_doc.getAttributeValue("id");
										String type = ip_doc.getAttributeValue("type");
										String relationship = ip_doc.getAttributeValue("relationship");
										
										if(id.equals(currentAffiliationID))
										{
											record.put("CURRENTAFFILIATIONID",id);
											record.put("CURRENTAFFILIATIONTYPE",type);
											record.put("CURRENTAFFILIATIONRELATIONSHIP",relationship);
										}
										else if(id.equals(parentAffiliationID))
										{
											record.put("PARENTAFFILIATIONID",id);
											record.put("PARENTAFFILIATIONTYPE",type);
											record.put("PARENTAFFILIATIONRELATIONSHIP",relationship);
										}										 										
									}
									outputAffiliation(ip_docs, out_aff);
								}								
								
							}
						}
						
						if(authorProfile.getChild("affiliation-history",noNamespace)!=null)
						{
							
							Element historyAffiliations  = authorProfile.getChild("affiliation-history",noNamespace);
							List affiliations = historyAffiliations.getChildren("affiliation",noNamespace);
							StringBuffer historyAffiliationsBuffer = new StringBuffer();
							for (int g = 0; g< affiliations.size();g++)
							{
								
								Element historyAffiliation =(Element) affiliations.get(g);
								String historyAffiliationID = historyAffiliation.getAttributeValue("affiliation-id");
								String parentHistoryAffiliationID  = historyAffiliation.getAttributeValue("parent");
								if(historyAffiliation.getChild("ip-doc",noNamespace)!=null)
								{
									List ip_docs = historyAffiliation.getChildren("ip-doc",noNamespace);
									for(int i=0;i<ip_docs.size();i++)
									{
										Element ip_doc =(Element) ip_docs.get(i);
										String id = ip_doc.getAttributeValue("id");
										//System.out.println("ID="+id);
										String type = ip_doc.getAttributeValue("type");
										//System.out.println("type="+type);
										String relationship = ip_doc.getAttributeValue("relationship");
										//System.out.println("relationship="+relationship);
										if(id.equals(historyAffiliationID))
										{
											historyAffiliationsBuffer.append("aff:"+historyAffiliationID+";");
											if(type!=null)
											{
												historyAffiliationsBuffer.append(type);
											}
											historyAffiliationsBuffer.append(";");
											if(relationship!=null)
											{
												historyAffiliationsBuffer.append(relationship);
											}
											
										}
										else if(id.equals(parentHistoryAffiliationID))
										{
											historyAffiliationsBuffer.append("parent:"+parentHistoryAffiliationID+";");
											if(type!=null)
											{
												historyAffiliationsBuffer.append(type);
											}
											historyAffiliationsBuffer.append(";");
											if(relationship!=null)
											{
												historyAffiliationsBuffer.append(relationship);
											}
										}	
										historyAffiliationsBuffer.append(Constants.IDDELIMITER);
									}
									outputAffiliation(ip_docs, out_aff);
								}								
															
								
								if(g< affiliations.size()-1)
								{
									historyAffiliationsBuffer.append(Constants.AUDELIMITER);
								}
							}
							
							if(historyAffiliationsBuffer.length()>0)
							{
								record.put("HISTORYAFFILIATIONID",historyAffiliationsBuffer.toString());
							}
						}
																	
					}
					else
					{
						System.out.println("authorProfile is null");
					}
					
				}
				else
				{
					System.out.println("xocsAuthorProfile is null");
				}
				
			}
			
			//System.out.println("ROOTELEMENT "+cpxRoot.getName());
		}
		return record;
		
	}
	
	private void outputAffiliation(List ip_docs, FileWriter out) throws Exception
	{
		String affid = null;
		for(int i=0;i<ip_docs.size();i++)
		{
			StringBuffer affBuffer = new StringBuffer();
			Element ip_doc =(Element) ip_docs.get(i);
			if(ip_doc.getAttributeValue("id")!=null)
			{
				affid = ip_doc.getAttributeValue("id");
				affBuffer.append(affid);
			}
			else
			{
				affid = null;
			}
			affBuffer.append(FIELDDELIM);
			if(ip_doc.getChildText("afnameid")!=null)
			{
				affBuffer.append(dictionary.mapEntity(ip_doc.getChildText("afnameid")));
			}
			affBuffer.append(FIELDDELIM);
			
			if(ip_doc.getChildText("afdispname")!=null)
			{
				affBuffer.append(dictionary.mapEntity(ip_doc.getChildText("afdispname")));
			}
			affBuffer.append(FIELDDELIM);
			if(ip_doc.getChildText("preferred-name")!=null)
			{
				affBuffer.append(dictionary.mapEntity(ip_doc.getChildText("preferred-name")));
			}
			affBuffer.append(FIELDDELIM);
			if(ip_doc.getChildText("parent-preferred-name")!=null)
			{
				affBuffer.append(dictionary.mapEntity(ip_doc.getChildText("parent-preferred-name")));
			}
			affBuffer.append(FIELDDELIM);
			if(ip_doc.getChildText("sort-name")!=null)
			{
				affBuffer.append(dictionary.mapEntity(ip_doc.getChildText("sort-name")));
			}
			affBuffer.append(FIELDDELIM);
			if(ip_doc.getChild("name-variant")!=null)
			{
				List name_variants=ip_doc.getChildren("name-variant");
				for(int j=0;j<name_variants.size();j++)
				{					
					Element name_variant =(Element) name_variants.get(j);
					if(name_variant!=null && name_variant.getTextTrim().length()>0)
					{
						affBuffer.append(dictionary.mapEntity(name_variant.getTextTrim()));
						if(j<name_variants.size()-1)
						{
							affBuffer.append(Constants.IDDELIMITER);
						}
					}
				}
				
			}
			affBuffer.append(FIELDDELIM);
			
			
			
			Element address=ip_doc.getChild("address");
			String country = null;
			String city_group = null;
			if(address!=null)
			{
				country = address.getAttributeValue("country");
				city_group = address.getChildText("city-group");
			}
			
			if(address != null && city_group!=null)
			{
				affBuffer.append(dictionary.mapEntity(city_group));
			}
			affBuffer.append(FIELDDELIM);
			
			if(address != null && address.getChildText("address-part")!=null)
			{
				affBuffer.append(dictionary.mapEntity(address.getChildText("address-part")));
			}
			affBuffer.append(FIELDDELIM);

			if(address != null && address.getChildText("city")!=null)
			{
				affBuffer.append(dictionary.mapEntity(address.getChildText("city")));
			}
			
			affBuffer.append(FIELDDELIM);
			if(address != null && address.getChildText("state")!=null)
			{
			
				affBuffer.append(dictionary.mapEntity(address.getChildText("state")));
			}
			
			affBuffer.append(FIELDDELIM);
			if(address != null && address.getChildText("postal-code")!=null)
			{
			
				affBuffer.append(dictionary.mapEntity(address.getChildText("postal-code")));
			}
			
			affBuffer.append(FIELDDELIM);
			if(address != null && address.getChildText("country")!=null)
			{
				affBuffer.append(dictionary.mapEntity(address.getChildText("country")));
			}
			else if(address != null && country!=null)
			{
				affBuffer.append(country);
			}
			affBuffer.append(FIELDDELIM);
			affBuffer.append(loadNumber);
			if(affid!=null && !affIDSet.contains(affid))
			{
				affIDSet.add(affid);
				out.write(affBuffer.toString()+"\n");
				//System.out.println("affid "+affid+" loaded");
			}
			//else
			//{
			//	System.out.println("affid "+affid+" not loaded");
			//}
			
		}
	}
	
	public void writeRec(Hashtable record, FileWriter out) throws Exception
	{
		StringBuffer recordBuf = new StringBuffer();
		if(record!=null)
		{
			if(record.get("M_ID")!=null)
			{
				recordBuf.append((String)record.get("M_ID"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("EID")!=null)
			{
				recordBuf.append((String)record.get("EID"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("TIMESTAMP")!=null)
			{
				recordBuf.append((String)record.get("TIMESTAMP"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("EPOCH")!=null)
			{
				recordBuf.append((String)record.get("EPOCH"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("INDEXEDDATE")!=null)
			{
				recordBuf.append((String)record.get("INDEXEDDATE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("AUTHORID")!=null)
			{
				recordBuf.append((String)record.get("AUTHORID"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("STATUS")!=null)
			{
				recordBuf.append((String)record.get("STATUS"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("DATECREATED")!=null)
			{
				recordBuf.append((String)record.get("DATECREATED"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("DATEREVISED")!=null)
			{
				recordBuf.append((String)record.get("DATEREVISED"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("INITIALS")!=null)
			{
				recordBuf.append((String)record.get("INITIALS"));				
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("INDEXEDNAME")!=null)
			{
				recordBuf.append((String)record.get("INDEXEDNAME"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("SURENAME")!=null)
			{
				recordBuf.append((String)record.get("SURENAME"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("GIVENNAME")!=null)
			{
				recordBuf.append((String)record.get("GIVENNAME"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("NAMEVARIANT")!=null)
			{
				recordBuf.append((String)record.get("NAMEVARIANT"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CLASS_SUBJABBR")!=null)
			{
				recordBuf.append((String)record.get("CLASS_SUBJABBR"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CLASS_ASJC")!=null)
			{
				recordBuf.append((String)record.get("CLASS_ASJC"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("PUBLICATIONRANGE")!=null)
			{
				recordBuf.append((String)record.get("PUBLICATIONRANGE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("JORNALHISTORYTYPE")!=null)
			{
				recordBuf.append((String)record.get("JORNALHISTORYTYPE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("JOURNALS")!=null)
			{
				recordBuf.append((String)record.get("JOURNALS"));				
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CURRENTAFFILIATIONID")!=null)
			{
				recordBuf.append((String)record.get("CURRENTAFFILIATIONID"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CURRENTAFFILIATIONTYPE")!=null)
			{
				recordBuf.append((String)record.get("CURRENTAFFILIATIONTYPE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CURRENTAFFILIATIONRELATIONSHIP")!=null)
			{
				recordBuf.append((String)record.get("CURRENTAFFILIATIONRELATIONSHIP"));
			}
			recordBuf.append(FIELDDELIM);
			
			
			if(record.get("PARENTAFFILIATIONID")!=null)
			{
				recordBuf.append((String)record.get("PARENTAFFILIATIONID"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("PARENTAFFILIATIONTYPE")!=null)
			{
				recordBuf.append((String)record.get("PARENTAFFILIATIONTYPE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("PARENTAFFILIATIONRELATIONSHIP")!=null)
			{
				recordBuf.append((String)record.get("PARENTAFFILIATIONRELATIONSHIP"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("HISTORYAFFILIATIONID")!=null)
			{
				recordBuf.append((String)record.get("HISTORYAFFILIATIONID"));				
			}			
			recordBuf.append(FIELDDELIM);
			
			if(record.get("E_ADDRESS")!=null)
			{
				recordBuf.append((String)record.get("E_ADDRESS"));
			}
			
			recordBuf.append(FIELDDELIM);
			
			if(record.get("ORCID")!=null)
			{			
				recordBuf.append((String)record.get("ORCID"));
			}
			
			recordBuf.append(FIELDDELIM);
						
			if(record.get("SRCTITLES")!=null)
			{			
				recordBuf.append((String)record.get("SRCTITLES"));
			}
			
			recordBuf.append(FIELDDELIM);
			
			recordBuf.append(this.loadNumber);
		}
		out.write(recordBuf.toString()+"\n");
	}
}