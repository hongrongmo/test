package org.ei.stripes;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.controller.content.ContentConfig;
import org.ei.stripes.action.ControllerAction;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.util.HttpRequestUtil;
import org.perf4j.log4j.Log4JStopWatch;

/**
 * This class is a Stripes Interceptor that is supposed to look at the incoming URL and determine whether or not it's a legacy URL that should be re-directed to
 * a new URL.
 * 
 * There is an existing ContentConfig.xml file that is currently used for a similar purpose but we can't use it here because it can use a customer ID as one of
 * the keys for a lookup. We want to determine the re-direct status BEFORE having to authenticate the user!
 * 
 * @author harovetm
 * 
 */
@Intercepts(LifecycleStage.BindingAndValidation)
public class LegacyRedirectInterceptor implements Interceptor {

    private static Logger log4j = Logger.getLogger(LegacyRedirectInterceptor.class);

    @Override
    public Resolution intercept(ExecutionContext executioncontext) throws Exception {
		Log4JStopWatch stopwatch = new Log4JStopWatch("Interceptor.LegacyRedirect");

		HttpServletRequest request = executioncontext.getActionBeanContext().getRequest();
        log4j.info("[" + HttpRequestUtil.getIP(request) + "] Entered LegacyRedirectInterceptor");
        String CID = request.getParameter("CID");
        log4j.info("Incoming CID for legacy check: " + CID);

        EVActionBean actionbean = (EVActionBean) executioncontext.getActionBean();
        if (actionbean == null) {
            // Let the AuthInterceptor handle the missing mapping
            return executioncontext.proceed();
        }

        // Use the CID from the request to determine if this is legacy request
        ContentConfig contentconfig = EVProperties.getContentConfig();
        if (contentconfig == null) {
            throw new RuntimeException("No ContentConfig object could be found - check app initialization!");
        }

        // Only redirect when incoming URL is /controller/servlet/Controller which only
        // maps to ControllerAction
        if (actionbean != null && actionbean instanceof ControllerAction) {
            if (contentconfig.containsKey("legacy_" + CID)) {
                // Create redirect URL with query string
                String redirURL = contentconfig.getContentDescriptor("legacy_" + CID).getDisplayURL();
                String urlAfterHash = null;
                if (redirURL.contains("#init")) {
                	urlAfterHash =  redirURL.substring(redirURL.indexOf("#init"),  redirURL.length());
                	redirURL = redirURL.substring(0, redirURL.indexOf("#init"));
                }
                
                if (redirURL.contains("?")) {
                    redirURL += "&" + request.getQueryString();
                } else {
                    redirURL += "?" + request.getQueryString();
                }
                if(urlAfterHash!=null)redirURL+=urlAfterHash;
                
                log4j.info("[" + HttpRequestUtil.getIP(request) + "] Redirecting from legacy URL to: '" + redirURL + "'");
                return new RedirectResolution(redirURL);
            }
        }

        stopwatch.stop();
        
        // Continue on with request
        return executioncontext.proceed();
    }

}
