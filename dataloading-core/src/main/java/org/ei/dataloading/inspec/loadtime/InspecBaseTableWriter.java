package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import org.ei.common.Constants;
import org.ei.common.*;
import org.ei.util.*;

public class InspecBaseTableWriter
{

	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private PrintWriter numericalIndexOut;
	private PrintWriter citationOut;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	private static Properties props;
	private String[] baseTableFields;
	public final int AUS_MAXSIZE = 4000;
    public final String NOABSTRACT = "NOABSTRACT";

	public InspecBaseTableWriter(int recsPerFile,
									String filename,
									String type)
	{
		this.recsPerFile = recsPerFile;
		this.filename = filename;

		if(type.equalsIgnoreCase("XML"))
		{
			this.baseTableFields= InspecBaseTableRecord.insxmlbaseTableFields;
		}
		else
		{
			this.baseTableFields= InspecBaseTableRecord.insbaseTableFields;
		}
	}

	public InspecBaseTableWriter(String filename,String type)
	{
		this.filename = filename;

		if(type.equalsIgnoreCase("XML"))
		{
			this.baseTableFields= InspecBaseTableRecord.insxmlbaseTableFields;
		}
		else
		{
			this.baseTableFields= InspecBaseTableRecord.insbaseTableFields;
		}
	}

