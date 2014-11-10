package org.ei.parser.base;

public class PhraseConnector
    extends BaseNode
{
    public static final String TYPE = "PhraseConnector";

    public PhraseConnector()
    {
        super(TYPE);
    }

    public BaseNode shallowCopy()
    {
        return this;
    }

    public void accept(BaseNodeVisitor v)
    {
        v.visitWith(this);
    }

    public String getNodeValue()
    {
        return " ";
    }

}