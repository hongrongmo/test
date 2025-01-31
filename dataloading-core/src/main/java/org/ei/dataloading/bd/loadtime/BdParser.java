package org.ei.dataloading.bd.loadtime;

import java.io.*;
import java.util.*;

import org.jdom2.*;                  //// replace svn jdom with recent jdom2
import org.jdom2.input.*;
import org.jdom2.output.*;
import org.ei.util.GUID;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import org.xml.sax.InputSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.ei.common.Constants;
import org.ei.common.bd.*;
import org.ei.dataloading.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.swing.text.html.parser.Element;

public class BdParser
{
	private List articles = null;
	private Document doc = null;
	private Iterator rec =null;
	private Hashtable recTable =null;
	private Perl5Util perl = new Perl5Util();
	private boolean inabstract = false;
	private HashSet entity = null;
	private static String weekNumber;
	private PrintWriter out;
	private SAXBuilder builder;
	private String accessNumber;
	private String pui;
	private String databaseName = "cpx";
	private String updatenumber;
	private String s3FileLoc = "";   //HH 04/05/2016 for Cafe
	private DataLoadDictionary dictionary = new DataLoadDictionary();
	
	private Namespace aitNamespace = Namespace.getNamespace("ait", "http://www.elsevier.com/xml/ait/dtd");
	private Namespace ceNamespace = Namespace.getNamespace("ce", "http://www.elsevier.com/xml/common/dtd");	  
	private Namespace noNamespace = Namespace.getNamespace("", "");	  
	private Namespace aniNamespace;	  
	private Namespace xmlNamespace = Namespace.getNamespace("xml", "http://www.w3.org/XML/1998/namespace");	  
	private Namespace xoeNamespace = Namespace.getNamespace("xoe", "http://www.elsevier.com/xml/xoe/dtd");	  
	private Namespace xocsNamespace = Namespace.getNamespace("xocs", "http://www.elsevier.com/xml/xocs/dtd");	  
	private Namespace aiiNamespace = Namespace.getNamespace("aii", "http://www.elsevier.com/xml/ani/internal");
	private Namespace mmlNamespace = Namespace.getNamespace("mml","http://www.w3.org/1998/Math/MathML");
	  	
	public static String accessNumberS;
	private int affid = 0;
	private static Hashtable contributorRole = new Hashtable();
	{
		contributorRole.put("auth","author");
		contributorRole.put("comp","compiler");
		contributorRole.put("edit","editor");
		contributorRole.put("illu","illustrator");
		contributorRole.put("phot","photographer");
		contributorRole.put("publ","publisher");
		contributorRole.put("revi","reviewer");
		contributorRole.put("tran","translator");
	}

	private static Hashtable descriptorsTypeTable = new Hashtable();
	{   
		descriptorsTypeTable.put("CMH","MAINHEADING"); //CPX
		descriptorsTypeTable.put("SPC","SPECIESTERM"); //GEO
		descriptorsTypeTable.put("RGI","REGIONALTERM");//GEO
		descriptorsTypeTable.put("CCV","CONTROLLEDTERM");//CPX
		descriptorsTypeTable.put("PCV","CONTROLLEDTERM");//PCH
		descriptorsTypeTable.put("GDE","CONTROLLEDTERM");//GEO
		descriptorsTypeTable.put("MED","CHEMICALTERM");//CHM
		descriptorsTypeTable.put("CTC","TREATMENTCODE");
		descriptorsTypeTable.put("CFL","UNCONTROLLEDTERM");//CPX
		descriptorsTypeTable.put("CLU","UNCONTROLLEDTERM");//CPX
		descriptorsTypeTable.put("CSX","CSXTERM");//CPX //external Compendex Standards classification terms
		descriptorsTypeTable.put("MUD","MUDTERM");//CPX //MUDTERM  added for EVOPS-1244 by hmo at 11/10/2021
		
		descriptorsTypeTable.put("CBE","CBETERM");//CBNB
		descriptorsTypeTable.put("CBB","CBBTERM");//CBNB
		descriptorsTypeTable.put("CBC","CBCTERM");//CBNB
		descriptorsTypeTable.put("CNC","CNCTERM");//CBNB
		descriptorsTypeTable.put("CBA","CBATERM");//CBNB
		
			
	}

	public BdParser()
	{
		builder = new SAXBuilder();
		builder.setExpandEntities(false);
	}

	public BdParser(Reader r)throws Exception
	{
		//super(r);

		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		builder.setFeature( "http://xml.org/sax/features/namespaces", true );
		this.doc = builder.build(r);
		Element cpxRoot = doc.getRootElement();
		List lt = cpxRoot.getChildren();
		this.articles = cpxRoot.getChildren();
		this.rec=articles.iterator();

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
		BdParser.weekNumber = weekNumber;
	}

	public String getWeekNumber()
	{
		return BdParser.weekNumber;
	}
	
	public void setUpdateNumber(String updatenumber)
	{
		this.updatenumber = updatenumber;
	}

	public String getUpdateNumber()
	{
		return this.updatenumber;
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
		BdParser.accessNumberS=accessNumber;
	}

	public String getAccessNumber()
	{
		return this.accessNumber;
	}
	
	public void setPui(String pui)
	{
		this.accessNumber = accessNumber;
	}

	public String getPui()
	{
		return this.pui;
	}
	
	//HH 04/05/2016 for Cafe
	public void setCafeS3Loc(String s3FileLocation)
	{
		this.s3FileLoc =  s3FileLocation;
	}

	public String getCafeS3Loc()
	{
		return this.s3FileLoc;
	}


	public void parseRecord(Reader r) throws Exception
	{
		//InputSource is = new InputSource(r);
		//is.setEncoding("UTF-8");
		//this.doc = builder.build(is);
		this.doc = builder.build(r);
		doc.setBaseURI(".");
		Element cpxRoot = doc.getRootElement();
		setRecordTable(getRecord(cpxRoot));
	}

	public void setRecordTable(Hashtable recTable)
	{
		this.recTable = recTable;
	}

	public Hashtable getRecordTable()
	{
		return this.recTable;
	}

	public int getRecordCount()
	{
		return articles.size();
	}
	public void close()
	{
		System.out.println("Closed");
	}

