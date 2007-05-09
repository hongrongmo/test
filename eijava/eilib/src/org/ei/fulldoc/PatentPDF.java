package org.ei.fulldoc;



import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import org.ei.controller.ControllerClient;
import org.ei.logging.LogClient;
import org.ei.util.*;

public class PatentPDF extends HttpServlet
{



    // Logging variables
    private LogClient logClient;
    private String logServiceURL;
    private String appName;
    private String defaultURL;

    public void init()
        throws ServletException
    {
        ServletConfig config = getServletConfig();

        logServiceURL = config.getInitParameter("logURL");
        appName       = config.getInitParameter("appName");
        if ((logServiceURL != null) && (appName != null) ) {
            this.logClient = new LogClient(logServiceURL);
        }

    }


    public void service(HttpServletRequest request,
                HttpServletResponse response)
        throws IOException, ServletException
    {


        HttpMethod method = null;
        String redirect = null;
        try {

            String patNum = request.getParameter("pn");
            String authCode = request.getParameter("ac");
            String kindCode = request.getParameter("kc");
            String cType = request.getParameter("type");
            String pKey = request.getParameter("key");
            redirect = request.getParameter("rurl");

            if (redirect == null) {
                redirect = getRedirectURL(authCode,patNum,kindCode).url;
            }
            //System.out.println("Prams"+redirect+authCode+patNum+kindCode+pKey);


            if(patNum != null && authCode != null && kindCode != null) {

                if(UniventioPDFGateway.isValidKey(authCode,patNum,kindCode,pKey)){
                    if( cType.equalsIgnoreCase("PDF"))   {
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition","inline; filename=" + authCode+patNum+kindCode+".pdf");
                    }
                    else if (cType.equalsIgnoreCase("SAVEPDF")){
                            response.setContentType("application/pdf");
                            StringBuffer bufHeader = new StringBuffer("attachments; ");
                            bufHeader.append("filename=").append(authCode+patNum+kindCode).append(".pdf");
                            response.setHeader("Content-disposition", bufHeader.toString());
                    }
                    String url = UniventioPDFGateway.getUniventioLink(authCode,patNum,kindCode);
                    method = new GetMethod(url);
                    HttpClient cl = new HttpClient();

                    cl.setConnectionTimeout(3000);
                    cl.setTimeout(60000);

                    //long begin = System.currentTimeMillis();

                    if (cl.executeMethod(method)  == HttpServletResponse.SC_OK) {
                        //System.out.println("Found PDF...");
                        readPDF(response,method);

                    }
                    else
                    {
                        response.sendRedirect(redirect);
                    }

                }
                else {
                  // System.out.println("Invalid:"+redirect);
                   response.sendRedirect(redirect);
                }
             }


        }
        catch(IOException e)  {
            e.printStackTrace();
            response.sendRedirect(redirect);


        }
        catch(Exception e)  {

            log("PatentPDF", e);

        }
        finally {
            method.releaseConnection();
        }

    }

     private void readPDF(HttpServletResponse response,HttpMethod method) throws IOException
     {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{

            bis = new BufferedInputStream(method.getResponseBodyAsStream());
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;

            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                //System.out.print("#");
                bos.write(buff, 0, bytesRead);
            }


        }catch(final IOException e) {
                   throw e;
        } finally {
           if (bis != null)
               bis.close();
           if (bos != null)
               bos.close();
        }

      }

      private LinkInfo getRedirectURL(String acode,String patnum,String kcode)
      {
           LinkInfo linkInfo = new LinkInfo();
           StringBuffer buf = new StringBuffer();
           buf.append("http://v3.espacenet.com/textdoc?DB=EPODOC&IDX=");
           buf.append(acode.trim());
           while(patnum.length() < 7)
           {
              patnum = "0"+patnum;
           }
           buf.append(patnum.trim());
           linkInfo.url = buf.toString();
           return linkInfo;
       }



}