	public void begin() throws Exception
	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		//out = new PrintWriter(new FileWriter(filename));
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable<String,StringBuffer> record)
		throws Exception
	{

        if(record == null)
        {
        	System.out.println("Record was null");
        	return;
        }

		for(int i=0; i<baseTableFields.length; ++i)
		{
			String bf = baseTableFields[i];
			StringBuffer value = new StringBuffer();			
			
			value =  record.get(bf);
						
			String valueS = null;
			String mid = null;
			String accessnumber = null;
			String loadnumber = null;
			if(value != null)
			{
				valueS = value.toString();
				
				mid = record.get("M_ID").toString();		
				accessnumber = record.get("ANUM").toString();								
				loadnumber = record.get("LOAD_NUMBER").toString();
								
				if(valueS.length() == 0 && bf.equalsIgnoreCase("AB"))
				{
					valueS = NOABSTRACT;
				}
				valueS = valueS.replaceAll("\\t","\\s{5}");
				if(bf.equalsIgnoreCase("AUS"))
				{
                    if(valueS.length() > AUS_MAXSIZE)
                    {
                        // store the AUS_MAXSIZE+ substring in Hashtable for later processing
                        record.put("AUS2", new StringBuffer(valueS.substring(AUS_MAXSIZE)));
                        // truncate first author string to AUS_MAXSIZE - we don't care if we truncate
                        // an author name since AUS + AUS2 fields will be rejoined by concatenation
    					valueS = valueS.substring(0, AUS_MAXSIZE);
                    }
				}
				if(bf.equalsIgnoreCase("AUS2"))
				{
				    // fix last author in string to make sure it isn't a partial author name
				    valueS = fixAuthors(valueS);
                }
				
				if(bf.equalsIgnoreCase("NDI"))
				{
				    // output Numerical Index
					//System.out.println("LOADNUMBER= "+loadnumber);
				    outPutNumericalIndex(mid,accessnumber,loadnumber,valueS);
                }
				
				if(bf.equalsIgnoreCase("CIT"))
				{
				    // output inspec citation
					//System.out.println("LOADNUMBER= "+loadnumber);
				    outPutCitation(mid,accessnumber,loadnumber,valueS);
                }
				
			}
			else
			{
				if(bf.equalsIgnoreCase("AB"))
				{
					valueS = NOABSTRACT;
				}
			}

			if(i > 0)
			{
				out.print("	");
			}

			if(valueS != null)
			{
				out.print(valueS);
			}

		}
		out.println("");
		out.flush();
		if(numericalIndexOut!=null)
		{
			numericalIndexOut.flush();
		}
		if(citationOut!=null)
		{
			citationOut.flush();
		}
		++curRecNum;
	}
	
	public void outPutCitation(String mid, String accessnumber, String loadnumber, String valueS) throws Exception
	{
		if(citationOut==null)
		{
			citationOut = new PrintWriter(new FileWriter("Citation_"+this.filename+"."+filenumber+".out")); 
			System.out.println("create citation file");
		}
		String[] citationRecords = valueS.split(Constants.AUDELIMITER, -1);
		for(int i=0;i<citationRecords.length;i++)
		{
			String citationRecord = citationRecords[i];
			if(citationRecord!=null && citationRecord.length()>0)
			{
				StringBuffer outputBuffer = new StringBuffer();
				
				String[] subItems = citationRecord.split(Constants.IDDELIMITER,-1);
				//System.out.println("SIZE"+subItems.length);
				if(subItems.length>30)
				{
					outputBuffer.append("cit_"+(new GUID()).toString()+"\t");
					//outputBuffer.append(mid + "\t");
					outputBuffer.append(accessnumber + "\t");
					outputBuffer.append(loadnumber + "\t");
					
					String accessNumber = subItems[0];
					outputBuffer.append(accessNumber + "\t");
					
					String doi = subItems[1];
					if(doi!=null)
					{
						outputBuffer.append(doi);
					}
					outputBuffer.append("\t");
					
					String citationType = subItems[2];
					if(citationType!=null)
					{
						outputBuffer.append(citationType);
					}
					outputBuffer.append("\t");
					
					String citationlabel = subItems[3];
					if(citationlabel!=null)
					{
						outputBuffer.append(citationlabel);
					}
					outputBuffer.append("\t");
					
					String author = subItems[4];
					if(author!=null)
					{
						//to Make the same format with inspec author
						author=author.replace("|", Constants.IDDELIMITER);
						author=author.replace(Constants.GROUPDELIMITER, Constants.AUDELIMITER);		
						outputBuffer.append(author);
					}
					outputBuffer.append("\t");
					
					String citationYear = subItems[5];
					if(citationYear!=null)
					{
						outputBuffer.append(citationYear);
					}
					outputBuffer.append("\t");
					
					String journalTitle = subItems[6];
					if(journalTitle!=null)
					{
						outputBuffer.append(journalTitle);
					}
					outputBuffer.append("\t");
					
					String abbreviatedJournalTitle = subItems[7];
					if(abbreviatedJournalTitle!=null)
					{
						outputBuffer.append(abbreviatedJournalTitle);
					}
					outputBuffer.append("\t");
					
					String issn = subItems[8];
					if(issn!=null)
					{
						outputBuffer.append(issn);
					}
					outputBuffer.append("\t");
					
					String volumeIssue = subItems[9];
					if(volumeIssue!=null)
					{
						outputBuffer.append(volumeIssue);
					}
					outputBuffer.append("\t");
					
					String publicationDate = subItems[10];
					if(publicationDate!=null)
					{
						outputBuffer.append(publicationDate);
					}
					outputBuffer.append("\t");
					
					String firstPage = subItems[11];
					if(firstPage!=null)
					{
						outputBuffer.append(firstPage);
					}
					outputBuffer.append("\t");
					
					String page = subItems[12];
					if(page!=null)
					{
						outputBuffer.append(page);
					}
					outputBuffer.append("\t");
					
					String rawText = subItems[13];
					if(rawText!=null)
					{
						outputBuffer.append(rawText);
					}
					outputBuffer.append("\t");
					
					/*
					String collaboration = subItems[14];
					if(collaboration!=null)
					{
						outputBuffer.append(collaboration);
					}
					outputBuffer.append("\t");
					
					String etAl = subItems[15];
					if(etAl!=null)
					{
						outputBuffer.append(etAl);
					}
					outputBuffer.append("\t");
					*/
					
					String title = subItems[14];
					if(title!=null)
					{
						outputBuffer.append(title);
					}
					outputBuffer.append("\t");
					
					String publicationTitle = subItems[15];
					if(publicationTitle!=null)
					{
						outputBuffer.append(publicationTitle);
					}
					outputBuffer.append("\t");
					
					String abbreaviatedJournalTitle = subItems[16];					       
					if(abbreaviatedJournalTitle!=null)
					{
						outputBuffer.append(abbreaviatedJournalTitle);
					}
					outputBuffer.append("\t");
					
					String publisher = subItems[17];
					if(publisher!=null)
					{
						outputBuffer.append(publisher);
					}
					outputBuffer.append("\t");
					
					String seriesTitle = subItems[18];
					if(seriesTitle!=null)
					{
						outputBuffer.append(seriesTitle);
					}
					outputBuffer.append("\t");
					
					String editor = subItems[19];
					if(editor!=null)
					{
						editor=editor.replace("|", Constants.IDDELIMITER);
						editor=editor.replace(Constants.GROUPDELIMITER, Constants.AUDELIMITER);	
						outputBuffer.append(editor);
					}
					outputBuffer.append("\t");
					
					String issuingOrganisation = subItems[20];
					if(issuingOrganisation!=null)
					{
						outputBuffer.append(issuingOrganisation);
					}
					outputBuffer.append("\t");
					
					String publicationPlace = subItems[21];	
					if(publicationPlace!=null)
					{
						outputBuffer.append(publicationPlace);
					}
					outputBuffer.append("\t");
					
					String country = subItems[22];
					if(country!=null)
					{
						outputBuffer.append(country);
					}
					outputBuffer.append("\t");
					
					String isbn = subItems[23];
					if(isbn!=null)
					{
						outputBuffer.append(isbn);
					}
					outputBuffer.append("\t");
					
					String conference = subItems[24];
					if(conference!=null)
					{
						outputBuffer.append(conference);
					}
					outputBuffer.append("\t");
					
					String reportNumber = subItems[25];
					if(reportNumber!=null)
					{
						outputBuffer.append(reportNumber);
					}
					outputBuffer.append("\t");
					
					String standardNumber = subItems[26];
					if(standardNumber!=null)
					{
						outputBuffer.append(standardNumber);
					}
					outputBuffer.append("\t");
					
					String patentDetail = subItems[27];
					if(patentDetail!=null)
					{
						outputBuffer.append(patentDetail);
					}
					outputBuffer.append("\t");									
					
					String date = subItems[28];
					if(date!=null)
					{
						outputBuffer.append(date);
					}
					outputBuffer.append("\t");
					
					String link = subItems[29];
					if(link!=null)
					{
						outputBuffer.append(link);
					}
					outputBuffer.append("\t");
					
					String notes = subItems[30];
					if(notes!=null)
					{
						outputBuffer.append(notes);
					}
					outputBuffer.append("\n");
									
					citationOut.write(outputBuffer.toString());
					
				}
				else
				{
					System.out.println("record "+mid+" in wrong numerical format SIZE="+subItems.length+" String="+valueS);
				}
				
			}
			else
			{
				System.out.println("PROBLEM in 1");
			}
			
		}
		citationOut.flush();
		
	}
	
	public void outPutNumericalIndex(String mid, String accessnumber, String loadnumber, String valueS) throws Exception
	{
		if(numericalIndexOut==null)
		{
			numericalIndexOut = new PrintWriter(new FileWriter("Numerical_"+this.filename+"."+filenumber+".out")); 
			System.out.println("create data file");
		}
		String[] numericalIndexRecords = valueS.split(Constants.AUDELIMITER);
		for(int i=0;i<numericalIndexRecords.length;i++)
		{
			String numericalIndexRecord = numericalIndexRecords[i];
			if(numericalIndexRecord!=null && numericalIndexRecord.length()>0)
			{
				String[] subItems = numericalIndexRecord.split(Constants.IDDELIMITER);
				if(subItems.length>2)
				{
					String quan = subItems[0];
					String value1 = subItems[1];
					String value2 = null;
					String unit = null;
					if(subItems.length>3)
					{
						value2 = subItems[2];
						unit = subItems[3];
					}
					else
					{
						value2 = subItems[1];
						unit = subItems[2];
					}
					
					if(value2==null || value2.length()==0)
					{
						value2 = value1;
					}
					else if(value1==null || value1.length()==0)
					{
						value1 = value2;	
					}
					
					if(isNumeric(value1) && isNumeric(value2))
					{
						if(Double.parseDouble(value1)>Double.parseDouble(value2))
						{
							String temp = value2;
							value2 = value1;
							value1 = temp;
						}
					}
					else
					{
						System.out.println("either " +value1+ " or "+value2 +" is not numerical value");
					}
					
					numericalIndexOut.write(mid+"\t"+accessnumber+"\t"+quan+"\t"+value1+"\t"+value2+"\t"+unit+"\t"+loadnumber+"\n");
					
				}
				else
				{
					System.out.println("record "+mid+" in wrong numerical format SIZE="+subItems.length+" String="+numericalIndexRecord);
				}
				
			}
			else
			{
				System.out.println("PROBLEM in 1");
			}
			
		}
		numericalIndexOut.flush();
		
	}
	
	public boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
		System.out.println("problem on "+str);
		nfe.printStackTrace();  
	    return false;  
	  }  
	  return true;  
	}

	private String fixAuthors(String authors)
	{
		if(authors != null && authors.length() > AUS_MAXSIZE)
		{
			authors = authors.substring(0,AUS_MAXSIZE-1);
			int i = authors.lastIndexOf(Constants.AUDELIMITER);
            if(i != -1)
            {
			    authors = authors.substring(0,i);
            }
		}
		return authors;
	}


	public void end() throws Exception
	{
		if(open)
		{
			out.close();
			open = false;
		}
		
		if(numericalIndexOut!=null)
		{
			numericalIndexOut.close();
		}
	}
}
