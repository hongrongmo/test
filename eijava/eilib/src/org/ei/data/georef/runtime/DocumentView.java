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
      includeField(Keys.TITLE, new Title());
      includeField(Keys.TITLE_TRANSLATION, new TranslatedTitle());
      includeField(Keys.VOLUME, new Volume());
      includeField(Keys.ISSUE, new Issue());
      includeField(Keys.VOLISSUE, new VolIssue());


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

      includeField(Keys.CONFERENCE_NAME, "NAME_OF_MEETING");
      includeField(Keys.COUNTRY_OF_PUB, "COUNTRY_OF_PUBLICATION");
      includeField(Keys.CODEN, "CODEN");
      includeField(Keys.PUBLISHER, "PUBLISHER");
      includeField(Keys.SOURCE, "TITLE_OF_SERIAL");
      includeField(Keys.COPYRIGHT, "COPYRIGHT");
      includeField(Keys.ACCESSION_NUMBER, "ID_NUMBER");
      includeField(Keys.NUMBER_OF_REFERENCES, "NUMBER_OF_REFERENCES");
      includeField(Keys.DOC_URL, "URL");
      includeField(Keys.REPORT_NUMBER, "REPORT_NUMBER");
      includeField(Keys.DOI, "DOI");

      includeField(GRFDocBuilder.ILLUSTRATION, "ILLUSTRATION");
      includeField(GRFDocBuilder.ANNOTATION, "ANNOTATION");
      includeField(GRFDocBuilder.MAP_SCALE, "MAP_SCALE");
      includeField(GRFDocBuilder.MAP_TYPE, "MAP_TYPE");

      includeField(GRFDocBuilder.AFFILIATION_OTHER, new OtherAffiliation());
      includeField(Keys.ABSTRACT, new Abstract());

      includeField(Keys.DOC_TYPE, new DocumenttypeDecorator(new DocumentType()));
      includeField(Keys.ABSTRACT_TYPE, new BibliographicLevelDecorator(new BibliographicLevel()));
      includeField(Keys.LANGUAGE, new LanguageDecorator(new Language()));
      includeField(GRFDocBuilder.CATEGORY, new CategoryDecorator(new Category()));

      EIDoc eiDoc = new EIDoc(did, ht, getFormat());
      eiDoc.exportLabels(exportLabels());
      eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
      eiDoc.setOutputKeys(getKeys());

      return eiDoc;
    }

    public String getQuery()
    {
      return "SELECT " + StringUtil.join(getFields(),",") + " FROM GEOREF_MASTER WHERE M_ID IN ";
    }
    public boolean isIncluded(Key key)
    {
      return (Arrays.asList(getKeys()).contains(key));
    }

    public void includeField(Key key, String strfieldname)
        throws Exception
    {
      if(isIncluded(key))
      {
        String strvalue = this.rset.getString(strfieldname);
        if(strvalue != null)
        {
          ht.put(key, new XMLWrapper(key, strvalue));
        }
      }
    }

    public void includeField(Key key, DocumentField field)
        throws Exception
    {
      if(isIncluded(key))
      {
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          ht.put(key, new XMLWrapper(key, strvalue));
        }
      }
    }

    public void includeField(Key key, ResultsSetField field)
        throws Exception
    {
      if(isIncluded(key))
      {
        String strvalue = field.setResultSet(rset).getValue();
        if(strvalue != null)
        {
          ht.put(key, new XMLWrapper(key, strvalue));
        }
      }
    }

    public String toString() { return getFormat() + "\n == \n" + getQuery(); }

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

    /* ========================================================================= */
    /* Fields                                                                    */
    /* ========================================================================= */
    public abstract class DocumentField
    {
      public abstract String getValue();
    }

    public abstract class ConcatendatedDocumentFields extends DocumentField
    {
      public String getValue()
      {
        String strreturnvalue = null;

        List values = new ArrayList();
        Iterator itr = getFields().iterator();
        while(itr.hasNext())
        {
          DocumentField field = (DocumentField) itr.next();
          String strvalue = field.getValue();
          if(strvalue != null)
          {
            values.add(field.getValue());
          }
        }
        if(!values.isEmpty())
        {
          strreturnvalue = StringUtil.join(values,getConcatenationString());
        }
        return strreturnvalue;
      }
      public String getConcatenationString()
      {
        return ", ";
      }
      public abstract List getFields();
    }

    public class VolIssue extends ConcatendatedDocumentFields
    {
      public List getFields()
      {
        return Arrays.asList(new DocumentField[]{new Volume(), new Issue()});
      }
    }

    public abstract class ResultsSetField extends DocumentField
    {
      protected  ResultSet rs = null;
      public ResultsSetField setResultSet(ResultSet rs)
      {
        this.rs = rs;
        return this;
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
        }
        catch(SQLException sqle)
        {
          sqle.printStackTrace();
        }
        return strvalue;
      }
      public abstract String getColumn();
    }

    public class Abstract extends ResultsSetField
    {
      public String getValue()
      {
        String strvalue = null;
        try
        {
          Clob clob = rs.getClob("ABSTRACT");
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
        return (strvalue == null || strvalue.length() < 100) ? null : strvalue;
      }
      public String getColumn() { return "ABSTRACT"; }
    }

    public class OtherAffiliation extends ResultsSetField
    {
      public String getValue()
      {
        String strvalue = super.getValue();
        if(strvalue != null)
        {
          strvalue = strvalue.replaceAll(GRFDocBuilder.AUDELIMITER,";");
        }
        return strvalue;
      }
      public String getColumn() { return "AFFILIATION_SECONDARY"; }
    }

    public class Language extends ResultsSetField
    {
      public String getValue()
      {
        String strvalue = super.getValue();
        if((strvalue != null) && strvalue.equalsIgnoreCase("EL"))
        {
          strvalue = null;
        }
        return strvalue;
      }
      public String getColumn() { return "LANGUAGE_TEXT"; }
    }

    public class DocumentType extends ResultsSetField
    {
      public String getColumn() { return "DOCUMENT_TYPE"; }
    }
    public class Category extends ResultsSetField
    {
      public String getColumn() { return "CATEGORY_CODE"; }
    }
    public class BibliographicLevel extends ResultsSetField
    {
      public String getColumn() { return "BIBLIOGRAPHIC_LEVEL_CODE"; }
    }
    public class Issue extends ResultsSetField
    {
      public String getColumn() { return "ISSUE_ID"; }
    }
    public class Volume extends ResultsSetField
    {
      public String getColumn() { return "VOLUME_ID"; }
    }


    public abstract class MultiNameValueField extends ResultsSetField
    {
      private Map tokenize(String field)
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

      public Map getValueMap()
      {
        Map mapvalues = new HashMap();
        String strvalue = super.getValue();
        if(strvalue != null)
        {
          mapvalues = tokenize(strvalue);
        }
        return mapvalues;
      }

      public String getValue()
      {
        String strvalue = null;
        Map mapvalues = getValueMap();
        if(mapvalues.containsKey(geNameValueCode()))
        {
          strvalue = (String) mapvalues.get(geNameValueCode());
        }
        return strvalue;
      }

      public abstract String geNameValueCode();
    }

    public class Title extends DocumentField
    {
      public String getValue()
      {
        String strvalue = null;
        if(strvalue == null)
        {
          strvalue = (new OriginalTitle()).getValue();
        }
        if(strvalue == null)
        {
          strvalue = (new TransliteratedTitle()).getValue();
        }
        if(strvalue == null)
        {
          strvalue = (new MonographTitle()).getValue();
        }
        return strvalue;
      }
    }

    // O Original title, i.e. the title, if any, as given on the document entered in the original language and alphabet
    // M Original title in original language and alphabet, but modified or enriched in content as part of the cataloguing process
    // L Original title transliterated or transcribed as part of the cataloging process. Used only for Cyrillic alphabet in GeoRef.
    // T Original title translated (with or without modification of content) as part of the cataloging process

    public class OriginalTitle extends MultiNameValueField
    {
      public String getColumn() { return "TITLE_OF_ANALYTIC"; }
      public String geNameValueCode() { return "O"; }
    }
    public class TransliteratedTitle extends MultiNameValueField
    {
      public String getColumn() { return "TITLE_OF_ANALYTIC"; }
      public String geNameValueCode() { return "T"; }
    }
    public class TranslatedTitle extends MultiNameValueField
    {
      public String getColumn() { return "TITLE_OF_ANALYTIC"; }
      public String geNameValueCode() { return "L"; }
    }
    public class MonographTitle extends MultiNameValueField
    {
      public String getColumn() { return "TITLE_OF_MONOGRAPH"; }
      public String geNameValueCode() { return "O"; }
    }

    /* ========================================================================= */
    /* Field Decorators                                                          */
    /* ========================================================================= */
    public abstract class FieldDecorator extends DocumentField
    {
    }

    /*
    * Translation Decorators
    */
    public abstract class TranslationDecorator extends FieldDecorator
    {
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
          String strtranslated = getTranslator().getValue(strvalue);
          if(strtranslated != null)
          {
            decoratedvalue = strtranslated;
          }
        }
        return decoratedvalue;
      }

      public abstract CodeTranslator getTranslator();
    }

    public class LanguageDecorator extends TranslationDecorator
    {
      public LanguageDecorator(DocumentField field)
      {
        super(field);
      }
      public CodeTranslator getTranslator()
      {
        return new LanguageTranslator();
      }
    }

    public class BibliographicLevelDecorator extends TranslationDecorator
    {
      public BibliographicLevelDecorator(DocumentField field)
      {
        super(field);
      }
      public CodeTranslator getTranslator()
      {
        return new BibliographicLevelTranslator();
      }
    }

    public class CategoryDecorator extends TranslationDecorator
    {
      public CategoryDecorator(DocumentField field)
      {
        super(field);
      }
      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          String[] codes = strvalue.split(GRFDocBuilder.AUDELIMITER);
          for(int i = 0; i < codes.length; i++)
          {
            String strtranslated = getTranslator().getValue(codes[i]);
            if(strtranslated != null)
            {
              decoratedvalue = (decoratedvalue == null) ? strtranslated : decoratedvalue.concat("; ").concat(strtranslated);
            }
          }
        }
        return decoratedvalue;
      }
      public CodeTranslator getTranslator()
      {
        return new CategoryTranslator();
      }
    }

    public class DocumenttypeDecorator extends TranslationDecorator
    {
      public DocumenttypeDecorator(DocumentField field)
      {
        super(field);
      }

      public String getValue()
      {
        String decoratedvalue = null;
        String strvalue = field.getValue();
        if(strvalue != null)
        {
          String[] doctypes = strvalue.split("\\w");
          for(int i = 0; i < doctypes.length; i++)
          {
            String strtranslated = getTranslator().getValue(doctypes[i]);
            if(strtranslated != null)
            {
              decoratedvalue = (decoratedvalue == null) ? strtranslated : decoratedvalue.concat(", ").concat(strtranslated);
            }
          }
        }
        return decoratedvalue;
      }
      public CodeTranslator getTranslator()
      {
        return new DocumenttypeTranslator();
      }
    }

    /* ========================================================================= */
    /* Code Translators                                                          */
    /* ========================================================================= */
    public abstract class CodeTranslator
    {
      public String getValue(String strcode)
      {
        String strtranslated = null;
        if(strcode != null)
        {
          Map transtable = getTranslationTable();
          if(transtable.containsKey(strcode))
          {
            strtranslated = (String) transtable.get(strcode);
          }
        }
        return strtranslated;
      }
      public abstract Map getTranslationTable();
    }

    public class DocumenttypeTranslator extends CodeTranslator
    {
      public Map getTranslationTable()
      {
        return (new GRFDataDictionary()).getDocumenttypes();
      }
    }
    public class CategoryTranslator extends CodeTranslator
    {
      public Map getTranslationTable()
      {
        return (new GRFDataDictionary()).getCategories();
      }
    }
    public class BibliographicLevelTranslator extends CodeTranslator
    {
      public Map getTranslationTable()
      {
        return (new GRFDataDictionary()).getBibliographiccodes();
      }
    }
    public class LanguageTranslator extends CodeTranslator
    {
      public Map getTranslationTable()
      {
        return (new GRFDataDictionary()).getLanguages();
      }
    }
}