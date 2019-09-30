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
        writeRec(rec,"kafka");
        
        end();
    }
    
    public void writeRec(EVCombinedRec rec, String flag)
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
            
           
            
            //recordBuffer.append("   <ROW> \n");
                 
            //recordBuffer.append("       <EIDOCID>" + eid + "</EIDOCID>\n");
            
            contentObject.put("EIDOCID",eid);
            
            //recordBuffer.append("       <PARENTID>" +  rec.getString(EVCombinedRec.PARENT_ID) + "</PARENTID>\n");
            
            contentObject.put("PARENTID",notNull(rec.getString(EVCombinedRec.PARENT_ID)));
            
            //recordBuffer.append("       <DEDUPKEY>" + rec.getString(EVCombinedRec.DEDUPKEY) + "</DEDUPKEY>\n");
            
            contentObject.put("DEDUPKEY",notNull(rec.getString(EVCombinedRec.DEDUPKEY)));
            
            //recordBuffer.append("       <DATABASE>" + rec.getString(EVCombinedRec.DATABASE) + "</DATABASE>\n");
            
            contentObject.put("DATABASE",rec.getString(EVCombinedRec.DATABASE));
            
            //recordBuffer.append("       <LOADNUMBER>" + loadnumber + "</LOADNUMBER>\n");
            
            contentObject.put("LOADNUMBER",loadnumber);
            
            //added for future use only, should be removed for regular database loading
            //recordBuffer.append("       <UPDATENUMBER>" + rec.getString(EVCombinedRec.UPDATE_NUMBER) + "</UPDATENUMBER>\n");
            
            contentObject.put("UPDATENUMBER",notNull(rec.getString(EVCombinedRec.UPDATE_NUMBER)));
            
            //recordBuffer.append("       <DATESORT>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.DATESORT))) + "</DATESORT>\n");
            
            contentObject.put("DATESORT",notNull(rec.getString(EVCombinedRec.DATESORT)));           
            
            //recordBuffer.append("       <PUBYEAR>" + rec.getString(EVCombinedRec.PUB_YEAR) + "</PUBYEAR>");
                      
            contentObject.put("PUBYEAR",rec.getString(EVCombinedRec.PUB_YEAR));           
            
           // recordBuffer.append("       <ACCESSIONNUMBER>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ACCESSION_NUMBER))) + "</ACCESSIONNUMBER>\n");
                      
            contentObject.put("ACCESSIONNUMBER",rec.getString(EVCombinedRec.ACCESSION_NUMBER));
                       
            //recordBuffer.append("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + "]]></AUTHOR>"); //added QstemQ portion to search both with qqdashqq and without it
            //recordBuffer.append("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + " QstemQ " + notNull(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR))) + "]]></AUTHOR>\n");
            
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR");          
            contentObject.put("AUTHOR",elementArrayObject);
                       
            //recordBuffer.append("       <AUTHORID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHORID)))) + "]]></AUTHORID>\n");
            
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHORID),"AUTHORID");          
            contentObject.put("AUTHORID",elementArrayObject);
            
            //recordBuffer.append("       <AUTHORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.),"AUTHORAFFILIATION")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))))) + "]]></AUTHORAFFILIATION>\n");
            
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION");          
            contentObject.put("AUTHORAFFILIATION",elementArrayObject);
            
            //recordBuffer.append("       <AFFILIATIONLOCATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)))) + "]]></AFFILIATIONLOCATION>");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION),"AFFILIATIONLOCATION");          
            contentObject.put("AFFILIATIONLOCATION",elementArrayObject);
            
            //out.println("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>");
            //recordBuffer.append("       <TITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE))))) + "]]></TITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TITLE),"TITLE");          
            contentObject.put("TITLE",elementArrayObject);
            
            //recordBuffer.append("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE),"TRANSLATEDTITLE");          
            contentObject.put("TRANSLATEDTITLE",elementArrayObject);
            
            //recordBuffer.append("       <VOLUMETITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE))))) + "]]></VOLUMETITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLUME_TITLE),"VOLUMETITLE");          
            contentObject.put("VOLUMETITLE",elementArrayObject);
            
            //recordBuffer.append("       <ABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT)))) + "]]></ABSTRACT>\n");
            contentObject.put("ABSTRACT",rec.getString(EVCombinedRec.ABSTRACT));
            
            //recordBuffer.append("       <OTHERABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT)))) + "]]></OTHERABSTRACT>\n");
            contentObject.put("OTHERABSTRACT",notNull(rec.getString(EVCombinedRec.OTHER_ABSTRACT)));
                        
            //recordBuffer.append("       <EDITOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.EDITOR))))+"]]></EDITOR>");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EDITOR),"EDITOR");          
            contentObject.put("EDITOR",elementArrayObject);
            
            //recordBuffer.append("       <EDITORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION))))) + "]]></EDITORAFFILIATION>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION),"EDITORAFFILIATION");          
            contentObject.put("EDITORAFFILIATION",elementArrayObject);
            
            //recordBuffer.append("       <TRANSLATOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.TRANSLATOR)))) + "]]></TRANSLATOR>");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TRANSLATOR),"TRANSLATOR");          
            contentObject.put("TRANSLATOR",elementArrayObject);
                       
            //recordBuffer.append("       <CONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS"))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS)))))) + "]]></CONTROLLEDTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS");          
            contentObject.put("CONTROLLEDTERMS",elementArrayObject);
                        
            //recordBuffer.append("       <UNCONTROLLEDTERMS><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS)))))) + "]]></UNCONTROLLEDTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS),"UNCONTROLLEDTERMS");          
            contentObject.put("UNCONTROLLEDTERMS",elementArrayObject);
                        
            //recordBuffer.append("       <ISSN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN))))) + "]]></ISSN>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ISSN),"ISSN");          
            contentObject.put("ISSN",elementArrayObject);
                       
            //recordBuffer.append("       <CODEN><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN)))) + "]]></CODEN>\n");            
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CODEN),"CODEN");          
            contentObject.put("CODEN",elementArrayObject);                     
            
            //recordBuffer.append("       <CODENOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION)))) + "]]></CODENOFTRANSLATION>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION),"CODENOFTRANSLATION");          
            contentObject.put("CODENOFTRANSLATION",elementArrayObject);
            
            //recordBuffer.append("       <ISBN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISBN(rec.getStrings(EVCombinedRec.ISBN))))) + "]]></ISBN>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ISBN),"ISBN");          
            contentObject.put("ISBN",elementArrayObject);
            
            //recordBuffer.append("       <SERIALTITLE><![CDATA[" + notNull(Entity.prepareString(notNull(multiFormat(addIndex(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE)))))) + "]]></SERIALTITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE");          
            contentObject.put("SERIALTITLE",elementArrayObject);
            
            //recordBuffer.append("       <SERIALTITLETRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION))))) + "]]></SERIALTITLETRANSLATION>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION),"SERIALTITLETRANSLATION");          
            contentObject.put("SERIALTITLETRANSLATION",elementArrayObject);
            
            //recordBuffer.append("       <MAINHEADING><![CDATA[" + notNull(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING))))) + " QstemQ " + notNull(getStems(removeSpecialTag(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING)))))) + "]]></MAINHEADING>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAIN_HEADING),"MAINHEADING");          
            contentObject.put("MAINHEADING",elementArrayObject);
            
            //recordBuffer.append("       <SUBHEADING><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SUB_HEADING))) + "]]></SUBHEADING>\n");
            contentObject.put("SUBHEADING",rec.getString(EVCombinedRec.SUB_HEADING));
            
            recordBuffer.append("       <PUBLISHERNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PUBLISHER_NAME))))) + "]]></PUBLISHERNAME>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME");          
            contentObject.put("PUBLISHERNAME",elementArrayObject);
            
            //recordBuffer.append("       <TREATMENTCODE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TREATMENT_CODE)))) + "</TREATMENTCODE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TREATMENT_CODE),"TREATMENTCODE");          
            contentObject.put("TREATMENTCODE",elementArrayObject);
            
            //recordBuffer.append("       <LANGUAGE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LANGUAGE)))) + "]]></LANGUAGE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LANGUAGE),"LANGUAGE");          
            contentObject.put("LANGUAGE",elementArrayObject);
            
            //recordBuffer.append("       <RECTYPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOCTYPE)))) + "]]></RECTYPE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DOCTYPE),"RECTYPE");          
            contentObject.put("RECTYPE",elementArrayObject);
            
            //recordBuffer.append("       <CLASSIFICATIONCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE)))) + "]]></CLASSIFICATIONCODE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CLASSIFICATION_CODE),"CLASSIFICATIONCODE");          
            contentObject.put("CLASSIFICATIONCODE",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCECODE>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_CODE))) + "</CONFERENCECODE>\n");                     
            contentObject.put("CONFERENCECODE",notNull(rec.getString(EVCombinedRec.CONFERENCE_CODE)));
            
            //recordBuffer.append("       <CONFERENCENAME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_NAME)))) + "]]></CONFERENCENAME>\n");                    
            contentObject.put("CONFERENCENAME",notNull(rec.getString(EVCombinedRec.CONFERENCE_NAME)));
            
            //recordBuffer.append("       <CONFERENCELOCATION><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)))) + "]]></CONFERENCELOCATION>\n");
            contentObject.put("CONFERENCELOCATION",notNull(rec.getString(EVCombinedRec.CONFERENCE_LOCATION)));
            
            //recordBuffer.append("       <MEETINGDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.MEETING_DATE))) + "]]></MEETINGDATE>\n");
            contentObject.put("MEETINGDATE",notNull(rec.getString(EVCombinedRec.MEETING_DATE)));
            
            //recordBuffer.append("       <SPONSORNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPONSOR_NAME))))) + "]]></SPONSORNAME>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPONSOR_NAME),"SPONSORNAME");          
            contentObject.put("SPONSORNAME",elementArrayObject);
            
            //recordBuffer.append("       <MONOGRAPHTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MONOGRAPH_TITLE))))) + "]]></MONOGRAPHTITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SPONSOR_NAME),"MONOGRAPHTITLE");          
            contentObject.put("MONOGRAPHTITLE",elementArrayObject);
            
            //recordBuffer.append("       <DISCIPLINE>" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DISCIPLINE)))) + "</DISCIPLINE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DISCIPLINE),"DISCIPLINE");          
            contentObject.put("DISCIPLINE",elementArrayObject);
            
            //recordBuffer.append("       <MATERIALNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MATERIAL_NUMBER)))) + "]]></MATERIALNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MATERIAL_NUMBER),"MATERIALNUMBER");          
            contentObject.put("MATERIALNUMBER",elementArrayObject);
            
            //recordBuffer.append("       <NUMERICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING)))) + "]]></NUMERICALINDEXING>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NUMERICAL_INDEXING),"NUMERICALINDEXING");          
            contentObject.put("NUMERICALINDEXING",elementArrayObject);
            
            //recordBuffer.append("       <CHEMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING)))) + "]]></CHEMICALINDEXING>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICAL_INDEXING),"CHEMICALINDEXING");          
            contentObject.put("CHEMICALINDEXING",elementArrayObject);
            
            //recordBuffer.append("       <ASTRONOMICALINDEXING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING)))) + "]]></ASTRONOMICALINDEXING>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ASTRONOMICAL_INDEXING),"ASTRONOMICALINDEXING");          
            contentObject.put("ASTRONOMICALINDEXING",elementArrayObject);
            
            //recordBuffer.append("       <REPORTNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REPORTNUMBER)))) + "]]></REPORTNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.REPORTNUMBER),"REPORTNUMBER");          
            contentObject.put("REPORTNUMBER",elementArrayObject);
            
            //recordBuffer.append("       <ORDERNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORDERNUMBER)))) + "]]></ORDERNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ORDERNUMBER),"ORDERNUMBER");          
            contentObject.put("ORDERNUMBER",elementArrayObject);
                       
            //recordBuffer.append("       <COUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COUNTRY)))) + "]]></COUNTRY>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.COUNTRY),"COUNTRY");          
            contentObject.put("COUNTRY",elementArrayObject);
            
            //recordBuffer.append("       <VOLUME><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.VOLUME))) + "]]></VOLUME>\n");
            contentObject.put("VOLUME",notNull(rec.getString(EVCombinedRec.VOLUME)));
            
            //recordBuffer.append("       <ISSUE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ISSUE))) + "]]></ISSUE>\n");
            contentObject.put("ISSUE",notNull(rec.getString(EVCombinedRec.ISSUE)));
            
            //recordBuffer.append("       <STARTPAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STARTPAGE))) + "]]></STARTPAGE>\n");
            contentObject.put("STARTPAGE",notNull(rec.getString(EVCombinedRec.STARTPAGE)));
            
            //recordBuffer.append("       <PAGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.PAGE))) + "]]></PAGE>\n");
            contentObject.put("PAGE",notNull(rec.getString(EVCombinedRec.PAGE)));
            
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
            contentObject.put("AVAILABILITY",rec.getString(EVCombinedRec.AVAILABILITY));
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
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NOTES),"NOTES");          
            contentObject.put("NOTES",elementArrayObject);
            
            //recordBuffer.append("       <PATENTAPPDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTAPPDATE)))) + "]]></PATENTAPPDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENTAPPDATE),"PATENTAPPDATE");          
            contentObject.put("PATENTAPPDATE",elementArrayObject);
            
            //recordBuffer.append("       <PATENTISSUEDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTISSUEDATE)))) + "]]></PATENTISSUEDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENTISSUEDATE),"PATENTISSUEDATE");          
            contentObject.put("PATENTISSUEDATE",elementArrayObject);
            
            //recordBuffer.append("       <COMPANIES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COMPANIES)))) + "]]></COMPANIES>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.COMPANIES),"COMPANIES");          
            contentObject.put("COMPANIES",elementArrayObject);
            
            //recordBuffer.append("       <CASREGISTRYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER)))) + "]]></CASREGISTRYNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER),"CASREGISTRYNUMBER");          
            contentObject.put("CASREGISTRYNUMBER",elementArrayObject);
            
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
            
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.BUSINESSTERMS),"BUSINESSTERMS");          
            contentObject.put("BUSINESSTERMS",elementArrayObject);
            
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
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICALTERMS),"CHEMICALTERMS");          
            contentObject.put("CHEMICALTERMS",elementArrayObject);
            
            //recordBuffer.append("       <CHEMAC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS)))) + "]]></CHEMAC>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS),"CHEMAC");          
            contentObject.put("CHEMAC",elementArrayObject);
            
            //recordBuffer.append("       <SIC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_NUMBER)))) + "]]></SIC>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_NUMBER),"SIC");          
            contentObject.put("SIC",elementArrayObject);
            
            //recordBuffer.append("       <INDUSTRIALCODES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALCODES)))) + "]]></INDUSTRIALCODES>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUSTRIALCODES),"INDUSTRIALCODES");          
            contentObject.put("INDUSTRIALCODES",elementArrayObject);
            
            //recordBuffer.append("       <INDUSTRIALSECTORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS)))) + "]]></INDUSTRIALSECTORS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS),"INDUSTRIALSECTORS");          
            contentObject.put("INDUSTRIALSECTORS",elementArrayObject);
            
            //recordBuffer.append("       <SCOPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SCOPE)))) + "]]></SCOPE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SCOPE),"SCOPE");          
            contentObject.put("SCOPE",elementArrayObject);
            
            //recordBuffer.append("       <AGENCY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGENCY)))) + "]]></AGENCY>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AGENCY),"AGENCY");          
            contentObject.put("AGENCY",elementArrayObject);
            
            //recordBuffer.append("       <DERWENTACCESSIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER)))) + "]]></DERWENTACCESSIONNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER),"DERWENTACCESSIONNUMBER");          
            contentObject.put("DERWENTACCESSIONNUMBER",elementArrayObject);
            
            
            //recordBuffer.append("       <APPLICATIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER)))) + "]]></APPLICATIONNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER),"APPLICATIONNUMBER");          
            contentObject.put("APPLICATIONNUMBER",elementArrayObject);
            
            //recordBuffer.append("       <APPLICATIONCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY))))+ "]]></APPLICATIONCOUNTRY>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY),"APPLICATIONCOUNTRY");          
            contentObject.put("APPLICATIONCOUNTRY",elementArrayObject);
            
            //recordBuffer.append("       <INTPATENTCLASSIFICATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIpcIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION")))) + "]]></INTPATENTCLASSIFICATION>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTPATENTCLASSIFICATION");          
            contentObject.put("INTPATENTCLASSIFICATION",elementArrayObject);
            
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
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.LINKED_TERMS),"LINKEDTERMS");          
            contentObject.put("LINKEDTERMS",elementArrayObject);
            
            //recordBuffer.append("       <ENTRYYEAR><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ENTRY_YEAR))) + "]]></ENTRYYEAR>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENTRY_YEAR),"ENTRYYEAR");          
            contentObject.put("ENTRYYEAR",elementArrayObject);
            
            //recordBuffer.append("       <PRIORITYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER)))) + "]]></PRIORITYNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER),"PRIORITYNUMBER");          
            contentObject.put("PRIORITYNUMBER",elementArrayObject);
            
            //recordBuffer.append("       <PRIORITYDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_DATE)))) + "]]></PRIORITYDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_DATE),"PRIORITYDATE");          
            contentObject.put("PRIORITYDATE",elementArrayObject);
            
            //recordBuffer.append("       <PRIORITYCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY))))+ "]]></PRIORITYCOUNTRY>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY),"PRIORITYCOUNTRY");          
            contentObject.put("PRIORITYCOUNTRY",elementArrayObject);
            
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
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SOURCE),"SOURCE");          
            contentObject.put("SOURCE",elementArrayObject);
            
            //recordBuffer.append("       <SECONDARYSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE)))) + "]]></SECONDARYSRCTITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SECONDARY_SRC_TITLE),"SECONDARYSRCTITLE");          
            contentObject.put("SECONDARYSRCTITLE",elementArrayObject);
                       
            //recordBuffer.append("       <MAINTERM><![CDATA[" + notNull((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM)))) + " QstemQ " + notNull(getStems((Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM))))) + "]]></MAINTERM>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAIN_TERM),"MAINTERM");          
            contentObject.put("MAINTERM",elementArrayObject);
            
            //recordBuffer.append("       <ABBRVSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABBRV_SRC_TITLE)))) + "]]></ABBRVSRCTITLE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ABBRV_SRC_TITLE),"ABBRVSRCTITLE");          
            contentObject.put("ABBRVSRCTITLE",elementArrayObject);
            
            //recordBuffer.append("       <NOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOROLE_TERMS))))) + "]]></NOROLETERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.NOROLE_TERMS),"NOROLETERMS");          
            contentObject.put("NOROLETERMS",elementArrayObject);
            
            //recordBuffer.append("       <REAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.REAGENT_TERMS))))) + "]]></REAGENTTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.REAGENT_TERMS),"REAGENTTERMS");          
            contentObject.put("REAGENTTERMS",elementArrayObject);
            
            //recordBuffer.append("       <PRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRODUCT_TERMS))))) + "]]></PRODUCTTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRODUCT_TERMS),"PRODUCTTERMS");          
            contentObject.put("PRODUCTTERMS",elementArrayObject);
            
            //recordBuffer.append("       <MAJORNOROLETERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS))))) + "]]></MAJORNOROLETERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORNOROLE_TERMS),"MAJORNOROLETERMS");          
            contentObject.put("MAJORNOROLETERMS",elementArrayObject);
            
            //recordBuffer.append("       <MAJORREAGENTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS))))) + "]]></MAJORREAGENTTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORREAGENT_TERMS),"MAJORREAGENTTERMS");          
            contentObject.put("MAJORREAGENTTERMS",elementArrayObject);
            
            //recordBuffer.append("       <MAJORPRODUCTTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS))))) + "]]></MAJORPRODUCTTERMS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAJORPRODUCT_TERMS),"MAJORPRODUCTTERMS");          
            contentObject.put("MAJORPRODUCTTERMS",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCEAFFILIATIONS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS))))) + "]]></CONFERENCEAFFILIATIONS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEAFFILIATIONS),"CONFERENCEAFFILIATIONS");          
            contentObject.put("CONFERENCEAFFILIATIONS",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCEEDITORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS))))) + "]]></CONFERENCEEDITORS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEEDITORS),"CONFERENCEEDITORS");          
            contentObject.put("CONFERENCEEDITORS",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCESTARTDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCESTARTDATE))) + "]]></CONFERENCESTARTDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCESTARTDATE),"CONFERENCESTARTDATE");          
            contentObject.put("CONFERENCESTARTDATE",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCEENDDATE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEENDDATE))) + "]]></CONFERENCEENDDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEENDDATE),"CONFERENCEENDDATE");          
            contentObject.put("CONFERENCEENDDATE",elementArrayObject);
            
            
            //recordBuffer.append("       <CONFERENCEVENUESITE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEVENUESITE)))) + "]]></CONFERENCEVENUESITE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEVENUESITE),"CONFERENCEVENUESITE");          
            contentObject.put("CONFERENCEVENUESITE",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCECITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECITY)))) + "]]></CONFERENCECITY>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCECITY),"CONFERENCECITY");          
            contentObject.put("CONFERENCECITY",elementArrayObject);
                      
            //recordBuffer.append("       <CONFERENCECOUNTRYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCECOUNTRYCODE))) + "]]></CONFERENCECOUNTRYCODE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCECOUNTRYCODE),"CONFERENCECOUNTRYCODE");          
            contentObject.put("CONFERENCECOUNTRYCODE",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCEPAGERANGE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPAGERANGE))) + "]]></CONFERENCEPAGERANGE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEPAGERANGE),"CONFERENCEPAGERANGE");          
            contentObject.put("CONFERENCEPAGERANGE",elementArrayObject);
                       
            //recordBuffer.append("       <CONFERENCENUMBERPAGES><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCENUMBERPAGES))) + "]]></CONFERENCENUMBERPAGES>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCENUMBERPAGES),"CONFERENCENUMBERPAGES");          
            contentObject.put("CONFERENCENUMBERPAGES",elementArrayObject);
            
            //recordBuffer.append("       <CONFERENCEPARTNUMBER><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.CONFERENCEPARTNUMBER))) + "]]></CONFERENCEPARTNUMBER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CONFERENCEPARTNUMBER),"CONFERENCEPARTNUMBER");          
            contentObject.put("CONFERENCEPARTNUMBER",elementArrayObject);
            
            //recordBuffer.append("       <DESIGNATEDSTATES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DESIGNATED_STATES)))) + "]]></DESIGNATEDSTATES>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DESIGNATED_STATES),"DESIGNATEDSTATES");          
            contentObject.put("DESIGNATEDSTATES",elementArrayObject);
            
            //recordBuffer.append("       <STNCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_CONFERENCE)))) + "]]></STNCONFERENCE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.STN_CONFERENCE),"STNCONFERENCE");          
            contentObject.put("STNCONFERENCE",elementArrayObject);
            
            //recordBuffer.append("       <STNSECONDARYCONFERENCE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.STN_SECONDARY_CONFERENCE)))) + "]]></STNSECONDARYCONFERENCE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.STN_SECONDARY_CONFERENCE),"STNSECONDARYCONFERENCE");          
            contentObject.put("STNSECONDARYCONFERENCE",elementArrayObject);
            
            //recordBuffer.append("       <FILINGDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_FILING_DATE)))) + "]]></FILINGDATE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_FILING_DATE),"FILINGDATE");          
            contentObject.put("FILINGDATE",elementArrayObject);
            
            //recordBuffer.append("       <PRIORITYKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_KIND)))) + "]]></PRIORITYKIND>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIORITY_KIND),"PRIORITYKIND");          
            contentObject.put("PRIORITYKIND",elementArrayObject);
            
            //recordBuffer.append("       <ECLACODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CODES)))) +"]]></ECLACODE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_CODES),"ECLACODE");          
            contentObject.put("ECLACODE",elementArrayObject);
            
            //recordBuffer.append("       <ATTORNEYNAME><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ATTORNEY_NAME)))) + "]]></ATTORNEYNAME>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ATTORNEY_NAME),"ATTORNEYNAME");          
            contentObject.put("ATTORNEYNAME",elementArrayObject);
            
            //recordBuffer.append("       <PRIMARYEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER)))) + "]]></PRIMARYEXAMINER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PRIMARY_EXAMINER),"PRIMARYEXAMINER");          
            contentObject.put("PRIMARYEXAMINER",elementArrayObject);
            
            //recordBuffer.append("       <ASSISTANTEXAMINER><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER)))) + "]]></ASSISTANTEXAMINER>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ASSISTANT_EXAMINER),"ASSISTANTEXAMINER");          
            contentObject.put("ASSISTANTEXAMINER",elementArrayObject);
            
            //recordBuffer.append("       <IPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES)))) + "]]></IPCCLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSES),"IPCCLASS");          
            contentObject.put("IPCCLASS",elementArrayObject);
            
            //recordBuffer.append("       <IPCSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES)))) + "]]></IPCSUBCLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INT_PATENT_SUB_CLASSES),"IPCCLASS");          
            contentObject.put("IPCCLASS",elementArrayObject);
            
            //recordBuffer.append("       <ECLACLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CLASSES)))) + "]]></ECLACLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_CLASSES),"ECLACLASS");          
            contentObject.put("ECLACLASS",elementArrayObject);
            
            //recordBuffer.append("       <ECLASUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES)))) + "]]></ECLASUBCLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ECLA_SUB_CLASSES),"ECLASUBCLASS");          
            contentObject.put("ECLASUBCLASS",elementArrayObject);
                        
            //recordBuffer.append("       <USPTOCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCLASS)))) + "]]></USPTOCLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOCLASS),"USPTOCLASS");          
            contentObject.put("USPTOCLASS",elementArrayObject);
            
            //recordBuffer.append("       <USPTOSUBCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOSUBCLASS)))) + "]]></USPTOSUBCLASS>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOSUBCLASS),"USPTOSUBCLASS");          
            contentObject.put("USPTOSUBCLASS",elementArrayObject);
            
            //recordBuffer.append("       <USPTOCODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.USPTOCODE)))) + "]]></USPTOCODE>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.USPTOCODE),"USPTOCODE");          
            contentObject.put("USPTOCODE",elementArrayObject);
            
            //recordBuffer.append("       <PATENTKIND><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_KIND)))) + "]]></PATENTKIND>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PATENT_KIND),"PATENTKIND");          
            contentObject.put("PATENTKIND",elementArrayObject);
            
            //recordBuffer.append("       <KINDDESCRIPTION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.KIND_DESCR))))) + "]]></KINDDESCRIPTION>\n");        
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.KIND_DESCR),"KINDDESCRIPTION");          
            contentObject.put("KINDDESCRIPTION",elementArrayObject);
                        
            //recordBuffer.append("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AUTHORITY_CODE))) + "]]></AUTHORITYCODE>\n");
            contentObject.put("AUTHORITYCODE",notNull(rec.getString(EVCombinedRec.AUTHORITY_CODE)));
            
            //recordBuffer.append("       <PCITED><![CDATA[" + hasPcited(rec.getString(EVCombinedRec.PCITED)) + "]]></PCITED>\n");         
            contentObject.put("PCITED",notNull(rec.getString(EVCombinedRec.PCITED)));
            
            //recordBuffer.append("       <PCITEDINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PCITEDINDEX)))) + "]]></PCITEDINDEX>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PCITEDINDEX),"PCITEDINDEX");          
            contentObject.put("PCITEDINDEX",elementArrayObject);
            
            //recordBuffer.append("       <PREFINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PREFINDEX)))) + "]]></PREFINDEX>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PREFINDEX),"PREFINDEX");          
            contentObject.put("PREFINDEX",elementArrayObject);
            
            //recordBuffer.append("       <DMASK><![CDATA[" + getMask(rec) + "]]></DMASK>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.DMASK),"DMASK");          
            contentObject.put("DMASK",elementArrayObject);
            
            //recordBuffer.append("       <DOI><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOI)))) + hasDOI(rec) + "]]></DOI>\n");         
            contentObject.put("DOI",notNull(rec.getString(EVCombinedRec.DOI)));
            
            //recordBuffer.append("       <SCOPUSID><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SCOPUSID))) + "]]></SCOPUSID>\n");   
            contentObject.put("SCOPUSID",notNull(rec.getString(EVCombinedRec.SCOPUSID)));
            
            //recordBuffer.append("       <AFFILIATIONID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATIONID)))) + "]]></AFFILIATIONID>\n");
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AFFILIATIONID),"AFFILIATIONID");          
            contentObject.put("AFFILIATIONID",elementArrayObject);
            
            //recordBuffer.append("       <LAT_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NW))) + "]]></LAT_NW>\n");       
            contentObject.put("LAT_NW",notNull(rec.getString(EVCombinedRec.LAT_NW)));
            
            //recordBuffer.append("       <LNG_NW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NW))) + "]]></LNG_NW>\n");        
            contentObject.put("LNG_NW",notNull(rec.getString(EVCombinedRec.LNG_NW)));
            
            //recordBuffer.append("       <LAT_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_NE))) + "]]></LAT_NE>\n");         
            contentObject.put("LAT_NE",notNull(rec.getString(EVCombinedRec.LAT_NE)));
            
            //recordBuffer.append("       <LNG_NE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_NE))) + "]]></LNG_NE>\n");        
            contentObject.put("LNG_NE",notNull(rec.getString(EVCombinedRec.LNG_NE)));
            
            //recordBuffer.append("       <LAT_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SW))) + "]]></LAT_SW>\n");         
            contentObject.put("LAT_SW",notNull(rec.getString(EVCombinedRec.LAT_SW)));
            
            //recordBuffer.append("       <LNG_SW><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SW))) + "]]></LNG_SW>\n");       
            contentObject.put("LNG_SW",notNull(rec.getString(EVCombinedRec.LNG_SW)));
                       
            //recordBuffer.append("       <LAT_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LAT_SE))) + "]]></LAT_SE>\n");                
            contentObject.put("LAT_SE",notNull(rec.getString(EVCombinedRec.LAT_SE)));
            
            recordBuffer.append("       <LNG_SE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.LNG_SE))) + "]]></LNG_SE>\n");                
            contentObject.put("LNG_SE",notNull(rec.getString(EVCombinedRec.LNG_SE)));
            
 //         recordBuffer.append("       <CPCCLASS><![CDATA[]]></CPCCLASS>\n");
            
