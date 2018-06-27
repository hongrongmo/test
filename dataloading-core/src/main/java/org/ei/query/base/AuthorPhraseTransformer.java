package org.ei.query.base;

import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Literal;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.Term;

public class AuthorPhraseTransformer
    extends BaseNodeVisitor
{

    private Literal finalLiteral;
    private Phrase finalPhrase;
    private Perl5Util util = new Perl5Util();

    public Phrase transform(Phrase phrase)
    {
        Phrase finishedPhrase = null;
        AuthorPhraseGatherer ga = new AuthorPhraseGatherer();
        List parts = ga.gather(phrase);
        StringBuffer buf = new StringBuffer();
        boolean finishTransform = true;
        boolean hasRegex = false;
        boolean isExact = false;
        if(parts.size() > 1)
        {
            for(int i=0;i<parts.size();i++)
            {
                if(i>0)
                {
                    buf.append(" ");
                }

                BaseNode b = (BaseNode)parts.get(i);
                if(b.getType().equals("Literal"))
                {
                    buf.append(b.getNodeValue());
                }
                else if(b.getType().equals("Regex"))
                {
                    hasRegex = true;
                    buf.append(b.getNodeValue());
                }
                else if(b.getType().equals("ExactTerm"))
                {
                    isExact = true;
                    finishTransform = false;
                }
                else if(b.getType().equals("ProximityPhrase"))
                {
                    finishTransform = false;
                }
            }
        }
        else
        {
            BaseNode b = (BaseNode)parts.get(0);
            if(b.getType().equals("Literal"))
            {
                buf.append(b.getNodeValue());
            }
            else if(b.getType().equals("Regex"))
            {
                hasRegex = true;
                buf.append(b.getNodeValue());
            }
            else if(b.getType().equals("ExactTerm"))
            {
                isExact = true;
                buf.append(b.getNodeValue());
            }
            else if(b.getType().equals("ProximityPhrase"))
            {
                finishTransform = false;
            }
        }

        if(finishTransform)
        {

            String value = buf.toString();
            if(!hasRegex)
            {
                value = prepareString(value);
                if(isExact)
                {
                    value = joinString(value);
                }

                finishedPhrase = new Phrase(new Term(new ExactTerm(value)));
            }
            else
            {
                value = prepareString(value);
                finishedPhrase = new Phrase(new Term(new Regex(joinString(value))));
            }
        }
        else
        {
            finishedPhrase = phrase;
        }

        return finishedPhrase;
    }


    private String prepareString(String value)
    {
        value = util.substitute("s/-/ /g", value);
        value = util.substitute("s/\\./ /g", value);
        value = util.substitute("s/'//g", value);
        value = util.substitute("s/,/ /g", value);
        value = util.substitute("s/\\s+/ /g", value);
        value = value.trim();
        return value;
    }

    private String joinString(String value)
    {
        return util.substitute("s/ /9/g", value.trim());
    }

}
