package org.ei.struts.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkService;
import org.ei.struts.framework.service.IFrameworkServiceFactory;
/**
 * An abstract Action class that all store front action classes should
 * extend.
 */
abstract public class FrameworkBaseForm extends ActionForm {

	protected static Log log = LogFactory.getLog(FrameworkBaseForm.class);

	protected IFrameworkService getFrameworkService()
		{
		
		IFrameworkServiceFactory factory = null;	
		IFrameworkService frameworkservice = null;

		try {
			factory = (IFrameworkServiceFactory) getServlet().getServletContext().getAttribute(Constants.SERVICE_FACTORY_KEY);
			frameworkservice = factory.createService();
		}
		catch(DatastoreException de)
		{
		}

		return frameworkservice;
	}

}
