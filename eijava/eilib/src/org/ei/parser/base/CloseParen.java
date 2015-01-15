package org.ei.parser.base;




public class CloseParen
	extends BaseNode
{
	public static final String TYPE = "CloseParen";
	
	public CloseParen(String symbol)
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
