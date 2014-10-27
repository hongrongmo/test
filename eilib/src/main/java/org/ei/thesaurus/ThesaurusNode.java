package org.ei.thesaurus;


public interface ThesaurusNode
{
	public abstract void accept(ThesaurusNodeVisitor visitor)
		throws ThesaurusException;

}