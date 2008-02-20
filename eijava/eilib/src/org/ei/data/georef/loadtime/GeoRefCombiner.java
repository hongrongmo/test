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

public class GeoRefCombiner
  extends Combiner
{

  public static final String AUDELIMITER    = new String(new char[] {30});
  public static final String IDDELIMITER    = new String(new char[] {29});
  public static final String GROUPDELIMITER   = new String(new char[] {02});

  Perl5Util perl = new Perl5Util();

  private int exitNumber;

  private static String tablename;

  public static void main(String args[])
                          throws Exception
  {
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url  = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
    String username = "AP_PRO1";
    String password = "ei3it";
    url = args[0];
    driver  = args[1];
//    username  = args[2];
    password  = args[3];
    int loadNumber = 2008; //Integer.parseInt(args[4]);
    int recsPerfile = Integer.parseInt(args[5]);
    int exitAt = Integer.parseInt(args[6]);
    tablename = args[7];

    Combiner.TABLENAME = tablename;
    Combiner.EXITNUMBER = exitAt;

    CombinedWriter writer = new CombinedXMLWriter(recsPerfile,
                                                  loadNumber,
                                                  "gref");

    GeoRefCombiner c = new GeoRefCombiner(writer);
//    if (loadNumber > 3000 || loadNumber < 1000)
    if(false)
    {
      c.writeCombinedByWeekNumber(url,
                                  driver,
                                  username,
                                  password,
                                  loadNumber);
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

        String mid = rs.getString("M_ID");
        String year = rs.getString("DATE_OF_PUBLICATION");
        if(year == null)
        {
          year = rs.getString("DATE_OF_MEETING");
          if(year == null)
          {
            year = rs.getString("OTHER_DATE");
            if(year == null)
            {
              year = rs.getString("UPDATE_CODE");
            }
          }
        }
        Matcher yearmatch = Pattern.compile("^(\\d{4})").matcher(year);
        if(yearmatch.find())
        {
          year = yearmatch.group(1);
        }
        rec.putIfNotNull(EVCombinedRec.PUB_YEAR, year);

        // AUS
        String aString = rs.getString("PERSON_ANALYTIC");
        if(aString != null)
        {
          // alternate spellings
          String altAuthor = rs.getString("ALTERNATE_AUTHOR");
          if(altAuthor != null)
          {
            aString = aString.concat(AUDELIMITER).concat(altAuthor);
            rec.put(EVCombinedRec.AUTHOR, aString.split(AUDELIMITER));
          }
        }
        // EDS
        String eString = rs.getString("PERSON_MONOGRAPH");
        if(eString != null)
        {
          if(eString != null)
          {
            rec.put(EVCombinedRec.EDITOR, eString.split(AUDELIMITER));
          }
        }

        // CO - Author Aff. Country
        // Uses runtime Docview instance to access decorator class
        String country = rs.getString("AUTHOR_AFFILIATION_COUNTRY");
        if(country != null)
        {
          DocumentView.FieldDecorator cd = runtimeDocview.new CountryDecorator(country);
          rec.put(EVCombinedRec.COUNTRY, new String[]{country, cd.getValue()});
        }
        // LA
        String laString = runtimeDocview.new LanguageDecorator(runtimeDocview.createColumnValueField("LANGUAGE_TEXT")).getValue();
        if(laString != null)
        {
          rec.put(EVCombinedRec.LANGUAGE, laString.split(AUDELIMITER));
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
          }
          if(issn !=null && e_issn != null && issn.length()>0 && e_issn.length()>0)
          {
            issnString.append(AUDELIMITER);
          }
          if(e_issn != null && e_issn.length()>0)
          {
            issnString.append(e_issn);
          }
          rec.put(EVCombinedRec.ISSN, (issnString.toString()).split(AUDELIMITER));
        }
        // BN
        if (rs.getString("ISBN") != null)
        {
          rec.put(EVCombinedRec.ISBN,(rs.getString("ISBN")).split(AUDELIMITER));
        }

        rec.putIfNotNull(EVCombinedRec.TITLE, runtimeDocview.getTitle());
        rec.putIfNotNull(EVCombinedRec.TRANSLATED_TITLE, runtimeDocview.getTranslatedTitle());
        rec.putIfNotNull(EVCombinedRec.MONOGRAPH_TITLE, runtimeDocview.getMonographTitle());

        String pages = runtimeDocview.getPages();
        rec.put(EVCombinedRec.DEDUPKEY,
                getDedupKey(rec.getString(EVCombinedRec.ISSN),
                rec.getString(EVCombinedRec.CODEN),
                rs.getString("VOLUME_ID"),
                rs.getString("ISSUE_ID"),
                pages));
        rec.putIfNotNull(EVCombinedRec.STARTPAGE, getFirstPage(pages));
        rec.putIfNotNull(EVCombinedRec.CODEN, rs.getString("CODEN"));
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
          System.out.println("MID= "+mid);
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

  private String getLanguage(String code)
  {
    StringBuffer languageName= new StringBuffer();
    String lName = null;
    if(code.indexOf(AUDELIMITER)>-1)
    {
      StringTokenizer tokens = new StringTokenizer(code, AUDELIMITER);
      while(tokens.hasMoreTokens())
      {
        String  sCode = tokens.nextToken().trim();
        lName = Language.getIso639Language(sCode);
        if(lName!=null)
        languageName.append(lName+AUDELIMITER);
        else
        languageName.append(sCode+AUDELIMITER);
      }
    }
    else
    {
      lName = Language.getIso639Language(code);
      if(lName!=null)
      languageName.append(lName);
      else
      languageName.append(code);
    }
    return languageName.toString();
  }

  private String[] prepareMulti(String multiString)
                                throws Exception
  {
    AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
    String s = null;
    ArrayList list = new ArrayList();

    while ((s = astream.readAuthor()) != null)
    {
      s = s.trim();
      if (s.length() > 0)
      {
        list.add(s);
      }
    }

    return (String[]) list.toArray(new String[1]);

  }

  private String[] prepareAuthor(String aString)
                                throws Exception
  {
    String[] authorArray = null;
    authorArray = aString.split(AUDELIMITER);
    return authorArray;
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

  private boolean validYear(String year)
  {
    if (year == null)
    {
      return false;
    }

    if (year.length() != 4)
    {
      return false;
    }

    return perl.match("/[1-9][0-9][0-9][0-9]/", year);
  }

}
