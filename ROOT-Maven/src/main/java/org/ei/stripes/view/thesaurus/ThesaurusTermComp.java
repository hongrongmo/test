package org.ei.stripes.view.thesaurus;

import java.util.*;
import org.ei.thesaurus.*;


public class ThesaurusTermComp
	implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		int result=0;
		try{
		ThesaurusRecord tag1 = (ThesaurusRecord)o1;
		ThesaurusRecord tag2 = (ThesaurusRecord)o2;

		String term1="";
		String term2="";
		if(tag1 != null && tag1.getRecID()!=null)
		{
			term1 = tag1.getRecID().getMainTerm();
		}
		if(tag2 != null && tag2.getRecID() != null)
		{
			term2 = tag2.getRecID().getMainTerm();
		}
		result = term1.toLowerCase().compareTo(term2.toLowerCase());
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}