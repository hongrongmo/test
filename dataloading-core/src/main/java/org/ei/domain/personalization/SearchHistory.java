package org.ei.domain.personalization;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.CIDHelper;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseDisplayHelper;
import org.ei.domain.Query;
import org.ei.domain.SearchHistoryDedupCriteria;
import org.ei.domain.Sort;
import org.ei.domain.SortOption;
import org.ei.domain.XSLCIDHelper;

/**
 * The search history class.
 * @author harovetm
 *
 */
public class SearchHistory {
	private final static Logger log4j = Logger.getLogger(SearchHistory.class);

	private String serialnumber;
	private String searchtype;
	private String sessionid;
	private String displayquery;
	private String autostemming;
	private SortOption sort;
	private int resultscount;
	private String years;
	private String language;
	private String queryid;
	private int databasemask;
	private String databasedisplay;
	private boolean emailalert;
	private boolean savedsearch;
	private String ccList;
	private String savedDate;
	private String emailAlertWeek;
	private String startYear;
	private String endYear;
	private String lastFourUpdates;
	private String searchCID;
	private String databaseDisplayName;

	private List<SearchHistoryDedupCriteria> dedupCriterias= new ArrayList<SearchHistoryDedupCriteria>();

	public SearchHistory() {};

	public SearchHistory(Query query) {
        setDatabasemask(query.getDataBase());
        setQueryid(query.getID());
        setSearchtype(query.getSearchType());
        setSessionid(query.getSessionID());
        setDisplayquery(query.getDisplayQuery());
        setAutostemming(query.getAutoStemming());
        setSort(query.getSortOption());
        setLanguage(query.getLanguage());
        setStartYear(query.getStartYear());
        setEndYear(query.getEndYear());
        setYears(this.startYear + " - " + this.endYear);
        setLastFourUpdates(query.getLastFourUpdates());
        String tmp = query.getEmailAlert();
        setEmailalert(!"Off".equalsIgnoreCase(tmp));
        tmp = query.getSavedSearch();
        setSavedsearch(!"Off".equalsIgnoreCase(tmp));

        if (!GenericValidator.isBlankOrNull(query.getRecordCount())) {
            setResultscount(Integer.parseInt(query.getRecordCount()));
        }

        List<String> dupSet = query.getDupSet();
        for (int i = 0; i < dupSet.size(); i++)
        {
            SearchHistoryDedupCriteria dedupcriteria = new SearchHistoryDedupCriteria();
            String criteria = (String) dupSet.get(i);
            String[] options = criteria.split(":");
            if (options.length == 2)
            {
                dedupcriteria.setFieldPref(options[0]);
                dedupcriteria.setDbPref(options[1]);
            }
            dedupCriterias.add(dedupcriteria);
        }
	}

	public String getDatabasedisplay() {
		return DatabaseDisplayHelper.getDisplayName(databasemask);
	}

	public String getDisplayquery() {
		return displayquery;
	}

	public void setDisplayquery(String displayquery) {
		this.displayquery = displayquery;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getAutostemming() {
		return autostemming;
	}

	public void setAutostemming(String autostemming) {
		this.autostemming = autostemming;
	}

	public String getSerialNumber() {
		return serialnumber;
	}

	public void setSerialNumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public SortOption getSort() {
		return sort;
	}

    public void setSort(Sort sort) {
        this.sort = new SortOption();
        this.sort.setField(sort.getSortField());
        this.sort.setDirection(sort.getSortDirection());
    }

    public void setSort(String sort) {
        if (this.sort == null) this.sort = new SortOption();
        this.sort.setField(sort);
    }

	public void setSortdir(String sortdir) {
		if (this.sort == null) this.sort = new SortOption();
		this.sort.setDirection(sortdir);
	}

	public int getResultscount() {
		return resultscount;
	}

	public void setResultscount(int resultscount) {
		this.resultscount = resultscount;
	}

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getQueryid() {
		return queryid;
	}

	public void setQueryid(String queryid) {
		this.queryid = queryid;
	}

	public int getDatabasemask() {
		return databasemask;
	}

	public void setDatabasemask(int databasemask) {
		this.databasemask = databasemask;
	}

	public boolean isEmailalert() {
		return emailalert;
	}

	public void setEmailalert(boolean emailalert) {
		this.emailalert = emailalert;
	}

	public boolean isSavedsearch() {
		return savedsearch;
	}

	public void setSavedsearch(boolean savedsearch) {
		this.savedsearch = savedsearch;
	}

	public String getCid() {
		String CID = "home";
		try {
			if (databasemask == DatabaseConfig.USPTO_MASK) {
				CID = "uspto" + searchtype;
			} else if (databasemask == DatabaseConfig.CRC_MASK) {
				CID = "engnetbase" + searchtype;
			} else {
				CID = XSLCIDHelper.searchResultsCid(searchtype);
			}
		} catch (Exception e) {
			log4j.warn("Unable to convert database mask to number: " + databasemask);
		}
		return CID;
	}

	public String getCcList() {
		return ccList;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public String getSavedDate() {
		return savedDate;
	}

	public void setSavedDate(String savedDate) {
		this.savedDate = savedDate;
	}

	public String getEmailAlertWeek() {
		return emailAlertWeek;
	}

	public void setEmailAlertWeek(String emailAlertWeek) {
		this.emailAlertWeek = emailAlertWeek;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getLastFourUpdates() {
		return lastFourUpdates;
	}

	public void setLastFourUpdates(String lastFourUpdates) {
		this.lastFourUpdates = lastFourUpdates;
	}

	public String getSearchCID() {
		return CIDHelper.searchResultsCid(this.getSearchtype());
	}

	public void setSearchCID(String searchCID) {
		CIDHelper.searchResultsCid(this.getSearchtype());
	}

	public String getDatabaseDisplayName() {
		return DatabaseDisplayHelper.getDisplayName(databasemask);
	}

	public void setDatabaseDisplayName(String databaseDisplayName) {
		this.databaseDisplayName = databaseDisplayName;
	}

	public List<SearchHistoryDedupCriteria> getDedupCriterias() {
		return dedupCriterias;
	}

	public void setDedupCriterias(List<SearchHistoryDedupCriteria> dedupCritertas) {
		this.dedupCriterias = dedupCritertas;
	}

	public void addDedupCriterias(SearchHistoryDedupCriteria dedupCriterta) {
		this.dedupCriterias.add(dedupCriterta);
	}
}
