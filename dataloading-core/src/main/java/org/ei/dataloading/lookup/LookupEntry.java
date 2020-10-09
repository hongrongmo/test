package org.ei.dataloading.lookup;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.common.Constants;
import org.ei.common.bd.BdAffiliations;
import org.ei.common.bd.BdCorrespAffiliations;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.bd.loadtime.XmlCombiner;
import org.ei.xml.Entity;

/**
 * 
 * @author TELEBH
 * @Date: 09/01/2020
 * @Description: Create Lookups that works for ES 
 */
public class LookupEntry {

	private String database;
	private int updatenumber;
	String url = "jdbc:oracle:thin:@localhost:1521:eid"; // for localhost
	String driver = "oracle.jdbc.driver.OracleDriver";
    String username = "ap_correction1";
    String password = null;
     
     
	private String pui;
    private String accessnumber;
    private CombinedXMLWriter writer;
    
	private Connection con;
	
	private PrintWriter affiliationPW = null;
    private PrintWriter authorPW = null;
    private PrintWriter controltermsPW = null;
    private PrintWriter serialtitlePW = null;
    private PrintWriter publishernamePW = null;
    private PrintWriter patentcountryPW = null;
    private PrintWriter ipcPW = null;
    
    private HashMap<String, PrintWriter> lookupMap = new HashMap<>();
    
	public static void main(String[] args)
	{
		if(args.length > 2)
		{
			new LookupEntry().run(args);
		}
		else
		{
			System.out.println("Not enough parameters!");
			System.exit(1);
			
		}
	}
	
