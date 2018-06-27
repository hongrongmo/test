package org.ei.books;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.exception.InfrastructureException;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanQuery;
import org.ei.query.base.AutoStemmer;

/**
 *
 */
public class BookQuery {

	public static final String QUICK_SEARCH = "Q";
	public static final String EXPERT_SEARCH = "E";

	private String searchQuery;
	private String physicalQuery;
	private String displayQuery;
	private String term1;
	private String field1;
	private String term2;
	private String field2;
	private String term3;
	private String field3;
	private String bool1;
	private String bool2;
	private String queryType;
	private List<String> catCodes;
	private String queryID;
	private Perl5Util perl = new Perl5Util();
	private static String[] AUTOSTEM_ARRAY = { "AB" };
	private static Hashtable<String, String> displayMappings = new Hashtable<String, String>();
	private static Hashtable<String, String> colMappings = new Hashtable<String, String>();

	static {
		displayMappings.put("AB", "KY");
		displayMappings.put("PN", "PN");
		displayMappings.put("AU", "AU");
		displayMappings.put("BN", "BN");
		displayMappings.put("TI", "TI");
		displayMappings.put("CV", "Subject");
		colMappings.put("MAT", "Materials & Mechanical");
		colMappings.put("ELE", "Electronics & Electrical");
		colMappings.put("CHE", "Chemical, Petrochemical & Process");
	}

	public BookQuery(String queryID) {
		this.queryID = queryID;
	}

	public String getQueryID() {
		return this.queryID;
	}

	public void setQueryID(String queryID) {
		this.queryID = queryID;
	}

	public String getQueryType() {
		return this.queryType;
	}

	public String getDatabase() {
		String db = null;
		if (field1.equalsIgnoreCase("AB")) {
			db = "PART";
		} else {
			db = "BOOK";
		}

		return db;
	}

	public String getDisplayQuery() throws InfrastructureException {
		if (this.displayQuery == null) {
			compile();
		}

		return this.displayQuery;
	}

	public void setQuery(String queryAsString) {

		this.physicalQuery = null;
		this.searchQuery = null;
		List<String> termsAndCats = new ArrayList<String>();
		this.queryType = new String(new char[] { queryAsString.charAt(0) });
		queryAsString = queryAsString.substring(1);
		perl.split(termsAndCats, "/\\|\\|/", queryAsString);
		String allTermString = (String) termsAndCats.get(0);
		String catString = (String) termsAndCats.get(1);

		List<String> allTerms = new ArrayList<String>();
		perl.split(allTerms, "/==/", allTermString);

		String termString = "";
		ArrayList<String> termParts = null;

		// Doing 1st set of terms

		termString = (String) allTerms.get(0);
		termParts = new ArrayList<String>();
		perl.split(termParts, "/::/", termString);
		term1 = notNull((String) termParts.get(0));
		field1 = notNull((String) termParts.get(1));
		bool1 = notNull((String) termParts.get(2));

		// Doing 2nd set of terms

		termString = (String) allTerms.get(1);
		termParts = new ArrayList<String>();
		perl.split(termParts, "/::/", termString);
		term2 = notNull((String) termParts.get(0));
		field2 = notNull((String) termParts.get(1));
		bool2 = notNull((String) termParts.get(2));

		// Doing 3rd set of terms

		termString = (String) allTerms.get(2);
		termParts = new ArrayList<String>();
		perl.split(termParts, "/::/", termString);
		term3 = notNull((String) termParts.get(0));
		field3 = notNull((String) termParts.get(1));

		// Doing cat codes
		List<String> cats = new ArrayList<String>();
		perl.split(cats, "/::/", catString);
		this.catCodes = cats;

	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(term1).append("::").append(field1).append("::").append(bool1).append("==");
		buf.append(term2).append("::").append(field2).append("::").append(bool2).append("==");
		buf.append(term3).append("::").append(field3).append("||");

		for (int i = 0; i < catCodes.size(); i++) {
			String code = (String) catCodes.get(i);
			if (i > 0) {
				buf.append("::");
			}
			buf.append(code);
		}

		return this.queryType + buf.toString();
	}

	public void compile() throws InfrastructureException {
		buildPhysicalQuery();
		buildDisplayQuery();
		buildSearchQuery();
	}

