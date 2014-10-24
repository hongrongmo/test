package org.ei.tags;

import java.util.*;

public class AlphaComp
	implements Comparator
{

	public int compare(Object o1, Object o2)
	{
		Tag tag1 = (Tag)o1;
		Tag tag2 = (Tag)o2;

		String tagname1 = tag1.getTagSearchValue();
		String tagname2 = tag2.getTagSearchValue();
		return tagname1.compareTo(tagname2);

	}
}