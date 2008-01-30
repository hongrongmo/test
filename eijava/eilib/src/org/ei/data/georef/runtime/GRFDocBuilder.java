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

public class GRFDocBuilder implements DocumentBuilder
{
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {31});
    public static final String GROUPDELIMITER = new String(new char[] {02});
    public static String GRF_TEXT_COPYRIGHT = "GeoRef, Copyright 2008, American Geological Institute.";
    public static String GRF_HTML_COPYRIGHT = "GeoRef, Copyright &copy; 2008, American Geological Institute.";
    public static String PROVIDER_TEXT = "Ei";
    private static final Key GRF_CLASS_CODES = new Key(Keys.CLASS_CODES, "Classification codes");
    private static final Key GRF_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Index terms");

    private static final Key ILLUSTRATION = new Key("ILLUS", "Illustrations");
    private static final Key ANNOTATION = new Key("ANT", "Annotations");
    private static final Key MAP_SCALE = new Key("MPS", "Map scale");
    private static final Key MAP_TYPE = new Key("MPT", "Map type");
    private static final Key AFFILIATION_OTHER = new Key("OAF", "Other affiliation");
    private static final Key CATEGORY = new Key("CAT", "Category");


    private Database database;

    public DocumentBuilder newInstance(Database database)
    {
        return new GRFDocBuilder(database);
    }

    public GRFDocBuilder()
    {
    }

    public GRFDocBuilder(Database database)
    {
        this.database = database;
    }


    /** This method takes a list of DocID objects and dataFormat
    *  and returns a List of EIDoc Objects based on a particular
    *  dataformat
    *  @ param listOfDocIDs
    *  @ param dataFormat
    *  @ return List --list of EIDoc's
    *  @ exception DocumentBuilderException
    */
    public List buildPage(List listOfDocIDs, String dataFormat) throws DocumentBuilderException
    {
        List l = null;
        try
        {
          if(dataFormat != null)
          {
            DocumentView format = null;
            if(dataFormat.equalsIgnoreCase(Citation.CITATION_FORMAT))
            {
              format = new CitationView();
            }
            else if(dataFormat.equalsIgnoreCase(Abstract.ABSTRACT_FORMAT))
            {
              format = new AbstractView();
            }
            else if(dataFormat.equalsIgnoreCase(FullDoc.FULLDOC_FORMAT))
            {
              format = new DetailedView();
            }
            else if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT))
            {
              format = new RISView();
            }
            else if(dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT))
            {
              format = new XMLCitationView();
            }
            if(format != null)
            {
              l = loadDocument(listOfDocIDs, format);
            }
          }
        }
        catch(Exception e)
        {
            throw new DocumentBuilderException(e);
        }

        return l;
    }

    /**

        */
    private List loadDocument(List listOfDocIDs, DocumentView viewformat)
          throws Exception
    {
        Map oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List list=new ArrayList();
        List emailList = null;
        int count=0;
        Connection con=null;
        Statement stmt=null;
        ResultSet rset=null;
        ConnectionBroker broker=null;
        String INString=buildINString(listOfDocIDs);

        try
        {
            broker=ConnectionBroker.getInstance();
            con=broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset=stmt.executeQuery(viewformat.getQuery()+INString);

            while(rset.next())
            {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER,new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));
                ht.put(Keys.COPYRIGHT,new XMLWrapper(Keys.COPYRIGHT, GRF_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT,new XMLWrapper(Keys.COPYRIGHT_TEXT, GRF_TEXT_COPYRIGHT));

                // TI
                ht = viewformat.includeField(Keys.TITLE, ht, new Title(rset));
                ht = viewformat.includeField(Keys.TITLE_TRANSLATION, ht, new TranslatedTitle(rset));

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

                // AFF
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
                if(viewformat.isIncluded(Keys.E_ISSN))
                {
                  // E_ISSN
                  String streissns = rset.getString("EISSN");
                  if(streissns != null)
                  {
                    //ht.put(Keys.E_ISSN, new XMLMultiWrapper(Keys.E_ISSN, strissns.split(GRFDocBuilder.AUDELIMITER)));
                  }
                }

                // VOISS
                // VO
                String strvoliss = rset.getString("VOLUME_ID");
                if(strvoliss != null)
                {
                  ht.put(Keys.VOLUME, new Volume(strvoliss,perl));
                }
                // IS
                if(rset.getString("ISSUE_ID") != null)
                {
                  ht.put(Keys.ISSUE, new Issue(rset.getString("ISSUE_ID"),perl));
                  if(strvoliss != null)
                  {
                    strvoliss = strvoliss.concat(", ").concat(rset.getString("ISSUE_ID"));
                  }
                  else
                  {
                    strvoliss = rset.getString("ISSUE_ID");
                  }
                }
                if(strvoliss != null)
                {
                  ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE ,strvoliss));
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

                // ISBN - BN
                if(viewformat.isIncluded(Keys.ISBN))
                {
                  if(rset.getString("ISBN") != null)
                  {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("ISBN")));
                  }
                }

                // CONFERENCE DATE
                if(viewformat.isIncluded(Keys.CONF_DATE))
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
                if(viewformat.isIncluded(Keys.INDEX_TERM))
                {
                  String stridxtrms = rset.getString("INDEX_TERMS");
                  if(stridxtrms != null)
                  {
                    stridxtrms = stridxtrms.replaceAll(GRFDocBuilder.IDDELIMITER,":");
                    ht.put(Keys.INDEX_TERM, new XMLMultiWrapper(Keys.INDEX_TERM, stridxtrms.split(GRFDocBuilder.AUDELIMITER)));
                  }
                }

                ht = viewformat.includeField(Keys.CONFERENCE_NAME, "NAME_OF_MEETING", ht, rset);
                ht = viewformat.includeField(Keys.COUNTRY_OF_PUB, "COUNTRY_OF_PUBLICATION", ht, rset);
                ht = viewformat.includeField(Keys.CODEN, "CODEN", ht, rset);
                ht = viewformat.includeField(Keys.PUBLISHER, "PUBLISHER", ht, rset);
                ht = viewformat.includeField(Keys.SOURCE, "TITLE_OF_SERIAL", ht, rset);
                ht = viewformat.includeField(Keys.COPYRIGHT, "COPYRIGHT", ht, rset);
                ht = viewformat.includeField(Keys.ACCESSION_NUMBER, "ID_NUMBER", ht, rset);
                ht = viewformat.includeField(Keys.NUMBER_OF_REFERENCES, "NUMBER_OF_REFERENCES", ht, rset);
                ht = viewformat.includeField(Keys.DOC_URL, "URL", ht, rset);
                ht = viewformat.includeField(Keys.REPORT_NUMBER, "REPORT_NUMBER", ht, rset);
                ht = viewformat.includeField(Keys.DOI, "DOI", ht, rset);

                ht = viewformat.includeField(GRFDocBuilder.ILLUSTRATION, "ILLUSTRATION", ht, rset);
                ht = viewformat.includeField(GRFDocBuilder.ANNOTATION, "ANNOTATION", ht, rset);
                ht = viewformat.includeField(GRFDocBuilder.MAP_SCALE, "MAP_SCALE", ht, rset);
                ht = viewformat.includeField(GRFDocBuilder.MAP_TYPE, "MAP_TYPE", ht, rset);

                ht = viewformat.includeField(GRFDocBuilder.AFFILIATION_OTHER, ht, new OtherAffiliation(rset));

                ht = viewformat.includeField(Keys.DOC_TYPE, ht, new DocumenttypeDecorator(new DocumentType(rset)));
                ht = viewformat.includeField(GRFDocBuilder.CATEGORY, ht, new CategoryDecorator(new Category(rset)));
                ht = viewformat.includeField(Keys.ABSTRACT_TYPE, ht, new BibliographicLevelDecorator(new BibliographicLevel(rset)));
                ht = viewformat.includeField(Keys.LANGUAGE, ht, new LanguageDecorator(new Language(rset)));

                // ABSTRACT - AB
                if(viewformat.isIncluded(Keys.ABSTRACT))
                {
                  String abstr = hasAbstract(rset);
                  if(abstr != null)
                  {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abstr));
                  }
                }

                EIDoc eiDoc = new EIDoc(did, ht, viewformat.getFormat());
                eiDoc.exportLabels(true);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(viewformat.getKeys());
                list.add(eiDoc);
                count++;
            }

        }
        catch(Exception e)
        {
          e.printStackTrace();
          System.out.println("Exception e " + e.getMessage());
        }
        finally
        {
            if(rset != null)
            {
                try
                {
                    rset.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception sqle)
                {
                    sqle.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                }
                catch(Exception cpe)
                {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

   /* This method builds the IN String
    * from list of docId objects.
    * The select query will get the result set in a reverse way
    * So in order to get in correct order we are doing a reverse
    * example of return String--(23,22,1,12...so on);
    * @param listOfDocIDs
    * @return String
    */
    private String buildINString(List listOfDocIDs)
    {
        StringBuffer sQuery = new StringBuffer("(");
        Collections.reverse(listOfDocIDs);
        Iterator itrdocids = listOfDocIDs.iterator();
        while(itrdocids.hasNext())
        {
            DocID doc = (DocID) itrdocids.next();
            sQuery.append("'").append(doc.getDocID()).append("'");
            if(itrdocids.hasNext())
            {
                sQuery.append(",");
            }
        }
        sQuery.append(")");
        return sQuery.toString();
    }


    private String hasAbstract(ResultSet rs) throws Exception
    {
        String abs = null;
        Clob clob = rs.getClob("ABSTRACT");
        if(clob != null)
        {
          abs = StringUtil.getStringFromClob(clob);
        }

        if(abs == null || abs.length() < 100)
        {
          return null;
        }
        else
        {
          return abs;
        }
    }

    public List getContributors(String strAuthors, Key key)
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
        System.out.println("E " + e.getMessage());
      }
      finally
      {
      }
      return list;
    }

    private Map getDocIDTable(List listOfDocIDs)
    {
        Map h = new Hashtable();

        for(int i=0; i<listOfDocIDs.size(); ++i)
        {
            DocID d = (DocID)listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }


        return h;
    }

/* ========================================================================= */

    private abstract class DocumentView {

        public abstract String getFormat();
        public abstract Key[] getKeys();
        public abstract List getFields();

        public String getQuery()
        {
          return "SELECT " + StringUtil.join(getFields(),",") + " FROM GEOREF_MASTER WHERE M_ID IN ";
        }
        public boolean isIncluded(Key key)
        {
          return (Arrays.asList(getKeys()).contains(key));
        }

        public ElementDataMap includeField(Key key, String strfieldname, ElementDataMap ht, ResultSet rs)
            throws Exception
        {
          String strvalue =  null;
          if(isIncluded(key))
          {
            strvalue = rs.getString(strfieldname);
            if(strvalue != null)
            {
              ht.put(key, new XMLWrapper(key, strvalue));
            }
          }
          return ht;
        }

        public ElementDataMap includeField(Key key, ElementDataMap ht, DocumentField field)
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
          return ht;
        }

        public String toString() { return getFormat() + "\n == \n" + getQuery(); }
    }

    private class CitationView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"M_ID",
                                                        "ISSN",
                                                        "TITLE_OF_SERIAL",
                                                        "VOLUME_ID",
                                                        "ISSUE_ID",
                                                        "TITLE_OF_ANALYTIC",
                                                        "TITLE_OF_MONOGRAPH",
                                                        "PERSON_ANALYTIC",
                                                        "PERSON_MONOGRAPH",
                                                        "AUTHOR_AFFILIATION",
                                                        "AUTHOR_AFFILIATION_ADDRESS",
                                                        "AUTHOR_AFFILIATION_COUNTRY",
                                                        "DATE_OF_PUBLICATION",
                                                        "DATE_OF_MEETING",
                                                        "LANGUAGE_TEXT",
                                                        "PUBLISHER",
                                                        "LOAD_NUMBER",
                                                        "COPYRIGHT",
                                                        "COLLATION_ANALYTIC",
                                                        "COLLATION_MONOGRAPH"});
        private Key[] keys = new Key[]{Keys.AUTHORS,
                                        Keys.AUTHOR_AFFS,
                                        Keys.COPYRIGHT,
                                        Keys.COPYRIGHT_TEXT,
                                        Keys.DOCID,
                                        Keys.EDITORS,
                                        Keys.ISSN,
                                        Keys.ISSUE_DATE,
                                        Keys.LANGUAGE ,
                                        Keys.MONOGRAPH_TITLE,
                                        Keys.NO_SO,
                                        Keys.PAGE_RANGE,
                                        Keys.PUBLICATION_DATE,
                                        Keys.PUBLICATION_YEAR,
                                        Keys.PUBLISHER,
                                        Keys.SOURCE,
                                        Keys.TITLE,
                                        Keys.VOLISSUE};

        public Key[] getKeys() {
          List abstractkeys = new ArrayList();
          abstractkeys.addAll(Arrays.asList(keys));
          return (Key[]) abstractkeys.toArray(new Key[]{});
        }

        public List getFields()
        {
          List abstractfields = new ArrayList();
          abstractfields.addAll(fields);
          return abstractfields;
        }

        public String getFormat() { return Citation.CITATION_FORMAT; }
    }

    private class AbstractView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"ABSTRACT",
                                                        "BIBLIOGRAPHIC_LEVEL_CODE",
                                                        "CODEN",
                                                        "EISSN",
                                                        "INDEX_TERMS",
                                                        "NAME_OF_MEETING",
                                                        "ISBN"});

        private Key[] keys = new Key[]{Keys.ABSTRACT,
                                        Keys.ABSTRACT_TYPE,
                                        Keys.CLASS_CODES,
                                        Keys.CODEN,
                                        Keys.CONFERENCE_NAME,
                                        Keys.CONF_DATE,
                                        Keys.EDITOR_AFFS,
                                        Keys.E_ISSN,
                                        Keys.INDEX_TERM,
                                        Keys.ISBN,
                                        Keys.I_PUBLISHER,
                                        Keys.MAIN_HEADING,
                                        Keys.PROVIDER,
                                        Keys.SPONSOR};

        public Key[] getKeys() {
          List abstractkeys = new ArrayList();
          abstractkeys.addAll(Arrays.asList((new CitationView()).getKeys()));
          abstractkeys.addAll(Arrays.asList(keys));
          return (Key[]) abstractkeys.toArray(new Key[]{});
        }

        public List getFields()
        {
          List abstractfields = new ArrayList();
          abstractfields.addAll((new CitationView()).getFields());
          abstractfields.addAll(fields);
          return abstractfields;
        }

        public String getFormat() { return Abstract.ABSTRACT_FORMAT; }
    }
    private class DetailedView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"DOI",
                                                        "URL",
                                                        "ILLUSTRATION",
                                                        "ID_NUMBER",
                                                        "MAP_TYPE",
                                                        "MAP_SCALE",
                                                        "ANNOTATION",
                                                        "CATEGORY_CODE",
                                                        "DOCUMENT_TYPE",
                                                        "UNIVERSITY",
                                                        "TYPE_OF_DEGREE",
                                                        "AFFILIATION_SECONDARY",
                                                        "COUNTRY_OF_PUBLICATION",
                                                        "REPORT_NUMBER",
                                                        "NUMBER_OF_REFERENCES"});

        private Key[] keys = new Key[]{Keys.ABBRV_SERIAL_TITLE,
                                        Keys.ACCESSION_NUMBER,
                                        GRFDocBuilder.AFFILIATION_OTHER,
                                        GRFDocBuilder.ANNOTATION,
                                        Keys.ARTICLE_NUMBER,
                                        Keys.CONF_CODE,
                                        Keys.COUNTRY_OF_PUB,
                                        Keys.DOC_TYPE,
                                        Keys.DOI,
                                        Keys.GLOBAL_TAGS,
                                        Keys.ISSUE,
                                        GRFDocBuilder.ILLUSTRATION,
                                        GRFDocBuilder.MAP_SCALE,
                                        GRFDocBuilder.MAP_TYPE,
                                        GRFDocBuilder.CATEGORY,
                                        Keys.MEETING_LOCATION,
                                        Keys.NUMBER_OF_REFERENCES,
                                        Keys.PAGE_COUNT,
                                        Keys.PRIVATE_TAGS,
                                        Keys.REGION_CONTROLLED_TERMS,
                                        Keys.REPORT_NUMBER,
                                        Keys.SERIAL_TITLE,
                                        Keys.SOURCE_COUNTRY,
                                        Keys.TITLE_TRANSLATION,
                                        Keys.DOC_URL,
                                        Keys.TREATMENTS,
                                        Keys.VOLUME};

        public String getFormat() { return FullDoc.FULLDOC_FORMAT; }
        // Detail.FULLDOC_FORMAT

        public Key[] getKeys() {
          List abstractkeys = new ArrayList();
          abstractkeys.addAll(Arrays.asList((new AbstractView()).getKeys()));
          abstractkeys.addAll(Arrays.asList(keys));
          abstractkeys.remove(Keys.VOLISSUE);
          return (Key[]) abstractkeys.toArray(new Key[]{});
        }

        public List getFields()
        {
          List abstractfields = new ArrayList();
          abstractfields.addAll((new AbstractView()).getFields());
          abstractfields.addAll(fields);
          return abstractfields;
        }

    }

    private class RISView extends CitationView {

        private Key[] keys = new Key[]{Keys.RIS_TY,
                                        Keys.RIS_LA,
                                        Keys.RIS_N1,
                                        Keys.RIS_TI,
                                        Keys.RIS_T1,
                                        Keys.RIS_BT,
                                        Keys.RIS_JO,
                                        Keys.RIS_T3,
                                        Keys.RIS_AUS,
                                        Keys.RIS_AD,
                                        Keys.RIS_EDS,
                                        Keys.RIS_VL,
                                        Keys.RIS_IS,
                                        Keys.RIS_PY,
                                        Keys.RIS_AN,
                                        Keys.RIS_SP,
                                        Keys.RIS_EP,
                                        Keys.RIS_SN,
                                        Keys.RIS_S1,
                                        Keys.RIS_MD,
                                        Keys.RIS_CY,
                                        Keys.RIS_PB,
                                        Keys.RIS_N2,
                                        Keys.RIS_KW,
                                        Keys.RIS_CVS,
                                        Keys.RIS_FLS,
                                        Keys.RIS_DO,
                                        Keys.BIB_TY };

        public String getFormat() { return RIS.RIS_FORMAT; }
    }

    private class XMLCitationView extends CitationView {

        private List fields = Arrays.asList(new String[]{"COPYRIGHT",
                                                        "COPYRIGHT_TYPE",
                                                        "M_ID",
                                                        "SOURCE_TYPE",
                                                        "CITATION_TITLE",
                                                        "TRANSLATED_TITLE",
                                                        "AUTHORS",
                                                        "AUTHOR2",
                                                        "AUTHOR_AFFILIATION",
                                                        "AUTHOR_ADDRESS_PART",
                                                        "AUTHOR_AFFILIATION_ADDRESS",
                                                        "AUTHOR_AFFILIATION_COUNTRY",
                                                        "SOURCE_EDITOR",
                                                        "SOURCE_TITLE",
                                                        "ABBR_SOURCETITLE",
                                                        "SOURCE_VOLUME",
                                                        "SOURCE_ISSUE",
                                                        "SOURCE_PUBLICATIONDATE",
                                                        "ISSUE_TITLE",
                                                        "VOLUME_TITLE",
                                                        "PUBLISHER_NAME",
                                                        "SOURCE_PUBLICATIONYEAR",
                                                        "SOURCE_PUBLICATIONDATE",
                                                        "CREATED_DATE",
                                                        "SORT_DATE",
                                                        "REPORT_NUMBER",
                                                        "PAGES",
                                                        "PAGECOUNT",
                                                        "SOURCE_PAGERANGE",
                                                        "ARTICLE_NUMBER",
                                                        "CITATION_LANGUAGE",
                                                        "ABSTRACT_LANGUAGE",
                                                        "TITLETEXT_LANGUAGE",
                                                        "ISSN",
                                                        "EISSN",
                                                        "DOI",
                                                        "ISBN",
                                                        "LOAD_NUMBER",
                                                        "ACCESSION_NUMBER",
                                                        "DESCRIPTOR_MAINTERM_GDE",
                                                        "DESCRIPTOR_MAINTERM_RGI",
                                                        "DESCRIPTOR_MAINTERM_SPC",
                                                        "DESCRIPTOR_MAINTERM_SPC2",
                                                        "CORRESPONDENCE_PERSON",
                                                        "CORRESPONDENCE_AFFILIATION"});
        private Key[] keys = new Key[]{Keys.ISSN,
                                        Keys.E_ISSN,
                                        Keys.MAIN_HEADING,
                                        Keys.NO_SO,
                                        Keys.MONOGRAPH_TITLE,
                                        Keys.PUBLICATION_YEAR,
                                        Keys.VOLUME_TITLE,
                                        Keys.CONTROLLED_TERM,
                                        Keys.ISBN,
                                        Keys.AUTHORS,
                                        Keys.DOCID,
                                        Keys.SOURCE,
                                        Keys.NUMVOL,
                                        Keys.EDITOR_AFFS,
                                        Keys.EDITORS,
                                        Keys.PUBLISHER,
                                        Keys.VOLUME,
                                        Keys.AUTHOR_AFFS,
                                        Keys.PROVIDER,
                                        Keys.ISSUE_DATE,
                                        Keys.COPYRIGHT_TEXT,
                                        Keys.DOI,
                                        Keys.PAGE_COUNT,
                                        Keys.ARTICLE_NUMBER,
                                        Keys.PUBLICATION_DATE,
                                        Keys.TITLE,
                                        Keys.LANGUAGE,
                                        Keys.PAGE_RANGE,
                                        Keys.PAPER_NUMBER,
                                        Keys.COPYRIGHT,
                                        Keys.ISSUE,
                                        Keys.ACCESSION_NUMBER,
                                        Keys.CONTROLLED_TERMS };

        public String getFormat() { return RIS.RIS_FORMAT; }
    }

    /* ========================================================================= */
    /* Fields
    /* ========================================================================= */
    public abstract class DocumentField
    {
      public Map tokenize(String field)
      {
        Map multivalues = new HashMap();
        if(field != null)
        {
          String[] strvaluepairs = field.split(GRFDocBuilder.AUDELIMITER);
          for(int i = 0; i < strvaluepairs.length; i++)
          {
            String[] strkeyvalue = strvaluepairs[i].split(GRFDocBuilder.IDDELIMITER);
            if(strkeyvalue.length == 2)
            {
              multivalues.put(strkeyvalue[0],strkeyvalue[1]);
            }
          }
        }
        return multivalues;
      }
      public abstract String getValue();
    }

    public abstract class ResultsSetField extends DocumentField
    {
      protected ResultSet rs;
      public ResultsSetField(ResultSet rs)
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
        }
        catch(SQLException sqle)
        {
          sqle.printStackTrace();
          System.out.println("getColumn " + getColumn());
          strvalue  = null;
        }
        return strvalue;
      }
      public abstract String getColumn();
    }

    public class OtherAffiliation extends ResultsSetField
    {
      public OtherAffiliation(ResultSet rs)
      {
        super(rs);
      }
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
      public Language(ResultSet rs)
      {
        super(rs);
      }
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
      public DocumentType(ResultSet rs)
      {
        super(rs);
      }
      public String getColumn() { return "DOCUMENT_TYPE"; }
    }
    public class Category extends ResultsSetField
    {
      public Category(ResultSet rs)
      {
        super(rs);
      }
      public String getColumn() { return "CATEGORY_CODE"; }
    }
    public class BibliographicLevel extends ResultsSetField
    {
      public BibliographicLevel(ResultSet rs)
      {
        super(rs);
      }
      public String getColumn() { return "BIBLIOGRAPHIC_LEVEL_CODE"; }
    }

    public abstract class MultiKeyValueField extends ResultsSetField
    {
      public MultiKeyValueField(ResultSet rs)
      {
        super(rs);
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

      public abstract String getValue();
      public abstract String getColumn();
    }

    public class Title extends MultiKeyValueField
    {
      private MultiKeyValueField alternate = null;

      public Title(ResultSet rs)
      {
        super(rs);
      }

      public String getValue()
      {
        this.alternate = new MonographTitle(rs);
        String strvalue = null;
        Map mapvalues = getValueMap();
        if(mapvalues.isEmpty() && (alternate !=null))
        {
          mapvalues = alternate.getValueMap();
        }
        if(mapvalues.containsKey(getKeyCode()))
        {
          strvalue = (String) mapvalues.get(getKeyCode());
        }
        else
        {
          strvalue = (String) mapvalues.get("T");
        }
        //System.out.println(getColumn()+"/"+getKeyCode() + " == " + strvalue);
        return strvalue;
      }

      // O Original title, i.e. the title, if any, as given on the document entered in the original language and alphabet
      // M Original title in original language and alphabet, but modified or enriched in content as part of the cataloguing process
      // L Original title transliterated or transcribed as part of the cataloging process. Used only for Cyrillic alphabet in GeoRef.
      // T Original title translated (with or without modification of content) as part of the cataloging process

      public String getColumn() { return "TITLE_OF_ANALYTIC"; }
      public String getKeyCode() { return "O"; }
    }

    public class TranslatedTitle extends Title
    {
      public TranslatedTitle(ResultSet rs)
      {
        super(rs);
      }

      // T Original title translated (with or without modification of content) as part of the cataloging process
      public String getKeyCode() { return "L"; }
    }

    public class MonographTitle extends Title
    {
      public MonographTitle(ResultSet rs)
      {
        super(rs);
      }
      public String getColumn() { return "TITLE_OF_MONOGRAPH"; }
    }

    /* ========================================================================= */
    /* Field Decorators
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
    /* Code Translators
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