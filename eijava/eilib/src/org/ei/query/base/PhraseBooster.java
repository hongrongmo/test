package org.ei.query.base;

import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;


public class PhraseBooster
    extends BaseNodeVisitor implements RelevanceBooster
{

    private FieldGetter fget = new FieldGetter();
    private QueryRules rules;
    private TermGatherer gatherer = new TermGatherer();
    private String currentField;

    public PhraseBooster(QueryRules r)
    {
        rules = r;
    }

    public BooleanQuery applyBoost(BooleanQuery bQuery)
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
        FieldRule rule = rules.getFieldRule(field);
        if(rule.getAutoStem())
        {
            descend(exp);
        }

        field = null;
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

/*
    public void visitWith(Phrase phrase)
    {
        boolean shouldboost = true;
        ParseNode parent = phrase.getParent();
        ArrayList terms = gatherer.gatherTerms(phrase);
        if(terms.size() > 1)
        {
            StringBuffer buf = new StringBuffer();
            for(int i=0;i<terms.size();++i)
            {
                Term term = (Term)terms.get(i);
                ParseNode pnode = term.getChildAt(0);
                if(pnode.getType().equals(Regex.TYPE))
                {
                    //System.out.println("IS REGEX");
                    if(((Regex)pnode).isStem())
                    {
                        //System.out.println("IS Stem");
                        Literal l = (Literal)pnode.getChildAt(1);
                        buf.append(l.getValue().trim()+" ");
                    }
                    else
                    {
                        shouldboost = false;
                        break;
                    }
                }
                else
                {
                    buf.append(term.getValue().trim()+" ");
                }
            }

            if(shouldboost)
            {
                ExactTerm eTerm = new ExactTerm(buf.toString().trim());
                Term t = new Term(eTerm);
                Phrase phrase2 = new Phrase(t);
                BooleanOr b = new BooleanOr("OR");
                OpenParen oParen = new OpenParen("(");
                CloseParen cParen = new CloseParen(")");
                OrPhrase oPhrase = new OrPhrase(new BooleanPhrase(phrase2), b, new BooleanPhrase(oParen,new BooleanPhrase(phrase),cParen));
                BooleanPhrase bPhrase = new BooleanPhrase(oPhrase);
                BooleanPhrase nPhrase = new BooleanPhrase(oParen, bPhrase, cParen);
                parent.setChildAt(phrase.getChildIndex(), nPhrase);
            }
        }


    }
*/
}