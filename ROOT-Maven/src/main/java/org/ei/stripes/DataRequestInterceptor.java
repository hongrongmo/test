package org.ei.stripes;

import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.DataRequest;
import org.ei.controller.DataResponse;
import org.ei.controller.OutputPrinter;
import org.ei.controller.content.ContentDescriptor;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.results.SearchResultsAction;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.HttpRequestUtil;
import org.perf4j.log4j.Log4JStopWatch;

/**
 * This class is a Stripes Interceptor that is supposed to determine if the current Action requires XML from the engvillage application.
 *
 * It examines the current attached ActionBean to see if it implements the IBizBean interface which means it does require data retrieval.
 *
 * @author harovetm
 *
 */
@Intercepts(LifecycleStage.EventHandling)
public class DataRequestInterceptor implements Interceptor {

    private Logger log4j = Logger.getLogger(DataRequestInterceptor.class);

    @Override
    public Resolution intercept(ExecutionContext executioncontext) throws Exception {
		Log4JStopWatch stopwatch = new Log4JStopWatch("Interceptor.DataRequest");

		EVActionBeanContext context = (EVActionBeanContext) executioncontext.getActionBeanContext();
        EVActionBean actionbean = (EVActionBean) executioncontext.getActionBean();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        log4j.info("[" + HttpRequestUtil.getIP(request) + "] Entered DataRequestInterceptor");
        // Validate ActionBean is not null
        if (actionbean == null) {
            throw new RuntimeException("No ActionBean is attached to request!");
        }
        UserSession usersession = context.getUserSession();

        // *****************************************************
        // Check to see if the current action requires an XML
        // feed. Retrieve from the data (model) layer if so.
        // *****************************************************
        if (actionbean instanceof IBizBean) {
            String dataCacheDir = EVProperties.getProperty(ApplicationProperties.DATA_CACHE_DIR);

            DataRequest dataRequest;
            try {
                dataRequest = context.buildDataRequest(usersession, request);
            } catch (UnknownHostException e) {
                throw new ServiceException(SystemErrorCodes.DATA_SERVICE_CONNECTION_ERROR, "Unable to build data request!", e);
            }
            // Add a "legacy" ContentDescriptor object. This is mainly to keep
            // Usage logging happy.
            ContentDescriptor cd = new ContentDescriptor("LEGACY");
            cd.setContentID(context.getRequest().getParameter("CID"));
            // cd.setDataSourceURL(((IBizBean) actionbean).getXMLPath());
            // cd.setDisplayURL(((IBizBean) actionbean).getXSLPath());
            dataRequest.setContentDescriptor(cd);

            DataResponse dataResponse = null;

            // We should be able to get rid of this at some point but
            // for now it's used heavily in building the DataResponse
            OutputPrinter printer;
            try {
                printer = context.buildPrinter(request, response, getAppendSessionProperty());
            } catch (Exception e) {
                EVExceptionHandler.logException("Unable to create OutputPrinter", e, request);
                throw new RuntimeException("Unable to create OutputPrinter", e);
            }

            // Inject the XML
            log4j.info("[" + HttpRequestUtil.getIP(request) + "] Injecting XML into actionbean: " + actionbean.getClass().getName());
            // Inject the XML via the DataRequest object. We embed it here
            // and the DataResponse processing picks it up for injection
            dataRequest.setBizBean((IBizBean) actionbean);

            dataResponse = context.getDataResponseForActionBean(request, dataCacheDir, printer, dataRequest, ((IBizBean) actionbean).getXMLPath());
            // If we're redirecting, stop processing!
            if (!GenericValidator.isBlankOrNull(dataResponse.getRedirectURL())) {
                log4j.info("[" + HttpRequestUtil.getIP(request) + "] Redirecting after XML retrieval.  URL: " + dataResponse.getRedirectURL());
                return new RedirectResolution(dataResponse.getRedirectURL(), false);
            } else if (dataResponse.isError()) {
                return ((IBizBean) actionbean).handleException(dataResponse.getErrorXml());
            }

            // Add the DataResponse info to the LogEntry
            context.getLogEntry().addDataResponse(dataResponse);

        }

        if (actionbean instanceof SearchResultsAction) {
            actionbean.isActionBeanInstance = "true";
        }

        stopwatch.stop();

        // Continue on with request
        return executioncontext.proceed();
    }

    private boolean getAppendSessionProperty() {
        return Boolean.parseBoolean(EVProperties.getProperty(ApplicationProperties.APPEND_SESSION));
    }

}
