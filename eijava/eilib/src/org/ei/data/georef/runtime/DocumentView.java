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

 /* ========================================================================= */
/*                                                                           */
/* ========================================================================= */
public abstract class DocumentView {

    public abstract String getFormat();
    public abstract Key[] getKeys();
    public abstract List getFields();
    public abstract boolean exportLabels();

    private ResultSet rset = null;
    private ElementDataMap ht = null;
    private Perl5Util perl = new Perl5Util();

    public EIDoc buildDocument(ResultSet rset, DocID did)
              throws Exception
    {
      this.rset = rset;
      this.ht = new ElementDataMap();

      ht.put(Keys.DOCID, did);

      ht.put(Keys.PROVIDER,new XMLWrapper(Keys.PROVIDER, GRFDocBuilder.PROVIDER_TEXT));
      ht.put(Keys.COPYRIGHT,new XMLWrapper(Keys.COPYRIGHT, GRFDocBuilder.GRF_HTML_COPYRIGHT));
      ht.put(Keys.COPYRIGHT_TEXT,new XMLWrapper(Keys.COPYRIGHT_TEXT, GRFDocBuilder.GRF_TEXT_COPYRIGHT));

      // TI
      addDocumentValue(Keys.TITLE, getTitle());
      addDocumentValue(Keys.VOLISSUE, getVolIssue());
      addDocumentValue(Keys.TITLE_TRANSLATION, getTranslatedTitle());
      addDocumentValue(Keys.ABSTRACT, getAbstract());

      // AU-AUS
      Contributors authors = null;
      String strauthors = rset.getString("PERSON_ANALYTIC");
      if(strauthors == null || strauthors.equalsIgnoreCase("Anonymous"))
      {
        strauthors = rset.getString("PERSON_MONOGRAPH");
      }
      if(strauthors != null)
      {
        authors = new Contributors(Keys.AUTHORS,getContributors(strauthors,Keys.AUTHORS));
        if(authors != null)
        {
          ht.put(Keys.AUTHORS, authors);
        }
      }

      // AFF - Uses authors Collection
      String straffl = null;
      straffl = rset.getString("AUTHOR_AFFILIATION");
      if(straffl != null)
      {
        if(rset.getString("AUTHOR_AFFILIATION_ADDRESS") != null)
        {
          straffl = straffl.concat(", ").concat(rset.getString("AUTHOR_AFFILIATION_ADDRESS"));
        }
        if(rset.getString("AUTHOR_AFFILIATION_COUNTRY") != null)
        {
          straffl = straffl.concat(", ").concat(rset.getString("AUTHOR_AFFILIATION_COUNTRY"));
        }
        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, straffl);
        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
        if(authors != null)
        {
          authors.setFirstAffiliation(affil);
        }
      }

      // SN
      if(rset.getString("ISSN") != null)
      {
        ht.put(Keys.ISSN, new ISSN(rset.getString("ISSN")));
      }
      if(isIncluded(Keys.E_ISSN))
      {
        // E_ISSN
        String streissns = rset.getString("EISSN");
        if(streissns != null)
        {
          //ht.put(Keys.E_ISSN, new XMLMultiWrapper(Keys.E_ISSN, strissns.split(GRFDocBuilder.AUDELIMITER)));
        }
      }
      // ISBN - BN
      if(isIncluded(Keys.ISBN))
      {
        if(rset.getString("ISBN") != null)
        {
          ht.put(Keys.ISBN, new ISBN(rset.getString("ISBN")));
        }
      }

      // PAGES - PP
      String strpages = rset.getString("COLLATION_ANALYTIC");
      if(strpages == null)
      {
        // if we are using the monograph title
        if((rset.getString("TITLE_OF_MONOGRAPH") != null) && rset.getString("COLLATION_MONOGRAPH") != null)
        {
          strpages = rset.getString("COLLATION_MONOGRAPH");
        }
      }
      if(strpages != null)
      {
        ht.put(Keys.PAGE_RANGE, new PageRange(strpages, perl));
      }

