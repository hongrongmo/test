package org.ei.evtools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class PasswordEncoderGenerator {

	public static void main(String[] args) {
		
		//  prompt the user to enter their name
	    System.out.print("Enter your password text to hash:");
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String passwordText = null;
	    
	    try {
	    	passwordText = br.readLine();
	     } catch (IOException ioe) {
	        System.out.println("IO error trying to read your password text!");
	        System.exit(1);
	     }
	    
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(passwordText);
	 	System.out.println("Your hashed value for the password text '"+passwordText+"' is: "+hashedPassword);
	}

}
