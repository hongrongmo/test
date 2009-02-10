package org.ei.fulldoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PatentPDF extends HttpServlet
{
    private HttpClient client;

    public void init()
        throws ServletException
    {
      MultiThreadedHttpConnectionManager connectionManager =  new MultiThreadedHttpConnectionManager();

      client = new HttpClient(connectionManager);
      client.setConnectionTimeout(3000);
    }

    public void service(HttpServletRequest request,
                HttpServletResponse response)
        throws IOException, ServletException
    {
        String redirect = null;
        try {

            String patNum = request.getParameter("pn");
            String authCode = request.getParameter("ac");
            String kindCode = request.getParameter("kc");
            //String cType = request.getParameter("type");
            redirect = request.getParameter("rurl");

            if(patNum != null && authCode != null && kindCode != null) {

                if("EP".equalsIgnoreCase(authCode)) {
                  // send to Espacenet site
                  redirect = "http://v3.espacenet.com/publicationDetails/originalDocument?CC=" + authCode + "&NR=" + patNum + kindCode + "&KC=" + kindCode + "&FT=D";

                  boolean redirectsent = false;

                  GetMethod get = new GetMethod(redirect);
                  // test to see if Espacenet site is up/page exists
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
                    try {
                      client.executeMethod(pdf_get);
                      if(pdf_get.getStatusCode() == HttpStatus.SC_OK) {
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition","inline; filename=" + authCode+patNum+kindCode+".pdf");
                        readPDF(response.getOutputStream(), pdf_get.getResponseBodyAsStream());
                        redirectsent = true;
                      }
                    } finally {
                      pdf_get.releaseConnection();
                    }
                  }

                } // if("EP".equalsIgnoreCase(authCode))
                else {
                  // US Patent
                  Pat2PdfCreator pdfcreator = new Pat2PdfCreator();
                  pdfcreator.init();
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  if(pdfcreator.createPatentPdf(patNum,baos))
                  {
                      response.setContentType("application/pdf");
                      response.setHeader("Content-Disposition","inline; filename=PatUS"+patNum+".pdf");

                      response.setContentLength(baos.size());

                      ServletOutputStream out = response.getOutputStream();
                      baos.writeTo(out);
                      out.flush();
                  }
                  else {
                      response.sendRedirect(redirect);
                  }
                }
             }
        }
        catch(IOException e)  {
            e.printStackTrace();
            if(redirect != null) {
              response.sendRedirect(redirect);
            }
        }
        catch(Exception e)  {
            log("PatentPDF", e);
        }
        finally {

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
      } catch(IOException e) {
        throw e;
      } finally {
        if (bis != null) {
          bis.close();
        }
        if (bos != null) {
          bos.close();
        }
      }
    }

}
