package org.ei.query.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.ei.domain.DatabaseConfig;
import org.ei.domain.Database;
import org.ei.domain.navigators.EiNavigator;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Term;


public class ExpressionMask
	extends BaseNodeVisitor
{
	private List termList;
    private List dbfieldList;
    private boolean isnot = false;
    private int mask = 0;


	public int getExpressionMask(Expression exp)
	{
		exp.accept(this);
		return this.mask;
	}

	public void visitWith(Expression exp)
	{
		descend(exp);
	}

	public void visitWith(BooleanPhrase bPhrase)
	{
		descend(bPhrase);
	}

	public void visitWith(OrPhrase phrase)
	{
		descend(phrase);
	}

	public void visitWith(Phrase phrase)
	{
		descend(phrase);
	}

	public void visitWith(Term term)
	{
        String db = term.getValue().trim();
        int dbmask = getMask(db);
        mask = mask|dbmask;
	}

	private int getMask(String db)
	{
		DatabaseConfig dbconfig = DatabaseConfig.getInstance();
		int dbmask = (dbconfig.getDatabase(db.substring(0,3).toLowerCase())).getMask();
		return dbmask;
	}


}