package org.ei.fulldoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
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
                  if(pdfcreator.createPatentPdf(patNum))
                  {
                      response.setContentType("application/pdf");
                      response.setHeader("Content-Disposition","inline; filename=Pat"+patNum+".pdf");
                      readPDF(response, new File(pdfcreator.getPatentPdfFilename(patNum)));
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

    private void readPDF(HttpServletResponse response,File pdffile) throws IOException
    {
      readPDF(response, new FileInputStream(pdffile));
    }

    private void readPDF(HttpServletResponse response,InputStream pdfstream) throws IOException
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            bis = new BufferedInputStream(pdfstream);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;

            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                //System.out.print("#");
                bos.write(buff, 0, bytesRead);
            }
        } catch(final IOException e) {
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
