package org.ei.parser.base;


public class OrPhrase extends BaseNode
{
	public static final String TYPE = "OrPhrase";
	
	public OrPhrase(BooleanPhrase bp1,
			BooleanOr bo,
			BooleanPhrase bp2)
	{
		super(TYPE);
		addChild(bp1);
		addChild(bo);
		addChild(bp2);
	}
	
	private OrPhrase()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new OrPhrase();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

}