	private void buildPhysicalQuery() {

		StringBuffer buf = new StringBuffer();

		if (term1.length() > 0) {
			buf.append("((");

			if (term1.length() > 0) {
				buf.append(term1);
				buf.append(" WN ");
				buf.append(field1);
			}

			if (term2.length() > 0) {
				buf.append(" ").append(bool1).append(" ");
				buf.append(term2);
				buf.append(" WN ");
				buf.append(field2);
			}

			buf.append(")");

			if (term3.length() > 0) {
				buf.append(" ").append(bool2).append(" ");
				buf.append(term3);
				buf.append(" WN ");
				buf.append(field3);
			}

			buf.append(")");
		}

		if (term1.length() > 0) {
			buf.append(" AND ");
		}

		buf.append("((");

		for (int i = 0; i < this.catCodes.size(); i++) {
			String code = (String) catCodes.get(i);
			if (i > 0) {
				buf.append(" OR ");
			}
			buf.append(code);

		}

		buf.append(") WN CL) AND (");
		buf.append(getDatabase());
		buf.append(" WN DB");

		buf.append(")");
		this.physicalQuery = buf.toString();
		// System.out.println("TEST:"+this.physicalQuery);
	}

	public static void main(String args[]) throws Exception {
		/*
		 * BookQuery q = new BookQuery("Test"); ArrayList l = new ArrayList();
		 * l.add("CAT1"); l.add("CAT2"); l.add("CAT3");
		 * 
		 * q.setQuery("terms test","TT","term2","TT",null,null,"AND","AND",l);
		 * System.out.println(q.getPhysicalQuery());
		 * System.out.println(q.getSearchQuery());
		 * System.out.println(q.toString());
		 * 
		 * 
		 * 
		 * System.out.println("Setting query...");
		 * 
		 * q.setQuery("term1 test","AB","term2","TT","water","TT","AND","AND",l);
		 * System.out.println(q.getPhysicalQuery()); System.out.println("111:"+
		 * q.getSearchQuery()); System.out.println(q.toString());
		 * 
		 * BookQuery q1 = new BookQuery("Test"); q1.setQuery(q.toString());
		 * System.out.println("from toString");
		 * System.out.println("111:"+q1.getSearchQuery());
		 */

	}

	private void buildSearchQuery() throws InfrastructureException {

		BookQueryWriter searchQueryWriter = new BookQueryWriter();
		BaseParser parser = new BaseParser();
		BooleanQuery queryTree;
		queryTree = (BooleanQuery) parser.parse(this.physicalQuery);
		AutoStemmer stemmer = new AutoStemmer(AUTOSTEM_ARRAY);
		queryTree = stemmer.autoStem(queryTree);
		this.searchQuery = searchQueryWriter.getQuery(queryTree);

	}

	private void buildDisplayQuery() {
		StringBuffer buf = new StringBuffer();

		if (term1.length() > 0) {
			if (term2.length() > 0) {
				buf.append("(");
			}

			if (term1.length() > 0) {
				buf.append("((");
				buf.append(term1);
				buf.append(") WN ");
				buf.append((String) displayMappings.get(field1));
				buf.append(")");
			}

			if (term2.length() > 0) {
				buf.append(" ").append(bool1).append(" ");
				buf.append("((");
				buf.append(term2);
				buf.append(") WN ");
				buf.append((String) displayMappings.get(field2));
				buf.append(")");
			}

			if (term2.length() > 0) {
				buf.append(")");
			}

			if (term3.length() > 0) {
				buf.append(" ").append(bool2).append(" ((");
				buf.append(term3);
				buf.append(") WN ");
				buf.append((String) displayMappings.get(field3));
				buf.append(")");
			}
		}

		if (catCodes.size() == 3) {
			if (buf.length() > 0) {
				buf.append(", ");
			}

			buf.append("All Referex collections");
		} else {
			if (buf.length() > 0) {
				buf.append(", ");
			}

			for (int i = 0; i < catCodes.size(); i++) {
				String ca = (String) catCodes.get(i);
				buf.append((String) colMappings.get(ca));
				if (i < (catCodes.size() - 1)) {
					buf.append(", ");
				}
			}
		}

		this.displayQuery = buf.toString();
		// System.out.println(this.displayQuery);
	}

