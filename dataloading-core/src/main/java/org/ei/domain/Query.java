package org.ei.domain;

/** import statements of Java packages */
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.ei.domain.navigators.Refinements;
import org.ei.domain.navigators.ResultsState;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanQuery;
import org.ei.query.base.AutoStemmer;
import org.ei.query.base.EIQueryWriter;
import org.ei.query.base.FieldValidator;
import org.ei.query.base.QueryMask;
import org.ei.query.base.QueryWriter;
import org.ei.query.base.RelevanceBooster;
import org.ei.query.limiter.Limiter;
import org.ei.query.limiter.ReferexLimiter;
import org.ei.util.StringUtil;
import org.ei.xml.Entity;

/**
 * Encapsulates search query statistics such as the query phrase, number of
 * records resulted, language in which to search.A Hashtable with the reqired
 * paramaters as key value pairs are input to this class.
 *
 **/

public class Query implements Comparable<Object>, Serializable {
	private static final long serialVersionUID = 5646600129664548775L;

	public static String TERM_SEPARATOR = "|";

	protected static Log log = LogFactory.getLog(Query.class);

	private static final String[] stemFields = { "ALL", "KY", "TI", "AB", "FL", "CV", "AV", "AF", "ST", "MH", "CF", "PN", "BI" };

	private static final String[] allFields = { "GEO", "RGI", "PU", "PM", "PE", "VO", "SU", "SP", "DB", "AN", "RN", "AG", "AV", "CT", "CO", "NT", "PA", "PI",
			"WK", "YR", "ALL", "KY", "AF", "PN", "ST", "MH", "CL", "CN", "TI", "AB", "AU", "CC", "CV", "FL", "SN", "DT", "BN", "TR", "LA", "CF", "DI", "MI",
			"NI", "CI", "AI", "OC", "BI", "PEC", "PUC", "PAC", "PK", "PID", "PCI", "PRN", "SIC", "PEX", "PAM", "DOI", "PFD", "BKS", "BKT", "PD", "CR", "PC",
			"IP", "IS", "EB", "CM", "GC", "RC", "RO", "SI", "VOM", "CP", "SC", "GD", "IC", "CE", "DS", "DPID", "SD", "SO", "PPA",
			// EnCompass fields
			"AJ", "PRD", "PRC", "AP", "AD", "AC", "EY", "PT", "LT", "CVA", "CVP", "CVN", "CVM", "CVMN", "CVMA", "CVMP", "DG" };

	private static Map<String, String> quickSearchDiplayOptions = new HashMap<String, String>();

	private ResultsState resultsstate = new ResultsState();

	public ResultsState getResultsState() {
		return resultsstate;
	}

	public void setResultsState(ResultsState aresultsstate) {
		resultsstate = aresultsstate;
	}

	public void clearResultsState() {
		resultsstate = new ResultsState();
	}

	private Refinements queryrefinements = new Refinements();

	public Refinements getRefinements() {
		return queryrefinements;
	}

	public void setRefinements(Refinements refinements) {
		queryrefinements = refinements;
	}

	static {
		quickSearchDiplayOptions.put("DT:AB", "Abstract");
		quickSearchDiplayOptions.put("DT:MP", "Map");
		quickSearchDiplayOptions.put("DT:JA", "Journal article");
		quickSearchDiplayOptions.put("DT:CA", "Conference article");
		quickSearchDiplayOptions.put("DT:CP", "Conference proceeding");
		quickSearchDiplayOptions.put("DT:MC", "Monograph chapter");
		quickSearchDiplayOptions.put("DT:MR", "Monograph review");
		quickSearchDiplayOptions.put("DT:RC", "Report chapter");
		quickSearchDiplayOptions.put("DT:RR", "Report review");
		quickSearchDiplayOptions.put("DT:DS", "Dissertation");
		quickSearchDiplayOptions.put("DT:ST", "Standard");
		quickSearchDiplayOptions.put("DT:PA", "Patent");
		quickSearchDiplayOptions.put("DT:UP", "Unpublished paper");
		quickSearchDiplayOptions.put("DT:CORE", "CORE");
		quickSearchDiplayOptions.put("DT:UA", "US Applications");
		quickSearchDiplayOptions.put("DT:UG", "US Grants");
		quickSearchDiplayOptions.put("DT:EA", "EU Applications");
		quickSearchDiplayOptions.put("DT:EG", "EU Grants");
		quickSearchDiplayOptions.put("DT:EA", "EU Applications");
		quickSearchDiplayOptions.put("DT:EG", "EU Grants");
		quickSearchDiplayOptions.put("DT:(CA or CP)", "Conferences");
		quickSearchDiplayOptions.put("DT:MC or MR or RC or RR or DS or UP", "Other documents");
		quickSearchDiplayOptions.put("DT:Journal", "Journal article");
		quickSearchDiplayOptions.put("DT:Advertizement", "Advertisement");
		quickSearchDiplayOptions.put("DT:Book", "Book");
		quickSearchDiplayOptions.put("DT:Directory", "Directory");
		quickSearchDiplayOptions.put("DT:Company", "Company Report");
		quickSearchDiplayOptions.put("DT:Stockbroker", "Stockbroker Report");
		quickSearchDiplayOptions.put("DT:Market", "Market Research Report");
		quickSearchDiplayOptions.put("DT:Press", "Press");
		quickSearchDiplayOptions
				.put("DT:({J_AB} or {J_AR} or {J_BZ} or {J_CP} or {J_ED} or {J_ER} or {J_LE} or {J_NO} or {J_RE} or {J_SH} or {D_AR} or {D_BZ} or {D_CP} or {J_BK} or {J_BR} or {J_CH} or {J_CR} or {J_DI} or {J_PA} or {J_PR} or {J_RP} or {J_WP})",
						"Journal article");
		quickSearchDiplayOptions
				.put("DT:(P or {P_AR} or {P_CP} or {P_AB} or {P_BK} or {P_BR} or {P_BZ} or {P_CH} or {P_CR} or {P_DI} or {P_ED} or {P_ER} or {P_LE} or {P_NO} or {P_PA} or {P_PR} or {P_RE} or {P_SH} or {P_RP} or {P_WP} or {D_CP} or {J_CP})",
						"Conference");
		quickSearchDiplayOptions.put("DT:({J_BZ} or {D_BZ} or {D_AR} or {D_CP} or {D_LE} or {D_NO} or {B_BZ} or {K_BZ} or {M_BZ} or {P_BZ} or {R_BZ})",
				"Business article");
		quickSearchDiplayOptions.put("DT:(AB or {J_AB} or {R_AB} or {P_AB} or {B_AB} or {D_AB} or {K_AB} or {M_AB})", "Abstract");
		quickSearchDiplayOptions.put("DT:Other", "Other");
        quickSearchDiplayOptions.put("DT:IP", "Article in Press");
        quickSearchDiplayOptions.put("DT:GI", "In Process");

		quickSearchDiplayOptions.put("TR:APP", "Applications");
		quickSearchDiplayOptions.put("TR:ECO", "Economic");
		quickSearchDiplayOptions.put("TR:EXP", "Experimental");
		quickSearchDiplayOptions.put("TR:GEN", "General review");
		quickSearchDiplayOptions.put("TR:THR", "Theoretical");
		quickSearchDiplayOptions.put("TR:BIO", "Biographical");
		quickSearchDiplayOptions.put("TR:HIS", "Historical");
		quickSearchDiplayOptions.put("TR:LIT", "Literature review");
		quickSearchDiplayOptions.put("TR:MAN", "Management aspects");
		quickSearchDiplayOptions.put("TR:NUM", "Numerical");
		quickSearchDiplayOptions.put("TR:BIB", "Bibliography");
		quickSearchDiplayOptions.put("TR:NEW", "New development");
		quickSearchDiplayOptions.put("TR:PRA", "Practical");
		quickSearchDiplayOptions.put("TR:PRO", "Product review");

		quickSearchDiplayOptions.put("English", "English");
		quickSearchDiplayOptions.put("Chinese", "Chinese");
		quickSearchDiplayOptions.put("French", "French");
		quickSearchDiplayOptions.put("German", "German");
		quickSearchDiplayOptions.put("Italian", "Italian");
		quickSearchDiplayOptions.put("Japanese", "Japanese");
		quickSearchDiplayOptions.put("Russian", "Russian");
		quickSearchDiplayOptions.put("Spanish", "Spanish");

		quickSearchDiplayOptions.put("DI:A", "Physics");
		quickSearchDiplayOptions.put("DI:B", "Electrical/Electronic engineering");
		quickSearchDiplayOptions.put("DI:C", "Computers/Control engineering");
		quickSearchDiplayOptions.put("DI:D", "Information technology");
		quickSearchDiplayOptions.put("DI:E", "Manufacturing and production engineering");
	}

