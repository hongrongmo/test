package org.ei.parser.base;

public class UnorderedNear extends BaseNode {
    public static final String TYPE = "UnorderedNear";
    private int distance;

    public UnorderedNear(String s, int distance) {
        super(TYPE, s);
        this.distance = distance;
    }

    public BaseNode shallowCopy() {
        return new UnorderedNear(getNodeValue(), this.distance);
    }

    public int getDistance() {
        return this.distance;
    }

    public void accept(BaseNodeVisitor v) {
        v.visitWith(this);
    }
}