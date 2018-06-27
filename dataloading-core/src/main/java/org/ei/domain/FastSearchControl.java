package org.ei.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.ei.domain.navigators.EiNavigator;
import org.ei.domain.navigators.NavigatorCache;
import org.ei.domain.navigators.ResultNavigator;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.HitHighlighter;

public class FastSearchControl implements SearchControl {
	private Logger log4j = Logger.getLogger(FastClient.class);

	protected Query query;
	protected SearchResult searchResult;
	protected boolean checkBasket = true;

	private boolean searchInitialized = false;
	private int pageSize;
	private String sessionID;

	private List<DocID> docIDList;
	private boolean cachePage;
	private boolean lazy;
	private boolean usenavigators = true;
	private boolean backfileStandAlone = false;
	private boolean archiveStandAlone = false;
	public static String BASE_URL;
	private String errorCode;
	private String searchID;

	private HitHighlighter highlighter;

	// jam
	private ResultNavigator navigator = null;

	public boolean isInitialized() {
		return searchInitialized;
	}

	public boolean hasError() {
		if (errorCode != null) {
			return true;
		}

		return false;
	}

	public boolean checkBasket() {
		return this.checkBasket;
	}

	public void checkBasket(boolean checkBasket) {
		this.checkBasket = checkBasket;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setUseNavigators(boolean usenavs) {
		this.usenavigators = usenavs;
	}

	public ResultNavigator getNavigator() throws SearchException {
		log4j.info("getNavigator() call for query ID: " + query.getID());
		int count = 0;
		NavigatorCache nc = new NavigatorCache(sessionID);
		while (this.navigator == null) {
			try {
				count++;
				this.navigator = nc.getFromCache(query.getID());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new SearchException(SystemErrorCodes.NAVIGATOR_FETCH_FROM_CACHE_FAILED, "Navigator Data fetch for cache failed: " + e.getMessage(), e);
			}
			if (count == 5) {
				break;
			}
		}
		return this.navigator;
	}

	public void setHitHighlighter(HitHighlighter h) {
		this.highlighter = h;
	}

	public void closeSearch() throws SearchException {
		this.highlighter = null;
		searchInitialized = false;
	}

	protected DocumentBuilder getBuilder() {
		return new MultiDatabaseDocBuilder();
	}

	public SearchControl newInstance(Database d) {
		return new FastSearchControl();
	}

	public SearchResult openSearch(Query sQuery, String sSessionID, int nPageSize, boolean lazy) throws SearchException, InfrastructureException {

		if (searchInitialized) {
			throw new SearchException(SystemErrorCodes.SEARCH_CONTROL_ALREADY_ACTIVE, "Search control already active");
		} else {
			searchInitialized = true;
		}

		searchResult = new SearchResult(this);
		searchResult.setQuery(sQuery);

		if ((sQuery.getDataBase() & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) {
			this.backfileStandAlone = true;
		}

		if ((sQuery.getDataBase() & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK) {
			this.archiveStandAlone = true;
		}

		this.query = sQuery;
		this.sessionID = sSessionID;
		this.pageSize = nPageSize;
		this.lazy = lazy;
		this.searchID = query.getID();

		//log4j.info("query: " + sQuery.getSearchQuery() + ", sessionID: " + sSessionID + ", lazy: " + lazy + ", searchID: " + query.getID());

		if (!lazy) {
			this.docIDList = search(0, nPageSize);
			cachePage = true;
		} else {
			this.docIDList = null;
			cachePage = false;
		}

		return searchResult;
	}

	protected List<DocID> search(int offset, int pSize) throws SearchException, InfrastructureException {
		List<DocID> dList = new ArrayList<DocID>();

		// try {

		DatabaseConfig dConfig = DatabaseConfig.getInstance();
		String fastSearchString;
		// try {
		fastSearchString = query.getSearchQuery();
		/*
		 * } catch (SearchQueryCompilationException e) {
		 * //ExceptionWriterHelper.
		 * writeException(SearchQueryCompilationException
		 * .SearchQueryCompilationFailed, e.getMessage(),
		 * e.getClass().getName(), this.getClass().getName(),
		 * query.getExceptionXml()); throw new
		 * SearchQueryExecutionException(e.getMessage(), e,
		 * SearchQueryCompilationException.SearchQueryCompilationFailed); }
		 */
		Sort sortOption = query.getSortOption();

		//log4j.info("BASE_URL: " + BASE_URL + ", query string: " + fastSearchString);

		FastClient client = new FastClient();
		client.setBaseURL(BASE_URL);
		client.setQueryString(fastSearchString);
		client.setOffSet(offset);
		client.setPageSize(pSize);
		client.setDoClustering(false);
		client.setResultView("ei");
		client.setDoNavigators(usenavigators);

		if (usenavigators) {
			client.setNavigatorMask(query.getGatheredMask());
		}

		Database[] databases = dConfig.getDatabases(query.getDataBase());

		if (databases.length > 1) {
			client.setDoCatCount(true);
		}

		if (sortOption != null) {
			String sortField = sortOption.getFastClientSortField(query.getGatheredMask());
			client.setPrimarySort(sortField);
			client.setPrimarySortDirection(getSortDirection(sortOption));
		}

		//comment out for dataloading
		//log4j.info("Calling Fast search, URL='" + BASE_URL + "', query='" + fastSearchString + "', offset='" + offset + "'");
		client.search();
		/*
		 * } catch (SearchQueryCompilationException e) {
		 * ExceptionWriterHelper.writeException
		 * (SearchQueryExecutionException.SearchQueryExecutionFailed,
		 * e.getMessage(), e.getClass().getName(), this.getClass().getName(),
		 * query.getExceptionXml()); throw new
		 * SearchQueryExecutionException(e.getMessage(), e); }
		 */

		this.errorCode = client.getErrorCode();

		if (usenavigators) {
			Hashtable<String, List<String[]>> hnavs = getNavigators(client.getNavigators());
			navigator = new ResultNavigator(hnavs, query.getDatabases());
		}

		int docCount = client.getHitCount();

		searchResult.setHitCount(docCount);
		searchResult.setResponseTime(client.getSearchTime());
		List<?> ds = client.getDocIDs();
		//log4j.info("Number of doc IDs returned: " + ds.size());

		for (int i = 0; i < ds.size(); i++) {
			String[] fields = (String[]) ds.get(i);
			String did = getDocID(fields[0]);
			DocID docID = new DocID(++offset, did, dConfig.getDatabase(did.substring(0, 3)), fields[1], fields[2], Integer.parseInt(fields[3]));
			dList.add(docID);

		}
		/*
		 * } catch (Exception e) { throw new SearchException(e); }
		 */

		return dList;
	}

	private String getDocID(String mid) {
		if (backfileStandAlone && mid.indexOf(DatabaseConfig.C84_PREF) == 0) {
			return DatabaseConfig.CBF_PREF.concat(mid.substring(3));
		} else if (archiveStandAlone && mid.indexOf("ibf") == 0) {
			return "ibs".concat(mid.substring(3));
		}

		return mid;
	}

	private Hashtable<String, List<String[]>> getNavigators(Hashtable<String, List<String[]>> hnavs) {
		if (backfileStandAlone) {
			List<String[]> navlist = (List<String[]>) hnavs.get(EiNavigator.DB);
			if (navlist != null) {
				for (int i = 0; i < navlist.size(); i++) {
					String[] dataElement = (String[]) navlist.get(i);
					if (dataElement[0].equals(DatabaseConfig.C84_PREF)) {
						dataElement[0] = DatabaseConfig.CBF_PREF;
						break;
					}
				}
			}
		}

		if (archiveStandAlone) {
			List<String[]> navlist = (List<String[]>) hnavs.get(EiNavigator.DB);
			if (navlist != null) {
				for (int i = 0; i < navlist.size(); i++) {
					String[] dataElement = (String[]) navlist.get(i);
					if (dataElement[0].equals("ibf")) {
						dataElement[0] = "ibs";
						break;
					}
				}
			}
		}

		return hnavs;
	}

	public Page pageAt(int docIndex, String dataFormat) throws SearchException, InfrastructureException {

		if (!searchInitialized) {
			throw new SearchException(SystemErrorCodes.SEARCH_QUERY_EXECUTION_FAILED, "Search is not initialized");
		}

		Page page = null;

		// try {

		if (lazy || getPageIndex(this.pageSize, docIndex) != 1) {
			PageCache pageCache = new PageCache(sessionID);
			// try {
			this.docIDList = pageCache.getPage(getPageIndex(pageSize, docIndex), searchID);
			/*
			 * } catch (PageCacheException e) {
			 * ExceptionWriterHelper.writeException
			 * (SearchQueryExecutionException.PageFetchFromCacheFailed,
			 * "error while fatching cached pafe from database",
			 * e.getClass().getName(), this.getClass().getName(),
			 * query.getExceptionXml()); throw new
			 * SearchQueryExecutionException(
			 * "error while fatching cached pafe from database::"
			 * +e.getMessage(), e); }
			 */

			if ((this.docIDList == null) || (this.docIDList.size() != this.pageSize)) {
				int offset = ((getPageIndex(pageSize, docIndex) - 1) * pageSize);
				usenavigators = false;
				this.docIDList = search(offset, this.pageSize);
				cachePage = true;
			}
		}
		page = buildPage(this.docIDList, dataFormat);
		page.setHitHighlighter(this.highlighter);

		/*
		 * } catch (Exception e) { throw new SearchException(e); }
		 */

		page.setPageIndex(docIndex);
		return page;
	}

	public List<DocID> getDocIDRange(int startRange, int endRange) throws SearchException, InfrastructureException {

		if (!searchInitialized) {
			throw new SearchException(SystemErrorCodes.SEARCH_QUERY_EXECUTION_FAILED, "Search is not initialized");
		}

		List<DocID> dList = null;

		int offset = startRange - 1;
		int pSize = endRange - offset;
		cachePage = false;
		// try {
		dList = search(offset, pSize);
		/*
		 * } catch (SearchQueryExecutionException e) {
		 * ExceptionWriterHelper.writeException
		 * (SearchQueryExecutionException.SearchQueryExecutionFailed,
		 * "Fast data call failed", e.getClass().getName(),
		 * this.getClass().getName(), query.getExceptionXml()); throw new
		 * SearchException(e); }
		 */

		return dList;
	}

	public FastDeduper dedupSearch(int dupsetSize, String fieldPref, String databasePref) throws Exception {
		String fastSearchString = query.getSearchQuery();
		Sort sortOption = query.getSortOption();

		FastClient client = new FastClient();
		client.setBaseURL(BASE_URL);
		client.setQueryString(fastSearchString);
		client.setOffSet(0);
		client.setPageSize(dupsetSize);

		if (sortOption != null) {
			String sortField = sortOption.getFastClientSortField(query.getGatheredMask());
			client.setPrimarySort(sortField);
			client.setPrimarySortDirection(getSortDirection(sortOption));
		}

		return client.dedupSearch(fieldPref, databasePref);
	}

	public List<DocID> getDocIDList(int pSize) throws SearchException, InfrastructureException {

		if (!searchInitialized) {
			throw new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, "Search is not initialized");
		}

		List<DocID> dList = null;
		cachePage = false;
		dList = search(0, pSize);

		return dList;
	}

	public PageEntry entryAt(int docIndex, String dataFormat) throws SearchException, InfrastructureException {

		if (!searchInitialized) {
			throw new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, "Search is not initialized");
		}

		List<DocID> entryList = new ArrayList<DocID>();
		List<?> pageEntries = null;
		PageEntry pEntry = null;

		if (lazy || getPageIndex(this.pageSize, docIndex) != 1) {
			PageCache pageCache = new PageCache(sessionID);
			this.docIDList = pageCache.getPage(getPageIndex(pageSize, docIndex), searchID);

			if (this.docIDList == null) {

				int offset = ((getPageIndex(pageSize, docIndex) - 1) * pageSize);
				usenavigators = false;
				docIDList = search(offset, this.pageSize);
				cachePage = true;
			}
		}

		for (int x = 0; x < this.docIDList.size(); ++x) {
			DocID id = (DocID) this.docIDList.get(x);
			if (id.getHitIndex() == docIndex) {
				entryList.add(id);
				break;
			}
		}

		DocumentBuilder builder = getBuilder();
		List<?> docList = builder.buildPage(entryList, dataFormat);

		PageEntryBuilder pb = new PageEntryBuilder(sessionID);
		pageEntries = pb.buildPageEntryList(docList);
		pEntry = (PageEntry) pageEntries.get(0);
		pEntry.setHitHighlighter(highlighter);

		return pEntry;
	}

	protected Page buildPage(List<DocID> didList, String dataFormat) throws SearchException {
		Page currentPage = new Page();
		DocumentBuilder builder = getBuilder();
		List<?> docList;
		try {
			docList = builder.buildPage(didList, dataFormat);
		} catch (DocumentBuilderException e) {
			throw new SearchException(SystemErrorCodes.DOCUMENT_BUILDER_FAILED, "document builder failed", e);
		}

		if (this.checkBasket) {
			// System.out.println("Checking basket");
			PageEntryBuilder pb = new PageEntryBuilder(sessionID);
			List<PageEntry> pageEntries = new ArrayList<PageEntry>();
			try {
				pageEntries = pb.buildPageEntryList(docList);
			} catch (PageEntryBuilderException e) {
				throw new SearchException(SystemErrorCodes.PAGE_ENTRY_BUILDER_FAILED, "page builder failed", e);
			}
			currentPage.addAll(pageEntries);
		} else {
			// System.out.println("Not checking basket");
			int dsize = docList.size();

			for (int i = 0; i < dsize; i++) {
				PageEntry entry = new PageEntry();
				try {
					entry.setSelected(false);
					if ((EIDoc) docList.get(i) != null) {
						entry.setDoc((EIDoc) docList.get(i));
						currentPage.add(entry);
					}
				} catch (InvalidArgumentException e) {
					throw new SearchException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "Invalid Argument set in page entry creation", e);
				}
			}
		}
		return currentPage;
	}

