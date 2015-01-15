package org.ei.parser.base;




public class AndPhrase extends BaseNode
{
	public static final String TYPE = "AndPhrase";
	
	public AndPhrase(BooleanPhrase bp1,
	                 BooleanAnd ba,
			 BooleanPhrase bp2)
	{
		super(TYPE);
		addChild(bp1);
		addChild(ba);
		addChild(bp2);
	}
	
	private AndPhrase()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new AndPhrase();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}


}
