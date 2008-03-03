package org.ei.data.georef.runtime;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.domain.ElementDataMap;
import org.ei.domain.XMLWrapper;
import org.ei.domain.XMLMultiWrapper;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.apache.oro.text.perl.*;

import java.util.regex.*;

/* =========================================================================
 *
 * ========================================================================= */
public abstract class DocumentView {

    public abstract String getFormat();
    public abstract Key[] getKeys();
    public abstract List getFields();
    public abstract boolean exportLabels();

    private ResultSet rset = null;
    private ElementDataMap ht = null;
    private Perl5Util perl = new Perl5Util();

    public void setResultSet(ResultSet anrset) {
      this.rset = anrset;
    }

    public EIDoc buildDocument(ResultSet rset, DocID did)
              throws Exception
    {
      this.rset = rset;
      this.ht = new ElementDataMap();

      ht.put(Keys.DOCID, did);

      addDocumentValue(Keys.PROVIDER, GRFDocBuilder.PROVIDER_TEXT);
      addDocumentValue(Keys.COPYRIGHT, GRFDocBuilder.GRF_HTML_COPYRIGHT);
      addDocumentValue(Keys.COPYRIGHT_TEXT, GRFDocBuilder.GRF_TEXT_COPYRIGHT);

      addDocumentValue(Keys.TITLE, getTitle());
      addDocumentValue(Keys.TITLE_TRANSLATION, getTranslatedTitle());
      addDocumentValue(Keys.MONOGRAPH_TITLE, getMonographTitle());
      addDocumentValue(Keys.VOLISSUE, getVolIssue());
      addDocumentValue(Keys.ABSTRACT, getAbstract());

      // AUS
      Contributors contributors = null;
      Key keyAffiliation = null;

      String strperson_analytic = createColumnValueField("PERSON_ANALYTIC").getValue();
      if(strperson_analytic != null &&  !strperson_analytic.equalsIgnoreCase("Anonymous"))
      {
        Contributors authors = new Contributors(Keys.AUTHORS,getContributors(strperson_analytic,Keys.AUTHORS));
        if(authors != null)
        {
          ht.put(Keys.AUTHORS, authors);
          contributors = authors;
        }
        // if there is an AUTHOR_AFFILIATION
        // then it will be a PERSON_ANALYTIC affiliation since PERSON_ANALYTIC is not empty
        keyAffiliation = Keys.AUTHOR_AFFS;
      }

      // EDS
      String strperson_monograph = createColumnValueField("PERSON_MONOGRAPH").getValue();
      if(strperson_monograph != null &&  !strperson_monograph.equalsIgnoreCase("Anonymous"))
      {
        // Editors of the Collection/Series
        // String strperson_collection = createColumnValueField("PERSON_COLLECTION").getValue();
        // if(strperson_collection != null)
        //{
          //strperson_monograph = strperson_monograph.concat(GRFDocBuilder.AUDELIMITER).concat(strperson_collection);
        //}

        Contributors editors = new Contributors(Keys.EDITORS,getContributors(strperson_monograph,Keys.EDITORS));
        if(editors != null)
        {
          ht.put(Keys.EDITORS, editors);
        }
        // if there was no PERSON_ANALYTIC, if there is an AUTHOR_AFFILIATION,
        // then it is the PERSON_MONOGRAPH affiliation since PERSON_MONOGRAPH is not empty and PERSON_ANALYTIC was empty
        if(keyAffiliation == null)
        {
          keyAffiliation = Keys.EDITOR_AFFS;
          contributors = editors;
        }
      }

      // AFS/EFS
      String straffl = createColumnValueField("AUTHOR_AFFILIATION").getValue();
      if(straffl != null)
      {
        String straddr = createColumnValueField("AUTHOR_AFFILIATION_ADDRESS").getValue();
        if(straddr != null)
        {
          straffl = straffl.concat(", ").concat(straddr);
        }
        String strcountry = new CountryDecorator(createColumnValueField("AUTHOR_AFFILIATION_COUNTRY")).getValue();
        if(strcountry != null)
        {
          straffl = straffl.concat(", ").concat(strcountry);
        }
        Affiliation affil = new Affiliation(keyAffiliation, straffl);
        ht.put(keyAffiliation, new Affiliations(keyAffiliation, affil));
        if(contributors != null)
        {
          contributors.setFirstAffiliation(affil);
        }
      }

      addDocumentValue(Keys.VOLUME, createColumnValueField("VOLUME_ID"), new Volume(StringUtil.EMPTY_STRING,perl));
      addDocumentValue(Keys.ISSUE, createColumnValueField("ISSUE_ID"), new Issue(StringUtil.EMPTY_STRING,perl));
      addDocumentValue(Keys.ISBN, createColumnValueField("ISBN"), new ISBN(StringUtil.EMPTY_STRING));
      addDocumentValue(Keys.ISSN, new IssnDecorator(createColumnValueField("ISSN")), new ISSN(StringUtil.EMPTY_STRING));
      addDocumentValue(Keys.E_ISSN, new IssnDecorator(createColumnValueField("EISSN")), new ISSN(StringUtil.EMPTY_STRING));

      addDocumentValue(Keys.PAGE_RANGE, new SimpleValueField(getPages()), new PageRange(StringUtil.EMPTY_STRING, perl));
      addDocumentValue(Keys.PUBLICATION_YEAR, new SimpleValueField(getYear()), new Year(StringUtil.EMPTY_STRING, perl));

      // INDEX_TERMS (CVS)
      if(isIncluded(Keys.INDEX_TERM))
      {
        String stridxtrms = createColumnValueField("INDEX_TERMS").getValue();
        if(stridxtrms != null)
        {
          String[] idxterms = stridxtrms.split(GRFDocBuilder.AUDELIMITER);
          for(int i = 0; i < idxterms.length; i++)
          {
            idxterms[i] = idxterms[i].replaceAll("[A-Z]*" + GRFDocBuilder.IDDELIMITER,"");
          }
          ht.put(Keys.INDEX_TERM, new XMLMultiWrapper(Keys.INDEX_TERM, idxterms));
        }
      }

      // UNCONTROLLED_TERMS (FLS)
      if(isIncluded(Keys.UNCONTROLLED_TERMS))
      {
        String stridxtrms = createColumnValueField("UNCONTROLLED_TERMS").getValue();
        if(stridxtrms != null)
        {
          ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, stridxtrms.split(GRFDocBuilder.AUDELIMITER)));
        }
      }
      // SPONSOR
      if(isIncluded(Keys.RSRCH_SPONSOR))
      {
        String strsponsor = createColumnValueField("CORPORATE_BODY_ANALYTIC").getValue();
        if(strsponsor == null)
        {
          strsponsor = createColumnValueField("CORPORATE_BODY_MONOGRAPH").getValue();
        }
        if(strsponsor != null)
        {
          addDocumentValue(Keys.RSRCH_SPONSOR, new PublisherDecorator(new SimpleValueField(strsponsor)));
        }
      }

      // COORDINATES
      if(isIncluded(GRFDocBuilder.COORDINATES))
      {
// Latitude. Latitude indicates the number of degrees north or south of the equator (0 degrees). Latitude is indicated by a character string of seven characters: a letter N or S to indicate direction (i.e., north or south of the equator) and a six-digit number indicating the number of degrees, minutes, and seconds. Latitude coordinates are also cascaded to the full degree (i.e., first two digits): N451430 is North, 45 degrees, 14 minutes, 30 seconds; it is also cascaded to N45.
// Longitude. Longitude indicates the number of degrees east or west of the prime meridian (0 degrees). Longitude is indicated by a character string of eight characters: a letter E or W to indicate direction (i.e., east or west of the prime meridian) and a seven-digit number indicating the number of degrees, minutes, and seconds. Longitude coordinates are also cascaded to the full degree (i.e., first three digits): W1211752 is West, 121 degrees, 17 minutes, 52 seconds; it is also cascaded to W121
// Coordinates are assigned as follows: Starting from the lower right-hand corner, a latitude is assigned, followed by the latitude of the upper right-hand corner (counterclockwise), the longitude of that point, and finally the longitude of the upper left-hand corner.
// i.e. N174800N180000W0661000W0664000
// S230000S100000E1530000E1430000

        String strcoordinates = createColumnValueField("COORDINATES").getValue();
        if(strcoordinates != null)
        {
          ht.put(GRFDocBuilder.COORDINATES, new RectangleCoordinates(strcoordinates.split(GRFDocBuilder.AUDELIMITER)));
        }
      }
