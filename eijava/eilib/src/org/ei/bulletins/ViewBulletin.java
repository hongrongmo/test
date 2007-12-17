package org.ei.bulletins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewBulletin extends HttpServlet
{

    String bulletinFileLocation;
    String logURL;
    String appName;
    String authURL;

    // ControllerClient client;
    ServletConfig config;

    private static final String PAT_PATH = "bulletins/api/patent/";
    private static final String LIT_PATH = "bulletins/api/lit/";

    public void init() throws ServletException
    {
        config = getServletConfig();
        authURL = config.getInitParameter("authURL");
        bulletinFileLocation = config.getInitParameter("bulletinFileLocation");
        logURL = config.getInitParameter("logURL");
        appName = config.getInitParameter("appName");

    }

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {

        BufferedInputStream bis = null;
        BufferedOutputStream bout = null;
        File bulletinFile = null;
        Writer out = null;

        try
        {
            String cType = request.getParameter("cType");
            String db 	= request.getParameter("db");
            String fn 	= request.getParameter("fn");
            String yr 	= request.getParameter("yr");
            String cy 	= request.getParameter("cy");
            String tm 	= request.getParameter("tm");
            String sc 	= request.getParameter("sc");
            String link = null;

            BulletinSecurity bSecurity = new BulletinSecurity();

            if (fn != null && tm != null && sc != null)
            {
                if (!bSecurity.isExpired(tm) && bSecurity.isValidKey(sc, tm, fn))
                {
                    if (db.equals("1"))
                    {
                        link = LIT_PATH;
                    }
                    else if (db.equals("2"))
                    {
                        link = PAT_PATH;
                    }

                    String filename = null;
                    if (cType.equals("HTML"))
                    {
                        response.setContentType("text/html");
                        filename = fn + ".htm";
                    }
                    else if (cType.equals("PDF"))
                    {
                        response.setContentType("application/pdf");
                        filename = fn + ".pdf";
                    }
                    else if (cType.equals("ZIP"))
                    {
                        response.setContentType("application/zip");
                        StringBuffer bufHeader = new StringBuffer(
                                "attachments; ");
                        bufHeader.append("filename=").append(fn).append(".zip");
                        filename = fn + ".zip";
                        response.setHeader("Content-disposition", bufHeader
                                .toString());
                    }
                    else if (cType.equals("SAVEHTML"))
                    {
                        response.setContentType("text/html");
                        StringBuffer bufHeader = new StringBuffer(
                                "attachments; ");
                        bufHeader.append("filename=").append(fn).append(".htm");
                        response.setHeader("Content-disposition", bufHeader
                                .toString());
                        filename = fn + ".htm";
                    }
                    else if (cType.equals("SAVEPDF"))
                    {
                        response.setContentType("application/pdf");
                        StringBuffer bufHeader = new StringBuffer(
                                "attachments; ");
                        bufHeader.append("filename=").append(fn).append(".pdf");
                        response.setHeader("Content-disposition", bufHeader
                                .toString());
                        filename = fn + ".pdf";
                    }
                    String type = null;

                    if (cType.indexOf("HTML") > -1)
                    {
                        type = "html";
                    }
                    else if (cType.indexOf("PDF") > -1)
                    {
                        type = "pdf";
                    }
                    else
                    {
                        type = "graphic";
                    }

                    StringBuffer fullUrl = new StringBuffer();
                    String fullLink = fullUrl.append(bulletinFileLocation)
                            .append("/").append(link).append(yr).append("/")
                            .append(cy).append("/").append(type).append("/")
                            .append(filename).toString();
                    bulletinFile = new File(fullLink);

                    bout = new BufferedOutputStream(response.getOutputStream());
                    bis = new BufferedInputStream(new FileInputStream(
                            bulletinFile));

                    byte[] buff = new byte[1024];
                    int bytesRead;

                    while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                        bout.write(buff, 0, bytesRead);
                    }
                }
                else
                {
                    out = response.getWriter();
                    if (bSecurity.isExpired(tm))
                    {
                        out.write("<DISPLAY>Your link has expired, please refresh the search page and try again.</DISPLAY>");
                    } else if (!bSecurity.isValidKey(sc, tm, fn)) {
                        out.write("<DISPLAY>Invalid link</DISPLAY>");
                    }
                }
            }
            else
            {
                out = response.getWriter();
                out.write("<DISPLAY>Invalid link</DISPLAY>");
            }

        }
        catch (FileNotFoundException fe)
        {
            response.setContentType("text/html");
            String errorMessage = "<display>The file you required is not yet available, please check back later.</display>";
            byte[] errorByte = errorMessage.getBytes();

            for (int i = 0; i < errorByte.length; i++)
            {
                bout.write(errorByte[i]);
            }
        } catch (Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (Exception e)
                {
                    System.out.println("Error:" + e.getMessage());
                }
            }
            if (bout != null)
            {
                try
                {
                    bout.close();
                }
                catch (Exception e)
                {
                    System.out.println("Error:" + e.getMessage());
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e)
                {
                    System.out.println("Error:" + e.getMessage());
                }
            }
        }
    }
}
