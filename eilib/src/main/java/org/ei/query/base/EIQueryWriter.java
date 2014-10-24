package org.ei.query.base;

import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BooleanAnd;
import org.ei.parser.base.BooleanNot;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.CloseParen;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;
import org.ei.parser.base.KeywordWITHIN;
import org.ei.parser.base.Literal;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OpenParen;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.OrderedNear;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.ProximityOperator;
import org.ei.parser.base.ProximityPhrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.StemmedTerm;
import org.ei.parser.base.Term;
import org.ei.parser.base.UnorderedNear;

public class EIQueryWriter extends QueryWriter {

    private BufferStream buffer = new BufferStream();

    public String getQuery(BooleanQuery bQuery) {
        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp) {
        descend(exp);
    }

    public void visitWith(Field field) {
        try {
            buffer.write(" " + field.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(Phrase phrase) {
        try {
            buffer.write(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        descend(phrase);
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
        try {
            buffer.write(" WN ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(ProximityPhrase pp) {
        descend(pp);
    }

    public void visitWith(ProximityOperator po) {
        descend(po);
    }

    public void visitWith(OrderedNear o) {
        try {
            buffer.write(" ONEAR");
            if (o.getDistance() != 4) {
                buffer.write("/");
                buffer.write(Integer.toString(o.getDistance()));
            }
            buffer.write(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(UnorderedNear o) {
        try {
            buffer.write(" NEAR");
            if (o.getDistance() != 4) {
                buffer.write("/");
                buffer.write(Integer.toString(o.getDistance()));
            }
            buffer.write(" ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(StemmedTerm sTerm) {
        try {
            buffer.write("$");
            descend(sTerm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitWith(ExactTerm eTerm) {
        try {
            buffer.write("{" + eTerm.getNodeValue() + "}");
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
            buffer.write(reg.getNodeValue());
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
