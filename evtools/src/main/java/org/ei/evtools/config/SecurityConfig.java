package org.ei.evtools.config;

import org.apache.log4j.Logger;
import org.ei.evtools.db.services.EVToolsUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);
	
	@Autowired
	@Qualifier("userDetailsService")
	EVToolsUserDetailsService userDetailsService;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	  //auth.inMemoryAuthentication().withUser("mkamaraj").password("123456").roles("USER");
	  //auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
	  //auth.inMemoryAuthentication().withUser("dba").password("123456").roles("DBA");
	  auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .authorizeRequests()
        	.antMatchers("/static/css/**").permitAll()
        	.antMatchers("/static/js/**").permitAll()
        	.antMatchers("/static/images/**").permitAll()
        	.antMatchers("/world/**").permitAll()
            .antMatchers("/app/**").access("hasRole('ROLE_USER')")
            .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
            .and()
        .formLogin()
            .loginPage("/login")
            .failureUrl("/login?error=1")
            .permitAll()
            .and()
        .logout().logoutSuccessUrl("/login?logout=1")                                   
                .permitAll()
        .and().csrf()
        .and().exceptionHandling().accessDeniedPage("/world/403");
		
	   logger.info("Spring security configuration has been initialized.");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	
	
}
