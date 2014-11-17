package org.ei.parser.base;



public class BooleanNot extends BaseNode
{

	public static final String TYPE = "BooleanNot";
	
	public BooleanNot(String s)
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
