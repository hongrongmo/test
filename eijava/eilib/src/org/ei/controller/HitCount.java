package org.ei.controller;

import org.ei.security.utils.SecureID;

public class HitCount
{
	private String key;
	private long createTime;
	private int hits = 0;
	private boolean blocked;


	public HitCount(String key) throws Exception

	{
		this.key = key;
		this.createTime = System.currentTimeMillis();
		this.hits++;
		this.blocked = false;
	}


	public HitCount(String key, String value)
	{
		this.key = key;
		String [] valueArray = value.split(":");
		this.createTime = Long.parseLong(valueArray[0]);
		this.hits = Integer.parseInt(valueArray[1]);
		this.blocked = new Boolean(valueArray[2]).booleanValue();
		this.hits++;
		long curTime = System.currentTimeMillis();
		long diff =  curTime - this.createTime;
		if(diff > 10000)
		{
			this.hits = 0;
			this.createTime = curTime;
		}

		if(this.hits > 7)
		{
			this.blocked = true;
		}
	}

	public String getKey()
	{
		return this.key;
	}

	public long getCreateTime()
	{
		return this.createTime;
	}

	public boolean getBlocked()
	{
		return this.blocked;
	}

	public String stringValue()
	{
		return this.createTime+":"+this.hits+":"+Boolean.toString(this.blocked);
	}
}