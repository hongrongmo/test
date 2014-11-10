package org.ei.parser.base;

public class Phrase extends BaseNode {
    public static final String TYPE = "Phrase";
    public static final PhraseConnector pc = new PhraseConnector();

    public Phrase(Term t) {
        super(TYPE);
        addChild(t);
    }

    public Phrase(Phrase p1, Phrase p2) {
        super(TYPE);
        addChild(p1);
        addChild(pc);
        addChild(p2);
    }

    public Phrase(ProximityPhrase pp) {
        super(TYPE);
        addChild(pp);
    }

    private Phrase() {
        super(TYPE);
    }

    public BaseNode shallowCopy() {
        return new Phrase();
    }

    public void accept(BaseNodeVisitor v) {
        v.visitWith(this);
    }

}
