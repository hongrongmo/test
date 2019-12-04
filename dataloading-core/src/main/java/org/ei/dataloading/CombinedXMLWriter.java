package org.ei.dataloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.query.base.PorterStemmer;
import org.ei.xml.Entity;
import org.ei.common.Constants;
import org.ei.common.DataCleaner;

import java.util.Date;
import java.util.zip.*;
import java.text.*;

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

import org.ei.common.*;
import org.ei.util.kafka.*;

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
            init();
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
            init();
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

    public void writeRec(EVCombinedRec rec)
        throws Exception
    {

        setDatabase(rec.getString(EVCombinedRec.DATABASE));
        setPui(rec.getString(EVCombinedRec.PUI));
        setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
        setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
        String loadnumber = rec.getString(EVCombinedRec.LOAD_NUMBER);
        if(loadnumber !=null && loadnumber.length()>6)
        {
        	loadnumber = loadnumber.substring(0,6);
        }
        
        this.eid = rec.getString(EVCombinedRec.DOCID);
        begin();
        out.println("   <ROW> ");
        out.println("       <EIDOCID>" + this.eid + "</EIDOCID>");
        out.println("       <PARENTID>" +  rec.getString(EVCombinedRec.PARENT_ID) + "</PARENTID>");
        out.println("       <DEDUPKEY>" + rec.getString(EVCombinedRec.DEDUPKEY) + "</DEDUPKEY>");
        out.println("       <DATABASE>" + rec.getString(EVCombinedRec.DATABASE) + "</DATABASE>");
        out.println("       <LOADNUMBER>" + loadnumber + "</LOADNUMBER>");
        
        //added for future use only, should be removed for regular database loading
        out.println("       <UPDATENUMBER>" + rec.getString(EVCombinedRec.UPDATE_NUMBER) + "</UPDATENUMBER>");
        
        out.println("       <DATESORT>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.DATESORT))) + "</DATESORT>");
        out.println("       <PUBYEAR>" + rec.getString(EVCombinedRec.PUB_YEAR) + "</PUBYEAR>");
        out.println("       <ACCESSIONNUMBER>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ACCESSION_NUMBER))) + "</ACCESSIONNUMBER>");
        //out.println("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + "]]></AUTHOR>"); //added QstemQ portion to search both with qqdashqq and without it
        out.println("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + " QstemQ " + notNull(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR))) + "]]></AUTHOR>");
        out.println("       <AUTHORID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHORID)))) + "]]></AUTHORID>");
        out.println("       <AUTHORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))))) + "]]></AUTHORAFFILIATION>");
        out.println("       <AFFILIATIONLOCATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)))) + "]]></AFFILIATIONLOCATION>");
        out.println("       <TITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE))))) + "]]></TITLE>");
        //out.println("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>");
        out.println("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>");
        out.println("       <VOLUMETITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE))))) + "]]></VOLUMETITLE>");
        out.println("       <ABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT)))) + "]]></ABSTRACT>");
        out.println("       <OTHERABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT)))) + "]]></OTHERABSTRACT>");
        out.println("       <EDITOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.EDITOR))))+"]]></EDITOR>");
        out.println("       <EDITORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION))))) + "]]></EDITORAFFILIATION>");
        out.println("       <TRANSLATOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.TRANSLATOR)))) + "]]></TRANSLATOR>");
        out.println("       <CONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS"))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS)))))) + "]]></CONTROLLEDTERMS>");
        out.println("       <UNCONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS)))))) + "]]></UNCONTROLLEDTERMS>");
        out.println("       <ISSN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN))))) + "]]></ISSN>");
        out.println("       <ISSNOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN_OF_TRANSLATION))))) + "]]></ISSNOFTRANSLATION>");
        out.println("       <CODEN><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN)))) + "]]></CODEN>");
        out.println("       <CODENOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION)))) + "]]></CODENOFTRANSLATION>");
        out.println("       <ISBN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISBN(rec.getStrings(EVCombinedRec.ISBN))))) + "]]></ISBN>");
        out.println("       <SERIALTITLE><![CDATA[" + notNull(Entity.prepareString(notNull(multiFormat(addIndex(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE)))))) + "]]></SERIALTITLE>");
        out.println("       <SERIALTITLETRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION))))) + "]]></SERIALTITLETRANSLATION>");
        out.println("       <MAINHEADING><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING)))))) + "]]></MAINHEADING>");
        out.println("       <SUBHEADING><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SUB_HEADING))) + "]]></SUBHEADING>");
        out.println("       <PUBLISHERNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PUBLISHER_NAME))))) + "]]></PUBLISHERNAME>");
        out.println("       <TREATMENTCODE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TREATMENT_CODE)))) + "</TREATMENTCODE>");
        out.println("       <LANGUAGE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LANGUAGE)))) + "]]></LANGUAGE>");
        out.println("       <RECTYPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOCTYPE)))) + "]]></RECTYPE>");
        out.println("       <CLASSIFICATIONCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE)))) + "]]></CLASSIFICATIONCODE>");
        out.println("       <CONFERENCECODE>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_CODE))) + "</CONFERENCECODE>");
        out.println("       <CONFERENCENAME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME)))) + "]]></CONFERENCENAME>");
        out.println("       <CONFERENCELOCATION><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)))) + "]]></CONFERENCELOCATION>");
        out.println("       <MEETINGDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.MEETING_DATE))) + "]]></MEETINGDATE>");
        out.println("       <SPONSORNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME))))) + "]]></SPONSORNAME>");
        out.println("       <MONOGRAPHTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE))))) + "]]></MONOGRAPHTITLE>");
        out.println("       <DISCIPLINE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DISCIPLINE)))) + "</DISCIPLINE>");
        out.println("       <MATERIALNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MATERIAL_NUMBER)))) + "]]></MATERIALNUMBER>");
        out.println("       <NUMERICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING)))) + "]]></NUMERICALINDEXING>");
        out.println("       <CHEMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING)))) + "]]></CHEMICALINDEXING>");
        out.println("       <ASTRONOMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING)))) + "]]></ASTRONOMICALINDEXING>");
        out.println("       <REPORTNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REPORTNUMBER)))) + "]]></REPORTNUMBER>");
        out.println("       <ORDERNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORDERNUMBER)))) + "]]></ORDERNUMBER>");
        out.println("       <COUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COUNTRY)))) + "]]></COUNTRY>");
        out.println("       <VOLUME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.VOLUME))) + "]]></VOLUME>");
        out.println("       <ISSUE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ISSUE))) + "]]></ISSUE>");
        out.println("       <STARTPAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STARTPAGE))) + "]]></STARTPAGE>");
        out.println("       <PAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.PAGE))) + "]]></PAGE>");
        if(rec.getString(EVCombinedRec.AVAILABILITY)!=null)
        {
        	out.println("		<AVAILABILITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY)))) + "]]></AVAILABILITY>");
        }
        else
        {
        	out.println("		<AVAILABILITY><![CDATA[]]></AVAILABILITY>");
        }
        
        if(rec.getStrings(EVCombinedRec.NOTES)!=null)
        {
        	out.println("       <NOTES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES))))) + "]]></NOTES>");
        }
        else
        {
        	out.println("       <NOTES><![CDATA[]]></NOTES>");
        }
        out.println("       <PATENTAPPDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTAPPDATE)))) + "]]></PATENTAPPDATE>");
        out.println("       <PATENTISSUEDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTISSUEDATE)))) + "]]></PATENTISSUEDATE>");
        out.println("       <COMPANIES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COMPANIES)))) + "]]></COMPANIES>");
        out.println("       <CASREGISTRYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER)))) + "]]></CASREGISTRYNUMBER>");
        
        if(rec.getStrings(EVCombinedRec.BUSINESSTERMS)!=null)
        {
        	out.println("       <BUSINESSTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS))))) + "]]></BUSINESSTERMS>");
        }
        else
        {
        	out.println("       <BUSINESSTERMS><![CDATA[]]></BUSINESSTERMS>");
        }
        
        if(rec.getStrings(EVCombinedRec.CHEMICALTERMS)!=null)
        {
        	out.println("       <CHEMICALTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS))))) + "]]></CHEMICALTERMS>");
        }
        else
        {
        	out.println("       <CHEMICALTERMS><![CDATA[]]></CHEMICALTERMS>");
        }
        
        out.println("       <CHEMAC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS)))) + "]]></CHEMAC>");
        out.println("       <SIC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_NUMBER)))) + "]]></SIC>");
        out.println("       <INDUSTRIALCODES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALCODES)))) + "]]></INDUSTRIALCODES>");
        out.println("       <INDUSTRIALSECTORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS)))) + "]]></INDUSTRIALSECTORS>");
        out.println("       <SCOPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SCOPE)))) + "]]></SCOPE>");
        out.println("       <AGENCY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGENCY)))) + "]]></AGENCY>");
        out.println("       <DERWENTACCESSIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER)))) + "]]></DERWENTACCESSIONNUMBER>");
        out.println("       <APPLICATIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER)))) + "]]></APPLICATIONNUMBER>");
        out.println("       <APPLICATIONCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY))))+ "]]></APPLICATIONCOUNTRY>");
        out.println("       <INTPATENTCLASSIFICATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION")))) + "]]></INTPATENTCLASSIFICATION>");
        
        if(rec.getStrings(EVCombinedRec.LINKED_TERMS)!=null)
        {
        	out.println("       <LINKEDTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS))))) + "]]></LINKEDTERMS>");
        }
        else
        {
        	out.println("       <LINKEDTERMS><![CDATA[]]></LINKEDTERMS>");
        }
        
        out.println("       <ENTRYYEAR><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ENTRY_YEAR))) + "]]></ENTRYYEAR>");
        out.println("       <PRIORITYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER)))) + "]]></PRIORITYNUMBER>");
        out.println("       <PRIORITYDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_DATE)))) + "]]></PRIORITYDATE>");
        out.println("       <PRIORITYCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY))))+ "]]></PRIORITYCOUNTRY>");
        
        if(rec.getStrings(EVCombinedRec.SOURCE)!=null)
        {
        	out.println("       <SOURCE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE))))) + "]]></SOURCE>");
        }
        else
        {
        	out.println("       <SOURCE><![CDATA[]]></SOURCE>");
        }
        
        out.println("       <SECONDARYSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE)))) + "]]></SECONDARYSRCTITLE>");
        out.println("       <MAINTERM><![CDATA[" + notNull((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM)))) + " QstemQ " + notNull(getStems((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM))))) + "]]></MAINTERM>");
        out.println("       <ABBRVSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE)))) + "]]></ABBRVSRCTITLE>");
        out.println("       <NOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS))))) + "]]></NOROLETERMS>");
        out.println("       <REAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS))))) + "]]></REAGENTTERMS>");
        out.println("       <PRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS))))) + "]]></PRODUCTTERMS>");
        out.println("       <MAJORNOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS))))) + "]]></MAJORNOROLETERMS>");
        out.println("       <MAJORREAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS))))) + "]]></MAJORREAGENTTERMS>");
        out.println("       <MAJORPRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS))))) + "]]></MAJORPRODUCTTERMS>");
        out.println("       <CONFERENCEAFFILIATIONS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS))))) + "]]></CONFERENCEAFFILIATIONS>");
        out.println("       <CONFERENCEEDITORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS))))) + "]]></CONFERENCEEDITORS>");
        out.println("       <CONFERENCESTARTDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCESTARTDATE))) + "]]></CONFERENCESTARTDATE>");
        out.println("       <CONFERENCEENDDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEENDDATE))) + "]]></CONFERENCEENDDATE>");
        out.println("       <CONFERENCEVENUESITE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE)))) + "]]></CONFERENCEVENUESITE>");
        out.println("       <CONFERENCECITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY)))) + "]]></CONFERENCECITY>");
        out.println("       <CONFERENCECOUNTRYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECOUNTRYCODE))) + "]]></CONFERENCECOUNTRYCODE>");
        out.println("       <CONFERENCEPAGERANGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPAGERANGE))) + "]]></CONFERENCEPAGERANGE>");
        out.println("       <CONFERENCENUMBERPAGES><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCENUMBERPAGES))) + "]]></CONFERENCENUMBERPAGES>");
        out.println("       <CONFERENCEPARTNUMBER><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPARTNUMBER))) + "]]></CONFERENCEPARTNUMBER>");
        out.println("       <DESIGNATEDSTATES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DESIGNATED_STATES)))) + "]]></DESIGNATEDSTATES>");
        out.println("       <STNCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE)))) + "]]></STNCONFERENCE>");
        out.println("       <STNSECONDARYCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE)))) + "]]></STNSECONDARYCONFERENCE>");
        out.println("       <FILINGDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_FILING_DATE)))) + "]]></FILINGDATE>");
        out.println("       <PRIORITYKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_KIND)))) + "]]></PRIORITYKIND>");
        out.println("       <ECLACODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CODES)))) +"]]></ECLACODE>");
