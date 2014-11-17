package org.ei.domain.personalization;

/** project specific imports*/
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.config.ApplicationProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.domain.Sort;
import org.ei.domain.navigators.Refinements;
import org.ei.domain.navigators.ResultsState;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.limiter.ReferexLimiter;
import org.ei.util.StringUtil;

/**
 *
 * Handles Saved Searches related tasks such as
 * <p>
 * a) Storing Query in saved_searches based on USER_ID .
 * </p>
 * <p>
 * b) Retriving Query from SESSION_HISTORY based on Session Id and comparing
 * Queries from saved_searches.
 * </p>
 * <p>
 * c) Clearing and updating Queries in saved_searches for a USER_ID,SEARCH_ID.
 * </p>
 *
 */
/*
 * jam _ removed todayDate and replaced with SYSDATE in SQL statements
 */
public class SavedSearches {
	protected static Log log = LogFactory.getLog(SavedSearches.class);

	public static final int EMAIL_ALERT_COUNT = 125;
	public static final int SAVED_SEARCH_COUNT = 125;

	public static int getEmailAlertCount() {
		return EMAIL_ALERT_COUNT;
	}

	public static int getSavedSearchCount() {
		return SAVED_SEARCH_COUNT;
	}

	private SavedSearches() {
	};

	/**
	 * @return java.lang.String This method gets QRY string string from
	 *         SESSION_HISTORY table for the current sessionID. For each QRY
	 *         string it checks,is it present in the saved_searches tabel.
	 *
	 */
	public static void getUserAlertsXML(String userid, Writer out) throws InfrastructureException {
		getUserSavedSearchesXML(userid, true, out);
	}

	public static void getUserSavedSearchesXML(String userid, Writer out) throws InfrastructureException {
		getUserSavedSearchesXML(userid, false, out);
	}

	private static void getUserSavedSearchesXML(String userid, boolean alerts, Writer out) throws InfrastructureException {
		int emailCount = 0;
		int savedSearchesCount = 0;

		try {
			out.write("<SESSION-HISTORY>");
			int[] counts = getUserSavedSearches(userid, alerts, out);
			out.write("<EMAIL-ALERT-COUNT>" + Integer.toString(counts[1]) + "</EMAIL-ALERT-COUNT>");
			out.write("<SAVEDSEARCHES-COUNT>" + Integer.toString(counts[0]) + "</SAVEDSEARCHES-COUNT>");
			out.write("</SESSION-HISTORY>");
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		}

	}

	public static int saveSearch(Query query) throws InfrastructureException {
		return insertSavedSearch(query);
	}

