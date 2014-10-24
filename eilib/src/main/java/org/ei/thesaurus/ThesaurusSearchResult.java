package org.ei.thesaurus;


public class ThesaurusSearchResult
{
	private int hitCount;
	private ThesaurusSearchControl sc;


	public ThesaurusSearchResult(ThesaurusSearchControl sc)
	{
		this.sc = sc;
	}

	public int getHitCount()
	{
		return this.hitCount;
	}

	public void setHitCount(int hitCount)
	{
		this.hitCount = hitCount;
	}

	public ThesaurusPage pageAt(int index)
		throws ThesaurusException
	{
		return sc.pageAt(index);
	}

}