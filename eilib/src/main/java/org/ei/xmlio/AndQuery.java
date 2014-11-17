package org.ei.xmlio;

public class AndQuery 
	extends XqueryxNode
{
	public void accept(XqueryxNodeVisitor v)
	{
		v.visitWith(this);
	}



}