package org.ei.thesaurus;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.ClassificationID;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.util.StringUtil;


public class ThesaurusRecordBroker
{
	private final static Logger log4j = Logger.getLogger(ThesaurusRecordBroker.class);

    private Database database;
    private int AndOrlength=0;
    private int pageSize = 0;

    public ThesaurusRecordBroker(Database database)
    {
        this.database = database;
    }

    public ThesaurusPage buildPage(ThesaurusRecordID[] thesaurusRecIDs,
                                   int beginIndex,
                                   int endIndex)
            throws ThesaurusException
    {

        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        if(endIndex == -1)
        {
            return new ThesaurusPage(0);
        }

        ThesaurusPage tpage = new ThesaurusPage(((endIndex - beginIndex) + 1));

        StringBuffer buf = new StringBuffer("select * from ");
        String fieldName = null;
        boolean useRecordID = false;
        if(thesaurusRecIDs[0].getRecordID() > -1)
        {
            fieldName = "t_id";
            useRecordID = true;
        }
        else
        {
            fieldName = thesaurusRecIDs[0].getSearchField();
            useRecordID = false;
        }

        buf.append(database.getIndexName());
        buf.append("_thesaurus where "+fieldName+" in ");
        buf.append(buildInList(thesaurusRecIDs, beginIndex, endIndex, useRecordID,fieldName));
        buf.append(" order by "+fieldName);
        pageSize=(endIndex +AndOrlength - beginIndex) + 1;
        tpage = new ThesaurusPage(pageSize);

        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();

            rs = stmt.executeQuery(buf.toString());
            int i=0;
            while(rs.next())
            {

                ThesaurusRecordID id = null;
                if(this.database.getIndexName().equals("cpx"))
                {
                	id = new ThesaurusRecordID(	 rs.getInt("t_id"),
												 rs.getString("MAIN_TERM_DISPLAY"),
												 rs.getString("HISTORY_SCOPE_NOTES"),
												 rs.getString("STATUS"),
												 this.database);
				}
				else
				{
					id = new ThesaurusRecordID(	 rs.getInt("t_id"),
                                                 rs.getString("MAIN_TERM_DISPLAY"),
												 "main_term_display",
                                                 this.database);
				}


                ThesaurusRecordImpl trec = new ThesaurusRecordImpl(id);
                ThesaurusRecordProxy proxy = new ThesaurusRecordProxy(trec);
                proxy.setLeadinTermIDs(buildRecIDs(rs.getString("LEADIN_TERMS")));
                proxy.setRelatedTermIDs(buildRecIDs(rs.getString("RELATED_TERMS")));
                if(database.getIndexName().equals("geo"))
                {
					proxy.setNarrowerTermIDs(buildRecIDs(getStringFromClob(rs.getClob("NARROWER_TERMS"))));
				}
				else
				{
                proxy.setNarrowerTermIDs(buildRecIDs(rs.getString("NARROWER_TERMS")));
				}

                proxy.setBroaderTermIDs(buildRecIDs(rs.getString("BROADER_TERMS")));

				if(this.database.getIndexName().equals("cpx") && thesaurusRecIDs[0].getMainTerm()!=null && thesaurusRecIDs[0].getMainTerm().indexOf(" OR ")>0)
                {
					proxy.setUserTermAndOrFlag("OR");
				}
				else if(this.database.getIndexName().equals("cpx") && thesaurusRecIDs[0].getMainTerm()!=null && thesaurusRecIDs[0].getMainTerm().indexOf(" AND ")>0)
				{
					proxy.setUserTermAndOrFlag("AND");
				}

                proxy.setUseTermIDs(buildRecIDs(rs.getString("USE_TERMS")));
                proxy.setScopeNotes(rs.getString("SCOPE_NOTES"));
                proxy.setStatus(rs.getString("STATUS"));
                if(database.getIndexName().equals("ins") || database.getIndexName().equals("cpx"))
                {
					proxy.setTopTermIDs(buildRecIDs(rs.getString("TOP_TERMS")));
					proxy.setHistoryScopeNotes(rs.getString("HISTORY_SCOPE_NOTES"));
					proxy.setClassificationIDs(buildClassIDs(rs.getString("CLASS_CODES")));
					proxy.setPriorTermIDs(buildRecIDs(rs.getString("PRIOR_TERMS")));
					proxy.setDateOfIntro(rs.getString("DATE_OF_INTRO"));
				}
				if(database.getIndexName().equals("grf"))
				{
					proxy.setCoordinates(rs.getString("COORDINATES"));
					proxy.setType(rs.getString("TYPE"));
				}

				if(database.getIndexName().equals("ept"))
				{
					proxy.setSearchInfo(rs.getString("SEARCHING_INFO"));
					proxy.setRegistryNumber (rs.getString("CASE_NUMBER"));
					proxy.setRegistryNumberBroaderTerm(rs.getString("CASE_NUMBER_BROADTERM"));
					proxy.setSeeAlsoIDs (buildRecIDs(rs.getString("SA")));
					proxy.setSeeIDs (buildRecIDs(rs.getString("SEE")));
					proxy.setChemicalAspectsIDs (buildRecIDs(rs.getString("chemical_aspects")));
				}

                int index = getOriginalIndex(id,
                                             thesaurusRecIDs,
                                             beginIndex,
                                             endIndex+AndOrlength+i);

                if(i>tpage.size()-1){
                	tpage.add();
                }

               	tpage.set(i,proxy);
                i++;
            }
        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }

            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }

        return tpage;
    }

    private String getStringFromClob(Clob clob) throws Exception
	{
		String temp = null;
		if (clob != null)
		{
			temp = clob.getSubString(1, (int) clob.length());
		}

		return temp;
    }


    protected ThesaurusRecordID[] buildRecIDs(String s)
    {
        if(s == null)
        {
            return null;
        }

        StringTokenizer tokens = new StringTokenizer(s, ";");
        ThesaurusRecordID[] ids = new ThesaurusRecordID[tokens.countTokens()];
        int i = 0;

        while(tokens.hasMoreTokens())
        {
            String token = tokens.nextToken();
            ids[i] = new ThesaurusRecordID(token, "main_term_display",this.database);
            i++;
        }

        return ids;
    }


    protected ClassificationID[] buildClassIDs(String s)
    {
        if(s == null)
        {
            return null;
        }

        StringTokenizer tokens = new StringTokenizer(s, ";");
        ClassificationID[] ids = new ClassificationID[tokens.countTokens()];
        int i = 0;

        while(tokens.hasMoreTokens())
        {
            String token = tokens.nextToken();
            ids[i] = new ClassificationID(token, this.database);
            i++;
        }

        return ids;
    }




    protected int getOriginalIndex(ThesaurusRecordID recID,
                                   ThesaurusRecordID[] originalArray,
                                   int beginIndex,
                                   int endIndex)
    {
        int i = 0;
        for(int t=beginIndex; t<=endIndex;t++)
        {
            if(originalArray.length>t && originalArray[t].equals(recID))
            {
                return i;
            }
            i++;
        }

        return i;
    }


    protected String buildInList(ThesaurusRecordID[] ids,
                                 int beginIndex,
                                 int endIndex,
                                 boolean useRecordID,
                                 String fieldName)
    {
        StringUtil sUtil = new StringUtil();
        StringBuffer buf = new StringBuffer("(");

        for(int i=beginIndex; i<=endIndex; i++)
        {
            if(i > beginIndex)
            {
                buf.append(",");
            }

            ThesaurusRecordID id = ids[i];
            if(id.getDatabase().getIndexName().equals("ept"))
            {
                if(useRecordID)
                {
                    buf.append(Integer.toString(id.getRecordID()));
                }
                else
                {

                    String ns = sUtil.replace(id.getMainTerm(),
                                  "'",
                                  "''",
                    StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

                    buf.append("'");
                   if(ns.indexOf(" plus ")>-1)
                    {
    					ns = ns.replaceAll(" plus ",";");
    					String[] nsString=ns.split(";");
    					if(nsString.length>0)
    					{
    						AndOrlength = nsString.length-1;
    					}

    				}

    				if(fieldName.equals("main_term_search"))
    				{
                    	buf.append(ns.replace(";","','").toLowerCase());

    				}
    				else
    				{
                    	buf.append(ns.replace(";","','"));

    				}
    				buf.append("'");

                }
            }
            else
            {
            	if(useRecordID)
	            {
	                buf.append(Integer.toString(id.getRecordID()));
	            }
	            else
	            {

	                String ns = sUtil.replace(id.getMainTerm(),
	                              "'",
	                              "''",
	                StringUtil.REPLACE_GLOBAL,
	                StringUtil.MATCH_CASE_INSENSITIVE);

	                buf.append("'");
	                if(ns.indexOf(" OR ")>-1)
	                {
						String[] nsString=ns.split(" OR ");
						if(nsString.length>0)
						{
							AndOrlength = nsString.length-1;
						}
						ns=ns.replaceAll(" OR ","','");
					}
					else  if(ns.indexOf(" AND ")>-1)
	                {
						String[] nsString=ns.split(" AND ");
						if(nsString.length>0)
						{
							AndOrlength = nsString.length-1;
						}
						ns=ns.replaceAll(" AND ","','");
					}

					if(fieldName.equals("main_term_search"))
					{
	                	buf.append(ns.toLowerCase());
					}
					else
					{
	                	buf.append(ns+"','"+ns+"*");
					}

	                buf.append("'");
	            }
            }

        }

        buf.append(")");
		return buf.toString();
    }


    public ThesaurusPage getAlphaStartingPoint(String basePhrase)
        throws ThesaurusException
    {

        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ThesaurusPage tpage = null;

        try
        {

            ThesaurusRecordID trecID = null;
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);

			pstmt = con.prepareStatement("select t_id, main_term_search from "+database.getIndexName()+"_thesaurus where main_term_search like ? order by t_id");
            boolean keepTrying = true;

            int endPoint = basePhrase.length();

            while(keepTrying)
            {

                String firstChars = basePhrase.substring(0,endPoint);
                pstmt.setString(1, firstChars+"%");

                try
                {
                    rs = pstmt.executeQuery();

                    while(rs.next())
                    {
                        int recID = rs.getInt("t_id");
                        String mainTermSearch = rs.getString("main_term_search");
                        if(basePhrase.compareTo(mainTermSearch) < 0)
                        {
                            if(recID > 0)
                            {
                                trecID = new ThesaurusRecordID(--recID, database);
                            }
                            else
                            {
                                trecID = new ThesaurusRecordID(0, database);
                            }

                            break;
                        }
                    }
                }
                finally
                {
                    if(rs != null)
                    {
                        try
                        {
                            rs.close();
                        }
                        catch(Exception e)
                        {
                        }
                    }
                }

                --endPoint;
                if(endPoint == 0 ||
                   trecID != null)
                {
                    keepTrying = false;
                }
            }

            ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
            int num = -1;

            if(trecID != null)
            {
                recIDs[0] = trecID;
                num = 0;
            }

            tpage = buildPage(recIDs,0,num);


        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }
        finally
        {
            try
            {
                if(pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
            }

            try
            {
                if(con != null)
                {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                }
            }
            catch(Exception e3)
            {
                e3.printStackTrace();
            }
        }

        return tpage;
    }


    public ThesaurusPage getSuggestions(String basePhrase,
                                        int howMany)
        throws ThesaurusException
    {

        ThesaurusPage tpage = null;
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int bsize = basePhrase.length();
        BestTerms best = null;

        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);

			pstmt = con.prepareStatement("select t_id, main_term_search from "+database.getIndexName()+"_thesaurus where main_term_search like ? order by main_term_search");
            //System.out.println("ThesaurusPage::getSuggestions: " + "select t_id, main_term_search from "+database.getIndexName()+"_thesaurus where main_term_search like ?");
            boolean keepTrying = true;

            int endPoint = basePhrase.length();
            Hashtable cache = new Hashtable();

            while(keepTrying)
            {
                best = new BestTerms(howMany);

                String firstChars = basePhrase.substring(0,endPoint);
                pstmt.setString(1, firstChars+"%");


                try
                {
                    rs = pstmt.executeQuery();
                    while(rs.next())
                    {
                        String possible = rs.getString("main_term_search");
                        int recID = rs.getInt("t_id");

                        int distance = editDistance(basePhrase,
                                                    possible,
                                                    bsize,
                                                    possible.length(),
                                                    endPoint,
                                                    cache);

                        ThesaurusRecordID trecID = new ThesaurusRecordID(recID, database);
                        RecIDScore ps = new RecIDScore();
                        ps.score = distance;
                        ps.id = trecID;
                        best.put(ps);
                    }
                }
                finally
                {
                    if(rs != null)
                    {
                        try
                        {
                            rs.close();
                        }
                        catch(Exception e)
                        {
                        }
                    }
                }

                --endPoint;

                /*
                if(endPoint == 2 ||
                   best.size() == howMany)
                {
                    keepTrying = false;
                }
                */

                if(endPoint <= 1)
                {
                    keepTrying = false;
                }

            }


            List slist = best.getBestTerms();
            ThesaurusRecordID[] recIDs = new ThesaurusRecordID[slist.size()];
            for(int i=0; i<slist.size();++i)
            {
                    RecIDScore r = (RecIDScore)slist.get(i);
                    recIDs[i] = r.id;
            }

            tpage = buildPage(recIDs,0, (slist.size()-1));

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }
        finally
        {

            try
            {
                if(pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
            }

            try
            {
                if(con != null)
                {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                }
            }
            catch(Exception e3)
            {
                e3.printStackTrace();
            }
        }

        return tpage;
    }


     /**
        Finds and returns the smallest of three integers
     */
    private final static int min(int a, int b, int c)
    {
            int t = (a < b) ? a : b;
            return (t < c) ? t : c;
    }


    /**
    * This static array saves us from the time required to create a new array
    * everytime editDistance is called.
    */
    private int e[][] = new int[0][0];

    /**
    Levenshtein distance also known as edit distance is a measure of similiarity
    between two strings where the distance is measured as the number of character
    deletions, insertions or substitutions required to transform one string to
    the other string.
    <p>This method takes in four parameters; two strings and their respective
    lengths to compute the Levenshtein distance between the two strings.
    The result is returned as an integer.
    */

    private final int editDistance(String s,
                                   String t,
                                   int n,
                                   int m,
                                   int booster,
                                   Hashtable cache)
    {

        int theDistance = 0;

        if(cache.containsKey(t))
        {
            Integer inte = (Integer)cache.get(t);
            theDistance = inte.intValue();
        }
        else
        {


            if (e.length <= n || e[0].length <= m)
            {
                e = new int[Math.max(e.length, n+1)][Math.max(e.length, m+1)];
            }

            int d[][] = e; // matrix
            int i; // iterates through s
            int j; // iterates through t
            char s_i; // ith character of s

            if (n == 0) return m;
            if (m == 0) return n;

            // init matrix d
            for (i = 0; i <= n; i++) d[i][0] = i;
            for (j = 0; j <= m; j++) d[0][j] = j;

            // start computing edit distance
            for (i = 1; i <= n; i++)
            {
                s_i = s.charAt(i - 1);
                for (j = 1; j <= m; j++)
                {
                    if (s_i != t.charAt(j-1))
                        d[i][j] = min(d[i-1][j], d[i][j-1], d[i-1][j-1])+1;
                    else d[i][j] = min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]);
                }
            }

            // we got the result!

            theDistance = d[n][m];
            theDistance = (theDistance - (5*booster));
            cache.put(t, new Integer(theDistance));
        }

        return theDistance;
    }

    class RecIDScore
    {
        public int score;
        public ThesaurusRecordID id;

        public boolean equals(Object o)
        {
            RecIDScore r = (RecIDScore)o;
            if(r.id.getRecordID() == this.id.getRecordID())
            {
                return true;
            }

            return false;
        }

        public String toString()
        {
            return Integer.toString(id.getRecordID());
        }
    }


    class ScoreSortAlgo implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            int r = 0;
            RecIDScore r1 = (RecIDScore) o1;
            RecIDScore r2 = (RecIDScore) o2;


            if(r1.score == r2.score)
            {
                if(r1.id.getRecordID() > r2.id.getRecordID())
                {
                    r = 1;
                }
                else
                {
                    r = -1;
                }
            }
            if(r1.score > r2.score)
            {
                r = 1;
            }
            else
            {
                r = -1;
            }
            return r;
        }
    }


    class BestTerms
    {
        Comparator comp = new ScoreSortAlgo();
        private int maxSize;
        LinkedList l = new LinkedList();
        public BestTerms(int maxSize)
        {
            this.maxSize = maxSize;
        }

        public int size()
        {
            return l.size();
        }

        public void put(RecIDScore recScore)
        {
            if(l.size() >= maxSize)
            {
                l.add(recScore);
                Collections.sort(l,comp);
                l.removeLast();
            }
            else
            {
                l.add(recScore);
            }
        }

        public List getBestTerms()
        {
            Collections.sort(l,comp);
            return l;
        }
    }

}