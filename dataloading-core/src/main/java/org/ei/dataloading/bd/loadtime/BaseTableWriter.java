package org.ei.dataloading.bd.loadtime;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.bd.*;
import org.ei.common.Constants;

public class BaseTableWriter
{
	private Perl5Util perl = new Perl5Util();

	private String filename;

	private PrintWriter out;
	private PrintWriter referenceOut;
	private PrintWriter puiOut;
	private PrintWriter samePuiPuisecondaryOut;
	private PrintWriter oversizeFieldOut;
	
	private boolean open = false;

	private LinkedHashSet bdColumns = BaseTableRecord.getBdColumns();
	private Hashtable bdColumnsSize = new Hashtable();
	public static final char FIELDDELIM = '\t';
	private String accessNumber;
	private String pui;
	private String doi;
	private String loadnumber;
	private String updatenumber;
	private String mid;
	private String database;
	private int refCount;
	private List nullAccessumberRecord=new ArrayList();

	public BaseTableWriter(String filename)
	{
		this.filename = filename;
	}

	public void begin()
			throws Exception
	{

		out = new PrintWriter(new FileWriter(filename));
		String path="";
		String name="";
		int pathSeperator = filename.lastIndexOf("/");
		System.out.println("pathSeperator "+pathSeperator);
		if(pathSeperator>=0)
		{
			path=filename.substring(0,pathSeperator+1);			
		}
		name=filename.substring(pathSeperator+1);
		System.out.println("PATH "+path);
		System.out.println("NAME "+name);
		
		referenceOut = new PrintWriter(new FileWriter(path+"Reference_"+name));
		System.out.println("Output Filename "+filename);
		System.out.println("Reference Output Filename "+path+"Reference_"+name);
		
		puiOut = new PrintWriter(new FileWriter(path+"Pui_"+name));
		System.out.println("Output Filename "+filename);
		System.out.println("Reference Output Filename "+path+"Reference_"+name);
		System.out.println("PUI Output Filename "+path+"Pui_"+name);
		
		samePuiPuisecondaryOut = new PrintWriter(new FileWriter(path+"SamePui_PuiSecondary_"+name));
		
		oversizeFieldOut = new PrintWriter(new FileWriter(path+"OverSizeField_"+name));
		open = true;
	}

