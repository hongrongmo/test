package org.ei.query.base;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Term;

public class RefinementFieldGatherer extends BaseNodeVisitor {

    private List<String> termList;
    private Map<Field, List<String>> fieldMap;

    public Map<Field, List<String>> gatherExactFields(BooleanQuery bQuery) {
        fieldMap = new Hashtable<Field, List<String>>();
        bQuery.accept(this);
        return fieldMap;
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
        // reset term list for new expression
        termList = new ArrayList<String>();
        descend(exp);
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
        descend(phrase);
    }

    public void visitWith(Term term) {
        if ((termList != null) && (term != null)) {
            termList.add(term.getValue().trim());
        }

        descend(term);
    }

    public void visitWith(Field field) {
        // when we've hit the field we are at the end of the expression
        if (!termList.isEmpty()) {
            List<String> alist;
            if (fieldMap.containsKey(field)) {
                // add terms to list already in map under this field
                alist = (List<String>) fieldMap.get(field);
            } else {
                // create a new list for this field
                alist = new ArrayList<String>();
            }
            if (alist != null) {
                // add all of the collected terms to the list stored under this field
                // in the map/hashtable
                alist.addAll(termList);
                fieldMap.put(field, alist);
            }
        }

        // not reall needed - we will reset the termlist upon visitWith(Expression e)
        termList = new ArrayList<String>();
    }

    public void visitWith(ExactTerm eTerm) {

    }

}