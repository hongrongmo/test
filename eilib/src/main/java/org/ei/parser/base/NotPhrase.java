package org.ei.parser.base;


public class NotPhrase extends BaseNode
{
	public static final String TYPE = "NotPhrase";
	
	public NotPhrase(BooleanPhrase bp1,
			 BooleanNot bn,
			 BooleanPhrase bp2)
	{
		super(TYPE);
		addChild(bp1);
		addChild(bn);
		addChild(bp2);
	}
	
	private NotPhrase()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new NotPhrase();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}



}
