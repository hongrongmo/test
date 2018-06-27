package org.ei.parser.base;




public class BooleanPhrase extends BaseNode
{

	public static final String TYPE = "BooleanPhrase";

	public BooleanPhrase(Phrase ph)
	{
		super(TYPE);
		addChild(ph);
	}
	
	public BooleanPhrase(AndPhrase ap)
	{
		super(TYPE);
		addChild(ap);
	}
	
	public BooleanPhrase(OrPhrase op)
	{
		super(TYPE);
		addChild(op);
	}
	
	public BooleanPhrase(NotPhrase np)
	{
		super(TYPE);
		addChild(np);
	}
	
	public BooleanPhrase(OpenParen op,
	                     BooleanPhrase bp,
			     CloseParen cp)
	{
		super(TYPE);
		addChild(op);
		addChild(bp);
		addChild(cp);
	}


	private BooleanPhrase()
	{
		super(TYPE);
	}
	
	public BaseNode shallowCopy()
	{
		return new BooleanPhrase();
	}

	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

			   


}
