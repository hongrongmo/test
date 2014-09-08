package org.ei.struts.emetrics.service;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkService;
import org.ei.struts.framework.service.IFrameworkServiceFactory;
import org.ei.struts.emetrics.Constants;

/**
 * A factory for creating emetrics Service Implementations. The specific
 * service to instantiate is determined from the initialization parameter
 * of the ServiceContext. Otherwise, a default implementation is used.
 * @see org.ei.struts.emetrics.service.EmetricsDebugServiceImpl
 */
public class EmetricsServiceFactory implements IFrameworkServiceFactory, PlugIn{
  // Hold onto the servlet for the destroy method
  private ActionServlet servlet = null;

  public IFrameworkService createService() throws DatastoreException {
    return EmetricsServiceImpl.getInstance();
  }

  public void init(ActionServlet servlet, ModuleConfig config)
    throws ServletException{
    // Store the servlet for later
    this.servlet = servlet;

    /* Store the factory for the application. Any Emetrics service factory
     * must either store itself in the ServletContext at this key, or extend
     * this class and don't override this method. The Emetrics application
     * assumes that a factory class that implements the IStorefrtonServiceFactory
     * is stored at the proper key in the ServletContext.
     */
    servlet.getServletContext().setAttribute( Constants.SERVICE_FACTORY_KEY, this );
  }

  public void destroy(){
    // Do nothing for now
  }
}