      // YR
      String strdate = rset.getString("DATE_OF_PUBLICATION");
      if(strdate == null)
      {
        // if there is no pubdate, check if there is a conference date
        strdate = rset.getString("DATE_OF_MEETING");
      }
      if(strdate != null)
      {
        Matcher yearmatch = Pattern.compile("^(\\d{4})").matcher(strdate);
        if(yearmatch.find())
        {
          ht.put(Keys.PUBLICATION_YEAR, new Year(yearmatch.group(1), perl));
        }
      }

      // CONFERENCE DATE
      if(isIncluded(Keys.CONF_DATE))
      {
        String strconfdate = rset.getString("DATE_OF_MEETING");
        if(strconfdate != null)
        {
          Matcher yearmatch = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$").matcher(strconfdate);
          if(yearmatch.find())
          {
            String strmmddyyy = yearmatch.group(2) + "/" + yearmatch.group(3) + "/" + yearmatch.group(1);
            ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, strmmddyyy));
          }
        }
      }

      // INDEX_TERMS
      if(isIncluded(Keys.INDEX_TERM))
      {
        String stridxtrms = rset.getString("INDEX_TERMS");
        if(stridxtrms != null)
        {
          stridxtrms = stridxtrms.replaceAll(GRFDocBuilder.IDDELIMITER,":");
          ht.put(Keys.INDEX_TERM, new XMLMultiWrapper(Keys.INDEX_TERM, stridxtrms.split(GRFDocBuilder.AUDELIMITER)));
        }
      }

      addDocumentValue(Keys.VOLUME, createColumnValueField("VOLUME_ID"));
      addDocumentValue(Keys.ISSUE, createColumnValueField("ISSUE_ID"));

      addDocumentValue(Keys.CONFERENCE_NAME, createColumnValueField("NAME_OF_MEETING"));
      addDocumentValue(Keys.COUNTRY_OF_PUB, createColumnValueField("COUNTRY_OF_PUBLICATION"));
      addDocumentValue(Keys.CODEN, createColumnValueField("CODEN"));
      addDocumentValue(Keys.PUBLISHER, createColumnValueField("PUBLISHER"));
      addDocumentValue(Keys.SOURCE, createColumnValueField("TITLE_OF_SERIAL"));
      addDocumentValue(Keys.COPYRIGHT, createColumnValueField("COPYRIGHT"));
      addDocumentValue(Keys.ACCESSION_NUMBER, createColumnValueField("ID_NUMBER"));
      addDocumentValue(Keys.NUMBER_OF_REFERENCES, createColumnValueField("NUMBER_OF_REFERENCES"));
      addDocumentValue(Keys.DOC_URL, createColumnValueField("URL"));
      addDocumentValue(Keys.REPORT_NUMBER, createColumnValueField("REPORT_NUMBER"));
      addDocumentValue(Keys.DOI, createColumnValueField("DOI"));

      addDocumentValue(GRFDocBuilder.ILLUSTRATION, createColumnValueField("ILLUSTRATION"));
      addDocumentValue(GRFDocBuilder.ANNOTATION, createColumnValueField("ANNOTATION"));
      addDocumentValue(GRFDocBuilder.MAP_SCALE, createColumnValueField("MAP_SCALE"));
      addDocumentValue(GRFDocBuilder.MAP_TYPE, createColumnValueField("MAP_TYPE"));
      addDocumentValue(GRFDocBuilder.HOLDING_LIBRARY, createColumnValueField("HOLDING_LIBRARY"));
      addDocumentValue(GRFDocBuilder.TARGET_AUDIENCE, createColumnValueField("TARGET_AUDIENCE"));


      addDocumentValue(GRFDocBuilder.AFFILIATION_OTHER, new OtherAffiliationDecorator(createColumnValueField("AFFILIATION_SECONDARY")));
      addDocumentValue(Keys.DOC_TYPE, new DocumentTypeDecorator(createColumnValueField("DOCUMENT_TYPE")));
      addDocumentValue(Keys.LANGUAGE, new LanguageDecorator(createColumnValueField("LANGUAGE_TEXT")));
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

    private ResultsSetField createColumnValueField(String columnname)
    {
      ResultsSetField rsfield = new ColumnValueField(columnname);
      rsfield.setResultSet(rset);

      return rsfield;
    }

    public void addDocumentValue(Key key, String strfieldvalue)
        throws Exception
    {
      if(isIncluded(key))
      {
        putXMLWrappedValue(key, strfieldvalue);
      }
    }

    public void addDocumentValue(Key key, DocumentField field)
        throws Exception
    {
      if(isIncluded(key))
      {
        putXMLWrappedValue(key, field.getValue());
      }
    }

    private void putXMLWrappedValue(Key key, String strvalue)
    {
      if(strvalue != null)
      {
        ht.put(key, new XMLWrapper(key, strvalue));
      }
    }

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

    private String getTranslatedTitle()
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

    private String getTitle()
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

    private String getVolIssue()
    {
      String strvalue = null;
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

    /* ========================================================================= */
    /* Inner classes                                                             */
    /* ========================================================================= */


    /* ========================================================================= */
    /* Fields                                                                    */
    /* ========================================================================= */
    public abstract class DocumentField
    {
      public abstract String getValue();
    }

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
          sqle.printStackTrace();
        }
        return strvalue;
      }
      public abstract String getColumn();
    }

    public class ColumnValueField extends ResultsSetField
    {
      String m_column = null;

      public ColumnValueField(String column)
      {
        m_column = column;
      }
      public String getColumn() { return m_column; }
    }

    /* ========================================================================= */
    /* Field Decorators                                                          */
    /* ========================================================================= */
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

    public class OtherAffiliationDecorator extends FieldDecorator
    {
      protected  DocumentField field;
      public OtherAffiliationDecorator(DocumentField field)
      {
        this.field = field;
      }
      public String getValue()
      {
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          strvalue = strvalue.replaceAll(GRFDocBuilder.AUDELIMITER,";");
        }
        return strvalue;
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
    public abstract class TranslationDecorator extends FieldDecorator
    {
      protected GRFDataDictionary dataDictionary = new GRFDataDictionary();

      protected  DocumentField field;
      public TranslationDecorator(DocumentField field)
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

    public class LanguageDecorator extends TranslationDecorator
    {
      public LanguageDecorator(DocumentField field)
      {
        super(field);
      }
      public String getValue()
      {
        String strvalue = field.getValue();
        if((strvalue != null) && !strvalue.equalsIgnoreCase("EL"))
        {
          strvalue = super.getValue();
        }
        else
        {
          strvalue = null;
        }
        return strvalue;
      }
      public Map getLookupTable()
      {
        return dataDictionary.getLanguages();
      }
    }

    public class BibliographicLevelDecorator extends TranslationDecorator
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
    * This decorator extends the TranslationDecorator by splitting the code into multiple values, based on a split expression
    * and then conctenating each decorated value into a single string, using a concatenationstring to join the decorated values
    */
    public abstract class MultiValueTranslationDecorator extends TranslationDecorator
    {
      public MultiValueTranslationDecorator(DocumentField field)
      {
        super(field);
      }
      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          String[] codes = strvalue.split(getSplitExpression());
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
      public abstract String getSplitExpression();
      public abstract String getConcatenationString();
    }

    public class CategoryDecorator extends MultiValueTranslationDecorator
    {
      public CategoryDecorator(DocumentField field)
      {
        super(field);
      }
      public String getSplitExpression() { return GRFDocBuilder.AUDELIMITER; }
      public String getConcatenationString() { return "; "; }
      public Map getLookupTable()
      {
        return dataDictionary.getCategories();
      }
    }

    public class DocumentTypeDecorator extends MultiValueTranslationDecorator
    {
      public DocumentTypeDecorator(DocumentField field)
      {
        super(field);
      }
      public String getSplitExpression() { return "\\w"; }
      public String getConcatenationString() { return ", "; }
      public Map getLookupTable()
      {
        return dataDictionary.getDocumenttypes();
      }
    }
}