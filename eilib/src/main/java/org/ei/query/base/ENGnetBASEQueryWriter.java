package org.ei.query.base;

import java.util.ArrayList;

import org.ei.parser.ParseNode;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BooleanAnd;
import org.ei.parser.base.BooleanNot;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.CloseParen;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.KeywordWITHIN;
import org.ei.parser.base.Literal;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OpenParen;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.Term;

public class ENGnetBASEQueryWriter extends QueryWriter {
    private BufferStream buffer = new BufferStream();
    private FieldGetter fieldGetter = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();

    private String currentField;
    private boolean isStem = false;

    public String getQuery(BooleanQuery bQuery) {
        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp) {

        currentField = fieldGetter.getFieldValue(exp);
        descend(exp);

    }

    // jam 11-25-2002 - bug 12.19 and 12.20
    // added phrase parsing for when more that one term is entered
    // in search word text boxes.
    public void visitWith(Phrase phrase) {

        try {

            ParseNode pNode = phrase.getParent();

            ArrayList<BaseNode> terms = gatherer.gatherTerms(phrase);
            if (terms.size() > 1) {
                buffer.write(" (");
                for (int i = 0; i < terms.size(); ++i) {
                    Term t = (Term) terms.get(i);
                    descend(t);
                    if (i < (terms.size() - 1)) {
                        buffer.write(" and ");
                    }
                }
                buffer.write(")");
            } else {
                buffer.write(" ");
                descend(phrase);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void visitWith(BooleanQuery bquery) {
        descend(bquery);
    }

    public void visitWith(OpenParen oParen) {
        try {

            buffer.write(" (");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(Literal literal) {
        try {
            buffer.write(literal.getValue().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(KeywordWITHIN kWIHIN) {
        currentField = null;
    }

    public void visitWith(ExactTerm eTerm) {
        try {
            buffer.write("\"" + eTerm.getValue().trim() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(CloseParen cParen) {
        try {
            buffer.write(")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(AndPhrase aPhrase) {
        descend(aPhrase);
    }

    public void visitWith(OrPhrase oPhrase) {
        descend(oPhrase);
    }

    public void visitWith(NotPhrase nPhrase) {
        descend(nPhrase);
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

    public void visitWith(Term term) {
        descend(term);
    }

    public void visitWith(Regex reg) {

        try {
            String r = reg.getNodeValue();
            r = r.replace('*', '$');
            buffer.write(r);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void visitWith(BooleanPhrase bPhrase) {
        descend(bPhrase);
    }

    public void visitWith(BooleanAnd bAND) {
        try {
            buffer.write(" AND");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanOr bOR) {
        try {
            buffer.write(" OR");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanNot bNOT) {
        try {
            buffer.write(" NOT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}