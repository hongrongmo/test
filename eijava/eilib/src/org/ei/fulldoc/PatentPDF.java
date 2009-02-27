package org.ei.fulldoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ei.logging.LogClient;
import org.ei.util.GUID;
import org.ei.security.utils.SecureID;

public class PatentPDF extends HttpServlet
{
    private String logServiceURL;
    private HttpClient client;
    private static final String PAT2PDF_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; MS-RTC LM 8)";
    public static final long PATENT_LINK_EXPIRES = 600000;

    public void init()
        throws ServletException
    {

      System.getProperties().setProperty("httpclient.useragent", PatentPDF.PAT2PDF_USER_AGENT);
      MultiThreadedHttpConnectionManager connectionManager =  new MultiThreadedHttpConnectionManager();

      client = new HttpClient(connectionManager);
      client.setConnectionTimeout(3000);

      ServletConfig config = getServletConfig();
      logServiceURL = config.getInitParameter("logURL");
    }

    public void service(HttpServletRequest request,
                HttpServletResponse response)
        throws IOException, ServletException
    {
        String redirect = null;
        try {

            String secureID = request.getParameter("secureID");
            if((secureID == null) || (secureID.equals("")) || (!SecureID.validSecureID(secureID)))
            {
                log("Invalid securID - Access forbidden.");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String patNum = request.getParameter("pn");
            String authCode = request.getParameter("ac");
            String kindCode = request.getParameter("kc");
            //String cType = request.getParameter("type");
            redirect = request.getParameter("rurl");

            if(patNum != null && authCode != null && kindCode != null) {

                if("EP".equalsIgnoreCase(authCode)) {
                  // send to redirect site
                  boolean redirectsent = false;

                  GetMethod get = new GetMethod(redirect);
                  // test to see if redirect site is up/page exists
                  try {
                    client.executeMethod(get);
                    log("ESPACENET URL: " + redirect + " STATUS: " + get.getStatusCode());
                    if(get.getStatusCode() == HttpStatus.SC_OK) {
                      response.sendRedirect(redirect);
                      redirectsent = true;
                    }
                  } catch(Exception e) {
                    log("Exception connecting to espacenet");
                    redirectsent = false;
                  } finally {
                    get.releaseConnection();
                  }

                  // if we were not successful - use unvientio
                  if(!redirectsent) {
                    String univentiourl = UniventioPDFGateway.getUniventioLink(authCode,patNum,kindCode);
                    GetMethod pdf_get = new GetMethod(univentiourl);
                    pdf_get.setFollowRedirects(false);
                    OutputStream responseStream  = null;
                    InputStream getmethodStream = null;
                    try {
                      client.executeMethod(pdf_get);
                      if(pdf_get.getStatusCode() == HttpStatus.SC_OK) {
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition","inline; filename=" + authCode+patNum+kindCode+".pdf");

                        responseStream  = response.getOutputStream();
                        getmethodStream = pdf_get.getResponseBodyAsStream();
                        readPDF(responseStream, getmethodStream);
                        redirectsent = true;
                        logUniventioCall(request, response);
                      }
                    }
                    finally
                    {
                      try
                      {
                        if(responseStream != null)
                        {
                          responseStream.close();
                        }
                      }
                      catch(IOException e)
                      {
                      }

                      try
                      {
                        if(getmethodStream != null)
                        {
                          getmethodStream.close();
                        }
                      }
                      catch(IOException e)
                      {
                      }

                      try
                      {
                      	if(pdf_get != null)
                      	{
                      		pdf_get.releaseConnection();
						}
					  }
					  catch(Exception e)
					  {
					  }
                    }
                  }
                } // if("EP".equalsIgnoreCase(authCode))
                else
                {
                  // US Patent
                	Pat2PdfCreator pdfcreator = new Pat2PdfCreator();
                	pdfcreator.init();
                	ByteArrayOutputStream baos = null;
                	ServletOutputStream out = null;
                  	try
                  	{
                  		baos = new ByteArrayOutputStream();
                  		if(pdfcreator.createPatentPdf(patNum,baos))
                  		{
                  		    response.setContentType("application/pdf");
                  		    response.setHeader("Content-Disposition","inline; filename=PatUS"+patNum+".pdf");
                  		    response.setContentLength(baos.size());
                  			try
                  			{
                  				out = response.getOutputStream();
                  		    	baos.writeTo(out);
                  		    	out.flush();
							}
							finally
							{
								if(out != null)
								{
									try
									{
										out.close();
									}
									catch(Exception e)
									{
									}
								}
							}
                  		}
                  		else
                  		{
                  		    response.sendRedirect(redirect);
                  		}
			  		}
			  		finally
			  		{
						if(baos != null)
						{
							try
							{
								baos.close();
							}
							catch(Exception e1)
							{
							}
						}
					}
                }
             }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            if(redirect != null)
            {
              response.sendRedirect(redirect);
            }
        }
        catch(Exception e)
        {
            log("PatentPDF", e);
        }
    }

    private void readPDF(OutputStream response, InputStream method) throws IOException
    {
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      try {
          bis = new BufferedInputStream(method);
          bos = new BufferedOutputStream(response);
          byte[] buff = new byte[1024];
          int bytesRead;

          while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
              //System.out.print("#");
              bos.write(buff, 0, bytesRead);
          }
      }
      catch(IOException e)
      {
        throw e;
      }
      finally
      {
        if(bis != null)
        {
		  	try
	  	  	{
          		bis.close();
	  		}
	  		catch(Exception e)
	  		{
			}
        }

        if(bos != null)
        {
			try
			{
          		bos.close();
			}
			catch(Exception e)
			{
			}
        }
      }
    }

    private void logUniventioCall(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception
    {
        long end, start = System.currentTimeMillis();

        LogClient logClient;
        logClient = new LogClient(logServiceURL);

        Enumeration paraNames = request.getParameterNames();
        Hashtable ht = new Hashtable();

        while (paraNames.hasMoreElements() )
        {
          String key = (String) paraNames.nextElement();
          ht.put(key, request.getParameter(key) );
        }

        String patNum = request.getParameter("pn");
        String authCode = request.getParameter("ac");
        String kindCode = request.getParameter("kc");
        ht.put("univentio_download", authCode+patNum+kindCode);
        logClient.setappdata(ht);

        // populate mandatory fields for CLF construction
        logClient.setdate(start);
        logClient.setbegin_time(start);
        end = System.currentTimeMillis();
        logClient.setend_time(end);
        logClient.setresponse_time(end-start);

        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null)
        {
        	ipAddress = request.getRemoteAddr();
        }
        logClient.setHost(ipAddress);
        logClient.setrfc931("-");
        logClient.setusername("-");
        logClient.setcust_id(0);
        logClient.setsid("0");
        logClient.setuser_agent(request.getHeader("User-Agent"));
        logClient.setHTTPmethod(request.getMethod());
        logClient.setreferrer(request.getHeader("referer"));
        logClient.seturi_stem(request.getRequestURI());
        logClient.seturi_query(request.getQueryString());
        logClient.setstatuscode(HttpServletResponse.SC_OK);
        logClient.setrid(new GUID().toString());
        logClient.setappid("EngVillage2");
        logClient.setappdata(ht);

        logClient.sendit();
        log("Sent Univentio PDF Log Event.");
    }
}
