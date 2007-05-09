package org.ei.controller;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.ei.controller.content.ContentDescriptor;
import org.ei.exception.MajorSystemProblem;
import org.ei.session.SessionCache;
import org.ei.session.User;
import org.ei.session.UserSession;

public class DataResponseCache
{


    private String cacheDir;

    public DataResponseCache(String cacheDir)
    {
        this.cacheDir = cacheDir;

    }

    public DataResponse getDataResponse(OutputPrinter printer,
                                        DataRequest dataRequest)
        throws ModelException,
               MajorSystemProblem,
               RequestException
    {

        DataResponse dataResponse = null;
        HttpURLConnection con = null;
        BufferedWriter outWriter = null;
        InputStream inStream = null;

        try
        {

            /*
            *   1) Get the dataSourceURL and open
            *      the connection.
            */

            ContentDescriptor cd = dataRequest.getContentDescriptor();
            String dataSourceURL = cd.getDataSourceURL();
            URL dataURL = null;
            dataURL = new URL(dataSourceURL);
            con = (HttpURLConnection)dataURL.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);



            /*
            *   2) Put the session information in the
            *      out-going headers.
            *
            */

            UserSession us = dataRequest.getUserSession();

            Properties sesProps = us.unloadToProperties();
            Enumeration sesKeys = sesProps.keys();

            while(sesKeys.hasMoreElements())
            {
                String key = (String)sesKeys.nextElement();
                con.setRequestProperty(key, sesProps.getProperty(key));
            }


            /*
            *   3) Get the request parameters and send them to the
            *   dataSource.
            *
            */


            Properties requestParams = dataRequest.getRequestParameters();

            try
            {
                outWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            }
            catch(Exception e)
            {
                if(e.getMessage().trim().equals("Connection refused"))
                {
//                    System.out.println("Fatal Error.....");
                    throw new Exception("SHUTDOWN");
                }
            }


            Enumeration paramKeys = requestParams.keys();
            StringBuffer debugBuffer = new StringBuffer();
            while(paramKeys.hasMoreElements())
            {
                String paramKey = (String)paramKeys.nextElement();
                String[] values = (String[])requestParams.get(paramKey);
                for(int i=0; i<values.length;i++)
                {
					debugBuffer.append(paramKey+"="+values[i]+"&");
                    outWriter.write(paramKey+"="+URLEncoder.encode(values[i]));
                    if(i<(values.length-1))
                    {
                        outWriter.write("&");
                    }
                }
                if(paramKeys.hasMoreElements())
                {
                    outWriter.write("&");
                }
            }
            outWriter.flush();
            outWriter.close();
            outWriter = null;


            inStream = con.getInputStream();


            /*
            *   4) Get the logging and session info
            *      from the headers.
            *
            */

            Properties logProps = new Properties();
            boolean sessionUpdated = false;
            UserSession nsession = new UserSession();
            Properties sessionProps = new Properties();
            String redirectURL = null;
            String viewURL = null;
            User u = null;
            boolean cache = false;
            long timeToLive = 1L;
            ArrayList comments = new ArrayList();
            String strCdFilename = null;

            boolean sessionUpdate = true;

            int j = 1;

            while(true)
            {
                String hkey = con.getHeaderFieldKey(j);

                if(hkey == null)
                {
                    break;
                }

                if(hkey.indexOf("SESSION") > -1)
                {
                    sessionProps.setProperty(hkey,
                     con.getHeaderField(j));
                    sessionUpdated = true;
                }
                if(hkey.indexOf("FILENAME") > -1)
                {
                    strCdFilename = con.getHeaderField(j);
                }
                else if(hkey.indexOf("COMMENT") > -1)
                {
                    comments.add(con.getHeaderField(j));
                }
                else if(hkey.indexOf("LOG") > -1)
                {
                    // Get logging information.
                    String lkey = hkey.substring(hkey.indexOf(".")+1, hkey.length());
                    logProps.put(lkey, con.getHeaderField(j));
                }
                else if(hkey.indexOf("REDIRECT") > -1)
                {
                    redirectURL = con.getHeaderField(j);
                }
				else if(hkey.indexOf("VIEW") > -1)
				{
					viewURL = con.getHeaderField(j);
				}
                else if(hkey.indexOf("EXCEPTION") > -1)
                {
                    throw new ModelException(con.getHeaderField(j));
                }

                ++j;
            }

            if(sessionUpdated)
            {

                SessionCache sCache = SessionCache.getInstance();
                nsession.loadFromProperties(sessionProps);

                long beginC = System.currentTimeMillis();
                nsession = sCache.updateUserSession(nsession);
                long endC = System.currentTimeMillis();
                //System.out.println("Session Update Time:"+Long.toString(endC-beginC));
            }
            else
            {
                nsession.setTouched(false);
                nsession = dataRequest.getUserSession();
            }



            /*
            *   5) Deal with the incoming XML.
            */


            if(redirectURL == null)
            {
                printer.setContentType(cd.getMimeType());
                // if the FILENAME header was set, then send
                // it on to the printer
                if(strCdFilename != null)
                    printer.setContentDisposition(strCdFilename);

                if(!cd.isBulkmode())
                {
                    printer.setComments(comments);
                    printer.print(getDisplayURL(cd.getDisplayURL(),viewURL),
                                  inStream,
                                  (nsession.getSessionID()).toString());
                }
                else
                {
                    int bufferIndex = -1;
                    SectionStream sectionStream = new SectionStream(inStream);

                    // Get the header and transform it
                    byte[] header = sectionStream.readHeader();
                    bufferIndex = sectionStream.getBufferIndex();
                    ByteArrayInputStream bi = new ByteArrayInputStream(header,
                                                                       0,
                                                                       bufferIndex);
                    long begin = System.currentTimeMillis();
                    printer.print(cd.getDisplayURL(),
                                  bi,
                                  (nsession.getSessionID()).toString());

                    // Get the records and transform them one at a time
                    byte[] record = null;
					int i=0;
                    while((record = sectionStream.readRecord()) != null)
                    {
                        bufferIndex = sectionStream.getBufferIndex();
                        bi  = new ByteArrayInputStream(record,0,bufferIndex);

                        printer.print(cd.getDisplayURL(),
                                      bi,
                                      (nsession.getSessionID()).toString());
						i++;
						if(i >=450)
						{
							throw new Exception("Section Stream Exception:"+debugBuffer.toString());

						}
                    }
                    System.out.println("Number of bulk mode records:"+i);

                    // Get the footer and transform it
                    byte[] footer = sectionStream.readFooter();
                    bufferIndex = sectionStream.getBufferIndex();
                    bi = new ByteArrayInputStream(footer,0, bufferIndex);
                    printer.print(cd.getDisplayURL(),
                                  bi,
                                  (nsession.getSessionID()).toString());
                    //long end = System.currentTimeMillis();
                    //long dif = end - begin;
                    //System.out.println("Record Transform Time:"+ dif);
                }
            }
            else
            {
                printer.printModelRedirect(redirectURL,
                                           nsession.getSessionID());
            }

            dataResponse = new DataResponse(dataRequest,
                                            nsession,
                                            logProps);


        }
        catch(MajorSystemProblem m)
        {
            throw m;
        }
        catch(ModelException me)
        {
            throw me;
        }
        catch(Exception e)
        {
            throw new RequestException(e);
        }
        finally
        {

            if(outWriter != null)
            {
                try
                {
                    outWriter.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(inStream != null)
            {
                try
                {
                    inStream.close();
                }
                catch(Exception e2)
                {
                    e2.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    con.disconnect();
                }
                catch(Exception e3)
                {
                    e3.printStackTrace();
                }
            }
        }

        return dataResponse;
    }

    private String getDisplayURL(String defaultURL,
    							 String customURL)
    {
		if(customURL != null)
		{
			String[] urlparts = defaultURL.split("/");
			StringBuffer urlbase = new StringBuffer("http://");
			urlbase.append(urlparts[2]);
			urlbase.append(customURL);
			System.out.println(customURL);
			return urlbase.toString();
		}

		return defaultURL;
	}
}
