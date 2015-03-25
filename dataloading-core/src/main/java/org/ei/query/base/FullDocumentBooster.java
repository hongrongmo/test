package org.ei.query.base;

import org.ei.parser.ParseNode;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanAnd;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.CloseParen;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OpenParen;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;


public class FullDocumentBooster
	extends BaseNodeVisitor implements RelevanceBooster
{

	private FieldGetter fget = new FieldGetter();
	private FieldSetter fset = new FieldSetter();

	private TermGatherer gatherer = new TermGatherer();
	private String currentField;

	public BooleanQuery applyBoost(BooleanQuery bQuery)
	{
		bQuery.accept(this);
		return bQuery;
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
		String field = fget.getFieldValue(exp);
		if(field.equalsIgnoreCase("ti"))
		{

			OpenParen oParen = new OpenParen("(");
			CloseParen cParen = new CloseParen(")");
			ParseNode parent = exp.getParent();
			Expression exp1 = (Expression)exp.deepCopy();
			Expression exp2 = (Expression)exp.deepCopy();
			fset.setFieldValue(exp2, "cv");
			BooleanAnd bAnd = new BooleanAnd("AND");
			BooleanOr bOr = new BooleanOr("OR");


			BooleanQuery wrappedBQ1 = new BooleanQuery(oParen,new BooleanQuery(exp1),cParen);
			BooleanQuery wrappedBQ2 = new BooleanQuery(oParen,new BooleanQuery(exp2),cParen);
			AndQuery andQuery = new AndQuery(wrappedBQ1, bAnd, wrappedBQ2);
			BooleanQuery bandQuery = new BooleanQuery(andQuery);
			BooleanQuery bandParenQuery = new BooleanQuery(oParen,
														   bandQuery,
														   cParen);
			BooleanQuery wrappedBQ = new BooleanQuery(oParen,new BooleanQuery(exp),cParen);
			OrQuery orQuery = new OrQuery(wrappedBQ,
						                   bOr,
						                   bandParenQuery);
			BooleanQuery bOrQuery = new BooleanQuery(orQuery);
			BooleanQuery bandQueryParen2 = new BooleanQuery(oParen,
															bOrQuery,
															cParen);
			parent.setChildAt(exp.getChildIndex(),
							  bandQueryParen2);
		}
	}

	public void visitWith(BooleanPhrase bPhrase)
	{
		descend(bPhrase);
	}

	public void visitWith(AndPhrase aPhrase)
	{
		descend(aPhrase);
	}

	public void visitWith(OrPhrase oPhrase)
	{
		descend(oPhrase);
	}
}