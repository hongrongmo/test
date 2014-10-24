package org.ei.parser.base;

public class ProximityOperator extends BaseNode {

    public static final String TYPE = "ProximityOperator";

    public ProximityOperator(UnorderedNear un) {
        super(TYPE);
        addChild(un);
    }

    public ProximityOperator(OrderedNear on) {
        super(TYPE);
        addChild(on);
    }

    public void accept(BaseNodeVisitor v) {
        v.visitWith(this);
    }

    public BaseNode shallowCopy() {
        return this;
    }
}