//        out.println("       <ECLACODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CODES)))) +" "+  notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CPCCLASS)))) +"]]></ECLACODE>");
        out.println("       <ATTORNEYNAME><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ATTORNEY_NAME)))) + "]]></ATTORNEYNAME>");
        out.println("       <PRIMARYEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER)))) + "]]></PRIMARYEXAMINER>");
        out.println("       <ASSISTANTEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER)))) + "]]></ASSISTANTEXAMINER>");
        out.println("       <IPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES)))) + "]]></IPCCLASS>");
        out.println("       <IPCSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES)))) + "]]></IPCSUBCLASS>");
        out.println("       <ECLACLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CLASSES)))) + "]]></ECLACLASS>");
        out.println("       <ECLASUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES)))) + "]]></ECLASUBCLASS>");
        out.println("       <USPTOCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCLASS)))) + "]]></USPTOCLASS>");
        out.println("       <USPTOSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOSUBCLASS)))) + "]]></USPTOSUBCLASS>");
        out.println("       <USPTOCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCODE)))) + "]]></USPTOCODE>");
        out.println("       <PATENTKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_KIND)))) + "]]></PATENTKIND>");
        out.println("       <KINDDESCRIPTION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR))))) + "]]></KINDDESCRIPTION>");        
        //out.println("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(addIndex(rec.getString(EVCombinedRec.AUTHORITY_CODE),"AUTHORITYCODE"))) + "]]></AUTHORITYCODE>");
        out.println("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AUTHORITY_CODE))) + "]]></AUTHORITYCODE>");
        out.println("       <PCITED><![CDATA[" + hasPcited(rec.getString(EVCombinedRec.PCITED)) + "]]></PCITED>");
        out.println("       <PCITEDINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PCITEDINDEX)))) + "]]></PCITEDINDEX>");
        out.println("       <PREFINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PREFINDEX)))) + "]]></PREFINDEX>");
        out.println("       <DMASK><![CDATA[" + getMask(rec) + "]]></DMASK>");
        out.println("       <DOI><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOI)))) + hasDOI(rec) + "]]></DOI>");
        out.println("       <SCOPUSID><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SCOPUSID))) + "]]></SCOPUSID>");
        out.println("       <AFFILIATIONID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATIONID)))) + "]]></AFFILIATIONID>");
        out.println("       <LAT_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NW))) + "]]></LAT_NW>");
        out.println("       <LNG_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NW))) + "]]></LNG_NW>");
        out.println("       <LAT_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NE))) + "]]></LAT_NE>");
        out.println("       <LNG_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NE))) + "]]></LNG_NE>");
        out.println("       <LAT_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SW))) + "]]></LAT_SW>");
        out.println("       <LNG_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SW))) + "]]></LNG_SW>");
        out.println("       <LAT_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SE))) + "]]></LAT_SE>");
        out.println("       <LNG_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SE))) + "]]></LNG_SE>");
        out.println("       <CPCCLASS><![CDATA[]]></CPCCLASS>");