	private int getPageIndex(int pSize, int dIndex) {
		int pageIndex;

		if ((dIndex % pSize) == 0) {
			// if the remainder is zero the page index will find in the
			// following way
			pageIndex = (dIndex / pSize);
		} else {
			// otherwise find the page index in the following way
			pageIndex = (dIndex / pSize) + 1;
		}

		return pageIndex;

	}

	public void maintainNavigatorCache() throws SearchException {
		if ((usenavigators) && (navigator != null)) {
			NavigatorCache nc = new NavigatorCache(sessionID);
			try {
				nc.addToCache(searchID, navigator);
			} catch (PageCacheException pe) {
				// ExceptionWriterHelper.writeException(SystemErrorCodes.NavigatorDataCachingUpdateFailed.value(),
				// "Navigator Data Cache Update failed::"+ pe.getMessage() ,
				// pe.getClass().getName(), this.getClass().getName(),
				// query.getExceptionXml());
				throw new SearchException(SystemErrorCodes.NAVIGATOR_DATA_CACHING_UPDATE_FAILED, "Navigator Data Cache Update failed: " + pe.getMessage(), pe);
			}
		}
	}

	public void maintainCache(int docIndex, int numPagesCacheAhead) throws SearchException, InfrastructureException {
		if (!searchInitialized) {
			throw new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, "Search is not initialized");
		}

