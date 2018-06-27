package org.ei.xmlio;

public class XqueryxRewriter
    implements XqueryxNodeVisitor
{
    StringBuffer buf = new StringBuffer();

    public String getQuery()
    {
        return buf.toString();
    }

    public void visitWith(Query q)
    {
        buf.append("(");
        for(int i=0;i<q.countChildren();i++)
        {
            XqueryxNode xn = q.childAt(i);
            xn.accept(this);

        }
        buf.append(")");
    }

    public void visitWith(AndQuery a)
    {
        buf.append("(");
        for(int i=0;i<a.countChildren();i++)
        {
            XqueryxNode xn = a.childAt(i);
            xn.accept(this);
            if((a.countChildren()-1) > i)
            {
                buf.append(" AND ");
            }
        }
        buf.append(")");
    }

    public void visitWith(OrQuery o)
    {
        buf.append("(");
        for(int i=0;i<o.countChildren();i++)
        {
            XqueryxNode xn =o.childAt(i);
            xn.accept(this);
            if((o.countChildren()-1) > i)
            {
                buf.append(" OR ");
            }
        }
        buf.append(")");
    }

    public void visitWith(NotQuery n)
    {
        buf.append("(");
        for(int i=0;i<n.countChildren();i++)
        {
            XqueryxNode xn = n.childAt(i);
            xn.accept(this);
            if((n.countChildren()-1) > i)
            {
                buf.append(" NOT ");
            }
        }
        buf.append(")");
    }

    public void visitWith(Word w)
    {
        boolean quotes = false;
        if(w.getWord().indexOf(" ") > -1)
        {
            quotes = true;
        }

        buf.append("(");
        if(quotes)
        {
            buf.append("\"");
        }
        buf.append(w.getWord());
        if(quotes)
        {
            buf.append("\"");
        }
        if(w.getField() != null)
        {
            buf.append(" WN ");
            buf.append(w.getField());
        }

        buf.append(")");
    }
}