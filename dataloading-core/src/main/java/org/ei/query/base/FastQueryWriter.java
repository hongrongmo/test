package org.ei.query.base;

import java.util.List;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.LimitGroups;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseParser;
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
import org.ei.parser.base.OrderedNear;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.PhraseConnector;
import org.ei.parser.base.ProximityOperator;
import org.ei.parser.base.ProximityPhrase;
import org.ei.parser.base.Regex;
import org.ei.parser.base.StemmedTerm;
import org.ei.parser.base.Term;
import org.ei.parser.base.UnorderedNear;
import org.ei.util.Base64Coder;

public class FastQueryWriter
    extends QueryWriter
{
    private BufferStream buffer = new BufferStream();
    private FieldGetter fieldGetter = new FieldGetter();
    private TermGatherer gatherer = new TermGatherer();
    private PorterStemmer stemmer = new PorterStemmer();
    private String[] fields;
    private Perl5Util perl = new Perl5Util();
    private String currentField;
    private boolean isStem = false;
    private boolean isWildcard = false;
    private boolean isProximity = false;
    int pdepth = 0;
    DatabaseConfig dconfig;

    public static void main(String args[])
        throws Exception
    {
        String[] stemFields = {"TI","AB"};
        String query = "(({Bernstein J} AND Mike B*) WN AU) AND (*t?me* AND (waters ONEAR acids NEAR/4 rains NEAR tent) WN TI) NOT $test WN AB";
        BaseParser parser = new BaseParser();
        AutoStemmer au = new AutoStemmer(stemFields);
        BooleanQuery bq = (BooleanQuery)parser.parse(query);
        bq = au.autoStem(bq);
        FastQueryWriter wr = new FastQueryWriter();

        String fastQuery = wr.getQuery(bq);
        System.out.println(fastQuery);
    }





    private static Properties mappings;

    {
        dconfig = DatabaseConfig.getInstance();
        mappings = new Properties();
        mappings.setProperty("ky", "ky");
        mappings.setProperty("fl", "fl");
        mappings.setProperty("ab", "ab");
        mappings.setProperty("mh", "mh");
        mappings.setProperty("ti", "ti");
        mappings.setProperty("au", "au");
        mappings.setProperty("af", "af");
        mappings.setProperty("pn", "pn");
        mappings.setProperty("st", "st");
        mappings.setProperty("cv", "cv");
        mappings.setProperty("dt", "dt");
        mappings.setProperty("la", "la");
        mappings.setProperty("tr", "tr");
        mappings.setProperty("ai", "ai");
        mappings.setProperty("db", "db");
        mappings.setProperty("yr", "yr");
        mappings.setProperty("ps", "ps");
        mappings.setProperty("wk", "wk");
        mappings.setProperty("cl", "cl");
        mappings.setProperty("cn", "cn");
        mappings.setProperty("sn", "sn");
        mappings.setProperty("bn", "bn");
        mappings.setProperty("an", "an");
        mappings.setProperty("vo", "vo");
        mappings.setProperty("sp", "sp");
        mappings.setProperty("su", "su");
        mappings.setProperty("di", "di");
        mappings.setProperty("cf", "cf");
        mappings.setProperty("cc", "cc");
        mappings.setProperty("mi", "mi");
        mappings.setProperty("ni", "ni");
        mappings.setProperty("ci", "ci");
        mappings.setProperty("pi", "pi");
        mappings.setProperty("pa", "pa");
        mappings.setProperty("rn", "rn");
        mappings.setProperty("nt", "nt");
        mappings.setProperty("ct", "ct");
        mappings.setProperty("av", "av");
        mappings.setProperty("pu", "co");
        mappings.setProperty("co", "co");
        mappings.setProperty("pm", "si");
        mappings.setProperty("pe", "cm");
        mappings.setProperty("ag", "ag");
        mappings.setProperty("bi", "ds");
        mappings.setProperty("oc", "ic");
        mappings.setProperty("pid", "pid");
        mappings.setProperty("pci", "pci");
        mappings.setProperty("pk", "pk");
        mappings.setProperty("puc", "puc");
        mappings.setProperty("pac", "pac");
        mappings.setProperty("pec", "pec");
        mappings.setProperty("prn", "prn");
        mappings.setProperty("pex", "pe");
        mappings.setProperty("pam", "pam");
        mappings.setProperty("doi", "doi");
        mappings.setProperty("all", "all");
        mappings.setProperty("rgi", "ch");
        mappings.setProperty("pfd", "pfd");
        mappings.setProperty("bks", "co");
        mappings.setProperty("bkt", "st");
        mappings.setProperty("ic", "pk");
        mappings.setProperty("ro", "puc");
        mappings.setProperty("dpid", "pk");

        // geobase/georef navigator
        mappings.setProperty("geo", "pid");



        ///EnCompass fields
        mappings.setProperty("pd", "ad");
        mappings.setProperty("cr", "cr");
        mappings.setProperty("sc", "sc");
        mappings.setProperty("cp", "cp");
        mappings.setProperty("cm", "ch"); ///???
        mappings.setProperty("rc", "rc");
        mappings.setProperty("si", "si");///???
        mappings.setProperty("gc", "gc");
        mappings.setProperty("ip", "pk");
        mappings.setProperty("ppa", "af");
        mappings.setProperty("gd", "ds");

        mappings.setProperty("ce", "ca");

        mappings.setProperty("cva", "ta");
        mappings.setProperty("cvp", "tp");
        mappings.setProperty("cvn", "tn");
        mappings.setProperty("cvma", "ma");
        mappings.setProperty("cvmp", "mp");
        mappings.setProperty("cvm", "mh");
        mappings.setProperty("cvmn", "mn");

        //EnCompassPAT fields
        mappings.setProperty("ap", "pam");
        mappings.setProperty("ac", "pco");
        mappings.setProperty("ad", "pa");
        mappings.setProperty("aj", "dn");

        mappings.setProperty("pt", "pt");
        mappings.setProperty("pc", "pac");
        mappings.setProperty("ds", "dg");

        mappings.setProperty("pla", "pla");
        mappings.setProperty("lt", "lt");
        mappings.setProperty("ey", "ey");
        mappings.setProperty("ppd", "pdt");
        mappings.setProperty("prd", "prd");
        mappings.setProperty("prc", "prc");
        mappings.setProperty("prn", "prn");

        mappings.setProperty("so", "st");
        mappings.setProperty("dg", "dg");
    }



    private AuthorExpressionTransformer authorTransformer = new AuthorExpressionTransformer();

    public String getQuery(BooleanQuery bQuery)
    {
        this.credentials = credentials;
        bQuery = authorTransformer.transform(bQuery);
        PhraseBooster pbooster = new PhraseBooster();
        bQuery = pbooster.applyBoost(bQuery);
		FieldGatherer fg = new FieldGatherer();
		this.fields = fg.gatherFields(bQuery);
        bQuery.accept(this);
        return buffer.toString();
    }

    public void visitWith(Expression exp)
    {
        currentField = fieldGetter.getFieldValue(exp);
        descend(exp);
    }


    public void visitWith(Phrase phrase)
    {
		++pdepth;
		try
		{
			if(pdepth == 1 && phrase.getChildCount() == 3)
			{
				buffer.write(" (");
			}

        	descend(phrase);

			if(pdepth == 1 && phrase.getChildCount() == 3)
			{
				buffer.write(")");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		--pdepth;
    }


    public void visitWith(ProximityPhrase pp)
    {
        isProximity = true;
        try
        {

            ProximityParts pt = new ProximityParts();
            List parts = pt.gatherParts(pp);
            buffer.write(" ((");
            for(int i=0; i<parts.size();i++)
            {
                BaseNode bnode = (BaseNode)parts.get(i);
                descend(bnode);
            }

            buffer.write(")");

            if(pt.stems())
            {
                buffer.write(" OR (");
                for(int i=0; i<parts.size();i++)
                {
                    BaseNode bnode = (BaseNode)parts.get(i);
                    if(bnode.getType().equals("Term"))
                    {
                        buffer.write(" ");
                        buffer.write(mappings.getProperty(currentField.toLowerCase())+":QZ"+stemmer.stem((bnode.toString()).toLowerCase())+"QZ");
                    }
                    else
                    {
                        descend(bnode);
                    }
                }

                buffer.write(")");
            }

            buffer.write(")");

            isProximity = false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void visitWith(PhraseConnector pc)
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
            if(isStem && !isProximity)
            {

                buffer.write(" (");
                buffer.write(mappings.getProperty(currentField.toLowerCase())+":QZ"+stemmer.stem(literal.getValue().trim().toLowerCase())+"QZ OR "+mappings.getProperty(currentField.toLowerCase())+":"+literal.getValue().trim());
                buffer.write(")");

            }
            else
            {


                String t = null;

                if((currentField.equalsIgnoreCase("yr")) || (currentField.equalsIgnoreCase("wk")))
                {
                        t = formatRange(literal.getValue().trim());
                }
                else if(currentField.equalsIgnoreCase("di"))
                {
                    t = literal.getValue().trim()+"DCLS";
                }
                else if(currentField.equalsIgnoreCase("bn"))
                {
                  t = perl.substitute("s/-//g", literal.getValue().trim());
                  t = t + "*";
                }
                else if(currentField.equalsIgnoreCase("cl"))
                {
                    t = perl.substitute("s/\\./DQD/g", literal.getValue().trim());
                }
                else if(currentField.equalsIgnoreCase("puc") ||
                        currentField.equalsIgnoreCase("pec") ||
                        currentField.equalsIgnoreCase("pid"))
                {
                  t = perl.substitute("s/\\./PERIOD/g", literal.getValue().trim());
                  t = perl.substitute("s/\\//SLASH/g", t);
                }
                else
                {
                    t = literal.getValue().trim();
                }


                if(currentField.equalsIgnoreCase("db"))
                {
                    t = literal.getValue().trim();
                    String databaseID = t.substring(0,3);
                    Database database = dconfig.getDatabase(databaseID);
                    LimitGroups lg = database.limitSearch(credentials,
                    									   this.fields);

                    int upgradeMask = dconfig.doUpgrade(database.getMask(), credentials);
                    Database[] databases = dconfig.getDatabases(upgradeMask);
                    if(lg != null)
                    {
                      buffer.write("(");
                    }
                    buffer.write(" (");
                    for(int i=0;i<databases.length;i++)
                    {
                        String iname = databases[i].getIndexName();
                        if(i>0)
                        {
                            buffer.write(" OR ");
                        }
                        buffer.write("db:"+iname);
                    }
                    buffer.write(")");
                    if(lg != null)
                    {
						buffer.write(" AND (");
						String fields[] = lg.getFields();

						for(int i=0;i<fields.length; i++)
						{

							if(i>0)
							{
								buffer.write(" AND ");
							}

							String values[] = lg.getValues(fields[i]);
							buffer.write("(");
							for(int j=0; j<values.length; j++)
							{
								if(j>0)
								{
									buffer.write(" OR ");
								}

								buffer.write(fields[i]);
								buffer.write(":");
								buffer.write(values[j]);
							}
							buffer.write(")");

						}
						buffer.write("))");
					}
                }
                else
                {
                    buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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

    public void visitWith(ProximityOperator po)
    {
        descend(po);
    }

    public void visitWith(OrderedNear onear)
    {

        try
        {
            buffer.write(" ONEAR/");
            buffer.write(Integer.toString(onear.getDistance()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void visitWith(UnorderedNear near)
    {
        try
        {
            buffer.write(" NEAR/");
            buffer.write(Integer.toString(near.getDistance()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void visitWith(StemmedTerm st)
    {
        isStem = true;
        descend(st);
        isStem = false;

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

            if(currentField.equalsIgnoreCase("di"))
            {
                t = "\""+eTerm.getNodeValue()+"DCLS\"";
            }
            else if(currentField.equalsIgnoreCase("cr"))
            {
                t = "\""+perl.substitute("s/\\sand\\s/%/g", eTerm.getNodeValue())+"\"";
            }
            else if(currentField.equalsIgnoreCase("cl"))
            {
                 t = "\""+perl.substitute("s/\\./DQD/g", eTerm.getNodeValue())+"\"";
            }
            else if(currentField.equalsIgnoreCase("fl") ||
                    currentField.equalsIgnoreCase("pn") ||
                    currentField.equalsIgnoreCase("cv") ||
                    currentField.equalsIgnoreCase("st") ||
                    currentField.equalsIgnoreCase("af") ||   
                    currentField.equalsIgnoreCase("cva") ||
                    currentField.equalsIgnoreCase("cvp") ||
                    currentField.equalsIgnoreCase("cvn") ||
                    currentField.equalsIgnoreCase("cvma") ||
                    currentField.equalsIgnoreCase("cvmp") ||
                    currentField.equalsIgnoreCase("cvm") ||
                    currentField.equalsIgnoreCase("cvmn") ||            
                    currentField.equalsIgnoreCase("cvm"))
            {
                t = "\"QQDelQQ "+eTerm.getNodeValue()+" QQDelQQ\"";
            }
            else if(currentField.equalsIgnoreCase("puc") ||
			        currentField.equalsIgnoreCase("pec") ||
			        currentField.equalsIgnoreCase("pid"))
            {
              t = perl.substitute("s/\\./PERIOD/g", eTerm.getNodeValue());
              t = perl.substitute("s/\\//SLASH/g", t);
              t = "\"QQDelQQ "+t+" QQDelQQ\"";
            }
            else if(currentField.equalsIgnoreCase("bks") ||
                currentField.equalsIgnoreCase("bkt"))
            {
              t = "B64"+Base64Coder.encode(eTerm.getNodeValue().toLowerCase().trim());
            }
            else
            {
                t = "\""+eTerm.getNodeValue()+"\"";
            }


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

    public void visitWith(Regex reg)
    {
        if(currentField == null)
        {
            currentField = "all";
        }

        String t = null;
        try
        {

            if(currentField.equalsIgnoreCase("di"))
            {
                t = reg.getNodeValue()+"DCLS";
            }
            else if(currentField.equalsIgnoreCase("cl"))
            {
                 t = perl.substitute("s/\\./DQD/g", reg.getNodeValue());
            }
            else if(currentField.equalsIgnoreCase("puc") ||
            		currentField.equalsIgnoreCase("pec") ||
            		currentField.equalsIgnoreCase("pid"))
            {
				t = perl.substitute("s/\\./PERIOD/g", reg.getNodeValue());
				t = perl.substitute("s/\\//SLASH/g", t);
			}
            else
            {
                t = reg.getNodeValue();
            }

            buffer.write(" "+mappings.getProperty(currentField.toLowerCase())+":"+t);

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