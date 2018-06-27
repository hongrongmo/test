package org.ei.parser.base;



public class BooleanOr extends BaseNode
{

	public static final String TYPE = "BooleanOr";
	
	public BooleanOr(String s)
	{
		super(TYPE, s);	
	}
	
	public BaseNode shallowCopy()
	{
		return this;	
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}


}
