package org.ei.thesaurus;

import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class ThesaurusPath
{
    private final static Logger log4j = Logger.getLogger(ThesaurusPath.class);

    private String sessionID;
    private List<ThesaurusStep> steps = new ArrayList<ThesaurusStep>();
    private int stepNum;

    private String termsearchurl;
    private String fullrecurl;
    private String browseurl;

    // New constructor for use with JSESSion
    public ThesaurusPath(String termsearchurl, String fullrecurl, String browseurl) {
        super();
        this.termsearchurl = termsearchurl;
        this.fullrecurl = fullrecurl;
        this.browseurl = browseurl;
    }

    // Old constructor for database
    @Deprecated
    public ThesaurusPath(String sessionID)
    {
        this.sessionID = sessionID;
    }

    /**
     * Add a new "step" to thesaurus path
     *
     * @param context
     * @param title
     * @param props
     * @throws ThesaurusException
     */
    public void addStep(String context,
        String title,
        Properties props)
        throws ThesaurusException {

        ThesaurusStep step = new ThesaurusStep();
        step.setStepNum(stepNum++);
        step.setContext(context);
        step.setTitle(title);

        StringBuffer linkBuffer = new StringBuffer();
        if (ThesaurusAction.SEARCH.equals(context)) {
            linkBuffer.append(this.termsearchurl + "?");
        } else if (ThesaurusAction.BROWSE.equals(context)) {
            linkBuffer.append(this.browseurl + "?");
        } else if (ThesaurusAction.FULL.equals(context)) {
            linkBuffer.append(this.fullrecurl + "?");
        }
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            linkBuffer.append(key);
            linkBuffer.append("=");
            linkBuffer.append(URLEncoder.encode(value));
            if (keys.hasMoreElements()) {
                linkBuffer.append("&");
            }
        }
        step.setLink(linkBuffer.toString());
        steps.add(step);
    }

    /**
     * Clear the path from the selected index
     *
     * @param index
     * @throws ThesaurusException
     */
    public void clearPathFrom(int index) throws ThesaurusException {
        if (index >= steps.size()) {
            log4j.warn("Index to clear is outside the current steps range!");
            return;
        }

        while (index <= steps.size()-1) {
            steps.remove(steps.size()-1);
        }
        return;
    }

    /**
     * Return list of all steps
     * @return
     * @throws ThesaurusException
     */
    public List<ThesaurusStep> getSteps() throws ThesaurusException {
        return steps;
    }

    /**
     * Convert path to XML
     * @param out
     * @throws ThesaurusException
     */
    @Deprecated
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
            while (rs.next())
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

        } catch (Exception e)
        {
            throw new ThesaurusException(e);
        } finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (pstmt != null)
            {
                try
                {
                    pstmt.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (con != null)
            {
                try
                {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if step index exists
     * @param index
     * @return
     * @throws Exception
     */
    public boolean hasIndex(int index) {
        return steps.size() > index;

    }

    /**
     * Return first step
     * @return
     * @throws ThesaurusException
     */
    public ThesaurusStep getFirstStep() {
        return steps.get(0);
    }
}