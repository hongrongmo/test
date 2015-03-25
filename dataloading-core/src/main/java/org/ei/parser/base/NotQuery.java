package org.ei.parser.base;


public class NotQuery extends BaseNode
{
	public static final String TYPE = "NotQuery";
	
	public NotQuery(BooleanQuery bq1,
	                BooleanNot bn,
			BooleanQuery bq2)
	{
		super(TYPE);
		addChild(bq1);
		addChild(bn);
		addChild(bq2);
	}
	
	
	private NotQuery()
	{
		super(TYPE);	
	}
	
	public BaseNode shallowCopy()
	{
		return new NotQuery();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

}
