 package org.ei.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.domain.navigators.EiNavigator;
import org.ei.domain.navigators.NavigatorCache;
import org.ei.domain.navigators.ResultNavigator;
import org.ei.query.base.HitHighlighter;

public class FastSearchControl
    implements SearchControl
{
    private static Log log = LogFactory.getLog(FastSearchControl.class);

    protected Query query;
    protected SearchResult searchResult;
    protected boolean checkBasket = true;

    private boolean searchInitialized = false;
    private int pageSize;
    private String sessionID;

    private List docIDList;
    private int currentPage;
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

    public boolean isInitialized()
    {
        return searchInitialized;
    }

    public boolean hasError()
    {
        if(errorCode != null)
        {
            return true;
        }

        return false;
    }

	public boolean checkBasket()
	{
		return this.checkBasket;
	}

	public void checkBasket(boolean checkBasket)
	{
		this.checkBasket = checkBasket;
	}

    public String getErrorCode()
    {
        return this.errorCode;
    }

    public void setUseNavigators(boolean usenavs)
    {
        this.usenavigators = usenavs;
    }

    public ResultNavigator getNavigator()
    {
        int count = 0;
        NavigatorCache nc = new NavigatorCache(sessionID);
        while(this.navigator == null)
        {
            try {
                count++;
                this.navigator = nc.getFromCache(query.getID());
                Thread.sleep(100);
            }
            catch(PageCacheException e)
            {
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            if(count == 5)
            {
                break;
            }
        }
        return this.navigator;
    }


    public void setHitHighlighter(HitHighlighter h)
    {
        this.highlighter = h;
    }


    public void closeSearch()
        throws SearchException
    {
        this.highlighter = null;
        searchInitialized = false;
    }

    protected DocumentBuilder getBuilder()
    {
        return new MultiDatabaseDocBuilder();
    }




    public SearchControl newInstance(Database d)
    {
        return new FastSearchControl();
    }

    public SearchResult openSearch(Query sQuery,
                                   String sSessionID,
                                   int nPageSize,
                                   boolean lazy)
            throws SearchException
    {

        if(searchInitialized)
        {
            throw new SearchException(new Exception("Search control already active"));
        }
        else
        {
            searchInitialized = true;
        }

        searchResult = new SearchResult(this);
        searchResult.setQuery(sQuery);

		if((sQuery.getDataBase() & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK)
		{
			this.backfileStandAlone = true;
		}

		if((sQuery.getDataBase() & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK)
		{
			this.archiveStandAlone = true;
		}


        this.query = sQuery;
        this.sessionID = sSessionID;
        this.pageSize = nPageSize;
        this.lazy = lazy;
        this.searchID = query.getID();

        if(!lazy)
        {
            this.docIDList = search(0,nPageSize);
            cachePage = true;
        }
        else
        {
            this.docIDList = null;
            cachePage = false;
        }


        return searchResult;
    }



    protected ArrayList search(int offset,
                             int pSize)
            throws SearchException
    {
        ArrayList dList = new ArrayList();

        try
        {


            DatabaseConfig dConfig = DatabaseConfig.getInstance();

            String fastSearchString = query.getSearchQuery();
            Sort sortOption = query.getSortOption();

            FastClient client = new FastClient();
            client.setBaseURL(BASE_URL);
            client.setQueryString(fastSearchString);
            client.setOffSet(offset);
            client.setPageSize(pSize);
            client.setDoClustering(false);
            client.setResultView("ei");
            client.setDoNavigators(usenavigators);

            if(usenavigators)
            {
                client.setNavigatorMask(query.getGatheredMask());
            }

            Database[] databases = dConfig.getDatabases(query.getDataBase());


            if(databases.length > 1)
            {
                client.setDoCatCount(true);
            }

            if(sortOption != null)
            {
                String sortField = sortOption.getSortField();
                if(sortOption.getSortField().equals(Sort.PUB_YEAR_FIELD)) {
                  int qmask = query.getGatheredMask();
                  System.out.println("Checking mask for YR sort " + qmask);
                  if((qmask & Sort.DTS_SORT_MASK) == qmask) {
                    System.out.println("Changing Fast sort field to DTS ");
                    sortField = Sort.DTS_SORT_FIELD;
                  }
                }
                client.setPrimarySort(sortField);
                client.setPrimarySortDirection(getSortDirection(sortOption));
            }

            client.search();
            this.errorCode = client.getErrorCode();

            if(usenavigators)
            {
				Hashtable hnavs = getNavigators(client.getNavigators());
                navigator = new ResultNavigator(hnavs,
                						        query.getDatabases());
            }

            int docCount = client.getHitCount();

            searchResult.setHitCount(docCount);
            searchResult.setResponseTime(client.getSearchTime());
            List ds = client.getDocIDs();

            for(int i=0;i<ds.size();i++)
            {
                String[] fields = (String[])ds.get(i);
				String did = getDocID(fields[0]);
                DocID docID = new DocID(++offset,
                						did,
                                        dConfig.getDatabase(did.substring(0,3)),
                                        fields[1],
                                        fields[2],
                                        Integer.parseInt(fields[3]));
                dList.add(docID);

            }
        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        return dList;
    }

	private String getDocID(String mid)
	{
		if(backfileStandAlone &&
		   mid.indexOf(DatabaseConfig.C84_PREF) == 0)
    	{
			return DatabaseConfig.CBF_PREF.concat(mid.substring(3));
    	}
    	else if(archiveStandAlone &&
    			mid.indexOf("ibf") == 0)
    	{
			return "ibs".concat(mid.substring(3));
		}

		return mid;
	}

	private Hashtable getNavigators(Hashtable hnavs)
	{
		if(backfileStandAlone)
		{
			List navlist =(List) hnavs.get(EiNavigator.DB);
			if(navlist != null)
			{
				for(int i=0; i<navlist.size(); i++)
				{
					String[] dataElement  = (String[]) navlist.get(i);
					if(dataElement[0].equals(DatabaseConfig.C84_PREF))
					{
						dataElement[0] = DatabaseConfig.CBF_PREF;
						break;
					}
				}
			}
		}

		if(archiveStandAlone)
		{
			List navlist =(List) hnavs.get(EiNavigator.DB);
			if(navlist != null)
			{
				for(int i=0; i<navlist.size(); i++)
				{
					String[] dataElement  = (String[]) navlist.get(i);
					if(dataElement[0].equals("ibf"))
					{
						dataElement[0] = "ibs";
						break;
					}
				}
			}
		}

		return hnavs;
	}

    public Page pageAt(int docIndex,
                       String dataFormat)
            throws SearchException
    {

        if(!searchInitialized)
        {
            throw new SearchException(new Exception("Search is not initialized"));
        }

        Page page = null;


        try
        {

            if(lazy || getPageIndex(this.pageSize, docIndex) != 1)
            {
                PageCache pageCache = new PageCache(sessionID);
                this.docIDList = pageCache.getPage(getPageIndex(pageSize, docIndex),
                                                   searchID);


                if(this.docIDList == null)
                {
                    int offset = ((getPageIndex(pageSize, docIndex)-1) * pageSize);
                    usenavigators = false;
                    this.docIDList = search(offset,
                                            this.pageSize);
                    cachePage = true;
                }
            }
            page = buildPage(this.docIDList, dataFormat);
            page.setHitHighlighter(this.highlighter);

        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        page.setPageIndex(docIndex);
        return page;
    }

    public List getDocIDRange(int startRange,
                              int endRange)
            throws SearchException
    {

        if(!searchInitialized)
        {
            throw new SearchException(new Exception("Search is not initialized"));
        }

        ArrayList dList = null;

        int offset = startRange - 1;
        int pSize = endRange - offset;
        cachePage = false;
        try
        {
            dList = search(offset, pSize);
        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        return dList;
    }


	public FastDeduper dedupSearch(int dupsetSize,
								   String fieldPref,
								   String databasePref)
		throws Exception
	{
		DatabaseConfig dConfig = DatabaseConfig.getInstance();
		String fastSearchString = query.getSearchQuery();
		Sort sortOption = query.getSortOption();

		FastClient client = new FastClient();
		client.setBaseURL(BASE_URL);
		client.setQueryString(fastSearchString);
		client.setOffSet(0);
		client.setPageSize(dupsetSize);

		if(sortOption != null)
		{
			client.setPrimarySort(sortOption.getSortField());
			client.setPrimarySortDirection(getSortDirection(sortOption));
		}

		return client.dedupSearch(fieldPref,
								  databasePref);
	}


    public List getDocIDList(int pSize)
        throws SearchException
    {

        if(!searchInitialized)
        {
            throw new SearchException(new Exception("Search is not initialized"));
        }

        ArrayList dList = null;
        cachePage = false;
        try
        {
            dList = search(0, pSize);
        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        return dList;
    }


    public PageEntry entryAt(int docIndex, String dataFormat)
                throws SearchException
    {

        if(!searchInitialized)
        {
            throw new SearchException(new Exception("Search is not initialized"));
        }

        List entryList = new ArrayList();
        List pageEntries = null;
        PageEntry pEntry = null;

        try
        {

            if(lazy || getPageIndex(this.pageSize, docIndex) != 1)
            {
                PageCache pageCache = new PageCache(sessionID);
                this.docIDList = pageCache.getPage(getPageIndex(pageSize, docIndex), searchID);

                if(this.docIDList == null)
                {

                    int offset = ((getPageIndex(pageSize, docIndex)-1) * pageSize);
                    usenavigators = false;
                    docIDList = search(offset, this.pageSize);
                    cachePage = true;
                }
            }

            for(int x=0; x<this.docIDList.size(); ++x)
            {
                DocID id = (DocID)this.docIDList.get(x);
                if(id.getHitIndex() == docIndex)
                {
                    entryList.add(id);
                    break;
                }
            }

            DocumentBuilder builder = getBuilder();
            List docList = builder.buildPage(entryList,
                                             dataFormat);

            PageEntryBuilder pb = new PageEntryBuilder(sessionID);
            pageEntries = pb.buildPageEntryList(docList);
            pEntry = (PageEntry)pageEntries.get(0);
            pEntry.setHitHighlighter(highlighter);
        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }

        return pEntry;
    }

    protected Page buildPage(List didList,
                             String dataFormat)
            throws Exception
    {
        Page currentPage = new Page();
        DocumentBuilder builder = getBuilder();
        List docList = builder.buildPage(didList,
                                         dataFormat);

		if(this.checkBasket)
		{
			//System.out.println("Checking basket");
        	PageEntryBuilder pb = new PageEntryBuilder(sessionID);
        	List  pageEntries = pb.buildPageEntryList(docList);
        	currentPage.addAll(pageEntries);
		}
		else
		{
			//System.out.println("Not checking basket");
			int dsize = docList.size();

			for(int i=0;i<dsize; i++)
			{
				PageEntry entry = new PageEntry();
				entry.setSelected(false);
				if((EIDoc)docList.get(i) != null)
				{
					entry.setDoc((EIDoc)docList.get(i));
					currentPage.add(entry);
				}
			}
		}
        return currentPage;
    }

    private int getPageIndex(int pSize, int dIndex)
    {
        int pageIndex;

        if ( (dIndex%pSize) == 0 )
        {
            // if the remainder is zero the page index will find in the following way
            pageIndex  = (dIndex/pSize);
        }
        else
        {
                        // otherwise find the page index in the following way
            pageIndex  = (dIndex/pSize) + 1;
        }

        return pageIndex;

    }

    public void maintainNavigatorCache()
        throws SearchException
    {
        if((usenavigators) && (navigator != null))
        {
            NavigatorCache nc = new NavigatorCache(sessionID);
            try
            {
                nc.addToCache(searchID,navigator);
            }
            catch(PageCacheException pe)
            {
                pe.printStackTrace();
                throw new SearchException(pe);
            }
        }
    }

    public void maintainCache(int docIndex,
                              int numPagesCacheAhead)
            throws SearchException
    {
        if(!searchInitialized)
        {
            throw new SearchException(new Exception("Search is not initialized"));
        }

        try
        {

            PageCache cache = new PageCache(sessionID);

            if(cachePage)
            {
                int pageNumber = getPageIndex(pageSize,
                                              docIndex);

                cache.cachePage(pageNumber,
                                this.docIDList,
                                searchID);

                if(numPagesCacheAhead > 0)
                {
                    ++pageNumber;

                    for(int i=0; i<numPagesCacheAhead; ++i)
                    {
                        int offset = ((pageNumber-1) * pageSize);

                        if(searchResult.getHitCount() > offset)
                        {
                            usenavigators = false;
                            List dlist = search(offset,pageSize);
                            cache.cachePage(pageNumber,
                                            dlist,
                                            searchID);
                            ++pageNumber;
                            usenavigators = true;
                        }
                        else
                        {
                            break;
                        }
                    }

                }
            }
        }
        catch(Exception e)
        {
            throw new SearchException(e);
        }
    }

    private String getSortDirection(Sort sortOption)
    {
        StringBuffer sb = new StringBuffer();
        String FastDESC = "-";
        String FastASC = "+";
        sb.append(sortOption.getSortDirection().equalsIgnoreCase(Sort.DOWN) ? FastDESC : FastASC);
        return sb.toString();
    }


}