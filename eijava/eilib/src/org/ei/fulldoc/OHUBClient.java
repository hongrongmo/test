package org.ei.fulldoc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;

import HTTPClient.CookieModule;
import HTTPClient.HTTPConnection;

public class OHUBClient
{
    private String baseURL;

    static
    {
        HTTPConnection.setDefaultTimeout(7000);
        CookieModule.setCookiePolicyHandler(null);
    }


    public static void main(String[] argv)
        throws Exception
    {
        OHUBClient oc = new OHUBClient("http://linkinghub.elsevier.com/servlets/OHXmlRequestXml");
        IssueVolumeID iv1 = new IssueVolumeID();
        iv1.setISSN("1063651X");
        iv1.setFirstPage("471011");
        iv1.setFirstVolume("64");
        iv1.setFirstIssue("4 II");


        OHUBID[] docs = new OHUBID[1];
        docs[0] = iv1;


        OHUBRequest request = new OHUBRequest("E78C4EAF-4064-41C5-9FC2-E1C73DE9FBF5",
                                               "1",
                               "14",
                               docs);

        System.out.println("Sending Request ....");
        long begin = System.currentTimeMillis();
        OHUBResponses out = oc.getOHUBResponses(request);

        OHUBResponse r1 = out.responseAt(0);
        if(r1.itemCount() > 0)
        {
            OHUBResponseItem item = r1.itemAt(0);
            System.out.println(item.getURL());
        }
        else
        {
            System.out.println("No Full text");
        }
        long end = System.currentTimeMillis();
        long dif = end - begin;

        System.out.println("<p>Test Time:"+ dif);

    }


    public OHUBClient(String baseURL)
    {
        this.baseURL = baseURL;
    }

    public OHUBResponses getOHUBResponses(OHUBRequest request)
        throws Exception
    {
        String out = null;

        OHUBResponses responses = null;
        HTTPClient.HttpURLConnection con = null;
        InputStream inStream = null;
        PrintWriter writer = null;
        try
        {
            //System.out.println("New Connection");
            URL u = new URL(baseURL);
            con = new HTTPClient.HttpURLConnection(u);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            writer = new PrintWriter(new OutputStreamWriter(con.getOutputStream()));
            //System.out.println(request.getRequestString());
            writer.print("ohubRequest="+request.getRequestString());
            writer.flush();
            inStream = con.getInputStream();
            responses = new OHUBResponses(new OHUBResponseStream(inStream));
        }
        finally
        {
            if(inStream != null)
            {
                inStream.close();
            }

            if(writer != null)
            {
                writer.close();
            }

            if(con != null)
            {
                con.disconnect();
            }
        }

        return responses;
    }


    class OHUBResponseStream extends FilterInputStream
    {
        public OHUBResponseStream(InputStream in)
        {
            super(in);
        }

        public void close()
            throws IOException
        {
            this.in.close();
        }

        public void mark(int readLimit)
        {
            this.in.mark(readLimit);
        }

        public boolean markSupportedd()
        {
            return this.in.markSupported();
        }

        public int read()
            throws IOException
        {
            int i = this.in.read();
            //System.out.print((char)i);
            return i;
        }

        public int read(byte[] b)
            throws IOException
        {
            //System.out.println("Reading into byte array 1");
            int numBytes = 0;
            for(int x=0; x<b.length; ++x)
            {
                int i = read();
                if(i == -1)
                {
                    if(numBytes == 0)
                    {
                        numBytes = -1;
                    }
                    break;
                }
                else
                {
                    ++numBytes;
                    System.out.print((char)i);
                    b[x] = (byte)i;
                }
            }

            return numBytes;

        }


        public int read(byte[] b, int off, int length)
            throws IOException
        {
            int numBytes = 0;
            //System.out.println("Reading into byte array 2");
            for(int x=0; x<length; ++x)
            {
                int i = read();
                if(i== -1)
                {
                    if(numBytes == 0)
                    {
                        numBytes = -1;
                    }

                    break;
                }
                else if(x >= off)
                {
                    //System.out.print((char)i);
                    ++numBytes;
                    b[x] = (byte)i;
                }
            }


            return numBytes;

        }

        public long skip(long n)
            throws IOException
        {
            return in.skip(n);
        }

    }










}