	public static final String TYPE_QUICK = "Quick";
	public static final String TYPE_EASY = "Easy";
	public static final String TYPE_EXPERT = "Expert";
	public static final String TYPE_THESAURUS = "Thesaurus";
	public static final String TYPE_COMBINED_PAST = "Combined";
	public static final String TYPE_BOOK = "Book";

	public static final String ON = "On";
	public static final String OFF = "Off";

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	private String strEmailAlertWeek = StringUtil.EMPTY_STRING;

	private QueryWriter searchQueryWriter;
	private RelevanceBooster[] boosters;

	private DatabaseConfig config;

	private int mask = -1;
	private int gatheredMask = 0;
	private String[] credentials;
	private Database[] databases;
	private Limiter referexCollections;

	private QueryWriter physicalQueryWriter = new EIQueryWriter();

	private BooleanQuery queryTree;
	private String physicalLimits = "";
	private String displayLimits = "";
	private String displayBase = "";
	private String physicalBase = "";
	private String yearRange = "";
	private String intermediatequery = "";

	private String emailAlertWeek = "";
	private String subcounts = "";
	private boolean display = false;

	/** search phrase components */

	private String sSeaPhr1 = "";
	private String sSeaOpt1 = "";
	private String sBool1 = "";
	private String sSeaPhr2 = "";
	private String sSeaOpt2 = "";
	private String sBool2 = "";
	private String sSeaPhr3 = "";
	private String sSeaOpt3 = "";
	private String[] searchWords;
	private String[] booleans;
	private String[] sections;

	/** represents the actual search query */
	protected String sSearchQuery = "";

	/** number of records that the query results */
	protected String sRecordCount = "";

	/** published Year from which to start the search */
	protected String sStartYear = "";

	/** published year upto which to search */
	protected String sEndYear = "";

	/** database in which search is done */
	protected Database sDatabase;

	/** language in which search is done */
	protected String sLanguage = "";

	/** indicator whether auto stemming of phrase is to be done */
	protected String sAutoStemming = "";

	/** sort option */
	protected Sort sortOption = Sort.getDefaultSortOption();

	/** search type */
	protected String sSearchType = "";

	/** Query Id */
	protected String sId = "";

	/** Display Query */
	protected String sDisplayQuery = "";

	/** Physical Query */
	protected String sPhysicalQuery = "";

	/** Document Type */
	protected String sDocType = "";

	/** Treatment Type */
	protected String sTreatmentType = "";

	/** Last Four Updates */
	protected String sLastFourUpdates = "";

	/** DisciplineType */
	protected String sDisciplineType = "";

	protected String sessionID = "";
	private String userID = "";

	/** EmailAlert */
	/** SavedSearch */
	protected String savedSearch = Query.OFF;
	protected String emailAlert = Query.OFF;
	protected String visible = Query.ON;
	protected Date saveddate = null;
	protected String ccList = "";

	// jam - Savedsearchid is no longer different from
	// queryid
	// protected String savedSearchID = "";

	/** DeDup set */
	protected List<String> dupSet = new ArrayList<String>();

	/** DeDup Database */
	protected String deDupDB = "cpx";

	/** DeDup State */
	protected boolean deDup = false;

	/** XML String containing remote database information */
	protected StringBuffer sXMLString = new StringBuffer();

	public Query() {

	}

	public Query(String xmlString) throws InfrastructureException {
		try {
			this.loadFromXML(xmlString);

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		}
	}

	public Query(DatabaseConfig config, String[] credents) {
		this.config = config;
		this.credentials = credents;
	}

	public Query(QueryWriter sqw, DatabaseConfig config, String[] credentials, String xmlString) throws InfrastructureException {
		try {
			this.searchQueryWriter = sqw;
			this.config = config;
			this.credentials = credentials;
			this.loadFromXML(xmlString);
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		}
	}

	private void loadFromXML(String xmlString) throws InfrastructureException {
		try {
			// Instanciate sax parser for Query History
			SaxHistoryParser hp = new SaxHistoryParser();
			// parse query xml to a hashtable
			Hashtable<String, String> ht = hp.read(xmlString);

			// Set Class variables from the Hashtable
			setEmailAlertWeek((String) ht.get("EMAILALERTWEEK"));

			setSeaPhr1((String) ht.get("SEARCH-PHRASE_SEARCH-PHRASE-1"));
			setSeaOpt1((String) ht.get("SEARCH-PHRASE_SEARCH-OPTION-1"));
			setBool1((String) ht.get("SEARCH-PHRASE_BOOLEAN-1"));
			setSeaPhr2((String) ht.get("SEARCH-PHRASE_SEARCH-PHRASE-2"));
			setSeaOpt2((String) ht.get("SEARCH-PHRASE_SEARCH-OPTION-2"));
			setBool2((String) ht.get("SEARCH-PHRASE_BOOLEAN-2"));
			setSeaPhr3((String) ht.get("SEARCH-PHRASE_SEARCH-PHRASE-3"));
			setSeaOpt3((String) ht.get("SEARCH-PHRASE_SEARCH-OPTION-3"));
			setBooleans((String) ht.get("SEARCH-PHRASE_BOOLEANS"));
			setSearchWords((String) ht.get("SEARCH-PHRASE_SEARCH-PHRASES"));
			setSections((String) ht.get("SEARCH-PHRASE_SEARCH-OPTIONS"));
			setRecordCount((String) ht.get("RESULTS-COUNT"));
			setLanguage((String) ht.get("LANGUAGE"));
			setStartYear((String) ht.get("START-YEAR"));
			setEndYear((String) ht.get("END-YEAR"));

			if (ht.get("SORT-OPTION") != null) {
				setSortOption(new Sort((String) ht.get("SORT-OPTION"), "dw"));
			} else {
				setSortOption(new Sort(Sort.RELEVANCE_FIELD, "dw"));
			}

			setSearchType((String) ht.get("SEARCH-TYPE"));
			setID((String) ht.get("QUERY-ID"));
			setDisplayQuery((String) ht.get("DISPLAY-QUERY"));

			// setSearchQuery((String)ht.get("SEARCH-QUERY"));

			if (ht.containsKey("PHYSICAL-QUERY")) {
				setPhysicalQuery((String) ht.get("PHYSICAL-QUERY"));
			}

			setDocumentType((String) ht.get("DOCUMENT-TYPE"));
			setTreatmentType((String) ht.get("TREATMENT-TYPE"));
			setLastFourUpdates((String) ht.get("LASTFOURUPDATES"));
			setDisciplineType((String) ht.get("DISCIPLINE-TYPE"));
			setDeDupDB((String) ht.get("DEDUPDB"));
			setAutoStemming((String) ht.get("AUTOSTEMMING"));
			setDeDup((String) ht.get("DEDUP"));
			setDupSet((String) ht.get("DUPSET"));

			setSubcounts((String) ht.get("SC"));

			if (ht.get("DATABASE_ID") != null) {
				String did = (String) ht.get("DATABASE_ID");

				if (did.equalsIgnoreCase("cpx")) {
					setDataBase(1);
				} else if (did.equalsIgnoreCase("inspec")) {
					setDataBase(2);
				} else if (did.equalsIgnoreCase("com")) {
					setDataBase(3);
				} else if (did.equalsIgnoreCase("uspto")) {
					setDataBase(8);
				} else if (did.equalsIgnoreCase("crc")) {
					setDataBase(16);
				}

			} else {
				setDataBase(Integer.parseInt((String) ht.get("DATABASE-MASK")));
			}

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		}
	}

	public DatabaseConfig getDatabaseConfig() {
		return this.config;
	}

	public void setDatabaseConfig(DatabaseConfig config) {
		this.config = config;
	}

	public String[] getCredentials() {
		return this.credentials;
	}

	public void setCredentials(String[] creds) {
		this.credentials = creds;
	}

	public void setSubcounts(String subcounts) {
		this.subcounts = subcounts;
	}

	public String getSubcounts() {
		return this.subcounts;
	}

	public void setDisplay(boolean b) {
		this.display = b;
	}

	public void setGatheredMask(int mask) {
		this.gatheredMask = mask;
	}

	public int getGatheredMask() {
		return this.gatheredMask;
	}

	// public Query(QueryWriter sqw, DatabaseConfig config, String[]
	// credentials, String xmlString) throws InfrastructureException
	// {
	// try
	// {
	// this.searchQueryWriter = sqw;
	// this.config = config;
	// this.credentials = credentials;
	// this.loadFromXML(xmlString);
	// }
	// catch (Exception e)
	// {
	// throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
	// }
	// }

	// public Query(String xmlString) throws InfrastructureException
	// {
	// try
	// {
	// this.loadFromXML(xmlString);
	// }
	// catch (Exception e)
	// {
	// throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
	// }
	// }

