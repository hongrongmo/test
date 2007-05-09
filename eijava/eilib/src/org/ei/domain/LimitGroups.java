package org.ei.domain;

import java.util.*;

public class LimitGroups
{
	private Map fieldMap = new HashMap();

	public void addLimiter(String field, String value)
	{
		if(fieldMap.containsKey(field))
		{
			List values = (List)fieldMap.get(field);
			values.add(value);
		}
		else
		{
			ArrayList values = new ArrayList();
			values.add(value);
			fieldMap.put(field,
				         values);
		}
	}

	public int size()
	{
		return this.fieldMap.size();
	}

	public String[] getFields()
	{
		String[] keys = new String[fieldMap.size()];
		Iterator it = fieldMap.keySet().iterator();
		int i=0;
		while(it.hasNext())
		{
			String field = (String)it.next();
			keys[i] = field;
			i++;
		}

		return keys;
	}

	public String[] getValues(String key)
	{
		ArrayList values = (ArrayList)fieldMap.get(key);
		return (String[])values.toArray(new String[values.size()]);
	}
}