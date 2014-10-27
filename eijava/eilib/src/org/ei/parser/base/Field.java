package org.ei.parser.base;

public class Field extends BaseNode
{
	public static final String TYPE = "FIELD";
	
	public Field(String s)
	{
		super(TYPE, s);
	}
	
	public BaseNode shallowCopy()
	{
		return new Field(getNodeValue());
	}

	public void accept(BaseNodeVisitor v)
	{
		v.visitWith(this);
	}

	public String getNodeValue()
	{
		return this.value.toUpperCase();
	}

}
