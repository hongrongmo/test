package org.ei.xmlio;


public class Word
    extends XqueryxNode
{
    private String word;
    private String field;
    public void accept(XqueryxNodeVisitor v)
    {
        v.visitWith(this);
    }

    public void setWord(String w)
    {
        if(w != null)
        {
            this.word = w.trim();
        }
    }


    public String getWord()
    {
        return this.word;
    }

    public void setField(String f)
    {
        this.field = f;
    }

    public String getField()
    {
        return this.field;
    }
}