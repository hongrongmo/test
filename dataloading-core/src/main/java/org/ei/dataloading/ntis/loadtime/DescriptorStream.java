package org.ei.dataloading.ntis.loadtime;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DescriptorStream extends FilterInputStream
  {
    private InputStream in;

    public DescriptorStream(InputStream r)
    {
      super(r);
      in = r;
    }


    public String getDescriptor()
      throws IOException
    {
      int numOpen = 0;
      boolean done = false;
      StringBuffer buf = new StringBuffer();
      int i = -1;
      while((i = in.read()) != -1)
      {
        char c = (char)i;

        if(c == '(')
        {
          ++numOpen;
        }

        if(c == ')')
        {
          --numOpen;
        }

        if(c == ',' && numOpen == 0)
        {
          done = true;
        }

        if(done)
        {
          break;
        }
        else
        {
          buf.append(c);
        }
      }

      if(buf.length() == 0)
      {
        return null;
      }

      return buf.toString();
    }


  public boolean getBalancedParen()
    throws IOException
  {
    int numOpen = 0;
    boolean done = false;
    int i = -1;
    while((i = in.read()) != -1)
    {
      char c = (char)i;

      if(c == '(')
      {
        ++numOpen;
      }

      if(c == ')')
      {
        --numOpen;
      }
    }

    if(numOpen == 0)
    {
      done = true;
//      System.out.println("balanced ");
    }
    return done;
  }
}
