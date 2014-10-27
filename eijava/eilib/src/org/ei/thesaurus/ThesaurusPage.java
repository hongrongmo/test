package org.ei.thesaurus;

import java.util.ArrayList;
import java.util.List;

public class ThesaurusPage
	implements ThesaurusNode
{

	private List recs = new ArrayList();


	public void accept(ThesaurusNodeVisitor v)
		throws ThesaurusException
	{
		v.visitWith(this);
	}

	public ThesaurusPage(int size)
	{
		for(int i = 0; i<size;i++)
		{
			recs.add(null);
		}
	}

	public void set(int index, ThesaurusRecord rec)
	{
		recs.set(index, rec);
	}

	public void add(ThesaurusRecord rec)
	{
		recs.add(rec);
	}

	public ThesaurusRecord get(int index)
	{
		if(recs.size() == 0)
		{
			return null;
		}
		else
		{
			return (ThesaurusRecord)recs.get(index);
		}
	}

	public int size()
	{
		return recs.size();
	}
}