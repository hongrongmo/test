package org.ei.logging;



import java.sql.Connection;
import java.sql.Statement;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

public class ALSSqlHandler
{



    /**
     *  Gets the statement attribute of the ALSSqlHandler object
     *
     *@param  event          Description of Parameter
     *@return                The statement value
     *@exception  Exception  Description of Exception
     *@since
     */
    public void writeMessage(CLFMessage message,
                             Connection con)
        throws Exception
    {
        StringBuffer insert = new StringBuffer();
        boolean debug = false;

        // REGEX stuff
        int limit, interps, i;
        PatternMatcher matcher;
        PatternCompiler compiler;
        Pattern pattern = null;
        String regularExpression, input;
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
        } catch(MalformedPatternException e) {
          System.err.println("Bad pattern.");
          System.err.println(e.getMessage());
        }


        if (matcher.contains(message.getCLFMessage(),pattern))
        {
            MatchResult result=matcher.getMatch();
            if (debug) {
                System.out.println("APPID   : "+result.group(1));
                System.out.println("REQID   : "+result.group(2));
                System.out.println("HTTPDATA: "+result.group(3)+" size="+result.group(3).toString().length());
                System.out.println("COOKIE  : "+result.group(4));
                System.out.println("APPDATA : "+result.group(5)+" size="+result.group(5).toString().length());
            }


            appID    = result.group(1);
            reqID    = result.group(2);
            // clean up potential data sql insert issues
            httpData = stringReplace(result.group(3),"'","\'",false);
            cookie   = result.group(4);
            // clean up potential data sql insert issues
            appData  = stringReplace(result.group(5),"'","\'",false);

            // Construct the insert statment for ALS Analytic Logging Service DB format.
            // Consult Websphere for format.
            insert.append("INSERT INTO als_village_log (tmstp, appid, reqid, httpdata,cookie,appdata) ");
            insert.append(" VALUES ( ");
            insert.append("SYSDATE ,");
            insert.append("'" + appID + "' ,");
            insert.append("'" + reqID + "' ,");
            insert.append("'" + httpData + "' ,");
            insert.append("'" + cookie + "' ,");
            insert.append("'" + appData + "' )");
        }
        else
        {
            //insert.insert(0, "SELECT * FROM dual");
            insert = new StringBuffer();
            insert.append("SELECT * FROM dual");
        }
        if (debug)
        {
            System.out.println(insert.toString());
        }

        String sql = insert.toString();

        Statement stmt = null;

        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
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
        }
    }



    /**
     * Replace all occurences of string one with string two.
     * It does not replace if a string has an escape character
     * in front of it ('\').
     * @param org The original string in which strings have to be replaced.
     * @param searchString The string to-be-replaced.
     * @param str2 The new string that replaced 'searchString'.
     * @param caseSensitive Whether the search for the 'searchString' should be case sensitive.
     * @return The newly constructed string.
     */
    public String stringReplace(String org, String searchString, String str2, boolean caseSensitive) {
        if (org == null || searchString == null) {
            return org;
        }
        if (str2 == null) {
            str2 = "";
        }
        StringBuffer  sb = new StringBuffer();
        int  oldi = 0;
        int  i = (!caseSensitive) ? org.toUpperCase().indexOf(searchString.toUpperCase()) : org.indexOf(searchString);
        while (i >= 0) {
            sb.append(org.substring(oldi, i));

            // check escape character
            if (i > 0 && org.charAt(i-1) == '\\') {
                // do not replace
                sb.append(searchString);
            } else {
                // replace now!
                sb.append(str2);
            }
            oldi = i + searchString.length();
            i = (!caseSensitive) ? org.toUpperCase().indexOf(searchString.toUpperCase(), oldi) : org.indexOf(searchString, oldi);
        }
        sb.append(org.substring(oldi, org.length()));
        return sb.toString();
    }
}

