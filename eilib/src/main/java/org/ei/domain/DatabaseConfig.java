package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.domain.sort.SortField;

public final class DatabaseConfig {

	public static final int CPX_MASK = 1;
	public static final int INS_MASK = 2;
	public static final int NTI_MASK = 4;
	public static final int USPTO_MASK = 8;
	public static final int CRC_MASK = 16;
	public static final int C84_MASK = 32;
	public static final int PCH_MASK = 64;
	public static final int CHM_MASK = 128;
	public static final int CBN_MASK = 256;
	public static final int ELT_MASK = 1024;
	public static final int EPT_MASK = 2048;
	public static final int IBF_MASK = 4096;
	public static final int GEO_MASK = 8192;
	public static final int EUP_MASK = 16384;
	public static final int UPA_MASK = 32768;
	public static final int REF_MASK = 65536;
	public static final int PAG_MASK = 131072;
	public static final int CBF_MASK = 262144;
	public static final int UPT_MASK = 524288;
	public static final int IBS_MASK = 1048576;
	public static final int GRF_MASK = 2097152;

	public static final String CPX_PREF = "cpx";
	public static final String INS_PREF = "ins";
	public static final String NTI_PREF = "nti";
	public static final String USPTO_PREF = "usp";
	public static final String CRC_PREF = "crc";
	public static final String C84_PREF = "c84";
	public static final String PCH_PREF = "pch";
	public static final String CHM_PREF = "chm";
	public static final String CBN_PREF = "cbn";
	public static final String ELT_PREF = "elt";
	public static final String EPT_PREF = "ept";
	public static final String IBF_PREF = "ibf";
	public static final String GEO_PREF = "geo";
	public static final String EUP_PREF = "eup";
	public static final String UPA_PREF = "upa";
	public static final String REF_PREF = "ref";
	public static final String PAG_PREF = "pag";
	public static final String CBF_PREF = "zbf";
	public static final String UPT_PREF = "upt";
	public static final String IBS_PREF = "ibs";
	public static final String GRF_PREF = "grf";

	public static final int CBF_ENDYEAR = 1969;
	public static final int IBS_ENDYEAR = 1969;
	
	protected static Log log = LogFactory.getLog(DatabaseConfig.class);
	private static Hashtable<String, String> dtName = new Hashtable<String, String>();

	static {
		dtName.put("JA", "Journal article");
		dtName.put("CA", "Conference article");
		dtName.put("CP", "Conference proceeding");
		dtName.put("MC", "Monograph chapter");
		dtName.put("MR", "Monograph review");
		dtName.put("RC", "Report chapter");
		dtName.put("RR", "Report review");
		dtName.put("DS", "Dissertation");
		dtName.put("ST", "Standard");
		dtName.put("PA", "Patent");
		dtName.put("UP", "Unpublished paper");
		dtName.put("CORE", "CORE");
        dtName.put("IP", "Article in Press");
        dtName.put("GI", "In Process");
		// upt dt
		dtName.put("AP", "All patents");
		dtName.put("UA", "US Applications");
		dtName.put("UG", "US Granted");
		dtName.put("EA", "European Applications");
		dtName.put("EG", "European Granted");

		// pag (books)
		dtName.put("PAGE", "Page");
		dtName.put("BOOK", "Book");

		// paperchem
		dtName.put("(CA or CP)", "Conferences");
		dtName.put("MC or MR or RC or RR or DS or UP", "Other documents");

		// CBNB
		dtName.put("JOURNAL", "Journal article");
		dtName.put("ADVERTIZEMENT", "Advertisement");
		dtName.put("BOOK", "Book");
		dtName.put("DIRECTORY", "Directory");
		dtName.put("COMPANY", "Company Report");
		dtName.put("STOCKBROKER", "Stockbroker Report");
		dtName.put("MARKET", "Market Research Report");
		dtName.put("PRESS", "Press");
		dtName.put("PRESS RELEASE", "Press Release");

		// Chimica
		dtName.put("ARTICLE", "Article");
		dtName.put("BOOK", "Book");
		dtName.put("BOOK SERIES", "Book Series");
		dtName.put("CONFERENCE PAPER", "Conference Paper");
		dtName.put("CONFERENCE REVIEW", "Conference Review");
		dtName.put("EDITORIAL", "Editorial");
		dtName.put("ERRATUM", "Erratum");
		dtName.put("JOURNAL", "Journal");
		dtName.put("LETTER", "Letter");
		dtName.put("NOTE", "Note");
		dtName.put("PROCEEDING", "Proceeding");
		dtName.put("REVIEW", "Review");
		dtName.put("SHORT SURVEY", "Short Survey");
		dtName.put("TRADE JOURNAL", "Trade Journal");
	}

