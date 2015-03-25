package org.ei.parser.base;


public class Expression extends BaseNode
{
	public static final String TYPE = "Expression";
	
	public Expression(BooleanPhrase bp,
	                  KeywordWITHIN wn,
			  Field f)
	{
		super(TYPE);
		addChild(bp);
		addChild(wn);
		addChild(f);
	}
	
	public Expression(OpenParen op,
	                  Expression ex,
			  CloseParen cp)
	{
		super(TYPE);
		addChild(op);
		addChild(ex);
		addChild(cp);
	}
	
	private Expression()
	{
		super(TYPE);
	}
	
	public BaseNode shallowCopy()
	{
		return new Expression();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}



}
