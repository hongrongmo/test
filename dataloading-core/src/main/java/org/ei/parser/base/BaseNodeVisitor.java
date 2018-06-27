package org.ei.parser.base;


import java.util.Iterator;

public class BaseNodeVisitor
{

    protected void descend(BaseNode node)
    {
        Iterator children = node.iterator();
        while(children.hasNext())
        {
            BaseNode child = (BaseNode)children.next();
            child.accept(this);
        }
    }


    public void visitWith(StemmedTerm sterm)
    {

    }

    public void visitWith(Term term)
    {

    }

    public void visitWith(Regex regex)
    {

    }

    public void visitWith(Literal lit)
    {

    }

    public void visitWith(ExactTerm eTerm)
    {

    }

    public void visitWith(Field field)
    {

    }

    public void visitWith(Expression exp)
    {

    }

    public void visitWith(Phrase phrase)
    {

    }

    public void visitWith(BooleanQuery query)
    {

    }

    public void visitWith(AndQuery aquery)
    {

    }

    public void visitWith(OrQuery oquery)
    {
    }
    public void visitWith(NotQuery nquery)
    {
    }
    public void visitWith(OpenParen oParen)
    {
    }
    public void visitWith(KeywordWITHIN kWIHIN)
    {
    }


    public void visitWith(OrderedNear onear)
    {

    }

    public void visitWith(UnorderedNear near)
    {

    }

    public void visitWith(ProximityOperator po)
    {

    }

    public void visitWith(ProximityPhrase pp)
    {

    }

    public void visitWith(PhraseConnector pc)
    {

    }

    public void visitWith(CloseParen cParen)
    {

    }

    public void visitWith(BooleanPhrase bPhrase)
    {
    }
    public void visitWith(AndPhrase aPhrase)
    {
    }
    public void visitWith(OrPhrase oPhrase)
    {
    }

    public void visitWith(NotPhrase nPhrase)
    {
    }

    public void visitWith(BooleanAnd bAND)
    {
    }


    public void visitWith(BooleanOr bOR)
    {
    }

    public void visitWith(BooleanNot bNOT)
    {
    }
}
