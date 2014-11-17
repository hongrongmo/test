package org.ei.parser.base;


public class AndQuery extends BaseNode
{
	
	public static final String TYPE = "AndQuery";
	
	public AndQuery(BooleanQuery bq1, BooleanAnd ba, BooleanQuery bq2)
	{
		super(TYPE);
		addChild(bq1);
		addChild(ba);
		addChild(bq2);
	}
	
	private AndQuery()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new AndQuery();
	}

	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

}
