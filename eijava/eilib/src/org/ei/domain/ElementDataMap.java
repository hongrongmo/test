package org.ei.domain;

import java.util.*;

public class ElementDataMap
{
	Map emap = new LinkedHashMap();

	public void put(Key key, ElementData elementData)
	{
		emap.put(key, elementData);
	}

	public ElementData get(Key key)
	{
		return (ElementData)emap.get(key);
	}

	public boolean containsKey(Key key)
	{
		return emap.containsKey(key);
	}

	public Collection values()
	{
		return emap.values();
	}

	public Set keySet()
	{
		return emap.keySet();
	}
}