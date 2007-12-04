package org.ei.data;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.query.base.PorterStemmer;
import org.ei.xml.Entity;

public class CombinedXMLWriter
    extends CombinedWriter
{
    private Perl5Util perl = new Perl5Util();

    private PorterStemmer stemmer = new PorterStemmer();
    private DataCleaner cleaner = new DataCleaner();

    private int recsPerfile;

    private int curRecNum = 0;

    private int numberID;

    private String databaseID;

    private PrintWriter out;

    private boolean open = false;

    private String filepath;

    //private String bogusAbstract = "";

    public String getDatabaseID() {
        return databaseID;
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


    public CombinedXMLWriter(int recsPerfile,
                             int numberID,
                             String databaseID)

    {
        this.databaseID = databaseID;
        this.numberID = numberID;
        this.recsPerfile = recsPerfile;
    }

    public void begin()
        throws Exception
    {
        long time = System.currentTimeMillis();
        filepath = databaseID + Long.toString(time) + ".xml";
        out = new PrintWriter(new FileWriter(filepath));
        open = true;
        curRecNum = 0;
        out.println("<?xml version=\"1.0\"?>");
        out.println("<!DOCTYPE ROWSET SYSTEM \"../bin/EVROWSET.dtd\">");
        out.println("<ROWSET>");
    }

    public void writeRec(EVCombinedRec rec)
        throws Exception
    {

        if (curRecNum >= recsPerfile)
        {
            if (open)
            {
                end();
            }
            begin();
        }

        out.println("   <ROW> ");
        out.println("       <EIDOCID>" + rec.getString(EVCombinedRec.DOCID) + "</EIDOCID>");
        out.println("       <DEDUPKEY>" + rec.getString(EVCombinedRec.DEDUPKEY) + "</DEDUPKEY>");
        out.println("       <DATABASE>" + rec.getString(EVCombinedRec.DATABASE) + "</DATABASE>");
        out.println("       <LOADNUMBER>" + rec.getString(EVCombinedRec.LOAD_NUMBER) + "</LOADNUMBER>");
        out.println("       <PUBYEAR>" + rec.getString(EVCombinedRec.PUB_YEAR) + "</PUBYEAR>");
        out.println("       <ACCESSIONNUMBER>" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ACCESSION_NUMBER))) + "</ACCESSIONNUMBER>");
        out.println("       <AUTHOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.AUTHOR)))) + "]]></AUTHOR>");
        out.println("       <AUTHORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))))) + "]]></AUTHORAFFILIATION>");
        out.println("       <AFFILIATIONLOCATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION)))) + "]]></AFFILIATIONLOCATION>");
        out.println("       <TITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TITLE))))) + "]]></TITLE>");
        out.println("       <TRANSLATEDTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.TRANSLATED_TITLE))))) + "]]></TRANSLATEDTITLE>");
        out.println("       <VOLUMETITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.VOLUME_TITLE))))) + "]]></VOLUMETITLE>");
        out.println("       <ABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.ABSTRACT)))) + "]]></ABSTRACT>");
        out.println("       <OTHERABSTRACT><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.OTHER_ABSTRACT)))) + "]]></OTHERABSTRACT>");
        out.println("       <EDITOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.EDITOR))))+"]]></EDITOR>");
        out.println("       <EDITORAFFILIATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.EDITOR_AFFILIATION))))) + "]]></EDITORAFFILIATION>");
        out.println("       <TRANSLATOR><![CDATA[" + notNull(Entity.prepareString(formatAuthors(rec.getStrings(EVCombinedRec.TRANSLATOR)))) + "]]></TRANSLATOR>");
        out.println("       <CONTROLLEDTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CONTROLLED_TERMS))))) + "]]></CONTROLLEDTERMS>");
        out.println("       <UNCONTROLLEDTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.UNCONTROLLED_TERMS))))) + "]]></UNCONTROLLEDTERMS>");
        out.println("       <ISSN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN))))) + "]]></ISSN>");
        out.println("       <ISSNOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISSN(rec.getStrings(EVCombinedRec.ISSN_OF_TRANSLATION))))) + "]]></ISSNOFTRANSLATION>");
        out.println("       <CODEN><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN)))) + "]]></CODEN>");
        out.println("       <CODENOFTRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CODEN_OF_TRANSLATION)))) + "]]></CODENOFTRANSLATION>");
        out.println("       <ISBN><![CDATA[" + notNull(Entity.prepareString(multiFormat(prepareISBN(rec.getStrings(EVCombinedRec.ISBN))))) + "]]></ISBN>");
        out.println("       <SERIALTITLE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE))))) + "]]></SERIALTITLE>");
        out.println("       <SERIALTITLETRANSLATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SERIAL_TITLE_TRANSLATION))))) + "]]></SERIALTITLETRANSLATION>");
        out.println("       <MAINHEADING><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.MAIN_HEADING))))) + "]]></MAINHEADING>");
        out.println("       <SUBHEADING><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SUB_HEADING))) + "]]></SUBHEADING>");
        out.println("       <PUBLISHERNAME><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PUBLISHER_NAME)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PUBLISHER_NAME))))) + "]]></PUBLISHERNAME>");
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
        out.println("       <AVAILABILITY><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.AVAILABILITY)))) + "]]></AVAILABILITY>");
        out.println("       <NOTES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.NOTES))))) + "]]></NOTES>");
        out.println("       <PATENTAPPDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTAPPDATE)))) + "]]></PATENTAPPDATE>");
        out.println("       <PATENTISSUEDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENTISSUEDATE)))) + "]]></PATENTISSUEDATE>");
        out.println("       <COMPANIES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.COMPANIES)))) + "]]></COMPANIES>");
        out.println("       <CASREGISTRYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CASREGISTRYNUMBER)))) + "]]></CASREGISTRYNUMBER>");
        out.println("       <BUSINESSTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.BUSINESSTERMS))))) + "]]></BUSINESSTERMS>");
        out.println("       <CHEMICALTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALTERMS))))) + "]]></CHEMICALTERMS>");
        out.println("       <CHEMAC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.CHEMICALACRONYMS)))) + "]]></CHEMAC>");
        out.println("       <SIC><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PATENT_NUMBER)))) + "]]></SIC>");
        out.println("       <INDUSTRIALCODES><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALCODES)))) + "]]></INDUSTRIALCODES>");
        out.println("       <INDUSTRIALSECTORS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INDUSTRIALSECTORS)))) + "]]></INDUSTRIALSECTORS>");
        out.println("       <SCOPE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SCOPE)))) + "]]></SCOPE>");
        out.println("       <AGENCY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.AGENCY)))) + "]]></AGENCY>");
        out.println("       <DERWENTACCESSIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DERWENT_ACCESSION_NUMBER)))) + "]]></DERWENTACCESSIONNUMBER>");
        out.println("       <APPLICATIONNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_NUMBER)))) + "]]></APPLICATIONNUMBER>");
        out.println("       <APPLICATIONCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.APPLICATION_COUNTRY))))+ "]]></APPLICATIONCOUNTRY>");
        out.println("       <INTPATENTCLASSIFICATION><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.INT_PATENT_CLASSIFICATION)))) + "]]></INTPATENTCLASSIFICATION>");
        out.println("       <LINKEDTERMS><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.LINKED_TERMS))))) + "]]></LINKEDTERMS>");
        out.println("       <ENTRYYEAR><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.ENTRY_YEAR))) + "]]></ENTRYYEAR>");
        out.println("       <PRIORITYNUMBER><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_NUMBER)))) + "]]></PRIORITYNUMBER>");
        out.println("       <PRIORITYDATE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_DATE)))) + "]]></PRIORITYDATE>");
        out.println("       <PRIORITYCOUNTRY><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PRIORITY_COUNTRY))))+ "]]></PRIORITYCOUNTRY>");
        out.println("       <SOURCE><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE)))) + " QstemQ " + notNull(getStems(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.SOURCE))))) + "]]></SOURCE>");
        out.println("       <SECONDARYSRCTITLE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.SECONDARY_SRC_TITLE)))) + "]]></SECONDARYSRCTITLE>");
        out.println("       <MAINTERM><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM))) + " QstemQ " + notNull(getStems(Entity.prepareString(rec.getString(EVCombinedRec.MAIN_TERM)))) + "]]></MAINTERM>");
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
        out.println("       <AUTHORITYCODE><![CDATA[" + notNull(Entity.prepareString(rec.getString(EVCombinedRec.AUTHORITY_CODE))) + "]]></AUTHORITYCODE>");
        out.println("       <PCITED><![CDATA[" + hasPcited(rec.getString(EVCombinedRec.PCITED)) + "]]></PCITED>");
        out.println("       <PCITEDINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PCITEDINDEX)))) + "]]></PCITEDINDEX>");
        out.println("       <PREFINDEX><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.PREFINDEX)))) + "]]></PREFINDEX>");
        out.println("       <DMASK><![CDATA[" + getMask(rec) + "]]></DMASK>");
        out.println("       <DOI><![CDATA[" + notNull(Entity.prepareString(multiFormat(rec.getStrings(EVCombinedRec.DOI)))) + hasDOI(rec) + "]]></DOI>");
        out.println("   </ROW>");
        ++curRecNum;
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
        return finishedString;
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

    public void end()
        throws Exception
    {
        if (open)
        {
            out.println("</ROWSET>");
            out.close();
            File f = new File(filepath);
            String part1 = Long.toString(System.currentTimeMillis());
            String part2 = "_updat_rowset_";
            String part3 = Integer.toString(curRecNum);
            String part4 = "_" + databaseID + "-";
            String part5 = Integer.toString(numberID);
            String part6 = ".xml";
            StringBuffer finalFileName = new StringBuffer();
            finalFileName.append(part1);
            finalFileName.append(part2);
            finalFileName.append(part3);
            finalFileName.append(part4);
            finalFileName.append(part5);
            finalFileName.append(part6);
            System.out.println(" finalFileName :: "+finalFileName.toString());
            f.renameTo(new File(finalFileName.toString()));
            DataValidator d = new DataValidator();
            d.validateFile(finalFileName.toString());
            open = false;
        }
    }
}