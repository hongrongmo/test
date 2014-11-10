package org.ei.query.base;

import org.ei.parser.base.*;
import org.ei.parser.*;
import java.util.*;

public class PhraseBooster extends BaseNodeVisitor implements RelevanceBooster {

    private FieldGetter fget = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();
    private String currentField;
    private boolean done = false;

    public BooleanQuery applyBoost(BooleanQuery bQuery) {
        bQuery.accept(this);
        return bQuery;
    }

    public void visitWith(BooleanQuery bQuery) {
        descend(bQuery);
    }

    public void visitWith(AndQuery aQuery) {
        descend(aQuery);
    }

    public void visitWith(OrQuery oQuery) {
        descend(oQuery);
    }

    public void visitWith(NotQuery nQuery) {
        descend(nQuery);
    }

    public void visitWith(Expression exp) {
        String field = fget.getFieldValue(exp);
        if (field.equalsIgnoreCase("all") || field.equalsIgnoreCase("ky")) {
            descend(exp);
        }

        field = null;
    }

    public void visitWith(BooleanPhrase bPhrase) {
        descend(bPhrase);
    }

    public void visitWith(AndPhrase aPhrase) {
        descend(aPhrase);
    }

    public void visitWith(OrPhrase oPhrase) {
        descend(oPhrase);
    }

    public void visitWith(Phrase phrase) {
        if (!done) {
            boolean shouldboost = true;
            ParseNode parent = phrase.getParent();
            ArrayList<BaseNode> terms = gatherer.gatherTerms(phrase);
            if (terms.size() > 1) {
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < terms.size(); ++i) {
                    Term term = (Term) terms.get(i);
                    ParseNode pnode = term.getChildAt(0);
                    if (pnode.getType().equals(Regex.TYPE)) {
                        shouldboost = false;
                        break;
                    } else {
                        buf.append(term.getValue().trim() + " ");
                    }
                }

                if (shouldboost) {
                    done = true;
                    ExactTerm eTerm = new ExactTerm(buf.toString().trim());
                    Term t = new Term(eTerm);
                    Phrase phrase2 = new Phrase(t);
                    BooleanOr b = new BooleanOr("OR");
                    OpenParen oParen = new OpenParen("(");
                    CloseParen cParen = new CloseParen(")");
                    OrPhrase oPhrase = new OrPhrase(new BooleanPhrase(phrase2), b, new BooleanPhrase(oParen, new BooleanPhrase(phrase), cParen));
                    BooleanPhrase bPhrase = new BooleanPhrase(oPhrase);
                    BooleanPhrase nPhrase = new BooleanPhrase(oParen, bPhrase, cParen);
                    parent.setChildAt(phrase.getChildIndex(), nPhrase);
                }
            }
        }
    }
}