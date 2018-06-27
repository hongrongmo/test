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

import org.ei.common.*;


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
        setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
        this.eid = rec.getString(EVCombinedRec.DOCID);
        begin();
        out.println("   <ROW> ");
        out.println("       <EIDOCID>" + this.eid + "</EIDOCID>");
        out.println("       <PARENTID>" +  rec.getString(EVCombinedRec.PARENT_ID) + "</PARENTID>");
        out.println("       <DEDUPKEY>" + rec.getString(EVCombinedRec.DEDUPKEY) + "</DEDUPKEY>");
        out.println("       <DATABASE>" + rec.getString(EVCombinedRec.DATABASE) + "</DATABASE>");
        out.println("       <LOADNUMBER>" + rec.getString(EVCombinedRec.LOAD_NUMBER) + "</LOADNUMBER>");
        out.println("       <DATESORT>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.DATESORT))) + "</DATESORT>");
        out.println("       <PUBYEAR>" + rec.getString(EVCombinedRec.PUB_YEAR) + "</PUBYEAR>");
        out.println("       <ACCESSIONNUMBER>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ACCESSION_NUMBER))) + "</ACCESSIONNUMBER>");
        out.println("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR")))) + "]]></AUTHOR>");
        out.println("       <AUTHORID><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHORID)))) + "]]></AUTHORID>");
        out.println("       <AUTHORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(addIndex(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION")))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))))) + "]]></AUTHORAFFILIATION>");
        out.println("       <AFFILIATIONLOCATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)))) + "]]></AFFILIATIONLOCATION>");
        out.println("       <TITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE))))) + "]]></TITLE>");
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
        out.println("       <ECLACODE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.ECLA_CODES)))) + "]]></ECLACODE>");
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
        out.println("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(addIndex(rec.getString(EVCombinedRec.AUTHORITY_CODE),"AUTHORITYCODE"))) + "]]></AUTHORITYCODE>");
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
        out.println("       <CPCCLASS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CPCCLASS)))) + "]]></CPCCLASS>");
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
       
        out.println("       <EV_SPARE2><![CDATA[]]></EV_SPARE2>"); //move GRANTTEXT to spa9 to avoid all search  //SPA2
        //out.println("       <EV_SPARE2><![CDATA["+ notNull(rec.getString(EVCombinedRec.GRANTTEXT))+ " QstemQ " + notNull(getStems(rec.getString(EVCombinedRec.GRANTTEXT))) +"]]></EV_SPARE2>");                
        
        out.println("       <EV_SPARE3><![CDATA[]]></EV_SPARE3>");//SPA3
        
        out.println("       <EV_SPARE4><![CDATA[]]></EV_SPARE4>");//SPA4
        
        out.println("       <EV_SPARE5><![CDATA[]]></EV_SPARE5>");//SPA5
        
        out.println("       <EV_SPARE6><![CDATA[]]></EV_SPARE6>");//SPA6
        
        //STANDARDID
        if(rec.getString(EVCombinedRec.STANDARDID)==null)
        {
        	out.println("       <EV_SPARE7><![CDATA[]]></EV_SPARE7>");
        }
        else      
        {
        	out.println("       <EV_SPARE7><![CDATA[" + notNull(rec.getString(EVCombinedRec.STANDARDID)) + "]]></EV_SPARE7>");//SPA7
        }       
        
        //STANDARDDESIGNATION
        if(rec.getString(EVCombinedRec.STANDARDDESIGNATION)==null)
        {
        	out.println("       <EV_SPARE8><![CDATA[]]></EV_SPARE8>");
        }      
        else
        {
        	out.println("       <EV_SPARE8><![CDATA[" + notNull(rec.getString(EVCombinedRec.STANDARDDESIGNATION)) + "]]></EV_SPARE8>");//SPA8
        }
        
        //GRANTTEXT
        out.println("       <EV_SPARE9><![CDATA["+ notNull(rec.getString(EVCombinedRec.GRANTTEXT))+ " QstemQ " + notNull(getStems(rec.getString(EVCombinedRec.GRANTTEXT))) +"]]></EV_SPARE9>");//SPA9
        out.println("       <EV_SPARE10><![CDATA[" + notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTID))) +" "+ notNull(multiFormat(rec.getStrings(EVCombinedRec.GRANTAGENCY))) + " "+
        			notNull(rec.getString(EVCombinedRec.GRANTTEXT))+ " QstemQ " + notNull(getStems(rec.getString(EVCombinedRec.GRANTTEXT))) +"]]></EV_SPARE10>");//SPA0
        
        out.println("   </ROW>");
        ++curRecNum;
        end();
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

    private String[] addIndex(String s[], String key)
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
                	String aid = "";
                	String oo = "";
      
                	if(ss!=null && ss.indexOf(Constants.GROUPDELIMITER)>-1)
                	{
                		int dd = ss.indexOf(Constants.GROUPDELIMITER);
                		aid = ss.substring(0,dd);
                		oo = ss.substring(dd+1);
                		
                	}
                	else
                	{
                		oo = ss;
                		
                	}
               
                    if(oo!=null && getDatabase()!=null && getDatabase().length()>=3)
                    {
                        indexWriter.println(Entity.prepareString(oo).toUpperCase().trim() + "\t" + getDatabase().substring(0,3) + "\t" + aid +"\t" + getPui()+"\t" + getLoadnumber());
                    }
                    
                    o[i] = oo;
                }
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

    private String addIndex(String s, String key)
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
    
            
}
