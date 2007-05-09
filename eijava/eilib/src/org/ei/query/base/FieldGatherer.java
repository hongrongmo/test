package org.ei.query.base;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Term;

public class FieldGatherer
	extends BaseNodeVisitor
{
	private List fieldList = new ArrayList();

	public String[] gatherFields(BooleanQuery bQuery)
	{
		descend(bQuery);
		return (String[])fieldList.toArray(new String[fieldList.size()]);
	}

	public void visitWith(BooleanQuery bQuery)
	{
		descend(bQuery);
	}

	public void visitWith(AndQuery aQuery)
	{
		descend(aQuery);
	}

	public void visitWith(OrQuery oQuery)
	{
		descend(oQuery);
	}

	public void visitWith(NotQuery nQuery)
	{
		descend(nQuery);
	}

	public void visitWith(Expression exp)
	{
		FieldGetter fg = new FieldGetter();
		fieldList.add(fg.getFieldValue(exp));
		// Not descending further, so will never see the BooleanPhrase.
	}

	public void visitWith(BooleanPhrase bPhrase)
	{
		/*
		If we have descended this far then we entered a bPhrase
		which is not part of an expression, in otherwords an ALL fields search.
		*/
		fieldList.add("ALL");
	}
}