	public void setSearchQueryWriter(QueryWriter sqw) {
		this.searchQueryWriter = sqw;
	}

	public void setRelevanceBoosters(RelevanceBooster[] boosters) {
		this.boosters = boosters;
	}

	private void buildDisplayQuery() {
		StringBuffer base = new StringBuffer(displayBase);

		if ((this.sSearchType.equals(TYPE_BOOK) || this.sSearchType.equals(TYPE_QUICK) || this.sSearchType.equals(TYPE_THESAURUS))
				&& displayLimits.length() > 0) {
			if (base.length() > 0) {
				base.append(", ");
			}
			base.append(displayLimits);
		}

		if (emailAlertWeek.length() > 0) {
			// jam 11-12-2002
			// removed "Email Alert " from following append string
			// jam 8-19-2003
			// removed completely from Display Query!
			// base.append(", Week ").append(strEmailAlertWeek);
		}

		this.sDisplayQuery = base.toString();
	}

	public Database[] getDatabases() {
		return databases;
	}

	public BooleanQuery getParseTree() throws SearchException, InfrastructureException {
		if (this.queryTree == null) {
			compile();
		}

		return queryTree;
	}

	public void compile() throws SearchException, InfrastructureException {

		/*
		 * if(((mask & 8) != 8) && ((mask & 16) != 16)) { boosters = new
		 * RelevanceBooster[1]; boosters[0] = new FullDocumentBooster(); }
		 */

		// Build up all the parts
		buildSearchBase();
		buildLimits();

		buildYearRange();

		buildEmailAlertWeek();

		// Put them together
		buildDisplayQuery();
		// try {
		buildPhysicalQuery();
		/*
		 * } catch (FieldException e) {
		 * ExceptionWriterHelper.writeException(SearchQueryCompilationException
		 * .SearchQueryCompilationFailed, e.getMessage(),
		 * e.getClass().getName(), this.getClass().getName(),
		 * getExceptionXml()); throw new
		 * SearchQueryCompilationException(e.getMessage(), e); }
		 */

		if (searchQueryWriter != null) {
			buildSearchQuery();
		}
	}

	private void buildEmailAlertWeek() {

		if (strEmailAlertWeek.length() > 0) {
			StringBuffer eBuff = new StringBuffer();
			eBuff.append("(").append(strEmailAlertWeek).append(" WN WK)");
			emailAlertWeek = eBuff.toString();
		}

	}

