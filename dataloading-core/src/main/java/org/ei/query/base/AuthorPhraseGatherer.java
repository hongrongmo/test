package org.ei.query.base;

import java.util.ArrayList;
import java.util.List;

import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Literal;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.ProximityPhrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.Term;

public class AuthorPhraseGatherer extends BaseNodeVisitor {

    private List<BaseNode> parts;

    public List<BaseNode> gather(Phrase phrase) {
        parts = new ArrayList<BaseNode>();
        phrase.accept(this);
        return parts;
    }

    public void visitWith(Phrase phrase) {
        descend(phrase);
    }

    public void visitWith(Term term) {
        descend(term);
    }

    public void visitWith(Literal lit) {
        parts.add(lit);
    }

    public void visitWith(ExactTerm eTerm) {
        parts.add(eTerm);
    }

    public void visitWith(ProximityPhrase pp) {
        parts.add(pp);
    }

    public void visitWith(Regex regex) {
        parts.add(regex);
    }

}
