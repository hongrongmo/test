package org.ei.domain.navigators;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.wink.json4j.JSONArray;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastSearchControl;
import org.ei.domain.Query;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchResult;
import org.ei.domain.Searches;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.FastQueryWriter;

public class NavigatorStandaloneSearch {

    private String searchId;
    private String field;

    private DatabaseConfig databaseConfig;

    public NavigatorStandaloneSearch(String searchId, String field) {
        databaseConfig = DatabaseConfig.getInstance();

        if (StringUtils.isNotBlank(searchId)) {
            this.searchId = searchId;
        }
        if (StringUtils.isNotBlank(field)) {
            this.field = field;

        }
    }

    public JSONArray runSearch(String sessionid, String[] cartridge, String recordsperpage) throws InfrastructureException, NumberFormatException, SearchException {
    	if (GenericValidator.isBlankOrNull(sessionid) || cartridge == null || cartridge.length == 0) {
    		throw new InfrastructureException(SystemErrorCodes.SEARCH_QUERY_EXECUTION_FAILED, "Invalid parameters - missing session ID or user cartridge");
    	} else if (GenericValidator.isBlankOrNull(recordsperpage)) {
    		recordsperpage = "25";
    	}

        // get Query from current session history
        JSONArray navsJSON = new JSONArray();
        SearchControl sc = new FastSearchControl();
        sc.setUseNavigators(true);

        Query queryObject = Searches.getSearch(searchId);
        if (queryObject != null) {
            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.setDatabaseConfig(databaseConfig);
            queryObject.setCredentials(cartridge);

            ResultsState rs = queryObject.getResultsState();
            rs.modifyState(field);
            Searches.updateSearchRefineState(queryObject);
            SearchResult result = sc.openSearch(queryObject, sessionid, Integer.parseInt(recordsperpage), true);
            ResultNavigator nav = null;
            nav = sc.getNavigator();
            if (nav != null) {
                navsJSON = nav.toJSON(queryObject.getResultsState(), field.split(":")[0]);
            }
        }
        return navsJSON;
    }
}