		// try {

		PageCache cache = new PageCache(sessionID);

		if (cachePage) {
			int pageNumber = getPageIndex(pageSize, docIndex);

			try {
				cache.cachePage(pageNumber, this.docIDList, searchID);
			} catch (PageCacheException e) {
				throw new SearchException(SystemErrorCodes.PAGE_DATA_CACHING_UPDATE_FAILED, "Page Data Cache Update failed: " + e.getMessage(), e);
			}
			//log4j.info("Current page had been cached");
			if (numPagesCacheAhead > 0) {
				++pageNumber;

				for (int i = 0; i < numPagesCacheAhead; ++i) {
					int offset = ((pageNumber - 1) * pageSize);

					if (searchResult.getHitCount() > offset) {
						usenavigators = false;
						List<DocID> dlist;
						// try {
						dlist = search(offset, pageSize);
						/*
						 * } catch (SearchQueryExecutionException e) {
						 * ExceptionWriterHelper
						 * .writeException(SystemErrorCodes.
						 * FastDataFetchForCacheFailed.value(),
						 * "Fast data call failed", e.getClass().getName(),
						 * this.getClass().getName(), query.getExceptionXml());
						 * throw new SearchException(e.getMessage(), e); }
						 */
						try {
							cache.cachePage(pageNumber, dlist, searchID);
						} catch (PageCacheException e) {
							// ExceptionWriterHelper.writeException(,
							// "Page Data Cache Update failed::"+
							// e.getMessage(), e.getClass().getName(),
							// this.getClass().getName(),
							// query.getExceptionXml());
							throw new SearchException(SystemErrorCodes.PAGE_DATA_CACHING_UPDATE_FAILED, "Page Data Cache Update failed: " + e.getMessage(), e);
						}
						//log4j.info("Next page has been cached");
						++pageNumber;
						usenavigators = true;
					} else {
						break;
					}
				}

			}
		}
		/*
		 * } catch (Exception e) { throw new SearchException(e); }
		 */
	}

	private String getSortDirection(Sort sortOption) {
		StringBuffer sb = new StringBuffer();
		String FastDESC = "-";
		String FastASC = "+";
		sb.append(sortOption.getSortDirection().equalsIgnoreCase(Sort.DOWN) ? FastDESC : FastASC);
		return sb.toString();
	}

}
