package org.ei.struts.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.sql.DataSource;

import org.apache.struts.action.ActionServlet;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkService;
import org.ei.struts.framework.service.IFrameworkServiceFactory;
/**
 * Extend the Struts ActionServlet to perform your own special
 * initialization.
 */
public class ExtendedActionServlet extends ActionServlet {

	protected IFrameworkService getFrameworkService()  throws DatastoreException {
	  IFrameworkServiceFactory factory = (IFrameworkServiceFactory) this.getServletContext().getAttribute( Constants.SERVICE_FACTORY_KEY );
	  return factory.createService();
	}

    public void init() throws ServletException {
        // Make sure to always call the super's init() first
        super.init();

        getServletContext().setAttribute(Constants.SERVLET_STARTTIME_KEY, new Long(System.currentTimeMillis()));

    	EmetricsServiceImpl serviceImpl = null;
        Connection conn = null;
		String currentpool = null;
    	try {
    		serviceImpl = (EmetricsServiceImpl) getFrameworkService();

    		Date lastupdate = serviceImpl.getLastUpdate();
    		this.getServletContext().setAttribute(Constants.LASTUPDATE,lastupdate);

            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);

			currentpool = Constants.EMETRICS_AUTH_DBCP_POOL;
            DataSource ds = (DataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
            conn = ds.getConnection();
            
        }
        catch (NamingException ne) {
        	log.error(ne.getMessage());
			throw new UnavailableException("Service is currently unavailable");
        }
        catch (SQLException sqle) {
			log.error(sqle.getMessage());
			throw new UnavailableException(currentpool + ": A Database connection is currently unavailable");
        }
    	catch(DatastoreException de) {
			log.error(de.getMessage());
    		throw new UnavailableException("Datastore error starting org.ei.struts.emetrics.service.EmetricsServiceImpl init().");
    	}
        finally {
            try {
                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("init ", e);
            }
        }
    }
}