	public static final String SESSION_POOL = "session";
	public static final String SEARCH_POOL = "search";

	private static DatabaseConfig instance;
	private Hashtable<String, Database> databaseTable = new Hashtable<String, Database>();
	private Hashtable<SortField, Integer> sortablefields = new Hashtable<SortField, Integer>();

	private DatabaseConfig(Map<String, String> driverTable) throws DatabaseConfigException {
		try {
			Iterator<String> itr = driverTable.keySet().iterator();
			while (itr.hasNext()) {
				String strKey = itr.next();
				String strDriverName = (String) driverTable.get(strKey);
				Database d = (Database) Class.forName(strDriverName).newInstance();
				databaseTable.put(strKey, d);
				if (d.hasChildren()) {
					Database[] children = d.getChildren();
					for (int i = 0; i < children.length; i++) {
						databaseTable.put(children[i].getID(), children[i]);
					}
				}

			}
			// loop through all databases and collect the sortable fields
			// in a map and store the sum of their masks using the fields as
			// keys
			itr = databaseTable.keySet().iterator();
			while (itr.hasNext()) {
				String strKey = itr.next();
				Database d = databaseTable.get(strKey);
				int dbmask = d.getMask();
				List sortfields = d.getSortableFields();
				Iterator itrfields = sortfields.iterator();
				while (itrfields.hasNext()) {
					SortField sortfield = (SortField) itrfields.next();
					int masks = 0;
					if (sortablefields.containsKey(sortfield)) {
						masks = sortablefields.get(sortfield).intValue();
					}
					masks += dbmask;
					sortablefields.put(sortfield, new Integer(masks));
				}
			}
		} catch (Exception e) {
			throw new DatabaseConfigException(e);
		}
	}

	public Hashtable<String, Database> getDatabaseTable() {
		return this.databaseTable;
	}

	public String getClsTitle(String clsCode, int mask) {
		String title = null;
		if ((clsCode != null) && (clsCode.length() > 0)) {
			Database[] dbs = getDatabases(mask);
			for (int i = 0; i < dbs.length; i++) {
				if ((clsCode.indexOf("DQD") > -1) || (clsCode.indexOf("dqd") > -1)) {
					clsCode = clsCode.replaceAll("DQD|dqd", ".");
				}
				title = dbs[i].getDataDictionary().getClassCodeTitle(clsCode.toUpperCase());
				if (title != null) {
					break;
				}
			}
		}
		return title;
	}

	public String getAuthorityCode(String auCode) {
		if ((auCode != null) && (auCode.length() > 0)) {
			Database d = databaseTable.get(EPT_PREF);
			if (d != null) {
				if ((auCode.indexOf("DQD") > -1) || (auCode.indexOf("dqd") > -1)) {
					auCode = auCode.replaceAll("DQD|dqd", ".");
				}
				if (d.getDataDictionary().getAuthorityCodes() != null) {
					return (String) d.getDataDictionary().getAuthorityCodes().get(auCode.toUpperCase());
				}
			}
		}
		return auCode;
	}

	public String getClassTitle(String clsCode) {
		int defaultMask = DatabaseConfig.CPX_MASK + DatabaseConfig.IBF_MASK + DatabaseConfig.NTI_MASK + DatabaseConfig.GEO_MASK;
		return getClsTitle(clsCode, defaultMask);
	}