	private void buildPhysicalQuery() throws SearchException, InfrastructureException {
		StringBuffer physicalQueryBuffer = new StringBuffer();

		physicalQueryBuffer.append(physicalBase);
		if (physicalLimits.length() > 0) {
			if (physicalQueryBuffer.length() > 0) {
				physicalQueryBuffer.append(" AND ");
			}
			physicalQueryBuffer.append(physicalLimits);
		}

		// jam - add EASY refinements query
		// jam - 11/2004 Use refinements to build entire EASY search string
		// initial search term is added to query object as a refinement
		if (getSearchType().equals(TYPE_EASY) && (queryrefinements.size() > 0)) {
			// the false,false parameters tell the method not to use a prefix
			// on returned query and not to use an inclusion or exclusion prefix
			physicalQueryBuffer = new StringBuffer(queryrefinements.getRefinementQueryString());
		}

		setIntermediateQuery(physicalQueryBuffer.toString());

		if (strEmailAlertWeek.length() > 0) {
			if (physicalQueryBuffer.length() > 0) {
				physicalQueryBuffer.append(" AND ");
			}
			physicalQueryBuffer.append(emailAlertWeek);
		} else {
			if (sLastFourUpdates.length() == 0) {
				if (yearRange.length() > 0) {
					if (physicalQueryBuffer.length() > 0) {
						physicalQueryBuffer.append(" AND ");
					}
					physicalQueryBuffer.append(yearRange);
				}
			}
		}

		BaseParser parser = new BaseParser();
		// System.out.println("Phisical query::::::"+physicalQueryBuffer.toString());

		BooleanQuery queryTree;
		// try {
		// try {
		queryTree = (BooleanQuery) parser.parse(physicalQueryBuffer.toString());
		// } catch (IOException e) {
		// ExceptionWriterHelper.writeException(SearchQueryCompilationException.StringTokenizerFailedForInputParams,
		// e.getMessage(), e.getClass().getName(), this.getClass().getName(),
		// getExceptionXml());
		// throw new SearchQueryCompilationException(e.getMessage(), e,
		// SearchQueryCompilationException.StringTokenizerFailedForInputParams);
		// }
		/*
		 * } catch (InfrastructureException e) {
		 * //ExceptionWriterHelper.writeException(SearchQueryCompilationException
		 * .parsingFailed, e.getMessage(), e.getClass().getName(),
		 * this.getClass().getName(), getExceptionXml()); throw new
		 * SearchQueryCompilationException("query parsing failed" +
		 * e.getMessage(), e, SearchQueryCompilationException.parsingFailed); }
		 */

		// DatabaseMaskGatherer truedb = new DatabaseMaskGatherer();
		// int newmask = truedb.gatherDbMask(queryTree,mask);

		QueryMask qm = new QueryMask();
		int newmask = qm.getQueryMask(queryTree, mask);
		// System.out.println(" Mask adjusted with query DB terms: " + newmask);
		this.setGatheredMask(newmask);

		if (((mask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK) && ((mask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK)) {
			FieldValidator fv = new FieldValidator(allFields);

			Vector<String> invalidFields = fv.validateFields(queryTree);
			if (invalidFields.size() > 0) {
				StringBuilder buf = new StringBuilder();
				for (int i = 0; i < invalidFields.size(); ++i) {
					if (i > 0) {
						buf.append(", ");
					}

					buf.append((String) invalidFields.get(i));
				}
				// fe.setFields(invalidFields);
				// ExceptionWriterHelper.writeException(SystemErrorCodes.InValidField.value(),
				// fe.getMessage(), fe.getClass().getName(),
				// this.getClass().getName(), getExceptionXml());
				throw new SearchException(SystemErrorCodes.INVALID_FIELD,
						"Query Error, The following field(s) do not exist::" + buf.toString());
			}
		}

		if (sAutoStemming.equalsIgnoreCase(ON) && ((mask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK)
				&& ((mask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK)) {
			AutoStemmer stemmer = new AutoStemmer(stemFields);
			queryTree = stemmer.autoStem(queryTree);
		}

		this.sPhysicalQuery = physicalQueryWriter.getQuery(queryTree);

	}

	private void buildSearchQuery() throws SearchException, InfrastructureException {
		StringBuffer searchQueryBuffer = new StringBuffer(sPhysicalQuery);

		if (((mask & DatabaseConfig.USPTO_MASK) != DatabaseConfig.USPTO_MASK) && ((mask & DatabaseConfig.CRC_MASK) != DatabaseConfig.CRC_MASK)) {

			searchQueryBuffer.append(" AND (");
			databases = config.getDatabases(mask);

			for (int i = 0; i < databases.length; ++i) {
				Database dbase = databases[i];
				if (i > 0) {
					searchQueryBuffer.append(" OR ");
				}

				searchQueryBuffer.append("(");
				searchQueryBuffer.append(dbase.getID());
				searchQueryBuffer.append(" WN DB");
				if (sLastFourUpdates.length() > 0 && emailAlertWeek.length() == 0) {
					searchQueryBuffer.append(" AND ");
					try {
						searchQueryBuffer.append(dbase.getUpdates(Integer.parseInt(sLastFourUpdates)));
					} catch (NumberFormatException e) {
						throw new SearchException(SystemErrorCodes.LAST_FOUR_UPDATED_NUMBER_EMPTY, e);
					}
					searchQueryBuffer.append(" WN WK");
				}
				searchQueryBuffer.append(")");
			}

			searchQueryBuffer.append(")");

		}

		BaseParser parser = new BaseParser();
		// try {
		queryTree = (BooleanQuery) parser.parse(searchQueryBuffer.toString());
		/*
		 * } catch (InfrastructureException e) {
		 * ExceptionWriterHelper.writeException(SearchQueryCompilationException
		 * .SearchQueryCompilationFailed, e.getMessage(),
		 * e.getClass().getName(), this.getClass().getName(),
		 * getExceptionXml()); throw new
		 * SearchQueryCompilationException(e.getMessage(), e); } catch
		 * (IOException e) {
		 * ExceptionWriterHelper.writeException(SearchQueryCompilationException
		 * .SearchQueryCompilationFailed, e.getMessage(),
		 * e.getClass().getName(), this.getClass().getName(),
		 * getExceptionXml()); throw new
		 * SearchQueryCompilationException(e.getMessage(), e); }
		 */
		if (boosters != null) {
			for (int i = 0; i < boosters.length; ++i) {
				queryTree = boosters[i].applyBoost(queryTree);
			}
		}

		searchQueryWriter.setCredentials(credentials);
		this.sSearchQuery = searchQueryWriter.getQuery(queryTree);
	}

	private void buildLimits() {
		StringBuffer displayLimitBuff = new StringBuffer();
		StringBuffer physicalLimitBuff = new StringBuffer();

		if ((sDocType != null) && (sDocType.length() > 0)) {

			if (!(sDocType.equals("NO-LIMIT"))) {
				displayLimitBuff.append(((String) quickSearchDiplayOptions.get("DT:" + sDocType))).append(" only");
				physicalLimitBuff.append("(").append(sDocType).append(" WN DT)");
			}
		}

		String colPhysical = getReferexCollections().getPhysicalQueryFormat();

		if (colPhysical.length() > 0) {
			if (physicalLimitBuff.length() > 0) {
				physicalLimitBuff.append(" AND ");
				displayLimitBuff.append(", ");
			}
			physicalLimitBuff.append(colPhysical);
			// Do not append display query unless
			// referex is only DB in search
			if (getDataBase() == DatabaseConfig.PAG_MASK) {
				displayLimitBuff.append(getReferexCollections().getDisplayQueryFormat());
			}
		}

		if (sTreatmentType.length() > 0) {

			if (!(sTreatmentType.equals("NO-LIMIT"))) {
				if (displayLimitBuff.length() > 0) {
					displayLimitBuff.append(", ");
					physicalLimitBuff.append(" AND ");
				}

				displayLimitBuff.append(((String) quickSearchDiplayOptions.get("TR:" + sTreatmentType))).append(" only");
				physicalLimitBuff.append("(").append(sTreatmentType).append(" WN TR)");
			}
		}

		if ((sDisciplineType != null) && (sDisciplineType.length() > 0)) {

			if (!(sDisciplineType.equals("NO-LIMIT"))) {
				if (displayLimitBuff.length() > 0) {
					displayLimitBuff.append(", ");
					physicalLimitBuff.append(" AND ");
				}

				displayLimitBuff.append(((String) quickSearchDiplayOptions.get("DI:" + sDisciplineType))).append(" only");
				physicalLimitBuff.append("(").append(sDisciplineType).append(" WN DI)");
			}

		}

		if (sLanguage != null && sLanguage.length() > 0) {

			if (!sLanguage.equals("NO-LIMIT")) {
				if (displayLimitBuff.length() > 0) {
					displayLimitBuff.append(", ");
					physicalLimitBuff.append(" AND ");
				}

				displayLimitBuff.append(((String) quickSearchDiplayOptions.get(sLanguage))).append(" only");
				physicalLimitBuff.append("(").append(sLanguage).append(" WN LA)");
			}
		}

		this.displayLimits = displayLimitBuff.toString();
		this.physicalLimits = physicalLimitBuff.toString();
	}

	private void buildYearRange() {
		if ((sStartYear == null) || (sEndYear == null)) {
			DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

			Map<String, String> startEndYears = databaseConfig.getStartEndYears(credentials, mask);
			sStartYear = (sStartYear == null) ? (String) startEndYears.get(DatabaseConfig.STARTYEAR) : sStartYear;
			sEndYear = (sEndYear == null) ? (String) startEndYears.get(DatabaseConfig.ENDYEAR) : sEndYear;
		}

		if (sEndYear != null && sStartYear != null && sStartYear.length() > 0 && sEndYear.length() > 0) {
			StringBuffer yearBuffer = new StringBuffer();
			yearBuffer.append("(").append(sStartYear).append("-").append(sEndYear).append(" WN YR)");
			this.yearRange = yearBuffer.toString();
		}
	}

	private void buildSearchBase() {
		if (sSearchType.equals(TYPE_QUICK) || sSearchType.equals(TYPE_BOOK)) {
			StringBuffer displayBase = new StringBuffer();
			StringBuffer physicalBase = new StringBuffer();
			StringBuffer exp1Display = null;
			StringBuffer exp2Display = null;
			StringBuffer exp3Display = null;
			StringBuffer exp1Physical = null;
			StringBuffer exp2Physical = null;
			StringBuffer exp3Physical = null;
			StringBuffer extDisplay = null;
			StringBuffer extPhysical = null;

			if (sSeaPhr1 != null && sSeaPhr1.length() > 0) {
				exp1Display = new StringBuffer();
				exp1Physical = new StringBuffer();

				if (sSeaOpt1.equals("NO-LIMIT") || sSeaOpt1.length() == 0) {
					exp1Display.append("((").append(sSeaPhr1).append(") WN ").append("All fields").append(")");
					exp1Physical.append("((").append(sSeaPhr1).append(") WN ALL)");
				} else {
					exp1Display.append("(");
					if (sSeaOpt1.equals("CV")) {
						exp1Display.append(addMoreCV(sSeaPhr1));
					} else {
						exp1Display.append("(").append(sSeaPhr1).append(") WN ").append(sSeaOpt1);
					}
					exp1Display.append(")");
					exp1Physical.append(exp1Display.toString());
				}
			}

			if (sSeaPhr2 != null && sSeaPhr2.length() > 0) {
				exp2Display = new StringBuffer();
				exp2Physical = new StringBuffer();

				if (sSeaOpt2.equals("NO-LIMIT") || sSeaOpt2.length() == 0) {
					exp2Display.append("((" + sSeaPhr2 + ") WN All fields)");
					exp2Physical.append("((" + sSeaPhr2 + ") WN ALL)");
				} else {
					// exp2Display.append("(");
					if (sSeaOpt2.equals("CV")) {
						exp2Display.append(addMoreCV(sSeaPhr2));
					} else {
						exp2Display.append("((" + sSeaPhr2 + ") WN " + sSeaOpt2 + ")");
					}
					// exp2Display.append(")");
					exp2Physical.append(exp2Display.toString());
				}
			}

			if (sSeaPhr3 != null && sSeaPhr3.length() > 0) {
				exp3Display = new StringBuffer();
				exp3Physical = new StringBuffer();

				if (sSeaOpt3.equals("NO-LIMIT") || sSeaOpt3.length() == 0) {
					exp3Display.append("((" + sSeaPhr3 + ") WN All fields)");
					exp3Physical.append("((" + sSeaPhr3 + ") WN ALL)");

				} else {
					// exp3Display.append("(");
					if (sSeaOpt3.equals("CV")) {
						exp3Display.append(addMoreCV(sSeaPhr3));
					} else {
						exp3Display.append("((" + sSeaPhr3 + ") WN " + sSeaOpt3 + ")");
					}
					// exp3Display.append(")");
					exp3Physical.append(exp3Display.toString());
				}
			}

			if (exp1Display != null && exp2Display != null && exp3Display != null) {
				displayBase.append("((" + exp1Display.toString() + " " + sBool1 + " " + exp2Display.toString() + ") " + sBool2 + " " + exp3Display.toString()
						+ ")");
				physicalBase.append("((" + exp1Physical.toString() + " " + sBool1 + " " + exp2Physical.toString() + ") " + sBool2 + " "
						+ exp3Physical.toString() + ")");

			} else if (exp1Display != null && exp2Display != null) {
				displayBase.append("(" + exp1Display.toString() + " " + sBool1 + " " + exp2Display.toString() + ")");
				physicalBase.append("(" + exp1Physical.toString() + " " + sBool1 + " " + exp2Physical.toString() + ")");

			} else if (exp1Display != null && exp3Display != null) {
				displayBase.append("(" + exp1Display.toString() + " " + sBool1 + " " + exp3Display.toString() + ")");
				physicalBase.append("(" + exp1Physical.toString() + " " + sBool1 + " " + exp3Physical.toString() + ")");

			} else if (exp2Display != null && exp3Display != null) {
				displayBase.append("(" + exp2Display.toString() + " " + sBool1 + " " + exp3Display.toString() + ")");
				physicalBase.append("(" + exp2Display.toString() + " " + sBool1 + " " + exp3Display.toString() + ")");

			} else if (exp1Display != null) {
				displayBase = exp1Display;
				physicalBase = exp1Physical;
			}

			if (searchWords != null && booleans != null && sections != null) {
				for (int i = 0; i < searchWords.length; i++) {
					String searchWord = searchWords[i];
					if (!GenericValidator.isBlankOrNull(searchWord)) {
						extDisplay = new StringBuffer();
						extPhysical = new StringBuffer();
						if (sections[i].equals("NO-LIMIT") || sections[i].length() == 0) {
							extDisplay.append("((" + searchWord + ") WN All fields)");
							extPhysical.append("((" + searchWord + ") WN ALL)");

						} else {
							if (sections[i].equals("CV")) {
								extDisplay.append(addMoreCV(searchWord));
							} else {
								extDisplay.append("((" + searchWord + ") WN " + sections[i] + ")");
							}
							extPhysical.append(extDisplay.toString());
						}
						displayBase.insert(0, "(");
						displayBase.append(" " + booleans[i] + " ");
						displayBase.append(extDisplay);
						displayBase.append(")");

						physicalBase.insert(0, "(");
						physicalBase.append(" " + booleans[i] + " ");
						physicalBase.append(extPhysical);
						physicalBase.append(")");
					}
				}
			}

			this.displayBase = displayBase.toString();
			this.physicalBase = physicalBase.toString();
		} else {
			this.displayBase = sSeaPhr1;
			this.physicalBase = "(" + sSeaPhr1 + ")";
		}
	}

	public String addMoreCV(String searchPhase) {

		StringBuffer queryBuffer = new StringBuffer();
		if (((mask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) || ((mask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK)) {
			if (searchPhase.indexOf('"') > -1) {
				queryBuffer.append("(((" + searchPhase + ") WN CV) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-A\") WN CVA) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-N\") WN CVN) OR ");
				queryBuffer.append("((" + searchPhase + ") WN CVM) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-P\") WN CVP) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-N\") WN CVMN) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-A\") WN CVMA) OR ");
				queryBuffer.append("((\"" + searchPhase.replaceAll("\"", "") + "-P\") WN CVMP)) ");
			} else if (searchPhase.indexOf('{') > -1 && searchPhase.indexOf('}') > -0) {
				queryBuffer.append("(((" + searchPhase + ") WN CV) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-A}") + ") WN CVA) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-N}") + ") WN CVN) OR ");
				queryBuffer.append("((" + searchPhase + ") WN CVM) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-P}") + ") WN CVP) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-N}") + ") WN CVMN) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-A}") + ") WN CVMA) OR ");
				queryBuffer.append("((" + searchPhase.replace("}", "-P}") + ") WN CVMP)) ");
			} else {
				queryBuffer.append("(((" + searchPhase + ") WN CV) OR ");
				queryBuffer.append("((" + searchPhase + "-A) WN CVA) OR ");
				queryBuffer.append("((" + searchPhase + "-N) WN CVN) OR ");
				queryBuffer.append("((" + searchPhase + ") WN CVM) OR ");
				queryBuffer.append("((" + searchPhase + "-P) WN CVP) OR ");
				queryBuffer.append("((" + searchPhase + "-N) WN CVMN) OR ");
				queryBuffer.append("((" + searchPhase + "-A) WN CVMA) OR ");
				queryBuffer.append("((" + searchPhase + "-P) WN CVMP)) ");
			}
		} else {
			queryBuffer.append("((" + searchPhase + ") WN CV) ");
		}

		return queryBuffer.toString();
	}

	public void setEmailAlertWeek(String strValue) {
		strEmailAlertWeek = notNull(strValue);
	}

	public String getEmailAlertWeek() {
		return strEmailAlertWeek;
	}

	public void setCCList(String ccList) {
		this.ccList = notNull(ccList);
	}

	public String getCCList() {
		return this.ccList;
	}

	/**
	 * Set SeaPhr1
	 *
	 * @param String
	 *            inPhr1
	 * @return void
	 */

	public void setSeaPhr1(String inPhr1) {

		inPhr1 = Entity.prepareString(inPhr1);
		this.sSeaPhr1 = notNull(inPhr1);
	}

	public String getSeaPhr1() {
		return this.sSeaPhr1;
	}

	/**
	 * Set SeaOpt1
	 *
	 * @param String
	 *            inOpt1
	 * @return void
	 */

	public void setSeaOpt1(String inOpt1) {
		this.sSeaOpt1 = notNull(inOpt1);
	}

	public String getSeaOpt1() {
		return this.sSeaOpt1;
	}

	/**
	 * Set Bool1
	 *
	 * @param String
	 *            inBool1
	 * @return void
	 */

	public void setBool1(String inBool1) {
		this.sBool1 = notNull(inBool1);
	}

	public String getBool1() {
		return this.sBool1;
	}

	/**
	 * Set SeaPhr2
	 *
	 * @param String
	 *            inPhr2
	 * @return void
	 */

	public void setSeaPhr2(String inPhr2) {

		inPhr2 = Entity.prepareString(inPhr2);
		this.sSeaPhr2 = notNull(inPhr2);
	}

	public String getSeaPhr2() {
		return this.sSeaPhr2;
	}

	/**
	 * Set SeaOpt2
	 *
	 * @param String
	 *            inOpt2
	 * @return void
	 */

	public void setSeaOpt2(String inOpt2) {
		this.sSeaOpt2 = notNull(inOpt2);
	}

	public String getSeaOpt2() {
		return this.sSeaOpt2;
	}

	/**
	 * Set Bool2
	 *
	 * @param String
	 *            inBool2
	 * @return void
	 */

	public void setBool2(String inBool2) {
		this.sBool2 = notNull(inBool2);
	}

	public String getBool2() {
		return this.sBool2;
	}

	/**
	 * Set SeaPhr3
	 *
	 * @param String
	 *            inPhr3
	 * @return void
	 */

	public void setSeaPhr3(String inPhr3) {

		inPhr3 = Entity.prepareString(inPhr3);
		this.sSeaPhr3 = notNull(inPhr3);
	}

	public String getSeaPhr3() {
		return this.sSeaPhr3;
	}

	/**
	 * Set SeaOpt3
	 *
	 * @param String
	 *            inOpt3
	 * @return void
	 */

	public void setSeaOpt3(String inOpt3) {
		this.sSeaOpt3 = notNull(inOpt3);
	}

	public String getSeaOpt3() {
		return this.sSeaOpt3;
	}

	/**
	 * Gets the search phrase
	 *
	 * @return String search phrase of session data
	 */
	private String getSearchPhrase() throws InvalidArgumentException {

		StringBuffer xmlPhrase = new StringBuffer();

		if (sSeaPhr1 != null) {
			xmlPhrase.append("<SEARCH-PHRASE-1>").append("<![CDATA[").append(sSeaPhr1).append("]]>").append("</SEARCH-PHRASE-1>");
		}

		if (sSeaOpt1 != null) {
			xmlPhrase.append("<SEARCH-OPTION-1>").append("<![CDATA[").append(sSeaOpt1).append("]]>").append("</SEARCH-OPTION-1>");
		}

		if (sBool1 != null) {
			xmlPhrase.append("<BOOLEAN-1>").append("<![CDATA[").append(sBool1).append("]]>").append("</BOOLEAN-1>");
		}

		if (sSeaPhr2 != null) {
			xmlPhrase.append("<SEARCH-PHRASE-2>").append("<![CDATA[").append(sSeaPhr2).append("]]>").append("</SEARCH-PHRASE-2>");
		}

		if (sSeaOpt2 != null) {
			xmlPhrase.append("<SEARCH-OPTION-2>").append("<![CDATA[").append(sSeaOpt2).append("]]>").append("</SEARCH-OPTION-2>");
		}

		if (sBool2 != null) {
			xmlPhrase.append("<BOOLEAN-2>").append("<![CDATA[").append(sBool2).append("]]>").append("</BOOLEAN-2>");
		}

		if (sSeaPhr3 != null) {
			xmlPhrase.append("<SEARCH-PHRASE-3>").append("<![CDATA[").append(sSeaPhr3).append("]]>").append("</SEARCH-PHRASE-3>");
		}

		if (sSeaOpt3 != null) {
			xmlPhrase.append("<SEARCH-OPTION-3>").append("<![CDATA[").append(sSeaOpt3).append("]]>").append("</SEARCH-OPTION-3>");
		}

		if (booleans != null && booleans.length > 0) {
			xmlPhrase.append("<BOOLEANS>").append("<![CDATA[").append(this.getBooleansStr()).append("]]>").append("</BOOLEANS>");
		}

		if (searchWords != null && searchWords.length > 0) {
			xmlPhrase.append("<SEARCH-PHRASES>").append("<![CDATA[").append(this.getSearchWordsStr()).append("]]>").append("</SEARCH-PHRASES>");
		}

		if (sections != null && sections.length > 0) {
			xmlPhrase.append("<SEARCH-OPTIONS>").append("<![CDATA[").append(this.getSectionsStr()).append("]]>").append("</SEARCH-OPTIONS>");
		}

		return xmlPhrase.toString();
	}

	public void clearDupSet() {
		if (dupSet != null) {
			dupSet.clear();
		}
	}

	/**
	 * adds dedup criteria
	 *
	 * @return void
	 * @param String
	 *            dupString
	 */
	public void addDupSet(String criteria) {
		if (!dupSet.contains(criteria)) {
			this.dupSet.add(criteria);
		}
	}

	/**
	 * set the saved deduplication criteria
	 *
	 * @return void
	 * @param String
	 *            dupString
	 */
	public void setDupSet(String dupString) {
		if ((dupString != null) && (!dupString.equals(""))) {
			StringTokenizer st = new StringTokenizer(dupString, "~", false);
			while (st.hasMoreTokens()) {
				dupSet.add(st.nextToken().trim());
			}
		}
	}

	/**
	 * gets the dupSet criteria
	 *
	 * @return List
	 */
	public List<String> getDupSet() {
		return this.dupSet;
	}

	public String getDupSetString() {
		StringBuffer sb = new StringBuffer();
		List<String> dupSet = getDupSet();
		for (int i = 0; i < dupSet.size(); i++) {
			String criteria = (String) dupSet.get(i);
			if (i > 0) {
				sb.append("~");
			}
			sb.append(criteria);
		}
		return sb.toString();
	}

	/**
	 * sets the dedup database
	 *
	 * @return void
	 * @param String
	 *            inStr
	 */
	public void setDeDupDB(String inStr) {
		this.deDupDB = inStr;
	}

	/**
	 * gets the dedup database
	 *
	 * @return String
	 */
	public String getDeDupDB() {
		return this.deDupDB;
	}

	/**
	 * sets the deDup state
	 *
	 * @return void
	 * @param boolean inBool
	 */
	public void setDeDup(boolean inBool) {
		this.deDup = inBool;
	}

	/**
	 * sets the deDup state from a string
	 *
	 * @return void
	 * @param String
	 *            inStr
	 */
	public void setDeDup(String inStr) {
		if ((inStr != null) && (inStr.equals("true"))) {
			this.deDup = true;
		} else {
			this.deDup = false;
		}
	}

	/**
	 * gets the deDup state
	 *
	 * @return boolean
	 */
	public boolean isDeDup() {
		return this.deDup;
	}

	/**
	 * gets the deDup state as a string
	 *
	 * @return String
	 */
	public String isDeDupString() {
		if (this.deDup) {
			return "true";
		}
		return "false";
	}

	/**
	 * sets the search query
	 *
	 * @return void
	 * @param String
	 *            sSearchQuery
	 */

	public void setSearchQuery(String sSeaQue) {

		sSearchQuery = sSeaQue;

	}

	public void setSearchPhrase(String sSeaPhr1, String sSeaOpt1, String sBool1, String sSeaPhr2, String sSeaOpt2, String sBool2, String sSeaPhr3,
			String sSeaOpt3) {
		setSeaPhr1(sSeaPhr1);
		setSeaOpt1(sSeaOpt1);
		setBool1(sBool1);
		setSeaPhr2(sSeaPhr2);
		setSeaOpt2(sSeaOpt2);
		setBool2(sBool2);
		setSeaPhr3(sSeaPhr3);
		setSeaOpt3(sSeaOpt3);
	}

	public void setSearchPhrase(ISearchForm searchform) {
		setSeaPhr1(searchform.getSearchWord1());
		setSeaOpt1(searchform.getSection1());
		setBool1(searchform.getBoolean1());
		setSeaPhr2(searchform.getSearchWord2());
		setSeaOpt2(searchform.getSection2());
		setBool2(searchform.getBoolean2());
		setSeaPhr3(searchform.getSearchWord3());
		setSeaOpt3(searchform.getSection3());
		setSearchWords(searchform.getSearchWords());
		setBooleans(searchform.getBooleans());
		setSections(searchform.getSections());
	}

	public String[] getSearchWords() {
		return searchWords;
	}

	public String getSearchWordsStr() {
		return join(searchWords, TERM_SEPARATOR);
	}

	public void setSearchWords(String searchWords) {
		// Split from delimited string
		this.searchWords = (searchWords == null) ? null : searchWords.split("\\" + TERM_SEPARATOR);
	}

	public void setSearchWords(String[] searchWords) {
		// Split from delimited string
		this.searchWords = searchWords;
	}

	public String[] getBooleans() {
		return booleans;
	}

	public String getBooleansStr() {
		return join(booleans, TERM_SEPARATOR);
	}

	public void setBooleans(String[] booleans) {
		this.booleans = booleans;
	}

	public void setBooleans(String booleans) {
		// Split from delimited string
		this.booleans = (booleans == null) ? null : booleans.split("\\" + TERM_SEPARATOR);
	}

	public String[] getSections() {
		return sections;
	}

	public String getSectionsStr() {
		return join(sections, TERM_SEPARATOR);
	}

	public void setSections(String[] sections) {
		this.sections = sections;
	}

	public void setSections(String sections) {
		// Split from delimited string
		this.sections = (sections == null) ? null : sections.split("\\" + TERM_SEPARATOR);
	}

	private String join(String[] delimarr, String delim) {
		if (delimarr == null)
			return null;
		StringBuffer join = new StringBuffer();
		boolean first = true;
		for (String s : delimarr) {
			if (first)
				first = false;
			else
				join.append(delim);
			join.append(s);
		}
		return join.toString();

	}

	/**
	 * Gets the search query in string format
	 *
	 * @return String sSearchQuery
	 * @throws SearchException
	 * @throws InfrastructureException
	 */
	public String getSearchQuery() throws SearchException, InfrastructureException {
		if (this.sSearchQuery.length() == 0) {
			compile();
		}

		return sSearchQuery;
	}

	/**
	 * sets the number of records that the search results
	 *
	 * @return void
	 * @param String
	 *            sRecCount
	 */
	public void setRecordCount(String sRecCount) throws InvalidArgumentException {
		sRecordCount = notNull(sRecCount);
	}

	/**
	 * gets the number of records that the search returns
	 *
	 * @return String sRecordCount
	 */

	public String getRecordCount() {
		return sRecordCount;
	}

	/**
	 * sets the start year of publish for the search
	 *
	 * @return void
	 * @param String
	 *            sStartYear
	 * @exception InvalidArgumentException
	 *                Thrown if the input parameter is null
	 */

	public void setStartYear(String sStYear) throws InvalidArgumentException {

		sStartYear = notNull(sStYear);
	}

	/**
	 * returns the publish year from which the search started
	 *
	 * @return String sStartYear
	 */

	public String getStartYear() {
		return sStartYear;
	}

	/**
	 * sets the end year of publish for the search
	 *
	 * @return void
	 * @param String
	 *            sEYear
	 * @exception InvalidArgumentException
	 *                Thrown if the input parameter is null
	 */

	public void setEndYear(String sEYear) throws InvalidArgumentException {

		sEndYear = notNull(sEYear);
	}

	/**
	 * returns the publish year from which the search should end
	 *
	 * @return Calendar sEndYear
	 */

	public String getEndYear() {
		return sEndYear;
	}

	/**
	 * sets the data base name in which the search has to happen
	 *
	 * @return void
	 * @param String
	 *            sDBase
	 * @exception InvalidArgumentException
	 *                Thrown if the input parameter is null
	 */

	public void setDataBase(int mask) throws InvalidArgumentException {

		this.mask = mask;
	}

	/**
	 * returns the data base in which the search happened
	 *
	 * @return String sDataBase
	 */
	public int getDataBase() {
		return this.mask;
	}

	/**
	 * sets the Language in which the search has to happen
	 *
	 * @return void
	 * @param String
	 *            sLanguage
	 */
	public void setLanguage(String lang) throws InvalidArgumentException {

		sLanguage = notNull(lang);
	}

	/**
	 * returns the Language in which the search happened
	 *
	 * @return String sLanguage
	 */
	public String getLanguage() {
		return sLanguage;
	}

	/**
	 * sets the auto stemming property for the search
	 *
	 * @return void
	 * @param String
	 *            sAutoStemming
	 */
	public void setAutoStemming(String sAuSt) {
		sAutoStemming = notNull(sAuSt);

	}

	/**
	 * returns the auto stemming property of the search
	 *
	 * @return String sAutoStemming
	 */
	public String getAutoStemming() {
		return sAutoStemming;
	}

	/**
	 * sets the sort option
	 *
	 * @return void
	 * @param String
	 *            sSortOption
	 */
	public void setSortOption(Sort sSortOpt) throws InvalidArgumentException {
		this.sortOption = sSortOpt;
	}

	/**
	 * returns the sort option
	 *
	 * @return String sSortOption
	 */
	public Sort getSortOption() {

		if (this.sortOption == null) {
			this.sortOption = Sort.getDefaultSortOption();
		}

		return this.sortOption;
	}

	/**
	 * sets the search type
	 *
	 * @return void
	 * @param String
	 *            sSeaType
	 */
	public void setSearchType(String sSeaType) throws InvalidArgumentException {
		sSearchType = notNull(sSeaType);
	}

	/**
	 * returns the search Type
	 *
	 * @return String sSearchType
	 */
	public String getSearchType() {
		return sSearchType;
	}

	/**
	 * sets the Query Id
	 *
	 * @return void
	 * @param String
	 *            sQueryId
	 */
	public void setID(String sQueryId) throws InvalidArgumentException {

		if (sQueryId == null) {
			throw new InvalidArgumentException("Query Id should not be NULL");
		}

		sId = sQueryId;
	}

	/**
	 * returns the Query Id
	 *
	 * @return String sId
	 */
	public String getID() {
		return sId;
	}

	/**
	 * sets the Display Query
	 *
	 * @return void
	 * @param String
	 *            sDisplayQuery
	 */
	public void setDisplayQuery(String sDisQuery) throws InvalidArgumentException {

		if (sDisQuery == null) {
			throw new InvalidArgumentException("Display Query should not be NULL");
		}

		sDisplayQuery = sDisQuery;
	}

	/**
	 * returns the Display Query
	 *
	 * @return String sDisQuery
	 */
	public String getDisplayQuery() {
		return sDisplayQuery;
	}

	/**
	 * sets the Physical Query
	 *
	 * @return void
	 * @param String
	 *            sPhysicalQuery
	 */
	public void setPhysicalQuery(String sPhysicalQuery) throws InvalidArgumentException {

		if (sPhysicalQuery == null) {
			throw new InvalidArgumentException("Physical Query should not be NULL");
		}

		this.sPhysicalQuery = sPhysicalQuery;
	}

	/**
	 * returns the Physical Query
	 *
	 * @return String sPhysicalQuery
	 */
	public String getPhysicalQuery() throws InfrastructureException {
		if (sPhysicalQuery.length() == 0) {
			try {
				compile();
			} catch (Exception e) {
				throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
			}
		}

		return sPhysicalQuery;
	}

	public void setIntermediateQuery(String querystring) {
		if (querystring == null) {
			// do nothing
			return;
			// throw new
			// InvalidArgumentException("Intermediate Query should not be NULL");
		}

		this.intermediatequery = querystring;
	}

	public String getIntermediateQuery() throws InfrastructureException {
		if ((intermediatequery != null) && (intermediatequery.length() == 0)) {
			try {
				compile();
			} catch (Exception e) {
				throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
			}
		}

		return intermediatequery;
	}

	/**
	 * sets the Document Type
	 *
	 * @return void
	 * @param String
	 *            sDocType
	 */
	public void setDocumentType(String sDType) {
		sDocType = notNull(sDType);
	}

	/**
	 * returns the Document Type
	 *
	 * @return String sDocType
	 */
	public String getDocumentType() {
		return sDocType;
	}

	public void setReferexCollections(Limiter limits) {
		this.referexCollections = limits;
	}

	/**
	 * returns the Treatment Type
	 *
	 * @return String sTreatmentType
	 */
	public Limiter getReferexCollections() {
		return (referexCollections == null) ? new ReferexLimiter() : referexCollections;
	}

	/**
	 * sets the Treatment Type
	 *
	 * @return void
	 * @param String
	 *            sTreatType
	 */
	public void setTreatmentType(String sTreatType) {
		this.sTreatmentType = notNull(sTreatType);
	}

	/**
	 * returns the Treatment Type
	 *
	 * @return String sTreatmentType
	 */
	public String getTreatmentType() {
		return sTreatmentType;
	}

	/** WRAPPER ADDED sets the Last Updates Count from JSP Pages */
	public void setLastUpdatesCount(String sLFUType) throws InvalidArgumentException {
		setLastFourUpdates(sLFUType);
	}

	/**
	 * sets the Last Four Updates Type
	 *
	 * @return void
	 * @param String
	 *            sLFUType
	 */
	public void setLastFourUpdates(String sLFUType) throws InvalidArgumentException {
		this.sLastFourUpdates = notNull(sLFUType);
	}

	/**
	 * returns the LastFourUpdates Type
	 *
	 * @return String sLastFourUpdates
	 */
	public String getLastFourUpdates() {
		return sLastFourUpdates;
	}

	/**
	 * sets the DisciplineType
	 *
	 * @return void
	 * @param String
	 *            sDisciplineType
	 */
	public void setDisciplineType(String discType) throws InvalidArgumentException {

		this.sDisciplineType = notNull(discType);
	}

	/**
	 * returns the DisciplineType
	 *
	 * @return String sDisciplineType
	 */
	public String getDisciplineType() {
		return sDisciplineType;
	}

	/**
	 * sets the EmailAlert is present
	 *
	 * @return void
	 * @param String
	 *            emailAlert
	 */
	public void setEmailAlert(String emailAlert) throws InvalidArgumentException {
		this.emailAlert = emailAlert;
	}

	/**
	 * returns the EmailAlert
	 *
	 * @return String emailAlert
	 */
	public String getEmailAlert() {
		return emailAlert;
	}

	/**
	 * sets the SavedSearch is present
	 *
	 * @return void
	 * @param String
	 *            savedSearch
	 */
	public void setSavedSearch(String savedSearch) throws InvalidArgumentException {
		this.savedSearch = savedSearch;
	}

	/**
	 * returns the SavedSearch is present.
	 *
	 * @return String savedSearch
	 */
	public String getSavedSearch() {
		return savedSearch;
	}

	/**
	 * sets the SavedSearchID to a saved search
	 *
	 * @return void
	 * @param String
	 *            savedSearchID
	 */
	// public void setSavedSearchID(String savedSearchID)
	// throws InvalidArgumentException {
	// this.savedSearchID = savedSearchID;
	// }
	//
	// /** returns the SavedSearchID is present.
	// *
	// * @return String savedSearchID
	// */
	// public String getSavedSearchID() {
	// return savedSearchID;
	// }

	/**
	 * Return the Query objects in string format
	 *
	 * @return String Query.
	 */
	public String toString() {

		String sPhrase = "";

		try {
			sPhrase = getSearchPhrase();
		} catch (Exception e) {
			sPhrase = "Search Exception";
		}

		return (new StringBuffer().append(sPhrase).append(getRecordCount()).append(getLanguage()).append(getStartYear()).append(getEndYear())
				.append(getDataBase()).append(getAutoStemming()).append(getSortOption()).append(getSearchType()).append(getID()).append(getDisplayQuery())
				// .append(getPhysicalQuery())
				.append(getDocumentType()).append(getTreatmentType()).append(getDisciplineType()).append(getLastFourUpdates()).append(getDupSet().toString())
				.append(getDeDupDB()).append(isDeDupString()).toString());
	}

	/**
	 * Return the complete QueryData as an xml string.
	 *
	 * @return String The complete xml of this Object.
	 */
	public String toXMLString() throws InfrastructureException, IOException {
		/*
		 * String xmloutfilename = System.getProperty("java.io.tmpdir") +
		 * System.getProperty("file.separator") + getID() + ".xml";
		 * System.out.println("Logging query XML to: " + xmloutfilename); Writer
		 * fileWriter = new FileWriter(xmloutfilename); toXML(fileWriter);
		 * fileWriter.close();
		 */
		Writer strWriter = new StringWriter();
		toXML(strWriter);
		strWriter.close();
		return strWriter.toString();
	}

	private String getSubcountXML() {
		StringBuffer buf = new StringBuffer();

		if ((subcounts != null) && !(subcounts.equals(StringUtil.EMPTY_STRING))) {
			buf.append("<SC>");
			if (display) {
				StringTokenizer tokens = new StringTokenizer(subcounts, ",");
				while (tokens.hasMoreTokens()) {
					String count = tokens.nextToken();
					StringTokenizer pair = new StringTokenizer(count, ":");
					String db = pair.nextToken();
					String dbcount = pair.nextToken();
					buf.append("<CN DB=\"").append(db).append("\" NUM=\"").append(dbcount).append("\"/>");
				}
			} else {
				buf.append(subcounts);
			}
			buf.append("</SC>");
		}

		return buf.toString();

	}

	/**
	 * Compares the Object passed
	 *
	 * @param Object
	 *            An object of this class type
	 * @return int Zero or less than zero or greater than zero on comparizen(?)
	 *         comparison
	 * @see http://java.sun.com/j2se/1.4.2/docs/api/java/lang/Comparable.html
	 */
	public int compareTo(Object o)
	// Do not report Runtime exception subclass! ==> throws ClassCastException
	{

		if (o == null) {
			// from Sun Javadoc "Note that null is not an instance of any class,
			// and e.compareTo(null) should throw a NullPointerException
			// even though e.equals(null) returns false."
			throw new NullPointerException("null is not an instance of Query object");
		}

		// Original Implementation - if query ids are equal, then compareTo
		// returns equal
		Query que = (Query) o;
		if ((this.getID()).equals(que.getID())) {
			return 0;
		}

		// Otherwise, queries can still be equal if all these fields match
		int result = -1;
		try {
			result = ((this.getSearchPhrase()).equals(que.getSearchPhrase()) && (this.getLanguage()).equals(que.getLanguage())
					&& (this.getStartYear()).equals(que.getStartYear()) && (this.getEndYear()).equals(que.getEndYear())
					&& (this.getDataBase() == que.getDataBase()) && (this.getAutoStemming()).equals(que.getAutoStemming())
					&& (this.getSortOption().getSortField()).equals(que.getSortOption().getSortField())
					&& (this.getSortOption().getSortDirection()).equals(que.getSortOption().getSortDirection())
					&& (this.getSearchType()).equals(que.getSearchType()) && (this.getDocumentType()).equals(que.getDocumentType())
					&& (this.getTreatmentType()).equals(que.getTreatmentType()) && (this.getDisciplineType()).equals(que.getDisciplineType()) ? 0 : -1);
		} catch (InvalidArgumentException e) {
			result = -1;
		}

		return result;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Query))
			return false;

