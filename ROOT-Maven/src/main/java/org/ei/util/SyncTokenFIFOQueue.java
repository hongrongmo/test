package org.ei.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.ei.config.RuntimeProperties;

public class SyncTokenFIFOQueue implements Serializable{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -137127524009826269L;

	private static Logger log4j = Logger.getLogger(SyncTokenFIFOQueue.class);
	 
	 /** The random.
	  * 
	  *  SecureRandom objects are expensive to initialize, so you'll want to keep one around and reuse it.  
	  */
	 private static SecureRandom random = new SecureRandom();
	
	 private LinkedList<String> list = new LinkedList<String>();
	 
	 public int size() {
	   return list.size();
	 }
	 
	 public boolean isEmpty() {
	   return list.isEmpty();
	 }
	 
	 public boolean isMatchFound(String token){
		 return list.contains(token);
	 }
	 
	 public void createNewToken(){
		 
		long tokenListSize = 10;
		try {
			tokenListSize = Long.parseLong(RuntimeProperties.getInstance().getProperty(RuntimeProperties.SYNC_TOKEN_LIST_SIZE,"10"));
		} catch (Exception e) {
			log4j.warn("Could not get the value of runtime property 'SYNC_TOKEN_LIST_SIZE', using the default value as "+tokenListSize);
		}
		 
		if(list.size()>=tokenListSize){
			long variation = list.size()-tokenListSize;
			variation = variation+1;
			for(int j =0; j<variation;j++){
				list.removeFirst(); 
			}
		}
		list.addLast(SyncTokenFIFOQueue.getNewToken());
	 }
	 
	 public String getLastElement(){
		 if(list.isEmpty()){
			 list.push(SyncTokenFIFOQueue.getNewToken());
		 }
		 return list.getLast();
	 }
	 
	 public void clearAll(){
		 list.clear();
	 }
	 
	 private static String getNewToken(){
		return new BigInteger(130, random).toString(32);
	 }
	 
	 
	 
}
