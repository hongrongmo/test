/*
 * $Author:   johna  $
 * $Revision:   1.2  $
 * $Date:   Apr 14 2006 14:51:36  $
 *
 */


package org.ei.struts.emetrics.businessobjects.reports;


import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.PlugInConfig;
import org.ei.struts.emetrics.Constants;

import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkServiceFactory;

/**
 * <p><strong>OdmgReportsPlugIn</strong> initializes and finalizes the
 * persistent storage of Reports information for the Struts
 * EMetrics Application, using an in-Odmg Reports backed by an
 * XML file.</p>
 *
 * <p><strong>IMPLEMENTATION WARNING</strong> - If this web application is run
 * from a WAR file, or in another environment where reading and writing of the
 * web application resource is impossible, the initial contents will be copied
 * to a file in the web application temporary directory provided by the
 * container.  This is for demonstration purposes only - you should
 * <strong>NOT</strong> assume that files written here will survive a restart
 * of your servlet container.</p>
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.2  $ $Date:   Apr 14 2006 14:51:36  $
 */

//implements PlugIn
public final class ReportsPlugIn implements PlugIn {

    // ----------------------------------------------------- Instance Variables
    /**
     * Logging output for this plug in instance.
     */
    private Log log = LogFactory.getLog(this.getClass());

    protected PlugInConfig currentPlugInConfigObject;

    private Reports reports = null;


    // --------------------------------------------------------- PlugIn Methods


    /**
     * Gracefully shut down these reports, releasing any resources
     * that were allocated at initialization.
     */
    public void destroy() {

        log.info("Finalizing Castor reports plug in");

        if (reports != null) {
            try {
                reports.close();
            } catch (Exception e) {
                log.error("Closing Odmg reports", e);
            }
        }

        reports = null;
    }


    /**
     * Initialize and load our initial reports from persistent storage.
     *
     * @param servlet The ActionServlet for this web application
     * @param config The ApplicationConfig for our owning sub-application
     *
     * @exception ServletException if we cannot configure ourselves correctly
     */
    public void init(ActionServlet servlet, ModuleConfig config)
        throws ServletException  {

        Map reportsProperties = getPlugInConfigProperties(servlet, config);

        try {
      	  	IFrameworkServiceFactory factory = (IFrameworkServiceFactory) servlet.getServletContext().getAttribute( Constants.SERVICE_FACTORY_KEY );
      	  	EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) factory.createService();

      	  	String dtdfile = (String) reportsProperties.get("dtdfile");
            String path = (String) reportsProperties.get("pathname");

            reports = new AllReports();
            reports.setDtdfile(dtdfile);
            reports.setPathname(path);
            reports.open(servlet.getServletContext());

            serviceImpl.setReports(reports.getReports());

        } catch (DatastoreException e) {
            log.error("Opening persistance database reports", e);
            throw new ServletException("Cannot open database reports ", e);
        } catch (Exception e) {
            log.error("Opening persistance database reports", e);
            throw new ServletException("Cannot open database reports ", e);
        }

    }


    /**
    * Opens the database and prepares it for transactions
    */

    /**
     * Find original properties set in the Struts PlugInConfig object.
     * First, we need to find the index of this plugin. Then we retrieve the array of configs
     * and then the object for this plugin.
     * @param servlet ActionServlet that is managing all the modules
     *  in this web application.
     * @param config ModuleConfig for the module with which
     *  this plug in is associated.
     *
     * @exception ServletException if this <code>PlugIn</code> cannot
     *  be successfully initialized.
     */
    protected Map getPlugInConfigProperties(
        ActionServlet servlet,
        ModuleConfig config)
        throws ServletException {
        return currentPlugInConfigObject.getProperties();
    }

    /**
     * Method used by the ActionServlet initializing this plugin.
     * Set the plugin config object read from module config.
     * @param plugInConfigObject PlugInConfig.
     */
    public void setCurrentPlugInConfigObject(PlugInConfig plugInConfigObject) {
        this.currentPlugInConfigObject = plugInConfigObject;
    }
}
