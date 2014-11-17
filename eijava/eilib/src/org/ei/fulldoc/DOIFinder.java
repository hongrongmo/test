package org.ei.fulldoc;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

public abstract class DOIFinder
{
    public static String ohuburl = "http://linkinghub.elsevier.com/servlets/OHXmlRequestXml";
    public static String salt = "E78C4EAF-4064-41C5-9FC2-E1C73DE9FBF5";
    public static String saltVersion = "1";
    public static String partnerID = "14";
    protected String user;
    protected String password;
    protected String url;
    protected String load;
    protected String driver;
    protected String database;
    protected String skipID;
    protected String rtype;
    private PrintWriter writer;
    private String[] numberPatterns = {"/[1-9][0-9]*/"};
    private Perl5Util perl = new Perl5Util();


    public DOIFinder(String user,
                     String password,
                     String driver,
                     String url,
                     String database,
                     String rtype,
                     String load,
                     String skipID)
    {
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.url = url;
        this.load = load;
        this.rtype = rtype;
        this.database = database;
        this.skipID = skipID;
    }

    private Connection getConnection()
        throws Exception
    {
        Class.forName(this.driver);
        Connection con = DriverManager.getConnection(this.url,
                                      this.user,
                                      this.password);
        return con;
    }

    public void findDOIs()
        throws Exception
    {
        writer = new PrintWriter(new FileWriter(database+"_doi_"+load+".sql"));
        Connection con = getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            int counter = 0;
            String sql = getSQLHook(load);
            stmt = con.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            System.out.println("Version 2...");
            if(this.skipID != null)
            {
                System.out.println("Skipping until:"+ this.skipID);
                while(rs.next())
                {
                    String docID = rs.getString("M_ID");
                    System.out.println("Skipped:"+docID);
                    if(docID.equals(skipID))
                    {
                        break;
                    }
                }
                System.out.println("Done skipping.");

            }

            int i=0;
            while(rs.next())
            {
                i++;
                if(i > 300)
                {
                    break;
                }

                String docID = rs.getString("M_ID");
                String doiTest = rs.getString("do");
                if(doiTest == null)
                {
                    System.out.print(docID+" ");
                    System.out.print(" 1");

                    OHUBID[] ids = getOHUBIDHook(rs,rtype);
                    if(ids != null && (buildLink(ids,rtype)== null))
                    {

                        System.out.print(" 2");
                        String doi = getDOI(ids);
                        System.out.print(" 3");

                        if(doi != null)
                        {
                            writeDOI(docID, doi);
                            counter++;
                            System.out.println(" YES");
                        }
                        else
                        {
                            System.out.println(" NO");
                        }
                    }
                    else
                    {
                        System.out.println(" NA");
                    }
                }
            }
        }
        finally
        {
            System.out.println("Releasing resources ...");
            if(writer != null)
            {
                try
                {
                    System.out.println("Closing file");
                    writer.close();
                }
                catch(Exception e)
                {
                }
            }

            if(rs != null)
            {
                try
                {
                    System.out.println("Closing result set");
                    rs.close();
                }
                catch(Exception e)
                {

                }
            }

            if(stmt != null)
            {
                try
                {
                    System.out.println("Closing statement");
                    stmt.close();
                }
                catch(Exception e)
                {

                }
            }

            if(con != null)
            {
                try
                {
                    System.out.println("Closing the connection");
                    con.close();
                }
                catch(Exception e)
                {
                }
            }
        }
    }

    protected String buildLink(OHUBID[] ids,
                               String rtype)
    {
        if(!rtype.equals("ivip"))
        {
            return null;
        }

        APSGateway aps = APSGateway.getInstance();
        IssueVolumeID id = (IssueVolumeID)ids[0];
        String link = aps.getLink(id.getISSN(),
                           id.getFirstVolume(),
                           id.getFirstPage());

        if(link != null)
        {
            System.out.print(" Skipping APS Link");
        }

        return link;
    }


    protected String getIssn(String issn)
    {
        if(issn == null)
        {
            return null;
        }

        if (issn.length() == 9)
        {
            return issn.substring(0,4)+issn.substring(5,9);
        }
        else if(issn.length() == 8)
        {
            return issn;
        }


        return issn;
    }

    protected String getFirstIssue(String issue)
    {

        if(issue != null)
        {
            if(perl.match("/(\\d+)/", issue))
            {
                return (String) (perl.group(0).toString());
            }
        }

        return null;
    }

    protected String getFirstVolume(String volume)
    {

        if(volume != null)
        {
            if(perl.match("/(\\d+)/", volume))
            {
                return (String) (perl.group(0).toString());
            }
        }

        return null;
    }


    protected String getFirstAuthorSurname(String aus)
    {
        String f = null;

        if(aus == null)
        {
            return null;
        }


        StringTokenizer st = new StringTokenizer(aus,";",false);
        if (st.countTokens() > 0)
        {
            f = st.nextToken();
        }


        if(f != null)
        {
            StringTokenizer st1 = new StringTokenizer(f,",",false);
            if (st1.countTokens() > 0)
            {
                return st1.nextToken();
            }
        }
        return null;
    }

    protected String getFirstAuthorInitial(String aus)
        {
            String f = null;

            if(aus == null)
            {
                return null;
            }


            StringTokenizer st = new StringTokenizer(aus,";",false);
            if (st.countTokens() > 0)
            {
                f = st.nextToken();

            }


            if(f != null)
            {
                StringTokenizer st1 = new StringTokenizer(f,",",false);
                if (st1.countTokens() > 0)
                {
                    st1.nextToken();
                    if(st1.hasMoreTokens())
                    {
                        String firstName = st1.nextToken();
                        System.out.println("First Name********:"+ firstName);
                        return firstName.trim().substring(0,1);
                    }
                }
            }
            return null;
        }


    protected String getFirstPage(String pages)
    {
        StringBuffer retStr=new StringBuffer();
        String firstPage = null;
        if(pages != null)
        {
            StringTokenizer tmpPage = new StringTokenizer(pages,"-");
            if (tmpPage.countTokens()>0)
            {
                firstPage = tmpPage.nextToken();
            }
            else
            {
                firstPage = pages;
            }

            for(int x=0; x<numberPatterns.length; ++x)
            {
                String pattern = numberPatterns[x];
                if(perl.match(pattern, firstPage))
                {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;

    }

    protected String getLastPage(String pages)
    {
        StringBuffer retStr=new StringBuffer();
        String secondPage = null;
        if(pages != null)
        {
            StringTokenizer tmpPage = new StringTokenizer(pages,"-");
            if (tmpPage.countTokens()>0)
            {
                tmpPage.nextToken();
                if(tmpPage.hasMoreTokens())
                {
                    secondPage = tmpPage.nextToken();
                }
            }

            if(secondPage == null || secondPage.length() ==0)
            {
                return null;
            }

            for(int x=0; x<numberPatterns.length; ++x)
            {
                String pattern = numberPatterns[x];
                if(perl.match(pattern, secondPage))
                {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;
    }

    private String getDOI(OHUBID[] ids)
    {
        int counter = 0;
        String doi = null;


        while(counter < 4)
        {
            counter++;
            try
            {
                OHUBRequest orequest = null;
                OHUBResponses theResponses = null;
                orequest = new OHUBRequest(salt, saltVersion, partnerID, ids);
                OHUBClient client = new OHUBClient(DOIFinder.ohuburl);
                theResponses = client.getOHUBResponses(orequest);

                if((theResponses.countResponses() == 2) &&
                   (theResponses.responseAt(1)).itemCount() == 1)
                {
                    doi = theResponses.responseAt(1).itemAt(0).getURL();
                }
                else if((theResponses.responseAt(0)).itemCount() == 1)
                {
                    doi = theResponses.responseAt(0).itemAt(0).getURL();
                }

                //System.out.println("DOI:"+ doi);
                break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return doi;
    }



    protected abstract String getSQLHook(String load);
    protected abstract OHUBID[] getOHUBIDHook(ResultSet rs,
                                              String rtype)
              throws Exception;

    protected abstract String getUpdateHook(String id, String doi);

    private void writeDOI(String id,
                         String doi)
    {
        String updateStatement = getUpdateHook(id,doi);
        writer.println(updateStatement);
        writer.flush();
    }

}