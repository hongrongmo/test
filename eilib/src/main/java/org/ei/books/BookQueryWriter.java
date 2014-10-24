package org.ei.books;

import java.io.IOException;
import java.util.ArrayList;

import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BooleanAnd;
import org.ei.parser.base.BooleanNot;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Literal;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.PhraseConnector;
import org.ei.parser.base.Regex;
import org.ei.parser.base.StemmedTerm;
import org.ei.parser.base.Term;
import org.ei.query.base.FieldGetter;
import org.ei.query.base.QueryWriter;
import org.ei.query.base.TermGatherer;

public class BookQueryWriter extends QueryWriter {
    private BufferStream buffer = new BufferStream();
    private BufferStream lemBuffer = new BufferStream();
    private TermGatherer gatherer = new TermGatherer();
    private FieldGetter fieldGetter = new FieldGetter();

    private boolean isStem = false;
    private boolean isWildcard = false;
    private boolean isProximity = false;
    private boolean descend = true;

    public String getQuery(BooleanQuery bQuery) {
        bQuery.accept(this);
        return buffer.toString();
    }

    public String getLemBuffer(BooleanQuery bQuery) {
        bQuery.accept(this);
        return lemBuffer.toString();
    }

    public void visitWith(Expression exp) {
        String currentField = fieldGetter.getFieldValue(exp);
        if ((currentField != null) && (!currentField.equalsIgnoreCase("yr"))
            && (!currentField.equalsIgnoreCase("cl") && (!currentField.equalsIgnoreCase("db")))) {
            descend(exp);
        }
    }

    public void visitWith(Phrase phrase) {
        try {
            ArrayList<BaseNode> terms = gatherer.gatherTerms(phrase);
            if (terms.size() > 1) {
                for (int i = 0; i < terms.size(); ++i) {
                    Term t = (Term) terms.get(i);
                    descend(t);
                    if (i < (terms.size() - 1)) {
                        buffer.write(";");
                        lemBuffer.write(";");
                    }
                }
            } else {
                descend(phrase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanQuery bquery) {
        descend(bquery);
    }

    public void visitWith(Literal literal) {
        try {
            if (isStem && !isProximity) {
                buffer.write(literal.getValue().trim());
                lemBuffer.write(literal.getValue().trim());
            } else {
                buffer.write(literal.getValue().trim() + appendWildcard());
                lemBuffer.write(literal.getValue().trim() + appendWildcard());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String appendWildcard() {
        if (isWildcard) {
            return "*";
        }

        return "";
    }

    public void visitWith(PhraseConnector pc) {
        try {
            buffer.write(";");
            lemBuffer.write(";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(StemmedTerm st) {
        isStem = true;
        descend(st);
        isStem = false;
    }

    public void visitWith(ExactTerm eTerm) {
        try {
            buffer.write(eTerm.getNodeValue());
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            buffer.write(reg.getNodeValue());
            lemBuffer.write(reg.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanAnd bAND) {
        try {
            buffer.write(";");
            lemBuffer.write(";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanOr bOR) {
        try {
            buffer.write(";");
            lemBuffer.write(";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanNot b) {
        descend = false;
    }

    public void visitWith(BooleanPhrase b) {
        if (descend) {
            descend(b);
        }
    }

}