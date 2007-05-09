package org.ei.tags;

import java.util.*;

public class SizeComp
	implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Tag tag1 = (Tag)o1;
		Tag tag2 = (Tag)o2;

		int tagcount1 = tag1.getCount();
		int tagcount2 = tag2.getCount();

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