package org.ei.parser.base;



public class ExactTerm extends BaseNode
{
	public static final String TYPE = "ExactTerm";
	
	public ExactTerm(String t)
	{
		super(TYPE, t);	
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
