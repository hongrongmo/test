package org.ei.tags;

import java.util.*;

public class TimeComp
	implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Tag tag1 = (Tag)o1;
		Tag tag2 = (Tag)o2;

		long tagcount1 = tag1.getTimestamp();
		long tagcount2 = tag2.getTimestamp();

		if(tagcount1 == tagcount2)
		{
			return 0;
		}
		else if(tagcount1 > tagcount2)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}