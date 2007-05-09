package org.ei.domain;

import java.util.*;

public class DedupData
{
	private LinkedHashMap docIDs = new LinkedHashMap();
	private HashMap databases = new HashMap();

	public void add(DocID docID)
	{
		String database = getDatabase(docID.getDatabase());
		if(databases.containsKey(database))
		{
			Map m = (Map)databases.get(database);
			m.put(docID.getDocID(), docID);
		}
		else
		{
			LinkedHashMap m = new LinkedHashMap();
			m.put(docID.getDocID(), docID);
			databases.put(database, m);
		}

		this.docIDs.put(docID.getDocID(), docID);
	}

	private String getDatabase(Database database)
	{
		if(database.isBackfile())
		{
			if(database.getID().equals("c84"))
			{
				return "cpx";
			}
			else
			{
				return "ins";
			}

		}

		return database.getID();
	}

	public void replace(DocID oldDocID,
			    		DocID newDocID)
	{
		docIDs.remove(oldDocID.getDocID());
		docIDs.put(newDocID.getDocID(), newDocID);

		String oldDatabase = getDatabase(oldDocID.getDatabase());
		Map oldDatabaseMap = (Map)databases.get(oldDatabase);
		oldDatabaseMap.remove(oldDocID.getDocID());

		String newDatabaseID = getDatabase(newDocID.getDatabase());

		if(databases.containsKey(newDatabaseID))
		{
			Map m = (Map) databases.get(newDatabaseID);
			m.put(newDocID.getDocID(), newDocID);
		}
		else
		{
			Map m = new LinkedHashMap();
			m.put(newDocID.getDocID(), newDocID);
			databases.put(newDatabaseID, m);
		}
	}

	public List getDocIDs()
	{
		return fillList(docIDs.values());
	}

	public List getDocIDs(String database)
	{
		Map ids = (Map)databases.get(database);
		return fillList(ids.values());
	}

	public Map getDatabases()
	{
		return this.databases;
	}

	private List fillList(Collection col)
	{
		ArrayList list = new ArrayList();
		Iterator it = col.iterator();
		int index = 1;
		while(it.hasNext())
		{
			DocID docID = (DocID)it.next();
			docID.setHitIndex(index);
			list.add(docID);
			index++;
		}

		return list;
	}
}