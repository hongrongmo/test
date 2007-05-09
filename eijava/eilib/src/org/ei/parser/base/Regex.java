package org.ei.parser.base;

import org.ei.parser.*;

public class Regex extends BaseNode
{
    public static final String TYPE = "Regex";

    public Regex(String exp)
    {
        super(TYPE, exp);
    }

    public BaseNode shallowCopy()
    {
        return new Regex(getNodeValue());
    }

    public void accept(BaseNodeVisitor v)
    {
        v.visitWith(this);
    }
}
