package org.ei.data.books.tocs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.books.collections.ReferexCollection;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public abstract class ReferexBaseProcessor implements ArchiveProcessor {

  protected static Log log = LogFactory.getLog(ReferexBaseProcessor.class);

  public static final String FILE_SEP = System.getProperty("file.separator");
  protected PdfProcessorStamper pdfprocessor;
  protected TocTransformer toctransformer;

  private DataSource data_source = null;
  public static final String DEFAULT_ROOT = "V:" + FILE_SEP + "EW";
  private String BURST_AND_EXTRACTED =  "BurstAndExtracted";
  private String BURST_PAGES = "Pages";
  private String WHOLE_PDFS = "whole_pdfs";

  private String archive_root = null;
  private String burst_extract = null;
  private String pages = null;
  private String whole_pdfs = null;

  public String getBurst_extract() {
    return burst_extract;
  }

  public void setBurst_extract(String burst_extract) {
    this.burst_extract = burst_extract;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public String getWhole_pdfs() {
    return whole_pdfs;
  }

  public void setWhole_pdfs(String whole_pdfs) {
    this.whole_pdfs = whole_pdfs;
  }

  public String getArchive_root() {
    return archive_root;
  }

  public void setArchive_root(String archive_root) {
    log.info("Setting archive root to: " + archive_root);
    this.archive_root = archive_root;
  }

  public ReferexBaseProcessor() {
    // TODO Auto-generated constructor stub
	  init();
  }
  public void init() {
	  //Initialize classes and directories
	  pdfprocessor = new PdfProcessorStamper();
	  toctransformer = new TocTransformer();
	  createDataSource();
	  setArchive_root(System.getProperty("archive_root",DEFAULT_ROOT));
	  setBurst_extract(getArchive_root() + FILE_SEP + BURST_AND_EXTRACTED + FILE_SEP);
	  setPages(getArchive_root() + FILE_SEP + BURST_PAGES + FILE_SEP);
	  setWhole_pdfs(getArchive_root() + FILE_SEP + WHOLE_PDFS + FILE_SEP);
	  File dirBurstExtract = new File(getBurst_extract());
	  File dirBurstPages = new File(getPages());
	  File dirWholePDFs = new File(getWhole_pdfs());

	  if(!dirBurstExtract.exists()) {dirBurstExtract.mkdir();}
	  if(!dirBurstPages.exists()) {dirBurstPages.mkdir();}
	  if(!dirWholePDFs.exists()) {dirWholePDFs.mkdir();}
  }

  public void createDataSource() {
    try {
      ClassPathResource classPathResource = new ClassPathResource(
          "applicationContext.xml");
      XmlBeanFactory beanFactory = new XmlBeanFactory(classPathResource);
      data_source = (DataSource) beanFactory.getBean("dataSource");
    } finally {
    }
  }

  public Connection getDataSourceConnection() throws SQLException {
    Connection conn = null;
    try {
      conn = data_source.getConnection();
    } catch (SQLException e) {
        log.error("Error Creating Data Source.", e);
        throw e;
    }
    return conn;
  }

  public String showDataSource()
  {
    BasicDataSource jm = (BasicDataSource) data_source;

    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append("NumActive = ").append(jm.getNumActive()).append(", ");
    sb.append("NumIdle = ").append(jm.getNumIdle()).append(", ");
    sb.append("MaxActive = ").append(jm.getMaxActive()).append(", ");
    sb.append("DriverClassName = ").append(jm.getDriverClassName() ).append(", ");
    sb.append("Username = ").append(jm.getUsername() ).append(", ");
    sb.append("Url = ").append(jm.getUrl());
    sb.append("]");

    return sb.toString();
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
    System.out.println("ISBN SIZE= "+getIsbnList().size());
    int count = 0;
    while(isbnitr.hasNext()) {
      String isbn = (String) isbnitr.next();
      System.out.println("ISBN13= "+isbn);
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
  private boolean createCloud = false;

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

  public boolean isCreateCloud() {
    return createCloud;
  }

  public void setCreateCloud(boolean cloud) {
    this.createCloud = cloud;
  }

  private static List<String> stopwords;
  static {
    stopwords = new ArrayList<String>();
    stopwords.add("design");
    stopwords.add("engineering");
  }
  public Map<String, Integer> getCloudData(String isbn) {
    Map<String, Integer> bookInfo = new HashMap<String, Integer>();

    Connection con = null;
    try
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        con = getDataSourceConnection();
        pstmt = con.prepareStatement("SELECT PAGE_KEYWORDS FROM PAGES_ALL WHERE BN13=? ORDER BY PAGE_NUM DESC");

        pstmt.setString(1, isbn);
        rs = pstmt.executeQuery();
        while(rs.next())
        {
            String keywords = rs.getString("PAGE_KEYWORDS");
            if(keywords != null)
            {
              String[] keyword_array = keywords.split("\\|");
              for(int idx = 0; idx < keyword_array.length; idx++)
              {
                //if(!keyword_array[idx].contains(" "))
                //{
                // break;
                //}
                if(ReferexBaseProcessor.stopwords.contains(keyword_array[idx]))
                {
                  break;
                }
                if(bookInfo.containsKey(keyword_array[idx])) {
                  Integer count = bookInfo.get(keyword_array[idx]);
                  bookInfo.put(keyword_array[idx], new Integer(count.intValue() + 1));
                }
                else {
                  bookInfo.put(keyword_array[idx], new Integer(1));
                }
              }
            }
        }

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
}
