package org.ei.evtools.db.domain;

import java.util.ArrayList;
import java.util.List;
/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class UserAndRole {
	
	private String username;
	private String password;
	private boolean enabled;
	private List<String> userRole = new ArrayList<String>();
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public List<String> getUserRole() {
		return userRole;
	}
	public void setUserRole(List<String> userRole) {
		this.userRole = userRole;
	}
	
	public String getUserRolesText() {
		String text="";
		for(String role : this.userRole){
			if(text.equalsIgnoreCase("")){
				text+=role;
			}else{
				text+=","+role;
			}
		}
		return text;
	}
	
	public UserAndRole(String username, String password, boolean enabled,
			List<String> userRole) {
		super();
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.userRole = userRole;
	}
	
	public UserAndRole() {
		super();
		
	}
	
}
