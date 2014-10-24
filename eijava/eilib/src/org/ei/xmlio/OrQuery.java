package org.ei.xmlio;

public class OrQuery 
	extends XqueryxNode
{
	public void accept(XqueryxNodeVisitor v)
	{
		v.visitWith(this);
	}



}