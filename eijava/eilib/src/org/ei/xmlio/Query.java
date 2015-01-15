package org.ei.xmlio;

public class Query
    extends XqueryxNode
{
    public void accept(XqueryxNodeVisitor v)
    {
        v.visitWith(this);
    }



}