package org.ei.stripes;

import java.util.List;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;
/**
 * This intercepter is used to propagate Global Errors when redirecting 
 * Stripes doesn't allow this and you can only pass errors as messages. This will allow 
 * you to add a global error and it to be shown on another action
 * 
 * first it will catch the redirection resolution and add the global errors that would be removed back
 * into the actionBeanContext. It will then let the context to proceed
 * 
 * If the next resolution is a fowarding one, the errors will be there for the taking.
 * If is another redirecting resolution it will continue to propagate it
 * 
 * @author robbinrs
 *
 */
@Intercepts(LifecycleStage.EventHandling)
public class ErrorMessageInterceptor implements Interceptor {

  private static final String CTX_KEY = "MY_GLOBAL_ERRORS";

  @Override
  public Resolution intercept(ExecutionContext ctx) throws Exception {

    List<ValidationError> globalErrors = (List<ValidationError>) ctx.getActionBeanContext().getRequest().getAttribute(CTX_KEY);
    if(globalErrors != null) {
      for (ValidationError globalError : globalErrors) {
        ValidationErrors errors = ctx.getActionBeanContext().getValidationErrors();
        errors.addGlobalError(globalError);
      }
      ctx.getActionBeanContext().getRequest().removeAttribute(CTX_KEY);
    }

    Resolution resolution = ctx.proceed();

    ValidationErrors errors = ctx.getActionBeanContext().getValidationErrors();
    globalErrors = errors.get(ValidationErrors.GLOBAL_ERROR);
    if(globalErrors != null && globalErrors.size() > 0 && resolution instanceof RedirectResolution) {
      FlashScope scope = FlashScope.getCurrent(ctx.getActionBeanContext().getRequest(), true);
      scope.put(CTX_KEY, globalErrors);
    }

    return resolution;
  }
}
