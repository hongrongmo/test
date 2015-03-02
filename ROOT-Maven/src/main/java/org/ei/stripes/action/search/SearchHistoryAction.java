package org.ei.stripes.action.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.controller.logging.LogEntry;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.domain.SearchValidator;
import org.ei.domain.SearchValidator.SearchValidatorStatus;
import org.ei.domain.Searches;
import org.ei.domain.Sort;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.action.BackUrlAction;
import org.ei.stripes.action.BackUrlAction.BackURLByFeature;
import org.ei.stripes.action.SystemMessage;

@UrlBinding("/search/history/{$event}.url")
public class SearchHistoryAction extends BaseSearchAction {

    private final static Logger log4j = Logger.getLogger(SearchHistoryAction.class);

    // If present, indicates that only 1 item should be cleared
    @Validate(mask = "[a-zA-Z\\-1-9]*")
    String searchid;

    @Validate(mask = "[1-9#A-Za-z\\s]*")
    String txtcombine;

    private Query queryObject = new Query();

    /**
     * Combine search history items
     * 
     * @return
     * @throws InfrastructureException
     * @throws SessionException
     */
    @HandlesEvent("combine")
    @Validate
    public Resolution combine() throws InfrastructureException, SessionException {
        DatabaseConfig databaseconfig = DatabaseConfig.getInstance();
        UserSession usersession = context.getUserSession();
        String sessionid = usersession.getSessionid();
        List<String> databaseList = new ArrayList<String>();
        StringBuffer search = new StringBuffer();
        StringBuffer queryString = new StringBuffer();
        char[] scombine = this.txtcombine.trim().toCharArray();

        try {

            //
            // Combine the search queries
            //

            List<Query> sessionsearches = Searches.getListSessionSearches(context.getUserSession().getSessionid());

            for (int i = 0; i < scombine.length; i++) {
                // If char arry contains # , then we read char array that contains number after #
                if (scombine[i] == '#') {
                    search = new StringBuffer();
                    for (int j = (i + 1); j < (scombine.length); j++) {
                        // checking for number
                        if (Character.isDigit(scombine[j])) {
                            search.append(scombine[j]);
                            i = (j + 1);
                        } else {
                            // If not number we break the loop and continue from the current char index
                            i = (j - 1);
                            break;
                        }
                    }

                    if (search.toString().length() > 0) {
                        int sno = Integer.parseInt(search.toString()) - 1;
                        if (sno < sessionsearches.size()) {
                            // grab session object by index from list
                            Query aquery = (Query) sessionsearches.get(sno);
                            if (aquery != null) {
                                databaseList.add(Integer.toString(aquery.getDataBase()));
                                queryString.append("(").append(aquery.getPhysicalQuery()).append(")");
                            }
                        } else {
                            log4j.error("Invalid search number indicated!");
                            String url = BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SEARCHHISTORY);
                            url += (url.contains("?") ? "&" : "?") + "errorCode=" + SystemErrorCodes.SEARCH_HISTORY_NOIDEXISTS;
                            return new RedirectResolution(url);
                        }

                    }
                } else {
                    queryString.append(scombine[i]);
                }
            }

            //
            // Validate databases match across queries
            //

            int dbMask = -1;
            String dbName = null;
            if (!GenericValidator.isBlankOrNull(this.database)) {
                dbMask = Integer.parseInt(this.database);
            }
            if (dbMask == -1) // Coming from saved search
            {
                if (databaseList.size() == 1) {
                    dbName = (String) databaseList.get(0);
                } else if (databaseList.size() > 1) {
                    dbName = (String) databaseList.get(0);

                    for (int i = 1; i < databaseList.size(); i++) {
                        dbMask = Integer.parseInt(dbName);
                        if (!(dbName.equals((String) databaseList.get(i))) || dbMask == DatabaseConfig.CRC_MASK || dbMask == DatabaseConfig.USPTO_MASK) {
                            String url = BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SEARCHHISTORY);
                            url += (url.contains("?") ? "&" : "?") + "errorCode=" + SystemErrorCodes.COMBINE_HISTORY_UNIQUE_DATABASE_ERROR;
                            return new RedirectResolution(url);
                        }
                    }
                }

                dbMask = Integer.parseInt(dbName);
            }

            //
            // Build the Query object
            //

