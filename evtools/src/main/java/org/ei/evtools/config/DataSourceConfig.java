package org.ei.evtools.config;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.ei.evtools.exception.DatabaseConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig {
	
	static Logger logger = Logger.getLogger(DataSourceConfig.class);
	
	@Autowired
	private Environment environment; 
	
	@Bean(name="evDataSource")
    public DataSource dataSource() throws DatabaseConfigException {
       
		 DriverManagerDataSource dataSource;

		 try {
			 String key = environment.getProperty("ENVIRONMENT")+".jdbc.url";
			 String jdbcURL = environment.getProperty(key);
			 logger.info("jdbc url key='"+key+"', value='"+jdbcURL+"'");
			 dataSource = new DriverManagerDataSource();
			 dataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
			 dataSource.setUsername(environment.getProperty("jdbc.username"));
			 dataSource.setPassword(environment.getProperty("jdbc.password"));
			 dataSource.setUrl(jdbcURL);
			 logger.info("Data Source bean has been successfully created!");
			
			} catch (Exception e) {
				logger.error("Data Source bean creation failed, exception occured: "+e.getMessage());
				throw new DatabaseConfigException("Data Source Creation failed!", e);
			}
		
		
		
		/*DataSource dataSource;
		try {
			JndiTemplate template = new JndiTemplate();
			dataSource = (DataSource) template.lookup("java:comp/env/jdbc/session");
		} catch (NamingException e) {
			throw new DatabaseConfigException("JNDI lookup failed", e);
		}*/
        return dataSource;
    }
	
	

}
