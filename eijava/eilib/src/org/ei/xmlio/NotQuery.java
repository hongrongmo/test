package org.ei.xmlio;

public class NotQuery 
	extends XqueryxNode
{
	public void accept(XqueryxNodeVisitor v)
	{
		v.visitWith(this);
	}



}