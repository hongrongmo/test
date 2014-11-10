package org.ei.stripes.view.thesaurus;

import java.util.*;

import org.ei.thesaurus.*;



/**
 * fix stars issue for browse search - Jan 28,2013
 * @author telebh
 *
 */

public class ThesaurusTermInfoComp
implements Comparator
{

	public int compare (Object obj1, Object obj2)
	{

		int result=0;
		try{
		ThesaurusRecord rec1 = (ThesaurusRecord)obj1;
		ThesaurusRecord rec2 = (ThesaurusRecord)obj2;

		//Update, Feb 26,2013
		String term1="";
		String term2="";
		if(rec1!=null && rec1.getRecID()!=null)
		{
			term1 = Integer.toString(rec1.getRecID().getRecordID());
		}

		if(rec2!=null && rec2.getRecID()!=null)
		{
			term2 = Integer.toString(rec2.getRecID().getRecordID());
		}

		//
		result = term1.toLowerCase().compareTo(term2.toLowerCase());

		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}


}