	/**
	 * @param java
	 *            .lang.String xmlString
	 * @param java
	 *            .lang.Object Query
	 * @return void This method takes xmlString and Query object. From the query
	 *         object it checks if EmailAlert is on or off. Depending on
	 *         EmailAlert it stores search as max search_id for that userID.
	 * @throws InfrastructureException
	 *
	 */
	// jam 10/16/2002
	// bug 13.2 searches not being saved due to special characters...
	// changed over to PreparedStatement to avoid having to encode
	// special characters manually
	public static int insertSavedSearch(Query query) throws InfrastructureException {
		// log.debug("Accessing insertSavedSearch");
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;
		int result = 0;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = (CallableStatement) con
					.prepareCall("{ call SavedSearches_insertSearchRe(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			int intStmtIndex = 1;
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.setString(intStmtIndex++, query.getUserID());
			pstmt.setString(intStmtIndex++, query.getSessionID());
			pstmt.setString(intStmtIndex++, String.valueOf(query.getDataBase()));
			pstmt.setString(intStmtIndex++, query.getSearchType());
			pstmt.setString(intStmtIndex++, query.getEmailAlert());
			pstmt.setString(intStmtIndex++, Query.ON);
			pstmt.setString(intStmtIndex++, query.getEmailAlertWeek());

			String phrase1 = query.getSeaPhr1();
			String display_query = query.getDisplayQuery();
			String refinements = query.getRefinements().toUnlimitedString();
			if ((phrase1.length() >= Searches.UNCOMPRESSED_LIMIT) || (display_query.length() >= Searches.UNCOMPRESSED_LIMIT)
					|| (refinements.length() >= Searches.UNCOMPRESSED_LIMIT)) {
				phrase1 = Searches.COMPRESSION_INDICATOR + StringUtil.zipText(phrase1);
				display_query = Searches.COMPRESSION_INDICATOR + StringUtil.zipText(display_query);
				refinements = Searches.COMPRESSION_INDICATOR + StringUtil.zipText(refinements);
			}

			pstmt.setString(intStmtIndex++, phrase1); // rset.getString("SEARCH_PHRASE_1")
			pstmt.setString(intStmtIndex++, query.getSeaPhr2()); // rset.getString("SEARCH_PHRASE_2")
			pstmt.setString(intStmtIndex++, query.getSeaPhr3()); // rset.getString("SEARCH_PHRASE_3")
			pstmt.setString(intStmtIndex++, query.getSearchWordsStr()); // rset.getString("SEARCH_PHRASES")
			pstmt.setString(intStmtIndex++, query.getSeaOpt1()); // rset.getString("SEARCH_OPTION_1")
			pstmt.setString(intStmtIndex++, query.getSeaOpt2()); // rset.getString("SEARCH_OPTION_2")
			pstmt.setString(intStmtIndex++, query.getSeaOpt3()); // rset.getString("SEARCH_OPTION_3")
			pstmt.setString(intStmtIndex++, query.getSectionsStr()); // rset.getString("SEARCH_OPTIONS")
			pstmt.setString(intStmtIndex++, query.getBool1()); // rset.getString("BOOLEAN_1")
			pstmt.setString(intStmtIndex++, query.getBool2()); // rset.getString("BOOLEAN_2")
			pstmt.setString(intStmtIndex++, query.getBooleansStr()); // rset.getString("BOOLEANS")
			pstmt.setString(intStmtIndex++, query.getRecordCount()); // rset.getString("RESULTS_COUNT")
			pstmt.setString(intStmtIndex++, query.getSubcounts()); // rset.getString("SUBCOUNTS")
			pstmt.setString(intStmtIndex++, query.getLanguage()); // rset.getString("LANGUAGE")
			pstmt.setString(intStmtIndex++, query.getStartYear()); // rset.getString("START_YEAR")
			pstmt.setString(intStmtIndex++, query.getEndYear()); // rset.getString("END_YEAR")
			pstmt.setString(intStmtIndex++, query.getAutoStemming()); // rset.getString("AUTOSTEMMING")
			pstmt.setString(intStmtIndex++, query.getSortOption().getSortField()); // rset.getString("SORT_OPTION")
			pstmt.setString(intStmtIndex++, query.getSortOption().getSortDirection()); // rset.getString("SORT_DIRECTION")
			pstmt.setString(intStmtIndex++, display_query); // rset.getString("DISPLAY_QUERY")
			pstmt.setString(intStmtIndex++, query.getDocumentType()); // rset.getString("DOCUMENT_TYPE")
			pstmt.setString(intStmtIndex++, query.getTreatmentType()); // rset.getString("TREATMENT_TYPE")
			pstmt.setString(intStmtIndex++, query.getDisciplineType()); // rset.getString("DISCIPLINE_TYPE")

			// jam - new limiter - referexCollections
			pstmt.setString(intStmtIndex++, query.getReferexCollections().getDBStoreFormat()); // rset.getString("RFRX_COLLS")

			pstmt.setString(intStmtIndex++, query.getLastFourUpdates()); // rset.getString("LASTUPDATES")
			pstmt.setString(intStmtIndex++, query.getDupSetString()); // rset.getString("DUPSET")
			pstmt.setString(intStmtIndex++, query.isDeDupString()); // DEDUP
			pstmt.setString(intStmtIndex++, query.getDeDupDB()); // rset.getString("DEDUPDB")
			pstmt.setString(intStmtIndex++, refinements);
			pstmt.setString(intStmtIndex++, query.getResultsState().toString());
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Saved Search Insert failed into database for Search ID::"
					+ query == null ? "NO QUERY ID!" : query.getID(), sqle);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
		return result;
	}

	/**
	 * @param java
	 *            .lang.String removeOption
	 * @param java
	 *            .lang.String searchId
	 * @return void This method is used to remove single and multiple records.
	 *         This method takes searchId and removeOption as parameters. This
	 *         method is called when come through saved Searches page,So each
	 *         seach has a searchId. If removeAll is single it removes single
	 *         rsearch from saved searches table for that searchId and userId.
	 *         If removeAll is multiple it removes all Searches from table for
	 *         that userID
	 */

