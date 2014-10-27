package org.ei.query.base;

import java.util.ArrayList;

import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.ProximityOperator;
import org.ei.parser.base.ProximityPhrase;
import org.ei.parser.base.Term;


public class ProximityParts
    extends BaseNodeVisitor
{
    private ArrayList terms;
    private boolean allStems = true;

    public ArrayList gatherParts(ProximityPhrase phrase)
    {
        terms = new ArrayList();
        phrase.accept(this);
        return terms;
    }


    public void visitWith(Phrase phrase)
    {
        descend(phrase);
    }

    public void visitWith(Term term)
    {
        terms.add(term);
        if(!term.isStemmedTerm())
        {
            allStems = false;
        }
    }

    public boolean stems()
    {
        return allStems;
    }

    public void visitWith(ProximityOperator po)
    {
        terms.add(po);
    }

    public void visitWith(ProximityPhrase pp)
    {
        descend(pp);
    }

}