            this.queryObject.setSessionID(usersession.getSessionid());
            this.queryObject.setDataBase(dbMask);
            this.queryObject.setSearchType("Combined");
            this.queryObject.setDataBase(dbMask);
            this.queryObject.clearDupSet();
            this.queryObject.setDeDup(false);
            this.queryObject.setSortOption(new Sort(this.sort, Sort.DOWN));
            this.queryObject.setSearchPhrase(queryString.toString(), "", "", "", "", "", "", "");

            //
            // Use SearchValidator to validate incoming search
            //
            SearchValidator searchvalidator = new SearchValidator();
            SearchValidatorStatus status = searchvalidator.validate(this.queryObject);

            if (status != SearchValidatorStatus.SUCCESS) {
                String url = BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SEARCHHISTORY);
                if (url.contains("?"))
                    url += "&errorCode=" + SystemErrorCodes.SEARCH_HISTORY_ERROR;
                else
                    url += "?errorCode=" + SystemErrorCodes.SEARCH_HISTORY_ERROR;
                return (new RedirectResolution(url));
            }

            //
            // Validate the search. NOTE: the actual search is run either
            // quickSearchResults.jsp or expertSearchResults.jsp
            //
            this.queryObject = searchvalidator.getQueryObject();
            Searches.saveSearch(this.queryObject);

            //
            // Create a results URL - default quick search
            //
            String resultsURL = "/search/results/quick.url?CID=quickSearchCitationFormat" + "&database=" + dbMask + "&SEARCHID=" + queryObject.getID()
                + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
            if (Query.TYPE_EXPERT.equals(searchtype)) {
                resultsURL = "/search/results/expert.url?CID=expertSearchCitationFormat" + "&database=" + dbMask + "&SEARCHID=" + queryObject.getID()
                    + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
            } else if (Query.TYPE_THESAURUS.equals(searchtype)) {
                resultsURL = "/search/results/thes.url?CID=thesSearchCitationFormat" + "&database=" + dbMask + "&SEARCHID=" + queryObject.getID()
                    + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
            }

            // All good! Redirect to results page
            return new RedirectResolution(resultsURL);

        } catch (Throwable t) {
            log4j.error("Unable to parse combine search!", t);
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }

    }

    /**
     * Clears current search history - either by session or by search ID
     *
     * @return Resolution
     * @throws InfrastructureException
     * @throws UnsupportedEncodingException
     */
    @HandlesEvent("clear")
    @DontValidate
    public Resolution clear() throws InfrastructureException {
        String redir = "";

        // If searchid is present thn just clear single item in history
        if (!GenericValidator.isBlankOrNull(searchid)) {
            Searches.removeSingleSearch(searchid);
        } else {
            Searches.removeSessionSearches(context.getUserSession().getSessionID().getID());
        }

        // If nexturl is present use it for redirect.
        return new RedirectResolution(BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SEARCHHISTORY, true));
    }

    /**
     * Log search form usage metadata
     * 
     * @throws InfrastructureException
     */
    @After
    private void doUsageLogging() throws InfrastructureException {
        if ("combine".equals(context.getEventName())) {
            LogEntry logentry = context.getLogEntry();
            if (StringUtils.isNotBlank(this.searchid)) {
                if (logentry == null) {
                    throw new IllegalStateException("LogEntry object is empty!");
                }
                Properties logproperties = logentry.getLogProperties();
                logproperties.put("search_id", queryObject.getID());
                logproperties.put("query_string", queryObject.getPhysicalQuery());
                logproperties.put("sort_by", queryObject.getSortOption().getSortField());
                logproperties.put("sort_direction", queryObject.getSortOption().getSortDirection());
                logproperties.put("suppress_stem", queryObject.getAutoStemming());
                logproperties.put("context", "search");
                logproperties.put("action", "prepare");
                logproperties.put("type", "expert");
                logproperties.put("db_name", Integer.toString(queryObject.getDataBase()));
                logproperties.put("format", "citation");
                logproperties.put("doc_id", " ");
                logentry.setLogProperties(logproperties);
            }
        }
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public String getSearchid() {
        return searchid;
    }

    public void setSearchid(String searchid) {
        this.searchid = searchid;
    }

    /**
     * @return the txtcombine
     */
    public String getTxtcombine() {
        return txtcombine;
    }

    /**
     * @param txtcombine
     *            the txtcombine to set
     */
    public void setTxtcombine(String txtcombine) {
        this.txtcombine = txtcombine;
    }

}
