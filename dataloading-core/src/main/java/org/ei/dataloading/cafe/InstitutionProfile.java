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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;

import org.ei.common.Constants;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.util.GUID;
import org.jdom2.*;                  //// replace svn jdom with recent jdom2
import org.jdom2.input.*;
import org.jdom2.output.*;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class InstitutionProfile
{
	private int loadNumber;
	private String databaseName;
	private String action;
	private Namespace xoeNamespace=Namespace.getNamespace("xoe","http://www.elsevier.com/xml/xoe/dtd");
	private Namespace xocsNamespace=Namespace.getNamespace("xocs","http://www.elsevier.com/xml/xocs/dtd");
	private Namespace noNamespace=Namespace.getNamespace("","");
	private static Connection con;
	private static String infile;
	private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	private static String driver = "oracle.jdbc.driver.OracleDriver";
	private static String username = "ap_correction1";
	private static String password = "ei3it";
	private SAXBuilder builder;
	public static final char FIELDDELIM = '\t';
	private DataLoadDictionary dictionary = new DataLoadDictionary();
	
			
	public static void main(String args[]) throws Exception
	{
		long startTime = System.currentTimeMillis();
		if(args.length<2)
		{
		    System.out.println("not enough parameter, required two parameters");
		    System.out.println("Usage:");
		    System.out.println("org.ei.dataloading.authoraffiliationprofile.InstitutionProfile filename loadnumber");
		    System.exit(1);
		}
			
		infile = args[0];
		int loadN = Integer.parseInt(args[1]);
		//FileWriter outFile = null;
		BufferedWriter outFile = null;
		try
        {         
			InstitutionProfile c = new InstitutionProfile();  
			//outFile = new FileWriter(infile+".out");
			outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(infile+".out"),"UTF-8"));
			c.loadNumber = loadN;
            c.readFile(infile,outFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        { 
        	if(outFile != null)
 		    {
        		outFile.close();
 		    }
            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        }
	}
	
	public InstitutionProfile()
	{
		builder = new SAXBuilder();
		builder.setExpandEntities(false);
	}
	
	public InstitutionProfile(int loadnumber)
	{
		builder = new SAXBuilder();
		builder.setExpandEntities(false);
		this.loadNumber = loadnumber;
	}
	
	void readFile(String filename, BufferedWriter out) throws Exception
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
		            writeRecs(in,out);
		            i++;
		        }
		    }
		    else if(filename.toLowerCase().endsWith(".xml"))
		    {
		        System.out.println("IS XML FILE");
		        in = new BufferedReader(new FileReader(filename));
		        writeRecs(in,out);
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
		    		   
			if (out != null)
			{
			    try
			    {
			        out.close();
			    }
			    catch (Exception e)
			    {
			        e.printStackTrace();
			    }
			}	        		   		    
		}
			    
	}
	
	private void writeRecs(BufferedReader xmlReader, BufferedWriter out) throws Exception
    {     
        try
        {
	
            if (xmlReader!=null)
            {           	
                Hashtable h = parser(xmlReader);
            
                if (h != null)
                {                  
                	writeRec(h,out);                   
                }
               
            }
            out.flush();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            throw new Exception(e);
        }       
    }
	
	private Hashtable parser(Reader r)throws Exception
	{
		
		Hashtable record = new Hashtable();
		if(r != null)
		{
			
			Document doc = builder.build(r);
			Element cpxRoot = doc.getRootElement();
			Element docElement = cpxRoot.getChild("doc",xocsNamespace);
			if(docElement!=null)
			{
				String mid = "aff_"+(new GUID()).toString();
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
				}
				
				Element xocsInstitutionProfile = docElement.getChild("institution-profile",xocsNamespace);
				if(xocsInstitutionProfile != null)
				{
					Element institutionProfile = xocsInstitutionProfile.getChild("institution-profile",noNamespace);
					if(institutionProfile != null)
					{
						String affiliationID = institutionProfile.getAttributeValue("affiliation-id",noNamespace);
						record.put("AFFID",affiliationID);
						//System.out.println("affiliationID= "+affiliationID);
						String parentID = institutionProfile.getAttributeValue("parent",noNamespace);
						//System.out.println("parentID="+parentID);
						if(parentID!=null)
						{
							record.put("PARENTID",parentID);
						}
						
						if(institutionProfile.getChild("status",noNamespace)!=null)
						{
							String status = institutionProfile.getChildText("status",noNamespace);
							record.put("STATUS", status);
							//System.out.println("status= "+status);
						}
						
						if(institutionProfile.getChild("date-created",noNamespace)!=null)
						{
							Element dateCreated = institutionProfile.getChild("date-created",noNamespace);
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
						
						if(institutionProfile.getChild("date-revised",noNamespace)!=null)
						{
							Element dateRevised = institutionProfile.getChild("date-revised",noNamespace);
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
						
						if(institutionProfile.getChild("preferred-name",noNamespace)!=null)
						{
							String preferredName = institutionProfile.getChildText("preferred-name",noNamespace);
							record.put("PREFEREDNAME", dictionary.mapEntity(preferredName));
							//record.put("PREFEREDNAME", preferredName);
							//System.out.println("preferred-name= "+preferredName);
							//System.out.println("preferred-name= "+dictionary.mapEntity(preferredName));
						}
						
						if(institutionProfile.getChild("parent-preferred-name",noNamespace)!=null)
						{
							String parentPreferredName = institutionProfile.getChildText("parent-preferred-name",noNamespace);
							record.put("PARENTPREFEREDNAME", dictionary.mapEntity(parentPreferredName));						
						}
						
						
						if(institutionProfile.getChild("sort-name",noNamespace)!=null)
						{
							String sortName = institutionProfile.getChildText("sort-name",noNamespace);
							record.put("SORTNAME", dictionary.mapEntity(sortName));
							//System.out.println("sort-name= "+sortName);
						}
						
						if(institutionProfile.getChild("name-variant",noNamespace)!=null)
						{
							String nameVariant = institutionProfile.getChildText("name-variant",noNamespace);
							record.put("NAMEVARIANT", dictionary.mapEntity(nameVariant));
							//System.out.println("name-variant= "+nameVariant);
						}
						
						if(institutionProfile.getChild("address",noNamespace)!=null)
						{
							Element address = institutionProfile.getChild("address",noNamespace);
							String countryAtt = address.getAttributeValue("country",noNamespace);
							//System.out.println("countryAtt= "+countryAtt);
							String country = address.getChildText("country");
							//System.out.println("country= "+country);
							String addressPart = address.getChildText("address-part",noNamespace);
							//System.out.println("addressPart= "+addressPart);
							
							String city = address.getChildText("city",noNamespace);		
							//System.out.println("city= "+city);
							String state = address.getChildText("state",noNamespace);		
							//System.out.println("state= "+state);
							String postalCode = address.getChildText("postal-code",noNamespace);
							//System.out.println("postalCode= "+postalCode);
							if(addressPart!=null)
							{
								record.put("ADDRESSPART", dictionary.mapEntity(addressPart));
							}
							
							if(city!=null)
							{
								record.put("CITY", dictionary.mapEntity(city));
							}
							
							if(state!=null)
							{
								record.put("STATE", dictionary.mapEntity(state));
							}
							
							if(postalCode!=null)
							{
								record.put("POSTALCODE", dictionary.mapEntity(postalCode));
							}
							
							if(country!=null)
							{
								record.put("COUNTRY", dictionary.mapEntity(country));
							}
							else if(countryAtt!=null)
							{
								record.put("COUNTRY", dictionary.mapEntity(countryAtt));
							}
							
								
							
						}
						
						if(institutionProfile.getChild("certainty-scores",noNamespace)!=null)
						{
							StringBuffer certaintyScoresBuffer = new StringBuffer();
							Element certaintyScores = institutionProfile.getChild("certainty-scores",noNamespace);							
							List certaintyScoreList = certaintyScores.getChildren("certainty-score",noNamespace);
							for (int g = 0; g< certaintyScoreList.size();g++)
							{
								Element certaintyScore =(Element) certaintyScoreList.get(g);
								
								if(certaintyScore.getChild("org-id") != null )
								{
								    String orgId = certaintyScore.getChildText("org-id");
								    certaintyScoresBuffer.append(orgId);
								    
								}
								certaintyScoresBuffer.append(Constants.IDDELIMITER);
								if(certaintyScore.getChild("score") != null )
								{
								    String score = certaintyScore.getChildText("score");
								    certaintyScoresBuffer.append(score);
								   
								}
								certaintyScoresBuffer.append(Constants.AUDELIMITER);
							}
							record.put("CERTAINTYSCORES",certaintyScoresBuffer.toString());
						
						}	
						
						if(institutionProfile.getChild("quality",noNamespace)!=null)
						{
							String quality = institutionProfile.getChildText("quality",noNamespace);
							record.put("QUALITY", quality);
						}
						
					}
					else
					{
						System.out.println("institutionProfile is null");
					}
					
				}
				else
				{
					System.out.println("xocsInstitutionProfile is null");
				}
				
			}
			
			//System.out.println("ROOTELEMENT "+cpxRoot.getName());
		}
		return record;
		
	}
	
	public void writeRec(Hashtable record, BufferedWriter out) throws Exception
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
			
			if(record.get("AFFID")!=null)
			{
				recordBuf.append((String)record.get("AFFID"));
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
			
			if(record.get("PREFEREDNAME")!=null)
			{
				recordBuf.append((String)record.get("PREFEREDNAME"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("SORTNAME")!=null)
			{
				recordBuf.append((String)record.get("SORTNAME"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("NAMEVARIANT")!=null)
			{
				recordBuf.append((String)record.get("NAMEVARIANT"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("ADDRESSPART")!=null)
			{
				recordBuf.append((String)record.get("ADDRESSPART"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CITY")!=null)
			{
				recordBuf.append((String)record.get("CITY"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("STATE")!=null)
			{
				recordBuf.append((String)record.get("STATE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("POSTALCODE")!=null)
			{
				recordBuf.append((String)record.get("POSTALCODE"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("COUNTRY")!=null)
			{
				recordBuf.append((String)record.get("COUNTRY"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("CERTAINTYSCORES")!=null)
			{
				recordBuf.append((String)record.get("CERTAINTYSCORES"));
			}
			recordBuf.append(FIELDDELIM);
			
			if(record.get("QUALITY")!=null)
			{
				recordBuf.append((String)record.get("QUALITY"));				
			}
			recordBuf.append(FIELDDELIM);
			
			recordBuf.append(loadNumber);
			
			recordBuf.append(FIELDDELIM);
			
			if(record.get("PARENTID")!=null)
			{
				recordBuf.append((String)record.get("PARENTID"));				
			}
			
			recordBuf.append(FIELDDELIM);
			
			if(record.get("PARENTPREFEREDNAME")!=null)
			{
				recordBuf.append((String)record.get("PARENTPREFEREDNAME"));				
			}
		}
		out.write(recordBuf.toString()+"\n");
	}
}