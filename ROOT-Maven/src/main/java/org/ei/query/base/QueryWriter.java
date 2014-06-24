package org.ei.query.base;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanQuery;

abstract public class QueryWriter
    extends BaseNodeVisitor
{

    protected String[] credentials;

    public abstract String getQuery(BooleanQuery bQuery);

    public void setCredentials(String[] credentials)
    {
        this.credentials = credentials;
    }

    protected class BufferStream extends FilterOutputStream
    {

        private int last = -1;

        public BufferStream()
        {
            super(new ByteArrayOutputStream());
        }

        public void close()
            throws IOException
        {
            out.close();
        }

        public void flush()
            throws IOException
        {
            out.flush();
        }

        public String toString()
        {
            return out.toString();
        }

        public void write(byte b)
            throws IOException
        {

            int current = (int)b;
            if(current == 32)
            {
                if(last != 32 && last != 40)
                {
                    out.write(b);
                }

            }
            else
            {
                out.write(b);

            }

            last = current;
        }

        public void write(byte[] b)
            throws IOException
        {
            for(int x=0; x<b.length; ++x)
            {

                write(b[x]);
            }
        }


        public void write(byte[] b, int off, int len)
            throws IOException
        {
            for(int x=0; x<len; ++x)
            {
                write(b[off+x]);
            }

        }

        public void write(String s)
            throws IOException
        {
            for(int x=0; x<s.length(); ++x)
            {
                write((byte)s.charAt(x));
            }
        }


    }




}