		return this.compareTo(o) == 0;
	}

	/**
	 * Overrides Object.
	 *
	 * @param Object
	 *            reference of QueryData
	 * @return boolean true if equal
	 */
	// public boolean equals(Object o)
	// {
	// Query oQue = (Query) o;
	// if (this.getID().equals(oQue.getID()))
	// {
	// return true;
	// }
	// return false;
	// }

	/**
	 * generic method for setting attributes that are anticipated
	 */
	public void setMethod(String argumentName, String argumentValue) {

		sXMLString.append("<" + argumentName + ">");
		sXMLString.append(argumentValue);
		sXMLString.append("</" + argumentName + ">");

	}

	private String notNull(String s) {
		if (s == null) {
			return "";
		}

		return s;
	}

	/**
	 * @return
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param string
	 */
	public void setUserID(String string) {
		userID = string;
	}

	/**
	 * @return
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param string
	 */
	public void setSessionID(String string) {
		sessionID = string;
	}

	/**
	 * @return
	 */
	public Date getSavedDate() {
		return saveddate;
	}

	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public String getDisplaySavedDate() {
		if (saveddate != null) {
			return formatter.format(saveddate);
		} else {
			return "";
		}
	}

	/**
	 * @param date
	 */
	public void setSavedDate(Date date) {
		saveddate = date;
	}

	/**
	 * @return
	 */
	public String getVisible() {
		return visible;
	}

	/**
	 * @param string
	 */
	public void setVisible(String string) {
		visible = string;
	}

	public void toXML(Writer out) throws InfrastructureException, IOException {
		String sPhrase = "";
		try {
			sPhrase = getSearchPhrase();
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		}

		out.write("<SESSION-DATA>");
		out.write("<EMAILALERTWEEK>");
		if (strEmailAlertWeek != null) {
			out.write(strEmailAlertWeek);
		}
		out.write("</EMAILALERTWEEK>");
		out.write("<SEARCH-PHRASE>");
		if (sPhrase != null) {
			out.write(sPhrase);
		}
		out.write("</SEARCH-PHRASE>");

		out.write("<RESULTS-COUNT>");
		if (getRecordCount() != null) {
			out.write(getRecordCount());
		}
		out.write("</RESULTS-COUNT>");
		out.write("<LANGUAGE>");
		if (getLanguage() != null) {
			out.write(getLanguage());
		}
		out.write("</LANGUAGE>");
		out.write("<START-YEAR>");
		if (getStartYear() != null) {
			out.write(getStartYear());
		}
		out.write("</START-YEAR>");
		out.write("<END-YEAR>");
		if (getEndYear() != null) {
			out.write(getEndYear());
		}
		out.write("</END-YEAR>");
		out.write("<DATABASE-MASK>");
		out.write(String.valueOf(mask));
		out.write("</DATABASE-MASK>");
		out.write("<AUTOSTEMMING>");
		if (getAutoStemming() != null) {
			out.write(getAutoStemming());
		}
		out.write("</AUTOSTEMMING>");

		// jam - sort object contains XML representation of sort fields
		if (getSortOption() != null) {
			out.write(getSortOption().toXML());
		}

		out.write("<SEARCH-TYPE>");
		if (getSearchType() != null) {
			out.write(getSearchType());
		}
		out.write("</SEARCH-TYPE>");

		// jam - added properties which were being 'inserted' in SavedSearches
		// class
		// getXMLSavedSearches() method
		out.write("<SAVEDSEARCH>");
		if (getSavedSearch() != null) {
			out.write(getSavedSearch());
		}
		out.write("</SAVEDSEARCH>");
		out.write("<SAVEDSEARCH-DATE>");
		if (getDisplaySavedDate() != null) {
			out.write(getDisplaySavedDate());
		}
		out.write("</SAVEDSEARCH-DATE>");
		out.write("<EMAIL-ALERT>");
		if (getEmailAlert() != null) {
			out.write(getEmailAlert());
		}
		out.write("</EMAIL-ALERT>");
		out.write("<CC-LIST>");
		if (getCCList() != null) {
			out.write(getCCList());
		}
		out.write("</CC-LIST>");
		out.write("<VISIBLE>");
		if (getVisible() != null) {
			out.write(getVisible());
		}
		out.write("</VISIBLE>");

		out.write("<USER-ID>");
		if (getUserID() != null) {
			out.write(getUserID());
		}
		out.write("</USER-ID>");
		out.write("<SESSION-ID>");
		if (getSessionID() != null) {
			out.write(getSessionID());
		}
		out.write("</SESSION-ID>");
		out.write("<QUERY-ID>");
		if (getID() != null) {
			out.write(getID());
		}
		out.write("</QUERY-ID>");
		out.write("<DISPLAY-QUERY>");
		if (getSearchType() != null) {
			out.write("<![CDATA[");
			if (getSearchType().equals(TYPE_EASY)) {
				if (getIntermediateQuery() != null) {
					out.write(getIntermediateQuery());
				}
			} else {
				if (getDisplayQuery() != null) {
					out.write(getDisplayQuery());
				}
			}
			out.write("]]>");
		}
		out.write("</DISPLAY-QUERY>");

		out.write("<PHYSICAL-QUERY>");
		if (getPhysicalQuery() != null) {
			out.write("<![CDATA[");
			out.write(getPhysicalQuery());
			out.write("]]>");
		}
		out.write("</PHYSICAL-QUERY>");

		out.write("<I-QUERY>");
		if (getIntermediateQuery() != null) {
			out.write("<![CDATA[");
			out.write(getIntermediateQuery());
			out.write("]]>");
		}
		out.write("</I-QUERY>");

		if (getReferexCollections() != null) {
			out.write(getReferexCollections().toXML());
		}

		out.write("<DOCUMENT-TYPE>");
		if (getDocumentType() != null) {
			out.write(getDocumentType());
		}
		out.write("</DOCUMENT-TYPE>");
		out.write("<TREATMENT-TYPE>");
		if (getTreatmentType() != null) {
			out.write(getTreatmentType());
		}
		out.write("</TREATMENT-TYPE>");
		out.write("<DISCIPLINE-TYPE>");
		if (getDisciplineType() != null) {
			out.write(getDisciplineType());
		}
		out.write("</DISCIPLINE-TYPE>");
		out.write("<LASTFOURUPDATES>");
		if (getLastFourUpdates() != null) {
			out.write(getLastFourUpdates());
		}
		out.write("</LASTFOURUPDATES>");
		if (getSubcountXML() != null) {
			out.write(getSubcountXML());
		}
		out.write("<DUPSET>");

		for (int i = 0; i < dupSet.size(); i++) {
			out.write("<CRITERIA>");
			String criteria = (String) dupSet.get(i);
			String[] options = criteria.split(":");
			if (options.length == 2) {
				out.write("<FIELDPREF>");
				out.write(options[0]);
				out.write("</FIELDPREF>");
				out.write("<DBPREF>");
				out.write(options[1]);
				out.write("</DBPREF>");
			}
			out.write("</CRITERIA>");
		}
		out.write("</DUPSET>");
		out.write("<DEDUP>");
		if (isDeDupString() != null) {
			out.write(isDeDupString());
		}
		out.write("</DEDUP>");
		out.write("<DEDUPDB>");
		if (getDeDupDB() != null) {
			out.write(getDeDupDB());
		}
		out.write("</DEDUPDB>");

		if (getRefinements() != null) {
			out.write(getRefinements().toXML());
		}
		out.write("</SESSION-DATA>");

		return;
	}


}
