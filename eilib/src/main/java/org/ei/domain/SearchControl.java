package org.ei.domain;

// general imports
import java.util.List;

import org.ei.domain.navigators.ResultNavigator;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SearchException;
import org.ei.query.base.HitHighlighter;

////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This abstract class has the method declarations like getting search
 * result,initialization,getting the page and caching the pages.
 *
 * Sample usage of a search control
 *
 * DatabaseConfig dConfig = DatabaseConfig.getInstance(); Database database =
 * dConfig.getDatabase("compendex"); SearchControl sc =
 * database.newSearchControlInstance(); SearchResult sr = sc.openSearch(query,
 * sessionID, pageSize, false); Page page = sr.pageAt(docIndex,
 * Citation.CITATION_FORMAT); sc.maintainCache(docIndex, 8); sc.closeSearch();
 *
 *
 *
 */
// /////////////////////////////////////////////////////////////////////////////////////////////////////

public interface SearchControl {

	/**
	 * This method answers true if a search has already be opened with the
	 * SearchControl instance. Only openSearch call can be done at a time with a
	 * single search control. If openSearch is called on a SearchControl
	 * instance that is already initialized a SearchException will be thrown.
	 * After closeSearch is called on the SearchControl this method will return
	 * false, and you are free to call openSearch to begin a new search.
	 *
	 * @return true if search is initialized and not closed.
	 *
	 **/

	public boolean isInitialized();

	/**
	 * This method opens a search and returns a reference to a SearchResult
	 * object. If the boolean lazy is set true the search will not execute until
	 * a request has been made that specifies what page or what range of
	 * documents will be gathered from the search. If lazy is true the
	 * SearchResult will not have the hit count yet.
	 *
	 * @param sQuery
	 *            The search query
	 * @param sessionID
	 *            The sessionID
	 * @param pageSize
	 *            How many results will be each viewable page.
	 * @param lazy
	 *            If true search will not immediatly execute. If false the
	 *            search will execute immediatly.
	 * @return The search result object
	 * @see Query
	 * @see SearchResult
	 * @throws SearchException
	 *             In a problem with the search occurs
	 * @throws SearchQueryExecutionException
	 * @throws SearchQueryCompilationException
	 * @throws SearchException
	 * @throws InfrastructureException
	 **/

	public SearchResult openSearch(Query sQuery, String sessionID, int pageSize, boolean lazy) throws SearchException, InfrastructureException;

	public Page pageAt(int docIndex, String dataFormat) throws SearchException, InfrastructureException;

	public PageEntry entryAt(int docIndex, String dataFormat) throws SearchException, InfrastructureException;

	public List<DocID> getDocIDRange(int startRange, int endRange) throws SearchException, InfrastructureException;

	public List<DocID> getDocIDList(int pageSize) throws SearchException, InfrastructureException;

	public SearchControl newInstance(Database database);

	public void maintainCache(int docIndex, int numPagesCacheAhead) throws SearchException, InfrastructureException;

	public void maintainNavigatorCache() throws SearchException;

	public void closeSearch() throws SearchException;

	public void setHitHighlighter(HitHighlighter h);

	public String getErrorCode();

	public boolean hasError();

	public boolean checkBasket();

	public void checkBasket(boolean check);

	public void setUseNavigators(boolean usenavs);

	public ResultNavigator getNavigator() throws SearchException;
}
