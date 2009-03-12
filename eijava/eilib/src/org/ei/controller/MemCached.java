package org.ei.controller;

import java.util.Calendar;
import java.util.Date;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.danga.MemCached.SockIOPool.SockIO;

public class MemCached {
    
  private MemCachedClient mcc = null;  
  private SockIOPool pool = null; 
  private int expiryTime = 20;//minutes
  
  public void initialize(String[] servers, String[] weights)
  {
	 
	this.mcc = new MemCachedClient();
	this.pool = SockIOPool.getInstance();    
	this.pool.setServers( servers );
	Integer[] w = null;
	if (weights != null) {
	  w = new Integer[weights.length];
	  for (int i = 0; i < weights.length; i++)	 
	  {
		String s = weights[i];
	    try {
	      w[i] = new Integer(s);
	    }
	    catch (NumberFormatException e) {
	      w[i] = new Integer(1);
	    }	    
	  }
	}
	this.pool.setWeights( w );    
	this.pool.setInitConn( 5 );
	this.pool.setMinConn( 5 );
	this.pool.setMaxConn( 250 );
	this.pool.setMaxIdle( 1000 * 60 * 60 * 6 );
	this.pool.setMaintSleep( 30 );
	this.pool.setNagle( false );
	this.pool.setSocketTO( 3000 );
	this.pool.setSocketConnectTO( 0 );
	this.mcc.setCompressEnable( true );
	this.mcc.setCompressThreshold( 64 * 1024 );    
	this.mcc.setPrimitiveAsString( true );
	this.pool.setHashingAlg( SockIOPool.NEW_COMPAT_HASH );    
	this.pool.initialize();		
  }
  
  private MemCachedClient getMemCachedClient()
  {		
		return this.mcc;
  }
  
  private Date getExpiryDate()
	{
		Calendar calendar = Calendar.getInstance();
		long time = calendar.getTimeInMillis();
		time += expiryTime * 60 * 1000;
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}
  
  public void add(String key, String value)
	{
		MemCachedClient mc = this.getMemCachedClient();
		if(mc.keyExists(key))
		{
	 		mc.replace(key, value, getExpiryDate());
		}
		else
		{
	 		mc.add(key, value, getExpiryDate());
		}
	}
	
	public void add(String key, String value, Date expiryDate)
	{
		MemCachedClient mc = this.getMemCachedClient();
		if(mc.keyExists(key))
		{
	 		mc.replace(key, value, expiryDate);
		}
		else
		{
	 		mc.add(key, value, expiryDate);
		}		
	}
	
	public void delete(String key)
	{
		MemCachedClient mc = this.getMemCachedClient();
		mc.delete(key);
	}
	
	public String get(String key)
	{
		MemCachedClient mc = this.getMemCachedClient();			
		String value = (String) mc.get(key);		
		return value;
	}
		
	public boolean keyExists(String key)
	{
		MemCachedClient mc = this.getMemCachedClient();		
		return  mc.keyExists(key);
	}
	
	/*
	public boolean isAlive()
	{
		try
		{
			SockIO sockio;
			sockio = pool.getConnection("206.137.75.51:11211");
			if(sockio != null)
			{
				return true;				
			}
			else
			{
				return false;
			}
				
		}
		catch(Exception e){return false;}		
	}
	*/
}
