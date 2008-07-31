package org.ei.data.books.tocs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.books.collections.ReferexCollection;
import org.ei.data.books.tools.PDF_FileInfo;

public abstract class ReferexBaseProcessor implements ArchiveProcessor {
  
  protected static Log log = LogFactory.getLog(ReferexBaseProcessor.class);

  public static final String FILE_SEP = System.getProperty("file.separator");
  public static final String BURST_AND_EXTRACTED = "V:\\EW\\BurstAndExtracted\\";
  public static final String BURST_PAGES = "V:\\EW\\Pages\\";
  public static final String WHOLE_PDFS = "V:\\EW\\whole_pdfs\\";

  protected PdfProcessorStamper pdfprocessor;
  protected TocTransformer toctransformer;

  public ReferexBaseProcessor() {
    // TODO Auto-generated constructor stub
    pdfprocessor = new PdfProcessorStamper();
    toctransformer = new TocTransformer();
    createDataSource();
  }
  
  private final static String jdbcUrl = "jdbc:oracle:thin:AP_PRO1/ei3it@neptune.elsevier.com:1521:EI";
  private final static String driverClassName = "oracle.jdbc.driver.OracleDriver";
  public void createDataSource() {
    try {
        Class.forName(ReferexBaseProcessor.driverClassName);
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
  public Connection getDataSourceConnection() throws SQLException {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(ReferexBaseProcessor.jdbcUrl);
    } catch (SQLException e) {
        log.error("Error Creating Data Source.", e);
        throw e;
    }
    return conn;
  }
  
  public HashMap getBookInfo(String isbn) {
    HashMap<String, String> bookInfo = new HashMap<String, String>();
    
    Connection con = null;
    
    try
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        con = getDataSourceConnection();
        pstmt = con.prepareStatement("SELECT AUS, TI, CVS, VO FROM PAGES_ALL WHERE BN13=? AND PAGE_NUM=0");

        pstmt.setString(1, isbn);
        rs = pstmt.executeQuery();

        if(rs.next())
        {
            bookInfo.put("Title", rs.getString("TI"));
            bookInfo.put("Author", rs.getString("AUS"));
            String col = rs.getString("VO");
            ReferexCollection referexcol = ReferexCollection.getCollection(col);
            if(referexcol != null) {
              col = referexcol.getDisplayName();
            }
            bookInfo.put("Subject",col);
            String cvs = rs.getString("CVS");
            if(cvs != null) {
              cvs = "Referex; ".concat(cvs);
            }
            else {
              log.info("CVS are null for " + isbn);
              cvs = "Referex";
            }
            bookInfo.put("Keywords",cvs);
        }

        bookInfo.put("Creator", "Engineering Village");
        bookInfo.put("Producer", "Elsevier Engineering Information");

        if(rs != null) {
            rs.close();
        }
        if(pstmt != null) {
            pstmt.close();
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
    
    return bookInfo;
  }
  private List<String> isbnList = new ArrayList<String>();

  public void setIsbnList(List<String> isbnlist) { this.isbnList = isbnlist; }
  public List<String> getIsbnList() { return this.isbnList; }
  
  public int processall() throws IOException {
    Iterator isbnitr = getIsbnList().iterator();
    int count = 0;
    while(isbnitr.hasNext()) {
      String isbn = (String) isbnitr.next();
      if(process(isbn)) {
        count++;
      }
    }
    return count;
  }

  public boolean checkIsbn(String isbn) {
    boolean result = false;
    if((isbn == null) || (!isbnList.isEmpty() && !isbnList.contains(isbn))) {
        log.debug("Unused ISBN: " + isbn);
    }
    else {
        log.debug("found isbn " + isbn);
        result = true;
    }    
    return result;
  }
  
 
  private boolean createWholePdf = false;
  private boolean burstWholePdf = false;
  private boolean stampWholePdf = false;
  private boolean copyChapters = false;
  private boolean createToc = false;
  private boolean checkArchive = false;
  
  public boolean isBurstWholePdf() {
    return burstWholePdf;
  }

  public void setBurstWholePdf(boolean burstWholePdf) {
    this.burstWholePdf = burstWholePdf;
  }

  public boolean isCopyChapters() {
    return copyChapters;
  }

  public void setCopyChapters(boolean copyChapters) {
    this.copyChapters = copyChapters;
  }

  public boolean isCreateWholePdf() {
    return createWholePdf;
  }

  public void setCreateWholePdf(boolean createWholePdf) {
    this.createWholePdf = createWholePdf;
  }

  public boolean isStampWholePdf() {
    return stampWholePdf;
  }

  public void setStampWholePdf(boolean stampWholePdf) {
    this.stampWholePdf = stampWholePdf;
  }
  public boolean isCreateToc() {
    return createToc;
  }
  public void setCreateToc(boolean createToc) {
    this.createToc = createToc;
  }

  public boolean isCheckArchive() {
    return checkArchive;
  }

  public void setCheckArchive(boolean checkArchives) {
    this.checkArchive = checkArchives;
  }

}
