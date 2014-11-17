package org.ei.query.base;

import org.ei.parser.base.*;
import org.ei.parser.*;



public class AutoStemmer
    extends BaseNodeVisitor
{
    private String[] stemmedFields;
    private FieldGetter fget = new FieldGetter();


    public AutoStemmer(String stemmedFields[])
    {
        this.stemmedFields = stemmedFields;
    }

    public BooleanQuery autoStem(BooleanQuery bQuery)
    {
        bQuery.accept(this);
        return bQuery;
    }


  private boolean containsField(String strValue) {
    boolean found = false;
    for(int i = 0; i < stemmedFields.length; i++) {
      if(strValue.equals(stemmedFields[i])) {
        found = true;
      }
    }
    return found;
  }

    public void visitWith(Expression exp)
    {
        String fieldValue = fget.getFieldValue(exp);
        if(containsField(fieldValue))
        {
            descend(exp);
        }
    }

    public void visitWith(Phrase phrase)
    {
        descend(phrase);
    }
    public void visitWith(BooleanQuery query)
    {
        descend(query);
    }

    public void visitWith(AndQuery aQuery)
    {
        descend(aQuery);
    }

    public void visitWith(NotQuery nQuery)
    {
        descend(nQuery);
    }

    public void visitWith(OrQuery oQuery)
    {
        descend(oQuery);
    }

    public void visitWith(ProximityPhrase pp)
    {
        descend(pp);
    }

    public void visitWith(Literal literal)
    {
        ParseNode parent = literal.getParent();
        int cIndex = literal.getChildIndex();

        parent.setChildAt(cIndex, new StemmedTerm(literal));
        ((Term)parent).setStemmedTerm(true);
    }


    public void visitWith(BooleanPhrase bPhrase)
    {
        descend(bPhrase);
    }

    public void visitWith(OrPhrase oPhrase)
    {
        descend(oPhrase);
    }

    public void visitWith(AndPhrase aPhrase)
    {
        descend(aPhrase);
    }

    public void visitWith(NotPhrase nPhrase)
    {
        descend(nPhrase);
    }

    public void visitWith(Term term)
    {
        descend(term);
    }
}
