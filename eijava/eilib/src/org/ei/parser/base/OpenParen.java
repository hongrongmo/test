package org.ei.parser.base;



public class OpenParen
	extends BaseNode
{
	public static final String TYPE = "OpenParen";
	
	public OpenParen(String symbol)
	{
		super(TYPE, symbol);
	}

	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

	public BaseNode shallowCopy()
	{
		return this;
	}
}
