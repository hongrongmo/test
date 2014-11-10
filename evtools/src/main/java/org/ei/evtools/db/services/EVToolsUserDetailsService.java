package org.ei.evtools.db.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.UserAndRole;
import org.ei.evtools.exception.AWSAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Component
@PropertySource("classpath:application.properties")
public class EVToolsUserDetailsService implements UserDetailsService{

	private static Logger logger = Logger.getLogger(EVToolsUserDetailsService.class);
	
	@Autowired
	AmazonS3Client amazonS3Service;
	
	@Autowired
	private Environment environment;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		try {
			
			String bucketKey = null; 
			if(environment.getProperty("ENVIRONMENT").equalsIgnoreCase("prod")){
				bucketKey = environment.getProperty("aws.s3.users.key.prod.name");
			}else{
				bucketKey = environment.getProperty("aws.s3.users.key.nonprod.name");
			}
			List<UserAndRole> list = getUserAndRoleList(environment.getProperty("aws.s3.users.bucket.name"),bucketKey);
			UserAndRole userAndRole = null;
			for(UserAndRole user : list){
				if(username.equalsIgnoreCase(user.getUsername())){
					userAndRole = user;
					break;
				}
			}
			if(userAndRole != null){
				List<GrantedAuthority> authorities = 
                        buildUserAuthority(userAndRole.getUserRole());
				return buildUserForAuthentication(userAndRole, authorities);
			}else{
				logger.warn("User '"+username+"' not found in the system.");
				throw new UsernameNotFoundException("User '"+username+"' not found in the system."); 
			}
		} catch (AWSAccessException e) {
			logger.error("Error fetching user and role information from S3......exception="+e.getMessage());
		}
		return null;
	}
	
	public List<UserAndRole> loadAllUsers() throws AWSAccessException{
		List<UserAndRole> list = new ArrayList<UserAndRole>();
		try {
			
			String bucketKey = null; 
			if(environment.getProperty("ENVIRONMENT").equalsIgnoreCase("prod")){
				bucketKey = environment.getProperty("aws.s3.users.key.prod.name");
			}else{
				bucketKey = environment.getProperty("aws.s3.users.key.nonprod.name");
			}
			
			list = getUserAndRoleList(environment.getProperty("aws.s3.users.bucket.name"),bucketKey);
		} catch (Exception e) {
			logger.error("Error fetching user and role information from S3......exception="+e.getMessage());
			throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),e);
		}
		return list;
	}
	
	private List<GrantedAuthority> buildUserAuthority(List<String> userRoles) {
		 
		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
 
		// Build user's authorities
		for (String role : userRoles) {
			setAuths.add(new SimpleGrantedAuthority(role));
		}
 
		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
 
		return Result;
	}
	
	private User buildUserForAuthentication(UserAndRole userAndRole, 
		List<GrantedAuthority> authorities) {
		return new User(userAndRole.getUsername(), userAndRole.getPassword(), 
				userAndRole.isEnabled(), true, true, true, authorities);
	}
 
	private List<UserAndRole> getUserAndRoleList(String bucketName, String key) throws AWSAccessException{
		InputStream objStream = null;
		BufferedReader reader = null;
		List<UserAndRole> list = new ArrayList<UserAndRole>();
		try{
			S3Object object = amazonS3Service.getObject(bucketName, key);
			objStream = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objStream));
			while (true) {
	            String line = null;
	            line = reader.readLine();
	            if (line == null) {
	                break;
	            } else {
	                String[] tokens = line.split("---");
	                boolean enabled = Boolean.parseBoolean(tokens[2]);
	                List<String> userRoles = new ArrayList<String>(Arrays.asList(tokens[3].split(",")));
	                list.add(new UserAndRole(tokens[0], tokens[1], enabled, userRoles));
	            }
	        }
			
		}catch(Exception e){
			logger.error("Error fetching user and role information from S3......exception="+e.getMessage());
			if(objStream != null){
				try {
					objStream.close();
				} catch (IOException ex) {
					logger.error("Error fetching user and role information from S3......exception="+ex.getMessage());
					throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),ex);
				}
			}
			if(reader != null){
				try {
					reader.close();
				} catch (IOException ex) {
					logger.error("Error fetching user and role information from S3......exception="+ex.getMessage());
					throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),ex);
				}
			}
			throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),e);
		}finally{
			if(objStream != null){
				try {
					objStream.close();
				} catch (IOException e) {
					logger.error("Error fetching user and role information from S3......exception="+e.getMessage());
					throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),e);
				}
			}
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Error fetching user and role information from S3......exception="+e.getMessage());
					throw new AWSAccessException("Error fetching user and role information from S3......exception="+e.getMessage(),e);
				}
			}
		}
		return list;
	}

}
