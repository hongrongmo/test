package org.ei.data.xmlDataLoading;
import java.util.*;

public class Item_info extends BaseElement
{
	Copyright copyrights;
	Itemidlist itemidlist;
	History history;
	List dbcollections = new ArrayList();

	public void setItemidlist(Itemidlist itemidlist)
	{
		this.itemidlist = itemidlist;
	}

	public Itemidlist getItemidlist()
	{
		return this.itemidlist;
	}

	public void setHistory(History history)
	{
		this.history = history;
	}

	public History getHistory()
	{
		return this.history;
	}

	public void setCopyright(Copyright copyrights)
	{
		this.copyrights = copyrights;
	}

	public Copyright getCopyright()
	{
		return copyrights;
	}

	public void setDbcollection(List dbcollections)
	{
		this.dbcollections = dbcollections;
	}

	public void addDbcollection(Dbcollection dbcollection)
	{
		dbcollections.add(dbcollection);
	}

	public List getDbcollection()
	{
		return dbcollections;
	}
}