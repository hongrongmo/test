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

	private boolean open = false;

	private LinkedHashSet bdColumns = BaseTableRecord.getBdColumns();
	private Hashtable bdColumnsSize = new Hashtable();
	public static final char FIELDDELIM = '\t';
	private String accessNumber;
	private String mid;
	private int refCount;

	public BaseTableWriter(String filename)
	{
		this.filename = filename;
	}

	public void begin()
			throws Exception
	{

		out = new PrintWriter(new FileWriter(filename));
		referenceOut = new PrintWriter(new FileWriter("Reference_"+filename));
		System.out.println("Output Filename "+filename);
		System.out.println("Reference Output Filename Reference_"+filename);
		open = true;
	}

	public void writeRec(Hashtable record)
			throws Exception
	{

		StringBuffer recordBuf = new StringBuffer();
		Iterator bdData = bdColumns.iterator();
		boolean  isReference = false;
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

							if(thisColumnName.equals("M_ID"))
							{
								setMID(valueString);
							}

							if(thisColumnName.equals("REFCOUNT"))
							{
								setRefcount(valueString);
							}

						}
						else
						{
							isReference = true;
							Hashtable reference =  (Hashtable)record.get("REFERENCE");
							outputReference(reference,getAccessionNumber(),getMID(),getRefcount());
						}
					}
					catch(Exception e)
					{
						System.out.println("Access Number= "+getAccessionNumber()+" COLUMN NAME= "+thisColumnName);
						e.printStackTrace();
					}

			}




			if(valueString != null)
			{
				recordBuf.append(valueString);
				//recordBuf.append(thisColumnName+":"+valueString);
			}
			if(thisColumnName.length()<9 || !("REF".equals(thisColumnName.substring(0,3))))
			{
				recordBuf.append(FIELDDELIM);
			}

		}
		if(isReference)
		{
		   recordBuf.append("YES");
	    }
	    recordBuf.append(FIELDDELIM);

		out.println(recordBuf.toString().trim());


	}

	private void outputReference(Hashtable reference,String accessNumber,String mid, int refcount) throws Exception
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
			 Hashtable REFERENCEITEMCITATIONPII = (Hashtable)reference.get("REFERENCEITEMCITATIONPII");
			 Hashtable REFERENCEITEMCITATIONDOI = (Hashtable)reference.get("REFERENCEITEMCITATIONDOI");
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
			 Hashtable REFERENCEITEMCITATIONSOURCETITLEABBREV =(Hashtable)reference.get("REFERENCEITEMCITATIONSOURCETITLEABBREV");
			 Hashtable REFERENCEITEMCITATIONCODEN = (Hashtable)reference.get("REFERENCEITEMCITATIONCODEN");

			 StringBuffer recordBuf = null;
			 String referenceID = "0";
			 int size = 0;

			 for(int i=0;i<	refcount;i++)
			 {
				  recordBuf = new StringBuffer();
				  referenceID = Integer.toString(i+1);
				  recordBuf.append(mid);
				  recordBuf.append(FIELDDELIM);
				  if(accessNumber!=null)
				  {
				  	recordBuf.append(accessNumber);
				  }
				  recordBuf.append(FIELDDELIM);
				  recordBuf.append(referenceID);
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCETITLE!=null && REFERENCETITLE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCETITLE")).intValue(),
					  									"REFERENCETITLE",
					  									(String)REFERENCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEAUTHOR != null && REFERENCEAUTHOR.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEAUTHOR")).intValue(),
					  									"REFERENCEAUTHOR",
					  									(String)REFERENCEAUTHOR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCESOURCETITLE != null && REFERENCESOURCETITLE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCESOURCETITLE")).intValue(),
														"REFERENCESOURCETITLE",
														(String)REFERENCESOURCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEPUBLICATIONYEAR != null && REFERENCEPUBLICATIONYEAR.get(referenceID)!=null)
				  {
					  String value =  (String)REFERENCEPUBLICATIONYEAR.get(referenceID);
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEPUBLICATIONYEAR")).intValue(),
					  									"REFERENCEPUBLICATIONYEAR",
					  									(String)REFERENCEPUBLICATIONYEAR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEVOLUME !=null && REFERENCEVOLUME.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEVOLUME")).intValue(),
					  									"REFERENCEVOLUME",
					  									(String)REFERENCEVOLUME.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEISSUE !=null && REFERENCEISSUE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEISSUE")).intValue(),
					  									"REFERENCEISSUE",
					  									(String)REFERENCEISSUE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEPAGES != null && REFERENCEPAGES.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEPAGES")).intValue(),
					  									"REFERENCEPAGES",
					  									(String)REFERENCEPAGES.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEFULLTEXT != null && REFERENCEFULLTEXT.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEFULLTEXT")).intValue(),
					  									"REFERENCEFULLTEXT",
					  									(String)REFERENCEFULLTEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCETEXT != null && REFERENCETEXT.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCETEXT")).intValue(),
					  									"REFERENCETEXT",
					  									(String)REFERENCETEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEWEBSITE != null && REFERENCEWEBSITE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEWEBSITE")).intValue(),
					  									"REFERENCEWEBSITE",
					  									(String)REFERENCEWEBSITE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMID != null && REFERENCEITEMID.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMID")).intValue(),
					  									"REFERENCEITEMID",
					  									(String)REFERENCEITEMID.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONPII != null && REFERENCEITEMCITATIONPII.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPII")).intValue(),
					  									"REFERENCEITEMCITATIONPII",
					  									(String)REFERENCEITEMCITATIONPII.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONDOI != null && REFERENCEITEMCITATIONDOI.get(referenceID)!=null)
				  {
					 recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONDOI")).intValue(),
					 									"REFERENCEITEMCITATIONDOI",
					 									(String)REFERENCEITEMCITATIONDOI.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONAUTHOR != null && REFERENCEITEMCITATIONAUTHOR.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONAUTHOR")).intValue(),
					  									"REFERENCEITEMCITATIONAUTHOR",
					  									(String)REFERENCEITEMCITATIONAUTHOR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFITEMCITATIONSOURCETITLE != null && REFITEMCITATIONSOURCETITLE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFITEMCITATIONSOURCETITLE")).intValue(),
					  									"REFITEMCITATIONSOURCETITLE",
					  									(String)REFITEMCITATIONSOURCETITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);

				  if(REFERENCEITEMCITATIONISSN != null && REFERENCEITEMCITATIONISSN.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISSN")).intValue(),
					  									"REFERENCEITEMCITATIONISSN",
					  									(String)REFERENCEITEMCITATIONISSN.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONISBN !=null && REFERENCEITEMCITATIONISBN.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISBN")).intValue(),
					  									"REFERENCEITEMCITATIONISBN",
					  									(String)REFERENCEITEMCITATIONISBN.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONPART != null && REFERENCEITEMCITATIONPART.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPART")).intValue(),
					  									"REFERENCEITEMCITATIONPART",
					  									(String)REFERENCEITEMCITATIONPART.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFITEMCITATIONPUBLICATIONYEAR != null && REFITEMCITATIONPUBLICATIONYEAR.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFITEMCITATIONPUBLICATIONYEAR")).intValue(),
					  									"REFITEMCITATIONPUBLICATIONYEAR",
					  									(String)REFITEMCITATIONPUBLICATIONYEAR.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONVOLUME != null && REFERENCEITEMCITATIONVOLUME.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONVOLUME")).intValue(),
					  									"REFERENCEITEMCITATIONVOLUME",
					  									(String)REFERENCEITEMCITATIONVOLUME.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONISSUE != null && REFERENCEITEMCITATIONISSUE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONISSUE")).intValue(),
					  									"REFERENCEITEMCITATIONISSUE",
					  									(String)REFERENCEITEMCITATIONISSUE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONPAGE != null && REFERENCEITEMCITATIONPAGE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONPAGE")).intValue(),
					  									"REFERENCEITEMCITATIONPAGE",
					  									(String)REFERENCEITEMCITATIONPAGE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFITEMCITATIONARTICLENUMBER != null && REFITEMCITATIONARTICLENUMBER.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFITEMCITATIONARTICLENUMBER")).intValue(),
					  									"REFITEMCITATIONARTICLENUMBER",
					  									(String)REFITEMCITATIONARTICLENUMBER.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONWEBSITE != null && REFERENCEITEMCITATIONWEBSITE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONWEBSITE")).intValue(),
					  									"REFERENCEITEMCITATIONWEBSITE",
					  									(String)REFERENCEITEMCITATIONWEBSITE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONEADDRESS != null && REFERENCEITEMCITATIONEADDRESS.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONEADDRESS")).intValue(),
					  									"REFERENCEITEMCITATIONEADDRESS",
					  									(String)REFERENCEITEMCITATIONEADDRESS.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONREFTEXT != null && REFERENCEITEMCITATIONREFTEXT.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONREFTEXT")).intValue(),
					  									"REFERENCEITEMCITATIONREFTEXT",
					  									(String)REFERENCEITEMCITATIONREFTEXT.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);


				  if(REFERENCEITEMCITATIONCITATIONTITLE != null && REFERENCEITEMCITATIONCITATIONTITLE.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONCITATIONTITLE")).intValue(),
					  									"REFERENCEITEMCITATIONCITATIONTITLE",
					  									(String)REFERENCEITEMCITATIONCITATIONTITLE.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONSOURCETITLEABBREV != null && REFERENCEITEMCITATIONSOURCETITLEABBREV.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONSOURCETITLEABBREV")).intValue(),
					  									"REFERENCEITEMCITATIONSOURCETITLEABBREV",
					  									(String)REFERENCEITEMCITATIONSOURCETITLEABBREV.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  if(REFERENCEITEMCITATIONCODEN != null && REFERENCEITEMCITATIONCODEN.get(referenceID)!=null)
				  {
					  recordBuf.append(checkColumnWidth(((Integer)bdColumnsSize.get("REFERENCEITEMCITATIONCODEN")).intValue(),
					  									"REFERENCEITEMCITATIONCODEN",
					  									(String)REFERENCEITEMCITATIONCODEN.get(referenceID)));
				  }
				  recordBuf.append(FIELDDELIM);
				  referenceOut.println(recordBuf.toString().trim());

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
				cutOffPosition = data.lastIndexOf(Constants.AUDELIMITER);
				if(cutOffPosition<data.lastIndexOf(Constants.IDDELIMITER))
				{
					cutOffPosition = data.lastIndexOf(Constants.IDDELIMITER);
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
			referenceOut.close();
			open = false;
		}
	}
}