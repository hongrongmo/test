package org.ei.stripes;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.perf4j.log4j.Log4JStopWatch;

/**
 * This intercepter is used to propagate Global Errors when redirecting Stripes doesn't allow this and you can only pass errors as messages. This will allow you
 * to add a global error and it to be shown on another action
 *
 * first it will catch the redirection resolution and add the global errors that would be removed back into the actionBeanContext. It will then let the context
 * to proceed
 *
 * If the next resolution is a fowarding one, the errors will be there for the taking. If is another redirecting resolution it will continue to propagate it
 *
 * @author robbinrs
 *
 */
@Intercepts(LifecycleStage.EventHandling)
public class ErrorMessageInterceptor implements Interceptor {

    public static final String CTX_KEY = "VALIDATION_ERRORS";

    @Override
    public Resolution intercept(ExecutionContext ctx) throws Exception {
		Log4JStopWatch stopwatch = new Log4JStopWatch("Interceptor.ErrorMessage");

        ValidationErrors errors = (ValidationErrors) ctx.getActionBeanContext().getRequest().getAttribute(CTX_KEY);
        if (errors != null) {
            ctx.getActionBeanContext().setValidationErrors(errors);
            ctx.getActionBeanContext().getRequest().removeAttribute(CTX_KEY);
        }

        Resolution resolution = ctx.proceed();

        errors = ctx.getActionBeanContext().getValidationErrors();
        if (errors.size() > 0 && resolution instanceof RedirectResolution) {
            FlashScope scope = FlashScope.getCurrent(ctx.getActionBeanContext().getRequest(), true);
            scope.put(CTX_KEY, errors);
        }

        stopwatch.stop();
        
        return resolution;
    }
}
