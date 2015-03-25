package org.ei.parser.base;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;


public class QueryFilterReader
    extends FilterReader
{

    private boolean insertSingleQuoteNext = false;

    public QueryFilterReader(Reader in)
    {
        super(in);
    }


    public void close()
        throws IOException
    {
        in.close();

    }

    public boolean markSupported()
    {
        return in.markSupported();
    }

    public int read()
        throws IOException
    {
        char q = '"';
        int i = -1;

        i = in.read();
        if((char)i == '{' || (char)i == '}' || (char)i == '[' || (char)i == ']')
        {
            i = (int)q;
        }
        else if(i == 92)
        {
            i = 32;
        }


        return i;
    }

    public int read(char[] cbuf, int off, int len)
        throws IOException
    {
        return in.read(cbuf, off, len);
    }

    public boolean ready()
        throws IOException
    {

        return in.ready();
    }

    public void reset()
        throws IOException
    {
        in.reset();
    }

    public long skip(long n)
        throws IOException
    {
        return in.skip(n);
    }

}