	// pass a DT code to get the string of the document type
	public String getDtTitle(String dtCode) {
		// all DT keys/codes are in upper case in the hashtable
		// defined above
		if ((dtCode != null) && (dtCode.length() > 0)) {
			// maybe we should try toLowerCase on dtcode if this returns null
			return dtName.get(dtCode.toUpperCase());
		} else {
			return null;
		}
	}

	public static synchronized DatabaseConfig getInstance(Map driverTable) throws DatabaseConfigException {
		if (instance == null) {
			instance = new DatabaseConfig(driverTable);
		}

		return instance;
	}

	public static DatabaseConfig getInstance() {
		return instance;
	}

	public int getDeDupableMask() {
		return (DatabaseConfig.CPX_MASK + DatabaseConfig.INS_MASK);
	}

	public int getRemoteMask() {
		return (DatabaseConfig.USPTO_MASK + DatabaseConfig.CRC_MASK);
	}

	public Database getDatabase(String strID) {
		Database adb = null;
		if (strID != null) {
			adb = databaseTable.get(strID);
		}
		return adb;
	}

	public int getMinStartYear(String[] credentials) {
		int mask = this.getMask(credentials);

		Database[] d = this.getDatabases(mask);

		// start off start year at max and end year at min
		int intStartYear = 9999;
		for (int y = 0; y < d.length; y++) {
			if (d[y] != null) {
				boolean backFile = false;
				if (d[y].getBackfile() != null) {
					backFile = hasBackfile(credentials, d[y].getBackfile());
				}
				// take min of startYear and database start year
				intStartYear = (d[y].getStartYear(backFile) < intStartYear) ? d[y].getStartYear(backFile) : intStartYear;
				// take max of endYear and database end year
			}
		} // for

		return intStartYear;
	}

	public int getStartYear(String[] credentials, int dbmask) {
		Database[] db = getDatabases(dbmask);
		boolean backFile = false;
		if (db[0].getBackfile() != null) {
			backFile = hasBackfile(credentials, db[0].getBackfile());
		}
		int intStartYear = db[0].getStartYear(backFile);

		return intStartYear;
	}

	public Database[] getDatabases(int intMask) {
		List<Database> lstDatabases = new ArrayList<Database>();
		Iterator<String> itr = databaseTable.keySet().iterator();
		while (itr.hasNext()) {
			String strKey = itr.next();
			Database d = databaseTable.get(strKey);
			if ((intMask & d.getMask()) != 0) {
				lstDatabases.add(d);
			}
		}
		Database[] dbs = lstDatabases.toArray(new Database[] {});
		Arrays.sort(dbs);
		return dbs;
	}

	public int getScrubbedMask(String[] strDatabases) {
		int mask = getMask(strDatabases);
		int exmasks[] = { DatabaseConfig.USPTO_MASK, DatabaseConfig.CRC_MASK, DatabaseConfig.C84_MASK, DatabaseConfig.IBF_MASK };

		for (int idx = 0; idx < exmasks.length; idx++) {
			if ((mask & exmasks[idx]) == exmasks[idx]) {
				mask -= exmasks[idx];
			}
		}

		return mask;
	}

	public int doUpgrade(int dbmask, String[] credentials) {
		int newMask = 0;
		Database[] dbs = getDatabases(dbmask);
		for (int i = 0; i < dbs.length; i++) {
			Database da = dbs[i];
			newMask = newMask + da.getMask();
			/*
			 * First Check for the backfile
			 */

			Database backfile = da.getBackfile();
			if (backfile != null && hasBackfile(credentials, backfile)) {
				newMask = newMask + backfile.getMask();
			}

		}

		return newMask;
	}

	private boolean hasBackfile(String[] creds, Database backfile) {
		for (int i = 0; i < creds.length; i++) {
			if (creds[i].equalsIgnoreCase(backfile.getID())) {
				return true;
			}
		}

		return false;
	}

