package org.ei.evtools.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class WebAppInitializer implements WebApplicationInitializer {

	private static Logger logger = Logger.getLogger(WebAppInitializer.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException { 
		
		logger.info("Application initialization started....");
		
		WebApplicationContext rootContext = createRootContext(servletContext);
		configureSpringMvc(servletContext, rootContext);
		
		logger.info("Application initialization end....");
	} 
	
	private WebApplicationContext createRootContext(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.getEnvironment().setActiveProfiles("dev");
		rootContext.register(CoreConfig.class, SecurityConfig.class, DataSourceConfig.class ,JpaApplicationConfig.class, AmazonServiceConfig.class);
		rootContext.refresh();
		servletContext.addListener(new ContextLoaderListener(rootContext));
		servletContext.setInitParameter("defaultHtmlEscape", "true");
		logger.info("Root context successfully created.");
		return rootContext;
	}
	
	private void configureSpringMvc(ServletContext servletContext,WebApplicationContext rootContext) {
		AnnotationConfigWebApplicationContext mvcctx = new AnnotationConfigWebApplicationContext();  
		mvcctx.register(MvcConfiguration.class);  
		mvcctx.setServletContext(servletContext);    
		mvcctx.setParent(rootContext);
		Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(mvcctx));  
        servlet.addMapping("/");  
        servlet.setLoadOnStartup(1);
        logger.info("Spring MVC configuration .");
    }

}
