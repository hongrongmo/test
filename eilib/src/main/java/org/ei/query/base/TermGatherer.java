package org.ei.query.base;

import java.util.ArrayList;

import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.ProximityOperator;
import org.ei.parser.base.Term;

public class TermGatherer extends BaseNodeVisitor {
    private ArrayList<BaseNode> terms;

    public ArrayList<BaseNode> gatherTerms(Phrase phrase) {
        terms = new ArrayList<BaseNode>();
        phrase.accept(this);
        return terms;
    }

    public void visitWith(Phrase phrase) {
        descend(phrase);
    }

    public void visitWith(Term term) {
        terms.add(term);
    }

    public void visitWith(ProximityOperator po) {
        terms.add(po);
    }

}
