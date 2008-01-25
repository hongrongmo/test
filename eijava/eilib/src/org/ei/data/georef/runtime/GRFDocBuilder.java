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


public class GRFDocBuilder
    implements DocumentBuilder
{
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {29});
    public static final String GROUPDELIMITER = new String(new char[] {02});
    public static String GRF_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String GRF_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";
    private static final Key E_ISSN = new Key(Keys.E_ISSN, "Electronic ISSN");
    private static final Key GRF_CLASS_CODES = new Key(Keys.CLASS_CODES, "Classification codes");
    private static final Key GRF_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Index terms");

    private static final Key ILLUSTRATION = new Key("ILLUS", "Illustrations");


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
                String strtitle = rset.getString("TITLE_OF_ANALYTIC");
                if(strtitle == null)
                {
                  strtitle = rset.getString("TITLE_OF_MONOGRAPH");
                }
                else
                {
                    String strmonotitle = rset.getString("TITLE_OF_MONOGRAPH");
                    if(strmonotitle != null)
                    {
                      ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE,strmonotitle));
                    }
                }
                if(strtitle != null)
                {
                  ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE,strtitle));
                }

                // AU-AUS
                Contributors authors = null;
                String strauthors = rset.getString("PERSON_ANALYTIC");
                if(strauthors != null && strauthors.equalsIgnoreCase("Anonymous"))
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


                // SN
                if(rset.getString("ISSN") != null)
                {
                  ht.put(Keys.ISSN, new ISSN(rset.getString("ISSN")));
                }

                // ST
                if(rset.getString("TITLE_OF_SERIAL") != null)
                {
                  ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE,rset.getString("TITLE_OF_SERIAL")));
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

                // LANGUAGE - LA
                if((rset.getString("LANGUAGE_TEXT") != null) && (!rset.getString("LANGUAGE_TEXT").equalsIgnoreCase("EL")))
                {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE , rset.getString("LANGUAGE_TEXT")));
                }
                // PAGES - PP
                if(rset.getString("COLLATION_ANALYTIC") != null)
                {
                  ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("COLLATION_ANALYTIC"), perl));
                }

                // PUBLISHER - PN
                if(rset.getString("PUBLISHER") != null )
                {
                  ht.put(Keys.PUBLISHER,new XMLWrapper(Keys.PUBLISHER,rset.getString("PUBLISHER")));
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

                // CODEN - CN
                if(viewformat.isIncluded(Keys.CODEN))
                {
                  if(rset.getString("CODEN") != null)
                  {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN,rset.getString("CODEN")));
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
                if(viewformat.isIncluded(Keys.CONFERENCE_NAME))
                {
                  String strconf= rset.getString("NAME_OF_MEETING");
                  if(strconf != null)
                  {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, strconf));
                  }
                }


                // COPYRIGHT
                if(viewformat.isIncluded(Keys.COPYRIGHT))
                {
                  if(rset.getString("COPYRIGHT") != null)
                  {
                    ht.put(Keys.COPYRIGHT,new XMLWrapper(Keys.COPYRIGHT, rset.getString("COPYRIGHT")));
                  }
                }

                // ACCESSION NUMBER
                if(viewformat.isIncluded(Keys.ACCESSION_NUMBER))
                {
                  if(rset.getString("ID_NUMBER") != null)
                  {
                    ht.put(Keys.ACCESSION_NUMBER,new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ID_NUMBER")));
                  }
                }

                // AB
                if(viewformat.isIncluded(Keys.ABSTRACT))
                {
                  String abstr = hasAbstract(rset);
                  if(abstr != null)
                  {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abstr));
                  }
                }

                //DO
                if(viewformat.isIncluded(Keys.DOI))
                {
                  if(rset.getString("DOI") != null)
                  {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI ,rset.getString("DOI")));
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
                                                        "DATE_OF_PUBLICATION",
                                                        "DATE_OF_MEETING",
                                                        "LANGUAGE_TEXT",
                                                        "PUBLISHER",
                                                        "LOAD_NUMBER",
                                                        "COPYRIGHT",
                                                        "COLLATION_ANALYTIC"});
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
                                                        "CODEN",
                                                        "INDEX_TERMS",
                                                        "NAME_OF_MEETING",
                                                        "ISBN"
                                                        });
        private Key[] keys = new Key[]{Keys.ABSTRACT,
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
                                                        "ILLUSTRATION",
                                                        "ID_NUMBER"});

        private Key[] keys = new Key[]{Keys.ABBRV_SERIAL_TITLE,
                                        Keys.ABSTRACT_TYPE,
                                        Keys.ACCESSION_NUMBER,
                                        Keys.ARTICLE_NUMBER,
                                        Keys.CLASS_CODES,
                                        Keys.CONF_CODE,
                                        Keys.CONTROLLED_TERMS,
                                        Keys.DOC_TYPE,
                                        Keys.DOI,
                                        Keys.GLOBAL_TAGS,
                                        Keys.ISSUE,
                                        GRFDocBuilder.ILLUSTRATION,
                                        Keys.MEETING_LOCATION,
                                        Keys.NUMBER_OF_REFERENCES,
                                        Keys.PAGE_COUNT,
                                        Keys.PRIVATE_TAGS,
                                        Keys.REGION_CONTROLLED_TERMS,
                                        Keys.SERIAL_TITLE,
                                        Keys.SOURCE_COUNTRY,
                                        Keys.TITLE_TRANSLATION,
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
                                                        "AUTHOR_AFFILIATION_CITY",
                                                        "AUTHOR_AFFILIATION_STATE",
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
                                                        "E_ISSN",
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
}