package test.org.ei.action;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sourceforge.stripes.controller.DispatcherServlet;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.mock.MockServletContext;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;


public abstract class BaseActionTest extends TestNG{
    private static Logger log4j = Logger.getLogger(BaseActionTest.class);

    protected MockServletContext mockcontext = new MockServletContext("test");

    @BeforeClass
    public static void setupClass() throws Exception {
        try {
            // Create initial context
            // NOTE that this implementation depends
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
            Context ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");

            // Construct search DataSource
            OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
            ds = new OracleConnectionPoolDataSource();
            ds.setURL("jdbc:oracle:thin:@138.12.85.144:1521:EI");
            ds.setUser("AP_EV_SEARCH");
            ds.setPassword("ei3it");

            ic.bind("java:comp/env/jdbc/search", ds);

            // Construct session DataSource
            ds = new OracleConnectionPoolDataSource();
            ds.setURL("jdbc:oracle:thin:@138.12.85.144:1521:EI");
            ds.setUser("AP_EV_SESSION");
            ds.setPassword("ei3it");

            ic.bind("java:comp/env/jdbc/session", ds);

        } catch (NamingException e) {
            log4j.error("Unable to setup JNDI connection", e);
        }

    }

    @BeforeSuite
    public void beforeSuite() throws Exception {

        // Add the Stripes Filter
        Map<String, String> filterParams = new HashMap<String, String>();
        filterParams.put("LocalePicker.Locales", "en_US:UTF-8");
        filterParams.put("ActionBeanContext.Class", "org.ei.test.action.MockEVActionBeanContext");
        filterParams.put("Validation.InvokeValidateWhenErrorsExist", "true");
        filterParams.put("ActionResolver.Packages", "org.ei.stripes.action");
        filterParams.put("ExceptionHandler.Class", "org.ei.stripes.exception.EVExceptionHandler");
        mockcontext.addFilter(StripesFilter.class, "StripesFilter", filterParams);


        // Add the Stripes Dispatcher
        mockcontext.setServlet(DispatcherServlet.class, "StripesDispatcher", null);

        //intialize the EV Properties object
        EVProperties.getInstance();
        EVProperties.setStartup(System.currentTimeMillis());
        EVProperties.setApplicationProperties(EVProperties.getApplicationProperties());


    }


}
