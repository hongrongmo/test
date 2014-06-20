package org.ei.logging;


import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 *  Description of the Class
 *
 *@author     dbaptist
 *@created    November 28, 2001
 */
public class JavaVillageSqlHandler
{

    private StringBuffer insert;
    private Hashtable hashappdata;



    public void writeMessage(CLFMessage message,
                             Connection con)
        throws Exception
    {



        hashappdata = new Hashtable();
        String sql;
        String key;
        String value;
        String line;
        int equal;
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();

        insert = new StringBuffer();
        boolean debug = false;

        // REGEX stuff
        int limit, interps, i;
        PatternMatcher matcher;
        PatternMatcherInput input;
        PatternCompiler compiler;
        Pattern pattern = null;
        String regularExpression;
        String appID, reqID, httpData, cookie, appData;


        // Create Perl5Compiler and Perl5Matcher instances.
        compiler = new Perl5Compiler();
        matcher  = new Perl5Matcher();

        regularExpression = "^<(.*?)> <(.*?)> <(.*?)> <(.*?)> <(.*?)>";

        // Attempt to compile the pattern.  If the pattern is not valid,
        // report the error and exit.
        try
        {
          pattern = compiler.compile(regularExpression);
        }
        catch(MalformedPatternException e)
        {
          System.err.println("Bad pattern.");
          System.err.println(e.getMessage());
        }


        if (matcher.contains(message.getCLFMessage(),pattern))
        {
            MatchResult result=matcher.getMatch();
            if (debug)
            {
                System.out.println("APPID   : "+result.group(1));
                System.out.println("REQID   : "+result.group(2));
                System.out.println("HTTPDATA: "+result.group(3)+" size="+result.group(3).toString().length());
                System.out.println("COOKIE  : "+result.group(4));
                System.out.println("APPDATA : "+result.group(5)+" size="+result.group(5).toString().length());
            }

            appID    = result.group(1);
            reqID    = result.group(2);
            httpData = result.group(3);
            cookie   = result.group(4);
            appData  = result.group(5);


            regularExpression = ";?(.*?)=\"(.*?)\"";

            // Attempt to compile the pattern.  If the pattern is not valid,
            // report the error and exit.
            try
            {
                pattern = compiler.compile(regularExpression);
            }
            catch(MalformedPatternException e)
            {
                System.err.println("Bad pattern.");
                System.err.println(e.getMessage());
            }

            input   = new PatternMatcherInput(appData);
            int groups;

            hashappdata.put("query_string", "-"); // set default
            // Loop until there are no more matches left.
            while(matcher.contains(input, pattern))
            {
                // Since we're still in the loop, fetch match that was found.
                result = matcher.getMatch();
                // Retrieve the number of matched groups.  A group corresponds to
                // a parenthesized set in a pattern.
                groups = result.groups();
                key    = result.group(1);
                value  = result.group(2);
                hashappdata.put(key, new String(value));

            }



            if ( !((String) hashappdata.get("query_string")).equals("-"))
            {
                DateFormat format3 = new SimpleDateFormat("yyyy/MM/dd:hh:mm:ssa");

                insert.append("INSERT INTO java_village_log (cust_id, sid, tid, ip");
                insert.append(", db_name, begin_time, end_time, response_time, report_name");
                insert.append(", hits, query_string, rec_range, save_search, action");
                insert.append(" , application, num_recs) VALUES (");
                insert.append((String) hashappdata.get("cust_id") + ", ");
                // remove the session version number
                if ( hashappdata.get("sid") != null )
                {
                    insert.append("'" + ((String) hashappdata.get("sid")).substring(2) + "', ");
                }

                // Transaction id(TID) is now sequence from database
                insert.append("TransactionID.NextVal, ");

                insert.append("'" + (String) hashappdata.get("ip") + "', ");
                selectiveAppend("db_name");

                if (debug)
                {
                    System.out.println("BEGIN TIME:"+ (String)hashappdata.get("begin_time"));
                }
                date.setTime((long) Long.parseLong((String) hashappdata.get("begin_time")));
                calendar.setTime(date);
                insert.append("to_date('" + format3.format(date) + "', 'yyyy/mm/dd:hh:mi:ssam'), ");

                date.setTime((long) Long.parseLong((String) hashappdata.get("end_time")));
                calendar.setTime(date);
                insert.append("to_date('" + format3.format(date) + "', 'yyyy/mm/dd:hh:mi:ssam'), ");
                long rtime = Long.parseLong((String) hashappdata.get("response_time"));
                int itime = (int)(rtime / 1000);

                insert.append(Integer.toString(itime) + ", ");
                selectiveAppend("report_name");
                insert.append((String) hashappdata.get("hits") + ", ");
                selectiveAppend("query_string");
                insert.append((String) hashappdata.get("rec_range") + ", ");
                insert.append((String) hashappdata.get("save_search") + ", ");

                // Action mapping
                if (((String)hashappdata.get("action")).equalsIgnoreCase("document"))
                {
                    // Document types
                    if (((String)hashappdata.get("format")).equalsIgnoreCase("citation"))
                    {
                        insert.append("'CIT', ");
                    }
                    else if (((String)hashappdata.get("format")).equalsIgnoreCase("abstract"))
                    {
                        insert.append("'AB', ");
                    }
                    else if (((String)hashappdata.get("format")).equalsIgnoreCase("detailed"))
                    {
                        insert.append("'FUL', ");
                    }
                    else if (((String)hashappdata.get("format")).equalsIgnoreCase("fullDoc"))
                    {
                        insert.append("'FUL', ");
                    }
                    else
                    {
                        insert.append("'CIT', ");
                    }
                }
                else if (((String)hashappdata.get("action")).equalsIgnoreCase("search"))
                {
                    // Search types
                    if (((String)hashappdata.get("type")).equalsIgnoreCase("basic"))
                    {
                        insert.append("'BAS', ");
                    }
                    else if (((String)hashappdata.get("type")).equalsIgnoreCase("expert"))
                    {
                        insert.append("'ADV', ");
                    }
                    else if (((String)hashappdata.get("type")).equalsIgnoreCase("related"))
                    {
                        insert.append("'REL', ");
                    }
                }
                else if (((String)hashappdata.get("action")).equalsIgnoreCase("prepare"))
                {
                    // Search types before search
                    if ( ((String)hashappdata.get("context")).equalsIgnoreCase("search") )
                    {
                        if ( ((String)hashappdata.get("type")).equalsIgnoreCase("basic_search") )
                        {
                            insert.append("'BAS', ");
                        }
                        else if ( ((String)hashappdata.get("type")).equalsIgnoreCase("expert_search") )
                        {
                            insert.append("'ADV', ");
                        }
                        else if ( ((String)hashappdata.get("type")).equalsIgnoreCase("related_search") )
                        {
                            insert.append("'REL', ");
                        }
                    }
                }
                else if (((String)hashappdata.get("action")).equalsIgnoreCase("lookup"))
                {
                    insert.append("'LOK', ");
                }


                if ( appID.equals("ChemVillage") )
                {
                    insert.append("'CH', ");
                }
                else if ( appID.equals("EngVillage2") )
                {
                    insert.append("'V2', ");
                }
                else if ( appID.equals("PaperVillage2") )
                {
                    insert.append("'P2', ");
                }
                else if ( appID.equals("EnCompassWeb") )
                {
                    insert.append("'EW', ");
                }
                else if ( appID.equals("EnCompassLit") )
                {
                    insert.append("'EL', ");
                }
                else if ( appID.equals("EnCompassPat") )
                {
                    insert.append("'EP', ");
                }

                insert.append((String) hashappdata.get("num_recs") + ")");
            }
        }


        if(insert.length() == 0)
        {
            return;
        }

        sql = insert.toString();
//        System.out.println("Insert:"+sql+"|");
        Statement stmt = null;

        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        }
        finally
        {
            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean selectiveAppend(String key) {
        if ( hashappdata.get(key) != null ) {
            insert.append("'" + (String) hashappdata.get(key) + "', ");
        } else {
            insert.append("'-', ");
        }

        return (true);
    }
}

