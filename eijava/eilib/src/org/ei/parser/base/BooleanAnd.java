package org.ei.parser.base;



public class BooleanAnd extends BaseNode
{

	public static final String TYPE = "BooleanAnd";
	
	public BooleanAnd(String s)
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
