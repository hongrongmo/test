package org.ei.stripes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;


@Intercepts(LifecycleStage.BindingAndValidation)
public class CookiePageInterceptor implements Interceptor {
	
    private org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(CookiePageInterceptor.class);


    @Override
	public Resolution intercept(ExecutionContext context) throws Exception {
		EVActionBeanContext evcontext = (EVActionBeanContext) context.getActionBeanContext();
		HttpServletRequest request = evcontext.getRequest();
		HttpServletResponse response= evcontext.getResponse();
		String ip = request.getHeader("x-forwarded-for");
		
		Cookie[] cookies = request.getCookies();
		
		/*System.out.println("--------------Cookies in request----------------");
		for(int i=0; i< cookies.length; i++){ 
			System.out.println(cookies[i].getName()+"-------------------"+cookies[i].getValue());
		}*/
		
		if("cookie".equals(request.getParameter("CID"))){
			return new ForwardResolution("/WEB-INF/useOfCookies.jsp");
		}
		return context.proceed();
		
	}
	
}