	public int getMask(String[] strDatabases) {

		int intResult = 0;
		if(strDatabases != null){
			for (int i = 0; i < strDatabases.length; i++) {
				String strID = strDatabases[i];
				if (databaseTable.containsKey(strID.toLowerCase())) {
					Database d = databaseTable.get(strID.toLowerCase());
					intResult |= d.getMask();
				}
			}
		}
		return intResult;
	}

	public int getMask(String strDatabases) {
		int intResult = 0;

		if (databaseTable.containsKey(strDatabases.toLowerCase())) {
			Database d = databaseTable.get(strDatabases.toLowerCase());
			intResult = d.getMask();
		}
		return intResult;
	}

	public static final String STARTYEAR = "bYear";
	public static final String ENDYEAR = "eYear";

	public Map<String, String> getStartEndYears(String[] credentials, int dbmask) {
		Map<String, String> yrmap = new Hashtable<String, String>();
		yrmap.put(DatabaseConfig.STARTYEAR, "1790");
		yrmap.put(DatabaseConfig.ENDYEAR, String.valueOf(SearchForm.ENDYEAR));

		// start off start year at max and end year at min
		int intStartYear = 2099;
		int intEndYear = 0;
		int userMask = getMask(credentials);

		// loop through all dbs in the mask, opening up range to min start year
		// and max end year

		Database[] databases = getDatabases(dbmask);
		for (int x = 0; x < databases.length; x++) {
			// only include this database if it is in the user's mask

			if ((userMask & databases[x].getMask()) != databases[x].getMask()) {
				continue;
			}

			boolean backFile = false;
			if (databases[x].getBackfile() != null) {
				backFile = hasBackfile(credentials, databases[x].getBackfile());
			}
			// take min of startYear and database start year
			intStartYear = (databases[x].getStartYear(backFile) < intStartYear) ? databases[x].getStartYear(backFile) : intStartYear;
			// take max of endYear and database endear
			intEndYear = (databases[x].getEndYear() > intEndYear) ? databases[x].getEndYear() : intEndYear;
		}

		yrmap.put(DatabaseConfig.STARTYEAR, String.valueOf(intStartYear));
		yrmap.put(DatabaseConfig.ENDYEAR, String.valueOf(intEndYear));

		return yrmap;
	}

	public void toXML(String[] credentials, Writer out) throws IOException {

	} // toXML()

	public boolean sortableBy(int compmask, String field) {
		boolean result = false;

		Iterator<SortField> itrfields = this.sortablefields.keySet().iterator();
		while (itrfields.hasNext()) {
			SortField sortfield = itrfields.next();
			if (sortfield.getDisplayName().equalsIgnoreCase(field)) {
				int sortmask = this.sortablefields.get(sortfield).intValue();
				result = ((compmask & sortmask) == compmask);
				break;
			}
		}

		return result;
	}

	public void sortableToXML(String[] credentials, Writer out) throws IOException {

		out.write("<SORTABLE-FIELDS>");

		// sort fields
		ArrayList<SortField> lstfields = Collections.list(this.sortablefields.keys());
		Collections.sort(lstfields);

		Iterator<SortField> itrfields = lstfields.iterator();
		while (itrfields.hasNext()) {
			SortField field = itrfields.next();
			int mask = this.sortablefields.get(field).intValue();

			out.write("<SORTBYFIELD " + " mask=\"" + mask + "\"" + " defaultdir=\"" + field.getDefaultSortDirection() + "\"" + " value=\"" + field.getSearchIndexKey() + "\">");
			String[] dirs = field.getAvailableSortDirections();
			for (int i = 0; i < dirs.length; i++) {
				out.write("<SORT-DIR dir=\"" + dirs[i] + "\"/>");
			}
			out.write(field.getDisplayName());
			out.write("</SORTBYFIELD>");
		}
		out.write("</SORTABLE-FIELDS>");

	}
}
