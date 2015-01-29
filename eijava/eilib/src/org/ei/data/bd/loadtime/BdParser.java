package org.ei.data.bd.loadtime;

import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.ei.util.GUID;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.ei.data.*;
import org.ei.data.bd.*;
import org.ei.data.encompasslit.loadtime.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BdParser
{
	private List articles = null;
	private Document doc = null;
	private Iterator rec =null;
	private Hashtable recTable =null;
	private Perl5Util perl = new Perl5Util();
	private boolean inabstract = false;
	private HashSet entity = null;
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {29});
	public static final String REFERENCEDELIMITER = new String(new char[] {28});
	private static String weekNumber;
	private PrintWriter out;
	private SAXBuilder builder;
	private String accessNumber;
	//private String action;
	private String databaseName = "cpx";
	private DataLoadDictionary dictionary = new DataLoadDictionary();

    private Namespace aitNamespace=Namespace.getNamespace("ait","http://www.elsevier.com/xml/ait/dtd");
	private Namespace ceNamespace=Namespace.getNamespace("ce","http://www.elsevier.com/xml/common/dtd");
	private Namespace noNamespace=Namespace.getNamespace("","");
	private Namespace aniNamespace;
	private Namespace xmlNamespace=Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace");
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
		descriptorsTypeTable.put("CMH","MAINHEADING");
		descriptorsTypeTable.put("SPC","SPECIESTERM");
		descriptorsTypeTable.put("RGI","REGIONALTERM");
		descriptorsTypeTable.put("CCV","CONTROLLEDTERM");
		descriptorsTypeTable.put("PCV","CONTROLLEDTERM");
		descriptorsTypeTable.put("GDE","CONTROLLEDTERM");
		descriptorsTypeTable.put("MED","CHEMICALTERM");
		descriptorsTypeTable.put("CTC","TREATMENTCODE");
		descriptorsTypeTable.put("CFL","UNCONTROLLEDTERM");
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
		this.weekNumber = weekNumber;
	}

	public String getWeekNumber()
	{
		return this.weekNumber;
	}

