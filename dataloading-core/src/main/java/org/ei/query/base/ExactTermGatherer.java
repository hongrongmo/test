package org.ei.query.base;

import java.util.ArrayList;
import java.util.List;

import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Term;

public class ExactTermGatherer extends BaseNodeVisitor {

    private List<String> termList;
    private String firstCon = "or";
    boolean done = false;

    public static void main(String args[]) throws Exception {
        String query = "((({acid rain}) WN CV) AND (microwave oven WN CV)) OR {smoke} WN JA";
        BaseParser parser = new BaseParser();
        BooleanQuery bq = (BooleanQuery) parser.parse(query);
        ExactTermGatherer e = new ExactTermGatherer();
        List<String> l = e.gatherExactTerms(bq);
        for (int i = 0; i < l.size(); i++) {
            String s = (String) l.get(i);
            System.out.println(s);
        }
    }

    public List<String> gatherExactTerms(BooleanQuery bQuery) {
        done = false;
        termList = new ArrayList<String>();
        bQuery.accept(this);
        return termList;
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
        descend(exp);
    }

    public void visitWith(BooleanPhrase bPhrase) {
        descend(bPhrase);
    }

    public void visitWith(AndPhrase aPhrase) {
        if (!done) {
            done = true;
            firstCon = "and";
        }
        descend(aPhrase);
    }

    public void visitWith(OrPhrase oPhrase) {
        if (!done) {
            done = true;
            firstCon = "or";
        }
        descend(oPhrase);
    }

    public void visitWith(Phrase phrase) {
        descend(phrase);
    }

    public void visitWith(Term term) {
        descend(term);
    }

    public void visitWith(ExactTerm eTerm) {
        termList.add(eTerm.getNodeValue());
    }

    public String getFirstConnector() {
        return this.firstCon;
    }

}