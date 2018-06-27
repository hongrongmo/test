package org.ei.fulldoc;

import java.util.Vector;

public class OHUBResponse
{
	
	private Vector items = new Vector();
	
	public OHUBResponseItem itemAt(int index)
	{
		return (OHUBResponseItem)this.items.elementAt(index);	
	}
	
	public void addItem(OHUBResponseItem item)
	{
		items.addElement(item);	
	}
	
	public int itemCount()
	{
		return items.size();	
	}
}
