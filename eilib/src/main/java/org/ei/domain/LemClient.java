package org.ei.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ei.exception.SearchException;

public class LemClient
{
    private String baseURL;
    private Hashtable lems = new Hashtable();
    private long searchTime = 0L;
    private String[] inWords;

    public static void main(String args[])
        throws Exception
    {
        BufferedReader in = null;

        try
        {
            //in = new BufferedReader(new FileReader("test.txt"));
            LemClient client = new LemClient();
            client.setBaseURL("http://ei-lemm.bos3.fastseadrch.net");
            String[] inWords = new String[] {"better","polarity","round","run"};
            client.setInWords(inWords);
            client.search();

            Hashtable owh = client.getLems();
            for(int i=0;i<inWords.length;i++)
            {
                String[] outWords = (String[])owh.get(inWords[i]);

                //System.out.print(inWords[i] + ": ");
                for(int j=0;j< outWords.length;j++)
                {
                	System.out.print(outWords[j] + ",");
                }
                //System.out.println();
            }

        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }

    public void setBaseURL(String bURL)
    {
        this.baseURL = bURL;
    }

    public void setInWords(String[] inWords)
    {
    	this.inWords = inWords;
    }

    public Hashtable getLems()
    {
    	return lems;
    }

    public void search()
        throws SearchException
    {
        BufferedReader in = null;
        HttpMethod method = null;

        try
        {
            String URL = buildSearchURL();
            HttpClient client = new HttpClient();
            client.setTimeout(3000); // 3 second

            method = new GetMethod(URL);
            //System.out.println(" LemClient URL " + java.net.URLDecoder.decode(URL,"UTF-8"));
            int statusCode = client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            read(in);
        }
        catch(Exception e)
        {
            in = null;
            //throw new SearchException(new Exception("<DISPLAY>A network error has occurred, your request cannot be completed.</DISPLAY>"));
        }
        finally
        {

            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
            if(method != null)
            {
                try
                {
                    method.releaseConnection();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public long getSearchTime()
    {
        return this.searchTime;
    }

    public String buildSearchURL()
        throws Exception
    {
        if(this.baseURL == null)
        {
            throw new Exception("Base URL is not set.");
        }
        else if(this.inWords == null)
        {
            throw new Exception("Input Words are not set.");
        }

        StringBuffer buf = new StringBuffer(this.baseURL);
        buf.append("/cgi-bin/summary?lemmamode=on&inwords=");

        for(int i = 0;i < inWords.length;i++)
        {
        	buf.append(URLEncoder.encode(this.inWords[i],"UTF-8"));
        	buf.append(",");
        }

        buf.deleteCharAt(buf.length()-1);

        //System.out.println(buf.toString());
        return buf.toString();
    }

    public void read(BufferedReader in)
        throws IOException
    {
        String line = null;
        while((line = in.readLine()) != null)
        {
            if(line.indexOf("#in") == 0)
            {
            	if(line.indexOf(" ") != -1 && line.split(" ").length > 1)
            	{
                	String inWord = line.substring(4);
                	if((line = in.readLine()) != null && line.indexOf("#out") == 0)
                		parseOutWords(inWord,line);
            	}
            }
            else if(line.indexOf("#T") == 0)
            {
                parseSearchTime(line);
            }
        }

        if(lems.size() < 1)
        {
        	for(int i = 0; i < inWords.length;i++)
        	{
        		lems.put(inWords[i], inWords);
        	}
        }
    }

    private void parseOutWords(String inWord, String outLine)
    {
    	if(outLine != null && outLine.split(" ").length > 0)
    	{
    		String[] outWords = outLine.split(" ")[1].split(",");
    		lems.put(inWord, outWords);
    	}

    }

    private void parseSearchTime(String timeLine)
    {
        StringTokenizer tokens = new StringTokenizer(timeLine);
        tokens.nextToken();
        String time = tokens.nextToken();
        double t = Double.parseDouble(time);
        double tt = t*1000;
        BigDecimal bd = new BigDecimal(tt);
        this.searchTime = bd.longValue();
    }

}