package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import org.ei.common.Constants;

public class InspecBaseTableWriter
{

	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private PrintWriter numericalIndexOut;
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
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable record)
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
			//System.out.println("bf= "+bf);
			StringBuffer value = (StringBuffer) record.get(bf);
			//System.out.println("Value= "+value);
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
				    outPutNumericalIndex(mid,accessnumber,loadnumber,valueS);
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
		++curRecNum;
	}
	
	public void outPutNumericalIndex(String mid, String accessnumber, String loadnumber, String valueS) throws Exception
	{
		if(numericalIndexOut==null)
		{
			numericalIndexOut = new PrintWriter(new FileWriter("Numerical_"+this.filename+".out")); 
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
