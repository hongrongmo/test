package org.ei.evtools.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Configuration
@Import({DataSourceConfig.class})
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableMBeanExport
public class JpaApplicationConfig {
	
	private static Logger logger = Logger.getLogger(JpaApplicationConfig.class);
	
	@Autowired
	Environment environment;

	@Autowired
	DataSource evDataSource;
	
	public DataSource getDataSource() {
		return evDataSource;
	}

	public void setDataSource(DataSource evDataSource) {
		this.evDataSource = evDataSource;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setJpaProperties(getJpaProperties());
		em.setDataSource(getDataSource());
		em.setJpaVendorAdapter(jpaVendorAdapter());
		em.setLoadTimeWeaver(loadTimeWeaver());
		em.setPackagesToScan(getProperty("domain.packages.to.scan"), getProperty("repositories.packages.to.scan"));
		em.afterPropertiesSet();
		logger.info("Enitity manager factory has been succesfully created.");
		return em.getObject();

	}
	
	
	@Bean
	public Properties getJpaProperties() {
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", getProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.show_sql",Boolean.valueOf(getProperty("hibernate.show_sql")));
		jpaProperties.put("hibernate.generate_statistics", true);
		return jpaProperties;
	}
	
	private String getProperty(String name) {
		return environment.getProperty(name);
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(Boolean.valueOf(getProperty("jpa.showSql")));
		adapter.setDatabasePlatform(getProperty("jpa.databasePlatform"));
		logger.info("JPA vendor adapter has been succfully created.");
		return adapter;
	}

	@Bean
	public LoadTimeWeaver loadTimeWeaver() {
		return new InstrumentationLoadTimeWeaver();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		logger.info("TransactionManager has been succfully created.");
		return txManager;
	}

	/**
	 * BeanPostProcessor that processes PersistenceUnit and PersistenceContext
	 * annotations, for injection of the corresponding JPA resources
	 * EntityManagerFactory and EntityManager
	 */
	public @Bean
	BeanPostProcessor persistenceAnnotationBeanPostProcessor() {
		return new PersistenceAnnotationBeanPostProcessor();
	}
		
}
