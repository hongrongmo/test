package org.ei.data.books;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.books.collections.ReferexCollection;
import org.ei.common.AuthorStream;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.EVCombinedRec;
import org.ei.util.Base64Coder;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.ei.xml.Entity;

public class ReferexCombiner {
    private static Log log = LogFactory.getLog(ReferexCombiner.class);

    private String connectionURL = "jdbc:oracle:thin:@138.12.85.144:1521:EI";
    private String username = "AP_EV_SEARCH";
    private String password = "ei3it";
    private String driverClassName = "oracle.jdbc.driver.OracleDriver";

    private DataSource localDataSource = null;
    private List<String> isbnList = new ArrayList<String>();
    private static String tablename;
    private static String environment = "prod";

    static {
        // make sure we use the xerces parser for Vaildation
        // otherwise the first parser found in the classpath will be used
        System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
    }

    public ReferexCombiner() {
        super();
    }

    public static void main(String[] args) {
        ReferexCombiner c = new ReferexCombiner();
        c.runExtracts(args);
    }

    public void runExtracts(String[] args) {

        int recsPerbatch = 10000;
        int index = 1;
        try {
            createDataSource();
            if ((args.length == 1) && (args[0].length() == 13) && (args[0].startsWith("978"))) {
                log.info("ISBN on Command line: " + args[0]);
                isbnList.add(args[0]);
                callCombinedXMLWriter(recsPerbatch, index);
            } else if (args.length == 9) {

                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    int loadNumber = 0;
                    this.connectionURL = args[0];
                    this.driverClassName = args[1];
                    this.username = args[2];
                    this.password = args[3];
                    recsPerbatch = Integer.parseInt(args[5]);
                    String operation = args[6];
                    ReferexCombiner.tablename = args[7];
                    String environment = args[8].toLowerCase();
                    conn = getDataSourceConnection();
                    stmt = conn.createStatement();

                    for (int yearIndex = 1987; yearIndex <= 2008; yearIndex++) {
                        isbnList.clear();
                        index = yearIndex;
                        log.info("Processing year " + index + "...");
                        String yearQuery = "SELECT BN13 FROM PAGES_ALL where PAGE_NUM = 0 and SEQ_NUM is not null and yr = " + index;
                        rs = stmt.executeQuery(yearQuery);
                        while (rs.next()) {
                            if (rs.getString("BN13") != null)
                                isbnList.add(rs.getString("BN13"));
                        }
                        callCombinedXMLWriter(recsPerbatch, index);
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    log.error("Error:", e);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    log.error("Error:", e);
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        log.error("Error: finally ", e);
                    } catch (Exception e) {
                        log.error("General Error: finally ", e);
                    }
                }
            } else {
                try {
                    BufferedReader rdr = new BufferedReader(new FileReader("isbnList.txt"));
                    while (rdr.ready()) {
                        String aline = rdr.readLine();
                        isbnList.add(aline);
                    }
                    rdr.close();
                    log.info("Processing only ISBNs found in isbnList.txt file.");
                    callCombinedXMLWriter(recsPerbatch, index);
                } catch (IOException ioe) {
                    log.info("Processing all ISBNs from BOOKS table.");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("Error:", e);
        } finally {
            releaseDataSource();
        }
    }

    public void callCombinedXMLWriter(int recsPerbatch, int index) {

        try {
            CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, index, "pag", environment);
            List<BookRecord> extracts = new ArrayList<BookRecord>();
            extracts.add(new BookRecord());
            extracts.add(new PageRecord());
            Iterator<BookRecord> itr = extracts.iterator();

            while (itr.hasNext()) {
                writeRecords((Extractable) itr.next(), writer);
            }
            writer.end();
            writer.flush();
        } catch (Exception e) {
            log.error("exception:", e);
        }
    }

    public DataSource getDataSource() {
        return localDataSource;
    }

    public void setDataSource(DataSource ads) {
        localDataSource = ads;
    }

    public void releaseDataSource() {
    }