	public void writeRec(Hashtable record)
			throws Exception
	{

		StringBuffer recordBuf = new StringBuffer();
		Iterator bdData = bdColumns.iterator();
		boolean  isReference = false;
		setAccessionNumber("");
		setPui("");
		setMID("");
		setRefcount("");
		setLoadnumber("");
		setUpdatenumber("");
		
		
		
		while (bdData.hasNext())
		{
			BaseTableRecord column=null;
			Integer columnLength = null;
			String thisColumnName = null;
		    column = (BaseTableRecord)bdData.next();
			thisColumnName = (String)column.getName();
			if(record == null)
			{
				System.out.println("Record was null");
			}
			
			String valueString = null;
			columnLength =(Integer) column.getColumnLength();
			bdColumnsSize.put(thisColumnName,columnLength);
			if(record.get(thisColumnName)!=null)
			{
				try
				{
						if(!thisColumnName.equals("REFERENCE"))
						{
							valueString = checkColumnWidth(columnLength.intValue(),
														   thisColumnName,
														   (String)record.get(thisColumnName));
							if(thisColumnName.equals("ACCESSNUMBER"))
							{
								setAccessionNumber(valueString);
							}
							
							//if(thisColumnName.equals("PUI"))
							if(getPui().equals("") && record.get("PUI")!=null)
							{
								setPui((String)record.get("PUI"));
							}
							
							if(thisColumnName.equals("DOI"))
							{
								setDOI(valueString);
							}

							if(thisColumnName.equals("M_ID"))
							{
								setMID(valueString);
							}

							if(thisColumnName.equals("REFCOUNT"))
							{
								setRefcount(valueString);
							}
							
							if(thisColumnName.equals("LOADNUMBER"))
							{
								setLoadnumber(valueString);
							}
							
							//if(thisColumnName.equals("UPDATENUMBER"))
							if(getUpdatenumber().equals("") && record.get("UPDATENUMBER")!=null)
							{
								setUpdatenumber((String)record.get("UPDATENUMBER"));							
							}						
							
							if(thisColumnName.equals("DATABASE"))
							{
								setDatabase(valueString);
							}

						}
						else
						{
							isReference = true;
							Hashtable reference =  (Hashtable)record.get("REFERENCE");
							outputReference(reference,getAccessionNumber(),getMID(),getRefcount(), getPui(),getLoadnumber(),getUpdatenumber());
						}
					}
					catch(Exception e)
					{
						System.out.println("Access Number= "+getAccessionNumber()+" COLUMN NAME= "+thisColumnName);
						e.printStackTrace();
					}

			}

			if(valueString != null && !thisColumnName.equals("EID") &&!thisColumnName.equals("UPDATERESOURCE"))
			{
				recordBuf.append(valueString);
				//recordBuf.append(thisColumnName+":"+valueString);
			}
			if(thisColumnName.length()<9 || !("REF".equals(thisColumnName.substring(0,3))))
			{
				if(!("EID".equals(thisColumnName)) && !("UPDATERESOURCE".equals(thisColumnName)))
				{
					recordBuf.append(FIELDDELIM);
				}
			}
		}
		if(isReference)
		{
		   recordBuf.append("YES");
	    }
	    recordBuf.append(FIELDDELIM);
	    
	    ///* use for CAFE data
	    if(record.get("EID")!=null)
		{
		   recordBuf.append((String)record.get("EID"));
	    }
	    recordBuf.append(FIELDDELIM);
	    
	    if(record.get("UPDATERESOURCE")!=null)
		{
		   recordBuf.append((String)record.get("UPDATERESOURCE"));
	    }
	    recordBuf.append(FIELDDELIM);
	    recordBuf.append(getPui());
	    recordBuf.append(FIELDDELIM);
	    recordBuf.append("PUI");
	    recordBuf.append(FIELDDELIM);
	    if(record.get("RELATEDPUI")!=null)
		{
	    	recordBuf.append(record.get("RELATEDPUI"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("PUISECONDARY")!=null)
		{
	    	recordBuf.append(record.get("PUISECONDARY"));
	    	outputPuiSecondary((String)record.get("PUISECONDARY"),getPui(),getAccessionNumber(),getLoadnumber());
	    	//System.out.println("PUISECONDARY2="+record.get("PUISECONDARY"));
		}
	    else
	    {
	    	puiOut.println(getPui()+"\t"+getPui()+"\t"+getAccessionNumber()+"\t"+getLoadnumber());
	    }
	    recordBuf.append(FIELDDELIM);
	    if(record.get("GRANTTEXT")!=null)
		{
	    	int columnSize = 3990;
	    	String granttext= checkColumnWidth(columnSize,
					   "GRANTTEXT",
					   (String)record.get("GRANTTEXT"));
	    	//System.out.println("granttext length="+granttext.length()+" codelength="+lengthCodepoints(granttext));
	    	recordBuf.append(granttext);
	    		    	
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("NORMSTANDARDID")!=null)
		{
	    	recordBuf.append(record.get("NORMSTANDARDID"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("STANDARDDESIGNATION")!=null)
		{
	    	recordBuf.append(record.get("STANDARDDESIGNATION"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("STANDARDID")!=null)
		{
	    	recordBuf.append(record.get("STANDARDID"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("SOURCEBIBTEXT")!=null)
		{
	    	recordBuf.append(record.get("SOURCEBIBTEXT"));
		}
	   
	    
	    /*	    
	    recordBuf.append(FIELDDELIM);
	    if(record.get("RELATEDDOI")!=null)
		{
	    	recordBuf.append(record.get("RELATEDDOI"));
	    	//System.out.println("RELATEDDOI"+record.get("RELATEDDOI"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("RELATEDPII")!=null)
		{
	    	recordBuf.append(record.get("RELATEDPII"));
	    	//System.out.println("RELATEDPII"+record.get("RELATEDPII"));
		}
		*/
	    
	    //following fields added for open access status at 09/05/2018
	    recordBuf.append(FIELDDELIM);
	    if(record.get("OAACCESSDATE")!=null)
		{
	    	recordBuf.append(record.get("OAACCESSDATE"));
	    	//System.out.println("OAACCESSDATE"+record.get("OAACCESSDATE"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("OAUSERLICENSE")!=null)
		{
	    	recordBuf.append(record.get("OAUSERLICENSE"));
	    	//System.out.println("OAUSERLICENSE"+record.get("OAUSERLICENSE"));
		}
	    recordBuf.append(FIELDDELIM);
	    if(record.get("ISOPENACESS")!=null)
		{
	    	recordBuf.append(record.get("ISOPENACESS"));
	    	//System.out.println("ISOPENACESS"+record.get("ISOPENACESS"));
		}
	    
	    recordBuf.append(FIELDDELIM);
	    //added on 20200122 by hongrong for ani5.15
	    if(record.get("CSXTERM")!=null)
		{
	    	recordBuf.append(record.get("CSXTERM"));
	    	//System.out.println("CSXTERM"+record.get("CSXTERM"));
		}
	    
	    recordBuf.append(FIELDDELIM);
	    //added on 20200406 by hongrong for ani5.15
	    if(record.get("COLLABORATION")!=null && ((String)record.get("COLLABORATION")).length()>0)
		{
	    	recordBuf.append(record.get("COLLABORATION"));
	    	//System.out.println("COLLABORATION term"+record.get("COLLABORATION"));
		}
	    	    
	    recordBuf.append(FIELDDELIM);
	    //added on 20200406 by hongrong for ani5.15
	    if(record.get("COLLABORATION_AFF")!=null && ((String)record.get("COLLABORATION_AFF")).length()>0)
		{
	    	recordBuf.append(record.get("COLLABORATION_AFF"));
	    	//System.out.println("COLLABORATION AFF term"+record.get("COLLABORATION_AFF"));
		}
	    
	   //added on 20201209 by hongrong for EVOPS 1055
	    recordBuf.append(FIELDDELIM);
	    if(record.get("FREETOREADSTATUS")!=null)
		{
	    	recordBuf.append(record.get("FREETOREADSTATUS"));
	    	//System.out.println("FREETOREADSTATUS"+record.get("FREETOREADSTATUS"));
		}
	    
	    //*/
	    
	    if(getAccessionNumber()!=null && getAccessionNumber().length()>0)
	    {
	    	out.println(recordBuf.toString().trim());
	    }
	    else
	    {
	    	nullAccessumberRecord.add(getPui());
	    	System.out.println("****************************************");
	    	System.out.println("DATA PROBLEM");
	    	System.out.println("ACCESSNUMBER="+getAccessionNumber()+" PUI="+getPui()+" DOI="+getDOI());
	    	System.out.println("****************************************");
	    }


	}

	private void outputPuiSecondary(String puiSecondary, String pui, String accessnumber,String updatenumber)  throws Exception
	{
		
		if(puiSecondary!=null)
		{
			String[] puiSecondaryA = puiSecondary.split("\\|",-1);
			for(int i=0;i<puiSecondaryA.length;i++)
			{
				if(puiSecondaryA[i].length()>0)
				{
					if(!pui.equals(puiSecondaryA[i]))
					{
						puiOut.println(pui+"\t"+puiSecondaryA[i]+"\t"+accessnumber+"\t"+updatenumber);
					}
					else
					{
						samePuiPuisecondaryOut.println(pui);
					}
				}
			}
			puiOut.println(pui+"\t"+pui+"\t"+accessnumber+"\t"+updatenumber);
		}
		
		puiOut.flush();
		samePuiPuisecondaryOut.flush();
	}
	
	private void outputReference(Hashtable reference,String accessNumber,String mid, int refcount, String pui, String loadnumber, String updatenumber) throws Exception
	{
	   if(reference != null)
	   {

			 Hashtable REFERENCETITLE  =  (Hashtable)reference.get("REFERENCETITLE");
			 Hashtable REFERENCEAUTHOR =  (Hashtable)reference.get("REFERENCEAUTHOR");
			 Hashtable REFERENCESOURCETITLE = (Hashtable)reference.get("REFERENCESOURCETITLE");
			 Hashtable REFERENCEPUBLICATIONYEAR = (Hashtable)reference.get("REFERENCEPUBLICATIONYEAR");
			 Hashtable REFERENCEVOLUME = (Hashtable)reference.get("REFERENCEVOLUME");
			 Hashtable REFERENCEISSUE = (Hashtable)reference.get("REFERENCEISSUE");
			 Hashtable REFERENCEPAGES = (Hashtable)reference.get("REFERENCEPAGES");
			 Hashtable REFERENCEFULLTEXT = (Hashtable)reference.get("REFERENCEFULLTEXT");
			 Hashtable REFERENCETEXT = (Hashtable)reference.get("REFERENCETEXT");
			 Hashtable REFERENCEWEBSITE = (Hashtable)reference.get("REFERENCEWEBSITE");
			 Hashtable REFERENCEITEMID = (Hashtable)reference.get("REFERENCEITEMID");
			 Hashtable REFERENCEITEMCITATIONEID = (Hashtable)reference.get("REFERENCEITEMCITATIONEID");
			 Hashtable REFERENCEITEMCITATIONPII = (Hashtable)reference.get("REFERENCEITEMCITATIONPII");
			 
			 //change to take REFERENCEITEMDOI by hmo @12/16/2020
			 //Hashtable REFERENCEITEMCITATIONDOI = (Hashtable)reference.get("REFERENCEITEMCITATIONDOI");
			 Hashtable REFERENCEITEMCITATIONDOI = (Hashtable)reference.get("REFERENCEITEMDOI");
			 
			 Hashtable REFERENCEITEMCITATIONAUTHOR = (Hashtable)reference.get("REFERENCEITEMCITATIONAUTHOR");
			 Hashtable REFITEMCITATIONSOURCETITLE = (Hashtable)reference.get("REFITEMCITATIONSOURCETITLE");
			 Hashtable REFERENCEITEMCITATIONISSN = (Hashtable)reference.get("REFERENCEITEMCITATIONISSN");
			 Hashtable REFERENCEITEMCITATIONISBN = (Hashtable)reference.get("REFERENCEITEMCITATIONISBN");
			 Hashtable REFERENCEITEMCITATIONPART = (Hashtable)reference.get("REFERENCEITEMCITATIONPART");
			 Hashtable REFITEMCITATIONPUBLICATIONYEAR = (Hashtable)reference.get("REFITEMCITATIONPUBLICATIONYEAR");
			 Hashtable REFERENCEITEMCITATIONVOLUME = (Hashtable)reference.get("REFERENCEITEMCITATIONVOLUME");
			 Hashtable REFERENCEITEMCITATIONISSUE = (Hashtable)reference.get("REFERENCEITEMCITATIONISSUE");
			 Hashtable REFERENCEITEMCITATIONPAGE = (Hashtable)reference.get("REFERENCEITEMCITATIONPAGE");
			 Hashtable REFITEMCITATIONARTICLENUMBER = (Hashtable)reference.get("REFITEMCITATIONARTICLENUMBER");
			 Hashtable REFERENCEITEMCITATIONWEBSITE = (Hashtable)reference.get("REFERENCEITEMCITATIONWEBSITE");
			 Hashtable REFERENCEITEMCITATIONEADDRESS = (Hashtable)reference.get("REFERENCEITEMCITATIONEADDRESS");
			 Hashtable REFERENCEITEMCITATIONREFTEXT = (Hashtable)reference.get("REFERENCEITEMCITATIONREFTEXT");
			 Hashtable REFERENCEITEMCITATIONCITATIONTITLE =(Hashtable)reference.get("REFERENCEITEMCITATIONCITATIONTITLE");			 
			 //Hashtable REFERENCEITEMCITATIONSOURCETITLE =(Hashtable)reference.get("REFERENCEITEMCITATIONSOURCETITLE");
			 Hashtable REFERENCEITEMCITATIONSOURCETITLEABBREV =(Hashtable)reference.get("REFERENCEITEMCITATIONSOURCETITLEABBREV");
			 Hashtable REFERENCEITEMCITATIONCODEN = (Hashtable)reference.get("REFERENCEITEMCITATIONCODEN");
			 Hashtable REFERENCEITEMCITATIONID = (Hashtable)reference.get("REFERENCEITEMCITATIONID");
			 Hashtable REFITEMCITATIONAFFILIATION = (Hashtable)reference.get("REFITEMCITATIONAFFILIATION");
			 Hashtable REFERENCESOURCETEXT = (Hashtable)reference.get("REFERENCESOURCETEXT");

			 StringBuffer recordBuf = null;
			 //String referenceID = "0";
			 int referenceID = 0;
			 int size = 0;

			 for(int i=0;i<	refcount;i++)
			 {
				  recordBuf = new StringBuffer();
				  //referenceID = Integer.toString(i+1);
				  referenceID = i+1;
				  recordBuf.append(mid);
				  recordBuf.append(FIELDDELIM);
				  if(accessNumber!=null)
				  {
				  	recordBuf.append(accessNumber);
				  }
				  recordBuf.append(FIELDDELIM);
				  recordBuf.append(referenceID);
				  //System.out.println("referenceID="+referenceID);
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCETITLE!=null && REFERENCETITLE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCETITLE="+(String)REFERENCETITLE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCETITLE")).intValue(),
					  									"REFERENCETITLE",
					  									(String)REFERENCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEAUTHOR != null && REFERENCEAUTHOR.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEAUTHOR="+(String)REFERENCEAUTHOR.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEAUTHOR")).intValue(),
					  									"REFERENCEAUTHOR",
					  									(String)REFERENCEAUTHOR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCESOURCETITLE != null && REFERENCESOURCETITLE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCESOURCETITLE="+(String)REFERENCESOURCETITLE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCESOURCETITLE")).intValue(),
														"REFERENCESOURCETITLE",
														(String)REFERENCESOURCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEPUBLICATIONYEAR != null && REFERENCEPUBLICATIONYEAR.get(referenceID)!=null)
				  {
					  String value =  (String)REFERENCEPUBLICATIONYEAR.get(referenceID);
					  //System.out.println("REFERENCEPUBLICATIONYEAR="+(String)REFERENCEPUBLICATIONYEAR.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEPUBLICATIONYEAR")).intValue(),
					  									"REFERENCEPUBLICATIONYEAR",
					  									(String)REFERENCEPUBLICATIONYEAR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEVOLUME !=null && REFERENCEVOLUME.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEVOLUME="+(String)REFERENCEVOLUME.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEVOLUME")).intValue(),
					  									"REFERENCEVOLUME",
					  									(String)REFERENCEVOLUME.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEISSUE !=null && REFERENCEISSUE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEISSUE="+(String)REFERENCEISSUE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEISSUE")).intValue(),
					  									"REFERENCEISSUE",
					  									(String)REFERENCEISSUE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEPAGES != null && REFERENCEPAGES.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEPAGES="+(String)REFERENCEPAGES.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEPAGES")).intValue(),
					  									"REFERENCEPAGES",
					  									(String)REFERENCEPAGES.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEFULLTEXT != null && REFERENCEFULLTEXT.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEFULLTEXT="+(String)REFERENCEFULLTEXT.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEFULLTEXT")).intValue(),
					  									"REFERENCEFULLTEXT",
					  									(String)REFERENCEFULLTEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCETEXT != null && REFERENCETEXT.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCETEXT="+(String)REFERENCETEXT.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCETEXT")).intValue(),
					  									"REFERENCETEXT",
					  									(String)REFERENCETEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEWEBSITE != null && REFERENCEWEBSITE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEWEBSITE="+(String)REFERENCEWEBSITE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEWEBSITE")).intValue(),
					  									"REFERENCEWEBSITE",
					  									(String)REFERENCEWEBSITE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMID != null && REFERENCEITEMID.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMID="+(String)REFERENCEITEMID.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMID")).intValue(),
					  									"REFERENCEITEMID",
					  									(String)REFERENCEITEMID.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONPII != null && REFERENCEITEMCITATIONPII.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONPII="+(String)REFERENCEITEMCITATIONPII.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPII")).intValue(),
					  									"REFERENCEITEMCITATIONPII",
					  									(String)REFERENCEITEMCITATIONPII.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  //change to host referenceItemDOI
				  if(REFERENCEITEMCITATIONDOI != null && REFERENCEITEMCITATIONDOI.get(referenceID)!=null)
				  {
					 //System.out.println("REFERENCEITEMCITATIONDOI="+(String)REFERENCEITEMCITATIONDOI.get(referenceID));
					 recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONDOI")).intValue(),
					 									"REFERENCEITEMCITATIONDOI",
					 									(String)REFERENCEITEMCITATIONDOI.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONAUTHOR != null && REFERENCEITEMCITATIONAUTHOR.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONAUTHOR="+(String)REFERENCEITEMCITATIONAUTHOR.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONAUTHOR")).intValue(),
					  									"REFERENCEITEMCITATIONAUTHOR",
					  									(String)REFERENCEITEMCITATIONAUTHOR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFITEMCITATIONSOURCETITLE != null && REFITEMCITATIONSOURCETITLE.get(referenceID)!=null)
				  {
					  //System.out.println("***REFITEMCITATIONSOURCETITLE="+(String)REFITEMCITATIONSOURCETITLE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFITEMCITATIONSOURCETITLE")).intValue(),
					  									"REFITEMCITATIONSOURCETITLE",
					  									(String)REFITEMCITATIONSOURCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONISSN != null && REFERENCEITEMCITATIONISSN.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONISSN="+(String)REFERENCEITEMCITATIONISSN.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISSN")).intValue(),
					  									"REFERENCEITEMCITATIONISSN",
					  									(String)REFERENCEITEMCITATIONISSN.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONISBN !=null && REFERENCEITEMCITATIONISBN.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONISBN="+(String)REFERENCEITEMCITATIONISBN.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISBN")).intValue(),
					  									"REFERENCEITEMCITATIONISBN",
					  									(String)REFERENCEITEMCITATIONISBN.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONPART != null && REFERENCEITEMCITATIONPART.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONPART="+(String)REFERENCEITEMCITATIONPART.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPART")).intValue(),
					  									"REFERENCEITEMCITATIONPART",
					  									(String)REFERENCEITEMCITATIONPART.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFITEMCITATIONPUBLICATIONYEAR != null && REFITEMCITATIONPUBLICATIONYEAR.get(referenceID)!=null)
				  {  
					  
					  //System.out.println("REFITEMCITATIONPUBLICATIONYEAR="+(String)REFITEMCITATIONPUBLICATIONYEAR.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPUBLICATIONYEAR")).intValue(),
					  									"REFERENCEITEMCITATIONPUBLICATIONYEAR",
					  									(String)REFITEMCITATIONPUBLICATIONYEAR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONVOLUME != null && REFERENCEITEMCITATIONVOLUME.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONVOLUME="+(String)REFERENCEITEMCITATIONVOLUME.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONVOLUME")).intValue(),
					  									"REFERENCEITEMCITATIONVOLUME",
					  									(String)REFERENCEITEMCITATIONVOLUME.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONISSUE != null && REFERENCEITEMCITATIONISSUE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONISSUE="+(String)REFERENCEITEMCITATIONISSUE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISSUE")).intValue(),
					  									"REFERENCEITEMCITATIONISSUE",
					  									(String)REFERENCEITEMCITATIONISSUE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONPAGE != null && REFERENCEITEMCITATIONPAGE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONPAGE="+(String)REFERENCEITEMCITATIONPAGE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPAGE")).intValue(),
					  									"REFERENCEITEMCITATIONPAGE",
					  									(String)REFERENCEITEMCITATIONPAGE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFITEMCITATIONARTICLENUMBER != null && REFITEMCITATIONARTICLENUMBER.get(referenceID)!=null)
				  {
					  //System.out.println("REFITEMCITATIONARTICLENUMBER="+(String)REFITEMCITATIONARTICLENUMBER.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONARTICLENUMBER")).intValue(),
					  									"REFITEMCITATIONARTICLENUMBER",
					  									(String)REFITEMCITATIONARTICLENUMBER.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONWEBSITE != null && REFERENCEITEMCITATIONWEBSITE.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONWEBSITE="+(String)REFERENCEITEMCITATIONWEBSITE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONWEBSITE")).intValue(),
					  									"REFERENCEITEMCITATIONWEBSITE",
					  									(String)REFERENCEITEMCITATIONWEBSITE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONEADDRESS != null && REFERENCEITEMCITATIONEADDRESS.get(referenceID)!=null)
				  {
					  //System.out.println("***REFERENCEITEMCITATIONEADDRESS="+(String)REFERENCEITEMCITATIONEADDRESS.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONEADDRESS")).intValue(),
					  									"REFERENCEITEMCITATIONEADDRESS",
					  									(String)REFERENCEITEMCITATIONEADDRESS.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONREFTEXT != null && REFERENCEITEMCITATIONREFTEXT.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONREFTEXT="+(String)REFERENCEITEMCITATIONREFTEXT.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONREFTEXT")).intValue(),
					  									"REFERENCEITEMCITATIONREFTEXT",
					  									(String)REFERENCEITEMCITATIONREFTEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);


				  if(REFERENCEITEMCITATIONCITATIONTITLE != null && REFERENCEITEMCITATIONCITATIONTITLE.get(referenceID)!=null)
				  {
					  //System.out.println("***REFERENCEITEMCITATIONCITATIONTITLE="+(String)REFERENCEITEMCITATIONCITATIONTITLE.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONCITATIONTITLE")).intValue(),
					  									"REFERENCEITEMCITATIONCITATIONTITLE",
					  									(String)REFERENCEITEMCITATIONCITATIONTITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
			   
				  if(REFERENCEITEMCITATIONSOURCETITLEABBREV != null && REFERENCEITEMCITATIONSOURCETITLEABBREV.get(referenceID)!=null)
				  {
					 //System.out.println("REFERENCEITEMCITATIONSOURCETITLEABBREV="+(String)REFERENCEITEMCITATIONSOURCETITLEABBREV.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFCITATIONSOURCETITLEABBREV")).intValue(),
					  									"REFCITATIONSOURCETITLEABBREV",
					  									(String)REFERENCEITEMCITATIONSOURCETITLEABBREV.get(referenceID)));
				  }			  
				  
				  recordBuf.append(FIELDDELIM);
				  
				  if(REFERENCEITEMCITATIONCODEN != null && REFERENCEITEMCITATIONCODEN.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONCODEN="+(String)REFERENCEITEMCITATIONCODEN.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONCODEN")).intValue(),
					  									"REFERENCEITEMCITATIONCODEN",
					  									(String)REFERENCEITEMCITATIONCODEN.get(referenceID)));
				  }
				  
				  recordBuf.append(FIELDDELIM);
				  
				  if(pui != null)
				  {
					  recordBuf.append(pui.trim());
				  }
				  
				  recordBuf.append(FIELDDELIM);
				  
				  if(loadnumber!=null)
				  {
					  recordBuf.append(loadnumber);
				  }
				  
				  recordBuf.append(FIELDDELIM);
				  
				  if(updatenumber!=null)
				  {
					  recordBuf.append(loadnumber);
				  }
				  
				  recordBuf.append(FIELDDELIM);
				 
				  
				  // following fields are for CAFE Data
				  
				  if(REFERENCEITEMCITATIONEID != null && REFERENCEITEMCITATIONEID.get(referenceID)!=null)
				  {
					  //System.out.println("REFERENCEITEMCITATIONEID= "+REFERENCEITEMCITATIONEID.get(referenceID));
					  recordBuf.append((String)REFERENCEITEMCITATIONEID.get(referenceID));
				  }
				  				  
				  recordBuf.append(FIELDDELIM);
 				  
				  if(REFERENCEITEMCITATIONID != null && REFERENCEITEMCITATIONID.get(referenceID)!=null)
				  {
					  //System.out.println("CITATIONID= "+REFERENCEITEMCITATIONID.get(referenceID));
					  recordBuf.append((String)REFERENCEITEMCITATIONID.get(referenceID));
				  }
				  
				  recordBuf.append(FIELDDELIM);	
				  
				  if(REFITEMCITATIONAFFILIATION != null && REFITEMCITATIONAFFILIATION.get(referenceID)!=null)
				  {
					  //System.out.println("---REFERENCEITEMCITATIONAFFILIATION="+(String)REFITEMCITATIONAFFILIATION.get(referenceID));
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFITEMCITATIONAFFILIATION")).intValue(),
					  									"REFITEMCITATIONAFFILIATION",
					  									(String)REFITEMCITATIONAFFILIATION.get(referenceID)));
					  //System.out.println("---RECORD= "+recordBuf.toString());
				  }
				  
				  /* Source text is used for internal use only based on jira EVOPS-716
				  recordBuf.append(FIELDDELIM);	
				  
				  if(REFERENCESOURCETEXT != null && REFERENCESOURCETEXT.get(referenceID)!=null)
				  {
					  //System.out.println("---REFERENCEITEMCITATIONAFFILIATION="+(String)REFITEMCITATIONAFFILIATION.get(referenceID));
					  recordBuf.append((String)REFERENCESOURCETEXT.get(referenceID));
					  //System.out.println("---RECORD= "+recordBuf.toString());
				  }
				  */
				  
				  if(accessNumber!=null && accessNumber.length()>0)
				  {
					  referenceOut.println(recordBuf.toString().trim());
				  }
				  
				  
				  
				
				  //System.out.println("*****************************************************************************");
			 }

	   }
	}

	private String getMID()
	{
		return this.mid;
	}

	private void setMID(String mid)
	{
		this.mid = mid;
	}

	private int getRefcount()
	{
		return this.refCount;
	}

	private void setRefcount(String count)
	{
		if(count!=null && count.length()>0)
		{
			try
			{
				this.refCount = Integer.parseInt(count);
			}
			catch(Exception e)
			{
				System.out.println("REFERENCE count has wrong format. MID="+getMID()+", ACCESSIONNUMBER="+getAccessionNumber());
				this.refCount =0;
			}
		}
		else
		{
			this.refCount=0;
		}
	}

	private String getAccessionNumber()
	{
		return this.accessNumber;
	}

	private void setAccessionNumber(String accessNumber)
	{
		this.accessNumber = accessNumber;
	}
	
	private String getPui()
	{
		return this.pui;
	}

	private void setPui(String pui)
	{
		this.pui = pui;
	}
	
	private String getDOI()
	{
		return this.doi;
	}

	private void setDOI(String doi)
	{
		this.doi = doi;
	}
	
	private String getLoadnumber()
	{
		return this.loadnumber;
	}

	private void setLoadnumber(String loadnumber)
	{
		this.loadnumber = loadnumber;
	}
	
	public List getNullAccessNumberRecord()
	{
		return this.nullAccessumberRecord;
	}
	
	private String getUpdatenumber()
	{
		return this.updatenumber;
	}

	private void setUpdatenumber(String updatenumber)
	{
		this.updatenumber = updatenumber;
	}
	
	private String getDatabase()
	{
		return this.database;
	}

	private void setDatabase(String database)
	{
		this.database = database;
	}
	
	private String removeUnwantCharacter(String input)
	{
		String output=input;
		if(input!= null && input.indexOf('\t')>0)
		{
			output=perl.substitute("s/\\t/ /g", input);
			//System.out.println("***************remove tab character from record accessnumber= "+getAccessionNumber());
		}
		return output;
	}

	private String checkColumnWidth(int columnWidth,
	        						String columnName,
	        						String data) throws Exception
	{
		int cutOffPosition = 0;
		data = removeUnwantCharacter(data);
		
		if(columnWidth > 0  && data!= null)
		{
			//if(data.length()>columnWidth)
			if(data.getBytes().length>columnWidth) 
			{
				//System.out.println("1="+data);
				//System.out.println("normal length="+data.length()+" byteslength="+data.getBytes().length);
				System.out.println("Problem: "+getDatabase()+" record "+getAccessionNumber()+"'s data for column "+columnName+" is too big. data length is "+data.length());
				//added to output oversize data for later use by hmo at 5/10/2019				
				this.oversizeFieldOut.println(getAccessionNumber()+"\t"+getPui()+"\t"+columnName+"\t"+getUpdatenumber()+"\t"+getDatabase()+"\t"+this.filename+"\t"+data);
				
				//trim off data string by bytes array length
				byte[] result = new byte[columnWidth];
				System.arraycopy(data.getBytes(), 0, result, 0, columnWidth);
				data = new String(result);
				
				//data = substring(data,0,columnWidth);
				cutOffPosition = data.lastIndexOf(Constants.AUDELIMITER);
				if(cutOffPosition<data.lastIndexOf(Constants.IDDELIMITER))
				{
					cutOffPosition = data.lastIndexOf(Constants.IDDELIMITER);
				}
				if(cutOffPosition>0)
				{
					data = substring(data,0,cutOffPosition);
				}
				if(lengthCodepoints(data)>columnWidth)
				{
					data = substring(data,0,columnWidth);
				}
				//System.out.println(columnName+" DATA2_LENGTH="+data.length()+" "+data.getBytes().length);
				//System.out.println("DATA2="+data);
				//System.out.println("2="+data);

			}
		}
		this.oversizeFieldOut.flush();
		return data;
	}

	private int lengthCodepoints(String s)
	{
	  return s.codePointCount(0, s.length());
	}

	private int getRealIndex(String data,int index) 
	{		  
		return data.offsetByCodePoints(0, index);		
	}
	
	public String substring(String data, int startIndex, int endIndex)
	{
		  int codePointStartIndex = getRealIndex(data,startIndex);
		  int codePointEndIndex = getRealIndex(data,endIndex);
		  String newData = data.substring(codePointStartIndex, codePointEndIndex);
		  return newData;
	}
	
	private String substringCodepoint(String s, int startCodepoint, int numCodepoints)
	{
	  int startIndex = s.offsetByCodePoints(0, startCodepoint);
	  int endIndex = s.offsetByCodePoints(startIndex, numCodepoints);
	  return s.substring(startIndex, endIndex);
	}

	public void end()
			throws Exception
	{
		if(open)
		{
			out.close();
			referenceOut.close();
			puiOut.close();
			samePuiPuisecondaryOut.close();
			oversizeFieldOut.close();
			open = false;
		}
	}
}
