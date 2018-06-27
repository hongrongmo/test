package org.ei.thesaurus;

public interface ThesaurusNodeVisitor
{
	public abstract void visitWith(ThesaurusPage page)
		throws ThesaurusException;

	public abstract void visitWith(ThesaurusRecord record)
		throws ThesaurusException;

	public abstract void visitWith(ThesaurusRecordID id)
		throws ThesaurusException;

}