	public static int removeUserSavedSearches(String userid, String strSavedValue, String strAlertValue) throws InfrastructureException {
		// log.debug("accessing removeUserSavedSearches");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;

		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			// This will maitain any current searches not saved but still in
			// session
			// pstmt = (CallableStatement)
			// con.prepareCall("{ call SavedSearch_removeUserSearch(?)}");
			deleteAllSavedSearchCCList(userid);

			if (Query.OFF.equals(strSavedValue)) {
				pstmt = (CallableStatement) con.prepareCall("{ call SavedSearch_removeUserSearch(?)}");
				// pstmt =
				// con.prepareStatement("DELETE FROM SEARCHES_SAVED WHERE USER_ID=?");
			} else {
				// pstmt =
				// con.prepareStatement("UPDATE SEARCHES_SAVED SET EMAIL_ALERT=? WHERE USER_ID=?");
				pstmt = (CallableStatement) con.prepareCall("{ call SavedSearch_removeUserSearch1(?,?)}");
				pstmt.setString(idx++, Query.OFF);
			}
			pstmt.setString(idx++, userid);
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error removing user saved searches", sqle);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
		return result;
	}

	public static void getXMLCcList(String savedsearchid, Writer out) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			stmt = con.createStatement();
			rset = stmt.executeQuery("SELECT CC_LIST FROM SAVED_SEARCHES_CC where SEARCH_ID='" + savedsearchid + "'");
			out.write("<SESSION-HISTORY><SESSION-DATA>");
			out.write("<SAVEDSEARCH-ID>");
			out.write(savedsearchid);
			out.write("</SAVEDSEARCH-ID>");
			if (rset.next()) {
				String cclist = rset.getString("CC_LIST");
				if (cclist != null) {
					out.write("<CC_LIST>");
					out.write(cclist);
					out.write("</CC_LIST>");
				}

			}
			out.write("</SESSION-DATA></SESSION-HISTORY>");

		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error getting XML CC list", sqle);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (Exception e1) {
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception sqle) {
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
	}

	public static SavedSearchesAndAlerts getUserSavedSearchesAndAlerts(String userid) throws InfrastructureException {
		int emailAlerts = 0;
		int savedSearches = 0;

		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Query query = null;
		SavedSearchesAndAlerts searchesAndAlerts = new SavedSearchesAndAlerts();
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			// pstmt =
			// con.prepareStatement("SELECT * FROM SEARCHES_SAVED WHERE USER_ID=? AND SAVED = 'On' ORDER BY SAVE_DATE ASC");
			pstmt = con
					.prepareStatement("SELECT * FROM SEARCHES_SAVED, SAVED_SEARCHES_CC where SEARCHES_SAVED.USER_ID=? AND SEARCHES_SAVED.SEARCH_ID = SAVED_SEARCHES_CC.SEARCH_ID(+) AND SEARCHES_SAVED.SAVED = 'On' ORDER BY SEARCHES_SAVED.SAVE_DATE ASC");

			pstmt.setString(idx++, userid);
			rset = pstmt.executeQuery();
			while (rset.next()) {

				query = new Query();
				query.setID(rset.getString("SEARCH_ID"));
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setCCList(rset.getString("CC_LIST"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(Searches.prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
				query.setSeaPhr2(rset.getString("SEARCH_PHRASE_2"));
				query.setSeaPhr3(rset.getString("SEARCH_PHRASE_3"));
				query.setSearchWords(rset.getString("SEARCH_PHRASES"));
				query.setSeaOpt1(rset.getString("SEARCH_OPTION_1"));
				query.setSeaOpt2(rset.getString("SEARCH_OPTION_2"));
				query.setSeaOpt3(rset.getString("SEARCH_OPTION_3"));
				query.setSections(rset.getString("SEARCH_OPTIONS"));
				query.setBool1(rset.getString("BOOLEAN_1"));
				query.setBool2(rset.getString("BOOLEAN_2"));
				query.setBooleans(rset.getString("BOOLEANS"));

				query.setRecordCount(rset.getString("RESULTS_COUNT"));
				query.setSubcounts(rset.getString("SUBCOUNTS"));
				query.setLanguage(rset.getString("LANGUAGE"));
				query.setStartYear(rset.getString("START_YEAR"));
				query.setEndYear(rset.getString("END_YEAR"));
				query.setAutoStemming(rset.getString("AUTOSTEMMING"));

				query.setSortOption(new Sort(rset.getString("SORT_OPTION"), rset.getString("SORT_DIRECTION")));

				query.setDisplayQuery(Searches.prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));

				/**
				 * SR - Ebook Removal
				 */


				int mask = Integer.parseInt(rset.getString("MASK"));

				if(mask != DatabaseConfig.PAG_MASK && ((mask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) && ApplicationProperties.getInstance().isItTime(ApplicationProperties.REFEREX_MASK_DATE)){
					//search has referex db in it. Remove it
					mask -= DatabaseConfig.PAG_MASK;
				}
				query.setDataBase(mask);

				query.setRefinements(new Refinements(Searches.prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));

				if (query.getSearchType() != null) {
					if (query.getSearchType().equals(Query.TYPE_EASY)) {
						if (query.getIntermediateQuery() != null) {
							query.setDisplayQuery(query.getIntermediateQuery());
						}
					} else {
						if (query.getDisplayQuery() != null) {
							query.getDisplayQuery();
						}
					}
				}

				SearchHistory search = new SearchHistory();
				search.setQueryid(query.getID());
				search.setSessionid(query.getSessionID());
				if ("On".equalsIgnoreCase(query.getSavedSearch())) {
					search.setSavedsearch(true);
				} else {
					search.setSavedsearch(false);
				}
				if ("On".equalsIgnoreCase(query.getEmailAlert())) {
					search.setEmailalert(true);
				} else {
					search.setEmailalert(false);
				}
				search.setCcList(query.getCCList());
				search.setSavedDate(query.getDisplaySavedDate());
				search.setSearchtype(query.getSearchType());
				search.setEmailAlertWeek(query.getEmailAlertWeek());
				if (StringUtils.isNotBlank(query.getRecordCount())) {
					search.setResultscount(Integer.parseInt(query.getRecordCount()));
				} else {
					search.setResultscount(0);
				}

				search.setStartYear(query.getStartYear());
				search.setEndYear(query.getEndYear());
				search.setAutostemming(query.getAutoStemming());
				search.setSort(query.getSortOption().getSortField());
				search.setSortdir(query.getSortOption().getSortDirection());
				search.setDisplayquery(query.getDisplayQuery());
				search.setLastFourUpdates(query.getLastFourUpdates());
				search.setDatabasemask(query.getDataBase());

				if (Query.ON.equals(query.getEmailAlert())) {
					emailAlerts++;
					searchesAndAlerts.addSavedAlert(search);
				} else {
					savedSearches++;
					searchesAndAlerts.addSavedsearch(search);
				}
			}
			searchesAndAlerts.setSavedSearchCount(savedSearches);
			searchesAndAlerts.setSavedAlertCount(emailAlerts);

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, e);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (Exception e1) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}

		return searchesAndAlerts;
	}

	public static int[] getUserSavedSearches(String userid, boolean alertsonly, Writer out) throws InfrastructureException {
		int counts[] = new int[2];
		int emailAlerts = 0;
		int savedSearches = 0;

		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Query query = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			// pstmt =
			// con.prepareStatement("SELECT * FROM SEARCHES_SAVED WHERE USER_ID=? AND SAVED = 'On' ORDER BY SAVE_DATE ASC");
			pstmt = con
					.prepareStatement("SELECT * FROM SEARCHES_SAVED, SAVED_SEARCHES_CC where SEARCHES_SAVED.USER_ID=? AND SEARCHES_SAVED.SEARCH_ID = SAVED_SEARCHES_CC.SEARCH_ID(+) AND SEARCHES_SAVED.SAVED = 'On' ORDER BY SEARCHES_SAVED.SAVE_DATE ASC");

			pstmt.setString(idx++, userid);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				savedSearches++;
				query = new Query();
				query.setID(rset.getString("SEARCH_ID"));
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setCCList(rset.getString("CC_LIST"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(Searches.prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
				query.setSeaPhr2(rset.getString("SEARCH_PHRASE_2"));
				query.setSeaPhr3(rset.getString("SEARCH_PHRASE_3"));
				query.setSearchWords(rset.getString("SEARCH_PHRASES"));
				query.setSeaOpt1(rset.getString("SEARCH_OPTION_1"));
				query.setSeaOpt2(rset.getString("SEARCH_OPTION_2"));
				query.setSeaOpt3(rset.getString("SEARCH_OPTION_3"));
				query.setSections(rset.getString("SEARCH_OPTIONS"));
				query.setBool1(rset.getString("BOOLEAN_1"));
				query.setBool2(rset.getString("BOOLEAN_2"));
				query.setBooleans(rset.getString("BOOLEANS"));
				query.setRecordCount(rset.getString("RESULTS_COUNT"));
				query.setSubcounts(rset.getString("SUBCOUNTS"));
				query.setLanguage(rset.getString("LANGUAGE"));
				query.setStartYear(rset.getString("START_YEAR"));
				query.setEndYear(rset.getString("END_YEAR"));
				query.setAutoStemming(rset.getString("AUTOSTEMMING"));

				query.setSortOption(new Sort(rset.getString("SORT_OPTION"), rset.getString("SORT_DIRECTION")));

				query.setDisplayQuery(Searches.prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));

				/**
				 * SR - Ebook Removal
				 */
				int mask = Integer.parseInt(rset.getString("MASK"));
				if(mask != DatabaseConfig.PAG_MASK && ((mask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) && ApplicationProperties.getInstance().isItTime(ApplicationProperties.REFEREX_MASK_DATE)){
					//search has referex db in it. Remove it
					mask -= DatabaseConfig.PAG_MASK;
				}
				query.setDataBase(mask);

				query.setRefinements(new Refinements(Searches.prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));
				if (Query.ON.equals(query.getEmailAlert())) {
					emailAlerts++;
				}
				if (alertsonly) {
					if (Query.ON.equals(query.getEmailAlert())) {
						query.toXML(out);
					}
				} else {
					query.toXML(out);
				}
			}
			counts[0] = savedSearches;
			counts[1] = emailAlerts;
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Error getting saved searches", e);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (Exception e1) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}

		return counts;
	}

	public static Map<String, String> getCounts(String userID) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Map<String, String> countMap = new HashMap<String, String>();
		int idx = 1;

		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			String sqlString = "(SELECT 'ECOUNT',COUNT(EMAIL_ALERT)  FROM SEARCHES_SAVED WHERE USER_ID=? AND EMAIL_ALERT='On' AND SAVED = 'On') UNION (SELECT 'SCOUNT',COUNT(SAVED) FROM SEARCHES_SAVED WHERE USER_ID=? AND SAVED='On')";
			pstmt = con.prepareStatement(sqlString);
			pstmt.setString(idx++, userID);
			pstmt.setString(idx++, userID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				countMap.put(rset.getString(1), rset.getString(2));
			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error getting counts for saved searches", sqle);
		} finally {

			if (rset != null) {
				try {
					rset.close();
				} catch (Exception sqle) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
		return countMap;
	}

	public static int removeEmailAlertSearch(String queryid, String userid) throws InfrastructureException {
		deleteSavedSearchCCList(queryid, userid);
		return SavedSearches.updateSearch(queryid, userid, Query.ON, Query.OFF);
	}

	public static int addEmailAlertSearch(String queryid, String userid) throws InfrastructureException {
		return SavedSearches.updateSearch(queryid, userid, Query.ON, Query.ON);
	}

	private static int updateSearch(String queryid, String userid, String savedsetting, String emailalertsetting) throws InfrastructureException {
		// log.debug("Accessing SavedSearches.updateSearch");
		int result = 0;
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;
		int idx = 1;

		try {
			if (queryid != null) {
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(DatabaseConfig.SESSION_POOL);
				// pstmt =
				// con.prepareStatement("UPDATE SEARCHES_SAVED SET USER_ID=?, SAVED=?, EMAIL_ALERT=? WHERE SEARCH_ID=?");
				pstmt = (CallableStatement) con.prepareCall("{ call SavedSearch_updateSearch(?,?,?,?)}");
				pstmt.setString(idx++, userid);
				pstmt.setString(idx++, savedsetting);
				pstmt.setString(idx++, emailalertsetting);
				pstmt.setString(idx++, queryid);
				pstmt.executeUpdate();
				result = 1;
			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to update saved search, query ID::" + queryid, sqle);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
		return result;
	}

	public static int removeSavedSearch(String searchid, String userid) throws InfrastructureException {
		// log.debug("Accessing SavedSearches.removeSavedSearch");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;

		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			// This will maitain any current searches not saved but still in
			// session
			// pstmt =
			// con.prepareStatement("DELETE FROM SEARCHES_SAVED WHERE SEARCH_ID=?");
			pstmt = (CallableStatement) con.prepareCall("{ call SavedSearch_removeSearch(?)}");
			deleteSavedSearchCCList(searchid, userid);
			pstmt.setString(idx++, searchid);
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to remove saved search", sqle);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception sqle) {
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception cpe) {
				}
			}
		}
		return result;
	}

	// Method strictly for use for Running a SAved Search or Email Alert link
	public static Query getSearch(String strSearchID) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Query query = null;
		int idx = 1;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareStatement("SELECT * FROM SEARCHES_SAVED WHERE SEARCH_ID=?");
			pstmt.setString(idx++, strSearchID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				// Clob clob = rset.getClob("QUERY_STRING");
				// query = new Query(clob.getSubString(1, (int) clob.length()));
				query = new Query();
				query.setID(rset.getString("SEARCH_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(Searches.prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
				query.setSeaPhr2(rset.getString("SEARCH_PHRASE_2"));
				query.setSeaPhr3(rset.getString("SEARCH_PHRASE_3"));
				query.setSearchWords(rset.getString("SEARCH_PHRASES"));
				query.setSeaOpt1(rset.getString("SEARCH_OPTION_1"));
				query.setSeaOpt2(rset.getString("SEARCH_OPTION_2"));
				query.setSeaOpt3(rset.getString("SEARCH_OPTION_3"));
				query.setSections(rset.getString("SEARCH_OPTIONS"));
				query.setBool1(rset.getString("BOOLEAN_1"));
				query.setBool2(rset.getString("BOOLEAN_2"));
				query.setBooleans(rset.getString("BOOLEANS"));
				query.setRecordCount(rset.getString("RESULTS_COUNT"));
				query.setSubcounts(rset.getString("SUBCOUNTS"));
				query.setLanguage(rset.getString("LANGUAGE"));
				query.setStartYear(rset.getString("START_YEAR"));
				query.setEndYear(rset.getString("END_YEAR"));
				query.setAutoStemming(rset.getString("AUTOSTEMMING"));
				query.setSortOption(new Sort(rset.getString("SORT_OPTION"), rset.getString("SORT_DIRECTION")));
				query.setDisplayQuery(Searches.prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));

				/**
				 * SR - Ebook Removal
				 */
				int mask = Integer.parseInt(rset.getString("MASK"));

				if(mask != DatabaseConfig.PAG_MASK && ((mask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) && ApplicationProperties.getInstance().isItTime(ApplicationProperties.REFEREX_MASK_DATE)){
					//search has referex db in it. Remove it
					mask -= DatabaseConfig.PAG_MASK;
				}
				query.setDataBase(mask);

				query.setRefinements(new Refinements(Searches.prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));

			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Saved Search fetch from database failed for Search ID::" + query.getID(),
					sqle);
		} finally {
			if (rset != null) {
				try {
					rset.close();
					rset = null;
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception e) {
				}
			}
		}
		return query;
	}

	// Method strictly for use by EmailAlerts
	// to retreive a list of all the user's alerts
	public static List<Query> getListUserAlerts(String userid) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Query query = null;
		List<Query> usersearches = new ArrayList<Query>();
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			pstmt = con.prepareStatement("SELECT * FROM SEARCHES_SAVED WHERE USER_ID=? AND SAVED =? AND EMAIL_ALERT=? ORDER BY SAVE_DATE ASC");
			pstmt.setString(idx++, userid);
			pstmt.setString(idx++, Query.ON);
			pstmt.setString(idx++, Query.ON);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				query = new Query();
				query.setID(rset.getString("SEARCH_ID"));
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(Searches.prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
				query.setSeaPhr2(rset.getString("SEARCH_PHRASE_2"));
				query.setSeaPhr3(rset.getString("SEARCH_PHRASE_3"));
				query.setSeaOpt1(rset.getString("SEARCH_OPTION_1"));
				query.setSeaOpt2(rset.getString("SEARCH_OPTION_2"));
				query.setSeaOpt3(rset.getString("SEARCH_OPTION_3"));
				query.setBool1(rset.getString("BOOLEAN_1"));
				query.setBool2(rset.getString("BOOLEAN_2"));
				query.setRecordCount(rset.getString("RESULTS_COUNT"));
				query.setSubcounts(rset.getString("SUBCOUNTS"));
				query.setLanguage(rset.getString("LANGUAGE"));
				query.setStartYear(rset.getString("START_YEAR"));
				query.setEndYear(rset.getString("END_YEAR"));
				query.setAutoStemming(rset.getString("AUTOSTEMMING"));

				query.setSortOption(new Sort(rset.getString("SORT_OPTION"), rset.getString("SORT_DIRECTION")));

				query.setDisplayQuery(Searches.prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));

				/**
				 * SR - Ebook Removal
				 */
				int mask = Integer.parseInt(rset.getString("MASK"));
				if(mask != DatabaseConfig.PAG_MASK && ((mask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) && ApplicationProperties.getInstance().isItTime(ApplicationProperties.REFEREX_MASK_DATE)){
					//search has referex db in it. Remove it
					mask -= DatabaseConfig.PAG_MASK;
				}
				query.setDataBase(mask);


				query.setRefinements(new Refinements(Searches.prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));

				if (!usersearches.contains(query)) {
					usersearches.add(query);
				} else {
					log.info(userid + " duplicate Saved Search! " + rset.getString("SEARCH_ID") + ": " + query.getSeaPhr1());
				}
			}
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Error trying to get alerts list", e);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (Exception e1) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException sqle) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {
				}
			}
		}

		return usersearches;
	}

	// This method is only used to see if a search has already been saved
	// When a user creates an email alert, the search may have not been saved
	// previously. Before stored procs. we used the return value from the
	// update call. Now we need to explicity look for it first.
	public static boolean savedSearchExists(String strSearchID) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		boolean searchexists = false;
		int idx = 1;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			// we only select one field, since this is just for test if the
			// query exists
			pstmt = con.prepareStatement("SELECT SESSION_ID FROM SEARCHES_SAVED WHERE SEARCH_ID=?");
			pstmt.setString(idx++, strSearchID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				searchexists = true;
			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to determine saved search exists", sqle);
		} finally {
			if (rset != null) {
				try {
					rset.close();
					rset = null;
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception e) {
				}
			}
		}
		return searchexists;
	}

	public static void deleteAllSavedSearchCCList(String userId) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			// pstmt =
			// con.prepareStatement("DELETE FROM SAVED_SEARCHES_CC WHERE USER_ID=?");
			pstmt = con.prepareCall("{call SavedSearch_deleteAllSearch(?)}");
			pstmt.setString(1, userId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to delete all saved searches", e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void deleteSavedSearchCCList(String savedsearchid, String userid) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			// pstmt =
			// con.prepareStatement("DELETE FROM SAVED_SEARCHES_CC WHERE SEARCH_ID=? AND USER_ID=?");
			pstmt = (CallableStatement) con.prepareCall("{call SavedSearch_deleteCCSearch(?,?)}");
			pstmt.setString(1, savedsearchid);
			pstmt.setString(2, userid);
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to delete saved searches CC list", e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void storeSavedSearchCCList(String userid, String savedsearchid, String cclist) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;

		try {
			deleteSavedSearchCCList(savedsearchid, userid);
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			if ((cclist != null) && !(StringUtil.EMPTY_STRING.equals(cclist.trim()))) {
				// pstmt =
				// con.prepareStatement("INSERT INTO SAVED_SEARCHES_CC (SEARCH_ID,USER_ID,CC_LIST,SAVE_DATE) VALUES(?,?,?,SYSDATE)");
				pstmt = (CallableStatement) con.prepareCall("{call SavedSearch_storeCCSearch(?,?,?)}");
				pstmt.setString(1, savedsearchid);
				pstmt.setString(2, userid);
				pstmt.setString(3, cclist);
				pstmt.executeUpdate();
			}
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, "Database error trying to store saved searches CC list", e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