	public String getPhysicalQuery() throws InfrastructureException {
		if (this.physicalQuery == null) {
			compile();
		}

		return this.physicalQuery;
	}

	public String getSearchQuery() throws InfrastructureException {
		if (this.searchQuery == null) {
			compile();
		}

		return this.searchQuery;
	}

	public void setQuery(String term1, String field1, String term2, String field2, String term3, String field3, String bool1, String bool2, String queryType,
			List<String> catCodes) throws BookException {
		this.physicalQuery = null;
		this.searchQuery = null;
		this.term1 = notNull(term1);
		this.field1 = notNull(field1);
		this.term2 = notNull(term2);
		this.field2 = notNull(field2);
		this.term3 = notNull(term3);
		this.field3 = notNull(field3);
		this.bool1 = notNull(bool1);
		this.bool2 = notNull(bool2);
		this.queryType = queryType;

		if (catCodes == null || catCodes.size() == 0) {
			throw new BookException(new Exception("catCodes cannot be null or 0 length"));
		}
		this.catCodes = catCodes;
	}

	private String notNull(String s) {
		if (s == null) {
			return "";
		}

		return s;
	}

	public void toXML(Writer out) throws IOException {
		out.write("<QUERY>");
		if (term1.length() > 0) {
			out.write("<T1><![CDATA[");
			out.write(term1);
			out.write("]]></T1>");
		}

		if (term2.length() > 0) {
			out.write("<T2><![CDATA[");
			out.write(term1);
			out.write("]]></T2>");
		}

		if (term3.length() > 0) {
			out.write("<T3><![CDATA[");
			out.write(term1);
			out.write("]]></T3>");
		}

		if (field1.length() > 0) {
			out.write("<f1>");
			out.write(field1);
			out.write("</f1>");
		}

		if (field2.length() > 0) {
			out.write("<f2>");
			out.write(field2);
			out.write("</f2>");
		}

		if (field3.length() > 0) {
			out.write("<f3>");
			out.write(field2);
			out.write("</f3>");
		}

		if (bool1.length() > 0) {
			out.write("<b1>");
			out.write(bool1);
			out.write("</b1>");
		}

		if (bool2.length() > 0) {
			out.write("<b2>");
			out.write(bool2);
			out.write("</b2>");
		}

		out.write("<CLS>");
		for (int i = 0; i < catCodes.size(); i++) {
			String cl = (String) catCodes.get(i);
			out.write("<CL>");
			out.write(cl);
			out.write("</CL>");
		}
		out.write("</CLS>");
		out.write("</QUERY>");
	}

	/**
	 * @return
	 */
	public String getBool1() {
		return bool1;
	}

	/**
	 * @return
	 */
	public String getBool2() {
		return bool2;
	}

	/**
	 * @return
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @return
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @return
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * @return
	 */
	public List<String> getCatCodes() {
		return catCodes;
	}

	/**
	 * @return
	 */
	public String getTerm1() {
		return term1;
	}

	/**
	 * @return
	 */
	public String getTerm2() {
		return term2;
	}

	/**
	 * @return
	 */
	public String getTerm3() {
		return term3;
	}

	/**
	 * @param string
	 */
	public void setBool1(String string) {
		bool1 = string;
	}

	/**
	 * @param string
	 */
	public void setBool2(String string) {
		bool2 = string;
	}

	/**
	 * @param string
	 */
	public void setField1(String string) {
		field1 = string;
	}

	/**
	 * @param string
	 */
	public void setField2(String string) {
		field2 = string;
	}

	/**
	 * @param string
	 */
	public void setField3(String string) {
		field3 = string;
	}

	/**
	 * @param list
	 */
	public void setCatCodes(List<String> catCodes) {
		this.catCodes = catCodes;
	}

	/**
	 * @param string
	 */
	public void setTerm1(String string) {
		term1 = string;
	}

	/**
	 * @param string
	 */
	public void setTerm2(String string) {
		term2 = string;
	}

	/**
	 * @param string
	 */
	public void setTerm3(String string) {
		term3 = string;
	}
}
