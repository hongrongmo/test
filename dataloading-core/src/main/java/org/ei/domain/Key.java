package org.ei.domain;

public class Key
{
	private String key;
	private String subKey;
	private String label;

	public Key(String key)
	{
		this.key = key;
		this.subKey = key.substring(0,key.length()-1);
	}

	public Key(String key,
		String label)
	{
		this.key = key;
		this.subKey = key.substring(0,key.length()-1);
		this.label = label;
	}

	public Key(String key,
			   String subKey,
			   String label)
	{
		this.key = key;
		this.subKey = subKey;
		this.label = label;
	}

	public Key(Key k,
			   String label)
	{
		this.key = k.getKey();
		this.subKey = k.getSubKey();
		this.label = label;
	}


	public String getKey()
	{
		return this.key;
	}

	public String getSubKey()
	{
		return this.subKey;
	}

	public String getLabel()
	{
		return this.label;
	}

	public boolean equals(Object o)
	{
		Key k = null;

		try
		{
			k = (Key)o;
			if(this.key.equals(k.getKey()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}

	}

	public int hashCode()
	{
		return key.hashCode();
	}
}