//          recordBuffer.append("       <CPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CPCCLASS)))) + "]]></CPCCLASS>");
            recordBuffer.append("       <TABLEOFCONTENT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT)))) + "]]></TABLEOFCONTENT>\n");										//TOC
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TABLE_OF_CONTENT),"TABLEOFCONTENT");          
            contentObject.put("TABLEOFCONTENT",elementArrayObject);
            
            //************************************************ added for numericalIndex ******************************************************//
            
            
            //amount of substance
            //recordBuffer.append("       <AMOUNTOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES)) + "]]></AMOUNTOFSUBSTANCE_RANGES>\n"); 											//NASR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES),"AMOUNTOFSUBSTANCE_RANGES");          
            contentObject.put("AMOUNTOFSUBSTANCE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <AMOUNTOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT)))) + "]]></AMOUNTOFSUBSTANCE_TEXT>\n");				//NAST
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT),"AMOUNTOFSUBSTANCE_TEXT");          
            contentObject.put("AMOUNTOFSUBSTANCE_TEXT",elementArrayObject);
            
            //electric current
            //recordBuffer.append("       <ELECTRICCURRENT_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_CURRENT_RANGES)) + "]]></ELECTRICCURRENT_RANGES>\n");													//NECR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_RANGES),"ELECTRICCURRENT_RANGES");          
            contentObject.put("ELECTRICCURRENT_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ELECTRICCURRENT_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT)))) + "]]></ELECTRICCURRENT_TEXT>\n");						//NECT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_CURRENT_TEXT),"ELECTRICCURRENT_TEXT");          
            contentObject.put("ELECTRICCURRENT_TEXT",elementArrayObject);
            
            //mass
            //recordBuffer.append("       <MASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_RANGES)) + "]]></MASS_RANGES>\n");																						//NMAR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_RANGES),"MASS_RANGES");          
            contentObject.put("MASS_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <MASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_TEXT)))) + "]]></MASS_TEXT>\n");														//NMAT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MASS_TEXT),"MASS_TEXT");          
            contentObject.put("MASS_TEXT",elementArrayObject);
            
            //temperature
            //recordBuffer.append("       <TEMPERATURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TEMPERATURE_RANGES)) + "]]></TEMPERATURE_RANGES>\n");																//NTER
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_RANGES),"TEMPERATURE_RANGES");          
            contentObject.put("TEMPERATURE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <TEMPERATURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT)))) + "]]></TEMPERATURE_TEXT>\n");									//NTET
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TEMPERATURE_TEXT),"TEMPERATURE_TEXT");          
            contentObject.put("TEMPERATURE_TEXT",elementArrayObject);
            
            //time
            //recordBuffer.append("       <TIME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TIME_RANGES)) + "]]></TIME_RANGES>\n");																						//NTIR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_RANGES),"TIME_RANGES");          
            contentObject.put("TIME_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <TIME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TIME_TEXT)))) + "]]></TIME_TEXT>\n");														//NTIT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.TIME_TEXT),"TIME_TEXT");          
            contentObject.put("TIME_TEXT",elementArrayObject);
            
            //size
            //recordBuffer.append("       <SIZE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SIZE_RANGES)) + "]]></SIZE_RANGES>\n");																						//NSIR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_RANGES),"SIZE_RANGES");          
            contentObject.put("SIZE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <SIZE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SIZE_TEXT)))) + "]]></SIZE_TEXT>\n");														//NSIT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.SIZE_TEXT),"SIZE_TEXT");          
            contentObject.put("SIZE_TEXT",elementArrayObject);
            
            //electrical conductance
            recordBuffer.append("       <ELECTRICALCONDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES)) + "]]></ELECTRICALCONDUCTANCE_RANGES>\n");									//NEDR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES),"ELECTRICALCONDUCTANCE_RANGES");          
            contentObject.put("ELECTRICALCONDUCTANCE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ELECTRICALCONDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT)))) + "]]></ELECTRICALCONDUCTANCE_TEXT>\n");	//NEDT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT),"ELECTRICALCONDUCTANCE_TEXT");          
            contentObject.put("ELECTRICALCONDUCTANCE_TEXT",elementArrayObject);
            
            //electrical conductivity
            //recordBuffer.append("       <ELECTRICALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES)) + "]]></ELECTRICALCONDUCTIVITY_RANGES>\n");								//NETR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES),"ELECTRICALCONDUCTIVITY_RANGES");          
            contentObject.put("ELECTRICALCONDUCTIVITY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ELECTRICALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT)))) + "]]></ELECTRICALCONDUCTIVITY_TEXT>\n");//NETT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT),"ELECTRICALCONDUCTIVITY_TEXT");          
            contentObject.put("ELECTRICALCONDUCTIVITY_TEXT",elementArrayObject);
            
            //voltage
            //recordBuffer.append("       <VOLTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLTAGE_RANGES)) + "]]></VOLTAGE_RANGES>\n");																			//NVOR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_RANGES),"VOLTAGE_RANGES");          
            contentObject.put("VOLTAGE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <VOLTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT)))) + "]]></VOLTAGE_TEXT>\n");												//NVOT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.VOLTAGE_TEXT),"VOLTAGE_TEXT");          
            contentObject.put("VOLTAGE_TEXT",elementArrayObject);
            
            //electric field strength
            //recordBuffer.append("       <ELECTRICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES)) + "]]></ELECTRICFIELDSTRENGTH_RANGES>\n");								//NEFR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES),"ELECTRICFIELDSTRENGTH_RANGES");          
            contentObject.put("ELECTRICFIELDSTRENGTH_RANGES",elementArrayObject);
            
            recordBuffer.append("       <ELECTRICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT)))) + "]]></ELECTRICFIELDSTRENGTH_TEXT>\n");	//NEFT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT),"ELECTRICFIELDSTRENGTH_TEXT");          
            contentObject.put("ELECTRICFIELDSTRENGTH_TEXT",elementArrayObject);
            
            //current density
            //recordBuffer.append("       <CURRENTDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CURRENT_DENSITY_RANGES)) + "]]></CURRENTDENSITY_RANGES>\n");														//NCDR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_RANGES),"CURRENTDENSITY_RANGES");          
            contentObject.put("CURRENTDENSITY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <CURRENTDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT)))) + "]]></CURRENTDENSITY_TEXT>\n");						//NCDT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CURRENT_DENSITY_TEXT),"CURRENTDENSITY_TEXT");          
            contentObject.put("CURRENTDENSITY_TEXT",elementArrayObject);
            
            //energy
            //recordBuffer.append("       <ENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ENERGY_RANGES)) + "]]></ENERGY_RANGES>\n");																				//NENR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_RANGES),"ENERGY_RANGES");          
            contentObject.put("ENERGY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ENERGY_TEXT)))) + "]]></ENERGY_TEXT>\n");													//NENT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ENERGY_TEXT),"ENERGY_TEXT");          
            contentObject.put("ENERGY_TEXT",elementArrayObject);
            
            //electrical resistance
            //recordBuffer.append("       <ELECTRICALRESISTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES)) + "]]></ELECTRICALRESISTANCE_RANGES>\n");									//NERR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES),"ELECTRICALRESISTANCE_RANGES");          
            contentObject.put("ELECTRICALRESISTANCE_RANGES",elementArrayObject);
            
            recordBuffer.append("       <ELECTRICALRESISTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT)))) + "]]></ELECTRICALRESISTANCE_TEXT>\n");		//NERT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT),"ELECTRICALRESISTANCE_TEXT");          
            contentObject.put("ELECTRICALRESISTANCE_TEXT",elementArrayObject);
            
            //electrical resistivity
            //recordBuffer.append("       <ELECTRICALRESISTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES)) + "]]></ELECTRICALRESISTIVITY_RANGES>\n");									//NESR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES),"ELECTRICALRESISTIVITY_RANGES");          
            contentObject.put("ELECTRICALRESISTIVITY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ELECTRICALRESISTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT)))) + "]]></ELECTRICALRESISTIVITY_TEXT>\n");	//NEST
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT),"ELECTRICALRESISTIVITY_TEXT");          
            contentObject.put("ELECTRICALRESISTIVITY_TEXT",elementArrayObject);
            
            //electron volt energy
            //recordBuffer.append("       <ELECTRONVOLTENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ELECTRON_VOLT_RANGES)) + "]]></ELECTRONVOLTENERGY_RANGES>\n");												//NEVR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_RANGES),"ELECTRONVOLTENERGY_RANGES");          
            contentObject.put("ELECTRONVOLTENERGY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <ELECTRONVOLTENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT)))) + "]]></ELECTRONVOLTENERGY_TEXT>\n");					//NEVT	
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.ELECTRON_VOLT_TEXT),"ELECTRONVOLTENERGY_TEXT");          
            contentObject.put("ELECTRONVOLTENERGY_TEXT",elementArrayObject);
            
            //capacitance
            //recordBuffer.append("       <CAPACITANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.CAPACITANCE_RANGES)) + "]]></CAPACITANCE_RANGES>\n");																//NCAR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_RANGES),"CAPACITANCE_RANGES");          
            contentObject.put("CAPACITANCE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <CAPACITANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT)))) + "]]></CAPACITANCE_TEXT>\n");									//NCAT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.CAPACITANCE_TEXT),"CAPACITANCE_TEXT");          
            contentObject.put("CAPACITANCE_TEXT",elementArrayObject);
            
            //frequency	
            //recordBuffer.append("       <FREQUENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FREQUENCY_RANGES)) + "]]></FREQUENCY_RANGES>\n");																		//NFRR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_RANGES),"FREQUENCY_RANGES");          
            contentObject.put("FREQUENCY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <FREQUENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT)))) + "]]></FREQUENCY_TEXT>\n");										//NFRT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.FREQUENCY_TEXT),"FREQUENCY_TEXT");          
            contentObject.put("FREQUENCY_TEXT",elementArrayObject);
            
            //power
            //recordBuffer.append("       <POWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.POWER_RANGES)) + "]]></POWER_RANGES>\n");																					//NPOR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_RANGES),"POWER_RANGES");          
            contentObject.put("POWER_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <POWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.POWER_TEXT)))) + "]]></POWER_TEXT>\n");													//NPOT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.POWER_TEXT),"POWER_TEXT");          
            contentObject.put("POWER_TEXT",elementArrayObject);
            
            //apparent power 
            //recordBuffer.append("       <APPARENTPOWER_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.APPARENT_POWER_RANGES)) + "]]></APPARENTPOWER_RANGES>\n");															//NAPR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPARENT_POWER_RANGES),"APPARENTPOWER_RANGES");          
            contentObject.put("APPARENTPOWER_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <APPARENTPOWER_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT)))) + "]]></APPARENTPOWER_TEXT>\n");							//NAPT							
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.APPARENT_POWER_TEXT),"APPARENTPOWER_TEXT");          
            contentObject.put("APPARENTPOWER_TEXT",elementArrayObject);
            
            //percentage
            //recordBuffer.append("       <PERCENTAGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PERCENTAGE_RANGES)) + "]]></PERCENTAGE_RANGES>\n");																	//NPCR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PERCENTAGE_RANGES),"PERCENTAGE_RANGES");          
            contentObject.put("PERCENTAGE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <PERCENTAGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT)))) + "]]></PERCENTAGE_TEXT>\n");										//NPCT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.PERCENTAGE_TEXT),"PERCENTAGE_TEXT");          
            contentObject.put("PERCENTAGE_TEXT",elementArrayObject);
            
            //magnetic flux density
            //recordBuffer.append("       <MAGNETICFLUXDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES)) + "]]></MAGNETICFLUXDENSITY_RANGES>\n");										//NMDR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES),"MAGNETICFLUXDENSITY_RANGES");          
            contentObject.put("MAGNETICFLUXDENSITY_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <MAGNETICFLUXDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT)))) + "]]></MAGNETICFLUXDENSITY_TEXT>\n");		//NMDT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT),"MAGNETICFLUXDENSITY_TEXT");          
            contentObject.put("MAGNETICFLUXDENSITY_TEXT",elementArrayObject);
            
            //inductance
            //recordBuffer.append("       <INDUCTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.INDUCTANCE_RANGES)) + "]]></INDUCTANCE_RANGES>\n");																	//NINR
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_RANGES),"INDUCTANCE_RANGES");          
            contentObject.put("INDUCTANCE_RANGES",elementArrayObject);
            
            //recordBuffer.append("       <INDUCTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT)))) + "]]></INDUCTANCE_TEXT>\n");										//NINT
            elementArrayObject = formJsonArray(rec.getStrings(EVCombinedRec.INDUCTANCE_TEXT),"INDUCTANCE_TEXT");          
            contentObject.put("INDUCTANCE_TEXT",elementArrayObject);
            
            //volume charge density
            recordBuffer.append("       <VOLUMECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES)) + "]]></VOLUMECHARGEDENSITY_RANGES>\n");										//NVCR
            recordBuffer.append("       <VOLUMECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT)))) + "]]></VOLUMECHARGEDENSITY_TEXT>\n");		//NVCT
            
            //surface charge density
            recordBuffer.append("       <SURFACECHARGEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES)) + "]]></SURFACECHARGEDENSITY_RANGES>\n");									//NSCR
            recordBuffer.append("       <SURFACECHARGEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT)))) + "]]></SURFACECHARGEDENSITY_TEXT>\n");		//NSCT
            
            //decibel
            recordBuffer.append("       <DECIBEL_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_RANGES)) + "]]></DECIBEL_RANGES>\n");																			//NDER
            recordBuffer.append("       <DECIBEL_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_TEXT)))) + "]]></DECIBEL_TEXT>\n");												//NDET
            
            //luminous flux
            recordBuffer.append("       <LUMINOUSFLUX_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_FLUX_RANGES)) + "]]></LUMINOUSFLUX_RANGES>\n");															//NLFR
            recordBuffer.append("       <LUMINOUSFLUX_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_FLUX_TEXT)))) + "]]></LUMINOUSFLUX_TEXT>\n");								//NLFT
            
            //illuminance
            recordBuffer.append("       <ILLUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ILLUMINANCE_RANGES)) + "]]></ILLUMINANCE_RANGES>\n");																//NILR
            recordBuffer.append("       <ILLUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ILLUMINANCE_TEXT)))) + "]]></ILLUMINANCE_TEXT>\n");									//NILT
            
            //bit rate
            recordBuffer.append("       <BITRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.BIT_RATE_RANGES)) + "]]></BITRATE_RANGES>\n");																			//NBIR
            recordBuffer.append("       <BITRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BIT_RATE_TEXT)))) + "]]></BITRATE_TEXT>\n");												//NBIT
            
            //mass density
            recordBuffer.append("       <MASSDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_DENSITY_RANGES)) + "]]></MASSDENSITY_RANGES>\n");																//NMSR
            recordBuffer.append("       <MASSDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_DENSITY_TEXT)))) + "]]></MASSDENSITY_TEXT>\n");									//NMST
            
            //mass flow rate
            recordBuffer.append("       <MASSFLOWRATE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MASS_FLOW_RATE_RANGES)) + "]]></MASSFLOWRATE_RANGES>\n");																	//NMRR
            recordBuffer.append("       <MASSFLOWRATE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MASS_FLOW_RATE_TEXT)))) + "]]></MASSFLOWRATE_TEXT>\n");								//NMRT
            
            //force
            recordBuffer.append("       <FORCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.FORCE_RANGES)) + "]]></FORCE_RANGES>\n");																					//NFOR
            recordBuffer.append("       <FORCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.FORCE_TEXT)))) + "]]></FORCE_TEXT>\n");													//NFOT
            
            //torque
            recordBuffer.append("       <TORQUE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.TORQUE_RANGES)) + "]]></TORQUE_RANGES>\n");																				//NTOR
            recordBuffer.append("       <TORQUE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TORQUE_TEXT)))) + "]]></TORQUE_TEXT>\n");													//NTOT
            
            //pressure
            recordBuffer.append("       <PRESSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.PRESSURE_RANGES)) + "]]></PRESSURE_RANGES>\n");																			//NPRR
            recordBuffer.append("       <PRESSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRESSURE_TEXT)))) + "]]></PRESSURE_TEXT>\n");											//NPRT
            
            //area
            recordBuffer.append("       <AREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AREA_RANGES)) + "]]></AREA_RANGES>\n");																						//NARR
            recordBuffer.append("       <AREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AREA_TEXT)))) + "]]></AREA_TEXT>\n");														//NART
            
            //volume
            recordBuffer.append("       <VOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VOLUME_RANGES)) + "]]></VOLUME_RANGES>\n");																				//NVLR
            recordBuffer.append("       <VOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TEXT)))) + "]]></VOLUME_TEXT>\n");													//NVLT
            
            //velocity
            recordBuffer.append("       <VELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.VELOCITY_RANGES)) + "]]></VELOCITY_RANGES>\n");																			//NVER
            recordBuffer.append("       <VELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VELOCITY_TEXT)))) + "]]></VELOCITY_TEXT>\n");											//NVET
            
            //acceleration
            recordBuffer.append("       <ACCELERATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ACCELERATION_RANGES)) + "]]></ACCELERATION_RANGES>\n");																//NACR
            recordBuffer.append("       <ACCELERATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ACCELERATION_TEXT)))) + "]]></ACCELERATION_TEXT>\n");								//NACT
            
            //angular velocity
            recordBuffer.append("       <ANGULARVELOCITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ANGULAR_VELOCITY_RANGES)) + "]]></ANGULARVELOCITY_RANGES>\n");													//NAVR
            recordBuffer.append("       <ANGULARVELOCITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ANGULAR_VELOCITY_TEXT)))) + "]]></ANGULARVELOCITY_TEXT>\n");						//NAVT
            
            //rotational speed 	
            recordBuffer.append("       <ROTATIONALSPEED_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ROTATIONAL_SPEED_RANGES)) + "]]></ROTATIONALSPEED_RANGES>\n");													//NRSR
            recordBuffer.append("       <ROTATIONALSPEED_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ROTATIONAL_SPEED_TEXT)))) + "]]></ROTATIONALSPEED_TEXT>\n");						//NRST
            
            //age mass
            recordBuffer.append("       <AGE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.AGE_RANGES)) + "]]></AGE_RANGES>\n");																						//NAGR
            recordBuffer.append("       <AGE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGE_TEXT)))) + "]]></AGE_TEXT>\n");															//NAGT
           
            //molar mass
            recordBuffer.append("       <MOLARMASS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_MASS_RANGES)) + "]]></MOLARMASS_RANGES>\n");																		//NMMR
            recordBuffer.append("       <MOLARMASS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_MASS_TEXT)))) + "]]></MOLARMASS_TEXT>\n");										//NMMT
            
            //molality
            recordBuffer.append("       <MOLALITYOFSUBSTANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES)) + "]]></MOLALITYOFSUBSTANCE_RANGES>\n");										//NMOR
            recordBuffer.append("       <MOLALITYOFSUBSTANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT)))) + "]]></MOLALITYOFSUBSTANCE_TEXT>\n");		//NMOT
            
            //radioactivity
            recordBuffer.append("       <RADIOACTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIOACTIVITY_RANGES)) + "]]></RADIOACTIVITY_RANGES>\n");															//NRAR
            recordBuffer.append("       <RADIOACTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIOACTIVITY_TEXT)))) + "]]></RADIOACTIVITY_TEXT>\n");							//NRAT
            
            //absorbed dose
            recordBuffer.append("       <ABSORBEDDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.ABSORBED_DOSE_RANGES)) + "]]></ABSORBEDDOSE_RANGES>\n");															//NABR
            recordBuffer.append("       <ABSORBEDDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ABSORBED_DOSE_TEXT)))) + "]]></ABSORBEDDOSE_TEXT>\n");								//NABT
            
            //radiation exposure
            recordBuffer.append("       <RADIATIONEXPOSURE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.RADIATION_EXPOSURE_RANGES)) + "]]></RADIATIONEXPOSURE_RANGES>\n");												//NRER
            recordBuffer.append("       <RADIATIONEXPOSURE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.RADIATION_EXPOSURE_TEXT)))) + "]]></RADIATIONEXPOSURE_TEXT>\n");				//NRET
            
            //Luminance
            recordBuffer.append("       <LUMINANCE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINANCE_RANGES)) + "]]></LUMINANCE_RANGES>\n");																		//NLUR
            recordBuffer.append("       <LUMINANCE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINANCE_TEXT)))) + "]]></LUMINANCE_TEXT>\n");										//NLUT
            
            //Magnetic field strengt
            recordBuffer.append("       <MAGNETICFIELDSTRENGTH_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES)) + "]]></MAGNETICFIELDSTRENGTH_RANGES>\n");								//NFSR
            recordBuffer.append("       <MAGNETICFIELDSTRENGTH_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT)))) + "]]></MAGNETICFIELDSTRENGTH_TEXT>\n");	//NFST
            
            //Spectral_Efficiency
            recordBuffer.append("       <SPECTRALEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES)) + "]]></SPECTRALEFFICIENCY_RANGES>\n");											//NSER
            recordBuffer.append("       <SPECTRALEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT)))) + "]]></SPECTRALEFFICIENCY_TEXT>\n");			//NSET
            
            //Surface_Power_Density
            recordBuffer.append("       <SURFACEPOWERDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_POWER_DENSITY_RANGES)) + "]]></SURFACEPOWERDENSITY_RANGES>\n");										//NSPR
            recordBuffer.append("       <SURFACEPOWERDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_POWER_DENSITY_TEXT)))) + "]]></SURFACEPOWERDENSITY_TEXT>\n");		//NSPT
            
            //thermal conductivity
            recordBuffer.append("       <THERMALCONDUCTIVITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES)) + "]]></THERMALCONDUCTIVITY_RANGES>\n");										//NTCR
            recordBuffer.append("       <THERMALCONDUCTIVITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT)))) + "]]></THERMALCONDUCTIVITY_TEXT>\n");			//NTCT
            
            //new added on 4/11/2016
            //Decibel isotropic
            recordBuffer.append("       <DECIBELISOTROPIC_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_ISOTROPIC_RANGES)) + "]]></DECIBELISOTROPIC_RANGES>\n");												//NDIR
            recordBuffer.append("       <DECIBELISOTROPIC_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_ISOTROPIC_TEXT)))) + "]]></DECIBELISOTROPIC_TEXT>\n");					//NDIT
            
            //Decibel milliwatts
            recordBuffer.append("       <DECIBELMILLIWATTS_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.DECIBEL_MILLIWATTS_RANGES)) + "]]></DECIBELMILLIWATTS_RANGES>\n");												//NDMR
            recordBuffer.append("       <DECIBELMILLIWATTS_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DECIBEL_MILLIWATTS_TEXT)))) + "]]></DECIBELMILLIWATTS_TEXT>\n");				//NDMT
                   
            //Equivalent Dose
            recordBuffer.append("       <EQUIVALENTDOSE_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.EQUIVALENT_DOSE_RANGES)) + "]]></EQUIVALENTDOSE_RANGES>\n");														//NEQR
            recordBuffer.append("       <EQUIVALENTDOSE_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EQUIVALENT_DOSE_TEXT)))) + "]]></EQUIVALENTDOSE_TEXT>\n");						//NEQT
            
            //Molar concentration
            recordBuffer.append("       <MOLARCONCENTRATION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.MOLAR_CONCENTRATION_RANGES)) + "]]></MOLARCONCENTRATION_RANGES>\n");											//NMCR
            recordBuffer.append("       <MOLARCONCENTRATION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MOLAR_CONCENTRATION_TEXT)))) + "]]></MOLARCONCENTRATION_TEXT>\n");			//NMCT
            
            //Linear Density
            recordBuffer.append("       <LINEARDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LINEAR_DENSITY_RANGES)) + "]]></LINEARDENSITY_RANGES>\n");															//NLDR
            out.println("       <LINEARDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINEAR_DENSITY_TEXT)))) + "]]></LINEARDENSITY_TEXT>\n");							//NLDT
            
            //luminous efficiency
            recordBuffer.append("       <LUMINOUSEFFICIENCY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES)) + "]]></LUMINOUSEFFICIENCY_RANGES>\n");											//NLYR
            recordBuffer.append("       <LUMINOUSEFFICIENCY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT)))) + "]]></LUMINOUSEFFICIENCY_TEXT>\n");			//NLYT
            
            //luminous efficacy
            recordBuffer.append("       <LUMINOUSEFFICACY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.LUMINOUS_EFFICACY_RANGES)) + "]]></LUMINOUSEFFICACY_RANGES>\n");												//NLER
            recordBuffer.append("       <LUMINOUSEFFICACY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LUMINOUS_EFFICACY_TEXT)))) + "]]></LUMINOUSEFFICACY_TEXT>\n");					//NLET
            
            //Specific Energy
            recordBuffer.append("       <SPECIFICENERGY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_ENERGY_RANGES)) + "]]></SPECIFICENERGY_RANGES>\n");														//NSFR
            recordBuffer.append("       <SPECIFICENERGY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_ENERGY_TEXT)))) + "]]></SPECIFICENERGY_TEXT>\n");						//NSFT
            
            //Specific Surface area
            recordBuffer.append("       <SPECIFICSURFACEAREA_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES)) + "]]></SPECIFICSURFACEAREA_RANGES>\n");										//NSSR
            recordBuffer.append("       <SPECIFICSURFACEAREA_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT)))) + "]]></SPECIFICSURFACEAREA_TEXT>\n");		//NSST
            
            //Specific Volume
            recordBuffer.append("       <SPECIFICVOLUME_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SPECIFIC_VOLUME_RANGES)) + "]]></SPECIFICVOLUME_RANGES>\n");														//NSVR
            recordBuffer.append("       <SPECIFICVOLUME_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SPECIFIC_VOLUME_TEXT)))) + "]]></SPECIFICVOLUME_TEXT>\n");						//NSVT
            
            //Surface Tension
            recordBuffer.append("       <SURFACETENSION_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_TENSION_RANGES)) + "]]></SURFACETENSION_RANGES>\n");														//NSTR
            recordBuffer.append("       <SURFACETENSION_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_TENSION_TEXT)))) + "]]></SURFACETENSION_TEXT>\n");						//NSTT
            
            //Surface Density
            recordBuffer.append("       <SURFACEDENSITY_RANGES><![CDATA[" + notNull(rec.getString(EVCombinedRec.SURFACE_DENSITY_RANGES)) + "]]></SURFACEDENSITY_RANGES>\n");														//NSDR
            recordBuffer.append("       <SURFACEDENSITY_TEXT><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SURFACE_DENSITY_TEXT)))) + "]]></SURFACEDENSITY_TEXT>\n");						//NSDT
            
            
            
            recordBuffer.append("       <NUMERICAL_UNITS><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.NUMERICALUNITS)))+ "]]></NUMERICAL_UNITS>\n");																//NUU
            
            //EID
            recordBuffer.append("       <EID><![CDATA[" + notNull(rec.getString(EVCombinedRec.EID)) + "]]></EID>\n");	        																									//EID
            recordBuffer.append("       <DEPARTMENTID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DEPARTMENTID)))) + "]]></DEPARTMENTID>\n");												//DTID
            
            //added for georef at 03/16/2016
            //TITLE_OF_COLLECTION
            recordBuffer.append("       <TITLEOFCOLLECTION><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION))) + " QstemQ " +  notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.TITLE_OF_COLLECTION)))) + "]]></TITLEOFCOLLECTION>\n");									//TIC
            
            //UNIVERSITY
            recordBuffer.append("       <UNIVERSITY><![CDATA[" +notNull(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))+ " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.UNIVERSITY)))) + "]]></UNIVERSITY>\n");														//UNI
            
            //TYPE_OF_DEGREE
            recordBuffer.append("       <TYPEOFDEGREE><![CDATA[" + notNull(rec.getString(EVCombinedRec.TYPE_OF_DEGREE)) + "]]></TYPEOFDEGREE>\n");																				//TOD
           
            //ANNOTATION
            recordBuffer.append("       <ANNOTATION><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.ANNOTATION))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ANNOTATION)))) + "]]></ANNOTATION>\n");																			//ANN
            
            //MAP_SCALE
            recordBuffer.append("       <MAPSCALE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_SCALE))) + "]]></MAPSCALE>\n");																				//MPS
            
            //MAP_TYPE
            recordBuffer.append("       <MAPTYPE><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.MAP_TYPE))) + "]]></MAPTYPE>\n");																					//MPT
            
            //SOURCE_NOTE
            recordBuffer.append("       <SOURCENOTE><![CDATA[" + notNull(rec.getString(EVCombinedRec.SOURCE_NOTE)) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SOURCE_NOTE))))+ "]]></SOURCENOTE>\n");																						//SNO
            
            //GRANTID
            recordBuffer.append("       <GRANTID><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) + "]]></GRANTID>\n");																					//GID
            
            //GRANTAGENCY
            recordBuffer.append("       <GRANTAGENCY><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + "]]></GRANTAGENCY>\n");		//GAG
            
            //SPARE FIELDS
            //SOURCEBIBTEXT
            //recordBuffer.append("       <EV_SPARE1><![CDATA[]]></EV_SPARE1>");	//tempotary block out SOURCEBIBTEXT
            recordBuffer.append("       <EV_SPARE1><![CDATA["+ notNull(rec.getString(EVCombinedRec.SOURCEBIBTEXT)) +"]]></EV_SPARE1>\n");//SPA1
           
            //move standardid to here to get all search
            //STANDARDID
            if(rec.getString(EVCombinedRec.STANDARDID)==null)
            {
            	recordBuffer.append("       <EV_SPARE2><![CDATA[]]></EV_SPARE2>\n");
            }
            else      
            {
            	recordBuffer.append("       <EV_SPARE2><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID))) + "QstemQ " +notNull(getStems(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDID)))) +"]]></EV_SPARE2>\n");//SPA2
            }                     
            
            //recordBuffer.append("       <EV_SPARE3><![CDATA[]]></EV_SPARE3>");//SPA3
            //added by hmo on 2019/09/11 for inspec orgid
            recordBuffer.append("       <EV_SPARE3><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ORG_ID)))) + "]]></EV_SPARE3>\n");//SPA3
            
            recordBuffer.append("       <EV_SPARE4><![CDATA[]]></EV_SPARE4>\n");//SPA4
            
            recordBuffer.append("       <EV_SPARE5><![CDATA[]]></EV_SPARE5>\n");//SPA5
            
            recordBuffer.append("       <EV_SPARE6><![CDATA[]]></EV_SPARE6>\n");//SPA6
            
            //move standardid to SPA2 to get search "all"
            //use for ISOPENACESS
            recordBuffer.append("       <EV_SPARE7><![CDATA["+ notNull(rec.getString(EVCombinedRec.ISOPENACESS)) +"]]></EV_SPARE7>\n");//SPA7
            
           
            if(rec.getString(EVCombinedRec.STANDARDDESIGNATION)==null)
            {
            	recordBuffer.append("       <EV_SPARE8><![CDATA[]]></EV_SPARE8>\n");
            }      
            else
            {
            	recordBuffer.append("       <EV_SPARE8><![CDATA[" + notNull(formatStandardCodes(rec.getString(EVCombinedRec.STANDARDDESIGNATION))) + "]]></EV_SPARE8>\n");//SPA8
            }
            
            //GRANTTEXT
            //recordBuffer.append("       <EV_SPARE9><![CDATA["+ notNull(rec.getString(EVCombinedRec.GRANTTEXT))+ " QstemQ " + notNull(getStems(rec.getString(EVCombinedRec.GRANTTEXT))) +"]]></EV_SPARE9>");//SPA9
            recordBuffer.append("       <EV_SPARE9><![CDATA[]]></EV_SPARE9>\n");//SPA9
            recordBuffer.append("       <EV_SPARE10><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) +" "+ notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + " "+
            			notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTTEXT)))+ " QstemQ " + notNull(getStems(multiFormat(rec.getStrings(EVCombinedRec.GRANTTEXT)))) +"]]></EV_SPARE10>\n");//SPA0
            
            recordBuffer.append("   </ROW>");
            //recordBuffer.append("\"Content\": \"");
            /*
            JSONObject json = new JSONObject();
            json.put("DataSource","EV");
            json.put("Action","Update");
            json.put("UniqueID",eid);
            json.put("Content",recordBuffer.toString());
            //recordBuffer.append("   \"\n}");
            KafkaTest kafka = new KafkaTest();
            kafka.runProducer(json.toString(),eid);
            //kafka.runProducer(prettyPrintJSON(json.toString()),eid);
            */
            /*
            StringBuffer jBuffer = new StringBuffer();
            jBuffer.append("{\n");
            jBuffer.append("\t\"DataSource\":\"EV\",\n");
            jBuffer.append("\t\"Action\":\"Update\",\n");
            jBuffer.append("\t\"UniqueID\":\""+eid+"\",\n");
            jBuffer.append("\t\"Content\":\""+recordBuffer.toString()+"\"\n");
            jBuffer.append("}");
            */
            JSONArray evArray = new JSONArray();
            JSONObject dataSourceObject = new JSONObject();
            dataSourceObject.put("DataSource","EV");
            JSONObject actionObject = new JSONObject();
            actionObject.put("Action","Update");
            JSONObject uniqueIDObject = new JSONObject();
            uniqueIDObject.put("UniqueID",eid);
            JSONObject evContent = new JSONObject();
            evContent.put("CONTENT",contentObject);
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
            //kafka.runProducer(jBuffer.toString(),eid);
            kafka.runProducer(prettyJsonString,eid);
          
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
	    		arrayElement = cleaner.stripBadChars(arrayElement);	    		
	    		jArray.add(arrayElement);
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
