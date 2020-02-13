package org.ei.dataloading.lambda;

import java.io.File;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 
 * @author TELEBH
 * @Date: 02/04/2020
 * @Description: Test SSH connection to AWS EC2 Linux instance
 */
public class SshConnectionTest {
	
	public static void main(String args[])
	{
		//String hostIP = "10.178.165.18";
		String hostIP = "35.170.240.186";
		String userName = "ec2-user";
		String command = "ls -ltr";
		String privateKey = "Prod.pem";
		
		
		 try{
		        JSch jsch = new JSch();
		        
		        File keyFile = new File(SshConnectionTest.class.getResource(privateKey).toURI()); 
		        String privateKeyAbsolutePath = keyFile.getAbsolutePath();
		        jsch.addIdentity(privateKeyAbsolutePath);
		       		 
		       		 
		        Session session = jsch.getSession(userName, hostIP, 22);
		        
		        //Missing code
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        //


		        System.out.println("Establishing connection");
		        session.connect(2000);
		        System.out.println("Connection Established to BastionHost");
		      
		        session.setPortForwardingL(22, "10.178.165.18", 22);
		        Session sessionB = jsch.getSession(userName,"10.178.165.18",22);

	            sessionB.setConfig(config);
	            
	            sessionB.connect(2000);
		        System.out.println("Connection to Cafeloading is establilshed");
		       
//		            name = session.getUserName();
//		            System.out.println(session.getHost());

		    }
		    catch(Exception e){
		        System.out.print(e);
		    }        

	
		/*
		 * try { java.util.Properties config = new java.util.Properties();
		 * config.put("StrinctHostKeyChecking", "no");
		 * 
		 * JSch jsch = new JSch(); File keyFile = new
		 * File(SshConnectionTest.class.getResource(privateKey).toURI()); String
		 * privateKeyAbsolutePath = keyFile.getAbsolutePath();
		 * jsch.addIdentity(privateKeyAbsolutePath);
		 * 
		 * Session session = jsch.getSession(userName, hostIP);
		 * session.setConfig(config);
		 * 
		 * session.setTimeout(10); session.connect(); System.out.println("Connected");
		 * 
		 * } catch(Exception e) { e.printStackTrace(); }
		 */
	}

}
