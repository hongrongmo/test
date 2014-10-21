package org.ei.evtools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Configuration
@Import({JpaApplicationConfig.class})
public class CoreConfig {
	
	@Bean
	public EVSpringApplicationContext createEVSpringApplicationContext(){
		return new EVSpringApplicationContext();
	}
}
