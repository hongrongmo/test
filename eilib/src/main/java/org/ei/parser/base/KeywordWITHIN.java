package org.ei.parser.base;


public class KeywordWITHIN
	extends BaseNode
{

	public static final String TYPE = "KeywordWITHIN";

	public KeywordWITHIN(String keyword)
	{
		super(TYPE, keyword);
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
