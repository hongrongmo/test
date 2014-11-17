package org.ei.parser.base;


public class Literal extends BaseNode
{
	public static final String TYPE = "Literal";
	
	public Literal(String s)
	{
		super(TYPE, s);	
	}
	
	public BaseNode shallowCopy()
	{
		return new Literal(getNodeValue());
	}
	
	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

}
