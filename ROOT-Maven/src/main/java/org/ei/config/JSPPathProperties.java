package org.ei.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
/**
 * This class is used to look up the path to a JSP that will
 * render XML.  It is meant to work in conjunction with the 
 * JSPPath.properties file in the resources source folder in
 * this project.  
 * 
 * Generally the JSPs that render XML are in the /engvillage
 * application.
 * 
 * @author harovetm
 *
 */
public final class JSPPathProperties extends Properties {

	private static final long serialVersionUID = -6603419890282536678L;

	public static final String QUICK_SEARCH_PATH = "quickSearch";
	public static final String EXPERT_SEARCH_PATH = "expertSearch";
	public static final String EBOOK_SEARCH_PATH = "ebookSearch";
	public static final String THES_SEARCH_PATH = "thesSearch";
	public static final String REFERENCE_SERVICES_PATH = "referenceServices";
	public static final String QUICK_SEARCH_RESULTS = "quickSearchResults";
	public static final String EXPERT_SEARCH_RESULTS = "expertSearchResults";
	public static final String DEDUP_SEARCH_RESULTS = "dedupSearchResults";
	public static final String THES_SEARCH_RESULTS = "thesSearchResults";
	public static final String COMBINE_SEARCH_RESULTS = "combineSearchResults";
	public static final String PATENREF_SEARCH_RESULTS = "patenrefSearchResults";
	public static final String OTHERREF_SEARCH_RESULTS = "otherRefSearchResults";
	public static final String TAG_SEARCH_RESULTS = "tagSearchResults";
	public static final String DEDUP_FORM_PATH = "dedupForm";
	public static final String VIEW_SAVED_RECORDS = "viewSavedRecords";
	public static final String DELETE_FROM_SAVED_RECORDS = "deleteFromSavedRecords";
	public static final String DELETE_ALL_FROM_FOLDER = "deleteAllFromFolder";
    public static final String ABSTRACT_DETAILED_RECORD_PATH = "abstractDetailedRecord";
    public static final String PATENT_REF_ABSTRACT_DETAILED_PATH = "patentReferenceAbstractDetail";
	public static final String DEDUP_RECORD_FORWARD_PATH = "dedupRecordForward";
	public static final String TAG_RECORD_FORWARD_PATH = "tagRecordForward";
	public static final String SELECTED_SET = "selectedSet";
	public static final String FORGOT_PASSWORD = "forgotpassword";
    public static final String BOOK_SUMMARY_PATH="bookSummary";
    public static final String OPEN_XML_PATH="openXML";
    public static final String BLOG_THIS_PATH="blogThis";
	
	public JSPPathProperties() {
	}

	public JSPPathProperties(InputStream propsContent) throws IOException {
		loadProperties(propsContent);
	}

	public JSPPathProperties(String propsFile) throws IOException {
		loadProperties(new BufferedInputStream(new FileInputStream(propsFile)));
	}

	public void loadProperties(InputStream inStream) throws IOException {
		try {
			load(new BufferedInputStream(inStream));
		} catch (IOException e) {
			throw new IOException("<" + this.getClass().getName()
					+ ".loadProperties> " + "IOException " + e.getMessage()
					+ "\nError Opening properties from " + inStream);
		}
	}

	public void list(PrintStream out) {
		for (Enumeration<?> e = propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = getProperty(key);
			out.println(key + "=" + val);
			out.println("");
		}
	}

	public void list(PrintWriter out) {
		for (Enumeration<?> e = propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = getProperty(key);
			out.println(key + "=" + val);
			out.println("");
		}
	}

	public synchronized Object setProperty(String key, String value) {
		return put(key, value);
	}

}
