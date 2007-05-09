package org.ei.tags;

import java.util.*;

public class ScopeComp
	implements Comparator
{


	public int[] order = {0,1,2,4,3};

	public int compare(Object o1, Object o2)
	{
		Tag tag1 = (Tag)o1;
		Tag tag2 = (Tag)o2;

		/*
			This a little confusing. The order array maintains
			a fixed order that every scope should sort by.

		*/

		int scopeOrder1 = order[tag1.getScope()];
		int scopeOrder2 = order[tag2.getScope()];

		if(scopeOrder1 == scopeOrder2)
		{
			if(scopeOrder1 == 4)
			{
				String group1 = tag1.getGroupID();
				String group2 = tag2.getGroupID();
				int groupComp = group1.compareTo(group2);
				if(groupComp == 0)
				{
					String tagValue1 = tag1.getTagSearchValue();
					String tagValue2 = tag2.getTagSearchValue();
					return tagValue1.compareTo(tagValue2);
				}
				else
				{
					return groupComp;
				}
			}
			else
			{
				String tagValue1 = tag1.getTagSearchValue();
				String tagValue2 = tag2.getTagSearchValue();
				return tagValue1.compareTo(tagValue2);
			}

		}
		else if(scopeOrder1 < scopeOrder2)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}