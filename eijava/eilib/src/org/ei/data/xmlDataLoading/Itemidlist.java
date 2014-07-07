package org.ei.data.xmlDataLoading;
import java.util.*;

public class Itemidlist extends BaseElement
{
	List itemids = new ArrayList();
	String pii;
	String doi;
	Itemid itemid = null;

	public void setItemids(List itemids)
	{
		this.itemids = itemids;
	}


	public void setItemid(String itemidString)
	{
		itemid = new Itemid();
		itemid.setItemid(itemidString);
		addItemid(itemid);
	}

	public void addItemid(Itemid itemid)
	{
		itemids.add(itemid);
	}

	public List getItemids()
	{
		return itemids;

	}

	public void setPii(String pii)
	{
		this.pii = pii;
	}

	public String getPii()
	{
		return this.pii;
	}

	public void setDoi(String doi)
	{
		this.doi = doi;
	}

	public String getDoi()
	{
		return this.doi;
	}

	public void setItemid_idtype(String itemid_idtypeString)
	{
		itemid.setItemid_idtype(itemid_idtypeString);

	}




}