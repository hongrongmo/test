package org.ei.xmlio;



public interface XqueryxNodeVisitor
{
    public void visitWith(AndQuery a);
    public void visitWith(OrQuery o);
    public void visitWith(NotQuery n);
    public void visitWith(Query q);
    public void visitWith(Word w);
}









