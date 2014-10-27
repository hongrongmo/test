package org.ei.query.base;

import org.ei.parser.base.*;
import org.ei.parser.*;



public class AuthorExpressionTransformer
    extends BaseNodeVisitor
{
    boolean isExpression = false;

    AuthorPhraseTransformer authorPhraseTransformer = new AuthorPhraseTransformer();
    private FieldGetter fget = new FieldGetter();

    public BooleanQuery transform(BooleanQuery bQuery)
    {
        bQuery.accept(this);
        return bQuery;
    }

    public void visitWith(BooleanQuery bQuery)
    {
        descend(bQuery);
    }

    public void visitWith(AndQuery aQuery)
    {
        descend(aQuery);
    }

    public void visitWith(OrQuery oQuery)
    {
        descend(oQuery);
    }

    public void visitWith(NotQuery nQuery)
    {
        descend(nQuery);
    }

    public void visitWith(Expression exp)
    {
        String field = fget.getFieldValue(exp);
        //System.out.println("Visiting with expression:"+ field);
        if(field.equalsIgnoreCase(QueryRules.AUTHOR_RULE))
        {
            //System.out.println("Is author expression");
            isExpression = true;
            descend(exp);
            isExpression = false;
        }
    }

    public void visitWith(BooleanPhrase bPhrase)
    {
        descend(bPhrase);
    }

    public void visitWith(AndPhrase aPhrase)
    {
        descend(aPhrase);
    }

    public void visitWith(OrPhrase oPhrase)
    {
        descend(oPhrase);
    }


    public void visitWith(Phrase phrase)
    {
        if(isExpression)
        {
            //System.out.println("Visiting with phrase:"+ phrase.getValue());
            Phrase transformedPhrase = authorPhraseTransformer.transform(phrase);
            ParseNode parent = phrase.getParent();
            parent.setChildAt(phrase.getChildIndex(), transformedPhrase);
        }
    }

}
