package org.ei.parser.base;

public class StemmedTerm extends BaseNode {
    public static final String TYPE = "StemmedTerm";

    public StemmedTerm(Literal l) {
        super(TYPE);
        addChild(l);

    }

    public StemmedTerm() {
        super(TYPE);
    }

    public BaseNode shallowCopy() {
        return new StemmedTerm();
    }

    public void accept(BaseNodeVisitor v) {
        v.visitWith(this);
    }
}