//       out.println("       <CPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CPCCLASS)))) + "]]></CPCCLASS>");
        out.println("       <TABLEOFCONTENT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT)))) + "]]></TABLEOFCONTENT>");										//TOC
    
        
        //************************************************ added for numericalIndex ******************************************************//
        
        
        //amount of substance
        out.println("       <AMOUNTOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES)) + "]]></AMOUNTOFSUBSTANCE_RANGES>"); 											//NASR
        out.println("       <AMOUNTOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)))) + "]]></AMOUNTOFSUBSTANCE_TEXT>");				//NAST
        
        //electric current
        out.println("       <ELECTRICCURRENT_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_CURRENT_RANGES)) + "]]></ELECTRICCURRENT_RANGES>");													//NECR
        out.println("       <ELECTRICCURRENT_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)))) + "]]></ELECTRICCURRENT_TEXT>");						//NECT
        
        //mass
        out.println("       <MASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_RANGES)) + "]]></MASS_RANGES>");																						//NMAR
        out.println("       <MASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_TEXT)))) + "]]></MASS_TEXT>");														//NMAT
        
        //temperature
        out.println("       <TEMPERATURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TEMPERATURE_RANGES)) + "]]></TEMPERATURE_RANGES>");																//NTER
        out.println("       <TEMPERATURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)))) + "]]></TEMPERATURE_TEXT>");									//NTET
        
        //time
        out.println("       <TIME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TIME_RANGES)) + "]]></TIME_RANGES>");																						//NTIR
        out.println("       <TIME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TIME_TEXT)))) + "]]></TIME_TEXT>");														//NTIT
        
        //size
        out.println("       <SIZE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SIZE_RANGES)) + "]]></SIZE_RANGES>");																						//NSIR
        out.println("       <SIZE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SIZE_TEXT)))) + "]]></SIZE_TEXT>");														//NSIT
        
        //electrical conductance
        out.println("       <ELECTRICALCONDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES)) + "]]></ELECTRICALCONDUCTANCE_RANGES>");									//NEDR
        out.println("       <ELECTRICALCONDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)))) + "]]></ELECTRICALCONDUCTANCE_TEXT>");	//NEDT
        
        //electrical conductivity
        out.println("       <ELECTRICALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES)) + "]]></ELECTRICALCONDUCTIVITY_RANGES>");								//NETR
        out.println("       <ELECTRICALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)))) + "]]></ELECTRICALCONDUCTIVITY_TEXT>");//NETT
        
        //voltage
        out.println("       <VOLTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLTAGE_RANGES)) + "]]></VOLTAGE_RANGES>");																			//NVOR
        out.println("       <VOLTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)))) + "]]></VOLTAGE_TEXT>");												//NVOT
        
        //electric field strength
        out.println("       <ELECTRICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES)) + "]]></ELECTRICFIELDSTRENGTH_RANGES>");								//NEFR
        out.println("       <ELECTRICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)))) + "]]></ELECTRICFIELDSTRENGTH_TEXT>");	//NEFT
        
        //current density
        out.println("       <CURRENTDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CURRENT_DENSITY_RANGES)) + "]]></CURRENTDENSITY_RANGES>");														//NCDR
        out.println("       <CURRENTDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)))) + "]]></CURRENTDENSITY_TEXT>");						//NCDT
        
        //energy
        out.println("       <ENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ENERGY_RANGES)) + "]]></ENERGY_RANGES>");																				//NENR
        out.println("       <ENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ENERGY_TEXT)))) + "]]></ENERGY_TEXT>");													//NENT
        
        //electrical resistance
        out.println("       <ELECTRICALRESISTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES)) + "]]></ELECTRICALRESISTANCE_RANGES>");									//NERR
        out.println("       <ELECTRICALRESISTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)))) + "]]></ELECTRICALRESISTANCE_TEXT>");		//NERT
        
        //electrical resistivity
        out.println("       <ELECTRICALRESISTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES)) + "]]></ELECTRICALRESISTIVITY_RANGES>");									//NESR
        out.println("       <ELECTRICALRESISTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)))) + "]]></ELECTRICALRESISTIVITY_TEXT>");	//NEST
        
        //electron volt energy
        out.println("       <ELECTRONVOLTENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRON_VOLT_RANGES)) + "]]></ELECTRONVOLTENERGY_RANGES>");												//NEVR
        out.println("       <ELECTRONVOLTENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)))) + "]]></ELECTRONVOLTENERGY_TEXT>");					//NEVT	
        
        //capacitance
        out.println("       <CAPACITANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CAPACITANCE_RANGES)) + "]]></CAPACITANCE_RANGES>");																//NCAR
        out.println("       <CAPACITANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)))) + "]]></CAPACITANCE_TEXT>");									//NCAT
        
        //frequency	
        out.println("       <FREQUENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FREQUENCY_RANGES)) + "]]></FREQUENCY_RANGES>");																		//NFRR
        out.println("       <FREQUENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)))) + "]]></FREQUENCY_TEXT>");										//NFRT
        
        //power
        out.println("       <POWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.POWER_RANGES)) + "]]></POWER_RANGES>");																					//NPOR
        out.println("       <POWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.POWER_TEXT)))) + "]]></POWER_TEXT>");													//NPOT
        
        //apparent power 
        out.println("       <APPARENTPOWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.APPARENT_POWER_RANGES)) + "]]></APPARENTPOWER_RANGES>");															//NAPR
        out.println("       <APPARENTPOWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)))) + "]]></APPARENTPOWER_TEXT>");							//NAPT							
        
        //percentage
        out.println("       <PERCENTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PERCENTAGE_RANGES)) + "]]></PERCENTAGE_RANGES>");																	//NPCR
        out.println("       <PERCENTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)))) + "]]></PERCENTAGE_TEXT>");										//NPCT
        
        //magnetic flux density
        out.println("       <MAGNETICFLUXDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES)) + "]]></MAGNETICFLUXDENSITY_RANGES>");										//NMDR
        out.println("       <MAGNETICFLUXDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)))) + "]]></MAGNETICFLUXDENSITY_TEXT>");		//NMDT
        
        //inductance
        out.println("       <INDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.INDUCTANCE_RANGES)) + "]]></INDUCTANCE_RANGES>");																	//NINR
        out.println("       <INDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)))) + "]]></INDUCTANCE_TEXT>");										//NINT
        
        //volume charge density
        out.println("       <VOLUMECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES)) + "]]></VOLUMECHARGEDENSITY_RANGES>");										//NVCR
        out.println("       <VOLUMECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)))) + "]]></VOLUMECHARGEDENSITY_TEXT>");		//NVCT
        
        //surface charge density
        out.println("       <SURFACECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES)) + "]]></SURFACECHARGEDENSITY_RANGES>");									//NSCR
        out.println("       <SURFACECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)))) + "]]></SURFACECHARGEDENSITY_TEXT>");		//NSCT
        
        //decibel
        out.println("       <DECIBEL_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_RANGES)) + "]]></DECIBEL_RANGES>");																			//NDER
        out.println("       <DECIBEL_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)))) + "]]></DECIBEL_TEXT>");												//NDET
        
        //luminous flux
        out.println("       <LUMINOUSFLUX_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_FLUX_RANGES)) + "]]></LUMINOUSFLUX_RANGES>");															//NLFR
        out.println("       <LUMINOUSFLUX_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)))) + "]]></LUMINOUSFLUX_TEXT>");								//NLFT
        
        //illuminance
        out.println("       <ILLUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ILLUMINANCE_RANGES)) + "]]></ILLUMINANCE_RANGES>");																//NILR
        out.println("       <ILLUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)))) + "]]></ILLUMINANCE_TEXT>");									//NILT
        
        //bit rate
        out.println("       <BITRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.BIT_RATE_RANGES)) + "]]></BITRATE_RANGES>");																			//NBIR
        out.println("       <BITRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)))) + "]]></BITRATE_TEXT>");												//NBIT
        
        //mass density
        out.println("       <MASSDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_DENSITY_RANGES)) + "]]></MASSDENSITY_RANGES>");																//NMSR
        out.println("       <MASSDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)))) + "]]></MASSDENSITY_TEXT>");									//NMST
        
        //mass flow rate
        out.println("       <MASSFLOWRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_FLOW_RATE_RANGES)) + "]]></MASSFLOWRATE_RANGES>");																	//NMRR
        out.println("       <MASSFLOWRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)))) + "]]></MASSFLOWRATE_TEXT>");								//NMRT
        
        //force
        out.println("       <FORCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FORCE_RANGES)) + "]]></FORCE_RANGES>");																					//NFOR
        out.println("       <FORCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FORCE_TEXT)))) + "]]></FORCE_TEXT>");													//NFOT
        
        //torque
        out.println("       <TORQUE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TORQUE_RANGES)) + "]]></TORQUE_RANGES>");																				//NTOR
        out.println("       <TORQUE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TORQUE_TEXT)))) + "]]></TORQUE_TEXT>");													//NTOT
        
        //pressure
        out.println("       <PRESSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PRESSURE_RANGES)) + "]]></PRESSURE_RANGES>");																			//NPRR
        out.println("       <PRESSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)))) + "]]></PRESSURE_TEXT>");											//NPRT
        
        //area
        out.println("       <AREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AREA_RANGES)) + "]]></AREA_RANGES>");																						//NARR
        out.println("       <AREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AREA_TEXT)))) + "]]></AREA_TEXT>");														//NART
        
        //volume
        out.println("       <VOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_RANGES)) + "]]></VOLUME_RANGES>");																				//NVLR
        out.println("       <VOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TEXT)))) + "]]></VOLUME_TEXT>");													//NVLT
        
        //velocity
        out.println("       <VELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VELOCITY_RANGES)) + "]]></VELOCITY_RANGES>");																			//NVER
        out.println("       <VELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)))) + "]]></VELOCITY_TEXT>");											//NVET
        
        //acceleration
        out.println("       <ACCELERATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ACCELERATION_RANGES)) + "]]></ACCELERATION_RANGES>");																//NACR
        out.println("       <ACCELERATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)))) + "]]></ACCELERATION_TEXT>");								//NACT
        
        //angular velocity
        out.println("       <ANGULARVELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ANGULAR_VELOCITY_RANGES)) + "]]></ANGULARVELOCITY_RANGES>");													//NAVR
        out.println("       <ANGULARVELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)))) + "]]></ANGULARVELOCITY_TEXT>");						//NAVT
        
        //rotational speed 	
        out.println("       <ROTATIONALSPEED_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ROTATIONAL_SPEED_RANGES)) + "]]></ROTATIONALSPEED_RANGES>");													//NRSR
        out.println("       <ROTATIONALSPEED_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)))) + "]]></ROTATIONALSPEED_TEXT>");						//NRST
        
        //age mass
        out.println("       <AGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AGE_RANGES)) + "]]></AGE_RANGES>");																						//NAGR
        out.println("       <AGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGE_TEXT)))) + "]]></AGE_TEXT>");															//NAGT
       
        //molar mass
        out.println("       <MOLARMASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_MASS_RANGES)) + "]]></MOLARMASS_RANGES>");																		//NMMR
        out.println("       <MOLARMASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)))) + "]]></MOLARMASS_TEXT>");										//NMMT
        
        //molality
        out.println("       <MOLALITYOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES)) + "]]></MOLALITYOFSUBSTANCE_RANGES>");										//NMOR
        out.println("       <MOLALITYOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)))) + "]]></MOLALITYOFSUBSTANCE_TEXT>");		//NMOT
        
        //radioactivity
        out.println("       <RADIOACTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIOACTIVITY_RANGES)) + "]]></RADIOACTIVITY_RANGES>");															//NRAR
        out.println("       <RADIOACTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)))) + "]]></RADIOACTIVITY_TEXT>");							//NRAT
        
        //absorbed dose
        out.println("       <ABSORBEDDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ABSORBED_DOSE_RANGES)) + "]]></ABSORBEDDOSE_RANGES>");															//NABR
        out.println("       <ABSORBEDDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)))) + "]]></ABSORBEDDOSE_TEXT>");								//NABT
        
        //radiation exposure
        out.println("       <RADIATIONEXPOSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIATION_EXPOSURE_RANGES)) + "]]></RADIATIONEXPOSURE_RANGES>");												//NRER
        out.println("       <RADIATIONEXPOSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)))) + "]]></RADIATIONEXPOSURE_TEXT>");				//NRET
        
        //Luminance
        out.println("       <LUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINANCE_RANGES)) + "]]></LUMINANCE_RANGES>");																		//NLUR
        out.println("       <LUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)))) + "]]></LUMINANCE_TEXT>");										//NLUT
        
        //Magnetic field strengt
        out.println("       <MAGNETICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES)) + "]]></MAGNETICFIELDSTRENGTH_RANGES>");								//NFSR
        out.println("       <MAGNETICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)))) + "]]></MAGNETICFIELDSTRENGTH_TEXT>");	//NFST
        
        //Spectral_Efficiency
        out.println("       <SPECTRALEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES)) + "]]></SPECTRALEFFICIENCY_RANGES>");											//NSER
        out.println("       <SPECTRALEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)))) + "]]></SPECTRALEFFICIENCY_TEXT>");			//NSET
        
        //Surface_Power_Density
        out.println("       <SURFACEPOWERDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES)) + "]]></SURFACEPOWERDENSITY_RANGES>");										//NSPR
        out.println("       <SURFACEPOWERDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)))) + "]]></SURFACEPOWERDENSITY_TEXT>");		//NSPT
        
        //thermal conductivity
        out.println("       <THERMALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)) + "]]></THERMALCONDUCTIVITY_RANGES>");										//NTCR
        out.println("       <THERMALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)))) + "]]></THERMALCONDUCTIVITY_TEXT>");			//NTCT
        
        //new added on 4/11/2016
        //Decibel isotropic
        out.println("       <DECIBELISOTROPIC_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES)) + "]]></DECIBELISOTROPIC_RANGES>");												//NDIR
        out.println("       <DECIBELISOTROPIC_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)))) + "]]></DECIBELISOTROPIC_TEXT>");					//NDIT
        
        //Decibel milliwatts
        out.println("       <DECIBELMILLIWATTS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES)) + "]]></DECIBELMILLIWATTS_RANGES>");												//NDMR
        out.println("       <DECIBELMILLIWATTS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)))) + "]]></DECIBELMILLIWATTS_TEXT>");				//NDMT
               
        //Equivalent Dose
        out.println("       <EQUIVALENTDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.EQUIVALENT_DOSE_RANGES)) + "]]></EQUIVALENTDOSE_RANGES>");														//NEQR
        out.println("       <EQUIVALENTDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)))) + "]]></EQUIVALENTDOSE_TEXT>");						//NEQT
        
        //Molar concentration
        out.println("       <MOLARCONCENTRATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_CONCENTRATION_RANGES)) + "]]></MOLARCONCENTRATION_RANGES>");											//NMCR
        out.println("       <MOLARCONCENTRATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)))) + "]]></MOLARCONCENTRATION_TEXT>");			//NMCT
        
        //Linear Density
        out.println("       <LINEARDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LINEAR_DENSITY_RANGES)) + "]]></LINEARDENSITY_RANGES>");															//NLDR
        out.println("       <LINEARDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)))) + "]]></LINEARDENSITY_TEXT>");							//NLDT
        
        //luminous efficiency
        out.println("       <LUMINOUSEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES)) + "]]></LUMINOUSEFFICIENCY_RANGES>");											//NLYR
        out.println("       <LUMINOUSEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)))) + "]]></LUMINOUSEFFICIENCY_TEXT>");			//NLYT
        
        //luminous efficacy
        out.println("       <LUMINOUSEFFICACY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICACY_RANGES)) + "]]></LUMINOUSEFFICACY_RANGES>");												//NLER
        out.println("       <LUMINOUSEFFICACY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)))) + "]]></LUMINOUSEFFICACY_TEXT>");					//NLET
        
        //Specific Energy
        out.println("       <SPECIFICENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_ENERGY_RANGES)) + "]]></SPECIFICENERGY_RANGES>");														//NSFR
        out.println("       <SPECIFICENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)))) + "]]></SPECIFICENERGY_TEXT>");						//NSFT
        
        //Specific Surface area
        out.println("       <SPECIFICSURFACEAREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES)) + "]]></SPECIFICSURFACEAREA_RANGES>");										//NSSR
        out.println("       <SPECIFICSURFACEAREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)))) + "]]></SPECIFICSURFACEAREA_TEXT>");		//NSST
        
        //Specific Volume
        out.println("       <SPECIFICVOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_VOLUME_RANGES)) + "]]></SPECIFICVOLUME_RANGES>");														//NSVR
        out.println("       <SPECIFICVOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)))) + "]]></SPECIFICVOLUME_TEXT>");						//NSVT
        
        //Surface Tension
        out.println("       <SURFACETENSION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_TENSION_RANGES)) + "]]></SURFACETENSION_RANGES>");														//NSTR
        out.println("       <SURFACETENSION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_TENSION_TEXT)))) + "]]></SURFACETENSION_TEXT>");						//NSTT
        
        //Surface Density
        out.println("       <SURFACEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_DENSITY_RANGES)) + "]]></SURFACEDENSITY_RANGES>");														//NSDR
        out.println("       <SURFACEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)))) + "]]></SURFACEDENSITY_TEXT>");						//NSDT
        
        
        
        out.println("       <NUMERICAL_UNITS><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.NUMERICALUNITS)))+ "]]></NUMERICAL_UNITS>");																//NUU
        
        //EID
        out.println("       <EID><![CDATA[" + notNull(rec.getString(EVCombinedRec.EID)) + "]]></EID>");	        																									//EID
        out.println("       <DEPARTMENTID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DEPARTMENTID)))) + "]]></DEPARTMENTID>");												//DTID
        
        //added for georef at 03/16/2016
        //TITLE_OF_COLLECTION
        out.println("       <TITLEOFCOLLECTION><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION))) + " QstemQ " +  notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION)))) + "]]></TITLEOFCOLLECTION>");									//TIC
        
        //UNIVERSITY
        out.println("       <UNIVERSITY><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))+ " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))) + "]]></UNIVERSITY>");														//UNI
        
        //TYPE_OF_DEGREE
        out.println("       <TYPEOFDEGREE><![CDATA[" + notNull(rec.getString(EVCombinedRec.TYPE_OF_DEGREE)) + "]]></TYPEOFDEGREE>");																				//TOD
       
        //ANNOTATION
        out.println("       <ANNOTATION><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.ANNOTATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ANNOTATION)))) + "]]></ANNOTATION>");																			//ANN
        
        //MAP_SCALE
        out.println("       <MAPSCALE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_SCALE))) + "]]></MAPSCALE>");																				//MPS
        
        //MAP_TYPE
        out.println("       <MAPTYPE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_TYPE))) + "]]></MAPTYPE>");																					//MPT
        
        //SOURCE_NOTE
        out.println("       <SOURCENOTE><![CDATA[" + notNull(rec.getString(EVCombinedRec.SOURCE_NOTE)) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SOURCE_NOTE))))+ "]]></SOURCENOTE>");																						//SNO
        
        //GRANTID
        out.println("       <GRANTID><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) + "]]></GRANTID>");																					//GID
        
        //GRANTAGENCY
        out.println("       <GRANTAGENCY><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + "]]></GRANTAGENCY>");		//GAG
        
        //SPARE FIELDS
        //SOURCEBIBTEXT
        //out.println("       <EV_SPARE1><![CDATA[]]></EV_SPARE1>");	//tempotary block out SOURCEBIBTEXT
        out.println("       <EV_SPARE1><![CDATA["+ notNull(rec.getString(EVCombinedRec.SOURCEBIBTEXT)) +"]]></EV_SPARE1>");//SPA1
       
        //move standardid to here to get all search
        //STANDARDID
        if(rec.getString(EVCombinedRec.STANDARDID)==null)
        {
        	out.println("       <EV_SPARE2><![CDATA[]]></EV_SPARE2>");
        }
        else      
        {
        	out.println("       <EV_SPARE2><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID))) + "QstemQ " +notNull(getStems(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID)))) +"]]></EV_SPARE2>");//SPA2
        }                     
        
        //out.println("       <EV_SPARE3><![CDATA[]]></EV_SPARE3>");//SPA3
        //added by hmo on 2019/09/11 for inspec orgid
        out.println("       <EV_SPARE3><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORG_ID)))) + "]]></EV_SPARE3>");//SPA3
        
        out.println("       <EV_SPARE4><![CDATA[]]></EV_SPARE4>");//SPA4
        
        out.println("       <EV_SPARE5><![CDATA[]]></EV_SPARE5>");//SPA5
        
        out.println("       <EV_SPARE6><![CDATA[]]></EV_SPARE6>");//SPA6
        
        //move standardid to SPA2 to get search "all"
        //use for ISOPENACESS
        out.println("       <EV_SPARE7><![CDATA["+ notNull(rec.getString(EVCombinedRec.ISOPENACESS)) +"]]></EV_SPARE7>");//SPA7
        
        /*
        //remove SPA7 after new standardid release at 08/29/2018
        if(rec.getString(EVCombinedRec.STANDARDID)==null)
        {
        	out.println("       <EV_SPARE7><![CDATA[]]></EV_SPARE7>");
        }
        else      
        {
        	out.println("       <EV_SPARE7><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID))) + "]]></EV_SPARE7>");//SPA7
        }
        */
        //STANDARDDESIGNATION
        if(rec.getString(EVCombinedRec.STANDARDDESIGNATION)==null)
        {
        	out.println("       <EV_SPARE8><![CDATA[]]></EV_SPARE8>");
        }      
        else
        {
        	out.println("       <EV_SPARE8><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDDESIGNATION))) + "]]></EV_SPARE8>");//SPA8
        }
        
        //GRANTTEXT
        //out.println("       <EV_SPARE9><![CDATA["+ notNull(rec.getString(EVCombinedRec.GRANTTEXT))+ " QstemQ " + notNull(getStems(rec.getString(EVCombinedRec.GRANTTEXT))) +"]]></EV_SPARE9>");//SPA9
        out.println("       <EV_SPARE9><![CDATA[]]></EV_SPARE9>");//SPA9
        out.println("       <EV_SPARE10><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) +" "+ notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + " "+
        			notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTTEXT)))+ " QstemQ " + notNull(getStems(multiFormat(rec.getStrings(EVCombinedRec.GRANTTEXT)))) +"]]></EV_SPARE10>");//SPA0
        
        out.println("   </ROW>");
        ++curRecNum;
        
        //following code used to test kafka by hmo@2019/09/23
        //writeRec(rec,"kafka");
        
        end();
    }
    
    public void writeRec(EVCombinedRec rec, String endpoint)
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
            
            
            
            
            //evObject.add("Content",recordBuffer.toString());
            
            JSONObject  elementObject = new JSONObject();
            JSONObject  contentObject = new JSONObject();
            JSONArray  	elementArrayObject = new JSONArray();;
            JSONArray 	elementJsonArray = new JSONArray();
            JSONArray 	contentJsonArray = new JSONArray();
           
            
            //recordBuffer.append("   <ROW> \n");
                 
            //recordBuffer.append("       <EIDOCID>" + eid + "</EIDOCID>\n");
            
            contentObject.put("EIDOCID",eid);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PARENTID>" +  rec.getString(EVCombinedRec.PARENT_ID) + "</PARENTID>\n");
            contentObject = new JSONObject();
            contentObject.put("PARENTID",notNull(rec.getString(EVCombinedRec.PARENT_ID)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DEDUPKEY>" + rec.getString(EVCombinedRec.DEDUPKEY) + "</DEDUPKEY>\n");
            contentObject = new JSONObject();
            contentObject.put("DEDUPKEY",notNull(rec.getString(EVCombinedRec.DEDUPKEY)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DATABASE>" + rec.getString(EVCombinedRec.DATABASE) + "</DATABASE>\n");
            contentObject = new JSONObject();
            contentObject.put("DATABASE",rec.getString(EVCombinedRec.DATABASE));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LOADNUMBER>" + loadnumber + "</LOADNUMBER>\n");
            contentObject = new JSONObject();
            contentObject.put("LOADNUMBER",loadnumber);
            contentJsonArray.add(contentObject);
            //added for future use only, should be removed for regular database loading
            //recordBuffer.append("       <UPDATENUMBER>" + rec.getString(EVCombinedRec.UPDATE_NUMBER) + "</UPDATENUMBER>\n");
            contentObject = new JSONObject();
            contentObject.put("UPDATENUMBER",notNull(rec.getString(EVCombinedRec.UPDATE_NUMBER)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DATESORT>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.DATESORT))) + "</DATESORT>\n");
            contentObject = new JSONObject();
            contentObject.put("DATESORT",notNull(rec.getString(EVCombinedRec.DATESORT)));           
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PUBYEAR>" + rec.getString(EVCombinedRec.PUB_YEAR) + "</PUBYEAR>");
            contentObject = new JSONObject();          
            contentObject.put("PUBYEAR",rec.getString(EVCombinedRec.PUB_YEAR));           
            contentJsonArray.add(contentObject);
           // recordBuffer.append("       <ACCESSIONNUMBER>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ACCESSION_NUMBER))) + "</ACCESSIONNUMBER>\n");
            contentObject = new JSONObject();          
            contentObject.put("ACCESSIONNUMBER",rec.getString(EVCombinedRec.ACCESSION_NUMBER));
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + "]]></AUTHOR>"); //added QstemQ portion to search both with qqdashqq and without it
            //recordBuffer.append("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + " QstemQ " + notNull(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR))) + "]]></AUTHOR>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR");          
            contentObject.put("AUTHOR",elementArrayObject);
            contentJsonArray.add(contentObject);          
            //recordBuffer.append("       <AUTHORID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHORID)))) + "]]></AUTHORID>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHORID),"AUTHORID");          
            contentObject.put("AUTHORID",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AUTHORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.),"AUTHORAFFILIATION")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))))) + "]]></AUTHORAFFILIATION>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION");          
            contentObject.put("AUTHORAFFILIATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AFFILIATIONLOCATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)))) + "]]></AFFILIATIONLOCATION>");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION),"AFFILIATIONLOCATION");          
            contentObject.put("AFFILIATIONLOCATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //out.println("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>");
            //recordBuffer.append("       <TITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE))))) + "]]></TITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TITLE),"TITLE");          
            contentObject.put("TITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE),"TRANSLATEDTITLE");          
            contentObject.put("TRANSLATEDTITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VOLUMETITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE))))) + "]]></VOLUMETITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_TITLE),"VOLUMETITLE");          
            contentObject.put("VOLUMETITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT)))) + "]]></ABSTRACT>\n");
            contentObject = new JSONObject();
            contentObject.put("ABSTRACT",rec.getString(EVCombinedRec.ABSTRACT));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <OTHERABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT)))) + "]]></OTHERABSTRACT>\n");
            contentObject = new JSONObject();
            contentObject.put("OTHERABSTRACT",notNull(rec.getString(EVCombinedRec.OTHER_ABSTRACT)));
            contentJsonArray.add(contentObject);            
            //recordBuffer.append("       <EDITOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.EDITOR))))+"]]></EDITOR>");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EDITOR),"EDITOR");          
            contentObject.put("EDITOR",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <EDITORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION))))) + "]]></EDITORAFFILIATION>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION),"EDITORAFFILIATION");          
            contentObject.put("EDITORAFFILIATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TRANSLATOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.TRANSLATOR)))) + "]]></TRANSLATOR>");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TRANSLATOR),"TRANSLATOR");          
            contentObject.put("TRANSLATOR",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <CONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS"))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS)))))) + "]]></CONTROLLEDTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS");          
            contentObject.put("CONTROLLEDTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);            
            //recordBuffer.append("       <UNCONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS)))))) + "]]></UNCONTROLLEDTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS),"UNCONTROLLEDTERMS");          
            contentObject.put("UNCONTROLLEDTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);            
            //recordBuffer.append("       <ISSN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN))))) + "]]></ISSN>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ISSN),"ISSN");          
            contentObject.put("ISSN",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <CODEN><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN)))) + "]]></CODEN>\n");            
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CODEN),"CODEN");          
            contentObject.put("CODEN",elementArrayObject);                     
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CODENOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION)))) + "]]></CODENOFTRANSLATION>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION),"CODENOFTRANSLATION");          
            contentObject.put("CODENOFTRANSLATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ISBN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISBN(rec.getStrings(EVCombinedRec.ISBN))))) + "]]></ISBN>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ISBN),"ISBN");          
            contentObject.put("ISBN",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SERIALTITLE><![CDATA[" + notNull(Entity.prepareString(notNull(multiFormat(addIndex(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE)))))) + "]]></SERIALTITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE");          
            contentObject.put("SERIALTITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SERIALTITLETRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION))))) + "]]></SERIALTITLETRANSLATION>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION),"SERIALTITLETRANSLATION");          
            contentObject.put("SERIALTITLETRANSLATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAINHEADING><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING)))))) + "]]></MAINHEADING>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAIN_HEADING),"MAINHEADING");          
            contentObject.put("MAINHEADING",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SUBHEADING><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SUB_HEADING))) + "]]></SUBHEADING>\n");
            contentObject = new JSONObject();
            contentObject.put("SUBHEADING",rec.getString(EVCombinedRec.SUB_HEADING));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PUBLISHERNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PUBLISHER_NAME))))) + "]]></PUBLISHERNAME>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME");          
            contentObject.put("PUBLISHERNAME",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TREATMENTCODE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TREATMENT_CODE)))) + "</TREATMENTCODE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TREATMENT_CODE),"TREATMENTCODE");          
            contentObject.put("TREATMENTCODE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LANGUAGE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LANGUAGE)))) + "]]></LANGUAGE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LANGUAGE),"LANGUAGE");          
            contentObject.put("LANGUAGE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <RECTYPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOCTYPE)))) + "]]></RECTYPE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DOCTYPE),"RECTYPE");          
            contentObject.put("RECTYPE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CLASSIFICATIONCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE)))) + "]]></CLASSIFICATIONCODE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE),"CLASSIFICATIONCODE");          
            contentObject.put("CLASSIFICATIONCODE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCECODE>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_CODE))) + "</CONFERENCECODE>\n");                     
            contentObject = new JSONObject();
            contentObject.put("CONFERENCECODE",notNull(rec.getString(EVCombinedRec.CONFERENCE_CODE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCENAME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME)))) + "]]></CONFERENCENAME>\n");                    
            contentObject = new JSONObject();
            contentObject.put("CONFERENCENAME",notNull(rec.getString(EVCombinedRec.CONFERENCE_NAME)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCELOCATION><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)))) + "]]></CONFERENCELOCATION>\n");
            contentObject = new JSONObject();
            contentObject.put("CONFERENCELOCATION",notNull(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MEETINGDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.MEETING_DATE))) + "]]></MEETINGDATE>\n");
            contentObject = new JSONObject();
            contentObject.put("MEETINGDATE",notNull(rec.getString(EVCombinedRec.MEETING_DATE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SPONSORNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME))))) + "]]></SPONSORNAME>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPONSOR_NAME),"SPONSORNAME");          
            contentObject.put("SPONSORNAME",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MONOGRAPHTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE))))) + "]]></MONOGRAPHTITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPONSOR_NAME),"MONOGRAPHTITLE");          
            contentObject.put("MONOGRAPHTITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DISCIPLINE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DISCIPLINE)))) + "</DISCIPLINE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DISCIPLINE),"DISCIPLINE");          
            contentObject.put("DISCIPLINE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MATERIALNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MATERIAL_NUMBER)))) + "]]></MATERIALNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MATERIAL_NUMBER),"MATERIALNUMBER");          
            contentObject.put("MATERIALNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <NUMERICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING)))) + "]]></NUMERICALINDEXING>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING),"NUMERICALINDEXING");          
            contentObject.put("NUMERICALINDEXING",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CHEMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING)))) + "]]></CHEMICALINDEXING>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING),"CHEMICALINDEXING");          
            contentObject.put("CHEMICALINDEXING",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ASTRONOMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING)))) + "]]></ASTRONOMICALINDEXING>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING),"ASTRONOMICALINDEXING");          
            contentObject.put("ASTRONOMICALINDEXING",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <REPORTNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REPORTNUMBER)))) + "]]></REPORTNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.REPORTNUMBER),"REPORTNUMBER");          
            contentObject.put("REPORTNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ORDERNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORDERNUMBER)))) + "]]></ORDERNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ORDERNUMBER),"ORDERNUMBER");          
            contentObject.put("ORDERNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <COUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COUNTRY)))) + "]]></COUNTRY>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.COUNTRY),"COUNTRY");          
            contentObject.put("COUNTRY",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VOLUME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.VOLUME))) + "]]></VOLUME>\n");
            contentObject = new JSONObject();
            contentObject.put("VOLUME",notNull(rec.getString(EVCombinedRec.VOLUME)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ISSUE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ISSUE))) + "]]></ISSUE>\n");
            contentObject = new JSONObject();
            contentObject.put("ISSUE",notNull(rec.getString(EVCombinedRec.ISSUE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <STARTPAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STARTPAGE))) + "]]></STARTPAGE>\n");
            contentObject = new JSONObject();
            contentObject.put("STARTPAGE",notNull(rec.getString(EVCombinedRec.STARTPAGE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.PAGE))) + "]]></PAGE>\n");
            contentObject = new JSONObject();
            contentObject.put("PAGE",notNull(rec.getString(EVCombinedRec.PAGE)));
            contentJsonArray.add(contentObject);
            /*
            if(rec.getString(EVCombinedRec.AVAILABILITY)!=null)
            {
            	recordBuffer.append("		<AVAILABILITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY)))) + "]]></AVAILABILITY>\n");
            }
            else
            {
            	recordBuffer.append("		<AVAILABILITY><![CDATA[]]></AVAILABILITY>\n");
            }
            */
            contentObject = new JSONObject();
            contentObject.put("AVAILABILITY",rec.getString(EVCombinedRec.AVAILABILITY));
            contentJsonArray.add(contentObject);
            /*
            if(rec.getStrings(EVCombinedRec.NOTES)!=null)
            {
            	recordBuffer.append("       <NOTES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES))))) + "]]></NOTES>\n");
            }
            else
            {
            	recordBuffer.append("       <NOTES><![CDATA[]]></NOTES>\n");
            }
            */
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NOTES),"NOTES");          
            contentObject.put("NOTES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PATENTAPPDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTAPPDATE)))) + "]]></PATENTAPPDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENTAPPDATE),"PATENTAPPDATE");          
            contentObject.put("PATENTAPPDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PATENTISSUEDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTISSUEDATE)))) + "]]></PATENTISSUEDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENTISSUEDATE),"PATENTISSUEDATE");          
            contentObject.put("PATENTISSUEDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <COMPANIES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COMPANIES)))) + "]]></COMPANIES>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.COMPANIES),"COMPANIES");          
            contentObject.put("COMPANIES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CASREGISTRYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER)))) + "]]></CASREGISTRYNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER),"CASREGISTRYNUMBER");          
            contentObject.put("CASREGISTRYNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            /*
            if(rec.getStrings(EVCombinedRec.BUSINESSTERMS)!=null)
            {
            	recordBuffer.append("       <BUSINESSTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS))))) + "]]></BUSINESSTERMS>\n");
            }
            else
            {
            	recordBuffer.append("       <BUSINESSTERMS><![CDATA[]]></BUSINESSTERMS>\n");
            }
            */
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BUSINESSTERMS),"BUSINESSTERMS");          
            contentObject.put("BUSINESSTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            /*
            if(rec.getStrings(EVCombinedRec.CHEMICALTERMS)!=null)
            {
            	recordBuffer.append("       <CHEMICALTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS))))) + "]]></CHEMICALTERMS>\n");
            }
            else
            {
            	recordBuffer.append("       <CHEMICALTERMS><![CDATA[]]></CHEMICALTERMS>\n");
            }
            */
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICALTERMS),"CHEMICALTERMS");          
            contentObject.put("CHEMICALTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CHEMAC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS)))) + "]]></CHEMAC>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS),"CHEMAC");          
            contentObject.put("CHEMAC",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SIC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_NUMBER)))) + "]]></SIC>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_NUMBER),"SIC");          
            contentObject.put("SIC",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <INDUSTRIALCODES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALCODES)))) + "]]></INDUSTRIALCODES>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUSTRIALCODES),"INDUSTRIALCODES");          
            contentObject.put("INDUSTRIALCODES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <INDUSTRIALSECTORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS)))) + "]]></INDUSTRIALSECTORS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS),"INDUSTRIALSECTORS");          
            contentObject.put("INDUSTRIALSECTORS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SCOPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SCOPE)))) + "]]></SCOPE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SCOPE),"SCOPE");          
            contentObject.put("SCOPE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AGENCY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGENCY)))) + "]]></AGENCY>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGENCY),"AGENCY");          
            contentObject.put("AGENCY",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DERWENTACCESSIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER)))) + "]]></DERWENTACCESSIONNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER),"DERWENTACCESSIONNUMBER");          
            contentObject.put("DERWENTACCESSIONNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            
            //recordBuffer.append("       <APPLICATIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER)))) + "]]></APPLICATIONNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER),"APPLICATIONNUMBER");          
            contentObject.put("APPLICATIONNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <APPLICATIONCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY))))+ "]]></APPLICATIONCOUNTRY>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY),"APPLICATIONCOUNTRY");          
            contentObject.put("APPLICATIONCOUNTRY",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <INTPATENTCLASSIFICATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION")))) + "]]></INTPATENTCLASSIFICATION>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTPATENTCLASSIFICATION");          
            contentObject.put("INTPATENTCLASSIFICATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            /*
            if(rec.getStrings(EVCombinedRec.LINKED_TERMS)!=null)
            {
            	recordBuffer.append("       <LINKEDTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS))))) + "]]></LINKEDTERMS>\n");
            }
            else
            {
            	recordBuffer.append("       <LINKEDTERMS><![CDATA[]]></LINKEDTERMS>\n");
            }
            */
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINKED_TERMS),"LINKEDTERMS");          
            contentObject.put("LINKEDTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ENTRYYEAR><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ENTRY_YEAR))) + "]]></ENTRYYEAR>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENTRY_YEAR),"ENTRYYEAR");          
            contentObject.put("ENTRYYEAR",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRIORITYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER)))) + "]]></PRIORITYNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER),"PRIORITYNUMBER");          
            contentObject.put("PRIORITYNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRIORITYDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_DATE)))) + "]]></PRIORITYDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_DATE),"PRIORITYDATE");          
            contentObject.put("PRIORITYDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRIORITYCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY))))+ "]]></PRIORITYCOUNTRY>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY),"PRIORITYCOUNTRY");          
            contentObject.put("PRIORITYCOUNTRY",elementArrayObject);
            contentJsonArray.add(contentObject);
            /*
            if(rec.getStrings(EVCombinedRec.SOURCE)!=null)
            {
            	recordBuffer.append("       <SOURCE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE))))) + "]]></SOURCE>\n");
            }
            else
            {
            	recordBuffer.append("       <SOURCE><![CDATA[]]></SOURCE>\n");
            }
            */
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SOURCE),"SOURCE");          
            contentObject.put("SOURCE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SECONDARYSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE)))) + "]]></SECONDARYSRCTITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SECONDARY_SRC_TITLE),"SECONDARYSRCTITLE");          
            contentObject.put("SECONDARYSRCTITLE",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <MAINTERM><![CDATA[" + notNull((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM)))) + " QstemQ " + notNull(getStems((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM))))) + "]]></MAINTERM>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAIN_TERM),"MAINTERM");          
            contentObject.put("MAINTERM",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ABBRVSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE)))) + "]]></ABBRVSRCTITLE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABBRV_SRC_TITLE),"ABBRVSRCTITLE");          
            contentObject.put("ABBRVSRCTITLE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <NOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS))))) + "]]></NOROLETERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NOROLE_TERMS),"NOROLETERMS");          
            contentObject.put("NOROLETERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <REAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS))))) + "]]></REAGENTTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.REAGENT_TERMS),"REAGENTTERMS");          
            contentObject.put("REAGENTTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS))))) + "]]></PRODUCTTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRODUCT_TERMS),"PRODUCTTERMS");          
            contentObject.put("PRODUCTTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAJORNOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS))))) + "]]></MAJORNOROLETERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS),"MAJORNOROLETERMS");          
            contentObject.put("MAJORNOROLETERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAJORREAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS))))) + "]]></MAJORREAGENTTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS),"MAJORREAGENTTERMS");          
            contentObject.put("MAJORREAGENTTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAJORPRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS))))) + "]]></MAJORPRODUCTTERMS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS),"MAJORPRODUCTTERMS");          
            contentObject.put("MAJORPRODUCTTERMS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCEAFFILIATIONS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS))))) + "]]></CONFERENCEAFFILIATIONS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS),"CONFERENCEAFFILIATIONS");          
            contentObject.put("CONFERENCEAFFILIATIONS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCEEDITORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS))))) + "]]></CONFERENCEEDITORS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS),"CONFERENCEEDITORS");          
            contentObject.put("CONFERENCEEDITORS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCESTARTDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCESTARTDATE))) + "]]></CONFERENCESTARTDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCESTARTDATE),"CONFERENCESTARTDATE");          
            contentObject.put("CONFERENCESTARTDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCEENDDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEENDDATE))) + "]]></CONFERENCEENDDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEENDDATE),"CONFERENCEENDDATE");          
            contentObject.put("CONFERENCEENDDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            
            //recordBuffer.append("       <CONFERENCEVENUESITE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE)))) + "]]></CONFERENCEVENUESITE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEVENUESITE),"CONFERENCEVENUESITE");          
            contentObject.put("CONFERENCEVENUESITE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCECITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY)))) + "]]></CONFERENCECITY>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCECITY),"CONFERENCECITY");          
            contentObject.put("CONFERENCECITY",elementArrayObject);
            contentJsonArray.add(contentObject);          
            //recordBuffer.append("       <CONFERENCECOUNTRYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECOUNTRYCODE))) + "]]></CONFERENCECOUNTRYCODE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCECOUNTRYCODE),"CONFERENCECOUNTRYCODE");          
            contentObject.put("CONFERENCECOUNTRYCODE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCEPAGERANGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPAGERANGE))) + "]]></CONFERENCEPAGERANGE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEPAGERANGE),"CONFERENCEPAGERANGE");          
            contentObject.put("CONFERENCEPAGERANGE",elementArrayObject);
            contentJsonArray.add(contentObject);          
            //recordBuffer.append("       <CONFERENCENUMBERPAGES><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCENUMBERPAGES))) + "]]></CONFERENCENUMBERPAGES>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCENUMBERPAGES),"CONFERENCENUMBERPAGES");          
            contentObject.put("CONFERENCENUMBERPAGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CONFERENCEPARTNUMBER><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPARTNUMBER))) + "]]></CONFERENCEPARTNUMBER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEPARTNUMBER),"CONFERENCEPARTNUMBER");          
            contentObject.put("CONFERENCEPARTNUMBER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DESIGNATEDSTATES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DESIGNATED_STATES)))) + "]]></DESIGNATEDSTATES>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DESIGNATED_STATES),"DESIGNATEDSTATES");          
            contentObject.put("DESIGNATEDSTATES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <STNCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE)))) + "]]></STNCONFERENCE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.STN_CONFERENCE),"STNCONFERENCE");          
            contentObject.put("STNCONFERENCE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <STNSECONDARYCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE)))) + "]]></STNSECONDARYCONFERENCE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.STN_SECONDARY_CONFERENCE),"STNSECONDARYCONFERENCE");          
            contentObject.put("STNSECONDARYCONFERENCE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <FILINGDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_FILING_DATE)))) + "]]></FILINGDATE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_FILING_DATE),"FILINGDATE");          
            contentObject.put("FILINGDATE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRIORITYKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_KIND)))) + "]]></PRIORITYKIND>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_KIND),"PRIORITYKIND");          
            contentObject.put("PRIORITYKIND",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ECLACODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CODES)))) +"]]></ECLACODE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_CODES),"ECLACODE");          
            contentObject.put("ECLACODE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ATTORNEYNAME><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ATTORNEY_NAME)))) + "]]></ATTORNEYNAME>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ATTORNEY_NAME),"ATTORNEYNAME");          
            contentObject.put("ATTORNEYNAME",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRIMARYEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER)))) + "]]></PRIMARYEXAMINER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER),"PRIMARYEXAMINER");          
            contentObject.put("PRIMARYEXAMINER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ASSISTANTEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER)))) + "]]></ASSISTANTEXAMINER>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER),"ASSISTANTEXAMINER");          
            contentObject.put("ASSISTANTEXAMINER",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <IPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES)))) + "]]></IPCCLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES),"IPCCLASS");          
            contentObject.put("IPCCLASS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <IPCSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES)))) + "]]></IPCSUBCLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES),"IPCCLASS");          
            contentObject.put("IPCCLASS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ECLACLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CLASSES)))) + "]]></ECLACLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_CLASSES),"ECLACLASS");          
            contentObject.put("ECLACLASS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ECLASUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES)))) + "]]></ECLASUBCLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES),"ECLASUBCLASS");          
            contentObject.put("ECLASUBCLASS",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <USPTOCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCLASS)))) + "]]></USPTOCLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOCLASS),"USPTOCLASS");          
            contentObject.put("USPTOCLASS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <USPTOSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOSUBCLASS)))) + "]]></USPTOSUBCLASS>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOSUBCLASS),"USPTOSUBCLASS");          
            contentObject.put("USPTOSUBCLASS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <USPTOCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCODE)))) + "]]></USPTOCODE>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOCODE),"USPTOCODE");          
            contentObject.put("USPTOCODE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PATENTKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_KIND)))) + "]]></PATENTKIND>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_KIND),"PATENTKIND");          
            contentObject.put("PATENTKIND",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <KINDDESCRIPTION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR))))) + "]]></KINDDESCRIPTION>\n");        
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.KIND_DESCR),"KINDDESCRIPTION");          
            contentObject.put("KINDDESCRIPTION",elementArrayObject);
            contentJsonArray.add(contentObject);            
            //recordBuffer.append("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AUTHORITY_CODE))) + "]]></AUTHORITYCODE>\n");
            contentObject = new JSONObject();
            contentObject.put("AUTHORITYCODE",notNull(rec.getString(EVCombinedRec.AUTHORITY_CODE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PCITED><![CDATA[" + hasPcited(rec.getString(EVCombinedRec.PCITED)) + "]]></PCITED>\n");         
            contentObject = new JSONObject();
            contentObject.put("PCITED",notNull(rec.getString(EVCombinedRec.PCITED)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PCITEDINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PCITEDINDEX)))) + "]]></PCITEDINDEX>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PCITEDINDEX),"PCITEDINDEX");          
            contentObject.put("PCITEDINDEX",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PREFINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PREFINDEX)))) + "]]></PREFINDEX>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PREFINDEX),"PREFINDEX");          
            contentObject.put("PREFINDEX",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DMASK><![CDATA[" + getMask(rec) + "]]></DMASK>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DMASK),"DMASK");          
            contentObject.put("DMASK",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DOI><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOI)))) + hasDOI(rec) + "]]></DOI>\n");         
            contentObject = new JSONObject();
            contentObject.put("DOI",notNull(rec.getString(EVCombinedRec.DOI)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SCOPUSID><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SCOPUSID))) + "]]></SCOPUSID>\n");   
            contentObject = new JSONObject();
            contentObject.put("SCOPUSID",notNull(rec.getString(EVCombinedRec.SCOPUSID)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AFFILIATIONID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATIONID)))) + "]]></AFFILIATIONID>\n");
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AFFILIATIONID),"AFFILIATIONID");          
            contentObject.put("AFFILIATIONID",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LAT_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NW))) + "]]></LAT_NW>\n");       
            contentObject = new JSONObject();
            contentObject.put("LAT_NW",notNull(rec.getString(EVCombinedRec.LAT_NW)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LNG_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NW))) + "]]></LNG_NW>\n");        
            contentObject = new JSONObject();
            contentObject.put("LNG_NW",notNull(rec.getString(EVCombinedRec.LNG_NW)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LAT_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NE))) + "]]></LAT_NE>\n");         
            contentObject = new JSONObject();
            contentObject.put("LAT_NE",notNull(rec.getString(EVCombinedRec.LAT_NE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LNG_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NE))) + "]]></LNG_NE>\n");        
            contentObject = new JSONObject();
            contentObject.put("LNG_NE",notNull(rec.getString(EVCombinedRec.LNG_NE)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LAT_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SW))) + "]]></LAT_SW>\n");         
            contentObject = new JSONObject();
            contentObject.put("LAT_SW",notNull(rec.getString(EVCombinedRec.LAT_SW)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LNG_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SW))) + "]]></LNG_SW>\n");       
            contentObject = new JSONObject();
            contentObject.put("LNG_SW",notNull(rec.getString(EVCombinedRec.LNG_SW)));
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <LAT_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SE))) + "]]></LAT_SE>\n");                
            contentObject = new JSONObject();
            contentObject.put("LAT_SE",notNull(rec.getString(EVCombinedRec.LAT_SE)));
            contentJsonArray.add(contentObject);
            contentObject = new JSONObject();
            recordBuffer.append("       <LNG_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SE))) + "]]></LNG_SE>\n");                
            contentObject.put("LNG_SE",notNull(rec.getString(EVCombinedRec.LNG_SE)));
            contentJsonArray.add(contentObject);
 //         recordBuffer.append("       <CPCCLASS><![CDATA[]]></CPCCLASS>\n");
            
