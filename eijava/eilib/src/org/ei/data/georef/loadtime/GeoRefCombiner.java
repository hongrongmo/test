package org.ei.data.georef.loadtime;

import java.io.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.*;

import org.ei.data.*;
import org.ei.data.georef.runtime.*;
import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.util.GUID;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class GeoRefCombiner
  extends Combiner
{
  public static final String AUDELIMITER = GRFDocBuilder.AUDELIMITER;
  public static final String IDDELIMITER = GRFDocBuilder.IDDELIMITER;

  Perl5Util perl = new Perl5Util();

  private int exitNumber;

  private static String tablename;

  public static void main(String args[])
                          throws Exception
  {
    String driver = null;
    String url  = null;
    String username = null;
    String password = null;
    url = args[0];
    driver  = args[1];
    username  = args[2];
    password  = args[3];
    int loadNumber = 0;
    int recsPerfile = Integer.parseInt(args[5]);
    int exitAt = Integer.parseInt(args[6]);
    tablename = args[7];

    try {
      loadNumber = Integer.parseInt(args[4]);
    }
    catch(NumberFormatException e) {
      loadNumber = 0;
    }

    Combiner.TABLENAME = tablename;
    Combiner.EXITNUMBER = exitAt;

    CombinedWriter writer = new CombinedXMLWriter(recsPerfile,
                                                  loadNumber,
                                                  "gref");

    GeoRefCombiner c = new GeoRefCombiner(writer);
    if(loadNumber > 200801)
    {
      c.writeCombinedByWeekNumber(url,
                                  driver,
                                  username,
                                  password,
                                  loadNumber);
    }
    // extract the whole thing
    else if(loadNumber == 0)
    {
      for(int yearIndex = 1960; yearIndex <= 2008; yearIndex++)
      {
        c.writeCombinedByYear(url,
                            driver,
                            username,
                            password,
                            yearIndex);
      }
    }
    else
    {
      c.writeCombinedByYear(url,
                            driver,
                            username,
                            password,
                            loadNumber);
    }
  }

  public GeoRefCombiner(CombinedWriter writer)
  {
    super(writer);

    DataValidator d = new DataValidator();
    d.setErrorHandler(new LocalErrorHandler());
    d.setEntityResolver(new LocalEntityResolver());

    ((CombinedXMLWriter) writer).setDataValidator(d);

  }

  public void writeCombinedByWeekHook(Connection con,
                                      int weekNumber)
                                      throws Exception
  {
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      this.writer.begin();
      stmt = con.createStatement();
      String sqlQuery = "select DESCRIPTOR_MAINTERM_SPC2,AUTHOR2,AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, ISBN, CLASSIFICATION, CONFERENCE_CITY, CONFERENCE_CODE,CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI,DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE, load_number, CREATED_DATE, SORT_DATE from " + Combiner.TABLENAME + " where load_number ='" + weekNumber + "' AND load_number != 0 and load_number < 1000000";
      rs = stmt.executeQuery(sqlQuery);
      writeRecs(rs);
      this.writer.end();
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }

      if (stmt != null)
      {
        try
        {
          stmt.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void writeCombinedByYearHook(Connection con,
                                      int year)
                                      throws Exception
  {
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      this.writer.begin();
      stmt = con.createStatement();

      // Here we will use the Z44: UPDATE CODE to get break the data into years
      /* Field Z44 is used to enter the update code. The update code consists of six digits. The first four
      are the update year. The last two are the number of the update within the year. Currently, GeoRef
      is updated twice per month, so there are 24 updates per year and the update codes for 1996 are
      199601, 199602, 199603, etc. through 199624. Prior to 1994, only the year (first four digits) is
      given in this field. */

      String sqlquery = "SELECT * FROM " + Combiner.TABLENAME + " WHERE UPDATE_CODE IS NOT NULL AND SUBSTR(UPDATE_CODE,1,4) =  " + year;
      rs = stmt.executeQuery(sqlquery);

      writeRecs(rs);
      this.writer.end();
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }

      if (stmt != null)
      {
        try
        {
          stmt.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  private void writeRecs(ResultSet rs)
                          throws Exception
  {
    try
    {
      DocumentView runtimeDocview = new CitationView();
      runtimeDocview.setResultSet(rs);

      int i = 1;
      while (rs.next())
      {
        EVCombinedRec rec = new EVCombinedRec();

        if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER)
        {
          break;
        }

        // AUS
        String aString = rs.getString("PERSON_ANALYTIC");
        if(aString != null)
        {
          // DO NOT USE - alternate spellings
          /* String altAuthor = rs.getString("ALTERNATE_AUTHOR");
          if(altAuthor != null)
          {
            aString = aString.concat(AUDELIMITER).concat(altAuthor);
          } */
          rec.put(EVCombinedRec.AUTHOR, aString.split(AUDELIMITER));
        }

        // EDS
        String eString = rs.getString("PERSON_MONOGRAPH");
        if(eString != null)
        {
          String otherEditors = rs.getString("PERSON_COLLECTION");
          if(otherEditors != null)
          {
            eString = eString.concat(AUDELIMITER).concat(otherEditors);
          }
          rec.put(EVCombinedRec.EDITOR, eString.split(AUDELIMITER));
        }

        // AFF
        String affilitation = rs.getString("AUTHOR_AFFILIATION");
        if(affilitation != null)
        {
          List affilations= new ArrayList();
          affilations.add(affilitation);

          // parse out second Affilitation institutions
          if(rs.getString("AFFILIATION_SECONDARY") != null)
          {
            String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
            String[] affilvalues = secondaffiliations.split(AUDELIMITER);
            for(int x = 0 ; x < affilvalues.length; x++)
            {
              String[] values = affilvalues[x].split(IDDELIMITER);
              affilations.add(values[0]);
            }
          }
          if(!affilations.isEmpty())
          {
            rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, (String[]) affilations.toArray(new String[]{}));
          }
        }


        // CO - Author Affiliation Countries and Author Affiliation Location(s)
        // Uses runtime Docview instance to access decorator class
        String country = rs.getString("AUTHOR_AFFILIATION_COUNTRY");
        if(country != null)
        {
          List affcountries = new ArrayList();
          List affilitationlocations = new ArrayList();

          DocumentView.FieldDecorator cd = runtimeDocview.new CountryDecorator(country);
          affcountries.add(cd.getValue());

          // parse out second Affilitation Countries
          if(rs.getString("AFFILIATION_SECONDARY") != null)
          {
            Map countries = GRFDataDictionary.getInstance().getCountries();
            String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
            String[] affilvalues = secondaffiliations.split(AUDELIMITER);
            for(int z = 0 ; z < affilvalues.length; z++)
            {
              String[] values = affilvalues[z].split(IDDELIMITER);
              for(int x = 0; x < values.length; x++)
              {
                if(countries.containsValue(values[x]))
                {
                  affcountries.add(values[x]);
                }
                else if(x != 0)
                {
                  // pick up address info that is not the first entry, which is the Affiliated Institution
                  // or the name of a country
                  affilitationlocations.add(values[x]);
                }
              }
            }
          }
          if(!affcountries.isEmpty())
          {
            rec.putIfNotNull(EVCombinedRec.COUNTRY, (String[]) affcountries.toArray(new String[]{}));
            affilitationlocations.addAll(affcountries);
          }
          if(rs.getString("AUTHOR_AFFILIATION_ADDRESS") != null)
          {
            affilitationlocations.add(rs.getString("AUTHOR_AFFILIATION_ADDRESS"));
          }
          if(!affilitationlocations.isEmpty())
          {
            rec.putIfNotNull(EVCombinedRec.AFFILIATION_LOCATION, (String[]) affilitationlocations.toArray(new String[]{}));
          }

        }

        // LA
        String laStrings = runtimeDocview.new LanguageDecorator(runtimeDocview.createColumnValueField("LANGUAGE_TEXT")).getValue();
        if(laStrings != null)
        {
          rec.putIfNotNull(EVCombinedRec.LANGUAGE, laStrings.split(AUDELIMITER));
        }

        // DT
        String dtStrings = runtimeDocview.new DocumentTypeDecorator(runtimeDocview.createColumnValueField("DOCUMENT_TYPE")).getValue();
        if(dtStrings != null)
        {
          // Get EV system DOC_TYPE codes for indexing and append them to (or use in favor of ?) the GeoRef values
          String mappingcode = runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue().concat(AUDELIMITER).concat(runtimeDocview.createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue());
          if(mappingcode != null)
          {
            // DocumentTypeMappingDecorator takes <DOCTYPE>AUDELIMITER<BIBCODE> String as field argument
            mappingcode = runtimeDocview.new DocumentTypeMappingDecorator(mappingcode).getValue();
            dtStrings = dtStrings.concat(AUDELIMITER).concat(mappingcode);
          }
          rec.putIfNotNull(EVCombinedRec.DOCTYPE, dtStrings.split(AUDELIMITER));
        }


        // AB
        String abString =  StringUtil.getStringFromClob(rs.getClob("ABSTRACT"));
        if (abString != null && abString.length() > 0)
        {
          rec.put(EVCombinedRec.ABSTRACT, abString);
        }

        // SN
        if (rs.getString("ISSN") != null || rs.getString("EISSN") != null)
        {
          String issn = rs.getString("ISSN");
          String e_issn = rs.getString("EISSN");
          StringBuffer issnString = new StringBuffer();
          if(issn != null && issn.length()>0)
          {
            issnString.append(issn);
            if(e_issn != null && e_issn.length()>0)
            {
              issnString.append(AUDELIMITER);
            }
          }
          if(e_issn != null && e_issn.length()>0)
          {
            issnString.append(e_issn);
          }
          rec.put(EVCombinedRec.ISSN, (issnString.toString()).split(AUDELIMITER));
        }
        // Multiple ISBNs exist in GeoRef (ID:2008-005033)
        if(rs.getString("ISBN") != null)
        {
          rec.put(EVCombinedRec.ISBN,(rs.getString("ISBN")).split(AUDELIMITER));
        }
        if(rs.getString("PUBLISHER") != null)
        {
          rec.put(EVCombinedRec.PUBLISHER_NAME,(rs.getString("PUBLISHER")).split(AUDELIMITER));
        }


        // COORDINATES - Extract attached Index Terms
        if(rs.getString("COORDINATES") != null)
        {
          String strcoordinates = rs.getString("COORDINATES");
          String[] termcoordinate = strcoordinates.split(GRFDocBuilder.AUDELIMITER);
          List geoterms = new ArrayList();
          for(int j = 0; j < termcoordinate.length; j++)
          {
            String[] termcoordinates = termcoordinate[j].split(GRFDocBuilder.IDDELIMITER);
            if(termcoordinates.length == 2)
            {
              geoterms.add(termcoordinates[0]);
            }
          }
          if(!geoterms.isEmpty())
          {
            rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, (String[])geoterms.toArray(new String[]{}));
          }
        }

        // INDEX_TERMS (CVS)
        if(rs.getString("INDEX_TERMS") != null)
        {
          String[] idxterms = rs.getString("INDEX_TERMS").split(AUDELIMITER);
          for(int z = 0; z < idxterms.length; z++)
          {
            idxterms[z] = idxterms[z].replaceAll("[A-Z]*" + IDDELIMITER,"");
          }
          rec.putIfNotNull(EVCombinedRec.CONTROLLED_TERMS, idxterms);
        }

        // FLS
        if(rs.getString("UNCONTROLLED_TERMS") != null)
        {
          rec.put(EVCombinedRec.UNCONTROLLED_TERMS,(rs.getString("UNCONTROLLED_TERMS")).split(AUDELIMITER));
        }

        if(rs.getString("AVAILABILITY") != null)
        {
          rec.putIfNotNull(EVCombinedRec.AVAILABILITY, rs.getString("AVAILABILITY").split(AUDELIMITER));
        }

        // Meridian data in Patent Navigators
        /*rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, parseMeridianData(rs.getString("LAND")));
        rec.putIfNotNull(EVCombinedRec.ECLA_CODES, parseMeridianData(rs.getString("WATER")));
        rec.putIfNotNull(EVCombinedRec.USPTOCODE, parseMeridianData(rs.getString("OIL")));
        rec.putIfNotNull(EVCombinedRec.PATENT_KIND, parseMeridianData(rs.getString("CITIES"))); */


        rec.putIfNotNull(EVCombinedRec.PUB_YEAR, runtimeDocview.getYear());
        rec.putIfNotNull(EVCombinedRec.TITLE, runtimeDocview.getTitle());
        rec.putIfNotNull(EVCombinedRec.TRANSLATED_TITLE, runtimeDocview.getTranslatedTitle());
        rec.putIfNotNull(EVCombinedRec.MONOGRAPH_TITLE, runtimeDocview.getMonographTitle());
        rec.putIfNotNull(EVCombinedRec.SERIAL_TITLE, rs.getString("TITLE_OF_SERIAL"));
        rec.putIfNotNull(EVCombinedRec.REPORTNUMBER, rs.getString("REPORT_NUMBER"));
        rec.putIfNotNull(EVCombinedRec.CONFERENCE_LOCATION, rs.getString("LOCATION_OF_MEETING"));

        // CL
        if(rs.getString("CATEGORY_CODE") != null)
        {
          rec.put(EVCombinedRec.CLASSIFICATION_CODE,(rs.getString("CATEGORY_CODE")).split(AUDELIMITER));
        }

        String pages = runtimeDocview.getPages();
        rec.put(EVCombinedRec.DEDUPKEY,
                getDedupKey(rec.getString(EVCombinedRec.ISSN),
                            rec.getString(EVCombinedRec.CODEN),
                            rs.getString("VOLUME_ID"),
                            rs.getString("ISSUE_ID"),
                            pages));
        rec.putIfNotNull(EVCombinedRec.STARTPAGE, getFirstPage(pages));
        rec.putIfNotNull(EVCombinedRec.CODEN, rs.getString("CODEN"));

        rec.putIfNotNull(EVCombinedRec.CONFERENCE_NAME, rs.getString("NAME_OF_MEETING"));
        rec.putIfNotNull(EVCombinedRec.MEETING_DATE, rs.getString("DATE_OF_MEETING"));

        rec.putIfNotNull(EVCombinedRec.DOCID, rs.getString("M_ID"));
        rec.putIfNotNull(EVCombinedRec.DATABASE, "gref");
        rec.putIfNotNull(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
        rec.putIfNotNull(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("VOLUME_ID")));
        rec.putIfNotNull(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("ISSUE_ID")));
        rec.putIfNotNull(EVCombinedRec.ACCESSION_NUMBER,rs.getString("ID_NUMBER"));
        rec.putIfNotNull(EVCombinedRec.DOI, rs.getString("DOI"));

        rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));

        try
        {
          this.writer.writeRec(rec);
        }
        catch(Exception e)
        {
          System.out.println("MID = " + rs.getString("M_ID"));
          e.printStackTrace();
        }
        i++;
      } // while
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private String[] parseMeridianData(String column)
  {
    String[] keys = null;
    if(column != null)
    {
      String[] features = column.split("\\|");
      keys = new String[features.length];
      for(int i = 0; i < features.length; i++)
      {
        keys[i] = features[i].split(";")[0];
      }
    }
    return keys;
  }

  private String getFirstNumber(String v)
  {
    MatchResult mResult = null;
    if (v == null)
    {
      return null;
    }

    if (perl.match("/[1-9][0-9]*/", v))
    {
      mResult = perl.getMatch();
    }
    else
    {
      return null;
    }

    return mResult.toString();
  }

  private String getFirstPage(String v)
  {

    MatchResult mResult = null;
    if (v == null)
    {
      return null;
    }

    if (perl.match("/[A-Z]?[0-9][0-9]*/", v))
    {
      mResult = perl.getMatch();
    }
    else
    {
      return null;
    }

    return mResult.toString();
  }


  private String getDedupKey(String issn,
                              String coden,
                              String volume,
                              String issue,
                              String page)
                              throws Exception
  {

    String firstVolume = getFirstNumber(volume);
    String firstIssue = getFirstNumber(issue);
    String firstPage = getFirstPage(page);

    if ((issn == null && coden == null) ||
    firstVolume == null ||
    firstIssue == null ||
    firstPage == null)
    {
      return (new GUID()).toString();
    }

    StringBuffer buf = new StringBuffer();

    if (issn != null)
    {
      buf.append(perl.substitute("s/-//g", issn));
    }
    else if(coden != null)
    {
      buf.append(coden);
    }

    buf.append("vol" + firstVolume);
    buf.append("is" + firstIssue);
    buf.append("pa" + firstPage);

    return buf.toString().toLowerCase();

  }

  private class LocalEntityResolver implements EntityResolver {

    public LocalEntityResolver() {
        // TODO Auto-generated constructor stub
    }
    public InputSource resolveEntity(String publicId, String systemId) {
        InputStreamReader is = null;
//        try {
            String dtdfile = new File(systemId).getName();
            //System.out.println("<!--" + dtdfile + " == " + systemId + "-->");
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/" + dtdfile);
            if(in != null)
            {
              is = new InputStreamReader(in);
            }
            else
            {
              System.out.println("Cannot open resource as stream");
            }
//        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return new InputSource(is);
    }

  }

  private class LocalErrorHandler implements ErrorHandler {
    public void warning(SAXParseException e) throws SAXException {
      System.out.println("Warning: ");
      printInfo(e);
    }

    public void error(SAXParseException e) throws SAXException {
      System.out.println("Error: ");
      printInfo(e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
      System.out.println("Fatal error: ");
      printInfo(e);
    }

    private void printInfo(SAXParseException e) {
      System.out.println("   Public ID: " + e.getPublicId());
      System.out.println("   System ID: " + e.getSystemId());
      System.out.println("   Line number: " + e.getLineNumber());
      System.out.println("   Column number: " + e.getColumnNumber());
      System.out.println("   Message: " + e.getMessage());
    }
  }
}
