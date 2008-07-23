package org.ei.data.books;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.oro.text.perl.Perl5Util;
//import org.ei.query.base.PorterStemmer;
//import org.ei.data.books.tools.OutputPageData;
//import org.ei.data.books.tools.PDF_FileInfo;
import org.ei.util.StringUtil;

public class BookIndexer
{
    private static Log log = LogFactory.getLog(BookIndexer.class);
    private static Log sqllog = LogFactory.getLog("SqlBatch");

//  private static PorterStemmer stemmer = new PorterStemmer();
//  private static TextFileFilter filter = new TextFileFilter();
    private static Perl5Util perl = new Perl5Util();
    private static Map stopWords = new HashMap();
//  private static int numfiles = 0;
//  private static int numbooks = 0;

  static
  {
    stopWords.put("Engineering", "n");
    stopWords.put("Engineers", "n");
    stopWords.put("pH", "n");
  }

  public static void main(String args[])
    //throws Exception
  {
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
    String username = "AP_PRO1"; //args[2];
    String password = "ei3it"; //args[3];
    boolean executeBatch = false;
    List terms = new ArrayList();

    if(args != null)
    {
	    if(args.length >= 1 && args[0] != null) {
          executeBatch = (args[0].equalsIgnoreCase("batch"));
      }
    }

    if(executeBatch) {
      log.info("Executing batch updates...");
    }
    else {
      log.info("SQL UPDATE statements written to log file...");
    }

    log.info("Using table PAGES_ALL");
    Connection con = null;
    ResultSet rs = null;
    Statement stmt = null;

    try {
        Class.forName(driver);
    } catch (ClassNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

    try
    {
        url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
        username = "AP_EV_SEARCH"; //args[2];
        password = "ei3it"; //args[3];
        con = DriverManager.getConnection(url,username,password);

        stmt = con.createStatement();
        rs = stmt.executeQuery("select MAIN_TERM_SEARCH from CPX_THESAURUS WHERE STATUS='C'");
        while(rs.next())
        {
            String term = rs.getString("MAIN_TERM_SEARCH");
            terms.add(term);
//                log.info(term);
        }

        if(rs != null) {
            close(rs);
        }
        if(stmt != null) {
            close(stmt);
        }
    }
    catch(SQLException e) {
        e.printStackTrace();
    }
    finally {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    try
    {
      // "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it"

      Map books = new HashMap();
      url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
      username = "AP_PRO1"; //args[2];
      password = "ei3it"; //args[3];
      con = DriverManager.getConnection(url,username,password);
      stmt = con.createStatement();

      List isbnList = new ArrayList();
      try {
        BufferedReader rdr = new BufferedReader(new FileReader("isbnList.txt"));
        while (rdr.ready()) {
          String aline = rdr.readLine();
          isbnList.add(aline);
        }
        rdr.close();
        log.info("Processing only ISBNs found in isbnList.txt file.");
      }
      catch(IOException ioe) {
        log.info("Processing all ISBNs from BOOKS table.");
      }

      String isbnString = null;
      if(!isbnList.isEmpty()) {
        Iterator iItr = isbnList.iterator();
        while(iItr.hasNext()) {
          String nextIsbn = "'" + (String) iItr.next() + "'";
          if(isbnString != null) {
            isbnString = isbnString + nextIsbn;
          }
          else {
            isbnString = nextIsbn;
          }
          if(iItr.hasNext()) {
            isbnString = isbnString.concat(",");
          }
        }
      }

      // use view which contains all books from both the 407 and 991 tables
      String bookQuery = "SELECT BN13, TI FROM BOOKS ";
      if(isbnString != null)
      {
        bookQuery = bookQuery + " WHERE BN13 IN ("+ isbnString + ")";
      }

      log.info(bookQuery);
      rs = stmt.executeQuery(bookQuery);

      while(rs.next())
      {
        String bn = rs.getString("BN13");
        String ti = rs.getString("TI");
        books.put(bn,ti);
      }
      //log.info(books.size());

      if(rs != null) {
          close(rs);
      }
      if(stmt != null) {
          close(stmt);
      }

      Iterator booksIt = (books.keySet()).iterator();

      PreparedStatement pstmt1 = null;

      pstmt1 = con.prepareStatement("SELECT docid,page_txt,vo FROM PAGES_ALL WHERE BN13 = ? ORDER BY PAGE_NUM ASC");

      long commitCount = 0;
      Statement batchstmt = null;

      if(executeBatch)
      {
        con.setAutoCommit(false);
        batchstmt = con.createStatement();
      }

      while(booksIt.hasNext())
      {
        String isbn13 = (String)booksIt.next();
        String bookTitle = (String)books.get(isbn13);
        pstmt1.setString(1, isbn13);
        rs = pstmt1.executeQuery();

        log.info("/* " + isbn13 + " */");

        while(rs.next())
        {
          String id = rs.getString("docid");
          String col = rs.getString("vo");

          String keywords = null;
          Clob clob = rs.getClob("page_txt");
          if(clob != null)
          {
              String abs = null; //getPageTextFromFile(id,isbn13);
              try {
                  if(abs == null) {
                      abs = StringUtil.getStringFromClob(clob);
                  }
              } catch (Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
              if(abs != null) {
                abs = abs.replaceFirst(bookTitle, "");
                //abs = abs.replaceFirst("This page intentionally left blank", "");
                keywords = getKeywords(terms, abs);
                if(keywords != null)
                {
                  sqllog.info("UPDATE PAGES_ALL SET page_keywords = '" + keywords.replaceAll("'", "''") + "' WHERE docid = '" + id + "';");
                  if(executeBatch)
                  {
                    batchstmt.addBatch("UPDATE PAGES_ALL SET page_keywords = '" + keywords.replaceAll("'", "''") + "' WHERE docid = '" + id + "'");
                  }
                  commitCount++;
                }
                else {
                  log.info("/* No Keyword matches for " + id + ", " + col + " */");
                }
              }
              else {
                log.info("/* NULL Abstract for " + id + " */");
              }
          } // if
          if(commitCount >= 100)
          {
              if(executeBatch)
              {
                int[] updCnt = batchstmt.executeBatch();
                con.commit();
                /* for(int i = 0; i < updCnt.length; i++)
                {
                  log.info("" + i);
                } */
                close(batchstmt);
                batchstmt = con.createStatement();
              }

              sqllog.info("commit;");
              commitCount = 0;

          }
        } // while rs()
      } // while bookList()
      if(executeBatch)
      {
        int[] updCnt = stmt.executeBatch();
        con.commit();
      }
      sqllog.info("commit;");

      if(rs != null) {
        close(rs);
      }
      if(pstmt1 != null) {
        close(pstmt1);
      }
      if(batchstmt != null) {
        close(batchstmt);
      }
    }
    catch (BatchUpdateException be) {
      try
      {
        con.rollback();
      }
      catch(SQLException e) {
        log.error("SQLException ",e);
      }
      log.error("BatchUpdateException ",be);
    }
    catch(SQLException e) {
      log.error("SQLException ",e);
    }
    finally {
      if(con != null)
      {
        try {
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error("SQLException ",e);
        }
      }
    }
    sqllog.info("quit;");
    log.info("Complete.");
  }

/*
  private static String getPageTextFromFile(String id, String isbn13) {
      String[] idtokens = id.split("_");
        StringBuffer allPageTxt = new StringBuffer();
        File page_txt = new File(OutputPageData.PATH_PREFIX + System.getProperty("file.separator") + "BurstAndExtracted" + System.getProperty("file.separator") + isbn13 + System.getProperty("file.separator") + "pg_" + PDF_FileInfo.formatPageNumber(Long.parseLong(idtokens[2])) + ".txt");
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new FileReader(page_txt));
            while (rdr.ready()) {
                allPageTxt.append(rdr.readLine());
                // TODO Auto-generated method stub
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {

        }
        return allPageTxt.toString();
    }
*/
    private static void close(ResultSet rs)
  {
    if(rs != null)
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  private static void close(Statement stmt)
  {
    if(stmt != null)
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  private static String getKeywords(List terms, String text)
  {
    StringBuffer termbuf = new StringBuffer();
    String spage = stem(text);
    for(int j=0; j<terms.size();j++)
    {
      String term = (String) terms.get(j);
      term = term.trim();
      if(term.length()>0 &&
         !stopWords.containsKey(term))
      {
        String stemmedTerm = stem(term);
        String regex = "m/\\b"+stemmedTerm+"\\b/";
        if(perl.match(regex,spage))
        {
          //log.info("Hit:"+term);
          if(termbuf.length() > 0)
          {
            termbuf.append("|");
          }
          termbuf.append(perl.substitute("s/'/''/g",term));
        }
      }
    }

    if(termbuf.length()>0)
    {
      return termbuf.toString();
    }

    return null;
  }

  private static String stem(String words)
  {
    String lwords = words.toLowerCase();
    String[] splitwords = lwords.split("\\W+");
    StringBuffer stemmedBuffer = new StringBuffer();

    for(int i=0; i<splitwords.length;i++)
    {
      if(stemmedBuffer.length() > 0)
      {
        stemmedBuffer.append(" ");
      }

      stemmedBuffer.append(splitwords[i]);
    }

    return stemmedBuffer.toString();
  }
}