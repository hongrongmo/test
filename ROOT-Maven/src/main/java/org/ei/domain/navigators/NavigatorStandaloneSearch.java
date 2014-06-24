package org.ei.domain.navigators;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.json4j.JSONArray;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastSearchControl;
import org.ei.domain.Query;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchResult;
import org.ei.domain.Searches;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.query.base.FastQueryWriter;
import org.ei.session.UserSession;

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

    public JSONArray runSearch(UserSession session) throws InfrastructureException, NumberFormatException, SearchException {
        // get Query from current session history
        JSONArray navsJSON = new JSONArray();
        if (session != null && session.getUser() != null) {
            SearchControl sc = new FastSearchControl();
            sc.setUseNavigators(true);

            Query queryObject = Searches.getSearch(searchId);
            if (queryObject != null) {
                queryObject.setSearchQueryWriter(new FastQueryWriter());
                queryObject.setDatabaseConfig(databaseConfig);
                queryObject.setCredentials(session.getUser().getCartridge());

                ResultsState rs = queryObject.getResultsState();
                rs.modifyState(field);
                Searches.updateSearchRefineState(queryObject);
                SearchResult result = sc.openSearch(queryObject, session.getSessionID().getID(), Integer.parseInt(session.getRecordsPerPage()), true);
                ResultNavigator nav = null;
                nav = sc.getNavigator();
                if (nav != null) {
                    navsJSON = nav.toJSON(queryObject.getResultsState(), field.split(":")[0]);
                }
            }
        }
        return navsJSON;
    }
}
