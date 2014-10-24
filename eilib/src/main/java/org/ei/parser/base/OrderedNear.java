package org.ei.parser.base;



public class OrderedNear extends BaseNode
{
    public static final String TYPE = "OrderedNear";
    private int distance;

    public OrderedNear(String s, int distance)
    {
        super(TYPE, s);
        this.distance = distance;
    }

    public BaseNode shallowCopy()
    {
        return new OrderedNear(getNodeValue(),this.distance);
    }

    public int getDistance()
    {
        return this.distance;
    }

    public void accept(BaseNodeVisitor v)
    {
        v.visitWith(this);
    }
}
