package org.ei.controller;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import org.ei.util.*;


public class RssFileWriter
    extends FilterWriter
{
    private FileWriter out1;
    private String cacheID;

    public RssFileWriter(Writer out, String cacheID)
    	throws IOException,
    		   SpinLockException
    {
		super(out);
		this.cacheID = cacheID;
		SpinLock spinLock = SpinLock.getInstance();
		boolean lockPlaced = spinLock.placeLock(cacheID,
												SpinLock.FOR_WRITE,
												7,
												1000);
		if(lockPlaced)
		{
			String fileName = null;
			try
			{
				fileName = RssCache.getFileName(cacheID);
				out1 = new FileWriter(fileName);
			}
			catch(Exception e)
			{
				/*
					If for some reason the filehandle could not be created
					remove the write lock now because it won't be able
					to be removed later.
				*/
				spinLock.releaseLock(cacheID, SpinLock.FOR_WRITE);
				throw new IOException(e.getMessage());
			}
		}
		else
		{
			throw new IOException("could not obtain write lock");
		}
    }

    public void close()
        throws IOException
    {
		SpinLock spinLock = SpinLock.getInstance();
		try
		{
			try
			{
				out1.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				out.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			spinLock.releaseLock(this.cacheID, SpinLock.FOR_WRITE);
		}
    }

    public void flush()
        throws IOException
    {
		try
		{
			out1.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try
		{
        	out.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }




	public void write(char buf[])
		throws IOException
	{
		write(buf,0,buf.length);
	}

    public void write(String s)
	        throws IOException
	{
		write(s,0,s.length());
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
    	out.write(b);
   		out1.write(b);
    }
}


