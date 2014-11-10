package org.ei.domain;

// general imports
import java.util.List;

import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;

//////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This class basically used for getting & setting the hit count, whats the query and page based on the document handle.
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////////////

public class SearchResult {

    private SearchControl sc;
    private int count;
    private long responseTime;
    private Query query;

    /**
     * Constructor which takes SearchControl as argument.
     */
    public SearchResult(SearchControl sc) {
        this.sc = sc;
    }

    public long getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * This method sets the result count of the search query.
     */

    public void setHitCount(int nCount) {
        this.count = nCount;
    }

    /**
     * This method gets the result count of the search query.
     *
     * @return : int ( hit count).
     */

    public int getHitCount() {
        return count;
    }

    /**
     * This method sets the query string of search query.
     */

    public void setQuery(Query sQuery) {
        this.query = sQuery;
    }

    /**
     * This method gets the search query .
     *
     * @return : String(query string).
     */
    public Query getQuery() {
        return query;
    }

    /**
     * This method gets the page based on doc index which internally calls the pageAt method of search component.
     *
     * @exception : SearchException
     * @return : Page.
     * @throws EVBaseException
     */

    public Page pageAt(int docIndex, String dataFormat)

    throws EVBaseException {
        return sc.pageAt(docIndex, dataFormat);
    }

    public List<DocID> getDocIDRange(int startIndex, int endIndex) throws EVBaseException {
        DupFilter filter = new DupFilter(sc, query);
        return filter.getDocIDRange(startIndex, endIndex);
        // return sc.getDocIDRange(startIndex,endIndex);

    }

    public List<DocID> getDocIDList(int pageSize) throws EVBaseException {
        return sc.getDocIDList(pageSize);
    }

    public PageEntry entryAt(int docIndex, String dataFormat) throws SearchException, InfrastructureException {

        return sc.entryAt(docIndex, dataFormat);
    }
}
