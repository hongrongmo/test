<<<<<<< HEAD
package org.ei.thesaurus;

import java.util.*;
import java.io.*;
import org.ei.parser.*;
import org.ei.parser.base.*;
import org.ei.query.base.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;


public class ThesaurusQueryWriter
    extends QueryWriter
{
    private BufferStream buffer = new BufferStream();
    private FieldGetter fieldGetter = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();
    private PorterStemmer stemmer = new PorterStemmer();
    private Perl5Util perl = new Perl5Util();
    private String currentField;
    private boolean isStem = false;
    private boolean isWildcard = false;

    private static Properties mappings;

    {

        mappings = new Properties();
        mappings.setProperty("db", "db");
        mappings.setProperty("all", "thes");
    }




    public String getQuery(BooleanQuery bQuery)
    {
        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp)
    {
        ParseNode n = exp.getParent();
        currentField = fieldGetter.getFieldValue(exp);
        descend(exp);
    }

    public void visitWith(Phrase phrase)
    {
        try
        {

            ParseNode pNode = phrase.getParent();


            ArrayList terms = gatherer.gatherTerms(phrase);
            if(terms.size() > 1)
            {
                buffer.write(" (");
                for(int i =0; i<terms.size(); ++i)
                {
                    Term t = (Term)terms.get(i);
                    descend(t);
                    if(i < (terms.size()-1))
                    {
                        buffer.write(" AND");
                    }
                }
                buffer.write(")");
            }
            else
            {
                buffer.write(" ");
                descend(phrase);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanQuery bquery)
    {
        descend(bquery);
    }


    public void visitWith(OpenParen oParen)
    {
        try
        {

            buffer.write(" (");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void visitWith(Literal literal)
    {
        if(currentField == null)
        {
            currentField = "all";
        }

        try
        {
            if(isStem)
            {



                buffer.write(" (");
                buffer.write(mappings.getProperty(currentField.toLowerCase())+":QZ"+stemmer.stem(literal.getValue().trim())+"QZ OR "+mappings.getProperty(currentField.toLowerCase())+":"+literal.getValue().trim());
                buffer.write(")");

            }
            else
            {

                String t = null;
                t = literal.getValue().trim()+appendWildcard();
                buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String appendWildcard()
    {

        if(isWildcard)
        {
            return "*";
        }

        return "";
    }

    private String formatRange(String range)
    {

        if(range.indexOf("-") > 0)
        {
            range = range.replace('-', ';');
            range = "["+range+"]";
        }


        return range;
    }

    public void visitWith(KeywordWITHIN kWIHIN)
    {
        currentField = null;
    }



    public void visitWith(ExactTerm eTerm)
    {
        if(currentField == null)
        {
            currentField = "all";
        }

        try
        {
            String t = null;

            t = "\""+eTerm.getNodeValue()+"\"";

            buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(AndPhrase aPhrase)
    {

        descend(aPhrase);

    }

    public void visitWith(OrPhrase oPhrase)
    {

        descend(oPhrase);

    }

    public void visitWith(NotPhrase nPhrase)
    {
        descend(nPhrase);
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

    public void visitWith(Term term)
    {
        descend(term);
    }

/*
    public void visitWith(Regex reg)
    {
        ParseNode pNode = reg.getChildAt(0);
        if(pNode.getType().equals(StemSymbol.TYPE))
        {
            this.isStem = true;
            this.isWildcard = false;
        }
        else
        {
            this.isStem = false;
            this.isWildcard = true;
        }

        descend(reg);
        this.isWildcard = false;
        this.isStem = false;
    }

*/

    public void visitWith(CloseParen cParen)
    {
        try
        {
            buffer.write(")");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanPhrase bPhrase)
    {
        descend(bPhrase);
    }

    public void visitWith(BooleanAnd bAND)
    {
        try
        {
            buffer.write(" AND");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanOr bOR)
    {
        try
        {
            buffer.write(" OR");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanNot bNOT)
    {
        try
        {
            buffer.write(" ANDNOT");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    class BufferStream extends FilterOutputStream
    {

        private int last = -1;

        public BufferStream()
        {
            super(new ByteArrayOutputStream());
        }

        public void close()
            throws IOException
        {
            out.close();
        }

        public void flush()
            throws IOException
        {
            out.flush();
        }

        public String toString()
        {
            return out.toString();
        }

        public void write(byte b)
            throws IOException
        {

            int current = (int)b;
            if(current == 32)
            {
                if(last != 32 && last != 40)
                {
                    out.write(b);
                }

            }
            else
            {
                out.write(b);

            }

            last = current;
        }

        public void write(byte[] b)
            throws IOException
        {
            for(int x=0; x<b.length; ++x)
            {

                write(b[x]);
            }
        }


        public void write(byte[] b, int off, int len)
            throws IOException
        {
            for(int x=0; x<len; ++x)
            {
                write(b[off+x]);
            }

        }

        public void write(String s)
            throws IOException
        {
            for(int x=0; x<s.length(); ++x)
            {
                write((byte)s.charAt(x));
            }
        }


    }

=======
package org.ei.thesaurus;

import java.util.*;
import java.io.*;
import org.ei.parser.*;
import org.ei.parser.base.*;
import org.ei.query.base.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;


public class ThesaurusQueryWriter
    extends QueryWriter
{
    private BufferStream buffer = new BufferStream();
    private FieldGetter fieldGetter = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();
    private PorterStemmer stemmer = new PorterStemmer();
    private Perl5Util perl = new Perl5Util();
    private String currentField;
    private boolean isStem = false;
    private boolean isWildcard = false;

    private static Properties mappings;

    {

        mappings = new Properties();
        mappings.setProperty("db", "db");
        mappings.setProperty("all", "all");
    }




    public String getQuery(BooleanQuery bQuery)
    {
        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp)
    {
        ParseNode n = exp.getParent();
        currentField = fieldGetter.getFieldValue(exp);
        descend(exp);
    }

    public void visitWith(Phrase phrase)
    {
        try
        {

            ParseNode pNode = phrase.getParent();


            ArrayList terms = gatherer.gatherTerms(phrase);
            if(terms.size() > 1)
            {
                buffer.write(" (");
                for(int i =0; i<terms.size(); ++i)
                {
                    Term t = (Term)terms.get(i);
                    descend(t);
                    if(i < (terms.size()-1))
                    {
                        buffer.write(" AND");
                    }
                }
                buffer.write(")");
            }
            else
            {
                buffer.write(" ");
                descend(phrase);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanQuery bquery)
    {
        descend(bquery);
    }


    public void visitWith(OpenParen oParen)
    {
        try
        {

            buffer.write(" (");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void visitWith(Literal literal)
    {
        if(currentField == null)
        {
            currentField = "all";
        }

        try
        {
            if(isStem)
            {



                buffer.write(" (");
                buffer.write(mappings.getProperty(currentField.toLowerCase())+":QZ"+stemmer.stem(literal.getValue().trim())+"QZ OR "+mappings.getProperty(currentField.toLowerCase())+":"+literal.getValue().trim());
                buffer.write(")");

            }
            else
            {

                String t = null;
                t = literal.getValue().trim()+appendWildcard();
                buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String appendWildcard()
    {

        if(isWildcard)
        {
            return "*";
        }

        return "";
    }

    private String formatRange(String range)
    {

        if(range.indexOf("-") > 0)
        {
            range = range.replace('-', ';');
            range = "["+range+"]";
        }


        return range;
    }

    public void visitWith(KeywordWITHIN kWIHIN)
    {
        currentField = null;
    }



    public void visitWith(ExactTerm eTerm)
    {
        if(currentField == null)
        {
            currentField = "all";
        }

        try
        {
            String t = null;

            t = "\""+eTerm.getNodeValue()+"\"";

            buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(AndPhrase aPhrase)
    {

        descend(aPhrase);

    }

    public void visitWith(OrPhrase oPhrase)
    {

        descend(oPhrase);

    }

    public void visitWith(NotPhrase nPhrase)
    {
        descend(nPhrase);
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

    public void visitWith(Term term)
    {
        descend(term);
    }

/*
    public void visitWith(Regex reg)
    {
        ParseNode pNode = reg.getChildAt(0);
        if(pNode.getType().equals(StemSymbol.TYPE))
        {
            this.isStem = true;
            this.isWildcard = false;
        }
        else
        {
            this.isStem = false;
            this.isWildcard = true;
        }

        descend(reg);
        this.isWildcard = false;
        this.isStem = false;
    }

*/

    public void visitWith(CloseParen cParen)
    {
        try
        {
            buffer.write(")");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanPhrase bPhrase)
    {
        descend(bPhrase);
    }

    public void visitWith(BooleanAnd bAND)
    {
        try
        {
            buffer.write(" AND");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanOr bOR)
    {
        try
        {
            buffer.write(" OR");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(BooleanNot bNOT)
    {
        try
        {
            buffer.write(" ANDNOT");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    class BufferStream extends FilterOutputStream
    {

        private int last = -1;

        public BufferStream()
        {
            super(new ByteArrayOutputStream());
        }

        public void close()
            throws IOException
        {
            out.close();
        }

        public void flush()
            throws IOException
        {
            out.flush();
        }

        public String toString()
        {
            return out.toString();
        }

        public void write(byte b)
            throws IOException
        {

            int current = (int)b;
            if(current == 32)
            {
                if(last != 32 && last != 40)
                {
                    out.write(b);
                }

            }
            else
            {
                out.write(b);

            }

            last = current;
        }

        public void write(byte[] b)
            throws IOException
        {
            for(int x=0; x<b.length; ++x)
            {

                write(b[x]);
            }
        }


        public void write(byte[] b, int off, int len)
            throws IOException
        {
            for(int x=0; x<len; ++x)
            {
                write(b[off+x]);
            }

        }

        public void write(String s)
            throws IOException
        {
            for(int x=0; x<s.length(); ++x)
            {
                write((byte)s.charAt(x));
            }
        }


    }

>>>>>>> 15dca715b02cb03c5fa8fd5c16635f7e802f1eae
}