/*      if(isIncluded(GRFDocBuilder.MERIDIAN))
      {
        MeridianData md = new MeridianData();
        String strfeatures = createColumnValueField("LAND").getValue();
        if(strfeatures != null)
        {
          md.setFeatures(strfeatures.split("\\|"),"Land");
        }
        strfeatures = createColumnValueField("WATER").getValue();
        if(strfeatures != null)
        {
          md.setFeatures(strfeatures.split("\\|"),"Water");
        }
        ht.put(GRFDocBuilder.MERIDIAN, md);
      }
*/

      if(isIncluded(Keys.AVAILABILITY))
      {
        String stravail = createColumnValueField("AVAILABILITY").getValue();
        if(stravail != null)
        {
          ht.put(Keys.AVAILABILITY, new XMLMultiWrapper(Keys.AVAILABILITY, stravail.split(GRFDocBuilder.AUDELIMITER)));
        }
      }

      addDocumentValue(Keys.CONF_DATE, new ConferenceDateDecorator(createColumnValueField("DATE_OF_MEETING")));
      addDocumentValue(Keys.CONFERENCE_NAME, createColumnValueField("NAME_OF_MEETING"));
      addDocumentValue(Keys.CODEN, createColumnValueField("CODEN"));
      addDocumentValue(Keys.SOURCE, createColumnValueField("TITLE_OF_SERIAL"));
      addDocumentValue(Keys.COPYRIGHT, createColumnValueField("COPYRIGHT"));
      addDocumentValue(Keys.ACCESSION_NUMBER, createColumnValueField("ID_NUMBER"));
      addDocumentValue(Keys.NUMBER_OF_REFERENCES, createColumnValueField("NUMBER_OF_REFERENCES"));
      addDocumentValue(Keys.REPORT_NUMBER, createColumnValueField("REPORT_NUMBER"));
      addDocumentValue(Keys.DOI, createColumnValueField("DOI"));
      addDocumentValue(Keys.EDITION, createColumnValueField("EDITION"));

      addDocumentValue(GRFDocBuilder.SOURCE_MEDIUM, createColumnValueField("MEDIUM_OF_SOURCE"));
      addDocumentValue(GRFDocBuilder.SOURCE_NOTE, createColumnValueField("SOURCE_NOTE"));
      addDocumentValue(GRFDocBuilder.SUMMARY_ONLY_NOTE, createColumnValueField("SUMMARY_ONLY_NOTE"));

      addDocumentValue(GRFDocBuilder.UNIVERSITY, createColumnValueField("UNIVERSITY"));
      addDocumentValue(GRFDocBuilder.DEGREE_TYPE, createColumnValueField("TYPE_OF_DEGREE"));
      addDocumentValue(GRFDocBuilder.RESEARCH_PROGRAM, createColumnValueField("RESEARCH_PROGRAM"));
      addDocumentValue(GRFDocBuilder.ILLUSTRATION, createColumnValueField("ILLUSTRATION"));
      addDocumentValue(GRFDocBuilder.ANNOTATION, createColumnValueField("ANNOTATION"));
      addDocumentValue(GRFDocBuilder.MAP_SCALE, createColumnValueField("MAP_SCALE"));
      addDocumentValue(GRFDocBuilder.MAP_TYPE, createColumnValueField("MAP_TYPE"));
      addDocumentValue(GRFDocBuilder.HOLDING_LIBRARY, createColumnValueField("HOLDING_LIBRARY"));
      addDocumentValue(GRFDocBuilder.TARGET_AUDIENCE, createColumnValueField("TARGET_AUDIENCE"));

      addDocumentValue(GRFDocBuilder.GRF_URLS, new URLDecorator(createColumnValueField("URL")));
      addDocumentValue(Keys.COLLECTION_TITLE, new TitleDecorator(createColumnValueField("TITLE_OF_COLLECTION")));
      addDocumentValue(Keys.PUBLISHER, new PublisherDecorator(new SimpleValueField(getPublisher())));
      addDocumentValue(Keys.COUNTRY_OF_PUB, new CountryDecorator(createColumnValueField("COUNTRY_OF_PUBLICATION")));
      addDocumentValue(GRFDocBuilder.AFFILIATION_OTHER, new OtherAffiliationDecorator(createColumnValueField("AFFILIATION_SECONDARY")));
      addDocumentValue(Keys.DOC_TYPE, new DocumentTypeDecorator(createColumnValueField("DOCUMENT_TYPE")));
      addDocumentValue(Keys.LANGUAGE, new NonEnglishLanguageDecorator(new LanguageDecorator(createColumnValueField("LANGUAGE_TEXT"))));
      addDocumentValue(Keys.ABSTRACT_TYPE, new BibliographicLevelDecorator(createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE")));
      addDocumentValue(GRFDocBuilder.CATEGORY, new CategoryDecorator(createColumnValueField("CATEGORY_CODE")));

      EIDoc eiDoc = new EIDoc(did, ht, getFormat());
      eiDoc.exportLabels(exportLabels());
      eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
      eiDoc.setOutputKeys(getKeys());

      return eiDoc;
    }

    public String toString() { return getFormat() + "\n == \n" + getQuery(); }

    public String getQuery()
    {
      return "SELECT " + StringUtil.join(getFields(),",") + " FROM GEOREF_MASTER WHERE M_ID IN ";
    }
    public boolean isIncluded(Key key)
    {
      return (Arrays.asList(getKeys()).contains(key));
    }

    /*
     * "Factory" prevents having to pass around ResultSet when creating objects
     */
    public DocumentField createColumnValueField(String columnname)
    {
      ResultsSetField rsfield = new ColumnValueField(columnname);
      rsfield.setResultSet(rset);

      return rsfield;
    }

    /*
     * addDocumentValue wrappers. These check to see if a field is included
     * before calling getValue()
     */
    public void addDocumentValue(Key key, String strfieldvalue)
        throws Exception
    {
      addDocumentValue(key, new SimpleValueField(strfieldvalue));
    }

    public void addDocumentValue(Key key, DocumentField field)
    {
      addDocumentValue(key, field, new XMLWrapper(key,StringUtil.EMPTY_STRING));
    }

    public void addDocumentValue(Key key, DocumentField field, ElementData data)
    {
      if(isIncluded(key))
      {
        String fieldvalue = field.getValue();
        if(fieldvalue != null)
        {
          //System.out.println("Setting " + key.getKey() + " value =" + fieldvalue);
          data.setElementData(new String[]{fieldvalue});
          putElementData(key, data);
        }
      }
    }

    private void putElementData(Key key, ElementData data)
    {
      if(data != null)
      {
        ht.put(key, data);
      }
    }


    /* =========================================================================
     * Virtual Fields that require conditions for determining their values
     * =========================================================================
     */
    private List getContributors(String strAuthors, Key key)
    {

      List list = new ArrayList();
      try
      {
        if(strAuthors != null)
        {
          DataCleaner dataCleaner = new DataCleaner();
          strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
          String[] aus = strAuthors.split(GRFDocBuilder.AUDELIMITER);
          for(int auindex = 0; auindex < aus.length; auindex++)
          {
            list.add(new Contributor(key, aus[auindex]));
          }
        }
      }
      catch (Exception e)
      {
        System.out.println("getContributors() Exception: " + e.getMessage());
      }
      finally
      {
      }
      return list;
    }

    public String getTitle()
    {
      DocumentField afield = new TitleDecorator(createColumnValueField("TITLE_OF_ANALYTIC"));
      String strvalue = afield.getValue();
      if(strvalue == null)
      {
        afield = new TitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
        strvalue = afield.getValue();
      }
      return strvalue;
    }

    public String getTranslatedTitle()
    {
      DocumentField afield = new TranslatedTitleDecorator(createColumnValueField("TITLE_OF_ANALYTIC"));
      String strvalue = afield.getValue();
      if(strvalue == null)
      {
        afield = new TranslatedTitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
        strvalue = afield.getValue();
      }
      if(strvalue != null)
      {
        if(strvalue.equals(getTitle()))
        {
          strvalue = null;
        }
      }
      return strvalue;
    }

    public String getMonographTitle()
    {
      DocumentField afield = new TitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
      String strvalue = afield.getValue();
      if(strvalue != null)
      {
        if(strvalue.equals(getTitle()))
        {
          strvalue = null;
        }
      }
      return strvalue;
    }

    public String getPublisher()
    {
      String strvalue = createColumnValueField("PUBLISHER").getValue();
      String strvalueaddr = createColumnValueField("PUBLISHER_ADDRESS").getValue();

      if((strvalue != null) && (strvalueaddr != null))
      {
        strvalue = strvalue.concat(", ").concat(strvalueaddr);
      }
      return strvalue;
    }

    private String getVolIssue()
    {
      String strvalue = null;
      if(isIncluded(Keys.VOLISSUE))
      {
        List lstvalues = new ArrayList();
        strvalue = createColumnValueField("VOLUME_ID").getValue();
        if(strvalue != null)
        {
          lstvalues.add(strvalue);
          strvalue = null;
        }
        strvalue = createColumnValueField("ISSUE_ID").getValue();
        if(strvalue != null)
        {
          lstvalues.add(strvalue);
        }
        if(!lstvalues.isEmpty())
        {
          strvalue = StringUtil.join(lstvalues,", ");
        }
      }
      return strvalue;
    }

    private String getAbstract()
    {
      String strvalue = null;
      if(isIncluded(Keys.ABSTRACT))
      {
        try
        {
          Clob clob = rset.getClob("ABSTRACT");
          if(clob != null)
          {
            strvalue = StringUtil.getStringFromClob(clob);
          }
        }
        catch(SQLException sqle)
        {
          sqle.printStackTrace();
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      return (strvalue == null || strvalue.length() < GRFDocBuilder.MINIMUM_ABSTRACT_LENGTH) ? null : strvalue;
    }

    public String getPages()
    {
      String strvalue = null;
      DocumentField pagesAnalytic = createColumnValueField("COLLATION_ANALYTIC");
      strvalue = pagesAnalytic.getValue();
      if(strvalue == null)
      {
        DocumentField pagesMono = createColumnValueField("COLLATION_MONOGRAPH");
        DocumentField titleMono = createColumnValueField("TITLE_OF_MONOGRAPH");
        // if we are using the monograph title
        if((titleMono.getValue() != null) && (pagesMono.getValue() != null))
        {
          strvalue = pagesMono.getValue();
        }
        // settle on the collation field for the collection if it exists
        if(strvalue == null)
        {
          DocumentField pagesColl = createColumnValueField("COLLATION_COLLECTION");
          if(pagesColl.getValue() != null)
          {
            strvalue = pagesMono.getValue();
          }
        }
      }
      return strvalue;
    }

    public String getYear()
    {
      String strvalue = null;
      DocumentField yearPub = createColumnValueField("DATE_OF_PUBLICATION");
      String strdate = yearPub.getValue();
      if(strdate == null)
      {
        DocumentField yearMeeting = createColumnValueField("DATE_OF_MEETING");
        // if there is no pubdate, check if there is a conference date
        strdate = yearMeeting.getValue();
      }
      if(strdate != null)
      {
        Matcher yearmatch = Pattern.compile("^(\\d{4})").matcher(strdate);
        if(yearmatch.find())
        {
          strvalue = yearmatch.group(1);
        }
      }
      return strvalue;
    }

    /* =========================================================================
     * Nested Inner classes (@see http://java.sun.com/docs/books/tutorial/java/javaOO/nested.html)
     * These classes are public and are accessible through an instance of
     * the DocumentView class.
     * i.e.
     *          DocumentView runtimeDocview = new CitationView();
     *          DocumentView.FieldDecorator cd = runtimeDocview.new CountryDecorator(country);
     *
     * =========================================================================
     */


    /* =========================================================================
     * DocumentField
     * =========================================================================
     */
    public abstract class DocumentField
    {
      public abstract String getValue();
    }

    /* =========================================================================
     * This is a simple field for wrapping strings so getValue() can be caled
     * on them in the addDocument() methods
     * =========================================================================
     */
    public class SimpleValueField extends DocumentField
    {
      private String value = null;
      public  SimpleValueField(String strvalue)
      {
        this.value = strvalue;
      }
      public String getValue() { return this.value; }
    }

    /* =========================================================================
     * This is for wrapping strings ResultSet fields
     * =========================================================================
     */
    public abstract class ResultsSetField extends DocumentField
    {
      protected  ResultSet rs = null;
      public void setResultSet(ResultSet rs)
      {
        this.rs = rs;
      }

      public String getValue()
      {
        String strvalue = null;
        try
        {
          if(rs != null)
          {
            strvalue = rs.getString(getColumn());
          }
          else
          {
            System.out.println("Skipping column  " + getColumn() + " RS is null");
          }
        }
        catch(SQLException sqle)
        {
          //sqle.printStackTrace();
          System.out.println("getValue() error.  Possible missing or misspelled column name " + getColumn() + ".");
        }
        return strvalue;
      }
      public abstract String getColumn();
    }

    /* =========================================================================
     * This is a simple field for wrapping values which are store as strings
     * in a result set - this is mainly for a delayed load so we can check
     * whether or not they are part of the document before calling getValue()
     * =========================================================================
     */
    public class ColumnValueField extends ResultsSetField
    {
      private String m_column = null;

      public ColumnValueField(String column)
      {
        this.m_column = column;
      }
      public String getColumn() { return this.m_column; }
    }

    /* =========================================================================
     * Field Decorators
     * ========================================================================= */
    public abstract class FieldDecorator extends DocumentField
    {
      protected Map tokenize(String field)
      {
        Map multivalues = new HashMap();
        if(field != null)
        {
          String[] strpairs = field.split(GRFDocBuilder.AUDELIMITER);
          for(int i = 0; i < strpairs.length; i++)
          {
            String[] strnamevalue = strpairs[i].split(GRFDocBuilder.IDDELIMITER);
            if(strnamevalue.length == 2)
            {
              multivalues.put(strnamevalue[0],strnamevalue[1]);
            }
          }
        }
        return multivalues;
      }
    }

    public abstract class ReplaceDelimiterDecorator extends FieldDecorator
    {
      private DocumentField field;
      public ReplaceDelimiterDecorator(DocumentField field)
      {
        this.field = field;
      }
      public String getValue()
      {
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          strvalue = strvalue.replaceAll(GRFDocBuilder.AUDELIMITER,getSeparator());
        }
        return strvalue;
      }
      public abstract String getSeparator();
    }

    public class PublisherDecorator extends ReplaceDelimiterDecorator
    {
      public PublisherDecorator(DocumentField field)
      {
        super(field);
      }
      public String getSeparator() { return ";"; }
    }

    public class OtherAffiliationDecorator extends ReplaceDelimiterDecorator
    {
      public OtherAffiliationDecorator(DocumentField field)
      {
        super(field);
      }
      public String getSeparator() { return ";"; }
    }
    public static Pattern patternYYYYMMDD = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");

    public class ConferenceDateDecorator extends FieldDecorator
    {
      private DocumentField field;
      public ConferenceDateDecorator(DocumentField field)
      {
        this.field = field;
      }
      public String getValue()
      {
        String strdate = null;
        String strconfdate = field.getValue();
        if(strconfdate != null)
        {
          Matcher yearmatch = DocumentView.patternYYYYMMDD.matcher(strconfdate);
          if(yearmatch.find())
          {
            strdate = yearmatch.group(2) + "/" + yearmatch.group(3) + "/" + yearmatch.group(1);
          }
        }
        return strdate;
      }
    }

    public class IssnDecorator extends FieldDecorator
    {
      protected  DocumentField field;
      public IssnDecorator(DocumentField field)
      {
        this.field = field;
      }
      public String getValue()
      {
        String strtitle = null;
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          String[] strvalues = strvalue.split(GRFDocBuilder.IDDELIMITER);
          if(strvalues != null && strvalues.length > 1)
          {
            strtitle = strvalues[0];
          }
          else
          {
            strtitle = strvalue;
          }
        }
        return strtitle;
      }
    }

    // O Original title, i.e. the title, if any, as given on the document entered in the original language and alphabet
    // M Original title in original language and alphabet, but modified or enriched in content as part of the cataloguing process
    // L Original title transliterated or transcribed as part of the cataloging process. Used only for Cyrillic alphabet in GeoRef.
    // T Original title translated (with or without modification of content) as part of the cataloging process

    public class TitleDecorator extends FieldDecorator
    {
      protected  DocumentField field;
      public TitleDecorator(DocumentField field)
      {
        this.field = field;
      }
      public TitleDecorator(String stringvalue)
      {
        this(new SimpleValueField(stringvalue));
      }

      public String getValue()
      {
        String strtitle = null;
        String strvalue = field.getValue();
        Map mapvalues = tokenize(strvalue);
        if(mapvalues.containsKey("T"))
        {
          strtitle = (String) mapvalues.get("T");
        }
        else if((strtitle == null) && mapvalues.containsKey("O"))
        {
          strtitle = (String) mapvalues.get("O");
        }
        else if((strtitle == null) && mapvalues.containsKey("M"))
        {
          strtitle = (String) mapvalues.get("M");
        }
        else if((strtitle == null) && mapvalues.containsKey("L"))
        {
          strtitle = (String) mapvalues.get("L");
        }
        return strtitle;
      }
    }

    public class TranslatedTitleDecorator extends FieldDecorator
    {
      protected  DocumentField field;
      public TranslatedTitleDecorator(DocumentField field)
      {
        this.field = field;
      }
      public TranslatedTitleDecorator(String stringvalue)
      {
        this(new SimpleValueField(stringvalue));
      }
      public String getValue()
      {
        String strtitle = null;
        String strvalue = field.getValue();
        Map mapvalues = tokenize(strvalue);
        if(mapvalues.containsKey("O"))
        {
          strtitle = (String) mapvalues.get("O");
        }
        return strtitle;
      }
    }

    /*
    * Translation Decorators
    */

    /*
    * This base decorator will take a code decorate it by translating it into a readable string.
    * The code is a String key which is looked up in a Map
    */
    public abstract class LookupValueDecorator extends FieldDecorator
    {
      protected GRFDataDictionary dataDictionary = new GRFDataDictionary();

      protected  DocumentField field;
      public LookupValueDecorator(DocumentField field)
      {
        this.field = field;
      }
      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          String strtranslated = dataDictionary.translateValue(strvalue,getLookupTable());
          if(strtranslated != null)
          {
            decoratedvalue = strtranslated;
          }
        }
        return decoratedvalue;
      }
      public abstract Map getLookupTable();
    }

    public class LanguageDecorator extends LookupValueDecorator
    {
      public LanguageDecorator(DocumentField field)
      {
        super(field);
      }
      public Map getLookupTable()
      {
        return dataDictionary.getLanguages();
      }
    }

    public class NonEnglishLanguageDecorator extends LanguageDecorator
    {
      public NonEnglishLanguageDecorator(DocumentField field)
      {
        super(field);
      }
      public String getValue()
      {
        String strvalue = field.getValue();
        if((strvalue != null) && !strvalue.equalsIgnoreCase("English") && !strvalue.equalsIgnoreCase("EL"))
        {
          strvalue = super.getValue();
        }
        else
        {
          strvalue = null;
        }
        return strvalue;
      }
    }

    public class CountryDecorator extends LookupValueDecorator
    {
      public CountryDecorator(DocumentField field)
      {
        super(field);
      }
      public CountryDecorator(String stringvalue)
      {
        super(new SimpleValueField(stringvalue));
      }
      public Map getLookupTable()
      {
        return dataDictionary.getCountries();
      }
    }

    public class BibliographicLevelDecorator extends LookupValueDecorator
    {
      public BibliographicLevelDecorator(DocumentField field)
      {
        super(field);
      }
      public Map getLookupTable()
      {
        return dataDictionary.getBibliographiccodes();
      }
    }

    /*
    * This decorator extends the LookupValueDecorator by splitting the code into multiple values, based on a split expression
    * and then conctenating each decorated value into a single string, using a concatenationstring to join the decorated values
    */
    public abstract class MultiValueLookupValueDecorator extends LookupValueDecorator
    {
      public MultiValueLookupValueDecorator(DocumentField field)
      {
        super(field);
      }
      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();

        if(strvalue != null)
        {
          String[] codes = new String[]{strvalue};
          if(getSplitPattern().matcher(strvalue).find())
          {
            codes = getSplitPattern().split(strvalue);
          }
          for(int i = 0; i < codes.length; i++)
          {
            String strtranslated = dataDictionary.translateValue(codes[i],getLookupTable());
            if(strtranslated != null)
            {
              decoratedvalue = (decoratedvalue == null) ? strtranslated : decoratedvalue.concat(getConcatenationString()).concat(strtranslated);
            }
          }
        }
        return decoratedvalue;
      }
      public abstract Pattern getSplitPattern();
      public abstract String getConcatenationString();
    }

    public class CategoryDecorator extends MultiValueLookupValueDecorator
    {
      public CategoryDecorator(DocumentField field)
      {
        super(field);
      }
      public Pattern getSplitPattern() { return Pattern.compile(GRFDocBuilder.AUDELIMITER); }
      public String getConcatenationString() { return "; "; }
      public Map getLookupTable()
      {
        return dataDictionary.getCategories();
      }
    }

    public class DocumentTypeDecorator extends MultiValueLookupValueDecorator
    {
      public DocumentTypeDecorator(DocumentField field)
      {
        super(field);
      }
      // empty pattern will split string into single characters
      public Pattern getSplitPattern() { return Pattern.compile(StringUtil.EMPTY_STRING); }
      public String getConcatenationString() { return ", "; }
      public Map getLookupTable()
      {
        return dataDictionary.getDocumenttypes();
      }
    }

    public class URLDecorator extends MultiValueLookupValueDecorator
    {
      public URLDecorator(DocumentField field)
      {
        super(field);
      }

      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();

        if(strvalue != null)
        {
          String[] urls = new String[]{strvalue};
          if(getSplitPattern().matcher(strvalue).find())
          {
            urls = getSplitPattern().split(strvalue);
          }
          for(int i = 0; i < urls.length; i++)
          {
            String[] urlstrings = urls[i].split(",");
            String strtranslated = dataDictionary.translateValue(urlstrings[0],getLookupTable());
            if(strtranslated != null)
            {
              strtranslated = strtranslated.concat(" [").concat(urlstrings[1]).concat("] ");
              decoratedvalue = (decoratedvalue == null) ? strtranslated : decoratedvalue.concat(getConcatenationString()).concat(strtranslated);
            }
          }
        }
        return decoratedvalue;
      }

      public Pattern getSplitPattern() { return Pattern.compile(GRFDocBuilder.AUDELIMITER); }
      public String getConcatenationString() { return ", "; }
      public Map getLookupTable()
      {
        Map urlmap = new HashMap();

        urlmap.put("F","Full-text");
        urlmap.put("A","Availability");
        urlmap.put("P","Publisher");
        urlmap.put("S","Series");

        return urlmap;
      }
    }


} //  End of DocumentView class