	public Hashtable getRecord(Element cpxRoot) throws Exception
	{
		Hashtable record = new Hashtable();
		Element source = null;
		String midstr= null;
		try
		{
			//reset accessnumber and PUI for every record
			setAccessNumber("");
			setPui("");
			if(cpxRoot != null)
			{
				//System.out.println("NAME= "+cpxRoot.getName());
				String namespaceURI = cpxRoot.getNamespace().getURI();
				if(namespaceURI!=null && namespaceURI.length()>0)
				{
					noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");
				}
								
				Element item = cpxRoot.getChild("item",noNamespace);

				if(item!= null)
				{					
					String eid = item.getChildText("document-id",xoeNamespace);	
					if(eid!=null)
					{
						record.put("EID",eid);
					}
					//System.out.println("EID1="+eid);
					
					Element fundingList = cpxRoot.getChild("funding-list",this.xocsNamespace);					
					Element openAccess = cpxRoot.getChild("open-access", this.xocsNamespace);
				          if (openAccess != null) {
				            if (openAccess.getChild("oa-access-effective-date", this.xocsNamespace) != null) {
				              String oaAcessEffectiveDate = openAccess.getChildText("oa-access-effective-date", this.xocsNamespace);
				              record.put("OAACCESSDATE", oaAcessEffectiveDate);
				            } 
				            if (openAccess.getChild("oa-user-license", this.xocsNamespace) != null) {
				              String oaUserLicense = openAccess.getChildText("oa-user-license", this.xocsNamespace);
				              record.put("OAUSERLICENSE", oaUserLicense);
				            } 
				            if (openAccess.getChild("oa-article-status", this.xocsNamespace) != null) {
				              Element oaArticleStatus = openAccess.getChild("oa-article-status", this.xocsNamespace);
				              String isOpenAccess = oaArticleStatus.getAttributeValue("free-to-read-status");
				              if (isOpenAccess != null)
				                record.put("ISOPENACESS", isOpenAccess); 
				            } 
				          } 
				          
					
					Element indexeddate = item.getChild("indexeddate",this.xocsNamespace);
					if(indexeddate !=null)
					{
						String epoch = indexeddate.getAttributeValue("epoch");
						record.put("EPOCH",epoch);
					}
					//System.out.println("indexeddate"+indexeddate);
					String database = getDatabaseName().toLowerCase();
					String mid = database+"_"+(new GUID()).toString();
					record.put("M_ID",mid);

				    //PUBDATE
					Element processinfo = item.getChild("process-info",aitNamespace);
					if(processinfo!=null)
					{
					    List datesort = processinfo.getChildren("date-sort",aitNamespace);
						StringBuffer yearvalue = new StringBuffer();
						for (int g = 0; g< datesort.size();g++)
						{
							Element year =(Element) datesort.get(g);
							yearvalue.append((String) year.getAttributeValue("year"));
							if(year.getAttributeValue("month") != null )
							{
							    yearvalue.append(Constants.IDDELIMITER);
							    yearvalue.append((String) year.getAttributeValue("month"));
							}
							if(year.getAttributeValue("day") != null)
							{
							    yearvalue.append(Constants.IDDELIMITER);
							    yearvalue.append((String) year.getAttributeValue("day"));
							}
						}
						if(yearvalue.length() > 0 )
						{
							record.put("DATESORT", yearvalue.toString());
						}
						
						Element status = processinfo.getChild("status",aitNamespace);
						String stage = status.getAttributeValue("stage");
						if(stage!=null && stage.length() > 0 )
						{
							record.put("STAGE", stage);
						}
						
						Element dateDelivery = processinfo.getChild("date-delivered",aitNamespace);
						if(dateDelivery!=null) {
							String dateDeliveryYear = dateDelivery.getAttributeValue("year");
							String dateDeliveryMonth = dateDelivery.getAttributeValue("month");
							String dateDeliveryDay = dateDelivery.getAttributeValue("day");
							record.put("DELIVERYDATE", dateDeliveryDay+" "+dateDeliveryMonth+" "+dateDeliveryYear);
						}
						
					}


					//bibrecord

					Element bibrecord 	= item.getChild("bibrecord",noNamespace);
					if(bibrecord==null)
					{
						System.out.println("bibrecord is null");
					}
					if(bibrecord != null)
					{
						//System.out.println("bibrecord");
						Element item_info 	= bibrecord.getChild("item-info",noNamespace);
						Element copyright   = item_info.getChild("copyright",noNamespace);

						if(copyright != null)
						{
							String right = copyright.getTextTrim();
							//System.out.println("COPYRIGHT "+right);
							if(right==null)
							{
								right = copyright.getAttributeValue("type");
								//System.out.println("COPYRIGHT_TYPE is not null "+right);
							}
							record.put("COPYRIGHT",DataLoadDictionary.mapEntity(right));
							//System.out.println("COPYRIGHT"+copyright.getTextTrim());
						}

						Element itemidlist 	= item_info.getChild("itemidlist",noNamespace);

						Element doi = itemidlist.getChild("doi", ceNamespace);
						if(doi != null)
						{
							record.put("DOI",doi.getTextTrim());
						}

						Element pii = itemidlist.getChild("pii", ceNamespace);
						if(pii != null)
						{
							record.put("PII",pii.getTextTrim());
						}
						List itemidList = itemidlist.getChildren("itemid",noNamespace);
						String  puisecondary="|";
						for(int i=0;i<itemidList.size();i++)
						{
							Element itemidElement = (Element)itemidList.get(i);
							String  itemid_idtype = itemidElement.getAttributeValue("idtype");
							String  pui=null;
							
							if(itemid_idtype != null &&	
									(itemid_idtype.equals("CPX")||
									itemid_idtype.equals("GEO")||
									itemid_idtype.equals("API")||
									itemid_idtype.equals("SNBOOK")||
									itemid_idtype.equals("APILIT")||
									itemid_idtype.equals("CBNB")||
									itemid_idtype.equals("CHEM")))
							{
								String  itemid = itemidElement.getTextTrim();

								//System.out.println("ACCESSNUMBER= "+itemid);
								
								//pre frank request, add book record as cpx database by hmo at 11/10/2016
								if((!database.equals("cpx") && !itemid_idtype.equals("CPX")) || itemid_idtype.equals("CBNB")|| ((database.equals("cpx") || database.equals("pch")) && (itemid_idtype.equals("CPX") || itemid_idtype.equals("SNBOOK"))))
								{
									if(record.get("ACCESSNUMBER")==null) //added on 10/26/2021 for preprint
									{
										record.put("ACCESSNUMBER",itemid);
										setAccessNumber(itemid);
									}
										//System.out.println("DATABSE="+database+" ACCESSNUMBER= "+itemid);
								}
								
								
							}
							else if (itemid_idtype != null && itemid_idtype.equals("PUI"))
							{
								pui = itemidElement.getTextTrim();
								setPui(pui);
								record.put("PUI",pui);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("SEC"))
							{
								pui = itemidElement.getTextTrim();
								record.put("SEC",pui);
							}
							else if (itemid_idtype != null && itemid_idtype.equalsIgnoreCase("PUISECONDARY"))
							{								
								puisecondary = puisecondary+itemidElement.getTextTrim()+"|";							
								record.put("PUISECONDARY",puisecondary);
								//System.out.println("PUISECONDARY="+puisecondary);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("NORMSTANDARDID"))
							{
								String  normstandardid = itemidElement.getTextTrim();
								record.put("NORMSTANDARDID",normstandardid);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("STANDARDDESIGNATION"))
							{
								String  standarddesignation = itemidElement.getTextTrim();
								record.put("STANDARDDESIGNATION",standarddesignation);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("STANDARDID"))
							{
								String  standardid = itemidElement.getTextTrim();
								record.put("STANDARDID",standardid);
							}
							else if(itemid_idtype != null && itemid_idtype.equals("PREPRINT")) //added on 10/26/2021 && requested for EVOPS-1233,EVOPS-936, & EVOPS-1058
							{
								String itemid = itemidElement.getTextTrim();
								record.put("ACCESSNUMBER",itemid);
								setAccessNumber(itemid);
							}
							else if(itemid_idtype != null && (itemid_idtype.equals("ARXIV") || itemid_idtype.equals("SSRN") || itemid_idtype.equals("TECHRXIV"))) //added on 11/11/2021 && requested for EVOPS-1233,EVOPS-936, & EVOPS-1058 & EVOPS-1358
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("PREPRINTIDTYPE",itemid_idtype);
								record.put("PREPRINTID",itemid);
								//setAccessNumber(itemid);
							}
							else if(itemid_idtype != null && (itemid_idtype.equals("VORDOI"))) //added on 04/25/2022 && requested for EVOPS-1301
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("VORDOI",itemid);					
							}
							else if(itemid_idtype != null && (itemid_idtype.equals("EGI"))) //added on 04/25/2022 && requested for EVOPS-1301
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("EGI",itemid);					
							}
							else if(itemid_idtype != null && (itemid_idtype.equals("SSRNVERSION"))) //added on 04/25/2022 && requested for EVOPS-1301
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("SSRNVERSION",itemid);					
							}
							
							//capture subtype pre EVOPS-1383 by hmo @12/15/2022
							else if(itemid_idtype != null && (itemid_idtype.equals("SUBTYPE"))) //added on 04/11/2022 && requested for EVOPS-1301
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("SUBTYPE",itemid);	
								//System.out.println("SUBTYPE="+itemid);
							}
							//capture CONFSERIES ID pre EVOPS-1310 by hmo @05/11/2023
							else if(itemid_idtype != null && (itemid_idtype.equals("CONFSERIES"))) 
							{							
								String itemid = itemidElement.getTextTrim();
								record.put("CONFSERIES",itemid);	
								//Fake CONFSERIES ACRONYM pre EVOPS-1310 by hmo @05/11/2023 for test only
								//record.put("CONFSERIESACRONYM",itemid.substring(10,15));	
								//System.out.println("CONFSERIESACRONYM="+record.get("CONFSERIESACRONYM"));
							}
							
						}

						//head
						Element head = bibrecord.getChild("head",noNamespace);
						if(head != null)
						{
							String docType=null;
							Element citinfo = head.getChild("citation-info",noNamespace);
							if(citinfo != null)
							{
								Element citlang = citinfo.getChild("citation-language",noNamespace);
								if(citlang != null)
								{
									Attribute lang =(Attribute) citlang.getAttribute("lang",xmlNamespace);
									if(lang != null)
									{
										record.put("CITATIONLANGUAGE",lang.getValue());
									}
								}
								Element figinfo = citinfo.getChild("figure-information",noNamespace);
								if(figinfo != null)
								{
									String fig = figinfo.getTextTrim();
									record.put("FIG", fig);
								}

								Element cittype = citinfo.getChild("citation-type",noNamespace);
								if(cittype != null)
								{
									Attribute type =(Attribute) cittype.getAttribute("code");
									if(type != null)
									{
										String cititype=type.getValue();
										
										/******Use this for new AIP LOGIC*/
										///* block for BD comfirmation
										if(record.get("STAGE")!=null && ((String)record.get("STAGE")).equals("S200"))
										{
											docType="ip";
										}
										else										
										{																			 
											docType = cititype;
										}
										//*/
										//docType = cititype;
										/*
										if(!cittypeList.contains(docType.toLowerCase()))
										{
											sendNotisfication(pui,docType);
										}
										*/
										record.put("CITTYPE",docType);
										//System.out.println("STAGE="+record.get("STAGE")+"TYPE= "+cititype+"DOCTYPE= "+docType);
									}
								}

								Element authorKeywords = citinfo.getChild("author-keywords",noNamespace);
								if(authorKeywords != null)
								{
									StringBuffer authorKeywordBuffer = new StringBuffer();
									List authorKeywordList = authorKeywords.getChildren("author-keyword",noNamespace);
									if(authorKeywordList != null)
									{
										for(int i=0;i<authorKeywordList.size();i++)
										{
											Element authorKeyword = (Element)authorKeywordList.get(i);
											String authorKeywordString = getMixData(authorKeyword.getContent());
											if(authorKeywordString != null && authorKeywordString.length()>0)
											{
												if(authorKeywordBuffer.length()>0)
												{
													authorKeywordBuffer.append(Constants.IDDELIMITER);
												}
												authorKeywordBuffer.append(DataLoadDictionary.mapEntity(authorKeywordString));
											}
										}
									}

									record.put("AUTHORKEYWORD",authorKeywordBuffer.toString().replaceAll(">\\s+<", "><"));
								}
							}//citinfo
							
							//related-item
							Element relatedItem = head.getChild("related-item",noNamespace);
							if(relatedItem!=null)
							{
								if(relatedItem.getChild("doi",ceNamespace)!=null)
								{
									Element rdoi = (Element)relatedItem.getChild("doi",ceNamespace);
									record.put("RELATEDDOI",rdoi.getTextTrim());																
								}
								
								if(relatedItem.getChild("pii",ceNamespace)!=null)
								{
									Element rpii = (Element)relatedItem.getChild("pii",ceNamespace);
									record.put("RELATEDPII",rpii.getTextTrim());																
								}
							
								List relatePUIs = relatedItem.getChildren("itemid",noNamespace);
								if(relatePUIs != null)
								{
									String relatedPUIContent="";
									for(int i=0;i<relatePUIs.size();i++)
									{
										Element relatedPUI = (Element)relatePUIs.get(i);
										if(relatedPUIContent.length()>0)
										{
											relatedPUIContent = relatedPUIContent+","+relatedPUI.getTextTrim();
										}
										else
										{
											relatedPUIContent = relatedPUI.getTextTrim();
										}
										
									}
									record.put("RELATEDPUI",relatedPUIContent);
								}
							}

							//citation title
							Element cittitle = head.getChild("citation-title",noNamespace);
							StringBuffer cbnbForeignTitle = new StringBuffer();
							if(cittitle != null)
							{
								List cittextlst = cittitle.getChildren("titletext",noNamespace);

								if(cittextlst != null)
								{
									StringBuffer cittext = new StringBuffer();

									for (int i = 0; i < cittextlst.size(); i++)
									{
										Element cittextelm = (Element)cittextlst.get(i);
										cittext.append(i);
										cittext.append(Constants.IDDELIMITER);
										if(cittextelm.getContent()!=null)
										{
											
											cittext.append(DataLoadDictionary.mapEntity(getMixData(cittextelm.getContent())));
											//cittext.append(getMixData(cittextelm.getContent()));
										}
										cittext.append(Constants.IDDELIMITER);
										if(cittextelm.getAttribute("original")!=null)
										{
											cittext.append(cittextelm.getAttributeValue("original"));
										}
										cittext.append(Constants.IDDELIMITER);
										if(cittextelm.getAttribute("lang",xmlNamespace)!=null)
										{
											//System.out.println("LANG="+cittextelm.getAttributeValue("lang",xmlNamespace));
											if(cittextelm.getAttributeValue("lang",xmlNamespace).equals("SPA"))
											{
												if(cbnbForeignTitle.length()>0)
												{
													cbnbForeignTitle.append(";");
												}
												cbnbForeignTitle.append(DataLoadDictionary.mapEntity(getMixData(cittextelm.getContent())));
												
											}
											cittext.append(cittextelm.getAttributeValue("lang",xmlNamespace));
										}
										if(i<cittextlst.size()-1)
										{
											cittext.append(Constants.AUDELIMITER);
										}
									}
									String citation = cittext.toString();
									if(this.databaseName.equalsIgnoreCase("elt"))
									{
										citation = citation.replaceAll("<inf>", "<sub>");
										citation = citation.replaceAll("</inf>", "</sub>");

									}
									
									if(cbnbForeignTitle.length()>0)
									{
										//System.out.println("****************CBNBFOREIGNTITLE="+cbnbForeignTitle.toString());
										record.put("CBNBFOREIGNTITLE",cbnbForeignTitle.toString());
									}
									record.put("CITATIONTITLE",citation.replaceAll(">\\s+<", "><"));
								}
							}

							Element abstracts = head.getChild("abstracts",noNamespace);
							
							if(abstracts!= null && abstracts.getChild("abstract",noNamespace)!=null)
							{
								
								List abstractDatas = abstracts.getChildren("abstract",noNamespace);
								for(int f=0; f<abstractDatas.size();f++)
								{
									String abstractCopyRight=null;
									String abstractPerspective=null;
									String abstractlanguage=null;
									
									Element abstractData = (Element)abstractDatas.get(f);
									if(	abstractData.getChild("publishercopyright",noNamespace)!=null)
									{							
									 	abstractCopyRight= DataLoadDictionary.mapEntity(abstractData.getChildTextTrim("publishercopyright",noNamespace));
										//System.out.println("COPYRIGHT="+ abstractCopyRight);
									}
	
									// abstract data									
									
									if(abstractData.getAttributeValue("lang",xmlNamespace) != null)
									{
										//System.out.println("ABSTRACT_Language "+abstractData.getAttributeValue("lang",xmlNamespace));
										abstractlanguage=abstractData.getAttributeValue("lang",xmlNamespace);
									}
									
									if(abstractData.getAttributeValue("original") != null)
									{
										//System.out.println("ABSTRACT_original "+abstractData.getAttributeValue("original"));
										record.put("ABSTRACTORIGINAL", abstractData.getAttributeValue("original"));
									}
	
									if(abstractData.getAttributeValue("perspective") != null)
									{
										//System.out.println("ABSTRACTPERSPECTIVE "+abstractData.getAttributeValue("perspective"));
										abstractPerspective = abstractData.getAttributeValue("perspective");
										record.put("ABSTRACTPERSPECTIVE", abstractPerspective);
									}
	
									//if(abstractData.getChildTextTrim("para",ceNamespace) != null)
									if(abstractData.getChildren("para",ceNamespace) != null)
									{
										
										//String abstractString = dictionary.mapEntity(getMixData(abstractData.getChild("para",ceNamespace).getContent()));															
									
										String 	abstractString = DataLoadDictionary.mapEntity(getAbstractMixData(abstractData.getChildren("para",ceNamespace)));
										if(abstractlanguage==null || abstractlanguage.equalsIgnoreCase("eng"))
										{
											abstractString = "<div data-language=\"eng\" data-ev-field=\"abstract\">"+abstractString+"</div>";
										}
										else
										{
											abstractString = "<div data-language=\""+abstractlanguage+"\" data-ev-field=\"abstract\">"+abstractString+"</div>";
										}
																				
										//System.out.println("ABSTRACT2="+ abstractString);
										if(this.databaseName.equalsIgnoreCase("elt"))
										{
											abstractString = abstractString.replaceAll("<inf>", "<sub>");
											abstractString = abstractString.replaceAll("</inf>", "</sub>");											
										}
	
										if(abstractCopyRight!=null)
										{
											abstractString = abstractString+" "+abstractCopyRight;
										}
										
										//abstractString = abstractString.replaceAll("&#55349;&#56476;","&#119964;");//need to fix this bug later hmo@10/26/2016
										if(docType!=null && docType.equals("st") && abstractPerspective!=null && abstractPerspective.equalsIgnoreCase("EDITED"))
										{
											record.put("ABSTRACTDATA", abstractString.replaceAll(">\\s+<", "><"));
											break;
										}
										else 
										{
											record.put("ABSTRACTDATA", abstractString.replaceAll(">\\s+<", "><"));
										}
										
									}
									
								}

							}

							//author-group conatins (redundunt authors - removing redundunt authors;

							List authorgroup = head.getChildren("author-group",noNamespace);
							BdAuthors ausmap = new BdAuthors();
							BdAffiliations affmap = new BdAffiliations();
							BdAuthors collaborationMap = new BdAuthors();
							BdAffiliations collaborationAffMap = new BdAffiliations();
							this.affid = 0;
							for (int e=0 ; e < authorgroup.size() ; e++)
							{
								Element agroup= (Element)authorgroup.get(e);
								if(agroup.getChild("collaboration",noNamespace)!=null)
								{
									setCollaborationAndAffs( collaborationMap,
															 collaborationAffMap,
															 (Element) authorgroup.get(e));
								}
								else
								{
									setAuthorAndAffs( ausmap,
													affmap,
													(Element) authorgroup.get(e));
								}
							}
							StringBuffer secondAuGroup = new StringBuffer();
							StringBuffer secondAffGroup = new StringBuffer();
							
							record.put("COLLABORATION", auToStringBuffer(collaborationMap));	
							record.put("COLLABORATION_AFF", auffToStringBuffer(collaborationAffMap, secondAffGroup));
							record.put("AUTHOR", auToStringBuffer(ausmap, secondAuGroup));
							if(secondAuGroup.length() > 0)
							{
								record.put("AUTHOR_1",secondAuGroup.toString());
							}
							

							record.put("AFFILIATION", auffToStringBuffer(affmap, secondAffGroup).toString());

							if(secondAffGroup.length() > 0)
							{
								record.put("AFFILIATION_1",secondAffGroup.toString());
							}

							//enhancement

							Element enhancement = head.getChild("enhancement",noNamespace);

							if(enhancement != null)
							{
									Element descriptorgroup = enhancement.getChild("descriptorgroup",noNamespace);
									parseDescriptorgroup(descriptorgroup,record);

									Element classificationgroup = enhancement.getChild("classificationgroup",noNamespace);
									parseClassificationgroup(classificationgroup,record);

									Element manufacturergroup = enhancement.getChild("manufacturergroup",noNamespace);
									parseManufacturergroup(manufacturergroup,record);

									Element tradenamegroup = enhancement.getChild("tradenamegroup",noNamespace);
									parseTradenamegroup(tradenamegroup,record);

									Element sequencebanks = enhancement.getChild("sequencebanks",noNamespace);
									parseSequencebanks(sequencebanks,record);

									Element chemicalgroup = enhancement.getChild("chemicalgroup",noNamespace);
									parseChemicalgroup(chemicalgroup,record);

									Element apidescriptorgroup = enhancement.getChild("API-descriptorgroup",noNamespace);
									if(apidescriptorgroup!=null)
									{

										Element autoposting = apidescriptorgroup.getChild("autoposting",noNamespace);
								        //APICC
										if(autoposting != null)
										{

											Element classificationdescription = autoposting.getChild("API-CC", noNamespace);

											if(classificationdescription != null)
											{

												List classdescgroup = classificationdescription.getChildren("classification", noNamespace);
												StringBuffer apiccterms = new StringBuffer();
												if(classdescgroup != null)
												{
													//System.out.println("ENTER classdescgroup");
													for (int j = 0; j < classdescgroup.size(); j++)
													{
														Element ell = (Element)classdescgroup.get(j);
														List apicc = ell.getChildren("classification-description",noNamespace);

														if(apicc != null)
														{
															for (int i = 0; i < apicc.size(); i++)
															{
																Element el = (Element)apicc.get(i);
																if(el!=null)
																{
																	apiccterms.append(DataLoadDictionary.mapEntity((String)el.getTextTrim()));
																}
																if(i<(apicc.size()-1) )
																{
																	apiccterms.append(Constants.AUDELIMITER);
																}
																
															}
														}
														if(j<(classdescgroup.size()-1) )
														{
															apiccterms.append(Constants.AUDELIMITER);
														}
													}
												}

												if(apiccterms != null && apiccterms.length()> 0)
												{
													String apiccstr = apiccterms.toString();
													record.put("CLASSIFICATIONDESC",apiccstr);
												}
											}


									//APICT
									// exclude CRN from API-CT

										Element apicttop = autoposting.getChild("API-CT",noNamespace);
										if(apicttop != null)
										{
											List ctTerms = apicttop.getChildren("autoposting-term",noNamespace);

											StringBuffer apict = new StringBuffer();
											StringBuffer apictextended = new StringBuffer();

											for (int j = 0; j < ctTerms.size(); j++)
											{
												Element el = (Element)ctTerms.get(j);
												boolean isCRN = false;


												if(el.getAttribute("CAS-nr")!= null  &&
														(el.getAttributeValue("CAS-nr").equals("y")||
														 el.getAttributeValue("CAS-nr").equals("b")))
												{
													isCRN = true;
												}
												if(!isCRN)
												{
													StringBuffer termbuf = new StringBuffer();
													String pref = (String)el.getAttributeValue("prefix");
													String postf = (String)el.getAttributeValue("postfix");
													String term = (String)el.getTextTrim();
													if ( pref != null && pref.length() > 0)
													{
														termbuf.append(DataLoadDictionary.mapEntity(pref)).append("-");
													}if(term!=null)
													{
														termbuf.append(DataLoadDictionary.mapEntity(term));
													}
													if ( postf != null && postf.length() > 0)
													{
														termbuf.append("-").append(DataLoadDictionary.mapEntity(postf));
													}
													if(apict.length() < 3000)
													{
														apict.append(termbuf.toString()).append(Constants.IDDELIMITER);
													}
													else if(apict.length() >= 3000)
													{
														apictextended.append(termbuf.toString()).append(Constants.IDDELIMITER);
													}
												}
											}
											if(apict != null &&
													apict.toString().length() > 0)
											{

												record.put("APICT",apict.toString());
											}

											if(apictextended != null &&
													apictextended.toString().length()> 0)
											{
												record.put("APICT1",apictextended.toString());
											}

										}

										Element apilt = autoposting.getChild("API-LT",noNamespace);
										StringBuffer apiterms = new StringBuffer();
										StringBuffer apigroups = new StringBuffer();
										StringBuffer apiterms1 = new StringBuffer();
										StringBuffer apigroups1 = new StringBuffer();
										if(apilt != null)
										{
											List apiltgroup = apilt.getChildren("API-LT-group",noNamespace);
											for (int i = 0; i < apiltgroup.size(); i++)
											{
												Element ltgroup =(Element) apiltgroup.get(i);
												List apilttop = ltgroup.getChildren("autoposting-term",noNamespace);
												apiterms = new StringBuffer();
												if(apilttop != null)
												{
														for (int j = 0; j < apilttop.size(); j++)
														{
															Element el = (Element)apilttop.get(j);
															boolean isCRN = false;
															if(el.getAttribute("CAS-nr")!= null  &&
																	(el.getAttributeValue("CAS-nr").equals("y")||
																	 el.getAttributeValue("CAS-nr").equals("b")))
															{
																isCRN = true;
															}
															if(!isCRN)
															{

																StringBuffer termbuf = new StringBuffer();
																String pref = (String)el.getAttributeValue("prefix");
																String postf = (String)el.getAttributeValue("postfix");
																String term = (String)el.getTextTrim();
																if ( pref != null && pref.length() > 0)
																{
																	termbuf.append(DataLoadDictionary.mapEntity(pref)).append("-");
																}
																if(term!=null)
																{
																	termbuf.append(DataLoadDictionary.mapEntity(term));
																}
																if ( postf != null && postf.length() > 0)
																{
																	termbuf.append("-").append(DataLoadDictionary.mapEntity(postf));
																}
																if(apiterms.length() < 3000)
																{

																	apiterms.append(termbuf.toString()).append(Constants.IDDELIMITER);
																}
																else if(apiterms.length() >= 3000)
																{
																	apiterms1.append(termbuf.toString()).append(Constants.IDDELIMITER);
																}
															}
														}

												}
												if(apiterms != null && apiterms.length()>0)
												{
													apigroups.append(apiterms);
													apigroups.append(Constants.GROUPDELIMITER);
												}
												if(apiterms1 != null && apiterms1.length()>0)
												{
													apigroups1.append(apiterms1);
													apigroups1.append(Constants.GROUPDELIMITER);
												}
											}
											// end of groups
											if(apiterms != null && apiterms.length()>0)
											{
												record.put("APILT",apigroups.toString());
											}
											if(apiterms1 != null && apiterms1.length()>0)
											{
												record.put("APILT1",apiterms1.toString());
											}

										}
										Element apiams = autoposting.getChild("API-AMS",noNamespace);

										if(apiams != null)
										{
											StringBuffer apiamsbuf = new StringBuffer();
											if(apiams.getChild("API-term",noNamespace)!= null)
											{
												Element ams = apiams.getChild("API-term",noNamespace);
												apiamsbuf.append(DataLoadDictionary.mapEntity(ams.getTextTrim()));
											}
											record.put("APIAMS",apiamsbuf.toString());
										}

										//API-APC field

										Element apiapc = autoposting.getChild("API-APC", noNamespace);

										if(apiapc != null)
										{
											StringBuffer apiapcbuf = new StringBuffer();
											if(apiapc.getChildren("API-term", noNamespace)!= null)
											{
												List l = apiapc.getChildren("API-term", noNamespace);
												for (int i = 0; i < l.size(); i++)
												{
													Element el = (Element)l.get(i);
													if(el != null)
													{
														apiapcbuf.append(DataLoadDictionary.mapEntity(el.getTextTrim()));
													}
													apiapcbuf.append(Constants.IDDELIMITER);
												}

											}
											if(apiapcbuf != null && apiapcbuf.length()> 0)
											{
												record.put("APIAPC", apiapcbuf.toString());
											}
										}


										//API-CRN field

										Element apicrn = autoposting.getChild("API-CRN", noNamespace);

										if(apicrn != null)
										{
											StringBuffer apicrnbuf = new StringBuffer();
											if(apicrn.getChildren("autoposting-term", noNamespace)!= null)
											{
												List l = apicrn.getChildren("autoposting-term", noNamespace);

												for (int i = 0; i < l.size(); i++)
												{
													Element el = (Element)l.get(i);
													if(el.getAttributeValue("CAS-nr") != null)
													{
														String carnr =(String) el.getAttributeValue("CAS-nr");
														apicrnbuf.append(carnr);

													}
													apicrnbuf.append(Constants.AUDELIMITER);
													if(el.getAttributeValue("postfix") != null)
													{
														String carnr =(String) el.getAttributeValue("postfix");
														apicrnbuf.append(carnr);

													}
													apicrnbuf.append(Constants.AUDELIMITER);
													apicrnbuf.append(DataLoadDictionary.mapEntity(el.getTextTrim()));
													apicrnbuf.append(Constants.IDDELIMITER);
												}
											}
											if(apicrnbuf != null && apicrnbuf.length()> 0)
											{
												record.put("CASREGISTRYNUMBER", apicrnbuf.toString());
											}
										}

										Element apialc = autoposting.getChild("API-ALC",noNamespace);
										if(apialc != null)
										{
											StringBuffer apialcbuf = new StringBuffer();
											if(apialc.getChild("LTM-COUNT",noNamespace)!= null)
											{
												Element ltmcount = apialc.getChild("LTM-COUNT",noNamespace);
												apialcbuf.append(ltmcount.getTextTrim());

											}
											apialcbuf.append(Constants.IDDELIMITER);
											if(apialc.getChild("LT-COUNT", noNamespace) != null)
											{
												Element ltcount = apialc.getChild("LT-COUNT", noNamespace);
												apialcbuf.append(ltcount.getTextTrim());
											}
											if(apialc.getChild("LTM-COUNT",noNamespace)!= null  ||
													apialc.getChild("LT-COUNT", noNamespace) != null)
											{
												record.put("APIALC",apialcbuf.toString());
											}
										}
										//APIATM

										if(autoposting.getChild("API-ATM-group",noNamespace) != null)
										{
											ApiAtm apiatm = new ApiAtm(autoposting.getChild("API-ATM-group",noNamespace));
											record.put("APIATM",apiatm.toAPIString());
										}

										//APIAT

										Element apiat = autoposting.getChild("API-AT",noNamespace);
										if(apiat != null)
										{
											StringBuffer termsbuf = new StringBuffer();
											List apiltmtop = apiat.getChildren("autoposting-term",noNamespace);
											for (int i = 0; i < apiltmtop.size(); i++)
											{
												Element el =(Element) apiltmtop.get(i);
												StringBuffer buf= new StringBuffer();
												String pref = (String)el.getAttributeValue("prefix");
												String postf = (String)el.getAttributeValue("postfix");
												String term = (String)el.getTextTrim();
												if ( pref != null && pref.length() > 0)
												{
													buf.append(pref).append("-");
												}
												buf.append(term);
												if ( postf != null && postf.length() > 0)
												{
													buf.append("-").append(postf);
												}

												termsbuf.append(buf.toString()).append(Constants.IDDELIMITER);

											}

											//end of groups
											record.put("APIAT",termsbuf.toString());
										}


										Element apiltm = autoposting.getChild("API-LTM",noNamespace);

										if(apiltm != null)
										{
											StringBuffer apimterms = new StringBuffer();
											StringBuffer apimgroups = new StringBuffer();
											List apiltmgroup = apiltm.getChildren("API-LTM-group",noNamespace);
											for (int i = 0; i < apiltmgroup.size(); i++)
											{
												Element egroup =(Element) apiltmgroup.get(i);
												List apiltmtop = egroup.getChildren("autoposting-term",noNamespace);
												apimterms = new StringBuffer();

												if(apiltmtop != null)
												{
														for (int j = 0; j < apiltmtop.size(); j++)
														{
															Element el = (Element)apiltmtop.get(j);

															StringBuffer termbuf = new StringBuffer();
															String pref = (String)el.getAttributeValue("prefix");
															String postf = (String)el.getAttributeValue("postfix");
															String term = (String)el.getTextTrim();
															if ( pref != null && pref.length() > 0)
															{
																termbuf.append(pref).append("-");
															}
															termbuf.append(term);
															if ( postf != null && postf.length() > 0)
															{
																termbuf.append("-").append(postf);
															}

															apimterms.append(termbuf.toString()).append(Constants.IDDELIMITER);
														}

												}
												apimgroups.append(apimterms);
												apimgroups.append(Constants.GROUPDELIMITER);
											}
											//end of groups
											record.put("APILTM",apimgroups.toString());
										}
									}
								}

							}

							//SOURCE SOURCETYPE SOURCECOUNTRY SOURCEID

							source =(Element) head.getChild("source",noNamespace);
							parseSourceElement(source,record,bibrecord);

							//CORRESPONDENCE
							//Element correspondence = (Element) head.getChild("correspondence",noNamespace);
							//change to get multiple correspondence
							List correspondences = head.getChildren("correspondence",noNamespace);
							parseCorrespondenceElement(correspondences,record);
							
							//GRANTLIST
							Element grantlist = (Element) head.getChild("grantlist",noNamespace);
							StringBuffer grantBuffer = new StringBuffer();
							//String fundingText = null;
							if(fundingList !=null)
							{
								List fundinggroup = fundingList.getChildren("funding",xocsNamespace);
								for (int i = 0; i < fundinggroup.size(); i++)
								{
									Element funding =(Element) fundinggroup.get(i);
									if(funding.getChildText("funding-id",xocsNamespace)!=null)
									{
										List fundingIDgroup = funding.getChildren("funding-id",xocsNamespace);
										StringBuffer fundingIDbuffer = new StringBuffer();
										for(int j=0;j<fundingIDgroup.size();j++)
										{
											Element fundingid =(Element) fundingIDgroup.get(j);
											fundingIDbuffer.append(fundingid.getTextTrim());
											if(j<fundingIDgroup.size()-1)
											{
												fundingIDbuffer.append(",");
											}
										}
										grantBuffer.append(fundingIDbuffer.toString());
										//System.out.println("FUNDINGID="+fundingIDbuffer.toString());
									}
										
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-acronym",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-acronym",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-id",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-id",xocsNamespace)));
									}
									else if(funding.getChildText("funding-agency-ids",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-ids",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-country",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-country",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-matched-string",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-country",xocsNamespace)));
									}
																		 
									if(i<fundinggroup.size()-1)
									{
										grantBuffer.append(Constants.AUDELIMITER);
									}	
								}
								
								if(grantBuffer.length()>0)
								{
									record.put("GRANTLIST",grantBuffer.toString());
									//System.out.println(eid+" get funding from funding-list");
								}
																
