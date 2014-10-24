package org.ei.parser.base;



public class Term extends BaseNode
{
    public static final String TYPE = "Term";
    public boolean isExactTerm = false;
    public boolean isRegex = false;
    public boolean isStemmedTerm = false;

    public Term(Literal lit)
    {
        super(TYPE);
        addChild(lit);
    }

    public Term(Regex re)
    {
        super(TYPE);
        addChild(re);
        isRegex = true;
    }

    public Term(ExactTerm t)
    {
        super(TYPE);
        addChild(t);
        isExactTerm = true;
    }


    public Term(StemmedTerm t)
    {
        super(TYPE);
        addChild(t);
        isStemmedTerm = true;
    }

    public String toString()
    {
        return getValue().trim();
    }


    private Term()
    {
        super(TYPE);
    }

    public void setExactTerm(boolean b)
    {
        isExactTerm = b;
    }

    public void setRegex(boolean b)
    {
        isRegex = b;
    }

    public void setStemmedTerm(boolean b)
    {
        isStemmedTerm = b;
    }

    public boolean isExactTerm()
    {
        return isExactTerm;
    }

    public boolean isRegex()
    {
        return isRegex;
    }

    public boolean isStemmedTerm()
    {
        return isStemmedTerm;
    }

    public BaseNode shallowCopy()
    {
        return new Term();
    }

    public void accept(BaseNodeVisitor v)
    {
        v.visitWith(this);
    }



}