/*
	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return this.action;
	}
*/
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
			if(cpxRoot != null)
			{
				String namespaceURI = cpxRoot.getNamespace().getURI();
				if(namespaceURI!=null && namespaceURI.length()>0)
				{
					noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");;
				}
				Element item = cpxRoot.getChild("item",noNamespace);

				if(item!= null)
				{


					String mid = getDatabaseName().toLowerCase()+"_"+(new GUID()).toString();
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
							    yearvalue.append(IDDELIMITER);
							    yearvalue.append((String) year.getAttributeValue("month"));
							}
							if(year.getAttributeValue("day") != null)
							{
							    yearvalue.append(IDDELIMITER);
							    yearvalue.append((String) year.getAttributeValue("day"));
							}
						}
						if(yearvalue.length() > 0 )
						{
							record.put("DATESORT", yearvalue.toString());
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
						Element item_info 	= bibrecord.getChild("item-info",noNamespace);
						Element copyright   = item_info.getChild("copyright",noNamespace);

						if(copyright != null)
						{
							record.put("COPYRIGHT",copyright.getTextTrim());
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
						for(int i=0;i<itemidList.size();i++)
						{
							Element itemidElement = (Element)itemidList.get(i);
							String  itemid_idtype = itemidElement.getAttributeValue("idtype");

							if(itemid_idtype != null &&
									 (itemid_idtype.equals("CPX")||
									itemid_idtype.equals("GEO")||
									itemid_idtype.equals("API")||
									itemid_idtype.equals("APILIT")||
									itemid_idtype.equals("CHEM")))
							{
								String  itemid = itemidElement.getTextTrim();

								//System.out.println("ACCESSNUMBER= "+itemid);

								record.put("ACCESSNUMBER",itemid);
								setAccessNumber(itemid);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("PUI"))
							{
								String  pui = itemidElement.getTextTrim();
								record.put("PUI",pui);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("SEC"))
							{
								String  pui = itemidElement.getTextTrim();
								record.put("SEC",pui);
							}
						}

						//head
						Element head = bibrecord.getChild("head",noNamespace);
						if(head != null)
						{
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

										//if(cititype!=null && cititype.equals("ip"))
										//{
										//	cititype="aip";
										//}
										record.put("CITTYPE",cititype);
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
													authorKeywordBuffer.append(IDDELIMITER);
												}
												authorKeywordBuffer.append(dictionary.mapEntity(authorKeywordString));
											}
										}
									}

									record.put("AUTHORKEYWORD",authorKeywordBuffer.toString());
								}
							}

							//citation title
							Element cittitle = head.getChild("citation-title",noNamespace);
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
										cittext.append(IDDELIMITER);
										if(cittextelm.getContent()!=null)
										{
											cittext.append(dictionary.mapEntity(getMixData(cittextelm.getContent())));
										}
										cittext.append(IDDELIMITER);
										if(cittextelm.getAttribute("original")!=null)
										{
											cittext.append(cittextelm.getAttributeValue("original"));
										}
										cittext.append(IDDELIMITER);
										if(cittextelm.getAttribute("lang",xmlNamespace)!=null)
										{
											cittext.append(cittextelm.getAttributeValue("lang",xmlNamespace));
										}
										if(i<cittextlst.size()-1)
										{
											cittext.append(AUDELIMITER);
										}
									}
									String citation = cittext.toString();
									if(this.databaseName.equalsIgnoreCase("elt"))
									{
										citation = citation.replaceAll("<inf>", "<sub>");
										citation = citation.replaceAll("</inf>", "</sub>");

									}

									record.put("CITATIONTITLE",citation);
								}
							}

							Element abstracts = head.getChild("abstracts",noNamespace);
							if(abstracts!= null && abstracts.getChild("abstract",noNamespace)!=null)
							{
								String abstractCopyRight=null;
								Element abstractData = abstracts.getChild("abstract",noNamespace);
								if(	abstractData.getChild("publishercopyright",noNamespace)!=null)
								{
								 	abstractCopyRight= dictionary.mapEntity(abstractData.getChildTextTrim("publishercopyright",noNamespace));
									//System.out.println("COPYRIGHT="+ abstractCopyRight);
								}

								// abstract data

								if(abstractData.getAttributeValue("original") != null)
								{
									//System.out.println("ABSTRACT_original "+abstractData.getAttributeValue("original"));
									record.put("ABSTRACTORIGINAL", abstractData.getAttributeValue("original"));
								}

								if(abstractData.getAttributeValue("perspective") != null)
								{
									//System.out.println("ABSTRACTPERSPECTIVE "+abstractData.getAttributeValue("perspective"));
									record.put("ABSTRACTPERSPECTIVE", abstractData.getAttributeValue("perspective"));
								}

								if(abstractData.getChildTextTrim("para",ceNamespace) != null)
								{
									String abstractString = dictionary.mapEntity(getMixData(abstractData.getChild("para",ceNamespace).getContent()));
									//System.out.println(this.databaseName+"  ::about to replace ::"+abstractString);
									if(this.databaseName.equalsIgnoreCase("elt"))
									{
										abstractString = abstractString.replaceAll("<inf>", "<sub>");
										abstractString = abstractString.replaceAll("</inf>", "</sub>");

										//System.out.println("\nreplacing inf ::"+abstractString);
									}

									if(abstractCopyRight!=null)
									{
										abstractString = abstractString+" "+abstractCopyRight;
									}

									record.put("ABSTRACTDATA", abstractString);
								}

							}

							//author-group conatins (redundunt authors - removing redundunt authors;

							List authorgroup = head.getChildren("author-group",noNamespace);
							BdAuthors ausmap = new BdAuthors();
							BdAffiliations affmap = new BdAffiliations();
							this.affid = 0;
							for (int e=0 ; e < authorgroup.size() ; e++)
							{
								setAuthorAndAffs( ausmap,
													affmap,
													(Element) authorgroup.get(e));
							}
							StringBuffer secondAuGroup = new StringBuffer();
							record.put("AUTHOR", auToStringBuffer(ausmap, secondAuGroup));
							if(secondAuGroup.length() > 0)
							{
								record.put("AUTHOR_1",secondAuGroup.toString());
							}
							StringBuffer secondAffGroup = new StringBuffer();

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
																apiccterms.append((String)el.getTextTrim());
																if(i<(apicc.size()-1) )
																{
																	apiccterms.append(AUDELIMITER);
																}
															}
														}
														if(j<(classdescgroup.size()-1) )
														{
															apiccterms.append(AUDELIMITER);
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
														termbuf.append(pref).append("-");
													}
													termbuf.append(term);
													if ( postf != null && postf.length() > 0)
													{
														termbuf.append("-").append(postf);
													}
													if(apict.length() < 3000)
													{
														apict.append(termbuf.toString()).append(IDDELIMITER);
													}
													else if(apict.length() >= 3000)
													{
														apictextended.append(termbuf.toString()).append(IDDELIMITER);
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
																	termbuf.append(pref).append("-");
																}
																termbuf.append(term);
																if ( postf != null && postf.length() > 0)
																{
																	termbuf.append("-").append(postf);
																}
																if(apiterms.length() < 3000)
																{

																	apiterms.append(termbuf.toString()).append(IDDELIMITER);
																}
																else if(apiterms.length() >= 3000)
																{
																	apiterms1.append(termbuf.toString()).append(IDDELIMITER);
																}
															}
														}

												}
												if(apiterms != null && apiterms.length()>0)
												{
													apigroups.append(apiterms);
													apigroups.append(GROUPDELIMITER);
												}
												if(apiterms1 != null && apiterms1.length()>0)
												{
													apigroups1.append(apiterms1);
													apigroups1.append(GROUPDELIMITER);
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
												apiamsbuf.append(ams.getTextTrim());
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
													apiapcbuf.append(el.getTextTrim());
													apiapcbuf.append(IDDELIMITER);
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
													apicrnbuf.append(AUDELIMITER);
													if(el.getAttributeValue("postfix") != null)
													{
														String carnr =(String) el.getAttributeValue("postfix");
														apicrnbuf.append(carnr);

													}
													apicrnbuf.append(AUDELIMITER);
													apicrnbuf.append(el.getTextTrim());
													apicrnbuf.append(IDDELIMITER);
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
											apialcbuf.append(IDDELIMITER);
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

												termsbuf.append(buf.toString()).append(IDDELIMITER);

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

															apimterms.append(termbuf.toString()).append(IDDELIMITER);
														}

												}
												apimgroups.append(apimterms);
												apimgroups.append(GROUPDELIMITER);
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
							Element correspondence = (Element) head.getChild("correspondence",noNamespace);
							parseCorrespondenceElement(correspondence,record);


						}

						Element tail = bibrecord.getChild("tail",noNamespace);
						if(tail != null)
						{
							Element bibliography = tail.getChild("bibliography",noNamespace);
							if(bibliography != null && bibliography.getAttributeValue("refcount")!=null)
							{
								record.put("REFCOUNT",(String)bibliography.getAttributeValue("refcount"));
							}

							List referencegroup = bibliography.getChildren("reference", noNamespace);

							parseReferencegroup(referencegroup,record);

						}

						//weekNumber
						//record.put("LOADNUMBER",weekNumber);

						// only for elt database conversion
						record.put("DATABASE",databaseName.trim());
					}

					if(item.getChild("loadnumber", noNamespace) != null)
					{
						record.put("LOADNUMBER",item.getChild("loadnumber", noNamespace).getText());
					}
					else
					{
						record.put("LOADNUMBER",weekNumber);
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
                            secpubdate.append(IDDELIMITER);
                            if(secpublicationdate.getChild("month", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("month", noNamespace).getText());
                            }
                            secpubdate.append(IDDELIMITER);
                            if(secpublicationdate.getChild("day", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("day", noNamespace).getText());
                            }
                            secpubdate.append(IDDELIMITER);
                            if(secpublicationdate.getChild("date-text", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("date-text", noNamespace).getText());
                            }
                            secpubdate.append(IDDELIMITER);
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
		String referenceID = null;
		StringBuffer referenceTitle = new StringBuffer();
		StringBuffer referenceAuthor = new StringBuffer();
		StringBuffer referenceSourcetitle = new StringBuffer();
		StringBuffer referencePublicationyear = new StringBuffer();
		StringBuffer referenceVolume = new StringBuffer();
		StringBuffer referenceIssue = new StringBuffer();
		StringBuffer referencePages = new StringBuffer();
		StringBuffer referenceFullText = new StringBuffer();
		StringBuffer referenceText = new StringBuffer();
		StringBuffer referenceWebsite = new StringBuffer();
		StringBuffer referenceItemid = new StringBuffer();
		StringBuffer referenceItemcitationPII = new StringBuffer();
		StringBuffer referenceItemcitationDOI = new StringBuffer();
		StringBuffer referenceItemcitationCitationTitle = new StringBuffer();
		StringBuffer referenceItemcitationAuthor = new StringBuffer();
		StringBuffer referenceItemcitationSourcetitle = new StringBuffer();
		StringBuffer referenceItemcitationSourcetitle_abbrev = new StringBuffer();
		StringBuffer referenceItemcitationISSN = new StringBuffer();
		StringBuffer referenceItemcitationISBN = new StringBuffer();
		StringBuffer referenceItemcitationCoden = new StringBuffer();
		StringBuffer referenceItemcitationPart = new StringBuffer();
		StringBuffer referenceItemcitationPublicationyear = new StringBuffer();
		StringBuffer referenceItemcitationVolume = new StringBuffer();
		StringBuffer referenceItemcitationIssue = new StringBuffer();
		StringBuffer referenceItemcitationPage = new StringBuffer();
		StringBuffer referenceItemcitationArticleNumber = new StringBuffer();
		StringBuffer referenceItemcitationWebsite = new StringBuffer();
		StringBuffer referenceItemcitationEAddress = new StringBuffer();
		StringBuffer referenceItemcitationRefText = new StringBuffer();
		StringBuffer pElectronicA = new StringBuffer();

		if(referenceGroup != null && referenceGroup.size()>0)
		{
			for(int i=0;i<referenceGroup.size();i++)
			{
				Element reference = (Element)referenceGroup.get(i);

				if(reference != null)
				{
					referenceID = reference.getAttributeValue("id");
					/*
					if(i>0)
					{
						referenceTitle.append(REFERENCEDELIMITER);
						referenceAuthor.append(REFERENCEDELIMITER);
						referenceSourcetitle.append(REFERENCEDELIMITER);
						referencePublicationyear.append(REFERENCEDELIMITER);
						referenceVolume.append(REFERENCEDELIMITER);
						referenceIssue.append(REFERENCEDELIMITER);
						referencePages.append(REFERENCEDELIMITER);
						referenceFullText.append(REFERENCEDELIMITER);
						referenceText.append(REFERENCEDELIMITER);
						referenceWebsite.append(REFERENCEDELIMITER);
						referenceItemid.append(REFERENCEDELIMITER);
						referenceItemcitationPII.append(REFERENCEDELIMITER);
						referenceItemcitationDOI.append(REFERENCEDELIMITER);
						referenceItemcitationCitation_title.append(REFERENCEDELIMITER);
						referenceItemcitationAuthor.append(REFERENCEDELIMITER);
						referenceItemcitationSourcetitle.append(REFERENCEDELIMITER);
						referenceItemcitationSourcetitle_abbrev.append(REFERENCEDELIMITER);
						referenceItemcitationISSN.append(REFERENCEDELIMITER);
						referenceItemcitationISBN.append(REFERENCEDELIMITER);
						referenceItemcitationCoden.append(REFERENCEDELIMITER);
						referenceItemcitationPart.append(REFERENCEDELIMITER);
						referenceItemcitationPublicationyear.append(REFERENCEDELIMITER);
						referenceItemcitationVolume.append(REFERENCEDELIMITER);
						referenceItemcitationIssue.append(REFERENCEDELIMITER);
						referenceItemcitationPage.append(REFERENCEDELIMITER);
						referenceItemcitationArticleNumber.append(REFERENCEDELIMITER);
						referenceItemcitationWebsite.append(REFERENCEDELIMITER);
						referenceItemcitationEAddress.append(REFERENCEDELIMITER);
						referenceItemcitationRefText.append(REFERENCEDELIMITER);
						pElectronicA.append(REFERENCEDELIMITER);
					}
					referenceTitle.append(referenceID+GROUPDELIMITER);
					referenceAuthor.append(referenceID+GROUPDELIMITER);
					referenceSourcetitle.append(referenceID+GROUPDELIMITER);
					referencePublicationyear.append(referenceID+GROUPDELIMITER);
					referenceVolume.append(referenceID+GROUPDELIMITER);
					referenceIssue.append(referenceID+GROUPDELIMITER);
					referencePages.append(referenceID+GROUPDELIMITER);
					referenceFullText.append(referenceID+GROUPDELIMITER);
					referenceText.append(referenceID+GROUPDELIMITER);
					referenceWebsite.append(referenceID+GROUPDELIMITER);
					referenceItemid.append(referenceID+GROUPDELIMITER);
					referenceItemcitationPII.append(referenceID+GROUPDELIMITER);
					referenceItemcitationDOI.append(referenceID+GROUPDELIMITER);
					referenceItemcitationCitation_title.append(referenceID+GROUPDELIMITER);
					referenceItemcitationAuthor.append(referenceID+GROUPDELIMITER);
					referenceItemcitationSourcetitle.append(referenceID+GROUPDELIMITER);
					referenceItemcitationSourcetitle_abbrev.append(referenceID+GROUPDELIMITER);
					referenceItemcitationISSN.append(referenceID+GROUPDELIMITER);
					referenceItemcitationISBN.append(referenceID+GROUPDELIMITER);
					referenceItemcitationCoden.append(referenceID+GROUPDELIMITER);
					referenceItemcitationPart.append(referenceID+GROUPDELIMITER);
					referenceItemcitationPublicationyear.append(referenceID+GROUPDELIMITER);
					referenceItemcitationVolume.append(referenceID+GROUPDELIMITER);
					referenceItemcitationIssue.append(referenceID+GROUPDELIMITER);
					referenceItemcitationPage.append(referenceID+GROUPDELIMITER);
					referenceItemcitationArticleNumber.append(referenceID+GROUPDELIMITER);
					referenceItemcitationWebsite.append(referenceID+GROUPDELIMITER);
					referenceItemcitationEAddress.append(referenceID+GROUPDELIMITER);
					referenceItemcitationRefText.append(referenceID+GROUPDELIMITER);
					pElectronicA.append(referenceID+GROUPDELIMITER);
					*/

					Element refInfo = (Element) reference.getChild("ref-info",noNamespace);
					Element refTitle = (Element) refInfo.getChild("ref-title",noNamespace);
					if(refTitle!=null)
					{
						if(referenceTitle.length()>0)
						{
							referenceTitle.append(REFERENCEDELIMITER);
						}
						referenceTitle.append(referenceID+GROUPDELIMITER);
						List refTitletextList = (List) refTitle.getChildren("ref-titletext",noNamespace);
						for(int j=0;j<refTitletextList.size();j++)
						{
							Element refTitletextElement = (Element)refTitletextList.get(j);
							String  refTitletext = refTitletextElement.getTextTrim();
							referenceTitle.append(refTitletext);
							if(j<refTitletextList.size()-1)
							{
								referenceTitle.append(", ");
							}
						}
					}

					List refAuthorsList = (List) refInfo.getChildren("ref-authors",noNamespace);
					BdAuthors ausmap = new BdAuthors();
					BdAffiliations affmap = new BdAffiliations();
					this.affid = 0;
					if(refAuthorsList!=null && refAuthorsList.size()>0)
					{
						if(referenceAuthor.length()>0)
						{
							referenceAuthor.append(REFERENCEDELIMITER);
						}
						referenceAuthor.append(referenceID+GROUPDELIMITER);

						for (int j=0 ; j < refAuthorsList.size() ; j++)
						{
							setAuthorAndAffs( ausmap,
											  affmap,
											  (Element) refAuthorsList.get(j));
						}

						referenceAuthor.append(auToStringBuffer(ausmap));
					}

					//ref-sourcetitle
					Element refSourceTitle = (Element) refInfo.getChild("ref-sourcetitle",noNamespace);
					if(	refSourceTitle !=null)
					{
						if(referenceSourcetitle.length()>0)
						{
							referenceSourcetitle.append(REFERENCEDELIMITER);
						}
						referenceSourcetitle.append(referenceID+GROUPDELIMITER);
						String  refSourceTitleText = refSourceTitle.getTextTrim();
						referenceSourcetitle.append(refSourceTitleText);
					}

					//ref-publicationyear
					Element refPublicationyear = (Element) refInfo.getChild("ref-publicationyear",noNamespace);
					if(refPublicationyear != null)
					{
						if(referencePublicationyear.length()>0)
						{
							referencePublicationyear.append(REFERENCEDELIMITER);
						}
						referencePublicationyear.append(referenceID+GROUPDELIMITER);
						String publicationyear = getPublicationYear(refPublicationyear);
						referencePublicationyear.append(publicationyear);
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
								if(referenceVolume.length()>0)
								{
									referenceVolume.append(REFERENCEDELIMITER);
								}
								referenceVolume.append(referenceID+GROUPDELIMITER);

								referenceVolume.append(volume);
							}
							if(issue != null)
							{
								if(referenceIssue.length()>0)
								{
									referenceIssue.append(REFERENCEDELIMITER);
								}
								referenceIssue.append(referenceID+GROUPDELIMITER);

								referenceIssue.append(issue);
							}
						}

						//PAGERANGE PAGE
						String pages = getPages(refVolisspag);
						if(referencePages.length()>0)
						{
							referencePages.append(REFERENCEDELIMITER);
						}
						referencePages.append(referenceID+GROUPDELIMITER);
						referencePages.append(pages);
					}


					//ref-website
					Element refWebsite = (Element) refInfo.getChild("ref-website",noNamespace);
					if(refWebsite!=null)
					{
						if(referenceWebsite.length()>0)
						{
							referenceWebsite.append(REFERENCEDELIMITER);
						}
						referenceWebsite.append(referenceID+GROUPDELIMITER);
						Element websitename = (Element) refWebsite.getChild("websitename",noNamespace);
						if(websitename!=null)
						{
							String websitenameText = websitename.getTextTrim();
							referenceWebsite.append(websitenameText);
						}
						referenceWebsite.append(IDDELIMITER);
						Element eaddress = (Element) refWebsite.getChild("e-address",ceNamespace);
						if(eaddress!=null)
						{
							String eaddresstext = eaddress.getText();
							if(eaddresstext != null)
							{
								referenceWebsite.append(eaddresstext);
							}
						}
					}

					//ref-text
					Element refText = (Element) refInfo.getChild("ref-text",noNamespace);
					if(refText!=null)
					{

						if(referenceText.length()>0)
						{
							referenceText.append(REFERENCEDELIMITER);
						}
						referenceText.append(referenceID+GROUPDELIMITER);
						String  refTextValue = refText.getTextTrim();
						referenceText.append(refTextValue);
					}

					//refd-itemidlist
					Element refdItemidlist = (Element) refInfo.getChild("refd-itemidlist",noNamespace);
					if(refdItemidlist!=null)
					{
						List itemidList = refdItemidlist.getChildren("itemid",noNamespace);
						if(itemidList!=null)
						{
							if(referenceItemid.length()>0)
							{
								referenceItemid.append(REFERENCEDELIMITER);
							}
							referenceItemid.append(referenceID+GROUPDELIMITER);
							String itemid = getItemID(itemidList);
							referenceItemid.append(itemid);
						}
					}


					//ref-fulltext
					Element refFullText = (Element) reference.getChild("ref-fulltext",noNamespace);
					if(refFullText!=null)
					{
						String  refFullTextValue = refFullText.getTextTrim();
						if(referenceText.length()>0)
						{
							referenceText.append(REFERENCEDELIMITER);
						}
						referenceText.append(referenceID+GROUPDELIMITER);
						referenceText.append(refFullTextValue);
					}

					//refd-itemcitation
					Element refdItemcitation = (Element) reference.getChild("refd-itemcitation",noNamespace);
					if(refdItemcitation!=null)
					{
						//REFERENCE CITATION PII
						if(refdItemcitation.getChild("pii",ceNamespace)!=null)
						{
							if(referenceItemcitationPII.length()>0)
							{
								referenceItemcitationPII.append(REFERENCEDELIMITER);
							}
							referenceItemcitationPII.append(referenceID+GROUPDELIMITER);
							Element pii = (Element)refdItemcitation.getChild("pii",ceNamespace);
							referenceItemcitationPII.append(pii.getTextTrim());
						}

						//REFERENCE CITATION DOI
						if(refdItemcitation.getChild("doi",ceNamespace)!=null)
						{
							if(referenceItemcitationDOI.length()>0)
							{
								referenceItemcitationDOI.append(REFERENCEDELIMITER);
							}
							referenceItemcitationDOI.append(referenceID+GROUPDELIMITER);
							Element doi = (Element)refdItemcitation.getChild("doi",ceNamespace);
							referenceItemcitationDOI.append(doi.getTextTrim());
						}

						//REFERENCE CITATION CITATIONTITLE
						if(refdItemcitation.getChild("citation-title",ceNamespace)!=null)
						{
							if(referenceItemcitationCitationTitle.length()>0)
							{
								referenceItemcitationCitationTitle.append(REFERENCEDELIMITER);
							}
							referenceItemcitationCitationTitle.append(referenceID+GROUPDELIMITER);
							Element citationTitles = (Element)refdItemcitation.getChild("citation-title",ceNamespace);
							String citation = getCitationTitle(citationTitles);
							referenceItemcitationCitationTitle.append(citation);

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
							if(referenceItemcitationAuthor.length()>0)
							{
								referenceItemcitationAuthor.append(REFERENCEDELIMITER);
							}
							referenceItemcitationAuthor.append(referenceID+GROUPDELIMITER);
							referenceItemcitationAuthor.append(auToStringBuffer(refAusmap));

						}

						//REFERENCE CITATION SOURCETITLE
						if(refdItemcitation.getChild("sourcetitle",noNamespace)!=null)
						{
							Element sourcetitle = (Element)refdItemcitation.getChild("sourcetitle",noNamespace);
							if(referenceItemcitationSourcetitle.length()>0)
							{
								referenceItemcitationSourcetitle.append(REFERENCEDELIMITER);
							}
							referenceItemcitationSourcetitle.append(referenceID+GROUPDELIMITER);
							referenceItemcitationSourcetitle.append(sourcetitle.getTextTrim());
						}

						//REFERENCE CITATION SOURCETITLE ABBREV
						if(refdItemcitation.getChild("sourcetitle-abbrev",noNamespace)!=null)
						{
							Element sourcetitleAbbrev = (Element)refdItemcitation.getChild("sourcetitle-abbrev",noNamespace);
							if(referenceItemcitationSourcetitle_abbrev.length()>0)
							{
								referenceItemcitationSourcetitle_abbrev.append(REFERENCEDELIMITER);
							}
							referenceItemcitationSourcetitle_abbrev.append(referenceID+GROUPDELIMITER);
							referenceItemcitationSourcetitle_abbrev.append(sourcetitleAbbrev.getTextTrim());
						}

						//REFERENCE CITATION ISSN
						List issnList =  refdItemcitation.getChildren("issn",noNamespace);

						if(issnList != null)
						{
							String issn = getISSN(issnList);
							if(referenceItemcitationISSN.length()>0)
							{
								referenceItemcitationISSN.append(REFERENCEDELIMITER);
							}
							referenceItemcitationISSN.append(referenceID+GROUPDELIMITER);
							referenceItemcitationISSN.append(issn);

						}

						//REFERENCE CITATION ISBN
						List refIsbnList = refdItemcitation.getChildren("isbn",noNamespace);
						if(refIsbnList != null)
						{
							String refIsbn = getISBN(refIsbnList);
							if(referenceItemcitationISBN.length()>0)
							{
								referenceItemcitationISBN.append(REFERENCEDELIMITER);
							}
							referenceItemcitationISBN.append(referenceID+GROUPDELIMITER);
							referenceItemcitationISBN.append(refIsbn);
						}

						//REFERENCE CITATION CODENCODE
						Element codencode = (Element) refdItemcitation.getChild("codencode",noNamespace);
						if (codencode != null)
						{
							//System.out.println("codencode::"+codencode.getTextTrim());
							if(referenceItemcitationCoden.length()>0)
							{
								referenceItemcitationCoden.append(REFERENCEDELIMITER);
							}
							referenceItemcitationCoden.append(referenceID+GROUPDELIMITER);
							referenceItemcitationCoden.append(codencode.getTextTrim());
						}

						//REFERENCE CITATION PART
						Element part = (Element) refdItemcitation.getChild("part",noNamespace);
						if (part != null)
						{
							//System.out.println("part::"+part.getTextTrim());
							if(referenceItemcitationPart.length()>0)
							{
								referenceItemcitationPart.append(REFERENCEDELIMITER);
							}
							referenceItemcitationPart.append(referenceID+GROUPDELIMITER);
							referenceItemcitationPart.append(part.getTextTrim());
						}

						//REFERENCE CITATION PUBLICATIONYEAR
						Element refcitationPublicationyear = (Element) refdItemcitation.getChild("publicationyear",noNamespace);
						if(refcitationPublicationyear != null)
						{
							String citationPublicationyear = getPublicationYear(refcitationPublicationyear);
							if(referenceItemcitationPublicationyear.length()>0)
							{
								referenceItemcitationPublicationyear.append(REFERENCEDELIMITER);
							}
							referenceItemcitationPublicationyear.append(referenceID+GROUPDELIMITER);
							referenceItemcitationPublicationyear.append(citationPublicationyear);
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
									if(referenceItemcitationVolume.length()>0)
									{
										referenceItemcitationVolume.append(REFERENCEDELIMITER);
									}
									referenceItemcitationVolume.append(referenceID+GROUPDELIMITER);
									referenceItemcitationVolume.append(citation_volume);
								}
								if(citation_issue != null)
								{
									//System.out.println("citation_issue::"+citation_issue);
									if(referenceItemcitationIssue.length()>0)
									{
										referenceItemcitationIssue.append(REFERENCEDELIMITER);
									}
									referenceItemcitationIssue.append(referenceID+GROUPDELIMITER);
									referenceItemcitationIssue.append(citation_issue);
								}
							}

							//PAGERANGE PAGE
							String citation_pages = getPages(citation_volisspag);
							if(referenceItemcitationPage.length()>0)
							{
								referenceItemcitationPage.append(REFERENCEDELIMITER);
							}
							referenceItemcitationPage.append(referenceID+GROUPDELIMITER);
							referenceItemcitationPage.append(citation_pages);
						}

						//REFERENCE CITATION ARTICLE NUMBER
						Element articlenumber = (Element) refdItemcitation.getChild("article-number",noNamespace);
						if (articlenumber != null)
						{
							//System.out.println("articlenumber::"+articlenumber.getTextTrim());
							if(referenceItemcitationArticleNumber.length()>0)
							{
								referenceItemcitationArticleNumber.append(REFERENCEDELIMITER);
							}
							referenceItemcitationArticleNumber.append(referenceID+GROUPDELIMITER);
							referenceItemcitationArticleNumber.append(articlenumber.getTextTrim());
						}

						//REFERENCE CITATION WEBSITE
						Element citationWebsite = (Element) refdItemcitation.getChild("website",noNamespace);
						if(citationWebsite!=null)
						{
							Element citationWebsitename = (Element) citationWebsite.getChild("websitename",noNamespace);
							if(citationWebsitename!=null)
							{
								if(referenceItemcitationWebsite.length()>0)
								{
									referenceItemcitationWebsite.append(REFERENCEDELIMITER);
								}
								referenceItemcitationWebsite.append(referenceID+GROUPDELIMITER);
								referenceItemcitationWebsite.append(citationWebsitename.getTextTrim());
							}

							Element citationEAddress = (Element) citationWebsitename.getChild("e-address",ceNamespace);
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
									if(referenceItemcitationEAddress.length()>0)
									{
										referenceItemcitationEAddress.append(REFERENCEDELIMITER);
									}
									referenceItemcitationEAddress.append(referenceID+GROUPDELIMITER);
									referenceItemcitationEAddress.append(citationEAddressType+IDDELIMITER+citationEAddresstext);
								}
							}
						}

						//REFERENCE CITATION REF-TEXT
						Element citationRefText = (Element) refdItemcitation.getChild("ref-text",noNamespace);
						if(citationRefText!=null)
						{
							if(referenceItemcitationRefText.length()>0)
							{
								referenceItemcitationRefText.append(REFERENCEDELIMITER);
							}
							referenceItemcitationRefText.append(referenceID+GROUPDELIMITER);
							referenceItemcitationRefText.append(citationRefText.getTextTrim());
						}

					}//refd-itemcitation

				}

			}

		}

		record.put("REFERENCETITLE",referenceTitle.toString());
		//System.out.println("REFERENCETITLE:"+referenceTitle.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEAUTHOR",referenceAuthor.toString());
		//System.out.println("REFERENCEAUTHOR:"+referenceAuthor.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCESOURCETITLE",referenceSourcetitle.toString());
		//System.out.println("REFERENCESOURCETITLE:"+referenceSourcetitle.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEPUBLICATIONYEAR",referencePublicationyear.toString());
		//System.out.println("REFERENCEPUBLICATIONYEAR:"+referencePublicationyear.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEVOLUME",referenceVolume.toString());
		//System.out.println("REFERENCEVOLUME:"+referenceVolume.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEISSUE",referenceIssue.toString());
		//System.out.println("REFERENCEISSUE:"+referenceIssue.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEPAGES",referencePages.toString());
		//System.out.println("REFERENCEPAGES:"+referencePages.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEFULLTEXT",referenceFullText.toString());
		//System.out.println("REFERENCEFULLTEXT:"+referenceFullText.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCETEXT",referenceText.toString());
		//System.out.println("REFERENCETEXT:"+referenceText.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEWEBSITE",referenceWebsite.toString());
		//System.out.println("REFERENCEWEBSITE:"+referenceWebsite.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMID",referenceItemid.toString());
		//System.out.println("REFERENCEITEMID:"+referenceItemid.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONPII",referenceItemcitationPII.toString());
		//System.out.println("REFERENCEITEMCITATIONPII:"+referenceItemcitationPII.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONDOI",referenceItemcitationDOI.toString());
		//System.out.println("REFERENCEITEMCITATIONDOI:"+referenceItemcitationDOI.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONTITLE",referenceItemcitationCitationTitle.toString());
		//System.out.println("REFERENCEITEMCITATIONTITLE:"+referenceItemcitationCitationTitle.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONAUTHOR",referenceItemcitationAuthor.toString());
		//System.out.println("REFERENCEITEMCITATIONAUTHOR:"+referenceItemcitationAuthor.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONSOURCETITLE",referenceItemcitationSourcetitle.toString());
		//System.out.println("REFERENCEITEMCITATIONSOURCETITLE:"+referenceItemcitationSourcetitle.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONSOURCETITLEABBREV",referenceItemcitationSourcetitle_abbrev.toString());
		//System.out.println("REFERENCEITEMCITATIONSOURCETITLEABBREV:"+referenceItemcitationSourcetitle_abbrev.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONISSN",referenceItemcitationISSN.toString());
		//System.out.println("REFERENCEITEMCITATIONISSN:"+referenceItemcitationISSN.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONISBN",referenceItemcitationISBN.toString());
		//System.out.println("REFERENCEITEMCITATIONISBN:"+referenceItemcitationISBN.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONCODEN",referenceItemcitationCoden.toString());
		//System.out.println("REFERENCEITEMCITATIONCODEN:"+referenceItemcitationCoden.toString());

		record.put("REFERENCEITEMCITATIONPART",referenceItemcitationPart.toString());
		//System.out.println("REFERENCEITEMCITATIONPART:"+referenceItemcitationPart.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONPUBLICATIONYEAR",referenceItemcitationPublicationyear.toString());
		//System.out.println("REFERENCEITEMCITATIONPUBLICATIONYEAR:"+referenceItemcitationPublicationyear.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONVOLUME",referenceItemcitationVolume.toString());
		//System.out.println("REFERENCEITEMCITATIONVOLUME:"+referenceItemcitationVolume.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONISSUE",referenceItemcitationIssue.toString());
		//System.out.println("REFERENCEITEMCITATIONISSUE:"+referenceItemcitationIssue.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONPAGE",referenceItemcitationPage.toString());
		//System.out.println("REFERENCEITEMCITATIONPAGE:"+referenceItemcitationPage.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONARTICLENUMBER",referenceItemcitationArticleNumber.toString());
		//System.out.println("REFERENCEITEMCITATIONARTICLENUMBER:"+referenceItemcitationArticleNumber.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONWEBSITE",referenceItemcitationWebsite.toString());
		//System.out.println("REFERENCEITEMCITATIONWEBSITE:"+referenceItemcitationWebsite.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONEADDRESS",referenceItemcitationEAddress.toString());
		//System.out.println("REFERENCEITEMCITATIONEADDRESS:"+referenceItemcitationEAddress.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEITEMCITATIONREFTEXT",referenceItemcitationRefText.toString());
		//System.out.println("REFERENCEITEMCITATIONREFTEXT:"+referenceItemcitationRefText.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));

		record.put("REFERENCEEMAIL",pElectronicA.toString());
		//System.out.println("REFERENCEEMAIL:"+pElectronicA.toString().replaceAll(REFERENCEDELIMITER,"|").replaceAll(GROUPDELIMITER,"**").replaceAll(IDDELIMITER,"--"));
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

	private String getItemID(List itemidList) throws Exception
	{
		StringBuffer referenceItemid = new StringBuffer();
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
				referenceItemid.append("ACCESSNUMBER:"+itemid);

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
			referenceItemid.append(IDDELIMITER);
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
			referencePages.append(IDDELIMITER);
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
			referencePages.append("-");
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
					referencePages.append(pagecountType+IDDELIMITER+pagecountValue);
				}
				else if(pagecountValue!=null)
				{
					referencePages.append(pagecountValue);
					referencePages.append(IDDELIMITER);
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
					cittext.append(IDDELIMITER);
					if(cittextelm.getContent()!=null)
					{
						cittext.append(dictionary.mapEntity(getMixData(cittextelm.getContent())));
					}
					cittext.append(IDDELIMITER);
					if(cittextelm.getAttribute("original")!=null)
					{
						cittext.append(cittextelm.getAttributeValue("original"));
					}
					cittext.append(IDDELIMITER);
					if(cittextelm.getAttribute("lang",xmlNamespace)!=null)
					{
						cittext.append(cittextelm.getAttributeValue("lang",xmlNamespace));
					}
					if(i<cittextlst.size()-1)
					{
						cittext.append(AUDELIMITER);
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
			if(issnType!=null && issnValue!= null)
			{
				issnBuffer.append(issnType+":"+issnValue);
			}
			if(e<issnList.size()-1)
			{
				issnBuffer.append(IDDELIMITER);
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
			isbnBuffer.append(IDDELIMITER);
			if(isbnLength!=null)
			{
				isbnBuffer.append(isbnLength);
			}
			isbnBuffer.append(IDDELIMITER);
			if(isbnVolume!=null)
			{
				isbnBuffer.append(isbnVolume);
			}
			isbnBuffer.append(IDDELIMITER);
			if(isbnValue!=null)
			{
				isbnBuffer.append(isbnValue);
			}
			if(i<isbnList.size()-1)
			{
				isbnBuffer.append(AUDELIMITER);
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
						tradenamegroupBuffer.append(AUDELIMITER);
					}
					Element tradenames = (Element)tradenamesList.get(i);

					if(tradenames != null)
					{

						List tradenameList = tradenames.getChildren("trademanuitem",noNamespace);
						String tradenameType = tradenames.getAttributeValue("type");
						tradenamegroupBuffer.append(tradenameType);

						tradenamegroupBuffer.append(IDDELIMITER);

						for(int j=0;j<tradenameList.size();j++)
						{
							Element tradenameElement = (Element)tradenameList.get(j);

							String tradename =  tradenameElement.getChildText("tradename",noNamespace);

							if(	tradename != null && tradename.length()>0)
							{
								tradenamegroupBuffer.append(tradename);
							}

							tradenamegroupBuffer.append(IDDELIMITER);

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
						manufacturergroupBuffer.append(AUDELIMITER);
					}
					Element manufacturers = (Element)manufacturergroupList.get(i);

					if(manufacturers != null)
					{
						List manufacturerList = manufacturers.getChildren("manufacturer",noNamespace);
						String manufacturerType = manufacturers.getAttributeValue("type");

						manufacturergroupBuffer.append(manufacturerType);

						manufacturergroupBuffer.append(IDDELIMITER);

						for(int j=0;j<manufacturerList.size();j++)
						{
							Element manufacturerElement = (Element)manufacturerList.get(j);
							String country = manufacturerElement.getAttributeValue("country");
							if(country!=null)
							{
								manufacturergroupBuffer.append(country);
							}

							manufacturergroupBuffer.append(GROUPDELIMITER);

							String manufacturer =  manufacturerElement.getText();

							if(	manufacturer != null && manufacturer.length()>0)
							{
								manufacturergroupBuffer.append(manufacturer);
							}

							manufacturergroupBuffer.append(IDDELIMITER);

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
						sequencebanksBuffer.append(AUDELIMITER);
					}
					Element sequencebank = (Element)sequencebankList.get(i);
					if(sequencebank != null)
					{
						List sequencenumberList = sequencebank.getChildren("sequence-number",noNamespace);
						String sequencebankName = sequencebank.getAttributeValue("name");
						sequencebanksBuffer.append(sequencebankName);

						sequencebanksBuffer.append(IDDELIMITER);

						for(int j=0;j<sequencenumberList.size();j++)
						{
							Element sequencenumberElement = (Element)sequencenumberList.get(j);

							String sequencenumber =  sequencenumberElement.getText();

							if(	sequencenumber != null && sequencenumber.length()>0)
							{
								sequencebanksBuffer.append(sequencenumber);
							}

							sequencebanksBuffer.append(IDDELIMITER);

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
						chemicalgroupBuffer.append(AUDELIMITER);
					}
					Element chemicals = (Element)chemicalsList.get(i);
					if(chemicals != null)
					{
						List chemicalList = chemicals.getChildren("chemical",noNamespace);
						for(int j=0;j<chemicalList.size();j++)
						{
							Element chemical = (Element)chemicalList.get(j);

							chemicalgroupBuffer.append(IDDELIMITER);

							String chemical_name =  chemical.getChildText("chemical-name",noNamespace);
							List casregistrynumberList = chemical.getChildren("cas-registry-number",noNamespace);

							if(	chemical_name != null)
							{
								chemicalgroupBuffer.append(chemical_name);
							}

							chemicalgroupBuffer.append(GROUPDELIMITER);

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
										chemicalgroupBuffer.append(GROUPDELIMITER);
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

	private void parseCorrespondenceElement(Element correspondence,Hashtable record) throws Exception
	{
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
				eAddressString = eAddressType+IDDELIMITER+eAddressString;
				record.put("CORRESPONDENCEEADDRESS",eAddressString);
				record.put("CORRESPONDENCEEADDRESS",eAddressString);
			}

			if(personName!=null)
			{
				//System.out.println("personName "+personName);
				record.put("CORRESPONDENCENAME",personName);
			}

			if(affString!=null)
			{
				//System.out.println("CORRESPONDENCEAFFILIATION "+affString);
				record.put("CORRESPONDENCEAFFILIATION",affString);
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
				record.put("SOURCECOUNTRY", sourceCountry);
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
				record.put("SOURCETITLE", dictionary.mapEntity(getMixData(sourcetitle.getContent())));
				//System.out.println("sourcetitle::"+sourcetitle.getText());
				//System.out.println("sourcetitle1::"+getMixData(sourcetitle.getText()));
			}

			// SOURCETITLEABBREV

			Element sourcetitleabbrev = (Element)source.getChild("sourcetitle-abbrev",noNamespace);
			if(sourcetitleabbrev != null)
			{
				record.put("SOURCETITLEABBREV", dictionary.mapEntity(getMixData(sourcetitleabbrev.getContent())));
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
						ttBuffer.append(AUDELIMITER);
					}
					Element translateTitle = (Element)translatedsourcetitle.get(i);
					ttBuffer.append(dictionary.mapEntity(getMixData(translateTitle.getContent())));
				}
				record.put("TRANSLATEDSOURCETITLE", (ttBuffer.toString()));
				//System.out.println("translatedsourcetitle::"+ttBuffer.toString());
			}

			//volumetitle
			Element volumetitle = (Element)source.getChild("volumetitle",noNamespace);
			if(volumetitle != null)
			{
				record.put("VOLUMETITLE", dictionary.mapEntity(getMixData(volumetitle.getContent())));
				//System.out.println("volumetitle::"+volumetitle.getText());
			}

			//ISSUETITLE
			Element issuetitle = (Element)source.getChild("issuetitle",noNamespace);
			if(issuetitle != null)
			{
				record.put("ISSUETITLE", dictionary.mapEntity(getMixData(issuetitle.getContent())));
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
					isbnBuffer.append(IDDELIMITER);
					if(isbnLength!=null)
					{
						isbnBuffer.append(isbnLength);
					}
					isbnBuffer.append(IDDELIMITER);
					if(isbnVolume!=null)
					{
						isbnBuffer.append(isbnVolume);
					}
					isbnBuffer.append(IDDELIMITER);
					if(isbnValue!=null)
					{
						isbnBuffer.append(isbnValue);
					}
					if(i<isbnList.size()-1)
					{
						isbnBuffer.append(AUDELIMITER);
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
					pagesBuffer.append(AUDELIMITER);
					pagesBuffer.append(AUDELIMITER);
				}
				else if(pagerange != null)
				{
					String firstPage = pagerange.getAttributeValue("first");
					String lastPage = pagerange.getAttributeValue("last");
					pagesBuffer.append(AUDELIMITER);
					if(firstPage != null)
					{
						pagesBuffer.append(firstPage);
					}
					pagesBuffer.append(AUDELIMITER);
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
							pagecountBuffer.append(AUDELIMITER);
						}
						Element pagecount = (Element)pagecountList.get(i);
						String pagecountValue = pagecount.getTextTrim();
						String pagecountType  = pagecount.getAttributeValue("type");
						if(pagecountType != null && pagecountValue != null)
						{
							pagecountBuffer.append(pagecountType+IDDELIMITER+pagecountValue);
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
				if(bibrecord!=null && bibrecord.getChild("history",noNamespace)!=null)
				{
					Element dateCreated = (bibrecord.getChild("history",noNamespace)).getChild("date-created",noNamespace);
					if(dateCreated!=null && dateCreated.getAttributeValue("year")!=null)
					{
						aipPubYear = dateCreated.getAttributeValue("year");
						record.put("PUBLICATIONYEAR",aipPubYear);
					}
				}

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
					record.put("PUBLICATIONDATE",publicationdateDateText);
				}
				else
				{
					dateString = getDateString(publicationdateYear,publicationdateMonth,publicationdateDay);
					if(dateString != null)
					{
						record.put("PUBLICATIONDATE",dateString);
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
						record.put("PUBLICATIONDATE",dateString);
					}
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
			ContributorBuffer.append(IDDELIMITER);
			if(contributorSeq!=null)
			{
				ContributorBuffer.append(contributorSeq);
			}
			else
			{
				ContributorBuffer.append(i);
			}
			ContributorBuffer.append(IDDELIMITER);
			if(contributorAuid!=null)
			{
				ContributorBuffer.append(contributorAuid);
			}
			ContributorBuffer.append(IDDELIMITER);
			if(contributorType!=null)
			{
				ContributorBuffer.append(contributorType);
			}
			ContributorBuffer.append(IDDELIMITER);
			if(contributorRoleString!=null)
			{
				if(contributorRole.get(contributorRoleString)!=null)
				{
					contributorRoleString = (String)contributorRole.get(contributorRoleString);
				}
				ContributorBuffer.append(contributorRoleString);
			}
			ContributorBuffer.append(IDDELIMITER);
			if(contributorString!=null)
			{
				ContributorBuffer.append(contributorString);
			}
			ContributorBuffer.append(IDDELIMITER);
			if(eaddress!=null)
			{
				ContributorBuffer.append(eaddress);
			}
			if(i<contributorList.size()-1)
			{
				ContributorBuffer.append(AUDELIMITER);
			}

		}
		return ContributorBuffer.toString();
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
				contributeString = getContributor(e,collaborationList);
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
				contributorAffiliationBuffer.append(e+IDDELIMITER+affiliationString);
			}
			contributorBuffer.append(AUDELIMITER);
			contributorAffiliationBuffer.append(AUDELIMITER);
		}

		record.put("CONTRIBUTOR", contributorBuffer.toString());
		//System.out.println("CONTRIBUTOR "+contributorBuffer.toString());
		record.put("CONTRIBUTORAFFILIATION", contributorAffiliationBuffer.toString());
		//System.out.println("CONTRIBUTORAFFILIATION "+contributorAffiliationBuffer.toString());
	}

	private String getDateString(String year,String month, String day)
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
						pnBuffer.append(AUDELIMITER);
					}
					pnBuffer.append(publisherName);
				}

				String publisheraddress = publisher.getChildTextTrim("publisheraddress",noNamespace);
				if(publisheraddress != null)
				{
					if(paBuffer.length()>0)
					{
						paBuffer.append(AUDELIMITER);
					}
					paBuffer.append(publisheraddress);
				}
				else if(publisher.getChild("affiliation",noNamespace)!=null)
				{
					if(paBuffer.length()>0)
					{
						paBuffer.append(AUDELIMITER);
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
						pElectronicABuffer.append(AUDELIMITER);
					}
					String peaType = publisherElectronicAddress.getAttributeValue("type");
					if(peaType == null)
					{
						peaType = "email";
					}
					String publisherElectronicAddresValue = publisherElectronicAddress.getText();
					if(publisherElectronicAddresValue != null)
					{
						pElectronicABuffer.append(peaType+IDDELIMITER+publisherElectronicAddresValue);
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

					//System.out.println("CONFNAME " + confname);

					//if(confnumber!= null && confname != null)
					//{
					//	record.put("CONFNAME",dictionary.mapEntity(getMixData(confnumber.getContent())+" "+getMixData(confname.getContent())));
					//}
					//else if(confname!= null)
					/* based on Frank request, remove confnumber from conference name column 10/23/2014 */
					if(confname!= null)
					{
						record.put("CONFNAME",dictionary.mapEntity(getMixData(confname.getContent())));
					}
					/*
					else if(confnumber!= null)
					{
						record.put("CONFNAME",dictionary.mapEntity(getMixData(confnumber.getContent())));
					}
					*/

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
								confsponsorsBuffer.append(AUDELIMITER);
							}
							if(confsponsor != null)
							{
								confsponsorsBuffer.append(confsponsor.getTextTrim());
							}
						}
						record.put("CONFSPONSORS",confsponsorsBuffer.toString());
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
					record.put("CONFERENCEEDITOR",editor);
				}
			}

			String editororganization = confeditors.getChildTextTrim("editororganization",noNamespace);
			if(editororganization != null)
			{
				//System.out.println("editororganization "+editororganization);
				record.put("CONFERENCEORGANIZATION",editororganization);
			}

			String editoraddress = confeditors.getChildTextTrim("editoraddress",noNamespace);
			if(editoraddress != null)
			{
				//System.out.println("editoraddress "+editoraddress);
				record.put("CONFERENCEEDITORADDRESS",editoraddress);
			}

		}

	}


	private String getConfAffiliation(Element affiliation)
	{
		StringBuffer affBuffer = new StringBuffer();

		//afid
		if(affiliation!=null)
		{
			if(affiliation.getAttributeValue("afid")!=null)
			{
				affBuffer.append(affiliation.getAttributeValue("afid"));
			}

			affBuffer.append(IDDELIMITER);
			// Text
			Element afElem =(Element) affiliation.getChild("text",ceNamespace);
			Element venue = (Element)affiliation.getChild("venue",noNamespace);

			if(venue!= null)
			{
				affBuffer.append(venue.getTextTrim());
			}
			affBuffer.append(IDDELIMITER);

			if(afElem!=null)
			{
			    affBuffer.append(dictionary.mapEntity(getMixData(afElem.getContent())));
			}
			affBuffer.append(IDDELIMITER);

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
						organizationBuffer.append(dictionary.mapEntity(getMixData(organization.getContent())));
					}
				}
			}
			affBuffer.append(organizationBuffer.toString());
			affBuffer.append(IDDELIMITER);

			if(affiliation.getChild("address-part",noNamespace)!=null)
			{
				affBuffer.append(dictionary.mapEntity(getMixData(affiliation.getChild("address-part",noNamespace).getContent())));
			}

			affBuffer.append(IDDELIMITER);

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
					cityGroupBuffer.append(city);
					if(state != null || postalCode != null)
					{
						cityGroupBuffer.append(", ");
					}
				}

				if(state != null)
				{
					cityGroupBuffer.append(state+" ");
				}
				if(postalCode != null)
				{
					cityGroupBuffer.append(postalCode);
				}
			}

			if(cityGroupBuffer.length()>0)
			{
				affBuffer.append(cityGroupBuffer.toString());
			}

			affBuffer.append(IDDELIMITER);

			if(affiliation.getAttributeValue("country")!=null)
			{
				affBuffer.append(affiliation.getAttributeValue("country"));
			}
			affBuffer.append(AUDELIMITER);
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

			affBuffer.append(IDDELIMITER);
			// Text
			Element afElem =(Element) affiliation.getChild("text",ceNamespace);
			Element venue = (Element)affiliation.getChild("venue",noNamespace);

			if(venue!= null)
			{
				affBuffer.append(venue.getTextTrim());
			}
			affBuffer.append(IDDELIMITER);

			if(afElem!=null)
			{
				affBuffer.append(dictionary.mapEntity(getMixData(afElem.getContent())));
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
							organizationBuffer.append(dictionary.mapEntity(getMixData(organization.getContent())));
						}
					}
				}
				affBuffer.append(organizationBuffer.toString());
				affBuffer.append(GROUPDELIMITER);

				if(affiliation.getChild("address-part",noNamespace)!=null)
				{
					affBuffer.append(dictionary.mapEntity(getMixData(affiliation.getChild("address-part",noNamespace).getContent())));
				}

				affBuffer.append(GROUPDELIMITER);

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
						cityGroupBuffer.append(city);
						if(state != null || postalCode != null)
						{
							cityGroupBuffer.append(", ");
						}
					}

					if(state != null)
					{
						cityGroupBuffer.append(state+" ");
					}
					if(postalCode != null)
					{
						cityGroupBuffer.append(postalCode);
					}
				}

				if(cityGroupBuffer.length()>0)
				{
					affBuffer.append(cityGroupBuffer.toString());
				}

				affBuffer.append(GROUPDELIMITER);

				if(affiliation.getAttributeValue("country")!=null)
				{
					affBuffer.append(affiliation.getAttributeValue("country"));
				}

			}
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
				editorBuffer.append(AUDELIMITER);
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
			editorBuffer.append(IDDELIMITER);
			if(editor.getChildTextTrim("initials",ceNamespace)!=null)
			{
				editorBuffer.append(editor.getChildTextTrim("initials",ceNamespace));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChild("indexed-name",ceNamespace)!=null)
			{
				editorBuffer.append(dictionary.mapEntity(getMixData(editor.getChild("indexed-name",ceNamespace).getContent())));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChildTextTrim("degrees",ceNamespace)!=null)
			{
				editorBuffer.append(editor.getChildTextTrim("degrees",ceNamespace));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChild("surname",ceNamespace)!=null)
			{
				editorBuffer.append(dictionary.mapEntity(getMixData(editor.getChild("surname",ceNamespace).getContent())));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChild("given-name",ceNamespace)!=null)
			{
				editorBuffer.append(dictionary.mapEntity(getMixData(editor.getChild("given-name",ceNamespace).getContent())));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChildTextTrim("suffix",ceNamespace)!=null)
			{
				editorBuffer.append(editor.getChildTextTrim("suffix",ceNamespace));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChild("nametext",noNamespace)!=null)
			{
				editorBuffer.append(dictionary.mapEntity(getMixData(editor.getChild("nametext",noNamespace).getContent())));
			}
			editorBuffer.append(IDDELIMITER);
			if(editor.getChild("text",ceNamespace)!=null)
			{
				editorBuffer.append(dictionary.mapEntity(getMixData(editor.getChild("text",ceNamespace).getContent())));
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
	private void setAuthorAndAffs(BdAuthors ausmap,
								  BdAffiliations affs,
								  Element aelement)
	{
		List authorlist = aelement.getChildren("author",noNamespace);

		if(authorlist==null || authorlist.size()==0)
		{
			authorlist = aelement.getChildren("contributor",noNamespace);
		}
		if(authorlist==null || authorlist.size()==0)
		{
			authorlist = aelement.getChildren("collaboration",noNamespace);
		}


		//affiliations

		Element affiliation =(Element) aelement.getChild("affiliation",noNamespace);

		if(affiliation != null)
		{
			BdAffiliation aff = new BdAffiliation();
			aff.setAffCountry(affiliation.getAttributeValue("country"));
			Element addresspart =(Element) affiliation.getChild("address-part",noNamespace);
			if(addresspart != null)
			{
				aff.setAffAddressPart(dictionary.mapEntity(getMixData(addresspart.getContent())));
			}

			Element citygroup = (Element) affiliation.getChild("city-group",noNamespace);
			if(citygroup != null)
			{
			    aff.setAffCityGroup(dictionary.mapEntity(getMixData(citygroup.getContent())));
			}

			Element city = (Element) affiliation.getChild("city",noNamespace);
			if(city != null)
			{
			    aff.setAffCity(dictionary.mapEntity(getMixData(city.getContent())));
			}

			Element state = (Element) affiliation.getChild("state",noNamespace);
			if(state != null)
			{
				aff.setAffState(state.getText());
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
			        }
			    }
			}
			if(zipbuf.length() > 0)
			{
			    aff.setAffPostalCode(zipbuf.toString());
			}

			Element text = (Element) affiliation.getChild("text",ceNamespace);
			if(text != null)
			{
				aff.setAffText(dictionary.mapEntity(getMixData(text.getContent())));
			}

			// organization  - mulity element
			List organization = affiliation.getChildren("organization",noNamespace);
			for (int i = 0; i < organization.size(); i++)
			{
				Element oe = (Element) organization.get(i);
				aff.addAffOrganization(dictionary.mapEntity(getMixData(oe.getContent())));
			}
			this.affid = this.affid+1;
			aff.setAffid(this.affid);
			affs.addAff(aff);
		}

		//end of affiliation

		// begin authors
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

		    Attribute auid = author.getAttribute("auid");
		    if(auid != null)
		    {
		        String auidstr = (String) auid.getValue();
		        aus.setAuid(auidstr);
		    }


		    Element indexedName = author.getChild("indexed-name",ceNamespace );
		    if(indexedName != null)
		    {
		        aus.setIndexedName(dictionary.mapEntity(getMixData(indexedName.getContent())));
		    }

		    Element initials = author.getChild("initials",ceNamespace );
		    if(initials != null)
		    {
			    aus.setInitials(initials.getText());
		    }

		    Element degrees = author.getChild("degrees", ceNamespace);
		    if(degrees != null)
		    {
		        aus.setDegrees(degrees.getText());

		    }

		    Element surname = author.getChild("surname", ceNamespace);
		    if(surname != null)
		    {
		        aus.setSurname(dictionary.mapEntity(getMixData(surname.getContent())));
		    }

		    Element givenName = author.getChild("given-name", ceNamespace);
		    if(givenName != null)
		    {
		        aus.setGivenName(dictionary.mapEntity(getMixData(givenName.getContent())));
		    }

		    Element suffix = author.getChild("suffix",ceNamespace);
		    if(suffix != null)
		    {
		        aus.setSuffix(suffix.getText());
		    }

		    Element nametext= author.getChild("nametext", ceNamespace);
		    if(nametext != null)
		    {
		        aus.setNametext(dictionary.mapEntity(getMixData(nametext.getContent())));
		    }

		    Element prefferedName= author.getChild("preffered-name", ceNamespace);

		    // prefferedName block

		    if(prefferedName != null)
		    {
		       Element prefferedNameInitials = prefferedName.getChild("initials",ceNamespace);
		       if(prefferedNameInitials != null)
		       {
		           aus.setPrefnameInitials(dictionary.mapEntity(getMixData(prefferedNameInitials.getContent())));
		       }

		       Element prefferedNameIndexedname = prefferedName.getChild("indexed_name",ceNamespace);
		       if(prefferedNameIndexedname != null)
		       {

		           aus.setPrefnameIndexedname(dictionary.mapEntity(getMixData(prefferedNameIndexedname.getContent())));
		       }

		       Element prefferedNameDegrees = prefferedName.getChild("degree", ceNamespace);
		       if(prefferedNameDegrees != null)
		       {
		           aus.setPrefnameDegrees(prefferedNameDegrees.getText());
		       }

		       Element prefferedNameSurname = prefferedName.getChild("surname", ceNamespace);
		       if(prefferedNameSurname != null)
		       {
		           aus.setPrefnameSurname(dictionary.mapEntity(getMixData(prefferedNameSurname.getContent())));
		       }
		       Element prefferedNameGivenname = prefferedName.getChild("given-name", ceNamespace);

		       if(prefferedNameGivenname != null)
		       {
		           aus.setPrefnameGivenname(dictionary.mapEntity(getMixData(prefferedNameGivenname.getContent())));
		       }
		    }
		    // end of prefferedName block

		    Element eaddress = author.getChild("e-address", ceNamespace);
		    if(eaddress != null)
		    {
		        aus.setEaddress(eaddress.getText());
		    }

		    ausmap.addCpxaus(aus);
		}

	}

	private String auffToStringBuffer(BdAffiliations aufflist,
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
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffOrganization() != null)
				{
					bufaffiliations.append(aAffiliation.getAffOrganization());
				}
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffCityGroup() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCityGroup());
				}
				bufaffiliations.append(IDDELIMITER);
				if (aAffiliation.getAffCountry() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCountry());
				}
				bufaffiliations.append(IDDELIMITER);

				if (aAffiliation.getAffAddressPart() != null)
				{
					bufaffiliations.append(aAffiliation.getAffAddressPart());
				}
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffCity() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCity());
				}
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffState() != null)
				{
					bufaffiliations.append(aAffiliation.getAffState());
				}
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffPostalCode() != null)
				{
					bufaffiliations.append(aAffiliation.getAffPostalCode());

				}
				bufaffiliations.append(IDDELIMITER);
				if(aAffiliation.getAffText() != null)
				{
					bufaffiliations.append(aAffiliation.getAffText());

				}
				bufaffiliations.append(IDDELIMITER);
				bufaffiliations.append(AUDELIMITER);

			}
		}

		if(bufaffiliations.length() < 3500 )
		{
			return bufaffiliations.toString();
		}
		else if(bufaffiliations.length() >= 3500)
		{

			int endFirstAffGroupMarker = bufaffiliations.lastIndexOf(AUDELIMITER, 3500)+1;
			String firstAffGroup = bufaffiliations.substring(0, endFirstAffGroupMarker);
			if(bufaffiliations.length() > 7500)
			{
				String secAffGroup = bufaffiliations.substring(endFirstAffGroupMarker,(endFirstAffGroupMarker+3500));
				int endSecondAffGroup = secAffGroup.lastIndexOf(AUDELIMITER)+1;
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

	private String auToStringBuffer(BdAuthors auslist)
	{
			StringBuffer bufauthor = new StringBuffer();
			StringBuffer returnbuf = new StringBuffer();
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
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getAuid() != null)
					{
						bufauthor.append(aAuthor.getAuid());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getAffidStr() != null)
					{
						bufauthor.append(aAuthor.getAffidStr());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getIndexedName() != null)
					{
						bufauthor.append(aAuthor.getIndexedName());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getInitials() != null)
					{
						bufauthor.append(aAuthor.getInitials());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getSurname() != null)
					{
						bufauthor.append(aAuthor.getSurname());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getDegrees() != null)
					{
						bufauthor.append(aAuthor.getDegrees());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getGivenName() != null)
					{
						bufauthor.append(aAuthor.getGivenName());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getSuffix() != null)
					{
						bufauthor.append(aAuthor.getSuffix());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getNametext() != null)
					{
						bufauthor.append(aAuthor.getNametext());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getPrefnameInitials() != null)
					{
						bufauthor.append(aAuthor.getPrefnameInitials());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getPrefnameIndexedname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameIndexedname());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getPrefnameDegrees() != null)
					{
						bufauthor.append(aAuthor.getPrefnameDegrees());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getPrefnameSurname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameSurname());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getPrefnameGivenname() != null)
					{
						bufauthor.append(aAuthor.getPrefnameGivenname());
					}
					bufauthor.append(IDDELIMITER);
					if(aAuthor.getEaddress() != null)
					{
						bufauthor.append(aAuthor.getEaddress());
					}
					bufauthor.append(IDDELIMITER);
					bufauthor.append(AUDELIMITER);
				}
			}


			return bufauthor.toString();

	}



	private String auToStringBuffer(BdAuthors auslist,
						        	StringBuffer secondAuGroup)
	{
		StringBuffer bufauthor = new StringBuffer();
		StringBuffer returnbuf = new StringBuffer();
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
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getAuid() != null)
				{
					bufauthor.append(aAuthor.getAuid());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getAffidStr() != null)
				{
					bufauthor.append(aAuthor.getAffidStr());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getIndexedName() != null)
				{
					bufauthor.append(aAuthor.getIndexedName());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getInitials() != null)
				{
					bufauthor.append(aAuthor.getInitials());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getSurname() != null)
				{
					bufauthor.append(aAuthor.getSurname());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getDegrees() != null)
				{
					bufauthor.append(aAuthor.getDegrees());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getGivenName() != null)
				{
					bufauthor.append(aAuthor.getGivenName());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getSuffix() != null)
				{
					bufauthor.append(aAuthor.getSuffix());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getNametext() != null)
				{
					bufauthor.append(aAuthor.getNametext());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getPrefnameInitials() != null)
				{
					bufauthor.append(aAuthor.getPrefnameInitials());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getPrefnameIndexedname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameIndexedname());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getPrefnameDegrees() != null)
				{
					bufauthor.append(aAuthor.getPrefnameDegrees());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getPrefnameSurname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameSurname());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getPrefnameGivenname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameGivenname());
				}
				bufauthor.append(IDDELIMITER);
				if(aAuthor.getEaddress() != null)
				{
					bufauthor.append(aAuthor.getEaddress());
				}
				bufauthor.append(IDDELIMITER);
				bufauthor.append(AUDELIMITER);
			}
		}

		if(bufauthor.length() < 3500 )
		{
			return bufauthor.toString();
		}
		else if(bufauthor.length() >= 3500)
		{
			int endFirstAuGroupMarker = bufauthor.lastIndexOf(AUDELIMITER, 3500)+1;
			String firstAuGroup = bufauthor.substring(0, endFirstAuGroupMarker);
			if(bufauthor.length() > 7500)
			{
				String secAuGroup = bufauthor.substring(endFirstAuGroupMarker,(endFirstAuGroupMarker+3500));
				int endSecondAuGroupMarker= secAuGroup.lastIndexOf(AUDELIMITER)+1;
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
							String mainterm = dictionary.mapEntity(getMixData(descriptor.getChild("mainterm",noNamespace).getContent()));
							if(mhBuffer.length()>0)
							{
								mhBuffer.append(AUDELIMITER);
							}
							mhBuffer.append(mainterm);
						}
						if(descriptorsType.equals("SPC") && mhBuffer.length()>4000)
						{
							term1 = mhBuffer.substring(0,4000);
							term2 = mhBuffer.substring(4000);
							record.put("SPECIESTERM",term1);
							record.put("SPECIESTERM2",term2);
						}
						else
						{
							record.put(descriptorsTypeTable.get(descriptorsKey),mhBuffer.toString());
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
						(classificationType.equals("CPXCLASS")||
								classificationType.equals("GEOCLASS")))
				{
					List classificationList = classifications.getChildren("classification",noNamespace);
					for(int j=0;j<classificationList.size();j++)
					{
						Element classification = (Element)classificationList.get(j);
						String cl = classification.getTextTrim();
						String clc = classification.getChildTextTrim("classification-code",noNamespace);

						if(clBuffer.length()>0)
						{
							clBuffer.append(AUDELIMITER);
						}

						if((cl!=null && cl.length()>0) && (clc!=null && clc.length()>0))
						{
							clBuffer.append(cl+AUDELIMITER+clc);
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
			}
		}

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