								if(fundingList.getChild("funding-text",xocsNamespace)!=null)
								{
									List fundingTextList =  fundingList.getChildren("funding-text",xocsNamespace);
									StringBuffer fundingTextBuffer = new StringBuffer();
									for (int j = 0; j < fundingTextList.size(); j++)
									{
										Element fundingText =(Element) fundingTextList.get(j);
										fundingTextBuffer.append(DataLoadDictionary.mapEntity(fundingText.getTextTrim()));
										if(j<fundingTextList.size()-1)
										{
											fundingTextBuffer.append(Constants.AUDELIMITER);
										}
									}
									//System.out.println("GRANTtext from funding="+fundingTextBuffer.toString());
									record.put("GRANTTEXT",fundingTextBuffer.toString());
									//System.out.println(eid+" get fundingText from funding-list");
								}
								
							}
							
							if(grantlist!=null && grantBuffer.length()==0)
							{
								List grantgroup = grantlist.getChildren("grant",noNamespace);
								for (int i = 0; i < grantgroup.size(); i++)
								{
									Element grant =(Element) grantgroup.get(i);
									if(grant.getChildText("grant-id",noNamespace)!=null)
									{
										grantBuffer.append(grant.getChildText("grant-id",noNamespace));
									}
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(grant.getChildText("grant-acronym",noNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(grant.getChildText("grant-acronym",noNamespace)));
									}
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(grant.getChildText("grant-agency",noNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(grant.getChildText("grant-agency",noNamespace)));
									}
									if(i<grantgroup.size()-1)
									{
										grantBuffer.append(Constants.AUDELIMITER);
									}									
								}
								if(grantBuffer.length()>0)
								{
									record.put("GRANTLIST",grantBuffer.toString());
									//System.out.println(eid+" get funding from grantlist");
								}
								
								//added by hmo on 7/17/2017
								//modify by hmo on 02/08/2019
								if(record.get("GRANTTEXT")==null && grantlist.getChild("grant-text",noNamespace)!=null)
								{
									String grantText =  grantlist.getChildText("grant-text",noNamespace);
									grantText =  DataLoadDictionary.mapEntity(grantText);
									if(grantText.length()>3900)
									{
										grantText = grantText.substring(0,3900);
									}
									record.put("GRANTTEXT",grantText);
								}
								
							}


						}

						Element tail = bibrecord.getChild("tail",noNamespace);
						if(tail != null)
						{
							Element bibliography = tail.getChild("bibliography",noNamespace);
							if(bibliography != null && bibliography.getAttributeValue("refcount")!=null)
							{
								record.put("REFCOUNT",(String)bibliography.getAttributeValue("refcount"));

								List referencegroup = bibliography.getChildren("reference", noNamespace);
								if(referencegroup!=null)
								{
									parseReferencegroup(referencegroup,record);
								}
							}

						}						

						// only for elt database conversion
						record.put("DATABASE",databaseName.trim());
						
						//HH 04/05/2016 for Cafe
						record.put("UPDATERESOURCE", s3FileLoc);
					}
					
					//UpdateNumber
					record.put("UPDATENUMBER",this.updatenumber);					

					if(item.getChild("loadnumber", noNamespace) != null)
					{
						record.put("LOADNUMBER",item.getChild("loadnumber", noNamespace).getText());
					}
					else
					{
						record.put("LOADNUMBER",weekNumber);
						//System.out.println("LOADNUMBER="+weekNumber);
					}

                   //record.put("LOADNUMBER", item.getChildText("load-number", noNamespace));

					Element additionalsrcinfo = null;
					if( source != null)
					{
						additionalsrcinfo = source.getChild("additional-srcinfo",noNamespace);
					}
					Element additionalsrcinfosecjournal = null;
					if(additionalsrcinfo != null)
					{
						additionalsrcinfosecjournal = additionalsrcinfo.getChild("additional-srcinfo-secjournal",noNamespace);
					}

					Element secondaryjournal = null;
					if(additionalsrcinfosecjournal != null)
					{
						secondaryjournal = additionalsrcinfosecjournal.getChild("secondaryjournal",noNamespace);
					}

                    if (secondaryjournal != null)
                    {
                		if(secondaryjournal.getChild("sourcetitle", noNamespace) != null)
    					{
                			record.put("SECSTI", secondaryjournal.getChild("sourcetitle", noNamespace).getText());
    					}
                		if(secondaryjournal.getChild("issn", noNamespace) != null)
    					{
                			record.put("SECISS", secondaryjournal.getChild("issn", noNamespace).getText());
    					}

                        Element voliss = secondaryjournal.getChild("voliss",noNamespace);
                        if(voliss != null && voliss.getAttributeValue("volume")!=null)
				        {
					        record.put("SECVOLUME",(String)voliss.getAttributeValue("volume"));
					    }
                        if(voliss != null && voliss.getAttributeValue("issue")!=null)
						{
					        record.put("SECISSUE",(String)voliss.getAttributeValue("issue"));
						}

                        Element secpublicationdate = secondaryjournal.getChild("publicationdate",noNamespace);

                        if(secpublicationdate != null)
                        {
                            StringBuffer secpubdate = new StringBuffer();

                            if(secpublicationdate.getChild("year", noNamespace) != null)
                            {
                                  secpubdate.append(secpublicationdate.getChild("year", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("month", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("month", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("day", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("day", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("date-text", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("date-text", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            record.put("SECPUBDATE", secpubdate.toString());
                        }
                     }
				}

			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return record;
	}

	private void parseReferencegroup(List referenceGroup,Hashtable record) throws Exception
	{

		int referenceID = 0;

		Hashtable referenceTitle = new Hashtable();
		Hashtable referenceAuthor = new Hashtable();
		Hashtable referenceSourcetitle = new Hashtable();
		Hashtable referencePublicationyear = new Hashtable();
		Hashtable referenceVolume = new Hashtable();
		Hashtable referenceIssue = new Hashtable();
		Hashtable referencePages = new Hashtable();
		Hashtable referenceFullText = new Hashtable();
		Hashtable referenceText = new Hashtable();
		Hashtable referenceWebsite = new Hashtable();
		Hashtable referenceItemid = new Hashtable();
		Hashtable referenceItemDOI = new Hashtable();
		Hashtable referenceItemcitationPII = new Hashtable();
		Hashtable referenceItemcitationEID = new Hashtable();
		Hashtable referenceItemcitationDOI = new Hashtable();
		Hashtable referenceItemcitationID = new Hashtable();
		Hashtable referenceItemcitationCitationTitle = new Hashtable();
		Hashtable referenceItemcitationAuthor = new Hashtable();
		Hashtable referenceItemcitationAffiliation = new Hashtable();
		Hashtable referenceItemcitationSourcetitle = new Hashtable();
		Hashtable referenceItemcitationSourcetitle_abbrev = new Hashtable();
		Hashtable referenceItemcitationISSN = new Hashtable();
		Hashtable referenceItemcitationISBN = new Hashtable();
		Hashtable referenceItemcitationCoden = new Hashtable();
		Hashtable referenceItemcitationPart = new Hashtable();
		Hashtable referenceItemcitationPublicationyear = new Hashtable();
		Hashtable referenceItemcitationVolume = new Hashtable();
		Hashtable referenceItemcitationIssue = new Hashtable();
		Hashtable referenceItemcitationPage = new Hashtable();
		Hashtable referenceItemcitationArticleNumber = new Hashtable();
		Hashtable referenceItemcitationWebsite = new Hashtable();
		Hashtable referenceItemcitationEAddress = new Hashtable();
		Hashtable referenceItemcitationRefText = new Hashtable();
		Hashtable referenceSourceText = new Hashtable();
		Hashtable referenceItemcitationDatabase = new Hashtable();

		if(referenceGroup != null && referenceGroup.size()>0)
		{
			for(int i=0;i<referenceGroup.size();i++)
			{
				Element reference = (Element)referenceGroup.get(i);

				if(reference != null)
				{
					if(reference.getAttributeValue("id")!=null)
					{
						referenceID = Integer.parseInt(reference.getAttributeValue("id"));
					}
					else
					{									
						referenceID = referenceID+1;						
					}

					Element refInfo = (Element) reference.getChild("ref-info",noNamespace);
					Element refTitle = (Element) refInfo.getChild("ref-title",noNamespace);
					if(refTitle!=null)
					{
						List refTitletextList = (List) refTitle.getChildren("ref-titletext",noNamespace);
						StringBuffer referenceTitleBuffer = new StringBuffer();
						for(int j=0;j<refTitletextList.size();j++)
						{
							Element refTitletextElement = (Element)refTitletextList.get(j);
							if(refTitletextElement!=null)
							{
								String  refTitletext = DataLoadDictionary.mapEntity(getMixData(refTitletextElement.getContent()));
								//System.out.println("REFTITLE="+refTitletext);
								if(	refTitletext!=null)
								{
									//change to deal with non-roman characters @08/10/2018
									//referenceTitleBuffer.append(dictionary.mapEntity(refTitletext));
									referenceTitleBuffer.append(refTitletext);
								}
								if(j<refTitletextList.size()-1)
								{
									referenceTitleBuffer.append(", ");
								}
							}
						}
						referenceTitle.put(referenceID,	referenceTitleBuffer.toString());
						//System.out.println("referenceTitle="+referenceTitleBuffer.toString());
					}



					List refAuthorsList = (List) refInfo.getChildren("ref-authors",noNamespace);
					BdAuthors ausmap = new BdAuthors();
					BdAffiliations affmap = new BdAffiliations();
					this.affid = 0;
					if(refAuthorsList!=null && refAuthorsList.size()>0)
					{

						for (int j=0 ; j < refAuthorsList.size() ; j++)
						{
							setAuthorAndAffs( ausmap,
											  affmap,
											  (Element) refAuthorsList.get(j));
						}

						referenceAuthor.put(referenceID,  auToStringBuffer(ausmap));
					}



					//ref-sourcetitle
					Element refSourceTitle = (Element) refInfo.getChild("ref-sourcetitle",noNamespace);
					if(	refSourceTitle !=null)
					{
						String  refSourceTitleText = DataLoadDictionary.mapEntity(getMixData(refSourceTitle.getContent()));
						//change to deal with non-roman characters @08/10/2018
						//referenceSourcetitle.put(referenceID,dictionary.mapEntity(refSourceTitleText));
						referenceSourcetitle.put(referenceID,refSourceTitleText);
					}

					//ref-publicationyear
					Element refPublicationyear = (Element) refInfo.getChild("ref-publicationyear",noNamespace);
					if(refPublicationyear != null)
					{
						String publicationyear = getPublicationYear(refPublicationyear);
						referencePublicationyear.put(referenceID,publicationyear);
					}

					//ref-volisspag
					Element refVolisspag = (Element)refInfo.getChild("ref-volisspag",noNamespace);
					if(refVolisspag != null)
					{
						//VOLUME, ISSUE,

						Element voliss = (Element) refVolisspag.getChild("voliss",noNamespace);
						if(voliss != null)
						{
							String volume = voliss.getAttributeValue("volume");
							String issue  = voliss.getAttributeValue("issue");

							if (volume != null)
							{

								referenceVolume.put(referenceID,volume);
							}
							if(issue != null)
							{
								referenceIssue.put(referenceID,issue);
							}
						}

						//PAGERANGE PAGE
						String pages = getPages(refVolisspag);
						if(pages!=null && pages.length()>0)
						{
							referencePages.put(referenceID,pages);
						}

					}


					//ref-website
					Element refWebsite = (Element) refInfo.getChild("ref-website",noNamespace);
					if(refWebsite!=null)
					{
						Element websitename = (Element) refWebsite.getChild("websitename",noNamespace);
						StringBuffer  referenceWebsiteBuffer = new StringBuffer();
						if(websitename!=null)
						{
							String websitenameText = websitename.getTextTrim();
							referenceWebsiteBuffer.append(websitenameText);
						}
						referenceWebsiteBuffer.append(Constants.IDDELIMITER);
						Element eaddress = (Element) refWebsite.getChild("e-address",ceNamespace);
						if(eaddress!=null)
						{
							String eaddresstext = eaddress.getText();
							if(eaddresstext != null)
							{
								referenceWebsiteBuffer.append(eaddresstext);
							}
						}
						referenceWebsite.put(referenceID,referenceWebsiteBuffer.toString());
					}

					//ref-text
					Element refText = (Element) refInfo.getChild("ref-text",noNamespace);
					if(refText!=null)
					{

						String  refTextValue = DataLoadDictionary.mapEntity(getMixData(refText.getContent()));
						
						//change to deal with non-roman characters @08/10/2018
						
						//referenceText.put(referenceID,trimStringToLength(refTextValue,3950));
						referenceText.put(referenceID,refTextValue);
						if(refTextValue.length()>3950)
						{
							System.out.println("record "+getAccessNumber()+" ref-text oversize::"+refTextValue.length());
						}
					}

					//refd-itemidlist
					Element refdItemidlist = (Element) refInfo.getChild("refd-itemidlist",noNamespace);
					if(refdItemidlist!=null)
					{
						List itemidList = refdItemidlist.getChildren("itemid",noNamespace);
						if(itemidList!=null)
						{
							String itemid = getRefItemID(itemidList);
							referenceItemid.put(referenceID,itemid);
							
							String itemdoi=getItemDoi(itemidList);
							if(itemdoi!=null)
							{
								referenceItemDOI.put(referenceID,itemdoi);
							}
						}
					}


					//ref-fulltext
					Element refFullText = (Element) reference.getChild("ref-fulltext",noNamespace);
					if(refFullText!=null)
					{
						String  refFullTextValue = refFullText.getTextTrim();
						//System.out.println("ref-fulltext1::"+refFullTextValue);
						//change to deal with non-roman characters @08/10/2018
						//referenceText.put(referenceID,dictionary.mapEntity(refFullTextValue));						
						//referenceFullText.put(referenceID,refFullTextValue);
						if(refFullTextValue.length()>3500)
						{
							System.out.println("record "+getAccessNumber()+" ref-fulltext oversize::"+refFullTextValue.length());
						}
						referenceFullText.put(referenceID,trimStringToLength(refFullTextValue,3500));
					}
					
					
					
					//refd-itemcitation
					Element refdItemcitation = (Element) reference.getChild("refd-itemcitation",noNamespace);
					if(refdItemcitation!=null)
					{
						//REFERENCE CITATION EID
						if(refdItemcitation.getChildText("eid",noNamespace)!=null)
						{
							String refdItemcitationEID = refdItemcitation.getChildText("eid",noNamespace);
							referenceItemcitationEID.put(referenceID, refdItemcitationEID);
							//System.out.println("referenceItemcitationEID "+refdItemcitationEID);
						}
						
						//REFERENCE CITATION ID
						List itemidList = refdItemcitation.getChildren("itemid",noNamespace);
						if(itemidList!=null)
						{
							String itemid = getRefItemID(itemidList);
							//System.out.println("CITATIONITEM_CPX_ID="+itemid);
							referenceItemcitationID.put(referenceID,itemid);
						}						
						
						//REFERENCE CITATION DATABASE
						Element refdItemcitationDatabase = (Element) refdItemcitation.getChild("databases",noNamespace);
						
						if(refdItemcitationDatabase!=null)
						{							
							List itemdatabaseList = refdItemcitationDatabase.getChildren("dbcollection",noNamespace);
							StringBuffer referenceItemdatabase = new StringBuffer();
							if(itemdatabaseList!=null)
							{								
								for(int j=0;j<itemdatabaseList.size();j++)
								{
									Element itemdatabaseElement = (Element)itemdatabaseList.get(j);									
									String citationitemdatabase = itemdatabaseElement.getTextTrim();
									referenceItemdatabase.append(citationitemdatabase);
									if(j<itemdatabaseList.size()-1)
									{
										referenceItemdatabase.append(",");
									}
								}								
								referenceItemcitationDatabase.put(referenceID,referenceItemdatabase.toString());
							}
						}
						
						//REFERENCE CITATION PII
						if(refdItemcitation.getChild("pii",ceNamespace)!=null)
						{
							Element pii = (Element)refdItemcitation.getChild("pii",ceNamespace);
							referenceItemcitationPII.put(referenceID,pii.getTextTrim());
							//System.out.println("referenceItemcitation PII "+pii.getTextTrim());
						}

						//REFERENCE CITATION DOI
						if(refdItemcitation.getChild("doi",ceNamespace)!=null)
						{
							Element doi = (Element)refdItemcitation.getChild("doi",ceNamespace);
							referenceItemcitationDOI.put(referenceID,doi.getTextTrim());
							//System.out.println("referenceItemcitation doi "+doi.getTextTrim());
						}

						//REFERENCE CITATION CITATIONTITLE
						if(refdItemcitation.getChild("citation-title",noNamespace)!=null)
						{
							Element citationTitles = (Element)refdItemcitation.getChild("citation-title",noNamespace);
							String citation = getCitationTitle(citationTitles);
							referenceItemcitationCitationTitle.put(referenceID,citation);
							//System.out.println("referenceItemcitation CITATIONTITLE "+citation);
							//fill the referencetitle with citation title when there is no reference title
							if(referenceTitle.get(referenceID)==null)
							{
								//System.out.println("REFTITLE="+citationTitles.getChildText("titletext"));
								referenceTitle.put(referenceID, citationTitles.getChildText("titletext"));
							}

						}

						//REFERENCE CITATION AUTHOR
						if(refdItemcitation.getChild("author-group",noNamespace)!=null)
						{
							List refAuthorgroup = refdItemcitation.getChildren("author-group",noNamespace);
							BdAuthors refAusmap = new BdAuthors();
							BdAffiliations refAffmap = new BdAffiliations();

							for (int e=0 ; e < refAuthorgroup.size() ; e++)
							{
								setAuthorAndAffs( refAusmap,
													refAffmap,
													(Element) refAuthorgroup.get(e));
							}

							referenceItemcitationAuthor.put(referenceID,auToStringBuffer(refAusmap));
							StringBuffer secondgroup = new StringBuffer();
							referenceItemcitationAffiliation.put(referenceID,auffToStringBuffer(refAffmap,secondgroup));
							//System.out.println("referenceItemcitation AUTHOR "+auToStringBuffer(refAusmap));

						}

						//REFERENCE CITATION SOURCETITLE
						if(refdItemcitation.getChild("sourcetitle",noNamespace)!=null)
						{
							Element sourcetitle = (Element)refdItemcitation.getChild("sourcetitle",noNamespace);
							//change to deal with non-roman characters @08/10/2018
							//referenceItemcitationSourcetitle.put(referenceID,dictionary.mapEntity(sourcetitle.getTextTrim()));
							referenceItemcitationSourcetitle.put(referenceID,sourcetitle.getTextTrim());
							//System.out.println("REFERENCE CITATION SOURCETITLE "+sourcetitle.getTextTrim());
						}

						//REFERENCE CITATION SOURCETITLE ABBREV
						if(refdItemcitation.getChild("sourcetitle-abbrev",noNamespace)!=null)
						{
							Element sourcetitleAbbrev = (Element)refdItemcitation.getChild("sourcetitle-abbrev",noNamespace);
							//change to deal with non-roman characters @08/10/2018
							//referenceItemcitationSourcetitle_abbrev.put(referenceID,dictionary.mapEntity(sourcetitleAbbrev.getTextTrim()));
							referenceItemcitationSourcetitle_abbrev.put(referenceID,sourcetitleAbbrev.getTextTrim());
							//System.out.println("REFERENCE CITATION SOURCETITLE ABBREV "+sourcetitleAbbrev.getTextTrim());
						}

						//REFERENCE CITATION ISSN
						List issnList =  refdItemcitation.getChildren("issn",noNamespace);

						if(issnList != null)
						{
							String issn = getISSN(issnList);
							referenceItemcitationISSN.put(referenceID,issn);
							//System.out.println("REFERENCE CITATION ISSN "+issn);
						}

						//REFERENCE CITATION ISBN
						List refIsbnList = refdItemcitation.getChildren("isbn",noNamespace);
						if(refIsbnList != null)
						{
							String refIsbn = getISBN(refIsbnList);
							referenceItemcitationISBN.put(referenceID,refIsbn);
							//System.out.println("REFERENCE CITATION ISBN "+refIsbn);
						}

						//REFERENCE CITATION CODENCODE
						Element codencode = (Element) refdItemcitation.getChild("codencode",noNamespace);
						if (codencode != null)
						{
							//System.out.println("codencode::"+codencode.getTextTrim());
							referenceItemcitationCoden.put(referenceID,codencode.getTextTrim());
						}

						//REFERENCE CITATION PART
						Element part = (Element) refdItemcitation.getChild("part",noNamespace);
						if (part != null)
						{
							//System.out.println("part::"+part.getTextTrim());
							referenceItemcitationPart.put(referenceID,part.getTextTrim());
						}

						//REFERENCE CITATION PUBLICATIONYEAR
						Element refcitationPublicationyear = (Element) refdItemcitation.getChild("publicationyear",noNamespace);
						if(refcitationPublicationyear != null)
						{
							String citationPublicationyear = getPublicationYear(refcitationPublicationyear);
							referenceItemcitationPublicationyear.put(referenceID,citationPublicationyear);
							//System.out.println("REFERENCE CITATION PUBLICATIONYEAR::"+citationPublicationyear);
						}

						//REFERENCE CITATION VOLISSPAGG
						Element citation_volisspag = (Element)refdItemcitation.getChild("volisspag",noNamespace);
						if(citation_volisspag != null)
						{
							//VOLUME, ISSUE,
							Element citation_voliss = (Element) citation_volisspag.getChild("voliss",noNamespace);
							if(citation_voliss != null)
							{
								String citation_volume = citation_voliss.getAttributeValue("volume");
								String citation_issue  = citation_voliss.getAttributeValue("issue");

								if (citation_volume != null)
								{
									//System.out.println("citation_volume:"+citation_volume);
									referenceItemcitationVolume.put(referenceID,citation_volume);
								}
								if(citation_issue != null)
								{
									//System.out.println("citation_issue::"+citation_issue);
									referenceItemcitationIssue.put(referenceID,citation_issue);
								}
							}

							//PAGERANGE PAGE
							String citation_pages = getPages(citation_volisspag);
							referenceItemcitationPage.put(referenceID,citation_pages);
							//System.out.println("PAGERANGE PAGE::"+citation_pages);
						}

						//REFERENCE CITATION ARTICLE NUMBER
						Element articlenumber = (Element) refdItemcitation.getChild("article-number",noNamespace);
						if (articlenumber != null)
						{
							//System.out.println("articlenumber::"+articlenumber.getTextTrim());
							referenceItemcitationArticleNumber.put(referenceID,articlenumber.getTextTrim());
						}

						//REFERENCE CITATION WEBSITE
						Element citationWebsite = (Element) refdItemcitation.getChild("website",noNamespace);
						if(citationWebsite!=null)
						{
							Element citationWebsitename = (Element) citationWebsite.getChild("websitename",noNamespace);
							Element citationEAddress = (Element) citationWebsite.getChild("e-address",ceNamespace);
							if(citationWebsitename!=null)
							{
								referenceItemcitationWebsite.put(referenceID,citationWebsitename.getTextTrim());
							
								citationEAddress = (Element) citationWebsitename.getChild("e-address",ceNamespace);
								if(citationEAddress!=null)
								{
									String citationEAddressType = citationEAddress.getAttributeValue("type");
									if(citationEAddressType == null)
									{
										citationEAddressType = "email";
									}
									String citationEAddresstext = citationEAddress.getTextTrim();
									if(citationEAddresstext != null)
									{
										referenceItemcitationEAddress.put(referenceID,citationEAddressType+Constants.IDDELIMITER+citationEAddresstext);
										//System.out.println("REFERENCE CITATION WEBSITE="+citationEAddressType+" | "+citationEAddresstext);
									}
								}
							}
							else if(citationEAddress!=null)
							{
								String citationEAddressType = citationEAddress.getAttributeValue("type");
								if(citationEAddressType == null)
								{
									citationEAddressType = "email";
								}
								String citationEAddresstext = citationEAddress.getTextTrim();
								if(citationEAddresstext != null)
								{
									referenceItemcitationEAddress.put(referenceID,citationEAddressType+Constants.IDDELIMITER+citationEAddresstext);
									//System.out.println("REFERENCE CITATION WEBSITE="+citationEAddressType+" | "+citationEAddresstext);
								}
							}
								
						}
						

						//REFERENCE CITATION REF-TEXT
						Element citationRefText = (Element) refdItemcitation.getChild("ref-text",noNamespace);
						if(citationRefText!=null)
						{
							//change to deal with non-roman characters @08/10/2018
							//referenceItemcitationRefText.put(referenceID,dictionary.mapEntity(citationRefText.getTextTrim()));
							referenceItemcitationRefText.put(referenceID,citationRefText.getTextTrim());							
						}

					}//refd-itemcitation
					
					//sourceText
					Element sourceText = (Element) reference.getChild("source-text",ceNamespace);
					if(sourceText!=null)
					{
						referenceSourceText.put(referenceID,sourceText.getTextTrim());
					}

				}

			}

		}

		Hashtable reference = new Hashtable();
		if(referenceTitle != null && referenceTitle.size()>0)
		{
			reference.put("REFERENCETITLE",referenceTitle);
			//System.out.println("REFERENCETITLE:"+referenceTitle.toString());
		}

		if(referenceAuthor!= null && referenceAuthor.size()>0)
		{
			reference.put("REFERENCEAUTHOR",referenceAuthor);
			//System.out.println("REFERENCEAUTHOR:"+referenceAuthor.toString());
		}

		if(referenceSourcetitle!=null && referenceSourcetitle.size()>0)
		{
			reference.put("REFERENCESOURCETITLE",referenceSourcetitle);
			//System.out.println("REFERENCESOURCETITLE:"+referenceSourcetitle.toString());
		}

		if(referencePublicationyear!=null && referencePublicationyear.size()>0)
		{
			reference.put("REFERENCEPUBLICATIONYEAR",referencePublicationyear);
			//System.out.println("REFERENCEPUBLICATIONYEAR:"+referencePublicationyear);
		}

		if(referenceVolume!=null && referenceVolume.size()>0)
		{
			reference.put("REFERENCEVOLUME",referenceVolume);
			//System.out.println("REFERENCEVOLUME:"+referenceVolume);
		}

		if(referenceIssue!=null && referenceIssue.size()>0)
		{
			reference.put("REFERENCEISSUE",referenceIssue);
			//System.out.println("REFERENCEISSUE:"+referenceIssue);
		}

		if(referencePages!=null && referencePages.size()>0)
		{
			reference.put("REFERENCEPAGES",referencePages);
			//System.out.println("REFERENCEPAGES:"+referencePages);
		}

		if(referenceFullText!=null && referenceFullText.size()>0)
		{
			reference.put("REFERENCEFULLTEXT",referenceFullText);
			//System.out.println("REFERENCEFULLTEXT:"+referenceFullText);
		}

		if(referenceText!=null && referenceText.size()>0)
		{
			reference.put("REFERENCETEXT",referenceText);
			//System.out.println("REFERENCETEXT:"+referenceText);
		}

		if(referenceWebsite!=null && referenceWebsite.size()>0)
		{
			reference.put("REFERENCEWEBSITE",referenceWebsite);
			//System.out.println("REFERENCEWEBSITE:"+referenceWebsite);
		}

		if(referenceItemid!=null && referenceItemid.size()>0)
		{
			reference.put("REFERENCEITEMID",referenceItemid);
			//System.out.println("REFERENCEITEMID::"+referenceItemid);
		}
		
		
		if(referenceItemDOI!=null && referenceItemDOI.size()>0)
		{
			reference.put("REFERENCEITEMDOI",referenceItemDOI);
			//System.out.println("REFERENCEITEMDOI::"+referenceItemDOI);
		}

		if(referenceItemcitationPII!=null && referenceItemcitationPII.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONPII",referenceItemcitationPII);
			//System.out.println("REFERENCEITEMCITATIONPII::"+referenceItemcitationPII);
		}

		if(referenceItemcitationDOI!=null && referenceItemcitationDOI.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONDOI",referenceItemcitationDOI);
			//System.out.println("REFERENCEITEMCITATIONDOI::"+referenceItemcitationDOI);
		}

		if(referenceItemcitationCitationTitle!=null && referenceItemcitationCitationTitle.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONCITATIONTITLE",referenceItemcitationCitationTitle);
			//System.out.println("REFERENCEITEMCITATIONTITLE::"+referenceItemcitationCitationTitle);
		}

		if(referenceItemcitationAuthor!=null && referenceItemcitationAuthor.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONAUTHOR",referenceItemcitationAuthor);
			//System.out.println("REFERENCEITEMCITATIONAUTHOR::"+referenceItemcitationAuthor);
		}
		
		if(referenceItemcitationAffiliation!=null && referenceItemcitationAffiliation.size()>0)
		{
			reference.put("REFITEMCITATIONAFFILIATION",referenceItemcitationAffiliation);
			//System.out.println("***REFERENCEITEMCITATIONAFFILIATION::"+referenceItemcitationAffiliation);
		}		

		if(referenceItemcitationSourcetitle!=null && referenceItemcitationSourcetitle.size()>0)
		{
			reference.put("REFITEMCITATIONSOURCETITLE",referenceItemcitationSourcetitle);
			//System.out.println("REFERENCEITEMCITATIONSOURCETITLE::"+referenceItemcitationSourcetitle);
		}

		if(referenceItemcitationSourcetitle_abbrev!=null && referenceItemcitationSourcetitle_abbrev.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONSOURCETITLEABBREV",referenceItemcitationSourcetitle_abbrev);
			//System.out.println("REFERENCEITEMCITATIONSOURCETITLEABBREV::"+referenceItemcitationSourcetitle_abbrev);
		}

		if(referenceItemcitationISSN!=null && referenceItemcitationISSN.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONISSN",referenceItemcitationISSN);
			//System.out.println("REFERENCEITEMCITATIONISSN::"+referenceItemcitationISSN);
		}

		if(referenceItemcitationISBN!=null && referenceItemcitationISBN.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONISBN",referenceItemcitationISBN);
			//System.out.println("REFERENCEITEMCITATIONISBN::"+referenceItemcitationISBN);
		}

		if(referenceItemcitationCoden!=null && referenceItemcitationCoden.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONCODEN",referenceItemcitationCoden);
			//System.out.println("REFERENCEITEMCITATIONCODEN::"+referenceItemcitationCoden);
		}

		if(referenceItemcitationPart!=null && referenceItemcitationPart.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONPART",referenceItemcitationPart);
			//System.out.println("REFERENCEITEMCITATIONPART::"+referenceItemcitationPart);
		}

		if(referenceItemcitationPublicationyear!=null && referenceItemcitationPublicationyear.size()>0)
		{
			reference.put("REFITEMCITATIONPUBLICATIONYEAR",referenceItemcitationPublicationyear);
			//System.out.println("REFERENCEITEMCITATIONPUBLICATIONYEAR::"+referenceItemcitationPublicationyear);
		}

		if(referenceItemcitationVolume!=null && referenceItemcitationVolume.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONVOLUME",referenceItemcitationVolume);
			//System.out.println("REFERENCEITEMCITATIONVOLUME::"+referenceItemcitationVolume);
		}

		if(referenceItemcitationIssue!=null && referenceItemcitationIssue.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONISSUE",referenceItemcitationIssue);
			//System.out.println("REFERENCEITEMCITATIONISSUE::"+referenceItemcitationIssue);
		}

		if(referenceItemcitationPage!=null && referenceItemcitationPage.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONPAGE",referenceItemcitationPage);
			//System.out.println("REFERENCEITEMCITATIONPAGE::"+referenceItemcitationPage);
		}

		if(referenceItemcitationArticleNumber!=null && referenceItemcitationArticleNumber.size()>0)
		{
			reference.put("REFITEMCITATIONARTICLENUMBER",referenceItemcitationArticleNumber);
			//System.out.println("REFERENCEITEMCITATIONARTICLENUMBER::"+referenceItemcitationArticleNumber);
		}

		if(referenceItemcitationWebsite!=null && referenceItemcitationWebsite.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONWEBSITE",referenceItemcitationWebsite);
			//System.out.println("REFERENCEITEMCITATIONWEBSITE::"+referenceItemcitationWebsite);
		}

		if(referenceItemcitationEAddress!=null && referenceItemcitationEAddress.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONEADDRESS",referenceItemcitationEAddress);
			//System.out.println("***REFERENCEITEMCITATIONEADDRESS::"+referenceItemcitationEAddress);
		}

		if(referenceItemcitationRefText!=null && referenceItemcitationRefText.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONREFTEXT",referenceItemcitationRefText);
			//System.out.println("REFERENCEITEMCITATIONREFTEXT::"+referenceItemcitationRefText);
		}
						
		//follow items added by hmo @6/26/2024
		if(referenceItemcitationEID!=null && referenceItemcitationEID.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONEID",referenceItemcitationEID);
		}
				
		if(referenceItemcitationID!=null && referenceItemcitationID.size()>0)
		{
			reference.put("REFERENCEITEMCITATIONID",referenceItemcitationID);
		}
		
		if(referenceItemcitationDatabase!=null && referenceItemcitationDatabase.size()>0)
		{			
			reference.put("REFITEMCITATIONDATACOLLECTION",referenceItemcitationDatabase);
		} 
		
		if(referenceSourceText!=null && referenceSourceText.size()>0)
		{
			reference.put("REFERENCESOURCETEXT",referenceSourceText);
		}
		

		if(reference.size()>0)
		{
			record.put("REFERENCE", reference);
		}
	}

	private String getPublicationYear(Element publicationyear) throws Exception
	{
		StringBuffer publicationyearBuffer = new StringBuffer();
		String publicationYearFirst = publicationyear.getAttributeValue("first");
		String publicationYearLast = publicationyear.getAttributeValue("last");
		if(publicationYearFirst!=null && publicationYearLast !=null)
		{
			publicationyearBuffer.append(publicationYearFirst+"-"+publicationYearLast);
		}
		else if(publicationYearFirst!=null)
		{
			publicationyearBuffer.append(publicationYearFirst);
		}
		else if(publicationYearLast!=null)
		{
			publicationyearBuffer.append(publicationYearLast);
		}

		return publicationyearBuffer.toString();
	}
	
	private String getItemDoi(List itemidList) throws Exception
	{
		String  itemid = null;
		
		for(int j=0;j<itemidList.size();j++)
		{
			Element itemidElement = (Element)itemidList.get(j);
			String  itemid_idtype = itemidElement.getAttributeValue("idtype");

			if(itemid_idtype != null && (itemid_idtype.equals("DOI")))
			{
				itemid = itemidElement.getTextTrim();
			}
		}
		return itemid;
	}

	private String getItemID(List itemidList) throws Exception
	{
		StringBuffer referenceItemid = new StringBuffer();
		String cpxID = null;
		String geoID = null;
		String chemID = null;
		String apiID = null;
		String apilitID = null;
		for(int j=0;j<itemidList.size();j++)
		{
			Element itemidElement = (Element)itemidList.get(j);
			String  itemid_idtype = itemidElement.getAttributeValue("idtype");

			if(itemid_idtype != null &&
					 (itemid_idtype.equals("CPX")||
					itemid_idtype.equals("GEO")||
					itemid_idtype.equals("API")||
					itemid_idtype.equals("APILIT")||
					itemid_idtype.equals("CHEM")))
			{
				String  itemid = itemidElement.getTextTrim();
				if(itemid_idtype.equals("CPX"))
				{
					cpxID = itemid;
				}
				else if(itemid_idtype.equals("GEO"))
				{
					geoID = itemid;
				}
				else if(itemid_idtype.equals("CHEM"))
				{
					chemID = itemid;
				}
				else if(itemid_idtype.equals("API"))
				{
					apiID = itemid;
				}
				else if(itemid_idtype.equals("APILIT"))
				{
					apilitID = itemid;
				}

			}
			else if (itemid_idtype != null && itemid_idtype.equals("PUI"))
			{
				String  pui = itemidElement.getTextTrim();
				referenceItemid.append("PUI:"+pui);
			}
			else if (itemid_idtype != null && itemid_idtype.equals("SEC"))
			{
				String  pui = itemidElement.getTextTrim();
				referenceItemid.append("SEC:"+pui);
			}
			
			if(referenceItemid.length()>0)
			{
				referenceItemid.append(Constants.IDDELIMITER);
			}
		}
		if(cpxID!=null)
		{
			referenceItemid.append("ACCESSNUMBER:"+cpxID);
		}
		else if(geoID!=null)
		{
			referenceItemid.append("ACCESSNUMBER:"+geoID);
		}
		else if(chemID!=null)
		{
			referenceItemid.append("ACCESSNUMBER:"+chemID);
		}
		else if(apiID!=null)
		{
			referenceItemid.append("ACCESSNUMBER:"+apiID);
		}
		else if(apilitID!=null)
		{
			referenceItemid.append("ACCESSNUMBER:"+apilitID);
		}
		
		if(referenceItemid.length()>0)
		{
			referenceItemid.append(Constants.IDDELIMITER);
		}
		return referenceItemid.toString();
	}
	
	private String getRefItemID(List itemidList) throws Exception
	{
		StringBuffer referenceItemid = new StringBuffer();
		String cpxID = null;
		String geoID = null;
		String chemID = null;
		String apiID = null;
		String apilitID = null;
		for(int j=0;j<itemidList.size();j++)
		{
			Element itemidElement = (Element)itemidList.get(j);
			String  itemid_idtype = itemidElement.getAttributeValue("idtype");			
			String itemid = itemidElement.getTextTrim();
			referenceItemid.append("\""+itemid_idtype+"\":\""+itemid+"\"")	;
			if(j<itemidList.size()-1)
			{
				referenceItemid.append(",");
			}
		}
		
		return referenceItemid.toString();
	}

	private String getPages(Element refVolisspag) throws Exception
	{
		Element pages = (Element) refVolisspag.getChild("pages",noNamespace);
		Element pagerange = (Element) refVolisspag.getChild("pagerange",noNamespace);
		Element pagecount = (Element) refVolisspag.getChild("pagecount",noNamespace);
		StringBuffer referencePages = new StringBuffer();

		if(pages != null)
		{
			referencePages.append("PAGE:"+pages.getTextTrim());
			referencePages.append(Constants.IDDELIMITER);
		}
		else if(pagerange != null)
		{
			String firstPage = pagerange.getAttributeValue("first");
			String lastPage = pagerange.getAttributeValue("last");
			referencePages.append("PAGERANGE:");
			if(firstPage != null)
			{
				referencePages.append(firstPage);
			}
			
			if(firstPage!=null & lastPage!=null)
			{
				referencePages.append("-");
			}
			
			if(lastPage != null)
			{
				referencePages.append(lastPage);
			}
		}
		else if(pagecount != null)
		{
				String pagecountValue = pagecount.getTextTrim();
				String pagecountType  = pagecount.getAttributeValue("type");
				referencePages.append("PAGECOUNT:");
				if(pagecountType != null && pagecountValue != null)
				{
					referencePages.append(pagecountType+Constants.IDDELIMITER+pagecountValue);
				}
				else if(pagecountValue!=null)
				{
					referencePages.append(pagecountValue);
					referencePages.append(Constants.IDDELIMITER);
				}

		}

		return referencePages.toString();
	}

	private String getCitationTitle(Element citationTitle) throws Exception
	{
		StringBuffer cittext = new StringBuffer();
		if(citationTitle != null)
		{
			List cittextlst = citationTitle.getChildren("titletext",noNamespace);

			if(cittextlst != null)
			{


				for (int i = 0; i < cittextlst.size(); i++)
				{
					Element cittextelm = (Element)cittextlst.get(i);
					cittext.append(i);
					cittext.append(Constants.IDDELIMITER);
					if(cittextelm.getContent()!=null)
					{
						cittext.append(DataLoadDictionary.mapEntity(getMixData(cittextelm.getContent())));
					}
					cittext.append(Constants.IDDELIMITER);
					if(cittextelm.getAttribute("original")!=null)
					{
						cittext.append(cittextelm.getAttributeValue("original"));
					}
					cittext.append(Constants.IDDELIMITER);
					if(cittextelm.getAttribute("lang",xmlNamespace)!=null)
					{
						cittext.append(cittextelm.getAttributeValue("lang",xmlNamespace));
					}
					if(i<cittextlst.size()-1)
					{
						cittext.append((Constants.AUDELIMITER));
					}
				}
				String citation = cittext.toString();
				if(this.databaseName.equalsIgnoreCase("elt"))
				{
					citation = citation.replaceAll("<inf>", "<sub>");
					citation = citation.replaceAll("</inf>", "</sub>");

				}


			}
		}
		return cittext.toString();
	}

	private String getISSN(List issnList) throws Exception
	{
		String issnValue = null;
		String issnType  = null;
		Element issn = null;
		StringBuffer issnBuffer = new StringBuffer();
		for(int e=0;e<issnList.size();e++)
		{
			issn = (Element)issnList.get(e);
			issnValue = issn.getTextTrim();
			issnType = (String)issn.getAttributeValue("type");
			
			if(issnType==null)
			{
				issnType="print";
			}
			
			if(issnType!=null && issnValue!= null)
			{
				issnBuffer.append(issnType+":"+issnValue);
			}
			
			if(e<issnList.size()-1)
			{
				issnBuffer.append(Constants.IDDELIMITER);
			}
		}

		return issnBuffer.toString();
	}

	private String getISBN(List isbnList) throws Exception
	{

		String  isbnValue = null;
		String  isbnType  = null;
		String  isbnLength = null;
		String  isbnVolume = null;
		Element isbn = null;
		StringBuffer isbnBuffer = new StringBuffer();
		for(int i=0;i<isbnList.size();i++)
		{
			isbn = (Element)isbnList.get(i);
			isbnValue = isbn.getTextTrim();
			isbnType  = isbn.getAttributeValue("type");
			isbnLength= isbn.getAttributeValue("length");
			isbnVolume= isbn.getAttributeValue("level");
			if(isbnType!=null)
			{
				isbnBuffer.append(isbnType);
			}
			isbnBuffer.append(Constants.IDDELIMITER);
			if(isbnLength!=null)
			{
				isbnBuffer.append(isbnLength);
			}
			isbnBuffer.append(Constants.IDDELIMITER);
			if(isbnVolume!=null)
			{
				isbnBuffer.append(isbnVolume);
			}
			isbnBuffer.append(Constants.IDDELIMITER);
			if(isbnValue!=null)
			{
				isbnBuffer.append(isbnValue);
			}
			if(i<isbnList.size()-1)
			{
				isbnBuffer.append((Constants.AUDELIMITER));
			}
		}

		return isbnBuffer.toString();
	}





	private void parseTradenamegroup(Element tradenamegroup,Hashtable record) throws Exception
	{
		StringBuffer tradenamegroupBuffer = new StringBuffer();

		if(tradenamegroup != null)
		{
			List tradenamesList = (List) tradenamegroup.getChildren("tradenames",noNamespace);
			if(tradenamesList != null)
			{
				for(int i=0;i<tradenamesList.size();i++)
				{
					if(tradenamegroupBuffer.length()>0)
					{
						tradenamegroupBuffer.append((Constants.AUDELIMITER));
					}
					Element tradenames = (Element)tradenamesList.get(i);

					if(tradenames != null)
					{

						List tradenameList = tradenames.getChildren("trademanuitem",noNamespace);
						String tradenameType = tradenames.getAttributeValue("type");
						tradenamegroupBuffer.append(tradenameType);

						tradenamegroupBuffer.append(Constants.IDDELIMITER);

						for(int j=0;j<tradenameList.size();j++)
						{
							Element tradenameElement = (Element)tradenameList.get(j);

							String tradename =  tradenameElement.getChildText("tradename",noNamespace);

							if(	tradename != null && tradename.length()>0)
							{
								tradenamegroupBuffer.append(DataLoadDictionary.mapEntity(tradename));
							}

							tradenamegroupBuffer.append(Constants.IDDELIMITER);

						}
					}
				}
			}
			if(tradenamegroupBuffer.length() > 0 )
			{
				record.put("TRADENAME", tradenamegroupBuffer.toString());
			}
		}
	}

	private void parseManufacturergroup(Element manufacturergroup,Hashtable record) throws Exception
	{
		StringBuffer manufacturergroupBuffer = new StringBuffer();

		if(manufacturergroup != null)
		{
			List manufacturergroupList = (List) manufacturergroup.getChildren("manufacturers",noNamespace);
			if(manufacturergroupList != null)
			{
				for(int i=0;i<manufacturergroupList.size();i++)
				{
					if(manufacturergroupBuffer.length()>0)
					{
						manufacturergroupBuffer.append((Constants.AUDELIMITER));
					}
					Element manufacturers = (Element)manufacturergroupList.get(i);

					if(manufacturers != null)
					{
						List manufacturerList = manufacturers.getChildren("manufacturer",noNamespace);
						String manufacturerType = manufacturers.getAttributeValue("type");

						manufacturergroupBuffer.append(manufacturerType);

						manufacturergroupBuffer.append(Constants.IDDELIMITER);

						for(int j=0;j<manufacturerList.size();j++)
						{
							Element manufacturerElement = (Element)manufacturerList.get(j);
							String country = manufacturerElement.getAttributeValue("country");
							if(country!=null)
							{
								manufacturergroupBuffer.append(DataLoadDictionary.mapEntity(country));
							}

							manufacturergroupBuffer.append(Constants.GROUPDELIMITER);

							String manufacturer =  manufacturerElement.getText();

							if(	manufacturer != null && manufacturer.length()>0)
							{
								manufacturergroupBuffer.append(DataLoadDictionary.mapEntity(manufacturer));
							}

							manufacturergroupBuffer.append(Constants.IDDELIMITER);

						}
					}
				}
			}
			if(manufacturergroupBuffer.length() > 0 )
			{
				record.put("MANUFACTURER", manufacturergroupBuffer.toString());
			}
		}
	}

	private void parseSequencebanks(Element sequencebanks,Hashtable record) throws Exception
	{
		StringBuffer sequencebanksBuffer = new StringBuffer();

		if(sequencebanks != null)
		{
			List sequencebankList = (List) sequencebanks.getChildren("sequencebank",noNamespace);
			if(sequencebankList != null)
			{
				for(int i=0;i<sequencebankList.size();i++)
				{
					if(sequencebanksBuffer.length()>0)
					{
						sequencebanksBuffer.append((Constants.AUDELIMITER));
					}
					Element sequencebank = (Element)sequencebankList.get(i);
					if(sequencebank != null)
					{
						List sequencenumberList = sequencebank.getChildren("sequence-number",noNamespace);
						String sequencebankName = sequencebank.getAttributeValue("name");
						if(sequencebankName!=null)
						{
							sequencebanksBuffer.append(DataLoadDictionary.mapEntity(sequencebankName));
						}

						sequencebanksBuffer.append(Constants.IDDELIMITER);

						for(int j=0;j<sequencenumberList.size();j++)
						{
							Element sequencenumberElement = (Element)sequencenumberList.get(j);

							String sequencenumber =  sequencenumberElement.getText();

							if(	sequencenumber != null && sequencenumber.length()>0)
							{
								sequencebanksBuffer.append(sequencenumber);
							}

							sequencebanksBuffer.append(Constants.IDDELIMITER);

						}
					}
				}
			}
			//System.out.println(sequencebanksBuffer.toString());
			if(sequencebanksBuffer.length() > 0 )
			{
				record.put("SEQUENCEBANKS", sequencebanksBuffer.toString());
			}
		}
	}

	private void parseChemicalgroup(Element chemicalgroup,Hashtable record) throws Exception
	{
		StringBuffer chemicalgroupBuffer = new StringBuffer();
		String cas_registry_number = null;
		if(chemicalgroup != null)
		{
			List chemicalsList = (List) chemicalgroup.getChildren("chemicals",noNamespace);
			if(chemicalsList != null)
			{
				for(int i=0;i<chemicalsList.size();i++)
				{
					if(chemicalgroupBuffer.length()>0)
					{
						chemicalgroupBuffer.append((Constants.AUDELIMITER));
					}
					Element chemicals = (Element)chemicalsList.get(i);
					if(chemicals != null)
					{
						List chemicalList = chemicals.getChildren("chemical",noNamespace);
						for(int j=0;j<chemicalList.size();j++)
						{
							Element chemical = (Element)chemicalList.get(j);

							chemicalgroupBuffer.append(Constants.IDDELIMITER);

							String chemical_name =  chemical.getChildText("chemical-name",noNamespace);
							List casregistrynumberList = chemical.getChildren("cas-registry-number",noNamespace);

							if(	chemical_name != null)
							{
								chemicalgroupBuffer.append(DataLoadDictionary.mapEntity(chemical_name));
							}

							chemicalgroupBuffer.append(Constants.GROUPDELIMITER);

							if(casregistrynumberList != null)
							{
								for(int k=0;k<casregistrynumberList.size();k++)
								{
									Element casregistrynumberElement = (Element)casregistrynumberList.get(k);
									if(casregistrynumberElement!=null)
									{
										cas_registry_number = casregistrynumberElement.getText();
										chemicalgroupBuffer.append(cas_registry_number);
									}
									if(k<casregistrynumberList.size()-1)
									{
										chemicalgroupBuffer.append(Constants.GROUPDELIMITER);
									}
								}
							}
						}
					}
				}
			}
			//System.out.println(chemicalgroupBuffer.toString());
			if(chemicalgroupBuffer.length() > 0 )
			{
				record.put("CASREGISTRYNUMBER", chemicalgroupBuffer.toString());
			}
		}
	}

	private void parseCorrespondenceElement(List correspondences,Hashtable record) throws Exception
	{
		
		for(int j=0;j<correspondences.size();j++)
		{
			Element correspondence = (Element)correspondences.get(j);

			if(correspondence != null)
			{
				Element person = (Element) correspondence.getChild("person",noNamespace);
				Element affiliation = (Element) correspondence.getChild("affiliation",noNamespace);
				Element eAddress = (Element) correspondence.getChild("e-address",ceNamespace);
				String personName = getPersonalName(0,person);
				String affString =  getAffiliation(affiliation);
				String eAddressType = null;
				String eAddressString = null;
				if(	eAddress != null)
				{
					eAddressType = eAddress.getAttributeValue("type");
					eAddressString = eAddress.getText();
				}
	
				if(eAddressType == null)
				{
					eAddressType = "email";
				}
				if(eAddressString != null)
				{
					eAddressString = eAddressType+Constants.IDDELIMITER+eAddressString;
					if(record.get("CORRESPONDENCEEADDRESS")!=null)
					{
						eAddressString=record.get("CORRESPONDENCEEADDRESS")+Constants.AUDELIMITER+eAddressString;
					}
					//record.put("CORRESPONDENCEEADDRESS",eAddressString);
					record.put("CORRESPONDENCEEADDRESS",eAddressString);
				}
	
				if(personName!=null && personName.length()>0)
				{
					//System.out.println("personName "+personName);
					if(record.get("CORRESPONDENCENAME")!=null)
					{
						personName=record.get("CORRESPONDENCENAME")+Constants.AUDELIMITER+personName;
					}
					record.put("CORRESPONDENCENAME",personName);
				}
	
				if(affString!=null)
				{
					//System.out.println("CORRESPONDENCEAFFILIATION "+affString);
					if(record.get("CORRESPONDENCEAFFILIATION")!=null)
					{
						affString=record.get("CORRESPONDENCEAFFILIATION")+Constants.AUDELIMITER+affString;
					}
					record.put("CORRESPONDENCEAFFILIATION",affString);
				}
			}
		}
	}

	private void parseSourceElement(Element source,Hashtable record,Element bibrecord) throws Exception
	{
		if(source != null)
		{

			String sourceType = source.getAttributeValue("type");
			//System.out.println("Source type::"+sourceType);

			if(sourceType != null)
			{
				record.put("SOURCETYPE", sourceType);
				//System.out.println("Source type::"+sourceType);
			}

			String sourceCountry = source.getAttributeValue("country");
			//System.out.println("sourceCountry::"+sourceCountry);

			if(sourceCountry != null)
			{
				record.put("SOURCECOUNTRY", DataLoadDictionary.mapEntity(sourceCountry));
				//System.out.println("Country type::"+sourceCountry.getValue());
			}

			String sourceID = source.getAttributeValue("srcid");
			//System.out.println("sourceID::"+sourceID);

			if(sourceID != null)
			{
				record.put("SOURCEID", sourceID);
				//System.out.println("SRCID ::"+sourceID);
			}


			//SOURCETITLE

			Element sourcetitle = (Element) source.getChild("sourcetitle",noNamespace);

			if(sourcetitle != null)
			{
				record.put("SOURCETITLE", DataLoadDictionary.mapEntity(getMixData(sourcetitle.getContent())));
				//System.out.println("sourcetitle::"+sourcetitle.getText());
				//System.out.println("sourcetitle1::"+getMixData(sourcetitle.getText()));
			}

			// SOURCETITLEABBREV

			Element sourcetitleabbrev = (Element)source.getChild("sourcetitle-abbrev",noNamespace);
			if(sourcetitleabbrev != null)
			{
				record.put("SOURCETITLEABBREV", DataLoadDictionary.mapEntity(getMixData(sourcetitleabbrev.getContent())));
				//System.out.println("sourcetitleabbrev::"+sourcetitleabbrev.getText());
			}

			//TRANSLATEDSOURCETITLE
			List translatedsourcetitle = source.getChildren("translated-sourcetitle",noNamespace);
			if(translatedsourcetitle != null)
			{
				StringBuffer ttBuffer = new StringBuffer();
				for(int i=0;i<translatedsourcetitle.size();i++)
				{
					if(ttBuffer.length()>0)
					{
						ttBuffer.append((Constants.AUDELIMITER));
					}
					Element translateTitle = (Element)translatedsourcetitle.get(i);
					ttBuffer.append(DataLoadDictionary.mapEntity(getMixData(translateTitle.getContent())));
				}
				record.put("TRANSLATEDSOURCETITLE", (ttBuffer.toString()));
				//System.out.println("translatedsourcetitle::"+ttBuffer.toString());
			}

			//volumetitle
			Element volumetitle = (Element)source.getChild("volumetitle",noNamespace);
			if(volumetitle != null)
			{
				record.put("VOLUMETITLE", DataLoadDictionary.mapEntity(getMixData(volumetitle.getContent())));
				//System.out.println("volumetitle::"+volumetitle.getText());
			}

			//ISSUETITLE
			Element issuetitle = (Element)source.getChild("issuetitle",noNamespace);
			if(issuetitle != null)
			{
				record.put("ISSUETITLE", DataLoadDictionary.mapEntity(getMixData(issuetitle.getContent())));
				//System.out.println("issuetitle::"+issuetitle.getText());
			}

			//ISSN
			List issnList =  source.getChildren("issn",noNamespace);

			if(issnList != null)
			{
				String issnValue = null;
				String issnType  = null;
				Element issn = null;
				for(int i=0;i<issnList.size();i++)
				{
					issn = (Element)issnList.get(i);
					issnValue = issn.getTextTrim();
					issnType = (String)issn.getAttributeValue("type");
					if(issnValue!= null)
					{
						if(issnType==null)
						{
							issnType="print";
						}
						if(issnType!=null && issnType.equalsIgnoreCase("print"))
						{
							record.put("ISSN",issnValue);
						}
						else if(issnType!=null && issnType.equalsIgnoreCase("electronic"))
						{
							record.put("EISSN",issnValue);
						}
					}
				}
			}

			//ISBN
			List isbnList = source.getChildren("isbn",noNamespace);
			if(isbnList != null)
			{
				String  isbnValue = null;
				String  isbnType  = null;
				String  isbnLength = null;
				String  isbnVolume = null;
				Element isbn = null;
				StringBuffer isbnBuffer = new StringBuffer();
				for(int i=0;i<isbnList.size();i++)
				{
					isbn = (Element)isbnList.get(i);
					isbnValue = isbn.getTextTrim();
					isbnType  = isbn.getAttributeValue("type");
					isbnLength= isbn.getAttributeValue("length");
					isbnVolume= isbn.getAttributeValue("level");
					if(isbnType!=null)
					{
						isbnBuffer.append(isbnType);
					}
					isbnBuffer.append(Constants.IDDELIMITER);
					if(isbnLength!=null)
					{
						isbnBuffer.append(isbnLength);
					}
					isbnBuffer.append(Constants.IDDELIMITER);
					if(isbnVolume!=null)
					{
						isbnBuffer.append(isbnVolume);
					}
					isbnBuffer.append(Constants.IDDELIMITER);
					if(isbnValue!=null)
					{
						isbnBuffer.append(isbnValue);
					}
					if(i<isbnList.size()-1)
					{
						isbnBuffer.append((Constants.AUDELIMITER));
					}
				}
				record.put("ISBN",isbnBuffer.toString());
			}

			//CODENCODE
			Element codencode = (Element) source.getChild("codencode",noNamespace);
			if (codencode != null)
			{
				//System.out.println("codencode::"+codencode.getTextTrim());
				record.put("CODEN",codencode.getTextTrim());
			}

			Element volisspag = (Element)source.getChild("volisspag",noNamespace);
			if(volisspag != null)
			{
				//VOLUME, ISSUE,

				Element voliss = (Element) volisspag.getChild("voliss",noNamespace);
				if(voliss != null)
				{
					String volume = voliss.getAttributeValue("volume");
					String issue  = voliss.getAttributeValue("issue");

					if (volume != null)
					{
						//System.out.println("volume:"+volume);
						record.put("VOLUME",volume);
					}
					if(issue != null)
					{
						//System.out.println("issue::"+issue);
						record.put("ISSUE",issue);
					}
				}

				//PAGERANGE PAGE
				Element pages = (Element) volisspag.getChild("pages",noNamespace);
				Element pagerange = (Element) volisspag.getChild("pagerange",noNamespace);
				StringBuffer pagesBuffer = new StringBuffer();
				if(pages != null)
				{
					pagesBuffer.append(pages.getTextTrim());
					pagesBuffer.append((Constants.AUDELIMITER));
					pagesBuffer.append((Constants.AUDELIMITER));
				}
				else if(pagerange != null)
				{
					String firstPage = pagerange.getAttributeValue("first");
					String lastPage = pagerange.getAttributeValue("last");
					pagesBuffer.append((Constants.AUDELIMITER));
					if(firstPage != null)
					{
						pagesBuffer.append(firstPage);
					}
					pagesBuffer.append((Constants.AUDELIMITER));
					if(lastPage != null)
					{
						pagesBuffer.append(lastPage);
					}
				}

				record.put("PAGE",pagesBuffer.toString());

				//PAGECOUNT

				List pagecountList = (List) volisspag.getChildren("pagecount",noNamespace);
				if(pagecountList != null)
				{
					StringBuffer pagecountBuffer = new StringBuffer();
					for(int i=0;i<pagecountList.size();i++)
					{
						if(pagecountBuffer !=null)
						{
							pagecountBuffer.append((Constants.AUDELIMITER));
						}
						Element pagecount = (Element)pagecountList.get(i);
						String pagecountValue = pagecount.getTextTrim();
						String pagecountType  = pagecount.getAttributeValue("type");
						if(pagecountType != null && pagecountValue != null)
						{
							pagecountBuffer.append(pagecountType+Constants.IDDELIMITER+pagecountValue);
						}
						else if(pagecountValue!=null)
						{
							pagecountBuffer.append(pagecountValue);
						}
					}
					record.put("PAGECOUNT",pagecountBuffer.toString());
				}
			}

			//ARTICLE NUMBER
			Element articleNumber = source.getChild("article-number",noNamespace);
			if(articleNumber != null)
			{
				record.put("ARTICLENUMBER",articleNumber.getTextTrim());
			}

			//PUBLICATIONYEAR
			Element publicationyear = source.getChild("publicationyear",noNamespace);
			if(publicationyear != null)
			{
				String publicationYearFirst = publicationyear.getAttributeValue("first");
				String publicationYearLast = publicationyear.getAttributeValue("last");
				if(publicationYearFirst!=null && publicationYearLast !=null)
				{
					record.put("PUBLICATIONYEAR",publicationYearFirst+"-"+publicationYearLast);
				}
				else if(publicationYearFirst!=null)
				{
					record.put("PUBLICATIONYEAR",publicationYearFirst);
				}
				else if(publicationYearLast!=null)
				{
					record.put("PUBLICATIONYEAR",publicationYearLast);
				}
			}//add for AIP
			else
			{
				String aipPubYear=null;
				Element history = null;
				if(bibrecord!=null)
				{
					Element itemInfo = bibrecord.getChild("item-info",noNamespace);
					
					if(itemInfo!=null && itemInfo.getChild("history",noNamespace)!=null)
					{
						Element dateCreated = (itemInfo.getChild("history",noNamespace)).getChild("date-created",noNamespace);
						if(dateCreated!=null && dateCreated.getAttributeValue("year")!=null)
						{
							aipPubYear = dateCreated.getAttributeValue("year");
							record.put("PUBLICATIONYEAR",aipPubYear);
						}
					}
				}
				//disable for cafe data
				
				if(aipPubYear==null)
				{
					java.util.Calendar calCurrentDate =  java.util.GregorianCalendar.getInstance();
					int year = calCurrentDate.get(java.util.Calendar.YEAR);
					record.put("PUBLICATIONYEAR",Integer.toString(year));
				}
				
			}

			//PUBLICATIONDATE
			Element publicationdate = source.getChild("publicationdate",noNamespace);
			String dateString = null;
			if(publicationdate != null)
			{
				String publicationdateYear  = publicationdate.getChildTextTrim("year",noNamespace);
				String publicationdateMonth = publicationdate.getChildTextTrim("month",noNamespace);
				String publicationdateDay   = publicationdate.getChildTextTrim("day",noNamespace);
				String publicationdateDateText = publicationdate.getChildTextTrim("date-text",noNamespace);
				if(publicationdateDateText!=null)
				{
					record.put("PUBLICATIONDATE",DataLoadDictionary.mapEntity(publicationdateDateText));
					record.put("PUBLICATIONDATEDATETEXT",DataLoadDictionary.mapEntity(publicationdateDateText));
				}
				else
				{
					dateString = getDateString(publicationdateYear,publicationdateMonth,publicationdateDay);
					if(dateString != null)
					{
						record.put("PUBLICATIONDATE",DataLoadDictionary.mapEntity(dateString));
					}
				}
			}//added for AIP
			else if(bibrecord!=null && bibrecord.getChild("history",noNamespace)!=null)
			{
					Element history = bibrecord.getChild("history",noNamespace);
					Element dateCreated = history.getChild("date-created",noNamespace);
					if(dateCreated!=null)
					{
						String year = dateCreated.getAttributeValue("year");
						String month= dateCreated.getAttributeValue("month");
						String day= dateCreated.getAttributeValue("day");
						dateString = getDateString(year,month,day);
					}
					if(dateString != null)
					{
						record.put("PUBLICATIONDATE",DataLoadDictionary.mapEntity(dateString));
					}
			}
			
			//SOURCE WEBSITE
			Element website = (Element) source.getChild("website",noNamespace);
			if(website!=null)
			{
				Element websitename = (Element) website.getChild("websitename",noNamespace);
				StringBuffer  sourceWebsiteBuffer = new StringBuffer();
				if(websitename!=null)
				{
					String websitenameText = websitename.getTextTrim();
					sourceWebsiteBuffer.append(websitenameText);
				}
				else if(website.getTextTrim()!=null && website.getTextTrim().length()>0)
				{
					String websitenameText = website.getTextTrim();
					sourceWebsiteBuffer.append(websitenameText);
					//System.out.println("website_body has content "+websitenameText);
				}
				sourceWebsiteBuffer.append(Constants.IDDELIMITER);
				Element eaddress = (Element) website.getChild("e-address",ceNamespace);
				if(eaddress!=null)
				{
					String eaddresstext = eaddress.getText();
					if(eaddresstext != null)
					{
						sourceWebsiteBuffer.append(eaddresstext);
					}
				}
				record.put("SOURCEWEBSITE",sourceWebsiteBuffer.toString());
			}

			//CONTRIBUTORGROUP
			List contributorgroup = source.getChildren("contributor-group",noNamespace);
			if(contributorgroup != null)
			{
				setContributorgroup(record,contributorgroup);
			}

			Element editors = source.getChild("editors",noNamespace);
			if(editors!=null)
			{
				record.put("EDITORS",getEditors(editors));
			}

			//PUBLISHER, PUBLISHERNAME,PUBLISHERADDRESS, PUBLISHERELECTRONICADDRESS
			List publisherList = source.getChildren("publisher",noNamespace);
			if(publisherList!=null)
			{
				setPublisher(record,publisherList);
			}

			//Additional-srcinfo
			Element additionalSrcinfo = source.getChild("additional-srcinfo",noNamespace);
			if(additionalSrcinfo != null)
			{
				//System.out.println("IN additionalSrcinfo");
				setAdditionalSrcinfo(record,additionalSrcinfo);
			}
			
			//bib-text
			
			if(source.getChild("bib-text",noNamespace) != null)
			{
				String bibtext = source.getChildText("bib-text",noNamespace);				
				record.put("SOURCEBIBTEXT",bibtext);			
			}
		}
	}

	private String getContributor(int e,List contributorList)
	{
		StringBuffer ContributorBuffer = new StringBuffer();
		for(int i=0;i<contributorList.size();i++)
		{
			Element contributor = (Element)contributorList.get(i);
			String 	contributorSeq = contributor.getAttributeValue("seq");
			String  contributorAuid= contributor.getAttributeValue("auid");
			String  contributorType= contributor.getAttributeValue("type");
			String  contributorRoleString= contributor.getAttributeValue("role");
			String  contributorString = getPersonalName(0,contributor);
			String  eaddress = contributor.getChildTextTrim("e-address",ceNamespace);
			ContributorBuffer.append(e);
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(contributorSeq!=null)
			{
				ContributorBuffer.append(contributorSeq);
			}
			else
			{
				ContributorBuffer.append(i);
			}
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(contributorAuid!=null)
			{
				ContributorBuffer.append(contributorAuid);
			}
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(contributorType!=null)
			{
				ContributorBuffer.append(contributorType);
			}
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(contributorRoleString!=null)
			{
				if(contributorRole.get(contributorRoleString)!=null)
				{
					contributorRoleString = (String)contributorRole.get(contributorRoleString);
				}
				ContributorBuffer.append(contributorRoleString);
			}
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(contributorString!=null)
			{
				ContributorBuffer.append(contributorString);
			}
			ContributorBuffer.append(Constants.IDDELIMITER);
			if(eaddress!=null)
			{
				ContributorBuffer.append(eaddress);
			}
			if(i<contributorList.size()-1)
			{
				ContributorBuffer.append((Constants.AUDELIMITER));
			}

		}
		return ContributorBuffer.toString();
	}
	
	private String getPersonalNames(List collaborationList)
	{
		StringBuffer collaborationBuffer = new StringBuffer();
		for(int i=0;i<collaborationList.size();i++)
		{
			Element collaboration = (Element)collaborationList.get(i);
			collaborationBuffer.append(getPersonalName(i,collaboration));
			collaborationBuffer.append(Constants.AUDELIMITER);
		}
		return collaborationBuffer.toString();
	}
	
	private String getCollaboration(int e,List collaborationList)
	{
		StringBuffer collaborationBuffer = new StringBuffer();
		for(int i=0;i<collaborationList.size();i++)
		{
			Element collaboration = (Element)collaborationList.get(i);
			String 	collaborationSeq = collaboration.getAttributeValue("seq");
			String  collaborationAuid= collaboration.getAttributeValue("auid");
			String  collaborationType= collaboration.getChildText("indexed-name");
			String  collaborationTextString= collaboration.getChildText("text");
			String  collaborationString = getPersonalNames(collaboration.getChildren("member-name"));
			String  eaddress = collaboration.getChildTextTrim("e-address",ceNamespace);
			collaborationBuffer.append(e);
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(collaborationSeq!=null)
			{
				collaborationBuffer.append(collaborationSeq);
			}
			else
			{
				collaborationBuffer.append(i);
			}
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(collaborationAuid!=null)
			{
				collaborationBuffer.append(collaborationAuid);
			}
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(collaborationType!=null)
			{
				collaborationBuffer.append(collaborationType);
			}
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(collaborationTextString!=null)
			{				
				collaborationBuffer.append(collaborationTextString);
			}
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(collaborationString!=null)
			{
				collaborationBuffer.append(collaborationString);
			}
			collaborationBuffer.append(Constants.AUDELIMITER);
			if(eaddress!=null)
			{
				collaborationBuffer.append(eaddress);
			}
			if(i<collaborationList.size()-1)
			{
				collaborationBuffer.append((Constants.AUDELIMITER));
			}

		}
		return collaborationBuffer.toString();
	}


	private void setContributorgroup(Hashtable record,List contributorgroupList) throws Exception
	{
		StringBuffer contributorBuffer = new StringBuffer();
		StringBuffer contributorAffiliationBuffer = new StringBuffer();
		String affiliationString = null;
		String contributeString = null;

		for (int e=0; e < contributorgroupList.size(); e++)
		{
			Element contributorgroup = (Element)contributorgroupList.get(e);
			List contributorList = contributorgroup.getChildren("contributor",noNamespace);
			List collaborationList = contributorgroup.getChildren("collaboration",noNamespace);
			Element affiliation = (Element)contributorgroup.getChild("affiliation",noNamespace);
			if(contributorList!=null)
			{
				contributeString = getContributor(e,contributorList);
			}
			else if(collaborationList!=null)
			{
				contributeString = getCollaboration(e,collaborationList);
			}
			if(contributeString!=null)
			{
				contributorBuffer.append(contributeString);
			}
			if(affiliation!=null)
			{
				affiliationString = getAffiliation(affiliation);
			}
			if(affiliationString!=null)
			{
				contributorAffiliationBuffer.append(e+Constants.IDDELIMITER+affiliationString);
			}
			contributorBuffer.append((Constants.AUDELIMITER));
			contributorAffiliationBuffer.append((Constants.AUDELIMITER));
		}

		record.put("CONTRIBUTOR", contributorBuffer.toString());
		//System.out.println("CONTRIBUTOR "+contributorBuffer.toString());
		record.put("CONTRIBUTORAFFILIATION", contributorAffiliationBuffer.toString());
		//System.out.println("CONTRIBUTORAFFILIATION "+contributorAffiliationBuffer.toString());
	}

	public String getDateString(String year,String month, String day)
	{
		StringBuffer dateBuffer = new StringBuffer();
		String cMonth = null;
		if(month != null && !month.equals("00"))
		{
			cMonth = convertMonth(month);
			dateBuffer.append(cMonth);
		}

		if(day!= null && !day.equals("00"))
		{
			if(dateBuffer.length()>0)
			{
				dateBuffer.append(" ");
			}
			dateBuffer.append(reFormatDate(day));
		}

		if(year!=null)
		{
			if(dateBuffer.length()>0)
			{
				dateBuffer.append(", ");
			}
			dateBuffer.append(year);
		}

		return dateBuffer.toString();
	}


	  private static final Pattern pubdateregex = Pattern.compile("(\\d{2})");

	  private String reFormatDate(String pubdate)
	  {
	    if(pubdate != null)
	    {
	      Matcher m = pubdateregex.matcher(pubdate);
	      if (m.find() && m.groupCount() == 1) {
	        String days = m.group(0);
	        // check for days which are formatted like July 01
	        if(days.startsWith("0"))
	        {
	          // trim off the leading zero to change to July 1
	          days = days.substring(1);
	        }
	        return days;
	       // System.out.println("reFormatted " + pubdate);
	      }
	    }
	    return pubdate;
	  }

	private void setPublisher(Hashtable record,List publisherList)
	{
		StringBuffer pnBuffer = new StringBuffer();
		StringBuffer paBuffer = new StringBuffer();
		StringBuffer pElectronicABuffer = new StringBuffer();
		for(int i=0;i<publisherList.size();i++)
		{
			Element publisher = (Element)publisherList.get(i);
			if(publisher!=null)
			{
				String publisherName = publisher.getChildTextTrim("publishername",noNamespace);
				if(publisherName!=null)
				{
					if(pnBuffer.length()>0)
					{
						pnBuffer.append((Constants.AUDELIMITER));
					}
					pnBuffer.append(DataLoadDictionary.mapEntity(publisherName));
				}

				String publisheraddress = publisher.getChildTextTrim("publisheraddress",noNamespace);
				if(publisheraddress != null)
				{
					if(paBuffer.length()>0)
					{
						paBuffer.append((Constants.AUDELIMITER));
					}
					paBuffer.append(DataLoadDictionary.mapEntity(publisheraddress));
				}
				else if(publisher.getChild("affiliation",noNamespace)!=null)
				{
					if(paBuffer.length()>0)
					{
						paBuffer.append((Constants.AUDELIMITER));
					}
					publisheraddress = getConfAffiliation(publisher.getChild("affiliation",noNamespace));
					//System.out.println("publisheraddress "+publisheraddress);
					if(publisheraddress != null && publisheraddress.length()>1)
					{
						paBuffer.append(publisheraddress);
					}
				}


				Element publisherElectronicAddress = (Element)publisher.getChild("e-address",ceNamespace);
				if(publisherElectronicAddress != null)
				{
					if(pElectronicABuffer.length()>0)
					{
						pElectronicABuffer.append((Constants.AUDELIMITER));
					}
					String peaType = publisherElectronicAddress.getAttributeValue("type");
					if(peaType == null)
					{
						peaType = "email";
					}
					String publisherElectronicAddresValue = publisherElectronicAddress.getText();
					if(publisherElectronicAddresValue != null)
					{
						pElectronicABuffer.append(peaType+Constants.IDDELIMITER+publisherElectronicAddresValue);
					}
				}
			}

		}

		record.put("PUBLISHERNAME",pnBuffer.toString());
		record.put("PUBLISHERADDRESS",paBuffer.toString());
		record.put("PUBLISHERELECTRONICADDRESS",pElectronicABuffer.toString());
	}

	private void setAdditionalSrcinfo(Hashtable record,Element additionalSrcinfo) throws Exception
	{
		//reportinfo
		Element reportinfo = additionalSrcinfo.getChild("reportinfo",noNamespace);
		if(reportinfo != null && reportinfo.getChild("reportnumber",noNamespace)!=null)
		{
			//System.out.println("IN REPORTINFO" + additionalSrcinfo.getChildTextTrim("reportinfo"));

			record.put("REPORTNUMBER",reportinfo.getChildTextTrim("reportnumber",noNamespace));
		}

		if(additionalSrcinfo.getChild("conferenceinfo",noNamespace) != null)
		{
			Element conferenceinfo = (Element)additionalSrcinfo.getChild("conferenceinfo",noNamespace);
			//System.out.println("IN  conferenceinfo");
			if(conferenceinfo != null)
			{
				Element confevent = (Element)conferenceinfo.getChild("confevent",noNamespace);
				//System.out.println("IN  confevent");
				if(confevent != null)
				{
					//confname

					Element confnumber = confevent.getChild("confnumber",noNamespace);
					Element confname = confevent.getChild("confname",noNamespace);
					Element confseriestitle = confevent.getChild("confseriestitle",noNamespace);
					Element conftheme = confevent.getChild("conftheme",noNamespace);
					Element conforganization = confevent.getChild("conforganization",noNamespace);
					Element confURL = confevent.getChild("confURL",noNamespace);
					//System.out.println("CONFNAME " + confname);

					//if(confnumber!= null)
					//{
					//	record.put("CONFNAME",dictionary.mapEntity(getMixData(confnumber.getContent())+" "+getMixData(confname.getContent())));
					//}
					//else if(confname!= null)
					/* based on Frank request, remove confnumber from conference name column 10/23/2014 */
					
					//added by hmo at 02/08/2021
					if(conforganization!= null)
					{
						record.put("CONFORGANIZATIONE",conforganization.getTextTrim());
					}
					
					//added by hmo at 02/08/2021
					if(confURL!= null)
					{
						record.put("CONFURL",confURL.getTextTrim());
					}
					
					//added by hmo at 02/08/2021
					if(conftheme!= null)
					{
						record.put("CONFTHEME",conftheme.getTextTrim());
					}
					
					//added by hmo at 02/08/2021
					if(confnumber!= null)
					{
						record.put("CONFNUMBER",confnumber.getTextTrim());
					}
					
					if(confname!= null)
					{
						record.put("CONFNAME",DataLoadDictionary.mapEntity(getMixData(confname.getContent())));
					}
					
					if(confseriestitle!= null)
					{
						record.put("CONFSERIESTITLE",confseriestitle.getTextTrim());
					}
					

					//confcatnumber

					String confcatnumber = confevent.getChildTextTrim("confcatnumber",noNamespace);
					if(confcatnumber != null)
					{
						//System.out.println("CONFCATNUMBER "+confcatnumber);
						record.put("CONFCATNUMBER",confcatnumber);
					}

					//confcode

					String confcode = confevent.getChildTextTrim("confcode",noNamespace);
					if(confcode != null)
					{
						//System.out.println("CONFCODE "+confcode);
						record.put("CONFCODE",confcode);
					}

					//conflocation

					Element conflocation = confevent.getChild("conflocation",noNamespace);
					if(conflocation != null)
					{
						String conflocationString = getConfAffiliation(conflocation);
						if(conflocationString != null && conflocationString.length()>1)
						{
							//System.out.println("CONFLOCATION "+conflocationString);
							record.put("CONFLOCATION",conflocationString);
						}
					}

					//confdate

					Element condate = confevent.getChild("confdate",noNamespace);
					if(condate != null)
					{
						String date_text = condate.getChildTextTrim("date-text",noNamespace);
						Element startdate = condate.getChild("startdate",noNamespace);
						Element enddate   = condate.getChild("enddate",noNamespace);
						String startdateString = null;
						String enddateString   = null;
						if(date_text != null)
						{
							//System.out.println("CONDATE "+date_text);
							record.put("CONFDATE",date_text);
						}
						else
						{
							if(startdate != null)
							{
								startdateString = getDateString(startdate.getAttributeValue("year"),startdate.getAttributeValue("month"),startdate.getAttributeValue("day"));
							}

							if(enddate != null)
							{
								enddateString = getDateString(enddate.getAttributeValue("year"),enddate.getAttributeValue("month"),enddate.getAttributeValue("day"));
							}

							if(startdateString != null && enddateString != null)
							{
								record.put("CONFDATE",startdateString+" - "+enddateString);
							}
							else if(startdateString != null)
							{
								record.put("CONFDATE",startdateString);
							}
							else if(enddateString != null)
							{
								record.put("CONFDATE",enddateString);
							}

							//System.out.println("CONFDATE "+startdateString+" - "+enddateString);
						}

					}


					//confsponsors
					Element confsponsors = confevent.getChild("confsponsors",noNamespace);
					if(confsponsors != null)
					{
						List confsponsorsList = confsponsors.getChildren("confsponsor",noNamespace);
						StringBuffer confsponsorsBuffer = new StringBuffer();
						for(int i=0;i<confsponsorsList.size();i++)
						{
							Element confsponsor = (Element)confsponsorsList.get(i);
							if(confsponsorsBuffer.length()>0)
							{
								confsponsorsBuffer.append((Constants.AUDELIMITER));
							}
							if(confsponsor != null)
							{
								confsponsorsBuffer.append(confsponsor.getTextTrim());
							}
						}
						record.put("CONFSPONSORS",DataLoadDictionary.mapEntity(confsponsorsBuffer.toString()));
					}
				}

				//confpublication
				Element confpublication = (Element)conferenceinfo.getChild("confpublication",noNamespace);
				if(confpublication != null)
				{
					//System.out.println("IN confpublication");
					setConfpublication(record,confpublication);
				}
			}

		}
	}

	private void setConfpublication(Hashtable record,Element confpublication) throws Exception
	{
		Element confeditors = confpublication.getChild("confeditors",noNamespace);
		String procpartno = confpublication.getChildTextTrim("procpartno",noNamespace);
		String procpagerange = confpublication.getChildTextTrim("procpagerange",noNamespace);
		String procpagecount = confpublication.getChildTextTrim("procpagecount",noNamespace);
		if(procpartno!=null)
		{
			//System.out.println("procpartno "+procpartno);
			record.put("CONFERENCEPARTNUMBER",procpartno);
		}
		if(procpagerange != null)
		{
			//System.out.println("CONFERENCEPAGERANGE "+procpagerange);
			record.put("CONFERENCEPAGERANGE",procpagerange);
		}
		if(procpagecount != null)
		{
			//System.out.println("procpagecount "+procpagecount);
			record.put("CONFERENCEPAGECOUNT",procpagecount);
		}
		if(confeditors != null)
		{
			Element editors = confeditors.getChild("editors",noNamespace);
			if(editors!=null)
			{
				String editor = getEditors(editors);
				if(editor != null)
				{
					//System.out.println("editor "+editor);
					record.put("CONFERENCEEDITOR",DataLoadDictionary.mapEntity(editor));
				}
			}

			String editororganization = confeditors.getChildTextTrim("editororganization",noNamespace);
			if(editororganization != null)
			{
				//System.out.println("editororganization "+editororganization);
				record.put("CONFERENCEORGANIZATION",DataLoadDictionary.mapEntity(editororganization));
			}

			String editoraddress = confeditors.getChildTextTrim("editoraddress",noNamespace);
			if(editoraddress != null)
			{
				//System.out.println("editoraddress "+editoraddress);
				record.put("CONFERENCEEDITORADDRESS",DataLoadDictionary.mapEntity(editoraddress));
			}

		}

	}


	public String getConfAffiliation(Element affiliation)
	{
		StringBuffer affBuffer = new StringBuffer();

		//afid
		if(affiliation!=null)
		{
			if(affiliation.getAttributeValue("afid")!=null)
			{
				affBuffer.append(affiliation.getAttributeValue("afid"));
			}

			affBuffer.append(Constants.IDDELIMITER);
			// Text
			String afString = affiliation.getTextTrim();
			Element afElem =(Element) affiliation.getChild("text",ceNamespace);
			Element venue = (Element)affiliation.getChild("venue",noNamespace);

			if(venue!= null)
			{
				affBuffer.append(DataLoadDictionary.mapEntity(venue.getTextTrim()));
			}
			affBuffer.append(Constants.IDDELIMITER);

			if(afElem!=null)
			{
			    affBuffer.append(DataLoadDictionary.mapEntity(getMixData(afElem.getContent())));
			}
			
			if(afString!=null && afString.trim().length()>0)
			{
				if(afElem!=null)
				{
					affBuffer.append("; ");
				}
				affBuffer.append(afString);
				System.out.println("AFF BODY is not empty, It has "+afString);
			}
			affBuffer.append(Constants.IDDELIMITER);

			List organizationList = (List)affiliation.getChildren("organization",noNamespace);
			StringBuffer organizationBuffer = new StringBuffer();
			if(organizationList!=null)
			{
				for(int i=0;i<organizationList.size();i++)
				{
					Element organization = (Element)organizationList.get(i);
					if(organization!=null)
					{
						if(organizationBuffer.length()>0)
						{
							organizationBuffer.append("; ");
						}
						organizationBuffer.append(DataLoadDictionary.mapEntity(getMixData(organization.getContent())));
					}
				}
			}
			affBuffer.append(organizationBuffer.toString());
			affBuffer.append(Constants.IDDELIMITER);

			if(affiliation.getChild("address-part",noNamespace)!=null)
			{
				affBuffer.append(DataLoadDictionary.mapEntity(getMixData(affiliation.getChild("address-part",noNamespace).getContent())));
			}

			affBuffer.append(Constants.IDDELIMITER);

			StringBuffer cityGroupBuffer = new StringBuffer();
			if(affiliation.getChild("city-group",noNamespace)!=null)
			{
				cityGroupBuffer.append(affiliation.getChildTextTrim("city-group",noNamespace));
			}
			else
			{
				String city = affiliation.getChildTextTrim("city",noNamespace);
				String state = affiliation.getChildTextTrim("state",noNamespace);
				String postalCode = affiliation.getChildTextTrim("postal-code",noNamespace);

				if(city != null)
				{
					cityGroupBuffer.append(DataLoadDictionary.mapEntity(city));
					if(state != null || postalCode != null)
					{
						cityGroupBuffer.append(", ");
					}
				}

				if(state != null)
				{
					cityGroupBuffer.append(DataLoadDictionary.mapEntity(state)+" ");
				}
				if(postalCode != null)
				{
					cityGroupBuffer.append(DataLoadDictionary.mapEntity(postalCode));
				}
			}

			if(cityGroupBuffer.length()>0)
			{
				affBuffer.append(cityGroupBuffer.toString());
			}

			affBuffer.append(Constants.IDDELIMITER);

			if(affiliation.getAttributeValue("country")!=null)
			{
				affBuffer.append(DataLoadDictionary.mapEntity(affiliation.getAttributeValue("country")));
			}
			
			///*use for CAFE data
			
			if(affiliation.getAttributeValue("afid") != null)
			{
				affBuffer.append(affiliation.getAttributeValue("afid"));
			}
			affBuffer.append(Constants.IDDELIMITER);
			if(affiliation.getAttributeValue("dptid") != null)
			{
				affBuffer.append(affiliation.getAttributeValue("dptid"));
			}
			affBuffer.append(Constants.IDDELIMITER);
			//*/
			
			affBuffer.append((Constants.AUDELIMITER));
		}
		return affBuffer.toString();
	}


	private String getAffiliation(Element affiliation)
	{
		StringBuffer affBuffer = new StringBuffer();

		//afid
		if(affiliation!=null)
		{
			if(affiliation.getAttributeValue("afid")!=null)
			{
				affBuffer.append(affiliation.getAttributeValue("afid"));
			}

			affBuffer.append(Constants.IDDELIMITER);
			// Text
			Element afElem =(Element) affiliation.getChild("text",ceNamespace);
			Element venue = (Element)affiliation.getChild("venue",noNamespace);

			if(venue!= null)
			{
				affBuffer.append(DataLoadDictionary.mapEntity(venue.getTextTrim()));
			}
			affBuffer.append(Constants.IDDELIMITER);

			if(afElem!=null)
			{
				affBuffer.append(DataLoadDictionary.mapEntity(getMixData(afElem.getContent())));
			}
			else
			{
				//organization

				List organizationList = (List)affiliation.getChildren("organization",noNamespace);
				StringBuffer organizationBuffer = new StringBuffer();
				if(organizationList!=null)
				{
					for(int i=0;i<organizationList.size();i++)
					{
						Element organization = (Element)organizationList.get(i);
						if(organization!=null)
						{
							if(organizationBuffer.length()>0)
							{
								organizationBuffer.append("; ");
							}
							organizationBuffer.append(DataLoadDictionary.mapEntity(getMixData(organization.getContent())));
						}
					}
				}
				affBuffer.append(organizationBuffer.toString());
				affBuffer.append(Constants.GROUPDELIMITER);

				if(affiliation.getChild("address-part",noNamespace)!=null)
				{
					affBuffer.append(DataLoadDictionary.mapEntity(getMixData(affiliation.getChild("address-part",noNamespace).getContent())));
				}

				affBuffer.append(Constants.GROUPDELIMITER);

				StringBuffer cityGroupBuffer = new StringBuffer();
				if(affiliation.getChild("city-group",noNamespace)!=null)
				{
					cityGroupBuffer.append(DataLoadDictionary.mapEntity(affiliation.getChildTextTrim("city-group",noNamespace)));
				}
				else
				{
					String city = affiliation.getChildTextTrim("city",noNamespace);
					String state = affiliation.getChildTextTrim("state",noNamespace);
					String postalCode = affiliation.getChildTextTrim("postal-code",noNamespace);

					if(city != null)
					{
						cityGroupBuffer.append(DataLoadDictionary.mapEntity(city));
						if(state != null || postalCode != null)
						{
							cityGroupBuffer.append(", ");
						}
					}

					if(state != null)
					{
						cityGroupBuffer.append(DataLoadDictionary.mapEntity(state)+" ");
					}
					if(postalCode != null)
					{
						cityGroupBuffer.append(DataLoadDictionary.mapEntity(postalCode));
					}
				}

				if(cityGroupBuffer.length()>0)
				{
					affBuffer.append(cityGroupBuffer.toString());
				}

				affBuffer.append(Constants.GROUPDELIMITER);

				if(affiliation.getAttributeValue("country")!=null)
				{
					affBuffer.append(DataLoadDictionary.mapEntity(affiliation.getAttributeValue("country")));
				}
			}
				
			///* USE for CAFE data
			affBuffer.append(Constants.IDDELIMITER);
			if(affiliation.getAttributeValue("afid") != null)
			{
				affBuffer.append(affiliation.getAttributeValue("afid"));
			}
			affBuffer.append(Constants.IDDELIMITER);
			if(affiliation.getAttributeValue("dptid") != null)
			{
				affBuffer.append(affiliation.getAttributeValue("dptid"));
			}
				
			//*/
			
		}
//		System.out.println("AFF:: "+affBuffer.toString());
		return affBuffer.toString();
	}

	private String getEditors(Element editors)
	{
		List editorList = editors.getChildren("editor",noNamespace);
		StringBuffer editorBuffer = new StringBuffer();
		for(int i=0;i<editorList.size();i++)
		{
			Element editor = (Element)editorList.get(i);
			if(editorBuffer.length()>0)
			{
				editorBuffer.append((Constants.AUDELIMITER));
			}
			editorBuffer.append(getPersonalName(i,editor));
		}
		return editorBuffer.toString();
	}

	private String getPersonalName(int id,Element editor)
	{
		StringBuffer editorBuffer = new StringBuffer();
		//System.out.println("in editor");
		if(editor != null)
		{
			editorBuffer.append(id);
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChildTextTrim("initials",ceNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(editor.getChildTextTrim("initials",ceNamespace)));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChild("indexed-name",ceNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(getMixData(editor.getChild("indexed-name",ceNamespace).getContent())));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChildTextTrim("degrees",ceNamespace)!=null)
			{
				editorBuffer.append(editor.getChildTextTrim("degrees",ceNamespace));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChild("surname",ceNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(getMixData(editor.getChild("surname",ceNamespace).getContent())));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChild("given-name",ceNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(getMixData(editor.getChild("given-name",ceNamespace).getContent())));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChildTextTrim("suffix",ceNamespace)!=null)
			{
				editorBuffer.append(editor.getChildTextTrim("suffix",ceNamespace));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChild("nametext",noNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(getMixData(editor.getChild("nametext",noNamespace).getContent())));
			}
			editorBuffer.append(Constants.IDDELIMITER);
			if(editor.getChild("text",ceNamespace)!=null)
			{
				editorBuffer.append(DataLoadDictionary.mapEntity(getMixData(editor.getChild("text",ceNamespace).getContent())));
			}
		}

		return editorBuffer.toString();
	}

	private String convertMonth(String month)
	{
		String monthString = null;
		try
		{
		 	int intMonth = Integer.parseInt(month);

		 	switch (intMonth)
		 	{
		 		case 1:   monthString =  "January"; 	break;
		 		case 2:   monthString =  "February"; 	break;
		 		case 3:   monthString =  "March"; 		break;
		 		case 4:   monthString =  "April"; 		break;
		 		case 5:   monthString =  "May"; 		break;
		 		case 6:   monthString =  "June"; 		break;
		 		case 7:   monthString =  "July"; 		break;
				case 8:   monthString =  "August"; 		break;
				case 9:   monthString =  "September"; 	break;
				case 10:  monthString =  "October"; 	break;
				case 11:  monthString = "November"; 	break;
		 		case 12:  monthString = "December"; 	break;
		 		default:  monthString =  month;			break;
			}
			//System.out.println("month= "+month+" intMonth= "+intMonth+" monthString= "+monthString);

		}
		catch(Exception e)
		{
			monthString = month;
			e.printStackTrace();
		}

		return monthString;

	}
	
	public void setCollaborationAndAffs(BdAuthors collaborationMap,
			  BdAffiliations affs,
			  Element aelement)
	{
		Element collaboration = aelement.getChild("collaboration",noNamespace);	
		List authorlist = collaboration.getChildren("member-name",noNamespace);	
		
		//affiliations
		
		Element affiliation =(Element) aelement.getChild("affiliation",noNamespace);
		
		if(affiliation != null)
		{
			BdAffiliation aff = new BdAffiliation();
			aff.setAffCountry(affiliation.getAttributeValue("country"));
			Element addresspart =(Element) affiliation.getChild("address-part",noNamespace);
			if(addresspart != null)
			{
				aff.setAffAddressPart(DataLoadDictionary.mapEntity(getMixData(addresspart.getContent())));
			}
			
			Element citygroup = (Element) affiliation.getChild("city-group",noNamespace);
			if(citygroup != null)
			{
				aff.setAffCityGroup(DataLoadDictionary.mapEntity(getMixData(citygroup.getContent())));
			}
			
			Element city = (Element) affiliation.getChild("city",noNamespace);
			if(city != null)
			{
				aff.setAffCity(DataLoadDictionary.mapEntity(getMixData(city.getContent())));
			}
			
			Element state = (Element) affiliation.getChild("state",noNamespace);
			if(state != null)
			{
				aff.setAffState(DataLoadDictionary.mapEntity(state.getText()));
			}
			
			List postalcode = affiliation.getChildren("postal-code",noNamespace);
			StringBuffer zipbuf = new StringBuffer();
			for (int i = 0; i<postalcode.size(); i++)
			{
				Element elmpostalcode = (Element)postalcode.get(i);
				if(postalcode != null)
				{
					Attribute zip = elmpostalcode.getAttribute("zip");
					if(zip != null)
					{
					  zipbuf.append((String) zip.getValue());
					
					  if(i < postalcode.size()-1)
					  {
					      zipbuf.append(", ");
					  }
					  //System.out.println("ZIP1="+zipbuf.toString());
					}
					
					String zipvalue=elmpostalcode.getTextTrim();
					if(zipvalue!=null)
					{			        	
						zipbuf.append(DataLoadDictionary.mapEntity(zipvalue));
						 if(i < postalcode.size()-1)
					   {
					          zipbuf.append(", ");
					   }
						 //System.out.println("ZIP2="+zipbuf.toString());
					}
				}
			}
			
			if(zipbuf.length() > 0)
			{			
				aff.setAffPostalCode(DataLoadDictionary.mapEntity(DataLoadDictionary.mapEntity(zipbuf.toString())));
			}
			
			Element text = (Element) affiliation.getChild("text",ceNamespace);
			if(text != null)
			{
				aff.setAffText(DataLoadDictionary.mapEntity(getMixData(text.getContent())));
			}
			
			// organization  - mulity element
			List organization = affiliation.getChildren("organization",noNamespace);
			for (int i = 0; i < organization.size(); i++)
			{
				Element oe = (Element) organization.get(i);
				aff.addAffOrganization(DataLoadDictionary.mapEntity(getMixData(oe.getContent())));
				//System.out.println("AffOrganization="+oe.getText());
			}
			
			this.affid = this.affid+1;
			aff.setAffid(this.affid);
			
			if(affiliation.getAttributeValue("afid")!=null)
			{
				aff.setAffiliationId(affiliation.getAttributeValue("afid"));
			}
			
			if(affiliation.getAttributeValue("dptid")!=null)
			{
				aff.setAffDepartmentId(affiliation.getAttributeValue("dptid"));
			}
			
			affs.addAff(aff);
		}
		
		//end of affiliation
		
		//begin authors
		for (int e = 0; e< authorlist.size(); e++)
		{
			Element agroupelement =(Element) authorlist.get(e);
			
			BdAuthor aus = new BdAuthor();
			Element author = (Element) authorlist.get(e);
			List atr = author.getAttributes();
			Attribute sec = author.getAttribute("seq");
			aus.setAffid(this.affid);
			
			if(sec != null)
			{
				String secstr = (String) sec.getValue();
				aus.setSec(secstr);
			}
			else
			{
				aus.setSec(Integer.toString(e));
			}
			
			if(e==0)
			{
				Element indexedName = collaboration.getChild("indexed-name",ceNamespace );
				if(indexedName != null)
				{
					aus.setSurname(DataLoadDictionary.mapEntity(getMixData(indexedName.getContent())));
				}
				
				Element textName = collaboration.getChild("text",ceNamespace );
				if(textName != null)
				{
					aus.setGivenName(DataLoadDictionary.mapEntity(getMixData(textName.getContent())));
				}
			}
			else
			{	
				Attribute auid = author.getAttribute("orcid");
				Attribute authorid = author.getAttribute("auid");
				StringBuffer authoridBuffer = new StringBuffer();
				if(auid != null || authorid!=null)
				{
					if(auid != null)
					{
						String auidstr = (String) auid.getValue();
						authoridBuffer.append(auidstr);
					}
									
					if(authorid!=null)
					{
						String authoridstr = (String)authorid.getValue();
						authoridBuffer.append(","+authoridstr);
					}						
				}
				else
				{
					authoridBuffer.append(e);
				}
			
				if(authoridBuffer.length()>0)
				{
					aus.setAuid(authoridBuffer.toString());
					//System.out.println("AUTHORID="+authoridBuffer.toString());
				}								
				
				Element initials = author.getChild("initials",ceNamespace );			
				
				if(initials != null)
				{
					aus.setInitials(DataLoadDictionary.mapEntity(initials.getText()));
				}
				
				Element degrees = author.getChild("degrees", ceNamespace);
				if(degrees != null)
				{
					aus.setDegrees(DataLoadDictionary.mapEntity(degrees.getText()));
				
				}
			
				Element surname = author.getChild("surname", ceNamespace);
				
				if(surname != null)
				{
					//System.out.println("surname="+surname.getText());
					aus.setSurname(DataLoadDictionary.mapEntity(getMixData(surname.getContent())));
				}
				
				Element givenName = author.getChild("given-name", ceNamespace);
							
				if(givenName != null)
				{
					//System.out.println("given-name="+givenName.getText());
					aus.setGivenName(DataLoadDictionary.mapEntity(getMixData(givenName.getContent())));
					//System.out.println("given-name1="+dictionary.mapEntity(getMixData(givenName.getContent())));
				}
				
				Element suffix = author.getChild("suffix",ceNamespace);
				if(suffix != null)
				{
					aus.setSuffix(DataLoadDictionary.mapEntity(suffix.getText()));
				}
				
				Element nametext= author.getChild("text", ceNamespace);
				if(nametext != null)
				{
					aus.setNametext(DataLoadDictionary.mapEntity(getMixData(nametext.getContent())));
				}
				
				Element prefferedName= author.getChild("preffered-name", ceNamespace);
				
				// prefferedName block
			
				if(prefferedName != null)
				{
					Element prefferedNameInitials = prefferedName.getChild("initials",ceNamespace);
					if(prefferedNameInitials != null)
					{
						aus.setPrefnameInitials(DataLoadDictionary.mapEntity(getMixData(prefferedNameInitials.getContent())));
					}
					
					Element prefferedNameIndexedname = prefferedName.getChild("indexed_name",ceNamespace);
					if(prefferedNameIndexedname != null)
					{
					
						aus.setPrefnameIndexedname(DataLoadDictionary.mapEntity(getMixData(prefferedNameIndexedname.getContent())));
					}
					
					Element prefferedNameDegrees = prefferedName.getChild("degree", ceNamespace);
					if(prefferedNameDegrees != null)
					{
						aus.setPrefnameDegrees(DataLoadDictionary.mapEntity(prefferedNameDegrees.getText()));
					}
					
					Element prefferedNameSurname = prefferedName.getChild("surname", ceNamespace);
					if(prefferedNameSurname != null)
					{
						aus.setPrefnameSurname(DataLoadDictionary.mapEntity(getMixData(prefferedNameSurname.getContent())));
					}
					Element prefferedNameGivenname = prefferedName.getChild("given-name", ceNamespace);
					
					if(prefferedNameGivenname != null)
					{
						aus.setPrefnameGivenname(DataLoadDictionary.mapEntity(getMixData(prefferedNameGivenname.getContent())));
					}
				}
				// end of prefferedName block
				
				Element eaddress = author.getChild("e-address", ceNamespace);
				if(eaddress != null)
				{
					aus.setEaddress(eaddress.getTextTrim());
				}
				
				// Author alias
				Element alias = author.getChild("alias", ceNamespace);
				if(alias != null)
				{
					aus.setAlias(alias.getTextTrim());
					//System.out.println("ALIAS1= "+alias.getTextTrim());
				}
				
				// Author alt-name
				Element altName = author.getChild("alt-name", ceNamespace);
				if(altName != null)
				{
					aus.setAltName(DataLoadDictionary.mapEntity(altName.getTextTrim()));
					//System.out.println(this.accessNumber+" altName1= "+altName.getTextTrim());
				}
				
				
				if(aus!=null) {
					//System.out.println("SEARCH NAME="+aus.getSearchValue());
					collaborationMap.addCpxaus(aus);
				}
			}
		}		
	}

	private void outputIntoCharNumber(String input)
	{
		//System.out.println("InputString="+input+" *** "+StringEscapeUtils.escapeHtml4(input));
		if(input !=null)
		{
			 //char[] gfg = input.toCharArray(); 
			 int[] gfg= DataLoadDictionary.toCodePointArray(input);
		     for (int i = 0; i < gfg.length; i++) { 
		            System.out.println(gfg[i]+" :: "+gfg[i]); 
		     } 
		}
	}
	
	public void setAuthorAndAffs(BdAuthors ausmap,
								  BdAffiliations affs,
								  Element aelement)
	{
		List authorlist = aelement.getChildren("author",noNamespace);

		if(authorlist==null || authorlist.size()==0)
		{
			authorlist = aelement.getChildren("contributor",noNamespace);
		}
		/*
		if(authorlist==null || authorlist.size()==0)
		{
			authorlist = aelement.getChildren("collaboration",noNamespace);
		}
		*/

		//affiliations

		Element affiliation =(Element) aelement.getChild("affiliation",noNamespace);

		if(affiliation != null)
		{
			BdAffiliation aff = new BdAffiliation();
			aff.setAffCountry(affiliation.getAttributeValue("country"));
			Element addresspart =(Element) affiliation.getChild("address-part",noNamespace);
			if(addresspart != null)
			{
				aff.setAffAddressPart(DataLoadDictionary.mapEntity(getMixData(addresspart.getContent())));
			}

			Element citygroup = (Element) affiliation.getChild("city-group",noNamespace);
			if(citygroup != null)
			{
			    aff.setAffCityGroup(DataLoadDictionary.mapEntity(getMixData(citygroup.getContent())));
			}

			Element city = (Element) affiliation.getChild("city",noNamespace);
			if(city != null)
			{
			    aff.setAffCity(DataLoadDictionary.mapEntity(getMixData(city.getContent())));
			}

			Element state = (Element) affiliation.getChild("state",noNamespace);
			if(state != null)
			{
				aff.setAffState(DataLoadDictionary.mapEntity(state.getText()));
			}

			List postalcode = affiliation.getChildren("postal-code",noNamespace);
			StringBuffer zipbuf = new StringBuffer();
			for (int i = 0; i<postalcode.size(); i++)
			{
			    Element elmpostalcode = (Element)postalcode.get(i);
			    if(postalcode != null)
			    {
			        Attribute zip = elmpostalcode.getAttribute("zip");
			        if(zip != null)
			        {
			            zipbuf.append((String) zip.getValue());

			            if(i < postalcode.size()-1)
			            {
			                zipbuf.append(", ");
			            }
			            //System.out.println("ZIP1="+zipbuf.toString());
			        }
			        
			        String zipvalue=elmpostalcode.getTextTrim();
			        if(zipvalue!=null)
			        {			        	
			        	zipbuf.append(DataLoadDictionary.mapEntity(zipvalue));
			        	 if(i < postalcode.size()-1)
				         {
				                zipbuf.append(", ");
				         }
			        	 //System.out.println("ZIP2="+zipbuf.toString());
			        }
			    }
			}
			if(zipbuf.length() > 0)
			{
				
			    aff.setAffPostalCode(DataLoadDictionary.mapEntity(DataLoadDictionary.mapEntity(zipbuf.toString())));
			}

			Element text = (Element) affiliation.getChild("text",ceNamespace);
			if(text != null)
			{
				aff.setAffText(DataLoadDictionary.mapEntity(getMixData(text.getContent())));
			}

			// organization  - mulity element
			List organization = affiliation.getChildren("organization",noNamespace);
			for (int i = 0; i < organization.size(); i++)
			{
				Element oe = (Element) organization.get(i);
				aff.addAffOrganization(DataLoadDictionary.mapEntity(getMixData(oe.getContent())));
			}
			
			this.affid = this.affid+1;
			aff.setAffid(this.affid);
			
			if(affiliation.getAttributeValue("afid")!=null)
			{
				aff.setAffiliationId(affiliation.getAttributeValue("afid"));
			}
			
			if(affiliation.getAttributeValue("dptid")!=null)
			{
				aff.setAffDepartmentId(affiliation.getAttributeValue("dptid"));
			}
			
			affs.addAff(aff);
		}

		//end of affiliation

		//begin authors
		for (int e = 0; e< authorlist.size(); e++)
		{
	        Element agroupelement =(Element) authorlist.get(e);

            BdAuthor aus = new BdAuthor();
            Element author = (Element) authorlist.get(e);
            List atr = author.getAttributes();
		    Attribute sec = author.getAttribute("seq");
		    aus.setAffid(this.affid);

		    if(sec != null)
		    {
		        String secstr = (String) sec.getValue();
		        aus.setSec(secstr);
		    }

		    Attribute auid = author.getAttribute("orcid");
		    Attribute authorid = author.getAttribute("auid");
		    StringBuffer authoridBuffer = new StringBuffer();
		    if(auid != null || authorid!=null)
		    {
		    	if(auid != null)
		    	{
		    		String auidstr = (String) auid.getValue();
		    		authoridBuffer.append(auidstr);
		    	}
		    	
		    	//authoridBuffer.append(",");
		    	
		    	if(authorid!=null)
		    	{
		    		String authoridstr = (String)authorid.getValue();
		    		authoridBuffer.append(","+authoridstr);
		    	}
		        
		        
		    }
		    
		    if(authoridBuffer.length()>0)
		    {
		    	aus.setAuid(authoridBuffer.toString());
		    	//System.out.println("AUTHORID="+authoridBuffer.toString());
		    }

		    Element indexedName = author.getChild("indexed-name",ceNamespace );
		    if(indexedName != null)
		    {
		        aus.setIndexedName(DataLoadDictionary.mapEntity(getMixData(indexedName.getContent())));
		    }

		    Element initials = author.getChild("initials",ceNamespace);
		    if(initials != null)
		    {
		    	//System.out.println("initials="+initials);
			    aus.setInitials(DataLoadDictionary.mapEntity(initials.getText()));
		    }
		   

		    Element degrees = author.getChild("degrees", ceNamespace);
		    if(degrees != null)
		    {
		        aus.setDegrees(DataLoadDictionary.mapEntity(degrees.getText()));

		    }

		    Element surname = author.getChild("surname", ceNamespace);
		    if(surname != null)
		    {
		    	String sureNameString = getMixData(surname.getContent());
		    	//System.out.println("sureNameString="+sureNameString);
		    	//outputIntoCharNumber(sureNameString);	
		        aus.setSurname(DataLoadDictionary.mapEntity(getMixData(surname.getContent())));
		    	//aus.setSurname(getMixData(surname.getContent()));
		    }

		    Element givenName = author.getChild("given-name", ceNamespace);
		    if(givenName != null)
		    {
		    	String givenNameString = getMixData(givenName.getContent());
		    	//outputIntoCharNumber(givenNameString);		
		        aus.setGivenName(DataLoadDictionary.mapEntity(getMixData(givenName.getContent())));
		    	//aus.setGivenName(getMixData(givenName.getContent()));
		    }

		    Element suffix = author.getChild("suffix",ceNamespace);
		    if(suffix != null)
		    {
		        aus.setSuffix(DataLoadDictionary.mapEntity(suffix.getText()));
		    }

		    Element nametext= author.getChild("nametext", ceNamespace);
		    if(nametext != null)
		    {
		        aus.setNametext(DataLoadDictionary.mapEntity(getMixData(nametext.getContent())));
		    }

		    Element prefferedName= author.getChild("preffered-name", ceNamespace);

		    // prefferedName block

		    if(prefferedName != null)
		    {
		       Element prefferedNameInitials = prefferedName.getChild("initials",ceNamespace);
		       if(prefferedNameInitials != null)
		       {
		           aus.setPrefnameInitials(DataLoadDictionary.mapEntity(getMixData(prefferedNameInitials.getContent())));
		       }

		       Element prefferedNameIndexedname = prefferedName.getChild("indexed_name",ceNamespace);
		       if(prefferedNameIndexedname != null)
		       {

		           aus.setPrefnameIndexedname(DataLoadDictionary.mapEntity(getMixData(prefferedNameIndexedname.getContent())));
		       }

		       Element prefferedNameDegrees = prefferedName.getChild("degree", ceNamespace);
		       if(prefferedNameDegrees != null)
		       {
		           aus.setPrefnameDegrees(DataLoadDictionary.mapEntity(prefferedNameDegrees.getText()));
		       }

		       Element prefferedNameSurname = prefferedName.getChild("surname", ceNamespace);
		       if(prefferedNameSurname != null)
		       {
		           aus.setPrefnameSurname(DataLoadDictionary.mapEntity(getMixData(prefferedNameSurname.getContent())));
		       }
		       Element prefferedNameGivenname = prefferedName.getChild("given-name", ceNamespace);

		       if(prefferedNameGivenname != null)
		       {
		           aus.setPrefnameGivenname(DataLoadDictionary.mapEntity(getMixData(prefferedNameGivenname.getContent())));
		       }
		    }
		    // end of prefferedName block

		    Element eaddress = author.getChild("e-address", ceNamespace);
		    if(eaddress != null)
		    {
		        aus.setEaddress(eaddress.getTextTrim());
		    }
		    
		    // Author alias
		    Element alias = author.getChild("alias", ceNamespace);
		    if(alias != null)
		    {
		        aus.setAlias(alias.getTextTrim());
		        //System.out.println("ALIAS1= "+alias.getTextTrim());
		    }
		    
		    // Author alt-name
		    Element altName = author.getChild("alt-name", ceNamespace);
		    if(altName != null)
		    {
		        aus.setAltName(DataLoadDictionary.mapEntity(altName.getTextTrim()));
		        //System.out.println(this.accessNumber+" altName1= "+altName.getTextTrim());
		    }
		    
	

		    ausmap.addCpxaus(aus);
		}

	}
	
	

	public String auffToStringBuffer(BdAffiliations aufflist,
		        					  StringBuffer secondAffGroup)
	{
		StringBuffer bufaffiliations = new StringBuffer();
		StringBuffer returnbuf = new StringBuffer();
		if (aufflist != null && aufflist.getAffmap() != null)
		{
			Iterator itr = aufflist.getAffmap().keySet().iterator();
			while (itr.hasNext())
			{
				BdAffiliation aAffiliation =(BdAffiliation) itr.next();

				if(aAffiliation.getAffid() != 0)
				{
					bufaffiliations.append(aAffiliation.getAffid());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffOrganization() != null)
				{
					//System.out.println("Organization="+aAffiliation.getAffOrganization());
					bufaffiliations.append(aAffiliation.getAffOrganization());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffCityGroup() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCityGroup());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if (aAffiliation.getAffCountry() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCountry());
				}
				bufaffiliations.append(Constants.IDDELIMITER);

				if (aAffiliation.getAffAddressPart() != null)
				{
					bufaffiliations.append(aAffiliation.getAffAddressPart());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffCity() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCity());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffState() != null)
				{
					bufaffiliations.append(aAffiliation.getAffState());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffPostalCode() != null)
				{
					bufaffiliations.append(aAffiliation.getAffPostalCode());

				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffText() != null)
				{
					bufaffiliations.append(aAffiliation.getAffText());

				}
				bufaffiliations.append(Constants.IDDELIMITER);
				
				///*use for CAFE data
				
				if(aAffiliation.getAffiliationId() != null)
				{
					bufaffiliations.append(aAffiliation.getAffiliationId());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffDepartmentId() != null)
				{
					bufaffiliations.append(aAffiliation.getAffDepartmentId());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				//*/
				bufaffiliations.append((Constants.AUDELIMITER));

			}
		}

		if(bufaffiliations.length() < 3500 )
		{
			return bufaffiliations.toString();
		}
		else if(bufaffiliations.length() >= 3500)
		{

			int endFirstAffGroupMarker = bufaffiliations.lastIndexOf((Constants.AUDELIMITER), 3500)+1;
			String firstAffGroup = bufaffiliations.substring(0, endFirstAffGroupMarker);
			if(bufaffiliations.length() > 7500)
			{
				String secAffGroup = bufaffiliations.substring(endFirstAffGroupMarker,(endFirstAffGroupMarker+3500));
				int endSecondAffGroup = secAffGroup.lastIndexOf((Constants.AUDELIMITER))+1;
				secondAffGroup.append(secAffGroup.substring(0,endSecondAffGroup));
			}
			else
			{
				secondAffGroup.append(bufaffiliations.substring(endFirstAffGroupMarker));
			}
			return firstAffGroup;
		}
		return null;
	}

	public String auToStringBuffer(BdAuthors auslist)
	{
			StringBuffer bufauthor = new StringBuffer();
			StringBuffer returnbuf = new StringBuffer();
			if (auslist != null && auslist.getCpxaus() != null)
			{
				Iterator itr = auslist.getCpxaus().keySet().iterator();
				int i=0;
				while(itr.hasNext())
				{
					BdAuthor aAuthor =(BdAuthor) itr.next();
					if ( aAuthor.getSec() != null)
					{
						bufauthor.append(aAuthor.getSec());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getAuid() != null)
					{
						bufauthor.append(aAuthor.getAuid());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getAffidStr() != null)
					{
						bufauthor.append(aAuthor.getAffidStr());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getIndexedName() != null)
					{
						bufauthor.append(aAuthor.getIndexedName());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getInitials() != null)
					{
						bufauthor.append(aAuthor.getInitials());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getSurname() != null)
					{
						bufauthor.append(aAuthor.getSurname());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getDegrees() != null)
					{
						bufauthor.append(aAuthor.getDegrees());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getGivenName() != null)
					{
						bufauthor.append(aAuthor.getGivenName());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getSuffix() != null)
					{
						bufauthor.append(aAuthor.getSuffix());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getNametext() != null)
					{
						bufauthor.append(aAuthor.getNametext());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getPrefnameInitials() != null)
					{
						bufauthor.append(aAuthor.getPrefnameInitials());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getPrefnameIndexedname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameIndexedname());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getPrefnameDegrees() != null)
					{
						bufauthor.append(aAuthor.getPrefnameDegrees());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getPrefnameSurname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameSurname());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getPrefnameGivenname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameGivenname());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getEaddress() != null)
					{
						bufauthor.append(aAuthor.getEaddress());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getAuthorId() != null)
					{
						bufauthor.append(aAuthor.getAuthorId());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getAlias() != null)
					{
						bufauthor.append(aAuthor.getAlias());
						//System.out.println("ALIAS3= "+aAuthor.getAlias());
					}
					bufauthor.append(Constants.IDDELIMITER);
					if(aAuthor.getAltName() != null)
					{
						bufauthor.append(aAuthor.getAltName());
						//System.out.println("AltName3= "+aAuthor.getAltName());
					}
					bufauthor.append(Constants.IDDELIMITER);
					//System.out.println("AUTHOR BUFFER"+i+"="+bufauthor.toString());
					bufauthor.append((Constants.AUDELIMITER));
					i++;
				}
			}

			if(bufauthor.length() < 3995 )
			{
				return bufauthor.toString();
			}
			else
			{
				int endFirstAuGroupMarker = bufauthor.lastIndexOf(Constants.AUDELIMITER, 3995)+1;
				String firstAuGroup = bufauthor.substring(0, endFirstAuGroupMarker);
				return firstAuGroup;
			}
			

	}



	public String auToStringBuffer(BdAuthors auslist,
						        	StringBuffer secondAuGroup)
	{
		StringBuffer bufauthor = new StringBuffer();
		StringBuffer returnbuf = new StringBuffer();
		//System.out.println("AUTHOR="+auslist.getSearchValue().toString());
		
		if (auslist != null && auslist.getCpxaus() != null)
		{
			
			Iterator itr = auslist.getCpxaus().keySet().iterator();
			while(itr.hasNext())
			{
				BdAuthor aAuthor =(BdAuthor) itr.next();
				if ( aAuthor.getSec() != null)
				{
					bufauthor.append(aAuthor.getSec());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAuid() != null)
				{
					bufauthor.append(aAuthor.getAuid());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAffidStr() != null)
				{
					bufauthor.append(aAuthor.getAffidStr());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getIndexedName() != null)
				{
					bufauthor.append(aAuthor.getIndexedName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getInitials() != null)
				{
					bufauthor.append(aAuthor.getInitials());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getSurname() != null)
				{
					bufauthor.append(aAuthor.getSurname());
					//System.out.println("SURENAME="+aAuthor.getSurname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getDegrees() != null)
				{
					bufauthor.append(aAuthor.getDegrees());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getGivenName() != null)
				{
					bufauthor.append(aAuthor.getGivenName());
					//System.out.println("GivenName="+aAuthor.getGivenName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getSuffix() != null)
				{
					bufauthor.append(aAuthor.getSuffix());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getNametext() != null)
				{
					bufauthor.append(aAuthor.getNametext());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameInitials() != null)
				{
					bufauthor.append(aAuthor.getPrefnameInitials());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameIndexedname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameIndexedname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameDegrees() != null)
				{
					bufauthor.append(aAuthor.getPrefnameDegrees());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameSurname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameSurname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameGivenname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameGivenname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getEaddress() != null)
				{
					bufauthor.append(aAuthor.getEaddress());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAuthorId() != null)
				{
					bufauthor.append(aAuthor.getAuthorId());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAlias() != null)
				{
					bufauthor.append(aAuthor.getAlias());
					//System.out.println("ALIAS2= "+aAuthor.getAlias());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAltName() != null)
				{
					bufauthor.append(aAuthor.getAltName());
					//System.out.println("getAltName2= "+aAuthor.getAltName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				
				bufauthor.append((Constants.AUDELIMITER));
			}
		}

		if(bufauthor.length() < 3500 )
		{
			return bufauthor.toString();
		}
		else if(bufauthor.length() >= 3500)
		{
			int endFirstAuGroupMarker = bufauthor.lastIndexOf((Constants.AUDELIMITER), 3500)+1;
			String firstAuGroup = bufauthor.substring(0, endFirstAuGroupMarker);
			if(bufauthor.length() > 7500)
			{
				String secAuGroup = bufauthor.substring(endFirstAuGroupMarker,(endFirstAuGroupMarker+3500));
				int endSecondAuGroupMarker= secAuGroup.lastIndexOf(Constants.AUDELIMITER)+1;
				secondAuGroup.append(secAuGroup.substring(0,endSecondAuGroupMarker));
			}
			else
			{
				secondAuGroup.append(bufauthor.substring(endFirstAuGroupMarker));
			}
			return firstAuGroup;
		}
		return null;

	}
	private void parseDescriptorgroup(Element descriptorgroup,Hashtable record) throws Exception
	{
		String term1 = "";
		String term2 = "";
		if(descriptorgroup != null)
		{
			List descriptorsList = descriptorgroup.getChildren("descriptors",noNamespace);
			String name = null;
			for(int i=0;i<descriptorsList.size();i++)
			{
				Element descriptors = (Element)descriptorsList.get(i);
				String descriptorsType = descriptors.getAttributeValue("type");
				StringBuffer mhBuffer = new StringBuffer();
				Enumeration descriptorsKeys = descriptorsTypeTable.keys();
				while(descriptorsKeys.hasMoreElements())
				{
					String descriptorsKey = (String)descriptorsKeys.nextElement();
					if(descriptorsType != null && descriptorsType.equals(descriptorsKey))
					{
						List descriptorList = descriptors.getChildren("descriptor",noNamespace);
						for(int j=0;j<descriptorList.size();j++)
						{
							Element descriptor = (Element)descriptorList.get(j);
							String mainterm = DataLoadDictionary.mapEntity(getMixData(descriptor.getChild("mainterm",noNamespace).getContent()));
							//added link element for "<descriptors controlled="n" type="MUD">" EVOPS-1244 by hmo at 11/9/2021
							String link=null;
							String sublink=null;
							String subsublink=null;
							Element linkElement = descriptor.getChild("link",noNamespace);

							//only MUD term need to keep link element by hmo at 5/26/2022
							if(descriptorsType.equalsIgnoreCase("MUD") && linkElement!=null)
							{								
								link = DataLoadDictionary.mapEntity(getMixData(linkElement.getContent()));
								if(linkElement.getChild("sublnk",noNamespace)!=null)
								{
									sublink = DataLoadDictionary.mapEntity(getMixData(linkElement.getChild("sublink",noNamespace).getContent()));
								}
								
								if(linkElement.getChild("subsublnk",noNamespace)!=null)
								{
									subsublink = DataLoadDictionary.mapEntity(getMixData(linkElement.getChild("subsublink",noNamespace).getContent()));
								}
							}												
							
							if(mhBuffer.length()>0)
							{
								mhBuffer.append(Constants.AUDELIMITER);
							}
							mhBuffer.append(mainterm);
							
							//added link element for "<descriptors controlled="n" type="MUD">" EVOPS-1244 by hmo at 11/9/2021
							if(link!=null)
							{
								mhBuffer.append(Constants.IDDELIMITER+link);
								if(sublink!=null)
								{
									mhBuffer.append(Constants.GROUPDELIMITER+sublink);
								}
								if(subsublink!=null)
								{
									mhBuffer.append(Constants.GROUPDELIMITER+subsublink);
								}
							}
							
						}
						//System.out.print("*** "+descriptorsType+" "+mhBuffer.toString());
						if(descriptorsType.equals("SPC") && mhBuffer.length()>4000)
						{
							term1 = mhBuffer.substring(0,4000);
							term2 = mhBuffer.substring(4000);
							record.put("SPECIESTERM",term1);
							record.put("SPECIESTERM2",term2);
						}
						//added link element for "<descriptors controlled="n" type="MUD">" by hmo at 11/9/2021
						else if (descriptorsType.equals("MUD")&& mhBuffer.length()>0)
						{							
							//record.put("MUDTERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));	
							record.put("MUDTERM",mhBuffer.toString());
						}
						else if (descriptorsType.equals("CBE")&& mhBuffer.length()>0)
						{							
							record.put("CBETERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));							
						}
						else if (descriptorsType.equals("CBB")&& mhBuffer.length()>0)
						{				
							record.put("CBBTERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));							
						}
						else if (descriptorsType.equals("CBC")&& mhBuffer.length()>0)
						{
							record.put("CBCTERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));							
						}
						else if (descriptorsType.equals("CNC")&& mhBuffer.length()>0)
						{
							record.put("CNCTERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));							
						}
						else if (descriptorsType.equals("CBA")&& mhBuffer.length()>0)
						{
							record.put("CBATERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));							
						}
						else if (descriptorsType.equals("CSX")&& mhBuffer.length()>0)
						{
							//System.out.println("CSXTERM="+mhBuffer.toString());
							//record.put("CSXTERM",mhBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));	
							record.put("CSXTERM",mhBuffer.toString());
						}
						else
						{
							if(descriptorsType.equals("CCV") || descriptorsType.equals("PCV") || descriptorsType.equals("GDE"))
							{
								if((descriptorsType.equals("CCV") && getDatabaseName().equals("cpx")) || (descriptorsType.equals("PCV") && getDatabaseName().equals("pch")) || (descriptorsType.equals("GDE") && getDatabaseName().equals("geo")))
								{
									record.put(descriptorsTypeTable.get(descriptorsKey),mhBuffer.toString());
								}
							}
							else
							{
								record.put(descriptorsTypeTable.get(descriptorsKey),mhBuffer.toString());
							}
						}

						//System.out.println(descriptorsKey+" "+descriptorsTypeTable.get(descriptorsKey)+" "+mhBuffer.toString());
					}
				}

			}
		}

	}

	private void parseClassificationgroup(Element classificationgroup,Hashtable record) throws Exception
	{
		if(classificationgroup != null)
		{
			List classificationsList = classificationgroup.getChildren("classifications",noNamespace);

			for(int i=0;i<classificationsList.size();i++)
			{
				Element classifications = (Element)classificationsList.get(i);
				String classificationType = classifications.getAttributeValue("type");
				StringBuffer clBuffer = new StringBuffer();
				if(classificationType != null &&
						(classificationType.equals("CPXCLASS") && getDatabaseName().equals("cpx")) || 
								(classificationType.equals("GEOCLASS") && getDatabaseName().equals("geo")))
				{
					List classificationList = classifications.getChildren("classification",noNamespace);
					for(int j=0;j<classificationList.size();j++)
					{
						Element classification = (Element)classificationList.get(j);
						String cl = classification.getTextTrim();
						String clc = classification.getChildTextTrim("classification-code",noNamespace);

						if(clBuffer.length()>0)
						{
							clBuffer.append(Constants.AUDELIMITER);
						}

						if((cl!=null && cl.length()>0) && (clc!=null && clc.length()>0))
						{
							clBuffer.append(cl+Constants.AUDELIMITER+clc);
						}
						else if(cl!=null && cl.length()>0)
						{
							clBuffer.append(cl);
						}
						else if(clc!=null && clc.length()>0)
						{
							clBuffer.append(clc);
						}
					}
					record.put("CLASSIFICATIONCODE",clBuffer.toString());
				}
				else if(classificationType != null && getDatabaseName().equals("cbn"))
				{
					List classificationList = classifications.getChildren("classification",noNamespace);
					StringBuffer cltBuffer = new StringBuffer();
					for(int j=0;j<classificationList.size();j++)
					{
						Element classification = (Element)classificationList.get(j);
						String cl = classification.getTextTrim();
						String clc = classification.getChildTextTrim("classification-code",noNamespace);
						String clt = classification.getChildTextTrim("classification-description",noNamespace);
						//System.out.println(classificationType+" "+clc+" "+clt);
						if(clBuffer.length()>0)
						{
							clBuffer.append(Constants.AUDELIMITER);
						}

						if((cl!=null && cl.length()>0) && (clc!=null && clc.length()>0))
						{
							clBuffer.append(cl+Constants.AUDELIMITER+clc);
						}
						else if(cl!=null && cl.length()>0)
						{
							clBuffer.append(cl);
						}
						else if(clc!=null && clc.length()>0)
						{
							clBuffer.append(clc);
						}
						
						if(cltBuffer.length()>0)
						{
							cltBuffer.append(Constants.AUDELIMITER);
						}

						if((cl!=null && cl.length()>0) && (clt!=null && clt.length()>0))
						{
							cltBuffer.append(cl+Constants.AUDELIMITER+clt);
						}						
						else if(clt!=null && clt.length()>0)
						{
							cltBuffer.append(clt);
						}
					}
					if(classificationType.equals("CBNBSCOPE") && cltBuffer.length()>0)
					{
						record.put("CBNBSCOPECLASSIFICATIONDESC",cltBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
					}
					else if(classificationType.equals("CBNBSECTOR") && cltBuffer.length()>0)
					{
						record.put("CBNBSECTORCLASSIFICATIONCODE",clBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
						record.put("CBNBSECTORCLASSIFICATIONDESC",cltBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
					} 
					else if(classificationType.equals("CBNBSIC") && cltBuffer.length()>0)
					{
						record.put("CBNBSICCLASSIFICATIONCODE",clBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
						record.put("CBNBSICCLASSIFICATIONDESC",cltBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
					} 
					else if(classificationType.equals("CBNBGEO") && cltBuffer.length()>0)
					{
						record.put("CBNBGEOCLASSIFICATIONCODE",clBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
						record.put("CBNBGEOCLASSIFICATIONDESC",cltBuffer.toString().replaceAll(Constants.AUDELIMITER,";"));
					} 
					
				}
			}
		}

	}
	
	public String getAbstractMixData(List l)
	{
		StringBuffer result = new StringBuffer();
		for(int i=0;i<l.size();i++)
		{
			Element abstractPara = (Element)l.get(i);
			result.append(getMixData(abstractPara.getContent())+"<br/>");
		}
		return result.toString();
	}

	public String getMixData(List l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }

    public StringBuffer getMixData(List l, StringBuffer b)
    {
    	
        Iterator it = l.iterator();

        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				String text=((Text)o).getText();

				text= perl.substitute("s/</&lt;/g",text);   	//restore by hmo@3/21/2021
				text= perl.substitute("s/>/&gt;/g",text);		//restore by hmo@3/21/2021
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);	
				text= perl.substitute("s/�/\"/g",text);	
				text= perl.substitute("s/�/\"/g",text);
				b.append(text);
				//System.out.println("text2::"+text);

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

    public  StringBuffer getMixCData(List l, StringBuffer b)
	{
		inabstract=true;
		b=getMixData(l,b);
		inabstract=false;
		return b;
	}
    
    public StringBuffer getMixData(Iterator it, StringBuffer b)
    {
        
        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				//String text=((Text)o).getTextTrim();
				String text=((Text)o).getText();

				//System.out.println("text3::"+text);

				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);

				//System.out.println("text4::"+text);
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
                getMixData(e.getDescendants(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
		
        return b;
    }
    
    public String getMixData(Iterator l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }
    
    public String trimStringToLength(String inputString,int StringSize)
    {
    	String outputString="";
    	if(inputString!=null && inputString.length()>StringSize)
    	{
    		outputString = inputString.substring(0,StringSize);
    		outputString = outputString.substring(0,outputString.lastIndexOf(" "));
    	}
    	else
    	{
    		return inputString;
    	}
    	
    	return outputString;
    }
    
    

}
