package org.ei.common.georef;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.data.CITEDBY;
import org.ei.common.DataCleaner;
import org.ei.domain.Affiliation;
import org.ei.domain.Affiliations;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ElementDataMap;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RectangleCoordinates;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;
import org.ei.common.Constants;

/* =========================================================================
 *
 * ========================================================================= */
public abstract class DocumentView {

    public abstract String getFormat();

    public abstract List getFields();

    public abstract boolean exportLabels();

    public abstract Map<Key, String> getKeyMap();

    private ResultSet      rset = null;
    private ElementDataMap ht   = null;
    private Perl5Util      perl = new Perl5Util();

    public Key[] getKeys() {
        return (Key[]) (getKeyMap().keySet()).toArray(new Key[] {});
    }

    public void setResultSet(ResultSet anrset) {
        this.rset = anrset;
    }

    public EIDoc buildDocument(ResultSet rset, DocID did) throws SQLException  {
        this.rset = rset;
        this.ht = new ElementDataMap();

        ht.put(Keys.DOCID, did);

        addDocumentValue(Keys.PROVIDER, Constants.PROVIDER_TEXT);
        addDocumentValue(Keys.COPYRIGHT, Constants.GRF_HTML_COPYRIGHT);
        addDocumentValue(Keys.COPYRIGHT_TEXT, Constants.GRF_TEXT_COPYRIGHT);

        addDocumentValue(Keys.TITLE, getTitle());
        addDocumentValue(Keys.TITLE_TRANSLATION, getTranslatedTitle());
        addDocumentValue(Keys.MONOGRAPH_TITLE, getMonographTitle());
        addDocumentValue(Keys.VOLISSUE, getVolIssue());
        addDocumentValue(Keys.ABSTRACT, getAbstract());

        // AUS
        Contributors contributors = null;
        Key keyAffiliation = null;

        String strperson_analytic = createColumnValueField("PERSON_ANALYTIC").getValue();
        if (strperson_analytic != null && !strperson_analytic.equalsIgnoreCase("Anonymous")) {
            Contributors authors = new Contributors(Keys.AUTHORS, getContributors(strperson_analytic, Keys.AUTHORS));
            if (authors != null) {
                ht.put(Keys.AUTHORS, authors);
                contributors = authors;
            }
            // if there is an AUTHOR_AFFILIATION
            // then it will be a PERSON_ANALYTIC affiliation since PERSON_ANALYTIC is not empty
            keyAffiliation = Keys.AUTHOR_AFFS;
        }

        // EDS
        String strperson_monograph = createColumnValueField("PERSON_MONOGRAPH").getValue();
        if (strperson_monograph != null && !strperson_monograph.equalsIgnoreCase("Anonymous")) {
            // Editors of the Collection/Series
            // String strperson_collection = createColumnValueField("PERSON_COLLECTION").getValue();
            // if(strperson_collection != null)
            // {
            // strperson_monograph = strperson_monograph.concat(GRFDocBuilder.AUDELIMITER).concat(strperson_collection);
            // }

            Contributors editors = new Contributors(Keys.EDITORS, getContributors(strperson_monograph, Keys.EDITORS));
            if (editors != null) {
                ht.put(Keys.EDITORS, editors);
            }
            // if there was no PERSON_ANALYTIC, if there is an AUTHOR_AFFILIATION,
            // then it is the PERSON_MONOGRAPH affiliation since PERSON_MONOGRAPH is not empty and PERSON_ANALYTIC was
            // empty
            if (keyAffiliation == null) {
                keyAffiliation = Keys.EDITOR_AFFS;
                contributors = editors;
            }
        }

        // AFS/EFS
        String straffl = createColumnValueField("AUTHOR_AFFILIATION").getValue();
        if (straffl != null) {
            String straddr = createColumnValueField("AUTHOR_AFFILIATION_ADDRESS").getValue();
            if (straddr != null) {
                straffl = straffl.concat(", ").concat(straddr);
            }
            String strcountry = new CountryDecorator(createColumnValueField("AUTHOR_AFFILIATION_COUNTRY")).getValue();
            if (strcountry != null) {
                straffl = straffl.concat(", ").concat(strcountry);
            }
            Affiliation affil = new Affiliation(keyAffiliation, straffl);
            ht.put(keyAffiliation, new Affiliations(keyAffiliation, affil));
            if (contributors != null) {
                contributors.setFirstAffiliation(affil);
            }
        }

        addDocumentValue(Keys.VOLUME, createColumnValueField("VOLUME_ID"), new Volume(StringUtil.EMPTY_STRING, perl));
        addDocumentValue(Keys.ISSUE, createColumnValueField("ISSUE_ID"), new Issue(StringUtil.EMPTY_STRING, perl));
        // Change Key INSIDE ISBN object in order to change Label
        ElementData isbn = new ISBN(StringUtil.EMPTY_STRING);
        isbn.setKey(Keys.MULTI_ISBN);
        // Include value under alternate ISBN for the View so we can display/link multiple ISBNs
        addDocumentValue(Keys.MULTI_ISBN, createColumnValueField("ISBN"), isbn);

        // Include value under ISBN key for linking - but this will NOT be exported to the View
        // So we bypass the addDocumentValue(0 checks and just add it directly to the document map
        String fieldvalue = createColumnValueField("ISBN").getValue();
        if (fieldvalue != null) {
            String[] multivalues = fieldvalue.split(Constants.AUDELIMITER);
            if ((multivalues != null) && (multivalues.length >= 1)) {
                ht.put(Keys.ISBN, new ISBN(multivalues[0]));
            }
        }

        addDocumentValue(Keys.ISSN, new IssnDecorator(createColumnValueField("ISSN")), new ISSN(StringUtil.EMPTY_STRING));
        // Change Key INSIDE ISSN object in order to change Label
        ElementData eissn = new ISSN(StringUtil.EMPTY_STRING);
        eissn.setKey(Keys.E_ISSN);
        addDocumentValue(Keys.E_ISSN, new IssnDecorator(createColumnValueField("EISSN")), eissn);

        addDocumentValue(Keys.PAGE_RANGE, new SimpleValueField(getPages()), new PageRange(StringUtil.EMPTY_STRING, perl));
        addDocumentValue(Keys.PUBLICATION_YEAR, new SimpleValueField(getYear()), new Year(StringUtil.EMPTY_STRING, perl));

        // LA is included when not null
        addDocumentValue(Keys.LANGUAGE, new SimpleValueField(getLanguage()));

        // INDEX_TERMS (CVS)
        if (isIncluded(Keys.INDEX_TERM)) {
            String stridxtrms = createColumnValueField("INDEX_TERMS").getValue();
            if (stridxtrms != null) {
                String[] idxterms = stridxtrms.split(Constants.AUDELIMITER);
                for (int i = 0; i < idxterms.length; i++) {
                    idxterms[i] = idxterms[i].replaceAll("[A-Z]*" + Constants.IDDELIMITER, StringUtil.EMPTY_STRING);
                }
                ht.put(Keys.INDEX_TERM, new XMLMultiWrapper(Keys.INDEX_TERM, idxterms));
            }
        }

        // UNCONTROLLED_TERMS (FLS)
        if (isIncluded(Keys.UNCONTROLLED_TERMS)) {
            String stridxtrms = createColumnValueField("UNCONTROLLED_TERMS").getValue();
            if (stridxtrms != null) {
                ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, stridxtrms.split(Constants.AUDELIMITER)));
            }
        }
        // SPONSOR
        if (isIncluded(Keys.RSRCH_SPONSOR)) {
            String strsponsor = createColumnValueField("CORPORATE_BODY_ANALYTIC").getValue();
            if (strsponsor == null) {
                strsponsor = createColumnValueField("CORPORATE_BODY_MONOGRAPH").getValue();
            }
            if (strsponsor != null) {
                addDocumentValue(Keys.RSRCH_SPONSOR, new SimpleValueField(strsponsor));
            }
        }

        // COORDINATES
        if (isIncluded(Keys.LOCATIONS)) {
            // Latitude. Latitude indicates the number of degrees north or south of the equator (0 degrees). Latitude is
            // indicated by a character string of seven characters: a letter N or S to indicate direction (i.e., north
            // or south of the equator) and a six-digit number indicating the number of degrees, minutes, and seconds.
            // Latitude coordinates are also cascaded to the full degree (i.e., first two digits): N451430 is North, 45
            // degrees, 14 minutes, 30 seconds; it is also cascaded to N45.
            // Longitude. Longitude indicates the number of degrees east or west of the prime meridian (0 degrees).
            // Longitude is indicated by a character string of eight characters: a letter E or W to indicate direction
            // (i.e., east or west of the prime meridian) and a seven-digit number indicating the number of degrees,
            // minutes, and seconds. Longitude coordinates are also cascaded to the full degree (i.e., first three
            // digits): W1211752 is West, 121 degrees, 17 minutes, 52 seconds; it is also cascaded to W121
            // Coordinates are assigned as follows: Starting from the lower right-hand corner, a latitude is assigned,
            // followed by the latitude of the upper right-hand corner (counterclockwise), the longitude of that point,
            // and finally the longitude of the upper left-hand corner.
            // i.e. N174800N180000W0661000W0664000
            // S230000S100000E1530000E1430000

            String strcoordinates = createColumnValueField("COORDINATES").getValue();
            if (strcoordinates != null) {
                ht.put(Keys.LOCATIONS, new RectangleCoordinates(strcoordinates.split(Constants.AUDELIMITER)));
            }
        }

        if (isIncluded(Keys.AVAILABILITY)) {
            String stravail = createColumnValueField("AVAILABILITY").getValue();
            if (stravail != null) {
                ht.put(Keys.AVAILABILITY, new XMLMultiWrapper(Keys.AVAILABILITY, stravail.split(Constants.AUDELIMITER)));
            }
        }

        addDocumentValue(Keys.CONF_DATE, new ConferenceDateDecorator(createColumnValueField("DATE_OF_MEETING")));
        // this is a (possible) multi-field that will be split on the AUDELIMITER in addDocumentValue() method
        addDocumentValue(Keys.CONFERENCE_NAME, createColumnValueField("NAME_OF_MEETING"));
        addDocumentValue(Keys.MEETING_LOCATION, createColumnValueField("LOCATION_OF_MEETING"));

        addDocumentValue(Keys.CODEN, createColumnValueField("CODEN"));

        addDocumentValue(Keys.SOURCE, createColumnValueField("TITLE_OF_SERIAL"));
        if (createColumnValueField("TITLE_OF_SERIAL").getValue() == null) {
            // Here if there is no Source - pick the firat value from the availability multi-field
            // if it exists
            String stravail = createColumnValueField("AVAILABILITY").getValue();
            if (stravail != null) {
                String firstAvail = stravail.split(Constants.AUDELIMITER)[0];
                addDocumentValue(Keys.SOURCE, firstAvail);
            } else {
                addDocumentValue(Keys.NO_SO, "NO SOURCE");
            }
        }

        addDocumentValue(Keys.SERIAL_TITLE, createColumnValueField("TITLE_OF_SERIAL"));
        addDocumentValue(Keys.COPYRIGHT, createColumnValueField("COPYRIGHT"));
        addDocumentValue(Keys.ACCESSION_NUMBER, createColumnValueField("ID_NUMBER"));
        addDocumentValue(Keys.NUMBER_OF_REFERENCES, createColumnValueField("NUMBER_OF_REFERENCES"));
        addDocumentValue(Keys.REPORT_NUMBER, createColumnValueField("REPORT_NUMBER"));
        addDocumentValue(Keys.DOI, createColumnValueField("DOI"));
        addDocumentValue(Keys.EDITION, createColumnValueField("EDITION"));

        addDocumentValue(Keys.SOURCE_MEDIUM, createColumnValueField("MEDIUM_OF_SOURCE"));
        addDocumentValue(Keys.SOURCE_NOTE, createColumnValueField("SOURCE_NOTE"));
        addDocumentValue(Keys.SUMMARY_ONLY_NOTE, createColumnValueField("SUMMARY_ONLY_NOTE"));

        addDocumentValue(Keys.UNIVERSITY, createColumnValueField("UNIVERSITY"));
        addDocumentValue(Keys.DEGREE_TYPE, createColumnValueField("TYPE_OF_DEGREE"));
        addDocumentValue(Keys.RESEARCH_PROGRAM, createColumnValueField("RESEARCH_PROGRAM"));
        addDocumentValue(Keys.ILLUSTRATION, createColumnValueField("ILLUSTRATION"));
        addDocumentValue(Keys.ANNOTATION, createColumnValueField("ANNOTATION"));
        addDocumentValue(Keys.MAP_SCALE, createColumnValueField("MAP_SCALE"));
        addDocumentValue(Keys.MAP_TYPE, createColumnValueField("MAP_TYPE"));
        addDocumentValue(Keys.HOLDING_LIBRARY, createColumnValueField("HOLDING_LIBRARY"));
        addDocumentValue(Keys.TARGET_AUDIENCE, createColumnValueField("TARGET_AUDIENCE"));

        addDocumentValue(Keys.COUNTRY_OF_PUB, new CountryDecorator(createColumnValueField("COUNTRY_OF_PUBLICATION")));
        addDocumentValue(Keys.COLLECTION_TITLE, new TitleDecorator(createColumnValueField("TITLE_OF_COLLECTION")));
        addDocumentValue(Keys.ABSTRACT_TYPE, new BibliographicLevelDecorator(createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE")));

        // this is a (possible) multi-field that will be split on the AUDELIMITER in addDocumentValue() method
        addDocumentValue(Keys.PUBLISHER, new SimpleValueField(getPublisher()));
        addDocumentValue(Keys.AFFILIATION_OTHER, new AffiliationDecorator(createColumnValueField("AFFILIATION_SECONDARY")));

        addDocumentValue(Keys.GRF_URLS, new URLDecorator(createColumnValueField("URL")));

        // New Doctype is shown as combination of GeoRef Doctypes and Ei Doctype codes in '()' i.e. Serial (JA),
        // Conference document (CA)
        // addDocumentValue(Keys.DOC_TYPE, new DocumentTypeDecorator(createColumnValueField("DOCUMENT_TYPE")));
       
        addDocumentValue(Keys.DOC_TYPE, getDocumentTypes());

        addDocumentValue(Keys.CATEGORY, new CategoryDecorator(createColumnValueField("CATEGORY_CODE")));

        addCitedBy(rset);

        EIDoc eiDoc = new EIDoc(did, ht, getFormat());
        eiDoc.exportLabels(exportLabels());
        eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
        eiDoc.setOutputKeys(getKeys());

        return eiDoc;
    }

    private void addCitedBy(ResultSet rset) throws SQLException{

    	if(isIncluded(Keys.CITEDBY)){
    		CITEDBY citedby = new CITEDBY();
            citedby.setKey(Keys.CITEDBY);

            if (rset.getString("DOI") != null) {
                try {
    				citedby.setDoi(URLEncoder.encode((rset.getString("DOI")).trim(), "UTF-8"));
    			} catch (UnsupportedEncodingException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            if (rset.getString("VOLUME_ID") != null) {
                citedby.setVolume((rset.getString("VOLUME_ID")).trim());
            }

            if (rset.getString("ISSUE_ID") != null) {
                citedby.setIssue((rset.getString("ISSUE_ID")).trim());
            }

            if (rset.getString("ISSN") != null) {
                citedby.setIssn((rset.getString("ISSN")).trim());
            }

            if (rset.getString("ISBN") != null) {
            	String[] multivalues = rset.getString("ISBN").split(Constants.AUDELIMITER);
            	if(multivalues != null && multivalues.length>0){
            		citedby.setIsbn((multivalues[0]).trim());
            	}
            }

            String pages  = getPages();

            if ( pages != null) {
            	citedby.setPage(pages.trim());
            }

            if (rset.getString("ID_NUMBER") != null) {
                citedby.setAccessionNumber(rset.getString("ID_NUMBER"));
            }
            putElementData(Keys.CITEDBY, citedby);
    	}
    }


    public String toString() {
        return getFormat() + "\n == \n" + getQuery();
    }

    public String getQuery() {
        return "SELECT " + StringUtil.join(getFields(), ",") + " FROM GEOREF_MASTER WHERE M_ID IN ";
    }

    public boolean isIncluded(Key documentkey) {
        return getKeyMap().containsKey(documentkey);
    }

    /*
     * "Factory" prevents having to pass around ResultSet when creating objects
     */
    public DocumentField createColumnValueField(String columnname) {
        ResultsSetField rsfield = new ColumnValueField(columnname);
        rsfield.setResultSet(rset);

        return rsfield;
    }

    /*
     * addDocumentValue wrappers for simple key and String value
     */
    public void addDocumentValue(Key key, String strfieldvalue)  {
        addDocumentValue(key, new SimpleValueField(strfieldvalue));
    }

    /*
     * addDocumentValue wrappers for simple key DocumentField value
     */
    public void addDocumentValue(Key key, DocumentField field) {
        addDocumentValue(key, field, new XMLWrapper(key, StringUtil.EMPTY_STRING));
    }

    /*
     * addDocumentValue checks to see if a field is included before calling getValue()!!! addDocumentValue ALWAYS calls
     * split on the field value, therefore turning simple concatenated strings into multiple values
     */
    public void addDocumentValue(Key key, DocumentField field, ElementData data) {
    	
        if (isIncluded(key)) {
            String fieldvalue = field.getValue();
            if (fieldvalue != null) {
                // Is this a hack ? - Tf there IS more than one value in the field string,
                // change the data element to a XMLMultiWrapper
                String[] multivalues = fieldvalue.split(Constants.AUDELIMITER);
                if (multivalues.length > 1) {
                    Key elementDataKey = data.getKey();
                    data = new XMLMultiWrapper(elementDataKey, new String[] {});
                }
                data.setElementData(multivalues);
                putElementData(key, data);
            }
        }
    }

    private void putElementData(Key key, ElementData data) {
        if (data != null) {
            ht.put(key, data);
        }
    }


    /*
     * ========================================================================= Virtual Fields that require conditions
     * for determining their values =========================================================================
     */
    private List<Contributor> getContributors(String strAuthors, Key key) {

        List<Contributor> list = new ArrayList<Contributor>();
        try {
            if (strAuthors != null) {
                DataCleaner dataCleaner = new DataCleaner();
                strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
                String[] aus = strAuthors.split(Constants.AUDELIMITER);
                for (int auindex = 0; auindex < aus.length; auindex++) {
                    list.add(new Contributor(key, aus[auindex]));
                }
            }
        } catch (Exception e) {
            System.out.println("getContributors() Exception: " + e.getMessage());
        } finally {
        }
        return list;
    }

    public String getTitleOFCOLLECTION() {
        DocumentField afield = new TitleDecorator(createColumnValueField("TITLE_OF_COLLECTION"));
        String strvalue = afield.getValue();
        return strvalue;
    }
    
    public String getTitle() {
        DocumentField afield = new TitleDecorator(createColumnValueField("TITLE_OF_ANALYTIC"));
        String strvalue = afield.getValue();
        if (strvalue == null) {
            afield = new TitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
            strvalue = afield.getValue();
        }
        return strvalue;
    }

    public String getTranslatedTitle() {
        DocumentField afield = new TranslatedTitleDecorator(createColumnValueField("TITLE_OF_ANALYTIC"));
        String strvalue = afield.getValue();
        if (strvalue == null) {
            afield = new TranslatedTitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
            strvalue = afield.getValue();
        }
        if (strvalue != null) {
            if (strvalue.equals(getTitle())) {
                strvalue = null;
            }
        }
        return strvalue;
    }

    public String getMonographTitle() {
        DocumentField afield = new TitleDecorator(createColumnValueField("TITLE_OF_MONOGRAPH"));
        String strvalue = afield.getValue();
        if (strvalue != null) {
            if (strvalue.equals(getTitle())) {
                strvalue = null;
            }
        }
        return strvalue;
    }

    public String getPublisher() {
        String strvalue = null;
        if (isIncluded(Keys.PUBLISHER)) {
            strvalue = createColumnValueField("PUBLISHER").getValue();
            String strvalueaddr = createColumnValueField("PUBLISHER_ADDRESS").getValue();

            if ((strvalue != null) && (strvalueaddr != null)) {
                strvalue = strvalue.concat(", ").concat(strvalueaddr);
            }
        }
        return strvalue;
    }

    private String getVolIssue() {
        String strvalue = null;
        if (isIncluded(Keys.VOLISSUE)) {
            List<String> lstvalues = new ArrayList<String>();
            strvalue = createColumnValueField("VOLUME_ID").getValue();
            if (strvalue != null) {
                lstvalues.add(strvalue);
                strvalue = null;
            }
            strvalue = createColumnValueField("ISSUE_ID").getValue();
            if (strvalue != null) {
                lstvalues.add(strvalue);
            }
            if (!lstvalues.isEmpty()) {
                strvalue = StringUtil.join(lstvalues, ", ");
            }
        }
        return strvalue;
    }

    private String getAbstract() {
        String strvalue = null;
        if (isIncluded(Keys.ABSTRACT)) {
            try {
                Clob clob = rset.getClob("ABSTRACT");
                if (clob != null) {
                    strvalue = StringUtil.getStringFromClob(clob);
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (strvalue == null || strvalue.length() < Constants.MINIMUM_ABSTRACT_LENGTH) ? null : strvalue;
    }

    public String getLanguage() {
        return new LanguageDecorator(createColumnValueField("LANGUAGE_TEXT")).getValue();
    }

    public String getPages() {
        String strvalue = null;
        DocumentField pagesAnalytic = createColumnValueField("COLLATION_ANALYTIC");
        strvalue = pagesAnalytic.getValue();
        if (strvalue == null) {
            DocumentField pagesMono = createColumnValueField("COLLATION_MONOGRAPH");
            DocumentField titleMono = createColumnValueField("TITLE_OF_MONOGRAPH");
            // if we are using the monograph title
            if ((titleMono.getValue() != null) && (pagesMono.getValue() != null)) {
                strvalue = pagesMono.getValue();
            }
            // settle on the collation field for the collection if it exists
            if (strvalue == null) {
                DocumentField pagesColl = createColumnValueField("COLLATION_COLLECTION");
                if (pagesColl.getValue() != null) {
                    strvalue = pagesMono.getValue();
                }
            }
        }
        return strvalue;
    }

    public String getYear() {
        String strvalue = null;
        DocumentField yearPub = createColumnValueField("DATE_OF_PUBLICATION");
        String strdate = yearPub.getValue();
        if (strdate == null) {
            DocumentField yearMeeting = createColumnValueField("DATE_OF_MEETING");
            // if there is no pubdate, check if there is a conference date
            strdate = yearMeeting.getValue();
        }
        if (strdate != null) {
            Matcher yearmatch = Pattern.compile("^(\\d{4})").matcher(strdate);
            if (yearmatch.find()) {
                strvalue = yearmatch.group(1);
            }
        }
        return strvalue;
    }

    public String getDocumentTypes() {

        String strvalue = null;

        if (isIncluded(Keys.DOC_TYPE)) {
            String doctype = createColumnValueField("DOCUMENT_TYPE").getValue();
            String bibcode = createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue();
           
            if ((doctype != null) && (bibcode != null)) {
                String mappingcode = doctype.concat(Constants.AUDELIMITER).concat(bibcode);
                if (mappingcode != null) {
                	
                    mappingcode = new DocumentTypeMappingDecorator(mappingcode).getValue();
                    
                }

                String dts = (new DocumentTypeDecorator(createColumnValueField("DOCUMENT_TYPE"))).getValue();
                
                String[] dtArray = dts.split(Constants.AUDELIMITER);              
                
                String[] mapcodeArray = mappingcode.split(Constants.AUDELIMITER);
                
                if (dtArray.length == mapcodeArray.length) {
                    List<String> decoratedvalues = new ArrayList<String>();
                    for (int x = 0; x < dtArray.length; x++) {
                        decoratedvalues.add(dtArray[x] + (mapcodeArray[x].equals("") ? "" : " (".concat(mapcodeArray[x]).concat(")")));
                    }
                    strvalue = StringUtil.join(decoratedvalues, Constants.AUDELIMITER);
                }
            }
        }
        
        return strvalue;
    }

    /*
     * ========================================================================= Nested Inner classes (@see
     * http://java.sun.com/docs/books/tutorial/java/javaOO/nested.html) These classes are public and are accessible
     * through an instance of the DocumentView class. i.e. DocumentView runtimeDocview = new CitationView();
     * DocumentView.FieldDecorator cd = runtimeDocview.new CountryDecorator(country);
     *
     * =========================================================================
     */


    /*
     * ========================================================================= DocumentField classes
     * =========================================================================
     */
    public abstract class DocumentField {
        public abstract String getValue();
    }

    /*
     * ========================================================================= This is a simple field for wrapping
     * strings so getValue() can be caled on them in the addDocument() methods
     * =========================================================================
     */
    public class SimpleValueField extends DocumentField {
        private String value = null;

        public SimpleValueField(String strvalue) {
            this.value = strvalue;
        }

        public String getValue() {
            return this.value;
        }
    }

    /*
     * ========================================================================= This is for wrapping strings ResultSet
     * fields =========================================================================
     */
    public abstract class ResultsSetField extends DocumentField {
        protected ResultSet rs = null;

        public void setResultSet(ResultSet rs) {
            this.rs = rs;
        }

        public String getValue() {
            String strvalue = null;
            try {
                if (rs != null) {
                    strvalue = rs.getString(getColumn());
                } else {
                    System.out.println("Skipping column  " + getColumn() + " RS is null");
                }
            } catch (SQLException sqle) {
                // sqle.printStackTrace();
                System.out.println("getValue() error.  Possible missing or misspelled column name " + getColumn() + ".");
            }
            return strvalue;
        }

        public abstract String getColumn();
    }

    /*
     * ========================================================================= This is a simple field for wrapping
     * values which are store as strings in a result set - this is mainly for a delayed load so we can check whether or
     * not they are part of the document before calling getValue()
     * =========================================================================
     */
    public class ColumnValueField extends ResultsSetField {
        private String m_column = null;

        public ColumnValueField(String column) {
            this.m_column = column;
        }

        public String getColumn() {
            return this.m_column;
        }
    }

    /*
     * ========================================================================= Field Decorators
     * =========================================================================
     */
    public abstract class FieldDecorator extends DocumentField {
        protected Map<String, String> tokenize(String field) {
            Map<String, String> multivalues = new HashMap<String, String>();
            if (field != null) {
                String[] strpairs = field.split(Constants.AUDELIMITER);
                for (int i = 0; i < strpairs.length; i++) {
                    String[] strnamevalue = strpairs[i].split(Constants.IDDELIMITER);
                    if (strnamevalue.length == 2) {
                        multivalues.put(strnamevalue[0], strnamevalue[1]);
                    }
                }
            }
            return multivalues;
        }
    }

    public static Pattern patternYYYYMMDD = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");

    public class ConferenceDateDecorator extends FieldDecorator {
        private DocumentField field;

        public ConferenceDateDecorator(DocumentField field) {
            this.field = field;
        }

        public String getValue() {
            String strdate = null;
            String strconfdate = field.getValue();
            if (strconfdate != null) {
                Matcher yearmatch = DocumentView.patternYYYYMMDD.matcher(strconfdate);
                if (yearmatch.find()) {
                    strdate = yearmatch.group(2) + "/" + yearmatch.group(3) + "/" + yearmatch.group(1);
                }
            }
            return strdate;
        }
    }

    public class AffiliationDecorator extends FieldDecorator {
        private DocumentField field;

        public AffiliationDecorator(DocumentField field) {
            this.field = field;
        }

        public String getValue() {
            String straffiliations = field.getValue();
            if (straffiliations != null) {
                straffiliations = straffiliations.replaceAll(Constants.IDDELIMITER, ", ");
            }
            return straffiliations;
        }
    }

    public class IssnDecorator extends FieldDecorator {
        protected DocumentField field;

        public IssnDecorator(DocumentField field) {
            this.field = field;
        }

        public String getValue() {
            String strissn = null;
            String strvalue = field.getValue();
            if (strvalue != null) {
                String[] strvalues = strvalue.split(Constants.IDDELIMITER);
                if (strvalues != null && strvalues.length > 1) {
                    strissn = strvalues[0];
                } else {
                    strissn = strvalue;
                }
            }
            return strissn;
        }
    }

    // O Original title, i.e. the title, if any, as given on the document entered in the original language and alphabet
    // M Original title in original language and alphabet, but modified or enriched in content as part of the
    // cataloguing process
    // L Original title transliterated or transcribed as part of the cataloging process. Used only for Cyrillic alphabet
    // in GeoRef.
    // T Original title translated (with or without modification of content) as part of the cataloging process

    public class TitleDecorator extends FieldDecorator {
        protected DocumentField field;

        public TitleDecorator(DocumentField field) {
            this.field = field;
        }

        public TitleDecorator(String stringvalue) {
            this(new SimpleValueField(stringvalue));
        }

        public String getValue() {
            String strtitle = null;
            String strvalue = field.getValue();
            Map<String,String> mapvalues = tokenize(strvalue);
            if (mapvalues.containsKey("T")) {
                strtitle = (String) mapvalues.get("T");
            } else if ((strtitle == null) && mapvalues.containsKey("O")) {
                strtitle = (String) mapvalues.get("O");
            } else if ((strtitle == null) && mapvalues.containsKey("M")) {
                strtitle = (String) mapvalues.get("M");
            } else if ((strtitle == null) && mapvalues.containsKey("L")) {
                strtitle = (String) mapvalues.get("L");
            }
            return strtitle;
        }
    }

    public class TranslatedTitleDecorator extends FieldDecorator {
        protected DocumentField field;

        public TranslatedTitleDecorator(DocumentField field) {
            this.field = field;
        }

        public TranslatedTitleDecorator(String stringvalue) {
            this(new SimpleValueField(stringvalue));
        }

        public String getValue() {
            String strtitle = null;
            String strvalue = field.getValue();
            Map<String, String> mapvalues = tokenize(strvalue);
            if (mapvalues.containsKey("O")) {
                strtitle = (String) mapvalues.get("O");
            } else if ((strtitle == null) && mapvalues.containsKey("L")) {
                strtitle = (String) mapvalues.get("L");
            }
            return strtitle;
        }
    }

    public class CitationAbstractLanguageDecorator extends FieldDecorator {
        protected DocumentField field;

        public CitationAbstractLanguageDecorator(DocumentField field) {
            this.field = field;
        }

        public CitationAbstractLanguageDecorator(String stringvalue) {
            this(new SimpleValueField(stringvalue));
        }

        public String getValue() {
            String strvalue = null;
            String strlang = field.getValue();
            if (strlang != null) {
                String[] strlangs = strlang.split(Constants.AUDELIMITER);
                List<String> lstvalues = new ArrayList<String>();
                if (strlangs != null) {
                    for (int i = 0; i < strlangs.length; i++) {
                        if (!strlangs[i].equalsIgnoreCase("English") && !strlangs[i].equalsIgnoreCase("EL")) {
                            lstvalues.add(strlangs[i]);
                        }
                    }
                }
                if (!lstvalues.isEmpty()) {
                    strvalue = StringUtil.join(lstvalues, Constants.AUDELIMITER);
                }
            }
            return strvalue;
        }
    }

    /*
     * Translation Decorators
     */

    /*
     * This base decorator will take a code decorate it by translating it into a readable string. The code is a String
     * key which is looked up in a Map
     */
    public abstract class LookupValueDecorator extends FieldDecorator {
        protected GRFDataDictionary dataDictionary = GRFDataDictionary.getInstance();

        protected DocumentField     field;

        public LookupValueDecorator(DocumentField field) {
            this.field = field;
        }

        public String getValue() {
            String decoratedvalue = null;
            String strvalue = field.getValue();
            if (strvalue != null) {
                String strtranslated = dataDictionary.translateValue(strvalue, getLookupTable());
                if (strtranslated != null) {
                    decoratedvalue = strtranslated;
                }
            }
            return decoratedvalue;
        }

        public abstract Map<String, String> getLookupTable();
    }

    public class CountryDecorator extends LookupValueDecorator {
        public CountryDecorator(DocumentField field) {
            super(field);
        }

        public CountryDecorator(String stringvalue) {
            super(new SimpleValueField(stringvalue));
        }

        public Map<String, String> getLookupTable() {
            return dataDictionary.getCountries();
        }

        public String getValue() {
            String decoratedvalue = super.getValue();
            if (decoratedvalue == null) {
                decoratedvalue = field.getValue();
            }
            return decoratedvalue;
        }
    }

    public class BibliographicLevelDecorator extends LookupValueDecorator {
        public BibliographicLevelDecorator(DocumentField field) {
            super(field);
        }

        public Map<String, String> getLookupTable() {
            return dataDictionary.getBibliographiccodes();
        }
    }

    /*
     * This decorator extends the LookupValueDecorator by splitting the code into multiple values, based on a split
     * expression and then conctenating each decorated value into a single string, using a concatenationstring to join
     * the decorated values
     */

    public static Pattern patternAUDELIMITER  = Pattern.compile(Constants.AUDELIMITER);
    public static Pattern patternEMPTY_STRING = Pattern.compile(StringUtil.EMPTY_STRING);

    public abstract class MultiValueLookupValueDecorator extends LookupValueDecorator {
        public MultiValueLookupValueDecorator(DocumentField field) {
            super(field);
        }

        public String getValue() {
            List<String> decoratedvalues = new ArrayList<String>();
            String strvalue = field.getValue();

            if (strvalue != null) {
                String[] codes = new String[] { strvalue };
                if (getSplitPattern().matcher(strvalue).find()) {
                    codes = getSplitPattern().split(strvalue);
                }
                for (int i = 0; i < codes.length; i++) {
                    String strtranslated = dataDictionary.translateValue(codes[i], getLookupTable());
                    if (strtranslated != null) {
                        decoratedvalues.add(strtranslated);
                    }
                }
            }
            if (!decoratedvalues.isEmpty()) {
                strvalue = StringUtil.join(decoratedvalues, Constants.AUDELIMITER);
            }
            return strvalue;
        }

        public abstract Pattern getSplitPattern();
    }

    public class CategoryDecorator extends MultiValueLookupValueDecorator {
        public CategoryDecorator(DocumentField field) {
            super(field);
        }

        public Pattern getSplitPattern() {
            return patternAUDELIMITER;
        }

        public Map<String, String> getLookupTable() {
            return dataDictionary.getCategories();
        }
    }

    public class LanguageDecorator extends MultiValueLookupValueDecorator {
        public LanguageDecorator(DocumentField field) {
            super(field);
        }

        public Pattern getSplitPattern() {
            return patternAUDELIMITER;
        }

        public Map<String, String> getLookupTable() {
            return dataDictionary.getLanguages();
        }
    }

    public class DocumentTypeDecorator extends MultiValueLookupValueDecorator {
        public DocumentTypeDecorator(DocumentField field) {
            super(field);
        }

        // empty pattern will split string into single characters
        public Pattern getSplitPattern() {
            return patternEMPTY_STRING;
        }

        public Map<String, String> getLookupTable() {
            return dataDictionary.getDocumenttypes();
        }
    }

    public class DocumentTypeMappingDecorator extends MultiValueLookupValueDecorator {
        public DocumentTypeMappingDecorator(DocumentField field) {
            super(field);
        }

        public DocumentTypeMappingDecorator(String stringvalue) {
            super(new SimpleValueField(stringvalue));
        }

        public String getValue() {
            List<String> decoratedvalues = new ArrayList<String>();
            String strvalue = field.getValue();

            // passed in value is <DOCTYPE>AUDELIMITER<BIBOCDE>
            String[] mapcodes = strvalue.split(Constants.AUDELIMITER);
            if ((mapcodes != null) && (mapcodes.length == 2)) {
                String doctypes = mapcodes[0];
                String bibcode = mapcodes[1];          
                // doctypes are repaeatable but have NO delimieter
                // so split the doctype string into an array of single characters
                if (doctypes != null) {
                    String[] doctypecodes = new String[] { doctypes };
                    if (getSplitPattern().matcher(doctypes).find()) {
                        doctypecodes = getSplitPattern().split(doctypes);
                    }
                    for (int i = 0; i < doctypecodes.length; i++) {
                        // combine each doctype with the bibliographic code to lookup the EV system wide document type
                        String strtranslated = dataDictionary.translateValue(doctypecodes[i].concat(bibcode), getLookupTable());
                        if (strtranslated != null) {
                            decoratedvalues.add(strtranslated);
                        }
                    }
                }
            }
            if (!decoratedvalues.isEmpty()) {
                strvalue = StringUtil.join(decoratedvalues, Constants.AUDELIMITER);
            }
            return strvalue;
        }

        // empty pattern will split string into single characters
        public Pattern getSplitPattern() {
            return Pattern.compile(StringUtil.EMPTY_STRING);
        }

        public Map<String, String> getLookupTable() {
            Map<String, String> mappings = new HashMap<String, String>();

            mappings.put("SA", "JA");
            //channge for book project by hmo at 5/17/2017
            //mappings.put("BA", "CA");
            mappings.put("BA", "BK");
            mappings.put("BC", "BK");
            mappings.put("RA", "RC");
            mappings.put("CA", "CA");
            mappings.put("MA", "MP");
            mappings.put("TA", "DS");

            mappings.put("SM", "MR");
            //change for book project by hmo at 5/17/2017
            //mappings.put("BM", "MR");
            mappings.put("BM", "BK");
            mappings.put("RM", "RR");
            mappings.put("CM", "CP");
            mappings.put("MM", "MP");
            mappings.put("TM", "DS");
            // tmh adding "In Process" to lookup table but leaving "empty" so it doesn't show code!
            mappings.put("GA", "");
            mappings.put("GM", "");
            mappings.put("GC", "");
            mappings.put("GS", "");
            /*
             * // GeoRef Collective Level mappings.put("SC","MR"); mappings.put("BC","MR"); mappings.put("RC","RR");
             * mappings.put("CC","CP"); mappings.put("MC","MP"); mappings.put("TC","DS");
             *
             * // GeoRef Serial Level mappings.put("SS","MR"); mappings.put("BS","MR"); mappings.put("RS","RR");
             * mappings.put("CS","CP"); mappings.put("MS","MP"); mappings.put("TS","DS");
             */
            return mappings;
        }
    }

    public class RisDocumentTypeMappingDecorator extends MultiValueLookupValueDecorator {
        public RisDocumentTypeMappingDecorator(DocumentField field) {
            super(field);
        }

        public RisDocumentTypeMappingDecorator(String stringvalue) {
            super(new SimpleValueField(stringvalue));
        }

        public String getValue() {
            String strvalue = field.getValue();
            List<String> decoratedvalues = new ArrayList<String>();

            // passed in value is <DOCTYPE>AUDELIMITER<DOCTYPE>....
            String[] dtcodes = strvalue.split(Constants.AUDELIMITER);
            if ((dtcodes != null)) {
                String doctype = dtcodes[0];
                String strtranslated = dataDictionary.translateValue(doctype, getLookupTable());
                if (strtranslated != null) {
                    decoratedvalues.add(strtranslated);
                }
            }
            if (!decoratedvalues.isEmpty()) {
                strvalue = StringUtil.join(decoratedvalues, Constants.AUDELIMITER);
            }
            return strvalue;
        }

        // empty pattern will split string into single characters
        public Pattern getSplitPattern() {
            return Pattern.compile(StringUtil.EMPTY_STRING);
        }

        public Map<String, String> getLookupTable() {
            Map<String, String> mappings = new HashMap<String, String>();

            mappings.put("JA", "JOUR");
            mappings.put("GI", "JOUR");
            mappings.put("CA", "CONF");
            mappings.put("CP", "CONF");
            mappings.put("MC", "CHAP");
            mappings.put("MR", "BOOK");
            mappings.put("RC", "RPRT");
            mappings.put("RR", "RPRT");
            mappings.put("DS", "THES");
            mappings.put("UP", "UNPB");
            mappings.put("MP", "MAP");

            return mappings;
        }
    }

    public class URLDecorator extends MultiValueLookupValueDecorator {
        public URLDecorator(DocumentField field) {
            super(field);
        }

        public String getValue() {
            List<String> decoratedvalues = new ArrayList<String>();
            String strvalue = field.getValue();

            if (strvalue != null) {
                String[] urls = new String[] { strvalue };
                if (getSplitPattern().matcher(strvalue).find()) {
                    urls = getSplitPattern().split(strvalue);
                }
                for (int i = 0; i < urls.length; i++) {
                    String[] urlstrings = urls[i].split(",");
                    if ((urlstrings != null) && (urlstrings.length == 2)) {
                        String strtranslated = dataDictionary.translateValue(urlstrings[0], getLookupTable());
                        if (strtranslated != null) {
                            String strurl = ((urlstrings[1].length() > 60) ? (urlstrings[1].substring(0, 59).concat("&#133;")) : (urlstrings[1]));
                            strtranslated = strtranslated
                                .concat("&nbsp<a title=\"External Link\" target=\"_gurl\" class=\"SpLink\" href=\"" + urlstrings[1] + "\">").concat(strurl)
                                .concat("</a>");
                            decoratedvalues.add(strtranslated);
                        }
                    }
                }
            }
            if (!decoratedvalues.isEmpty()) {
                strvalue = StringUtil.join(decoratedvalues, Constants.AUDELIMITER);
            }
            return strvalue;
        }

        public Pattern getSplitPattern() {
            return Pattern.compile(Constants.AUDELIMITER);
        }

        public Map<String, String> getLookupTable() {
            Map<String, String> urltypes = new HashMap<String, String>();

            urltypes.put("F", "Full-text");
            urltypes.put("A", "Availability");
            urltypes.put("P", "Publisher");
            urltypes.put("S", "Series");

            return urltypes;
        }
    }

} // End of DocumentView class