//          recordBuffer.append("       <CPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CPCCLASS)))) + "]]></CPCCLASS>");
            //recordBuffer.append("       <TABLEOFCONTENT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT)))) + "]]></TABLEOFCONTENT>\n");										//TOC
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT),"TABLEOFCONTENT");          
            contentObject.put("TABLEOFCONTENT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //************************************************ added for numericalIndex ******************************************************//
            
            
            //amount of substance
            //recordBuffer.append("       <AMOUNTOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES)) + "]]></AMOUNTOFSUBSTANCE_RANGES>\n"); 											//NASR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES),"AMOUNTOFSUBSTANCE_RANGES");          
            contentObject.put("AMOUNTOFSUBSTANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AMOUNTOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)))) + "]]></AMOUNTOFSUBSTANCE_TEXT>\n");				//NAST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT),"AMOUNTOFSUBSTANCE_TEXT");          
            contentObject.put("AMOUNTOFSUBSTANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electric current
            //recordBuffer.append("       <ELECTRICCURRENT_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_CURRENT_RANGES)) + "]]></ELECTRICCURRENT_RANGES>\n");													//NECR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES),"ELECTRICCURRENT_RANGES");          
            contentObject.put("ELECTRICCURRENT_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICCURRENT_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)))) + "]]></ELECTRICCURRENT_TEXT>\n");						//NECT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT),"ELECTRICCURRENT_TEXT");          
            contentObject.put("ELECTRICCURRENT_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //mass
            //recordBuffer.append("       <MASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_RANGES)) + "]]></MASS_RANGES>\n");																						//NMAR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_RANGES),"MASS_RANGES");          
            contentObject.put("MASS_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_TEXT)))) + "]]></MASS_TEXT>\n");														//NMAT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_TEXT),"MASS_TEXT");          
            contentObject.put("MASS_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //temperature
            //recordBuffer.append("       <TEMPERATURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TEMPERATURE_RANGES)) + "]]></TEMPERATURE_RANGES>\n");																//NTER
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES),"TEMPERATURE_RANGES");          
            contentObject.put("TEMPERATURE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TEMPERATURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)))) + "]]></TEMPERATURE_TEXT>\n");									//NTET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT),"TEMPERATURE_TEXT");          
            contentObject.put("TEMPERATURE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //time
            //recordBuffer.append("       <TIME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TIME_RANGES)) + "]]></TIME_RANGES>\n");																						//NTIR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_RANGES),"TIME_RANGES");          
            contentObject.put("TIME_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TIME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TIME_TEXT)))) + "]]></TIME_TEXT>\n");														//NTIT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_TEXT),"TIME_TEXT");          
            contentObject.put("TIME_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //size
            //recordBuffer.append("       <SIZE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SIZE_RANGES)) + "]]></SIZE_RANGES>\n");																						//NSIR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_RANGES),"SIZE_RANGES");          
            contentObject.put("SIZE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SIZE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SIZE_TEXT)))) + "]]></SIZE_TEXT>\n");														//NSIT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_TEXT),"SIZE_TEXT");          
            contentObject.put("SIZE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electrical conductance
            //recordBuffer.append("       <ELECTRICALCONDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES)) + "]]></ELECTRICALCONDUCTANCE_RANGES>\n");									//NEDR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES),"ELECTRICALCONDUCTANCE_RANGES");          
            contentObject.put("ELECTRICALCONDUCTANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICALCONDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)))) + "]]></ELECTRICALCONDUCTANCE_TEXT>\n");	//NEDT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT),"ELECTRICALCONDUCTANCE_TEXT");          
            contentObject.put("ELECTRICALCONDUCTANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electrical conductivity
            //recordBuffer.append("       <ELECTRICALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES)) + "]]></ELECTRICALCONDUCTIVITY_RANGES>\n");								//NETR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES),"ELECTRICALCONDUCTIVITY_RANGES");          
            contentObject.put("ELECTRICALCONDUCTIVITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)))) + "]]></ELECTRICALCONDUCTIVITY_TEXT>\n");//NETT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT),"ELECTRICALCONDUCTIVITY_TEXT");          
            contentObject.put("ELECTRICALCONDUCTIVITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //voltage
            //recordBuffer.append("       <VOLTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLTAGE_RANGES)) + "]]></VOLTAGE_RANGES>\n");																			//NVOR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES),"VOLTAGE_RANGES");          
            contentObject.put("VOLTAGE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VOLTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)))) + "]]></VOLTAGE_TEXT>\n");												//NVOT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT),"VOLTAGE_TEXT");          
            contentObject.put("VOLTAGE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electric field strength
            //recordBuffer.append("       <ELECTRICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES)) + "]]></ELECTRICFIELDSTRENGTH_RANGES>\n");								//NEFR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES),"ELECTRICFIELDSTRENGTH_RANGES");          
            contentObject.put("ELECTRICFIELDSTRENGTH_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)))) + "]]></ELECTRICFIELDSTRENGTH_TEXT>\n");	//NEFT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT),"ELECTRICFIELDSTRENGTH_TEXT");          
            contentObject.put("ELECTRICFIELDSTRENGTH_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //current density
            //recordBuffer.append("       <CURRENTDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CURRENT_DENSITY_RANGES)) + "]]></CURRENTDENSITY_RANGES>\n");														//NCDR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES),"CURRENTDENSITY_RANGES");          
            contentObject.put("CURRENTDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CURRENTDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)))) + "]]></CURRENTDENSITY_TEXT>\n");						//NCDT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT),"CURRENTDENSITY_TEXT");          
            contentObject.put("CURRENTDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //energy
            //recordBuffer.append("       <ENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ENERGY_RANGES)) + "]]></ENERGY_RANGES>\n");																				//NENR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_RANGES),"ENERGY_RANGES");          
            contentObject.put("ENERGY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ENERGY_TEXT)))) + "]]></ENERGY_TEXT>\n");													//NENT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_TEXT),"ENERGY_TEXT");          
            contentObject.put("ENERGY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electrical resistance
            //recordBuffer.append("       <ELECTRICALRESISTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES)) + "]]></ELECTRICALRESISTANCE_RANGES>\n");									//NERR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES),"ELECTRICALRESISTANCE_RANGES");          
            contentObject.put("ELECTRICALRESISTANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICALRESISTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)))) + "]]></ELECTRICALRESISTANCE_TEXT>\n");		//NERT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT),"ELECTRICALRESISTANCE_TEXT");          
            contentObject.put("ELECTRICALRESISTANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electrical resistivity
            //recordBuffer.append("       <ELECTRICALRESISTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES)) + "]]></ELECTRICALRESISTIVITY_RANGES>\n");									//NESR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES),"ELECTRICALRESISTIVITY_RANGES");          
            contentObject.put("ELECTRICALRESISTIVITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRICALRESISTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)))) + "]]></ELECTRICALRESISTIVITY_TEXT>\n");	//NEST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT),"ELECTRICALRESISTIVITY_TEXT");          
            contentObject.put("ELECTRICALRESISTIVITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //electron volt energy
            //recordBuffer.append("       <ELECTRONVOLTENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRON_VOLT_RANGES)) + "]]></ELECTRONVOLTENERGY_RANGES>\n");												//NEVR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES),"ELECTRONVOLTENERGY_RANGES");          
            contentObject.put("ELECTRONVOLTENERGY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ELECTRONVOLTENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)))) + "]]></ELECTRONVOLTENERGY_TEXT>\n");					//NEVT	
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT),"ELECTRONVOLTENERGY_TEXT");          
            contentObject.put("ELECTRONVOLTENERGY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //capacitance
            //recordBuffer.append("       <CAPACITANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CAPACITANCE_RANGES)) + "]]></CAPACITANCE_RANGES>\n");																//NCAR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES),"CAPACITANCE_RANGES");          
            contentObject.put("CAPACITANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <CAPACITANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)))) + "]]></CAPACITANCE_TEXT>\n");									//NCAT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT),"CAPACITANCE_TEXT");          
            contentObject.put("CAPACITANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //frequency	
            //recordBuffer.append("       <FREQUENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FREQUENCY_RANGES)) + "]]></FREQUENCY_RANGES>\n");																		//NFRR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES),"FREQUENCY_RANGES");          
            contentObject.put("FREQUENCY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <FREQUENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)))) + "]]></FREQUENCY_TEXT>\n");										//NFRT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT),"FREQUENCY_TEXT");          
            contentObject.put("FREQUENCY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //power
            //recordBuffer.append("       <POWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.POWER_RANGES)) + "]]></POWER_RANGES>\n");																					//NPOR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_RANGES),"POWER_RANGES");          
            contentObject.put("POWER_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <POWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.POWER_TEXT)))) + "]]></POWER_TEXT>\n");													//NPOT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_TEXT),"POWER_TEXT");          
            contentObject.put("POWER_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //apparent power 
            //recordBuffer.append("       <APPARENTPOWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.APPARENT_POWER_RANGES)) + "]]></APPARENTPOWER_RANGES>\n");															//NAPR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES),"APPARENTPOWER_RANGES");          
            contentObject.put("APPARENTPOWER_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <APPARENTPOWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)))) + "]]></APPARENTPOWER_TEXT>\n");							//NAPT							
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT),"APPARENTPOWER_TEXT");          
            contentObject.put("APPARENTPOWER_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //percentage
            //recordBuffer.append("       <PERCENTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PERCENTAGE_RANGES)) + "]]></PERCENTAGE_RANGES>\n");																	//NPCR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES),"PERCENTAGE_RANGES");          
            contentObject.put("PERCENTAGE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PERCENTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)))) + "]]></PERCENTAGE_TEXT>\n");										//NPCT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT),"PERCENTAGE_TEXT");          
            contentObject.put("PERCENTAGE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //magnetic flux density
            //recordBuffer.append("       <MAGNETICFLUXDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES)) + "]]></MAGNETICFLUXDENSITY_RANGES>\n");										//NMDR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES),"MAGNETICFLUXDENSITY_RANGES");          
            contentObject.put("MAGNETICFLUXDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAGNETICFLUXDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)))) + "]]></MAGNETICFLUXDENSITY_TEXT>\n");		//NMDT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT),"MAGNETICFLUXDENSITY_TEXT");          
            contentObject.put("MAGNETICFLUXDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //inductance
            //recordBuffer.append("       <INDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.INDUCTANCE_RANGES)) + "]]></INDUCTANCE_RANGES>\n");																	//NINR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES),"INDUCTANCE_RANGES");          
            contentObject.put("INDUCTANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <INDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)))) + "]]></INDUCTANCE_TEXT>\n");										//NINT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT),"INDUCTANCE_TEXT");          
            contentObject.put("INDUCTANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //volume charge density
            //recordBuffer.append("       <VOLUMECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES)) + "]]></VOLUMECHARGEDENSITY_RANGES>\n");										//NVCR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES),"VOLUMECHARGEDENSITY_RANGES");          
            contentObject.put("VOLUMECHARGEDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VOLUMECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)))) + "]]></VOLUMECHARGEDENSITY_TEXT>\n");		//NVCT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT),"VOLUMECHARGEDENSITY_TEXT");          
            contentObject.put("VOLUMECHARGEDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //surface charge density
            //recordBuffer.append("       <SURFACECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES)) + "]]></SURFACECHARGEDENSITY_RANGES>\n");									//NSCR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES),"SURFACECHARGEDENSITY_RANGES");          
            contentObject.put("SURFACECHARGEDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SURFACECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)))) + "]]></SURFACECHARGEDENSITY_TEXT>\n");		//NSCT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT),"SURFACECHARGEDENSITY_TEXT");          
            contentObject.put("SURFACECHARGEDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //decibel
            //recordBuffer.append("       <DECIBEL_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_RANGES)) + "]]></DECIBEL_RANGES>\n");																			//NDER
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_RANGES),"DECIBEL_RANGES");          
            contentObject.put("DECIBEL_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DECIBEL_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)))) + "]]></DECIBEL_TEXT>\n");												//NDET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_TEXT),"DECIBEL_TEXT");          
            contentObject.put("DECIBEL_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //luminous flux
            //recordBuffer.append("       <LUMINOUSFLUX_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_FLUX_RANGES)) + "]]></LUMINOUSFLUX_RANGES>\n");															//NLFR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_RANGES),"LUMINOUSFLUX_RANGES");          
            contentObject.put("LUMINOUSFLUX_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LUMINOUSFLUX_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)))) + "]]></LUMINOUSFLUX_TEXT>\n");								//NLFT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT),"LUMINOUSFLUX_TEXT");          
            contentObject.put("LUMINOUSFLUX_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //illuminance
            //recordBuffer.append("       <ILLUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ILLUMINANCE_RANGES)) + "]]></ILLUMINANCE_RANGES>\n");																//NILR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ILLUMINANCE_RANGES),"ILLUMINANCE_RANGES");          
            contentObject.put("ILLUMINANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ILLUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)))) + "]]></ILLUMINANCE_TEXT>\n");									//NILT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT),"ILLUMINANCE_TEXT");          
            contentObject.put("ILLUMINANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //bit rate
            //recordBuffer.append("       <BITRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.BIT_RATE_RANGES)) + "]]></BITRATE_RANGES>\n");																			//NBIR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BIT_RATE_RANGES),"BITRATE_RANGES");          
            contentObject.put("BITRATE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <BITRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)))) + "]]></BITRATE_TEXT>\n");												//NBIT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT),"BITRATE_TEXT");          
            contentObject.put("BITRATE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //mass density
            //recordBuffer.append("       <MASSDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_DENSITY_RANGES)) + "]]></MASSDENSITY_RANGES>\n");																//NMSR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_DENSITY_RANGES),"MASSDENSITY_RANGES");          
            contentObject.put("MASSDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MASSDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)))) + "]]></MASSDENSITY_TEXT>\n");									//NMST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT),"MASSDENSITY_TEXT");          
            contentObject.put("MASSDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //mass flow rate
            //recordBuffer.append("       <MASSFLOWRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_FLOW_RATE_RANGES)) + "]]></MASSFLOWRATE_RANGES>\n");																	//NMRR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_RANGES),"MASSFLOWRATE_RANGES");          
            contentObject.put("MASSFLOWRATE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MASSFLOWRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)))) + "]]></MASSFLOWRATE_TEXT>\n");								//NMRT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT),"MASSFLOWRATE_TEXT");          
            contentObject.put("MASSFLOWRATE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //force
            //recordBuffer.append("       <FORCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FORCE_RANGES)) + "]]></FORCE_RANGES>\n");																					//NFOR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FORCE_RANGES),"FORCE_RANGES");          
            contentObject.put("FORCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <FORCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FORCE_TEXT)))) + "]]></FORCE_TEXT>\n");													//NFOT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FORCE_TEXT),"FORCE_TEXT");          
            contentObject.put("FORCE_TEXT",elementArrayObject);
            
            //torque
            //recordBuffer.append("       <TORQUE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TORQUE_RANGES)) + "]]></TORQUE_RANGES>\n");																				//NTOR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TORQUE_RANGES),"TORQUE_RANGES");          
            contentObject.put("TORQUE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <TORQUE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TORQUE_TEXT)))) + "]]></TORQUE_TEXT>\n");													//NTOT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TORQUE_TEXT),"TORQUE_TEXT");          
            contentObject.put("TORQUE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //pressure
            //recordBuffer.append("       <PRESSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PRESSURE_RANGES)) + "]]></PRESSURE_RANGES>\n");																			//NPRR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRESSURE_RANGES),"PRESSURE_RANGES");          
            contentObject.put("PRESSURE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <PRESSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)))) + "]]></PRESSURE_TEXT>\n");											//NPRT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRESSURE_TEXT),"PRESSURE_TEXT");          
            contentObject.put("PRESSURE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //area
            //recordBuffer.append("       <AREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AREA_RANGES)) + "]]></AREA_RANGES>\n");																						//NARR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AREA_RANGES),"AREA_RANGES");          
            contentObject.put("AREA_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AREA_TEXT)))) + "]]></AREA_TEXT>\n");														//NART
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AREA_TEXT),"AREA_TEXT");          
            contentObject.put("AREA_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //volume
            //recordBuffer.append("       <VOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_RANGES)) + "]]></VOLUME_RANGES>\n");																				//NVLR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_RANGES),"VOLUME_RANGES");          
            contentObject.put("VOLUME_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TEXT)))) + "]]></VOLUME_TEXT>\n");													//NVLT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_TEXT),"VOLUME_TEXT");          
            contentObject.put("VOLUME_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //velocity
            //recordBuffer.append("       <VELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VELOCITY_RANGES)) + "]]></VELOCITY_RANGES>\n");																			//NVER
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VELOCITY_RANGES),"VELOCITY_RANGES");          
            contentObject.put("VELOCITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <VELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)))) + "]]></VELOCITY_TEXT>\n");											//NVET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VELOCITY_TEXT),"VELOCITY_TEXT");          
            contentObject.put("VELOCITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //acceleration
            //recordBuffer.append("       <ACCELERATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ACCELERATION_RANGES)) + "]]></ACCELERATION_RANGES>\n");																//NACR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ACCELERATION_RANGES),"ACCELERATION_RANGES");          
            contentObject.put("ACCELERATION_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ACCELERATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)))) + "]]></ACCELERATION_TEXT>\n");								//NACT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT),"ACCELERATION_TEXT");          
            contentObject.put("ACCELERATION_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //angular velocity
            //recordBuffer.append("       <ANGULARVELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ANGULAR_VELOCITY_RANGES)) + "]]></ANGULARVELOCITY_RANGES>\n");													//NAVR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_RANGES),"ANGULARVELOCITY_RANGES");          
            contentObject.put("ANGULARVELOCITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ANGULARVELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)))) + "]]></ANGULARVELOCITY_TEXT>\n");						//NAVT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT),"ANGULARVELOCITY_TEXT");          
            contentObject.put("ANGULARVELOCITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //rotational speed 	
            //recordBuffer.append("       <ROTATIONALSPEED_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ROTATIONAL_SPEED_RANGES)) + "]]></ROTATIONALSPEED_RANGES>\n");													//NRSR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_RANGES),"ROTATIONALSPEED_RANGES");          
            contentObject.put("ROTATIONALSPEED_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ROTATIONALSPEED_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)))) + "]]></ROTATIONALSPEED_TEXT>\n");						//NRST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT),"ROTATIONALSPEED_TEXT");          
            contentObject.put("ROTATIONALSPEED_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //age mass
            //recordBuffer.append("       <AGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AGE_RANGES)) + "]]></AGE_RANGES>\n");																						//NAGR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGE_RANGES),"AGE_RANGES");          
            contentObject.put("AGE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <AGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGE_TEXT)))) + "]]></AGE_TEXT>\n");															//NAGT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGE_TEXT),"AGE_TEXT");          
            contentObject.put("AGE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //molar mass
            //recordBuffer.append("       <MOLARMASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_MASS_RANGES)) + "]]></MOLARMASS_RANGES>\n");																		//NMMR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_MASS_RANGES),"MOLARMASS_RANGES");          
            contentObject.put("MOLARMASS_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MOLARMASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)))) + "]]></MOLARMASS_TEXT>\n");										//NMMT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT),"MOLARMASS_TEXT");          
            contentObject.put("MOLARMASS_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //molality
            //recordBuffer.append("       <MOLALITYOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES)) + "]]></MOLALITYOFSUBSTANCE_RANGES>\n");										//NMOR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES),"MOLALITYOFSUBSTANCE_RANGES");          
            contentObject.put("MOLALITYOFSUBSTANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MOLALITYOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)))) + "]]></MOLALITYOFSUBSTANCE_TEXT>\n");		//NMOT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT),"MOLALITYOFSUBSTANCE_TEXT");          
            contentObject.put("MOLALITYOFSUBSTANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //radioactivity
            //recordBuffer.append("       <RADIOACTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIOACTIVITY_RANGES)) + "]]></RADIOACTIVITY_RANGES>\n");															//NRAR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIOACTIVITY_RANGES),"RADIOACTIVITY_RANGES");          
            contentObject.put("RADIOACTIVITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <RADIOACTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)))) + "]]></RADIOACTIVITY_TEXT>\n");							//NRAT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT),"RADIOACTIVITY_TEXT");          
            contentObject.put("RADIOACTIVITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //absorbed dose
            //recordBuffer.append("       <ABSORBEDDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ABSORBED_DOSE_RANGES)) + "]]></ABSORBEDDOSE_RANGES>\n");															//NABR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_RANGES),"ABSORBEDDOSE_RANGES");          
            contentObject.put("ABSORBEDDOSE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <ABSORBEDDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)))) + "]]></ABSORBEDDOSE_TEXT>\n");								//NABT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT),"ABSORBEDDOSE_TEXT");          
            contentObject.put("ABSORBEDDOSE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //radiation exposure
            //recordBuffer.append("       <RADIATIONEXPOSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIATION_EXPOSURE_RANGES)) + "]]></RADIATIONEXPOSURE_RANGES>\n");												//NRER
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_RANGES),"RADIATIONEXPOSURE_RANGES");          
            contentObject.put("RADIATIONEXPOSURE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <RADIATIONEXPOSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)))) + "]]></RADIATIONEXPOSURE_TEXT>\n");				//NRET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT),"RADIATIONEXPOSURE_TEXT");          
            contentObject.put("RADIATIONEXPOSURE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Luminance
            //recordBuffer.append("       <LUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINANCE_RANGES)) + "]]></LUMINANCE_RANGES>\n");																		//NLUR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINANCE_RANGES),"LUMINANCE_RANGES");          
            contentObject.put("LUMINANCE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)))) + "]]></LUMINANCE_TEXT>\n");										//NLUT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT),"LUMINANCE_TEXT");          
            contentObject.put("LUMINANCE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Magnetic field strengt
            //recordBuffer.append("       <MAGNETICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES)) + "]]></MAGNETICFIELDSTRENGTH_RANGES>\n");								//NFSR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES),"MAGNETICFIELDSTRENGTH_RANGES");          
            contentObject.put("MAGNETICFIELDSTRENGTH_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MAGNETICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)))) + "]]></MAGNETICFIELDSTRENGTH_TEXT>\n");	//NFST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT),"MAGNETICFIELDSTRENGTH_TEXT");          
            contentObject.put("MAGNETICFIELDSTRENGTH_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Spectral_Efficiency
            //recordBuffer.append("       <SPECTRALEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES)) + "]]></SPECTRALEFFICIENCY_RANGES>\n");											//NSER
            
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES),"SPECTRALEFFICIENCY_RANGES");          
            contentObject.put("SPECTRALEFFICIENCY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SPECTRALEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)))) + "]]></SPECTRALEFFICIENCY_TEXT>\n");			//NSET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT),"SPECTRALEFFICIENCY_TEXT");          
            contentObject.put("SPECTRALEFFICIENCY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Surface_Power_Density
            //recordBuffer.append("       <SURFACEPOWERDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES)) + "]]></SURFACEPOWERDENSITY_RANGES>\n");										//NSPR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES),"SURFACEPOWERDENSITY_RANGES");          
            contentObject.put("SURFACEPOWERDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SURFACEPOWERDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)))) + "]]></SURFACEPOWERDENSITY_TEXT>\n");		//NSPT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT),"SURFACEPOWERDENSITY_TEXT");          
            contentObject.put("SURFACEPOWERDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //thermal conductivity
            //recordBuffer.append("       <THERMALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)) + "]]></THERMALCONDUCTIVITY_RANGES>\n");										//NTCR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES),"THERMALCONDUCTIVITY_RANGES");          
            contentObject.put("THERMALCONDUCTIVITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <THERMALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)))) + "]]></THERMALCONDUCTIVITY_TEXT>\n");			//NTCT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT),"THERMALCONDUCTIVITY_TEXT");          
            contentObject.put("THERMALCONDUCTIVITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //new added on 4/11/2016
            //Decibel isotropic
            //recordBuffer.append("       <DECIBELISOTROPIC_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES)) + "]]></DECIBELISOTROPIC_RANGES>\n");												//NDIR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES),"DECIBELISOTROPIC_RANGES");          
            contentObject.put("DECIBELISOTROPIC_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DECIBELISOTROPIC_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)))) + "]]></DECIBELISOTROPIC_TEXT>\n");					//NDIT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT),"DECIBELISOTROPIC_TEXT");          
            contentObject.put("DECIBELISOTROPIC_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Decibel milliwatts
            //recordBuffer.append("       <DECIBELMILLIWATTS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES)) + "]]></DECIBELMILLIWATTS_RANGES>\n");												//NDMR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES),"DECIBELMILLIWATTS_RANGES");          
            contentObject.put("DECIBELMILLIWATTS_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DECIBELMILLIWATTS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)))) + "]]></DECIBELMILLIWATTS_TEXT>\n");				//NDMT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT),"DECIBELMILLIWATTS_TEXT");          
            contentObject.put("DECIBELMILLIWATTS_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Equivalent Dose
            //recordBuffer.append("       <EQUIVALENTDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.EQUIVALENT_DOSE_RANGES)) + "]]></EQUIVALENTDOSE_RANGES>\n");														//NEQR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_RANGES),"EQUIVALENTDOSE_RANGES");          
            contentObject.put("EQUIVALENTDOSE_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <EQUIVALENTDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)))) + "]]></EQUIVALENTDOSE_TEXT>\n");						//NEQT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT),"EQUIVALENTDOSE_TEXT");          
            contentObject.put("EQUIVALENTDOSE_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Molar concentration
            //recordBuffer.append("       <MOLARCONCENTRATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_CONCENTRATION_RANGES)) + "]]></MOLARCONCENTRATION_RANGES>\n");											//NMCR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_RANGES),"MOLARCONCENTRATION_RANGES");          
            contentObject.put("MOLARCONCENTRATION_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <MOLARCONCENTRATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)))) + "]]></MOLARCONCENTRATION_TEXT>\n");			//NMCT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT),"MOLARCONCENTRATION_TEXT");          
            contentObject.put("MOLARCONCENTRATION_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Linear Density
            //recordBuffer.append("       <LINEARDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LINEAR_DENSITY_RANGES)) + "]]></LINEARDENSITY_RANGES>\n");															//NLDR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_RANGES),"LINEARDENSITY_RANGES");          
            contentObject.put("LINEARDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.println("       <LINEARDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)))) + "]]></LINEARDENSITY_TEXT>\n");							//NLDT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT),"LINEARDENSITY_TEXT");          
            contentObject.put("LINEARDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //luminous efficiency
            //recordBuffer.append("       <LUMINOUSEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES)) + "]]></LUMINOUSEFFICIENCY_RANGES>\n");											//NLYR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES),"LUMINOUSEFFICIENCY_RANGES");          
            contentObject.put("LUMINOUSEFFICIENCY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LUMINOUSEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)))) + "]]></LUMINOUSEFFICIENCY_TEXT>\n");			//NLYT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT),"LUMINOUSEFFICIENCY_TEXT");          
            contentObject.put("LUMINOUSEFFICIENCY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //luminous efficacy
            //recordBuffer.append("       <LUMINOUSEFFICACY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICACY_RANGES)) + "]]></LUMINOUSEFFICACY_RANGES>\n");												//NLER
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_RANGES),"LUMINOUSEFFICACY_RANGES");          
            contentObject.put("LUMINOUSEFFICACY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <LUMINOUSEFFICACY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)))) + "]]></LUMINOUSEFFICACY_TEXT>\n");					//NLET
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT),"LUMINOUSEFFICACY_TEXT");          
            contentObject.put("LUMINOUSEFFICACY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Specific Energy
            //recordBuffer.append("       <SPECIFICENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_ENERGY_RANGES)) + "]]></SPECIFICENERGY_RANGES>\n");														//NSFR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_RANGES),"SPECIFICENERGY_RANGES");          
            contentObject.put("SPECIFICENERGY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SPECIFICENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)))) + "]]></SPECIFICENERGY_TEXT>\n");						//NSFT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT),"SPECIFICENERGY_TEXT");          
            contentObject.put("SPECIFICENERGY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Specific Surface area
            //recordBuffer.append("       <SPECIFICSURFACEAREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES)) + "]]></SPECIFICSURFACEAREA_RANGES>\n");										//NSSR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES),"SPECIFICSURFACEAREA_RANGES");          
            contentObject.put("SPECIFICSURFACEAREA_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SPECIFICSURFACEAREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)))) + "]]></SPECIFICSURFACEAREA_TEXT>\n");		//NSST
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT),"SPECIFICSURFACEAREA_TEXT");          
            contentObject.put("SPECIFICSURFACEAREA_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Specific Volume
            //recordBuffer.append("       <SPECIFICVOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_VOLUME_RANGES)) + "]]></SPECIFICVOLUME_RANGES>\n");														//NSVR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_RANGES),"SPECIFICVOLUME_RANGES");          
            contentObject.put("SPECIFICVOLUME_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SPECIFICVOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)))) + "]]></SPECIFICVOLUME_TEXT>\n");						//NSVT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT),"SPECIFICVOLUME_TEXT");          
            contentObject.put("SPECIFICVOLUME_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Surface Tension
            //recordBuffer.append("       <SURFACETENSION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_TENSION_RANGES)) + "]]></SURFACETENSION_RANGES>\n");														//NSTR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_TENSION_RANGES),"SURFACETENSION_RANGES");          
            contentObject.put("SURFACETENSION_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SURFACETENSION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_TENSION_TEXT)))) + "]]></SURFACETENSION_TEXT>\n");						//NSTT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT),"SURFACETENSION_TEXT");          
            contentObject.put("SURFACETENSION_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //Surface Density
            //recordBuffer.append("       <SURFACEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_DENSITY_RANGES)) + "]]></SURFACEDENSITY_RANGES>\n");														//NSDR
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_RANGES),"SURFACEDENSITY_RANGES");          
            contentObject.put("SURFACEDENSITY_RANGES",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <SURFACEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)))) + "]]></SURFACEDENSITY_TEXT>\n");						//NSDT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT),"SURFACEDENSITY_TEXT");          
            contentObject.put("SURFACEDENSITY_TEXT",elementArrayObject);
            contentJsonArray.add(contentObject);           
            //recordBuffer.append("       <NUMERICAL_UNITS><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.NUMERICALUNITS)))+ "]]></NUMERICAL_UNITS>\n");																//NUU
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NUMERICALUNITS),"NUMERICAL_UNITS");          
            contentObject.put("NUMERICAL_UNITS",elementArrayObject);
            contentJsonArray.add(contentObject);
            //EID
            //recordBuffer.append("       <EID><![CDATA[" + notNull(rec.getString(EVCombinedRec.EID)) + "]]></EID>\n");	        																									//EID
            contentObject = new JSONObject();
            contentObject.put("EID",notNull(rec.getString(EVCombinedRec.EID)));
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <DEPARTMENTID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DEPARTMENTID)))) + "]]></DEPARTMENTID>\n");												//DTID
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DEPARTMENTID),"DEPARTMENTID");          
            contentObject.put("DEPARTMENTID",elementArrayObject);
            contentJsonArray.add(contentObject);
            //added for georef at 03/16/2016
            //TITLE_OF_COLLECTION
            //recordBuffer.append("       <TITLEOFCOLLECTION><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION))) + " QstemQ " +  notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION)))) + "]]></TITLEOFCOLLECTION>\n");									//TIC
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TITLE_OF_COLLECTION),"TITLEOFCOLLECTION");          
            contentObject.put("TITLEOFCOLLECTION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //UNIVERSITY
            //recordBuffer.append("       <UNIVERSITY><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))+ " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))) + "]]></UNIVERSITY>\n");														//UNI
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.UNIVERSITY),"UNIVERSITY");          
            contentObject.put("UNIVERSITY",elementArrayObject);
            contentJsonArray.add(contentObject);
            //TYPE_OF_DEGREE
            //recordBuffer.append("       <TYPEOFDEGREE><![CDATA[" + notNull(rec.getString(EVCombinedRec.TYPE_OF_DEGREE)) + "]]></TYPEOFDEGREE>\n");																				//TOD
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TYPE_OF_DEGREE),"TYPEOFDEGREE");          
            contentObject.put("TYPEOFDEGREE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //ANNOTATION
            //recordBuffer.append("       <ANNOTATION><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.ANNOTATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ANNOTATION)))) + "]]></ANNOTATION>\n");																			//ANN
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ANNOTATION),"ANNOTATION");          
            contentObject.put("ANNOTATION",elementArrayObject);
            contentJsonArray.add(contentObject);
            //MAP_SCALE
            //recordBuffer.append("       <MAPSCALE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_SCALE))) + "]]></MAPSCALE>\n");																				//MPS
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAP_SCALE),"MAPSCALE");          
            contentObject.put("MAPSCALE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //MAP_TYPE
            //recordBuffer.append("       <MAPTYPE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_TYPE))) + "]]></MAPTYPE>\n");																					//MPT
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAP_TYPE),"MAPTYPE");          
            contentObject.put("MAPTYPE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //SOURCE_NOTE
            //recordBuffer.append("       <SOURCENOTE><![CDATA[" + notNull(rec.getString(EVCombinedRec.SOURCE_NOTE)) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SOURCE_NOTE))))+ "]]></SOURCENOTE>\n");																						//SNO
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SOURCE_NOTE),"SOURCENOTE");          
            contentObject.put("SOURCENOTE",elementArrayObject);
            contentJsonArray.add(contentObject);
            //GRANTID
            //recordBuffer.append("       <GRANTID><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) + "]]></GRANTID>\n");																					//GID
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.GRANTID),"GRANTID");          
            contentObject.put("GRANTID",elementArrayObject);
            contentJsonArray.add(contentObject);
            //GRANTAGENCY
            //recordBuffer.append("       <GRANTAGENCY><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + "]]></GRANTAGENCY>\n");		//GAG
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.GRANTAGENCY),"GRANTAGENCY");          
            contentObject.put("GRANTAGENCY",elementArrayObject);
            contentJsonArray.add(contentObject);
            //SPARE FIELDS
            //SOURCEBIBTEXT
            //recordBuffer.append("       <EV_SPARE1><![CDATA[]]></EV_SPARE1>");	//tempotary block out SOURCEBIBTEXT
            //recordBuffer.append("       <EV_SPARE1><![CDATA["+ notNull(rec.getString(EVCombinedRec.SOURCEBIBTEXT)) +"]]></EV_SPARE1>\n");//SPA1
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SOURCEBIBTEXT),"SOURCEBIBTEXT");          
            contentObject.put("SOURCEBIBTEXT",elementArrayObject);
            contentJsonArray.add(contentObject);
            //move standardid to here to get all search
            //STANDARDID
            /*
            if(rec.getString(EVCombinedRec.STANDARDID)==null)
            {
            	recordBuffer.append("       <EV_SPARE2><![CDATA[]]></EV_SPARE2>\n");
            }
            else      
            {
            	recordBuffer.append("       <EV_SPARE2><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID))) + "QstemQ " +notNull(getStems(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID)))) +"]]></EV_SPARE2>\n");//SPA2
            }  
            */                   
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.STANDARDID),"STANDARDID");          
            contentObject.put("STANDARDID",elementArrayObject);
            contentJsonArray.add(contentObject);
            //recordBuffer.append("       <EV_SPARE3><![CDATA[]]></EV_SPARE3>");//SPA3
            //added by hmo on 2019/09/11 for inspec orgid
            //recordBuffer.append("       <EV_SPARE3><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORG_ID)))) + "]]></EV_SPARE3>\n");//SPA3
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ORG_ID),"ORG_ID");          
            contentObject.put("ORG_ID",elementArrayObject);
            contentJsonArray.add(contentObject);
           
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ISOPENACESS),"ISOPENACESS");          
            contentObject.put("ISOPENACESS",elementArrayObject);
            contentJsonArray.add(contentObject);
            
            contentObject = new JSONObject();
            elementArrayObject = formJsonArray(combine(rec.getStrings(EVCombinedRec.GRANTID),rec.getStrings(EVCombinedRec.GRANTAGENCY),rec.getStrings(EVCombinedRec.GRANTTEXT)),"GRANT");          
            contentObject.put("GRANT",elementArrayObject);
            contentJsonArray.add(contentObject);
          
            JSONArray evArray = new JSONArray();
            JSONObject dataSourceObject = new JSONObject();
            dataSourceObject.put("DataSource","EV");
            JSONObject actionObject = new JSONObject();
            actionObject.put("Action","Update");
            JSONObject uniqueIDObject = new JSONObject();
            uniqueIDObject.put("UniqueID",eid);
            JSONObject evContent = new JSONObject();
            evContent.put("CONTENT",contentJsonArray);
            evArray.add(dataSourceObject);
            evArray.add(actionObject);
            evArray.add(uniqueIDObject);   
            evArray.add(evContent);
            JSONObject evObject = new JSONObject();
            evObject.put("EV_DOCUMENT",evArray);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
        	JsonParser jp = new JsonParser();
        	//JsonElement je = jp.parse(evo.build().toString());
        	JsonElement je = jp.parse(evObject.toString());
        	String prettyJsonString = gson.toJson(je);
        	KafkaTest kafka = new KafkaTest();
        	kafka.getParameterFromPropertiesFile("config.properties");
            //KafkaTest kafka = new KafkaTest(getEndpoint());
            //kafka.runProducer(jBuffer.toString(),eid);
            kafka.runProducer(prettyJsonString,eid);
          
        }

    private String[] combine(String[] arr1,String[] arr2, String[] arr3) {
    	int arr1Length = 0;
    	int arr2Length = 0;
    	int arr3Length = 0;
    	
    	if(arr1 !=null)
    	{
    		arr1Length = arr1.length;
    	}
    	
    	if(arr2 !=null)
    	{
    		arr2Length = arr2.length;
    	}
    	
    	if(arr3 !=null)
    	{
    		arr3Length = arr3.length;
    	}
        
        String[] result = new String[arr1Length + arr2Length + arr3Length];
        if(arr1 !=null) {
        	System.arraycopy(arr1, 0, result, 0, arr1Length);
        }
        
        if(arr2 !=null) {
        	System.arraycopy(arr2, 0, result, arr1Length, arr2Length);
        }
        
        if(arr3 !=null) {
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
               
                    if(oo!=null && getDatabase()!=null && getDatabase().length()>=3)
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
                      indexWriter.println(Entity.prepareString(code).toUpperCase().trim() + "\t" +Entity.prepareString(name).toUpperCase().trim() + "\t" + getDatabase().substring(0,3)+"\t");
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
		
		s = perl.substitute("s/<sub>//g", s);
		s = perl.substitute("s/<\\/sub>//g", s);
		s = perl.substitute("s/<sup>//g", s);
		s = perl.substitute("s/<\\/sup>//g", s);
		s = perl.substitute("s/<inf>//g", s);
		s = perl.substitute("s/<\\/inf>//g", s);
		//s = perl.substitute("s/<\\//QPLUSQ/g", s);
		//s = perl.substitute("s/\\[0-9].[0-9]/$1DQD$2/g", s);
		//s = perl.substitute("s/-/QMINUSQ/g",s); 
		//s = perl.substitute("s/\\//QSLASHQ/g",s);
		   
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
    	if(arrays!=null) {
	    	for(int i=0;i<arrays.length;i++)
	    	{
	    		String arrayElement = arrays[i];
	    		if(arrayElement!=null) {
	    			arrayElement = cleaner.stripBadChars(arrayElement);	 	    		
	    			jArray.add(arrayElement);
	    		}
	    	}
	    	return jArray;
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
                zipBatch();
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
    	String testString = "check[comma";
    	System.out.println("before= "+testString+" after="+c.cleanup(testString));
    	System.out.println("before= check,comma after="+c.cleanup("check,comma"));
    	System.out.println("before= check}comma after="+c.cleanup("check}comma"));
    	
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
            
}
