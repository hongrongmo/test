package org.ei.query.base;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.ei.parser.ParseNode;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BooleanAnd;
import org.ei.parser.base.BooleanNot;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.CloseParen;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.KeywordWITHIN;
import org.ei.parser.base.Literal;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OpenParen;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.Term;


public class USPTOQueryWriter
    extends QueryWriter
{
  private static Map usptoFields = new Hashtable();
  static {
        usptoFields.put("AB","ABST");
        usptoFields.put("ACLM","ACLM");
        usptoFields.put("AP","APD");
        usptoFields.put("APN","APN");
        usptoFields.put("APT","APT");
        usptoFields.put("AC","AC");
        usptoFields.put("ACN","ACN");
        usptoFields.put("AF","AN");
        usptoFields.put("AS","AS");
        usptoFields.put("AU","IN");
        usptoFields.put("CCL","CCL");
        usptoFields.put("EXA","EXA");
        usptoFields.put("EXP","EXP");
        usptoFields.put("PRIR","PRIR");
        usptoFields.put("FREF","FREF");
        usptoFields.put("GOVT","GOVT");
        usptoFields.put("ICL","ICL");
        usptoFields.put("IC","IC");
        usptoFields.put("ICN","ICN");
        usptoFields.put("IS","IS");
        usptoFields.put("SD","ISD");
        usptoFields.put("LREP","LREP");
        usptoFields.put("OREF","OREF");
        usptoFields.put("PARN","PARN");
        usptoFields.put("PCT","PCT");
        usptoFields.put("PT","PN");
        usptoFields.put("REIS","REIS");
        usptoFields.put("RLAP","RLAP");
        usptoFields.put("SPEC","SPEC");
        usptoFields.put("TI","TTL");
        usptoFields.put("REF","REF");
  }

    private BufferStream buffer = new BufferStream();
    private FieldGetter fieldGetter = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();

    private String currentField;
    private boolean isStem = false;

    public String getQuery(BooleanQuery bQuery)
    {

        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp)
    {


        currentField = fieldGetter.getFieldValue(exp);
        if(usptoFields.containsKey(currentField)) {
        currentField = (String) usptoFields.get(currentField);
      }
        descend(exp);

    }



    // jam 11-25-2002 - bug 12.19 and 12.20
    // added phrase parsing for when more that one term is entered
    // in search word text boxes.
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
                        buffer.write(" and ");
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
        try
        {
            if(currentField != null && !currentField.equalsIgnoreCase("all"))
            {
                buffer.write(currentField.toLowerCase()+"/");
            }
            buffer.write(literal.getValue().trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(KeywordWITHIN kWIHIN)
    {
        currentField = null;
    }



    public void visitWith(ExactTerm eTerm)
    {
        try
        {
            if(currentField != null && !currentField.equalsIgnoreCase("all"))
            {
                buffer.write(currentField.toLowerCase()+"/");
            }
            buffer.write("\""+eTerm.getValue().trim()+"\"");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

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

    public void visitWith(Regex reg)
    {
        descend(reg);
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

}