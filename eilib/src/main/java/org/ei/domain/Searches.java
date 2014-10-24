package org.ei.domain;

/** project specific imports*/
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.navigators.Refinements;
import org.ei.domain.navigators.ResultsState;
import org.ei.domain.personalization.SavedSearches;
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
public class Searches {
	private static Logger log4j = Logger.getLogger(Searches.class);
	public static final int UNCOMPRESSED_LIMIT = 4000;
	public static final String COMPRESSION_INDICATOR = new String(new char[] { 31 });

	private Searches() {
	};

	/**
	 * @return java.lang.String This method gets QRY string string from
	 *         SESSION_HISTORY table for the current sessionID. For each QRY
	 *         string it checks,is it present in the saved_searches tabel.
	 *
	 */
	public static void getUserSavedSearchesXML(String userid, Writer out) throws InfrastructureException {
		SavedSearches.getUserSavedSearchesXML(userid, out);
	}

	public static void getUserAlertsXML(String userid, Writer out) throws InfrastructureException {
		SavedSearches.getUserAlertsXML(userid, out);
	}

	public static void getSessionXMLQuery(String userID, String sessionid, Writer out) throws InfrastructureException {
		String emailCount = StringUtil.EMPTY_STRING;
		String savedSearchesCount = StringUtil.EMPTY_STRING;

		try {
			out.write("<SESSION-HISTORY>");
			List<Query> sessionsearches = getSessionSearches(sessionid);
			for (Query query : sessionsearches) {
				query.toXML(out);
			}

			if (userID != null) {
				Map<String, String> countMap = getCounts(userID);
				emailCount = (String) countMap.get("ECOUNT");
				savedSearchesCount = (String) countMap.get("SCOUNT");
			}

			out.write("<EMAIL-ALERT-COUNT>" + emailCount + "</EMAIL-ALERT-COUNT>");
			out.write("<SAVEDSEARCHES-COUNT>" + savedSearchesCount + "</SAVEDSEARCHES-COUNT>");
			out.write("</SESSION-HISTORY>");
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, e);
		}
	}

	private static Map<String, String> getCounts(String userID) throws InfrastructureException {
		// Persistent saved searches are stored in Searches_Saved table
		// moved method to the personalization.SavedSearches class
		return SavedSearches.getCounts(userID);
	}

	public static int saveSearch(Query query) throws InfrastructureException {
		return insertSearch(query);
	}

	public static int updateExistingSearch(Query query) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			log4j.info("Deleting any existing search; SESSION_ID=" + query.getSessionID() + ", SEARCH_ID=" + query.getID());
			pstmt = con.prepareStatement("DELETE FROM SEARCHES WHERE SESSION_ID=? AND SEARCH_ID=?");
			pstmt.setString(idx++, query.getSessionID());
			pstmt.setString(idx++, query.getID());
			pstmt.executeQuery();
			con.commit();
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, "Search delete from database failed for Session ID: " + query.getSessionID() + ", Search ID::" + query.getID(), sqle);
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
				} catch (ConnectionPoolException cpe) {
				}
			}
		}

		return insertSearch(query);
	}

	/**
	 * @param java
	 *            .lang.String xmlString
	 * @param java
	 *            .lang.Object Query
	 * @return void This method takes xmlString and Query object. From the query
	 *         object it checks if EmailAlert is on or off. Depending on
	 *         EmailAlert it stores search as max search_id for that userID.
	 *
	 */
	// jam 10/16/2002
	// bug 13.2 searches not being saved due to special characters...
	// changed over to PreparedStatement to avoid having to encode
	// special characters manually
	public static int insertSearch(Query query) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;
		int result = 0;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareCall("{ call Searches_insertSearchRe(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			int intStmtIndex = 1;
            log4j.info("Inserting search; SESSION_ID=" + query.getSessionID() + ", SEARCH_ID=" + query.getID());
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.setString(intStmtIndex++, query.getUserID());
			pstmt.setString(intStmtIndex++, query.getSessionID());
			pstmt.setString(intStmtIndex++, String.valueOf(query.getDataBase()));
			pstmt.setString(intStmtIndex++, query.getSearchType());
			pstmt.setString(intStmtIndex++, query.getEmailAlert());
			pstmt.setString(intStmtIndex++, query.getSavedSearch());
			pstmt.setString(intStmtIndex++, query.getEmailAlertWeek());

			String phrase1 = query.getSeaPhr1();
			String display_query = query.getDisplayQuery();
			String refinements = query.getRefinements().toUnlimitedString();
			if ((phrase1.length() >= UNCOMPRESSED_LIMIT) || (display_query.length() >= UNCOMPRESSED_LIMIT) || (refinements.length() >= UNCOMPRESSED_LIMIT)) {
				phrase1 = COMPRESSION_INDICATOR + StringUtil.zipText(phrase1);
				display_query = COMPRESSION_INDICATOR + StringUtil.zipText(display_query);
				refinements = COMPRESSION_INDICATOR + StringUtil.zipText(refinements);
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
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, "Search insert into database failed for Session ID: " + query.getSessionID() + ", Search ID::" + query.getID(), sqle);
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
				} catch (ConnectionPoolException cpe) {
				}
			}
		}
		return result;
	}

	public static int insertErrorSearch(Query query) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		CallableStatement pstmt = null;
		int result = 0;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareCall("{ call Searches_insertErrorSearch(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			int intStmtIndex = 1;
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.setString(intStmtIndex++, query.getUserID());
			pstmt.setString(intStmtIndex++, query.getSessionID());
			pstmt.setString(intStmtIndex++, String.valueOf(query.getDataBase()));
			pstmt.setString(intStmtIndex++, query.getSearchType());
			pstmt.setString(intStmtIndex++, query.getEmailAlert());
			pstmt.setString(intStmtIndex++, query.getSavedSearch());
			pstmt.setString(intStmtIndex++, query.getEmailAlertWeek());

			String phrase1 = query.getSeaPhr1();
			String display_query = query.getDisplayQuery();
			String refinements = query.getRefinements().toUnlimitedString();
			if ((phrase1.length() >= UNCOMPRESSED_LIMIT) || (display_query.length() >= UNCOMPRESSED_LIMIT) || (refinements.length() >= UNCOMPRESSED_LIMIT)) {
				phrase1 = COMPRESSION_INDICATOR + StringUtil.zipText(phrase1);
				display_query = COMPRESSION_INDICATOR + StringUtil.zipText(display_query);
				refinements = COMPRESSION_INDICATOR + StringUtil.zipText(refinements);
			}

			pstmt.setString(intStmtIndex++, phrase1); // rset.getString("SEARCH_PHRASE_1")
			pstmt.setString(intStmtIndex++, query.getSeaPhr2()); // rset.getString("SEARCH_PHRASE_2")
			pstmt.setString(intStmtIndex++, query.getSeaPhr3()); // rset.getString("SEARCH_PHRASE_3")
			pstmt.setString(intStmtIndex++, query.getSeaOpt1()); // rset.getString("SEARCH_OPTION_1")
			pstmt.setString(intStmtIndex++, query.getSeaOpt2()); // rset.getString("SEARCH_OPTION_2")
			pstmt.setString(intStmtIndex++, query.getSeaOpt3()); // rset.getString("SEARCH_OPTION_3")
			pstmt.setString(intStmtIndex++, query.getBool1()); // rset.getString("BOOLEAN_1")
			pstmt.setString(intStmtIndex++, query.getBool2()); // rset.getString("BOOLEAN_2")
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
			pstmt.setString(intStmtIndex++, "Off");
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
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
				} catch (ConnectionPoolException cpe) {
				}
			}
		}
		return result;
	}

	/**
	 * @param java
	 *            .lang.String emailAlert
	 * @param java
	 *            .lang.Object Query
	 * @return void This method takes Query object and emailAlert as parameters.
	 *         Gets all the saved searches for this userId using
	 *         getSavedSearches(). From the query object we get SearchQuery()
	 *         and compare if it prasent in svaed searches. If so then get
	 *         Search_id for that search and updates emailAlert for this used_id
	 *         and Search_id.
	 *
	 */
	public static int updateSearch(Query query) throws InfrastructureException {
		// log.info("UPDATE !");
		// log.debug("accessing Searches_updateSearches");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			// here we are updating access date
			// a search could be edited (saved, email alert)
			// and we do not change the original saved date
			// but save when the last update occurred in the
			proc = con.prepareCall("{ call Searches_updateSearches(?,?,?,?)}");
			int intStmtIndex = 1;
			proc.setString(intStmtIndex++, query.getID());
			proc.setString(intStmtIndex++, query.getDupSetString());
			proc.setString(intStmtIndex++, query.isDeDupString());
			proc.setString(intStmtIndex++, query.getDeDupDB());
			proc.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {
			if (proc != null) {
				try {
					proc.close();
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

	public static int updateSearchRefineState(Query query) throws InfrastructureException {
		// log.info("UPDATE RefineState!");
		// log.debug("accessing Searches_updateSearchRefineState");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;

		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			// here we are updating access date
			// a search could be edited (saved, email alert)
			// and we do not change the original saved date
			// but save when the last update occurred in the
			pstmt = con.prepareCall("{ call Searches_updateRefineState(?,?)}");
			int intStmtIndex = 1;
			pstmt.setString(intStmtIndex++, query.getResultsState().toString());
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
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
	public static int removeUserEmailAlerts(String userid) throws InfrastructureException {
		return removeUserSavedSearches(userid, Query.ON, Query.OFF);
	}

	public static int removeUserSavedSearches(String userid) throws InfrastructureException {
		return removeUserSavedSearches(userid, Query.OFF, Query.OFF);
	}

	/**
	 * Remove all user saved searches
	 *
	 * @param userid
	 * @param strSavedValue
	 * @param strAlertValue
	 * @return
	 * @throws InfrastructureException
	 */
	public static int removeUserSavedSearches(String userid, String strSavedValue, String strAlertValue) throws InfrastructureException {
		SavedSearches.removeUserSavedSearches(userid, strSavedValue, strAlertValue);

		// log.debug("accessing Searches_removeUserSavedSearches");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			// This will maitain any current searches not saved but still in
			// session
			proc = con.prepareCall("{ call Searches_removeSavedSearches(?,?,?)}");
			proc.setString(1, strSavedValue);
			proc.setString(2, strAlertValue);
			proc.setString(3, userid);
			proc.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {

			if (proc != null) {
				try {
					proc.close();
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
	 * Remove all searches from the current session (history)
	 *
	 * @param sessionid
	 * @return
	 * @throws InfrastructureException
	 */
	public static int removeSessionSearches(String sessionid) throws InfrastructureException {
		// log.debug("accessing removeSessionSearches");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			proc = (CallableStatement) con.prepareCall("{ call Searches_removeSessionSearch(?,?)}");
			proc.setString(1, Query.OFF);
			proc.setString(2, sessionid);
			proc.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {
			if (proc != null) {
				try {
					proc.close();
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
	 * Remove a single search by ID from history
	 *
	 * @param searchid
	 * @return
	 * @throws InfrastructureException
	 */
	public static int removeSingleSearch(String searchid) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			proc = (CallableStatement) con.prepareCall("{ call SEARCHES_REMOVEHISTORYSEARCH(?,?)}");
			proc.setString(1, Query.OFF);
			proc.setString(2, searchid);
			proc.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {
			if (proc != null) {
				try {
					proc.close();
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

	public static int[] getUserSavedSearches(String userid, Writer out) throws Exception {
		return SavedSearches.getUserSavedSearches(userid, false, out);
	}

	/**
	 * Build a List of Query objects representing Search History items
	 *
	 * @param sessionid
	 * @param out
	 * @throws InfrastructureException
	 * @throws Exception
	 */
	public static List<Query> getSessionSearches(String sessionid) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Query> sessionsearches = new ArrayList<Query>();
		Query query = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			pstmt = con
					.prepareStatement("SELECT * FROM (SELECT * FROM SEARCHES WHERE SESSION_ID=? AND VISIBLE=? ORDER BY ACCESS_DATE DESC) WHERE rownum <= 50");
			pstmt.setString(idx++, sessionid);
			pstmt.setString(idx++, Query.ON);
			rset = pstmt.executeQuery();
			while (rset.next()) {

				query = new Query();
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setID(rset.getString("SEARCH_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
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

				query.setDisplayQuery(prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));
				query.setDataBase(Integer.parseInt(rset.getString("MASK")));
				query.setRefinements(new Refinements(prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));
				sessionsearches.add(query);
			}

			return sessionsearches;
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, "Search Session History From database failed" + e.getMessage(), e);
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
	}

	/**
	 * @param java
	 *            .lang.Object Query
	 * @return void This method takes Query object as parameter.
	 *
	 */
	public static int removeSearch(Query query) throws InfrastructureException {
		return removeSearch(query.getID());
	}

	public static int removeSearch(String queryid) throws InfrastructureException {
		return updateVisibility(queryid, Query.OFF);
	}

	public static int updateVisibility(String queryid, String visibilty) throws InfrastructureException {
		// log.debug("accessing Searches_updateVisibility");
		int result = 0;
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		try {
			if (queryid != null) {
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(DatabaseConfig.SESSION_POOL);
				proc = con.prepareCall("{ call Searches_updateVisibility(?,?)}");
				proc.setString(1, visibilty);
				proc.setString(2, queryid);
				proc.executeUpdate();
				result = 1;
			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {

			if (proc != null) {
				try {
					proc.close();
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

	/*
	 * public static String getXMLSearch(String strSearchID) throws
	 * InfrastructureException { return
	 * (Searches.getSearch(strSearchID)).toXMLString(); }
	 */
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
			pstmt = con.prepareStatement("SELECT * FROM SEARCHES WHERE SEARCH_ID=?");
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
				query.setSeaPhr1(prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
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
				query.setDisplayQuery(prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));
				query.setDataBase(Integer.parseInt(rset.getString("MASK")));

				query.setRefinements(new Refinements(prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));

			}
		} catch (Exception sqle) {
			// log.error(sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, "Search fetch from database failed for Search ID::" + query.getID(), sqle);
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

	public static int removeEmailAlertSearch(String queryid, String userid) throws InfrastructureException {
		SavedSearches.removeEmailAlertSearch(queryid, userid);
		return Searches.updateSearch(queryid, userid, Query.ON, Query.OFF);
	}

	public static int addEmailAlertSearch(String queryid, String userid) throws InfrastructureException {
		// update searches first, otherwise query sent to SavedSearches
		// will have no user id!
		Searches.updateSearch(queryid, userid, Query.ON, Query.ON);
		Query search = Searches.getSearch(queryid);
		int result = 0;

		// check to see if alert does not exist first
		boolean exists = SavedSearches.savedSearchExists(queryid);
		if (!exists) {
			// email alert checkbox was used to create saved search
			SavedSearches.saveSearch(search);
		}
		// always update saved search email alert field
		result = SavedSearches.addEmailAlertSearch(queryid, userid);
		return result;
	}

	public static int removeSavedSearch(String queryid, String userid) throws InfrastructureException {
		SavedSearches.removeSavedSearch(queryid, userid);
		return Searches.updateSearch(queryid, userid, Query.OFF, Query.OFF);
	}

	public static int addSavedSearch(String queryid, String userid) throws InfrastructureException {
		// update searches first, otherwise query sent to SavedSearches
		// will have no user id!
		Searches.updateSearch(queryid, userid, Query.ON, Query.OFF);
		Query search = Searches.getSearch(queryid);
		return SavedSearches.saveSearch(search);
	}

	private static int updateSearch(String queryid, String userid, String savedsetting, String emailalertsetting) throws InfrastructureException {
		// log.debug("accessing Searches_updateSearch");
		int result = 0;
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int idx = 1;
		try {
			if (queryid != null) {
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(DatabaseConfig.SESSION_POOL);
				proc = con.prepareCall("{ call Searches_updateSearch(?,?,?,?)}");
				proc.setString(idx++, userid);
				proc.setString(idx++, savedsetting);
				proc.setString(idx++, emailalertsetting);
				proc.setString(idx++, queryid);
				proc.executeUpdate();
				result = 1;
			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {
			if (proc != null) {
				try {
					proc.close();
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

	// for updating dedup flag (true/false) and dedupdb(which db has dupes
	// removed)
	public static int updateSearchDeDup(Query query) throws InfrastructureException {
		// log.info("UPDATE DeDup !");

		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;

		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareCall("{ call Searches_updateSearchDeDup(?,?,?)}");
			// here we are updating access date
			// a search could be edited (saved, email alert)
			// and we do not change the original saved date
			// but save when the last update occurred in the

			int intStmtIndex = 1;
			pstmt.setString(intStmtIndex++, query.isDeDupString());
			pstmt.setString(intStmtIndex++, query.getDeDupDB());
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
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

	// for updating just the set of dup documents, this is to avoid race
	// condition since dupset may be behind dedup request
	public static int updateSearchDupset(Query query) throws InfrastructureException {
		// log.info("UPDATE Dupset !");
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement pstmt = null;

		int result = 0;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareCall("{ call Searches_updateSearchDupset(?,?)}");
			// here we are updating access date
			// a search could be edited (saved, email alert)
			// and we do not change the original saved date
			// but save when the last update occurred in the

			int intStmtIndex = 1;
			pstmt.setString(intStmtIndex++, query.getDupSetString());
			pstmt.setString(intStmtIndex++, query.getID());
			pstmt.executeUpdate();
			result = 1;
		} catch (Exception sqle) {
			// log.error("Exception",sqle);
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
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

	public static int updateSearchRefinements(String queryid, String refinements) throws InfrastructureException {
		// log.debug("accessing Searches_updateSearchRefinements");
		int result = 0;
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc = null;
		int idx = 1;
		try {
			if (queryid != null) {
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(DatabaseConfig.SESSION_POOL);
				proc = con.prepareCall("{ call Searches_updateRefinements(?,?)}");
				if (refinements.length() >= UNCOMPRESSED_LIMIT) {
					refinements = COMPRESSION_INDICATOR + StringUtil.zipText(refinements);
				}
				proc.setString(idx++, refinements);
				proc.setString(idx++, queryid);
				proc.executeUpdate();
				result = 1;

			}
		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.SESSION_SEARCH_ERROR, sqle);
		} finally {

			if (proc != null) {
				try {
					proc.close();
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

	// Used EXCLUSIVELY in combineSearchHistory.jsp
	// This will/should/could be made into a method
	// which returns ONLY the two requested query objects
	// to be combined - much more efficient with large session
	// histories

	public static List<Query> getListSessionSearches(String sessionid) throws Exception {
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Query> combinesearches = new ArrayList<Query>();
		Query query = null;
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			int idx = 1;
			pstmt = con.prepareStatement("SELECT * FROM SEARCHES WHERE SESSION_ID=? AND VISIBLE='On' ORDER BY ACCESS_DATE ASC");
			pstmt.setString(idx++, sessionid);
			rset = pstmt.executeQuery();
			for (int index = 0; rset.next(); index++) {

				query = new Query();
				query.setSessionID(rset.getString("SESSION_ID"));
				query.setUserID(rset.getString("USER_ID"));
				query.setID(rset.getString("SEARCH_ID"));
				query.setSavedSearch(rset.getString("SAVED"));
				query.setEmailAlert(rset.getString("EMAIL_ALERT"));
				query.setSavedDate(rset.getDate("SAVE_DATE"));
				query.setVisible(rset.getString("VISIBLE"));
				query.setSearchType(rset.getString("SEARCH_TYPE"));

				// jam _ 'break_out' fields
				query.setEmailAlertWeek(rset.getString("EMAILALERTWEEK"));
				query.setSeaPhr1(prepare_unZipText(rset.getString("SEARCH_PHRASE_1")));
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

				query.setDisplayQuery(prepare_unZipText(rset.getString("DISPLAY_QUERY")));
				query.setDocumentType(rset.getString("DOCUMENT_TYPE"));
				query.setTreatmentType(rset.getString("TREATMENT_TYPE"));
				query.setDisciplineType(rset.getString("DISCIPLINE_TYPE"));

				// jam - new limiter - ReferexCollections
				query.setReferexCollections(new ReferexLimiter(rset.getString("RFRX_COLLS")));

				query.setLastFourUpdates(rset.getString("LASTUPDATES"));
				query.setDupSet(rset.getString("DUPSET"));
				query.setDeDup(rset.getString("DEDUP"));
				query.setDeDupDB(rset.getString("DEDUPDB"));
				query.setDataBase(Integer.parseInt(rset.getString("MASK")));
				query.setRefinements(new Refinements(prepare_unZipText(rset.getString("REFINE_STACK"))));
				query.setResultsState(new ResultsState(rset.getString("RESULTS_STATE")));

				combinesearches.add(query);
			}
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
		return combinesearches;
	}

	public static String prepare_unZipText(String text) {
		if ((text != null) && (text.startsWith(COMPRESSION_INDICATOR))) {
			return StringUtil.unZipText(text.substring(1));
		} else {
			return text;
		}
	}
}
