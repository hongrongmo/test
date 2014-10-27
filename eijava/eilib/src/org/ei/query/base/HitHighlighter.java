
package org.ei.query.base;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

import org.ei.domain.Key;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanNot;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.ExactTerm;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Literal;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.StemmedTerm;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Regex;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.ProximityPhrase;
import org.ei.parser.base.Term;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class HitHighlighter extends BaseNodeVisitor
{
//    private static Log log = LogFactory.getLog(HitHighlighter.class);

    Perl5Util util = new Perl5Util();
    PorterStemmer stemmer = new PorterStemmer();

    FieldGetter fget = new FieldGetter();
    private Highlightable doc;
    private String currentField;
    private boolean descend = true;
    private boolean isWildcard = false;
    private boolean isStem = false;
    private BooleanQuery bQuery;

    public HitHighlighter(BooleanQuery b)
    {
        this.bQuery = b;
    }


    public Highlightable highlight(Highlightable doc)
    {
        this.doc = doc;
        bQuery.accept(this);
        return doc;
    }

    public void visitWith(BooleanQuery q)
    {
        descend(q);
    }

    public void visitWith(AndQuery q)
    {
        descend(q);
    }

    public void visitWith(OrQuery q)
    {
        descend(q);
    }

    public void visitWith(NotQuery q)
    {
        descend(q);
        descend = true;
    }


    public void visitWith(Expression e)
    {
        currentField = fget.getFieldValue(e);

        descend(e);

        currentField = null;

    }

    public void visitWith(AndPhrase a)
    {
        descend(a);
    }

    public void visitWith(OrPhrase o)
    {
        descend(o);
    }

    public void visitWith(NotPhrase n)
    {
        descend(n);
        descend = true;
    }

    public void visitWith(BooleanNot b)
    {
        descend = false;
    }

    public void visitWith(BooleanPhrase b)
    {
        if(descend)
        {
            descend(b);
        }
    }

    public void visitWith(Phrase p)
    {
        descend(p);
    }

    public void visitWith(ProximityPhrase p)
    {
            descend(p);
    }


    public void visitWith(Term t)
    {
        descend(t);
    }

    public void visitWith(StemmedTerm t)
    {
        isStem = true;
        descend(t);
        isStem = false;
    }

    public void visitWith(Regex r)
    {

        if(currentField == null)
        {
            currentField = "all";
        }

        Map data =(Map) doc.getHighlightData(currentField);

        if(data != null)
        {
            data = markupWildcard(r.getValue().trim(), data);
            doc.setHighlightData(data);
        }
    }



    public void visitWith(Literal l)
    {

        //System.out.println("Getting ready to markup..."+l.getValue().trim());
        if(currentField == null)
        {
            currentField = "all";
        }

        Map data =(Map) doc.getHighlightData(currentField);
        if(data != null)
        {
            //System.out.println("Data was not null");
            if(isStem)
            {
                data = markupStems(l.getValue().trim(), data);
                //System.out.println("Marking stems ...");
            }
            else
            {
//                System.out.println("Marking normal phrase..");
                data = markupWithRegex(l.getValue().trim(), data);

            }

            doc.setHighlightData(data);
        }
    }


    public void visitWith(ExactTerm ep)
    {

        if(currentField == null)
        {
            currentField = "all";
        }

        Map data =(Map)doc.getHighlightData(currentField);
        if(data != null)
        {
            doc.setHighlightData(markupWithRegex(ep.getValue(),data));
        }
    }



    private Map markupStems(String term,
                            Map data)
    {
        try
        {
            Iterator en = data.keySet().iterator();
            String stemmedTerm = stemmer.stem(term.toLowerCase());
            Hashtable alreadyDone = new Hashtable();
            while(en.hasNext())
            {
                    Key key = (Key)en.next();
                    String value[] = (String[])data.get(key);
                    //System.out.println("Key:"+key);

					for(int i=0; i<value.length; i++)
					{
						alreadyDone.clear();
						ArrayList dataList = new ArrayList();
                    	util.split(dataList, "/\\W+/", value[i]);

                    	for(int m=0; m<dataList.size(); m++)
                    	{
                    	    String word = (String)dataList.get(m);

                    	    if(word != null && word.length() > 1)
                    	    {
                    	        String stemmedWord = stemmer.stem(word.toLowerCase());
                    	        //System.out.println(stemmedTerm+":"+stemmedWord);
                    	        if(!alreadyDone.containsKey(word) &&
                    	           stemmedTerm.equalsIgnoreCase(stemmedWord))
                    	        {
                    	            String reg ="s/(\\b)("+word+")(\\b)/$1::H:$2:H::$3/gi";
                    	            //System.out.println(key+" doing stems sub:"+reg);
                    	            value[i] = util.substitute(reg, value[i]);
                    	            alreadyDone.put(word, "Y");
                    	        }
                    	    }
                    	}
					}

                    data.put(key, value);
            }
        }
        catch(Exception e)
        {
            /*
                Sitting on exceptions here on purpose.
            */
        }

        return data;
    }

    private Map markupWildcard(String term,
    						   Map data)
    {


        try
        {
            // single character replacement
            // match only non-whitespace
            // that is all [a-zA-Z_0-9] and punctuation!
            term = cleanTerm(term);
            term = term.replaceAll("\\?","[\\\\w]");
            // * is zero or more word characters
            term = term.replaceAll("\\*","\\\\w{0,}");


            Iterator itr = data.keySet().iterator();
            String reg = "s/\\b(".concat(term).concat(")\\b/::H:$1:H::/gi");

            while (itr.hasNext())
            {
                Key key = (Key)itr.next();
                String value[] = (String[]) data.get(key);
     			for(int i=0; i<value.length; i++)
     			{
     				value[i] = util.substitute(reg, value[i]);
				}
                data.put(key, value);

            }
        }
        catch(Exception e)
        {
            /*
                Sitting on exceptions here on purpose.
            */
        }

        return data;
    }





    private String cleanTerm(String term)
    {
        boolean unclean = false;
        if(term.indexOf("(") > -1)
        {
            term = term.replace('(', ' ');
            unclean = true;
        }

        if(term.indexOf(")") > -1)
        {
            term = term.replace(')', ' ');
            unclean = true;
        }

        if(term.indexOf("[") > -1)
        {
            term = term.replace('[', ' ');
            unclean = true;
        }

        if(term.indexOf("]") > -1)
        {
            term = term.replace(']', ' ');
            unclean = true;
        }

        if(term.indexOf("/") > -1)
        {
            term = term.replace('/', ' ');
            unclean = true;
        }

        if(term.indexOf(".") > -1)
        {
            term = term.replace('.', ' ');
            unclean = true;
        }

        if(term.indexOf("-") > -1)
        {
            term = term.replace('-', ' ');
            unclean = true;
        }

        if(unclean)
        {
            term.trim();
            term = util.substitute("s/\\s+/[^a-zA-Z0-9]+/g",term);
        }

        return term;
    }


    private Map markupWithRegex(String phrase,
                                    Map data)
    {
        try
        {
            phrase = phrase.trim();
            String reg = buildRegex(phrase);
            //System.out.println("Reg+++++++++++++++:"+ reg);
            Iterator en = data.keySet().iterator();
            while(en.hasNext())
            {

                Key key = (Key)en.next();
                String value[] = (String[])data.get(key);

				for(int i=0; i<value.length; i++)
				{
                	value[i] = util.substitute("s/"+reg+"/$1::H:$2:H::$3/gi", value[i]);
				}

                data.put(key, value);
            }
        }
        catch(Exception e)
        {
            /*
                Sitting on exceptions here on purpose.
            */
        }

        return data;
    }

    private String buildRegex(String s)
    {
        ArrayList termList = new ArrayList();
        util.split(termList, s);
        StringBuffer buf = new StringBuffer();
        buf.append("(\\b)(");
        for(int i=0; i< termList.size(); ++i)
        {
            buf.append(cleanTerm((String)termList.get(i)));
            if(i==termList.size()-1)
            {
                buf.append(")(\\b)");
            }
            else
            {
                buf.append("[^a-zA-Z0-9]+");
            }
        }

        //System.out.println(buf.toString());
        return buf.toString();

    }
}