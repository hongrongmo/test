package org.ei.parser.base;


public class OrQuery extends BaseNode
{
	public static final String TYPE = "OrQuery";
	
	public OrQuery(BooleanQuery bq1,
	               BooleanOr  bo,
		       BooleanQuery bq2)
	{
		super(TYPE);
		addChild(bq1);
		addChild(bo);
		addChild(bq2);
	}
	
	private OrQuery()
	{
		super(TYPE);
	}
	
	public BaseNode shallowCopy()
	{
		return new OrQuery();
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}


}
