package org.ei.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.domain.DatabaseConfig;
import org.ei.domain.DriverConfig;
import org.ei.domain.FastSearchControl;
import org.ei.books.BookSearchControl;
import org.ei.thesaurus.ThesaurusSearchControl;
import org.ei.email.EMail;



public class ConfigService extends HttpServlet
{
    private static RuntimeProperties props;

    private static ServletContext context;

    private static final String SESSION_POOL = "session";
    private static final String SEARCH_POOL = "search";


    public void init()
        throws ServletException
    {

        context = getServletContext();

        try
        {
			System.out.println("Config service starting up. Configuring system...");
            String propsConf = context.getRealPath("/WEB-INF/Runtime.properties");
            DatabaseConfig.getInstance(DriverConfig.getDriverTable());
            props = new RuntimeProperties(propsConf);
            String mainIndexBaseURL = props.getProperty("FastBaseUrl");
            String thesaurusIndexBaseURL = props.getProperty("ThesaurusBaseURL");
            String mailHost = props.getProperty("mail.smtp.host");
            boolean emailDebug = (new Boolean(props.getProperty("debug"))).booleanValue();
            System.out.println("Setting main index base url to:"+mainIndexBaseURL);
            FastSearchControl.BASE_URL = mainIndexBaseURL;
            System.out.println("Setting book index base url to:"+mainIndexBaseURL);
            BookSearchControl.BASE_URL = mainIndexBaseURL;
            System.out.println("Setting thesaurus index base url to:"+thesaurusIndexBaseURL);
            ThesaurusSearchControl.BASE_URL = thesaurusIndexBaseURL;
            System.out.println("Setting mailhost:"+mailHost);
            EMail.MAIL_HOST = mailHost;
            System.out.println("Setting mail debug:"+emailDebug);
            EMail.DEBUG = emailDebug;
        	System.out.println("System configuration complete.");
        }
        catch(Exception e)
        {
            log("Init Error:", e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    public static RuntimeProperties getRuntimeProperties()
    {
        return props;
    }



    public static String getConfigPath(String configName)
    {
        String configURL = null;

        try
        {
            configURL = context.getRealPath("/WEB-INF/"+configName);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return configURL;
    }



    public void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        ServletOutputStream out = response.getOutputStream();
        BufferedReader reader = new BufferedReader(new FileReader(getConfigPath(request.getParameter("configName"))));
        try
        {
            String s = null;
            while((s = reader.readLine()) != null)
            {
                out.print(s);
            }
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
    }


}
