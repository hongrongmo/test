package org.ei.fulldoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PatentPDF extends HttpServlet
{
    public void init()
        throws ServletException
    {
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
                  redirect = "http://v3.espacenet.com/publicationDetails/originalDocument?CC=" + authCode + "&NR=" + patNum + kindCode + "&KC=" + kindCode + "&FT=D";
                  response.sendRedirect(redirect);
                }
                else {
                  Pat2PdfCreator pdfcreator = new Pat2PdfCreator();
                  pdfcreator.init();
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  if(pdfcreator.createPatentPdf(patNum,baos))
                  {
                      response.setHeader("Expires", "0");
                      response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
                      response.setHeader("Pragma", "public");
                      response.setContentType("application/pdf");
                      response.setHeader("Content-Disposition","inline; filename=PatUS"+patNum+".pdf");

                      //readPDF(baos, new File(pdfcreator.getPatentPdfFilename(patNum)));
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

    private void readPDF(OutputStream baos, File pdffile) throws IOException
    {
        InputStream pdfstream = new FileInputStream(pdffile);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            bis = new BufferedInputStream(pdfstream);
            byte[] buff = new byte[1024];
            int bytesRead;

            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                //System.out.print("#");
                baos.write(buff, 0, bytesRead);
            }
        } catch(final IOException e) {
          throw e;
        } finally {
          if (bis != null) {
            bis.close();
          }
        }
    }
}
