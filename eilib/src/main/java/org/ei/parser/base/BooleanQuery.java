package org.ei.parser.base;


public class BooleanQuery 
	extends BaseNode
{
	public static final String TYPE = "BooleanQuery";
	
	public BooleanQuery(BooleanPhrase bphrase)
	{
		super(TYPE);
		addChild(bphrase);
	}
	
	public BooleanQuery(Expression ex)
	{
		super(TYPE);
		addChild(ex);	
	}
	
	public BooleanQuery(AndQuery aq)
	{
		super(TYPE);
		addChild(aq);	
	}
	
	public BooleanQuery(OrQuery oq)
	{
		super(TYPE);
		addChild(oq);	
	}
	
	public BooleanQuery(NotQuery nq)
	{
		super(TYPE);
		addChild(nq);	
	}
	
	public BooleanQuery(OpenParen op, BooleanQuery bq, CloseParen cp)
	{
		super(TYPE);
		addChild(op);
		addChild(bq);
		addChild(cp);
	}
	
	/*
	*	This constructor is needed for the shallow copy method
	*/
	
	private BooleanQuery()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new BooleanQuery();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

}
