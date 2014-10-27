package org.ei.xmlio;


import java.util.ArrayList;
import java.util.List;

public abstract class XqueryxNode
{
    private List children = new ArrayList();


    public abstract void accept(XqueryxNodeVisitor v);

    public void addChild(XqueryxNode xn)
    {
        children.add(xn);
    }

    public int countChildren()
    {
        return children.size();
    }

    public XqueryxNode childAt(int index)
    {
        return (XqueryxNode)children.get(index);
    }
}