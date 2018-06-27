package org.ei.util;

import java.util.Hashtable;

/**
*
*	This class is a self managing table that implements a Least Recently Used
*	algorithm.  You need to tell it the maxSize and It will grow to that size.
*	When you add objects past its maxSize it removes the Least
*	Recently accessed key from the table and adds the new key.
*
*
**/


public class LRUTable
{

	private Hashtable cache = new Hashtable();
	private LRUListItem head;
	private LRUListItem tail;
	private int maxSize;

	public LRUTable(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public synchronized void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public synchronized int getMaxSize()
	{
		return this.maxSize;
	}
	
	public synchronized boolean containsKey(String key)
	{
		return this.cache.containsKey(key);
	}

	public synchronized Object get(String key)
	{
		LRUListItem item = (LRUListItem)cache.get(key);
		if(item == null)
		{
			return null;
		}

		unlink(item);
		moveItemToTail(item);
		return item.getObject();		
	}
	
	private void moveItemToTail(LRUListItem item)
	{
		if(head == null)
		{
			head = item;
		}
		else if(tail == null)
		{
			tail = item;
			item.setFrontLink(head);
			head.setBackLink(item);
			
		}
		else
		{
			tail.setBackLink(item);
			item.setFrontLink(tail);
			tail = item;
		}

	}


	public synchronized void put(String key, Object o)
	{
		LRUListItem item = new LRUListItem(key, o);
		remove(key);
		if(maxSize <= cache.size())
		{
			popLRU();
		}

		moveItemToTail(item); 
		cache.put(key, item);              
	}


	private void unlink(LRUListItem item)
        {
                if(item.equals(head))
                {
                        head = head.getBackLink();
                        if(head != null)
                        {
                                head.setFrontLink(null);
                                if(head.equals(tail))
                                {
                                        tail = null;
                                }
                        }
			item.setBackLink(null);
 
                }
                else if(item.equals(tail))
                {
                        tail = tail.getFrontLink();
                        tail.setBackLink(null);
                        if(tail.equals(head))
                        {
                                tail = null;
                        }
 			item.setFrontLink(null);
                }
                else
                {
                        LRUListItem bLink = item.getBackLink();
                        LRUListItem fLink = item.getFrontLink();
                        bLink.setFrontLink(fLink);
                        fLink.setBackLink(bLink);
                        item.setFrontLink(null);
                        item.setBackLink(null);
                }
        }                        

	public void remove(String key)
	{
		LRUListItem item = (LRUListItem)cache.get(key);
                if(item != null)
                {
                        unlink(item);
                        cache.remove(key);
                }      
	}

	
	private void popLRU()
	{
		cache.remove(head.getKey());
                unlink(head);
	}

	class LRUListItem
	{

		private LRUListItem frontLink;
		private LRUListItem backLink;
		private Object userObject;
		private String key;

		LRUListItem(String key, Object userObject)
		{
			this.key = key;
			this.userObject = userObject;
		}

		public Object getObject()
		{
			return this.userObject;
		}

		public void setObject(Object userObject)
		{
			this.userObject = userObject;
		}

		public String getKey()
		{
			return this.key;
		}

		public LRUListItem getFrontLink()
		{
			return this.frontLink;
		}

		public LRUListItem getBackLink()
		{
			return this.backLink;
		}

		public void setFrontLink(LRUListItem frontLink)
		{
			this.frontLink = frontLink;
		}

		public void setBackLink(LRUListItem backLink)
		{
			this.backLink = backLink;
		}

	
		public boolean equals(LRUListItem item)
		{
			boolean e = false;
			if(item.getKey().equals(this.key))
			{
				e = true;
			}


			return e;
		}
	}

}
