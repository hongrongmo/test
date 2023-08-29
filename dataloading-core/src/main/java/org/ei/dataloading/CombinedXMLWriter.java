package org.ei.dataloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.Arrays;

import org.apache.kafka.clients.producer.Producer;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.query.base.PorterStemmer;
import org.ei.xml.Entity;
import org.ei.common.Constants;
import org.ei.common.DataCleaner;
import org.ei.data.bd.runtime.BDDocBuilder;

import java.util.Date;
import java.util.zip.*;
import java.text.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import org.jsoup.Jsoup;

import org.ei.common.*;
import org.ei.util.kafka.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.kafka.clients.producer.*;

public class CombinedXMLWriter
    extends CombinedWriter
{
    private Perl5Util perl = new Perl5Util();
    private PorterStemmer stemmer = new PorterStemmer();
    private DataCleaner cleaner = new DataCleaner();
    private DataValidator d = new DataValidator();
    private int recsPerbatch;
    private int curRecNum = 0;
    private int numberID;
    private int batchID;
    private String batchidFormat;
    private String batchPath;
    private String databaseID;
    private PrintWriter out;
    private boolean open = false;
    private boolean isChild = false;
    private String root = null;
    private String filepath;
    private String logpath;
    private String logfile;
    private String eid = null;
    private String operation = "add";
    private String environment = "dev";
    private long parentid = 0;
    private String lasteid = null;
    private final String PARENT_ID = "PARENTID";
    private HashMap hm = new HashMap();
    private PrintWriter affiliationPW = null;
    private PrintWriter authorPW = null;
    private PrintWriter controltermsPW = null;
    private PrintWriter serialtitlePW = null;
    private PrintWriter publishernamePW = null;
    private PrintWriter patentcountryPW = null;
    private PrintWriter ipcPW = null;
    private String indexKey = null;
    private NumberFormat formatter;
    private long starttime = 0;
    private String database;
    private String pui;
    private String loadnumber;
    private String accessnumber;
    private String endpoint;

    public String getEndpoint() {
    	return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
    	this.endpoint = endpoint;
    }
    
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    public String getDatabaseID() {
        return databaseID;
    }
    
    public void setLoadnumber(String loadnumber) {
        this.loadnumber = loadnumber;
    }
    
    public String getLoadnumber() {
        return loadnumber;
    }
    
    public void setPui(String pui) {
        this.pui = pui;
    }
    
    public String getPui() {
        return pui;
    }
    
    public void setAccessnumber(String accessnumber) {
        this.accessnumber = accessnumber;
    }
    
    public String getAccessnumber() {
        return accessnumber;
    }

    public boolean getIsOpen() {
      return open;
    }

    public void setIsOpen(boolean status) {
      open = status;
    }
    public void setCurRecNum(int recno) {
      curRecNum = recno;
    }
    public void setOperation(String op) {
        this.operation = op.toLowerCase();
    }

    public void setEnvironment(String env) {
        this.environment = env.toLowerCase();
    }

    public int getCurRecNum() {
      return curRecNum;
    }

    public int getNumberID() {
      return numberID;
    }
    public PrintWriter getWriter() {
      return out;
    }
    public void setWriter(PrintWriter awriter) {
      out = awriter;
    }

    public void setDataValidator(DataValidator avalidator)
    {
      this.d = avalidator;
    }

    public CombinedXMLWriter(int recsPerbatch,
            int numberID,
            String databaseID,
            String env)
    {
        System.out.println("recsPerbatch "+recsPerbatch);
        System.out.println("numberID "+numberID);
        System.out.println("databaseID "+databaseID);
        System.out.println("env "+env);
        this.databaseID = databaseID;
        this.numberID = numberID;
        this.batchID = 1;
        formatter = new DecimalFormat("0000");
        this.recsPerbatch = recsPerbatch;
        this.lasteid = null;
        this.environment = env;
        //if(numberID != 0) //removed by hmo at 10/15/2016
           // init();							//HH commented 10/07/2020 as Fast no longer in use
    }

    public CombinedXMLWriter(int recsPerbatch,
                             int numberID,
                             String databaseID)
    {
        System.out.println("recsPerbatch "+recsPerbatch);
        System.out.println("numberID "+numberID);
        System.out.println("databaseID "+databaseID);
        this.databaseID = databaseID;
        this.numberID = numberID;
        this.batchID = 1;
        formatter = new DecimalFormat("0000");
        this.recsPerbatch = recsPerbatch;
        this.lasteid = null;
        //if(numberID != 0) //why loadnumber can't be 0? modified by hmo in10/15/2016
            //init();							//HH commented 10/07/2020 as Fast no longer in use
    }

    public void init()
    {
        try
        {
            File file=new File("ei");
            if(!file.exists())
            {
                file.mkdir();
            }
            file=new File("fast");
            if(!file.exists())
            {
                file.mkdir();
            }

            file=new File("ei/index_af");
            if(!file.exists())
            {
                file.mkdir();
            }
            affiliationPW = new PrintWriter(new FileWriter(file.getPath() + "/affiliation-" + this.numberID + "." + this.databaseID, true));
            hm.put("AUTHORAFFILIATION", affiliationPW);

            file=new File("ei/index_au");
            if(!file.exists())
            {
                file.mkdir();
            }
            authorPW = new PrintWriter(new FileWriter(file.getPath() + "/author-" + this.numberID + "." + this.databaseID, true));
            hm.put("AUTHOR", authorPW);

            file=new File("ei/index_cv");
            if(!file.exists())
            {
                file.mkdir();
            }
            controltermsPW = new PrintWriter(new FileWriter(file.getPath() + "/controlterms-" + this.numberID + "." + this.databaseID, true));
            hm.put("CONTROLLEDTERMS", controltermsPW);

            file=new File("ei/index_st");
            if(!file.exists())
            {
                file.mkdir();
            }
            serialtitlePW = new PrintWriter(new FileWriter(file.getPath() + "/serialtitle-" + this.numberID + "." + this.databaseID, true));
            hm.put("SERIALTITLE", serialtitlePW);

            file=new File("ei/index_pn");
            if(!file.exists())
            {
                file.mkdir();
            }
            publishernamePW = new PrintWriter(new FileWriter(file.getPath() + "/publishername-" + this.numberID + "." + this.databaseID, true));
            hm.put("PUBLISHERNAME", publishernamePW);

            file=new File("ei/index_pc");
            if(!file.exists())
            {
                file.mkdir();
            }
            patentcountryPW = new PrintWriter(new FileWriter(file.getPath() + "/patentcountry-" + this.numberID + "." + this.databaseID, true));
            hm.put("AUTHORITYCODE", patentcountryPW);


            file=new File("ei/index_ipc");
            if(!file.exists())
            {
                file.mkdir();
            }
            ipcPW = new PrintWriter(new FileWriter(file.getPath() + "/ipc-" + this.numberID + "." + this.databaseID, true));
            hm.put("INTERNATONALPATENTCLASSIFICATION", ipcPW);


            file=new File("ei/logs");
            if(!file.exists())
            {
                file.mkdir();
            }
            this.logfile = file.getPath() + "/extract.log";
            file=new File("ei/runtime");
            if(!file.exists())
            {
                file.mkdir();
            }
            createRoot(this.batchID);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    
    public void begin()
        throws Exception
    {
        filepath = this.root + "/" + this.eid + ".xml";
        out = new PrintWriter(new FileWriter(filepath));
        open = true;
        out.println("<?xml version=\"1.0\"?>");
        out.println("<!DOCTYPE ROWSET SYSTEM \"../bin/EVROWSET.dtd\">");
        out.println("<ROWSET>");
    }
    
    public void begin(String eid)
            throws Exception
    {
        filepath = this.root + "/" + eid + ".json";
        out = new PrintWriter(new FileWriter(filepath));
        open = true;        
    }
    public void createRoot(int batchid)
    {
        batchidFormat = formatter.format(this.batchID);
        this.starttime = System.currentTimeMillis();
        this.batchPath = "fast/batch_" + this.numberID + "_" + batchidFormat;
        File file=new File(batchPath);
        if(!file.exists())
        {
            file.mkdir();
        }
        this.root = this.batchPath +"/EIDATA";
        file=new File(this.root);
        if(!file.exists())
        {
            file.mkdir();
        }
        this.root = this.batchPath +"/EIDATA/tmp";
        file=new File(this.root);
        if(!file.exists())
        {
            file.mkdir();
        }
        file=new File(this.batchPath +"/PROD");
        if(!file.exists())
        {
            file.mkdir();
        }
        file=new File(this.batchPath +"/logs");
        if(!file.exists())
        {
            file.mkdir();
        }
        this.logpath = this.batchPath +"/logs";
        file=new File(this.batchPath +"/status");
        if(!file.exists())
        {
            file.mkdir();
        }
    }

    public void writeRec(EVCombinedRec[] rec)
    throws Exception
    {
    	
    	
        if(rec.length >1)
        {
            for(int i=0; i<rec.length; i++)
            {
                if(i>0)
                    this.isChild = true;

                if(rec[i].getString(EVCombinedRec.DATABASE)!=null)
                {
                    setDatabase(rec[i].getString(EVCombinedRec.DATABASE));
                }
                else
                {
                    setDatabase("bd");
                }
                writeRec(rec[i]);
            }
            this.isChild = false;
        }
        else if(rec.length >0)
        {
            setDatabase(rec[0].getString(EVCombinedRec.DATABASE));
            writeRec(rec[0]);
        }
              
    }
    
    public void writeRec(EVCombinedRec[] rec,KafkaService kafka)
    		throws Exception
    {
    	
    	
        if(rec.length >0)
        {
            for(int i=0; i<rec.length; i++)
            {
               // if(i>0)
                   // this.isChild = true;

                if(rec[i].getString(EVCombinedRec.DATABASE)!=null)
                {
                    setDatabase(rec[i].getString(EVCombinedRec.DATABASE));
                }
                else
                {
                    setDatabase("bd");
                }
               // writeRec(rec[i]);
                writeRec(rec[i],kafka);
            }
            //this.isChild = false;
        }
        
        
    }
    
    

    public void writeIndexOnly(EVCombinedRec rec)throws Exception
    {
        begin();
        setDatabase(rec.getString(EVCombinedRec.DATABASE));
        setPui(rec.getString(EVCombinedRec.PUI));
        setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
        setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
        addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR"); //AUTHOR
        if(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION)!=null && (rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))[0]!=null)
        {
            addIndex(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION");//AUTHOR_AFFILIATION
        }
        else if(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)!=null && (rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION))[0]!=null)
        {
            addIndex(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION),"AUTHORAFFILIATION");//AUTHOR_AFFILIATION
        }

        addIndex(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS");//CONTROLLEDTERMS
        addIndex(rec.getStrings(EVCombinedRec.CHEMICALTERMS),"CONTROLLEDTERMS");//CHEMICALTERMS
        addIndex(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE");//SERIALTITLE
        addIndex(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME");//PUBLISHERNAME
        addIndex(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION");//IPC CODE

        ++curRecNum;
        end();

    }
    
    public void writeRec(EVCombinedRec rec, KafkaService kafka)
            throws Exception
    {
    	Map<String,String> batchData = new ConcurrentHashMap<String,String>();
        writeRec(rec,  kafka,  batchData);
    }
    
    public void writeRec(EVCombinedRec rec, KafkaService kafka,Map<String,String> batchData)
            throws Exception
    {
    	Map<String,String> missedData = new ConcurrentHashMap<String,String>();
        writeRec(rec,  kafka,  batchData, missedData);
    }
    
    public void writeRec(EVCombinedRec[] rec,KafkaService kafka, Map<String,String> batchData, Map<String,String> missedData)
    		throws Exception
    { 	
        if(rec.length >0)
        {
            for(int i=0; i<rec.length; i++)
            {             
                if(rec[i].getString(EVCombinedRec.DATABASE)!=null)
                {
                    setDatabase(rec[i].getString(EVCombinedRec.DATABASE));
                }
                else
                {
                    setDatabase("bd");
                }               
                writeRec(rec[i],kafka,batchData,missedData);
            }
            //this.isChild = false;
        }
           
        
    }
    
    public void writeRec(EVCombinedRec[] rec,KafkaService kafka, Map batchData)
    		throws Exception
    { 
    	Map<String,String> missedData = new ConcurrentHashMap<String,String>();
        if(rec.length >0)
        {
            for(int i=0; i<rec.length; i++)
            {             
                if(rec[i].getString(EVCombinedRec.DATABASE)!=null)
                {
                    setDatabase(rec[i].getString(EVCombinedRec.DATABASE));
                }
                else
                {
                    setDatabase("bd");
                }               
                writeRec(rec[i],kafka,batchData,missedData);
            }
            //this.isChild = false;
        }
           
        
    }
   
    
    public void writeRec(EVCombinedRec rec, KafkaService kafka, Map batchData, Map missedData)
            throws Exception
        {
    		StringBuffer recordBuffer = new StringBuffer();
            setDatabase(rec.getString(EVCombinedRec.DATABASE));
            setPui(rec.getString(EVCombinedRec.PUI));
            setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
            String loadnumber = rec.getString(EVCombinedRec.LOAD_NUMBER);
            if(loadnumber !=null && loadnumber.length()>6)
            {
            	loadnumber = loadnumber.substring(0,6);
            }
            
            String eid = rec.getString(EVCombinedRec.DOCID);
            
            JSONObject  elementObject = new JSONObject();
            JSONObject  contentObject = new JSONObject();
            JSONArray  	elementArrayObject = new JSONArray();;
            JSONArray 	elementJsonArray = new JSONArray();
            JSONArray 	contentJsonArray = new JSONArray();            
           
            contentObject.put("EIDOCID".toLowerCase(),eid);
        
            if(rec.getString(EVCombinedRec.PUI)!=null && rec.getString(EVCombinedRec.PUI).length()>0)
            {
            	contentObject.put("PUI".toLowerCase(),rec.getString(EVCombinedRec.PUI));
            }
            
            if(rec.getString(EVCombinedRec.PARENT_ID)!=null && rec.getString(EVCombinedRec.PARENT_ID).length()>0)
            {
            	//change the parentID datatype to long
            	//contentObject.put("PARENTID".toLowerCase(),rec.getString(EVCombinedRec.PARENT_ID));
            	String parentID=rec.getString(EVCombinedRec.PARENT_ID);
            	if(isNumeric(parentID))
            		contentObject.put("PARENTID".toLowerCase(),Long.parseLong(parentID));         	
            }
         
            if(rec.getString(EVCombinedRec.DEDUPKEY)!=null && rec.getString(EVCombinedRec.DEDUPKEY).length()>0)
            {	            
            	contentObject.put("DEDUPKEY".toLowerCase(),rec.getString(EVCombinedRec.DEDUPKEY));
            }
            
            contentObject.put(("database").toLowerCase(),rec.getString(EVCombinedRec.DATABASE));
            
            //change the loadnumber datatype to int
            if(loadnumber.length()>6)
            {
            	if(isNumeric(loadnumber))
            		contentObject.put("LOADNUMBER".toLowerCase(),Integer.parseInt(loadnumber.substring(0,6)));
            }
            else
            {
            	if(isNumeric(loadnumber))
            		contentObject.put("LOADNUMBER".toLowerCase(),Integer.parseInt(loadnumber));
            }

            if(rec.getString(EVCombinedRec.UPDATE_NUMBER)!=null && rec.getString(EVCombinedRec.UPDATE_NUMBER).length()>0)
            {
            	contentObject.put("UPDATENUMBER".toLowerCase(),rec.getString(EVCombinedRec.UPDATE_NUMBER));
            }

            if(rec.getString(EVCombinedRec.DATESORT)!=null && rec.getString(EVCombinedRec.DATESORT).length()>0)
            {
            	//Chnage datesort datatype to long
            	//contentObject.put("DATESORT".toLowerCase(),rec.getString(EVCombinedRec.DATESORT));
            	String datesort=rec.getString(EVCombinedRec.DATESORT);
            	if(isNumeric(datesort))
            		contentObject.put("DATESORT".toLowerCase(),Long.parseLong(datesort));
            }

            if(rec.getString(EVCombinedRec.PUB_YEAR)!=null && rec.getString(EVCombinedRec.PUB_YEAR).length()>0)
            {
            	//change the publicationyear datatype to int
            	//contentObject.put("PUBYEAR".toLowerCase(),rec.getString(EVCombinedRec.PUB_YEAR));
            	String pubYear=rec.getString(EVCombinedRec.PUB_YEAR);
            	if(isNumeric(pubYear))
            		contentObject.put("PUBYEAR".toLowerCase(),Integer.parseInt(pubYear));
            }
                   
            contentObject.put("ACCESSIONNUMBER".toLowerCase(),rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            
            String[] author=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.AUTHOR)));
            if(author!=null && author.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(author),"AUTHOR");          
            	contentObject.put("AUTHOR".toLowerCase(),elementArrayObject);           	
            }
            
            String[] authorID=rec.getStrings(EVCombinedRec.AUTHORID);
            //System.out.println("AUTHORID="+Arrays.toString(authorID));
            if(authorID!=null && authorID.length>0 )
            {
            	elementArrayObject = formJsonArray(authorID,"AUTHORID");          
            	contentObject.put("AUTHORID".toLowerCase(),elementArrayObject);
            }

            String[] authorAffiliation=removeCommaANDPeriod(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION));
            if(authorAffiliation!=null && authorAffiliation.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(authorAffiliation),"AUTHORAFFILIATION");          
            	contentObject.put("AUTHORAFFILIATION".toLowerCase(),elementArrayObject);
            }
            
            String[] affiliationLocations=rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION);
            if(affiliationLocations!=null && affiliationLocations.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(affiliationLocations),"AFFILIATIONLOCATION");          
            	contentObject.put("AFFILIATIONLOCATION".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.TITLE)!=null && rec.getString(EVCombinedRec.TITLE).length()>0)
            {   
            	contentObject.put("TITLE".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.TITLE)));
            }
            
            String[] translatedTitles = rec.getStrings(EVCombinedRec.TRANSLATED_TITLE);
            if(translatedTitles!=null && translatedTitles.length>0 )
            {           	
            	elementArrayObject = formJsonArray(removeExtraSpace(translatedTitles),"TRANSLATEDTITLE");          
            	contentObject.put("TRANSLATEDTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] volumeTitle=rec.getStrings(EVCombinedRec.VOLUME_TITLE);
            if(volumeTitle!=null && volumeTitle.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(volumeTitle),"VOLUMETITLE");          
	            contentObject.put("VOLUMETITLE".toLowerCase(),elementArrayObject);
            }
          
            if(rec.getString(EVCombinedRec.ABSTRACT)!=null && rec.getString(EVCombinedRec.ABSTRACT).trim().length()>0)
            {           	
	            contentObject.put("ABSTRACT".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.ABSTRACT)));	           
            }
           
            //remove OTHERABSTRACT from KY per EVOPS-1195 by hmo at 09/16/2021
            /*           
            if(rec.getString(EVCombinedRec.OTHER_ABSTRACT)!=null && rec.getString(EVCombinedRec.OTHER_ABSTRACT).trim().length()>0)
            {
            	contentObject.put("OTHERABSTRACT".toLowerCase(),rec.getString(EVCombinedRec.OTHER_ABSTRACT));           	    
            }
            */
            
            String[] editor=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.EDITOR)));
            if(editor!=null && editor.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(editor),"EDITOR");          
	            contentObject.put("EDITOR".toLowerCase(),elementArrayObject);	            
            }
            
            String[] editorAffiliation=removeCommaANDPeriod(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION));
            if(editorAffiliation!=null && editorAffiliation.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(editorAffiliation),"EDITORAFFILIATION");          
	            contentObject.put("EDITORAFFILIATION".toLowerCase(),elementArrayObject);
            }
 
            String[] translator=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.TRANSLATOR)));
            if(translator!=null && translator.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(translator),"TRANSLATOR");          
	            contentObject.put("TRANSLATOR".toLowerCase(),elementArrayObject);
            }            
            
            String[] controlledTerms=rec.getStrings(EVCombinedRec.CONTROLLED_TERMS);
            if(controlledTerms!=null && controlledTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(controlledTerms),"CONTROLLEDTERMS");          
	            contentObject.put("CONTROLLEDTERMS".toLowerCase(),elementArrayObject);         
            }
            
            String[] csxTerms=rec.getStrings(EVCombinedRec.CSX_TERMS);
            if(csxTerms!=null && csxTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(csxTerms),"CSX_TERMS");          
	            contentObject.put("CSX".toLowerCase(),elementArrayObject);         
            }
            
            String[] uncontrolledTerms=rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS);
            if(uncontrolledTerms!=null && uncontrolledTerms.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(uncontrolledTerms),"UNCONTROLLEDTERMS");          
	            contentObject.put("UNCONTROLLEDTERMS".toLowerCase(),elementArrayObject);   
            }

            String[] issn=prepareISSN(rec.getStrings(EVCombinedRec.ISSN));
            if(issn!=null && issn.length>0 )
            {
	            elementArrayObject = formJsonArray(issn,"ISSN");          
	            contentObject.put("ISSN".toLowerCase(),elementArrayObject);
            }

            String coden=rec.getString(EVCombinedRec.CODEN);
            if(coden!=null && coden.length()>0)
            {
	            //elementArrayObject = formJsonArray(coden,"CODEN");          
	            contentObject.put("CODEN".toLowerCase(),coden);                     
            }

            String[] codenOfTranslation=rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION);
            if(codenOfTranslation!=null && codenOfTranslation.length>0 )
            {
	            elementArrayObject = formJsonArray(codenOfTranslation,"CODENOFTRANSLATION");          
	            contentObject.put("CODENOFTRANSLATION".toLowerCase(),elementArrayObject);
            }
            
            String[] isbn=prepareISBNForES(rec.getStrings(EVCombinedRec.ISBN));
            if(isbn!=null && isbn.length>0)
            {
	            elementArrayObject = formJsonArray(isbn,"ISBN");          
	            contentObject.put("ISBN".toLowerCase(),elementArrayObject);
            }

            String[] serialTitle=rec.getStrings(EVCombinedRec.SERIAL_TITLE);
            if(serialTitle!=null && serialTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(serialTitle),"SERIALTITLE");          
	            contentObject.put("SERIALTITLE".toLowerCase(),elementArrayObject);
            }

            String[] serialTitleTranslation=rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION);
            if(serialTitleTranslation!=null && serialTitleTranslation.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(serialTitleTranslation),"SERIALTITLETRANSLATION");          
	            contentObject.put("SERIALTITLETRANSLATION".toLowerCase(),elementArrayObject);
            }

            String[] mainHeading=rec.getStrings(EVCombinedRec.MAIN_HEADING);
            if(mainHeading!=null && mainHeading.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(mainHeading),"MAINHEADING");          
	            contentObject.put("MAINHEADING".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.SUB_HEADING)!=null && rec.getString(EVCombinedRec.SUB_HEADING).length()>0)
            {
	            contentObject.put("SUBHEADING".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.SUB_HEADING)));
            }

            String[] publisherName=rec.getStrings(EVCombinedRec.PUBLISHER_NAME);
            if(publisherName!=null && publisherName.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(publisherName),"PUBLISHERNAME");          
	            contentObject.put("PUBLISHERNAME".toLowerCase(),elementArrayObject);
            }

            String[] treatmentCode=rec.getStrings(EVCombinedRec.TREATMENT_CODE);
            if(treatmentCode!=null && treatmentCode.length>0)
            {
	            elementArrayObject = formJsonArray(treatmentCode,"TREATMENTCODE");          
	            contentObject.put("TREATMENTCODE".toLowerCase(),elementArrayObject);
            }
            
            String[] language=rec.getStrings(EVCombinedRec.LANGUAGE);
            if(language!=null && language.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(language),"LANGUAGE");          
	            contentObject.put("LANGUAGE".toLowerCase(),elementArrayObject);
            }

            String[] rectype=rec.getStrings(EVCombinedRec.DOCTYPE);
            if(rec.getString(EVCombinedRec.DOCTYPE)!=null && rectype.length>0)
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(rectype),"RECTYPE");          
 	            contentObject.put("RECTYPE".toLowerCase(),elementArrayObject);
	            //contentObject.put("RECTYPE".toLowerCase(),rec.getString(EVCombinedRec.DOCTYPE));
            }
            
            String[] classificationCode=rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE);
            if(classificationCode!=null && classificationCode.length>0)
            {
	            
	            String database=rec.getString(EVCombinedRec.DATABASE);
	            //System.out.println("DATABASE="+database);
	            if(database.equalsIgnoreCase("grf"))
	            {
	            	//elementArrayObject = formJsonArray(removeCategorySign(reverseSigns(classificationCode)),"CLASSIFICATIONCODE");
	            	elementArrayObject = formJsonArray(reverseSigns(classificationCode),"CLASSIFICATIONCODE");
	            	contentObject.put("CATEGORY".toLowerCase(),elementArrayObject);
	            }
	            else
	            {
	            	elementArrayObject = formJsonArray(reverseSigns(classificationCode),"CLASSIFICATIONCODE");
	            	contentObject.put("CLASSIFICATIONCODE".toLowerCase(),elementArrayObject);
	            }
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_CODE)!=null && rec.getString(EVCombinedRec.CONFERENCE_CODE).length()>0)
            {
	            contentObject.put("CONFERENCECODE".toLowerCase(),notNull(rec.getString(EVCombinedRec.CONFERENCE_CODE)));
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_NAME)!=null && rec.getString(EVCombinedRec.CONFERENCE_NAME).length()>0)
            {
	            contentObject.put("CONFERENCENAME".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.CONFERENCE_NAME)));
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)!=null && rec.getString(EVCombinedRec.CONFERENCE_LOCATION).length()>0)
            {
	            contentObject.put("CONFERENCELOCATION".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)));
            }            
            
            if(rec.getString(EVCombinedRec.MEETING_DATE)!=null && rec.getString(EVCombinedRec.MEETING_DATE).length()>0)
            {
	            contentObject.put("MEETINGDATE".toLowerCase(),notNull(rec.getString(EVCombinedRec.MEETING_DATE)));
            }

            String[] sponsorName=rec.getStrings(EVCombinedRec.SPONSOR_NAME);
            if(sponsorName!=null && sponsorName.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(sponsorName),"SPONSORNAME");          
	            contentObject.put("SPONSORNAME".toLowerCase(),elementArrayObject);
            }
           
            String[] monoGraphTitle=rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE);
            if(monoGraphTitle!=null && monoGraphTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(monoGraphTitle),"MONOGRAPHTITLE");          
	            contentObject.put("MONOGRAPHTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] discipline=rec.getStrings(EVCombinedRec.DISCIPLINE);
            if(discipline!=null && discipline.length>0)
            {
	            elementArrayObject = formJsonArray(discipline,"DISCIPLINE");          
	            contentObject.put("DISCIPLINE".toLowerCase(),elementArrayObject);
            }
            
            String[] materialNumber=rec.getStrings(EVCombinedRec.MATERIAL_NUMBER);
            if(materialNumber!=null && materialNumber.length>0)
            {
	            elementArrayObject = formJsonArray(materialNumber,"MATERIALNUMBER");          
	            contentObject.put("MATERIALNUMBER".toLowerCase(),elementArrayObject);
            }

            String[] numericalIndexing=rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING);
            if(numericalIndexing!=null && numericalIndexing.length>0 )
            {
	            elementArrayObject = formJsonArray(numericalIndexing,"NUMERICALINDEXING");          
	            contentObject.put("NUMERICALINDEXING".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalIndexing=rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING);
            if(chemicalIndexing!=null && chemicalIndexing.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalIndexing),"CHEMICALINDEXING");          
	            contentObject.put("CHEMICALINDEXING".toLowerCase(),elementArrayObject);
            }

            String[] astronomicalIndexing=rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING);
            if(astronomicalIndexing!=null && astronomicalIndexing.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(astronomicalIndexing),"ASTRONOMICALINDEXING");          
	            contentObject.put("ASTRONOMICALINDEXING".toLowerCase(),elementArrayObject);
            }
            
            String[] reportNumber=rec.getStrings(EVCombinedRec.REPORTNUMBER);
            if(reportNumber!=null && reportNumber.length>0)
            {
	            elementArrayObject = formJsonArray(reportNumber,"REPORTNUMBER");          
	            contentObject.put("REPORTNUMBER".toLowerCase(),elementArrayObject);
            }

            String[] ordernumber=rec.getStrings(EVCombinedRec.ORDERNUMBER);
            if(ordernumber!=null && ordernumber.length>0)
            {
	            elementArrayObject = formJsonArray(ordernumber,"ORDERNUMBER");          
	            contentObject.put("ORDERNUMBER".toLowerCase(),elementArrayObject);     
            }
            
            //added by hmo at 8/9/2023 for EVOPS-1497
            String[] countries=rec.getStrings(EVCombinedRec.COUNTRY);
            String sourcecountry = rec.getString(EVCombinedRec.SOURCE_COUNTRY); 
            if(countries!=null && countries.length>0 && countries[0]!=null && countries[0].length()>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(countries),"COUNTRY");          
	            contentObject.put("COUNTRY".toLowerCase(),elementArrayObject);
	            //System.out.println("COUNTRY="+Arrays.toString(countries));
            }
            /*** block - pending for confirmimg if we should use sourcecountry as conntry
            else if(sourcecountry!=null && sourcecountry.length()>0)
            {
            	String[] sourcecountries = {sourcecountry};
            	elementArrayObject = formJsonArray(removeExtraSpace(sourcecountries),"COUNTRY");          
	            contentObject.put("COUNTRY".toLowerCase(),elementArrayObject);	            
            }
            */
            else
            {
            	System.out.println("no country code found");
            }
            
            if(rec.getString(EVCombinedRec.VOLUME)!=null && rec.getString(EVCombinedRec.VOLUME).length()>0)
            {
	            contentObject.put("VOLUME".toLowerCase(),notNull(rec.getString(EVCombinedRec.VOLUME)));
            }
            
            if(rec.getString(EVCombinedRec.ISSUE)!=null && rec.getString(EVCombinedRec.ISSUE).length()>0)
            {
	            contentObject.put("ISSUE".toLowerCase(),notNull(rec.getString(EVCombinedRec.ISSUE)));
            }
            
            if(rec.getString(EVCombinedRec.STARTPAGE)!=null && rec.getString(EVCombinedRec.STARTPAGE).length()>0)
            {
	            contentObject.put("STARTPAGE".toLowerCase(),notNull(rec.getString(EVCombinedRec.STARTPAGE)));
            }
            
            if(rec.getString(EVCombinedRec.PAGE)!=null && rec.getString(EVCombinedRec.PAGE).length()>0)
            {
	            contentObject.put("PAGE".toLowerCase(),notNull(rec.getString(EVCombinedRec.PAGE)));
            }
            
            if(rec.getString(EVCombinedRec.AVAILABILITY)!=null && rec.getString(EVCombinedRec.AVAILABILITY).length()>0)
            {
	            contentObject.put("AVAILABILITY".toLowerCase(),rec.getString(EVCombinedRec.AVAILABILITY));
            }
            
            String[] notes=rec.getStrings(EVCombinedRec.NOTES);
            if(notes!=null && notes.length>0 )
            {
	            elementArrayObject = formJsonArray(notes,"NOTES");          
	            contentObject.put("NOTES".toLowerCase(),elementArrayObject);
            }
            
            String[] patentAppDate=rec.getStrings(EVCombinedRec.PATENTAPPDATE);
            if(patentAppDate!=null && patentAppDate.length>0 )
            {
	            elementArrayObject = formJsonArray(patentAppDate,"PATENTAPPDATE");          
	            contentObject.put("PATENTAPPDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] patentIssueDate=rec.getStrings(EVCombinedRec.PATENTISSUEDATE);
            if(patentIssueDate!=null && patentIssueDate.length>0)
            {
	            elementArrayObject = formJsonArray(patentIssueDate,"PATENTISSUEDATE");          
	            contentObject.put("PATENTISSUEDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] companies=rec.getStrings(EVCombinedRec.COMPANIES);
            if(companies!=null && companies.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(companies),"COMPANIES");          
	            contentObject.put("COMPANIES".toLowerCase(),elementArrayObject);
            }
            
            String[] casRegistryNumber=rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER);
            if(casRegistryNumber!=null && casRegistryNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(casRegistryNumber,"CASREGISTRYNUMBER");          
	            contentObject.put("CASREGISTRYNUMBER".toLowerCase(),elementArrayObject);
            }
           
            String[] businessTerms=rec.getStrings(EVCombinedRec.BUSINESSTERMS);
            if(businessTerms!=null && businessTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(businessTerms),"BUSINESSTERMS");          
	            contentObject.put("BUSINESSTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalTerms=rec.getStrings(EVCombinedRec.CHEMICALTERMS);
            if(chemicalTerms!=null && chemicalTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalTerms),"CHEMICALTERMS");          
	            contentObject.put("CHEMICALTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalAcronyms=rec.getStrings(EVCombinedRec.CHEMICALACRONYMS);
            if(chemicalAcronyms!=null && chemicalAcronyms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalAcronyms),"CHEMAC");          
	            contentObject.put("CHEMAC".toLowerCase(),elementArrayObject);
            }
            
            String[] patentNumber=rec.getStrings(EVCombinedRec.PATENT_NUMBER);
            if(patentNumber!=null && patentNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(patentNumber,"SIC");          
	            contentObject.put("SIC".toLowerCase(),elementArrayObject);
            }

            String[] industrialcodes=rec.getStrings(EVCombinedRec.INDUSTRIALCODES);
            if(industrialcodes!=null && industrialcodes.length>0 )
            {
	            elementArrayObject = formJsonArray(industrialcodes,"INDUSTRIALCODES");          
	            contentObject.put("INDUSTRIALCODES".toLowerCase(),elementArrayObject);
            }
            
            String[] industrialsectors=rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS);
            if(industrialsectors!=null && industrialsectors.length>0 )
            {
	            elementArrayObject = formJsonArray(industrialsectors,"INDUSTRIALSECTORS");          
	            contentObject.put("INDUSTRIALSECTORS".toLowerCase(),elementArrayObject);
            }
            
            String[] scope=rec.getStrings(EVCombinedRec.SCOPE);
            if(scope!=null && scope.length>0 )
            {
	            elementArrayObject = formJsonArray(scope,"SCOPE");          
	            contentObject.put("SCOPE".toLowerCase(),elementArrayObject);
            }
            
            String[] agency=rec.getStrings(EVCombinedRec.AGENCY);
            if(agency!=null && agency.length>0)
            {
	            elementArrayObject = formJsonArray(agency,"AGENCY");          
	            contentObject.put("AGENCY".toLowerCase(),elementArrayObject);
	            contentJsonArray.add(contentObject);
            }
            
            String[] derwentAccessionNumber=rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER); 
            if(derwentAccessionNumber!=null && derwentAccessionNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(derwentAccessionNumber,"DERWENTACCESSIONNUMBER");          
	            contentObject.put("DERWENTACCESSIONNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] applicationNumber=rec.getStrings(EVCombinedRec.APPLICATION_NUMBER);
            if(applicationNumber!=null && applicationNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(applicationNumber,"APPLICATIONNUMBER");          
	            contentObject.put("APPLICATIONNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] applicationCountry=rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY);
            if(applicationCountry!=null && applicationCountry.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(applicationCountry),"APPLICATIONCOUNTRY");          
	            contentObject.put("APPLICATIONCOUNTRY".toLowerCase(),elementArrayObject);
            }
            
            //String[] intPatentClassification=addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION");
            String[] intPatentClassification=rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION);
            if(intPatentClassification!=null && intPatentClassification.length>0 )
            {
	            elementArrayObject = formJsonArray(reverseSigns(intPatentClassification),"INTPATENTCLASSIFICATION");          
	            contentObject.put("INTPATENTCLASSIFICATION".toLowerCase(),elementArrayObject);
            }
            
            String[] linkedTerms=stripChar29(rec.getStrings(EVCombinedRec.LINKED_TERMS));
            if(linkedTerms!=null && linkedTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(linkedTerms),"LINKEDTERMS");          
	            contentObject.put("LINKEDTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] entryYear=rec.getStrings(EVCombinedRec.ENTRY_YEAR);
            if(entryYear!=null && entryYear.length>0 )
            {
	            elementArrayObject = formJsonArray(entryYear,"ENTRYYEAR");          
	            contentObject.put("ENTRYYEAR".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityNumber=rec.getStrings(EVCombinedRec.PRIORITY_NUMBER);
            if(priorityNumber!=null && priorityNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityNumber,"PRIORITYNUMBER");          
	            contentObject.put("PRIORITYNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityDate=rec.getStrings(EVCombinedRec.PRIORITY_DATE);
            if(priorityDate!=null && priorityDate.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityDate,"PRIORITYDATE");          
	            contentObject.put("PRIORITYDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityCountry=rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY);
            if(priorityCountry!=null && priorityCountry.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityCountry,"PRIORITYCOUNTRY");          
	            contentObject.put("PRIORITYCOUNTRY".toLowerCase(),elementArrayObject);
            }
            
            String[] source=rec.getStrings(EVCombinedRec.SOURCE);
            if(source!=null && source.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(source),"SOURCE");          
	            contentObject.put("SOURCE".toLowerCase(),elementArrayObject);
            }
            
            String[] secondarySrcTitle=rec.getStrings(EVCombinedRec.SECONDARY_SRC_TITLE);   
            if(secondarySrcTitle!=null && secondarySrcTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(secondarySrcTitle),"SECONDARYSRCTITLE");          
	            contentObject.put("SECONDARYSRCTITLE".toLowerCase(),elementArrayObject); 
            }

            String[] mainTerm=rec.getStrings(EVCombinedRec.MAIN_TERM);
            if(mainTerm!=null && mainTerm.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(mainTerm),"MAINTERM");          
	            contentObject.put("MAINTERM".toLowerCase(),elementArrayObject);
            }
            
            String[] abbrvSrcTitle=rec.getStrings(EVCombinedRec.ABBRV_SRC_TITLE);
            if(abbrvSrcTitle!=null && abbrvSrcTitle.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(abbrvSrcTitle),"ABBRVSRCTITLE");          
	            contentObject.put("ABBRVSRCTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] noroleTerms=rec.getStrings(EVCombinedRec.NOROLE_TERMS);
            if(noroleTerms!=null && noroleTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(noroleTerms,"NOROLETERMS");          
	            contentObject.put("NOROLETERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] reagentTerms=rec.getStrings(EVCombinedRec.REAGENT_TERMS);
            if(reagentTerms!=null && reagentTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(reagentTerms,"REAGENTTERMS");          
	            contentObject.put("REAGENTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] productTerms=rec.getStrings(EVCombinedRec.PRODUCT_TERMS);
            if(productTerms!=null && productTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(productTerms),"PRODUCTTERMS");          
	            contentObject.put("PRODUCTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorNoRoleTerms=rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS);
            if(majorNoRoleTerms!=null && majorNoRoleTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(majorNoRoleTerms,"MAJORNOROLETERMS");          
	            contentObject.put("MAJORNOROLETERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorReagentTerms=rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS);
            if(majorReagentTerms!=null && majorReagentTerms.length>0)
            {
	            elementArrayObject = formJsonArray(majorReagentTerms,"MAJORREAGENTTERMS");          
	            contentObject.put("MAJORREAGENTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorProductTerm=rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS);
            if(majorProductTerm!=null && majorProductTerm.length>0 )
            {
	            elementArrayObject = formJsonArray(majorProductTerm,"MAJORPRODUCTTERMS");          
	            contentObject.put("MAJORPRODUCTTERMS".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
            
            String[] conferenceAffiliations=rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS);
            if(conferenceAffiliations!=null && conferenceAffiliations.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(conferenceAffiliations),"CONFERENCEAFFILIATIONS");          
	            contentObject.put("CONFERENCEAFFILIATIONS".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
            
            String[] conferenceeditors=reverseSigns(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS));
            if(conferenceeditors!=null && conferenceeditors.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(conferenceeditors),"CONFERENCEEDITORS");          
	            contentObject.put("CONFERENCEEDITORS".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceStartDate=rec.getStrings(EVCombinedRec.CONFERENCESTARTDATE);
            if(conferenceStartDate!=null && conferenceStartDate.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceStartDate,"CONFERENCESTARTDATE");          
	            contentObject.put("CONFERENCESTARTDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceEndDate=rec.getStrings(EVCombinedRec.CONFERENCEENDDATE);
            if(conferenceEndDate!=null && conferenceEndDate.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceEndDate,"CONFERENCEENDDATE");          
	            contentObject.put("CONFERENCEENDDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferencevenusite=rec.getStrings(EVCombinedRec.CONFERENCEVENUESITE);
            if(conferencevenusite!=null && conferencevenusite.length>0 )
            {
	            elementArrayObject = formJsonArray(conferencevenusite,"CONFERENCEVENUESITE");          
	            contentObject.put("CONFERENCEVENUESITE".toLowerCase(),elementArrayObject);
            }
             
            String[] conferenceCity=rec.getStrings(EVCombinedRec.CONFERENCECITY);
            if(conferenceCity!=null && conferenceCity.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceCity,"CONFERENCECITY");          
	            contentObject.put("CONFERENCECITY".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceCountryCode=rec.getStrings(EVCombinedRec.CONFERENCECOUNTRYCODE);
            if(conferenceCountryCode!=null && conferenceCountryCode.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceCountryCode,"CONFERENCECOUNTRYCODE");          
	            contentObject.put("CONFERENCECOUNTRYCODE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferencePageRange=rec.getStrings(EVCombinedRec.CONFERENCEPAGERANGE);
            if(conferencePageRange!=null && conferencePageRange.length>0)
            {
	            elementArrayObject = formJsonArray(conferencePageRange,"CONFERENCEPAGERANGE");          
	            contentObject.put("CONFERENCEPAGERANGE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceNumberPages=rec.getStrings(EVCombinedRec.CONFERENCENUMBERPAGES);
            if(conferenceNumberPages!=null && conferenceNumberPages.length>0)
            {
	            elementArrayObject = formJsonArray(conferenceNumberPages,"CONFERENCENUMBERPAGES");          
	            contentObject.put("CONFERENCENUMBERPAGES".toLowerCase(),elementArrayObject);
            }

            String[] conferencePartNumber=rec.getStrings(EVCombinedRec.CONFERENCEPARTNUMBER);
            if(conferencePartNumber!=null && conferencePartNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(conferencePartNumber,"CONFERENCEPARTNUMBER");          
	            contentObject.put("CONFERENCEPARTNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] designatedStates=rec.getStrings(EVCombinedRec.DESIGNATED_STATES);
            if(designatedStates!=null && designatedStates.length>0 )
            {
	            elementArrayObject = formJsonArray(designatedStates,"DESIGNATEDSTATES");          
	            contentObject.put("DESIGNATEDSTATES".toLowerCase(),elementArrayObject);
            }
            
            String[] stnConference=rec.getStrings(EVCombinedRec.STN_CONFERENCE);
            if(stnConference!=null && stnConference.length>0 )
            {
	            elementArrayObject = formJsonArray(stnConference,"STNCONFERENCE");          
	            contentObject.put("STNCONFERENCE".toLowerCase(),elementArrayObject);
            }
            
            String[] stnSecondaryConference=rec.getStrings(EVCombinedRec.STN_SECONDARY_CONFERENCE);
            if(stnSecondaryConference!=null && stnSecondaryConference.length>0 && stnSecondaryConference[0]!=null)
            {
	            elementArrayObject = formJsonArray(stnSecondaryConference,"STNSECONDARYCONFERENCE");          
	            contentObject.put("STNSECONDARYCONFERENCE".toLowerCase(),elementArrayObject);
            }
            
            String[] patentFilingDate=rec.getStrings(EVCombinedRec.PATENT_FILING_DATE);
            if(patentFilingDate!=null && patentFilingDate.length>0 && patentFilingDate[0]!=null)
            {
	            elementArrayObject = formJsonArray(patentFilingDate,"FILINGDATE");          
	            contentObject.put("FILINGDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityKind=rec.getStrings(EVCombinedRec.PRIORITY_KIND);
            if(priorityKind!=null && priorityKind.length>0 && priorityKind[0]!=null)
            {
	            elementArrayObject = formJsonArray(priorityKind,"PRIORITYKIND");          
	            contentObject.put("PRIORITYKIND".toLowerCase(),elementArrayObject);
            }
            
            String[] eclaCode=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_CODES));
            if(eclaCode!=null && eclaCode.length>0 && eclaCode[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclaCode,"ECLACODE");          
	            contentObject.put("ECLACODE".toLowerCase(),elementArrayObject);
            }
            
            String[] attorneyName=removeSpace(rec.getStrings(EVCombinedRec.ATTORNEY_NAME));
            if(attorneyName!=null && attorneyName.length>0 && attorneyName[0]!=null)
            {
	            elementArrayObject = formJsonArray(attorneyName,"ATTORNEYNAME");          
	            contentObject.put("ATTORNEYNAME".toLowerCase(),elementArrayObject);
            }
            
            String[] primaryExaminer=rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER);
            if(primaryExaminer!=null && primaryExaminer.length>0 && primaryExaminer[0]!=null)
            {
	            elementArrayObject = formJsonArray(primaryExaminer,"PRIMARYEXAMINER");          
	            contentObject.put("PRIMARYEXAMINER".toLowerCase(),elementArrayObject);
            }

            String[] assistantExaminer=rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER);
            if(assistantExaminer!=null && assistantExaminer.length>0 && assistantExaminer[0]!=null)
            {
	            elementArrayObject = formJsonArray(assistantExaminer,"ASSISTANTEXAMINER");          
	            contentObject.put("ASSISTANTEXAMINER".toLowerCase(),elementArrayObject);
            }
            
            String[] intpatentclasses=reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES));
            if(intpatentclasses!=null && intpatentclasses.length>0 && intpatentclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(intpatentclasses,"IPCCLASS");          
	            contentObject.put("IPCCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] intpatentsubclasses=reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES));
            if(intpatentsubclasses!=null && intpatentsubclasses.length>0 && intpatentsubclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(intpatentsubclasses,"IPCCLASS");          
	            contentObject.put("IPCCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] eclaclasses=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_CLASSES));
            if(eclaclasses!=null && eclaclasses.length>0 && eclaclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclaclasses,"ECLACLASS");          
	            contentObject.put("ECLACLASS".toLowerCase(),elementArrayObject);
            }
            
            //added by hmo for cpcclass at 12/12/2022
            String[] cpcclasses=reverseSigns(rec.getStrings(EVCombinedRec.CPCCLASS));
            if(cpcclasses!=null && cpcclasses.length>0 && cpcclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(cpcclasses,"CPCCLASS");          
	            contentObject.put("CPCCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] eclassubclasses=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES));
            if(eclassubclasses!=null && eclassubclasses.length>0 && eclassubclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclassubclasses,"ECLASUBCLASS");          
	            contentObject.put("ECLASUBCLASS".toLowerCase(),elementArrayObject);
            }
                                
            String[] usptoclass=reverseSigns(rec.getStrings(EVCombinedRec.USPTOCLASS));
            if(usptoclass!=null && usptoclass.length>0 && usptoclass[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptoclass,"USPTOCLASS");          
	            contentObject.put("USPTOCLASS".toLowerCase(),elementArrayObject);
            }

            String[] usptosubclass=reverseSigns(rec.getStrings(EVCombinedRec.USPTOSUBCLASS));
            if(usptosubclass!=null && usptosubclass.length>0 && usptosubclass[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptosubclass,"USPTOSUBCLASS");          
	            contentObject.put("USPTOSUBCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] usptocode=reverseSigns(rec.getStrings(EVCombinedRec.USPTOCODE));
            if(usptocode!=null && usptocode.length>0 && usptocode[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptocode,"USPTOCODE");          
	            contentObject.put("USPTOCODE".toLowerCase(),elementArrayObject);
            }

            String[] patentkind=rec.getStrings(EVCombinedRec.PATENT_KIND);
            if(patentkind!=null && patentkind.length>0 && patentkind[0]!=null)
            {
	            elementArrayObject = formJsonArray(patentkind,"PATENTKIND");          
	            contentObject.put("PATENTKIND".toLowerCase(),elementArrayObject);
            }
            
            String[] kinddescr=rec.getStrings(EVCombinedRec.KIND_DESCR);
            if(kinddescr!=null && kinddescr.length>0 && kinddescr[0]!=null)
            {
	            elementArrayObject = formJsonArray(kinddescr,"KINDDESCRIPTION");          
	            contentObject.put("KINDDESCRIPTION".toLowerCase(),elementArrayObject);
            }

            if(rec.getString(EVCombinedRec.AUTHORITY_CODE)!=null && rec.getString(EVCombinedRec.AUTHORITY_CODE).length()>0)
            {
	            contentObject.put("AUTHORITYCODE".toLowerCase(),notNull(rec.getString(EVCombinedRec.AUTHORITY_CODE)));
            }
            
            if(rec.getString(EVCombinedRec.PCITED)!=null && rec.getString(EVCombinedRec.PCITED).length()>0)
            {
	            //change the pcited datatype to int
            	//contentObject.put("PCITED".toLowerCase(),notNull(rec.getString(EVCombinedRec.PCITED)));
            	String pcited=rec.getString(EVCombinedRec.PCITED);
            	if(isNumeric(pcited))
            		contentObject.put("PCITED".toLowerCase(),Integer.parseInt(pcited));
            }

            String[] pcitedindex=rec.getStrings(EVCombinedRec.PCITEDINDEX);
            if(pcitedindex!=null && pcitedindex.length>0 && pcitedindex[0]!=null)
            {                   
	            elementArrayObject = formJsonArray(pcitedindex,"PCITEDINDEX");          
	            contentObject.put("PCITEDINDEX".toLowerCase(),elementArrayObject);
            }

            String[] prefindex=rec.getStrings(EVCombinedRec.PREFINDEX);
            if(prefindex!=null && prefindex.length>0 && prefindex[0]!=null)
			{
	            elementArrayObject = formJsonArray(prefindex,"PREFINDEX");          
	            contentObject.put("PREFINDEX".toLowerCase(),elementArrayObject);
			}
                    
            contentObject.put("DMASK".toLowerCase(),getMask(rec));            
            
            if(rec.getString(EVCombinedRec.DOI)!=null && rec.getString(EVCombinedRec.DOI).length()>0)
            {
	            contentObject.put("DOI".toLowerCase(),rec.getString(EVCombinedRec.DOI).replaceAll(" ",""));
            }

            if(rec.getString(EVCombinedRec.SCOPUSID)!=null && rec.getString(EVCombinedRec.SCOPUSID).length()>0)
            {
	            contentObject.put("SCOPUSID".toLowerCase(),notNull(rec.getString(EVCombinedRec.SCOPUSID)));
            }
            
            String[] affiliationid=rec.getStrings(EVCombinedRec.AFFILIATIONID);
            if(affiliationid!=null && affiliationid.length>0 && affiliationid[0]!=null)
            {
	            elementArrayObject = formJsonArray(affiliationid,"AFFILIATIONID");          
	            contentObject.put("AFFILIATIONID".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.LAT_NW)!=null && rec.getString(EVCombinedRec.LAT_NW).length()>0)
            {
            	//change the LAT_NW to int
	            //contentObject.put("LAT_NW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_NW)));
	            String latitudeNorthWest=rec.getString(EVCombinedRec.LAT_NW);
            	if(isNumeric(latitudeNorthWest))
            		contentObject.put("LAT_NW".toLowerCase(),Integer.parseInt(latitudeNorthWest));
            }
            
            if(rec.getString(EVCombinedRec.LNG_NW)!=null && rec.getString(EVCombinedRec.LNG_NW).length()>0)
            {
            	//change the LNG_NW to int
	            //contentObject.put("LNG_NW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_NW)));
            	String longitudeNorthWest=rec.getString(EVCombinedRec.LNG_NW);
            	if(isNumeric(longitudeNorthWest))
            		contentObject.put("LNG_NW".toLowerCase(),Integer.parseInt(longitudeNorthWest));
            }
            
            if(rec.getString(EVCombinedRec.LAT_NE)!=null && rec.getString(EVCombinedRec.LAT_NE).length()>0)
            {
            	//change the LAT_NE to int
	            //contentObject.put("LAT_NE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_NE)));
            	String latitudeNorthEast=rec.getString(EVCombinedRec.LAT_NE);
            	if(isNumeric(latitudeNorthEast))
            		contentObject.put("LAT_NE".toLowerCase(),Integer.parseInt(latitudeNorthEast));
            }
            
            if(rec.getString(EVCombinedRec.LNG_NE)!=null && rec.getString(EVCombinedRec.LNG_NE).length()>0)
            {
            	//change the LNG_NE to int
	            //contentObject.put("LNG_NE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_NE)));
            	String longitudeNorthEast=rec.getString(EVCombinedRec.LNG_NE);
            	if(isNumeric(longitudeNorthEast))
            		contentObject.put("LNG_NE".toLowerCase(),Integer.parseInt(longitudeNorthEast));
            }
            
            if(rec.getString(EVCombinedRec.LAT_SW)!=null && rec.getString(EVCombinedRec.LAT_SW).length()>0)
            {
            	//change the LAT_SW to int
	            //contentObject.put("LAT_SW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_SW)));
            	String latitudeSouthWest=rec.getString(EVCombinedRec.LAT_SW);
            	if(isNumeric(latitudeSouthWest))
            		contentObject.put("LAT_SW".toLowerCase(),Integer.parseInt(latitudeSouthWest));
            }
            
            if(rec.getString(EVCombinedRec.LNG_SW)!=null && rec.getString(EVCombinedRec.LNG_SW).length()>0)
            {
            	//change the LNG_SW to int
	            //contentObject.put("LNG_SW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_SW)));
            	String longitudeSouthWest=rec.getString(EVCombinedRec.LNG_SW);
            	if(isNumeric(longitudeSouthWest))
            		contentObject.put("LNG_SW".toLowerCase(),Integer.parseInt(longitudeSouthWest));
            }
            
            if(rec.getString(EVCombinedRec.LAT_SE)!=null && rec.getString(EVCombinedRec.LAT_SE).length()>0)
            {
            	//change the LAT_SE to int
	            //contentObject.put("LAT_SE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_SE)));
            	String latitudeSouthEast=rec.getString(EVCombinedRec.LAT_SE);
            	if(isNumeric(latitudeSouthEast))
            		contentObject.put("LAT_SE".toLowerCase(),Integer.parseInt(latitudeSouthEast));
            }
                         
            if(rec.getString(EVCombinedRec.LNG_SE)!=null && rec.getString(EVCombinedRec.LNG_SE).length()>0)
            {
            	//change the LNG_SE to int
	            //contentObject.put("LNG_SE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_SE)));
            	String longitudeSouthEast=rec.getString(EVCombinedRec.LNG_SE);
            	if(isNumeric(longitudeSouthEast))
            		contentObject.put("LNG_SE".toLowerCase(),Integer.parseInt(longitudeSouthEast));
            }
            
            String[] tableofcontent=rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT);
            if(tableofcontent!=null && tableofcontent.length>0 && tableofcontent[0]!=null)
            {
	            elementArrayObject = formJsonArray(tableofcontent,"TABLEOFCONTENT");          
	            contentObject.put("TABLEOFCONTENT".toLowerCase(),elementArrayObject);
            }
            
            String firstAuthor=rec.getString(EVCombinedRec.FIRST_AUTHOR);
            if(firstAuthor!=null && firstAuthor.length()>0)
            {     
	            contentObject.put("FIRSTAUTHOR".toLowerCase(),removeSpace(firstAuthor));                     
            }
            
            //************************************************ added for numericalIndex ******************************************************//
            
        
            if(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES),"AMOUNTOFSUBSTANCE_RANGES");          
	            contentObject.put("AMOUNTOFSUBSTANCE_RANGES".toLowerCase(),elementArrayObject);	           
            }
                       
            if(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)),"AMOUNTOFSUBSTANCE_TEXT");          
	            contentObject.put("AMOUNTOFSUBSTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES),"ELECTRICCURRENT_RANGES");          
	            contentObject.put("ELECTRICCURRENT_RANGES".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)),"ELECTRICCURRENT_TEXT");          
	            contentObject.put("ELECTRICCURRENT_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_RANGES),"MASS_RANGES");          
	            contentObject.put("MASS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_TEXT)),"MASS_TEXT");          
	            contentObject.put("MASS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES),"TEMPERATURE_RANGES");          
	            contentObject.put("TEMPERATURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)),"TEMPERATURE_TEXT");          
	            contentObject.put("TEMPERATURE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TIME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_RANGES),"TIME_RANGES");          
	            contentObject.put("TIME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TIME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TIME_TEXT)),"TIME_TEXT");          
	            contentObject.put("TIME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SIZE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_RANGES),"SIZE_RANGES");          
	            contentObject.put("SIZE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SIZE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SIZE_TEXT)),"SIZE_TEXT");          
	            contentObject.put("SIZE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES),"ELECTRICALCONDUCTANCE_RANGES");          
	            contentObject.put("ELECTRICALCONDUCTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)),"ELECTRICALCONDUCTANCE_TEXT");          
	            contentObject.put("ELECTRICALCONDUCTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES),"ELECTRICALCONDUCTIVITY_RANGES");          
	            contentObject.put("ELECTRICALCONDUCTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)),"ELECTRICALCONDUCTIVITY_TEXT");          
	            contentObject.put("ELECTRICALCONDUCTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES),"VOLTAGE_RANGES");          
	            contentObject.put("VOLTAGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)),"VOLTAGE_TEXT");          
	            contentObject.put("VOLTAGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES),"ELECTRICFIELDSTRENGTH_RANGES");          
	            contentObject.put("ELECTRICFIELDSTRENGTH_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)),"ELECTRICFIELDSTRENGTH_TEXT");          
	            contentObject.put("ELECTRICFIELDSTRENGTH_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES),"CURRENTDENSITY_RANGES");          
	            contentObject.put("CURRENTDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)),"CURRENTDENSITY_TEXT");          
	            contentObject.put("CURRENTDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ENERGY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_RANGES),"ENERGY_RANGES");          
	            contentObject.put("ENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ENERGY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ENERGY_TEXT)),"ENERGY_TEXT");          
	            contentObject.put("ENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES),"ELECTRICALRESISTANCE_RANGES");          
	            contentObject.put("ELECTRICALRESISTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)),"ELECTRICALRESISTANCE_TEXT");          
	            contentObject.put("ELECTRICALRESISTANCE_TEXT".toLowerCase(),elementArrayObject);	            
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES),"ELECTRICALRESISTIVITY_RANGES");          
	            contentObject.put("ELECTRICALRESISTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
                     
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)),"ELECTRICALRESISTIVITY_TEXT");          
	            contentObject.put("ELECTRICALRESISTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES),"ELECTRONVOLTENERGY_RANGES");          
	            contentObject.put("ELECTRONVOLTENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)),"ELECTRONVOLTENERGY_TEXT");          
	            contentObject.put("ELECTRONVOLTENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES),"CAPACITANCE_RANGES");          
	            contentObject.put("CAPACITANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)),"CAPACITANCE_TEXT");          
	            contentObject.put("CAPACITANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES),"FREQUENCY_RANGES");          
	            contentObject.put("FREQUENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)),"FREQUENCY_TEXT");          
	            contentObject.put("FREQUENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.POWER_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_RANGES),"POWER_RANGES");          
	            contentObject.put("POWER_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.POWER_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.POWER_TEXT)),"POWER_TEXT");          
	            contentObject.put("POWER_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES)),"APPARENTPOWER_RANGES");          
	            contentObject.put("APPARENTPOWER_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)),"APPARENTPOWER_TEXT");          
	            contentObject.put("APPARENTPOWER_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES)),"PERCENTAGE_RANGES");          
	            contentObject.put("PERCENTAGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)),"PERCENTAGE_TEXT");          
	            contentObject.put("PERCENTAGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES),"MAGNETICFLUXDENSITY_RANGES");          
	            contentObject.put("MAGNETICFLUXDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)),"MAGNETICFLUXDENSITY_TEXT");          
	            contentObject.put("MAGNETICFLUXDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES),"INDUCTANCE_RANGES");          
	            contentObject.put("INDUCTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)),"INDUCTANCE_TEXT");          
	            contentObject.put("INDUCTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES),"VOLUMECHARGEDENSITY_RANGES");          
	            contentObject.put("VOLUMECHARGEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)),"VOLUMECHARGEDENSITY_TEXT");          
	            contentObject.put("VOLUMECHARGEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES),"SURFACECHARGEDENSITY_RANGES");          
	            contentObject.put("SURFACECHARGEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)),"SURFACECHARGEDENSITY_TEXT");          
	            contentObject.put("SURFACECHARGEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_RANGES),"DECIBEL_RANGES");          
	            contentObject.put("DECIBEL_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)),"DECIBEL_TEXT");          
	            contentObject.put("DECIBEL_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_RANGES),"LUMINOUSFLUX_RANGES");          
	            contentObject.put("LUMINOUSFLUX_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)),"LUMINOUSFLUX_TEXT");          
	            contentObject.put("LUMINOUSFLUX_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ILLUMINANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ILLUMINANCE_RANGES),"ILLUMINANCE_RANGES");          
	            contentObject.put("ILLUMINANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)),"ILLUMINANCE_TEXT");          
	            contentObject.put("ILLUMINANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.BIT_RATE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BIT_RATE_RANGES),"BITRATE_RANGES");          
	            contentObject.put("BITRATE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)),"BITRATE_TEXT");          
	            contentObject.put("BITRATE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_DENSITY_RANGES),"MASSDENSITY_RANGES");          
	            contentObject.put("MASSDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)),"MASSDENSITY_TEXT");          
	            contentObject.put("MASSDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_RANGES),"MASSFLOWRATE_RANGES");          
	            contentObject.put("MASSFLOWRATE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)),"MASSFLOWRATE_TEXT");          
	            contentObject.put("MASSFLOWRATE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FORCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FORCE_RANGES),"FORCE_RANGES");          
	            contentObject.put("FORCE_RANGES".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.FORCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.FORCE_TEXT)),"FORCE_TEXT");          
	            contentObject.put("FORCE_TEXT".toLowerCase(),elementArrayObject);
            }
        
            if(rec.getStrings(EVCombinedRec.TORQUE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TORQUE_RANGES),"TORQUE_RANGES");          
	            contentObject.put("TORQUE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TORQUE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TORQUE_TEXT)),"TORQUE_TEXT");          
	            contentObject.put("TORQUE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PRESSURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRESSURE_RANGES),"PRESSURE_RANGES");          
	            contentObject.put("PRESSURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)!=null)
			{
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)),"PRESSURE_TEXT");          
	            contentObject.put("PRESSURE_TEXT".toLowerCase(),elementArrayObject);
			}
			
            if(rec.getStrings(EVCombinedRec.AREA_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AREA_RANGES),"AREA_RANGES");          
	            contentObject.put("AREA_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AREA_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AREA_TEXT)),"AREA_TEXT");          
	            contentObject.put("AREA_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_RANGES),"VOLUME_RANGES");          
	            contentObject.put("VOLUME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLUME_TEXT)),"VOLUME_TEXT");          
	            contentObject.put("VOLUME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VELOCITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VELOCITY_RANGES),"VELOCITY_RANGES");          
	            contentObject.put("VELOCITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)),"VELOCITY_TEXT");          
	            contentObject.put("VELOCITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ACCELERATION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ACCELERATION_RANGES),"ACCELERATION_RANGES");          
	            contentObject.put("ACCELERATION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)),"ACCELERATION_TEXT");          
	            contentObject.put("ACCELERATION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_RANGES),"ANGULARVELOCITY_RANGES");          
	            contentObject.put("ANGULARVELOCITY_RANGES".toLowerCase(),elementArrayObject);
            }
            

            if(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)),"ANGULARVELOCITY_TEXT");          
	            contentObject.put("ANGULARVELOCITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_RANGES),"ROTATIONALSPEED_RANGES");          
	            contentObject.put("ROTATIONALSPEED_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)),"ROTATIONALSPEED_TEXT");          
	            contentObject.put("ROTATIONALSPEED_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGE_RANGES),"AGE_RANGES");          
	            contentObject.put("AGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AGE_TEXT)),"AGE_TEXT");          
	            contentObject.put("AGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_MASS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_MASS_RANGES),"MOLARMASS_RANGES");          
	            contentObject.put("MOLARMASS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)),"MOLARMASS_TEXT");          
	            contentObject.put("MOLARMASS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES),"MOLALITYOFSUBSTANCE_RANGES");          
	            contentObject.put("MOLALITYOFSUBSTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)),"MOLALITYOFSUBSTANCE_TEXT");          
	            contentObject.put("MOLALITYOFSUBSTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIOACTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIOACTIVITY_RANGES),"RADIOACTIVITY_RANGES");          
	            contentObject.put("RADIOACTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)),"RADIOACTIVITY_TEXT");          
	            contentObject.put("RADIOACTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_RANGES),"ABSORBEDDOSE_RANGES");          
	            contentObject.put("ABSORBEDDOSE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)),"ABSORBEDDOSE_TEXT");          
	            contentObject.put("ABSORBEDDOSE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_RANGES),"RADIATIONEXPOSURE_RANGES");          
	            contentObject.put("RADIATIONEXPOSURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)),"RADIATIONEXPOSURE_TEXT");          
	            contentObject.put("RADIATIONEXPOSURE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINANCE_RANGES),"LUMINANCE_RANGES");          
	            contentObject.put("LUMINANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)),"LUMINANCE_TEXT");          
	            contentObject.put("LUMINANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES),"MAGNETICFIELDSTRENGTH_RANGES");          
	            contentObject.put("MAGNETICFIELDSTRENGTH_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)),"MAGNETICFIELDSTRENGTH_TEXT");          
	            contentObject.put("MAGNETICFIELDSTRENGTH_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES),"SPECTRALEFFICIENCY_RANGES");          
	            contentObject.put("SPECTRALEFFICIENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)),"SPECTRALEFFICIENCY_TEXT");          
	            contentObject.put("SPECTRALEFFICIENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES),"SURFACEPOWERDENSITY_RANGES");          
	            contentObject.put("SURFACEPOWERDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)),"SURFACEPOWERDENSITY_TEXT");          
	            contentObject.put("SURFACEPOWERDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)),"THERMALCONDUCTIVITY_RANGES");          
	            contentObject.put("THERMALCONDUCTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)),"THERMALCONDUCTIVITY_TEXT");          
	            contentObject.put("THERMALCONDUCTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES),"DECIBELISOTROPIC_RANGES");          
	            contentObject.put("DECIBELISOTROPIC_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)),"DECIBELISOTROPIC_TEXT");          
	            contentObject.put("DECIBELISOTROPIC_TEXT".toLowerCase(),elementArrayObject);
            }
            

            if(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES),"DECIBELMILLIWATTS_RANGES");          
	            contentObject.put("DECIBELMILLIWATTS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)),"DECIBELMILLIWATTS_TEXT");          
	            contentObject.put("DECIBELMILLIWATTS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_RANGES)),"EQUIVALENTDOSE_RANGES");          
	            contentObject.put("EQUIVALENTDOSE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)),"EQUIVALENTDOSE_TEXT");          
	            contentObject.put("EQUIVALENTDOSE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_RANGES),"MOLARCONCENTRATION_RANGES");          
	            contentObject.put("MOLARCONCENTRATION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)),"MOLARCONCENTRATION_TEXT");          
	            contentObject.put("MOLARCONCENTRATION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_RANGES),"LINEARDENSITY_RANGES");          
	            contentObject.put("LINEARDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)),"LINEARDENSITY_TEXT");          
	            contentObject.put("LINEARDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES),"LUMINOUSEFFICIENCY_RANGES");          
	            contentObject.put("LUMINOUSEFFICIENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)),"LUMINOUSEFFICIENCY_TEXT");          
	            contentObject.put("LUMINOUSEFFICIENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_RANGES),"LUMINOUSEFFICACY_RANGES");          
	            contentObject.put("LUMINOUSEFFICACY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)),"LUMINOUSEFFICACY_TEXT");          
	            contentObject.put("LUMINOUSEFFICACY_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_RANGES),"SPECIFICENERGY_RANGES");          
	            contentObject.put("SPECIFICENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)),"SPECIFICENERGY_TEXT");          
	            contentObject.put("SPECIFICENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES),"SPECIFICSURFACEAREA_RANGES");          
	            contentObject.put("SPECIFICSURFACEAREA_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)),"SPECIFICSURFACEAREA_TEXT");          
	            contentObject.put("SPECIFICSURFACEAREA_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_RANGES),"SPECIFICVOLUME_RANGES");          
	            contentObject.put("SPECIFICVOLUME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)),"SPECIFICVOLUME_TEXT");          
	            contentObject.put("SPECIFICVOLUME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_TENSION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_TENSION_RANGES),"SURFACETENSION_RANGES");          
	            contentObject.put("SURFACETENSION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)),"SURFACETENSION_TEXT");          
	            contentObject.put("SURFACETENSION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_RANGES),"SURFACEDENSITY_RANGES");          
	            contentObject.put("SURFACEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)),"SURFACEDENSITY_TEXT");          
	            contentObject.put("SURFACEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            String[] numericalUnits = rec.getStrings(EVCombinedRec.NUMERICALUNITS);
            if(numericalUnits!=null && numericalUnits.length>0)
            {
	            elementArrayObject = formJsonArray(numericalUnits,"NUMERICAL_UNITS");          
	            contentObject.put("NUMERICAL_UNITS".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.EID)!=null && rec.getString(EVCombinedRec.EID).length()>0)
            {
	            contentObject.put("EID".toLowerCase(),notNull(rec.getString(EVCombinedRec.EID)));
            }

            String[] departmentID=rec.getStrings(EVCombinedRec.DEPARTMENTID);
            if(departmentID!=null && departmentID.length>0 && departmentID[0]!=null)
            {
            	//System.out.println("departmentID="+departmentID[0]);
	            elementArrayObject = formJsonArray(departmentID,"DEPARTMENTID");          
	            contentObject.put("DEPARTMENTID".toLowerCase(),elementArrayObject);
            }

            String[] titleOfCollection=rec.getStrings(EVCombinedRec.TITLE_OF_COLLECTION);
            if(titleOfCollection!=null && titleOfCollection.length>0)
            {
	            elementArrayObject = formJsonArray(titleOfCollection,"TITLEOFCOLLECTION");          
	            contentObject.put("TITLEOFCOLLECTION".toLowerCase(),elementArrayObject);
            }

            String[]university=rec.getStrings(EVCombinedRec.UNIVERSITY);
            if(university!=null && university.length>0)
            {
	            elementArrayObject = formJsonArray(university,"UNIVERSITY");          
	            contentObject.put("UNIVERSITY".toLowerCase(),elementArrayObject);
            }
            
            String[] typeOfDegree=rec.getStrings(EVCombinedRec.TYPE_OF_DEGREE);
            if(typeOfDegree!=null && typeOfDegree.length>0)
            {
	            elementArrayObject = formJsonArray(typeOfDegree,"TYPEOFDEGREE");          
	            contentObject.put("TYPEOFDEGREE".toLowerCase(),elementArrayObject);
            }
            
            String[] annotation=rec.getStrings(EVCombinedRec.ANNOTATION);
            if(annotation!=null && annotation.length>0)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANNOTATION),"ANNOTATION");          
	            contentObject.put("ANNOTATION".toLowerCase(),elementArrayObject);
            }
            
            String[] mapScale=rec.getStrings(EVCombinedRec.MAP_SCALE);
            if(mapScale!=null && mapScale.length>0)
            {
	            elementArrayObject = formJsonArray(mapScale,"MAPSCALE");          
	            contentObject.put("MAPSCALE".toLowerCase(),elementArrayObject);
            }
            
            String[] mapType=rec.getStrings(EVCombinedRec.MAP_TYPE);
            if(mapType!=null && mapType.length>0)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAP_TYPE),"MAPTYPE");          
	            contentObject.put("MAPTYPE".toLowerCase(),elementArrayObject);
            }

            String[] sourceNote=removeExtraSpace(rec.getStrings(EVCombinedRec.SOURCE_NOTE));
            if(sourceNote!=null && sourceNote.length>0)
            {
	            elementArrayObject = formJsonArray(sourceNote,"SOURCENOTE");          
	            contentObject.put("SOURCENOTE".toLowerCase(),elementArrayObject);
            }
            
            String[] grantIDs=removeExtraSpace(rec.getStrings(EVCombinedRec.GRANTID));           
            if(grantIDs!=null && grantIDs.length>0 && grantIDs[0]!=null)
            {
	            elementArrayObject = formJsonArray(grantIDs,"GRANTID");          
	            contentObject.put("GRANTID".toLowerCase(),elementArrayObject);
            }
                      
            
            String[] grantAgency=removeExtraSpace(rec.getStrings(EVCombinedRec.GRANTAGENCY));
            if(grantAgency!=null && grantAgency.length>0 && grantAgency[0]!=null)
            {
	            elementArrayObject = formJsonArray(grantAgency,"GRANTAGENCY");          
	            contentObject.put("GRANTAGENCY".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
          
            String[] sourceBibText=removeExtraSpace(rec.getStrings(EVCombinedRec.SOURCEBIBTEXT));
            if(sourceBibText!=null && sourceBibText.length>0)
            {           	
	            elementArrayObject = formJsonArray(sourceBibText,"SOURCEBIBTEXT");          
	            contentObject.put("SOURCEBIBTEXT".toLowerCase(),elementArrayObject);
            }
            

            String standardID=rec.getString(EVCombinedRec.STANDARDID);
            if(standardID!=null && standardID.length()>0)
            {		          
	            contentObject.put("STANDARDID".toLowerCase(),rec.getString(EVCombinedRec.STANDARDID));
            }
          
            String[] orgID=rec.getStrings(EVCombinedRec.ORG_ID);
            if(orgID!=null && orgID.length>0)
            {
	            elementArrayObject = formJsonArray(orgID,"ORG_ID");          
	            contentObject.put("ORG_ID".toLowerCase(),elementArrayObject);
            }
           
            //added by hmo @12-09-2020
            //String freetoreadstatus=rec.getString(EVCombinedRec.FREETOREADSTATUS);
            String isopenaccess=rec.getString(EVCombinedRec.ISOPENACESS);
            String openaccess="";
            if(isopenaccess!=null)
            {
            	openaccess=isopenaccess;
            }
            
            //remove on 1/21/2021 by hmo
            /*
            if(freetoreadstatus!=null)
            {
            	openaccess=openaccess+" "+freetoreadstatus;
            }
            */
            
            if(openaccess!=null && openaccess.length()>0)
            {
            	String[] openaccessArray=openaccess.split(" ", -1);
            	elementArrayObject = formJsonArray(openaccessArray,"ISOPENACESS");    
	            contentObject.put("ISOPENACESS".toLowerCase(),elementArrayObject);	
	            //System.out.println(" in 2 "+openaccess+" length="+openaccess.length());
            }
 
            String[] grantText=rec.getStrings(EVCombinedRec.GRANTTEXT); 
            //System.out.println("GRANTTEXT="+Arrays.toString(grantText));
           
            if(grantText!=null && grantText.length>0 && grantText[0]!=null)
            {          	
	            //elementArrayObject = formJsonArray(combine(grantIDs,grantAgency,grantText),"GRANTTEXT");  
            	elementArrayObject = formJsonArray(removeExtraSpace(grantText),"GRANTTEXT");      
	            contentObject.put("GRANTTEXT".toLowerCase(),elementArrayObject);	          
            }

            //STANDARDDESIGNATION
            if(rec.getString(EVCombinedRec.STANDARDDESIGNATION)!=null && rec.getString(EVCombinedRec.STANDARDDESIGNATION).length()>0)
            {           	     
 	            contentObject.put("STANDARDDESIGNATION".toLowerCase(),rec.getString(EVCombinedRec.STANDARDDESIGNATION));       
            }
            
            //SOURCE TYPE
            if(rec.getString(EVCombinedRec.SOURCE_TYPE)!=null && rec.getString(EVCombinedRec.SOURCE_TYPE).length()>0)
            {          	
 	            contentObject.put("SOURCETYPE".toLowerCase(),rec.getString(EVCombinedRec.SOURCE_TYPE));       
            }                           
            
           //PROCESSINFO
            if(rec.getString(EVCombinedRec.PROCESS_INFO)!=null && rec.getString(EVCombinedRec.PROCESS_INFO).length()>0)
            {          	
 	            contentObject.put("PROCESSINFO".toLowerCase(),rec.getString(EVCombinedRec.PROCESS_INFO));       
            } 
            
            //VIDEO_PUBLISHERNAME
            //System.out.println("DOI="+rec.getString(EVCombinedRec.LINK_DOI));
            if(rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME)!=null && rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME).length()>0)
            {          	
 	            contentObject.put("VIDEOPUBLISHERNAME".toLowerCase(),rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME));       
            } 
            
            //VIDEOLOCATION
            //System.out.println("VIDEO_LOCATION="+rec.getString(EVCombinedRec.VIDEO_LOCATION));
            if(rec.getString(EVCombinedRec.VIDEO_LOCATION)!=null && rec.getString(EVCombinedRec.VIDEO_LOCATION).length()>0)
            {          	
 	            contentObject.put("VIDEOLOCATION".toLowerCase(),rec.getString(EVCombinedRec.VIDEO_LOCATION));       
            }
            
            //CONFSERIESID
            //System.out.println("CONFSERIESID="+rec.getString(EVCombinedRec.CONFSERIESID));
            if(rec.getString(EVCombinedRec.CONFSERIESID)!=null && rec.getString(EVCombinedRec.CONFSERIESID).length()>0)
            {          	
 	            contentObject.put("CONFSERIESID".toLowerCase(),rec.getString(EVCombinedRec.CONFSERIESID));       
            }
            
            //CONFSERIESNAME
            //System.out.println("CONFSERIESNAME="+rec.getString(EVCombinedRec.CONFSERIESNAME));
            if(rec.getString(EVCombinedRec.CONFSERIALNAME)!=null && rec.getString(EVCombinedRec.CONFSERIALNAME).length()>0)
            {          	
 	            contentObject.put("CONFERENCESERIALTITLE".toLowerCase(),rec.getString(EVCombinedRec.CONFSERIALNAME));       
            }
            
            //CONFSERIALACRONYM
            //System.out.println("CONFSERIALACRONYM="+rec.getString(EVCombinedRec.CONFSERIALACRONYM));
            if(rec.getString(EVCombinedRec.CONFSERIALACRONYM)!=null && rec.getString(EVCombinedRec.CONFSERIALACRONYM).length()>0)
            {            	
 	            contentObject.put("CONFERENCEACR".toLowerCase(),rec.getString(EVCombinedRec.CONFSERIALACRONYM));       
            }
            
            //output regular format
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            
        	JsonParser jp = new JsonParser();
        	//JsonElement je = jp.parse(evo.build().toString());
        	JsonElement je = jp.parse(contentObject.toString());
        	 
        	String prettyJsonString = gson.toJson(je);
        	
        	batchData.put(eid,prettyJsonString);//run batchdata
        	//System.out.println(eid+"\t"+prettyJsonString);
        	//missedData.put(eid,prettyJsonString);//run batchdata
        	
        	//Send it to kafka server
        	//System.out.println(contentObject.toString());
            //kafka.runProducer(prettyJsonString,"\""+eid+"\"",false);
        	//System.out.println("The Thread1 name is " + Thread.currentThread().getName());
        	//MessageSender sendMessage= new MessageSender(kafka,eid,prettyJsonString);
            //pool.execute(sendMessage); 
        	//Thread thread = new Thread(sendMessage);
            //thread.sleep(1);
            //thread.start();  
            //System.out.println("The Thread2 name is " + Thread.currentThread().getName());
        	//print it out as file
        	//outputAsJsonFile(prettyJsonString,eid,getDatabase(),getLoadnumber());
          
    }
    
    public void writeRec(EVCombinedRec rec)
            throws Exception
        {
    		StringBuffer recordBuffer = new StringBuffer();
            setDatabase(rec.getString(EVCombinedRec.DATABASE));
            setPui(rec.getString(EVCombinedRec.PUI));
            setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
            String loadnumber = rec.getString(EVCombinedRec.LOAD_NUMBER);
            if(loadnumber !=null && loadnumber.length()>6)
            {
            	loadnumber = loadnumber.substring(0,6);
            }
            
            String eid = rec.getString(EVCombinedRec.DOCID);
            
            //begin(eid);
            
            JSONObject  elementObject = new JSONObject();
            JSONObject  contentObject = new JSONObject();
            JSONArray  	elementArrayObject = new JSONArray();;
            JSONArray 	elementJsonArray = new JSONArray();
            JSONArray 	contentJsonArray = new JSONArray();            
           
            contentObject.put("EIDOCID".toLowerCase(),eid);
        
            if(rec.getString(EVCombinedRec.PUI)!=null && rec.getString(EVCombinedRec.PUI).length()>0)
            {
            	contentObject.put("PUI".toLowerCase(),rec.getString(EVCombinedRec.PUI));
            }
            
            if(rec.getString(EVCombinedRec.PARENT_ID)!=null && rec.getString(EVCombinedRec.PARENT_ID).length()>0)
            {            	
            	contentObject.put("PARENTID".toLowerCase(),rec.getString(EVCombinedRec.PARENT_ID));
            }
         
            if(rec.getString(EVCombinedRec.DEDUPKEY)!=null && rec.getString(EVCombinedRec.DEDUPKEY).length()>0)
            {	            
            	contentObject.put("DEDUPKEY".toLowerCase(),rec.getString(EVCombinedRec.DEDUPKEY));
            }
            
            contentObject.put(("database").toLowerCase(),rec.getString(EVCombinedRec.DATABASE));
            
            if(loadnumber.length()>6)
            {
            	contentObject.put("LOADNUMBER".toLowerCase(),loadnumber.substring(0,6));
            }
            else
            {
            	contentObject.put("LOADNUMBER".toLowerCase(),loadnumber);
            }

            if(rec.getString(EVCombinedRec.UPDATE_NUMBER)!=null && rec.getString(EVCombinedRec.UPDATE_NUMBER).length()>0)
            {
            	contentObject.put("UPDATENUMBER".toLowerCase(),rec.getString(EVCombinedRec.UPDATE_NUMBER));
            }

            if(rec.getString(EVCombinedRec.DATESORT)!=null && rec.getString(EVCombinedRec.DATESORT).length()>0)
            {
            	contentObject.put("DATESORT".toLowerCase(),rec.getString(EVCombinedRec.DATESORT));
            }

            if(rec.getString(EVCombinedRec.PUB_YEAR)!=null && rec.getString(EVCombinedRec.PUB_YEAR).length()>0)
            {
            	contentObject.put("PUBYEAR".toLowerCase(),rec.getString(EVCombinedRec.PUB_YEAR));
            }
                   
            contentObject.put("ACCESSIONNUMBER".toLowerCase(),rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            
            String[] author=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.AUTHOR)));
            if(author!=null && author.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(author),"AUTHOR");          
            	contentObject.put("AUTHOR".toLowerCase(),elementArrayObject);
            	String firstAuthor=rec.getString(EVCombinedRec.FIRST_AUTHOR);
                if(firstAuthor!=null && firstAuthor.length()>0)
                {     
     	            contentObject.put("FIRSTAUTHOR".toLowerCase(),removeSpace(firstAuthor));                     
                }
            }
            
            String[] authorID=rec.getStrings(EVCombinedRec.AUTHORID);
            //System.out.println("AUTHORID="+Arrays.toString(authorID));
            if(authorID!=null && authorID.length>0 )
            {
            	elementArrayObject = formJsonArray(authorID,"AUTHORID");          
            	contentObject.put("AUTHORID".toLowerCase(),elementArrayObject);
            }

            String[] authorAffiliation=removeCommaANDPeriod(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION));
            if(authorAffiliation!=null && authorAffiliation.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(authorAffiliation),"AUTHORAFFILIATION");          
            	contentObject.put("AUTHORAFFILIATION".toLowerCase(),elementArrayObject);
            }
            
            String[] affiliationLocations=rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION);
            if(affiliationLocations!=null && affiliationLocations.length>0 )
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(affiliationLocations),"AFFILIATIONLOCATION");          
            	contentObject.put("AFFILIATIONLOCATION".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.TITLE)!=null && rec.getString(EVCombinedRec.TITLE).length()>0)
            {   
            	contentObject.put("TITLE".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.TITLE)));
            }
            
            String[] translatedTitles = rec.getStrings(EVCombinedRec.TRANSLATED_TITLE);
            if(translatedTitles!=null && translatedTitles.length>0 )
            {           	
            	elementArrayObject = formJsonArray(removeExtraSpace(translatedTitles),"TRANSLATEDTITLE");          
            	contentObject.put("TRANSLATEDTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] volumeTitle=rec.getStrings(EVCombinedRec.VOLUME_TITLE);
            if(volumeTitle!=null && volumeTitle.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(volumeTitle),"VOLUMETITLE");          
	            contentObject.put("VOLUMETITLE".toLowerCase(),elementArrayObject);
            }
          
            if(rec.getString(EVCombinedRec.ABSTRACT)!=null && rec.getString(EVCombinedRec.ABSTRACT).trim().length()>0)
            {           	
	            contentObject.put("ABSTRACT".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.ABSTRACT)));	           
            }
           
            //remove OTHERABSTRACT from KY per EVOPS-1195 by hmo at 09/16/2021
            /*           
            if(rec.getString(EVCombinedRec.OTHER_ABSTRACT)!=null && rec.getString(EVCombinedRec.OTHER_ABSTRACT).trim().length()>0)
            {
            	contentObject.put("OTHERABSTRACT".toLowerCase(),rec.getString(EVCombinedRec.OTHER_ABSTRACT));           	    
            }
            */
            
            String[] editor=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.EDITOR)));
            if(editor!=null && editor.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(editor),"EDITOR");          
	            contentObject.put("EDITOR".toLowerCase(),elementArrayObject);	            
            }
            
            String[] editorAffiliation=removeCommaANDPeriod(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION));
            if(editorAffiliation!=null && editorAffiliation.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(editorAffiliation),"EDITORAFFILIATION");          
	            contentObject.put("EDITORAFFILIATION".toLowerCase(),elementArrayObject);
            }
 
            String[] translator=removeSpace(reverseSigns(rec.getStrings(EVCombinedRec.TRANSLATOR)));
            if(translator!=null && translator.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(translator),"TRANSLATOR");          
	            contentObject.put("TRANSLATOR".toLowerCase(),elementArrayObject);
            }            
            
            String[] controlledTerms=rec.getStrings(EVCombinedRec.CONTROLLED_TERMS);
            if(controlledTerms!=null && controlledTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(controlledTerms),"CONTROLLEDTERMS");          
	            contentObject.put("CONTROLLEDTERMS".toLowerCase(),elementArrayObject);         
            }
            
            String[] uncontrolledTerms=rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS);
            if(uncontrolledTerms!=null && uncontrolledTerms.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(uncontrolledTerms),"UNCONTROLLEDTERMS");          
	            contentObject.put("UNCONTROLLEDTERMS".toLowerCase(),elementArrayObject);   
            }

            String[] issn=prepareISSN(rec.getStrings(EVCombinedRec.ISSN));
            if(issn!=null && issn.length>0 )
            {
	            elementArrayObject = formJsonArray(issn,"ISSN");          
	            contentObject.put("ISSN".toLowerCase(),elementArrayObject);
            }

            String coden=rec.getString(EVCombinedRec.CODEN);
            if(coden!=null && coden.length()>0)
            {
	            //elementArrayObject = formJsonArray(coden,"CODEN");          
	            contentObject.put("CODEN".toLowerCase(),coden);                     
            }

            String[] codenOfTranslation=rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION);
            if(codenOfTranslation!=null && codenOfTranslation.length>0 )
            {
	            elementArrayObject = formJsonArray(codenOfTranslation,"CODENOFTRANSLATION");          
	            contentObject.put("CODENOFTRANSLATION".toLowerCase(),elementArrayObject);
            }
            
            String[] isbn=prepareISBNForES(rec.getStrings(EVCombinedRec.ISBN));
            if(isbn!=null && isbn.length>0)
            {
	            elementArrayObject = formJsonArray(isbn,"ISBN");          
	            contentObject.put("ISBN".toLowerCase(),elementArrayObject);
            }

            String[] serialTitle=rec.getStrings(EVCombinedRec.SERIAL_TITLE);
            if(serialTitle!=null && serialTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(serialTitle),"SERIALTITLE");          
	            contentObject.put("SERIALTITLE".toLowerCase(),elementArrayObject);
            }

            String[] serialTitleTranslation=rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION);
            if(serialTitleTranslation!=null && serialTitleTranslation.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(serialTitleTranslation),"SERIALTITLETRANSLATION");          
	            contentObject.put("SERIALTITLETRANSLATION".toLowerCase(),elementArrayObject);
            }

            String[] mainHeading=rec.getStrings(EVCombinedRec.MAIN_HEADING);
            if(mainHeading!=null && mainHeading.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(mainHeading),"MAINHEADING");          
	            contentObject.put("MAINHEADING".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.SUB_HEADING)!=null && rec.getString(EVCombinedRec.SUB_HEADING).length()>0)
            {
	            contentObject.put("SUBHEADING".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.SUB_HEADING)));
            }

            String[] publisherName=rec.getStrings(EVCombinedRec.PUBLISHER_NAME);
            if(publisherName!=null && publisherName.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(publisherName),"PUBLISHERNAME");          
	            contentObject.put("PUBLISHERNAME".toLowerCase(),elementArrayObject);
            }

            String[] treatmentCode=rec.getStrings(EVCombinedRec.TREATMENT_CODE);
            if(treatmentCode!=null && treatmentCode.length>0)
            {
	            elementArrayObject = formJsonArray(treatmentCode,"TREATMENTCODE");          
	            contentObject.put("TREATMENTCODE".toLowerCase(),elementArrayObject);
            }
            
            String[] language=rec.getStrings(EVCombinedRec.LANGUAGE);
            if(language!=null && language.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(language),"LANGUAGE");          
	            contentObject.put("LANGUAGE".toLowerCase(),elementArrayObject);
            }

            String[] rectype=rec.getStrings(EVCombinedRec.DOCTYPE);
            if(rec.getString(EVCombinedRec.DOCTYPE)!=null && rectype.length>0)
            {
            	elementArrayObject = formJsonArray(removeExtraSpace(rectype),"RECTYPE");          
 	            contentObject.put("RECTYPE".toLowerCase(),elementArrayObject);
	            //contentObject.put("RECTYPE".toLowerCase(),rec.getString(EVCombinedRec.DOCTYPE));
            }
            
            String[] classificationCode=rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE);
            if(classificationCode!=null && classificationCode.length>0)
            {
	            
	            String database=rec.getString(EVCombinedRec.DATABASE);
	            //System.out.println("DATABASE="+database);
	            if(database.equalsIgnoreCase("grf"))
	            {
	            	//elementArrayObject = formJsonArray(removeCategorySign(reverseSigns(classificationCode)),"CLASSIFICATIONCODE");
	            	elementArrayObject = formJsonArray(reverseSigns(classificationCode),"CLASSIFICATIONCODE");
	            	contentObject.put("CATEGORY".toLowerCase(),elementArrayObject);
	            }
	            else
	            {
	            	elementArrayObject = formJsonArray(reverseSigns(classificationCode),"CLASSIFICATIONCODE");
	            	contentObject.put("CLASSIFICATIONCODE".toLowerCase(),elementArrayObject);
	            }
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_CODE)!=null && rec.getString(EVCombinedRec.CONFERENCE_CODE).length()>0)
            {
	            contentObject.put("CONFERENCECODE".toLowerCase(),notNull(rec.getString(EVCombinedRec.CONFERENCE_CODE)));
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_NAME)!=null && rec.getString(EVCombinedRec.CONFERENCE_NAME).length()>0)
            {
	            contentObject.put("CONFERENCENAME".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.CONFERENCE_NAME)));
            }
            
            if(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)!=null && rec.getString(EVCombinedRec.CONFERENCE_LOCATION).length()>0)
            {
	            contentObject.put("CONFERENCELOCATION".toLowerCase(),removeExtraSpace(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)));
            }            
            
            if(rec.getString(EVCombinedRec.MEETING_DATE)!=null && rec.getString(EVCombinedRec.MEETING_DATE).length()>0)
            {
	            contentObject.put("MEETINGDATE".toLowerCase(),notNull(rec.getString(EVCombinedRec.MEETING_DATE)));
            }

            String[] sponsorName=rec.getStrings(EVCombinedRec.SPONSOR_NAME);
            if(sponsorName!=null && sponsorName.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(sponsorName),"SPONSORNAME");          
	            contentObject.put("SPONSORNAME".toLowerCase(),elementArrayObject);
            }
           
            String[] monoGraphTitle=rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE);
            if(monoGraphTitle!=null && monoGraphTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(monoGraphTitle),"MONOGRAPHTITLE");          
	            contentObject.put("MONOGRAPHTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] discipline=rec.getStrings(EVCombinedRec.DISCIPLINE);
            if(discipline!=null && discipline.length>0)
            {
	            elementArrayObject = formJsonArray(discipline,"DISCIPLINE");          
	            contentObject.put("DISCIPLINE".toLowerCase(),elementArrayObject);
            }
            
            String[] materialNumber=rec.getStrings(EVCombinedRec.MATERIAL_NUMBER);
            if(materialNumber!=null && materialNumber.length>0)
            {
	            elementArrayObject = formJsonArray(materialNumber,"MATERIALNUMBER");          
	            contentObject.put("MATERIALNUMBER".toLowerCase(),elementArrayObject);
            }

            String[] numericalIndexing=rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING);
            if(numericalIndexing!=null && numericalIndexing.length>0 )
            {
	            elementArrayObject = formJsonArray(numericalIndexing,"NUMERICALINDEXING");          
	            contentObject.put("NUMERICALINDEXING".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalIndexing=rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING);
            if(chemicalIndexing!=null && chemicalIndexing.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalIndexing),"CHEMICALINDEXING");          
	            contentObject.put("CHEMICALINDEXING".toLowerCase(),elementArrayObject);
            }

            String[] astronomicalIndexing=rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING);
            if(astronomicalIndexing!=null && astronomicalIndexing.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(astronomicalIndexing),"ASTRONOMICALINDEXING");          
	            contentObject.put("ASTRONOMICALINDEXING".toLowerCase(),elementArrayObject);
            }
            
            String[] reportNumber=rec.getStrings(EVCombinedRec.REPORTNUMBER);
            if(reportNumber!=null && reportNumber.length>0)
            {
	            elementArrayObject = formJsonArray(reportNumber,"REPORTNUMBER");          
	            contentObject.put("REPORTNUMBER".toLowerCase(),elementArrayObject);
            }

            String[] ordernumber=rec.getStrings(EVCombinedRec.ORDERNUMBER);
            if(ordernumber!=null && ordernumber.length>0)
            {
	            elementArrayObject = formJsonArray(ordernumber,"ORDERNUMBER");          
	            contentObject.put("ORDERNUMBER".toLowerCase(),elementArrayObject);     
            }
            
            String[] countries=rec.getStrings(EVCombinedRec.COUNTRY);
            if(countries!=null && countries.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(countries),"COUNTRY");          
	            contentObject.put("COUNTRY".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.VOLUME)!=null && rec.getString(EVCombinedRec.VOLUME).length()>0)
            {
	            contentObject.put("VOLUME".toLowerCase(),notNull(rec.getString(EVCombinedRec.VOLUME)));
            }
            
            if(rec.getString(EVCombinedRec.ISSUE)!=null && rec.getString(EVCombinedRec.ISSUE).length()>0)
            {
	            contentObject.put("ISSUE".toLowerCase(),notNull(rec.getString(EVCombinedRec.ISSUE)));
            }
            
            if(rec.getString(EVCombinedRec.STARTPAGE)!=null && rec.getString(EVCombinedRec.STARTPAGE).length()>0)
            {
	            contentObject.put("STARTPAGE".toLowerCase(),notNull(rec.getString(EVCombinedRec.STARTPAGE)));
            }
            
            if(rec.getString(EVCombinedRec.PAGE)!=null && rec.getString(EVCombinedRec.PAGE).length()>0)
            {
	            contentObject.put("PAGE".toLowerCase(),notNull(rec.getString(EVCombinedRec.PAGE)));
            }
            
            if(rec.getString(EVCombinedRec.AVAILABILITY)!=null && rec.getString(EVCombinedRec.AVAILABILITY).length()>0)
            {
	            contentObject.put("AVAILABILITY".toLowerCase(),rec.getString(EVCombinedRec.AVAILABILITY));
            }
            
            String[] notes=rec.getStrings(EVCombinedRec.NOTES);
            if(notes!=null && notes.length>0 )
            {
	            elementArrayObject = formJsonArray(notes,"NOTES");          
	            contentObject.put("NOTES".toLowerCase(),elementArrayObject);
            }
            
            String[] patentAppDate=rec.getStrings(EVCombinedRec.PATENTAPPDATE);
            if(patentAppDate!=null && patentAppDate.length>0 )
            {
	            elementArrayObject = formJsonArray(patentAppDate,"PATENTAPPDATE");          
	            contentObject.put("PATENTAPPDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] patentIssueDate=rec.getStrings(EVCombinedRec.PATENTISSUEDATE);
            if(patentIssueDate!=null && patentIssueDate.length>0)
            {
	            elementArrayObject = formJsonArray(patentIssueDate,"PATENTISSUEDATE");          
	            contentObject.put("PATENTISSUEDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] companies=rec.getStrings(EVCombinedRec.COMPANIES);
            if(companies!=null && companies.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(companies),"COMPANIES");          
	            contentObject.put("COMPANIES".toLowerCase(),elementArrayObject);
            }
            
            String[] casRegistryNumber=rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER);
            if(casRegistryNumber!=null && casRegistryNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(casRegistryNumber,"CASREGISTRYNUMBER");          
	            contentObject.put("CASREGISTRYNUMBER".toLowerCase(),elementArrayObject);
            }
           
            String[] businessTerms=rec.getStrings(EVCombinedRec.BUSINESSTERMS);
            if(businessTerms!=null && businessTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(businessTerms),"BUSINESSTERMS");          
	            contentObject.put("BUSINESSTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalTerms=rec.getStrings(EVCombinedRec.CHEMICALTERMS);
            if(chemicalTerms!=null && chemicalTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalTerms),"CHEMICALTERMS");          
	            contentObject.put("CHEMICALTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] chemicalAcronyms=rec.getStrings(EVCombinedRec.CHEMICALACRONYMS);
            if(chemicalAcronyms!=null && chemicalAcronyms.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(chemicalAcronyms),"CHEMAC");          
	            contentObject.put("CHEMAC".toLowerCase(),elementArrayObject);
            }
            
            String[] patentNumber=rec.getStrings(EVCombinedRec.PATENT_NUMBER);
            if(patentNumber!=null && patentNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(patentNumber,"SIC");          
	            contentObject.put("SIC".toLowerCase(),elementArrayObject);
            }

            String[] industrialcodes=rec.getStrings(EVCombinedRec.INDUSTRIALCODES);
            if(industrialcodes!=null && industrialcodes.length>0 )
            {
	            elementArrayObject = formJsonArray(industrialcodes,"INDUSTRIALCODES");          
	            contentObject.put("INDUSTRIALCODES".toLowerCase(),elementArrayObject);
            }
            
            String[] industrialsectors=rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS);
            if(industrialsectors!=null && industrialsectors.length>0 )
            {
	            elementArrayObject = formJsonArray(industrialsectors,"INDUSTRIALSECTORS");          
	            contentObject.put("INDUSTRIALSECTORS".toLowerCase(),elementArrayObject);
            }
            
            String[] scope=rec.getStrings(EVCombinedRec.SCOPE);
            if(scope!=null && scope.length>0 )
            {
	            elementArrayObject = formJsonArray(scope,"SCOPE");          
	            contentObject.put("SCOPE".toLowerCase(),elementArrayObject);
            }
            
            String[] agency=rec.getStrings(EVCombinedRec.AGENCY);
            if(agency!=null && agency.length>0)
            {
	            elementArrayObject = formJsonArray(agency,"AGENCY");          
	            contentObject.put("AGENCY".toLowerCase(),elementArrayObject);
	            contentJsonArray.add(contentObject);
            }
            
            String[] derwentAccessionNumber=rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER); 
            if(derwentAccessionNumber!=null && derwentAccessionNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(derwentAccessionNumber,"DERWENTACCESSIONNUMBER");          
	            contentObject.put("DERWENTACCESSIONNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] applicationNumber=rec.getStrings(EVCombinedRec.APPLICATION_NUMBER);
            if(applicationNumber!=null && applicationNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(applicationNumber,"APPLICATIONNUMBER");          
	            contentObject.put("APPLICATIONNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] applicationCountry=rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY);
            if(applicationCountry!=null && applicationCountry.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(applicationCountry),"APPLICATIONCOUNTRY");          
	            contentObject.put("APPLICATIONCOUNTRY".toLowerCase(),elementArrayObject);
            }
            
            //String[] intPatentClassification=addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION");
            String[] intPatentClassification=rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION);
            if(intPatentClassification!=null && intPatentClassification.length>0 )
            {
	            elementArrayObject = formJsonArray(reverseSigns(intPatentClassification),"INTPATENTCLASSIFICATION");          
	            contentObject.put("INTPATENTCLASSIFICATION".toLowerCase(),elementArrayObject);
            }
            
            String[] linkedTerms=stripChar29(rec.getStrings(EVCombinedRec.LINKED_TERMS));
            if(linkedTerms!=null && linkedTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(linkedTerms),"LINKEDTERMS");          
	            contentObject.put("LINKEDTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] entryYear=rec.getStrings(EVCombinedRec.ENTRY_YEAR);
            if(entryYear!=null && entryYear.length>0 )
            {
	            elementArrayObject = formJsonArray(entryYear,"ENTRYYEAR");          
	            contentObject.put("ENTRYYEAR".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityNumber=rec.getStrings(EVCombinedRec.PRIORITY_NUMBER);
            if(priorityNumber!=null && priorityNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityNumber,"PRIORITYNUMBER");          
	            contentObject.put("PRIORITYNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityDate=rec.getStrings(EVCombinedRec.PRIORITY_DATE);
            if(priorityDate!=null && priorityDate.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityDate,"PRIORITYDATE");          
	            contentObject.put("PRIORITYDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityCountry=rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY);
            if(priorityCountry!=null && priorityCountry.length>0 )
            {
	            elementArrayObject = formJsonArray(priorityCountry,"PRIORITYCOUNTRY");          
	            contentObject.put("PRIORITYCOUNTRY".toLowerCase(),elementArrayObject);
            }
            
            String[] source=rec.getStrings(EVCombinedRec.SOURCE);
            if(source!=null && source.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(source),"SOURCE");          
	            contentObject.put("SOURCE".toLowerCase(),elementArrayObject);
            }
            
            String[] secondarySrcTitle=rec.getStrings(EVCombinedRec.SECONDARY_SRC_TITLE);   
            if(secondarySrcTitle!=null && secondarySrcTitle.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(secondarySrcTitle),"SECONDARYSRCTITLE");          
	            contentObject.put("SECONDARYSRCTITLE".toLowerCase(),elementArrayObject); 
            }

            String[] mainTerm=rec.getStrings(EVCombinedRec.MAIN_TERM);
            if(mainTerm!=null && mainTerm.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(mainTerm),"MAINTERM");          
	            contentObject.put("MAINTERM".toLowerCase(),elementArrayObject);
            }
            
            String[] abbrvSrcTitle=rec.getStrings(EVCombinedRec.ABBRV_SRC_TITLE);
            if(abbrvSrcTitle!=null && abbrvSrcTitle.length>0  )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(abbrvSrcTitle),"ABBRVSRCTITLE");          
	            contentObject.put("ABBRVSRCTITLE".toLowerCase(),elementArrayObject);
            }
            
            String[] noroleTerms=rec.getStrings(EVCombinedRec.NOROLE_TERMS);
            if(noroleTerms!=null && noroleTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(noroleTerms,"NOROLETERMS");          
	            contentObject.put("NOROLETERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] reagentTerms=rec.getStrings(EVCombinedRec.REAGENT_TERMS);
            if(reagentTerms!=null && reagentTerms.length>0  )
            {
	            elementArrayObject = formJsonArray(reagentTerms,"REAGENTTERMS");          
	            contentObject.put("REAGENTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] productTerms=rec.getStrings(EVCombinedRec.PRODUCT_TERMS);
            if(productTerms!=null && productTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(productTerms),"PRODUCTTERMS");          
	            contentObject.put("PRODUCTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorNoRoleTerms=rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS);
            if(majorNoRoleTerms!=null && majorNoRoleTerms.length>0 )
            {
	            elementArrayObject = formJsonArray(majorNoRoleTerms,"MAJORNOROLETERMS");          
	            contentObject.put("MAJORNOROLETERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorReagentTerms=rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS);
            if(majorReagentTerms!=null && majorReagentTerms.length>0)
            {
	            elementArrayObject = formJsonArray(majorReagentTerms,"MAJORREAGENTTERMS");          
	            contentObject.put("MAJORREAGENTTERMS".toLowerCase(),elementArrayObject);
            }
            
            String[] majorProductTerm=rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS);
            if(majorProductTerm!=null && majorProductTerm.length>0 )
            {
	            elementArrayObject = formJsonArray(majorProductTerm,"MAJORPRODUCTTERMS");          
	            contentObject.put("MAJORPRODUCTTERMS".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
            
            String[] conferenceAffiliations=rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS);
            if(conferenceAffiliations!=null && conferenceAffiliations.length>0)
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(conferenceAffiliations),"CONFERENCEAFFILIATIONS");          
	            contentObject.put("CONFERENCEAFFILIATIONS".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
            
            String[] conferenceeditors=reverseSigns(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS));
            if(conferenceeditors!=null && conferenceeditors.length>0 )
            {
	            elementArrayObject = formJsonArray(removeExtraSpace(conferenceeditors),"CONFERENCEEDITORS");          
	            contentObject.put("CONFERENCEEDITORS".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceStartDate=rec.getStrings(EVCombinedRec.CONFERENCESTARTDATE);
            if(conferenceStartDate!=null && conferenceStartDate.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceStartDate,"CONFERENCESTARTDATE");          
	            contentObject.put("CONFERENCESTARTDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceEndDate=rec.getStrings(EVCombinedRec.CONFERENCEENDDATE);
            if(conferenceEndDate!=null && conferenceEndDate.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceEndDate,"CONFERENCEENDDATE");          
	            contentObject.put("CONFERENCEENDDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferencevenusite=rec.getStrings(EVCombinedRec.CONFERENCEVENUESITE);
            if(conferencevenusite!=null && conferencevenusite.length>0 )
            {
	            elementArrayObject = formJsonArray(conferencevenusite,"CONFERENCEVENUESITE");          
	            contentObject.put("CONFERENCEVENUESITE".toLowerCase(),elementArrayObject);
            }
             
            String[] conferenceCity=rec.getStrings(EVCombinedRec.CONFERENCECITY);
            if(conferenceCity!=null && conferenceCity.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceCity,"CONFERENCECITY");          
	            contentObject.put("CONFERENCECITY".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceCountryCode=rec.getStrings(EVCombinedRec.CONFERENCECOUNTRYCODE);
            if(conferenceCountryCode!=null && conferenceCountryCode.length>0 )
            {
	            elementArrayObject = formJsonArray(conferenceCountryCode,"CONFERENCECOUNTRYCODE");          
	            contentObject.put("CONFERENCECOUNTRYCODE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferencePageRange=rec.getStrings(EVCombinedRec.CONFERENCEPAGERANGE);
            if(conferencePageRange!=null && conferencePageRange.length>0)
            {
	            elementArrayObject = formJsonArray(conferencePageRange,"CONFERENCEPAGERANGE");          
	            contentObject.put("CONFERENCEPAGERANGE".toLowerCase(),elementArrayObject);
            }
            
            String[] conferenceNumberPages=rec.getStrings(EVCombinedRec.CONFERENCENUMBERPAGES);
            if(conferenceNumberPages!=null && conferenceNumberPages.length>0)
            {
	            elementArrayObject = formJsonArray(conferenceNumberPages,"CONFERENCENUMBERPAGES");          
	            contentObject.put("CONFERENCENUMBERPAGES".toLowerCase(),elementArrayObject);
            }

            String[] conferencePartNumber=rec.getStrings(EVCombinedRec.CONFERENCEPARTNUMBER);
            if(conferencePartNumber!=null && conferencePartNumber.length>0 )
            {
	            elementArrayObject = formJsonArray(conferencePartNumber,"CONFERENCEPARTNUMBER");          
	            contentObject.put("CONFERENCEPARTNUMBER".toLowerCase(),elementArrayObject);
            }
            
            String[] designatedStates=rec.getStrings(EVCombinedRec.DESIGNATED_STATES);
            if(designatedStates!=null && designatedStates.length>0 )
            {
	            elementArrayObject = formJsonArray(designatedStates,"DESIGNATEDSTATES");          
	            contentObject.put("DESIGNATEDSTATES".toLowerCase(),elementArrayObject);
            }
            
            String[] stnConference=rec.getStrings(EVCombinedRec.STN_CONFERENCE);
            if(stnConference!=null && stnConference.length>0 )
            {
	            elementArrayObject = formJsonArray(stnConference,"STNCONFERENCE");          
	            contentObject.put("STNCONFERENCE".toLowerCase(),elementArrayObject);
            }
            
            String[] stnSecondaryConference=rec.getStrings(EVCombinedRec.STN_SECONDARY_CONFERENCE);
            if(stnSecondaryConference!=null && stnSecondaryConference.length>0 && stnSecondaryConference[0]!=null)
            {
	            elementArrayObject = formJsonArray(stnSecondaryConference,"STNSECONDARYCONFERENCE");          
	            contentObject.put("STNSECONDARYCONFERENCE".toLowerCase(),elementArrayObject);
            }
            
            String[] patentFilingDate=rec.getStrings(EVCombinedRec.PATENT_FILING_DATE);
            if(patentFilingDate!=null && patentFilingDate.length>0 && patentFilingDate[0]!=null)
            {
	            elementArrayObject = formJsonArray(patentFilingDate,"FILINGDATE");          
	            contentObject.put("FILINGDATE".toLowerCase(),elementArrayObject);
            }
            
            String[] priorityKind=rec.getStrings(EVCombinedRec.PRIORITY_KIND);
            if(priorityKind!=null && priorityKind.length>0 && priorityKind[0]!=null)
            {
	            elementArrayObject = formJsonArray(priorityKind,"PRIORITYKIND");          
	            contentObject.put("PRIORITYKIND".toLowerCase(),elementArrayObject);
            }
            
            String[] eclaCode=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_CODES));
            if(eclaCode!=null && eclaCode.length>0 && eclaCode[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclaCode,"ECLACODE");          
	            contentObject.put("ECLACODE".toLowerCase(),elementArrayObject);
            }
            
            String[] attorneyName=removeSpace(rec.getStrings(EVCombinedRec.ATTORNEY_NAME));
            if(attorneyName!=null && attorneyName.length>0 && attorneyName[0]!=null)
            {
	            elementArrayObject = formJsonArray(attorneyName,"ATTORNEYNAME");          
	            contentObject.put("ATTORNEYNAME".toLowerCase(),elementArrayObject);
            }
            
            String[] primaryExaminer=rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER);
            if(primaryExaminer!=null && primaryExaminer.length>0 && primaryExaminer[0]!=null)
            {
	            elementArrayObject = formJsonArray(primaryExaminer,"PRIMARYEXAMINER");          
	            contentObject.put("PRIMARYEXAMINER".toLowerCase(),elementArrayObject);
            }

            String[] assistantExaminer=rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER);
            if(assistantExaminer!=null && assistantExaminer.length>0 && assistantExaminer[0]!=null)
            {
	            elementArrayObject = formJsonArray(assistantExaminer,"ASSISTANTEXAMINER");          
	            contentObject.put("ASSISTANTEXAMINER".toLowerCase(),elementArrayObject);
            }
            
            String[] intpatentclasses=reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES));
            if(intpatentclasses!=null && intpatentclasses.length>0 && intpatentclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(intpatentclasses,"IPCCLASS");          
	            contentObject.put("IPCCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] intpatentsubclasses=reverseSigns(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES));
            if(intpatentsubclasses!=null && intpatentsubclasses.length>0 && intpatentsubclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(intpatentsubclasses,"IPCCLASS");          
	            contentObject.put("IPCCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] eclaclasses=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_CLASSES));
            if(eclaclasses!=null && eclaclasses.length>0 && eclaclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclaclasses,"ECLACLASS");          
	            contentObject.put("ECLACLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] eclassubclasses=reverseSigns(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES));
            if(eclassubclasses!=null && eclassubclasses.length>0 && eclassubclasses[0]!=null)
            {
	            elementArrayObject = formJsonArray(eclassubclasses,"ECLASUBCLASS");          
	            contentObject.put("ECLASUBCLASS".toLowerCase(),elementArrayObject);
            }
                                
            String[] usptoclass=reverseSigns(rec.getStrings(EVCombinedRec.USPTOCLASS));
            if(usptoclass!=null && usptoclass.length>0 && usptoclass[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptoclass,"USPTOCLASS");          
	            contentObject.put("USPTOCLASS".toLowerCase(),elementArrayObject);
            }

            String[] usptosubclass=reverseSigns(rec.getStrings(EVCombinedRec.USPTOSUBCLASS));
            if(usptosubclass!=null && usptosubclass.length>0 && usptosubclass[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptosubclass,"USPTOSUBCLASS");          
	            contentObject.put("USPTOSUBCLASS".toLowerCase(),elementArrayObject);
            }
            
            String[] usptocode=reverseSigns(rec.getStrings(EVCombinedRec.USPTOCODE));
            if(usptocode!=null && usptocode.length>0 && usptocode[0]!=null)
            {
	            elementArrayObject = formJsonArray(usptocode,"USPTOCODE");          
	            contentObject.put("USPTOCODE".toLowerCase(),elementArrayObject);
            }

            String[] patentkind=rec.getStrings(EVCombinedRec.PATENT_KIND);
            if(patentkind!=null && patentkind.length>0 && patentkind[0]!=null)
            {
	            elementArrayObject = formJsonArray(patentkind,"PATENTKIND");          
	            contentObject.put("PATENTKIND".toLowerCase(),elementArrayObject);
            }
            
            String[] kinddescr=rec.getStrings(EVCombinedRec.KIND_DESCR);
            if(kinddescr!=null && kinddescr.length>0 && kinddescr[0]!=null)
            {
	            elementArrayObject = formJsonArray(kinddescr,"KINDDESCRIPTION");          
	            contentObject.put("KINDDESCRIPTION".toLowerCase(),elementArrayObject);
            }

            if(rec.getString(EVCombinedRec.AUTHORITY_CODE)!=null && rec.getString(EVCombinedRec.AUTHORITY_CODE).length()>0)
            {
	            contentObject.put("AUTHORITYCODE".toLowerCase(),notNull(rec.getString(EVCombinedRec.AUTHORITY_CODE)));
            }
            
            if(rec.getString(EVCombinedRec.PCITED)!=null && rec.getString(EVCombinedRec.PCITED).length()>0)
            {
	            contentObject.put("PCITED".toLowerCase(),notNull(rec.getString(EVCombinedRec.PCITED)));
            }

            String[] pcitedindex=rec.getStrings(EVCombinedRec.PCITEDINDEX);
            if(pcitedindex!=null && pcitedindex.length>0 && pcitedindex[0]!=null)
            {                   
	            elementArrayObject = formJsonArray(pcitedindex,"PCITEDINDEX");          
	            contentObject.put("PCITEDINDEX".toLowerCase(),elementArrayObject);
            }

            String[] prefindex=rec.getStrings(EVCombinedRec.PREFINDEX);
            if(prefindex!=null && prefindex.length>0 && prefindex[0]!=null)
			{
	            elementArrayObject = formJsonArray(prefindex,"PREFINDEX");          
	            contentObject.put("PREFINDEX".toLowerCase(),elementArrayObject);
			}
                  
            contentObject.put("DMASK".toLowerCase(),getMask(rec));
            
            
            if(rec.getString(EVCombinedRec.DOI)!=null && rec.getString(EVCombinedRec.DOI).length()>0)
            {
	            contentObject.put("DOI".toLowerCase(),rec.getString(EVCombinedRec.DOI).replaceAll(" ",""));
            }

            if(rec.getString(EVCombinedRec.SCOPUSID)!=null && rec.getString(EVCombinedRec.SCOPUSID).length()>0)
            {
	            contentObject.put("SCOPUSID".toLowerCase(),notNull(rec.getString(EVCombinedRec.SCOPUSID)));
            }
            
            String[] affiliationid=rec.getStrings(EVCombinedRec.AFFILIATIONID);
            if(affiliationid!=null && affiliationid.length>0 && affiliationid[0]!=null)
            {
	            elementArrayObject = formJsonArray(affiliationid,"AFFILIATIONID");          
	            contentObject.put("AFFILIATIONID".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.LAT_NW)!=null && rec.getString(EVCombinedRec.LAT_NW).length()>0)
            {
	            contentObject.put("LAT_NW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_NW)));
            }
            
            if(rec.getString(EVCombinedRec.LNG_NW)!=null && rec.getString(EVCombinedRec.LNG_NW).length()>0)
            {
	            contentObject.put("LNG_NW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_NW)));
            }
            
            if(rec.getString(EVCombinedRec.LAT_NE)!=null && rec.getString(EVCombinedRec.LAT_NE).length()>0)
            {
	            contentObject.put("LAT_NE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_NE)));
            }
            
            if(rec.getString(EVCombinedRec.LNG_NE)!=null && rec.getString(EVCombinedRec.LNG_NE).length()>0)
            {
	            contentObject.put("LNG_NE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_NE)));
            }
            
            if(rec.getString(EVCombinedRec.LAT_SW)!=null && rec.getString(EVCombinedRec.LAT_SW).length()>0)
            {
	            contentObject.put("LAT_SW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_SW)));
            }
            
            if(rec.getString(EVCombinedRec.LNG_SW)!=null && rec.getString(EVCombinedRec.LNG_SW).length()>0)
            {
	            contentObject.put("LNG_SW".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_SW)));
            }
            
            if(rec.getString(EVCombinedRec.LAT_SE)!=null && rec.getString(EVCombinedRec.LAT_SE).length()>0)
            {
	            contentObject.put("LAT_SE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LAT_SE)));
            }
                         
            if(rec.getString(EVCombinedRec.LNG_SE)!=null && rec.getString(EVCombinedRec.LNG_SE).length()>0)
            {
	            contentObject.put("LNG_SE".toLowerCase(),notNull(rec.getString(EVCombinedRec.LNG_SE)));
            }
            
            String[] tableofcontent=rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT);
            if(tableofcontent!=null && tableofcontent.length>0 && tableofcontent[0]!=null)
            {
	            elementArrayObject = formJsonArray(tableofcontent,"TABLEOFCONTENT");          
	            contentObject.put("TABLEOFCONTENT".toLowerCase(),elementArrayObject);
            }
            
            String firstAuthor=rec.getString(EVCombinedRec.FIRST_AUTHOR);
            if(firstAuthor!=null && firstAuthor.length()>0)
            {     
	            contentObject.put("FIRSTAUTHOR".toLowerCase(),removeSpace(firstAuthor));                     
            }
            
            //************************************************ added for numericalIndex ******************************************************//
            
        
            if(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES),"AMOUNTOFSUBSTANCE_RANGES");          
	            contentObject.put("AMOUNTOFSUBSTANCE_RANGES".toLowerCase(),elementArrayObject);	           
            }
                       
            if(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)),"AMOUNTOFSUBSTANCE_TEXT");          
	            contentObject.put("AMOUNTOFSUBSTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES),"ELECTRICCURRENT_RANGES");          
	            contentObject.put("ELECTRICCURRENT_RANGES".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)),"ELECTRICCURRENT_TEXT");          
	            contentObject.put("ELECTRICCURRENT_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_RANGES),"MASS_RANGES");          
	            contentObject.put("MASS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_TEXT)),"MASS_TEXT");          
	            contentObject.put("MASS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES),"TEMPERATURE_RANGES");          
	            contentObject.put("TEMPERATURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)),"TEMPERATURE_TEXT");          
	            contentObject.put("TEMPERATURE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TIME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_RANGES),"TIME_RANGES");          
	            contentObject.put("TIME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TIME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TIME_TEXT)),"TIME_TEXT");          
	            contentObject.put("TIME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SIZE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_RANGES),"SIZE_RANGES");          
	            contentObject.put("SIZE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SIZE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SIZE_TEXT)),"SIZE_TEXT");          
	            contentObject.put("SIZE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES),"ELECTRICALCONDUCTANCE_RANGES");          
	            contentObject.put("ELECTRICALCONDUCTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)),"ELECTRICALCONDUCTANCE_TEXT");          
	            contentObject.put("ELECTRICALCONDUCTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES),"ELECTRICALCONDUCTIVITY_RANGES");          
	            contentObject.put("ELECTRICALCONDUCTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)),"ELECTRICALCONDUCTIVITY_TEXT");          
	            contentObject.put("ELECTRICALCONDUCTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES),"VOLTAGE_RANGES");          
	            contentObject.put("VOLTAGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)),"VOLTAGE_TEXT");          
	            contentObject.put("VOLTAGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES),"ELECTRICFIELDSTRENGTH_RANGES");          
	            contentObject.put("ELECTRICFIELDSTRENGTH_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)),"ELECTRICFIELDSTRENGTH_TEXT");          
	            contentObject.put("ELECTRICFIELDSTRENGTH_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES),"CURRENTDENSITY_RANGES");          
	            contentObject.put("CURRENTDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)),"CURRENTDENSITY_TEXT");          
	            contentObject.put("CURRENTDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ENERGY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_RANGES),"ENERGY_RANGES");          
	            contentObject.put("ENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ENERGY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ENERGY_TEXT)),"ENERGY_TEXT");          
	            contentObject.put("ENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES),"ELECTRICALRESISTANCE_RANGES");          
	            contentObject.put("ELECTRICALRESISTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)),"ELECTRICALRESISTANCE_TEXT");          
	            contentObject.put("ELECTRICALRESISTANCE_TEXT".toLowerCase(),elementArrayObject);	            
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES),"ELECTRICALRESISTIVITY_RANGES");          
	            contentObject.put("ELECTRICALRESISTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
                     
            if(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)),"ELECTRICALRESISTIVITY_TEXT");          
	            contentObject.put("ELECTRICALRESISTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES),"ELECTRONVOLTENERGY_RANGES");          
	            contentObject.put("ELECTRONVOLTENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)),"ELECTRONVOLTENERGY_TEXT");          
	            contentObject.put("ELECTRONVOLTENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES),"CAPACITANCE_RANGES");          
	            contentObject.put("CAPACITANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)),"CAPACITANCE_TEXT");          
	            contentObject.put("CAPACITANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES),"FREQUENCY_RANGES");          
	            contentObject.put("FREQUENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)),"FREQUENCY_TEXT");          
	            contentObject.put("FREQUENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.POWER_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_RANGES),"POWER_RANGES");          
	            contentObject.put("POWER_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.POWER_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.POWER_TEXT)),"POWER_TEXT");          
	            contentObject.put("POWER_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES)),"APPARENTPOWER_RANGES");          
	            contentObject.put("APPARENTPOWER_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)),"APPARENTPOWER_TEXT");          
	            contentObject.put("APPARENTPOWER_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES)),"PERCENTAGE_RANGES");          
	            contentObject.put("PERCENTAGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)),"PERCENTAGE_TEXT");          
	            contentObject.put("PERCENTAGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES),"MAGNETICFLUXDENSITY_RANGES");          
	            contentObject.put("MAGNETICFLUXDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)),"MAGNETICFLUXDENSITY_TEXT");          
	            contentObject.put("MAGNETICFLUXDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
                      
            if(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES),"INDUCTANCE_RANGES");          
	            contentObject.put("INDUCTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)),"INDUCTANCE_TEXT");          
	            contentObject.put("INDUCTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES),"VOLUMECHARGEDENSITY_RANGES");          
	            contentObject.put("VOLUMECHARGEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)),"VOLUMECHARGEDENSITY_TEXT");          
	            contentObject.put("VOLUMECHARGEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES),"SURFACECHARGEDENSITY_RANGES");          
	            contentObject.put("SURFACECHARGEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)),"SURFACECHARGEDENSITY_TEXT");          
	            contentObject.put("SURFACECHARGEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_RANGES),"DECIBEL_RANGES");          
	            contentObject.put("DECIBEL_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)),"DECIBEL_TEXT");          
	            contentObject.put("DECIBEL_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_RANGES),"LUMINOUSFLUX_RANGES");          
	            contentObject.put("LUMINOUSFLUX_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)),"LUMINOUSFLUX_TEXT");          
	            contentObject.put("LUMINOUSFLUX_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ILLUMINANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ILLUMINANCE_RANGES),"ILLUMINANCE_RANGES");          
	            contentObject.put("ILLUMINANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)),"ILLUMINANCE_TEXT");          
	            contentObject.put("ILLUMINANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.BIT_RATE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BIT_RATE_RANGES),"BITRATE_RANGES");          
	            contentObject.put("BITRATE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)),"BITRATE_TEXT");          
	            contentObject.put("BITRATE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_DENSITY_RANGES),"MASSDENSITY_RANGES");          
	            contentObject.put("MASSDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)),"MASSDENSITY_TEXT");          
	            contentObject.put("MASSDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_RANGES),"MASSFLOWRATE_RANGES");          
	            contentObject.put("MASSFLOWRATE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)),"MASSFLOWRATE_TEXT");          
	            contentObject.put("MASSFLOWRATE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.FORCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FORCE_RANGES),"FORCE_RANGES");          
	            contentObject.put("FORCE_RANGES".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.FORCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.FORCE_TEXT)),"FORCE_TEXT");          
	            contentObject.put("FORCE_TEXT".toLowerCase(),elementArrayObject);
            }
        
            if(rec.getStrings(EVCombinedRec.TORQUE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TORQUE_RANGES),"TORQUE_RANGES");          
	            contentObject.put("TORQUE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.TORQUE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.TORQUE_TEXT)),"TORQUE_TEXT");          
	            contentObject.put("TORQUE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PRESSURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRESSURE_RANGES),"PRESSURE_RANGES");          
	            contentObject.put("PRESSURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)!=null)
			{
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)),"PRESSURE_TEXT");          
	            contentObject.put("PRESSURE_TEXT".toLowerCase(),elementArrayObject);
			}
			
            if(rec.getStrings(EVCombinedRec.AREA_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AREA_RANGES),"AREA_RANGES");          
	            contentObject.put("AREA_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AREA_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AREA_TEXT)),"AREA_TEXT");          
	            contentObject.put("AREA_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_RANGES),"VOLUME_RANGES");          
	            contentObject.put("VOLUME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VOLUME_TEXT)),"VOLUME_TEXT");          
	            contentObject.put("VOLUME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VELOCITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VELOCITY_RANGES),"VELOCITY_RANGES");          
	            contentObject.put("VELOCITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)),"VELOCITY_TEXT");          
	            contentObject.put("VELOCITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ACCELERATION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ACCELERATION_RANGES),"ACCELERATION_RANGES");          
	            contentObject.put("ACCELERATION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)),"ACCELERATION_TEXT");          
	            contentObject.put("ACCELERATION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_RANGES),"ANGULARVELOCITY_RANGES");          
	            contentObject.put("ANGULARVELOCITY_RANGES".toLowerCase(),elementArrayObject);
            }
            

            if(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)),"ANGULARVELOCITY_TEXT");          
	            contentObject.put("ANGULARVELOCITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_RANGES),"ROTATIONALSPEED_RANGES");          
	            contentObject.put("ROTATIONALSPEED_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)),"ROTATIONALSPEED_TEXT");          
	            contentObject.put("ROTATIONALSPEED_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AGE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGE_RANGES),"AGE_RANGES");          
	            contentObject.put("AGE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.AGE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.AGE_TEXT)),"AGE_TEXT");          
	            contentObject.put("AGE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_MASS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_MASS_RANGES),"MOLARMASS_RANGES");          
	            contentObject.put("MOLARMASS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)),"MOLARMASS_TEXT");          
	            contentObject.put("MOLARMASS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES),"MOLALITYOFSUBSTANCE_RANGES");          
	            contentObject.put("MOLALITYOFSUBSTANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)),"MOLALITYOFSUBSTANCE_TEXT");          
	            contentObject.put("MOLALITYOFSUBSTANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIOACTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIOACTIVITY_RANGES),"RADIOACTIVITY_RANGES");          
	            contentObject.put("RADIOACTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)),"RADIOACTIVITY_TEXT");          
	            contentObject.put("RADIOACTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_RANGES),"ABSORBEDDOSE_RANGES");          
	            contentObject.put("ABSORBEDDOSE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)),"ABSORBEDDOSE_TEXT");          
	            contentObject.put("ABSORBEDDOSE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_RANGES),"RADIATIONEXPOSURE_RANGES");          
	            contentObject.put("RADIATIONEXPOSURE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)),"RADIATIONEXPOSURE_TEXT");          
	            contentObject.put("RADIATIONEXPOSURE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINANCE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINANCE_RANGES),"LUMINANCE_RANGES");          
	            contentObject.put("LUMINANCE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)),"LUMINANCE_TEXT");          
	            contentObject.put("LUMINANCE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES),"MAGNETICFIELDSTRENGTH_RANGES");          
	            contentObject.put("MAGNETICFIELDSTRENGTH_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)),"MAGNETICFIELDSTRENGTH_TEXT");          
	            contentObject.put("MAGNETICFIELDSTRENGTH_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES),"SPECTRALEFFICIENCY_RANGES");          
	            contentObject.put("SPECTRALEFFICIENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)),"SPECTRALEFFICIENCY_TEXT");          
	            contentObject.put("SPECTRALEFFICIENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES),"SURFACEPOWERDENSITY_RANGES");          
	            contentObject.put("SURFACEPOWERDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)),"SURFACEPOWERDENSITY_TEXT");          
	            contentObject.put("SURFACEPOWERDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)),"THERMALCONDUCTIVITY_RANGES");          
	            contentObject.put("THERMALCONDUCTIVITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)),"THERMALCONDUCTIVITY_TEXT");          
	            contentObject.put("THERMALCONDUCTIVITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES),"DECIBELISOTROPIC_RANGES");          
	            contentObject.put("DECIBELISOTROPIC_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)),"DECIBELISOTROPIC_TEXT");          
	            contentObject.put("DECIBELISOTROPIC_TEXT".toLowerCase(),elementArrayObject);
            }
            

            if(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES),"DECIBELMILLIWATTS_RANGES");          
	            contentObject.put("DECIBELMILLIWATTS_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)),"DECIBELMILLIWATTS_TEXT");          
	            contentObject.put("DECIBELMILLIWATTS_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_RANGES)),"EQUIVALENTDOSE_RANGES");          
	            contentObject.put("EQUIVALENTDOSE_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)),"EQUIVALENTDOSE_TEXT");          
	            contentObject.put("EQUIVALENTDOSE_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_RANGES),"MOLARCONCENTRATION_RANGES");          
	            contentObject.put("MOLARCONCENTRATION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)),"MOLARCONCENTRATION_TEXT");          
	            contentObject.put("MOLARCONCENTRATION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_RANGES),"LINEARDENSITY_RANGES");          
	            contentObject.put("LINEARDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)),"LINEARDENSITY_TEXT");          
	            contentObject.put("LINEARDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES),"LUMINOUSEFFICIENCY_RANGES");          
	            contentObject.put("LUMINOUSEFFICIENCY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)),"LUMINOUSEFFICIENCY_TEXT");          
	            contentObject.put("LUMINOUSEFFICIENCY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_RANGES),"LUMINOUSEFFICACY_RANGES");          
	            contentObject.put("LUMINOUSEFFICACY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)),"LUMINOUSEFFICACY_TEXT");          
	            contentObject.put("LUMINOUSEFFICACY_TEXT".toLowerCase(),elementArrayObject);
            }

            if(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_RANGES),"SPECIFICENERGY_RANGES");          
	            contentObject.put("SPECIFICENERGY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)),"SPECIFICENERGY_TEXT");          
	            contentObject.put("SPECIFICENERGY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES),"SPECIFICSURFACEAREA_RANGES");          
	            contentObject.put("SPECIFICSURFACEAREA_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)),"SPECIFICSURFACEAREA_TEXT");          
	            contentObject.put("SPECIFICSURFACEAREA_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_RANGES),"SPECIFICVOLUME_RANGES");          
	            contentObject.put("SPECIFICVOLUME_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)),"SPECIFICVOLUME_TEXT");          
	            contentObject.put("SPECIFICVOLUME_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_TENSION_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_TENSION_RANGES),"SURFACETENSION_RANGES");          
	            contentObject.put("SURFACETENSION_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)),"SURFACETENSION_TEXT");          
	            contentObject.put("SURFACETENSION_TEXT".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_RANGES)!=null)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_RANGES),"SURFACEDENSITY_RANGES");          
	            contentObject.put("SURFACEDENSITY_RANGES".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)!=null)
            {
	            elementArrayObject = formJsonArray(reverseSigns(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)),"SURFACEDENSITY_TEXT");          
	            contentObject.put("SURFACEDENSITY_TEXT".toLowerCase(),elementArrayObject);
            }
            
            String[] numericalUnits = rec.getStrings(EVCombinedRec.NUMERICALUNITS);
            if(numericalUnits!=null && numericalUnits.length>0)
            {
	            elementArrayObject = formJsonArray(numericalUnits,"NUMERICAL_UNITS");          
	            contentObject.put("NUMERICAL_UNITS".toLowerCase(),elementArrayObject);
            }
            
            if(rec.getString(EVCombinedRec.EID)!=null && rec.getString(EVCombinedRec.EID).length()>0)
            {
	            contentObject.put("EID".toLowerCase(),notNull(rec.getString(EVCombinedRec.EID)));
            }

            String[] departmentID=rec.getStrings(EVCombinedRec.DEPARTMENTID);
            if(departmentID!=null && departmentID.length>0 && departmentID[0]!=null)
            {
            	//System.out.println("departmentID="+departmentID[0]);
	            elementArrayObject = formJsonArray(departmentID,"DEPARTMENTID");          
	            contentObject.put("DEPARTMENTID".toLowerCase(),elementArrayObject);
            }

            String[] titleOfCollection=rec.getStrings(EVCombinedRec.TITLE_OF_COLLECTION);
            if(titleOfCollection!=null && titleOfCollection.length>0)
            {
	            elementArrayObject = formJsonArray(titleOfCollection,"TITLEOFCOLLECTION");          
	            contentObject.put("TITLEOFCOLLECTION".toLowerCase(),elementArrayObject);
            }

            String[]university=rec.getStrings(EVCombinedRec.UNIVERSITY);
            if(university!=null && university.length>0)
            {
	            elementArrayObject = formJsonArray(university,"UNIVERSITY");          
	            contentObject.put("UNIVERSITY".toLowerCase(),elementArrayObject);
            }
            
            String[] typeOfDegree=rec.getStrings(EVCombinedRec.TYPE_OF_DEGREE);
            if(typeOfDegree!=null && typeOfDegree.length>0)
            {
	            elementArrayObject = formJsonArray(typeOfDegree,"TYPEOFDEGREE");          
	            contentObject.put("TYPEOFDEGREE".toLowerCase(),elementArrayObject);
            }
            
            String[] annotation=rec.getStrings(EVCombinedRec.ANNOTATION);
            if(annotation!=null && annotation.length>0)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANNOTATION),"ANNOTATION");          
	            contentObject.put("ANNOTATION".toLowerCase(),elementArrayObject);
            }
            
            String[] mapScale=rec.getStrings(EVCombinedRec.MAP_SCALE);
            if(mapScale!=null && mapScale.length>0)
            {
	            elementArrayObject = formJsonArray(mapScale,"MAPSCALE");          
	            contentObject.put("MAPSCALE".toLowerCase(),elementArrayObject);
            }
            
            String[] mapType=rec.getStrings(EVCombinedRec.MAP_TYPE);
            if(mapType!=null && mapType.length>0)
            {
	            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAP_TYPE),"MAPTYPE");          
	            contentObject.put("MAPTYPE".toLowerCase(),elementArrayObject);
            }

            String[] sourceNote=removeExtraSpace(rec.getStrings(EVCombinedRec.SOURCE_NOTE));
            if(sourceNote!=null && sourceNote.length>0)
            {
	            elementArrayObject = formJsonArray(sourceNote,"SOURCENOTE");          
	            contentObject.put("SOURCENOTE".toLowerCase(),elementArrayObject);
            }
            
            String[] grantIDs=removeExtraSpace(rec.getStrings(EVCombinedRec.GRANTID));           
            if(grantIDs!=null && grantIDs.length>0 && grantIDs[0]!=null)
            {
	            elementArrayObject = formJsonArray(grantIDs,"GRANTID");          
	            contentObject.put("GRANTID".toLowerCase(),elementArrayObject);
            }
                      
            
            String[] grantAgency=removeExtraSpace(rec.getStrings(EVCombinedRec.GRANTAGENCY));
            if(grantAgency!=null && grantAgency.length>0 && grantAgency[0]!=null)
            {
	            elementArrayObject = formJsonArray(grantAgency,"GRANTAGENCY");          
	            contentObject.put("GRANTAGENCY".toLowerCase(),elementArrayObject);
	            //contentJsonArray.add(contentObject);
            }
          
            String[] sourceBibText=removeExtraSpace(rec.getStrings(EVCombinedRec.SOURCEBIBTEXT));
            if(sourceBibText!=null && sourceBibText.length>0)
            {           	
	            elementArrayObject = formJsonArray(sourceBibText,"SOURCEBIBTEXT");          
	            contentObject.put("SOURCEBIBTEXT".toLowerCase(),elementArrayObject);
            }
            

            String standardID=rec.getString(EVCombinedRec.STANDARDID);
            if(standardID!=null && standardID.length()>0)
            {		          
	            contentObject.put("STANDARDID".toLowerCase(),rec.getString(EVCombinedRec.STANDARDID));
            }
          
            String[] orgID=rec.getStrings(EVCombinedRec.ORG_ID);
            if(orgID!=null && orgID.length>0)
            {
	            elementArrayObject = formJsonArray(orgID,"ORG_ID");          
	            contentObject.put("ORG_ID".toLowerCase(),elementArrayObject);
            }                      
            
            String openaccess=rec.getString(EVCombinedRec.ISOPENACESS);                       
            if(openaccess!=null && openaccess.length()>0)
            {
            	String[] openaccessArray=openaccess.split(" ", -1);
            	elementArrayObject = formJsonArray(openaccessArray,"ISOPENACESS");    
	            contentObject.put("ISOPENACESS".toLowerCase(),elementArrayObject);		            
            }
 
            String[] grantText=rec.getStrings(EVCombinedRec.GRANTTEXT); 
            //System.out.println("GRANTTEXT="+Arrays.toString(grantText));
           
            if(grantText!=null && grantText.length>0 && grantText[0]!=null)
            {          	
	            //elementArrayObject = formJsonArray(combine(grantIDs,grantAgency,grantText),"GRANTTEXT");  
            	elementArrayObject = formJsonArray(removeExtraSpace(grantText),"GRANTTEXT");      
	            contentObject.put("GRANTTEXT".toLowerCase(),elementArrayObject);	          
            }

            //STANDARDDESIGNATION
            if(rec.getString(EVCombinedRec.STANDARDDESIGNATION)!=null && rec.getString(EVCombinedRec.STANDARDDESIGNATION).length()>0)
            {           	     
 	            contentObject.put("STANDARDDESIGNATION".toLowerCase(),rec.getString(EVCombinedRec.STANDARDDESIGNATION));       
            }
            
            //SOURCE TYPE
            if(rec.getString(EVCombinedRec.SOURCE_TYPE)!=null && rec.getString(EVCombinedRec.SOURCE_TYPE).length()>0)
            {          	
 	            contentObject.put("SOURCETYPE".toLowerCase(),rec.getString(EVCombinedRec.SOURCE_TYPE));       
            }                           
            
            //PROCESSINFO
            if(rec.getString(EVCombinedRec.PROCESS_INFO)!=null && rec.getString(EVCombinedRec.PROCESS_INFO).length()>0)
            {          	
 	            contentObject.put("PROCESSINFO".toLowerCase(),rec.getString(EVCombinedRec.PROCESS_INFO));       
            }  
            
            //VIDEO_PUBLISHERNAME
            //System.out.println("VIDEO_PUBLISHERNAME="+rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME));
            if(rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME)!=null && rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME).length()>0)
            {          	
 	            contentObject.put("VIDEO_PUBLISHERNAME".toLowerCase(),rec.getString(EVCombinedRec.VIDEO_PUBLISHERNAME));       
            } 
            
            //VIDEOLOCATION
            //System.out.println("VIDEO_LOCATION="+rec.getString(EVCombinedRec.VIDEO_LOCATION));
            if(rec.getString(EVCombinedRec.VIDEO_LOCATION)!=null && rec.getString(EVCombinedRec.VIDEO_LOCATION).length()>0)
            {          	
 	            contentObject.put("VIDEOLOCATION".toLowerCase(),rec.getString(EVCombinedRec.VIDEO_LOCATION));       
            }
            //output pretty format        
            //Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            
            //output regular format
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            
        	JsonParser jp = new JsonParser();
        	//JsonElement je = jp.parse(evo.build().toString());
        	JsonElement je = jp.parse(contentObject.toString());
        	 
        	String prettyJsonString = gson.toJson(je);
        	
        	//out.println(prettyJsonString);
        	outputAsJsonFile(prettyJsonString,eid,getDatabase(),getLoadnumber());      
            ++curRecNum;
             
            //end(eid);
        	//batchData.put(eid,prettyJsonString);//run batchdata
        	
          
        }
    
    
    private String[] removeCategorySign(String[] input)
    {
    	String[] output = new String[input.length];
    	int j=0;
    	for(int i=0;i<input.length;i++)
    	{
    		if(input[i].indexOf("(category)")<0)
    		{
    			output[j]=input[i];
    			j++;
    		}
    		
    	}
    	return output;
    }
    
    public String[] removeCommaANDPeriod(String[] input)
    {
    	if(input!=null)
    	{
	    	String[] outputArray=new String[input.length];
	    	for(int i=0;i<input.length;i++)
	    	{
	    		if(input[i]!=null)
	    		{
	    			//temporary stop removing comma and period
	    			//outputArray[i]=input[i].replaceAll("\\.|,", " "); 
	    		}
	    	}
	    	//return outputArray; //use for removing comma and period
	    	return input; 
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public String[] removeExtraSpace(String[] input)
    {
    	if(input!=null)
    	{
	    	String[] outputArray=new String[input.length];
	    	for(int i=0;i<input.length;i++)
	    	{
	    		outputArray[i]=removeExtraSpace(input[i]);   		
	    	}
	    	return outputArray;
    	}
    	else
    	{
    		return null;
    	}
    }
    
    private String removeExtraSpace(String input)    {
    	String output="";
    	if(input!=null)
    	{ 
    		//System.out.println("INPUT0="+input);
    		input = input.replaceAll("&mdash;", "—").replaceAll("&dollar;", "\\$").replaceAll("&percnt;", "%").replaceAll("&apos;","'").replaceAll("&les;","⩽").replaceAll("&ges;", "⩾");
    		//System.out.println("INPUT1="+input);
    		input = DataLoadDictionary.cleanBadCharacters(input);
    		//System.out.println("INPUT2="+input);
    		input = input.trim().replaceAll("\\s{2,}", " ");//"&les:" and "&ges;" are not recognize by escape api so w do it manually
    		//System.out.println("INPUT3="+input);
    		output=Entity.unescapeHtml(input);
    		//System.out.println("INPUT4="+input);
    		output=removeSpecialTag(output);
    		//System.out.println("INPUT5="+input);
    		
    	}
    	return output;
    }
    
    private void outputAsJsonFile(String content,String id, String database,String loadnumber) throws Exception
    {
    	if(database.equals("upa") || database.equalsIgnoreCase("wop") || database.equalsIgnoreCase("eup"))
    	{
    		database="upt";
    	}
    	File file=new File("json");
    	if(!file.exists())
        {
            file.mkdir();
        }
    	
    	file=new File("json/"+database);
    	if(!file.exists())
        {
            file.mkdir();
        }
    	
    	file=new File("json/"+database+"/"+loadnumber);
        if(!file.exists())
        {
            file.mkdir();
        }
        
        file=new File("json/"+database+"/"+loadnumber+"/tmp");
        if(!file.exists())
        {
            file.mkdir();
        }
        
        String filename = file.getPath() + "/"+id+".json"; 
        File f = new File(filename);
        //System.out.println("filename="+filename);
        if(!f.exists())
        {
            f.createNewFile();
        }
        PrintWriter output = new PrintWriter(new FileWriter(f));
        output.println(content);
        output.close();
        curRecNum++;
        if (curRecNum > recsPerbatch)
        {
            System.out.print(".");
            curRecNum = 0;
            zipBatchJsonFile();
            this.batchID++;
        }
        
    }
    
    private String[] stripChar29(String[] input)
    {
    	String[] output=null;
    	if(input!=null)
    	{
    		output=new String[input.length];
    		for(int i=0;i<input.length;i++)
    		{
    			if(input[i]!=null)
    			{				
    				output[i]=input[i].replaceAll(Constants.GROUPDELIMITER, "");
    			}
    		}
    	}
    	return output;
    }
       
    public String[] removeSpace(String[] input)
    {
    	String[] output=null;
    	
    	if(input!=null)
    	{
    		output=new String[input.length];
    		int j=0;
    		for(int i=0;i<input.length;i++)
    		{
    			
    			if(input[i]!=null)
    			{
    				output[j]=input[i].replaceAll(",","");
    				
    				output[j]=output[j].replaceAll("\\."," ");
    				
    				output[j]=output[j].replaceAll("\\s{2,}"," ");
    				
    				output[j]=output[j].replaceAll("\\s{0,}-\\s{0,}","-");   
    				output[j]= removeExtraSpace(output[j]);
    			}
    			
    			j++;
    		}
    		
    	}
    	return output;
    }
    
    public String removeSpace(String input)
    {
    	String output=null;
    	
    	if(input!=null)
    	{
    		
			output=input.replaceAll(",","");
			
			output=output.replaceAll("\\."," ");
			
			output=output.replaceAll("\\s{2,}"," ");
			
			output=output.replaceAll("\\s{0,}-\\s{0,}","-");   
			output= removeExtraSpace(output);    		   		
    		
    	}
    	return output;
    }
    
    public String[] reverseSigns(String[] input)
    {
    	String[] output=null;
    	
    	if(input!=null)
    	{
    		output=new String[input.length];
    		int j=0;
    		for(int i=0;i<input.length;i++)
    		{
    			if(input[i]!=null && input[i].trim().length()>0)
    			{
    				//System.out.println("BEFORE="+input[i]);
    				output[j]=input[i].replaceAll("minus","-").replaceAll("plus","+").replaceAll("DQD", ".").replaceAll(" qqdashqq ", "-").replaceAll("SLASH", "/");
    				//System.out.println("AFTER="+output[j]);
    				j++;
    			}
    		}
    	}
    	return output;
    }
               
    private String[] combine(String[] arr1,String[] arr2, String[] arr3) {
    	int arr1Length = 0;
    	int arr2Length = 0;
    	int arr3Length = 0;
    	
    	if(arr1 !=null && arr1.length>0 && arr1[0]!=null && arr1[0].length()>0)
    	{
    		arr1Length = arr1.length;
    	}
    	
    	if(arr2 !=null && arr2.length>0 && arr2[0]!=null && arr2[0].length()>0)
    	{
    		arr2Length = arr2.length;
    	}
    	
    	if(arr3 !=null && arr3.length>0 && arr3[0]!=null && arr3[0].length()>0)
    	{
    		arr3Length = arr3.length;
    	}
        
        String[] result = new String[arr1Length + arr2Length + arr3Length];
        if(arr1 !=null && arr1.length>0 && arr1[0]!=null && arr1[0].length()>0) {
        	System.arraycopy(arr1, 0, result, 0, arr1Length);
        }
        
        if(arr2 !=null && arr2.length>0 && arr2[0]!=null && arr2[0].length()>0) {
        	System.arraycopy(arr2, 0, result, arr1Length, arr2Length);
        }
        
        if(arr3 !=null && arr3.length>0 && arr3[0]!=null && arr3[0].length()>0) {
        	System.arraycopy(arr3, 0, result, arr1Length+arr2Length, arr3Length);
        }
        return result;
       // System.out.println(Arrays.toString(result));
    }
    
    private String hasDOI(EVCombinedRec rec)
    {
        String s = "";

        if (rec.containsKey(EVCombinedRec.DOI))
        {
            s = " TRUE QQDelQQ";
        }

        return s;
    }

    private String hasPcited(String pcited)
    {

        if ((pcited == null) ||
            (pcited.trim().equals("")))
        {
            pcited = "0";
        }

        return pcited;
    }

    private String notNull(String s)
    {
        String r = null;

        if (s == null)
        {
            r = "";
        }
        else
        {
            r = s;
        }

        return r;
    }

    private int getMask(EVCombinedRec rec)
    {
        int mask = 0;
        if (rec.containsKey(EVCombinedRec.ABSTRACT))
        {
            mask = mask + 1;
        }

        if (rec.containsKey(EVCombinedRec.CONTROLLED_TERMS) ||
            rec.containsKey(EVCombinedRec.MAIN_HEADING))
        {
            mask = mask + 2;
        }

        if (rec.containsKey(EVCombinedRec.DOI))
        {
            mask = mask + 4;
        }

        if (rec.containsKey(EVCombinedRec.PCITED))
        {
            mask = mask + 8;
        }

        return mask;
    }

    private String[] formatReportNumbers(String[] rn)
    {
        if (rn == null)
        {
            return null;
        }

        for (int i = 0; i < rn.length; i++)
        {

            String reportNumberCleaned = rn[i];
            reportNumberCleaned = perl.substitute("s# ##g", reportNumberCleaned);
            reportNumberCleaned = perl.substitute("s#-##g", reportNumberCleaned);
            reportNumberCleaned = perl.substitute("s#/##g", reportNumberCleaned);
            reportNumberCleaned = perl.substitute("s#\\.##g", reportNumberCleaned);
            rn[i] = reportNumberCleaned;
        }

        return rn;
    }
    
    private String formatStandardCodes(String c)
    {
    	//System.out.println("INPUT="+c);
		String r = "";
        if (c == null)
        {
            return null;
        }
        else
        {
        	r = c.replaceAll("\\|","|QPIPEQ|"); //use QPIPEQ for | sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\.","|DQD|"); //use DQD for dot(.)
        	r = r.replaceAll("-","|QMINUSQ|"); //use QMINUSQ for minus sign
        	r = r.replaceAll("/","|QSLASHQ|"); //use QSLASHQ for slash(/)
        	r = r.replaceAll(" ","|QSPACEQ|"); //use QSPACEQ for space
        	r = r.replaceAll(",","|QCOMMAQ|"); //use QCOMMAQ for comma
        	r = r.replaceAll(":","|QCOLONQ|"); //use QCOLONQ for colon
        	r = r.replaceAll(";","|QSEMICOLONQ|"); //use QSEMICOLONQ for semicolon
        	r = r.replaceAll("\\(","|QOPENINGBRACKETQ|"); //use QOPENINGBRACKETQ opening parenthesis
        	r = r.replaceAll("\\)","|QCLOSINGBRACKETQ|"); //closing parenthesis
        	r = r.replaceAll("&","|QANDQ|"); //use QANDQ for & sign
        	r = r.replaceAll("_","|QUNDERLINEQ|"); //use QUNDERLINEQ for underline sign
        	r = r.replaceAll("\\+","|QPLUSQ|"); //use QPLUSQ for plus sign (added on 2/11/2019 by hmo)
        	r = r.replaceAll("~","|QTILDEQ|"); //use QTILDEQ for ~ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("`","|QACUTEQ|"); //use QACUTEQ for ` sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("!","|QEXCLAMATIONQ|"); //use QEXCLAMATIONQ for ! sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("@","|QATQ|"); //use QATQ for @ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("#","|QNUMBERQ|"); //use QNUMBERQ for # sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\$","|QDOLLARQ|"); //use QDOLLARQ for $ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("%","|QPERCENTQ|"); //use QPERCENTQ for % sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\^","|QCARETQ|"); //use QCARETQ for ^ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\*","|QASTERISKQ|"); //use QASTERISKQ for * sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("=","|QEQUALQ|"); //use QEQUALQ for = sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\{","|QOPENBRACEQ|"); //use QOPENBRACEQ for { sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\}","|QCLOSEBRACEQ|"); //use QCLOSEBRACEQ for } sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\[","|QOPENSQUAREBRACKETQ|"); //use QOPENSQUAREBRACETQ for [ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\]","|QCLOSESQUAREBRACKETQ|"); //use QCLOSESQUAREBRACKETQ for ] sign (added on 2/12/2019 by hmo)       	
        	r = r.replaceAll("\\\\","|QBACKSLASHQ|"); //use QBACKSLASHQ for \ sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\"","|QQUOTEQ|"); //use QQUOTEQ for " sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("'","|QSINGLEQUOTEQ|"); //use QSINGLEQUOTEQ for ' sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\<","|QOPENANGLEBRACKETQ|"); //use QOPENANGLEBRACKETQ for < sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\>","|QCLOSEANGLEBRACKETQ|"); //use QCLOSEANGLEBRACKETQ for > sign (added on 2/12/2019 by hmo)
        	r = r.replaceAll("\\?","|QQUESTIONMARKQ|"); //use QQUESTIONMARKQ for ? sign (added on 2/12/2019 by hmo)
        	
        	
        }
        String[] ra = r.split("\\|",-1);
        StringBuffer rBuffer = new StringBuffer();
        String term = "";
        for(int i=0;i<ra.length;i++)
        {
        	if(ra[i]!=null && ra[i].length()>0)
        	{
	        	term = term+ra[i];
	        	rBuffer.append(term + " QQDELQQ ");
        	}
        }
        
        rBuffer.append(c+" QQDELQQ ");
        //System.out.println("OUTPUT="+rBuffer.toString());
        return rBuffer.toString(); 

    }

    private String[] prepareISSN(String[] issns)
    {
        if (issns == null)
        {
            return null;
        }

        ArrayList list = new ArrayList();
        for (int i = 0; i < issns.length; i++)
        {
            String issn = issns[i];
            if (issn == null)
            {
                break;
            }
            list.add(getISSN9(issn));
            list.add(getISSN8(issn));
        }

        return (String[]) list.toArray(new String[1]);
    }

    public String getISSN9(String ISSN)
    {
        if (ISSN.length() == 9)
        {
            return ISSN.substring(0, 4) + ISSN.substring(5, 9);
        }
        else if (ISSN.length() == 8)
        {
            return ISSN;
        }

        return null;
    }

    public String getISSN8(String ISSN)
    {
        if (ISSN.length() == 9)
        {
            return ISSN;
        }
        else if (ISSN.length() == 8)
        {
            return ISSN.substring(0, 4) + "-" + ISSN.substring(4, 8);
        }

        return null;
    }

    public String[] prepareISBN(String[] isbns)
    {
        if (isbns == null)
        {
            return null;
        }

        ArrayList list = new ArrayList();
        for (int i = 0; i < isbns.length; i++)
        {

            String isbn = isbns[i];
            if (isbn == null)
            {
                break;
            }
            isbn = perl.substitute("s#-# #g", isbn);
            list.add(isbn);
            isbn = perl.substitute("s# ##g", isbn);
            list.add(isbn);
        }

        return (String[]) list.toArray(new String[1]);
    }
    
    public String[] prepareISBNForES(String[] isbns)
    {
        if (isbns == null)
        {
            return null;
        }

        ArrayList list = new ArrayList();
        for (int i = 0; i < isbns.length; i++)
        {

            String isbn = isbns[i];
            if (isbn == null)
            {
                break;
            }
            list.add(isbn);
            String isbnWithoutDash = isbn.replaceAll("-","");
            list.add(isbnWithoutDash);                   
        }

        return (String[]) list.toArray(new String[1]);
    }

    
    private String formatSic(String sic)
    {
        if (sic == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer();

        String clean = perl.substitute("s#,##g", sic);

        buf.append(sic);
        buf.append(" QQDelQQ ");
        buf.append(clean);
        return buf.toString();

    }

    private void writeLog(String log)
    {
        PrintWriter extractlog = null;
        try
        {
            extractlog = new PrintWriter(new FileWriter(this.logfile, true));
            extractlog.println(log);
        }
        catch(Exception e)
        {
          if(extractlog!=null)
            e.printStackTrace(extractlog);
          else
            e.printStackTrace();
        }
        finally
        {
          if(extractlog != null)
          {
            try
            {
                extractlog.close();
            }
            catch(Exception e1)
            {
              e1.printStackTrace();
            }
          }
       }
    }

    public String[] addIndex(String s[], String key)
    {
    	String[] o = null;
        try
        {
            PrintWriter indexWriter = (PrintWriter)hm.get(key);
            if(s!=null)
            {
            	o = new String[s.length];
                for(int i=0; i<s.length; i++)
                {
                	String ss = s[i];
                	String aid = " ";
                	String oo = " ";
      
                	if(ss!=null && ss.indexOf(Constants.GROUPDELIMITER)>-1)
                	{
                		int dd = ss.indexOf(Constants.GROUPDELIMITER);
                		aid = ss.substring(0,dd);
                		oo = ss.substring(dd+1);
                		if(oo==null)
                		{
                			System.out.println("oo is null"+ ss);
                		}
                		
                	}
                	else
                	{
                		oo = ss;
                		
                	}
               
                	if(oo!=null && getDatabase()!=null && getDatabase().length()>=3 && Entity.prepareString(oo)!=null)
                    {
                    	indexWriter.println(Entity.prepareString(oo).toUpperCase().trim() + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + getLoadnumber()+"\t"+getAccessnumber());
                        //use accessnumber to replace pui 11/28/2018
                    	//indexWriter.println(Entity.prepareString(oo).toUpperCase().trim() + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getAccessnumber()+"\t" + getLoadnumber());
                    	//System.out.println("******* 1 ********"+ oo + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + getLoadnumber());
                    }
                    else
                    {
                    	indexWriter.println(" " + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + getLoadnumber()+"\t"+getAccessnumber());
                    	//System.out.println("*******something wrong with this record "+ oo + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + getLoadnumber()+" key="+key);
                    	//use accessnumber to replace pui 11/28/2018
                    	//System.out.println("******* 2 something wrong with this record "+ oo + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getAccessnumber()+"\t" + getLoadnumber());
                    }
                    indexWriter.flush();
                    o[i] = oo;
                }
            }
            else
            {
            	indexWriter.println(" " + "\t" + getDatabase().substring(0,3) + "\t"+ "0"  +"\t" + getPui()+"\t" + getLoadnumber()+"\t"+getAccessnumber());
            	//System.out.println("******* 3  "+key+" field with this record is empty "+ " " + "\t" + getDatabase().substring(0,3) + "\t" + " " +"\t" + getPui()+"\t" + getLoadnumber());
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return o;
    }

    private String cleanup(String s)
    {
        Perl5Util perl = new Perl5Util();

        if (s!=null && s.length()>0)
        {
        	//replace comma with space pre Frank request 11/25/2015
            s = perl.substitute("s/\"|\\[|\\]|\\(|\\)|\\{|\\}|\\?|\\,/ /g"  ,s);
        }

        return s;
    }

    public String addIndex(String s, String key)
    {
        s = cleanup(s);
        String sarray[] = {s};
        PrintWriter indexWriter = (PrintWriter)hm.get(key);
        addIndex(sarray, key);
        return s;
    }

    private String[] addIpcIndex(String ss, String key)  throws Exception
    {
        if(ss==null)
        {
            return null;
        }
        String[] sArray=prepareIpc(ss);
        PrintWriter indexWriter = (PrintWriter)hm.get(key);
        StringTokenizer st = new StringTokenizer(ss, Constants.AUDELIMITER);
        String name="";
        String code="";

        while (st.hasMoreTokens())
        {
            String s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      code = s.substring(0,i);
                      name = s.substring(i+1);
                      //System.out.println("code="+code+"-"+Entity.prepareString(code)+" name="+name+"-"+Entity.prepareString(name)+" key="+key+" database"+getDatabase());
                      if(indexWriter==null)
                    	  System.out.println("indexWriter is null");
                      indexWriter.println(Entity.prepareString(code).toUpperCase().trim() + "\t" +Entity.prepareString(name).toUpperCase().trim() + "\t" + getDatabase().substring(0,3)+"\t");
                }

            }

        }


        return sArray;
    }
    
    /*HT added 09/21/2020 for ES Lookup*/
    public String[] addIpcIndex(String ss, PrintWriter pWriter, String database)  throws Exception
    {
        if(ss==null)
        {
            return null;
        }
        String[] sArray=prepareIpc(ss);
        PrintWriter indexWriter = pWriter;
        StringTokenizer st = new StringTokenizer(ss, Constants.AUDELIMITER);
        String name="";
        String code="";

        while (st.hasMoreTokens())
        {
            String s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      code = s.substring(0,i);
                      name = s.substring(i+1);
                      indexWriter.println(Entity.prepareString(code).toUpperCase().trim() + "\t" +Entity.prepareString(name).toUpperCase().trim() + "\t" + database.substring(0,3)+"\t");
                }

            }

        }


        return sArray;
    }
    

     String[] prepareIpc(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      s = s.substring(0,i);
                }
                s = s.trim();

                list.add(s);
            }

        }

        return (String[])list.toArray(new String[1]);

    }




    private String multiFormat(String[] strings)
    {
        if (strings == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < strings.length; ++i)
        {
            String s = strings[i];
            if (s != null)
            {
                s = s.trim();
                if (s.length() > 0)
                {
                    buf.append(s);
                    buf.append(" QQDelQQ ");
                }
            }

        }

        
        if (buf.length() == 0)
        {
            return null;
        }

        String finishedString = "QQDelQQ " + buf.toString().trim();
        //System.out.println("RECORD= "+finishedString);
        return finishedString;
    }
    
    private String removeSpecialTag(String s)
    {
		if (s == null)
		{     return null;
		}
		
		s = s.trim();
		s = perl.substitute("s/<sup>|<\\/sup>/ /g", s);
		s = perl.substitute("s/<sub>|<\\/sub>|<inf>|<\\/inf>|<br>|<\\/br>|<br\\/>|<i>|<\\/i>|<em>|<b>|<\\/b>|<p>|<\\/p>//g", s);	
		s = perl.substitute("s/<span.*?>|<\\/span>|<svg:svg.*?>|<\\/svg:svg>|<svg:image.*?\\/>//g", s);  //added on 8/25/2020 to remove <span> tag		
		
		//s = perl.substitute("s/\\s+/ /g", s);
		//s = Jsoup.parse(s).text();			
		   
		return s;
    }

    private String checkForAuthor(String s, EVCombinedRec rec)
    {
        String r = null;
        if (!rec.containsKey(EVCombinedRec.AUTHOR))
        {
            r = s;
        }
        return r;
    }

    private String getStems(String s)
    {
        if (s == null)
        {
            return null;
        }

        s = s.trim();
       
        s = perl.substitute("s/\\\\/ /g", s);
        s = perl.substitute("s#/# #g", s);
        s = perl.substitute("s#\\$# #g", s);
        s = perl.substitute("s#\\%# #g", s);
        s = perl.substitute("s#\\&# #g", s);
        s = perl.substitute("s#\\*# #g", s);
        s = perl.substitute("s#\\+# #g", s);
        s = perl.substitute("s#\\]# #g", s);
        s = perl.substitute("s#\\[# #g", s);
        s = perl.substitute("s#=# #g", s);
        s = perl.substitute("s#_# #g", s);
        s = perl.substitute("s/,/ /g", s);
        s = perl.substitute("s/\\./ /g", s);
        s = perl.substitute("s/:/ /g", s);
        s = perl.substitute("s/#/ /g", s);
        s = perl.substitute("s/\\?/ /g", s);
        s = perl.substitute("s/\\!/ /g", s);
        s = perl.substitute("s/;/ /g", s);
        s = perl.substitute("s/\\(/ /g", s);
        s = perl.substitute("s/\\)/ /g", s);
        s = perl.substitute("s/\\{/ /g", s);
        s = perl.substitute("s/\\}/ /g", s);
        s = perl.substitute("s/-/ /g", s);
        s = perl.substitute("s/\"/ /g", s);
        s = perl.substitute("s/'/ /g", s);
        s = perl.substitute("s/\\`/ /g", s);      
        s = perl.substitute("s/</ /g", s);
        s = perl.substitute("s/>/ /g", s);
        s = perl.substitute("s/\\^/ /g", s);
        s = perl.substitute("s/\\|/ /g", s);
        s = perl.substitute("s/\\~/ /g", s);
        s = perl.substitute("s/\\@/ /g", s);

        StringBuffer buf = new StringBuffer();
        ArrayList strings = new ArrayList();
        perl.split(strings, "/\\s+/", s);

        for (int i = 0; i < strings.size(); ++i)
        {

            String a = (String) strings.get(i);
            if (a.indexOf("QQ") > -1 || a.indexOf("QZ") > -1)
            {
                buf.append(a + " ");
            }
            else
            {
                if (a.length() > 0)
                {
                    a = stemmer.stem(a.toLowerCase());
                    buf.append("QZ" + a + "QZ ");
                }

            }
        }

        return buf.toString();
    }

    public String formatClassCodes(String c)
    {
        if (c == null)
        {
            return null;
        }

        c = perl.substitute("s/\\./DQD/g", c);

        return c;

    }
    
    private JSONArray formJsonArray (String[] arrays, String arrayName)
    {
    	JSONArray jArray = new JSONArray();
    	ArrayList <String> arrayList = new ArrayList<String>();
    	if(arrays!=null) {
	    	for(int i=0;i<arrays.length;i++)
	    	{
	    		String arrayElement = arrays[i];
	    		if(arrayElement!=null && arrayElement.trim().length()>0 && !arrayList.contains(arrayElement)) {
	    			//arrayElement = cleaner.stripBadChars(arrayElement);	
	    			arrayList.add(arrayElement);
	    			jArray.add(arrayElement);
	    		}
	    	}
    	}
    	return jArray;
    }

    private String formatAuthors(String[] authors)
        throws Exception
    {

        if (authors == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < authors.length; i++)
        {
            String s = authors[i];
            if ((s != null )
                        && (!s.trim().equals("")))
            {

                if (buf.length() > 0)
                {
                    buf.append(" QQDelQQ ");
                }
                s = cleaner.stripBadChars(s);
                s = s.trim();
                buf.append(s);
                buf.append(" QQDelQQ ");
                buf.append(cleaner.joinAuthor(s));
            }
        }

        if (buf.length() == 0)
        {
            return null;
        }

        String finishedString = "QQDelQQ " + buf.toString() + " QQDelQQ";
        return finishedString;
    }

    public void endXML()
    throws Exception
    {
        out.println("</ROWSET>");
        out.close();
        File f = new File(filepath);
        StringBuffer GZIPFileName = new StringBuffer();
        GZIPFileName.append(f.getAbsolutePath());
        GZIPFileName.append(".gz");
        d.setLogfile(logpath + "/validator.log");
        d.validateFile(filepath);
        FileInputStream in = new FileInputStream(filepath);
        GZIPOutputStream outgz = new GZIPOutputStream(new FileOutputStream(GZIPFileName.toString()));
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            outgz.write(buf, 0, len);
        }
        in.close();
        outgz.finish();
        outgz.close();
        f.delete();
    }
    public void zipBatch()
    throws Exception
    {
        File tmpDir = new File(this.root);
        String[] gzFiles = tmpDir.list();
        File[] gzFilestoDelete = tmpDir.listFiles();
        byte[] buf = new byte[1024];
        long time = System.currentTimeMillis();
        String ZipFilename = this.batchPath + "/EIDATA/" + Long.toString(time) + "_"+ databaseID + "_" + this.operation + "_" + this.numberID + "-" + formatter.format(this.batchID) + ".zip";
        writeLog( formatTimeStamp(System.currentTimeMillis()) + "adding " + gzFiles.length + " files to zipfile " + ZipFilename);
        String ControlFile = this.batchPath + "/PROD/" + Long.toString(time) + "_"+ databaseID + "_" + this.operation + "_" + this.numberID + "-" + formatter.format(this.batchID) + ".ctl";
        //System.out.println("controlFile="+ControlFile);
        writeLog(formatTimeStamp(System.currentTimeMillis()) + "creating control file " + ControlFile);
        long timediff = time - this.starttime;
        writeLog(formatTimeStamp(System.currentTimeMillis()) + "batch processed in " + timediff + " milliseconds");
        writeLog(formatTimeStamp(System.currentTimeMillis()) + "last parent id generated is " + this.parentid);
        File f = new File(ControlFile);
        if(!f.exists())
        {
            f.createNewFile();
        }
        ZipOutputStream outZIP = new ZipOutputStream(new FileOutputStream(ZipFilename));
        for (int i=0; i<gzFiles.length; i++)
        {
            FileInputStream in = new FileInputStream(this.root + "/" + gzFiles[i]);
            outZIP.putNextEntry(new ZipEntry(gzFiles[i]));
            int len;
            while ((len = in.read(buf)) > 0) {
                outZIP.write(buf, 0, len);
            }
            outZIP.closeEntry();
            in.close();
            gzFilestoDelete[i].delete();
        }
        outZIP.close();
        tmpDir.delete();
    }
    
    public void zipBatchJsonFile()
    throws Exception
    {
    	long starttime = System.currentTimeMillis();
    	String database = getDatabase();
    	if(database.equalsIgnoreCase("upa") || database.equalsIgnoreCase("eup") || database.equalsIgnoreCase("wop"))
    	{
    		database="upt";
    	}
    	String path="json/"+database+"/"+getLoadnumber();
        File tmpDir = new File(path+"/tmp");
        //String tmpDir = new File(f.getAbsolutePath());
        String[] gzFiles = tmpDir.list();
        //File[] gzFilestoDelete = tmpDir.listFiles();
        byte[] buf = new byte[1024];
        
        String ZipFilename =  path+"/"+Long.toString(starttime) + "_"+ databaseID + "_" + this.operation + "_" + this.numberID + "-" + formatter.format(this.batchID) + ".zip";
       
        //long timediff = time - this.starttime;
       
        ZipOutputStream outZIP = new ZipOutputStream(new FileOutputStream(ZipFilename));
        for (int i=0; i<gzFiles.length; i++)
        {
        	File file = new File(tmpDir+"/"+gzFiles[i]);
        	//System.out.println("FILENAME="+gzFiles[i]);
            FileInputStream in = new FileInputStream(tmpDir+"/"+gzFiles[i]);
            outZIP.putNextEntry(new ZipEntry(tmpDir+"/"+gzFiles[i]));
            int len;
            while ((len = in.read(buf)) > 0) {
                outZIP.write(buf, 0, len);
            }
            outZIP.closeEntry();
            in.close();
            file.delete();
            
        }
        outZIP.close();
        tmpDir.delete();
        long endtime = System.currentTimeMillis();
        System.out.println("Time for Zipping="+(endtime-starttime));
    }

    public void end()
        throws Exception
    {
        if (open)
        {
            open = false;
            endXML();
            if (curRecNum >= recsPerbatch)
            {
                System.out.print(".");
                curRecNum = 0;
                zipBatch();
                createRoot(this.batchID++);
            }
        }
    }
    

    public void flush()
    {
        try
        {
            if (curRecNum > 0)
            {
                curRecNum = 0;
                //zipBatch();
                zipBatchJsonFile();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
          if(affiliationPW != null)
          {
              affiliationPW.close();
              affiliationPW = null;
          }
          if(authorPW != null)
          {
              authorPW.close();
              authorPW = null;
          }
          if(controltermsPW != null)
          {
              controltermsPW.close();
              controltermsPW = null;
          }
          if(serialtitlePW != null)
          {
              serialtitlePW.close();
              serialtitlePW = null;
          }
          if(publishernamePW != null)
          {
              publishernamePW.close();
              publishernamePW = null;
          }
          if(patentcountryPW != null)
          {
              patentcountryPW.close();
              patentcountryPW = null;
          }

          if(ipcPW != null)
          {
              ipcPW.close();
              ipcPW = null;
          }

       }
    }


    public String formatTimeStamp(long timeStamp)
    {
        SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return "[" + sdtf.format(new Date(timeStamp)) + " - LOAD :" + this.numberID  + " - BATCH:"  + batchidFormat  + " ]";
    }
    
    public static void main(String args[]) throws Exception
    {
    	CombinedXMLWriter c = new CombinedXMLWriter(1,1,"cpx");
    	//String testString = "  check   comma  space ";
    	String testString = args[0];
    	//System.out.println("before= "+testString+"\n after="+c.removeExtraSpace(testString));
    	//System.out.println("after2="+StringEscapeUtils.unescapeHtml4(testString));
   	
    }
    
    public String prettyPrintJSON(String unformattedJsonString) {
    	  StringBuilder prettyJSONBuilder = new StringBuilder();
    	  int indentLevel = 0;
    	  boolean inQuote = false;
    	  for(char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
    	    switch(charFromUnformattedJson) {
    	      case '"':
    	        // switch the quoting status
    	        inQuote = !inQuote;
    	        prettyJSONBuilder.append(charFromUnformattedJson);
    	        break;
    	      case ' ':
    	        // For space: ignore the space if it is not being quoted.
    	        if(inQuote) {
    	          prettyJSONBuilder.append(charFromUnformattedJson);
    	        }
    	        break;
    	      case '{':
    	      
    	      case '[':
    	        // Starting a new block: increase the indent level
    	        prettyJSONBuilder.append(charFromUnformattedJson);
    	        indentLevel++;
    	        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
    	        break;
    	        
    	      case '}':
    	      
    	      case ']':
    	        // Ending a new block; decrese the indent level
    	        indentLevel--;
    	        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
    	        prettyJSONBuilder.append(charFromUnformattedJson);
    	        break;
    	      
    	      case ',':
    	        // Ending a json item; create a new line after
    	        prettyJSONBuilder.append(charFromUnformattedJson);
    	        if(!inQuote) {
    	          appendIndentedNewLine(indentLevel, prettyJSONBuilder);
    	        }
    	        break;
    	      default:
    	        prettyJSONBuilder.append(charFromUnformattedJson);
    	    }
    	  }
    	  return prettyJSONBuilder.toString();
    	}
    
    /**
     * Print a new line with indention at the beginning of the new line.
     * @param indentLevel
     * @param stringBuilder
     */
    private static void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder) {
      stringBuilder.append("\n");
      for(int i = 0; i < indentLevel; i++) {
        // Assuming indention using 2 spaces
        stringBuilder.append("  ");
      }
    }  
    
    public boolean isNumeric(String strNum) 
    {
    	Pattern pattern = Pattern.compile("\\d+");
        if (strNum == null) {
            return false; 
        }
        return pattern.matcher(strNum).matches();
    }
}
