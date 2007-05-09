package org.ei.domain;

import java.util.*;
import org.ei.query.base.HitHighlighter;
import org.ei.domain.DatabaseConfig;

public class DedupBroker
{
	public List getDupList(String dups)
	{
		List outputList = new ArrayList();
		List dupIds = new ArrayList();
		DocID docIDObj = null;
		String docId = null;
		DatabaseConfig dbConfig = DatabaseConfig.getInstance();
		StringTokenizer st = new StringTokenizer(dups, ":", false);
		while(st.hasMoreTokens())
		{
			dupIds.add(st.nextToken().trim());
		}

		for(int i = 0; i < dupIds.size(); i++)
		{
			docId = (String)dupIds.get(i);
			String db = docId.substring(0,3);
			docIDObj = new DocID(i+1, docId, dbConfig.getDatabase(db));
			outputList.add(docIDObj);
		}

		return outputList;
	}


	public List getPage(List wantedList, int currentPage, int pageSize)
				throws Exception
	{

		List outputList = new ArrayList();
		DocID docObj = null;

		String docId = null;
		int max = wantedList.size();

		if(pageSize > max)
		{
			pageSize = max;
		}

		int iterator = currentPage + pageSize - 1;
		if(iterator > max)
		{
			iterator = max;
		}

		for(int i = currentPage - 1; i < iterator; i++)
		{
			DocID docID = (DocID)wantedList.get(i);
			docId = docID.getDocID();

			docObj = new DocID(i+1,docId,docID.getDatabase());
			if(docID.isDup() == true)
			{
				docObj.setIsDup(true);
			}
			String dupIds = docID.getDupIds();
			if(dupIds != null)
			{
				docObj.setDupIds(dupIds);
			}
			outputList.add(docObj);
		}

		return outputList;
	}

	public List getFromCache(int pageNumber,
							 String dedupSearchID,
							 String sessionID)
	{
		PageCache pageCache = new PageCache(sessionID);
		List docIDList = new ArrayList();

		try
		{
			docIDList = pageCache.getPage(pageNumber,
										  dedupSearchID);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return docIDList;

	}


	public PageEntry buildEntry(int docIndex,
								List docIDList,
								String dataFormat,
								String sessionID,
								HitHighlighter highlighter)
	{
        List entryList = new ArrayList();
        List pageEntries = null;
        PageEntry pEntry = null;
		try
		{

            for(int x=0; x<docIDList.size(); ++x)
            {
                DocID id = (DocID)docIDList.get(x);
                if(id.getHitIndex() == docIndex)
                {
                    entryList.add(id);
                    break;
                }
            }

			DocumentBuilder builder = new MultiDatabaseDocBuilder();
			List docList = builder.buildPage(entryList,
											 dataFormat);


			PageEntryBuilder pb = new PageEntryBuilder(sessionID);
			pageEntries = pb.buildPageEntryList(docList);
			pEntry = (PageEntry)pageEntries.get(0);
			pEntry.setHitHighlighter(highlighter);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return pEntry;

	}

	public PageEntry buildEntry(int docIndex,
								List docIDList,
								String dataFormat,
								String sessionID)
	{
        List entryList = new ArrayList();
        List pageEntries = null;
        PageEntry pEntry = null;
		try
		{

            for(int x=0; x<docIDList.size(); ++x)
            {
                DocID id = (DocID)docIDList.get(x);
                if(id.getHitIndex() == docIndex)
                {
                    entryList.add(id);
                    break;
                }
            }

			DocumentBuilder builder = new MultiDatabaseDocBuilder();
			List docList = builder.buildPage(entryList,
											 dataFormat);


			PageEntryBuilder pb = new PageEntryBuilder(sessionID);
			pageEntries = pb.buildPageEntryList(docList);
			pEntry = (PageEntry)pageEntries.get(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return pEntry;

	}


	public Page buildPage(List pageDocIDList, String format, String sessionID) throws Exception
	{

		Page currentPage = new Page();
		DocumentBuilder builder = new MultiDatabaseDocBuilder();
		List docList = builder.buildPage(pageDocIDList,
										 format);

		PageEntryBuilder pb = new PageEntryBuilder(sessionID);
		List  pageEntries = pb.buildPageEntryList(docList);
		currentPage.addAll(pageEntries);


		return currentPage;
	}
}