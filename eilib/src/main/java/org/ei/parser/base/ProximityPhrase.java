package org.ei.parser.base;




public class ProximityPhrase extends BaseNode
{
    public static final String TYPE = "ProximityPhrase";


    public ProximityPhrase()
    {
        super(TYPE);
    }

    public ProximityPhrase(Term p1,
                           ProximityOperator po,
                           Term p2)
    {
        super(TYPE);
        addChild(p1);
        addChild(po);
        addChild(p2);
    }

    public ProximityPhrase(Term p1,
                  ProximityOperator po,
                  ProximityPhrase p2)
    {
        super(TYPE);
        addChild(p1);
        addChild(po);
        addChild(p2);
    }


    public BaseNode shallowCopy()
    {
        return new ProximityPhrase();
    }

    public void accept(BaseNodeVisitor v)
    {
        v.visitWith(this);
    }
}