	public LookupEntry() {}
	public LookupEntry(String database,  int updatenumber)
	{
		this.database = database;
		this.updatenumber = updatenumber;
	}
	public LookupEntry(String database,  int updatenumber, Connection con)
	{
		this.database = database;
		this.updatenumber = updatenumber;
		this.con = con;
	}
	public void run(String[] args)
	{

		if(args[0] != null)
		{
			database = args[0];
			System.out.println("Database: " + database);
		}
		if(args[1] != null)
		{
			if(!args[1].isEmpty() && args[1].matches("\\d+"))
				updatenumber = Integer.parseInt(args[1]);
			else
			{
				System.out.println("Invalid updatenumber value!");
				System.exit(1);
				
			}
			System.out.println("updatenumber: " + updatenumber);
		}
		if(args.length > 2)
		{
			if(args[2] != null)
			{
				url = args[2];
				System.out.println("url: " + url);
			}
			if(args[3] != null)
			{
				driver = args[3];
				System.out.println("driver: " + driver);
			}
			if(args[4] != null)
			{
				username = args[4];
				System.out.println("userName: " + username);
			}
			if(args[5] != null)
				password = args[5];
				
		}
		else
		{
			System.out.println("Not Enough parameters!!!");
			System.exit(1);
		}
		
			new LookupEntry(database, updatenumber);
			start();

		
	}
    public void init()
    {
    	writer = new CombinedXMLWriter(50000,updatenumber,database);
        try
        {
            File file=new File("ei");
            if(!file.exists())
            {
                file.mkdir();
            }
            file=new File("ei/index_af");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_affiliation-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            	affiliationPW = new PrintWriter(new FileWriter(file, true));
                lookupMap.put("AUTHORAFFILIATION", affiliationPW);

            file=new File("ei/index_au");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_author-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            authorPW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("AUTHOR", authorPW);
                
            file=new File("ei/index_cv");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_controlterms-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            controltermsPW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("CONTROLLEDTERMS", controltermsPW);

            file=new File("ei/index_st");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_serialtitle-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            serialtitlePW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("SERIALTITLE", serialtitlePW);

            file=new File("ei/index_pn");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_publishername-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            publishernamePW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("PUBLISHERNAME", publishernamePW);

            file=new File("ei/index_pc");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_patentcountry-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            patentcountryPW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("AUTHORITYCODE", patentcountryPW);

            file=new File("ei/index_ipc");
            if(!file.exists())
            {
                file.mkdir();
            }
            file = new File(file.getPath() + "/es_ipc-" + this.updatenumber + "." + this.database);
            if(file.exists())
            	file.delete();
            ipcPW = new PrintWriter(new FileWriter(file, true));
            lookupMap.put("INTERNATONALPATENTCLASSIFICATION", ipcPW);

        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    
    public void start()
    {
		try {

			if (database != null && !(database.isEmpty())
					&& (database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("chm")
							|| database.equalsIgnoreCase("geo") || database.equalsIgnoreCase("pch")
							|| database.equalsIgnoreCase("elt"))) {
				XmlCombiner c = new XmlCombiner(writer);

				c.writeLookupByWeekNumber(updatenumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    public void flush()
    {
    	
    	if(affiliationPW != null)
        {
            try
            {
            	 affiliationPW.close();
                 affiliationPW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    	
    	if(authorPW != null)
        {
            try
            {
            	authorPW.close();
                authorPW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    	if(controltermsPW != null)
        {
            try
            {
            	controltermsPW.close();
                controltermsPW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    	if(serialtitlePW != null)
        {
            try
            {
            	 serialtitlePW.close();
                 serialtitlePW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    	if(publishernamePW != null)
        {
            try
            {
            	publishernamePW.close();
                publishernamePW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } 
    	if(patentcountryPW != null)
        {
            try
            {
            	patentcountryPW.close();
                patentcountryPW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } 
    	if(ipcPW != null)
        {
            try
            {
            	ipcPW.close();
                ipcPW = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } 
    }
    public void setDatabase(String database)
    {
    	this.database = database;
    }
    public String getDatabase()
    {
    	return this.database;
    }
    public void setAccessnumber(String accessnumber)
    {
    	this.accessnumber = accessnumber;
    }
    public void setPui(String pui)
    {
    	this.pui = pui;
    }
    public String getPui()
    {
    	return this.pui;
    }
    public String getAccessnumber()
    {
    	return this.accessnumber;
    }
    
 /*HT added 09/21/2020 to write Lookup in same format as extracted to ES for populating our DB lookup tables*/
    
    public void writeLookupRecs(EVCombinedRec[] recs)
    {
    	int counter=0;
    	if(recs.length >0)
    	{
    		for(EVCombinedRec rec: recs)
    		{
    			prepareLookupRec(rec);
    			counter++;
    		}
    	}
    	
    	//System.out.println("Total # of lookups written: " + counter);
    }
    
    public void writeLookupRec(EVCombinedRec rec)
    {
    	prepareLookupRec(rec);
    }
    
    public void prepareLookupRec(EVCombinedRec rec)
    {
    	setDatabase(rec.getString(EVCombinedRec.DATABASE));
        setPui(rec.getString(EVCombinedRec.PUI));
        setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
        String loadnumber = rec.getString(EVCombinedRec.LOAD_NUMBER);
        if(loadnumber !=null && loadnumber.length()>6)
        {
        	loadnumber = loadnumber.substring(0,6);
        }
        
        //Author
        String[] author=writer.removeExtraSpace(writer.removeSpace(writer.reverseSigns(rec.getStrings(EVCombinedRec.AUTHOR))));
        if(author!=null && author.length>0 )
        {
        	writeLookupRec(author,"AUTHOR");
        }
        
        //Affiliation
        String[] authorAffiliation=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION));
        if(authorAffiliation!=null && authorAffiliation.length>0 )
        {
        	writeLookupRec(authorAffiliation,"AUTHORAFFILIATION");
        }
        else
        {
        	String[] affiliationLocations=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION));
        	writeLookupRec(affiliationLocations,"AUTHORAFFILIATION");
        }
        
        //ControlledTerms
        
        String[] controlledTerms=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS));
        if(controlledTerms!=null && controlledTerms.length>0 )
        {
        	writeLookupRec(controlledTerms,"CONTROLLEDTERMS");  
        }
        else
        {
        	//ChemicalTerms
            String[] chemicalTerms=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.CHEMICALTERMS));
            if(chemicalTerms!=null && chemicalTerms.length>0  )
            {
            	writeLookupRec(chemicalTerms,"CONTROLLEDTERMS"); 
            }
        }
        
        //SerialTitle
        String[] serialTitle=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.SERIAL_TITLE));
        if(serialTitle!=null && serialTitle.length>0 )
        {
        	writeLookupRec(serialTitle,"SERIALTITLE");
        }
        
        //PublisherName
        String[] publisherName=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.PUBLISHER_NAME));
        if(publisherName!=null && publisherName.length>0)
        {
        	writeLookupRec(publisherName,"PUBLISHERNAME");
        }
        //IPC Code
        String[] intPatentClassification;
		try 
		{
			//intPatentClassification = writer.reverseSigns(writer.addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),lookupMap.get("INTERNATONALPATENTCLASSIFICATION"), getDatabase()));
			// HT changed to match ES extraction 10/09/2020
			intPatentClassification = writer.reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION));
			
			if(intPatentClassification!=null && intPatentClassification.length>0 )
	        {
	        	writeLookupRec(intPatentClassification,"INTERNATONALPATENTCLASSIFICATION");
	        }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
        
        
    }
    
    public void writeLookupRec(String values[], String lookupKey)
    {
        try
        {
            PrintWriter indexWriter = (PrintWriter)lookupMap.get(lookupKey);
            if(values!=null)
            {
                for(int i=0; i<values.length; i++)
                {
                	String value = values[i];
                	String aid = " ";
                	String lookupValue = " ";
      
                	if(value!=null && value.indexOf(Constants.GROUPDELIMITER)>-1)
                	{
                		int groupDelim = value.indexOf(Constants.GROUPDELIMITER);
                		aid = value.substring(0,groupDelim);
                		lookupValue = value.substring(groupDelim+1);
                		if(lookupValue == null)
                		{
                			System.out.println("lookupValue is null"+ value);
                		}
                		
                	}
                	else
                	{
                		lookupValue = value;
                		
                	}
               
                    if(lookupValue!=null && database!=null && database.length()>=3)
                    	indexWriter.println(Entity.prepareString(lookupValue).toUpperCase().trim() + "\t" + database.substring(0,3) + "\t" + aid +"\t" + getPui() + "\t" + updatenumber +"\t"+ getAccessnumber());
                    else
                    	indexWriter.println(" " + "\t" + database.substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + updatenumber + "\t"+getAccessnumber());
                }
            }		// no lookup value, still write given parameters
            else
            {
            	indexWriter.println(" " + "\t" + database.substring(0,3) + "\t" + "0"  +"\t" + getPui()+"\t" + updatenumber + "\t" + getAccessnumber());
            	
            }
            indexWriter.flush();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
  
    
    /* For BD correction*/
    public void setLookupRecs(EVCombinedRec[] recs, List<String>authorList, List<String>affiliationList,
    		List<String>serialTitleList, List<String>controltermList, List<String>publishernameList, List<String>ipcList)
    {
    	
    	for(EVCombinedRec rec: recs)
    	{
        	setDatabase(rec.getString(EVCombinedRec.DATABASE));
            setPui(rec.getString(EVCombinedRec.PUI));
            setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            String loadnumber = rec.getString(EVCombinedRec.LOAD_NUMBER);
            if(loadnumber !=null && loadnumber.length()>6)
            {
            	loadnumber = loadnumber.substring(0,6);
            }
            
            //Author
            String[] author=writer.removeExtraSpace(writer.removeSpace(writer.reverseSigns(rec.getStrings(EVCombinedRec.AUTHOR))));
            if(author!=null && author.length>0 )
            {
            	authorList.addAll(Arrays.asList(author));
            }
            
            //Affiliation
            String[] authorAffiliation=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION));
            if(authorAffiliation!=null && authorAffiliation.length>0 )
            {
            	affiliationList.addAll(Arrays.asList(authorAffiliation));
            }
            else
            {
            	String[] affiliationLocations=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION));
            	if(affiliationLocations!=null && affiliationLocations.length>0 )
            		affiliationList.addAll(Arrays.asList(affiliationLocations));
            }
            
            //ControlledTerms
            
            String[] controlledTerms=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS));
            if(controlledTerms!=null && controlledTerms.length>0 )
            {
            	controltermList.addAll(Arrays.asList(controlledTerms));
            }
            else
            {
            	//ChemicalTerms
                String[] chemicalTerms=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.CHEMICALTERMS));
                if(chemicalTerms!=null && chemicalTerms.length>0  )
                {
                	controltermList.addAll(Arrays.asList(chemicalTerms));
                }
            }
            
            //SerialTitle
            String[] serialTitle=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.SERIAL_TITLE));
            if(serialTitle!=null && serialTitle.length>0 )
            {
            	serialTitleList.addAll(Arrays.asList(serialTitle));
            }
            
            //PublisherName
            String[] publisherName=writer.removeExtraSpace(rec.getStrings(EVCombinedRec.PUBLISHER_NAME));
            if(publisherName!=null && publisherName.length>0)
            {
            	publishernameList.addAll(Arrays.asList(publisherName));
            }
            //IPC Code
            String[] intPatentClassification;
    		try 
    		{
    			//intPatentClassification = writer.reverseSigns(writer.addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),lookupMap.get("INTERNATONALPATENTCLASSIFICATION"), getDatabase()));
    			// HT changed to match ES extraction 10/09/2020
    			intPatentClassification = writer.reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION));
    			if(intPatentClassification!=null && intPatentClassification.length>0 )
    	        {
    				ipcList.addAll(Arrays.asList(intPatentClassification));
    	        }
    		} 
    		catch (Exception e) 
    		{
    			e.printStackTrace();
    		}
            
    	}

        
    }
    

}
