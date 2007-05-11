package org.ei.data.upt.runtime;

import java.util.*;
import org.ei.domain.*;

public class RefPager
{
	private List docIDs;
	private int pageSize;
	private PatRefQuery query;
	private String sessionID;

	public RefPager(List docIDs,
					int pageSize,
					PatRefQuery query,
					String sessionID)
	{
		this.pageSize = pageSize;
		this.docIDs = docIDs;
		this.query = query;
		this.sessionID = sessionID;
	}

	public int getSetSize()
	{
		return docIDs.size();
	}

	public String getQuery()
	{
		return query.getQuery();
	}

	public RefPage getPage(int offset)
		throws Exception
	{
		ArrayList range = new ArrayList();
		int count = 0;
		for(int i=offset; i<docIDs.size(); i++)
		{
			PatWrapper w = (PatWrapper)docIDs.get(i);
			DocID docID = w.did;
			range.add(docID);
			count++;
			if(count==pageSize)
			{
				break;
			}
		}

		// Build the docs
		MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
		List builtDocs = builder.buildPage(range,
										   Citation.CITATION_FORMAT);


		// Build the pageEntries
		PageEntryBuilder peBuilder = new PageEntryBuilder(this.sessionID);
		List page = peBuilder.buildPageEntryList(builtDocs);
		RefPage rp = new RefPage(page,offset);
		return rp;
	}

	 public List getPageIDs (int startRange,
	 						 int endRange,
	 						 int resultscount)
			throws Exception
	{

		ArrayList range = new ArrayList();
		int count = 0;
		if(startRange != 0)
		{
			startRange--;
		}
		for(int i=startRange; i<endRange; i++)
		{
			PatWrapper w = (PatWrapper)docIDs.get(i);
			DocID docID = w.did;
			range.add(docID);
			count++;
			if(count==resultscount)
			{
				break;
			}

		}

		return range;

	}

}
