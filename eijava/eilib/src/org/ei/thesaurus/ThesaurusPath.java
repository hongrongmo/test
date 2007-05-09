package org.ei.thesaurus;


import java.io.FileWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.util.Enumeration;
import java.util.Properties;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class ThesaurusPath
{

    private String sessionID;

    public ThesaurusPath(String sessionID)
    {
        this.sessionID = sessionID;
    }

    public static void main(String args[])
        throws Exception
    {

        ConnectionBroker broker = ConnectionBroker.getInstance("/javaplatform2/testpool.xml");
        FileWriter writer = null;
        try
        {
            ThesaurusPath tp = new ThesaurusPath("test");
            writer = new FileWriter("/javaplatform2/out.xml");
            tp.toXML(writer);

            //tp.clearPathFrom(2);
            /*
            Properties props = new Properties();
            props.setProperty("name1", "value1 test");
            props.setProperty("name2", "value2 test");
            props.setProperty("name3", "value2 test");
            tp.addStep("test title", props);
            */
        }
        finally
        {
            try
            {
                if(writer != null)
                {
                    writer.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            broker.closeConnections();
        }
    }


    public void addStep(String context,
                        String title,
                        Properties props)
        throws ThesaurusException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement proc = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            StringBuffer linkBuffer = new StringBuffer();
            Enumeration keys = props.keys();
            while(keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                String value = props.getProperty(key);
                linkBuffer.append(key);
                linkBuffer.append("=");
                linkBuffer.append(URLEncoder.encode(value));
                if(keys.hasMoreElements())
                {
                    linkBuffer.append("&amp;");
                }
            }

            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            proc = con.prepareCall("{call ThesaurusPath_addStep(?,?,?,?)}");
            proc.setString(1,this.sessionID);
            proc.setString(2,context);
            proc.setString(3,title);
            proc.setString(4,linkBuffer.toString());
            proc.executeUpdate();

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }
        finally
        {

            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearPathFrom(int index)
            throws ThesaurusException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement proc  = null;

        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            proc = con.prepareCall("{call ThesaurusPath_clearPathFrom(?,?)}");
            proc.setString(1,this.sessionID);
            proc.setInt(2,index);
            proc.executeUpdate();

        }
        catch(Exception e)
        {
            throw new ThesaurusException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void toXML(Writer out)
        throws ThesaurusException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        try
        {
            broker = ConnectionBroker.getInstance();
            String q = "select * from thesaurus_path where session_id = ? order by index_num";
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(q);
            pstmt.setString(1, this.sessionID);
            rs = pstmt.executeQuery();
            out.write("<TPATH>");
            while(rs.next())
            {
                out.write("<STEP>");
                out.write("<SNUM>");
                out.write(rs.getString("index_num"));
                out.write("</SNUM>");
                out.write("<SCON>");
                out.write(ThesaurusAction.ACTIONS.getProperty(rs.getString("context")));
                out.write("</SCON>");
                out.write("<STI><![CDATA[");
                out.write(rs.getString("title"));
                out.write("]]></STI>");
                out.write("<SLINK>");
                out.write(rs.getString("link"));
                out.write("</SLINK>");
                out.write("</STEP>");
            }
            out.write("</TPATH>");

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
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasIndex(int index)
        throws Exception
    {
        ConnectionBroker broker = ConnectionBroker.getInstance();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean b = false;

        try
        {
            String q = "select index_num from thesaurus_path where session_id = ? and index_num = ?";
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(q);
            pstmt.setString(1, this.sessionID);
            pstmt.setInt(2,index);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                b = true;
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
                    e.printStackTrace();
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return b;
    }


    public ThesaurusStep getFirstStep()
        throws ThesaurusException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ThesaurusStep thesStep = null;

        try
        {
            broker = ConnectionBroker.getInstance();
            String q = "select * from thesaurus_path where session_id = ? and index_num = 0";
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(q);
            pstmt.setString(1, this.sessionID);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                thesStep = new ThesaurusStep();
                thesStep.setStepNum(rs.getInt("index_num"));
                thesStep.setContext(rs.getString("context"));
                thesStep.setTitle(rs.getString("title"));
                thesStep.setLink(rs.getString("link"));
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
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return thesStep;
    }
}