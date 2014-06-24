package org.ei.controller;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;


public class SessionReplacerWriter
    extends FilterWriter
{

    private static final int DOLLAR_SIGN = 36;
    private static final int UPPER_S = 83;
    private static final int UPPER_E = 69;
    private static final int UPPER_I = 73;
    private static final int UPPER_O = 79;
    private static final int UPPER_D = 68;
    private static final int UPPER_N = 78;


    private static final int SPACE = 32;
    private static final int TAB = 9;
    private static final int CR = 13;
    private static final int LF = 10;
    private int LAST = -1;

    private char[] buffer = new char[10];
    private char[] sessionBuf;
    private boolean shouldBuffer = false;
    private boolean stripSpace = false;
    int bufIndex = -1;

    public SessionReplacerWriter(Writer out,
                                 String sessionID,
                                 boolean sSpace)
    {
        super(out);
        sessionBuf = sessionID.toCharArray();
        this.stripSpace = sSpace;
    }

    public void close()
        throws IOException
    {
        if(bufIndex > 0)
        {
            out.write(buffer, 0, bufIndex+1);
        }

        out.close();
    }

    public void flush()
        throws IOException
    {
        if(bufIndex > 0)
        {
            out.write(buffer, 0, bufIndex+1);
        }

        out.flush();
        bufIndex = -1;
    }

    private static void main(String[] args)
            throws Exception
    {
        /*
        StringBuffer buf = new StringBuffer();
        buf.append(" Hello $100 world here is ${$SESSIONID} some stuff  <font><a href=\"/going/home.jsp\">Test</a></font>");
        buf.append(" Lets try some more <A href=\"http://www.go.home?fdfds\"> test 2</a>");
        buf.append("bla $SESSIONID blah <a other=\"yes\" HREF=\"Controller?CID=3\">Home</a>");
        buf.append("<FORM name=\"test\" action=\"Controller\"></form>");
        buf.append("<p><a href=\"javascript:go()\">Test Java script</a> $SESSIONID");
        SessionReplacerStream sout = new SessionReplacerStream(System.out, "333333");
        PrintStream pout = new PrintStream(sout);
        pout.println(buf.toString());
        pout.flush();
        */
    }



    public void write(char buf[], int off, int length)
        throws IOException
    {
        for(int x=0; x<length; ++x)
        {
            write((int)buf[off]);
            ++off;
        }
    }

    public void write(String s, int off, int length)
        throws IOException
    {
        char[] buf = s.toCharArray();
        for(int x=0; x<buf.length; ++x)
        {
            write((int)buf[x]);
        }
    }

    public void write(int b)
        throws IOException
    {

        if(stripSpace)
        {
            if((b == SPACE || b == TAB || b == CR)  &&
              (LAST == SPACE || LAST == TAB || LAST == CR || LAST == LF))
            {
                return;
            }



            if(b == LF && (LAST == SPACE || LAST == TAB || LAST == LF))
            {
                return;
            }

            LAST = b;
        }





        if(!shouldBuffer)
        {
            if(b == DOLLAR_SIGN)
            {
                shouldBuffer = true;
                buffer[++bufIndex] = (char)b;
            }
            else
            {
                out.write(b);
            }
        }
        else
        {
            if(bufIndex < 9)
            {

                if(b == DOLLAR_SIGN)
                {
                    flush();
                }

                buffer[++bufIndex] = (char)b;
            }
            else
            {
                if(buffer[0] == DOLLAR_SIGN &&
                   buffer[1] == UPPER_S &&
                   buffer[2] == UPPER_E &&
                   buffer[3] == UPPER_S &&
                   buffer[4] == UPPER_S &&
                   buffer[5] == UPPER_I &&
                   buffer[6] == UPPER_O &&
                   buffer[7] == UPPER_N &&
                   buffer[8] == UPPER_I &&
                   buffer[9] == UPPER_D)
                {
                    out.write(sessionBuf);
                    bufIndex = -1;
                }
                else
                {
                    //System.out.println(bufIndex);
                    out.write(buffer, 0, bufIndex+1);
                    bufIndex = -1;
                }

                out.write(b);
                shouldBuffer = false;
            }
        }
    }
}