    public void createDataSource() {
        try {
            Class.forName(this.driverClassName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // if(getDataSource() == null)
        // {
        // ds = new OracleDataSource();
        // ((OracleDataSource)ds).setURL(ReferexCombiner.jdbcUrl);
        // setDataSource(ds);
        // }
    }

    public Connection getDataSourceConnection() throws SQLException {

        DataSource ds = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.connectionURL, this.username, this.password);

            // if(getDataSource() != null) {
            // conn = ds.getConnection();
            // }

        } catch (SQLException e) {
            log.error("Error Creating Data Source.", e);
            throw e;
        }

        return conn;
    }

    public void writeRecords(Extractable ext, CombinedWriter writer) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            conn = getDataSourceConnection();

            stmt = conn.createStatement();

            String isbnString = null;
            if (!isbnList.isEmpty()) {

                Iterator<String> iItr = isbnList.iterator();
                while (iItr.hasNext()) {
                    String bookQuery = ext.getQuery();
                    String isbn = (String) iItr.next();
                    String nextIsbn = "'" + isbn + "'";
                    bookQuery = bookQuery + " AND BN13 = " + nextIsbn;
                    rs = stmt.executeQuery(bookQuery);
                    while (rs.next()) {
                        EVCombinedRec rec = ext.populate(rs);
                        if (rec != null) {
                            writer.writeRec(rec);
                        }
                    }
                }
            }
            log.info("Wrote records.");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error("Error:", e);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("Error:", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Error: finally ", e);
            } catch (Exception e) {
                log.error("General Error: finally ", e);
            }
        }
    }

    /* Utility Methods */
    private String getStringFromClob(Clob clob) {
        String temp = "";

        if (clob != null) {
            try {
                temp = clob.getSubString(1, (int) clob.length());
            } catch (SQLException sqle) {
                temp = "";
            }
        }

        return temp;
    }

    // Be Careful - splitString wil be applied as a RegExp
    // So MetaChars (such as a '|' must be properly escaped
    // otherwise results will be unpredictable
    private List<String> convertString2List(String sList, String splitString) {

        if (sList == null) {
            return new ArrayList<String>();
        }
        Pattern p = Pattern.compile(splitString);

        return Arrays.asList(p.split(sList));
    }

    private List<String> convertString2List(String sList) {
        return convertString2List(sList, ";");
    }

    private String replaceNull(String sVal) {

        if (sVal == null) {
            sVal = "";
        }
        return sVal;
    }

    // pattern to replace all non visible ASCII characters
    // 0-31 and 128-255
    private static final Pattern nonAscii = Pattern.compile("[\\x00-\\x1F\\x80-\\xFF]");
    private static final Pattern pubYear = Pattern.compile("[1-9][0-9][0-9][0-9]");
    private static final Pattern pgNum = Pattern.compile("\\b?\\d+\\b?");
    private static Pattern hundredWords = Pattern.compile("((?:[\\w\\p{Punct}][^\\s]*)\\s+){1,100}", Pattern.MULTILINE);

    // These Queries will extract WOBL Extract
    private static final String BOOK_PAGES_QUERY = "SELECT PAGE_KEYWORDS,DOCID,BN,BN13,PAGE_TOTAL,TI,ST,AB,PN,CVS,AUS,VO,ISS,YR,NVL(SUB,0) AS SUB, PAGE_NUM, CHAPTER_TITLE, CHAPTER_START, SECTION_TITLE, SECTION_START, PAGE_TXT,SEQ_NUM FROM PAGES_ALL WHERE PAGE_NUM <> 0 AND SEQ_NUM IS NOT NULL";
    private static final String BOOK_QUERY = "SELECT DOCID,BN,BN13,PAGE_TOTAL,TI,ST,AB,PN,CVS,AUS,VO,ISS,YR,NVL(SUB,0) AS SUB,SEQ_NUM FROM PAGES_ALL WHERE PAGE_NUM=0 AND SEQ_NUM IS NOT NULL";

    private static final String BOOK_DATABASE = "pag";
    private static final String DOCTYPE_BOOK = "BOOK";
    private static final String DOCTYPE_PAGE = "PAGE";

    public static Map<String, String> colMappings = new HashMap<String, String>();
    static {
        colMappings.put(ReferexCollection.MAT.getAbbrev(), ReferexCollection.MAT.getShortname()); // & Mechanical");
        colMappings.put(ReferexCollection.ELE.getAbbrev(), ReferexCollection.ELE.getShortname()); // & Electrical");
        colMappings.put(ReferexCollection.CHE.getAbbrev(), ReferexCollection.CHE.getShortname()); // , Petrochemical & Process");
        colMappings.put(ReferexCollection.CIV.getAbbrev(), ReferexCollection.CIV.getShortname()); // , & Environmental");
        colMappings.put(ReferexCollection.COM.getAbbrev(), ReferexCollection.COM.getShortname()); // , ");
        colMappings.put(ReferexCollection.SEC.getAbbrev(), ReferexCollection.SEC.getShortname()); // , & Networking");
    }

    // interface Extractable
    private interface Extractable {
        public EVCombinedRec populate(ResultSet rs) throws Exception;

        public String getQuery();
    }

    private abstract class ReferexRecord implements Extractable {

        public abstract EVCombinedRec populate(ResultSet rs) throws Exception;

        public abstract String getQuery();

        public abstract String getDoctype();

        public String getCollection(String colkey) {
            if ((colkey != null) && (colMappings.containsKey(colkey.toUpperCase()))) {
                return (String) colMappings.get(colkey.toUpperCase());
            } else {
                return "";
            }
        }

        protected String getPubYear(String yr) {
            String year = "1955";

            if (yr != null) {
                Matcher m = pubYear.matcher(yr);
                if (m.find()) {
                    year = m.group(0);
                }
            }
            return year;
        }

        protected String getDatabase() {
            return BOOK_DATABASE;
        }

        protected String getDeDeupKey() throws Exception {
            String id = null;

            id = (new GUID()).toString();

            return id;
        }

        protected String reversePageNumber(String page, String total) {

            int intPage = Integer.parseInt(page);
            int intTotal = Integer.parseInt(total);

            return String.valueOf((intTotal - intPage) + 1);
        }
    }// inner abstract base class ReferexRecord

    private class BookRecord extends ReferexRecord {

        public String getDoctype() {
            return DOCTYPE_BOOK;
        }

        public String getQuery() {
            return BOOK_QUERY;
        }

        public EVCombinedRec populate(ResultSet rs) throws Exception {
            EVCombinedRec rec = new EVCombinedRec();
            try {

                String sIsbn = replaceNull(rs.getString("BN"));
                String sIsbn13 = replaceNull(rs.getString("BN13"));
                String sIssn = replaceNull(rs.getString("ISS"));

                String pubYear = getPubYear(rs.getString("YR"));
                String sDatabase = getDatabase();
                // pagenum == "0" for main book records
                String pageNum = "0";
                String dedupKey = getDeDeupKey();

                String docid = rs.getString("DOCID");
                String seqnum = rs.getString("SEQ_NUM");

                String sTitle = rs.getString("TI");
                String sFullTitle = sTitle;
                String sSubTitle = rs.getString("ST");
                if (sSubTitle != null) {
                    sFullTitle = sTitle.concat(": ").concat(sSubTitle.trim());
                }

                String sPublisher = rs.getString("PN");
                String sControlledTerms = rs.getString("CVS");
                String sAuthor = rs.getString("AUS");
                String referexColl = replaceNull(rs.getString("VO"));
                String subCollection = rs.getString("SUB");
                String bkDescript = getStringFromClob(rs.getClob("AB"));

                String pubSort = pageNum;
                String loadNumber = reversePageNumber(pageNum, rs.getString("PAGE_TOTAL"));
                String accNumber = pubYear + sIsbn;

                bkDescript = replaceNull(bkDescript);
                bkDescript = nonAscii.matcher(bkDescript).replaceAll(" ");

                String[] arrPubs = new String[] { replaceNull(sPublisher) };

                // authors are ';' delimited
                List<String> aus = new ArrayList<String>();
                String author = "";
                AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(sAuthor.getBytes()));
                while ((author = aStream.readAuthor()) != null) {
                    aus.add(author);
                }
                String arrAus[] = (String[]) aus.toArray(new String[0]);

                // controlled terms are ';' delimited
                List<String> cvs = convertString2List(sControlledTerms);
                String arrCvs[] = (String[]) cvs.toArray(new String[0]);

                // Class codes are where the collection name ELE,MAT,CHE,... is
                // stored.
                // subCollection is the subcollection number or 0
                // Convert Coll (ELE,MAT,CHE,...) from Key to String before we
                // append the
                // SUB number if it is not 0 (ELE1,MAT1,CHE1,...)
                String colName = getCollection(referexColl);
                if (subCollection != null) {
                    // append sub collection number onto collection name
                    try {
                        if (Integer.parseInt(subCollection) != 0) {
                            referexColl = referexColl.concat(subCollection);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
                String[] arrReferexColls = new String[] { referexColl, colName };
                String[] arrIsbns = new String[] { sIsbn, sIsbn13 };

                rec.put(EVCombinedRec.ISBN, arrIsbns);
                rec.put(EVCombinedRec.ISSN, sIssn);
                rec.put(EVCombinedRec.PUB_YEAR, pubYear);
                rec.put(EVCombinedRec.DATABASE, sDatabase);
                rec.put(EVCombinedRec.DEDUPKEY, dedupKey);
                rec.put(EVCombinedRec.DOCTYPE, getDoctype());

                rec.put(EVCombinedRec.DOCID, docid);
                rec.put(EVCombinedRec.PARENT_ID, seqnum);
                rec.put(EVCombinedRec.PAGE, pageNum);
                rec.put(EVCombinedRec.LOAD_NUMBER, loadNumber);
                rec.put(EVCombinedRec.ACCESSION_NUMBER, accNumber);
                rec.put(EVCombinedRec.PUB_SORT, pubSort);
                rec.put(EVCombinedRec.ABSTRACT, bkDescript);

                rec.put(EVCombinedRec.AUTHOR, arrAus);
                rec.put(EVCombinedRec.TITLE, sFullTitle);
                rec.put(EVCombinedRec.PUBLISHER_NAME, arrPubs);

                // 'BT' - Base 64 Encoded Book Title
                String b64Title = "B64" + Base64Coder.encode(sTitle.toLowerCase());
                rec.put(EVCombinedRec.SERIAL_TITLE, b64Title);
                rec.put(EVCombinedRec.CONTROLLED_TERMS, arrCvs);
                rec.put(EVCombinedRec.CLASSIFICATION_CODE, arrReferexColls);

            } // try
            catch (Exception e) {
                log.error("Error in populateBookRecords!");
                throw e;
            }
            return rec;

        }
    }// inner class BookRecord

    private static final String[] badArray = { "contents", "index", "index to", "whats on the cd-rom", "commands", "copyright", "title page",
        "half title page", "back cover", "front cover", "table of contents", "limited disclaimer and warranty", "subject index", "front matter", "frontmatter",
        "cover", "backmatter", "back matter" };

    public static boolean skipPage(String sectionTitle) {
        boolean skip = false;
        if (sectionTitle != null) {
            sectionTitle = sectionTitle.trim();
            for (int i = 0; i < badArray.length; i++) {
                String badTitle = badArray[i];
                if (badTitle.equalsIgnoreCase(sectionTitle)) {
                    log.debug(" Skipping Chapter or Section Title: " + sectionTitle);
                    skip = true;
                    break;
                }
            }
        }
        return skip;
    }

    private class PageRecord extends BookRecord {
        public String getDoctype() {
            return DOCTYPE_PAGE;
        }

        public String getQuery() {
            return BOOK_PAGES_QUERY;
        }

        public EVCombinedRec populate(ResultSet rs) throws Exception {
            EVCombinedRec rec = new EVCombinedRec();
            try {

                // check if we should bail out first to save time
                String pageChapterTitle = replaceNull(rs.getString("CHAPTER_TITLE"));
                String pageSectionTitle = replaceNull(rs.getString("SECTION_TITLE"));

                if (skipPage(pageChapterTitle) || skipPage(pageSectionTitle)) {
                    return null;
                }

                // fill common fields
                rec = super.populate(rs);

                // get Isbn from resultsset
                String sIsbn = replaceNull(rs.getString("BN"));
                String sIsbn13 = replaceNull(rs.getString("BN13"));

                // get page specific fields
                String pageText = getStringFromClob(rs.getClob("PAGE_TXT"));
                String pageNum = rs.getString("PAGE_NUM");

                String pageChapterStartPage = rs.getString("CHAPTER_START");
                String freeLang = rs.getString("PAGE_KEYWORDS");

                String loadNumber = reversePageNumber(pageNum, rs.getString("PAGE_TOTAL"));

                // These are both guaranteed to be non-null
                // pageText = pageText.concat(pageChapterTitle);
                pageText = nonAscii.matcher(pageText).replaceAll(" ");

                String bookTitle = replaceNull(rs.getString("TI"));

                // pull book title and page number from Begining of raw text
                // Page num DOES NOT match number in file
                String pageStartText = pageText;

                pageStartText = pageStartText.replaceFirst(bookTitle, "");
                if (sIsbn13.equals("9780123739735")) {
                    pageStartText = pageStartText.replaceFirst("System- on- Chip Test Architectures ", "");
                }

                String pageChapterTitleRegex = pageChapterTitle.replaceAll("\\p{Punct}", "");

                pageStartText = pageStartText.replaceFirst(pageChapterTitleRegex, "");
                pageStartText = pgNum.matcher(pageStartText).replaceFirst("");

                /*
                 * Matcher mwords = hundredWords.matcher(pageStartText); boolean mfound = mwords.find(); pageStartText = (mfound) ? mwords.group() :
                 * pageStartText; log.debug(mfound + " (" + mwords.groupCount() + ") <== " + pageStartText);
                 */

                // populate page specific fields
                rec.put(EVCombinedRec.PAGE, pageNum);
                rec.put(EVCombinedRec.LOAD_NUMBER, loadNumber);

                // full text and enclosing chapter start page
                rec.put(EVCombinedRec.ABSTRACT, pageStartText);
                rec.put(EVCombinedRec.STARTPAGE, pageChapterStartPage);

                // CHANGE - Put beinging of Page in Title field
                // new FL field, CPX Thes applied to page text
                List<String> fls = convertString2List(freeLang, "\\|");
                String arrFl[] = (String[]) fls.toArray(new String[0]);
                rec.put(EVCombinedRec.UNCONTROLLED_TERMS, arrFl);

                // CHANGE: remove subject terms from Page Record
                rec.put(EVCombinedRec.CONTROLLED_TERMS, StringUtil.EMPTY_STRING);
                // CHANGE: remove book title from title field for from Page Record
                rec.put(EVCombinedRec.TITLE, StringUtil.EMPTY_STRING);

                // ISBN Navigator
                // rec.put(EVCombinedRec.UNCONTROLLED_TERMS, sIsbn);

                String cleanedTitle = Entity.prepareString(bookTitle.toLowerCase());
                String b64Title = "B64" + Base64Coder.encode(cleanedTitle);
                rec.put(EVCombinedRec.SERIAL_TITLE, b64Title);
                // rec.put(EVCombinedRec.SERIAL_TITLE, cleanedTitle);

                String cleanedChapterTitle = Entity.prepareString(pageChapterTitle.toLowerCase());
                // 'BS' - COUNTRY Base 64 Encoded Chapter Title with ISBN
                String b64ChapterTitle = "B64" + Base64Coder.encode(cleanedChapterTitle + ", " + sIsbn13);
                rec.put(EVCombinedRec.COUNTRY, b64ChapterTitle);
                // rec.put(EVCombinedRec.COUNTRY, cleanedChapterTitle + ", " + sIsbn13);

            } // try
            catch (Exception e) {
                log.error("Error in populatePageRecord!");
                throw e;
            }
            return rec;

        }
    } // inner class PageRecord

}
